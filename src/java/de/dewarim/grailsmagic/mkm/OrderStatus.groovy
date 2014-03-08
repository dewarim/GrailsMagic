package de.dewarim.grailsmagic.mkm

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
}
