# Spring Boot — Udemy Course Projects

A collection of small Spring Boot projects built while following a Udemy
Spring Boot course, used to practice REST APIs, JPA/Hibernate, and common
backend patterns in Java.

Each folder is an independent, runnable Spring Boot application covering a
different set of concepts, roughly in the order they were built.

## Projects

### `chai-lms/`
A simple Student management REST API. Covers the fundamentals:
- Layered architecture: Controller → Service → Repository → Entity
- Full CRUD (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)
- Entity ↔ DTO mapping with **ModelMapper**
- Request validation with `jakarta.validation` (`@Valid`, `@NotNull`,
  `@Email`, `@Size`, etc.)
- Centralized exception handling via `@RestControllerAdvice`
- A standardized API response envelope (`ApiResponse<T>` / `ApiError`)
  applied globally with `ResponseBodyAdvice`

### `chaicommerce/`
A Product catalog service. Covers:
- `@Table` configuration — indexes, unique constraints
- Hibernate-managed audit timestamps (`@CreationTimestamp`, `@UpdateTimestamp`)
- Postgres via Docker Compose + SQL seed data

### `fitnessStudioManagementSystem-starter/`
A Fitness Studio domain model (Members, Trainers, Membership Plans, Zones,
Session Bookings). Covers:
- All JPA relationship types — `@OneToOne`, `@OneToMany`/`@ManyToOne`,
  `@ManyToMany` — with owning side vs inverse side (`mappedBy`) mapped out
  correctly across entities
- Spring Data JPA **projections**: interface-based (`IMemberInfo`) and
  class/DTO-based (`CMemberInfo`) via JPQL constructor expressions
- **Aggregation queries** in JPQL (`GROUP BY`, `COUNT`, `ORDER BY`) projected
  straight into DTOs (`MembershipTierStats`)
- **Cascading** (`CascadeType.ALL`) on `Member`'s `@OneToOne` (membership
  plan) and `@OneToMany` (bookings) associations — saving/deleting a `Member`
  propagates to its related rows
- **`@Transactional`** service methods, the persistence-context identity
  guarantee (two `findById` calls in the same transaction return the *same*
  Java object), and automatic dirty-checking (`setXxx(...)` inside a
  transaction persists on commit with no explicit `save()` call)
- `@JsonIgnore` + `@ToString.Exclude` on the back-reference side of
  bidirectional relations to avoid infinite recursion in serialization/`toString()`
- Postgres via Docker Compose

## Running any project

Each project has its own Maven wrapper, so no local Maven install is needed.

```bash
cd <project-folder>
docker compose up -d       # starts the Postgres container, if the project uses one
./mvnw spring-boot:run      # Linux/Mac
mvnw.cmd spring-boot:run    # Windows
```

## Notes

See [`learning.md`](./learning.md) for a running cheatsheet of concepts,
syntax, and interview questions compiled while working through this course —
updated as new topics are covered.
