import static org.springframework.cloud.contract.spec.Contract.*

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url '/api/product'
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
            [
                id: 1,
                name: "Laptop",
                price: 999.99,
                inventory: 10
            ],
            [
                id: 2,
                name: "Smartphone",
                price: 499.99,
                inventory: $(producer(anyNumber()))
            ]
        ])
    }
}
