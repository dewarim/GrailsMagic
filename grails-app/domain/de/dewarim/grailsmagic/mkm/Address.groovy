package de.dewarim.grailsmagic.mkm

class Address {

    static constraints = {
    }
    
    String name
    String extra
    String street
    String zip
    String city
    String country

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Address)) return false

        Address address = (Address) o

        if (city != address.city) return false
        if (country != address.country) return false
        if (extra != address.extra) return false
        if (name != address.name) return false
        if (street != address.street) return false
        if (zip != address.zip) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (street != null ? street.hashCode() : 0)
        return result
    }
}
