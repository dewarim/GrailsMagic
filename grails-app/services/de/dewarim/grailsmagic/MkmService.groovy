package de.dewarim.grailsmagic

import com.google.common.io.ByteStreams
import de.dewarim.grailsmagic.mkm.Address
import de.dewarim.grailsmagic.mkm.Article
import de.dewarim.grailsmagic.mkm.Language
import de.dewarim.grailsmagic.mkm.MkmConfig
import de.dewarim.grailsmagic.mkm.Product
import de.dewarim.grailsmagic.mkm.ProductName
import de.dewarim.grailsmagic.mkm.UserEntity
import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import java.text.SimpleDateFormat

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET

@Transactional
class MkmService {

    static final String dummyXml = "<empty/>"

    List<Article> getStock(MkmConfig config) {
        def user = fetchUserEntryById(config, config.userId)
        def result = doRequest(config, "stock")
//        log.debug("result: $result")
        def xml = new XmlSlurper().parseText(result)
        def articles = []
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//        def count = 0
//        xml.article.findAll{count++ < 2}.each{node ->
        xml.article.each { node ->
            try {                
                def articleId = Long.parseLong(node.idArticle.text())
                def article = Article.findByArticleId(articleId)
                def params = [
                        product: fetchProduct(config, Long.parseLong(node.idProduct.text())),
                        language: fetchLanguage(node.language),
                        comments: node.comments?.text(),
                        price: Integer.parseInt(node.price.text().replaceAll('\\.', '')),
                        count: Integer.parseInt(node.count.text()),
                        condition: node.condition?.text() ?: '-',
                        seller: user,
                        articleId: articleId,
                        lastModified: dateFormat.parse(node.lastEdited.text())
                ]
                if (article) {
                   log.debug("Found existing article, will update.")
                    article.properties = params
                }
                else {
                    log.debug("Found new article.")
                    article = new Article(params)
                }
                article.save()
//                log.debug("article: ${article.dump()}")
                articles.add(article)
            }
            catch (Exception e) {
                log.error("Failed to download article: ", e)
//                throw new RuntimeException(e)
            }
        }
        return articles
    }

    def fetchProduct(MkmConfig config, id) {
        def product = Product.findByProductId(id)
        if (!product) {
            def result = doRequest(config, "product/$id")
            def xml = new XmlSlurper().parseText(result).product
            product = new Product()
            def params = [
                    productId: Long.parseLong(xml.idProduct.text()),
                    metaProductId: Long.parseLong(xml.idMetaproduct?.text() ?: '0'),
                    imagePath: xml.image?.text() ?: '',
                    expansion: xml.expansion?.text() ?: '-',
                    rarity: xml.rarity?.text() ?: '-',
            ]
            product.properties = params
            product.save()
            parseNames(product, xml."name")
        }
        else{
            log.debug("Found existing product.")
        }
        if (config.downloadImages && product.image == null) {
            product.image = fetchCardImage(product)
            product.save()
        }
//        log.debug("product: ${product.dump()}")
        return product
    }

    def fetchCardImage(Product product) {
        def image = null
        def url = "http://mkmapi.eu/${product.imagePath?.replaceAll('^\\./', '')}"
        log.debug("fetch image from: $url")
        HttpGet req = new HttpGet(url)
        HttpClient client = new DefaultHttpClient()
        HttpResponse response = client.execute(req)
        if (response.statusLine.statusCode == 200) {
            log.debug("found image.")
            InputStream inputStream = response.getEntity().getContent()
            image = new CardImage(imageData: ByteStreams.toByteArray(inputStream),
                    name: product.getOriginalName(),
                    type: ImageType.JPEG,
                    imageSize: ImageSize.SMALL
            )
            image.save()
        }
        else {
            log.warn("image was not found on $url")
        }
        return image
    }

    def parseNames(Product product, names) {
        names.each {
            def pn = new ProductName(product: product,
                    languageId: it.idLanguage.text(),
                    languageName: it.languageName.text(),
                    name: it.productName.text()
            )
            product.addToNames(pn)
            pn.save()
        }
    }

    def fetchUserEntryById(config, id) {
        def user = UserEntity.findByUserId(id)
        // note: we always fetch the user - could be that the rating has changed.
        def result = doRequest(config, "user/$id")
        if (result.equals(dummyXml)) {
            if (user) {
                return user
            }
            user = new UserEntity(username: '--unknown user--')
            user.save()
            return user
        }
        def xml = new XmlSlurper().parseText(result).user
        def params = [userId: id,
                username: xml.username.text(),
                country: xml.country.text(),
                commercial: Boolean.parseBoolean(xml.isCommercial.text()),
                riskGroup: Integer.parseInt(xml.riskGroup.text()),
                reputation: Integer.parseInt(xml.reputation.text()),
                firstName: xml."name"?.firstName?.text(),
                lastName: xml."name"?.lastName?.text()
        ]
        if (user) {
            user.properties = params
        }
        else {
            user = new UserEntity(params)
        }
        user.save()
        return user
    }

    def fetchUserEntry(node) {
        def id = Long.parseLong(node.idUser.text())
        def params = [username: node.username.text(),
                country: node.country.text(),
                commercial: Boolean.parseBoolean(node.isCommercial.text()),
                riskGroup: Integer.parseInt(node.riskGroup.text()),
                reputation: Integer.parseInt(node.reputation.text()),
                firstName: node.name?.firstName?.text() ?: '',
                lastName: node.name?.lastName?.text() ?: '',
                address: fetchAddress(node.address)
        ]
        def user = UserEntity.findByUserId(id)
        if (!user) {
            user = new UserEntity(params)
            user.save()
        }
        else {
            user.properties = params
        }
        return user
    }

    def fetchAddress(node) {
        def address = Address.findOrSaveWhere([name: node.name.text(),
                extra: node.extra.text(),
                street: node.street.text(),
                zip: node.zip.text(),
                city: node.city.text(),
                country: node.country.text()
        ])
        return address
    }

    def fetchLanguage(node) {
        def id = Long.parseLong(node.idLanguage.text())
        def language = Language.findByIdLanguage(id)
        if (!language) {
            language = new Language(idLanguage: id, languageName: node.languageName.text())
            language.save()
        }
        return language
    }

    def String[] partialCommands = ['stock', 'articles', 'products', 'orders']

    def doRequest(MkmConfig config, command) {
        def http = new HTTPBuilder()
        def result = dummyXml
        http.request('https://www.mkmapi.eu/', GET, TEXT) { req ->
            uri.path = "/ws/${config.username}/${config.apiKey}/${command}"
            if (config.start && partialCommands.contains(command)) {
                uri.path = "${uri.path}/${config.start}"
            }
            log.debug("path: ${uri.path}")
            headers.'User-Agent' = "Mozilla/5.0 Firefox/201"
            headers.Accept = 'application/xml'

            response.success = { resp, reader ->

                result = reader.text
                log.debug "Got response: ${resp.statusLine}"
                log.debug "Content-Type: ${resp.headers.'Content-Type'}"
//                log.debug result

                if (resp.statusLine.statusCode == 206 && config.fetchAll) {
                    def rangeHeader = resp.headers."Range"
                    def range = rangeHeader.split('/')
                    def alreadyFound = Integer.parseInt(range[0].split('-')[1])
                    def totalItems = Integer.parseInt(range[1])
                    log.debug("range header is: ${rangeHeader}, totalItems:  ${totalItems}, alreadyFound: ${alreadyFound}")
                    if (alreadyFound < totalItems && config.start < totalItems) {
                        log.debug("range header is: ${rangeHeader}, would now fetch remaining ${totalItems - alreadyFound}")
                        config.start = alreadyFound + 1
                        def nextItems = doRequest(config, command)
                        result = addResponseToExistingXml(result, nextItems)
                    }
                }

            }

            response.'404' = {
                log.debug('Not found')
            }
        }
        return result
    }

    def addResponseToExistingXml(String oldContent, String newContent) {
        def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def doc = builder.parse(new ByteArrayInputStream(oldContent.bytes))
        def newDoc = builder.parse(new ByteArrayInputStream(newContent.bytes))
        def expr = XPathFactory.newInstance().newXPath().compile("//*[self::article or self::product or self::order or self::stock]")
        org.w3c.dom.NodeList nodes = (org.w3c.dom.NodeList) expr.evaluate(newDoc, XPathConstants.NODESET)
        def root = doc.getDocumentElement()
        nodes.each { node ->
            def adopted = doc.adoptNode(node)
            if (adopted) {
                root.insertBefore(adopted, null)
            }
        }
        return documentToString(doc)
    }

    String documentToString(Document doc) {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }
}
