package net.zeotrope.mizuho.codetest

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class OrderBookTest {

    private lateinit var orderBook: OrderBook
    private val orders = listOf(
        Order(11110001, 1200.11, OrderType.OFFER, 10, LocalDateTime.of(2023,1,1,0,1,30, 0).toInstant(ZoneOffset.UTC)),
        Order(11110002, 1200.11, OrderType.OFFER, 20, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(11110003, 1500.03, OrderType.OFFER, 12, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(11110004, 1500.00, OrderType.OFFER, 14, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(11110005, 1423.00, OrderType.OFFER, 18, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(11110006, 1250.50, OrderType.OFFER, 19, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(11110007, 1440.50, OrderType.OFFER, 22, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(33300001, 1600.50, OrderType.BID, 10, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(33300002, 1550.75, OrderType.BID, 12, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(33300003, 1533.33, OrderType.BID, 13, LocalDateTime.of(2023,1,1,0,5,0, 0).toInstant(ZoneOffset.UTC)),
        Order(33300004, 1533.33, OrderType.BID, 15, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(33300005, 1580.00, OrderType.BID, 16, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC)),
        Order(33300006, 1590.90, OrderType.BID, 17, LocalDateTime.of(2023,1,1,0,0,0, 0).toInstant(ZoneOffset.UTC))
    )

    @BeforeEach
    fun `setup`() {
        orderBook = OrderBook()
        orders.map { orderBook.addOrder(it) }
    }

    @Test
    fun `should add an order to the order book`() {
        val order = Order(12345678, 123.45, OrderType.BID, 1, Instant.now())
        val initialOrderBookSize = orderBook.orderBookSize()
        val addOrderResult = orderBook.addOrder(order)
        assertAll(
            { assertNull(addOrderResult) },
            { assertEquals( initialOrderBookSize + 1, orderBook.orderBookSize() )}
        )
    }

    @Test
    fun `should remove an order from the order book`() {
        val preRemoveOrderBookSize = orderBook.orderBookSize()
        val removedOrder = orderBook.removeOrder(11110003)

        assertAll(
            { assertEquals(orders[2], removedOrder) },
            { assertEquals( preRemoveOrderBookSize -1, orderBook.orderBookSize() )}
        )
    }

    @Test
    fun `should fail to remove an order from the order book with an unknown id`() {
        val preRemoveOrderBookSize = orderBook.orderBookSize()
        val removedOrder = orderBook.removeOrder(1)

        assertAll(
            { assertNull(removedOrder) },
            { assertEquals( preRemoveOrderBookSize, orderBook.orderBookSize() )}
        )
    }

    @Test
    fun `should update the size of an existing order`() {
        orderBook.updateOrderSize(11110005, 255)
        val updatedOrder = orderBook.getOrder(11110005)

        assertEquals(255, updatedOrder?.size)
    }

    @Test
    fun `should fail to update the size of an order that does not exist`(){
        val updatedOrder = orderBook.updateOrderSize(123, 1000)

        assertNull(updatedOrder)
    }

    @Test
    fun `should return the price for a Bid at a given level`() {
        val bidOrderPrice = orderBook.bestPriceOrder(OrderType.BID, 2)

        assertEquals(orderBook.getOrder(33300006)?.price, bidOrderPrice)
    }

    @Test
    fun `should return null for a Bid with an out of range level`() {
        val bidOrderPriceLevel0 = orderBook.bestPriceOrder(OrderType.BID, 0)
        val bidOrderPriceLevel20 = orderBook.bestPriceOrder(OrderType.BID, 20)
        assertAll(
            { assertNull(bidOrderPriceLevel0) },
            { assertNull(bidOrderPriceLevel20) }
        )
    }

    @Test
    fun `should return the price for a Offer at a given level`() {
        val offerOrderPrice = orderBook.bestPriceOrder(OrderType.OFFER, 4)

        assertEquals(orderBook.getOrder(11110005)?.price, offerOrderPrice)
    }

    @Test
    fun `should return null for a Offer with an out of range level`() {
        val offerOrderPriceLevel0 = orderBook.bestPriceOrder(OrderType.OFFER, 0)
        val offerOrderPriceLevel10 = orderBook.bestPriceOrder(OrderType.OFFER, 10)
        assertAll(
            { assertNull(offerOrderPriceLevel0) },
            { assertNull(offerOrderPriceLevel10) }
        )
    }

    @Test
    fun `should return the size for a Bid level`() {
        val bidLevelSize = orderBook.sizeForOrderLevel(OrderType.BID, 3)

        assertEquals(bidLevelSize, orderBook.getOrder(33300005)?.size)
    }

    @Test
    fun `should return null for an out of range Bid level`() {
        val bidLevelSize = orderBook.sizeForOrderLevel(OrderType.BID, 0)

        assertNull(bidLevelSize)
    }

    @Test
    fun `should return the size for a Offer level`() {
        val offerLevelSize = orderBook.sizeForOrderLevel(OrderType.OFFER, 6)

        assertEquals(offerLevelSize, orderBook.getOrder(11110004)?.size)
    }

    @Test
    fun `should return null for an out of range Offer level`() {
        val bidLevelSize = orderBook.sizeForOrderLevel(OrderType.OFFER, 0)

        assertNull(bidLevelSize)
    }

    @Test
    fun `should return all Bids in the order book in level and time order`() {
        val bidOrders = orderBook.ordersForSide(OrderType.BID)

        assertAll(
            { assertEquals(6, bidOrders.size) },
            { assertEquals(1600.5, bidOrders[0].price) },
            { assertEquals(1533.33, bidOrders[5].price) },
            { assertEquals(bidOrders[0], orderBook.getOrder(33300001)) },
            { assertEquals(bidOrders[5], orderBook.getOrder(33300003)) }
        )
    }

    @Test
    fun `should return all Offers in the order book in level and time order`() {
        val offerOrders = orderBook.ordersForSide(OrderType.OFFER)

        assertAll(
            { assertEquals(7, offerOrders.size) },
            { assertEquals(offerOrders[0], orderBook.getOrder(11110002)) },
            { assertEquals(offerOrders[6], orderBook.getOrder(11110003)) }
        )
    }
}