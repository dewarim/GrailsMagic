package de.dewarim.grailsmagic

import de.dewarim.grailsmagic.mkm.MkmConfig

class MkmController {

    def grailsApplication
    def mkmService

    def index() {
        def config = grailsApplication.config
        def mkmConfig = new MkmConfig(apiKey: config.mkmApiKey,
                userId: config.mkmUserId,
                username: config.mkmUsername,
                downloadImages: config.downloadImages 
        )
        def stock = mkmService.getStock(mkmConfig)
        return [cards: stock]
    }

}
