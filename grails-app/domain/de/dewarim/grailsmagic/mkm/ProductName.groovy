package de.dewarim.grailsmagic.mkm

class ProductName {

    static constraints = {
    }
    static belongsTo = [product:Product]
    
    Long languageId
    String languageName
    String name
    
}
