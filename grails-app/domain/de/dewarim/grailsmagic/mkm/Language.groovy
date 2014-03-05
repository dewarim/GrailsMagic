package de.dewarim.grailsmagic.mkm

class Language {

    static constraints = {
    }
    
    Long idLanguage
    String languageName

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Language)) return false

        Language language = (Language) o

        if (idLanguage != language.idLanguage) return false
        if (languageName != language.languageName) return false

        return true
    }

    int hashCode() {
        return idLanguage.hashCode()
    }
}
