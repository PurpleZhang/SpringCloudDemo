org.springframework.cloud.contract.spec.Contract.make {
    description "should increase inventory of a product"
    request {
        method POST()
        url "/api/product/2/increaseInventory"
        body(5)
        headers {
            contentType("application/json;charset=UTF-8")
        }
    }
    response {
        status OK()
        body "Inventory increased successfully"
    }
}