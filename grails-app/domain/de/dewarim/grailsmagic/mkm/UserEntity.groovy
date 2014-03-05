package de.dewarim.grailsmagic.mkm

/**
 * Created by ingo on 20.02.14.
 */
class UserEntity {
    
    static constraints = {
        firstName nullable: true
        lastName nullable: true
    }
    
    Long userId
    String username
    String country
    Boolean commercial
    Integer riskGroup
    Integer reputation
    String firstName
    String lastName

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof UserEntity)) return false

        UserEntity that = (UserEntity) o

        if (commercial != that.commercial) return false
        if (country != that.country) return false
        if (firstName != that.firstName) return false
        if (lastName != that.lastName) return false
        if (reputation != that.reputation) return false
        if (riskGroup != that.riskGroup) return false
        if (userId != that.userId) return false
        if (username != that.username) return false

        return true
    }

    int hashCode() {
        int result
        result = userId.hashCode()
        result = 31 * result + username.hashCode()
        return result
    }
}
