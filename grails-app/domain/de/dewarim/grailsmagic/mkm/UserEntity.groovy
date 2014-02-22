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
        
}
