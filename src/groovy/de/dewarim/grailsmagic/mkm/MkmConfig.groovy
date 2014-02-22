package de.dewarim.grailsmagic.mkm

/**
 * Parameter object relevant for requests to MKM
 */
class MkmConfig {
    
    String apiKey
    String username
    Long userId

    /**
     * For requests that may return partial result,
     * you can specify the number of the first element to fetch.
     * MKM will generally only reply with the first 100 elements found.
     */
    Integer start = 0

    /**
     * If set to true, check the response header if the request is partial
     * and if necessary do further requests to fetch more results.
     * Note: you should probably not use this for really large result sets (for example,
     * "get all the cards") - seems like a good way to get your API key revoked.
     * // TODO: implement fetchAll
     */
    Boolean fetchAll = false

    /**
     * If true, check the lastRefresh Date of a product and, if older than
     * maxItemDate fetch the item again from MKM
     * TODO: implement doRefresh
     */
    Boolean doRefresh = false

    /**
     * Maximum age of items before a refresh (loading the item again from MKM)
     * is necessary. Requires doRefresh to be set to true before any action is taken.
     * TODO: implement maxItemAge
     */
    Date maxItemAge = null 
    
}
