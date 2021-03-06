package de.dewarim.grailsmagic

import com.google.common.io.ByteStreams
import de.dewarim.grailsmagic.mkm.Address
import de.dewarim.grailsmagic.mkm.Article
import de.dewarim.grailsmagic.mkm.ArticleStatus
import de.dewarim.grailsmagic.mkm.Game
import de.dewarim.grailsmagic.mkm.Language
import de.dewarim.grailsmagic.mkm.orders.Evaluation
import de.dewarim.grailsmagic.mkm.orders.McmOrder
import de.dewarim.grailsmagic.mkm.MetaProduct
import de.dewarim.grailsmagic.mkm.MetaProductName
import de.dewarim.grailsmagic.mkm.MkmConfig
import de.dewarim.grailsmagic.mkm.orders.OrderArticle
import de.dewarim.grailsmagic.mkm.orders.OrderStatus
import de.dewarim.grailsmagic.mkm.PriceGuide
import de.dewarim.grailsmagic.mkm.Product
import de.dewarim.grailsmagic.mkm.ProductName
import de.dewarim.grailsmagic.mkm.UserEntity
import de.dewarim.grailsmagic.mkm.orders.ShippingMethod
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
    static final dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    List<Article> updateStock(MkmConfig config) {
        def user = fetchUserEntryById(config, config.userId)
        def result = doRequest(config, "stock")
        log.debug("result: $result")
        def xml = new XmlSlurper().parseText(result)
        Set currentArticleIdList = Article.executeQuery("select a.id from Article a where seller=:seller and status =:onlineStatus",
                [onlineStatus: ArticleStatus.ONLINE, seller: user]).toSet()
        log.debug("Current user's article list = ${currentArticleIdList.size()} articles.")
        def articles = parseArticles(config, xml, currentArticleIdList, user)
        updateArticlesStatus(currentArticleIdList, ArticleStatus.ARCHIVE)
        return articles
    }

    def fetchProduct(MkmConfig config, id) {
        def product = Product.findByProductId(id)
        if (!product) {
            log.debug("found new product $id")
            def result = doRequest(config, "product/$id")
            def xml = new XmlSlurper().parseText(result).product
            product = new Product()
            def params = [
                    productId: Long.parseLong(xml.idProduct.text()),
                    metaProduct: fetchMetaProduct(config, Long.parseLong(xml.idMetaproduct?.text() ?: '0')),
                    imagePath: xml.image?.text() ?: '',
                    expansion: xml.expansion?.text() ?: '-',
                    rarity: xml.rarity?.text() ?: '-',
            ]
            product.properties = params
            product.save()
            xml.priceGuide.each { guide ->
                def priceGuide = new PriceGuide(product: product,
                        sell: priceToInt(guide.SELL.text()),
                        low: priceToInt(guide.LOW.text()),
                        average: priceToInt(guide.AVG.text())
                )
                product.addToPriceGuides(priceGuide)
                priceGuide.save()
            }
            parseNames(product, xml."name")
        }
        else {
            log.debug("Found existing product.")
        }
        if (config.downloadImages && product.image == null) {
            product.image = fetchCardImage(product)
            product.save()
        }
//        log.debug("product: ${product.dump()}")
        return product
    }

    def fetchMetaProduct(MkmConfig config, id) {
        def metaProduct = MetaProduct.findByMetaProductId(id)
        if (!metaProduct) {
            log.debug("Found new MetaPropduct")
            def result = doRequest(config, "metaproduct/$id")
            log.debug("result: \n$result")
            def xml = new XmlSlurper().parseText(result).metaproduct
            def metaProductId = Long.parseLong(xml.idMetaproduct.text())
            metaProduct = new MetaProduct(metaProductId: metaProductId)
            metaProduct.save()
            parseMetaNames(metaProduct, xml."name")
            xml.products.idProduct.each {
                fetchProduct(config, it.text())
            }
        }
        return metaProduct
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

    def parseMetaNames(MetaProduct product, names) {
        names.each {
            def pn = new MetaProductName(metaProduct: product,
                    languageId: it.idLanguage.text(),
                    languageName: it.languageName.text(),
                    name: it.metaproductName.text()
            )
            product.addToNames(pn)
            pn.save()
        }
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
            log.debug("User with id $id was not found.")
            return user ?: getUnknownUser()
        }
        def xml = new XmlSlurper().parseText(result).user
        def params = parseUserNode(xml, user)
        if (!user) {
            user = new UserEntity(params)
        }
        user.properties = params
        if (user.validate()) {
            user.save()
            return user
        }
        else {
            log.debug("problem persisting user $user")
            return null
        }
    }

    def parseUserNode(xml, user) {
        def params = [userId: xml.idUser.text(),
                username: xml.username.text(),
                country: xml.country.text(),
                commercial: Boolean.parseBoolean(xml.isCommercial.text() ?: 'false'),
                riskGroup: Integer.parseInt(xml.riskGroup.text()),
                reputation: Integer.parseInt(xml.reputation.text()),
                firstName: xml."name"?.firstName?.text(),
                lastName: xml."name"?.lastName?.text()
        ]
        // do not update name with null value.
        // This may happen if browsing unknown user profiles before buying from this user.
        if (user?.firstName && params.firstName == null) {
            params.remove('firstName')
        }
        if (user?.lastName && params.lastName == null) {
            params.remove('lastName')
        }
        return params
    }

    def getUnknownUser() {
        def user = UserEntity.findByUsername('--unknown user--')
        if (user) {
            return user
        }
        user = new UserEntity(username: '--unknown user--',
                commercial: false, country: '--', riskGroup: 0, reputation: 0,
                userId: 0)
        user.save()
        return user
    }

    def fetchUserEntry(node) {
        def id = Long.parseLong(node.idUser.text())
        def user = UserEntity.findByUserId(id)
        def params = parseUserNode(node, user)
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
            if (config.start && partialCommands.find { command.startsWith(it) }) {
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

    List<Game> getGames(config) {
        def result = doRequest(config, "games")
        def xml = new XmlSlurper().parseText(result)
        def games = []
        xml.game.each { node ->
            def gameId = node.idGame.text()
            def game = Game.findByGameId(gameId)
            if (!game) {
                game = new Game(gameId: Long.valueOf(gameId), name: node.name.text())
                game.save()
            }
            games.add(game)
        }
        return games
    }

    // GET products/:searchString/:idGame/:idLanguage/:exact[/:start] 
    List<Product> searchForProducts(MkmConfig config, query, gameId, languageId, exactMatch) {
        def command = "products/$query/$gameId/$languageId/$exactMatch"
        config.fetchAll = true
        def result = doRequest(config, command)
        def xml = new XmlSlurper().parseText(result)
        log.debug("result:\n $result")
        def products = []
        xml.product?.each { prod ->
            def id = prod.idProduct?.text()
            if (id) {
                def product = fetchProduct(config, id)
                if (product) {
                    products.add(product)
                }
            }
        }
        return products
    }

    // GET products/:searchString/:idGame/:idLanguage/:exact[/:start] 
    def searchForMetaProducts(MkmConfig config, query, gameId, languageId) {
        def command = "metaproduct/$query/$gameId/$languageId"
        config.fetchAll = true
        def result = doRequest(config, command)
        def xml = new XmlSlurper().parseText(result)
        log.debug("result:\n $result")
        MetaProduct metaProduct = null
        xml.metaproduct?.each { prod ->
            def id = prod.idMetaproduct?.text()
            if (id) {
                metaProduct = fetchMetaProduct(config, id)
            }
        }
        return metaProduct
    }

    /**
     * Fetch a list of offers for a specific product.
     * @param config config object which includes the mcm api key.
     * @param productId the id of the product (magic card)
     * @param doFetchAll if set to true, will fetch all offers. Otherwise, fetch
     * only the first 100. Note: this method ignores config.fetchAll, because 
     * generally it's not useful to fetch all 1000 offers on a random magic card.
     * @return list of articles
     */
    List<Article> fetchArticlesByProductId(MkmConfig config, productId, doFetchAll) {
        def command = "articles/$productId"
        config.fetchAll = doFetchAll ?: false
        def result = doRequest(config, command)
        def xml = new XmlSlurper().parseText(result)
        def product = fetchProduct(config, productId)
        def currentArticleIdList = Article.executeQuery("select a.id from Article a where a.status =:onlineStatus and a.product=:product",
                [onlineStatus: ArticleStatus.ONLINE, product:product]).toSet()
        def articles = parseArticles(config, xml, currentArticleIdList, null)
        updateArticlesStatus(currentArticleIdList, ArticleStatus.ARCHIVE)
        log.debug("getArticlesByProductId found ${articles.size()}")
        return articles
    }

    /**
     * Update the status of a list of articles
     * @param articles a set of article ids
     * @param status the new ArticleStatus
     */
    def updateArticlesStatus(articles, ArticleStatus status){
        if (articles?.size() > 0) {
            Article.executeUpdate("update Article set status=:archiveStatus where id in (:idList)",
                    [archiveStatus: status, idList: articles.toList()]
            )
        }
    }

    List<Article> parseArticles(MkmConfig config, xml, updateableArticleList, cardOwner) {
        def articles = []
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
                        seller: cardOwner ?: fetchUserEntryById(config, node.seller?.idUser?.text()),
                        foil: node.isFoil?.text() == 'true',
                        playSet: node.isPlayset?.text() == 'true',
                        altered: node.isAltered?.text() == 'true',
                        signed: node.isSigned?.text() == 'true',
                        firstEd: node.isFirstEd?.text() == 'true',
                        articleId: articleId
                ]
                if (node.lastEdited?.text()) {
                    params.put('lastModified', dateFormat.parse(node.lastEdited?.text()))
                }
                if (article) {
//                    log.debug("Found existing article, will update.")
                    article.properties = params
                    if (updateableArticleList) {
                        updateableArticleList.remove(article.id)
                    }
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
                throw new RuntimeException(e)
            }
        }
        return articles
    }

    def fetchOrders(MkmConfig config, actor, OrderStatus status) {
        def command = "orders/$actor/${status.state}"
        def result = doRequest(config, command)
        log.debug("result:\n$result")
        def xml = new XmlSlurper().parseText(result)
        def orders = []
        xml.order.each { orderNode ->
            def orderId = Long.valueOf(orderNode.idOrder.text())
            def existingOrder = McmOrder.findByOrderId(orderId)
            log.debug("existingOrder: ${existingOrder}")
            def newOrder = new McmOrder(orderId: orderId)
            def buyer = fetchUserEntry(orderNode.buyer)
            def seller = fetchUserEntry(orderNode.seller)
            def params = [
                    orderId: orderId,
                    seller: seller,
                    buyer: buyer,
                    shippingMethod: parseShippingMethod(orderNode.shippingMethod),
                    evaluation: parseEvaluation(existingOrder, orderNode.evaluation),
                    articleValue: priceToInt(orderNode.articleValue.text()),
                    totalValue: priceToInt(orderNode.totalValue.text()),
            ]
            newOrder.properties = params
            updateOrderStateFromNode(newOrder, orderNode)
            McmOrder order
            if (existingOrder) {
                if (!newOrder.equals(existingOrder)) {
                    existingOrder.properties = params
                }
                order = existingOrder
            }
            else {
                newOrder.save()
                order = newOrder
            }
            // assumption: articles on a given order do not change.
            def articles = parseArticles(config, orderNode, null, order.seller)
            articles.each { article ->
                def orderArticle = new OrderArticle(article: article, mcmOrder: order)
                order.addToOrderArticles(orderArticle)
                orderArticle.save()
                if(order.status != order.cancelled){
                    article.status = ArticleStatus.OFFLINE // has been sold
                }
            }
            orders.add(order)
        }
        return orders
    }

    /**
     * Parse the state of an orderNode and update the order object accordingly.
     * @param order the oder object to be updated with state (paid, bought, sent etc plus date)
     * @param state an XML node
     */
    def updateOrderStateFromNode(McmOrder order, orderNode) {
        def state = orderNode.state
        order.status = OrderStatus.findByName(state.state.text())
        ['dateBought', 'datePaid', 'dateSent', 'dateReceived', 'dateCancelled'].each { dateField ->
            def dateFieldValue = state."$dateField"?.text()
            if (dateFieldValue) {
                def dateFieldName = (dateField - 'date').toLowerCase()
                order."${dateFieldName}" = dateFormat.parse(dateFieldValue)
            }
        }
    }

    Evaluation parseEvaluation(McmOrder order, evaluationNode) {
        if (evaluationNode == null) {
            return null
        }
        def evalValues = [:]
        ['overall', 'articles', 'packaging', 'speed', 'comments'].each { evalField ->
            evalValues.put(evalField, evaluationNode."$evalField"?.text())
        }

        def evaluation = new Evaluation()
        if (order?.evaluation) {
            // we already got an evaluation object, so we need to update it.
            evaluation = order.evaluation
        }
        evaluation.properties = evalValues
        evaluation.save()
        return evaluation
    }

    ShippingMethod parseShippingMethod(shippingNode) {
        def shippingMethod = ShippingMethod.findOrCreateWhere(name: shippingNode.name.text(),
                price: priceToInt(shippingNode.price.text()))
        shippingMethod.save()
        log.debug("shippingMethod: ${shippingMethod}")
        return shippingMethod
    }

    /**
     * Convert a price string to an integer value.
     * @param price an euro or dollar string, representing a positive decimal number, for example 0.02
     * @return the value in cent as integer, for example input '0.02'  returns 2
     */
    Integer priceToInt(String price) {
        //Note: GORM seems to parse a decimal string automatically to int, 
        // but I cannot seem to find the docs for this,
        // so I'd rather use my own implementation.
        def withoutSpaceOrLeadingZero = price.replaceAll('^0|\\D+', '')
        if (price == null || withoutSpaceOrLeadingZero.length() == 0) {
            return 0
        }
        return Integer.valueOf(withoutSpaceOrLeadingZero)
    }
}

