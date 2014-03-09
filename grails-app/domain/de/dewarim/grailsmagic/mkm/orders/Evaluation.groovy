package de.dewarim.grailsmagic.mkm.orders

class Evaluation {

    static constraints = {
        overall nullable: true, blank: true
        articles nullable: true, blank: true
        packaging nullable: true, blank: true
        speed nullable: true, blank: true
        comments nullable: true, blank: true
    }
    
    String overall
    String articles
    String packaging
    String speed
    String comments

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Evaluation)) return false

        Evaluation that = (Evaluation) o

        if (articles != that.articles) return false
        if (comments != that.comments) return false
        if (overall != that.overall) return false
        if (packaging != that.packaging) return false
        if (speed != that.speed) return false

        return true
    }

    int hashCode() {
        int result
        result = (overall != null ? overall.hashCode() : 0)
        result = 31 * result + (articles != null ? articles.hashCode() : 0)
        result = 31 * result + (packaging != null ? packaging.hashCode() : 0)
        result = 31 * result + (speed != null ? speed.hashCode() : 0)
        result = 31 * result + (comments != null ? comments.hashCode() : 0)
        return result
    }
}
