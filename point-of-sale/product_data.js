// Load base set of test data for products
// Point of Sale system
// 07 May 2014, version 1.0

conn = new Mongo()
db = conn.getDB("pos_test")
db.products.drop()
db.products.insert({ "sku" : "12345", "A package of breath mints" : "Breath Mints", "unit_price" : "1.49", "taxable" : true })
db.products.insert({ "sku" : "12346", "Single serving bag of potato chips" : "Potato Chips", "unit_price" : "1.19", "taxable" : true })
db.products.insert({ "sku" : "12347", "A refreshing beverage" : "20-oz. Soft Drink", "unit_price" : "1.99", "taxable" : true })
db.products.insert({ "sku" : "12348", "Grocery item" : "Loaf of white bread", "unit_price" : "1.59", "taxable" : false })
db.products.insert({ "sku" : "12349", "Grocery item" : "Bag of pistachios", "unit_price" : "2.89", "taxable" : false })
db.products.insert({ "sku" : "22334", "Tools" : "Phillips head screwdriver", "unit_price" : "5.25", "taxable" : true })
db.products.insert({ "sku" : "22335", "Tools" : "Flat head screwdriver", "unit_price" : "5.25", "taxable" : true })
db.products.insert({ "sku" : "22336", "Book" : "How Al Gore Created the Internet", "unit_price" : "16.88", "taxable" : true })
