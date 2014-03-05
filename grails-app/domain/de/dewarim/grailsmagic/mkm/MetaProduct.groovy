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

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof MetaProduct)) return false

        MetaProduct that = (MetaProduct) o

        if (metaProductId != that.metaProductId) return false

        return true
    }

    int hashCode() {
        return metaProductId.hashCode()
    }
}
