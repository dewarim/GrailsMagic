package de.dewarim.grailsmagic.mkm;

/**
 * Denotes the state of an article
 */
public enum ArticleStatus {

    /**
     * Article is currently on-sale.
     */
    ONLINE,
    
    /**
     * Article is not for sale - intended for removing cards from the MKM
     * and adding the same product later on again.
     */
    OFFLINE,

    /**
     * Article has been sold or is no longer for sale for some reason.
     * When updating, change the state of all articles that are no longer 
     * to be found to this state.  
     */
    ARCHIVE
    
}
