package de.dewarim.grailsmagic



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CardImageController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond CardImage.list(params), model:[cardImageCount: CardImage.count()]
    }

    def show(CardImage cardImage) {
        respond cardImage
    }

    def create() {
        respond new CardImage(params)
    }

    @Transactional
    def save(CardImage cardImage) {
        if (cardImage == null) {
            notFound()
            return
        }

        if (cardImage.hasErrors()) {
            respond cardImage.errors, view:'create'
            return
        }

        cardImage.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cardImage.label', default: 'CardImage'), cardImage.id])
                redirect cardImage
            }
            '*' { respond cardImage, [status: CREATED] }
        }
    }

    def edit(CardImage cardImage) {
        respond cardImage
    }

    @Transactional
    def update(CardImage cardImage) {
        if (cardImage == null) {
            notFound()
            return
        }

        if (cardImage.hasErrors()) {
            respond cardImage.errors, view:'edit'
            return
        }

        cardImage.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'cardImage.label', default: 'CardImage'), cardImage.id])
                redirect cardImage
            }
            '*'{ respond cardImage, [status: OK] }
        }
    }

    @Transactional
    def delete(CardImage cardImage) {

        if (cardImage == null) {
            notFound()
            return
        }

        cardImage.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'cardImage.label', default: 'CardImage'), cardImage.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cardImage.label', default: 'CardImage'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
