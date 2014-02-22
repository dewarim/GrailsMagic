package de.dewarim.grailsmagic.mkm

class Product {

    static constraints = {
    }
    
    static hasMany = [names:ProductName, categories: ProductCategory, priceGuides:PriceGuide]
    
    Long productId
    Long metaProductId     
    Date lastRefresh = new Date()
    String imagePath
    String expansion
    String rarity
}
