package de.dewarim.grailsmagic.mkm

class MetaProductName {

    static constraints = {
    }
    static belongsTo = [metaProduct:MetaProduct]
    
    Long languageId
    String languageName
    String name
    
}
