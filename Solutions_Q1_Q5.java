import java.util.*;
import java.util.stream.*;

/**
 * SOLUTIONS: Questions 1–5
 * Q1  — Banking Account Hierarchy (OOP + SOLID)
 * Q2  — E-Commerce Order Pipeline (Builder + Factory + Chain + Observer)
 * Q3  — Ride-Sharing Driver Matcher (Adapter + Proxy + State + Strategy)
 * Q4  — Log Anomaly Detector (Sliding Window + Hashing)
 * Q5  — Social Network Friend Suggester (Graph + DP + Union-Find)
 *
 * Compile: javac Solutions_Q1_Q5.java
 * Run:     java Solutions_Q1_Q5
 */
public class Solutions_Q1_Q5 {

    public static void main(String[] args) {
        System.out.println("=== Q1: Banking Account Hierarchy ===");
        Q1_BankingHierarchy.demo();

        System.out.println("\n=== Q2: E-Commerce Order Pipeline ===");
        Q2_OrderPipeline.demo();

        System.out.println("\n=== Q3: Ride-Sharing Driver Matcher ===");
        Q3_RideSharing.demo();

        System.out.println("\n=== Q4: Log Anomaly Detector ===");
        Q4_LogAnomalyDetector.demo();

        System.out.println("\n=== Q5: Social Network Friend Suggester ===");
        Q5_SocialNetwork.demo();
    }

    // =========================================================
    // Q1 — BANKING ACCOUNT HIERARCHY
    // Topics: Advanced OOP · SOLID (SRP, OCP, LSP)
    // =========================================================
    static class Q1_BankingHierarchy {

        // Base abstraction — encapsulates state, exposes behavior
        static abstract class BankAccount {
            protected final String accountId;
            protected double balance;

            BankAccount(String id, double initialBalance) {
                this.accountId = id;
                this.balance = initialBalance;
            }

            // Final: subclasses cannot bypass validation
            public final void deposit(double amount) {
                if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
                balance += amount;
                System.out.printf("  [%s] Deposited %.2f → balance %.2f%n", accountId, amount, balance);
            }

            // Abstract: each subtype enforces its own withdrawal rules (LSP safe)
            public abstract void withdraw(double amount);

            // Abstract: each account type calculates interest differently (OCP)
            public abstract double calculateInterest();

            public double getBalance() { return balance; }
            public String getAccountId() { return accountId; }
        }

        // SavingsAccount: cannot overdraw
        static class SavingsAccount extends BankAccount {
            private static final double INTEREST_RATE = 0.04;

            SavingsAccount(String id, double balance) { super(id, balance); }

            @Override
            public void withdraw(double amount) {
                if (amount > balance) throw new IllegalStateException("Insufficient funds in savings");
                balance -= amount;
                System.out.printf("  [%s] Withdrew %.2f → balance %.2f%n", accountId, amount, balance);
            }

            @Override
            public double calculateInterest() { return balance * INTEREST_RATE; }
        }

        // CurrentAccount: allows overdraft up to limit
        static class CurrentAccount extends BankAccount {
            private final double overdraftLimit;
            private static final double INTEREST_RATE = 0.02;

            CurrentAccount(String id, double balance, double overdraftLimit) {
                super(id, balance);
                this.overdraftLimit = overdraftLimit;
            }

            @Override
            public void withdraw(double amount) {
                if (amount > balance + overdraftLimit)
                    throw new IllegalStateException("Exceeds overdraft limit");
                balance -= amount;
                System.out.printf("  [%s] Withdrew %.2f → balance %.2f%n", accountId, amount, balance);
            }

            @Override
            public double calculateInterest() { return Math.max(balance, 0) * INTEREST_RATE; }
        }

        // FixedDepositAccount: no withdrawals during lock-in period
        static class FixedDepositAccount extends BankAccount {
            private final double interestRate;
            private boolean matured;

            FixedDepositAccount(String id, double balance, double rate) {
                super(id, balance);
                this.interestRate = rate;
                this.matured = false;
            }

            public void mature() { this.matured = true; }

            @Override
            public void withdraw(double amount) {
                if (!matured) throw new IllegalStateException("FD has not matured yet");
                if (amount > balance) throw new IllegalStateException("Insufficient balance");
                balance -= amount;
            }

            @Override
            public double calculateInterest() { return balance * interestRate; }
        }

        // PremiumAccount — OCP: added without touching any existing class
        static class PremiumAccount extends SavingsAccount {
            PremiumAccount(String id, double balance) { super(id, balance); }

            @Override
            public double calculateInterest() {
                return balance * 0.07; // Premium rate
            }
        }

        static void demo() {
            List<BankAccount> accounts = new ArrayList<>();
            accounts.add(new SavingsAccount("SAV-001", 10000));
            accounts.add(new CurrentAccount("CUR-001", 5000, 2000));
            accounts.add(new FixedDepositAccount("FD-001", 20000, 0.08));
            accounts.add(new PremiumAccount("PREM-001", 50000));

            // Deposit into savings
            accounts.get(0).deposit(1000);
            accounts.get(1).deposit(500);

            // Demonstrate LSP: caller uses BankAccount reference, not subtype
            System.out.println("  Monthly interest across all accounts:");
            double total = 0;
            for (BankAccount acc : accounts) {
                double interest = acc.calculateInterest();
                System.out.printf("  %s: %.2f%n", acc.getAccountId(), interest);
                total += interest;
            }
            System.out.printf("  Total interest: %.2f%n", total);

            // Demonstrate constraint enforcement
            try {
                accounts.get(0).withdraw(50000); // Should fail
            } catch (IllegalStateException e) {
                System.out.println("  ✓ Savings protected: " + e.getMessage());
            }
            accounts.get(1).withdraw(6000); // Overdraft allowed
        }
    }

    // =========================================================
    // Q2 — E-COMMERCE ORDER PIPELINE
    // Topics: Builder · Factory Method · Chain of Responsibility · Observer · DIP
    // =========================================================
    static class Q2_OrderPipeline {

        // ── Order (Builder pattern) ──────────────────────────
        static final class Order {
            private final String userId;
            private final List<String> items;
            private final String discountCode;
            private final String paymentMethod;
            private final boolean giftWrapping;
            private String status = "PENDING";

            private Order(Builder b) {
                this.userId = b.userId;
                this.items = List.copyOf(b.items);
                this.discountCode = b.discountCode;
                this.paymentMethod = b.paymentMethod;
                this.giftWrapping = b.giftWrapping;
            }

            public void confirm() {
                this.status = "CONFIRMED";
                OrderEventBus.publish(this);
            }

            @Override
            public String toString() {
                return String.format("Order{user=%s, items=%s, discount=%s, gift=%s, payment=%s, status=%s}",
                        userId, items, discountCode, giftWrapping, paymentMethod, status);
            }

            static class Builder {
                private final String userId;
                private final List<String> items = new ArrayList<>();
                private String discountCode = null;
                private String paymentMethod = "CARD";
                private boolean giftWrapping = false;

                Builder(String userId) { this.userId = userId; }

                Builder addItem(String item) { items.add(item); return this; }
                Builder discount(String code) { discountCode = code; return this; }
                Builder paymentMethod(String method) { paymentMethod = method; return this; }
                Builder giftWrapping() { giftWrapping = true; return this; }

                Order build() {
                    if (items.isEmpty()) throw new IllegalStateException("Order must have at least one item");
                    return new Order(this);
                }
            }
        }

        // ── Payment Processor (Factory Method) ──────────────
        interface PaymentProcessor {
            boolean charge(String userId, double amount);
        }

        static class CreditCardProcessor implements PaymentProcessor {
            public boolean charge(String u, double a) {
                System.out.printf("  [CreditCard] Charged $%.2f for %s%n", a, u); return true;
            }
        }

        static class UPIProcessor implements PaymentProcessor {
            public boolean charge(String u, double a) {
                System.out.printf("  [UPI] Charged $%.2f for %s%n", a, u); return true;
            }
        }

        static abstract class PaymentProcessorFactory {
            abstract PaymentProcessor create();

            static PaymentProcessorFactory of(String method) {
                return switch (method.toUpperCase()) {
                    case "UPI"  -> UPIProcessor::new;
                    case "CARD" -> CreditCardProcessor::new;
                    default     -> throw new IllegalArgumentException("Unknown payment: " + method);
                };
            }
        }

        // ── Validation Chain (Chain of Responsibility) ────────
        interface OrderValidator {
            boolean validate(Order order);
            void setNext(OrderValidator next);
        }

        static abstract class AbstractValidator implements OrderValidator {
            protected OrderValidator next;
            public void setNext(OrderValidator n) { this.next = n; }
            protected boolean passToNext(Order o) { return next == null || next.validate(o); }
        }

        static class StockValidator extends AbstractValidator {
            public boolean validate(Order o) {
                System.out.println("  [StockValidator] Checking stock...");
                return passToNext(o); // always passes in demo
            }
        }

        static class FraudValidator extends AbstractValidator {
            public boolean validate(Order o) {
                System.out.println("  [FraudValidator] Fraud check passed...");
                return passToNext(o);
            }
        }

        static class PriceValidator extends AbstractValidator {
            public boolean validate(Order o) {
                System.out.println("  [PriceValidator] Price validation passed...");
                return passToNext(o);
            }
        }

        // ── Observer (Event Bus) ──────────────────────────────
        interface OrderEventListener {
            void onOrderConfirmed(Order order);
        }

        static class OrderEventBus {
            private static final List<OrderEventListener> listeners = new ArrayList<>();
            static void subscribe(OrderEventListener l) { listeners.add(l); }
            static void publish(Order o) { listeners.forEach(l -> l.onOrderConfirmed(o)); }
        }

        static class EmailNotifier implements OrderEventListener {
            public void onOrderConfirmed(Order o) { System.out.println("  [Email] Confirmation sent for: " + o.userId); }
        }

        static class SMSNotifier implements OrderEventListener {
            public void onOrderConfirmed(Order o) { System.out.println("  [SMS] Alert sent for: " + o.userId); }
        }

        static class AnalyticsTracker implements OrderEventListener {
            public void onOrderConfirmed(Order o) { System.out.println("  [Analytics] Order recorded: " + o); }
        }

        static void demo() {
            // Register observers
            OrderEventBus.subscribe(new EmailNotifier());
            OrderEventBus.subscribe(new SMSNotifier());
            OrderEventBus.subscribe(new AnalyticsTracker());

            // Build order
            Order order = new Order.Builder("USR-001")
                    .addItem("Laptop").addItem("Mouse")
                    .discount("SAVE10").giftWrapping()
                    .paymentMethod("UPI").build();
            System.out.println("  Built: " + order);

            // Validation chain
            OrderValidator stock = new StockValidator();
            OrderValidator fraud = new FraudValidator();
            OrderValidator price = new PriceValidator();
            stock.setNext(fraud); fraud.setNext(price);
            boolean valid = stock.validate(order);
            System.out.println("  Valid: " + valid);

            // Payment
            PaymentProcessor processor = PaymentProcessorFactory.of(order.paymentMethod).create();
            processor.charge(order.userId, 999.99);

            // Confirm triggers observers
            order.confirm();

            // Builder validation guard
            try {
                new Order.Builder("USR-002").build();
            } catch (IllegalStateException e) {
                System.out.println("  ✓ Builder guard: " + e.getMessage());
            }
        }
    }

    // =========================================================
    // Q3 — RIDE-SHARING DRIVER MATCHER
    // Topics: Adapter · Proxy · State Machine · Strategy
    // =========================================================
    static class Q3_RideSharing {

        // ── Adapter ──────────────────────────────────────────
        interface DistanceCalculator {
            double distanceKm(double lat1, double lon1, double lat2, double lon2);
        }

        static class HereMapsAPI {
            double computeRoute(double[] from, double[] to) {
                return Math.hypot(to[0]-from[0], to[1]-from[1]) * 111;
            }
        }

        static class GoogleMapsAPI {
            double getDistanceMeters(String fromLatLon, String toLatLon) {
                String[] f = fromLatLon.split(","), t = toLatLon.split(",");
                return Math.hypot(Double.parseDouble(t[0])-Double.parseDouble(f[0]),
                                  Double.parseDouble(t[1])-Double.parseDouble(f[1])) * 111000;
            }
        }

        static class HereMapsAdapter implements DistanceCalculator {
            private final HereMapsAPI api = new HereMapsAPI();
            public double distanceKm(double lat1,double lon1,double lat2,double lon2) {
                return api.computeRoute(new double[]{lat1,lon1}, new double[]{lat2,lon2});
            }
        }

        static class GoogleMapsAdapter implements DistanceCalculator {
            private final GoogleMapsAPI api = new GoogleMapsAPI();
            public double distanceKm(double lat1,double lon1,double lat2,double lon2) {
                return api.getDistanceMeters(lat1+","+lon1, lat2+","+lon2) / 1000.0;
            }
        }

        // ── Caching Proxy ─────────────────────────────────────
        static class CachingDistanceProxy implements DistanceCalculator {
            private final DistanceCalculator real;
            private final Map<String, double[]> cache = new HashMap<>(); // key → {result, timestamp}
            private static final long TTL_MS = 30_000;

            CachingDistanceProxy(DistanceCalculator real) { this.real = real; }

            public double distanceKm(double lat1, double lon1, double lat2, double lon2) {
                String key = lat1+","+lon1+"→"+lat2+","+lon2;
                double[] cached = cache.get(key);
                if (cached != null && System.currentTimeMillis() - cached[1] < TTL_MS) {
                    System.out.println("  [Proxy] Cache HIT for " + key);
                    return cached[0];
                }
                System.out.println("  [Proxy] Cache MISS for " + key + " — calling real API");
                double result = real.distanceKm(lat1, lon1, lat2, lon2);
                cache.put(key, new double[]{result, System.currentTimeMillis()});
                return result;
            }
        }

        // ── State Machine ─────────────────────────────────────
        enum DriverStatus { AVAILABLE, EN_ROUTE, ON_TRIP, COMPLETED }

        static class Driver {
            String id; double lat, lon; double rating; DriverStatus status;

            Driver(String id, double lat, double lon, double rating) {
                this.id=id; this.lat=lat; this.lon=lon; this.rating=rating;
                this.status = DriverStatus.AVAILABLE;
            }

            void transition(DriverStatus next) {
                boolean allowed = switch (status) {
                    case AVAILABLE -> next == DriverStatus.EN_ROUTE;
                    case EN_ROUTE  -> next == DriverStatus.ON_TRIP;
                    case ON_TRIP   -> next == DriverStatus.COMPLETED;
                    case COMPLETED -> next == DriverStatus.AVAILABLE;
                };
                if (!allowed) throw new IllegalStateException(
                    "Cannot transition " + status + " → " + next);
                System.out.printf("  [Driver %s] %s → %s%n", id, status, next);
                this.status = next;
            }
        }

        // ── Strategy ──────────────────────────────────────────
        interface MatchingStrategy {
            Driver match(double pickupLat, double pickupLon,
                         List<Driver> available, DistanceCalculator calc);
        }

        static class NearestDriverStrategy implements MatchingStrategy {
            public Driver match(double lat, double lon, List<Driver> available, DistanceCalculator calc) {
                return available.stream()
                    .filter(d -> d.status == DriverStatus.AVAILABLE)
                    .min(Comparator.comparingDouble(d -> calc.distanceKm(lat,lon,d.lat,d.lon)))
                    .orElseThrow(() -> new RuntimeException("No drivers available"));
            }
        }

        static class HighestRatedStrategy implements MatchingStrategy {
            public Driver match(double lat, double lon, List<Driver> available, DistanceCalculator calc) {
                return available.stream()
                    .filter(d -> d.status == DriverStatus.AVAILABLE)
                    .max(Comparator.comparingDouble(d -> d.rating))
                    .orElseThrow(() -> new RuntimeException("No drivers available"));
            }
        }

        static void demo() {
            DistanceCalculator realCalc = new HereMapsAdapter();
            DistanceCalculator proxiedCalc = new CachingDistanceProxy(realCalc);

            List<Driver> drivers = List.of(
                new Driver("D1", 12.97, 77.59, 4.8),
                new Driver("D2", 12.98, 77.61, 4.5),
                new Driver("D3", 12.96, 77.57, 4.9)
            );

            MatchingStrategy strategy = new NearestDriverStrategy();
            Driver matched = strategy.match(12.97, 77.60, drivers, proxiedCalc);
            System.out.println("  Nearest driver: " + matched.id);

            // Second call — cache hit
            strategy.match(12.97, 77.60, drivers, proxiedCalc);

            // State transition
            matched.transition(DriverStatus.EN_ROUTE);
            matched.transition(DriverStatus.ON_TRIP);
            try {
                matched.transition(DriverStatus.AVAILABLE); // Illegal
            } catch (IllegalStateException e) {
                System.out.println("  ✓ State guard: " + e.getMessage());
            }

            // Switch strategy
            strategy = new HighestRatedStrategy();
            Driver topRated = strategy.match(12.97, 77.60, drivers, proxiedCalc);
            System.out.println("  Highest-rated driver: " + topRated.id + " (" + topRated.rating + ")");
        }
    }

    // =========================================================
    // Q4 — LOG ANOMALY DETECTOR
    // Topics: Sliding Window · Hashing Hybrid · Complexity
    // =========================================================
    static class Q4_LogAnomalyDetector {

        // Q4.1 — Count windows with exactly K distinct error codes
        // Strategy: exactlyK = atMost(K) - atMost(K-1)
        static int countWindowsExactlyKDistinct(String[] logs, int w, int k) {
            return atMostKDistinct(logs, w, k) - atMostKDistinct(logs, w, k - 1);
        }

        static int atMostKDistinct(String[] logs, int w, int k) {
            Map<String, Integer> freq = new HashMap<>();
            int left = 0, count = 0;
            for (int right = 0; right < logs.length; right++) {
                freq.merge(logs[right], 1, Integer::sum);
                while (freq.size() > k) {
                    String out = logs[left++];
                    freq.merge(out, -1, Integer::sum);
                    if (freq.get(out) == 0) freq.remove(out);
                }
                // All windows [left..right], [left+1..right], ..., [right..right] are valid
                // but we only count windows of exactly size w here
                if (right - left + 1 == w) {
                    count++;
                    // Slide left by 1 for fixed-size windows
                    String out = logs[left++];
                    freq.merge(out, -1, Integer::sum);
                    if (freq.get(out) == 0) freq.remove(out);
                }
            }
            return count;
        }

        // Q4.2 — Longest subarray with at most 2 distinct severity levels
        static int longestAtMost2Severities(String[] logs) {
            Map<String, Integer> freq = new HashMap<>();
            int left = 0, maxLen = 0;
            for (int right = 0; right < logs.length; right++) {
                freq.merge(logs[right], 1, Integer::sum);
                while (freq.size() > 2) {
                    String out = logs[left++];
                    freq.merge(out, -1, Integer::sum);
                    if (freq.get(out) == 0) freq.remove(out);
                }
                maxLen = Math.max(maxLen, right - left + 1);
            }
            return maxLen;
        }

        // Q4.3 — Find all anagram positions of attackSignature in logStream
        // O(n) fixed-window with frequency array comparison
        static List<Integer> findAttackSignaturePositions(String logStream, String pattern) {
            if (logStream.length() < pattern.length()) return List.of();
            int[] patFreq = new int[26], winFreq = new int[26];
            int m = pattern.length();
            for (char c : pattern.toCharArray()) patFreq[c - 'a']++;
            for (int i = 0; i < m; i++) winFreq[logStream.charAt(i) - 'a']++;

            List<Integer> result = new ArrayList<>();
            if (Arrays.equals(patFreq, winFreq)) result.add(0);

            for (int i = m; i < logStream.length(); i++) {
                winFreq[logStream.charAt(i) - 'a']++;
                winFreq[logStream.charAt(i - m) - 'a']--;
                if (Arrays.equals(patFreq, winFreq)) result.add(i - m + 1);
            }
            return result;
        }

        static void demo() {
            String[] logs = {"ERR","WARN","ERR","INFO","ERR","WARN","ERR","ERR"};
            System.out.println("  Windows of size 3 with exactly 2 distinct: "
                + countWindowsExactlyKDistinct(logs, 3, 2));

            String[] severity = {"ERROR","WARN","ERROR","INFO","WARN","WARN","ERROR","CRITICAL"};
            System.out.println("  Longest subarray with ≤2 severities: "
                + longestAtMost2Severities(severity));

            String logStream = "cbaebabacd";
            String attack = "abc";
            System.out.println("  Attack signature '" + attack + "' positions in '" + logStream + "': "
                + findAttackSignaturePositions(logStream, attack));

            // Complexity proof
            int n = 10_000_000, windowSize = 1000;
            System.out.printf("  Naive: %,d ops | Sliding window: %,d ops (%.0fx faster)%n",
                (long) n * windowSize, 2L * n, (double)(n * windowSize) / (2 * n));
        }
    }

    // =========================================================
    // Q5 — SOCIAL NETWORK FRIEND SUGGESTER
    // Topics: Graph (BFS + Union-Find) · DP
    // =========================================================
    static class Q5_SocialNetwork {

        // Q5.1 — BFS shortest path with path reconstruction
        static List<Integer> shortestPath(Map<Integer, List<Integer>> graph, int from, int to) {
            Map<Integer, Integer> prev = new HashMap<>();
            Queue<Integer> queue = new LinkedList<>();
            prev.put(from, -1);
            queue.offer(from);

            while (!queue.isEmpty()) {
                int u = queue.poll();
                if (u == to) break;
                for (int v : graph.getOrDefault(u, List.of())) {
                    if (!prev.containsKey(v)) {
                        prev.put(v, u);
                        queue.offer(v);
                    }
                }
            }

            if (!prev.containsKey(to)) return List.of();
            List<Integer> path = new ArrayList<>();
            for (int at = to; at != -1; at = prev.get(at)) path.add(0, at);
            return path;
        }

        // Q5.2 — Friends-of-friends suggestions
        static Map<Integer, Long> friendSuggestions(Map<Integer, List<Integer>> graph, int user) {
            Set<Integer> directFriends = new HashSet<>(graph.getOrDefault(user, List.of()));
            directFriends.add(user);

            Map<Integer, Long> mutualCount = new HashMap<>();
            for (int friend : directFriends) {
                for (int fof : graph.getOrDefault(friend, List.of())) {
                    if (!directFriends.contains(fof)) {
                        mutualCount.merge(fof, 1L, Long::sum);
                    }
                }
            }
            // Sort by mutual friend count descending, return top 5
            return mutualCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (a, b) -> a, LinkedHashMap::new));
        }

        // Q5.3 — Union-Find for connected components
        static class UnionFind {
            int[] parent, rank, size;
            int components;

            UnionFind(int n) {
                parent = new int[n]; rank = new int[n]; size = new int[n];
                components = n;
                for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
            }

            int find(int x) {
                if (parent[x] != x) parent[x] = find(parent[x]); // Path compression
                return parent[x];
            }

            void union(int a, int b) {
                int pa = find(a), pb = find(b);
                if (pa == pb) return;
                if (rank[pa] < rank[pb]) { int t = pa; pa = pb; pb = t; }
                parent[pb] = pa;
                size[pa] += size[pb];
                if (rank[pa] == rank[pb]) rank[pa]++;
                components--;
            }

            int largestComponentSize() {
                return Arrays.stream(size).max().getAsInt();
            }
        }

        static void demo() {
            Map<Integer, List<Integer>> graph = new HashMap<>();
            graph.put(1, new ArrayList<>(Arrays.asList(2, 3)));
            graph.put(2, new ArrayList<>(Arrays.asList(1, 4, 5)));
            graph.put(3, new ArrayList<>(Arrays.asList(1, 6)));
            graph.put(4, new ArrayList<>(List.of(2)));
            graph.put(5, new ArrayList<>(Arrays.asList(2, 7)));
            graph.put(6, new ArrayList<>(List.of(3)));
            graph.put(7, new ArrayList<>(List.of(5)));

            // Q5.1
            List<Integer> path = shortestPath(graph, 1, 7);
            System.out.println("  Shortest path 1→7: " + path + " (" + (path.size()-1) + " hops)");

            // Q5.2
            Map<Integer, Long> suggestions = friendSuggestions(graph, 1);
            System.out.println("  Friend suggestions for user 1: " + suggestions);

            // Q5.3
            int n = 8; // users 0–7
            UnionFind uf = new UnionFind(n);
            int[][] edges = {{1,2},{1,3},{2,4},{2,5},{3,6},{5,7}};
            for (int[] e : edges) uf.union(e[0], e[1]);
            System.out.println("  Communities: " + uf.components);
            System.out.println("  Largest community size: " + uf.largestComponentSize());
        }
    }
}
