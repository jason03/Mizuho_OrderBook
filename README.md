### Build Instructions

This project uses Gradle as the build tool

#### To run the tests

From the project root directory

```./gradlew test```

### Part A

This project covers the specifed use cases:

* Given an Order, add it to the OrderBook (order additions are expected to occur extremely frequently)
* Given an order id, remove an Order from the OrderBook (order deletions are expected to occur at ap- proximately 60% of the rate of order additions)
* Given an order id and a new size, modify an existing order in the book to use the new size (size modi􏰁- cations do not e􏰀ect time priority)
* Given a side and a level (an integer value >0) return the price for that level (where level 1 represents the best price for a given side). For example, given side=B and level=2 return the second best bid price 
* Given a side and a level return the total size available for that level 
* Given a side return all the orders from that side of the book, in level- and time-order


#### Test coverage
Unit tests are located in:
`src/test/kotlin/net.zeotrope/mizuho/codetest/OrderBookTest.kt`

And over the following use cases:
* Add an order to the order book
* Remove an order from the order book
* Fail to remove an order with an unknown ID
* Update the size of an existing order
* Fail to update the size of an order with an unknown ID
* Return the price for a Bid with a specified level
* Fail to return a Bid when the level is out of range
* Return the price for an Offer with a specified level
* Fail to return an Offer when the level is out of range
* Return the size for a Bid with a specified level
* Fail to return a Bid size for an out of range level
* Return the size for an Offer with a specified level
* Fail to return an Offer size for an out of range level
* Return all Bids in the order book in level and time order
* Return all Offers in the order book in level and time order


### Part B

#### Suggested changes and modifications

The order book is currently implemented as an in memory Map of the Order ID and Order. A real world implementation would be more complex and implement a scalable architecture using distributed queue, database and caching with low level object locking to isolate order transactions.

Order could be changed to use:
* UUID for the ID
* BigDecimal for the price (Double can induce rounding inaccuracies)
* Enum for side (Already implemented)
* Inclusion of a timestamp of when the order was created (already implemented)

With the current order book implementation as the number of stored orders increases the amount of time and resource used to sort the data will increase impacting on the real time service performance. Improvements here could be to order the data as it's inserted and create projections of the order book for faster data retrieval. 

The current implementation doesn't collate orders that have identical price and timestamp.