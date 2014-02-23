package de.dewarim.grailsmagic

class ImageController {

    def show(Long id) {
        def image = CardImage.get(id)
        if(! image){
            render(status:404)
            return
        }
        response.setHeader('Content-length', image.imageData.length.toString())
        response.contentType = 'image/'+image.type.name().toLowerCase()
        response.outputStream << image.imageData
        response.outputStream.flush()
    }
}
