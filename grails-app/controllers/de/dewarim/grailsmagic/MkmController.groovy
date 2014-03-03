package de.dewarim.grailsmagic

import de.dewarim.grailsmagic.mkm.Article
import de.dewarim.grailsmagic.mkm.ArticleStatus
import de.dewarim.grailsmagic.mkm.Game
import de.dewarim.grailsmagic.mkm.MkmConfig

class MkmController {

    def grailsApplication
    def mkmService

    def updateStock(){
        def mkmConfig = createConfig(grailsApplication.config)
        def stock = mkmService.updateStock(mkmConfig)
        log.debug("Found ${stock.size()} articles.")
        redirect(action:'showStock');
    }
    
    def index() {
        [games:Game.list()]
    }
    
    def showStock(){
        def articles = Article.findAllWhere(status: ArticleStatus.ONLINE)
        return [cards: articles] 
    }
    
    def updateGames(){
        def config = createConfig(grailsApplication.config)
        def games = mkmService.getGames(config)
        render(template: 'games', model: [games:games])
    }
    
    static protected createConfig(config){
        return new MkmConfig(apiKey: config.mkmApiKey,
                userId: config.mkmUserId,
                username: config.mkmUsername,
                downloadImages: config.downloadImages,
                fetchAll: true,
                start:0
        )
    }
    
    def doSearch(String query, Long gameId, Long languageId, Boolean exactMatch){
        def config = createConfig(grailsApplication.config)
        exactMatch = exactMatch != null ? exactMatch : false
        
        def products = mkmService.searchForProducts(config, query, gameId, languageId, exactMatch)
        render(template: 'products', model: [products: products])        
    }
    
    def doSearchMeta(String query, Long gameId, Long languageId){
        def config = createConfig(grailsApplication.config)
        def metaProduct = mkmService.searchForMetaProducts(config, query, gameId, languageId)
        render(template: 'products', model: [products: metaProduct.fetchProducts()])        
    }

}
