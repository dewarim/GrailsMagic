package de.dewarim.grailsmagic.mkm

class ProductName {

    static constraints = {
    }
    static belongsTo = [product:Product]
    
    Long languageId
    String languageName
    String name

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ProductName)) return false

        ProductName that = (ProductName) o

        if (languageId != that.languageId) return false
        if (languageName != that.languageName) return false
        if (name != that.name) return false
        if (product != that.product) return false

        return true
    }

    int hashCode() {
        return name.hashCode()
    }
}
