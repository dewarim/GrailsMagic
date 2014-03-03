package de.dewarim.grailsmagic.mkm

class MetaProduct {

    static constraints = {
    }
    
    static hasMany = [names:MetaProductName]
    
    Long metaProductId
    
    String getOriginalName(){
        return names.find{
            it.languageName == 'English'
        }?.name
    }
    
    List<Product> fetchProducts(){
        return Product.findAllByMetaProduct(this)
    }
}
