package de.dewarim.grailsmagic.mkm

class PriceGuide {

    static constraints = {
    }
    
    Product product
    Integer sell
    Integer low
    Integer average
    Date dateCreated
    
}
