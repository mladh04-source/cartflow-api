# CartFlow API 

A standalone **shopping cart REST API** built with Java 21, Spring Boot, Spring Data JPA, MySQL, Flyway and Maven. It uses a layered architecture and does not depend on any external starter project.

## Features

- Create a cart with a UUID (no login required)
- Add products; adding an existing product increments its quantity
- Read carts, including item and cart totals
- Change item quantities (allowed range: 1–100)
- Remove an item or clear the cart
- Create and list products for local testing
- Request validation, structured error responses and SQL migrations
- Service-layer unit tests with JUnit 5 and Mockito

## Architecture

```text
src/main/java/dev/cartflow/api/
├── CartflowApplication.java
├── controllers/       # REST endpoints and exception handler
├── dtos/              # API request and response models
├── entities/          # JPA entities
├── repositories/      # Spring Data JPA
└── services/          # Cart business logic and domain errors
src/main/resources/
├── application.yaml
└── db/migration/      # Flyway schema migrations
src/test/java/dev/cartflow/api/  # Unit tests
```

## Requirements

- Java 21
- MySQL 8+
- Maven (or the included Maven Wrapper)

## Setup

1. Clone the repository and open it in IntelliJ IDEA (as a Maven project).
2. Start MySQL. The default connection creates the `cartflow_db` database if the MySQL account has permission.
3. Configure `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` as **environment variables**. See `.env.example` for example values. The `.env.example` file is **not loaded automatically** by Spring Boot.
4. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows: `mvnw.cmd spring-boot:run`.

Flyway runs automatically at startup and creates the `products`, `carts` and `cart_items` tables. The app uses `spring.jpa.hibernate.ddl-auto=validate` to check the mapping against the schema.

## API endpoints

| Method | Endpoint | Description | Success |
|---|---|---|---|
| `POST` | `/products` | Create a product | 201 |
| `GET` | `/products` | List products | 200 |
| `GET` | `/products/{id}` | Get product | 200 |
| `POST` | `/carts` | Create empty cart | 201 |
| `GET` | `/carts/{cartId}` | View cart and totals | 200 |
| `POST` | `/carts/{cartId}/items` | Add a product (quantity +1) | 200 |
| `PUT` | `/carts/{cartId}/items/{productId}` | Set item quantity (1–100) | 200 |
| `DELETE` | `/carts/{cartId}/items/{productId}` | Remove one item | 204 |
| `DELETE` | `/carts/{cartId}/items` | Clear cart | 204 |

### Example flow

**1. Create a product**

```bash
curl -i -X POST http://localhost:8080/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Keyboard","description":"Mechanical keyboard","price":49.90}'
```

**2. Create an empty cart**

```bash
curl -i -X POST http://localhost:8080/carts
```

Response example (201 Created):

```json
{"id":"550e8400-e29b-41d4-a716-446655440000","items":[],"totalPrice":0}
```

**3. Add the product** (replace the UUID with the returned cart ID):

```bash
curl -i -X POST http://localhost:8080/carts/550e8400-e29b-41d4-a716-446655440000/items \
  -H 'Content-Type: application/json' \
  -d '{"productId":1}'
```

**4. Set quantity**

```bash
curl -i -X PUT http://localhost:8080/carts/550e8400-e29b-41d4-a716-446655440000/items/1 \
  -H 'Content-Type: application/json' \
  -d '{"quantity":3}'
```

**5. View or clear the cart**

```bash
curl http://localhost:8080/carts/550e8400-e29b-41d4-a716-446655440000
curl -i -X DELETE http://localhost:8080/carts/550e8400-e29b-41d4-a716-446655440000/items
```

### Error responses

- `404 Not Found` for missing carts/items
- `400 Bad Request` for missing products or invalid requests
- `204 No Content` for successful deletion

## Tests

```bash
./mvnw test
```

## GitHub

```bash
git init
git add .
git commit -m "Initial CartFlow API implementation"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/cartflow-api.git
git push -u origin main
```

Do not commit credentials or local `.env` files.

## Design decisions

- `Cart.id` uses `UUID`, persisted as MySQL `BINARY(16)`.
- Each cart may contain multiple cart items; a product appears at most once per cart.
- The cart does not require user registration or authentication.
- Product prices and totals use `BigDecimal` to avoid floating-point rounding problems.
