package de.dewarim.grailsmagic.mkm

class MetaProductName {

    static constraints = {
    }
    static belongsTo = [metaProduct:MetaProduct]
    
    Long languageId
    String languageName
    String name

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof MetaProductName)) return false

        MetaProductName that = (MetaProductName) o

        if (languageId != that.languageId) return false
        if (languageName != that.languageName) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        return languageId.hashCode()
    }
}
