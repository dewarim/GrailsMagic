package de.dewarim.grailsmagic.mkm.orders

import de.dewarim.grailsmagic.mkm.UserEntity

class McmOrder {

    static constraints = {
        paid nullable: true
        sent nullable: true
        received nullable: true
        cancelled nullable: true
        evaluation nullable: true
    }
    
    static hasMany = [orderArticles: OrderArticle]

    Long orderId
    UserEntity seller
    UserEntity buyer
    ShippingMethod shippingMethod
    Evaluation evaluation
    Integer articleValue = 0
    Integer totalValue = 0
    OrderStatus status

    Date bought
    Date paid
    Date sent
    Date received
    Date cancelled

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof McmOrder)) return false

        McmOrder mcmOrder = (McmOrder) o

        if (articleValue != mcmOrder.articleValue) return false
        if (bought != mcmOrder.bought) return false
        if (buyer != mcmOrder.buyer) return false
        if (cancelled != mcmOrder.cancelled) return false
        if (evaluation != mcmOrder.evaluation) return false
        if (orderId != mcmOrder.orderId) return false
        if (paid != mcmOrder.paid) return false
        if (received != mcmOrder.received) return false
        if (seller != mcmOrder.seller) return false
        if (sent != mcmOrder.sent) return false
        if (shippingMethod != mcmOrder.shippingMethod) return false
        if (status != mcmOrder.status) return false
        if (totalValue != mcmOrder.totalValue) return false

        return true
    }

    int hashCode() {
        return orderId.hashCode()
    }
}
