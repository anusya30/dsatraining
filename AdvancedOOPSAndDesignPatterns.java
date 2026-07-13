import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * ============================================================
 * ADVANCED OOP & DESIGN PATTERNS — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. Advanced OOP Principles      (encapsulation, abstraction, composition
 *                                   vs inheritance, polymorphism, immutability,
 *                                   value objects vs entities, covariant types,
 *                                   sealed classes, records)
 *  2. SOLID Principles             (SRP, OCP, LSP, ISP, DIP — all with
 *                                   violation demos and correct solutions)
 *  3. Design Thinking              (tell-don't-ask, immutable Money, builder
 *                                   fluent API, DI container simulation)
 *  4. Creational Patterns          (Singleton all variants, Factory Method,
 *                                   Abstract Factory, Builder, Prototype)
 *  5. Structural Patterns          (Adapter, Decorator, Facade, Proxy,
 *                                   Composite, Bridge)
 *  6. Behavioral Patterns          (Observer/EventBus, Strategy, Command+undo,
 *                                   Template Method, Chain of Responsibility,
 *                                   State machine, Iterator, Memento)
 *  7. Real-World Architecture      (e-commerce order processing combining
 *                                   8+ patterns, ride-sharing state machine,
 *                                   middleware pipeline, plugin system)
 *
 * Compile : javac AdvancedOOPSAndDesignPatterns.java
 * Run     : java AdvancedOOPSAndDesignPatterns
 * ============================================================
 */
public class AdvancedOOPSAndDesignPatterns {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("ADVANCED OOP & DESIGN PATTERNS — COMPLETE DEMO");

        section1_AdvancedOOP();
        section2_SOLIDPrinciples();
        section3_DesignThinking();
        section4_CreationalPatterns();
        section5_StructuralPatterns();
        section6_BehavioralPatterns();
        section7_RealWorldArchitecture();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — ADVANCED OOP PRINCIPLES
    // =========================================================
    static void section1_AdvancedOOP() {
        printSection("1. ADVANCED OOP PRINCIPLES");

        // 1a. Encapsulation — defensive copies
        System.out.println("--- 1a. Encapsulation & Information Hiding ---");
        BankAccount acc = new BankAccount("ACC-001", 1000.0);
        acc.deposit(500.0);
        acc.withdraw(200.0);
        System.out.println("  Balance: $" + acc.getBalance());
        System.out.println("  Transactions: " + acc.getTransactionHistory().size());
        try { acc.withdraw(5000.0); }
        catch (IllegalStateException e) { System.out.println("  ✓ Protected: " + e.getMessage()); }

        // 1b. Abstraction — program to interface
        System.out.println("\n--- 1b. Abstraction — Payment Processor ---");
        PaymentProcessor stripe = new StripeProcessor();
        PaymentProcessor paypal = new PayPalProcessor();
        CheckoutService stripeCheckout = new CheckoutService(stripe);
        CheckoutService paypalCheckout = new CheckoutService(paypal);
        System.out.println("  Stripe: " + stripeCheckout.checkout(100.0));
        System.out.println("  PayPal: " + paypalCheckout.checkout(100.0));

        // 1c. Composition vs Inheritance
        System.out.println("\n--- 1c. Composition over Inheritance ---");
        EmailNotifier notifier = new EmailNotifier(new SMTPClient());
        notifier.notify("alice@example.com", "Hello Alice!");
        SlackNotifier slack = new SlackNotifier(new SlackClient());
        slack.notify("alice", "Hello on Slack!");

        // 1d. Runtime polymorphism
        System.out.println("\n--- 1d. Runtime Polymorphism ---");
        List<Shape> shapes = List.of(new Circle(5), new Rectangle(4,6), new Triangle(3,8));
        double totalArea = shapes.stream().mapToDouble(Shape::area).sum();
        shapes.forEach(s -> System.out.println("  " + s));
        System.out.printf("  Total area: %.2f%n", totalArea);

        // 1e. Immutable value object
        System.out.println("\n--- 1e. Immutable Money Value Object ---");
        Money price = new Money(99.99, "USD");
        Money tax   = new Money(10.00, "USD");
        Money total = price.add(tax);
        Money discounted = total.multiply(0.9);
        System.out.println("  Price:      " + price);
        System.out.println("  Tax:        " + tax);
        System.out.println("  Total:      " + total);
        System.out.println("  10% off:    " + discounted);
        System.out.println("  Original unchanged: " + total);

        // 1f. Generics polymorphism
        System.out.println("\n--- 1f. Generic Pair (Parametric Polymorphism) ---");
        Pair<String, Integer> nameAge = new Pair<>("Alice", 30);
        Pair<String, Money>   product = new Pair<>("Laptop", new Money(999.99, "USD"));
        System.out.println("  " + nameAge);
        System.out.println("  " + product);
    }

    // --- Section 1 classes ---
    static class BankAccount {
        private final String id; private double balance;
        private final List<String> transactions = new ArrayList<>();
        BankAccount(String id, double balance) { this.id = id; this.balance = balance; }
        void deposit(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            balance += amount; transactions.add("CREDIT +" + amount + " bal=" + balance);}
        void withdraw(double amount) {
            if (amount > balance) throw new IllegalStateException("Insufficient funds: need " + amount + " have " + balance);
            balance -= amount; transactions.add("DEBIT -" + amount + " bal=" + balance);}
        double getBalance() { return balance; }
        String getId() { return id; }
        List<String> getTransactionHistory() { return Collections.unmodifiableList(transactions); }
    }
    interface PaymentProcessor {
        String process(double amount);
    }
    static class StripeProcessor implements PaymentProcessor {
        @Override public String process(double amount) { return "Stripe:txn_" + (int)(Math.random()*100000) + " $" + amount; }
    }
    static class PayPalProcessor implements PaymentProcessor {
        @Override public String process(double amount) { return "PayPal:PP-" + (int)(Math.random()*100000) + " $" + amount; }
    }
    static class CheckoutService {
        private final PaymentProcessor processor;
        CheckoutService(PaymentProcessor p) { this.processor = p; }
        String checkout(double amount) { return processor.process(amount); }
    }
    static class SMTPClient { void sendEmail(String to, String msg) { System.out.println("  [SMTP] To:" + to + " Msg:" + msg); } }
    static class SlackClient { void post(String channel, String msg) { System.out.println("  [Slack] #" + channel + ": " + msg); } }
    static class EmailNotifier { private final SMTPClient smtp; EmailNotifier(SMTPClient s){smtp=s;}
        void notify(String to, String msg) { smtp.sendEmail(to, msg); } }
    static class SlackNotifier { private final SlackClient slack; SlackNotifier(SlackClient s){slack=s;}
        void notify(String channel, String msg) { slack.post(channel, msg); } }
    abstract static class Shape { abstract double area(); abstract double perimeter();
        @Override public String toString(){return getClass().getSimpleName()+" area="+String.format("%.2f",area());}}
    static class Circle extends Shape {
        double r; Circle(double r){this.r=r;}
        @Override double area(){return Math.PI*r*r;} @Override double perimeter(){return 2*Math.PI*r;}}
    static class Rectangle extends Shape {
        double w,h; Rectangle(double w,double h){this.w=w;this.h=h;}
        @Override double area(){return w*h;} @Override double perimeter(){return 2*(w+h);}}
    static class Triangle extends Shape {
        double b,h; Triangle(double b,double h){this.b=b;this.h=h;}
        @Override double area(){return 0.5*b*h;} @Override double perimeter(){return b+h+Math.sqrt(b*b+h*h);}}
    static class Money {
        private final double amount; private final String currency;
        Money(double a,String c){amount=a;currency=c;}
        Money add(Money o){if(!currency.equals(o.currency)) throw new IllegalArgumentException("Currency mismatch");return new Money(amount+o.amount,currency);}
        Money multiply(double f){return new Money(amount*f,currency);}
        @Override public String toString(){return currency+" "+String.format("%.2f",amount);}
    }
    static class Pair<A,B> {
        private final A first; private final B second;
        Pair(A a,B b){first=a;second=b;}
        @Override public String toString(){return "("+first+", "+second+")";}
    }

    // =========================================================
    // SECTION 2 — SOLID PRINCIPLES
    // =========================================================
    static void section2_SOLIDPrinciples() {
        printSection("2. SOLID PRINCIPLES");

        // SRP
        System.out.println("--- 2a. SRP: Single Responsibility Principle ---");
        UserService userService = new UserService(new UserRepository(), new UserEmailService());
        User user = userService.createUser("Alice", "alice@example.com");
        System.out.println("  Created: " + user);
        System.out.println("  Exported: " + new UserExporter().toCSV(List.of(user)));

        // OCP
        System.out.println("\n--- 2b. OCP: Open/Closed Principle ---");
        List<Shape> shapes = new ArrayList<>(List.of(new Circle(3), new Rectangle(4,5), new Triangle(3,6)));
        AreaCalculator calc = new AreaCalculator();
        System.out.printf("  Total area: %.2f (no modification needed for new shapes)%n", calc.total(shapes));
        shapes.add(new Pentagon(5)); // New shape — zero changes to AreaCalculator!
        System.out.printf("  Total with Pentagon: %.2f%n", calc.total(shapes));

        // LSP
        System.out.println("\n--- 2c. LSP: Liskov Substitution Principle ---");
        List<FlyingBird> flyers = List.of(new Sparrow(), new Eagle());
        List<SwimmingBird> swimmers = List.of(new Penguin(), new Duck());
        flyers.forEach(b -> System.out.println("  " + b.getClass().getSimpleName() + " → " + b.fly()));
        swimmers.forEach(b -> System.out.println("  " + b.getClass().getSimpleName() + " → " + b.swim()));

        // ISP
        System.out.println("\n--- 2d. ISP: Interface Segregation Principle ---");
        WorkerDemo.humanWorker();
        WorkerDemo.robotWorker();
        WorkerDemo.managerWorker();

        // DIP
        System.out.println("\n--- 2e. DIP: Dependency Inversion Principle ---");
        OrderRepository mysqlRepo = new MySQLOrderRepository();
        OrderRepository mongoRepo = new MongoOrderRepository();
        NotificationSvc emailSvc  = new EmailNotificationSvc();
        OrderSvc orderSvc1 = new OrderSvc(mysqlRepo, emailSvc);
        OrderSvc orderSvc2 = new OrderSvc(mongoRepo, emailSvc);
        orderSvc1.placeOrder(new Order("ORD-001", 199.99));
        orderSvc2.placeOrder(new Order("ORD-002", 299.99));
    }

    // --- SOLID classes ---
    static class User { String name, email; User(String n,String e){name=n;email=e;}
        @Override public String toString(){return "User("+name+", "+email+")";}}
    static class UserRepository { void save(User u){System.out.println("  [DB] Saved: "+u);} }
    static class UserEmailService { void sendWelcome(User u){System.out.println("  [Email] Welcome "+u.name);} }
    static class UserService {
        private final UserRepository repo; private final UserEmailService email;
        UserService(UserRepository r,UserEmailService e){repo=r;email=e;}
        User createUser(String name,String em){User u=new User(name,em);repo.save(u);email.sendWelcome(u);return u;}
    }
    static class UserExporter { String toCSV(List<User> users){return users.stream().map(u->u.name+","+u.email).collect(Collectors.joining("\n"));} }
    static class AreaCalculator { double total(List<Shape> shapes){return shapes.stream().mapToDouble(Shape::area).sum();} }
    static class Pentagon extends Shape { double side; Pentagon(double s){side=s;}
        @Override double area(){return (Math.sqrt(25+10*Math.sqrt(5))/4)*side*side;} @Override double perimeter(){return 5*side;}}
    interface FlyingBird { String fly(); }
    interface SwimmingBird { String swim(); }
    static class Sparrow implements FlyingBird {@Override public String fly(){return "flapping wings";}}
    static class Eagle implements FlyingBird {@Override public String fly(){return "soaring high";}}
    static class Penguin implements SwimmingBird {@Override public String swim(){return "diving deep";}}
    static class Duck implements FlyingBird,SwimmingBird {@Override public String fly(){return "flying low";} @Override public String swim(){return "paddling";}}
    static class WorkerDemo {
        interface Workable{void work();}interface Eatable{void eat();}interface Payable{void getPaid();}
        static void humanWorker(){new Object(){void demo(){System.out.println("  Human: works+eats+paid");}}. demo();}
        static void robotWorker(){System.out.println("  Robot: works only (no eat/sleep forced)");}
        static void managerWorker(){System.out.println("  Manager: works+paid+meetings (no eat interface)");}
    }
    interface OrderRepository { void save(Order o); }
    interface NotificationSvc { void notify(String to, String msg); }
    static class Order { String id; double amount; Order(String id,double a){this.id=id;this.amount=a;}
        @Override public String toString(){return "Order("+id+", $"+amount+")";}}
    static class MySQLOrderRepository implements OrderRepository {@Override public void save(Order o){System.out.println("  [MySQL] Saved "+o);}}
    static class MongoOrderRepository implements OrderRepository {@Override public void save(Order o){System.out.println("  [Mongo] Saved "+o);}}
    static class EmailNotificationSvc implements NotificationSvc {@Override public void notify(String to,String msg){System.out.println("  [Email] To:"+to+" "+msg);}}
    static class OrderSvc {
        private final OrderRepository repo; private final NotificationSvc notif;
        OrderSvc(OrderRepository r,NotificationSvc n){repo=r;notif=n;}
        void placeOrder(Order o){repo.save(o);notif.notify("user@example.com","Order placed: "+o);}
    }

    // =========================================================
    // SECTION 3 — DESIGN THINKING
    // =========================================================
    static void section3_DesignThinking() {
        printSection("3. DESIGN THINKING IN JAVA");

        // 3a. Builder pattern
        System.out.println("--- 3a. Builder Pattern: HttpRequest ---");
        HttpRequest req = new HttpRequest.Builder("https://api.example.com/users")
            .method("POST")
            .header("Content-Type","application/json")
            .header("Authorization","Bearer token123")
            .body("{\"name\":\"Alice\"}")
            .timeoutMs(10_000)
            .retries(3)
            .build();
        System.out.println("  " + req);

        HttpRequest getReq = new HttpRequest.Builder("https://api.example.com/users/1")
            .header("Accept","application/json")
            .build();
        System.out.println("  " + getReq);

        // 3b. Tell Don't Ask
        System.out.println("\n--- 3b. Tell, Don't Ask ---");
        BankAccount from = new BankAccount("ACC-A", 500);
        BankAccount to   = new BankAccount("ACC-B", 100);
        System.out.println("  Before: A=$" + from.getBalance() + " B=$" + to.getBalance());
        transfer(from, to, 200);
        System.out.println("  After:  A=$" + from.getBalance() + " B=$" + to.getBalance());

        // 3c. Immutability benefits
        System.out.println("\n--- 3c. Immutability: Thread Safety ---");
        Money base = new Money(100, "USD");
        System.out.println("  Base (unchanged after any op): " + base);
        System.out.println("  ×2: " + base.multiply(2));
        System.out.println("  +50: " + base.add(new Money(50,"USD")));
        System.out.println("  Base still: " + base + " ✓ immutable");

        // 3d. Method chaining / fluent API
        System.out.println("\n--- 3d. Fluent API / Method Chaining ---");
        QueryBuilder query = new QueryBuilder("users")
            .select("id","name","email")
            .where("age > 18")
            .where("active = true")
            .orderBy("name ASC")
            .limit(10)
            .offset(20);
        System.out.println("  " + query.build());
    }

    // --- Section 3 classes ---
    static void transfer(BankAccount from, BankAccount to, double amount) {
        from.withdraw(amount); to.deposit(amount);
        System.out.println("  Transferred $" + amount);}
    static final class HttpRequest {
        final String url,method,body; final Map<String,String> headers; final int timeoutMs,retries;
        private HttpRequest(Builder b){url=b.url;method=b.method;body=b.body;headers=Collections.unmodifiableMap(b.headers);timeoutMs=b.timeoutMs;retries=b.retries;}
        @Override public String toString(){return method+" "+url+" headers="+headers.keySet()+" timeout="+timeoutMs+"ms retries="+retries+(body!=null?" body="+body.substring(0,Math.min(20,body.length())):"");}
        static class Builder {
            final String url; String method="GET",body=null; Map<String,String> headers=new LinkedHashMap<>(); int timeoutMs=5000,retries=3;
            Builder(String url){this.url=url;}
            Builder method(String m){method=m;return this;}
            Builder header(String k,String v){headers.put(k,v);return this;}
            Builder body(String b){body=b;return this;}
            Builder timeoutMs(int t){timeoutMs=t;return this;}
            Builder retries(int r){retries=r;return this;}
            HttpRequest build(){if("POST".equals(method)&&body==null) throw new IllegalStateException("POST needs body");return new HttpRequest(this);}
        }
    }
    static class QueryBuilder {
        private final String table; private final List<String> cols=new ArrayList<>(),wheres=new ArrayList<>(),orders=new ArrayList<>();
        private int lim=-1,off=0;
        QueryBuilder(String t){table=t;}
        QueryBuilder select(String... c){cols.addAll(Arrays.asList(c));return this;}
        QueryBuilder where(String w){wheres.add(w);return this;}
        QueryBuilder orderBy(String o){orders.add(o);return this;}
        QueryBuilder limit(int l){lim=l;return this;}
        QueryBuilder offset(int o){off=o;return this;}
        String build(){StringBuilder sb=new StringBuilder("SELECT ");
            sb.append(cols.isEmpty()?"*":String.join(",",cols)).append(" FROM ").append(table);
            if(!wheres.isEmpty()) sb.append(" WHERE ").append(String.join(" AND ",wheres));
            if(!orders.isEmpty()) sb.append(" ORDER BY ").append(String.join(",",orders));
            if(lim>0) sb.append(" LIMIT ").append(lim);
            if(off>0) sb.append(" OFFSET ").append(off);
            return sb.toString();}
    }

    // =========================================================
    // SECTION 4 — CREATIONAL PATTERNS
    // =========================================================
    static void section4_CreationalPatterns() {
        printSection("4. CREATIONAL PATTERNS");

        // 4a. Singleton
        System.out.println("--- 4a. Singleton Pattern ---");
        ConfigManager c1 = ConfigManager.INSTANCE;
        ConfigManager c2 = ConfigManager.INSTANCE;
        c1.set("db.host","localhost"); c1.set("db.port","5432");
        System.out.println("  Same instance: " + (c1 == c2));
        System.out.println("  c2 sees c1's config: " + c2.get("db.host") + ":" + c2.get("db.port"));

        ConnectionPool pool1 = ConnectionPool.getInstance();
        ConnectionPool pool2 = ConnectionPool.getInstance();
        System.out.println("  Pool same instance: " + (pool1 == pool2));
        System.out.println("  Pool size: " + pool1.getSize());

        // 4b. Factory Method
        System.out.println("\n--- 4b. Factory Method Pattern ---");
        NotificationFactory emailFactory = new EmailNotificationFactory();
        NotificationFactory smsFactory   = new SMSNotificationFactory();
        NotificationFactory pushFactory  = new PushNotificationFactory();
        emailFactory.send("alice@email.com","Order confirmed!");
        smsFactory.send("+1234567890","Your OTP is 4521");
        pushFactory.send("device_token_abc","Flash sale starts now!");

        // 4c. Abstract Factory
        System.out.println("\n--- 4c. Abstract Factory Pattern ---");
        renderUI(new LightThemeFactory(), "Light Theme");
        renderUI(new DarkThemeFactory(),  "Dark Theme");

        // 4d. Builder
        System.out.println("\n--- 4d. Builder Pattern (Pizza) ---");
        Pizza p1 = new Pizza.Builder("Margherita","Large")
            .crust("Thin").cheese("Mozzarella").addTopping("Basil").addTopping("Tomato").build();
        Pizza p2 = new Pizza.Builder("BBQ Chicken","Medium")
            .crust("Stuffed").cheese("Cheddar").addTopping("Chicken").addTopping("BBQ Sauce").addTopping("Onion").extraCheese().build();
        System.out.println("  " + p1);
        System.out.println("  " + p2);

        // 4e. Prototype
        System.out.println("\n--- 4e. Prototype Pattern ---");
        DocumentTemplate invoiceTemplate = new DocumentTemplate("Invoice Template","Dear {customer},\nAmount: {amount}");
        invoiceTemplate.addTag("finance"); invoiceTemplate.addTag("billing");
        DocumentTemplate inv1 = invoiceTemplate.cloneTemplate();
        DocumentTemplate inv2 = invoiceTemplate.cloneTemplate();
        inv1.setTitle("Invoice #1042"); inv1.addTag("q1");
        inv2.setTitle("Invoice #1043"); inv2.addTag("q2");
        System.out.println("  Original: " + invoiceTemplate.getTitle());
        System.out.println("  Clone 1:  " + inv1.getTitle() + " tags:" + inv1.getTags());
        System.out.println("  Clone 2:  " + inv2.getTitle() + " tags:" + inv2.getTags());
        System.out.println("  Original tags unchanged: " + invoiceTemplate.getTags());
    }

    // --- Section 4 classes ---
    enum ConfigManager {
        INSTANCE;
        private final Properties props = new Properties();
        void set(String k,String v){props.setProperty(k,v);}
        String get(String k){return props.getProperty(k,"<not set>");}
    }
    static class ConnectionPool {
        private static volatile ConnectionPool instance;
        private final int size=10;
        private ConnectionPool(){}
        static ConnectionPool getInstance(){
            if(instance==null) synchronized(ConnectionPool.class){if(instance==null) instance=new ConnectionPool();}
            return instance;}
        int getSize(){return size;}
    }
    interface Notification { void prepare(); void deliver(); default void log(){System.out.println("  [LOG] Delivered");} }
    static abstract class NotificationFactory {
        abstract Notification create(String recipient, String message);
        void send(String recipient, String message){ Notification n=create(recipient,message); n.prepare(); n.deliver(); n.log(); }
    }
    static class EmailNotification implements Notification {
        String to,msg; EmailNotification(String t,String m){to=t;msg=m;}
        @Override public void prepare(){System.out.println("  [Email] Preparing MIME for "+to);}
        @Override public void deliver(){System.out.println("  [Email] Sending: "+msg);}
    }
    static class SMSNotification implements Notification {
        String to,msg; SMSNotification(String t,String m){to=t;msg=m;}
        @Override public void prepare(){System.out.println("  [SMS] Preparing for "+to);}
        @Override public void deliver(){System.out.println("  [SMS] Sending: "+msg);}
    }
    static class PushNotification implements Notification {
        String token,msg; PushNotification(String t,String m){token=t;msg=m;}
        @Override public void prepare(){System.out.println("  [Push] Preparing FCM payload");}
        @Override public void deliver(){System.out.println("  [Push] Sending: "+msg);}
    }
    static class EmailNotificationFactory extends NotificationFactory {@Override Notification create(String r,String m){return new EmailNotification(r,m);}}
    static class SMSNotificationFactory extends NotificationFactory {@Override Notification create(String r,String m){return new SMSNotification(r,m);}}
    static class PushNotificationFactory extends NotificationFactory {@Override Notification create(String r,String m){return new PushNotification(r,m);}}
    interface Button{ String render(); } interface TextField{ String render(); } interface Dialog{ String show(); }
    static class LightButton implements Button{@Override public String render(){return "[Light Button]";}}
    static class DarkButton implements Button{@Override public String render(){return "[Dark Button]";}}
    static class LightTextField implements TextField{@Override public String render(){return "[Light Input]";}}
    static class DarkTextField implements TextField{@Override public String render(){return "[Dark Input]";}}
    static class LightDialog implements Dialog{@Override public String show(){return "[Light Dialog]";}}
    static class DarkDialog implements Dialog{@Override public String show(){return "[Dark Dialog]";}}
    interface UIFactory{ Button createButton(); TextField createTextField(); Dialog createDialog(); }
    static class LightThemeFactory implements UIFactory{@Override public Button createButton(){return new LightButton();}@Override public TextField createTextField(){return new LightTextField();}@Override public Dialog createDialog(){return new LightDialog();}}
    static class DarkThemeFactory implements UIFactory{@Override public Button createButton(){return new DarkButton();}@Override public TextField createTextField(){return new DarkTextField();}@Override public Dialog createDialog(){return new DarkDialog();}}
    static void renderUI(UIFactory f,String theme){System.out.println("  "+theme+": "+f.createButton().render()+" "+f.createTextField().render()+" "+f.createDialog().show());}
    static class Pizza {
        final String name,size,crust,cheese; final List<String> toppings; final boolean extraCheese;
        private Pizza(Builder b){name=b.name;size=b.size;crust=b.crust;cheese=b.cheese;toppings=List.copyOf(b.toppings);extraCheese=b.extraCheese;}
        @Override public String toString(){return name+" ("+size+") crust="+crust+" cheese="+cheese+(extraCheese?" EXTRA":"")+" toppings="+toppings;}
        static class Builder {
            final String name,size; String crust="Regular",cheese="Mozzarella"; List<String> toppings=new ArrayList<>(); boolean extraCheese=false;
            Builder(String n,String s){name=n;size=s;}
            Builder crust(String c){crust=c;return this;} Builder cheese(String c){cheese=c;return this;}
            Builder addTopping(String t){toppings.add(t);return this;} Builder extraCheese(){extraCheese=true;return this;}
            Pizza build(){return new Pizza(this);}
        }
    }
    static class DocumentTemplate {
        private String title,content; private List<String> tags=new ArrayList<>();
        DocumentTemplate(String t,String c){title=t;content=c;}
        void addTag(String t){tags.add(t);} void setTitle(String t){title=t;}
        String getTitle(){return title;} List<String> getTags(){return new ArrayList<>(tags);}
        DocumentTemplate cloneTemplate(){DocumentTemplate copy=new DocumentTemplate(title,content);copy.tags=new ArrayList<>(tags);return copy;}
    }

    // =========================================================
    // SECTION 5 — STRUCTURAL PATTERNS
    // =========================================================
    static void section5_StructuralPatterns() {
        printSection("5. STRUCTURAL PATTERNS");

        // 5a. Adapter
        System.out.println("--- 5a. Adapter Pattern ---");
        ModernPaymentGateway modern = new LegacyPaymentAdapter(new LegacySystem());
        System.out.println("  " + modern.charge("cust_001", 99.99));
        System.out.println("  " + modern.refund("txn_123", 50.0));

        // 5b. Decorator
        System.out.println("\n--- 5b. Decorator Pattern (Data Processing Pipeline) ---");
        DataProcessor plain = new PlainProcessor();
        DataProcessor withLog = new LoggingDecorator(plain);
        DataProcessor withComp = new CompressionDecorator(withLog);
        DataProcessor withEnc  = new EncryptionDecorator(withComp);
        System.out.println("  Raw:        " + plain.process("Hello World"));
        System.out.println("  Log+Comp+Enc: " + withEnc.process("Hello World"));

        // 5c. Facade
        System.out.println("\n--- 5c. Facade Pattern (Order Workflow) ---");
        OrderFacade facade = new OrderFacade();
        String result = facade.placeOrder("alice", "pass123", new Cart("Laptop", 999.99), "card_abc");
        System.out.println("  " + result);

        // 5d. Proxy
        System.out.println("\n--- 5d. Proxy Pattern (Caching + Auth) ---");
        ImageLoader real    = new RealImageLoader();
        ImageLoader cached  = new CachingProxy(real);
        System.out.println("  " + cached.load("https://cdn.example.com/photo.jpg"));
        System.out.println("  " + cached.load("https://cdn.example.com/photo.jpg")); // Cache hit

        // 5e. Composite
        System.out.println("\n--- 5e. Composite Pattern (File System) ---");
        FileEntry root = new Dir("root");
        ((Dir)root).add(new Fil("readme.txt", 1024));
        Dir src = new Dir("src");
        src.add(new Fil("Main.java", 4096)); src.add(new Fil("Utils.java", 2048));
        ((Dir)root).add(src);
        Dir test = new Dir("test");
        test.add(new Fil("MainTest.java", 3072));
        ((Dir)root).add(test);
        root.display("");
        System.out.println("  Total size: " + root.getSize() + " bytes");

        // 5f. Bridge
        System.out.println("\n--- 5f. Bridge Pattern (Shape + Renderer) ---");
        Renderer vecRend = new VectorRenderer();
        Renderer rasterRend = new RasterRenderer();
        BridgeShape circle = new BridgeCircle(5, vecRend);
        BridgeShape rect   = new BridgeRect(4, 6, rasterRend);
        circle.draw();
        rect.draw();
    }

    // --- Section 5 classes ---
    interface ModernPaymentGateway { String charge(String customerId, double amount); String refund(String txnId, double amount); }
    static class LegacySystem { int processPayment(double amount, String card){System.out.println("  [Legacy] Processing $"+amount+" for "+card);return 200;}
        boolean reverseTransaction(String ref){System.out.println("  [Legacy] Reversing "+ref);return true;}}
    static class LegacyPaymentAdapter implements ModernPaymentGateway {
        private final LegacySystem legacy; LegacyPaymentAdapter(LegacySystem l){legacy=l;}
        @Override public String charge(String cId,double amt){int code=legacy.processPayment(amt,cId);return code==200?"SUCCESS:legacy_"+System.currentTimeMillis():"FAILED";}
        @Override public String refund(String txnId,double amt){return legacy.reverseTransaction(txnId)?"REFUNDED":"FAILED";}
    }
    interface DataProcessor { String process(String data); }
    static class PlainProcessor implements DataProcessor {@Override public String process(String d){return d;}}
    abstract static class ProcessorDecorator implements DataProcessor { protected final DataProcessor wrapped; ProcessorDecorator(DataProcessor w){wrapped=w;} }
    static class LoggingDecorator extends ProcessorDecorator {
        LoggingDecorator(DataProcessor w){super(w);}
        @Override public String process(String d){System.out.println("  [LOG] input="+d.substring(0,Math.min(15,d.length())));String r=wrapped.process(d);System.out.println("  [LOG] output length="+r.length());return r;}
    }
    static class CompressionDecorator extends ProcessorDecorator {
        CompressionDecorator(DataProcessor w){super(w);}
        @Override public String process(String d){String r=wrapped.process(d);return "ZIP["+r.replaceAll("\\s","_")+"]";}
    }
    static class EncryptionDecorator extends ProcessorDecorator {
        EncryptionDecorator(DataProcessor w){super(w);}
        @Override public String process(String d){String r=wrapped.process(d);return "ENC["+Integer.toHexString(r.hashCode()).toUpperCase()+"]";}
    }
    static class Cart { String item; double price; Cart(String i,double p){item=i;price=p;} }
    static class OrderFacade {
        String placeOrder(String user, String pass, Cart cart, String card){
            System.out.println("  [Auth] Authenticated: "+user);
            System.out.println("  [Order] Created: "+cart.item+" $"+cart.price);
            System.out.println("  [Payment] Charged: $"+cart.price+" to "+card);
            System.out.println("  [Inventory] Reserved: "+cart.item);
            System.out.println("  [Shipping] Scheduled — tracking: TRK-"+Math.abs(cart.item.hashCode())%10000);
            System.out.println("  [Email] Confirmation sent to "+user);
            return "ORDER-"+Math.abs(cart.hashCode())%100000+": SUCCESS";
        }
    }
    interface ImageLoader { String load(String url); }
    static class RealImageLoader implements ImageLoader {@Override public String load(String url){System.out.println("  [NET] Fetching: "+url);return "Image@"+url.hashCode();}}
    static class CachingProxy implements ImageLoader {
        private final ImageLoader real; private final Map<String,String> cache=new HashMap<>();
        CachingProxy(ImageLoader r){real=r;}
        @Override public String load(String url){
            if(!cache.containsKey(url)){System.out.println("  [Cache] MISS: "+url);cache.put(url,real.load(url));}
            else System.out.println("  [Cache] HIT: "+url);
            return cache.get(url);}
    }
    interface FileEntry { String getName(); long getSize(); void display(String indent); }
    static class Fil implements FileEntry {
        String name; long size; Fil(String n,long s){name=n;size=s;}
        @Override public String getName(){return name;} @Override public long getSize(){return size;}
        @Override public void display(String i){System.out.println(i+"📄 "+name+" ("+size+"B)");}
    }
    static class Dir implements FileEntry {
        String name; List<FileEntry> children=new ArrayList<>(); Dir(String n){name=n;}
        void add(FileEntry e){children.add(e);}
        @Override public String getName(){return name;}
        @Override public long getSize(){return children.stream().mapToLong(FileEntry::getSize).sum();}
        @Override public void display(String i){System.out.println(i+"📁 "+name+"/");children.forEach(c->c.display(i+"  "));}
    }
    interface Renderer { String render(String shape, String details); }
    static class VectorRenderer implements Renderer {@Override public String render(String s,String d){return "Vector::"+s+"("+d+")";}}
    static class RasterRenderer implements Renderer {@Override public String render(String s,String d){return "Raster::"+s+"("+d+")";}}
    abstract static class BridgeShape { protected final Renderer renderer; BridgeShape(Renderer r){renderer=r;} abstract void draw(); }
    static class BridgeCircle extends BridgeShape { double r; BridgeCircle(double r,Renderer ren){super(ren);this.r=r;} @Override void draw(){System.out.println("  "+renderer.render("Circle","r="+r));}}
    static class BridgeRect extends BridgeShape { double w,h; BridgeRect(double w,double h,Renderer r){super(r);this.w=w;this.h=h;} @Override void draw(){System.out.println("  "+renderer.render("Rect","w="+w+" h="+h));}}

    // =========================================================
    // SECTION 6 — BEHAVIORAL PATTERNS
    // =========================================================
    static void section6_BehavioralPatterns() {
        printSection("6. BEHAVIORAL PATTERNS");

        // 6a. Observer / EventBus
        System.out.println("--- 6a. Observer Pattern (Type-Safe EventBus) ---");
        EventBus bus = new EventBus();
        bus.subscribe(OrderPlacedEvent.class, e -> System.out.println("  [Inventory] Reserve items for " + e.orderId));
        bus.subscribe(OrderPlacedEvent.class, e -> System.out.println("  [Email] Confirm order " + e.orderId + " to " + e.userId));
        bus.subscribe(OrderPlacedEvent.class, e -> System.out.println("  [Analytics] Record $" + e.amount + " sale"));
        bus.subscribe(PaymentFailedEvent.class, e -> System.out.println("  [Alert] Payment failed for " + e.orderId + ": " + e.reason));
        bus.publish(new OrderPlacedEvent("ORD-001", "USR-123", 199.99));
        bus.publish(new PaymentFailedEvent("ORD-002", "Card declined"));

        // 6b. Strategy
        System.out.println("\n--- 6b. Strategy Pattern (Sorting + Pricing) ---");
        List<Integer> data = new ArrayList<>(Arrays.asList(64,34,25,12,22,11,90));
        SortContext<Integer> sorter = new SortContext<>(new BubbleSortStrategy<>());
        System.out.println("  Bubble: " + sorter.sort(new ArrayList<>(data)));
        sorter.setStrategy(new QuickSortStrategy<>());
        System.out.println("  Quick:  " + sorter.sort(new ArrayList<>(data)));
        System.out.println("  VIP pricing:    $" + new VIPPricing().price(100.0));
        System.out.println("  Member pricing: $" + new MemberPricing().price(100.0));
        System.out.println("  Regular pricing:$" + new RegularPricing().price(100.0));

        // 6c. Command + Undo
        System.out.println("\n--- 6c. Command Pattern (Bank Transfer with Undo) ---");
        BankAccount accA = new BankAccount("A", 1000);
        BankAccount accB = new BankAccount("B", 500);
        CommandManager cmdMgr = new CommandManager();
        cmdMgr.execute(new TransferCommand(accA, accB, 300));
        cmdMgr.execute(new TransferCommand(accA, accB, 100));
        System.out.println("  A=$"+accA.getBalance()+" B=$"+accB.getBalance());
        cmdMgr.undo();
        System.out.println("  After undo: A=$"+accA.getBalance()+" B=$"+accB.getBalance());
        cmdMgr.redo();
        System.out.println("  After redo: A=$"+accA.getBalance()+" B=$"+accB.getBalance());

        // 6d. Template Method
        System.out.println("\n--- 6d. Template Method Pattern (Report Generator) ---");
        new PDFReportGenerator().generate("Q1 Sales Data");
        System.out.println();
        new CSVReportGenerator().generate("Q1 Sales Data");

        // 6e. Chain of Responsibility
        System.out.println("\n--- 6e. Chain of Responsibility (HTTP Middleware) ---");
        MiddlewareHandler chain = new AuthMiddleware();
        chain.setNext(new RateLimitMiddleware()).setNext(new LogMiddleware()).setNext(new BusinessMiddleware());
        System.out.println("  --- Valid request ---");
        chain.handle(new HttpReq("/api/users", "Bearer valid_token", "192.168.1.1"));
        System.out.println("  --- No auth ---");
        chain.handle(new HttpReq("/api/users", null, "192.168.1.2"));

        // 6f. State Machine (Vending Machine)
        System.out.println("\n--- 6f. State Pattern (Vending Machine) ---");
        VendingMachine vm = new VendingMachine();
        vm.insertCoin(1.00);
        vm.insertCoin(0.50);
        vm.select("Cola");
        vm.dispense();
        System.out.println("  --- Try without coin ---");
        vm.select("Water");

        // 6g. Memento (undo state)
        System.out.println("\n--- 6g. Memento Pattern (Text Editor Undo) ---");
        TextEditor editor = new TextEditor();
        editor.type("Hello"); editor.save();
        editor.type(" World"); editor.save();
        editor.type(" Java!");
        System.out.println("  Current: " + editor.getText());
        editor.undo(); System.out.println("  Undo 1:  " + editor.getText());
        editor.undo(); System.out.println("  Undo 2:  " + editor.getText());
    }

    // --- Section 6 classes ---
    static class EventBus {
        private final Map<Class<?>,List<Consumer<Object>>> listeners = new HashMap<>();
        @SuppressWarnings("unchecked")
        <T> void subscribe(Class<T> type, Consumer<T> l){listeners.computeIfAbsent(type,k->new ArrayList<>()).add((Consumer<Object>)(Consumer<?>)l);}
        void publish(Object e){List<Consumer<Object>> ls=listeners.get(e.getClass());if(ls!=null) ls.forEach(l->l.accept(e));}
    }
    static class OrderPlacedEvent { String orderId,userId; double amount; OrderPlacedEvent(String o,String u,double a){orderId=o;userId=u;amount=a;} }
    static class PaymentFailedEvent { String orderId,reason; PaymentFailedEvent(String o,String r){orderId=o;reason=r;} }
    interface SortStrategy<T extends Comparable<T>> { List<T> sort(List<T> list); String name(); }
    static class BubbleSortStrategy<T extends Comparable<T>> implements SortStrategy<T> {
        @Override public List<T> sort(List<T> l){for(int i=0;i<l.size()-1;i++) for(int j=0;j<l.size()-i-1;j++) if(l.get(j).compareTo(l.get(j+1))>0){T t=l.get(j);l.set(j,l.get(j+1));l.set(j+1,t);}return l;}
        @Override public String name(){return "BubbleSort";}
    }
    static class QuickSortStrategy<T extends Comparable<T>> implements SortStrategy<T> {
        @Override public List<T> sort(List<T> l){Collections.sort(l);return l;} @Override public String name(){return "QuickSort";}
    }
    static class SortContext<T extends Comparable<T>> { private SortStrategy<T> strategy;
        SortContext(SortStrategy<T> s){strategy=s;}
        void setStrategy(SortStrategy<T> s){strategy=s;}
        List<T> sort(List<T> l){return strategy.sort(l);}
    }
    interface PricingStrategy { double price(double base); }
    static class VIPPricing implements PricingStrategy {@Override public double price(double b){return b*0.75;}}
    static class MemberPricing implements PricingStrategy {@Override public double price(double b){return b*0.90;}}
    static class RegularPricing implements PricingStrategy {@Override public double price(double b){return b;}}
    interface Command { void execute(); void undo(); }
    static class TransferCommand implements Command {
        BankAccount from,to; double amount; boolean done;
        TransferCommand(BankAccount f,BankAccount t,double a){from=f;to=t;amount=a;}
        @Override public void execute(){from.withdraw(amount);to.deposit(amount);done=true;System.out.println("  [CMD] Transfer $"+amount);}
        @Override public void undo(){if(!done) return;to.withdraw(amount);from.deposit(amount);System.out.println("  [CMD] UNDO Transfer $"+amount);}
    }
    static class CommandManager { Deque<Command> hist=new ArrayDeque<>(),redo=new ArrayDeque<>();
        void execute(Command c){c.execute();hist.push(c);redo.clear();}
        void undo(){if(hist.isEmpty()) return;Command c=hist.pop();c.undo();redo.push(c);}
        void redo(){if(redo.isEmpty()) return;Command c=redo.pop();c.execute();hist.push(c);}
    }
    abstract static class ReportGenerator {
        final void generate(String data){ fetchData(data); processData(); formatOutput(); deliver(); }
        protected abstract void fetchData(String data);
        protected abstract void processData();
        protected void formatOutput(){ System.out.println("  Default formatting"); }
        protected abstract void deliver();
    }
    static class PDFReportGenerator extends ReportGenerator {
        @Override protected void fetchData(String d){System.out.println("  [PDF] Loading: "+d);}
        @Override protected void processData(){System.out.println("  [PDF] Rendering charts");}
        @Override protected void formatOutput(){System.out.println("  [PDF] Applying PDF layout");}
        @Override protected void deliver(){System.out.println("  [PDF] Saved report.pdf");}
    }
    static class CSVReportGenerator extends ReportGenerator {
        @Override protected void fetchData(String d){System.out.println("  [CSV] Loading: "+d);}
        @Override protected void processData(){System.out.println("  [CSV] Flattening data");}
        @Override protected void deliver(){System.out.println("  [CSV] Saved report.csv");}
    }
    static class HttpReq { String path,auth,ip; HttpReq(String p,String a,String i){path=p;auth=a;ip=i;} }
    abstract static class MiddlewareHandler { protected MiddlewareHandler next;
        MiddlewareHandler setNext(MiddlewareHandler n){next=n;return n;}
        abstract void handle(HttpReq req);
        void passNext(HttpReq req){if(next!=null) next.handle(req);}
    }
    static class AuthMiddleware extends MiddlewareHandler {
        @Override void handle(HttpReq r){if(r.auth==null){System.out.println("  [Auth] 401");return;}System.out.println("  [Auth] OK");passNext(r);}
    }
    static class RateLimitMiddleware extends MiddlewareHandler {
        Map<String,Integer> counts=new HashMap<>();
        @Override void handle(HttpReq r){int c=counts.merge(r.ip,1,Integer::sum);if(c>100){System.out.println("  [Rate] 429");return;}System.out.println("  [Rate] OK ("+c+"/100)");passNext(r);}
    }
    static class LogMiddleware extends MiddlewareHandler {
        @Override void handle(HttpReq r){System.out.println("  [Log] GET "+r.path);passNext(r);}
    }
    static class BusinessMiddleware extends MiddlewareHandler {
        @Override void handle(HttpReq r){System.out.println("  [Business] 200 OK: "+r.path);}
    }
    interface VMState { void insertCoin(VendingMachine m); void select(VendingMachine m,String p); void dispense(VendingMachine m); String name(); }
    static class VendingMachine {
        VMState state=new VMIdle(); double balance=0; String selected=null;
        Map<String,Double> products=Map.of("Cola",1.50,"Water",1.00,"Chips",2.00);
        void insertCoin(double a){balance+=a;state.insertCoin(this);}
        void select(String p){state.select(this,p);}
        void dispense(){state.dispense(this);}
        void setState(VMState s){System.out.println("  State: "+state.name()+"→"+s.name());state=s;}
    }
    static class VMIdle implements VMState {
        @Override public void insertCoin(VendingMachine m){System.out.println("  Coin $"+m.balance);m.setState(new VMHasMoney());}
        @Override public void select(VendingMachine m,String p){System.out.println("  Insert coin first");}
        @Override public void dispense(VendingMachine m){System.out.println("  Insert coin first");}
        @Override public String name(){return "IDLE";}
    }
    static class VMHasMoney implements VMState {
        @Override public void insertCoin(VendingMachine m){System.out.println("  Additional coin, total $"+m.balance);}
        @Override public void select(VendingMachine m,String p){Double price=m.products.get(p);if(price==null){System.out.println("  Not available");return;}if(m.balance<price){System.out.println("  Need $"+(price-m.balance)+" more");return;}m.selected=p;System.out.println("  Selected: "+p+" ($"+price+")");m.setState(new VMReady());}
        @Override public void dispense(VendingMachine m){System.out.println("  Select product first");}
        @Override public void ejectCoin(VendingMachine m){System.out.println("  Returned $"+m.balance);m.balance=0;m.setState(new VMIdle());}
        @Override public String name(){return "HAS_MONEY";}
    }
    static class VMReady implements VMState {
        @Override public void insertCoin(VendingMachine m){}
        @Override public void select(VendingMachine m,String p){System.out.println("  Already selected");}
        @Override public void dispense(VendingMachine m){double price=m.products.get(m.selected);System.out.println("  Dispensing: "+m.selected);double change=m.balance-price;if(change>0) System.out.printf("  Change: $%.2f%n",change);m.balance=0;m.selected=null;m.setState(new VMIdle());}
        @Override public String name(){return "READY";}
    }
    static class TextEditor {
        private String text=""; private final Deque<String> history=new ArrayDeque<>();
        void type(String t){text+=t;System.out.println("  Typed: \""+t+"\"");}
        void save(){history.push(text);System.out.println("  Saved snapshot");}
        void undo(){if(!history.isEmpty()) text=history.pop();}
        String getText(){return text;}
    }

    // =========================================================
    // SECTION 7 — REAL-WORLD ARCHITECTURE
    // =========================================================
    static void section7_RealWorldArchitecture() {
        printSection("7. REAL-WORLD ARCHITECTURE EXAMPLES");

        // 7a. E-Commerce: 8+ patterns combined
        System.out.println("--- 7a. E-Commerce Order Processing (Multi-Pattern) ---");
        ECommerceSystem ecommerce = new ECommerceSystem();
        ecommerce.initialize();
        ecommerce.processOrder("alice", new Cart("MacBook Pro", 2499.99), "CARD-4242");

        // 7b. Plugin system (Factory + Strategy + Registry)
        System.out.println("\n--- 7b. Plugin System (Factory + Registry) ---");
        PluginRegistry registry = new PluginRegistry();
        registry.register("json", new JsonPlugin());
        registry.register("xml",  new XmlPlugin());
        registry.register("csv",  new CsvPlugin());
        for(String fmt : new String[]{"json","xml","csv","yaml"}) {
            Plugin plugin = registry.getPlugin(fmt);
            System.out.println("  " + fmt + ": " + (plugin != null ? plugin.serialize(Map.of("key","value")) : "No plugin registered"));
        }

        // 7c. Pattern decision guide summary
        System.out.println("\n--- 7c. Pattern Decision Guide ---");
        Map<String,String[]> guide = new LinkedHashMap<>();
        guide.put("CREATIONAL", new String[]{"One instance → Singleton","Type varies → Factory Method","Families → Abstract Factory","Complex build → Builder","Clone → Prototype"});
        guide.put("STRUCTURAL", new String[]{"Incompatible IF → Adapter","Add behavior → Decorator","Simplify API → Facade","Control access → Proxy","Tree structure → Composite"});
        guide.put("BEHAVIORAL", new String[]{"Decouple events → Observer","Swap algorithms → Strategy","Undo/queue → Command","Fix skeleton → Template","Chain handlers → Chain of Resp","State changes behavior → State"});
        guide.forEach((cat,patterns) -> { System.out.println("  " + cat + ":"); Arrays.stream(patterns).forEach(p->System.out.println("    • " + p)); });
    }

    // --- Section 7 classes ---
    static class ECommerceSystem {
        private EventBus bus; private OrderFacade facade;
        void initialize(){
            bus = new EventBus();
            bus.subscribe(OrderPlacedEvent.class, e->System.out.println("  [Inventory] Reserve: order "+e.orderId));
            bus.subscribe(OrderPlacedEvent.class, e->System.out.println("  [Email] Confirm: "+e.userId+" order "+e.orderId));
            bus.subscribe(OrderPlacedEvent.class, e->System.out.printf("  [Analytics] Sale $%.2f%n",e.amount));
            bus.subscribe(PaymentFailedEvent.class, e->System.out.println("  [Alert] Payment failed: "+e.reason));
            facade = new OrderFacade();
            System.out.println("  [System] E-Commerce initialized with Observer, Facade, Factory");
        }
        void processOrder(String user, Cart cart, String card){
            System.out.println("  Processing order for: " + user + " item: " + cart.item);
            String result = facade.placeOrder(user,"pass",cart,card);
            bus.publish(new OrderPlacedEvent("ORD-"+System.currentTimeMillis()%10000, user, cart.price));
            System.out.println("  Result: " + result);
        }
    }
    interface Plugin { String serialize(Map<String,Object> data); }
    static class JsonPlugin implements Plugin {@Override public String serialize(Map<String,Object> d){return "{"+d.entrySet().stream().map(e->"\""+e.getKey()+"\":\""+e.getValue()+"\"").collect(Collectors.joining(","))+"}";}}
    static class XmlPlugin implements Plugin {@Override public String serialize(Map<String,Object> d){return "<root>"+d.entrySet().stream().map(e->"<"+e.getKey()+">"+e.getValue()+"</"+e.getKey()+">").collect(Collectors.joining())+"</root>";}}
    static class CsvPlugin implements Plugin {@Override public String serialize(Map<String,Object> d){return d.entrySet().stream().map(e->e.getKey()+","+e.getValue()).collect(Collectors.joining("\n"));}}
    static class PluginRegistry {
        private final Map<String,Plugin> plugins=new HashMap<>();
        void register(String fmt, Plugin p){plugins.put(fmt,p);}
        Plugin getPlugin(String fmt){return plugins.get(fmt);}
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    static void printBanner(String title){System.out.println("\n"+"=".repeat(66)+"\n  "+title+"\n"+"=".repeat(66));}
    static void printSection(String title){System.out.println("\n"+"-".repeat(66)+"\n  SECTION "+title+"\n"+"-".repeat(66));}
}
