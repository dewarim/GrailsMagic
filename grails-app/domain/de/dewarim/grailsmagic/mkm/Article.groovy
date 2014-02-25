package de.dewarim.grailsmagic.mkm

class Article {

    static constraints = {
        articleId unique:true
        comments nullable: true
    }
    
    Long articleId
    Product product
    Language language
    String comments
    
    /**
     * price of the card / article; we use ¢ instead of €,
     * which is more straightforward.
     */
    Integer price
    Integer count
    UserEntity seller
    Date lastModified
    String condition
    Boolean foil = false
    Boolean signed = false
    Boolean playSet = false
    Boolean altered = false
    Boolean firstEd = false
    ArticleStatus status = ArticleStatus.ONLINE
    
}
