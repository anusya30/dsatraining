import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * ============================================================
 * SYSTEM DESIGN BASICS & MICROSERVICES — Complete Java Demo
 * ============================================================
 * Topics:
 *  1. System Design Fundamentals   (estimation, capacity planning,
 *                                   latency hierarchy simulation)
 *  2. Scalability & Performance    (round-robin + weighted LB,
 *                                   LRU cache, token bucket rate limiter,
 *                                   sliding window rate limiter,
 *                                   connection pool simulation)
 *  3. Monolith vs Microservices    (monolith simulation, service registry,
 *                                   service discovery, health checks)
 *  4. Microservices Architecture   (API gateway simulation, circuit breaker
 *                                   state machine, retry with backoff,
 *                                   saga orchestration pattern)
 *  5. Communication Patterns       (sync REST simulation, async event bus,
 *                                   Kafka-like message queue, outbox pattern,
 *                                   dead letter queue, pub-sub)
 *  6. Data Management              (consistent hashing ring, read/write split,
 *                                   CQRS pattern, cache-aside pattern,
 *                                   distributed lock simulation)
 *  7. Java/Spring Patterns         (dependency injection container,
 *                                   repository pattern, event sourcing,
 *                                   interceptor chain, health check)
 *  8. Real-World Case Study        (food delivery platform simulation:
 *                                   order flow, payment saga, location
 *                                   tracking, search service, notifications)
 *
 * Compile : javac SystemDesign.java
 * Run     : java SystemDesign
 * ============================================================
 */
public class SystemDesign {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) throws Exception {
        printBanner("SYSTEM DESIGN BASICS & MICROSERVICES — COMPLETE DEMO");

        section1_SystemDesignFundamentals();
        section2_ScalabilityPerformance();
        section3_MonolithVsMicroservices();
        section4_MicroservicesArchitecture();
        section5_CommunicationPatterns();
        section6_DataManagement();
        section7_JavaEcosystemPatterns();
        section8_RealWorldCaseStudy();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — SYSTEM DESIGN FUNDAMENTALS
    // =========================================================
    static void section1_SystemDesignFundamentals() {
        printSection("1. SYSTEM DESIGN FUNDAMENTALS");

        // 1a. Capacity estimation
        System.out.println("--- 1a. Capacity Estimation (Twitter-Scale) ---");
        CapacityEstimator twitter = new CapacityEstimator("Twitter")
            .dailyActiveUsers(300_000_000L)
            .writeRatioPercent(1)           // 1% of users write
            .avgWriteSizeBytes(280)         // tweet size
            .avgReadMultiplier(100)         // 100 reads per write
            .dataRetentionYears(5);
        twitter.estimate();

        System.out.println();
        CapacityEstimator foodApp = new CapacityEstimator("Food Delivery App")
            .dailyActiveUsers(10_000_000L)
            .writeRatioPercent(10)
            .avgWriteSizeBytes(1024)
            .avgReadMultiplier(20)
            .dataRetentionYears(3);
        foodApp.estimate();

        // 1b. Latency hierarchy
        System.out.println("\n--- 1b. Latency Hierarchy (Simulated) ---");
        LatencySimulator sim = new LatencySimulator();
        sim.benchmark("L1 Cache",        () -> sim.l1CacheRead());
        sim.benchmark("L2 Cache",        () -> sim.l2CacheRead());
        sim.benchmark("RAM Read",         () -> sim.ramRead());
        sim.benchmark("Redis Network",    () -> sim.redisRead());
        sim.benchmark("SSD Read",         () -> sim.ssdRead());
        sim.benchmark("DB Query (simple)",() -> sim.dbQuery());
        sim.benchmark("HDD Read",         () -> sim.hddRead());
        sim.benchmark("Cross-DC call",    () -> sim.crossDatacenterCall());

        // 1c. CAP Theorem illustration
        System.out.println("\n--- 1c. CAP Theorem in Practice ---");
        System.out.println("  Scenario: Network partition between DC-East and DC-West");
        System.out.println();
        CAPDemo.demonstrateCP("HBase/ZooKeeper");
        CAPDemo.demonstrateAP("Cassandra/DynamoDB");
        CAPDemo.demonstrateCA("Single-node PostgreSQL");

        // 1d. SLA calculations
        System.out.println("\n--- 1d. SLA / Availability Calculations ---");
        double[] slas = {99.0, 99.9, 99.99, 99.999};
        System.out.printf("  %-12s %-18s %-18s %-15s%n", "SLA", "Downtime/Year", "Downtime/Month", "Downtime/Day");
        for (double sla : slas) {
            double yearSecs  = 365 * 24 * 3600;
            double downSec   = yearSecs * (1.0 - sla/100);
            System.out.printf("  %-12s %-18s %-18s %-15s%n",
                sla + "%",
                formatDuration((long)downSec),
                formatDuration((long)(downSec/12)),
                formatDuration((long)(downSec/365)));
        }
    }

    static class CapacityEstimator {
        String name; long dau; int writeRatio; int writeSizeBytes; int readMult; int retentionYears;
        CapacityEstimator(String name){this.name=name;}
        CapacityEstimator dailyActiveUsers(long n){dau=n;return this;}
        CapacityEstimator writeRatioPercent(int p){writeRatio=p;return this;}
        CapacityEstimator avgWriteSizeBytes(int b){writeSizeBytes=b;return this;}
        CapacityEstimator avgReadMultiplier(int m){readMult=m;return this;}
        CapacityEstimator dataRetentionYears(int y){retentionYears=y;return this;}
        void estimate(){
            long writesPerDay  = dau * writeRatio / 100;
            long readsPerDay   = writesPerDay * readMult;
            long writesPerSec  = writesPerDay / 86400;
            long readsPerSec   = readsPerDay  / 86400;
            long peakWrites    = writesPerSec * 3;
            long storagePerDay = writesPerDay * writeSizeBytes;
            long totalStorage  = storagePerDay * 365L * retentionYears;
            long bandwidthInBps= writesPerSec * writeSizeBytes;
            System.out.println("  === " + name + " ===");
            System.out.printf("  DAU:              %,d%n", dau);
            System.out.printf("  Writes/day:       %,d   Writes/sec: %,d (peak ~%,d)%n",writesPerDay,writesPerSec,peakWrites);
            System.out.printf("  Reads/day:        %,d   Reads/sec: %,d%n",readsPerDay,readsPerSec);
            System.out.printf("  Storage/day:      %s%n", formatBytes(storagePerDay));
            System.out.printf("  Total (%dy):      %s%n", retentionYears, formatBytes(totalStorage));
            System.out.printf("  Inbound BW:       %s/sec%n", formatBytes(bandwidthInBps));
        }
    }

    static class LatencySimulator {
        void benchmark(String op, Runnable r){long t=System.nanoTime();for(int i=0;i<1000;i++) r.run();long ns=(System.nanoTime()-t)/1000;System.out.printf("  %-25s ~%,d ns%n",op,ns);}
        void l1CacheRead()        { int x=1+1; }
        void l2CacheRead()        { int x=0; for(int i=0;i<4;i++) x+=i; }
        void ramRead()            { int[] a=new int[100]; for(int i=0;i<100;i++) a[i]=i; }
        void redisRead()          { try{Thread.sleep(0,100000);}catch(Exception e){} }
        void ssdRead()            { try{Thread.sleep(0,500000);}catch(Exception e){} }
        void dbQuery()            { try{Thread.sleep(1);}catch(Exception e){} }
        void hddRead()            { try{Thread.sleep(10);}catch(Exception e){} }
        void crossDatacenterCall(){ try{Thread.sleep(150);}catch(Exception e){} }
    }

    static class CAPDemo {
        static void demonstrateCP(String system){
            System.out.println("  CP — " + system);
            System.out.println("    Network partition detected!");
            System.out.println("    Decision: Reject writes to preserve consistency");
            System.out.println("    Result: System UNAVAILABLE but data is CONSISTENT ✓");
        }
        static void demonstrateAP(String system){
            System.out.println("  AP — " + system);
            System.out.println("    Network partition detected!");
            System.out.println("    Decision: Accept writes on both sides, reconcile later");
            System.out.println("    Result: System AVAILABLE but data may be INCONSISTENT ✓");
        }
        static void demonstrateCA(String system){
            System.out.println("  CA — " + system);
            System.out.println("    Single node — no partitions possible");
            System.out.println("    Result: CONSISTENT + AVAILABLE but NOT fault-tolerant ✓");
        }
    }

    // =========================================================
    // SECTION 2 — SCALABILITY & PERFORMANCE
    // =========================================================
    static void section2_ScalabilityPerformance() throws Exception {
        printSection("2. SCALABILITY & PERFORMANCE");

        // 2a. Load balancers
        System.out.println("--- 2a. Load Balancer Algorithms ---");
        List<ServerNode> servers = List.of(
            new ServerNode("server-1:8080", 3),
            new ServerNode("server-2:8080", 2),
            new ServerNode("server-3:8080", 1));

        RoundRobinLB rrLB = new RoundRobinLB(servers);
        WeightedRRLB wrrLB = new WeightedRRLB(servers);
        LeastConnLB lcLB = new LeastConnLB(servers);

        System.out.println("  Round-Robin (10 requests):");
        Map<String,Integer> rrCounts = new HashMap<>();
        for(int i=0;i<10;i++) rrCounts.merge(rrLB.route().host,1,Integer::sum);
        rrCounts.forEach((h,c)->System.out.println("    "+h+" → "+c+" requests"));

        System.out.println("  Weighted Round-Robin (12 requests, weights 3:2:1):");
        Map<String,Integer> wrrCounts = new HashMap<>();
        for(int i=0;i<12;i++) wrrCounts.merge(wrrLB.route().host,1,Integer::sum);
        wrrCounts.forEach((h,c)->System.out.println("    "+h+" → "+c+" requests"));

        // 2b. LRU Cache
        System.out.println("\n--- 2b. LRU Cache Simulation ---");
        LRUCache<String,String> cache = new LRUCache<>(3);
        cache.put("user:1","Alice"); cache.put("user:2","Bob"); cache.put("user:3","Carol");
        System.out.println("  Cache (size=3): " + cache.keys());
        cache.get("user:1"); // Access user:1 → it becomes most recent
        cache.put("user:4","Dave"); // Should evict user:2 (least recently used)
        System.out.println("  After access user:1, add user:4: " + cache.keys());
        System.out.println("  user:2 evicted: " + (cache.get("user:2") == null ? "✓" : "✗"));
        System.out.println("  user:1 still exists: " + (cache.get("user:1") != null ? "✓" : "✗"));

        // 2c. Token bucket rate limiter
        System.out.println("\n--- 2c. Token Bucket Rate Limiter ---");
        TokenBucketRL rl = new TokenBucketRL(5, 2.0); // 5 capacity, 2 tokens/sec
        System.out.println("  Burst of 7 requests at t=0:");
        for(int i=1;i<=7;i++) {
            boolean allowed = rl.tryAcquire();
            System.out.printf("    Request %d: %s%n", i, allowed?"ALLOWED ✓":"BLOCKED ✗");}
        Thread.sleep(2000); // Wait 2 seconds → 4 tokens refilled
        System.out.println("  After 2 seconds (4 tokens refilled), 4 more requests:");
        for(int i=1;i<=4;i++) System.out.printf("    Request %d: %s%n",i,rl.tryAcquire()?"ALLOWED ✓":"BLOCKED ✗");

        // 2d. Sliding window rate limiter
        System.out.println("\n--- 2d. Sliding Window Rate Limiter ---");
        SlidingWindowRL swrl = new SlidingWindowRL(5, 1000); // 5 per 1 second
        System.out.println("  5 requests per second:");
        for(int i=1;i<=8;i++) {
            boolean ok=swrl.tryAcquire();
            System.out.printf("    t+%dms req%d: %s%n",i*100,i,ok?"ALLOWED ✓":"BLOCKED ✗");
            if(i==5) Thread.sleep(500);}

        // 2e. Connection pool
        System.out.println("\n--- 2e. Connection Pool Simulation ---");
        ConnectionPool pool = new ConnectionPool("postgres:5432", 3);
        ExecutorService executor = Executors.newFixedThreadPool(6);
        CountDownLatch latch = new CountDownLatch(6);
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        for(int i=0;i<6;i++){final int id=i+1;executor.submit(()->{
            try{DBConnection conn=pool.acquire(500);
                if(conn!=null){results.add("Thread-"+id+": acquired "+conn.id);Thread.sleep(100);pool.release(conn);}
                else results.add("Thread-"+id+": TIMEOUT - no connections available");}
            catch(Exception e){}finally{latch.countDown();}});}
        latch.await(5,TimeUnit.SECONDS);
        executor.shutdown();
        results.forEach(r->System.out.println("  "+r));
        System.out.println("  Pool size: "+pool.size()+" connections");
    }

    static class ServerNode {
        String host; int weight; AtomicInteger activeConns=new AtomicInteger(0);
        ServerNode(String h,int w){host=h;weight=w;}
    }
    static class RoundRobinLB {
        List<ServerNode> servers; AtomicInteger counter=new AtomicInteger();
        RoundRobinLB(List<ServerNode> s){servers=s;}
        ServerNode route(){return servers.get(counter.getAndIncrement()%servers.size());}
    }
    static class WeightedRRLB {
        List<ServerNode> pool=new ArrayList<>();
        WeightedRRLB(List<ServerNode> s){s.forEach(sv->{for(int i=0;i<sv.weight;i++) pool.add(sv);}}
        AtomicInteger counter=new AtomicInteger();
        ServerNode route(){return pool.get(counter.getAndIncrement()%pool.size());}
    }
    static class LeastConnLB {
        List<ServerNode> servers; LeastConnLB(List<ServerNode> s){servers=s;}
        ServerNode route(){return servers.stream().min(Comparator.comparingInt(s->s.activeConns.get())).orElseThrow();}
    }
    static class LRUCache<K,V> {
        private final int cap;
        private final LinkedHashMap<K,V> map;
        LRUCache(int cap){this.cap=cap;map=new LinkedHashMap<>(cap,0.75f,true){
            @Override protected boolean removeEldestEntry(Map.Entry<K,V> e){return size()>cap;}};
        }
        synchronized V get(K k){return map.getOrDefault(k,null);}
        synchronized void put(K k,V v){map.put(k,v);}
        synchronized List<K> keys(){return new ArrayList<>(map.keySet());}
    }
    static class TokenBucketRL {
        double cap,rate,tokens; long last;
        TokenBucketRL(double c,double r){cap=c;rate=r;tokens=c;last=System.currentTimeMillis();}
        synchronized boolean tryAcquire(){
            long now=System.currentTimeMillis(); double elapsed=(now-last)/1000.0;
            tokens=Math.min(cap,tokens+elapsed*rate); last=now;
            if(tokens>=1){tokens--;return true;}return false;}
    }
    static class SlidingWindowRL {
        int limit; long windowMs; Deque<Long> timestamps=new ArrayDeque<>();
        SlidingWindowRL(int l,long w){limit=l;windowMs=w;}
        synchronized boolean tryAcquire(){
            long now=System.currentTimeMillis();
            while(!timestamps.isEmpty()&&now-timestamps.peek()>=windowMs) timestamps.poll();
            if(timestamps.size()<limit){timestamps.offer(now);return true;}return false;}
    }
    static class DBConnection { String id; DBConnection(int n){id="conn-"+n;} }
    static class ConnectionPool {
        BlockingQueue<DBConnection> pool=new LinkedBlockingQueue<>();
        int size(){return pool.size()+used;}int used=0;
        ConnectionPool(String dsn,int size){for(int i=1;i<=size;i++) pool.offer(new DBConnection(i));}
        DBConnection acquire(long timeoutMs) throws Exception{DBConnection c=pool.poll(timeoutMs,TimeUnit.MILLISECONDS);if(c!=null) used++;return c;}
        void release(DBConnection c){pool.offer(c);used--;}
    }

    // =========================================================
    // SECTION 3 — MONOLITH VS MICROSERVICES
    // =========================================================
    static void section3_MonolithVsMicroservices() {
        printSection("3. MONOLITH VS MICROSERVICES");

        // 3a. Monolith simulation
        System.out.println("--- 3a. Monolith — All Modules in One Process ---");
        MonolithApp monolith = new MonolithApp();
        monolith.placeOrder("alice", "product-42", 2);

        // 3b. Microservices with service registry
        System.out.println("\n--- 3b. Microservices with Service Registry ---");
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("user-service",    new ServiceInstance("user-service",    "10.0.0.1", 8081));
        registry.register("order-service",   new ServiceInstance("order-service",   "10.0.0.2", 8082));
        registry.register("payment-service", new ServiceInstance("payment-service", "10.0.0.3", 8083));
        registry.register("catalog-service", new ServiceInstance("catalog-service", "10.0.0.4", 8084));
        registry.register("order-service",   new ServiceInstance("order-service",   "10.0.0.5", 8082)); // 2nd instance
        System.out.println("  Registered services:");
        registry.printAll();
        System.out.println("  Resolving 'order-service': " + registry.discover("order-service"));

        // 3c. Health checks
        System.out.println("\n--- 3c. Service Health Checks ---");
        HealthCheckSystem hcs = new HealthCheckSystem();
        hcs.register("user-service",    ()-> new HealthResult(true,  "DB connected, 45ms"));
        hcs.register("payment-service", ()-> new HealthResult(true,  "Stripe reachable, 120ms"));
        hcs.register("catalog-service", ()-> new HealthResult(false, "Redis timeout after 5000ms"));
        hcs.register("order-service",   ()-> new HealthResult(true,  "All dependencies healthy"));
        hcs.runChecks();

        // 3d. Strangler Fig migration
        System.out.println("\n--- 3d. Strangler Fig Pattern (Migration) ---");
        StranglerProxy proxy = new StranglerProxy();
        proxy.route("/api/payments", "payment-microservice");   // Extracted
        proxy.route("/api/users",    "user-microservice");      // Extracted
        // Everything else still goes to monolith
        String[] requests={"/api/payments/charge","/api/orders/create","/api/users/profile","/api/reports"};
        for(String r:requests) System.out.println("  "+r+" → "+proxy.getDestination(r));
    }

    static class MonolithApp {
        void placeOrder(String user,String product,int qty){
            System.out.println("  [Monolith] Order for "+user);
            System.out.println("    [UserModule] Validating user..."+user+" ✓");
            System.out.println("    [CatalogModule] Checking product..."+product+" available ✓");
            System.out.println("    [InventoryModule] Reserving "+qty+" units ✓");
            System.out.println("    [PaymentModule] Charging card ✓");
            System.out.println("    [OrderModule] Creating order record ✓");
            System.out.println("    [NotifModule] Sending confirmation email ✓");
            System.out.println("  All modules in same JVM process, same DB transaction ✓");
        }
    }
    static class ServiceInstance {
        String name,host;int port;
        ServiceInstance(String n,String h,int p){name=n;host=h;port=p;}
        @Override public String toString(){return host+":"+port;}
    }
    static class ServiceRegistry {
        Map<String,List<ServiceInstance>> registry=new HashMap<>();
        AtomicInteger counter=new AtomicInteger();
        void register(String name,ServiceInstance inst){registry.computeIfAbsent(name,k->new ArrayList<>()).add(inst);}
        ServiceInstance discover(String name){List<ServiceInstance> l=registry.get(name);if(l==null||l.isEmpty()) return null;return l.get(counter.getAndIncrement()%l.size());}
        void printAll(){registry.forEach((k,v)->System.out.println("    "+k+" → "+v+" ("+v.size()+" instances)"));}
    }
    static class HealthResult{boolean healthy;String detail;HealthResult(boolean h,String d){healthy=h;detail=d;}}
    static class HealthCheckSystem {
        Map<String,Supplier<HealthResult>> checks=new LinkedHashMap<>();
        void register(String name,Supplier<HealthResult> check){checks.put(name,check);}
        void runChecks(){checks.forEach((name,check)->{HealthResult r=check.get();
            System.out.printf("  %-20s [%s] %s%n",name,r.healthy?"HEALTHY ✓":"DOWN ✗",r.detail);});}
    }
    static class StranglerProxy {
        Map<String,String> routes=new LinkedHashMap<>();
        void route(String prefix,String dest){routes.put(prefix,dest);}
        String getDestination(String path){for(Map.Entry<String,String> e:routes.entrySet()) if(path.startsWith(e.getKey())) return e.getValue()+" [microservice]";return "legacy-monolith [still there]";}
    }

    // =========================================================
    // SECTION 4 — MICROSERVICES ARCHITECTURE
    // =========================================================
    static void section4_MicroservicesArchitecture() throws Exception {
        printSection("4. MICROSERVICES ARCHITECTURE");

        // 4a. API Gateway simulation
        System.out.println("--- 4a. API Gateway Simulation ---");
        APIGateway gateway = new APIGateway();
        gateway.addRoute("/api/users",    "user-service",    true,  100);
        gateway.addRoute("/api/orders",   "order-service",   true,  50);
        gateway.addRoute("/api/catalog",  "catalog-service", false, 200); // no auth needed
        gateway.addRoute("/api/payments", "payment-service", true,  20);

        System.out.println("  Processing requests:");
        gateway.handle(new GatewayRequest("GET","/api/catalog/products","","192.168.1.1"));
        gateway.handle(new GatewayRequest("POST","/api/orders","Bearer valid_jwt_token","192.168.1.2"));
        gateway.handle(new GatewayRequest("POST","/api/payments","","192.168.1.3")); // No auth
        gateway.handle(new GatewayRequest("GET","/api/admin","Bearer valid_jwt_token","192.168.1.4")); // Unknown route

        // 4b. Circuit Breaker
        System.out.println("\n--- 4b. Circuit Breaker State Machine ---");
        CircuitBreaker cb = new CircuitBreaker("payment-service", 3, 5000L);
        System.out.println("  Simulating service degradation:");
        for(int i=1;i<=10;i++){
            boolean fail = i>=3 && i<=7; // Fail requests 3-7
            try{cb.execute(()->{if(fail) throw new RuntimeException("Service timeout");return "SUCCESS";}); System.out.printf("    Request %2d: SUCCESS [CB: %s]%n",i,cb.getState());}
            catch(Exception e){System.out.printf("    Request %2d: FAILED  [CB: %s] %s%n",i,cb.getState(),e.getMessage());}
            if(i==7){System.out.println("    >> Waiting for circuit to allow retry...");try{Thread.sleep(200);}catch(Exception ex){}}
        }

        // 4c. Retry with exponential backoff
        System.out.println("\n--- 4c. Retry with Exponential Backoff ---");
        RetryPolicy retry = new RetryPolicy(3, 100L, 2.0);
        AtomicInteger attempts = new AtomicInteger(0);
        try{
            retry.execute(()->{
                int att = attempts.incrementAndGet();
                System.out.printf("    Attempt %d: %s%n", att, att<3?"FAILED (simulated)":"SUCCESS ✓");
                if(att < 3) throw new RuntimeException("Service unavailable");
                return "Order created successfully";});
        }catch(Exception e){System.out.println("    All retries exhausted: "+e.getMessage());}

        // 4d. SAGA orchestration
        System.out.println("\n--- 4d. SAGA Pattern (Distributed Transaction) ---");
        SAGAOrchestrator saga = new SAGAOrchestrator();
        saga.execute("ORD-001", "USR-123", 150.00, true);  // Success flow
        System.out.println();
        saga.execute("ORD-002", "USR-456", 999.00, false); // Payment failure → compensate
    }

    static class GatewayRequest{String method,path,auth,ip;GatewayRequest(String m,String p,String a,String i){method=m;path=p;auth=a;ip=i;}}
    static class RouteConfig{String service;boolean requiresAuth;int rateLimit;RouteConfig(String s,boolean r,int l){service=s;requiresAuth=r;rateLimit=l;}}
    static class APIGateway {
        Map<String,RouteConfig> routes=new LinkedHashMap<>();
        void addRoute(String prefix,String service,boolean auth,int rateLimit){routes.put(prefix,new RouteConfig(service,auth,rateLimit));}
        void handle(GatewayRequest req){
            System.out.printf("  %s %s from %s%n",req.method,req.path,req.ip);
            RouteConfig route=null;
            for(Map.Entry<String,RouteConfig> e:routes.entrySet()) if(req.path.startsWith(e.getKey())){route=e.getValue();break;}
            if(route==null){System.out.println("    → 404 Not Found: No route");return;}
            if(route.requiresAuth&&(req.auth==null||req.auth.isEmpty())){System.out.println("    → 401 Unauthorized: Missing token");return;}
            if(route.requiresAuth) System.out.println("    → Auth: JWT validated ✓");
            System.out.println("    → Rate limit: OK ("+route.rateLimit+" req/sec allowed) ✓");
            System.out.println("    → Routed to: "+route.service+" ✓");
        }
    }
    enum CBState{CLOSED,OPEN,HALF_OPEN}
    static class CircuitBreaker {
        String name; int failThreshold,failures=0; long openDuration,openedAt;
        CBState state=CBState.CLOSED;
        CircuitBreaker(String n,int ft,long od){name=n;failThreshold=ft;openDuration=od;}
        String getState(){return state.name();}
        <T> T execute(Supplier<T> call) throws Exception{
            if(state==CBState.OPEN){
                if(System.currentTimeMillis()-openedAt>openDuration){state=CBState.HALF_OPEN;}
                else throw new RuntimeException("Circuit OPEN — fast fail for "+name);}
            try{T result=call.get();
                if(state==CBState.HALF_OPEN){state=CBState.CLOSED;failures=0;System.out.println("    [CB] Circuit CLOSED — "+name+" recovered");}
                return result;}
            catch(Exception e){failures++;
                if(state==CBState.HALF_OPEN||failures>=failThreshold){state=CBState.OPEN;openedAt=System.currentTimeMillis();System.out.println("    [CB] Circuit OPENED — "+name+" failing");}
                throw e;}
        }
    }
    static class RetryPolicy {
        int maxRetries; long baseDelayMs; double multiplier;
        RetryPolicy(int r,long d,double m){maxRetries=r;baseDelayMs=d;multiplier=m;}
        <T> T execute(Supplier<T> call) throws Exception{
            long delay=baseDelayMs;
            for(int i=0;i<=maxRetries;i++){
                try{return call.get();}
                catch(Exception e){if(i==maxRetries) throw e;
                    System.out.printf("    Waiting %dms before retry...%n",delay);
                    try{Thread.sleep(delay);}catch(Exception ex){}
                    delay=(long)(delay*multiplier);}}
            throw new RuntimeException("Unreachable");}
    }
    static class SAGAOrchestrator {
        void execute(String orderId, String userId, double amount, boolean paymentSucceeds){
            System.out.println("  SAGA: " + orderId);
            System.out.println("    Step 1 [OrderService] Create order → PENDING");
            System.out.println("    Step 2 [InventoryService] Reserve stock → OK ✓");
            if(paymentSucceeds){
                System.out.println("    Step 3 [PaymentService] Charge $"+amount+" → SUCCESS ✓");
                System.out.println("    Step 4 [OrderService] Confirm order → CONFIRMED ✓");
                System.out.println("    Step 5 [NotificationService] Send confirmation ✓");
                System.out.println("    SAGA COMPLETED ✓");
            } else {
                System.out.println("    Step 3 [PaymentService] Charge $"+amount+" → FAILED ✗");
                System.out.println("    --- Compensating transactions (rollback) ---");
                System.out.println("    Compensate [InventoryService] Release reserved stock ↩");
                System.out.println("    Compensate [OrderService] Cancel order → CANCELLED ↩");
                System.out.println("    Compensate [NotificationService] Send failure notice ↩");
                System.out.println("    SAGA ROLLED BACK ✓");}
        }
    }

    // =========================================================
    // SECTION 5 — COMMUNICATION PATTERNS
    // =========================================================
    static void section5_CommunicationPatterns() throws Exception {
        printSection("5. COMMUNICATION PATTERNS");

        // 5a. In-memory event bus (Kafka simulation)
        System.out.println("--- 5a. Async Event Bus (Kafka-like) ---");
        EventBus bus = new EventBus();
        bus.subscribe("order-events", "inventory-service",
            msg -> System.out.println("  [Inventory] Reserve items: orderId="+msg.get("orderId")));
        bus.subscribe("order-events", "email-service",
            msg -> System.out.println("  [Email] Send confirmation: user="+msg.get("userId")));
        bus.subscribe("order-events", "analytics-service",
            msg -> System.out.println("  [Analytics] Record sale: $"+msg.get("amount")));
        bus.subscribe("payment-events", "accounting-service",
            msg -> System.out.println("  [Accounting] Record payment: "+msg.get("txnId")));

        Map<String,Object> orderEvent = Map.of("orderId","ORD-001","userId","USR-123","amount",199.99);
        Map<String,Object> paymentEvent = Map.of("txnId","TXN-456","amount",199.99,"status","SUCCESS");
        System.out.println("  Publishing order-events:");
        bus.publish("order-events", orderEvent);
        System.out.println("  Publishing payment-events:");
        bus.publish("payment-events", paymentEvent);

        // 5b. Message queue with DLQ
        System.out.println("\n--- 5b. Message Queue with Dead Letter Queue (DLQ) ---");
        MessageQueue queue = new MessageQueue("order-processing", 3);
        for(int i=1;i<=5;i++) queue.enqueue(Map.of("orderId","ORD-00"+i,"amount",i*50.0));
        System.out.println("  Processing with simulated failures:");
        AtomicInteger failCount = new AtomicInteger(0);
        queue.process(msg -> {
            String oid = (String)msg.get("orderId");
            if(oid.equals("ORD-002")||oid.equals("ORD-004")) throw new RuntimeException("Payment declined for "+oid);
            System.out.println("    Processed: "+oid+" ✓");
        });
        System.out.println("  Dead Letter Queue contents: "+queue.getDLQ().size()+" messages");
        queue.getDLQ().forEach(m->System.out.println("    DLQ: "+m.get("orderId")+" → manual investigation needed"));

        // 5c. Outbox pattern (reliable event publishing)
        System.out.println("\n--- 5c. Outbox Pattern (Reliable Event Publishing) ---");
        OutboxPattern outbox = new OutboxPattern();
        outbox.saveOrderAndPublishEvent("ORD-007","USR-789",299.0);

        // 5d. Request-Response vs Fire-and-Forget
        System.out.println("\n--- 5d. Sync vs Async Communication ---");
        System.out.println("  Synchronous (user-facing query, need immediate response):");
        syncCall("GET /api/products/123", 45, "Product data");
        System.out.println("  Asynchronous (side effect, no immediate response needed):");
        asyncCall("order.placed", Map.of("orderId","ORD-001"));
        asyncCall("email.send",   Map.of("to","alice@example.com","template","order_confirmed"));
        asyncCall("analytics.track", Map.of("event","purchase","amount","199.99"));
    }

    static class EventBus {
        Map<String,List<Consumer<Map<String,Object>>>> subs=new HashMap<>();
        void subscribe(String topic,String consumer,Consumer<Map<String,Object>> handler){
            subs.computeIfAbsent(topic,k->new ArrayList<>()).add(handler);}
        void publish(String topic,Map<String,Object> msg){
            List<Consumer<Map<String,Object>>> handlers=subs.getOrDefault(topic,List.of());
            handlers.forEach(h->h.accept(msg));}
    }
    static class MessageQueue {
        String name; int maxRetries; Queue<Map<String,Object>> queue=new LinkedList<>(); List<Map<String,Object>> dlq=new ArrayList<>();
        Map<Map<String,Object>,Integer> retries=new HashMap<>();
        MessageQueue(String n,int r){name=n;maxRetries=r;}
        void enqueue(Map<String,Object> msg){queue.offer(msg);}
        void process(Consumer<Map<String,Object>> handler){
            while(!queue.isEmpty()){Map<String,Object> msg=queue.poll();
                try{handler.accept(msg);}
                catch(Exception e){int rc=retries.merge(msg,1,Integer::sum);
                    if(rc<=maxRetries){queue.offer(msg);System.out.println("    Retry "+rc+"/"+maxRetries+": "+msg.get("orderId"));}
                    else{dlq.add(msg);System.out.println("    DLQ: "+msg.get("orderId")+" after "+maxRetries+" retries");}}}
        }
        List<Map<String,Object>> getDLQ(){return dlq;}
    }
    static class OutboxPattern {
        List<Map<String,Object>> outboxTable=new ArrayList<>();
        void saveOrderAndPublishEvent(String orderId,String userId,double amount){
            System.out.println("  BEGIN TRANSACTION");
            System.out.println("    INSERT INTO orders (id,userId,amount) VALUES ("+orderId+","+userId+","+amount+")");
            Map<String,Object> event=new HashMap<>(Map.of("eventType","ORDER_PLACED","orderId",orderId,"userId",userId,"amount",amount,"published",false));
            outboxTable.add(event);
            System.out.println("    INSERT INTO outbox (event) VALUES (ORDER_PLACED for "+orderId+")");
            System.out.println("  COMMIT TRANSACTION ✓ (both in same transaction — atomically safe)");
            System.out.println("  [Outbox Poller] Reading unpublished events...");
            outboxTable.stream().filter(e->!(boolean)e.get("published")).forEach(e->{
                System.out.println("  [Outbox Poller] Publishing to Kafka: "+e.get("eventType")+" "+e.get("orderId"));
                e.put("published",true);});
        }
    }
    static void syncCall(String desc,int latencyMs,String response){System.out.printf("    %s → [%dms] %s (client waited)%n",desc,latencyMs,response);}
    static void asyncCall(String topic,Map<String,Object> msg){System.out.printf("    Publish '%s' → Kafka (client didn't wait) %s%n",topic,msg);}

    // =========================================================
    // SECTION 6 — DATA MANAGEMENT
    // =========================================================
    static void section6_DataManagement() {
        printSection("6. DATA MANAGEMENT");

        // 6a. Consistent hashing
        System.out.println("--- 6a. Consistent Hashing Ring ---");
        ConsistentHashRing ring = new ConsistentHashRing(150); // 150 virtual nodes per server
        ring.addServer("cache-1");
        ring.addServer("cache-2");
        ring.addServer("cache-3");
        System.out.println("  Key distribution:");
        String[] keys={"user:alice","user:bob","product:laptop","order:ORD-001","session:abc123"};
        for(String k:keys) System.out.printf("    %-20s → %s%n",k,ring.getServer(k));
        System.out.println("  Adding cache-4 (only nearby keys remapped):");
        ring.addServer("cache-4");
        for(String k:keys) System.out.printf("    %-20s → %s%n",k,ring.getServer(k));

        // 6b. Read/Write split
        System.out.println("\n--- 6b. Read Replica Pattern ---");
        ReadWriteDB db = new ReadWriteDB("primary:5432", List.of("replica1:5432","replica2:5432","replica3:5432"));
        String[] ops={"SELECT user WHERE id=1","INSERT INTO orders ...","SELECT products WHERE ...","UPDATE inventory ...","SELECT analytics ..."};
        for(String op:ops) db.execute(op);

        // 6c. Cache-aside pattern
        System.out.println("\n--- 6c. Cache-Aside Pattern ---");
        CacheAsideService cas = new CacheAsideService();
        System.out.println("  First access (cache miss → DB query):");
        cas.getUser("USR-001");
        System.out.println("  Second access (cache hit → no DB):");
        cas.getUser("USR-001");
        System.out.println("  Update user (invalidate cache):");
        cas.updateUser("USR-001","Alice Updated");
        System.out.println("  Access after update (cache miss again):");
        cas.getUser("USR-001");

        // 6d. CQRS pattern
        System.out.println("\n--- 6d. CQRS Pattern ---");
        CQRSSystem cqrs = new CQRSSystem();
        cqrs.createOrder("ORD-100","USR-001",List.of("Laptop","Mouse"),1499.0);
        cqrs.createOrder("ORD-101","USR-001",List.of("Monitor"),399.0);
        System.out.println("  Read model (denormalized for fast reads):");
        cqrs.getUserOrders("USR-001").forEach(v->System.out.println("    "+v));

        // 6e. Distributed lock
        System.out.println("\n--- 6e. Distributed Lock (Redlock Simulation) ---");
        DistributedLock lock = new DistributedLock();
        ExecutorService exec = Executors.newFixedThreadPool(3);
        for(int i=1;i<=3;i++){final int id=i;exec.submit(()->{
            if(lock.tryAcquire("inventory:P001",5000)){
                try{System.out.println("    Thread-"+id+": Acquired lock, updating inventory");
                    Thread.sleep(50);}catch(Exception e){}
                finally{lock.release("inventory:P001");System.out.println("    Thread-"+id+": Released lock");}
            } else System.out.println("    Thread-"+id+": Lock not available, skipping");});}
        exec.shutdown(); try{exec.awaitTermination(3,TimeUnit.SECONDS);}catch(Exception e){}
    }

    static class ConsistentHashRing {
        TreeMap<Long,String> ring=new TreeMap<>(); int vnodes;
        ConsistentHashRing(int v){vnodes=v;}
        void addServer(String server){for(int i=0;i<vnodes;i++) ring.put(hash(server+"-vnode-"+i),server);}
        String getServer(String key){if(ring.isEmpty()) return null;Long h=hash(key);Map.Entry<Long,String> e=ring.ceilingEntry(h);return e!=null?e.getValue():ring.firstEntry().getValue();}
        long hash(String key){long h=0;for(char c:key.toCharArray()) h=(h*31+c)%1000000007L;return h;}
    }
    static class ReadWriteDB {
        String primary; List<String> replicas; AtomicInteger rrCounter=new AtomicInteger();
        ReadWriteDB(String p,List<String> r){primary=p;replicas=r;}
        void execute(String sql){
            boolean isWrite=sql.toUpperCase().startsWith("INSERT")||sql.toUpperCase().startsWith("UPDATE")||sql.toUpperCase().startsWith("DELETE");
            if(isWrite) System.out.println("    WRITE → "+primary+": "+sql);
            else{String replica=replicas.get(rrCounter.getAndIncrement()%replicas.size());System.out.println("    READ  → "+replica+": "+sql);}
        }
    }
    static class CacheAsideService {
        Map<String,String> cache=new HashMap<>(); Map<String,String> db=new HashMap<>(Map.of("USR-001","Alice","USR-002","Bob"));
        void getUser(String id){
            if(cache.containsKey(id)){System.out.println("    [Cache HIT] "+id+" → "+cache.get(id));return;}
            System.out.println("    [Cache MISS] Querying DB for "+id);
            String val=db.getOrDefault(id,"Not found");
            cache.put(id,val);System.out.println("    [Cache SET] "+id+"="+val);}
        void updateUser(String id,String newVal){db.put(id,newVal);cache.remove(id);System.out.println("    [DB UPDATE] "+id+"="+newVal+" | [Cache EVICTED]");}
    }
    static class CQRSSystem {
        List<Map<String,Object>> eventLog=new ArrayList<>();
        List<Map<String,Object>> readModel=new ArrayList<>();
        void createOrder(String orderId,String userId,List<String> items,double total){
            Map<String,Object> event=Map.of("type","ORDER_CREATED","orderId",orderId,"userId",userId,"items",items,"total",total);
            eventLog.add(event);System.out.println("  [Command] Create order: "+orderId);
            Map<String,Object> view=new HashMap<>(Map.of("orderId",orderId,"userId",userId,"summary",items.size()+" items","total",total,"status","PENDING"));
            readModel.add(view);System.out.println("  [ReadModel] Projected: "+orderId+" → summary ready");}
        List<Map<String,Object>> getUserOrders(String userId){return readModel.stream().filter(v->v.get("userId").equals(userId)).collect(Collectors.toList());}
    }
    static class DistributedLock {
        Map<String,Long> locks=new ConcurrentHashMap<>();
        synchronized boolean tryAcquire(String key,long ttlMs){
            Long exp=locks.get(key);if(exp!=null&&System.currentTimeMillis()<exp) return false;
            locks.put(key,System.currentTimeMillis()+ttlMs);return true;}
        synchronized void release(String key){locks.remove(key);}
    }

    // =========================================================
    // SECTION 7 — JAVA ECOSYSTEM PATTERNS
    // =========================================================
    static void section7_JavaEcosystemPatterns() {
        printSection("7. JAVA ECOSYSTEM & SPRING BOOT PATTERNS");

        // 7a. Dependency injection container
        System.out.println("--- 7a. Simple DI Container (Spring-like) ---");
        DIContainer container = new DIContainer();
        container.register(UserRepository.class, UserRepository::new);
        container.register(EmailService.class, EmailService::new);
        container.register(UserService.class, () -> new UserService(
            container.get(UserRepository.class),
            container.get(EmailService.class)));
        UserService userSvc = container.get(UserService.class);
        userSvc.createUser("alice@example.com","Alice");

        // 7b. Repository pattern
        System.out.println("\n--- 7b. Repository Pattern ---");
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        ServiceOrder o1 = new ServiceOrder("ORD-001","USR-1",199.0,"PENDING");
        ServiceOrder o2 = new ServiceOrder("ORD-002","USR-1",299.0,"PAID");
        ServiceOrder o3 = new ServiceOrder("ORD-003","USR-2",99.0,"PENDING");
        repo.save(o1);repo.save(o2);repo.save(o3);
        System.out.println("  All orders: "+repo.findAll().size());
        System.out.println("  USR-1 orders: "+repo.findByUserId("USR-1").size());
        System.out.println("  Pending orders: "+repo.findByStatus("PENDING").size());
        repo.findById("ORD-002").ifPresent(o->System.out.println("  Order: "+o));

        // 7c. Event sourcing
        System.out.println("\n--- 7c. Event Sourcing ---");
        EventSourcedOrder esOrder = new EventSourcedOrder("ORD-ES-001");
        esOrder.apply(new OrderEvent("CREATED","USR-123",List.of("Laptop"),1299.0));
        esOrder.apply(new OrderEvent("ITEM_ADDED","USR-123",List.of("Mouse"),29.0));
        esOrder.apply(new OrderEvent("PAYMENT_RECEIVED","TXN-999",null,1328.0));
        esOrder.apply(new OrderEvent("SHIPPED","TRACK-ABC",null,0));
        System.out.println("  Events replayed: "+esOrder.getEvents().size());
        System.out.println("  Current state: "+esOrder.getStatus()+" total="+esOrder.getTotal());
        System.out.println("  Full audit trail:");
        esOrder.getEvents().forEach(e->System.out.println("    "+e));

        // 7d. Interceptor chain (Spring AOP simulation)
        System.out.println("\n--- 7d. Interceptor Chain (AOP Simulation) ---");
        ServiceCall<String> getProduct = () -> "Product{id=123,name=Laptop,price=999}";
        ServiceCall<String> withLogging = () -> {System.out.println("    [LOG] Calling getProduct");String r=getProduct.call();System.out.println("    [LOG] Response: "+r);return r;};
        ServiceCall<String> withMetrics = () -> {long t=System.nanoTime();String r=withLogging.call();System.out.printf("    [METRICS] Duration: %dns%n",System.nanoTime()-t);return r;};
        ServiceCall<String> withAuth    = () -> {System.out.println("    [AUTH] JWT validated ✓");return withMetrics.call();};
        System.out.println("  Executing: getProduct()");
        withAuth.call();
    }

    static class DIContainer {
        Map<Class<?>,Supplier<?>> registry=new HashMap<>();
        Map<Class<?>,Object> singletons=new HashMap<>();
        <T> void register(Class<T> type,Supplier<T> factory){registry.put(type,factory);}
        @SuppressWarnings("unchecked") <T> T get(Class<T> type){return (T)singletons.computeIfAbsent(type,k->registry.get(k).get());}
    }
    static class UserRepository {
        Map<String,String> db=new HashMap<>();
        void save(String email,String name){db.put(email,name);System.out.println("    [UserRepo] Saved: "+name+" ("+email+")");}
    }
    static class EmailService {
        void sendWelcome(String email){System.out.println("    [EmailSvc] Welcome email → "+email);}
    }
    static class UserService {
        UserRepository repo; EmailService email;
        UserService(UserRepository r,EmailService e){repo=r;email=e;}
        void createUser(String em,String name){repo.save(em,name);email.sendWelcome(em);}
    }
    static class ServiceOrder {
        String id,userId,status; double amount;
        ServiceOrder(String i,String u,double a,String s){id=i;userId=u;amount=a;status=s;}
        @Override public String toString(){return "Order("+id+", "+userId+", $"+amount+", "+status+")";}
    }
    static class InMemoryOrderRepository {
        Map<String,ServiceOrder> store=new LinkedHashMap<>();
        void save(ServiceOrder o){store.put(o.id,o);}
        Optional<ServiceOrder> findById(String id){return Optional.ofNullable(store.get(id));}
        List<ServiceOrder> findAll(){return new ArrayList<>(store.values());}
        List<ServiceOrder> findByUserId(String uid){return store.values().stream().filter(o->o.userId.equals(uid)).collect(Collectors.toList());}
        List<ServiceOrder> findByStatus(String s){return store.values().stream().filter(o->o.status.equals(s)).collect(Collectors.toList());}
    }
    static class OrderEvent {
        String type,actor; List<String> items; double amount; long ts=System.currentTimeMillis();
        OrderEvent(String t,String a,List<String> i,double amt){type=t;actor=a;items=i;amount=amt;}
        @Override public String toString(){return "["+type+"] actor="+actor+(items!=null?" items="+items:"")+(amount>0?" amount="+amount:"");}
    }
    static class EventSourcedOrder {
        String id; String status="NEW"; double total; List<OrderEvent> events=new ArrayList<>();
        EventSourcedOrder(String id){this.id=id;}
        void apply(OrderEvent e){events.add(e);switch(e.type){case "CREATED"->status="PENDING";case "PAYMENT_RECEIVED"->status="PAID";case "SHIPPED"->status="SHIPPED";}if(e.amount>0&&!e.type.equals("PAYMENT_RECEIVED")) total+=e.amount;}
        List<OrderEvent> getEvents(){return events;}
        String getStatus(){return status;}
        double getTotal(){return total;}
    }
    @FunctionalInterface interface ServiceCall<T>{T call();}

    // =========================================================
    // SECTION 8 — REAL-WORLD CASE STUDY
    // =========================================================
    static void section8_RealWorldCaseStudy() throws Exception {
        printSection("8. REAL-WORLD CASE STUDY: FOOD DELIVERY PLATFORM");

        // 8a. Full order flow
        System.out.println("--- 8a. Complete Order Flow ---");
        FoodDeliveryPlatform platform = new FoodDeliveryPlatform();
        platform.initialize();
        System.out.println();
        platform.placeOrder("USR-001","RST-042","Butter Chicken + Naan",24.99,"CARD-4242",true);
        System.out.println();
        platform.placeOrder("USR-002","RST-007","Sushi Platter",45.00,"CARD-9999",false); // Payment fail

        // 8b. Location tracking
        System.out.println("\n--- 8b. Real-Time Location Tracking ---");
        LocationTracker tracker = new LocationTracker();
        tracker.updateLocation("DRIVER-01", 37.7749, -122.4194);
        tracker.updateLocation("DRIVER-02", 37.7751, -122.4180);
        tracker.updateLocation("DRIVER-03", 37.7740, -122.4200);
        System.out.println("  Customer at (37.7749, -122.4194) nearest drivers:");
        tracker.findNearestDrivers(37.7749,-122.4194,3).forEach(d->System.out.println("    "+d));

        // 8c. Search service
        System.out.println("\n--- 8c. Search Service (Restaurant + Menu) ---");
        SearchService search = new SearchService();
        search.index("RST-001","Spice Garden","Indian","Butter Chicken, Biryani, Naan",4.5,25,"Curry, Rice, Bread");
        search.index("RST-002","Sakura Sushi","Japanese","Sushi, Ramen, Tempura",4.8,35,"Fish, Noodles");
        search.index("RST-003","Pizza Palace","Italian","Margherita, Pepperoni, BBQ Chicken",4.2,20,"Pizza, Pasta");
        search.index("RST-004","Spice Route","Indian","Dosa, Idli, Sambar",4.6,18,"South Indian");
        System.out.println("  Search 'chicken':");
        search.search("chicken").forEach(r->System.out.println("    "+r));
        System.out.println("  Search 'indian' category:");
        search.searchByCategory("Indian").forEach(r->System.out.println("    "+r));

        // 8d. Metrics and observability
        System.out.println("\n--- 8d. System Metrics Dashboard ---");
        MetricsDashboard dashboard = new MetricsDashboard();
        Random rng = new Random(42);
        for(int i=0;i<1000;i++) dashboard.recordOrderLatency(20+rng.nextInt(200));
        for(int i=0;i<50;i++) dashboard.recordError("PaymentTimeout");
        for(int i=0;i<10;i++) dashboard.recordError("InventoryUnavailable");
        dashboard.recordOrderLatency(5000); // Outlier
        dashboard.printReport();
    }

    static class FoodDeliveryPlatform {
        EventBus bus; SAGAOrchestrator saga; CircuitBreaker paymentCB;
        void initialize(){
            bus=new EventBus();saga=new SAGAOrchestrator();
            paymentCB=new CircuitBreaker("payment-gateway",3,5000L);
            bus.subscribe("order.placed","restaurant-service",e->System.out.println("  [Restaurant] New order notification → "+e.get("restaurant")));
            bus.subscribe("order.placed","driver-matching",  e->System.out.println("  [DriverMatch] Finding driver near "+e.get("restaurant")));
            bus.subscribe("order.placed","analytics",        e->System.out.println("  [Analytics] Recording $"+e.get("amount")+" order"));
            System.out.println("  Platform initialized: Gateway + SAGA + EventBus + CircuitBreaker");
        }
        void placeOrder(String userId,String restaurantId,String items,double amount,String card,boolean payOk){
            System.out.println("  ORDER: "+userId+" from "+restaurantId+" ("+items+") $"+amount);
            System.out.println("  [Gateway] Auth ✓ Rate-limit ✓ Routing to order-service");
            saga.execute("ORD-"+Math.abs(items.hashCode())%10000,userId,amount,payOk);
            if(payOk){bus.publish("order.placed",Map.of("userId",userId,"restaurant",restaurantId,"amount",amount,"items",items));}
        }
    }
    static class LocationTracker {
        Map<String,double[]> locations=new HashMap<>();
        void updateLocation(String driverId,double lat,double lng){locations.put(driverId,new double[]{lat,lng});System.out.printf("  [Location] %s → (%.4f, %.4f)%n",driverId,lat,lng);}
        List<String> findNearestDrivers(double lat,double lng,int k){
            return locations.entrySet().stream()
                .sorted(Comparator.comparingDouble(e->dist(lat,lng,e.getValue()[0],e.getValue()[1])))
                .limit(k)
                .map(e->String.format("%s (dist=%.4f km)",e.getKey(),dist(lat,lng,e.getValue()[0],e.getValue()[1])*111))
                .collect(Collectors.toList());
        }
        double dist(double lat1,double lng1,double lat2,double lng2){return Math.sqrt(Math.pow(lat1-lat2,2)+Math.pow(lng1-lng2,2));}
    }
    static class SearchService {
        List<Map<String,Object>> index=new ArrayList<>();
        void index(String id,String name,String category,String menu,double rating,int deliveryMin,String tags){index.add(Map.of("id",id,"name",name,"category",category,"menu",menu,"rating",rating,"delivery",deliveryMin,"tags",tags));}
        List<String> search(String query){return index.stream().filter(r->((String)r.get("menu")).toLowerCase().contains(query.toLowerCase())||((String)r.get("tags")).toLowerCase().contains(query.toLowerCase())).map(r->r.get("name")+" (★"+r.get("rating")+", "+r.get("delivery")+"min) — "+r.get("menu")).collect(Collectors.toList());}
        List<String> searchByCategory(String cat){return index.stream().filter(r->((String)r.get("category")).equalsIgnoreCase(cat)).map(r->r.get("name")+" — ★"+r.get("rating")).collect(Collectors.toList());}
    }
    static class MetricsDashboard {
        List<Long> latencies=new ArrayList<>(); Map<String,Integer> errors=new LinkedHashMap<>();
        void recordOrderLatency(long ms){latencies.add(ms);}
        void recordError(String type){errors.merge(type,1,Integer::sum);}
        void printReport(){
            List<Long> sorted=new ArrayList<>(latencies); Collections.sort(sorted);
            int n=sorted.size();
            System.out.printf("  Latency P50=%dms P90=%dms P99=%dms P999=%dms Max=%dms%n",
                sorted.get((int)(n*0.50)),sorted.get((int)(n*0.90)),sorted.get((int)(n*0.99)),sorted.get(Math.min(n-1,(int)(n*0.999))),sorted.get(n-1));
            System.out.printf("  Total requests: %,d  Errors: %,d  Error rate: %.2f%%%n",
                n,errors.values().stream().mapToInt(Integer::intValue).sum(),errors.values().stream().mapToInt(Integer::intValue).sum()*100.0/n);
            System.out.println("  Error breakdown:");
            errors.forEach((k,v)->System.out.printf("    %-30s: %d (%.1f%%)%n",k,v,v*100.0/n));
        }
    }

    // =========================================================
    // UTILITIES
    // =========================================================
    static String formatBytes(long bytes){
        if(bytes<1024) return bytes+"B";
        if(bytes<1024*1024) return String.format("%.1f KB",bytes/1024.0);
        if(bytes<1024L*1024*1024) return String.format("%.1f MB",bytes/1024.0/1024);
        if(bytes<1024L*1024*1024*1024) return String.format("%.1f GB",bytes/1024.0/1024/1024);
        return String.format("%.1f TB",bytes/1024.0/1024/1024/1024);
    }
    static String formatDuration(long secs){
        if(secs<60) return secs+"s";
        if(secs<3600) return String.format("%dm %ds",secs/60,secs%60);
        if(secs<86400) return String.format("%dh %dm",secs/3600,(secs%3600)/60);
        return String.format("%dd %dh",secs/86400,(secs%86400)/3600);
    }
    static void printBanner(String t){System.out.println("\n"+"=".repeat(70)+"\n  "+t+"\n"+"=".repeat(70));}
    static void printSection(String t){System.out.println("\n"+"-".repeat(70)+"\n  SECTION "+t+"\n"+"-".repeat(70));}
}
