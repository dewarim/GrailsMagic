package de.dewarim.grailsmagic

import de.dewarim.grailsmagic.mkm.MkmConfig

class MkmController {

    def grailsApplication
    def mkmService

    def index() {
        def config = new MkmConfig(apiKey: grailsApplication.config.mkmApiKey,
                userId: grailsApplication.config.mkmUserId,
                username: grailsApplication.config.mkmUsername
        )
        def stock = mkmService.getStock(config)
        return [cards: stock]
    }

}
