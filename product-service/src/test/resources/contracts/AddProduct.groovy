org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url '/api/product'
        headers {
            contentType(applicationJson())
        }
        body([
            name: "productName",
            price: 10.99,
            inventory: 10
        ])
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
            id: $(producer(anyNumber())),
            name: fromRequest().body('$.name'),
            price: fromRequest().body('$.price'),
            inventory: fromRequest().body('$.inventory')
        ])
    }
}