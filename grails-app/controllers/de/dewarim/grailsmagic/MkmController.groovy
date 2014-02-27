package de.dewarim.grailsmagic

import de.dewarim.grailsmagic.mkm.Article
import de.dewarim.grailsmagic.mkm.ArticleStatus
import de.dewarim.grailsmagic.mkm.MkmConfig

class MkmController {

    def grailsApplication
    def mkmService

    def updateStock(){
        def config = grailsApplication.config
        def mkmConfig = new MkmConfig(apiKey: config.mkmApiKey,
                userId: config.mkmUserId,
                username: config.mkmUsername,
                downloadImages: config.downloadImages,
                fetchAll: true,
                start:0
        )

        def stock = mkmService.updateStock(mkmConfig)
        log.debug("Found ${stock.size()} articles.")
        redirect(action:'showStock');
    }
    
    def index() {
          
    }
    
    def showStock(){
        def articles = Article.findAllWhere(status: ArticleStatus.ONLINE)
        return [cards: articles] 
    }

}
