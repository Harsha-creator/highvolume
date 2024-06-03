cURL for creating an order: curl --location 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data '{
    "id":"4",
    "customerId": "c126",
    "items": [
        {"productId": "P002", "quantity": 100}
    ]
}'

cURL for updating the stock levels in the database: curl --location 'http://localhost:8080/inventory/update' \
--header 'Content-Type: application/json' \
--data '{
    "productId":"P003",
    "newStockLevel": 300,
    "warehouseId" : "W002"
}'
