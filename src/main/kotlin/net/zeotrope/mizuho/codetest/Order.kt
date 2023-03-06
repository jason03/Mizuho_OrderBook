package net.zeotrope.mizuho.codetest

import java.time.Instant

data class Order(val id: Long, val price: Double, val side: OrderType, val size: Long, val timeStamp: Instant): Comparable<Order>{
    override fun compareTo(other: Order) =
        compareValuesBy(this, other, { it.price }, { it.timeStamp })
}

enum class OrderType(val side: Char){
    BID('B'),
    OFFER('O')
}