package de.dewarim.grailsmagic.mkm

class McmOrder {

    static constraints = {
    }
    
    static hasMany = [orderArticles:OrderArticle]
    
    Long orderId
    UserEntity seller
    UserEntity buyer
    ShippingMethod shippingMethod
    Evaluation evaluation
    Integer articleValue = 0
    Integer totalValue = 0
    OrderStatus status

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof McmOrder)) return false

        McmOrder mcmOrder = (McmOrder) o

        if (articleValue != mcmOrder.articleValue) return false
        if (buyer != mcmOrder.buyer) return false
        if (evaluation != mcmOrder.evaluation) return false
        if (orderId != mcmOrder.orderId) return false
        if (seller != mcmOrder.seller) return false
        if (shippingMethod != mcmOrder.shippingMethod) return false
        if (status != mcmOrder.status) return false
        if (totalValue != mcmOrder.totalValue) return false

        return true
    }

    int hashCode() {
        return orderId.hashCode()
    }
}
