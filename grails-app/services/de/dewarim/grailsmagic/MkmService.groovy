package de.dewarim.grailsmagic

import de.dewarim.grailsmagic.mkm.Address
import de.dewarim.grailsmagic.mkm.Article
import de.dewarim.grailsmagic.mkm.Language
import de.dewarim.grailsmagic.mkm.MkmConfig
import de.dewarim.grailsmagic.mkm.Product
import de.dewarim.grailsmagic.mkm.ProductName
import de.dewarim.grailsmagic.mkm.UserEntity
import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder

import java.text.SimpleDateFormat

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET

@Transactional
class MkmService {

    static final String dummyXml = "<empty/>"

    List<Article> getStock(MkmConfig config) {
        def user = fetchUserEntryById(config, config.userId)
        def result = doRequest(config, "stock")
        log.debug("result: $result")
        def xml = new XmlSlurper().parseText(result)
        def articles = []
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        xml.article.each { node ->
            def articleId = Long.parseLong(node.idArticle.text())
            def article = Article.findByArticleId(articleId)
            def params = [
                    product: fetchProduct(config, Long.parseLong(node.idProduct.text())),
                    language: fetchLanguage(node.language),
                    comments: node.comments?.text(),
                    price: Integer.parseInt(node.price.text().replaceAll('\\.', '')),
                    count: Integer.parseInt(node.count.text()),
                    condition: node.condition.text(),
                    seller: user,
                    articleId: articleId,
                    lastModified: dateFormat.parse(node.lastEdited.text())
            ]
            // in this case, the seller is the user.
//            if(node.seller?.text()?.length() > 0){
//                params.put('seller', fetchUserEntry(node.seller))
//            }
            if (article) {
                log.debug("Found existing article, will update.")
                article.properties = params
            }
            else {
                log.debug("Found new article.")
                article = new Article(params)
                article.save()
            }
            articles.add(article)
        }
        return articles
    }
    
    def fetchProduct(config, id){
        def product = Product.findByProductId(id)
        if(product){
            return product
        }
        def result = doRequest(config, "product/$id")
        def xml = new XmlSlurper().parseText(result).product
        product = new Product()
        def params = [
                productId: Long.parseLong(xml.idProduct.text()),
                metaProductId: Long.parseLong(xml.idMetaproduct.text()),               
                imagePath: xml.image.text(),
                expansion: xml.expansion.text(),
                rarity: xml.rarity.text(),                
        ]
        product.properties = params
        product.save()
        parseNames(product, xml."name")
        return product
    }
    
    def parseNames(Product product, names){
        names.each{
            def pn = new ProductName(product: product, 
                    languageId: it.idLanguage.text(),
                    languageName: it.languageName.text(),
                    name:it.productName.text()
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
        log.debug("riskGroup: ${xml.riskGroup.text()}")
        def params = [userId: id,
                username: xml.username.text(),
                country: xml.country.text(),
                commercial:Boolean.parseBoolean(xml.isCommercial.text()),
                riskGroup: Integer.parseInt(xml.riskGroup.text()),
                reputation: Integer.parseInt(xml.reputation.text()),
                firstName: xml."name"?.firstName?.text(),
                lastName: xml."name"?.lastName?.text()
        ]
        if (!user) {
            user = new UserEntity(params)
            user.save()            
        }
        else{
            user.properties = params
        }
        
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

    def doRequest(config, command) {
        def http = new HTTPBuilder()
        def result = dummyXml
        http.request('https://www.mkmapi.eu/', GET, TEXT) { req ->
            if (config.start) {
                uri.query = [start: config.start]
            }
            uri.path = "/ws/${config.username}/${config.apiKey}/${command}"
            headers.'User-Agent' = "Mozilla/5.0 Firefox/200"
            headers.Accept = 'application/xml'

            response.success = { resp, reader ->
                result = reader.text
                log.debug "Got response: ${resp.statusLine}"
                log.debug "Content-Type: ${resp.headers.'Content-Type'}"
                log.debug result
            }

            response.'404' = {
                log.debug('Not found')
            }
        }
        return result
    }
}
