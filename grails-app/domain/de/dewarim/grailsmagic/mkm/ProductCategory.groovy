package de.dewarim.grailsmagic.mkm

class ProductCategory {

    static constraints = {
    }
    
    static belongsTo = [product:Product]
    
    Long categoryId
    String name

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ProductCategory)) return false

        ProductCategory that = (ProductCategory) o

        if (categoryId != that.categoryId) return false
        if (name != that.name) return false
        if (product != that.product) return false

        return true
    }

    int hashCode() {
        int result
        result = categoryId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
