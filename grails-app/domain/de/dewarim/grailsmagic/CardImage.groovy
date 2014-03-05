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

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CardImage)) return false

        CardImage cardImage = (CardImage) o

        if (!Arrays.equals(imageData, cardImage.imageData)) return false
        if (imageSize != cardImage.imageSize) return false
        if (name != cardImage.name) return false
        if (type != cardImage.type) return false

        return true
    }

    int hashCode() {
        return name.hashCode()
    }
}
