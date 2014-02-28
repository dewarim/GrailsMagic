package de.dewarim.grailsmagic.mkm

class Game {

    static constraints = {
        name unique: true
    }
    
    Long gameId
    String name
}
