import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * SOLUTIONS: Questions 11–15
 * Q11 — Decorator-Based Data Pipeline (Decorator + Composite + Prototype + Strategy)
 * Q12 — Recommendation Engine (Tree problems + Graph DP + Sliding Window)
 * Q13 — Fraud Detection System (Sliding Window + Bit Manipulation + Hashing)
 * Q14 — Live Leaderboard (Segment Tree + Singleton + Observer)
 * Q15 — Versioned Configuration Store (Persistent Trie + Command + Memento)
 *
 * Compile: javac Solutions_Q11_Q15.java
 * Run:     java Solutions_Q11_Q15
 */
public class Solutions_Q11_Q15 {

    public static void main(String[] args) {
        System.out.println("=== Q11: Decorator Data Pipeline ===");
        Q11_DataPipeline.demo();

        System.out.println("\n=== Q12: Recommendation Engine ===");
        Q12_RecommendationEngine.demo();

        System.out.println("\n=== Q13: Fraud Detection ===");
        Q13_FraudDetection.demo();

        System.out.println("\n=== Q14: Live Leaderboard ===");
        Q14_Leaderboard.demo();

        System.out.println("\n=== Q15: Versioned Config Store ===");
        Q15_ConfigStore.demo();
    }

    // =========================================================
    // Q11 — DECORATOR-BASED DATA PIPELINE
    // Topics: Decorator · Composite · Prototype · Strategy
    // =========================================================
    static class Q11_DataPipeline {

        // Component interface
        interface DataProcessor {
            byte[] process(byte[] data);
            String name();
        }

        // Concrete component
        static class RawDataProcessor implements DataProcessor {
            public byte[] process(byte[] data) { return data; }
            public String name() { return "Raw"; }
        }

        // Base decorator
        static abstract class PipelineDecorator implements DataProcessor {
            protected final DataProcessor wrapped;
            PipelineDecorator(DataProcessor w) { this.wrapped = w; }
        }

        // Concrete decorators — each adds one responsibility
        static class EncryptionDecorator extends PipelineDecorator {
            EncryptionDecorator(DataProcessor w) { super(w); }
            public byte[] process(byte[] data) {
                byte[] result = wrapped.process(data);
                byte[] encrypted = new byte[result.length];
                for (int i = 0; i < result.length; i++) encrypted[i] = (byte)(result[i] ^ 0x42); // XOR cipher
                System.out.println("  [Encrypt] " + result.length + " bytes encrypted");
                return encrypted;
            }
            public String name() { return "Encrypt(" + wrapped.name() + ")"; }
        }

        static class CompressionDecorator extends PipelineDecorator {
            CompressionDecorator(DataProcessor w) { super(w); }
            public byte[] process(byte[] data) {
                byte[] result = wrapped.process(data);
                // Simulate compression: take every other byte (lossy demo)
                byte[] compressed = new byte[Math.max(1, result.length/2)];
                for (int i = 0; i < compressed.length; i++) compressed[i] = result[i*2];
                System.out.println("  [Compress] " + result.length + " → " + compressed.length + " bytes");
                return compressed;
            }
            public String name() { return "Compress(" + wrapped.name() + ")"; }
        }

        static class AuditDecorator extends PipelineDecorator {
            int callCount = 0;
            AuditDecorator(DataProcessor w) { super(w); }
            public byte[] process(byte[] data) {
                System.out.println("  [Audit] Processing call #" + (++callCount) + " (" + data.length + " bytes in)");
                byte[] result = wrapped.process(data);
                System.out.println("  [Audit] Output: " + result.length + " bytes");
                return result;
            }
            public String name() { return "Audit(" + wrapped.name() + ")"; }
        }

        // Strategy for deduplication
        interface DeduplicationStrategy {
            boolean isDuplicate(byte[] data);
            String name();
        }

        static class HashSetDedup implements DeduplicationStrategy {
            Set<Integer> seen = new HashSet<>();
            public boolean isDuplicate(byte[] data) { return !seen.add(Arrays.hashCode(data)); }
            public String name() { return "HashSet O(1)"; }
        }

        static class DeduplicationDecorator extends PipelineDecorator {
            private DeduplicationStrategy strategy;
            DeduplicationDecorator(DataProcessor w, DeduplicationStrategy s) { super(w); strategy=s; }
            public byte[] process(byte[] data) {
                if (strategy.isDuplicate(data)) { System.out.println("  [Dedup] Duplicate skipped"); return new byte[0]; }
                return wrapped.process(data);
            }
            public String name() { return "Dedup(" + strategy.name() + ")(" + wrapped.name() + ")"; }
            void setStrategy(DeduplicationStrategy s) { strategy = s; }
        }

        // Prototype for pipeline configuration cloning
        static class PipelineConfig implements Cloneable {
            List<String> decoratorOrder;
            boolean auditEnabled;
            String dedupStrategy;

            PipelineConfig(List<String> order, boolean audit, String dedup) {
                decoratorOrder = new ArrayList<>(order);
                auditEnabled = audit;
                dedupStrategy = dedup;
            }

            @Override
            public PipelineConfig clone() {
                return new PipelineConfig(new ArrayList<>(decoratorOrder), auditEnabled, dedupStrategy);
            }

            @Override public String toString() { return "Config{order=" + decoratorOrder + ", audit=" + auditEnabled + ", dedup=" + dedupStrategy + "}"; }
        }

        // Composite — group of pipelines
        static class PipelineGroup implements DataProcessor {
            List<DataProcessor> pipelines = new ArrayList<>();
            void add(DataProcessor p) { pipelines.add(p); }
            public byte[] process(byte[] data) {
                byte[] result = data;
                for (DataProcessor p : pipelines) result = p.process(result);
                return result;
            }
            public String name() { return "Group[" + pipelines.stream().map(DataProcessor::name).collect(Collectors.joining(",")) + "]"; }
        }

        static void demo() {
            // Build decorator chain
            DataProcessor pipeline = new AuditDecorator(
                new EncryptionDecorator(
                    new CompressionDecorator(
                        new RawDataProcessor())));

            System.out.println("  Pipeline: " + pipeline.name());
            byte[] input = "Hello World DataFusion!".getBytes();
            pipeline.process(input);

            // Strategy swap at runtime
            DataProcessor withDedup = new DeduplicationDecorator(
                new AuditDecorator(new RawDataProcessor()), new HashSetDedup());
            byte[] data = "same-data".getBytes();
            withDedup.process(data);
            withDedup.process(data); // Duplicate — should be skipped

            // Prototype cloning
            PipelineConfig config = new PipelineConfig(Arrays.asList("compress","encrypt","audit"), true, "hashset");
            PipelineConfig cloned = config.clone();
            cloned.auditEnabled = false;
            System.out.println("  Original: " + config);
            System.out.println("  Clone:    " + cloned);

            // Composite
            PipelineGroup group = new PipelineGroup();
            group.add(new CompressionDecorator(new RawDataProcessor()));
            group.add(new EncryptionDecorator(new RawDataProcessor()));
            System.out.println("  Composite: " + group.name());
            group.process("test".getBytes());
        }
    }

    // =========================================================
    // Q12 — RECOMMENDATION ENGINE
    // Topics: Tree (max path, LCA, diameter) · Sliding Window + Hashing
    // =========================================================
    static class Q12_RecommendationEngine {

        static class TreeNode {
            int val; String name; TreeNode left, right;
            TreeNode(int v, String n) { val=v; name=n; }
            TreeNode(int v, String n, TreeNode l, TreeNode r) { val=v; name=n; left=l; right=r; }
        }

        // Q12.1 — Max root-to-leaf path sum with path
        static int[] globalMax = {Integer.MIN_VALUE};
        static List<String> bestPath = new ArrayList<>();

        static int maxRootToLeaf(TreeNode node, int currentSum, List<String> path, List<String> best) {
            if (node == null) return Integer.MIN_VALUE;
            path.add(node.name);
            currentSum += node.val;
            if (node.left == null && node.right == null) {
                if (currentSum > globalMax[0]) { globalMax[0] = currentSum; best.clear(); best.addAll(path); }
                path.remove(path.size()-1);
                return currentSum;
            }
            maxRootToLeaf(node.left, currentSum, path, best);
            maxRootToLeaf(node.right, currentSum, path, best);
            path.remove(path.size()-1);
            return globalMax[0];
        }

        // Q12.2 — LCA (no BST property needed)
        static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
            if (root == null || root == p || root == q) return root;
            TreeNode left  = lca(root.left,  p, q);
            TreeNode right = lca(root.right, p, q);
            if (left != null && right != null) return root; // p and q on different sides
            return left != null ? left : right;
        }

        // Q12.3 — Diameter of tree (O(n) single DFS)
        static int diameter = 0;

        static int depth(TreeNode node) {
            if (node == null) return 0;
            int left  = depth(node.left);
            int right = depth(node.right);
            diameter = Math.max(diameter, left + right);
            return 1 + Math.max(left, right);
        }

        // Q12.4 — Most frequent category in sliding window
        static String mostFrequentCategory(List<String> actions, int windowSize) {
            Map<String, Integer> freq = new HashMap<>();
            Deque<String> window = new ArrayDeque<>();
            String topCategory = null; int maxCount = 0;

            for (String action : actions) {
                window.offer(action);
                freq.merge(action, 1, Integer::sum);
                if (window.size() > windowSize) {
                    String out = window.poll();
                    freq.merge(out, -1, Integer::sum);
                    if (freq.get(out) == 0) freq.remove(out);
                }
                int f = freq.get(action);
                if (f > maxCount) { maxCount = f; topCategory = action; }
            }
            return topCategory;
        }

        static void demo() {
            // Build tree:          Tech(10)
            //                    /        \
            //               AI(8)         Web(5)
            //              /    \            \
            //         NLP(3)  CV(7)       React(2)
            TreeNode nlp   = new TreeNode(3,  "NLP");
            TreeNode cv    = new TreeNode(7,  "CV");
            TreeNode react = new TreeNode(2,  "React");
            TreeNode ai    = new TreeNode(8,  "AI",  nlp, cv);
            TreeNode web   = new TreeNode(5,  "Web", null, react);
            TreeNode root  = new TreeNode(10, "Tech", ai, web);

            // Q12.1 Max root-to-leaf
            globalMax[0] = Integer.MIN_VALUE; bestPath.clear();
            List<String> path = new ArrayList<>(), best = new ArrayList<>();
            maxRootToLeaf(root, 0, path, best);
            System.out.println("  Max path sum: " + globalMax[0] + " via " + best);

            // Q12.2 LCA
            TreeNode ancestor = lca(root, nlp, cv);
            System.out.println("  LCA(NLP, CV): " + ancestor.name);
            ancestor = lca(root, nlp, react);
            System.out.println("  LCA(NLP, React): " + ancestor.name);

            // Q12.3 Diameter
            diameter = 0;
            depth(root);
            System.out.println("  Tree diameter (max topic distance): " + diameter + " edges");

            // Q12.4 Sliding window
            List<String> userActions = Arrays.asList("AI","Web","AI","AI","Sports","AI","Web","Web","Web","AI");
            System.out.println("  Most frequent in window-5: " + mostFrequentCategory(userActions, 5));
        }
    }

    // =========================================================
    // Q13 — FRAUD DETECTION SYSTEM
    // Topics: Sliding Window · Bit Manipulation · Hashing
    // =========================================================
    static class Q13_FraudDetection {

        // Q13.1 — Flag first timestamp exceeding K transactions in W seconds
        static long firstFraudTimestamp(long[] timestamps, int k, long windowSec) {
            Deque<Long> window = new ArrayDeque<>();
            for (long ts : timestamps) {
                window.offer(ts);
                while (window.peekFirst() < ts - windowSec) window.pollFirst();
                if (window.size() > k) return ts;
            }
            return -1;
        }

        // Q13.2 — Most frequent merchant in fixed window (O(1) per slide)
        static String mostFrequentMerchant(String[] transactions, int w) {
            Map<String, Integer> freq = new HashMap<>();
            String top = null; int maxF = 0;
            for (int i = 0; i < transactions.length; i++) {
                freq.merge(transactions[i], 1, Integer::sum);
                if (i >= w) {
                    String out = transactions[i-w];
                    freq.merge(out, -1, Integer::sum);
                    if (freq.get(out) == 0) freq.remove(out);
                }
                int f = freq.get(transactions[i]);
                if (f > maxF) { maxF = f; top = transactions[i]; }
            }
            return top;
        }

        // Q13.3 — Find two amounts that XOR to target signal (O(n))
        static int[] findXORPair(int[] amounts, int target) {
            Set<Integer> seen = new HashSet<>();
            for (int amount : amounts) {
                int complement = target ^ amount;
                if (seen.contains(complement)) return new int[]{complement, amount};
                seen.add(amount);
            }
            return null;
        }

        // Q13.4 — Find all pairs with Hamming distance exactly 1 (O(n × 32))
        static List<int[]> hammingDistance1Pairs(int[] fingerprints) {
            Set<Integer> set = new HashSet<>();
            for (int f : fingerprints) set.add(f);
            List<int[]> pairs = new ArrayList<>();
            Set<String> seen = new HashSet<>();

            for (int f : fingerprints) {
                for (int i = 0; i < 32; i++) {
                    int flipped = f ^ (1 << i); // Flip one bit
                    if (set.contains(flipped) && flipped != f) {
                        int a = Math.min(f, flipped), b = Math.max(f, flipped);
                        if (seen.add(a + "," + b)) pairs.add(new int[]{a, b});
                    }
                }
            }
            return pairs;
        }

        static void demo() {
            // Q13.1
            long[] timestamps = {100,101,102,103,110,111,112,115,120};
            long flagTs = firstFraudTimestamp(timestamps, 3, 5);
            System.out.println("  First fraud flag at timestamp: " + flagTs);

            // Q13.2
            String[] merchants = {"Amazon","Amazon","Walmart","Amazon","Walmart","Walmart","Amazon"};
            System.out.println("  Most frequent merchant (window=4): " + mostFrequentMerchant(merchants, 4));

            // Q13.3
            int[] amounts = {1200, 500, 300, 700, 900, 400};
            int target = 1000; // targetSignal
            int[] pair = findXORPair(amounts, target);
            System.out.println("  XOR pair for signal " + target + ": " + Arrays.toString(pair)
                + (pair!=null ? " (XOR=" + (pair[0]^pair[1]) + ")" : ""));

            // Q13.4
            int[] fingerprints = {0b1010, 0b1011, 0b0010, 0b1110};
            List<int[]> hdPairs = hammingDistance1Pairs(fingerprints);
            System.out.println("  Hamming-1 pairs:");
            hdPairs.forEach(p -> System.out.printf("    %d and %d (HD=1)%n", p[0], p[1]));

            // Q13.5 — Complexity proof
            long naiveOps   = (long)1_000_000 * 1_000_000;
            long smartOps   = (long)32 * 1_000_000;
            long opsPerSec  = 1_000_000_000L;
            System.out.printf("  Naïve O(n²):  %,d ops → %.0f seconds%n", naiveOps, (double)naiveOps/opsPerSec);
            System.out.printf("  Smart O(n×32):%,d ops → %.4f seconds (%,.0fx faster)%n",
                smartOps, (double)smartOps/opsPerSec, (double)naiveOps/smartOps);
        }
    }

    // =========================================================
    // Q14 — LIVE LEADERBOARD
    // Topics: Segment Tree (count on score axis) · Singleton · Observer
    // =========================================================
    static class Q14_Leaderboard {

        // Segment Tree over score axis [0, MAX_SCORE]
        static class ScoreSegmentTree {
            static final int MAX_SCORE = 1000;
            int[] tree = new int[4 * (MAX_SCORE + 1)];

            void update(int nd, int s, int e, int pos, int delta) {
                if (s == e) { tree[nd] += delta; return; }
                int m = (s+e)/2;
                if (pos<=m) update(2*nd+1, s, m, pos, delta);
                else        update(2*nd+2, m+1, e, pos, delta);
                tree[nd] = tree[2*nd+1] + tree[2*nd+2];
            }

            int query(int nd, int s, int e, int l, int r) {
                if (r<s || e<l) return 0;
                if (l<=s && e<=r) return tree[nd];
                int m=(s+e)/2;
                return query(2*nd+1,s,m,l,r) + query(2*nd+2,m+1,e,l,r);
            }

            // Find k-th largest score using segment tree walk
            int kthLargestScore(int nd, int s, int e, int k) {
                if (s == e) return s;
                int m=(s+e)/2;
                int rightCount = tree[2*nd+2];
                if (k <= rightCount) return kthLargestScore(2*nd+2, m+1, e, k);
                else                 return kthLargestScore(2*nd+1, s, m, k - rightCount);
            }

            void addScore(int score)    { update(0, 0, MAX_SCORE, score, +1); }
            void removeScore(int score) { update(0, 0, MAX_SCORE, score, -1); }
            int countInRange(int lo, int hi) { return query(0, 0, MAX_SCORE, lo, hi); }
            int getRank(int score) { return 1 + countInRange(score+1, MAX_SCORE); }
            int getKthScore(int k) { return kthLargestScore(0, 0, MAX_SCORE, k); }
        }

        // Singleton — initialization-on-demand holder (thread-safe, lazy)
        static class LeaderboardService {
            private static class Holder {
                static final LeaderboardService INSTANCE = new LeaderboardService();
            }
            public static LeaderboardService getInstance() { return Holder.INSTANCE; }
            private LeaderboardService() {}

            private final ScoreSegmentTree tree = new ScoreSegmentTree();
            private final Map<String, Integer> playerScores = new HashMap<>();
            private final List<RankChangeListener> observers = new ArrayList<>();

            void subscribe(RankChangeListener l) { observers.add(l); }

            void updateScore(String playerId, int newScore) {
                int oldScore = playerScores.getOrDefault(playerId, -1);
                int oldRank  = oldScore >= 0 ? tree.getRank(oldScore) : -1;
                if (oldScore >= 0) tree.removeScore(oldScore);
                tree.addScore(newScore);
                playerScores.put(playerId, newScore);
                int newRank = tree.getRank(newScore);
                if (oldRank >= 0 && Math.abs(newRank - oldRank) > 10) {
                    observers.forEach(l -> l.onRankChange(playerId, oldRank, newRank));
                }
            }

            int getRank(String playerId) {
                return tree.getRank(playerScores.getOrDefault(playerId, 0));
            }

            int countInScoreRange(int lo, int hi) { return tree.countInRange(lo, hi); }
            int getTopKScore(int k) { return tree.getKthScore(k); }
        }

        interface RankChangeListener {
            void onRankChange(String playerId, int oldRank, int newRank);
        }

        static void demo() {
            LeaderboardService lb1 = LeaderboardService.getInstance();
            LeaderboardService lb2 = LeaderboardService.getInstance();
            System.out.println("  Singleton same instance: " + (lb1 == lb2));

            lb1.subscribe((pid, old, nw) ->
                System.out.printf("  [Observer] %s rank changed %d → %d%n", pid, old, nw));

            lb1.updateScore("Alice", 850);
            lb1.updateScore("Bob",   920);
            lb1.updateScore("Carol", 780);
            lb1.updateScore("Dave",  960);
            lb1.updateScore("Eve",   830);

            System.out.println("  Rank of Dave (960):  " + lb1.getRank("Dave"));
            System.out.println("  Rank of Carol (780): " + lb1.getRank("Carol"));
            System.out.println("  Players with score 800-950: " + lb1.countInScoreRange(800, 950));
            System.out.println("  Top-2 score threshold: " + lb1.getTopKScore(2));

            // Large jump triggers observer
            lb1.updateScore("Alice", 200); // Big drop from 850
        }
    }

    // =========================================================
    // Q15 — VERSIONED CONFIGURATION STORE
    // Topics: Persistent Trie · Command + Undo · Memento · Diff
    // =========================================================
    static class Q15_ConfigStore {

        // Persistent Trie node — immutable, shared structure
        static class TrieNode {
            final Map<Character, TrieNode> children;
            final String value;

            TrieNode() { this(null); }
            TrieNode(String value) {
                this.children = new HashMap<>();
                this.value = value;
            }

            // Return a new node with a child replaced — sharing all other children
            TrieNode withChild(char c, TrieNode child) {
                TrieNode newNode = new TrieNode(this.value);
                newNode.children.putAll(this.children);
                newNode.children.put(c, child);
                return newNode;
            }

            TrieNode withValue(String v) {
                TrieNode newNode = new TrieNode(v);
                newNode.children.putAll(this.children);
                return newNode;
            }
        }

        // Persistent trie operations
        static TrieNode set(TrieNode root, String key, String value) {
            if (key.isEmpty()) return root.withValue(value);
            char c = key.charAt(0);
            TrieNode child = root.children.getOrDefault(c, new TrieNode());
            TrieNode newChild = set(child, key.substring(1), value);
            return root.withChild(c, newChild);
        }

        static String get(TrieNode root, String key) {
            if (root == null) return null;
            if (key.isEmpty()) return root.value;
            char c = key.charAt(0);
            return get(root.children.get(c), key.substring(1));
        }

        // Command pattern for config changes
        interface ConfigCommand {
            TrieNode execute(TrieNode current);
            TrieNode undo(TrieNode current);
            String describe();
        }

        static class SetCommand implements ConfigCommand {
            String key, value, prevValue;
            SetCommand(String k, String v, String prev) { key=k; value=v; prevValue=prev; }
            public TrieNode execute(TrieNode root) { return set(root, key, value); }
            public TrieNode undo(TrieNode root) {
                return prevValue == null ? root : set(root, key, prevValue);
            }
            public String describe() { return "SET " + key + "=" + value; }
        }

        // Version history manager
        static class VersionHistory {
            private final List<TrieNode> roots = new ArrayList<>();
            private final Deque<ConfigCommand> commands = new ArrayDeque<>();
            private TrieNode current = new TrieNode();

            VersionHistory() { roots.add(current); }

            void execute(ConfigCommand cmd) {
                current = cmd.execute(current);
                roots.add(current);
                commands.push(cmd);
                System.out.println("  [V" + (roots.size()-1) + "] " + cmd.describe());
            }

            void undo() {
                if (commands.isEmpty()) return;
                ConfigCommand cmd = commands.pop();
                current = cmd.undo(current);
                System.out.println("  [Undo] " + cmd.describe());
            }

            String getAt(int version, String key) { return get(roots.get(version), key); }
            String getCurrent(String key) { return get(current, key); }
            int currentVersion() { return roots.size()-1; }

            // Diff between two versions
            Map<String, String[]> diff(int v1, int v2, List<String> keys) {
                Map<String, String[]> diffs = new LinkedHashMap<>();
                for (String k : keys) {
                    String val1 = getAt(v1, k), val2 = getAt(v2, k);
                    if (!Objects.equals(val1, val2)) diffs.put(k, new String[]{val1, val2});
                }
                return diffs;
            }
        }

        // Memento — full snapshot of config
        static class ConfigSnapshot {
            final Map<String, String> flatConfig;
            final long timestamp;

            ConfigSnapshot(Map<String, String> config) {
                this.flatConfig = Map.copyOf(config);
                this.timestamp  = System.currentTimeMillis();
            }
        }

        static void demo() {
            VersionHistory history = new VersionHistory();
            List<String> allKeys = Arrays.asList("db.host","db.port","cache.ttl","feature.darkMode");

            history.execute(new SetCommand("db.host",       "localhost",    null));
            history.execute(new SetCommand("db.port",       "5432",         null));
            history.execute(new SetCommand("cache.ttl",     "300",          null));
            history.execute(new SetCommand("feature.darkMode", "false",     null));
            history.execute(new SetCommand("db.host",       "prod-db.aws",
                history.getCurrent("db.host")));
            history.execute(new SetCommand("cache.ttl",     "600",
                history.getCurrent("cache.ttl")));

            System.out.println("  Current version: " + history.currentVersion());
            System.out.println("  db.host @ v1: " + history.getAt(1, "db.host"));
            System.out.println("  db.host @ v5: " + history.getAt(5, "db.host"));

            System.out.println("  Diff v1 → v5:");
            history.diff(1, 5, allKeys)
                .forEach((k, v) -> System.out.printf("    %s: '%s' → '%s'%n", k, v[0], v[1]));

            // Undo
            history.undo();
            System.out.println("  cache.ttl after undo: " + history.getCurrent("cache.ttl"));

            // Memento snapshot
            Map<String, String> flat = new LinkedHashMap<>();
            allKeys.forEach(k -> { String v = history.getCurrent(k); if (v!=null) flat.put(k,v); });
            ConfigSnapshot snapshot = new ConfigSnapshot(flat);
            System.out.println("  Snapshot: " + snapshot.flatConfig);
        }
    }
}
