package de.dewarim.grailsmagic.mkm

class OrderArticle {

    static constraints = {
    }
    
    McmOrder mcmOrder
    Article article

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof OrderArticle)) return false

        OrderArticle that = (OrderArticle) o

        if (article != that.article) return false
        if (mcmOrder != that.mcmOrder) return false

        return true
    }

    int hashCode() {
        int result
        result = (mcmOrder != null ? mcmOrder.hashCode() : 0)
        result = 31 * result + (article != null ? article.hashCode() : 0)
        return result
    }
}
