# System Design Basics & Microservices
> Architecture Thinking for Scalable Systems
> Covers: What is System Design · Scalability · Monolith vs Microservices · Architecture · Communication · Data Management · Java/Spring Boot · Real-World Case Study

---

## Table of Contents
1. [What is System Design?](#1-what-is-system-design)
2. [Scalability & Performance Basics](#2-scalability--performance-basics)
3. [Monolith vs Microservices](#3-monolith-vs-microservices)
4. [Microservices Architecture](#4-microservices-architecture)
5. [Communication Patterns](#5-communication-patterns)
6. [Data Management](#6-data-management)
7. [Java Ecosystem & Spring Boot](#7-java-ecosystem--spring-boot)
8. [Real-World Architecture Case Study](#8-real-world-architecture-case-study)

---

## 1. What is System Design?

### Definition
System design is the process of **defining architecture, components, interfaces, and data flow** for a system that satisfies specified requirements. It bridges the gap between requirements and implementation.

```
Requirements → System Design → Implementation

Without system design:
  Code works on laptop → breaks under 10,000 users
  One database → single point of failure
  Monolithic jar → impossible to scale only the hot component

With system design:
  Traffic distributed across 50 servers
  3 database replicas → no single point of failure
  Payment service scaled independently → no wasted resources
```

### The System Design Thought Process
```
Step 1: CLARIFY requirements
  Functional:    What does the system DO? (user login, search, post tweet)
  Non-functional: How does it PERFORM? (latency, availability, consistency)
  Scale:         How BIG is it? (users, requests/sec, data volume)

Step 2: ESTIMATE scale
  Daily active users → requests per second
  Storage needed → GB/TB/PB per year
  Bandwidth in/out

Step 3: HIGH-LEVEL DESIGN
  Choose components (load balancer, API gateway, services, DB, cache)
  Draw data flow

Step 4: DEEP DIVE critical components
  Database choice and schema
  Caching strategy
  API design
  Failure handling

Step 5: IDENTIFY bottlenecks
  Single points of failure
  Hotspots in data
  Latency-critical paths
```

### Key Numbers Every Designer Should Know
```
Latency Comparisons:
  L1 cache access:      ~1 ns
  Main memory access:   ~100 ns
  SSD read:             ~100 µs  (100,000 ns)
  Network round trip:   ~1 ms    (1,000,000 ns)
  HDD seek:             ~10 ms
  Packet US→Europe:     ~150 ms

Storage:
  1 char = 1 byte
  Tweet = ~280 bytes
  Photo = ~300 KB
  Video (4min HD) = ~600 MB

Throughput benchmarks:
  Single MySQL: ~1,000 queries/sec
  Redis: ~100,000 ops/sec
  Kafka: ~1,000,000 msgs/sec
  Nginx (static): ~50,000 req/sec per core
```

### Core System Design Concepts
```
Availability:   % of time the system is operational  (99.9% = 8.7 hrs/year downtime)
Reliability:    System performs correctly over time
Durability:     Data is not lost even after failures
Consistency:    All nodes see the same data at the same time
Partition Tolerance: System works despite network failures

CAP Theorem:
  In a distributed system, you can only guarantee 2 of 3:
  ┌─────────────────────────────┐
  │  Consistency (C)            │
  │  Availability (A)           │
  │  Partition Tolerance (P)    │
  └─────────────────────────────┘
  CA: Traditional RDBMS (no partitions)
  CP: HBase, Zookeeper, MongoDB (strong consistency)
  AP: Cassandra, CouchDB, DynamoDB (always available)
```

---

## 2. Scalability & Performance Basics

### Vertical vs Horizontal Scaling
```
Vertical Scaling (Scale UP):
  Add more RAM/CPU/SSD to existing machine
  Pros: Simple, no code changes, strong consistency
  Cons: Hardware limits, single point of failure, expensive
  Example: t2.micro → t2.xlarge → x1e.32xlarge

Horizontal Scaling (Scale OUT):
  Add more machines
  Pros: Virtually unlimited, fault tolerant, cheap commodity hardware
  Cons: Needs load balancer, stateless services, distributed complexity
  Example: 1 server → 10 servers → 1000 servers

AWS/GCP pattern:
  Start vertical → hit limits → go horizontal
  Auto-scaling groups handle this automatically
```

### Load Balancing
```
Algorithms:
  Round Robin:        Request 1→Server1, 2→Server2, 3→Server3, 4→Server1...
  Weighted RR:        Server1 gets 3x requests (has 3x capacity)
  Least Connections:  Route to server with fewest active connections
  IP Hash:            Same IP always goes to same server (session affinity)
  Random:             Pick random server

Layers:
  L4 (Transport):     Routes by IP + port, no HTTP understanding, ultra-fast
  L7 (Application):  Routes by HTTP headers, URL path, cookies — more flexible

Tools: Nginx, HAProxy, AWS ALB/NLB, Envoy
```

```java
// Simulated Round-Robin Load Balancer
public class RoundRobinLoadBalancer {
    private final List<Server> servers;
    private final AtomicInteger counter = new AtomicInteger(0);

    public RoundRobinLoadBalancer(List<Server> servers) {
        this.servers = servers;
    }

    public Server getNext() {
        int index = counter.getAndIncrement() % servers.size();
        Server server = servers.get(index);
        if (!server.isHealthy()) {
            // Skip unhealthy, find next healthy
            for (int i = 1; i < servers.size(); i++) {
                Server alt = servers.get((index + i) % servers.size());
                if (alt.isHealthy()) return alt;
            }
            throw new NoHealthyServerException("All servers unhealthy");
        }
        return server;
    }
}

// Weighted Round-Robin
public class WeightedLoadBalancer {
    private final List<WeightedServer> servers;

    public Server getNext(Request request) {
        // Build weighted pool: server with weight 3 appears 3 times
        List<Server> pool = servers.stream()
            .flatMap(ws -> Collections.nCopies(ws.weight, ws.server).stream())
            .collect(Collectors.toList());
        return pool.get(random.nextInt(pool.size()));
    }
}
```

### Caching
```
Cache Hit Ratio = hits / (hits + misses)  → want > 90%

Cache Strategies:

1. Cache-Aside (Lazy Loading) — most common
   Read:  Check cache → if miss, read DB → store in cache → return
   Write: Write to DB, invalidate cache entry
   Pro:  Cache only what's needed
   Con:  Cache miss penalty (3 calls: cache + DB + write back)

2. Write-Through
   Write:  Write to cache AND DB simultaneously
   Read:   Always cache hit
   Pro:  No stale data
   Con:  Write latency higher, cache fills with unread data

3. Write-Behind (Write-Back)
   Write:  Write to cache, async write to DB later
   Pro:  Very fast writes
   Con:  Risk of data loss before DB flush

4. Read-Through
   Always go through cache layer
   Cache fetches from DB on miss automatically
   Used by: Hibernate L2 cache, Redis with spring-cache

Cache Eviction Policies:
  LRU (Least Recently Used)   — most popular, good general use
  LFU (Least Frequently Used) — good for hot-spot data
  TTL (Time To Live)          — expire after N seconds
  FIFO                        — first in, first out
```

```java
// Generic LRU Cache implementation
public class LRUCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;   // Evict when over capacity
            }
        };
    }

    public synchronized V get(K key) {
        return cache.getOrDefault(key, null);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    public int size() { return cache.size(); }
}
```

### Rate Limiting
```
Algorithms:
  Token Bucket:    Fill bucket at rate R, each request consumes 1 token. Burst allowed.
  Leaky Bucket:    Fixed output rate regardless of burst. Smooths traffic.
  Fixed Window:    Count requests per time window. Simple but boundary spike problem.
  Sliding Window:  Rolling window of last N seconds. More accurate.

Use cases:
  API Gateway rate limiting: 100 requests/min per API key
  Login attempts: 5 failed attempts → lock for 15 minutes
  SMS OTP: 3 OTPs per phone number per hour
  Search: 30 queries/sec per user
```

```java
// Token Bucket Rate Limiter
public class TokenBucketRateLimiter {
    private final double capacity;
    private final double refillRatePerSecond;
    private double tokens;
    private long lastRefillTime;

    public TokenBucketRateLimiter(double capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;    // Request allowed
        }
        return false;       // Rate limited
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double elapsed = (now - lastRefillTime) / 1000.0;   // Convert to seconds
        tokens = Math.min(capacity, tokens + elapsed * refillRatePerSecond);
        lastRefillTime = now;
    }
}
```

### Content Delivery Network (CDN)
```
CDN = Globally distributed cache at the edge of the network

How it works:
  User in Tokyo requests image stored in New York
  Without CDN: 200ms round trip
  With CDN:    Content cached in Tokyo edge node → 5ms

What to CDN:
  ✓ Static files: JS, CSS, images, fonts
  ✓ Video content
  ✓ API responses that don't change per user (exchange rates, product catalog)
  ✗ User-specific data (orders, profile)
  ✗ Real-time data

Providers: CloudFront (AWS), Cloudflare, Akamai, Fastly
```

---

## 3. Monolith vs Microservices

### Monolithic Architecture
```
All components in a single deployable unit:
  ┌──────────────────────────────────┐
  │           Monolith               │
  │  ┌────────┐  ┌────────┐          │
  │  │  User  │  │  Order │          │
  │  │ Module │  │ Module │          │
  │  └────────┘  └────────┘          │
  │  ┌────────┐  ┌────────┐          │
  │  │Payment │  │Catalog │          │
  │  │ Module │  │ Module │          │
  │  └────────┘  └────────┘          │
  └──────────────────────────────────┘
              │
        Single Database

Pros:
  ✓ Simple to develop initially
  ✓ Easy to test (everything in one process)
  ✓ No network latency between components
  ✓ Simpler deployment (one artifact)
  ✓ No distributed systems complexity

Cons:
  ✗ One bug can crash everything
  ✗ Must scale entire app even if only one module is hot
  ✗ Technology lock-in (all Java, all same framework)
  ✗ Large team conflicts on same codebase
  ✗ Deployment of small change = deploy everything
  ✗ Startup time grows as app grows

When to use:
  Small team (< 10 engineers)
  Early-stage startup (validate product first)
  Simple domain with few modules
  Low traffic (< 10K users)
```

### Microservices Architecture
```
Each business capability is an independent service:
  ┌───────────┐  ┌───────────┐  ┌───────────┐
  │  User     │  │  Order    │  │  Payment  │
  │  Service  │  │  Service  │  │  Service  │
  │  :8081    │  │  :8082    │  │  :8083    │
  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘
        │               │               │
   ┌────┴───┐     ┌─────┴──┐     ┌──────┴──┐
   │User DB │     │Order DB│     │Payment  │
   │(Postgres)   │(MySQL) │     │DB(Mongo)│
   └────────┘     └────────┘     └─────────┘

Pros:
  ✓ Independent deployment (deploy only what changed)
  ✓ Independent scaling (scale only hot services)
  ✓ Technology independence (each service chooses stack)
  ✓ Fault isolation (payment failure doesn't kill catalog)
  ✓ Small focused teams own each service
  ✓ Easier to understand individual service

Cons:
  ✗ Distributed system complexity
  ✗ Network calls instead of in-process calls (latency)
  ✗ Distributed transactions are hard
  ✗ Service discovery, load balancing needed
  ✗ More infrastructure (K8s, service mesh)
  ✗ Testing requires spinning up dependencies
  ✗ Operational overhead increases

When to use:
  Large teams (50+ engineers)
  Different scaling requirements per component
  Need independent release cycles
  High traffic (millions of users)
```

### The Migration Path
```
Strangler Fig Pattern — gradually replace monolith:

Phase 1: Monolith + Proxy
  ┌──────────┐     ┌──────────┐
  │  Client  │────▶│  Proxy   │
  └──────────┘     └────┬─────┘
                        │
                   ┌────▼─────┐
                   │ Monolith │  ← Still handles everything
                   └──────────┘

Phase 2: Extract first service (highest value/most isolated)
  ┌──────────┐     ┌──────────┐
  │  Client  │────▶│  Proxy   │
  └──────────┘     └──┬───┬───┘
                      │   │
               ┌──────▼┐ ┌▼─────────┐
               │Payment│ │ Monolith │  ← Payment extracted
               │Service│ │(shrinking)│
               └───────┘ └──────────┘

Phase 3: Continue extracting until monolith is gone
```

---

## 4. Microservices Architecture

### Core Components
```
┌──────────────────────────────────────────────────────────┐
│                    CLIENT LAYER                          │
│    Mobile App    Web Browser    Third-party API          │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                   API GATEWAY                            │
│  Authentication · Rate Limiting · SSL Termination        │
│  Routing · Request Aggregation · Load Balancing          │
└──┬──────────────┬──────────────┬──────────────┬──────────┘
   │              │              │              │
┌──▼───┐    ┌─────▼───┐   ┌─────▼───┐   ┌─────▼──────┐
│ User │    │  Order  │   │Payment  │   │ Catalog    │
│ Svc  │    │  Svc    │   │  Svc    │   │   Svc      │
└──┬───┘    └─────┬───┘   └─────┬───┘   └─────┬──────┘
   │              │              │              │
┌──▼──┐     ┌─────▼──┐   ┌─────▼──┐    ┌─────▼──────┐
│User │     │Order   │   │Payment │    │Product     │
│ DB  │     │  DB    │   │  DB    │    │    DB      │
└─────┘     └────────┘   └────────┘    └────────────┘
                         │
              ┌──────────▼──────────┐
              │   MESSAGE BUS       │
              │ Kafka / RabbitMQ    │
              └─────────────────────┘
```

### API Gateway
```
Responsibilities:
  1. Authentication & Authorization (verify JWT, API keys)
  2. Rate Limiting (per client, per endpoint)
  3. Request Routing (URL path → target service)
  4. SSL Termination (HTTPS handled once, internal HTTP)
  5. Request/Response Transformation
  6. Circuit Breaking (stop cascading failures)
  7. Request Aggregation (fan-out to multiple services)
  8. Observability (logging, metrics, distributed tracing)

Tools: Kong, AWS API Gateway, Nginx, Spring Cloud Gateway, Envoy
```

```java
// Spring Cloud Gateway route configuration
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            // User service routes
            .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Gateway", "true")
                    .retry(config -> config.setRetries(3))
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://user-service"))

            // Order service with circuit breaker
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("orderCircuitBreaker")
                        .setFallbackUri("forward:/fallback/orders")))
                .uri("lb://order-service"))

            .build();
    }
}
```

### Service Discovery
```
Problem: In microservices, services start/stop dynamically.
         How does Service A find the current IP of Service B?

Solution: Service Registry

Client-Side Discovery:
  Service registers itself → Registry
  Client queries Registry → gets list of instances
  Client chooses instance (load balances itself)
  Tools: Netflix Eureka, Consul

Server-Side Discovery:
  Client calls Load Balancer
  LB queries Registry
  LB routes to service instance
  Tools: AWS ALB, Nginx + Consul

Registration:
  Self-registration: Service registers itself on startup, deregisters on shutdown
  Third-party: Sidecar/Agent handles registration (Kubernetes, Consul agent)
```

```java
// Spring Boot Eureka registration (application.yml)
/*
spring:
  application:
    name: order-service

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
*/

// Consuming another service via service discovery
@Service
public class PaymentServiceClient {
    private final RestTemplate restTemplate;   // Load-balanced

    @Autowired
    public PaymentServiceClient(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentResponse charge(PaymentRequest request) {
        // "payment-service" resolved by service discovery — NOT a real hostname
        return restTemplate.postForObject(
            "http://payment-service/api/payments/charge",
            request,
            PaymentResponse.class
        );
    }
}
```

### Circuit Breaker Pattern
```
Problem: Service A calls Service B. Service B is slow/down.
         A's threads pile up waiting → A runs out of threads → A goes down.
         Cascading failure!

Solution: Circuit Breaker — like an electrical fuse

States:
  CLOSED:    Normal operation, calls pass through
  OPEN:      Too many failures → stop calling, return error immediately
  HALF-OPEN: Test probe: let one request through
             If success → CLOSED
             If failure → OPEN again

Configuration:
  failureRateThreshold: 50%      (open if >50% calls fail)
  waitDurationInOpenState: 60s   (stay open for 60 seconds)
  ringBufferSizeInClosedState: 10 (calculate rate from last 10 calls)

Benefits:
  Fail fast instead of waiting for timeout
  Gives downstream service time to recover
  Prevents resource exhaustion
```

```java
// Circuit Breaker with Resilience4j
@Service
public class ProductCatalogClient {

    private final CircuitBreaker circuitBreaker;
    private final WebClient webClient;

    public ProductCatalogClient(CircuitBreakerRegistry registry, WebClient webClient) {
        this.circuitBreaker = registry.circuitBreaker("catalogService");
        this.webClient = webClient;
    }

    public Mono<Product> getProduct(String productId) {
        return Mono.fromSupplier(() -> circuitBreaker.executeSupplier(
            () -> webClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(Product.class)
                .block()
        ))
        .onErrorResume(CallNotPermittedException.class,
            e -> Mono.just(Product.fallback(productId)));   // Return cached/default
    }
}

// Declarative with annotation
@CircuitBreaker(name = "inventoryService", fallbackMethod = "getInventoryFallback")
@TimeLimiter(name = "inventoryService")
@Retry(name = "inventoryService")
public CompletableFuture<Integer> getInventoryCount(String productId) {
    return CompletableFuture.supplyAsync(
        () -> inventoryClient.getCount(productId)
    );
}

public CompletableFuture<Integer> getInventoryFallback(String productId, Exception e) {
    log.warn("Inventory service unavailable for {}, returning cached: {}", productId, e.getMessage());
    return CompletableFuture.completedFuture(cachedInventory.getOrDefault(productId, -1));
}
```

---

## 5. Communication Patterns

### Synchronous vs Asynchronous
```
Synchronous (Request-Response):
  Client sends request → waits for response → continues
  Simple, immediate feedback
  Tight coupling: caller blocks until callee responds

Asynchronous (Event-Driven):
  Client sends message → doesn't wait → continues
  Message processed later by consumer
  Loose coupling, higher throughput, natural retry

When to use which:
  SYNCHRONOUS:
    User-facing queries (need immediate response)
    Validation (need instant yes/no)
    Simple request-response flows
    Example: GET /api/products/123 → response needed now

  ASYNCHRONOUS:
    Side effects (send email, update analytics, notify)
    Long-running operations (PDF generation, video processing)
    Cross-service data propagation
    Example: Order placed → notify warehouse, send email, update inventory
```

### REST API Design
```java
// Well-designed REST API
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    // GET collection — filtering, pagination, sorting
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(orderService.findAll(status, pageable)
            .map(OrderResponse::from));
    }

    // GET single resource
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return orderService.findById(id)
            .map(order -> ResponseEntity.ok(OrderResponse.from(order)))
            .orElse(ResponseEntity.notFound().build());
    }

    // POST — create resource, return 201 with Location header
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-Id") String userId) {

        Order order = orderService.create(request, userId);
        URI location = URI.create("/api/v1/orders/" + order.getId());
        return ResponseEntity.created(location).body(OrderResponse.from(order));
    }

    // PATCH — partial update
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {

        return orderService.updateStatus(id, request.getStatus())
            .map(order -> ResponseEntity.ok(OrderResponse.from(order)))
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE — idempotent cancellation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();   // 204
    }
}

// Standard error response
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage(),
                LocalDateTime.now().toString()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.toList());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_FAILED", errors.toString(),
                LocalDateTime.now().toString()));
    }
}
```

### Event-Driven Architecture with Kafka
```
Kafka concepts:
  Producer:   Publishes messages to topics
  Consumer:   Reads messages from topics
  Topic:      Named channel, like a queue that persists
  Partition:  Topic split for parallelism (more partitions = more consumers)
  Offset:     Position of a message in partition (consumer tracks this)
  Consumer Group: Multiple consumers sharing work from same topic

Message ordering:
  Within partition → guaranteed order
  Across partitions → no ordering guarantee
  Key-based partitioning → same key → same partition → ordered

Delivery guarantees:
  At most once:  Fire and forget (may lose messages)
  At least once: Retry on failure (may duplicate)
  Exactly once:  Transactional (Kafka Transactions + idempotent consumer)
```

```java
// Kafka producer configuration
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");             // Wait for all replicas
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Exactly-once
        return new DefaultKafkaProducerFactory<>(config);
    }
}

// Publishing events
@Service
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderPlaced(Order order) {
        OrderPlacedEvent event = OrderPlacedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("ORDER_PLACED")
            .orderId(order.getId())
            .userId(order.getUserId())
            .totalAmount(order.getTotal())
            .items(order.getItems())
            .timestamp(Instant.now())
            .build();

        // Key = orderId ensures all events for same order go to same partition (ordered)
        kafkaTemplate.send("order-events", order.getId(), event)
            .addCallback(
                result  -> log.info("Published order event: {}", event.getEventId()),
                failure -> log.error("Failed to publish: {}", failure.getMessage())
            );
    }
}

// Consuming events
@Component
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(
        topics = "order-events",
        groupId = "inventory-service",
        concurrency = "3"   // 3 consumer threads → needs 3+ partitions
    )
    public void handleOrderPlaced(
            @Payload OrderPlacedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Consuming event {} from partition {} offset {}", event.getEventId(), partition, offset);

        try {
            inventoryService.reserve(event.getOrderId(), event.getItems());
        } catch (InsufficientInventoryException e) {
            // Publish compensating event
            eventPublisher.publishInventoryFailed(event.getOrderId(), e.getMessage());
        }
    }
}
```

### SAGA Pattern — Distributed Transactions
```
Problem: Order + Payment + Inventory update span 3 services.
         One fails → how to roll back the others?
         Two-phase commit is slow and fragile across services.

Solution: SAGA — sequence of local transactions, each publishing an event.
          Compensating transactions for rollback.

Choreography SAGA (event-driven):
  OrderService:    CREATE order → publish "OrderCreated"
  PaymentService:  Listen "OrderCreated" → charge card → publish "PaymentCharged"
  InventoryService:Listen "PaymentCharged" → reserve stock → publish "StockReserved"
  OrderService:    Listen "StockReserved" → confirm order → done

  On failure:
  InventoryService: stock unavailable → publish "StockReservationFailed"
  PaymentService:   Listen failure → refund card → publish "PaymentRefunded"
  OrderService:     Listen refund → cancel order

Orchestration SAGA (central coordinator):
  SagaOrchestrator tells each service what to do → waits for response
  Simpler to understand, single point of coordination
  Tools: AWS Step Functions, Camunda, Temporal
```

---

## 6. Data Management

### Database Selection Guide
```
Relational (PostgreSQL, MySQL):
  ✓ Complex queries with JOINs
  ✓ ACID transactions
  ✓ Strong consistency required
  ✓ Well-defined schema
  Use: User accounts, orders, financial transactions

Document (MongoDB, Couchbase):
  ✓ Flexible/evolving schema
  ✓ Nested/hierarchical data
  ✓ High write throughput
  Use: Product catalogs, user profiles, content management

Key-Value (Redis, DynamoDB):
  ✓ Ultra-fast lookups by key
  ✓ Simple data structures
  ✓ Caching, sessions
  Use: Session storage, rate limiting, leaderboards, caches

Column-Family (Cassandra, HBase):
  ✓ Very high write throughput
  ✓ Time-series data
  ✓ Massive scale (PB of data)
  Use: IoT sensor data, user activity logs, Netflix viewing history

Search (Elasticsearch, Solr):
  ✓ Full-text search
  ✓ Complex aggregations
  ✓ Faceted search
  Use: Product search, log analytics, autocomplete

Graph (Neo4j, Neptune):
  ✓ Highly connected data
  ✓ Relationship traversal
  Use: Social networks, fraud detection, recommendations
```

### Database Replication
```
Master-Replica (Read Replicas):
  Master: Handles ALL writes
  Replicas: Handle reads (eventual consistency)
  Replication lag: Usually < 1 second

  ┌─────────┐       ┌──────────┐
  │ Master  │──────▶│ Replica1 │  (async replication)
  │ (Writes)│──────▶│ Replica2 │
  └─────────┘──────▶│ Replica3 │
       ▲             └──────────┘
       │                  ▼
    Writes              Reads

When to read from master vs replica:
  Read from master:  After a write (avoid replication lag), financial data
  Read from replica: Product browsing, search, reports, dashboards

Multi-Master:
  Any node accepts writes
  Complex conflict resolution
  Used by: CockroachDB, Cassandra, DynamoDB global tables
```

### Database Sharding
```
Sharding = horizontally partition data across multiple DB instances

Shard Key determines which shard holds which data:

Range-based:
  Shard 1: userId 1-1000000
  Shard 2: userId 1000001-2000000
  Pro: Range queries efficient (find all users 1-500)
  Con: Hotspots (new users all go to latest shard)

Hash-based:
  shard = hash(userId) % numShards
  Pro: Even distribution
  Con: Range queries span all shards

Directory-based:
  Lookup table maps key → shard
  Pro: Flexible resharding
  Con: Lookup table is a bottleneck

Consistent Hashing:
  Servers and keys mapped to a ring
  Key goes to first server clockwise on ring
  Adding/removing server only remaps adjacent keys (not all)
  Used by: Cassandra, Dynamo, distributed caches
```

### CQRS (Command Query Responsibility Segregation)
```java
// Commands (writes) and Queries (reads) use separate models

// Command side — write model
@Service
public class OrderCommandService {

    @Transactional
    public String createOrder(CreateOrderCommand command) {
        Order order = Order.create(command.getUserId(), command.getItems());
        orderRepository.save(order);

        // Publish event for read model update
        eventBus.publish(new OrderCreatedEvent(order.getId(), order.getUserId(),
            order.getItems(), order.getTotal(), order.getCreatedAt()));

        return order.getId();
    }
}

// Query side — read model (optimized for reads, may be denormalized)
@Service
public class OrderQueryService {

    public OrderSummaryView getOrderSummary(String orderId) {
        // Read from denormalized read store (Redis, Elasticsearch, or read replica)
        return orderReadRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public Page<OrderListItem> getUserOrders(String userId, Pageable pageable) {
        // Optimized read query — no joins, pre-computed totals
        return orderReadRepository.findByUserId(userId, pageable);
    }
}

// Event handler updates read model
@Component
public class OrderReadModelUpdater {

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderSummaryView view = OrderSummaryView.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .itemCount(event.getItems().size())
            .total(event.getTotal())
            .status("PENDING")
            .createdAt(event.getCreatedAt())
            .build();
        orderReadRepository.save(view);
    }
}
```

---

## 7. Java Ecosystem & Spring Boot

### Spring Boot Microservice Structure
```
order-service/
├── src/main/java/com/company/orderservice/
│   ├── OrderServiceApplication.java
│   ├── api/
│   │   ├── OrderController.java
│   │   ├── dto/
│   │   │   ├── CreateOrderRequest.java
│   │   │   └── OrderResponse.java
│   │   └── GlobalExceptionHandler.java
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Order.java              (entity/aggregate root)
│   │   │   ├── OrderItem.java
│   │   │   └── OrderStatus.java
│   │   ├── service/
│   │   │   └── OrderService.java
│   │   └── repository/
│   │       └── OrderRepository.java    (interface)
│   ├── infrastructure/
│   │   ├── persistence/
│   │   │   └── JpaOrderRepository.java (implementation)
│   │   ├── messaging/
│   │   │   ├── OrderEventPublisher.java
│   │   │   └── PaymentEventConsumer.java
│   │   └── client/
│   │       └── ProductServiceClient.java
│   └── config/
│       ├── SecurityConfig.java
│       ├── KafkaConfig.java
│       └── CacheConfig.java
├── src/main/resources/
│   ├── application.yml
│   └── application-prod.yml
└── src/test/
    ├── integration/       (Testcontainers for DB/Kafka)
    └── unit/
```

### Essential Spring Boot Configurations
```yaml
# application.yml — comprehensive microservice config
spring:
  application:
    name: order-service

  datasource:
    url: jdbc:postgresql://postgres:5432/orders
    username: ${DB_USER}
    password: ${DB_PASS}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000

  jpa:
    hibernate:
      ddl-auto: validate    # Never use create/create-drop in prod
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_schema: orders

  redis:
    host: redis
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        min-idle: 2

  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: order-service
      auto-offset-reset: earliest
      enable-auto-commit: false    # Manual commit for at-least-once

resilience4j:
  circuitbreaker:
    instances:
      payment-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 10

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.company: DEBUG
    org.springframework.web: INFO
```

### Testing Strategy
```java
// Unit test — pure logic, no Spring context
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private EventPublisher eventPublisher;
    @InjectMocks private OrderService orderService;

    @Test
    void shouldCreateOrderAndPublishEvent() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("USR-1",
            List.of(new OrderItem("PROD-1", 2, BigDecimal.valueOf(50))));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        Order result = orderService.create(request);

        // Then
        assertThat(result.getUserId()).isEqualTo("USR-1");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(eventPublisher).publish(argThat(event ->
            event instanceof OrderCreatedEvent &&
            ((OrderCreatedEvent)event).getOrderId().equals(result.getId())));
    }
}

// Integration test — real DB via Testcontainers
@SpringBootTest
@Testcontainers
class OrderRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test_orders")
        .withUsername("test")
        .withPassword("test");

    @Autowired private OrderRepository repository;

    @Test
    void shouldPersistAndRetrieveOrder() {
        Order order = Order.create("USR-1", List.of(new OrderItem("P1", 1, BigDecimal.TEN)));
        Order saved = repository.save(order);

        Order found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getUserId()).isEqualTo("USR-1");
        assertThat(found.getItems()).hasSize(1);
    }
}

// Contract test — verify API contract between services
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private OrderService orderService;

    @Test
    void shouldReturn201WhenOrderCreated() throws Exception {
        when(orderService.create(any(), any())).thenReturn(buildTestOrder());

        mockMvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-User-Id", "USR-1")
            .content("""{"items":[{"productId":"P1","quantity":1}]}"""))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
```

### Observability: Logs, Metrics, Traces
```java
// Structured logging with correlation ID
@Component
public class CorrelationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        String correlationId = ((HttpServletRequest)req)
            .getHeader("X-Correlation-ID");
        if (correlationId == null) correlationId = UUID.randomUUID().toString();

        MDC.put("correlationId", correlationId);          // Added to all log entries
        MDC.put("service", "order-service");
        ((HttpServletResponse)res).setHeader("X-Correlation-ID", correlationId);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}

// Custom metrics
@Component
public class OrderMetrics {
    private final Counter ordersCreated;
    private final Counter ordersFailed;
    private final Timer orderProcessingTime;

    public OrderMetrics(MeterRegistry registry) {
        ordersCreated = Counter.builder("orders.created")
            .tag("service", "order-service")
            .description("Total orders created")
            .register(registry);

        ordersFailed = Counter.builder("orders.failed")
            .description("Total order creation failures")
            .register(registry);

        orderProcessingTime = Timer.builder("orders.processing.time")
            .description("Order processing duration")
            .register(registry);
    }

    public void recordOrderCreated() { ordersCreated.increment(); }
    public void recordOrderFailed()  { ordersFailed.increment(); }
    public <T> T recordProcessingTime(Supplier<T> supplier) {
        return orderProcessingTime.record(supplier);
    }
}
```

---

## 8. Real-World Architecture Case Study

### Case: Design a Food Delivery Platform (Like Uber Eats / Swiggy)

#### Requirements
```
Functional:
  - Customer browses restaurants, views menus
  - Customer places order, pays
  - Restaurant receives and accepts/rejects order
  - Delivery partner picks up and delivers
  - Real-time tracking of delivery
  - Customer/restaurant/delivery ratings

Non-Functional:
  - 10M daily active users
  - Peak: 100K orders/minute during dinner rush
  - 99.99% uptime for payment (4 minutes downtime/year)
  - Menu search < 100ms p99
  - Order placement < 500ms p99
  - Real-time location updates every 3 seconds
```

#### Back-of-Envelope Estimation
```
Users:     10M DAU, 1M restaurants, 500K delivery partners
Orders:    10M orders/day = ~700 orders/second average
           Peak: 100K orders/minute = ~1700 orders/second
Storage:
  Orders: 1KB × 10M/day × 365 days = ~3.6 TB/year
  Menu photos: 500KB × 50 items × 1M restaurants = 25 TB
  Location updates: 100 bytes × 500K partners × 1/3sec = 150K updates/sec

Bandwidth:
  Incoming: 150K × 100 bytes = 15 MB/sec (location)
  Outgoing: Depends on concurrent users viewing delivery
```

#### High-Level Architecture
```
┌──────────┐  ┌──────────┐  ┌──────────────┐
│ Customer │  │Restaurant│  │  Delivery    │
│ App/Web  │  │  App     │  │  Partner App │
└────┬─────┘  └────┬─────┘  └──────┬───────┘
     │              │               │
     └──────────────┼───────────────┘
                    │
          ┌─────────▼──────────┐
          │    API Gateway     │
          │  (Kong / Envoy)    │
          └──┬──────┬────┬─────┘
             │      │    │
    ┌────────▼┐ ┌───▼──┐ ┌▼──────────┐
    │  User   │ │Order │ │  Search   │
    │ Service │ │ Svc  │ │  Service  │
    └────┬────┘ └──┬───┘ └──┬────────┘
         │         │         │
    ┌────▼──┐  ┌───▼──┐  ┌──▼──────┐
    │Postgres│ │Postgres  │Elastic  │
    │(users)│  │(orders)│ │ Search  │
    └────────┘ └───┬──┘  └─────────┘
                   │
          ┌────────▼────────┐
          │   Kafka Event   │
          │      Bus        │
          └──┬──────┬───┬───┘
             │      │   │
    ┌────────▼┐ ┌───▼──┐ ┌▼──────────┐
    │Notif.   │ │Inv.  │ │  Location │
    │ Service │ │ Svc  │ │  Service  │
    └─────────┘ └──────┘ └──────────┘
                              │
                          WebSocket
                          (real-time
                           tracking)
```

#### Service Breakdown
```
User Service:
  Profile management, authentication, ratings
  DB: PostgreSQL (ACID for financial-adjacent)
  Cache: Redis for session + user profile

Restaurant Service:
  Menu management, availability, hours
  DB: PostgreSQL + Redis (menu cached, invalidated on edit)
  Search indexed to Elasticsearch

Search Service:
  Restaurant search, menu search, autocomplete
  DB: Elasticsearch (full-text, geo-search, facets)
  Updated via Kafka events from Restaurant Service

Order Service:
  Order lifecycle (pending → accepted → picked up → delivered)
  DB: PostgreSQL with partitioning by date
  Outbox pattern for reliable event publishing

Payment Service:
  Stripe/Razorpay integration, refunds
  DB: PostgreSQL (ACID critical)
  Idempotency keys to prevent double-charge

Location Service:
  Real-time delivery partner location
  DB: Redis (TTL-based, in-memory speed needed)
  Protocol: WebSocket for push to customer

Notification Service:
  Push, SMS, email notifications
  Async consumer of Kafka events
  No state — pure event processor

Delivery Matching Service:
  Assign nearest available delivery partner
  Algorithm: geo-hash based proximity search
  Redis GEO commands for O(log n) nearest partner lookup
```

#### Critical Design Decisions
```
1. Real-time location tracking:
   Problem: 500K partners × update every 3 sec = 167K writes/sec
   Solution: Redis GEO commands (O(log n)), TTL auto-expires stale
   WebSocket server for push to customers (not polling!)

2. Search < 100ms:
   Problem: SQL LIKE query on 1M restaurants + menus is too slow
   Solution: Elasticsearch with geo-distance query + text search
   Cache top-100 most searched queries in Redis

3. Order flow reliability (SAGA):
   Order placed → reserve inventory → charge payment → notify restaurant
   Compensate: payment failed → release inventory → cancel order → notify user

4. Menu photo storage:
   25 TB, globally served
   Solution: S3 + CloudFront CDN, photos served from edge
   Waterfall resize: serve responsive image sizes (thumbnail → full)

5. Peak traffic handling:
   1700 orders/sec during peak
   Solution: Horizontal scaling, auto-scaling groups
   Database connection pooling (PgBouncer for Postgres)
   Rate limiting at API gateway level
```

---

## Summary

### System Design Checklist

```
Requirements:
  □ Identify functional requirements
  □ Identify non-functional: latency, availability, consistency
  □ Estimate scale: users, requests/sec, data volume

Architecture:
  □ Choose monolith vs microservices (justify)
  □ Define service boundaries (one business capability per service)
  □ Design API Gateway (auth, rate limit, routing)
  □ Choose sync vs async communication per use case
  □ Define data stores per service (right tool for the job)

Scalability:
  □ Load balancing strategy
  □ Caching strategy (L1/L2/CDN)
  □ Database scaling (read replicas, sharding)
  □ Stateless services for horizontal scaling

Reliability:
  □ Circuit breakers for downstream calls
  □ Retry with exponential backoff
  □ Idempotency for critical operations
  □ Health checks and graceful degradation
  □ SAGA for distributed transactions

Observability:
  □ Structured logging with correlation IDs
  □ Metrics (Prometheus + Grafana)
  □ Distributed tracing (Jaeger / Zipkin)
  □ Alerting on SLIs/SLOs
```

### The Core Insight
```
Great system design is not about using every technology.
It is about making TRADEOFFS explicitly:

  Consistency vs Availability (CAP)
  Latency vs Throughput
  Strong consistency vs Eventual consistency
  Monolith simplicity vs Microservices flexibility
  Synchronous reliability vs Asynchronous throughput
  SQL correctness vs NoSQL scalability

The engineer who can articulate WHY they made each tradeoff,
and what they gave up in exchange, is the one who designs
systems that last.
```
