# ShopNest product catalog

ShopNest is a Spring Boot REST API for managing products and categories in MySQL. It supports soft deletion, product pitches generated through Groq, and a supplier import endpoint. The project targets Java 17 and is packaged as a WAR.

## Getting started

1. Install Java 17 or later and start MySQL. Create the database used by the default configuration:

   ```sql
   CREATE DATABASE productcatalogservicetest;
   ```

2. Configure the datasource in `src/main/resources/application.properties` or override its Spring properties for your environment. The development defaults connect as MySQL `root` and enable Hibernate schema updates and SQL logging.
3. Copy `.env.example` to `.env` and set `GROQ_API_KEY`. The application loads this file automatically; it is gitignored. Set `AUTH_SERVICE_URL` if the authentication service is not at `http://localhost:8081`.
4. Start the application using the Maven wrapper:

   ```sh
   ./mvnw spring-boot:run
   ```

The API listens on port 8080 by default. For the `prod` profile, set `DB_PASSWORD` and configure a MySQL `shopnest_app` user; Hibernate validates the existing schema rather than updating it. Activate the profile with `SPRING_PROFILES_ACTIVE=prod`.

## API

| Method | Path | Description |
| --- | --- | --- |
| GET | `/products` | List active products, with optional `minPrice`, `maxPrice`, `page`, `size`, and `sort` query parameters. Returns an array rather than page metadata. |
| GET | `/products/{id}` | Get an active product. |
| POST | `/products` | Create a product. Returns 201. |
| PUT | `/products/{id}` | Replace an active product. |
| PATCH | `/products/{id}` | Partially update an active product. |
| DELETE | `/products/{id}` | Mark an active product inactive. Returns 204. |
| POST | `/products/import` | Import products from DummyJSON; see the limitation below. |
| GET | `/products/{id}/pitch` | Generate a marketing notification; a canned message is returned if the Groq call fails. |

GET requests bypass the token filter. Other methods require an `Authorization` header, which is forwarded to `GET /auth/validateToken` on the configured auth service. DELETE additionally requires the returned user to have the `ADMIN` role. Missing or rejected tokens return 401, a non-admin DELETE returns 403, and auth service errors return 503.

A product request can contain `name`, `description`, `price`, `imageUrl`, and a `category` object with `name` and `description`. Categories are resolved by name or created when needed. For PATCH, an omitted field is unchanged and an explicit `null` clears it. `"category": null` detaches the category; nested category patches resolve by the supplied name or the current category name. A null category name is ignored.

Example:

```sh
curl -X PATCH http://localhost:8080/products/1 \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"price": 29.99, "description": null}'
```

**Import limitation:** the current DummyJSON adapter casts a list to a `Page`, which causes the import request to fail. Its price and pagination parameters are also not applied by the adapter.

## Build and test

```sh
./mvnw -B test
./mvnw -B package
```

The full test suite includes a Spring context test that requires the configured MySQL database. To run the mapper unit test without a database:

```sh
./mvnw -B test -Dtest=ProductMapperTest
```

The resulting WAR can be deployed to an external Tomcat; `spring-boot:run` starts the application with embedded Tomcat.
