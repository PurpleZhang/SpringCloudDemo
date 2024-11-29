Spring Cloud Demo Application Readme

1.	Declaration:
  a)	Java version 11 jdk is installed
  b)	Maven is installed
  c)	Spring Boot version: 2.7.15
  d)	Spring Cloud version: 2021.0.5
  e)	Zipkin: 2.23.0

2.	Application description:
  a)	Spring Cloud Eureka: Eureka folder
    i.	Start: go to Eureka folder, run “mvn spring-boot:run”
    ii.	It is to start Spring Cloud Eureka server first for services to do the registration and founding.
    iii.	Eureka console: http://localhost:8761

b)	Spring Cloud Config: Config folder
i.	Start: go to Config folder, run “mvn spring-boot:run”
ii.	It is to start Spring Cloud Config server first for services. Product, user and order services config files are in this server, and they are depending on this service.
iii.	Config uri: http://localhost:8088 

c)	Start Zipkin server:
i.	This is to start Zipkin server. Product, user and order services accessing traces will be collected to it.
ii.	Use docker to pull and run zipkin
1.	docker pull openzipkin/zipkin:2.23.0
2.	docker run -d -p 9411:9411 openzipkin/zipkin:2.23.0
iii.	Zipkin console: http://localhost:9411

d)	Product service: product-service folder
i.	Start: go to product-service folder, run “mvn spring-boot:run”
ii.	Port: 8082

e)	User service: user-service folder
i.	Start: go to user-service folder, run “mvn spring-boot:run”
ii.	Port: 8081

f)	Order service: order-service folder
i.	Start: go to order -service folder, run “mvn spring-boot:run”
ii.	Port: 8083

g)	Spring Cloud Gateway: Gateway folder
i.	Start: go to Gateway folder, run “mvn spring-boot:run”
ii.	It is to start Spring Cloud Gateway server. Those 3 functional services can be all accessed with 8080 port after this.

h)	Integration test: integration-test folder
i.	Start: go to integration-test folder, run “mvn test” after all services are running.

3.	More Spring Cloud Technical Details
a)	Load Balancing: Spring Cloud LoadBalancer
i.	Introduced in order-service pom.xml
ii.	The annotation is in AppConfig.java
iii.	The configuration is in order-service.properties on Spring Cloud Config server.

b)	Circuit Breaker: Resilience4j
i.	Introduced in order-service pom.xml
ii.	The @CircuitBreaker annotation is on order-service OrderService.java validateUser, validateProduct, reduceInventory and increaseInventory methods.
iii.	The configurations are in order-service.properties on Spring Cloud Config server.

c)	Logging and Monitoring: Spring Cloud Sleuth and Zipkin
i.	They are introduced in product-service, user-service and order-service for accessing trace collection.

d)	Database Integration: H2 in-memory database

4.	Service Endpoints 
a)	Product-service:
i.	Get all products: GET http://localhost:8082/api/product
1.	Success response is 200 with a list of all products

ii.	Get one product: GET http://localhost:8082/api/product/{id}
1.	Success response is 200 with the product 
2.	If not found, 404 is returned.

iii.	Create a product: POST http://localhost:8082/api/product/ 
1.	Sample body: {"name": "Chair","price": 64.53, "inventory": 15}
2.	Success response is 200 with the product data

iv.	Delete a product: DELETE http://localhost:8082/api/product/{id}
1.	The id will be checked if it is used in any order from order-service. 
2.	If exists, response is 400 with error message "Product cannot be deleted as it is associated with an order"
3.	Success response is 204 without content 

v.	Update a product: PUT http://localhost:8082/api/product/{id}
1.	Sample body: {"name": "Chair","price": 64.53, "inventory": 15}
2.	If not found, 404 is returned.
3.	Success response is 200 with product data

vi.	Reduce Inventory: 
1.	POST http://localhost:8080/api/product/{id}/reduceInventory 
2.	Sample body: 5
3.	If the inventory will be negative, the action will fail, and the response is 409 with error message: “Insufficient inventory for product ID: 1”. 
4.	Success response is 200 with message: “Inventory reduced successfully”.
5.	TODO: check body number must be positive integer

vii.	Increase Inventory:
1.	POST http://localhost:8080/api/product/{id}/increaseInventory 
2.	Sample body: 5
3.	Success response is 200 with message: “Inventory increased successfully”.
4.	TODO: check body number must be positive integer

b)	User-service
i.	Get all users: GET http://localhost:8081/api/user 
1.	Success response is 200 with a list of all users

ii.	Get one user: GET http://localhost:8081/api/user/{id}
1.	Success response is 200 with the user 
2.	If not found, 404 is returned.

iii.	Create a user: POST http://localhost:8081/api/user/ 
1.	Sample body: {"username":"admin3","password": "admin","role": "ADMIN"}
2.	Success response is 200 with the product data
3.	If the username is duplicated, 500 will be returned.
4.	TODO: if the username is duplicated, return a specific error message.

iv.	TODO: Delete a user: DELETE http://localhost:8081/api/user/{id}
1.	TODO: It is not implemented in controller for integrity with order. And the plan is to implement the similar mechanism with product-service. 
2.	The id will be checked if it is used in any order from order-service. 
3.	If exists, response is 400 with error message "User cannot be deleted as it is associated with an order"
4.	Success response is 204 without content 

v.	Update a user: PUT http://localhost:8081/api/user/{id}
1.	Sample body: {"username":"admin3","password": "admin","role": "ADMIN"}
2.	If not found, 404 is returned.
3.	Success response is 200 with product data
4.	If the username is duplicated, 500 will be returned.
5.	TODO: if the username is duplicated, return a specific error message.

vi.	Login: POST http://localhost:8081/api/user/
1.	Sample body: {"username":"admin","password": "admin"}
2.	If valid, 401 is returned.
3.	Success response is 200 with user data
4.	TODO: password compare need to use encrypt/hash way as follow
5.	TODO: Handle user role in all service calls. It is processed in demo-app


vii.	TODO: encrypted/hashed password saving.
1.	This needs to be done with the official authentication together. The authentication verify needs to use the same encryption/hashing method.
2.	Or the user service just stores user information, like email, address…etc. The authentication and password can be dedicated to an authentication service.
3.	The role can be used for authorization, like if the authenticated user can call the manage end points, like update product, update user….etc

c)	Order-service
i.	Get all orders: GET http://localhost:8083/api/order/
1.	Success response is 200 with the orders 

ii.	Get one order: GET http://localhost:8083/api/order/{id}
1.	Success response is 200 with the order 
2.	If not found, 404 is returned.

iii.	Create an order: POST http://localhost:8083/api/order
1.	Sample data: {"userId": 1, "productId": 1, "quantity": 2}
2.	Success response is 200 with the order. Default status is PENDING
3.	The product-service reduceInventory will be called before creation and the 2 actions are in one transaction.

iv.	Check productid: 
1.	GET http://localhost:8080/api/order/check-product?productId={productId}
2.	If order with the productId exists, return true
3.	If no order with the productId, return false
4.	This will be called in product-service delete-product end point.

v.	Update order status: PUT http://localhost:8083/api/order/{id}/status
1.	Valid body: CANCELLED, COMPLETED, PENDING
2.	If the order is not found return 404 with message "Order not found with id {id}"
3.	If body is not valid, return 400 with message: "Invalid order status: {body}"
4.	If the order status is not CANCELLED and changed to CANCELLED, call product-service increaseInventory. The 2 actions are in one transaction.
5.	TODO: if the order status is COMPLETED, the status should not be updated, and specific error message should be returned. This is done in demo-app

vi.	TODO: Get orders for a user GET http://localhost:8083/api/order/user/{userid}
1.	Success response is 200 with a list of orders


5.	Tests
a)	Unit tests are created for services and controllers
b)	Integration test is in integration-test project
c)	Spring Cloud Contract tests are created in product-service, and stub run in order-service.
i.	Generate and install contract stub in product-service: “mvn clean install”
ii.	The stub runner, ProductClientTest.java, in order-service will be run with unit tests together.
d)	Test reports: test-reports folder
i.	com.example.integrationtests.OrderIntegrationTest: Integration test
ii.	com.example.orderservice.ProductClientTest: Contract stub runner
iii.	com.example.orderservice.controller.OrderControllerTest: Order-service controller unit tests
iv.	com.example.orderservice.service.OrderServiceTest: Order-service service unit tests
v.	com.example.productservice.ContractVerifierTest: Auto-generated contract tests for product-service
vi.	com.example.productservice.controller.ProductControllerTest: Product-service controller unit tests
vii.	com.example.productservice.service.ProductServiceIntegrationTest: Product-service service unit tests
viii.	com.example.userservice.controller.UserControllerTest.txt: User-service controller unit tests
ix.	com.example.userservice.service.UserServiceTest.txt: User-service service unit tests

6.	Optional: React client app
a)	In folder demo-app
b)	Launch: 
i.	Go into demo-app folder
ii.	npm install
iii.	npm start
iv.	Url: http://localhost:3000/

c)	Functions:
i.	Login with ADMIN role user will show All List
ii.	Login with other role users will not show All List
iii.	Logout
iv.	Product create, edit, delete(will fail if is used in orders)
1.	TODO: The inventory can be negative, need to validate
v.	User create, edit
vi.	Order create, cancel, complete
1.	Create may fail if user or product does not exit
2.	Create may fail if inventory is not enough
3.	Create an order, the product inventory will be changed
4.	Create an order with negative quantity will fail
5.	Cancel an order the product inventory will be changed

7.	Tips:
a)	Tomcat v10 may have issues on mac silicon chip desktop. Rest service always returns 404. Spring boot 3.x is based on tomcat v10. So Spring boot 2.7.15 is used.
b)	Java version higher than 11 does not work with Spring Cloud Contract 3.1.5. Spring Cloud Contract 3.1.5 is compatible with Spring Boot 2.7.15 and Spring Cloud 2021.0.5. 
c)	Ribbon seems not working with Spring Boot 2.7.15 and Spring Cloud 2021.0.5, use Spring Cloud LoadBalancer instead.


 
