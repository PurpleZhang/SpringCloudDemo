org.springframework.cloud.contract.spec.Contract.make {
    description "should reduce inventory of a product"
    request {
        method POST()
        url("/api/product/2/reduceInventory")
        body(3)
        headers {
            contentType("application/json;charset=UTF-8")
        }
    }
    response {
        status OK()
        body("Inventory reduced successfully")
    }
}

