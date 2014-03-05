package de.dewarim.grailsmagic.mkm

class Game {

    static constraints = {
        name unique: true
    }
    
    Long gameId
    String name

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Game)) return false

        Game game = (Game) o

        if (gameId != game.gameId) return false
        if (name != game.name) return false

        return true
    }

    int hashCode() {
        return gameId.hashCode()
    }
}
