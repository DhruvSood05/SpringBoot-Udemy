# Spring Boot Learning Notes & Interview Cheatsheet

Personal running log of everything learned while following the Udemy Spring Boot
course, backed by real code from the projects in this repo (`chai-lms`,
`chaicommerce`, `fitnessStudioManagementSystem-starter`). Organized by concept,
not by date — keep adding new sections as the course progresses.

---

## 1. Project Setup

- Every project uses **Spring Initializr** style structure: `src/main/java`,
  `src/main/resources`, `src/test/java`, `pom.xml`, `mvnw`/`mvnw.cmd`.
- `application.properties` holds DB connection, JPA settings, server port, etc.
- Local databases run via **Docker Compose** (see
  `fitnessStudioManagementSystem-starter/docker-compose.yaml`):
  ```yaml
  services:
    postgres:
      image: postgres:latest
      environment:
        POSTGRES_DB: postgres
        POSTGRES_USER: username
        POSTGRES_PASSWORD: password
      ports:
        - "5432:5432"
  ```
  `docker compose up -d` to start the DB before running the app.
- `data.sql` / `seed.sql` in `resources/` auto-run on startup (Spring Boot
  picks these up by default) to seed test data.

**Interview Q:** *How does Spring Boot auto-configure a DataSource?*
A: via `spring-boot-starter-data-jpa` + properties in `application.properties`
(`spring.datasource.url/username/password`), using auto-configuration based on
the driver on the classpath (`spring.jpa.hibernate.ddl-auto` controls schema
generation: `none`, `update`, `create`, `create-drop`, `validate`).

---

## 2. Layered Architecture (Controller → Service → Repository)

Pattern used consistently across projects:

```
Controller  -> handles HTTP, request/response, validation trigger
Service     -> business logic, orchestration, maps Entity <-> DTO
Repository  -> data access (extends JpaRepository)
Entity      -> maps to DB table
DTO         -> what's exposed over the API (never expose entities directly)
```

Example (`chai-lms`): `StudentController` → `StudentService` → `StudentRepository`.

**Why DTOs instead of returning Entities directly?**
- Decouples API contract from DB schema (DB can change without breaking API).
- Avoids leaking internal fields / lazy-loaded relations (avoids
  `LazyInitializationException` and infinite JSON recursion on bidirectional
  relations).
- Lets you apply validation annotations specific to the "input" shape.

**Interview Q:** *Why not just return the entity from the controller?*
A: Tight coupling to schema, risk of exposing sensitive/internal fields, JSON
serialization issues with lazy associations, no separate validation shape for
create vs update.

---

## 3. Dependency Injection

- Constructor injection is the pattern used everywhere (preferred over field
  injection — testable, immutable, dependencies explicit, works without
  reflection hacks).
- Plain constructor injection:
  ```java
  private final StudentService studentService;
  StudentController(StudentService studentService) {
      this.studentService = studentService;
  }
  ```
- Lombok shortcut used in the newer project:
  ```java
  @RequiredArgsConstructor   // generates constructor for all `final` fields
  public class MemberService {
      private final MemberRepository memberRepository;
  }
  ```
- No `@Autowired` needed on constructors since Spring 4.3+ when there's only
  one constructor.

**Interview Q:** *Field injection vs constructor injection — which and why?*
A: Constructor injection — makes dependencies mandatory & immutable (`final`),
easier to unit test (just call `new Service(mockRepo)`), fails fast at
startup if a bean is missing, and avoids partially-constructed objects.

---

## 4. Entities & JPA Mappings

### Core annotations
- `@Entity` — marks a class as a JPA-managed table.
- `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` — primary key,
  auto-increment delegated to DB.
- `@Column(nullable=, unique=, length=, updatable=)` — column constraints.
- `@Table(name=, indexes=, uniqueConstraints=)` — table-level config, e.g.
  `ProductEntity`:
  ```java
  @Table(
      name = "products",
      indexes = { @Index(name = "sku_list", columnList = "sku") },
      uniqueConstraints = { @UniqueConstraint(name = "title_price_unique",
                                               columnNames = {"title","price"}) }
  )
  ```
- `@CreationTimestamp` / `@UpdateTimestamp` (Hibernate) — auto-managed audit
  timestamps, no manual `setCreatedAt()` needed.
- `@Enumerated(EnumType.STRING)` — store enums as readable strings, not
  ordinal ints (ordinals break if you reorder/insert enum values later).

### Relationships — who owns the foreign key?

**Rule: the entity whose table physically holds the FK column is the owner
(`@JoinColumn`). The other side is the inverse side (`mappedBy = "<owner's field name>"`).**

| Relationship | Owner (`@JoinColumn`/`@JoinTable`) | Inverse (`mappedBy`) |
|---|---|---|
| `@OneToOne` | side with the FK column | other side |
| `@OneToMany` / `@ManyToOne` | always the `@ManyToOne` side | always the `@OneToMany` side |
| `@ManyToMany` | whichever side you choose to hold `@JoinTable` | other side |

Examples from `fitnessStudioManagementSystem-starter`:

```java
// Member owns the FK (members.membership_plan_id)
@OneToOne
@JoinColumn(name = "membership_plan_id", unique = true)
private MembershipPlan membershipPlan;

// MembershipPlan is the inverse side
@OneToOne(mappedBy = "membershipPlan")
private Member member;
```

```java
// SessionBooking is the "many" side -> owns both FKs
@ManyToOne
@JoinColumn(name = "member_id", nullable = false)
private Member member;

@ManyToOne
@JoinColumn(name = "trainer_id", nullable = false)
private Trainer trainer;

// Member / Trainer are inverse sides
@OneToMany(mappedBy = "member")
private Set<SessionBooking> bookings;
```

```java
// Zone owns the join table (zone <-> trainer many-to-many)
@ManyToMany
@JoinTable(
    name = "trainer_zone",
    joinColumns = { @JoinColumn(name = "zone_id") },
    inverseJoinColumns = { @JoinColumn(name = "trainer_id") }
)
private Set<Trainer> trainers = new HashSet<>();

// Trainer is the inverse side
@ManyToMany(mappedBy = "trainers")
private Set<Zone> zones;
```

`mappedBy`'s value is always a **field name** on the other class — never a
column/table name. This is the #1 source of bugs/typos in these mappings.

**Interview Q:** *What happens if you forget `mappedBy` and put `@OneToMany`
on both sides without it?*
A: Hibernate thinks both sides own the relationship and tries to create two
separate (and often conflicting) FK/join-table structures — leads to extra
join tables or mapping exceptions.

**Interview Q:** *Why use `Set` instead of `List` for `@OneToMany`/`@ManyToMany`?*
A: Avoids duplicate entries, and Hibernate handles bag semantics more safely
with `Set` (lists with certain fetch combos can trigger `MultipleBagFetchException`).

---

## 5. Repositories (Spring Data JPA)

- `interface XRepository extends JpaRepository<Entity, IdType>` gives CRUD for
  free: `save`, `findById`, `findAll`, `deleteById`, `existsById`, etc. — no
  implementation needed, Spring generates it at runtime (proxy).
- Custom queries with `@Query` (JPQL — operates on **entity/field names**, not
  table/column names):
  ```java
  @Query("SELECT m.id as id, m.name as name, m.email as email FROM Member m")
  List<IMemberInfo> getAllMembers();
  ```

### Projections — 2 ways to shape query results without pulling the whole entity

**1. Interface-based projection** (`IMemberInfo`) — Spring proxies the
interface at runtime based on getter names matching the query aliases:
```java
public interface IMemberInfo {
    Long getId();
    String getName();
    String getEmail();
}
```

**2. Class-based (DTO) projection** (`CMemberInfo`) — JPQL `new` expression,
constructs real objects, needs a matching constructor and **fully-qualified
class name** in the query:
```java
@Query("""
    SELECT new in.dhruvsood.fitnessstudiomanagementsystem.dto.CMemberInfo(m.id, m.name)
    FROM Member m
""")
List<CMemberInfo> getAllMembersConcrete();
```

**Interview Q:** *Interface projection vs class (DTO) projection — when to use which?*
A: Interface projections are quicker to write (no constructor, Spring proxies
getters) and lazily fetch only needed columns, good for simple flat
read-views. Class/DTO projections are real POJOs — needed when you want
`toString()`/equals, want to pass the object around as a normal Java object,
or need Jackson to serialize it predictably. Both avoid loading full entities
when you only need a few columns (more efficient than `findAll()` + manual
mapping).

### Aggregation with JPQL
```java
@Query("""
    SELECT new in....MembershipTierStats(m.membershipTier, COUNT(m))
    FROM Member m
    WHERE m.membershipTier IS NOT NULL
    GROUP BY m.membershipTier
    ORDER BY COUNT(m) desc
""")
List<MembershipTierStats> getMembershipTierStats();
```
Standard SQL-style `GROUP BY`/`COUNT`/`ORDER BY` works in JPQL, just
referencing entity fields instead of column names, and wrapping results in a
DTO constructor expression.

**Interview Q:** *Why project to a DTO for aggregate queries instead of
returning entities?* A: Aggregate rows (e.g. `(tier, count)`) don't correspond
to any single entity — there's no `Member` row that "is" the result, so it
must be a constructed DTO.

---

## 6. CRUD with Entity ↔ DTO mapping (ModelMapper)

`chai-lms` shows the classic CRUD pattern using **ModelMapper** to avoid
manual field-by-field copying:

```java
@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

```java
StudentEntity entity = modelMapper.map(studentDto, StudentEntity.class);
StudentDTO dto = modelMapper.map(entity, StudentDTO.class);
```

Used in service methods for `create`, `getById`, `getAll` (via
`.stream().map(...)`), `update`.

### PATCH (partial update) via reflection
```java
public StudentDTO partialUpdateStudentById(Map<String, Object> updates, Long id) {
    StudentEntity entity = studentRepository.findById(id).get();
    updates.forEach((field, value) -> {
        Field f = ReflectionUtils.findField(StudentEntity.class, field);
        if (f == null) return;
        f.setAccessible(true);
        ReflectionUtils.setField(f, entity, value);
    });
    studentRepository.save(entity);
    return modelMapper.map(entity, StudentDTO.class);
}
```
`PATCH` takes a generic `Map<String,Object>` (only the fields the client wants
to change) vs `PUT` which takes the full `@Valid StudentDTO` (replaces the
whole resource).

**Interview Q:** *Difference between PUT and PATCH?*
A: PUT = full replacement of the resource (idempotent, client sends the
complete representation). PATCH = partial update of only the given fields
(not guaranteed idempotent in general, though this implementation is).

---

## 7. REST Controller mapping annotations

| Annotation | HTTP verb | Used for |
|---|---|---|
| `@GetMapping` | GET | read (single via `@PathVariable`, list with none) |
| `@PostMapping` | POST | create |
| `@PutMapping("/{id}")` | PUT | full update |
| `@PatchMapping("/{id}")` | PATCH | partial update |
| `@DeleteMapping("/{id}")` | DELETE | delete |

- `@RestController` = `@Controller` + `@ResponseBody` (every method return
  value is serialized directly to the response body, usually JSON).
- `@RequestMapping(path = "/students")` on the class — base path for all
  endpoints in that controller.
- `@PathVariable` — binds `{studentId}` from the URL.
- `@RequestBody @Valid StudentDTO student` — deserializes JSON body into the
  DTO and triggers bean validation (`jakarta.validation`).

---

## 8. Bean Validation (`spring-boot-starter-validation`)

Declared on the DTO (not the entity) so that different shapes can have
different rules per use case:

```java
@NotNull(message = "...") @Size(min = 3, max = 36, message = "...")
private String name;

@NotBlank @Email
private String email;

@Min(3) @Max(18)
private Integer age;

@PastOrPresent
private LocalDate dayOfJoining;
```

Triggered via `@Valid` on the controller parameter. Validation failures throw
`MethodArgumentNotValidException`, caught centrally (see §9).

**Common annotations:** `@NotNull`, `@NotBlank` (string non-null + non-empty
after trim), `@NotEmpty`, `@Size`, `@Min`/`@Max`, `@Email`, `@Past`/`@Future`/
`@PastOrPresent`, `@AssertTrue`/`@AssertFalse`, `@Pattern` (regex).

**Interview Q:** *`@NotNull` vs `@NotBlank` vs `@NotEmpty`?*
A: `@NotNull` — not null (empty string ok). `@NotEmpty` — not null and length
> 0. `@NotBlank` — not null and contains at least one non-whitespace char
(strings only).

---

## 9. Centralized Error Handling

### Custom exception
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
```
Thrown from the service layer when a lookup fails, instead of returning
`null` or `Optional.empty()` silently.

### Global handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
            .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors()
            .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.BAD_REQUEST)
            .message("Input Validation Has Failed")
            .subErrors(errors)
            .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)   // catch-all -> 500
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception ex) { ... }
}
```

`@RestControllerAdvice` = global, cross-cutting `@ExceptionHandler` methods
for every controller in the app — avoids try/catch boilerplate in every
controller method.

**Order matters conceptually:** Spring matches the *most specific* exception
type handler first, so a catch-all `Exception.class` handler is safe to keep
alongside specific ones (specific types win).

**Interview Q:** *Why `@RestControllerAdvice` instead of try/catch in every controller?*
A: Centralizes error-response shape (consistent JSON structure across the
whole API), keeps controllers thin, single place to update error format.

---

## 10. Standardized API Response envelope

Every response (success or error) is wrapped the same way, using
`ResponseBodyAdvice` to intercept *all* controller return values globally —
no need to manually wrap every controller method's return value.

```java
@Data
public class ApiResponse<T> {
    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private LocalDateTime timestamp;
    private T data;
    private ApiError error;

    public ApiResponse(T data) { this(); this.data = data; }
    public ApiResponse(ApiError error) { this(); this.error = error; }
}

@Data @Builder
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> subErrors;
}

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) { return true; }

    @Override
    public Object beforeBodyWrite(Object body, ... ) {
        if (body instanceof ApiResponse<?>) return body;  // already wrapped (errors)
        return new ApiResponse<>(body);                   // wrap plain success bodies
    }
}
```

Result: every endpoint, success or failure, returns the same envelope shape
(`{ timestamp, data, error }`) without the controller ever constructing
`ApiResponse` itself — `GlobalResponseHandler` wraps plain returns, and
`GlobalExceptionHandler` builds error ones directly.

**Interview Q:** *What is `ResponseBodyAdvice` and when does it run?*
A: An interceptor that runs **after** a controller method returns and
**before** the body is written/serialized to the HTTP response — lets you
transform every response globally (wrapping, adding headers-driven logic,
etc.) without touching each controller.

---

## 11. Lombok (used everywhere to kill boilerplate)

| Annotation | Generates |
|---|---|
| `@Getter` / `@Setter` | getters / setters for all fields |
| `@Data` | getters, setters, `equals`, `hashCode`, `toString` (not for JPA entities with bidirectional relations — see gotcha below) |
| `@NoArgsConstructor` / `@AllArgsConstructor` | constructors (JPA requires a no-args constructor) |
| `@Builder` | builder pattern |
| `@ToString` | `toString()` |
| `@RequiredArgsConstructor` | constructor for all `final`/`@NonNull` fields (used for DI) |

**Gotcha:** `@Data`/`@ToString` on JPA entities with bidirectional relations
(e.g. `Member` ↔ `MembershipPlan`) can cause infinite recursion in
`toString()`/`equals()` if both sides reference each other and both include
the relation field — watch for `StackOverflowError`. Safer to use
`@Getter/@Setter` + selective `@ToString` excluding relation fields, or
`@ToString.Exclude` on the back-reference.

---

## 12. Cascading & Transactions

From the `fitnessStudioManagementSystem-starter` "JPA Cascading and
Transactions" module — `Member`, `MemberService`, `SessionBookingService`.

### `CascadeType` — propagating operations across relationships

By default, calling `save()`/`delete()` on an entity does **not** touch its
associations — you'd have to save/delete the related entity yourself.
`cascade = CascadeType.ALL` on the owning side makes JPA propagate
persist/merge/remove/refresh/detach down the association automatically:

```java
// Member owns the FK -> cascading here saves/deletes the MembershipPlan
// whenever the owning Member is saved/deleted
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "membership_plan_id", unique = true)
private MembershipPlan membershipPlan;

// Deleting a Member also deletes all of their SessionBookings
@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
@ToString.Exclude
private Set<SessionBooking> bookings = new HashSet<>();
```

Other `CascadeType` values worth knowing individually: `PERSIST`, `MERGE`,
`REMOVE`, `REFRESH`, `DETACH` — `ALL` is shorthand for every one of them.

**Interview Q:** *What happens if you call `memberRepository.delete(member)`
without `CascadeType.ALL`/`REMOVE` on `bookings`?*
A: JPA tries to delete the `Member` row while FK-referencing `SessionBooking`
rows still exist → a foreign key constraint violation at the DB level,
because nothing told JPA to delete the children first.

**Gotcha — cascade direction:** cascade is declared on the *owning* side's
annotation and only cascades operations performed *on that entity*. Cascading
`Member -> bookings` means deleting a `Member` deletes its `SessionBooking`s,
not the reverse.

### Bidirectional relations: breaking recursion in serialization

Once `SessionBooking` has `@ManyToOne` back to `Member`/`Trainer`, and
`Member`/`Trainer` have `@OneToMany` back to `SessionBooking`, both
`toString()` and Jackson JSON serialization will recurse infinitely
(`Member` -> `bookings` -> `SessionBooking` -> `member` -> `bookings` -> ...)
unless one side is excluded:

```java
@ManyToOne
@JoinColumn(name = "member_id", nullable = false)
@ToString.Exclude   // keep Lombok's toString() from recursing
@JsonIgnore          // keep Jackson from recursing when serialized to JSON
private Member member;
```

`@ToString.Exclude` and `@JsonIgnore` solve two *different* recursion
problems (Lombok-generated `toString()` vs Jackson serialization) — both are
needed on the "many" side of a bidirectional relation if the "one" side's
collection isn't itself excluded.

### `@Transactional` — what it actually buys you

```java
@Transactional
public void testMemberRepositoryTransactional() {
    Member m1 = memberRepository.findById(1L).orElseThrow();
    Member m2 = memberRepository.findById(1L).orElseThrow();

    System.out.println(m1 == m2);   // true!

    m1.setName("Dhruv");            // no explicit save() call...
}                                    // ...but the UPDATE still happens on commit
```

Two things happening here, both because `@Transactional` keeps a single
Hibernate **persistence context** (session) open for the whole method:

1. **Identity guarantee** — a second `findById` for the same id/entity type
   within the same transaction returns the *same* in-memory Java object from
   the session's cache, not a fresh one re-read from the DB (`m1 == m2`).
2. **Automatic dirty checking** — any setter call on a managed entity marks
   it "dirty"; Hibernate compares the entity's state against the snapshot it
   took when loading it, and flushes an `UPDATE` for changed fields when the
   transaction commits — **no explicit `.save()` needed**, unlike outside a
   transaction where you must call `save()` to persist a change.

Without `@Transactional` (see plain `testMemberRepository()`), each
`findById` opens and closes its own short-lived session — no identity
guarantee across calls, and any entity returned is **detached** the moment
the method returns, so mutating it afterward does nothing to the DB.

```java
@Transactional
public MembershipPlan assignMembershipPlanToMember(MembershipPlan membershipPlan, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow();
    member.setMembershipPlan(membershipPlan);   // dirty-checked, cascades PERSIST to the new plan
    return membershipPlan;                       // no explicit save() anywhere
}

@Transactional
public void deleteMember(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow();
    memberRepository.delete(member);   // cascades to membershipPlan + bookings
}
```

**Interview Q:** *Why does `assignMembershipPlanToMember` persist its change
with no `save()` call?* A: The `Member` returned by `findById` inside an
`@Transactional` method is a **managed** entity attached to the open
persistence context. Any setter mutates its state in memory; Hibernate's
dirty-checking detects the difference at flush/commit time and issues the
`UPDATE`/`INSERT` itself.

**Interview Q:** *`jakarta.transaction.Transactional` vs
`org.springframework.transaction.annotation.Transactional` — does it
matter which you import?* A: Both work with Spring's transaction
infrastructure (Spring recognizes the standard `jakarta.transaction`
annotation too), but only the Spring-native annotation exposes Spring-specific
options like `propagation`, `isolation`, `readOnly`, and `rollbackFor` — worth
switching to the Spring one once propagation/rollback rules need tuning.

**Interview Q:** *Where should `@Transactional` go — controller, service, or
repository?* A: Service layer. It's the layer that owns a unit of business
work (potentially touching multiple repositories), repositories are too
fine-grained (one call each), and controllers shouldn't know about
persistence boundaries.

---

## Interview Question Bank (quick recall)

1. **Explain the request lifecycle** for a `POST /students` call in this app,
   start to finish (DispatcherServlet → Controller → `@Valid` → Service →
   ModelMapper → Repository → Entity saved → mapped back to DTO →
   `ResponseBodyAdvice` wraps it → JSON response).
2. **What's the difference between `@Component`, `@Service`, `@Repository`,
   `@Controller`/`@RestController`?** All are `@Component` stereotypes;
   `@Service`/`@Repository`/`@Controller` exist for semantic clarity and some
   extra behavior (`@Repository` enables persistence-exception translation).
3. **What does `@Transactional` do and where would you add it?** Wraps a
   method in a single DB transaction/persistence context — commits on
   success, rolls back on an uncaught runtime exception. Gives entity
   identity guarantees (repeated `findById` within the method returns the
   same object) and automatic dirty-checking (setter changes flush without
   an explicit `save()`). Add it at the service layer (see §12).
4. **JPQL vs native SQL** — JPQL operates on entity/field names and is
   DB-agnostic; native queries (`@Query(nativeQuery = true)`) use actual table
   /column names and are DB-specific.
5. **What is N+1 query problem and how would lazy `@OneToMany` cause it?**
   (to explore further — relevant given multiple `@OneToMany`/`@ManyToMany`
   relations in the fitness project).
6. **`FetchType.LAZY` vs `FetchType.EAGER`** — default for `@OneToMany`/
   `@ManyToMany` is LAZY, default for `@ManyToOne`/`@OneToOne` is EAGER; worth
   explicitly setting based on access patterns.
7. **Why `existsById()` before `deleteById()`/`save()` in the service layer**
   instead of catching an exception from the repository? Explicit, cheap
   check avoids relying on exception-driven control flow for an expected case.
8. Walk through **owning side vs inverse side** for each relationship type
   (see §4 table) — this comes up constantly.
9. **Interface projection vs DTO (class) projection vs full entity fetch** —
   trade-offs in performance and usability (see §5).
10. **Why centralize exception handling with `@RestControllerAdvice`
    instead of handling errors per-controller?**

---

## TODO / Topics to add as the course progresses
- Transaction propagation levels (`REQUIRED`, `REQUIRES_NEW`, `NESTED`, etc.)
  and isolation levels — covered the default/basic case in §12, not the
  advanced propagation rules yet
- Spring Security (auth, JWT)
- `FetchType` tuning + N+1 problem solutions (`@EntityGraph`, `JOIN FETCH`)
- Pagination & sorting (`Pageable`, `Page<T>`)
- Testing (`@SpringBootTest`, `@DataJpaTest`, Mockito)
- Caching (`@Cacheable`)
- Spring profiles (`application-dev.properties`, etc.)
- OpenAPI/Swagger docs
