package de.dewarim.grailsmagic.mkm

import de.dewarim.grailsmagic.CardImage

class Product {

    static constraints = {
        image nullable: true
        rarity blank: true
        expansion blank: true
    }
    
    static hasMany = [names:ProductName, categories: ProductCategory, priceGuides:PriceGuide]
    
    Long productId
    Long metaProductId     
    Date lastRefresh = new Date()
    CardImage image
    String imagePath
    String expansion
    String rarity
    
    String getOriginalName(){
        return names.find{
            it.languageName == 'English'
        }?.name
    }
    
    String getCardImageName(){
        return expansion+"_"+originalName
    }
}
