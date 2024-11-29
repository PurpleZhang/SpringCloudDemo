org.springframework.cloud.contract.spec.Contract.make {
    description "should return product details by id"
    request {
        method GET()
        url("/api/product/1")
    }
    response {
        status OK()
        body(
            id: $(producer(anyNumber())),
            name: "Laptop",
            price: 999.99,
            inventory: $(producer(anyNumber()))
        )
        headers {
            contentType(applicationJson())
        }
    }
}


