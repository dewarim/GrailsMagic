package de.dewarim.grailsmagic

class CardImage {

    static constraints = {
        imageData maxSize: 10_000_000
        name nullable: true
    }
    
    byte[] imageData
    
    /**
     * Name should be the original english card name to help with
     * indexing / search.
     */
    String name    
    ImageType type
    ImageSize imageSize
    
}
