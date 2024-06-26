# Booking

This is a booking system that allows users to book a property for a specific date. The system is built using Java and SpringBoot Framework.

## Building the application

To build the application, you can use the following command:

```bash
mvn clean install
```

## Running the application

To run the application, you can use the following command:

```bash
mvn spring-boot:run
```

## Testing the api

Theres is a swagger UI that can be accessed by visiting the following URL:

```
http://localhost:8080/swagger-ui/index.html
```

## Accessing the database for the application

The application uses an in-memory database called H2. You can access the database by visiting the following URL:

```
http://localhost:8080/h2-console
```

**Note:** The JDBC URL is `jdbc:h2:mem:bookingdb` and the username is `sa` and the password is `admin`.

## Technologies

- Java 17
- [Spring Boot](https://spring.io/projects/spring-boot): used to create the RESTful Web Services
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa): used to interact with the database
- [H2 Database](https://www.h2database.com/html/main.html): used as the in-memory database
- [Lombok](https://projectlombok.org): used to mostly create getter and setter methods
- [Junit 5](https://junit.org/junit5): used for testing
- [Mockito](https://site.mockito.org): used for mocking objects in testing
- [Maven](https://maven.apache.org): used for building the project
- [Swagger](https://swagger.io): used to document the RESTful Web Services