# Azure functions (serverless) demo

Two functions were implemented:
- Add item: Adds an item to the specified category (Fruits, Cars, Dogs). The items are saved to a Table, the partition key is the category
- List items: List the items from the specified category.

Demo:
- POST to: https://software-engineering-sia-2021.azurewebsites.net/api/items?name=orange&category=fruits
- GET to: https://software-engineering-sia-2021.azurewebsites.net/api/getItems?category=cars

Possible categories are: fruits, cars, dogs (case insensitive)

**Remark: Using "getItems" as the path for the list items endpoint is not ideal. If a verb is present, then it is more of an RPC than HTTP. Unfortunately Azure Functions don't seem to support setting the same path with different HTTP methods.**



