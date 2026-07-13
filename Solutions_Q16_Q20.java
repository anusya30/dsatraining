import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * SOLUTIONS: Questions 16–20
 * Q16 — Airline Seat Booking Engine (Greedy + Two Pointer + Facade + DP)
 * Q17 — Distributed Cache Eviction Policy (LRU + LFU + Strategy + Template Method)
 * Q18 — Code Plagiarism Detector (Rabin-Karp + Suffix Array + Rolling Hash)
 * Q19 — Hospital Appointment Scheduler (Greedy + Two Pointer + Segment Tree + Immutability)
 * Q20 — Compiler Symbol Table (Scope Stack + Cycle Detection + Backtracking + Prototype)
 *
 * Compile: javac Solutions_Q16_Q20.java
 * Run:     java Solutions_Q16_Q20
 */
public class Solutions_Q16_Q20 {

    public static void main(String[] args) {
        System.out.println("=== Q16: Airline Seat Booking ===");
        Q16_AirlineBooking.demo();

        System.out.println("\n=== Q17: Cache Eviction Policies ===");
        Q17_CacheEviction.demo();

        System.out.println("\n=== Q18: Plagiarism Detector ===");
        Q18_PlagiarismDetector.demo();

        System.out.println("\n=== Q19: Hospital Appointment Scheduler ===");
        Q19_HospitalScheduler.demo();

        System.out.println("\n=== Q20: Compiler Symbol Table ===");
        Q20_CompilerSymbolTable.demo();
    }

    // =========================================================
    // Q16 — AIRLINE SEAT BOOKING ENGINE
    // Topics: Greedy · Two Pointer · Facade · DP
    // =========================================================
    static class Q16_AirlineBooking {

        // Q16.1 — Greedy maximum non-overlapping appointment selection
        static List<int[]> greedyMaxAppointments(int[][] appointments) {
            Arrays.sort(appointments, Comparator.comparingInt(a -> a[1])); // Sort by end time
            List<int[]> selected = new ArrayList<>();
            int lastEnd = Integer.MIN_VALUE;
            for (int[] apt : appointments) {
                if (apt[0] >= lastEnd) {
                    selected.add(apt);
                    lastEnd = apt[1];
                }
            }
            return selected;
        }

        // Q16.2 — Two pointer: assign smallest-fit slot to each passenger
        static Map<Integer,Integer> assignSeats(int[] passengerReqs, int[] seatDurations) {
            Arrays.sort(passengerReqs);
            Arrays.sort(seatDurations);
            Map<Integer,Integer> assignment = new LinkedHashMap<>();
            int j = 0;
            for (int i = 0; i < passengerReqs.length; i++) {
                while (j < seatDurations.length && seatDurations[j] < passengerReqs[i]) j++;
                if (j < seatDurations.length) {
                    assignment.put(passengerReqs[i], seatDurations[j]);
                    j++;
                }
            }
            return assignment;
        }

        // Q16.4 — Facade pattern
        interface SeatInventory { boolean isAvailable(int seatNo); void book(int seatNo); }
        interface PricingService { double getPrice(int seatNo, String tier); }
        interface PaymentService { boolean charge(String userId, double amount); }
        interface NotificationService { void notify(String userId, String msg); }

        static class BookingRequest { String userId; int seatNo; String tier;
            BookingRequest(String u,int s,String t){userId=u;seatNo=s;tier=t;} }
        static class BookingConfirmation { String bookingId; boolean success; String message;
            BookingConfirmation(String id, boolean ok, String msg){bookingId=id;success=ok;message=msg;} }

        static class BookingFacade {
            private final SeatInventory inv; private final PricingService pricing;
            private final PaymentService payment; private final NotificationService notif;

            BookingFacade(SeatInventory i, PricingService p, PaymentService pay, NotificationService n) {
                inv=i; pricing=p; payment=pay; notif=n;
            }

            BookingConfirmation bookSeats(BookingRequest req) {
                if (!inv.isAvailable(req.seatNo))
                    return new BookingConfirmation(null, false, "Seat not available");
                double price = pricing.getPrice(req.seatNo, req.tier);
                if (!payment.charge(req.userId, price))
                    return new BookingConfirmation(null, false, "Payment failed");
                inv.book(req.seatNo);
                String id = "BK-" + System.nanoTime() % 100000;
                notif.notify(req.userId, "Booked seat " + req.seatNo + " for $" + price);
                return new BookingConfirmation(id, true, "Confirmed!");
            }
        }

        static void demo() {
            // Q16.1 Greedy
            int[][] appointments = {{0,6},{1,4},{3,5},{3,8},{5,7},{6,9}};
            List<int[]> selected = greedyMaxAppointments(appointments);
            System.out.println("  Max non-overlapping appointments: " + selected.size());
            selected.forEach(a -> System.out.println("    [" + a[0] + "," + a[1] + "]"));

            // Q16.2 Two pointer seat assignment
            int[] passengers = {90, 45, 60, 30, 75}; // min duration required
            int[] slots = {100, 50, 60, 35, 80, 55};  // available slot durations
            Map<Integer,Integer> assignment = assignSeats(passengers, slots);
            System.out.println("  Seat assignments (req → slot): " + assignment);

            // Q16.4 Facade demo
            Set<Integer> bookedSeats = new HashSet<>();
            BookingFacade facade = new BookingFacade(
                seat -> !bookedSeats.contains(seat),
                (seat, tier) -> tier.equals("BUSINESS") ? 500.0 : 150.0,
                (user, amount) -> { System.out.printf("  [Payment] $%.0f from %s%n", amount, user); return true; },
                (user, msg) -> System.out.println("  [Notif] " + user + ": " + msg)
            );
            BookingConfirmation conf = facade.bookSeats(new BookingRequest("Alice", 12, "BUSINESS"));
            System.out.println("  Booking: " + conf.message + " (id=" + conf.bookingId + ")");
        }
    }

    // =========================================================
    // Q17 — DISTRIBUTED CACHE EVICTION
    // Topics: LRU · LFU · Strategy · Template Method
    // =========================================================
    static class Q17_CacheEviction {

        // ── LRU Cache: HashMap + DoublyLinkedList ────────────
        static class LRUCache {
            static class DNode { int key, val; DNode prev, next; DNode(int k,int v){key=k;val=v;} }
            Map<Integer,DNode> map = new HashMap<>();
            DNode head = new DNode(0,0), tail = new DNode(0,0);
            int cap;

            LRUCache(int cap) { this.cap=cap; head.next=tail; tail.prev=head; }

            int get(int key) {
                if (!map.containsKey(key)) return -1;
                DNode n = map.get(key); remove(n); addToHead(n); return n.val;
            }

            void put(int key, int val) {
                if (map.containsKey(key)) { DNode n=map.get(key); n.val=val; remove(n); addToHead(n); return; }
                if (map.size()==cap) { DNode lru=tail.prev; remove(lru); map.remove(lru.key); }
                DNode n = new DNode(key,val); addToHead(n); map.put(key,n);
            }

            private void remove(DNode n) { n.prev.next=n.next; n.next.prev=n.prev; }
            private void addToHead(DNode n) { n.next=head.next; n.prev=head; head.next.prev=n; head.next=n; }

            @Override public String toString() {
                List<Integer> order = new ArrayList<>();
                DNode cur = head.next;
                while (cur != tail) { order.add(cur.key); cur=cur.next; }
                return "LRU" + order + "(MRU→LRU)";
            }
        }

        // ── LFU Cache: O(1) via freq buckets ─────────────────
        static class LFUCache {
            Map<Integer,Integer> keyVal = new HashMap<>(), keyFreq = new HashMap<>();
            Map<Integer, LinkedHashSet<Integer>> freqKeys = new HashMap<>();
            int cap, minFreq;

            LFUCache(int cap) { this.cap=cap; }

            int get(int key) {
                if (!keyVal.containsKey(key)) return -1;
                updateFreq(key); return keyVal.get(key);
            }

            void put(int key, int val) {
                if (cap<=0) return;
                if (keyVal.containsKey(key)) { keyVal.put(key,val); updateFreq(key); return; }
                if (keyVal.size()==cap) {
                    LinkedHashSet<Integer> minBucket = freqKeys.get(minFreq);
                    int evict = minBucket.iterator().next(); minBucket.remove(evict);
                    keyVal.remove(evict); keyFreq.remove(evict);
                }
                keyVal.put(key,val); keyFreq.put(key,1);
                freqKeys.computeIfAbsent(1, k->new LinkedHashSet<>()).add(key);
                minFreq=1;
            }

            private void updateFreq(int key) {
                int f = keyFreq.get(key);
                keyFreq.put(key, f+1);
                freqKeys.get(f).remove(key);
                if (freqKeys.get(f).isEmpty()) { freqKeys.remove(f); if (minFreq==f) minFreq++; }
                freqKeys.computeIfAbsent(f+1, k->new LinkedHashSet<>()).add(key);
            }
        }

        // ── Strategy pattern for eviction ─────────────────────
        interface EvictionPolicy {
            void onAccess(int key);
            int evict();
            void onInsert(int key);
        }

        static class LRUPolicy implements EvictionPolicy {
            Deque<Integer> order = new LinkedList<>();
            public void onAccess(int key) { order.remove(key); order.offerFirst(key); }
            public int evict()             { return order.pollLast(); }
            public void onInsert(int key)  { order.offerFirst(key); }
        }

        // ── Template Method for cache operations ──────────────
        static abstract class AbstractCachePolicy {
            final void accessKey(int key) { recordAccess(key); evictIfNeeded(); }
            protected abstract void recordAccess(int key);
            protected abstract void evictIfNeeded();
            protected abstract int selectEvictionCandidate();
        }

        static class LRUPolicyTemplate extends AbstractCachePolicy {
            Deque<Integer> order = new LinkedList<>(); int cap;
            LRUPolicyTemplate(int c) { cap=c; }
            protected void recordAccess(int key) { order.remove(key); order.offerFirst(key); }
            protected void evictIfNeeded() {
                if (order.size() > cap) { int evicted = selectEvictionCandidate(); order.removeLast(); System.out.println("  [Template-LRU] Evicted: " + evicted); }
            }
            protected int selectEvictionCandidate() { return order.peekLast(); }
        }

        static void demo() {
            // LRU
            LRUCache lru = new LRUCache(3);
            lru.put(1, 10); lru.put(2, 20); lru.put(3, 30);
            System.out.println("  " + lru);
            lru.get(1); // Access 1 → moves to head (MRU)
            System.out.println("  After get(1): " + lru);
            lru.put(4, 40); // Evicts key 2 (LRU)
            System.out.println("  After put(4): " + lru);
            System.out.println("  get(2)=" + lru.get(2) + " (evicted → -1)");

            // LFU
            LFUCache lfu = new LFUCache(3);
            lfu.put(1,1); lfu.put(2,2); lfu.put(3,3);
            lfu.get(1); lfu.get(1); // key 1 frequency=2
            lfu.get(2);              // key 2 frequency=1
            lfu.put(4,4);            // Evicts key 3 (lowest freq=1, oldest)
            System.out.println("  LFU after eviction: get(3)=" + lfu.get(3) + " (evicted → -1)");
            System.out.println("  LFU get(1)=" + lfu.get(1));

            // Template Method
            LRUPolicyTemplate template = new LRUPolicyTemplate(2);
            for (int k : new int[]{1,2,3,4}) { template.order.offerFirst(k); template.accessKey(k); }

            // Complexity proof
            System.out.println("  LinkedList.remove(key): O(n) — must scan to find node");
            System.out.println("  HashMap[key] → DNode ref → pointer update: O(1) — direct pointer!");
        }
    }

    // =========================================================
    // Q18 — CODE PLAGIARISM DETECTOR
    // Topics: Rabin-Karp · Suffix Array · Rolling Hash · Sliding Window
    // =========================================================
    static class Q18_PlagiarismDetector {

        // Q18.1 — Rolling hash for 50-token window plagiarism
        static Map<Long, List<String>> hashWindows(String[] tokens, int windowSize,
                                                    String submissionId) {
            long BASE = 131, MOD = 1_000_000_007L;
            Map<Long, List<String>> result = new HashMap<>();
            if (tokens.length < windowSize) return result;

            long hash = 0, power = 1;
            for (int i = 0; i < windowSize-1; i++) power = power*BASE%MOD;
            for (int i = 0; i < windowSize; i++) hash = (hash*BASE + tokens[i].hashCode()%MOD + MOD)%MOD;
            result.computeIfAbsent(hash, k->new ArrayList<>()).add(submissionId+":0");

            for (int i = windowSize; i < tokens.length; i++) {
                hash = (hash - (tokens[i-windowSize].hashCode()%MOD + MOD)*power%MOD + MOD*2)%MOD;
                hash = (hash*BASE + tokens[i].hashCode()%MOD + MOD)%MOD;
                result.computeIfAbsent(hash, k->new ArrayList<>()).add(submissionId+":"+(i-windowSize+1));
            }
            return result;
        }

        // Q18.3 — Permutation check: sorted arrays equal
        static boolean isPermutation(String[] tokensA, String[] tokensB) {
            if (tokensA.length != tokensB.length) return false;
            String[] a = tokensA.clone(), b = tokensB.clone();
            Arrays.sort(a); Arrays.sort(b);
            return Arrays.equals(a, b);
        }

        // Q18.4 — Sliding window self-plagiarism detection
        static int findSelfCopy(String[] tokens, int windowSize) {
            long BASE = 131, MOD = 1_000_000_007L;
            Set<Long> seen = new HashSet<>();
            if (tokens.length < windowSize) return -1;

            long hash = 0, power = 1;
            for (int i = 0; i < windowSize-1; i++) power = power*BASE%MOD;
            for (int i = 0; i < windowSize; i++) hash = (hash*BASE + tokens[i].hashCode()%MOD + MOD)%MOD;
            seen.add(hash);

            for (int i = windowSize; i < tokens.length; i++) {
                hash = (hash - (tokens[i-windowSize].hashCode()%MOD + MOD)*power%MOD + MOD*2)%MOD;
                hash = (hash*BASE + tokens[i].hashCode()%MOD + MOD)%MOD;
                if (!seen.add(hash)) return i - windowSize + 1; // Duplicate found
            }
            return -1;
        }

        // Simplified suffix array via sorted suffixes
        static int[] buildSuffixArray(String s) {
            int n = s.length();
            Integer[] sa = new Integer[n];
            for (int i = 0; i < n; i++) sa[i] = i;
            Arrays.sort(sa, Comparator.comparing(s::substring));
            return Arrays.stream(sa).mapToInt(Integer::intValue).toArray();
        }

        static int longestCommonSubstring(String a, String b) {
            String combined = a + '#' + b; // '#' as sentinel
            int[] sa = buildSuffixArray(combined);
            int aLen = a.length(), max = 0;

            for (int i = 1; i < sa.length; i++) {
                int x = sa[i-1], y = sa[i];
                // One suffix from A, one from B (cross sentinel)
                if ((x < aLen) != (y < aLen)) {
                    int lcp = 0;
                    while (x+lcp < combined.length() && y+lcp < combined.length()
                           && combined.charAt(x+lcp) == combined.charAt(y+lcp)
                           && combined.charAt(x+lcp) != '#') lcp++;
                    max = Math.max(max, lcp);
                }
            }
            return max;
        }

        static void demo() {
            // Q18.1 rolling hash windows
            String[] subA = {"void","main","String","args","System","out","println","Hello"};
            String[] subB = {"public","void","main","String","args","System","out","println"};

            Map<Long,List<String>> hashA = hashWindows(subA, 4, "SUB-A");
            Map<Long,List<String>> hashB = hashWindows(subB, 4, "SUB-B");
            boolean plagiarism = hashA.keySet().stream().anyMatch(hashB::containsKey);
            System.out.println("  Plagiarism detected (hash collision): " + plagiarism);

            // Q18.3 permutation check
            String[] a = {"int","x","=","5",";","return","x",";"};
            String[] b = {"return","x",";","int","x","=","5",";"};
            System.out.println("  Is permutation: " + isPermutation(a, b));

            // Q18.4 self-copy detection
            String[] code = {"for","i","in","range","x","for","i","in","range","x","return","i"};
            int selfCopy = findSelfCopy(code, 4);
            System.out.println("  Self-copy at position: " + selfCopy);

            // Q18.2 LCS via suffix array
            String sub1 = "public void processOrder(Order order)";
            String sub2 = "private void processOrder(Order ord)";
            System.out.println("  Longest common substring length: " + longestCommonSubstring(sub1, sub2));

            // Q18.5 double hashing proof
            System.out.println("  Single hash FP rate: ~1/10^9");
            System.out.println("  Double hash FP rate: ~1/10^18 (product of two independent hashes)");
        }
    }

    // =========================================================
    // Q19 — HOSPITAL APPOINTMENT SCHEDULER
    // Topics: Greedy · Two Pointer · Segment Tree · Immutability
    // =========================================================
    static class Q19_HospitalScheduler {

        // Q19.4 — Immutable Appointment (value object)
        static final class Appointment {
            final String patientId, doctorId;
            final int start, end;

            Appointment(String p, String d, int s, int e) {
                patientId=p; doctorId=d; start=s; end=e;
            }

            // Cancellation returns a new availability slot — never mutates Appointment
            AvailableSlot cancel() {
                System.out.println("  [Cancel] " + patientId + "'s appointment → new slot [" + start+","+end+"]");
                return new AvailableSlot(doctorId, start, end);
            }

            @Override public String toString() { return patientId+"@["+start+"-"+end+"]"; }
        }

        record AvailableSlot(String doctorId, int start, int end) {}

        // Q19.1 — Greedy max non-overlapping appointments
        static List<int[]> maxNonOverlapping(int[][] intervals) {
            Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));
            List<int[]> result = new ArrayList<>();
            int lastEnd = Integer.MIN_VALUE;
            for (int[] iv : intervals) {
                if (iv[0] >= lastEnd) { result.add(iv); lastEnd = iv[1]; }
            }
            return result;
        }

        // Q19.2 — Two pointer: smallest-fit slot per patient
        static List<int[]> assignPatients(int[] patientDurations, int[] slotDurations) {
            int[] pIdx = new int[patientDurations.length], sIdx = new int[slotDurations.length];
            for (int i=0;i<pIdx.length;i++) pIdx[i]=i;
            for (int i=0;i<sIdx.length;i++) sIdx[i]=i;
            Arrays.sort(pIdx, (a,b)->patientDurations[a]-patientDurations[b]);
            Arrays.sort(sIdx, (a,b)->slotDurations[a]-slotDurations[b]);

            List<int[]> assignments = new ArrayList<>();
            int j = 0;
            for (int pi : pIdx) {
                while (j<sIdx.length && slotDurations[sIdx[j]] < patientDurations[pi]) j++;
                if (j<sIdx.length) { assignments.add(new int[]{pi, sIdx[j]}); j++; }
            }
            return assignments;
        }

        // Q19.3 — Segment Tree on time axis [0, 1440] for available slot counts
        static class TimeSegmentTree {
            int[] tree; static final int MAX = 1440;

            TimeSegmentTree(int initialSlots) {
                tree = new int[4*(MAX+1)];
                // Initialize entire range with slot count
                fill(0, 0, MAX, initialSlots);
            }

            void fill(int nd, int s, int e, int val) {
                tree[nd] = val*(e-s+1);
                if (s==e) return;
                int m=(s+e)/2;
                fill(2*nd+1,s,m,val); fill(2*nd+2,m+1,e,val);
            }

            void bookSlot(int nd, int s, int e, int l, int r) {
                if (r<s||e<l) return;
                if (l<=s&&e<=r) { tree[nd]--; return; }
                int m=(s+e)/2;
                bookSlot(2*nd+1,s,m,l,r); bookSlot(2*nd+2,m+1,e,l,r);
                tree[nd]=tree[2*nd+1]+tree[2*nd+2];
            }

            int availableCount(int nd, int s, int e, int l, int r) {
                if (r<s||e<l) return 0;
                if (l<=s&&e<=r) return tree[nd];
                int m=(s+e)/2;
                return availableCount(2*nd+1,s,m,l,r)+availableCount(2*nd+2,m+1,e,l,r);
            }

            void book(int start, int end)      { bookSlot(0,0,MAX,start,end); }
            int count(int start, int end)      { return availableCount(0,0,MAX,start,end); }
        }

        static void demo() {
            // Q19.1 Greedy
            int[][] requests = {{0,60},{30,90},{60,120},{90,150},{120,180},{0,180}};
            List<int[]> schedule = maxNonOverlapping(requests);
            System.out.println("  Max appointments: " + schedule.size());
            schedule.forEach(a -> System.out.println("    ["+a[0]+"-"+a[1]+"] mins"));

            // Q19.2 Two pointer
            int[] patients = {45, 30, 60, 20}; // required duration
            int[] slots    = {60, 30, 90, 25, 45}; // available slot durations
            List<int[]> assignments = assignPatients(patients, slots);
            System.out.println("  Patient assignments (patientIdx→slotIdx):");
            assignments.forEach(a -> System.out.println("    Patient["+a[0]+"]("+patients[a[0]]+") → Slot["+a[1]+"]("+slots[a[1]]+")"));

            // Q19.3 Segment Tree on time axis
            TimeSegmentTree timeTree = new TimeSegmentTree(1); // 1 slot per minute
            System.out.println("  Available 9:00-12:00 (540-720): " + timeTree.count(540,720));
            timeTree.book(540, 600); // Book 9:00-10:00
            timeTree.book(600, 660); // Book 10:00-11:00
            System.out.println("  After booking 2 slots, available 540-720: " + timeTree.count(540,720));

            // Q19.4 Immutability
            Appointment apt = new Appointment("P-001","DR-Smith",540,600);
            System.out.println("  Original appointment: " + apt);
            AvailableSlot freed = apt.cancel();
            System.out.println("  Freed slot: " + freed);
            System.out.println("  Original appointment unchanged: " + apt);
        }
    }

    // =========================================================
    // Q20 — COMPILER SYMBOL TABLE
    // Topics: Scope Stack · Cycle Detection · All Topological Orders · Prototype
    // =========================================================
    static class Q20_CompilerSymbolTable {

        // Q20.1 — Scope-aware symbol table
        static class SymbolTable {
            Deque<Map<String,String>> scopeStack = new ArrayDeque<>();

            void enterScope() { scopeStack.push(new LinkedHashMap<>()); }

            void exitScope() {
                if (scopeStack.isEmpty()) throw new IllegalStateException("No scope to exit");
                scopeStack.pop();
            }

            void declare(String name, String type) {
                if (scopeStack.isEmpty()) throw new IllegalStateException("No active scope");
                Map<String,String> current = scopeStack.peek();
                if (current.containsKey(name)) throw new RuntimeException("Duplicate: " + name);
                current.put(name, type);
            }

            String lookup(String name) {
                for (Map<String,String> scope : scopeStack) {
                    if (scope.containsKey(name)) return scope.get(name);
                }
                return null; // Not found in any scope
            }

            // Q20.4 Prototype — deep clone
            @Override
            public SymbolTable clone() {
                SymbolTable copy = new SymbolTable();
                // scopeStack is a Deque — iterate from bottom to top, push copies
                List<Map<String,String>> frames = new ArrayList<>(scopeStack);
                Collections.reverse(frames);
                for (Map<String,String> frame : frames) copy.scopeStack.push(new LinkedHashMap<>(frame));
                return copy;
            }
        }

        // Q20.2 — Cycle detection in call graph
        enum Color { WHITE, GREY, BLACK }

        static List<String> detectCallCycle(Map<String, List<String>> callGraph) {
            Map<String,Color> color = new HashMap<>();
            callGraph.keySet().forEach(k -> color.put(k, Color.WHITE));
            List<String> cycle = new ArrayList<>();
            Deque<String> path = new ArrayDeque<>();

            for (String fn : callGraph.keySet()) {
                if (color.get(fn) == Color.WHITE) {
                    if (dfsCycle(fn, callGraph, color, path, cycle)) return cycle;
                }
            }
            return Collections.emptyList();
        }

        static boolean dfsCycle(String u, Map<String,List<String>> g, Map<String,Color> color,
                                 Deque<String> path, List<String> cycle) {
            color.put(u, Color.GREY); path.push(u);
            for (String v : g.getOrDefault(u, List.of())) {
                if (color.get(v) == Color.GREY) {
                    List<String> c = new ArrayList<>();
                    boolean collecting = false;
                    for (String s : path) {
                        if (s.equals(u)) collecting = true;
                        if (collecting) c.add(0, s);
                        if (s.equals(v) && collecting && !c.isEmpty()) { c.add(v); break; }
                    }
                    cycle.addAll(c);
                    return true;
                }
                if (color.get(v) == Color.WHITE && dfsCycle(v, g, color, path, cycle)) return true;
            }
            color.put(u, Color.BLACK); path.pop();
            return false;
        }

        // Q20.3 — All valid topological orderings via backtracking
        static List<List<String>> allTopOrders(Map<String, List<String>> graph) {
            Map<String,Integer> inDeg = new HashMap<>();
            graph.keySet().forEach(k -> inDeg.put(k, 0));
            graph.values().forEach(vs -> vs.forEach(v -> inDeg.merge(v,1,Integer::sum)));

            List<List<String>> results = new ArrayList<>();
            Set<String> visited = new HashSet<>();
            backtrackTopo(graph, inDeg, visited, new ArrayList<>(), results);
            return results;
        }

        static void backtrackTopo(Map<String,List<String>> graph, Map<String,Integer> inDeg,
                                   Set<String> visited, List<String> current,
                                   List<List<String>> results) {
            if (current.size() == graph.size()) { results.add(new ArrayList<>(current)); return; }
            for (String node : graph.keySet()) {
                if (!visited.contains(node) && inDeg.get(node) == 0) {
                    visited.add(node); current.add(node);
                    graph.get(node).forEach(v -> inDeg.merge(v,-1,Integer::sum));
                    backtrackTopo(graph, inDeg, visited, current, results);
                    // Backtrack
                    graph.get(node).forEach(v -> inDeg.merge(v,1,Integer::sum));
                    current.remove(current.size()-1); visited.remove(node);
                }
            }
        }

        static void demo() {
            // Q20.1 Scope-aware symbol table
            SymbolTable st = new SymbolTable();
            st.enterScope();
            st.declare("x", "int"); st.declare("y", "String");
            st.enterScope(); // Inner scope
            st.declare("z", "double"); st.declare("x", "float"); // Shadows outer x
            System.out.println("  Lookup 'x' in inner scope: " + st.lookup("x")); // float
            System.out.println("  Lookup 'y' in inner scope: " + st.lookup("y")); // String from outer
            st.exitScope();
            System.out.println("  Lookup 'x' after exit: " + st.lookup("x")); // int from outer
            System.out.println("  Lookup 'z' after exit: " + st.lookup("z")); // null — gone

            // Q20.4 Prototype
            SymbolTable templateST = st.clone();
            templateST.enterScope();
            templateST.declare("T", "TypeParam");
            System.out.println("  Clone has 'x': " + templateST.lookup("x"));
            System.out.println("  Original has 'T': " + st.lookup("T")); // null — isolated

            // Q20.2 Cycle detection
            Map<String,List<String>> callGraph = new LinkedHashMap<>();
            callGraph.put("main",  Arrays.asList("foo","bar"));
            callGraph.put("foo",   Arrays.asList("baz"));
            callGraph.put("bar",   Arrays.asList("foo"));
            callGraph.put("baz",   Arrays.asList("foo")); // foo→baz→foo = cycle!

            List<String> cycle = detectCallCycle(callGraph);
            System.out.println("  Call graph cycle: " + cycle);

            // Q20.3 All topological orders (small DAG)
            Map<String,List<String>> dag = new LinkedHashMap<>();
            dag.put("A", Arrays.asList("C")); dag.put("B", Arrays.asList("C"));
            dag.put("C", new ArrayList<>());
            List<List<String>> orders = allTopOrders(dag);
            System.out.println("  All topo orders for A→C, B→C: " + orders);
            System.out.println("  Count: " + orders.size() + " (A and B can be in either order)");

            // Q20.5 Complexity proof
            System.out.println("  5 independent nodes: 5! = " + factorial(5) + " orderings");
            System.out.println("  Backtracking generates all → Ω(n!) unavoidable for general DAG");
        }

        static long factorial(int n) { return n<=1?1:n*factorial(n-1); }
    }
}
