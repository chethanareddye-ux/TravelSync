import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * TravelSync — Intelligent Travel Planning & Information Platform
 * Educational DSA3 project demonstrating hand-built advanced algorithms.
 * Uses arrays and custom logic for the core algorithms; Java library utilities
 * are used only for basic console input/output and threading.
 */
public class Main {
    static final Scanner SC = new Scanner(System.in);

    static final String[] DESTINATIONS = {
        "Vijayawada", "Visakhapatnam", "Tirupati", "Hyderabad",
        "Chennai", "Bengaluru", "Araku Valley", "Amaravati"
    };
    static final String[] HOTELS = {
        "River View Residency Vijayawada",
        "Beach Breeze Hotel Visakhapatnam",
        "Temple Stay Inn Tirupati",
        "City Comfort Hyderabad",
        "Marina Bay Lodge Chennai",
        "Garden City Suites Bengaluru",
        "Hilltop Retreat Araku Valley",
        "Capital Grand Amaravati"
    };
    static final String[] KEYWORDS = {
        "river", "beach", "temple", "city", "marina", "garden", "hilltop", "capital"
    };
    static final String[] ATTRACTIONS = {
        "Kanaka Durga Temple", "RK Beach", "Sri Venkateswara Temple",
        "Charminar", "Marina Beach", "Lalbagh", "Borra Caves", "Undavalli Caves"
    };

    // Distances between eight destinations; 0 means no direct road in this demo.
    static final int[][] DIST = {
        {0, 350, 410, 275, 430, 520, 300, 40},
        {350, 0, 600, 620, 800, 850, 115, 390},
        {410, 600, 0, 550, 135, 250, 700, 400},
        {275, 620, 550, 0, 630, 570, 650, 280},
        {430, 800, 135, 630, 0, 350, 850, 450},
        {520, 850, 250, 570, 350, 0, 900, 540},
        {300, 115, 700, 650, 850, 900, 0, 330},
        {40, 390, 400, 280, 450, 540, 330, 0}
    };

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" TravelSync — Intelligent Travel Planning Platform");
        System.out.println(" DSA3 Advanced Algorithms Demonstration (Java)");
        System.out.println("==================================================");
        while (true) {
            System.out.println("\nMAIN MENU");
            System.out.println("1. Search destinations/hotels (KMP + Z + Rabin-Karp)");
            System.out.println("2. Fuzzy destination matching (edit distance)");
            System.out.println("3. Show suffix array and LCP");
            System.out.println("4. Plan a route (shortest path)");
            System.out.println("5. Transportation capacity / max-flow");
            System.out.println("6. Bipartite matching (guides to tours)");
            System.out.println("7. Approximate multi-stop trip (TSP bitmask DP)");
            System.out.println("8. Vertex-cover approximation");
            System.out.println("9. Randomised algorithms (quicksort + Miller-Rabin)");
            System.out.println("10. Streaming booking sample (reservoir sampling)");
            System.out.println("11. Parallel reduction demo");
            System.out.println("12. View all sample destinations");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = SC.nextLine().trim();
            try {
                switch (choice) {
                    case "1": searchMenu(); break;
                    case "2": fuzzySearch(); break;
                    case "3": suffixDemo(); break;
                    case "4": routeMenu(); break;
                    case "5": flowDemo(); break;
                    case "6": matchingDemo(); break;
                    case "7": tspDemo(); break;
                    case "8": vertexCoverDemo(); break;
                    case "9": randomizedDemo(); break;
                    case "10": reservoirDemo(); break;
                    case "11": parallelDemo(); break;
                    case "12": showDestinations(); break;
                    case "0": System.out.println("Thank you for using TravelSync!"); return;
                    default: System.out.println("Please choose a menu number from 0 to 12.");
                }
            } catch (Exception ex) {
                System.out.println("Could not complete that operation: " + ex.getMessage());
                System.out.println("Please try again with a valid input.");
            }
        }
    }

    static void showDestinations() {
        System.out.println("\nSAMPLE DESTINATIONS");
        for (int i = 0; i < DESTINATIONS.length; i++)
            System.out.println((i + 1) + ". " + DESTINATIONS[i] + " — " + HOTELS[i] + " — " + ATTRACTIONS[i]);
        System.out.println("Note: these are demonstration records, not live travel inventory.");
    }

    static void searchMenu() {
        System.out.print("Enter a keyword (e.g. temple, beach, Vijayawada): ");
        String query = SC.nextLine().trim();
        if (query.isEmpty()) { System.out.println("Search term cannot be empty."); return; }
        StringBuilder corpus = new StringBuilder();
        for (int i = 0; i < HOTELS.length; i++)
            corpus.append(HOTELS[i]).append(" | ").append(DESTINATIONS[i]).append(" | ").append(ATTRACTIONS[i]).append("\n");
        String text = corpus.toString().toLowerCase();
        String q = query.toLowerCase();
        System.out.println("\nKMP match positions: " + kmpSearch(text, q));
        System.out.println("Z-function match positions: " + zSearch(text, q));
        System.out.println("Rabin-Karp match positions: " + rabinKarp(text, q));
        System.out.println("\nMatching records:");
        boolean found = false;
        for (int i = 0; i < HOTELS.length; i++) {
            String record = HOTELS[i] + " | " + DESTINATIONS[i] + " | " + ATTRACTIONS[i];
            if (kmpSearch(record.toLowerCase(), q).length() > 2) {
                System.out.println("- " + record); found = true;
            }
        }
        if (!found) System.out.println("No exact substring match. Try option 2 for fuzzy matching.");
        System.out.println("Complexity: KMP O(n+m), Z O(n+m), Rabin-Karp average O(n+m).");
    }

    static String kmpSearch(String text, String pattern) {
        if (pattern.length() == 0) return "[]";
        int[] lps = new int[pattern.length()];
        for (int i = 1, len = 0; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(len)) lps[i++] = ++len;
            else if (len != 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        StringBuilder out = new StringBuilder("[");
        int i = 0, j = 0; boolean first = true;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) { i++; j++; }
            if (j == pattern.length()) {
                if (!first) out.append(", "); out.append(i - j); first = false; j = lps[j - 1];
            } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) j = lps[j - 1]; else i++;
            }
        }
        return out.append("]").toString();
    }

    static String zSearch(String text, String pattern) {
        String combined = pattern + "\u0001" + text;
        int n = combined.length(), left = 0, right = 0;
        int[] z = new int[n];
        StringBuilder out = new StringBuilder("["); boolean first = true;
        for (int i = 1; i < n; i++) {
            if (i <= right) z[i] = Math.min(right - i + 1, z[i - left]);
            while (i + z[i] < n && combined.charAt(z[i]) == combined.charAt(i + z[i])) z[i]++;
            if (i + z[i] - 1 > right) { left = i; right = i + z[i] - 1; }
            if (z[i] == pattern.length() && i > pattern.length()) {
                if (!first) out.append(", "); out.append(i - pattern.length() - 1); first = false;
            }
        }
        return out.append("]").toString();
    }

    static String rabinKarp(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return "[]";
        long mod1 = 1_000_000_007L, mod2 = 1_000_000_009L, base = 257;
        long ph1=0, th1=0, ph2=0, th2=0, pow1=1, pow2=1;
        for (int i=0;i<m;i++) {
            ph1=(ph1*base+pattern.charAt(i))%mod1; th1=(th1*base+text.charAt(i))%mod1;
            ph2=(ph2*base+pattern.charAt(i))%mod2; th2=(th2*base+text.charAt(i))%mod2;
            if(i<m-1){pow1=pow1*base%mod1;pow2=pow2*base%mod2;}
        }
        StringBuilder out=new StringBuilder("["); boolean first=true;
        for(int i=0;;i++){
            if(ph1==th1 && ph2==th2 && text.regionMatches(i,pattern,0,m)){
                if(!first)out.append(", ");out.append(i);first=false;
            }
            if(i+m>=n)break;
            th1=(th1-(text.charAt(i)*pow1)%mod1+mod1)%mod1;
            th1=(th1*base+text.charAt(i+m))%mod1;
            th2=(th2-(text.charAt(i)*pow2)%mod2+mod2)%mod2;
            th2=(th2*base+text.charAt(i+m))%mod2;
        }
        return out.append("]").toString();
    }

    static void fuzzySearch() {
        System.out.print("Enter destination spelling: ");
        String q = SC.nextLine().trim();
        if (q.isEmpty()) { System.out.println("Input cannot be empty."); return; }
        int best = Integer.MAX_VALUE;
        for (String d : DESTINATIONS) best = Math.min(best, editDistance(q.toLowerCase(), d.toLowerCase()));
        System.out.println("Closest destination suggestions:");
        for (String d : DESTINATIONS) {
            int dist = editDistance(q.toLowerCase(), d.toLowerCase());
            if (dist <= best + 2) System.out.println("- " + d + " (edit distance " + dist + ")");
        }
        System.out.println("Levenshtein DP time: O(mn); space: O(mn) in this teaching version.");
    }

    static int editDistance(String a, String b) {
        int[][] dp = new int[a.length()+1][b.length()+1];
        for(int i=0;i<=a.length();i++)dp[i][0]=i;
        for(int j=0;j<=b.length();j++)dp[0][j]=j;
        for(int i=1;i<=a.length();i++)for(int j=1;j<=b.length();j++){
            int cost=a.charAt(i-1)==b.charAt(j-1)?0:1;
            dp[i][j]=Math.min(Math.min(dp[i-1][j]+1,dp[i][j-1]+1),dp[i-1][j-1]+cost);
        }
        return dp[a.length()][b.length()];
    }

    static void suffixDemo() {
        String s = "travelplan";
        int n=s.length(); Integer[] order=new Integer[n];
        for(int i=0;i<n;i++)order[i]=i;
        // Simple suffix sorting for demonstration; O(n^2 log n), not SA-IS.
        for(int i=1;i<n;i++){int key=order[i],j=i-1;while(j>=0 && s.substring(order[j]).compareTo(s.substring(key))>0){order[j+1]=order[j];j--;}order[j+1]=key;}
        System.out.println("\nSuffix array for \""+s+"\":");
        for(int i=0;i<n;i++)System.out.println(order[i]+" : "+s.substring(order[i]));
        System.out.println("LCP array (Kasai-style comparison, simple teaching implementation):");
        for(int i=0;i<n;i++){
            if(i==0){System.out.println(order[i]+" -> 0");continue;}
            int a=order[i-1],b=order[i],k=0;
            while(a+k<n && b+k<n && s.charAt(a+k)==s.charAt(b+k))k++;
            System.out.println(order[i]+" -> "+k);
        }
        System.out.println("Note: this compact demo sorts suffixes directly; production SA-IS is O(n).");
    }

    static void routeMenu() {
        showDestinations();
        int s=readInt("Start destination number (1-8): ")-1;
        int t=readInt("End destination number (1-8): ")-1;
        if(s<0||s>=8||t<0||t>=8){System.out.println("Invalid destination.");return;}
        int n=8; int[] dist=new int[n],prev=new int[n]; boolean[] used=new boolean[n];
        for(int i=0;i<n;i++){dist[i]=Integer.MAX_VALUE/4;prev[i]=-1;} dist[s]=0;
        // Dijkstra for positive road distances.
        for(int z=0;z<n;z++){
            int u=-1;
            for(int i=0;i<n;i++)if(!used[i]&&(u==-1||dist[i]<dist[u]))u=i;
            if(u==-1||dist[u]>=Integer.MAX_VALUE/4)break;
            used[u]=true;
            for(int v=0;v<n;v++)if(DIST[u][v]>0&&dist[v]>dist[u]+DIST[u][v]){
                dist[v]=dist[u]+DIST[u][v];prev[v]=u;
            }
        }
        if(dist[t]>=Integer.MAX_VALUE/4){System.out.println("No route found.");return;}
        int[] path=new int[n+1];int len=0;for(int x=t;x!=-1;x=prev[x])path[len++]=x;
        System.out.print("Suggested route: ");
        for(int i=len-1;i>=0;i--){System.out.print(DESTINATIONS[path[i]]);if(i>0)System.out.print(" -> ");}
        System.out.println("\nEstimated demo distance: "+dist[t]+" km");
        System.out.println("Dijkstra complexity: O(V^2) in this adjacency-matrix implementation.");
    }

    static void flowDemo() {
        int[][] cap = {
            {0,8,5,0,0,0},
            {0,0,3,4,0,0},
            {0,0,0,2,6,0},
            {0,0,0,0,3,7},
            {0,0,0,0,0,9},
            {0,0,0,0,0,0}
        };
        int max = edmondsKarp(cap,0,5);
        System.out.println("Demo transport network: source=Travel Hub, sink=Destination Hub");
        System.out.println("Maximum flow / modelled capacity: "+max+" capacity units.");
        System.out.println("Edmonds-Karp complexity: O(VE^2); capacities are illustrative, not live data.");
        System.out.println("A minimum cut identifies the bottleneck separating source and destination.");
    }

    static int edmondsKarp(int[][] capacity,int source,int sink) {
        int n=capacity.length;int[][] residual=new int[n][n];
        for(int i=0;i<n;i++)for(int j=0;j<n;j++)residual[i][j]=capacity[i][j];
        int total=0;
        while(true){
            int[] parent=new int[n];for(int i=0;i<n;i++)parent[i]=-1;
            int[] q=new int[n];int head=0,tail=0;q[tail++]=source;parent[source]=source;
            while(head<tail&&parent[sink]==-1){
                int u=q[head++];
                for(int v=0;v<n;v++)if(parent[v]==-1&&residual[u][v]>0){parent[v]=u;q[tail++]=v;}
            }
            if(parent[sink]==-1)break;
            int flow=Integer.MAX_VALUE;
            for(int v=sink;v!=source;v=parent[v])flow=Math.min(flow,residual[parent[v]][v]);
            for(int v=sink;v!=source;v=parent[v]){int u=parent[v];residual[u][v]-=flow;residual[v][u]+=flow;}
            total+=flow;
        }
        return total;
    }

    static void matchingDemo() {
        // Guides: 0..2; tours: 0..2. 1 means guide can serve the tour.
        int[][] can = {{1,1,0},{0,1,1},{1,0,1}};
        int[] tourGuide={-1,-1,-1};int matched=0;
        for(int g=0;g<3;g++){boolean[] seen=new boolean[3];if(augment(g,can,seen,tourGuide))matched++;}
        System.out.println("Guide-to-tour maximum matching: "+matched+" of 3 tours assigned.");
        for(int t=0;t<3;t++)System.out.println("Tour "+(t+1)+" <- "+(tourGuide[t]<0?"unassigned":"Guide "+(tourGuide[t]+1)));
        System.out.println("Bipartite matching can be reduced to max-flow.");
    }
    static boolean augment(int g,int[][] can,boolean[] seen,int[] tourGuide){
        for(int t=0;t<can[g].length;t++)if(can[g][t]==1&&!seen[t]){
            seen[t]=true;if(tourGuide[t]<0||augment(tourGuide[t],can,seen,tourGuide)){tourGuide[t]=g;return true;}
        }return false;
    }

    static void tspDemo() {
        int n=5;int[][] w={{0,10,15,20,18},{10,0,35,25,12},{15,35,0,30,10},{20,25,30,0,14},{18,12,10,14,0}};
        int states=1<<n;int INF=1_000_000_000;int[][] dp=new int[states][n];
        for(int m=0;m<states;m++)for(int j=0;j<n;j++)dp[m][j]=INF;
        dp[1][0]=0;
        for(int mask=1;mask<states;mask++)if((mask&1)!=0){
            for(int u=0;u<n;u++)if(dp[mask][u]<INF){
                for(int v=0;v<n;v++)if((mask&(1<<v))==0){
                    int nm=mask|(1<<v);if(dp[nm][v]>dp[mask][u]+w[u][v])dp[nm][v]=dp[mask][u]+w[u][v];
                }
            }
        }
        int best=INF;for(int u=1;u<n;u++)best=Math.min(best,dp[states-1][u]+w[u][0]);
        System.out.println("Best demo round-trip cost visiting 5 stops: "+best);
        System.out.println("Held-Karp bitmask DP complexity: O(n^2 * 2^n) time, O(n * 2^n) space.");
        System.out.println("This is an exact small-instance result; large trips need heuristics/approximation.");
    }

    static void vertexCoverDemo() {
        int[][] edges={{0,1},{1,2},{2,3},{3,4},{0,4},{1,4}};
        boolean[] chosen=new boolean[5];boolean[] covered=new boolean[edges.length];int count=0;
        for(int i=0;i<edges.length;i++)if(!covered[i]){
            int u=edges[i][0],v=edges[i][1];chosen[u]=true;chosen[v]=true;count+=2;
            for(int j=0;j<edges.length;j++)if(edges[j][0]==u||edges[j][1]==u||edges[j][0]==v||edges[j][1]==v)covered[j]=true;
        }
        System.out.print("Approximate vertex cover (selected hubs): ");
        for(int i=0;i<chosen.length;i++)if(chosen[i])System.out.print("Hub"+(i+1)+" ");
        System.out.println("\nSelected hub count: "+count+" (duplicates can be counted in this simple demo).");
        System.out.println("The standard 2-approximation selects both endpoints of each edge in a maximal matching.");
        System.out.println("NP-hard route planning motivates approximation for large instances.");
    }

    static void randomizedDemo() {
        int[] a={42,7,19,3,28,11,5,31,2};
        quicksort(a,0,a.length-1);
        System.out.print("Randomised quicksort result: ");for(int x:a)System.out.print(x+" ");System.out.println();
        long candidate=1_000_003L;
        System.out.println(candidate+" is probably prime? "+millerRabin(candidate,8));
        System.out.println("Randomised quicksort expected time O(n log n); Miller-Rabin is probabilistic.");
    }
    static void quicksort(int[] a,int lo,int hi){
        if(lo>=hi)return;
        int pivotIndex=lo+(int)(Math.random()*(hi-lo+1));int temp=a[pivotIndex];a[pivotIndex]=a[hi];a[hi]=temp;
        int pivot=a[hi],i=lo;
        for(int j=lo;j<hi;j++)if(a[j]<=pivot){temp=a[i];a[i++]=a[j];a[j]=temp;}
        temp=a[i];a[i]=a[hi];a[hi]=temp;quicksort(a,lo,i-1);quicksort(a,i+1,hi);
    }
    static boolean millerRabin(long n,int rounds){
        if(n<2)return false;long[] small={2,3,5,7,11,13,17,19,23,29,31,37};
        for(long p:small){if(n==p)return true;if(n%p==0)return false;}
        long d=n-1;int s=0;while((d&1)==0){d>>=1;s++;}
        long[] bases={2,3,5,7,11,13,17,19};
        for(int i=0;i<rounds;i++){
            long a=bases[i%bases.length]%(n-2)+2;long x=modPow(a,d,n);
            if(x==1||x==n-1)continue;boolean witness=true;
            for(int r=1;r<s;r++){x=mulMod(x,x,n);if(x==n-1){witness=false;break;}}
            if(witness)return false;
        }return true;
    }
    static long modPow(long a,long e,long mod){long r=1;a%=mod;while(e>0){if((e&1)==1)r=mulMod(r,a,mod);a=mulMod(a,a,mod);e>>=1;}return r;}
    static long mulMod(long a,long b,long mod){return (a*b)%mod;} // safe for the small demo candidate range

    static void reservoirDemo() {
        String[] bookings={"BK101","BK102","BK103","BK104","BK105","BK106","BK107","BK108","BK109","BK110"};
        int k=3;String[] sample=new String[k];
        for(int i=0;i<k;i++)sample[i]=bookings[i];
        for(int i=k;i<bookings.length;i++){int j=(int)(Math.random()*(i+1));if(j<k)sample[j]=bookings[i];}
        System.out.println("Uniform sample of 3 from 10 booking IDs:");
        for(String id:sample)System.out.println("- "+id);
        System.out.println("Reservoir sampling uses O(k) memory for a stream of unknown length.");
    }

    static void parallelDemo() {
        final int n=100_000;final long[] data=new long[n];for(int i=0;i<n;i++)data[i]=i+1;
        final long[] partial=new long[4];Thread[] threads=new Thread[4];
        for(int t=0;t<4;t++){
            final int id=t;threads[t]=new Thread(() -> {
                int start=id*n/4,end=(id+1)*n/4;long sum=0;
                for(int i=start;i<end;i++)sum+=data[i];partial[id]=sum;
            });
            threads[t].start();
        }
        try{for(Thread thread:threads)thread.join();}catch(InterruptedException e){Thread.currentThread().interrupt();System.out.println("Interrupted.");return;}
        long total=0;for(long v:partial)total+=v;
        System.out.println("Parallel sum of integers 1.."+n+" = "+total);
        System.out.println("Expected formula n(n+1)/2 = "+((long)n*(n+1)/2));
        System.out.println("Work O(n); ideal span O(n/p), excluding thread overhead.");
    }

    static int readInt(String prompt) {
        System.out.print(prompt);
        String s=SC.nextLine().trim();
        try{return Integer.parseInt(s);}catch(NumberFormatException e){return -1;}
    }
}
