# Advanced OOP & Design Patterns — Complete Guide
> Object-Oriented Thinking & Scalable System Design
> Covers: Advanced OOP · SOLID Principles · Design Thinking · Creational · Structural · Behavioral Patterns · Real-World Architecture

---

## Table of Contents
1. [Advanced OOP Principles](#1-advanced-oop-principles)
2. [SOLID Principles](#2-solid-principles)
3. [Design Thinking in Java](#3-design-thinking-in-java)
4. [Creational Patterns](#4-creational-patterns)
5. [Structural Patterns](#5-structural-patterns)
6. [Behavioral Patterns](#6-behavioral-patterns)
7. [Real-World Architecture Examples](#7-real-world-architecture-examples)

---

## 1. Advanced OOP Principles

### The Four Pillars — Beyond the Basics

```
Encapsulation:  Bundle data + behavior; hide internal state
Abstraction:    Expose WHAT, hide HOW
Inheritance:    Reuse and extend behavior (IS-A relationship)
Polymorphism:   Same interface, different behavior at runtime
```

---

### 1.1 Encapsulation — Information Hiding Done Right

```java
// WRONG: Leaking internal representation
public class BankAccount {
    public List<Transaction> transactions = new ArrayList<>(); // Direct access = dangerous
    public double balance;
}

// RIGHT: Encapsulate internal state, expose behavior
public class BankAccount {
    private final String accountId;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String id, double initialBalance) {
        this.accountId = id;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
        transactions.add(new Transaction(TransactionType.CREDIT, amount, balance));
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient funds");
        balance -= amount;
        transactions.add(new Transaction(TransactionType.DEBIT, amount, balance));
    }

    // Return defensive copy — don't expose internal list
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }

    public double getBalance() { return balance; }
    public String getAccountId() { return accountId; }
}
```

**Key rules:**
- Fields are `private` by default
- Never return mutable internal collections — return copies or unmodifiable views
- Validate input in setters/methods, not in caller code
- Expose behavior (verbs), not data (nouns)

---

### 1.2 Abstraction — Program to Interfaces, Not Implementations

```java
// Define WHAT, not HOW
public interface PaymentProcessor {
    PaymentResult process(PaymentRequest request);
    boolean refund(String transactionId, double amount);
    PaymentStatus getStatus(String transactionId);
}

// Multiple HOW implementations
public class StripeProcessor implements PaymentProcessor {
    @Override
    public PaymentResult process(PaymentRequest request) {
        // Stripe-specific API call
        return new PaymentResult(true, "stripe_" + UUID.randomUUID(), "SUCCESS");
    }
    // ...
}

public class PayPalProcessor implements PaymentProcessor {
    @Override
    public PaymentResult process(PaymentRequest request) {
        // PayPal-specific API call
        return new PaymentResult(true, "paypal_" + UUID.randomUUID(), "SUCCESS");
    }
    // ...
}

// Client depends on abstraction — zero knowledge of Stripe or PayPal
public class CheckoutService {
    private final PaymentProcessor processor;  // Interface, not concrete class

    public CheckoutService(PaymentProcessor processor) {
        this.processor = processor;  // Injected at runtime
    }

    public OrderConfirmation checkout(Cart cart, PaymentInfo paymentInfo) {
        PaymentRequest request = buildRequest(cart, paymentInfo);
        PaymentResult result = processor.process(request);   // Polymorphic call
        return result.isSuccess()
            ? OrderConfirmation.success(result.getTransactionId())
            : OrderConfirmation.failed(result.getError());
    }
}
```

---

### 1.3 Inheritance vs Composition

```
"Favor composition over inheritance" — Gang of Four

WHY:
  Inheritance creates TIGHT COUPLING between parent and child.
  Composition creates LOOSE COUPLING through interfaces.

Inheritance breaks encapsulation:
  Child classes can observe or be broken by parent's internal behavior changes.

The Liskov test (informally): "Is every B truly an A in all contexts?"
  Car IS-A Vehicle ✓          → Inheritance OK
  Stack IS-A Vector ✗         → Java's Stack is broken because of this
  Square IS-A Rectangle ✗     → Classic LSP violation
```

```java
// WRONG: Inheritance to reuse code
public class EmailNotifier extends SMTPClient {  // Not "is-a" relationship
    public void notifyUser(User user, String message) {
        sendEmail(user.getEmail(), message);  // Reusing SMTP functionality
    }
}

// RIGHT: Composition to reuse code
public class EmailNotifier {
    private final SMTPClient smtpClient;  // Has-a relationship

    public EmailNotifier(SMTPClient smtpClient) {
        this.smtpClient = smtpClient;
    }

    public void notifyUser(User user, String message) {
        smtpClient.sendEmail(user.getEmail(), message);
    }
}
```

---

### 1.4 Polymorphism — Runtime vs Compile-Time

```java
// Runtime polymorphism (dynamic dispatch)
public abstract class Shape {
    abstract double area();
    abstract double perimeter();

    // Template method pattern — calls polymorphic methods
    public String describe() {
        return String.format("Shape: area=%.2f, perimeter=%.2f", area(), perimeter());
    }
}

public class Circle extends Shape {
    private double radius;
    @Override double area()      { return Math.PI * radius * radius; }
    @Override double perimeter() { return 2 * Math.PI * radius; }
}

public class Rectangle extends Shape {
    private double w, h;
    @Override double area()      { return w * h; }
    @Override double perimeter() { return 2 * (w + h); }
}

// Compile-time polymorphism (method overloading)
public class Converter {
    public String convert(int n)     { return "int:"    + n; }
    public String convert(double d)  { return "double:" + d; }
    public String convert(boolean b) { return "bool:"   + b; }
}

// Parametric polymorphism (generics)
public class Pair<A, B> {
    private final A first;
    private final B second;
    public Pair(A first, B second) { this.first = first; this.second = second; }
    public A getFirst()  { return first; }
    public B getSecond() { return second; }
}
```

---

### 1.5 Advanced Java OOP Features

```java
// Covariant return types
public class Animal {
    public Animal create() { return new Animal(); }
}
public class Dog extends Animal {
    @Override
    public Dog create() { return new Dog(); }   // More specific return type — valid
}

// Default methods in interfaces (Java 8+)
public interface Collection<E> {
    void add(E element);
    int size();
    default boolean isEmpty() { return size() == 0; }   // Provided default behavior
    default void addAll(Iterable<E> elements) {
        for (E e : elements) add(e);
    }
}

// Sealed classes (Java 17+) — restrict hierarchy
public sealed class Result<T> permits Success, Failure {
    // Only Success and Failure can extend Result
}
public final class Success<T> extends Result<T> {
    private final T value;
    public Success(T value) { this.value = value; }
    public T getValue() { return value; }
}
public final class Failure<T> extends Result<T> {
    private final String error;
    public Failure(String error) { this.error = error; }
    public String getError() { return error; }
}

// Records (Java 16+) — immutable data classes
public record Point(double x, double y) {
    // Compact constructor with validation
    public Point {
        if (Double.isNaN(x) || Double.isNaN(y))
            throw new IllegalArgumentException("Coordinates cannot be NaN");
    }
    public double distanceTo(Point other) {
        return Math.hypot(x - other.x, y - other.y);
    }
}
```

---

## 2. SOLID Principles

### Overview
```
S — Single Responsibility Principle (SRP)
O — Open/Closed Principle (OCP)
L — Liskov Substitution Principle (LSP)
I — Interface Segregation Principle (ISP)
D — Dependency Inversion Principle (DIP)
```

---

### 2.1 Single Responsibility Principle (SRP)

> A class should have **one reason to change**.

```java
// VIOLATION: Three responsibilities in one class
public class UserManager {
    public User createUser(String name, String email) { /* ... */ }
    public void sendWelcomeEmail(User user) { /* ... */ }     // Email concern
    public void saveToDatabase(User user) { /* ... */ }       // Persistence concern
    public String exportToCSV(List<User> users) { /* ... */ } // Export concern
}
// If email template changes → UserManager changes
// If DB schema changes → UserManager changes
// If CSV format changes → UserManager changes → 3 reasons to change!

// CORRECT: Separate responsibilities
public class UserService {
    private final UserRepository repository;
    private final EmailService emailService;

    public User createUser(String name, String email) {
        User user = new User(name, email);
        repository.save(user);
        emailService.sendWelcome(user);
        return user;
    }
}

public class UserRepository {
    public void save(User user) { /* Only DB concern */ }
    public User findById(Long id) { /* Only DB concern */ }
}

public class EmailService {
    public void sendWelcome(User user) { /* Only email concern */ }
}

public class UserExporter {
    public String toCSV(List<User> users) { /* Only export concern */ }
}
```

---

### 2.2 Open/Closed Principle (OCP)

> Software entities should be **open for extension, closed for modification**.

```java
// VIOLATION: Adding new shape requires modifying existing code
public class AreaCalculator {
    public double calculate(Object shape) {
        if (shape instanceof Circle c)    return Math.PI * c.radius * c.radius;
        if (shape instanceof Rectangle r) return r.width * r.height;
        // Adding Triangle requires modifying this method → OCP violation
        throw new IllegalArgumentException("Unknown shape");
    }
}

// CORRECT: Extend without modifying
public interface Shape {
    double area();
}

public class Circle implements Shape {
    private double radius;
    @Override public double area() { return Math.PI * radius * radius; }
}

public class Rectangle implements Shape {
    private double w, h;
    @Override public double area() { return w * h; }
}

// New Triangle class — zero modification to existing code
public class Triangle implements Shape {
    private double base, height;
    @Override public double area() { return 0.5 * base * height; }
}

// Calculator never needs to change regardless of new shapes
public class AreaCalculator {
    public double calculate(Shape shape) { return shape.area(); }
    public double total(List<Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }
}
```

---

### 2.3 Liskov Substitution Principle (LSP)

> If S is a subtype of T, objects of type T may be replaced with objects of type S **without altering correctness**.

```java
// CLASSIC VIOLATION: Square-Rectangle problem
public class Rectangle {
    protected int width, height;
    public void setWidth(int w)  { this.width  = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

public class Square extends Rectangle {
    @Override public void setWidth(int w)  { this.width = this.height = w; } // Both!
    @Override public void setHeight(int h) { this.width = this.height = h; } // Both!
}

// This breaks:
void testRectangle(Rectangle r) {
    r.setWidth(5);
    r.setHeight(4);
    assert r.area() == 20;  // FAILS for Square: 4×4=16, not 5×4=20
}
// Square cannot substitute Rectangle → LSP violated

// CORRECT: Don't make Square extend Rectangle
public interface Shape { int area(); }
public final class Rectangle implements Shape { /* independent */ }
public final class Square    implements Shape { /* independent */ }

// ANOTHER VIOLATION: Throwing exceptions in overrides
public class Bird {
    public void fly() { System.out.println("Flying..."); }
}
public class Penguin extends Bird {
    @Override public void fly() { throw new UnsupportedOperationException("Can't fly!"); }
    // Substituting Penguin for Bird breaks callers that call fly()
}

// CORRECT: Use more specific abstractions
public interface FlyingBird { void fly(); }
public interface SwimmingBird { void swim(); }
public class Sparrow implements FlyingBird { public void fly() { /* ... */ } }
public class Penguin implements SwimmingBird { public void swim() { /* ... */ } }
```

---

### 2.4 Interface Segregation Principle (ISP)

> Clients should **not be forced to depend on interfaces they don't use**.

```java
// VIOLATION: Fat interface forces unused implementations
public interface Worker {
    void work();
    void eat();
    void sleep();
    void getPaycheck();
    void attendMeeting();
}

// Robot doesn't eat or sleep
public class Robot implements Worker {
    @Override public void work() { /* OK */ }
    @Override public void eat()  { throw new UnsupportedOperationException(); } // Forced!
    @Override public void sleep(){ throw new UnsupportedOperationException(); } // Forced!
    // ...
}

// CORRECT: Segregate into focused interfaces
public interface Workable   { void work(); }
public interface Eatable    { void eat(); void sleep(); }
public interface Payable    { void getPaycheck(); }
public interface Meetable   { void attendMeeting(); }

// Each implementor takes only what it needs
public class Human   implements Workable, Eatable, Payable, Meetable { /* all */ }
public class Robot   implements Workable { /* only work */ }
public class Manager implements Workable, Payable, Meetable { /* no eat/sleep */ }
```

---

### 2.5 Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. **Both should depend on abstractions**.

```java
// VIOLATION: High-level OrderService depends on low-level MySQLDatabase
public class OrderService {
    private MySQLDatabase database = new MySQLDatabase(); // Concrete dependency!
    private EmailSender emailSender = new EmailSender();  // Concrete dependency!

    public void placeOrder(Order order) {
        database.save(order);
        emailSender.send(order.getUser().getEmail(), "Order placed!");
    }
}
// Cannot test without real MySQL. Cannot swap to MongoDB.

// CORRECT: Depend on abstractions
public interface OrderRepository { void save(Order order); }
public interface NotificationService { void notify(String recipient, String message); }

// High-level module — depends ONLY on abstractions
public class OrderService {
    private final OrderRepository repository;
    private final NotificationService notifier;

    // Dependencies injected from outside (DI)
    public OrderService(OrderRepository repository, NotificationService notifier) {
        this.repository = repository;
        this.notifier   = notifier;
    }

    public void placeOrder(Order order) {
        repository.save(order);
        notifier.notify(order.getUser().getEmail(), "Order placed!");
    }
}

// Low-level modules implement abstractions
public class MySQLOrderRepository implements OrderRepository {
    @Override public void save(Order order) { /* MySQL logic */ }
}
public class MongoOrderRepository implements OrderRepository {
    @Override public void save(Order order) { /* MongoDB logic */ }
}
public class EmailNotificationService implements NotificationService {
    @Override public void notify(String to, String msg) { /* SMTP logic */ }
}
// Testable with MockOrderRepository and MockNotificationService
```

---

## 3. Design Thinking in Java

### 3.1 Tell, Don't Ask

```java
// ASK: Ask for data, then make decisions outside the object
if (account.getBalance() >= order.getTotal()) {   // Asking for data
    account.setBalance(account.getBalance() - order.getTotal()); // Making decision
    order.setStatus(OrderStatus.PAID);
}

// TELL: Tell the object to do something
account.pay(order);   // Object makes the decision internally
// Inside Account:
public void pay(Order order) {
    if (balance < order.getTotal()) throw new InsufficientFundsException();
    balance -= order.getTotal();
    order.markAsPaid();
}
```

### 3.2 Immutability

```java
// Immutable class — thread-safe by design, no defensive copies needed
public final class Money {
    private final double amount;
    private final Currency currency;

    public Money(double amount, Currency currency) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        this.amount = amount;
        this.currency = Objects.requireNonNull(currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new CurrencyMismatchException("Cannot add different currencies");
        return new Money(this.amount + other.amount, this.currency); // Returns new instance
    }

    public Money multiply(double factor) {
        return new Money(this.amount * factor, this.currency);
    }

    public boolean isGreaterThan(Money other) { return this.amount > other.amount; }
    public double getAmount() { return amount; }
    public Currency getCurrency() { return currency; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Money m)) return false;
        return Double.compare(amount, m.amount) == 0 && currency.equals(m.currency);
    }
    @Override public int hashCode() { return Objects.hash(amount, currency); }
    @Override public String toString() { return currency + " " + String.format("%.2f", amount); }
}
```

### 3.3 Value Objects vs Entities

```java
// VALUE OBJECT: Defined by its attributes, no identity
// Two Money(100, USD) objects ARE equal
public record Money(BigDecimal amount, String currency) {
    public Money add(Money other) { return new Money(amount.add(other.amount), currency); }
}

// ENTITY: Has identity — two users with same name are DIFFERENT people
public class User {
    private final UUID id;    // Identity — never changes
    private String name;       // Attributes can change — still same user
    private String email;

    @Override public boolean equals(Object o) {
        if (!(o instanceof User u)) return false;
        return id.equals(u.id);   // Equality by ID only
    }
    @Override public int hashCode() { return id.hashCode(); }
}
```

---

## 4. Creational Patterns

### 4.1 Singleton Pattern

**Intent:** Ensure only one instance exists and provide global access.

```java
// Thread-safe Singleton with double-checked locking
public class DatabaseConnectionPool {
    private static volatile DatabaseConnectionPool instance;  // volatile for memory visibility
    private final List<Connection> connections;
    private final int maxConnections;

    private DatabaseConnectionPool(int maxConnections) {
        this.maxConnections = maxConnections;
        this.connections = new ArrayList<>();
        initializePool();
    }

    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {                           // First check (no lock)
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {                   // Second check (with lock)
                    instance = new DatabaseConnectionPool(10);
                }
            }
        }
        return instance;
    }

    // Better: Initialization-on-demand holder (lazy, thread-safe, no sync overhead)
    private static class Holder {
        static final DatabaseConnectionPool INSTANCE = new DatabaseConnectionPool(10);
    }
    public static DatabaseConnectionPool getInstanceBetter() { return Holder.INSTANCE; }
}

// Best: Enum Singleton (serialization-safe, reflection-safe)
public enum ConfigManager {
    INSTANCE;

    private final Properties config = new Properties();

    public String get(String key) { return config.getProperty(key); }
    public void set(String key, String value) { config.setProperty(key, value); }
}
```

**Real-world uses:** Database connection pools, thread pools, configuration managers, logging frameworks (Log4j, SLF4J)

---

### 4.2 Factory Method Pattern

**Intent:** Define an interface for creating an object, but let subclasses decide which class to instantiate.

```java
// Abstract creator
public abstract class NotificationFactory {
    // Factory method — subclasses provide concrete product
    public abstract Notification createNotification(String recipient, String message);

    // Template method that uses the factory method
    public void send(String recipient, String message) {
        Notification notification = createNotification(recipient, message);
        notification.prepare();
        notification.deliver();
        notification.log();
    }
}

// Concrete creators
public class EmailNotificationFactory extends NotificationFactory {
    @Override
    public Notification createNotification(String recipient, String message) {
        return new EmailNotification(recipient, message);
    }
}

public class SMSNotificationFactory extends NotificationFactory {
    @Override
    public Notification createNotification(String recipient, String message) {
        return new SMSNotification(recipient, message);
    }
}

public class PushNotificationFactory extends NotificationFactory {
    @Override
    public Notification createNotification(String recipient, String message) {
        return new PushNotification(recipient, message);
    }
}

// Selecting factory at runtime
public class NotificationService {
    public static NotificationFactory getFactory(NotificationType type) {
        return switch (type) {
            case EMAIL -> new EmailNotificationFactory();
            case SMS   -> new SMSNotificationFactory();
            case PUSH  -> new PushNotificationFactory();
        };
    }
}
```

---

### 4.3 Abstract Factory Pattern

**Intent:** Create families of related objects without specifying their concrete classes.

```java
// Abstract factory — creates a FAMILY of related UI components
public interface UIComponentFactory {
    Button createButton();
    TextField createTextField();
    Dialog createDialog();
}

// Concrete factories — different themes/platforms
public class LightThemeFactory implements UIComponentFactory {
    @Override public Button createButton()       { return new LightButton(); }
    @Override public TextField createTextField() { return new LightTextField(); }
    @Override public Dialog createDialog()       { return new LightDialog(); }
}

public class DarkThemeFactory implements UIComponentFactory {
    @Override public Button createButton()       { return new DarkButton(); }
    @Override public TextField createTextField() { return new DarkTextField(); }
    @Override public Dialog createDialog()       { return new DarkDialog(); }
}

// Client — uses only the abstract factory interface
public class Application {
    private final UIComponentFactory factory;

    public Application(UIComponentFactory factory) {
        this.factory = factory;
    }

    public void buildLoginScreen() {
        Button loginBtn  = factory.createButton();
        TextField email  = factory.createTextField();
        TextField pass   = factory.createTextField();
        Dialog dialog    = factory.createDialog();

        loginBtn.render();
        email.render();
        pass.render();
        dialog.show();
    }
}

// Usage
UIComponentFactory factory = isDarkMode ? new DarkThemeFactory() : new LightThemeFactory();
Application app = new Application(factory);
app.buildLoginScreen();
```

---

### 4.4 Builder Pattern

**Intent:** Construct complex objects step by step. Separate construction from representation.

```java
// Immutable complex object with Builder
public final class HttpRequest {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    private final int timeoutMs;
    private final int retries;
    private final boolean followRedirects;

    private HttpRequest(Builder builder) {
        this.url             = builder.url;
        this.method          = builder.method;
        this.headers         = Collections.unmodifiableMap(new HashMap<>(builder.headers));
        this.body            = builder.body;
        this.timeoutMs       = builder.timeoutMs;
        this.retries         = builder.retries;
        this.followRedirects = builder.followRedirects;
    }

    // Getters...

    public static class Builder {
        private final String url;          // Required
        private String method = "GET";     // Optional with defaults
        private Map<String, String> headers = new HashMap<>();
        private String body = null;
        private int timeoutMs = 5000;
        private int retries = 3;
        private boolean followRedirects = true;

        public Builder(String url) {
            this.url = Objects.requireNonNull(url, "URL cannot be null");
        }

        public Builder method(String method)          { this.method = method; return this; }
        public Builder header(String key, String val) { headers.put(key, val); return this; }
        public Builder body(String body)              { this.body = body; return this; }
        public Builder timeoutMs(int ms)              { this.timeoutMs = ms; return this; }
        public Builder retries(int retries)           { this.retries = retries; return this; }
        public Builder noRedirects()                  { this.followRedirects = false; return this; }

        public HttpRequest build() {
            if ("POST".equals(method) && body == null)
                throw new IllegalStateException("POST request requires a body");
            return new HttpRequest(this);
        }
    }
}

// Usage — fluent, readable, immutable
HttpRequest request = new HttpRequest.Builder("https://api.example.com/users")
    .method("POST")
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer " + token)
    .body("""{"name":"Alice","role":"admin"}""")
    .timeoutMs(10_000)
    .retries(5)
    .build();
```

---

### 4.5 Prototype Pattern

**Intent:** Create new objects by copying an existing object (prototype).

```java
public interface Cloneable<T> {
    T clone();
}

public class DocumentTemplate implements Cloneable<DocumentTemplate> {
    private String title;
    private String content;
    private List<String> tags;
    private DocumentStyle style;

    @Override
    public DocumentTemplate clone() {
        DocumentTemplate copy = new DocumentTemplate();
        copy.title   = this.title;
        copy.content = this.content;
        copy.tags    = new ArrayList<>(this.tags);    // Deep copy of mutable list
        copy.style   = this.style.clone();            // Deep copy of mutable object
        return copy;
    }
}

// Registry of prototypes
public class DocumentTemplateRegistry {
    private final Map<String, DocumentTemplate> templates = new HashMap<>();

    public void register(String name, DocumentTemplate template) {
        templates.put(name, template);
    }

    public DocumentTemplate get(String name) {
        DocumentTemplate template = templates.get(name);
        if (template == null) throw new IllegalArgumentException("Template not found: " + name);
        return template.clone();   // Always return a copy
    }
}

// Usage
DocumentTemplate invoiceTemplate = createInvoiceTemplate();
registry.register("invoice", invoiceTemplate);

DocumentTemplate myInvoice = registry.get("invoice");  // Fresh copy
myInvoice.setTitle("Invoice #1042");
myInvoice.addItem(new LineItem("Service", 500.0));
```

---

## 5. Structural Patterns

### 5.1 Adapter Pattern

**Intent:** Convert the interface of a class into another interface clients expect. Make incompatible interfaces compatible.

```java
// Target interface (what client expects)
public interface ModernPaymentGateway {
    PaymentResult charge(String customerId, BigDecimal amount, String currency);
    RefundResult refund(String chargeId, BigDecimal amount);
}

// Adaptee (legacy third-party library with incompatible interface)
public class LegacyPaymentSystem {
    public int processPayment(double amount, String card, String cvv) { /* ... */ return 200; }
    public boolean reverseTransaction(String txnRef) { /* ... */ return true; }
}

// Adapter — makes legacy compatible with modern interface
public class LegacyPaymentAdapter implements ModernPaymentGateway {
    private final LegacyPaymentSystem legacy;

    public LegacyPaymentAdapter(LegacyPaymentSystem legacy) {
        this.legacy = legacy;
    }

    @Override
    public PaymentResult charge(String customerId, BigDecimal amount, String currency) {
        // Translate: modern → legacy
        int statusCode = legacy.processPayment(amount.doubleValue(), customerId, "***");
        return statusCode == 200
            ? PaymentResult.success("legacy_" + System.currentTimeMillis())
            : PaymentResult.failed("Legacy system error: " + statusCode);
    }

    @Override
    public RefundResult refund(String chargeId, BigDecimal amount) {
        boolean success = legacy.reverseTransaction(chargeId);
        return success ? RefundResult.success() : RefundResult.failed("Reversal failed");
    }
}

// Client uses the modern interface — unaware of legacy system
ModernPaymentGateway gateway = new LegacyPaymentAdapter(new LegacyPaymentSystem());
PaymentResult result = gateway.charge("cust_123", BigDecimal.valueOf(99.99), "USD");
```

---

### 5.2 Decorator Pattern

**Intent:** Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

```java
// Component interface
public interface DataProcessor {
    String process(String data);
}

// Concrete component
public class PlainDataProcessor implements DataProcessor {
    @Override public String process(String data) { return data; }
}

// Base decorator
public abstract class DataProcessorDecorator implements DataProcessor {
    protected final DataProcessor wrapped;
    protected DataProcessorDecorator(DataProcessor wrapped) { this.wrapped = wrapped; }
}

// Concrete decorators — each adds one responsibility
public class EncryptionDecorator extends DataProcessorDecorator {
    public EncryptionDecorator(DataProcessor wrapped) { super(wrapped); }
    @Override
    public String process(String data) {
        String processed = wrapped.process(data);
        return "ENCRYPTED(" + encrypt(processed) + ")";
    }
    private String encrypt(String data) { return Base64.getEncoder().encodeToString(data.getBytes()); }
}

public class CompressionDecorator extends DataProcessorDecorator {
    public CompressionDecorator(DataProcessor wrapped) { super(wrapped); }
    @Override
    public String process(String data) {
        String processed = wrapped.process(data);
        return "COMPRESSED(" + compress(processed) + ")";
    }
    private String compress(String data) { return data.replaceAll("\\s+", ""); }
}

public class LoggingDecorator extends DataProcessorDecorator {
    public LoggingDecorator(DataProcessor wrapped) { super(wrapped); }
    @Override
    public String process(String data) {
        System.out.println("[LOG] Processing: " + data.substring(0, Math.min(20, data.length())));
        String result = wrapped.process(data);
        System.out.println("[LOG] Result length: " + result.length());
        return result;
    }
}

// Compose decorators at runtime
DataProcessor processor = new LoggingDecorator(
                            new EncryptionDecorator(
                              new CompressionDecorator(
                                new PlainDataProcessor())));

processor.process("Hello World");
// LOG → then compress → then encrypt → then log result
```

---

### 5.3 Facade Pattern

**Intent:** Provide a simplified interface to a complex subsystem.

```java
// Complex subsystems
public class UserAuthService { public boolean authenticate(String user, String pass) { /* ... */ return true; } }
public class OrderService    { public Order createOrder(Cart cart) { /* ... */ return new Order(); } }
public class PaymentService  { public PaymentResult charge(Order order, Card card) { /* ... */ return PaymentResult.success("txn123"); } }
public class InventoryService{ public void reserve(Order order) { /* ... */ } }
public class ShippingService { public Shipment schedule(Order order) { /* ... */ return new Shipment(); } }
public class EmailService    { public void sendConfirmation(Order order) { /* ... */ } }

// Facade: one simple entry point for "place order" workflow
public class OrderFacade {
    private final UserAuthService auth;
    private final OrderService orders;
    private final PaymentService payments;
    private final InventoryService inventory;
    private final ShippingService shipping;
    private final EmailService email;

    // Constructor injection
    public OrderFacade(UserAuthService auth, OrderService orders,
                       PaymentService payments, InventoryService inventory,
                       ShippingService shipping, EmailService email) {
        this.auth = auth; this.orders = orders; this.payments = payments;
        this.inventory = inventory; this.shipping = shipping; this.email = email;
    }

    // Simple facade method — hides all complexity
    public OrderResult placeOrder(String username, String password, Cart cart, Card card) {
        if (!auth.authenticate(username, password))
            return OrderResult.authFailed();

        Order order = orders.createOrder(cart);
        PaymentResult payment = payments.charge(order, card);
        if (!payment.isSuccess())
            return OrderResult.paymentFailed(payment.getError());

        inventory.reserve(order);
        Shipment shipment = shipping.schedule(order);
        email.sendConfirmation(order);

        return OrderResult.success(order.getId(), shipment.getTrackingNumber());
    }
}

// Client calls ONE method instead of coordinating 6 services
OrderFacade facade = new OrderFacade(/*inject all services*/);
OrderResult result = facade.placeOrder("alice", "pass123", cart, card);
```

---

### 5.4 Proxy Pattern

**Intent:** Provide a surrogate or placeholder for another object to control access.

```java
// Real subject
public interface ImageLoader {
    Image load(String url);
}

public class RemoteImageLoader implements ImageLoader {
    @Override
    public Image load(String url) {
        System.out.println("Fetching from network: " + url);
        return networkFetch(url);  // Expensive operation
    }
}

// Caching Proxy
public class CachingImageLoaderProxy implements ImageLoader {
    private final ImageLoader real;
    private final Map<String, Image> cache = new ConcurrentHashMap<>();

    public CachingImageLoaderProxy(ImageLoader real) { this.real = real; }

    @Override
    public Image load(String url) {
        return cache.computeIfAbsent(url, k -> {
            System.out.println("Cache miss — fetching: " + url);
            return real.load(k);
        });
    }
}

// Access control proxy
public class AuthorizedImageLoaderProxy implements ImageLoader {
    private final ImageLoader real;
    private final SecurityContext security;

    public AuthorizedImageLoaderProxy(ImageLoader real, SecurityContext security) {
        this.real = real;
        this.security = security;
    }

    @Override
    public Image load(String url) {
        if (!security.hasPermission("IMAGE_READ"))
            throw new AccessDeniedException("Not authorized to load images");
        return real.load(url);
    }
}
```

---

### 5.5 Composite Pattern

**Intent:** Compose objects into tree structures to represent part-whole hierarchies.

```java
// Component
public interface FileSystemEntry {
    String getName();
    long getSize();
    void display(String indent);
}

// Leaf
public class File implements FileSystemEntry {
    private final String name;
    private final long size;

    public File(String name, long size) { this.name = name; this.size = size; }

    @Override public String getName() { return name; }
    @Override public long getSize()   { return size; }
    @Override public void display(String indent) {
        System.out.println(indent + "📄 " + name + " (" + size + " bytes)");
    }
}

// Composite
public class Directory implements FileSystemEntry {
    private final String name;
    private final List<FileSystemEntry> children = new ArrayList<>();

    public Directory(String name) { this.name = name; }

    public void add(FileSystemEntry entry)    { children.add(entry); }
    public void remove(FileSystemEntry entry) { children.remove(entry); }

    @Override public String getName() { return name; }

    @Override public long getSize() {
        return children.stream().mapToLong(FileSystemEntry::getSize).sum();
    }

    @Override public void display(String indent) {
        System.out.println(indent + "📁 " + name + "/");
        children.forEach(child -> child.display(indent + "  "));
    }
}

// Usage — treat files and directories uniformly
Directory root = new Directory("root");
root.add(new File("readme.txt", 1024));
Directory src = new Directory("src");
src.add(new File("Main.java", 4096));
src.add(new File("Utils.java", 2048));
root.add(src);
root.display("");
System.out.println("Total size: " + root.getSize());
```

---

## 6. Behavioral Patterns

### 6.1 Observer Pattern

**Intent:** Define a one-to-many dependency so that when one object changes state, all dependents are notified automatically.

```java
// Event-driven observer (type-safe version)
public interface EventListener<T> {
    void onEvent(T event);
}

public class EventBus {
    private final Map<Class<?>, List<EventListener<?>>> listeners = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<?> listener : eventListeners) {
                ((EventListener<T>) listener).onEvent(event);
            }
        }
    }
}

// Events
public record OrderPlacedEvent(String orderId, String userId, BigDecimal amount) {}
public record PaymentFailedEvent(String orderId, String reason) {}

// Listeners
public class InventoryListener implements EventListener<OrderPlacedEvent> {
    @Override public void onEvent(OrderPlacedEvent event) {
        System.out.println("[Inventory] Reserving items for order: " + event.orderId());
    }
}
public class EmailListener implements EventListener<OrderPlacedEvent> {
    @Override public void onEvent(OrderPlacedEvent event) {
        System.out.println("[Email] Sending confirmation for order: " + event.orderId());
    }
}
public class AnalyticsListener implements EventListener<OrderPlacedEvent> {
    @Override public void onEvent(OrderPlacedEvent event) {
        System.out.println("[Analytics] Recording $" + event.amount() + " sale");
    }
}

// Usage
EventBus bus = new EventBus();
bus.subscribe(OrderPlacedEvent.class, new InventoryListener());
bus.subscribe(OrderPlacedEvent.class, new EmailListener());
bus.subscribe(OrderPlacedEvent.class, new AnalyticsListener());

bus.publish(new OrderPlacedEvent("ORD-001", "USR-123", BigDecimal.valueOf(99.99)));
// All three listeners react independently
```

---

### 6.2 Strategy Pattern

**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

```java
// Strategy interface
public interface SortingStrategy<T extends Comparable<T>> {
    void sort(List<T> list);
    String getName();
}

// Concrete strategies
public class QuickSortStrategy<T extends Comparable<T>> implements SortingStrategy<T> {
    @Override public void sort(List<T> list) { Collections.sort(list); }  // Simplified
    @Override public String getName() { return "QuickSort"; }
}

public class BubbleSortStrategy<T extends Comparable<T>> implements SortingStrategy<T> {
    @Override public void sort(List<T> list) {
        for (int i = 0; i < list.size()-1; i++)
            for (int j = 0; j < list.size()-i-1; j++)
                if (list.get(j).compareTo(list.get(j+1)) > 0) {
                    T temp = list.get(j); list.set(j, list.get(j+1)); list.set(j+1, temp);
                }
    }
    @Override public String getName() { return "BubbleSort"; }
}

// Context — uses strategy
public class Sorter<T extends Comparable<T>> {
    private SortingStrategy<T> strategy;

    public Sorter(SortingStrategy<T> strategy) { this.strategy = strategy; }

    public void setStrategy(SortingStrategy<T> strategy) { this.strategy = strategy; }

    public List<T> sort(List<T> data) {
        List<T> copy = new ArrayList<>(data);
        long start = System.nanoTime();
        strategy.sort(copy);
        long duration = System.nanoTime() - start;
        System.out.printf("[%s] sorted %d elements in %d ns%n", strategy.getName(), copy.size(), duration);
        return copy;
    }
}

// Real-world: Payment strategy
public interface PricingStrategy {
    BigDecimal calculatePrice(Product product, Customer customer);
}
public class RegularPricing  implements PricingStrategy { /* full price */ }
public class MemberPricing   implements PricingStrategy { /* 10% discount */ }
public class VIPPricing      implements PricingStrategy { /* 25% discount */ }
public class SalePricing     implements PricingStrategy { /* 50% off */ }
```

---

### 6.3 Command Pattern

**Intent:** Encapsulate a request as an object, supporting undo/redo, queuing, and logging.

```java
// Command interface
public interface Command {
    void execute();
    void undo();
    String getDescription();
}

// Concrete commands
public class TransferMoneyCommand implements Command {
    private final BankAccount from;
    private final BankAccount to;
    private final double amount;
    private boolean executed = false;

    public TransferMoneyCommand(BankAccount from, BankAccount to, double amount) {
        this.from = from; this.to = to; this.amount = amount;
    }

    @Override
    public void execute() {
        from.withdraw(amount);
        to.deposit(amount);
        executed = true;
        System.out.printf("Transferred $%.2f from %s to %s%n", amount, from.getId(), to.getId());
    }

    @Override
    public void undo() {
        if (!executed) throw new IllegalStateException("Command not executed");
        to.withdraw(amount);
        from.deposit(amount);
        System.out.printf("UNDONE: Transfer $%.2f%n", amount);
    }

    @Override public String getDescription() { return "Transfer $" + amount; }
}

// Invoker — executes and manages command history
public class TransactionManager {
    private final Deque<Command> history = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public void execute(Command command) {
        command.execute();
        history.push(command);
        redoStack.clear();   // New command clears redo history
    }

    public void undo() {
        if (history.isEmpty()) { System.out.println("Nothing to undo"); return; }
        Command command = history.pop();
        command.undo();
        redoStack.push(command);
    }

    public void redo() {
        if (redoStack.isEmpty()) { System.out.println("Nothing to redo"); return; }
        Command command = redoStack.pop();
        command.execute();
        history.push(command);
    }

    public void printHistory() {
        System.out.println("Transaction history:");
        history.forEach(c -> System.out.println("  " + c.getDescription()));
    }
}
```

---

### 6.4 Template Method Pattern

**Intent:** Define the skeleton of an algorithm in a base class, deferring some steps to subclasses.

```java
// Abstract template
public abstract class DataMigrator {

    // Template method — defines the algorithm skeleton
    public final void migrate() {
        System.out.println("=== Starting migration ===");
        List<Record> data = extractData();
        List<Record> transformed = transformData(data);
        validateData(transformed);        // Hook with default implementation
        loadData(transformed);
        cleanup();                        // Hook with default implementation
        System.out.println("=== Migration complete ===");
    }

    protected abstract List<Record> extractData();
    protected abstract List<Record> transformData(List<Record> data);
    protected abstract void loadData(List<Record> data);

    // Hooks — optional override
    protected void validateData(List<Record> data) {
        System.out.println("Default validation: " + data.size() + " records");
    }
    protected void cleanup() {
        System.out.println("Default cleanup complete");
    }
}

// Subclass: MySQL to Postgres migration
public class MySQLToPostgresMigrator extends DataMigrator {
    @Override
    protected List<Record> extractData() {
        System.out.println("Extracting from MySQL...");
        return fetchFromMySQL();
    }
    @Override
    protected List<Record> transformData(List<Record> data) {
        System.out.println("Converting MySQL types to Postgres...");
        return data.stream().map(this::convertTypes).collect(Collectors.toList());
    }
    @Override
    protected void loadData(List<Record> data) {
        System.out.println("Bulk inserting " + data.size() + " records into Postgres...");
    }
    @Override
    protected void validateData(List<Record> data) {
        System.out.println("Checking referential integrity...");
        super.validateData(data);
    }
}
```

---

### 6.5 Chain of Responsibility Pattern

**Intent:** Pass a request along a chain of handlers until one handles it.

```java
public abstract class RequestHandler {
    protected RequestHandler next;

    public RequestHandler setNext(RequestHandler next) {
        this.next = next;
        return next;   // Enables chaining: h1.setNext(h2).setNext(h3)
    }

    public abstract void handle(HttpRequest request);

    protected void passToNext(HttpRequest request) {
        if (next != null) next.handle(request);
        else System.out.println("[Chain] Request not handled");
    }
}

// Handlers in an HTTP middleware chain
public class AuthenticationHandler extends RequestHandler {
    @Override public void handle(HttpRequest request) {
        if (!request.hasHeader("Authorization")) {
            System.out.println("[Auth] 401 Unauthorized");
            return;  // Stop chain
        }
        System.out.println("[Auth] Authenticated ✓");
        passToNext(request);
    }
}

public class RateLimitHandler extends RequestHandler {
    private final Map<String, Integer> requestCounts = new HashMap<>();
    @Override public void handle(HttpRequest request) {
        String ip = request.getClientIP();
        int count = requestCounts.merge(ip, 1, Integer::sum);
        if (count > 100) {
            System.out.println("[RateLimit] 429 Too Many Requests from " + ip);
            return;
        }
        System.out.println("[RateLimit] OK (" + count + "/100) ✓");
        passToNext(request);
    }
}

public class LoggingHandler extends RequestHandler {
    @Override public void handle(HttpRequest request) {
        System.out.println("[Log] " + request.getMethod() + " " + request.getPath());
        passToNext(request);
    }
}

public class BusinessHandler extends RequestHandler {
    @Override public void handle(HttpRequest request) {
        System.out.println("[Business] Processing request: " + request.getPath());
    }
}

// Chain setup
RequestHandler auth = new AuthenticationHandler();
auth.setNext(new RateLimitHandler())
    .setNext(new LoggingHandler())
    .setNext(new BusinessHandler());
auth.handle(request);
```

---

### 6.6 State Pattern

**Intent:** Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

```java
// State interface
public interface VendingMachineState {
    void insertCoin(VendingMachine machine);
    void selectProduct(VendingMachine machine, String product);
    void dispense(VendingMachine machine);
    void ejectCoin(VendingMachine machine);
    String getStateName();
}

// Context
public class VendingMachine {
    private VendingMachineState currentState;
    private double balance;
    private final Map<String, Double> products;

    public VendingMachine() {
        products = Map.of("Cola", 1.50, "Water", 1.00, "Chips", 2.00);
        currentState = new IdleState();
    }

    public void insertCoin(double amount) { balance += amount; currentState.insertCoin(this); }
    public void selectProduct(String product) { currentState.selectProduct(this, product); }
    public void dispense() { currentState.dispense(this); }
    public void ejectCoin() { currentState.ejectCoin(this); }

    public void setState(VendingMachineState state) {
        System.out.println("State: " + currentState.getStateName() + " → " + state.getStateName());
        this.currentState = state;
    }
    public double getBalance() { return balance; }
    public void setBalance(double b) { balance = b; }
    public Map<String, Double> getProducts() { return products; }
}

// States
public class IdleState implements VendingMachineState {
    @Override public void insertCoin(VendingMachine m) {
        System.out.println("Coin accepted: $" + m.getBalance());
        m.setState(new HasMoneyState());
    }
    @Override public void selectProduct(VendingMachine m, String p) { System.out.println("Please insert coin first"); }
    @Override public void dispense(VendingMachine m)               { System.out.println("Please insert coin first"); }
    @Override public void ejectCoin(VendingMachine m)              { System.out.println("No coin to eject"); }
    @Override public String getStateName() { return "IDLE"; }
}

public class HasMoneyState implements VendingMachineState {
    private String selectedProduct;
    @Override public void insertCoin(VendingMachine m)     { System.out.println("Additional coin: $" + m.getBalance()); }
    @Override public void selectProduct(VendingMachine m, String p) {
        Double price = m.getProducts().get(p);
        if (price == null) { System.out.println("Product not available"); return; }
        if (m.getBalance() < price) { System.out.println("Insufficient funds. Need $" + (price - m.getBalance())); return; }
        selectedProduct = p;
        System.out.println("Selected: " + p + " ($" + price + ")");
        m.setState(new ProductSelectedState(p, price));
    }
    @Override public void dispense(VendingMachine m)  { System.out.println("Please select a product first"); }
    @Override public void ejectCoin(VendingMachine m) { System.out.println("Returned: $" + m.getBalance()); m.setBalance(0); m.setState(new IdleState()); }
    @Override public String getStateName() { return "HAS_MONEY"; }
}

public class ProductSelectedState implements VendingMachineState {
    private final String product; private final double price;
    public ProductSelectedState(String p, double price) { this.product=p; this.price=price; }
    @Override public void insertCoin(VendingMachine m) { System.out.println("Already selected product"); }
    @Override public void selectProduct(VendingMachine m, String p) { System.out.println("Already selected"); }
    @Override public void dispense(VendingMachine m) {
        System.out.println("Dispensing: " + product);
        double change = m.getBalance() - price;
        if (change > 0) System.out.printf("Change returned: $%.2f%n", change);
        m.setBalance(0); m.setState(new IdleState());
    }
    @Override public void ejectCoin(VendingMachine m) { System.out.println("Cannot eject after selecting"); }
    @Override public String getStateName() { return "PRODUCT_SELECTED"; }
}
```

---

## 7. Real-World Architecture Examples

### 7.1 E-Commerce Order Processing (Multi-Pattern)

```
Patterns used:
  Facade       → OrderFacade for client simplicity
  Observer     → Event bus for order events
  Strategy     → Pricing strategies per customer tier
  Command      → Order actions (place, cancel, modify) with undo
  Chain of Resp→ Order validation pipeline
  Factory      → Payment processor selection
  Builder      → Order construction
```

```java
// Validation chain
OrderValidator validator = new StockValidator();
validator.setNext(new PriceValidator())
         .setNext(new FraudValidator())
         .setNext(new AddressValidator());

// Strategy selected per customer
PricingStrategy pricing = switch (customer.getTier()) {
    case VIP      -> new VIPPricingStrategy();
    case MEMBER   -> new MemberPricingStrategy();
    case REGULAR  -> new RegularPricingStrategy();
};

// Command for undo capability
Command placeOrder = new PlaceOrderCommand(order, repository, eventBus, pricing);
transactionManager.execute(placeOrder);

// Event bus notifies downstream systems
eventBus.publish(new OrderPlacedEvent(order.getId(), customer.getId(), order.getTotal()));
// → InventoryListener, EmailListener, AnalyticsListener, ShippingListener
```

### 7.2 Ride-Sharing Platform (State + Strategy + Observer)

```
State machine for ride:
  REQUESTED → DRIVER_ASSIGNED → PICKUP → IN_PROGRESS → COMPLETED / CANCELLED

Strategy for pricing:
  SurgePricingStrategy, StandardPricingStrategy, CorporatePricingStrategy

Observer for real-time updates:
  DriverLocationEvent → RiderApp, AdminDashboard, ETACalculator
```

### 7.3 Microservices Event-Driven Architecture

```
Patterns in distributed systems:
  Saga pattern          → Distributed transactions via event chains
  Circuit Breaker       → Proxy pattern + State pattern for fault tolerance
  API Gateway           → Facade pattern for microservices entry point
  Event Sourcing        → Command pattern persisted as event log
  CQRS                  → Strategy pattern: read model vs write model
```

### 7.4 Pattern Decision Guide

```
CREATING OBJECTS:
  One instance needed globally?               → Singleton
  Creation logic varies by type?              → Factory Method
  Families of related objects?                → Abstract Factory
  Complex construction step by step?          → Builder
  Clone existing configured objects?          → Prototype

STRUCTURING CODE:
  Incompatible interfaces need to work?       → Adapter
  Add behavior without changing class?        → Decorator
  Complex subsystem needs simple API?         → Facade
  Control/cache/log access to object?         → Proxy
  Tree structure, treat parts and whole same? → Composite

BEHAVIOR:
  Decouple sender from receivers?             → Observer
  Swap algorithms at runtime?                 → Strategy
  Queue, undo, log operations?                → Command
  Fix algorithm skeleton, vary steps?         → Template Method
  Pass request through handlers?              → Chain of Responsibility
  Object changes behavior with state?         → State
  Two incompatible interfaces communicate?    → Mediator
```

---

## Summary

### The Core OOP + Patterns Mindset

```
1. Encapsulate what varies
   → Identify what changes and isolate it behind an interface

2. Program to interfaces, not implementations
   → Client code never knows concrete classes

3. Favor composition over inheritance
   → "Has-a" beats "Is-a" for flexibility

4. Depend on abstractions, not concretions
   → Both high and low level modules depend on interfaces

5. Classes should be open for extension, closed for modification
   → Add new behavior via new classes, not by editing existing ones

The result: code that is:
  ✓ Testable    (depends on interfaces → mockable)
  ✓ Extensible  (open/closed → add without breaking)
  ✓ Maintainable (SRP → one reason to change)
  ✓ Flexible    (strategy/DI → swap implementations)
  ✓ Reusable    (well-defined interfaces → plug anywhere)
```
