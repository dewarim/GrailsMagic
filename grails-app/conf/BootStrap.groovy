class BootStrap {

    def grailsApplication
    
    def init = { servletContext ->
        
        environments{
            development{
//                log.debug("mkmApiKey: "+grailsApplication.config.mkmApiKey)
            }
        }
        
    }
    def destroy = {
    }
}
