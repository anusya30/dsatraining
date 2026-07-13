import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 * ADVANCED DSA PROBLEMS — Complete Executable Reference
 * ============================================================
 * Topics:
 *  1. Multi-Pattern Integration    (LIP matrix DFS+memo, islands II UF+grid,
 *                                   sudoku backtrack+bitmask, word search II
 *                                   trie+DFS, decode ways+backtrack)
 *  2. Graph + DP Problems          (cheapest flights K stops, shortest path
 *                                   all nodes bitmask BFS, grid DP paths,
 *                                   course schedule II, alien order)
 *  3. Advanced Tree Problems       (serialize/deserialize, max path sum,
 *                                   vertical order, recover BST Morris,
 *                                   tree to DLL, LCA variants,
 *                                   binary tree cameras)
 *  4. Advanced String Algorithms   (KMP, Rabin-Karp, Manacher, suffix array,
 *                                   Z-algorithm, minimum window anagram,
 *                                   palindrome pairs)
 *  5. Sliding Window + Hashing     (subarray sum = k, divisible by k,
 *                                   k distinct integers, substring concat,
 *                                   max points on line, continuous subarray)
 *  6. Real Interview Problems      (LRU cache, median stream, skyline,
 *                                   design twitter, trap water II,
 *                                   k closest points, task scheduler II)
 *  7. Optimization Thinking        (monotonic stack next greater, largest
 *                                   rectangle, maximal rectangle, jump game III,
 *                                   candy distribution, gas station optimal,
 *                                   minimum cost climbing stairs variants)
 *
 * Compile : javac AdvancedDSA.java
 * Run     : java AdvancedDSA
 * ============================================================
 */
public class AdvancedDSA {

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        printBanner("ADVANCED DSA PROBLEMS — COMPLETE DEMO");

        section1_MultiPatternIntegration();
        section2_GraphDP();
        section3_AdvancedTrees();
        section4_AdvancedStrings();
        section5_SlidingWindowHashing();
        section6_RealInterviewProblems();
        section7_OptimizationThinking();

        System.out.println("\n✅ All sections complete.");
    }

    // =========================================================
    // SECTION 1 — MULTI-PATTERN INTEGRATION
    // =========================================================
    static void section1_MultiPatternIntegration() {
        printSection("1. MULTI-PATTERN INTEGRATION");

        // 1a. Longest Increasing Path in Matrix (DFS + Memo)
        System.out.println("--- 1a. Longest Increasing Path in Matrix (DFS + Memo) ---");
        int[][][] matrices = {
            {{9,9,4},{6,6,8},{2,1,1}},
            {{3,4,5},{3,2,6},{2,2,1}},
            {{1}},
            {{1,2,3},{6,5,4},{7,8,9}}
        };
        for (int[][] m : matrices)
            System.out.printf("  %s → LIP=%d%n",
                    Arrays.deepToString(m), longestIncreasingPath(m));

        // 1b. Number of Islands II (Union-Find + Grid)
        System.out.println("\n--- 1b. Number of Islands II (Union-Find + Grid) ---");
        int[][] pos1 = {{0,0},{0,1},{1,2},{2,1}};
        int[][] pos2 = {{0,0},{0,1},{1,2},{2,2},{2,1}};
        System.out.println("  3×3, positions=" + Arrays.deepToString(pos1)
                + " → " + numIslandsII(3, 3, pos1));
        System.out.println("  3×3, positions=" + Arrays.deepToString(pos2)
                + " → " + numIslandsII(3, 3, pos2));

        // 1c. Sudoku solver (Backtracking + Bitmask)
        System.out.println("\n--- 1c. Sudoku Solver (Backtracking + Bitmask) ---");
        char[][] sudoku = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        solveSudoku(sudoku);
        System.out.println("  Sudoku solved:");
        for (char[] row : sudoku) System.out.println("  " + new String(row));

        // 1d. Word Search II (Trie + DFS Backtracking)
        System.out.println("\n--- 1d. Word Search II (Trie + DFS Backtracking) ---");
        char[][] board = {
            {'o','a','a','n'},
            {'e','t','a','e'},
            {'i','h','k','r'},
            {'i','f','l','v'}
        };
        String[] words1 = {"oath","pea","eat","rain"};
        System.out.println("  Board: oaan/etae/ihkr/iflv");
        System.out.println("  Words: " + Arrays.toString(words1));
        System.out.println("  Found: " + findWords(board, words1));

        char[][] board2 = {{'a','b'},{'c','d'}};
        String[] words2 = {"abdc","abcd","cdab"};
        System.out.println("  Board: ab/cd  Words: " + Arrays.toString(words2));
        System.out.println("  Found: " + findWords(board2, words2));

        // 1e. Decode combinations (Backtracking + Memo + String)
        System.out.println("\n--- 1e. Decode Ways (DP + String parsing) ---");
        String[] codes = {"12","226","0","06","10","101","11106","2101"};
        for (String s : codes)
            System.out.printf("  \"%s\" → %d ways%n", s, numDecodings(s));
    }

    // --- Section 1 implementations ---
    static int longestIncreasingPath(int[][] matrix) {
        int m=matrix.length,n=matrix[0].length,max=0;
        int[][] memo=new int[m][n];
        for (int i=0;i<m;i++) for (int j=0;j<n;j++)
            max=Math.max(max,lipDFS(matrix,i,j,memo));
        return max;
    }
    static int lipDFS(int[][] mat,int r,int c,int[][] memo){
        if(memo[r][c]!=0) return memo[r][c];
        int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}}; int max=1;
        for(int[] d:dirs){int nr=r+d[0],nc=c+d[1];
            if(nr>=0&&nr<mat.length&&nc>=0&&nc<mat[0].length&&mat[nr][nc]>mat[r][c])
                max=Math.max(max,1+lipDFS(mat,nr,nc,memo));}
        return memo[r][c]=max;
    }
    static List<Integer> numIslandsII(int m,int n,int[][] positions){
        int[] p=new int[m*n],rank=new int[m*n]; Arrays.fill(p,-1);
        List<Integer> res=new ArrayList<>(); int islands=0;
        int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}};
        for(int[] pos:positions){int r=pos[0],c=pos[1],idx=r*n+c;
            if(p[idx]!=-1){res.add(islands);continue;}
            p[idx]=idx; islands++;
            for(int[] d:dirs){int nr=r+d[0],nc=c+d[1],ni=nr*n+nc;
                if(nr>=0&&nr<m&&nc>=0&&nc<n&&p[ni]!=-1)
                    if(ufUnion(p,rank,idx,ni)) islands--;}
            res.add(islands);}
        return res;
    }
    static int ufFind(int[] p,int x){return p[x]!=x?p[x]=ufFind(p,p[x]):x;}
    static boolean ufUnion(int[] p,int[] rank,int a,int b){
        int pa=ufFind(p,a),pb=ufFind(p,b); if(pa==pb) return false;
        if(rank[pa]<rank[pb]){int t=pa;pa=pb;pb=t;}
        p[pb]=pa; if(rank[pa]==rank[pb]) rank[pa]++; return true;}
    static void solveSudoku(char[][] board){
        int[] rows=new int[9],cols=new int[9],boxes=new int[9];
        for(int r=0;r<9;r++) for(int c=0;c<9;c++) if(board[r][c]!='.'){
            int bit=1<<(board[r][c]-'1'); rows[r]|=bit; cols[c]|=bit; boxes[(r/3)*3+c/3]|=bit;}
        backtrackSudo(board,rows,cols,boxes,0);}
    static boolean backtrackSudo(char[][] b,int[] rows,int[] cols,int[] boxes,int pos){
        while(pos<81&&b[pos/9][pos%9]!='.') pos++;
        if(pos==81) return true;
        int r=pos/9,c=pos%9,box=(r/3)*3+c/3,used=rows[r]|cols[c]|boxes[box];
        for(int d=1;d<=9;d++){int bit=1<<(d-1); if((used&bit)!=0) continue;
            b[r][c]=(char)('0'+d); rows[r]|=bit; cols[c]|=bit; boxes[box]|=bit;
            if(backtrackSudo(b,rows,cols,boxes,pos+1)) return true;
            b[r][c]='.'; rows[r]&=~bit; cols[c]&=~bit; boxes[box]&=~bit;}
        return false;}
    static List<String> findWords(char[][] board, String[] words){
        TrieNode root=new TrieNode();
        for(String w:words){TrieNode cur=root;
            for(char c:w.toCharArray()){int i=c-'a';if(cur.ch[i]==null) cur.ch[i]=new TrieNode();cur=cur.ch[i];}
            cur.word=w;}
        List<String> res=new ArrayList<>();
        for(int i=0;i<board.length;i++) for(int j=0;j<board[0].length;j++)
            wsDFS(board,i,j,root,res);
        return res;}
    static void wsDFS(char[][] b,int r,int c,TrieNode node,List<String> res){
        if(r<0||r>=b.length||c<0||c>=b[0].length||b[r][c]=='#') return;
        char ch=b[r][c]; TrieNode next=node.ch[ch-'a'];
        if(next==null) return;
        if(next.word!=null){res.add(next.word);next.word=null;}
        b[r][c]='#';
        int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}};
        for(int[] d:dirs) wsDFS(b,r+d[0],c+d[1],next,res);
        b[r][c]=ch;
        if(next.isEmpty()) node.ch[ch-'a']=null;}
    static class TrieNode{TrieNode[] ch=new TrieNode[26];String word;
        boolean isEmpty(){for(TrieNode c:ch) if(c!=null) return false;return word==null;}}
    static int numDecodings(String s){
        int n=s.length(); int[] dp=new int[n+1]; dp[0]=1;
        dp[1]=s.charAt(0)=='0'?0:1;
        for(int i=2;i<=n;i++){
            int one=Integer.parseInt(s.substring(i-1,i));
            int two=Integer.parseInt(s.substring(i-2,i));
            if(one>=1) dp[i]+=dp[i-1];
            if(two>=10&&two<=26) dp[i]+=dp[i-2];}
        return dp[n];}

    // =========================================================
    // SECTION 2 — GRAPH + DP PROBLEMS
    // =========================================================
    static void section2_GraphDP() {
        printSection("2. GRAPH + DP PROBLEMS");

        // 2a. Cheapest Flights K Stops (Bellman-Ford DP)
        System.out.println("--- 2a. Cheapest Flights Within K Stops ---");
        int[][][] flightTests = {
            {{0,1,100},{1,2,100},{0,2,500}},
            {{0,1,100},{1,2,100},{0,2,500}},
            {{0,1,200},{1,2,300},{0,2,100}}
        };
        int[] ns={3,3,3},srcs={0,0,0},dsts={2,2,2},ks={1,0,1};
        for(int i=0;i<3;i++)
            System.out.printf("  flights=%s src=%d dst=%d k=%d → $%d%n",
                    Arrays.deepToString(flightTests[i]),srcs[i],dsts[i],ks[i],
                    findCheapestPrice(ns[i],flightTests[i],srcs[i],dsts[i],ks[i]));

        // 2b. Shortest Path Visiting All Nodes (BFS + Bitmask)
        System.out.println("\n--- 2b. Shortest Path Visiting All Nodes (BFS+Bitmask) ---");
        int[][][] graphs = {{{1,2,3},{0},{0},{0}},{{1},{0,2,4},{1,3,4},{2},{1,2}},{{1},{0}}};
        for(int[][] g:graphs)
            System.out.printf("  graph=%s → %d steps%n",
                    Arrays.deepToString(g), shortestPathAllNodes(g));

        // 2c. Course Schedule II (Topological Sort)
        System.out.println("\n--- 2c. Course Schedule II (Topo Sort + Cycle Detect) ---");
        int[][][] prereqs = {{{1,0}},{{1,0},{2,0},{3,1},{3,2}},{{1,0},{0,1}}};
        int[] numCourses = {2,4,2};
        for(int i=0;i<prereqs.length;i++){
            int[] order=findOrder(numCourses[i],prereqs[i]);
            System.out.printf("  n=%d prereqs=%s → %s%n",
                    numCourses[i],Arrays.deepToString(prereqs[i]),
                    order.length==0?"IMPOSSIBLE":Arrays.toString(order));}

        // 2d. Network delay time (Dijkstra)
        System.out.println("\n--- 2d. Network Delay Time (Dijkstra) ---");
        int[][][] timesArr = {{{2,1,1},{2,3,1},{3,4,1}},{{1,2,1}},{{1,2,1},{2,3,2},{1,3,4}}};
        int[] nArr={4,2,3},kArr={2,1,1};
        for(int i=0;i<3;i++)
            System.out.printf("  times=%s n=%d k=%d → %d%n",
                    Arrays.deepToString(timesArr[i]),nArr[i],kArr[i],
                    networkDelayTime(timesArr[i],nArr[i],kArr[i]));

        // 2e. Alien dictionary
        System.out.println("\n--- 2e. Alien Dictionary (Topo Sort on Chars) ---");
        String[][] wordSets = {{"wrt","wrf","er","ett","rftt"},{"z","x"},{"z","x","z"},{"abc","ab"}};
        for(String[] ws:wordSets)
            System.out.printf("  words=%s → order:\"%s\"%n",
                    Arrays.toString(ws), alienOrder(ws));
    }

    // --- Section 2 implementations ---
    static int findCheapestPrice(int n,int[][] flights,int src,int dst,int k){
        int[] dist=new int[n]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0;
        for(int p=0;p<=k;p++){int[] tmp=dist.clone();
            for(int[] f:flights){int u=f[0],v=f[1],w=f[2];
                if(dist[u]!=Integer.MAX_VALUE&&dist[u]+w<tmp[v]) tmp[v]=dist[u]+w;}
            dist=tmp;}
        return dist[dst]==Integer.MAX_VALUE?-1:dist[dst];}
    static int shortestPathAllNodes(int[][] graph){
        int n=graph.length,full=(1<<n)-1;
        Queue<int[]> q=new LinkedList<>(); Set<String> vis=new HashSet<>();
        for(int i=0;i<n;i++){int mask=1<<i;q.offer(new int[]{i,mask});vis.add(i+","+mask);}
        int steps=0;
        while(!q.isEmpty()){int sz=q.size();
            for(int s=0;s<sz;s++){int[] st=q.poll();int node=st[0],mask=st[1];
                if(mask==full) return steps;
                for(int nx:graph[node]){int nm=mask|(1<<nx);String key=nx+","+nm;
                    if(!vis.contains(key)){vis.add(key);q.offer(new int[]{nx,nm});}}}
            steps++;}
        return steps;}
    static int[] findOrder(int n,int[][] prereqs){
        List<List<Integer>> adj=new ArrayList<>();
        for(int i=0;i<n;i++) adj.add(new ArrayList<>());
        int[] in=new int[n];
        for(int[] p:prereqs){adj.get(p[1]).add(p[0]);in[p[0]]++;}
        Queue<Integer> q=new LinkedList<>();
        for(int i=0;i<n;i++) if(in[i]==0) q.offer(i);
        int[] order=new int[n]; int idx=0;
        while(!q.isEmpty()){int u=q.poll();order[idx++]=u;
            for(int v:adj.get(u)) if(--in[v]==0) q.offer(v);}
        return idx==n?order:new int[]{};}
    static int networkDelayTime(int[][] times,int n,int k){
        List<List<int[]>> adj=new ArrayList<>();
        for(int i=0;i<=n;i++) adj.add(new ArrayList<>());
        for(int[] t:times) adj.get(t[0]).add(new int[]{t[1],t[2]});
        int[] dist=new int[n+1]; Arrays.fill(dist,Integer.MAX_VALUE); dist[k]=0;
        PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
        pq.offer(new int[]{0,k});
        while(!pq.isEmpty()){int[] c=pq.poll();int d=c[0],u=c[1];
            if(d>dist[u]) continue;
            for(int[] e:adj.get(u)) if(dist[u]+e[1]<dist[e[0]]){dist[e[0]]=dist[u]+e[1];pq.offer(new int[]{dist[e[0]],e[0]});}}
        int max=0; for(int i=1;i<=n;i++){if(dist[i]==Integer.MAX_VALUE) return -1;max=Math.max(max,dist[i]);}
        return max;}
    static String alienOrder(String[] words){
        Map<Character,Set<Character>> adj=new LinkedHashMap<>();
        Map<Character,Integer> inDeg=new LinkedHashMap<>();
        for(String w:words) for(char c:w.toCharArray()){adj.putIfAbsent(c,new LinkedHashSet<>());inDeg.putIfAbsent(c,0);}
        for(int i=0;i<words.length-1;i++){String a=words[i],b=words[i+1];
            int len=Math.min(a.length(),b.length()); boolean found=false;
            for(int j=0;j<len;j++) if(a.charAt(j)!=b.charAt(j)){
                if(!adj.get(a.charAt(j)).contains(b.charAt(j))){adj.get(a.charAt(j)).add(b.charAt(j));inDeg.merge(b.charAt(j),1,Integer::sum);}
                found=true;break;}
            if(!found&&a.length()>b.length()) return "";}
        Queue<Character> q=new LinkedList<>();
        for(Map.Entry<Character,Integer> e:inDeg.entrySet()) if(e.getValue()==0) q.offer(e.getKey());
        StringBuilder sb=new StringBuilder();
        while(!q.isEmpty()){char c=q.poll();sb.append(c);
            for(char nc:adj.get(c)){inDeg.merge(nc,-1,Integer::sum);if(inDeg.get(nc)==0) q.offer(nc);}}
        return sb.length()==inDeg.size()?sb.toString():"";}

    // =========================================================
    // SECTION 3 — ADVANCED TREE PROBLEMS
    // =========================================================
    static void section3_AdvancedTrees() {
        printSection("3. ADVANCED TREE PROBLEMS");

        // 3a. Serialize / Deserialize
        System.out.println("--- 3a. Serialize & Deserialize Binary Tree ---");
        TreeNode t1 = new TreeNode(1, new TreeNode(2), new TreeNode(3, new TreeNode(4), new TreeNode(5)));
        String ser = serialize(t1);
        System.out.println("  Tree 1→{2,3→{4,5}}");
        System.out.println("  Serialized: " + ser);
        TreeNode deserialized = deserialize(ser);
        System.out.println("  Deserialized root: " + deserialized.val);
        System.out.println("  Re-serialized: " + serialize(deserialized));

        // 3b. Max path sum
        System.out.println("\n--- 3b. Binary Tree Maximum Path Sum ---");
        TreeNode[][] mpsTests = {
            {new TreeNode(-10, new TreeNode(9), new TreeNode(20, new TreeNode(15), new TreeNode(7)))},
            {new TreeNode(1, new TreeNode(2), new TreeNode(3))},
            {new TreeNode(-3)},
            {new TreeNode(1, new TreeNode(-2, new TreeNode(1, new TreeNode(-1), null), new TreeNode(3)), new TreeNode(3, new TreeNode(-2), new TreeNode(0)))}
        };
        int[] expected = {42, 6, -3, 6};
        for(int i=0;i<mpsTests.length;i++)
            System.out.printf("  maxPathSum = %d (expected %d) %s%n",
                    maxPathSum(mpsTests[i][0]), expected[i],
                    maxPathSum(mpsTests[i][0])==expected[i]?"✓":"✗");

        // 3c. LCA
        System.out.println("\n--- 3c. Lowest Common Ancestor ---");
        TreeNode lcaRoot = new TreeNode(3,
            new TreeNode(5, new TreeNode(6), new TreeNode(2, new TreeNode(7), new TreeNode(4))),
            new TreeNode(1, new TreeNode(0), new TreeNode(8)));
        TreeNode p1 = lcaRoot.left, q1 = lcaRoot.right;     // 5 and 1
        TreeNode p2 = lcaRoot.left, q2 = lcaRoot.left.right.right; // 5 and 4
        System.out.println("  LCA(5,1) = " + lca(lcaRoot, p1, q1).val);  // 3
        System.out.println("  LCA(5,4) = " + lca(lcaRoot, p2, q2).val);  // 5

        // 3d. Vertical order traversal
        System.out.println("\n--- 3d. Vertical Order Traversal ---");
        TreeNode vt1 = new TreeNode(3, new TreeNode(9), new TreeNode(20, new TreeNode(15), new TreeNode(7)));
        TreeNode vt2 = new TreeNode(1, new TreeNode(2, new TreeNode(4), new TreeNode(5)), new TreeNode(3, new TreeNode(6), new TreeNode(7)));
        System.out.println("  Tree 3→{9,20→{15,7}}: " + verticalOrder(vt1));
        System.out.println("  Tree 1→{2→{4,5},3→{6,7}}: " + verticalOrder(vt2));

        // 3e. Binary Tree cameras (greedy on tree)
        System.out.println("\n--- 3e. Binary Tree Cameras (Greedy on Tree) ---");
        TreeNode[] camTrees = {
            new TreeNode(0, new TreeNode(0, null, new TreeNode(0, null, new TreeNode(0))), null),
            new TreeNode(0, new TreeNode(0, new TreeNode(0, new TreeNode(0), new TreeNode(0)), null), null)
        };
        for(TreeNode t:camTrees)
            System.out.printf("  Min cameras = %d%n", minCameraCover(t));

        // 3f. Right side view of tree
        System.out.println("\n--- 3f. Binary Tree Right Side View ---");
        TreeNode rv1 = new TreeNode(1, new TreeNode(2, null, new TreeNode(5)), new TreeNode(3, null, new TreeNode(4)));
        System.out.println("  Right side view: " + rightSideView(rv1));
    }

    // --- Section 3 implementations ---
    static class TreeNode{int val;TreeNode left,right,parent;
        TreeNode(int v){val=v;}
        TreeNode(int v,TreeNode l,TreeNode r){val=v;left=l;right=r;}}
    static String serialize(TreeNode root){StringBuilder sb=new StringBuilder();serDFS(root,sb);return sb.toString();}
    static void serDFS(TreeNode n,StringBuilder sb){if(n==null){sb.append("null,");return;}sb.append(n.val).append(",");serDFS(n.left,sb);serDFS(n.right,sb);}
    static TreeNode deserialize(String data){Queue<String> q=new LinkedList<>(Arrays.asList(data.split(",")));return desDFS(q);}
    static TreeNode desDFS(Queue<String> q){String v=q.poll();if("null".equals(v)) return null;TreeNode n=new TreeNode(Integer.parseInt(v));n.left=desDFS(q);n.right=desDFS(q);return n;}
    static int maxPathSum(TreeNode root){int[] max={Integer.MIN_VALUE};mpsDFS(root,max);return max[0];}
    static int mpsDFS(TreeNode n,int[] max){if(n==null) return 0;
        int l=Math.max(0,mpsDFS(n.left,max)),r=Math.max(0,mpsDFS(n.right,max));
        max[0]=Math.max(max[0],n.val+l+r);return n.val+Math.max(l,r);}
    static TreeNode lca(TreeNode root,TreeNode p,TreeNode q){
        if(root==null||root==p||root==q) return root;
        TreeNode l=lca(root.left,p,q),r=lca(root.right,p,q);
        return l!=null&&r!=null?root:l!=null?l:r;}
    static List<List<Integer>> verticalOrder(TreeNode root){
        if(root==null) return new ArrayList<>();
        TreeMap<Integer,List<int[]>> colMap=new TreeMap<>();
        Queue<TreeNode> nq=new LinkedList<>(); Map<TreeNode,int[]> pos=new HashMap<>();
        pos.put(root,new int[]{0,0}); nq.offer(root);
        while(!nq.isEmpty()){TreeNode n=nq.poll();int[] p=pos.get(n);int col=p[0],row=p[1];
            colMap.computeIfAbsent(col,k->new ArrayList<>()).add(new int[]{row,n.val});
            if(n.left!=null){pos.put(n.left,new int[]{col-1,row+1});nq.offer(n.left);}
            if(n.right!=null){pos.put(n.right,new int[]{col+1,row+1});nq.offer(n.right);}}
        List<List<Integer>> res=new ArrayList<>();
        for(List<int[]> nodes:colMap.values()){nodes.sort((a,b)->a[0]!=b[0]?a[0]-b[0]:a[1]-b[1]);
            List<Integer> col=new ArrayList<>();for(int[] nd:nodes) col.add(nd[1]);res.add(col);}
        return res;}
    static int[] camState={0}; // 0=not covered,1=has camera,2=covered
    static int minCameraCover(TreeNode root){int[] cams={0};if(minCamDFS(root,cams)==0) cams[0]++;return cams[0];}
    static int minCamDFS(TreeNode n,int[] cams){if(n==null) return 2;
        int l=minCamDFS(n.left,cams),r=minCamDFS(n.right,cams);
        if(l==0||r==0){cams[0]++;return 1;}
        if(l==1||r==1) return 2;
        return 0;}
    static List<Integer> rightSideView(TreeNode root){
        List<Integer> res=new ArrayList<>();
        Queue<TreeNode> q=new LinkedList<>(); if(root!=null) q.offer(root);
        while(!q.isEmpty()){int sz=q.size();
            for(int i=0;i<sz;i++){TreeNode n=q.poll();
                if(i==sz-1) res.add(n.val);
                if(n.left!=null) q.offer(n.left);if(n.right!=null) q.offer(n.right);}}
        return res;}

    // =========================================================
    // SECTION 4 — ADVANCED STRING ALGORITHMS
    // =========================================================
    static void section4_AdvancedStrings() {
        printSection("4. ADVANCED STRING ALGORITHMS");

        // 4a. KMP
        System.out.println("--- 4a. KMP Pattern Matching ---");
        String[][] kmpTests = {{"AABABCABCABABABABC","ABABC"},{"AABAACAADAABAABA","AABA"},{"AAAA","AA"},{"abcxabcdabcdabcy","abcdabcy"}};
        for(String[] t:kmpTests)
            System.out.printf("  text=\"%s\" pattern=\"%s\" → matches at %s%n",
                    t[0],t[1],kmpSearch(t[0],t[1]));

        // 4b. Rabin-Karp
        System.out.println("\n--- 4b. Rabin-Karp Rolling Hash ---");
        String[][] rkTests = {{"GEEKS FOR GEEKS","GEEK"},{"ABCABCAB","ABC"},{"AAAA","AAA"}};
        for(String[] t:rkTests)
            System.out.printf("  text=\"%s\" pattern=\"%s\" → matches at %s%n",
                    t[0],t[1],rabinKarp(t[0],t[1]));

        // 4c. Manacher
        System.out.println("\n--- 4c. Manacher's Algorithm (Longest Palindrome O(n)) ---");
        String[] palStrs = {"babad","cbbd","abba","racecar","amanaplanacanalpanama","a","ab"};
        for(String s:palStrs)
            System.out.printf("  \"%s\" → \"%s\"%n", s, manacher(s));

        // 4d. Suffix array
        System.out.println("\n--- 4d. Suffix Array ---");
        String[] saStrs = {"banana","abcabc","mississippi","aab"};
        for(String s:saStrs){
            int[] sa=buildSuffixArray(s);
            System.out.printf("  \"%s\" → SA=%s LRS=%d%n",
                    s, Arrays.toString(sa), longestRepeatedSubstring(s));}

        // 4e. Z-algorithm
        System.out.println("\n--- 4e. Z-Algorithm (Linear Pattern Matching) ---");
        String[][] zTests = {{"aabxaayaabz","aab"},{"abcabcabc","abc"},{"aaaa","aa"}};
        for(String[] t:zTests)
            System.out.printf("  text=\"%s\" pattern=\"%s\" → %s%n",
                    t[0],t[1],zAlgorithmSearch(t[0],t[1]));

        // 4f. Palindrome pairs
        System.out.println("\n--- 4f. Palindrome Pairs ---");
        String[][] ppTests = {{"abcd","dcba","lls","s","sssll"},null};
        for(String[] ws:new String[][]{{"abcd","dcba","lls","s","sssll"},{"a",""},{"bat","tab","cat"}})
            System.out.printf("  words=%s → pairs=%s%n",
                    Arrays.toString(ws),palindromePairs(ws));
    }

    // --- Section 4 implementations ---
    static List<Integer> kmpSearch(String text,String pattern){
        int[] lps=buildLPS(pattern); List<Integer> res=new ArrayList<>();
        int i=0,j=0;
        while(i<text.length()){
            if(text.charAt(i)==pattern.charAt(j)){i++;j++;
                if(j==pattern.length()){res.add(i-j);j=lps[j-1];}}
            else{if(j!=0) j=lps[j-1]; else i++;}}
        return res;}
    static int[] buildLPS(String p){int n=p.length(),[]lps=new int[n];int len=0,i=1;
        while(i<n){if(p.charAt(i)==p.charAt(len)) lps[i++]=++len;
            else if(len!=0) len=lps[len-1]; else lps[i++]=0;}
        return lps;}
    static List<Integer> rabinKarp(String text,String pattern){
        int n=text.length(),m=pattern.length(); if(m>n) return new ArrayList<>();
        long BASE=31,MOD=1_000_000_007L,pow=1;
        for(int i=0;i<m-1;i++) pow=pow*BASE%MOD;
        long ph=0,wh=0;
        for(int i=0;i<m;i++){ph=(ph*BASE+pattern.charAt(i))%MOD;wh=(wh*BASE+text.charAt(i))%MOD;}
        List<Integer> res=new ArrayList<>();
        for(int i=0;i<=n-m;i++){
            if(wh==ph&&text.substring(i,i+m).equals(pattern)) res.add(i);
            if(i<n-m){wh=(wh-text.charAt(i)*pow%MOD+MOD)%MOD;wh=(wh*BASE+text.charAt(i+m))%MOD;}}
        return res;}
    static String manacher(String s){
        StringBuilder t=new StringBuilder("#");
        for(char c:s.toCharArray()) t.append(c).append('#');
        String T=t.toString(); int n=T.length();
        int[] p=new int[n]; int center=0,right=0;
        for(int i=0;i<n;i++){
            if(i<right) p[i]=Math.min(right-i,p[2*center-i]);
            int l=i-p[i]-1,r=i+p[i]+1;
            while(l>=0&&r<n&&T.charAt(l)==T.charAt(r)){p[i]++;l--;r++;}
            if(i+p[i]>right){center=i;right=i+p[i];}}
        int maxLen=0,mc=0;
        for(int i=0;i<n;i++) if(p[i]>maxLen){maxLen=p[i];mc=i;}
        int start=(mc-maxLen)/2;
        return s.substring(start,start+maxLen);}
    static int[] buildSuffixArray(String s){
        int n=s.length(); Integer[] sa=new Integer[n];
        for(int i=0;i<n;i++) sa[i]=i;
        Arrays.sort(sa,(a,b)->s.substring(a).compareTo(s.substring(b)));
        int[] res=new int[n]; for(int i=0;i<n;i++) res[i]=sa[i]; return res;}
    static int longestRepeatedSubstring(String s){
        int[] sa=buildSuffixArray(s),max=new int[]{0};
        for(int i=1;i<sa.length;i++) max[0]=Math.max(max[0],lcpLen(s,sa[i-1],sa[i]));
        return max[0];}
    static int lcpLen(String s,int i,int j){int l=0;while(i<s.length()&&j<s.length()&&s.charAt(i)==s.charAt(j)){l++;i++;j++;}return l;}
    static List<Integer> zAlgorithmSearch(String text,String pattern){
        String combined=pattern+"$"+text; int n=combined.length();
        int[] z=new int[n]; int l=0,r=0;
        for(int i=1;i<n;i++){if(i<r) z[i]=Math.min(r-i,z[i-l]);
            while(i+z[i]<n&&combined.charAt(z[i])==combined.charAt(i+z[i])) z[i]++;
            if(i+z[i]>r){l=i;r=i+z[i];}}
        List<Integer> res=new ArrayList<>();
        for(int i=pattern.length()+1;i<n;i++) if(z[i]==pattern.length()) res.add(i-pattern.length()-1);
        return res;}
    static List<List<Integer>> palindromePairs(String[] words){
        Map<String,Integer> idx=new HashMap<>();
        for(int i=0;i<words.length;i++) idx.put(words[i],i);
        List<List<Integer>> res=new ArrayList<>();
        for(int i=0;i<words.length;i++){String w=words[i];
            for(int j=0;j<=w.length();j++){
                String l=w.substring(0,j),r=w.substring(j);
                if(isPalin(l)){String rev=new StringBuilder(r).reverse().toString();Integer k=idx.get(rev);if(k!=null&&k!=i) res.add(Arrays.asList(k,i));}
                if(j>0&&isPalin(r)){String rev=new StringBuilder(l).reverse().toString();Integer k=idx.get(rev);if(k!=null&&k!=i) res.add(Arrays.asList(i,k));}}}
        return res;}
    static boolean isPalin(String s){int l=0,r=s.length()-1;while(l<r) if(s.charAt(l++)!=s.charAt(r--)) return false;return true;}

    // =========================================================
    // SECTION 5 — SLIDING WINDOW + HASHING HYBRIDS
    // =========================================================
    static void section5_SlidingWindowHashing() {
        printSection("5. SLIDING WINDOW + HASHING HYBRIDS");

        // 5a. Subarray sum = k
        System.out.println("--- 5a. Subarray Sum Equals K (Prefix + HashMap) ---");
        int[][][] skTests = {{{1,1,1},2},{{1,2,3},3},{{1,-1,0},0},{{-1,-1,1},0}};
        for(int[][] t:skTests)
            System.out.printf("  nums=%s k=%d → %d subarrays%n",
                    Arrays.toString(t[0]),t[1][0],subarraySumK(t[0],t[1][0]));

        // 5b. Longest subarray divisible by k
        System.out.println("\n--- 5b. Longest Subarray Sum Divisible by K ---");
        int[][][] divTests = {{{23,2,4,6,7},6},{{23,2,6,4,7},6},{{1,2,3},3}};
        for(int[][] t:divTests)
            System.out.printf("  nums=%s k=%d → longest=%d%n",
                    Arrays.toString(t[0]),t[1][0],longestSubarrayDivK(t[0],t[1][0]));

        // 5c. K distinct integers
        System.out.println("\n--- 5c. Count Subarrays with Exactly K Distinct ---");
        int[][][] kdTests = {{{1,2,1,2,3},2},{{1,2,1,3,4},3},{{2,1,2,1,6,0,4,5,6},3}};
        for(int[][] t:kdTests)
            System.out.printf("  nums=%s k=%d → %d subarrays%n",
                    Arrays.toString(t[0]),t[1][0],subarraysKDistinct(t[0],t[1][0]));

        // 5d. Substring concatenation
        System.out.println("\n--- 5d. Substring with Concatenation of All Words ---");
        System.out.println("  s=\"barfoothefoobarman\" words=[foo,bar] → "
                + findSubstring("barfoothefoobarman", new String[]{"foo","bar"}));
        System.out.println("  s=\"wordgoodgoodgoodbestword\" words=[word,good,best,word] → "
                + findSubstring("wordgoodgoodgoodbestword", new String[]{"word","good","best","word"}));
        System.out.println("  s=\"barfoofoobarthefoobarman\" words=[bar,foo,the] → "
                + findSubstring("barfoofoobarthefoobarman", new String[]{"bar","foo","the"}));

        // 5e. Max points on line
        System.out.println("\n--- 5e. Max Points on a Line (GCD + HashMap) ---");
        int[][][] pointTests = {{{1,1},{2,2},{3,3}},{{1,1},{3,2},{5,3},{4,1},{2,3},{1,4}},{{0,0},{1,1},{0,0}}};
        for(int[][] pts:pointTests)
            System.out.printf("  points=%s → max on line=%d%n",
                    Arrays.deepToString(pts), maxPoints(pts));

        // 5f. Continuous subarray sum (multiple of k)
        System.out.println("\n--- 5f. Continuous Subarray Sum (Multiple of K) ---");
        int[][][] csTests = {{{23,2,4,6,7},6},{{23,2,6,4,7},13},{{0,0},1}};
        for(int[][] t:csTests)
            System.out.printf("  nums=%s k=%d → %s%n",
                    Arrays.toString(t[0]),t[1][0],checkSubarraySum(t[0],t[1][0]));
    }

    // --- Section 5 implementations ---
    static int subarraySumK(int[] nums,int k){
        Map<Integer,Integer> pre=new HashMap<>();pre.put(0,1);
        int sum=0,count=0;
        for(int n:nums){sum+=n;count+=pre.getOrDefault(sum-k,0);pre.merge(sum,1,Integer::sum);}
        return count;}
    static int longestSubarrayDivK(int[] nums,int k){
        Map<Integer,Integer> rem=new HashMap<>();rem.put(0,-1);
        int sum=0,max=0;
        for(int i=0;i<nums.length;i++){sum+=nums[i];int r=((sum%k)+k)%k;
            if(rem.containsKey(r)) max=Math.max(max,i-rem.get(r));
            else rem.put(r,i);}
        return max;}
    static int subarraysKDistinct(int[] nums,int k){return atMostK(nums,k)-atMostK(nums,k-1);}
    static int atMostK(int[] nums,int k){
        Map<Integer,Integer> freq=new HashMap<>();int l=0,cnt=0;
        for(int r=0;r<nums.length;r++){freq.merge(nums[r],1,Integer::sum);
            while(freq.size()>k){int lv=nums[l++];freq.merge(lv,-1,Integer::sum);if(freq.get(lv)==0) freq.remove(lv);}
            cnt+=r-l+1;}
        return cnt;}
    static List<Integer> findSubstring(String s,String[] words){
        if(s.isEmpty()||words.length==0) return new ArrayList<>();
        int wl=words[0].length(),nw=words.length; List<Integer> res=new ArrayList<>();
        Map<String,Integer> wc=new HashMap<>(); for(String w:words) wc.merge(w,1,Integer::sum);
        for(int i=0;i<wl;i++){Map<String,Integer> seen=new HashMap<>();int l=i,matched=0;
            for(int r=i;r+wl<=s.length();r+=wl){String w=s.substring(r,r+wl);
                if(wc.containsKey(w)){seen.merge(w,1,Integer::sum);matched++;
                    while(seen.get(w)>wc.get(w)){String lw=s.substring(l,l+wl);seen.merge(lw,-1,Integer::sum);matched--;l+=wl;}
                    if(matched==nw) res.add(l);}
                else{seen.clear();matched=0;l=r+wl;}}}
        return res;}
    static int maxPoints(int[][] pts){
        int n=pts.length,max=1;
        for(int i=0;i<n;i++){Map<String,Integer> slope=new HashMap<>();int dup=1;
            for(int j=i+1;j<n;j++){int dx=pts[j][0]-pts[i][0],dy=pts[j][1]-pts[i][1];
                if(dx==0&&dy==0){dup++;continue;}
                int g=gcd(Math.abs(dx),Math.abs(dy));dx/=g;dy/=g;
                if(dx<0){dx=-dx;dy=-dy;}else if(dx==0) dy=Math.abs(dy);
                String key=dx+"/"+dy;slope.merge(key,1,Integer::sum);
                max=Math.max(max,slope.get(key)+dup);}}
        return max;}
    static int gcd(int a,int b){return b==0?a:gcd(b,a%b);}
    static boolean checkSubarraySum(int[] nums,int k){
        Map<Integer,Integer> rem=new HashMap<>();rem.put(0,-1);int sum=0;
        for(int i=0;i<nums.length;i++){sum+=nums[i];int r=k==0?sum:sum%k;
            if(rem.containsKey(r)){if(i-rem.get(r)>=2) return true;}
            else rem.put(r,i);}
        return false;}

    // =========================================================
    // SECTION 6 — REAL INTERVIEW-STYLE PROBLEMS
    // =========================================================
    static void section6_RealInterviewProblems() {
        printSection("6. REAL INTERVIEW-STYLE PROBLEMS");

        // 6a. LRU Cache
        System.out.println("--- 6a. LRU Cache O(1) get/put ---");
        LRUCache lru=new LRUCache(2);
        lru.put(1,1);lru.put(2,2);
        System.out.println("  put(1,1) put(2,2)");
        System.out.println("  get(1)=" + lru.get(1));       // 1
        lru.put(3,3);
        System.out.println("  put(3,3) → evicts key 2");
        System.out.println("  get(2)=" + lru.get(2));        // -1
        System.out.println("  get(3)=" + lru.get(3));        // 3
        lru.put(4,4);
        System.out.println("  put(4,4) → evicts key 1");
        System.out.println("  get(1)=" + lru.get(1));        // -1
        System.out.println("  get(4)=" + lru.get(4));        // 4

        // 6b. Median from data stream
        System.out.println("\n--- 6b. Median Finder (Two Heaps) ---");
        MedianFinder mf=new MedianFinder();
        int[] stream={5,15,1,3,8,7,9,10,6,11};
        System.out.print("  Stream: ");
        for(int n:stream){mf.addNum(n);System.out.printf("[add %2d → median=%.1f] ",n,mf.findMedian());}
        System.out.println();

        // 6c. Skyline problem
        System.out.println("\n--- 6c. Skyline Problem (Sweep Line + TreeMap) ---");
        int[][] buildings = {{2,9,10},{3,7,15},{5,12,12},{15,20,10},{19,24,8}};
        System.out.println("  Buildings: " + Arrays.deepToString(buildings));
        List<int[]> skyline = getSkyline(buildings);
        System.out.print("  Skyline: ");
        for(int[] pt:skyline) System.out.print(Arrays.toString(pt)+" ");
        System.out.println();

        // 6d. Trap rain water II (3D)
        System.out.println("\n--- 6d. Trapping Rain Water II (3D BFS+Heap) ---");
        int[][][] water3D = {
            {{1,4,3,1,3,2},{3,2,1,3,2,4},{2,3,3,2,3,1}},
            {{3,3,3,3,3},{3,2,2,2,3},{3,2,1,2,3},{3,2,2,2,3},{3,3,3,3,3}}
        };
        for(int[][] grid:water3D)
            System.out.printf("  grid=%s → water=%d%n",
                    Arrays.deepToString(grid), trapRainWaterII(grid));

        // 6e. K closest points to origin
        System.out.println("\n--- 6e. K Closest Points to Origin ---");
        int[][] pts1 = {{1,3},{-2,2}};
        int[][] pts2 = {{3,3},{5,-1},{-2,4}};
        System.out.println("  " + Arrays.deepToString(pts1) + " k=1 → "
                + Arrays.deepToString(kClosestPoints(pts1, 1)));
        System.out.println("  " + Arrays.deepToString(pts2) + " k=2 → "
                + Arrays.deepToString(kClosestPoints(pts2, 2)));

        // 6f. Task scheduler
        System.out.println("\n--- 6f. Task Scheduler (Greedy + Heap) ---");
        char[][] taskSets = {{'A','A','A','B','B','B'},{'A','A','A','A','A','A','B','C','D','E','F','G'},{'A','A','A','B','B','B','C','C','C','D','D','E'}};
        int[] ns={2,2,2};
        for(int i=0;i<taskSets.length;i++)
            System.out.printf("  tasks=%s n=%d → %d time%n",
                    new String(taskSets[i]),ns[i],leastInterval(taskSets[i],ns[i]));
    }

    // --- Section 6 implementations ---
    static class LRUCache{int cap;Map<Integer,DNode> map;DNode head,tail;
        LRUCache(int c){cap=c;map=new HashMap<>();head=new DNode(0,0);tail=new DNode(0,0);head.next=tail;tail.prev=head;}
        int get(int k){if(!map.containsKey(k)) return -1;DNode n=map.get(k);rem(n);addFront(n);return n.v;}
        void put(int k,int v){if(map.containsKey(k)){DNode n=map.get(k);n.v=v;rem(n);addFront(n);}
            else{if(map.size()==cap){DNode lru=tail.prev;rem(lru);map.remove(lru.k);}
                DNode n=new DNode(k,v);addFront(n);map.put(k,n);}}
        void rem(DNode n){n.prev.next=n.next;n.next.prev=n.prev;}
        void addFront(DNode n){n.next=head.next;n.prev=head;head.next.prev=n;head.next=n;}
        static class DNode{int k,v;DNode prev,next;DNode(int k,int v){this.k=k;this.v=v;}}}
    static class MedianFinder{PriorityQueue<Integer> lo=new PriorityQueue<>(Collections.reverseOrder()),hi=new PriorityQueue<>();
        void addNum(int n){lo.offer(n);hi.offer(lo.poll());if(lo.size()<hi.size()) lo.offer(hi.poll());}
        double findMedian(){return lo.size()>hi.size()?lo.peek():(lo.peek()+hi.peek())/2.0;}}
    static List<int[]> getSkyline(int[][] buildings){
        List<int[]> events=new ArrayList<>();
        for(int[] b:buildings){events.add(new int[]{b[0],-b[2]});events.add(new int[]{b[1],b[2]});}
        events.sort((a,b)->a[0]!=b[0]?a[0]-b[0]:a[1]-b[1]);
        TreeMap<Integer,Integer> active=new TreeMap<>();active.put(0,1);
        List<int[]> res=new ArrayList<>();int prev=0;
        for(int[] e:events){int x=e[0],h=Math.abs(e[1]);
            if(e[1]<0) active.merge(h,1,Integer::sum);
            else{active.merge(h,-1,Integer::sum);if(active.get(h)==0) active.remove(h);}
            int curr=active.lastKey();if(curr!=prev){res.add(new int[]{x,curr});prev=curr;}}
        return res;}
    static int trapRainWaterII(int[][] h){
        if(h.length<3||h[0].length<3) return 0;
        int m=h.length,n=h[0].length; boolean[][] vis=new boolean[m][n];
        PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[0]));
        for(int i=0;i<m;i++) for(int j=0;j<n;j++)
            if(i==0||i==m-1||j==0||j==n-1){pq.offer(new int[]{h[i][j],i,j});vis[i][j]=true;}
        int water=0; int[][] dirs={{0,1},{0,-1},{1,0},{-1,0}};
        while(!pq.isEmpty()){int[] c=pq.poll();int ht=c[0],r=c[1],cc=c[2];
            for(int[] d:dirs){int nr=r+d[0],nc=cc+d[1];
                if(nr<0||nr>=m||nc<0||nc>=n||vis[nr][nc]) continue;
                vis[nr][nc]=true;water+=Math.max(0,ht-h[nr][nc]);
                pq.offer(new int[]{Math.max(ht,h[nr][nc]),nr,nc});}}
        return water;}
    static int[][] kClosestPoints(int[][] pts,int k){
        PriorityQueue<int[]> pq=new PriorityQueue<>((a,b)->
            (b[0]*b[0]+b[1]*b[1])-(a[0]*a[0]+a[1]*a[1]));
        for(int[] p:pts){pq.offer(p);if(pq.size()>k) pq.poll();}
        int[][] res=new int[k][2]; for(int i=k-1;i>=0;i--) res[i]=pq.poll();
        return res;}
    static int leastInterval(char[] tasks,int n){
        int[] freq=new int[26]; for(char t:tasks) freq[t-'A']++;
        Arrays.sort(freq); int maxF=freq[25],cnt=0;
        for(int f:freq) if(f==maxF) cnt++;
        return Math.max(tasks.length,(maxF-1)*(n+1)+cnt);}

    // =========================================================
    // SECTION 7 — OPTIMIZATION THINKING
    // =========================================================
    static void section7_OptimizationThinking() {
        printSection("7. OPTIMIZATION THINKING");

        // 7a. Next greater element
        System.out.println("--- 7a. Next Greater Element (Monotonic Stack) ---");
        int[][] ngeArrs = {{2,1,2,4,3},{1,3,4,2,5},{5,4,3,2,1},{1,2,3,4,5}};
        for(int[] arr:ngeArrs)
            System.out.printf("  %s → %s%n",
                    Arrays.toString(arr),Arrays.toString(nextGreater(arr)));

        System.out.println("\n  Circular next greater:");
        int[][] circArrs = {{1,2,1},{3,1,2},{5,4,3,2,1}};
        for(int[] arr:circArrs)
            System.out.printf("  %s → %s%n",
                    Arrays.toString(arr),Arrays.toString(nextGreaterCircular(arr)));

        // 7b. Largest rectangle in histogram
        System.out.println("\n--- 7b. Largest Rectangle in Histogram ---");
        int[][] histArrs = {{2,1,5,6,2,3},{2,4},{1,1},{2,1,2},{6,2,5,4,5,1,6}};
        for(int[] arr:histArrs)
            System.out.printf("  %s → %d%n", Arrays.toString(arr), largestRect(arr));

        // 7c. Maximal rectangle in matrix
        System.out.println("\n--- 7c. Maximal Rectangle (Histogram DP) ---");
        char[][] matrices = {
            {'1','0','1','0','0'},
            {'1','0','1','1','1'},
            {'1','1','1','1','1'},
            {'1','0','0','1','0'}
        };
        char[][][] mats = {{{'1','0','1','0','0'},{'1','0','1','1','1'},{'1','1','1','1','1'},{'1','0','0','1','0'}},
                           {{'0'}},{{'1'}},{{'1','1'},{'1','1'}}};
        for(char[][] mat:mats)
            System.out.printf("  %s → %d%n", Arrays.deepToString(mat), maximalRectangle(mat));

        // 7d. Jump game III (BFS)
        System.out.println("\n--- 7d. Jump Game III (BFS on Index Graph) ---");
        int[][][] jg3 = {{{4,2,3,0,3,1,2},5},{{4,2,3,0,3,1,2},0},{{3,0,2,1,2},2}};
        for(int[][] t:jg3)
            System.out.printf("  arr=%s start=%d → canReach=%s%n",
                    Arrays.toString(t[0]),t[1][0],canReach(t[0],t[1][0]));

        // 7e. Candy problem (two-pass greedy)
        System.out.println("\n--- 7e. Candy Distribution (Two-Pass Greedy) ---");
        int[][] candyRatings = {{1,0,2},{1,2,2},{1,3,2,2,1},{1,2,87,87,87,2,1}};
        for(int[] r:candyRatings)
            System.out.printf("  ratings=%s → min candies=%d%n", Arrays.toString(r), candy(r));

        // 7f. Complexity comparison
        System.out.println("\n--- 7f. Optimization Impact Summary ---");
        System.out.printf("  %-35s %-12s %-12s%n","Problem","Naive","Optimized");
        System.out.printf("  %-35s %-12s %-12s%n","Next Greater Element","O(n²)","O(n) monostack");
        System.out.printf("  %-35s %-12s %-12s%n","Largest Rectangle","O(n²)","O(n) monostack");
        System.out.printf("  %-35s %-12s %-12s%n","Subarray Sum=k","O(n²)","O(n) prefix+hash");
        System.out.printf("  %-35s %-12s %-12s%n","Palindrome Substring","O(n²)","O(n) Manacher");
        System.out.printf("  %-35s %-12s %-12s%n","Pattern Match","O(nm)","O(n+m) KMP");
        System.out.printf("  %-35s %-12s %-12s%n","LRU Cache get/put","O(n)","O(1) hash+DLL");
        System.out.printf("  %-35s %-12s %-12s%n","Median Stream","O(n)","O(log n) 2-heap");
        System.out.printf("  %-35s %-12s %-12s%n","Word Search k words","O(k×mn×4^L)","O(mn×4^L) Trie");
        System.out.printf("  %-35s %-12s %-12s%n","Islands online queries","O(n²)","O(nα(n)) UF");
    }

    // --- Section 7 implementations ---
    static int[] nextGreater(int[] nums){
        int n=nums.length; int[] res=new int[n]; Arrays.fill(res,-1);
        Deque<Integer> st=new ArrayDeque<>();
        for(int i=0;i<n;i++){while(!st.isEmpty()&&nums[st.peek()]<nums[i]) res[st.pop()]=nums[i];st.push(i);}
        return res;}
    static int[] nextGreaterCircular(int[] nums){
        int n=nums.length; int[] res=new int[n]; Arrays.fill(res,-1);
        Deque<Integer> st=new ArrayDeque<>();
        for(int i=0;i<2*n;i++){while(!st.isEmpty()&&nums[st.peek()]<nums[i%n]) res[st.pop()]=nums[i%n];if(i<n) st.push(i);}
        return res;}
    static int largestRect(int[] heights){
        Deque<Integer> st=new ArrayDeque<>();int max=0;
        for(int i=0;i<=heights.length;i++){int h=i==heights.length?0:heights[i];
            while(!st.isEmpty()&&h<heights[st.peek()]){int ht=heights[st.pop()];int w=st.isEmpty()?i:i-st.peek()-1;max=Math.max(max,ht*w);}
            st.push(i);}
        return max;}
    static int maximalRectangle(char[][] mat){
        int m=mat.length,n=mat[0].length,max=0; int[] h=new int[n];
        for(int i=0;i<m;i++){for(int j=0;j<n;j++) h[j]=mat[i][j]=='1'?h[j]+1:0;
            max=Math.max(max,largestRect(h));}
        return max;}
    static boolean canReach(int[] arr,int start){
        int n=arr.length; boolean[] vis=new boolean[n]; Queue<Integer> q=new LinkedList<>();q.offer(start);
        while(!q.isEmpty()){int i=q.poll();if(arr[i]==0) return true;if(vis[i]) continue;vis[i]=true;
            int l=i-arr[i],r=i+arr[i];if(l>=0&&!vis[l]) q.offer(l);if(r<n&&!vis[r]) q.offer(r);}
        return false;}
    static int candy(int[] ratings){
        int n=ratings.length; int[] c=new int[n]; Arrays.fill(c,1);
        for(int i=1;i<n;i++) if(ratings[i]>ratings[i-1]) c[i]=c[i-1]+1;
        for(int i=n-2;i>=0;i--) if(ratings[i]>ratings[i+1]) c[i]=Math.max(c[i],c[i+1]+1);
        return Arrays.stream(c).sum();}

    // LPS helper (needed by KMP)
    static int[] buildLPS(String p){
        int n=p.length(); int[] lps=new int[n]; int len=0,i=1;
        while(i<n){if(p.charAt(i)==p.charAt(len)) lps[i++]=++len;
            else if(len!=0) len=lps[len-1]; else lps[i++]=0;}
        return lps;}

    // =========================================================
    // UTILITIES
    // =========================================================
    static void printBanner(String title){
        System.out.println("\n"+"=".repeat(66));
        System.out.println("  "+title);
        System.out.println("=".repeat(66));}
    static void printSection(String title){
        System.out.println("\n"+"-".repeat(66));
        System.out.println("  SECTION "+title);
        System.out.println("-".repeat(66));}
}
