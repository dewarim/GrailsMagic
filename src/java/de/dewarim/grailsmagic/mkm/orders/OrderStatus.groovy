package de.dewarim.grailsmagic.mkm.orders

import de.dewarim.grailsmagic.mkm.UserEntity

/**
 */
enum OrderStatus {

    // Official: Bought, Paid, Sent, Received, Lost, Cancelled
    Unknown(0),
    Bought(1),
    Paid(2),
    Sent(4),
    Received(8),
    Lost(32),
    Cancelled(128);

    Integer state
    
    OrderStatus(Integer state) {
        this.state = state
    }

    /**
     * Find OrderStatus by state value.
     * @param state
     * @return the orderStatus defined by the unique state value - or OrderStatus.Unknown
     */
    static OrderStatus findByState(state){
        def status = values().find{it.state == state}
        return status ? status : Unknown
    }

    /**
     * Find OrderStatus by case insensitive name
     * @param name
     * @return the OrderStatus with the given name - or OrderStatus.Unknown
     */
    static OrderStatus findByName(String name){
        def status = values().find{it.name().toLowerCase() == name?.toLowerCase()}
        return status ? status : Unknown
    }
    
}
