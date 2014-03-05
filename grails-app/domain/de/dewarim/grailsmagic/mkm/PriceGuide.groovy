package de.dewarim.grailsmagic.mkm

class PriceGuide {

    static constraints = {
    }
    
    Product product
    Integer sell = 0
    Integer low = 0
    Integer average = 0
    Date dateCreated

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof PriceGuide)) return false

        PriceGuide that = (PriceGuide) o

        if (average != that.average) return false
        if (low != that.low) return false
        if (product != that.product) return false
        if (sell != that.sell) return false

        return true
    }

    int hashCode() {
        int result
        result = sell.hashCode()
        result = 31 * result + low.hashCode()
        result = 31 * result + average.hashCode()
        return result
    }
}
