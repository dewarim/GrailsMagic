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

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Article)) return false

        Article article = (Article) o

        if (altered != article.altered) return false
        if (articleId != article.articleId) return false
        if (comments != article.comments) return false
        if (condition != article.condition) return false
        if (count != article.count) return false
        if (firstEd != article.firstEd) return false
        if (foil != article.foil) return false
        if (language != article.language) return false
        if (lastModified != article.lastModified) return false
        if (playSet != article.playSet) return false
        if (price != article.price) return false
        if (product != article.product) return false
        if (seller != article.seller) return false
        if (signed != article.signed) return false
        if (status != article.status) return false

        return true
    }

    int hashCode() {
        return articleId.hashCode()
    }
}
