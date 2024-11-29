org.springframework.cloud.contract.spec.Contract.make {
    description "should update an existing product"
    request {
        method PUT()
        url("/api/product/1")
        body(
            name: "Updated Product",
            price: 120.0,
            inventory: 8
        )
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body(
            id: $(producer(anyNumber())),
            name: "Updated Product",
            price: 120.0,
            inventory: 8
        )
        headers {
            contentType(applicationJson())
        }
    }
}



