# JpaJoinEntityPattern

Spring Boot sample project demonstrating a clean and explicit
many-to-many relationship using a join entity instead of @ManyToMany.

This project focuses on real-world backend concerns such as partial updates,
soft delete, validation, and consistent error handling.

---

## Key Concepts

- **Join Entity Pattern**  
  Product–Category relationship is modeled explicitly via `ProductCategory`

- **Partial Update (PATCH)**  
  Only provided fields are updated, null values are ignored

- **Soft Delete**  
  Products are logically deleted and automatically filtered from queries

- **Validation**  
  Request body, path variables, and query parameters are validated

- **Centralized Exception Handling**  
  Consistent API error responses using `@RestControllerAdvice`

- **MapStruct**  
  Compile-time safe DTO mapping with strict unmapped field checks

---

## Tech Stack

 - Java 17+
 - Spring Boot 3.5
 - Spring Data JPA 
 - Hibernate Validation 
 - MapStruct 
 - H2 H2 in-memory database


