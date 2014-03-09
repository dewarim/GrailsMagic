package de.dewarim.grailsmagic.mkm.orders

class ShippingMethod {

    static constraints = {
    }
    
    String name
    Integer price

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ShippingMethod)) return false

        ShippingMethod that = (ShippingMethod) o

        if (name != that.name) return false
        if (price != that.price) return false

        return true
    }

    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + price.hashCode()
        return result
    }
}
