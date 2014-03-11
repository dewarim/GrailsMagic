package de.dewarim.grailsmagic

import de.dewarim.grailsmagic.mkm.Article
import de.dewarim.grailsmagic.mkm.ArticleStatus
import de.dewarim.grailsmagic.mkm.Game
import de.dewarim.grailsmagic.mkm.MkmConfig
import de.dewarim.grailsmagic.mkm.orders.McmOrder
import de.dewarim.grailsmagic.mkm.orders.OrderStatus

class MkmController {

    def grailsApplication
    def mkmService

    def updateStock() {
        def mkmConfig = createConfig(grailsApplication.config)
        def stock = mkmService.updateStock(mkmConfig)
        log.debug("Found ${stock.size()} articles.")
        redirect(action: 'showStock');
    }

    def index() {
        def mkmConfig = createConfig(grailsApplication.config)
        [games: Game.list(), missingApiKey: mkmConfig.apiKey == null || mkmConfig.username == '--unknown--']
    }

    def showStock() {
        def articles = Article.findAllWhere(status: ArticleStatus.ONLINE)
        return [cards: articles]
    }

    def updateGames() {
        def config = createConfig(grailsApplication.config)
        def games = mkmService.getGames(config)
        render(template: 'games', model: [games: games])
    }

    protected createConfig(config) {
        def apiKey = config.mkmApiKey ?: session.apiKey
        return new MkmConfig(apiKey: apiKey,
                userId: config.mkmUserId ?: 0,
                username: config.mkmUsername ?: '--unknown--',
                downloadImages: config.downloadImages ?: true,
                fetchAll: true,
                start: 0
        )
    }

    def showOrders(Integer actor, Integer status) {
        def mkmConfig = createConfig(grailsApplication.config)
        if (!status) {
            status = 2 // status: paid=2, sent=4
        }
        def orderStatus = OrderStatus.findByState(status)
        if (!actor) {
            actor = 1 // as seller; buyer=2
        }
        def orders = mkmService.fetchOrders(mkmConfig, actor, orderStatus)
        return [orders: orders]
    }

    def renderChecklist(Long id) {
        try {
            def order = McmOrder.get(id)
            if (order) {
                render(template: '/mkm/checklist', model: [articles: order.orderArticles.collect { it.article }])
                return
            }
        }
        catch (Exception e) {
            log.debug("failed to render checklist:", e)
        }
        render(status: 500, text: 'Failed to load order.')
    }

    def doSearch(String query, Long gameId, Long languageId, Boolean exactMatch) {
        def config = createConfig(grailsApplication.config)
        exactMatch = exactMatch != null ? exactMatch : false

        def products = mkmService.searchForProducts(config, query, gameId, languageId, exactMatch)
        render(template: 'products', model: [products: products])
    }

    def doSearchMeta(String query, Long gameId, Long languageId) {
        def config = createConfig(grailsApplication.config)
        def metaProduct = mkmService.searchForMetaProducts(config, query, gameId, languageId)
        render(template: 'products', model: [products: metaProduct.fetchProducts()])
    }

    def doSearchArticles(String query, Long gameId, Long languageId, Boolean exactMatch) {
        def config = createConfig(grailsApplication.config)
        exactMatch = exactMatch != null ? exactMatch : false
        languageId = languageId != null ? languageId : 1
        def products = mkmService.searchForProducts(config, query, gameId, languageId, exactMatch)
        def articles = []
        products.each { product ->
            articles.addAll(mkmService.fetchArticlesByProductId(config, product.productId, false))
        }
        render(template: 'stock', model: [cards: articles])
    }

}
