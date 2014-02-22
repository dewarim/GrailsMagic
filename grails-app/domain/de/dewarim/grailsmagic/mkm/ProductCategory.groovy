package de.dewarim.grailsmagic.mkm

class ProductCategory {

    static constraints = {
    }
    
    static belongsTo = [product:Product]
    
    Long categoryId
    String name
    
}
