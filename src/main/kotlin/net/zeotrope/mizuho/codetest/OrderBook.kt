package net.zeotrope.mizuho.codetest

import java.time.Instant

class OrderBook {

    private val orders = mutableMapOf<Long, Order>()

    fun addOrder(order: Order) =
        orders.put(order.id, order)

    fun removeOrder(id: Long) =
        orders.remove(id)

    fun updateOrderSize(id: Long, size: Long) =
        orders[id]?.let {
            orders.put(it.id, it.copy(size = size, timeStamp = Instant.now()))
        }

    fun bestPriceOrder(side: OrderType, level: Int): Double? {
        if (level < 1)
            return null
        return sortByPrice(side).getOrNull(level - 1)?.price
    }

    fun sizeForOrderLevel(side: OrderType, level: Int): Long? {
        if (level < 1)
            return null
        return sortByPrice(side).getOrNull(level -1 )?.size
    }

    fun ordersForSide(side: OrderType) =
        sortByPrice(side)

    private fun sortByPrice(side: OrderType): List<Order> {
        val sorted = orders
            .filter { it.value.side == side }
            .map { it.value }
            .toSet()
        if (side == OrderType.OFFER)
            return sorted.sorted()
        return sorted.sortedDescending().sortedBy { it.timeStamp }
    }

    fun getOrder(id: Long) =
        orders[id]

    fun orderBookSize() =
        orders.size
}
