package cse417hw2;

import java.io.*;
import java.util.*;

class hw2 {
	static class Edge {
		int u;
		int v;

		public Edge(int u, int v) {
			this.u = u;
			this.v = v;
		}

		public String toString() {
			return u + "--" + v;
		}
	}

	static class Graph {
		public int V, E; // No. of vertices & Edges respectively
		public LinkedList<Integer>[] adj; // Adjacency List
		public List<List<Edge>> bcc = new LinkedList<>();
		public Set<Integer> art = new HashSet<>();

		// dfsCounter
		public int time = 1;

		// Constructor
		public Graph(int v) {
			V = v;
			E = 0;
			adj = new LinkedList[v];
			for (int i = 0; i < v; i++) {
				adj[i] = new LinkedList<>();
			}
		}

		// Function to add an edge into the graph
		public void addEdge(int v, int w) {
			adj[v].add(w);
			adj[w].add(v);
			E++;
		}



		/**
		 * 
		 * @param u - the vertex to run dfs algorithm
		 * @param visited - status of each vertex and their dfs counter
		 * @param low - low number of each vertex
		 * @param st - stack to store edges
		 * @param parent - parent vertex of each vertex
		 */
		public void dfs(int u, int visited[], int low[], Stack<Edge> st, int parent[]) {

			// Initialize dfs counter and low value
			visited[u] = time;
			low[u] = time;
			time++;

			// Go through all vertices adjacent to this
			Iterator<Integer> it = adj[u].iterator();
			while (it.hasNext()) {
				int v = it.next(); // v is current adjacent of 'u'

				// If v is not visited yet, then recur for it
				if (visited[v] == -1) {
					parent[v] = u;

					// store the edge in stack
					st.add(new Edge(u, v));
					dfs(v, visited, low, st, parent);

					// Check if the subtree rooted with 'v' has a
					// connection to one of the ancestors of 'u'
					if (low[u] > low[v]) low[u] = low[v];

					// If u is an articulation point,
					// pop all edges from stack till u -- v
					if (visited[u] > 1 && low[v] >= visited[u]) {
						art.add(u);
						LinkedList<Edge> temp = new LinkedList<>();
						while (st.peek().u != u || st.peek().v != v) {
							temp.add(st.pop());
						}
						temp.add(st.pop());
						bcc.add(new LinkedList<>(temp));
					}
				}

				// Update low value if not parent // back edge
				else if (v != parent[u] && visited[v] < visited[u]) {
					if (low[u] > visited[v]) low[u] = visited[v];
					st.add(new Edge(u, v));
				}
			}
		}

		// Base case
		public void BCC() {
			int visited[] = new int[V];
			int low[] = new int[V];
			int parent[] = new int[V];
			Stack<Edge> st = new Stack<Edge>();

			Arrays.fill(visited, -1);
			Arrays.fill(low, -1);
			Arrays.fill(parent, -1);

			for (int i = 0; i < V; i++) {
				if (visited[i] == -1) dfs(i, visited, low, st, parent);

				// If stack is not empty, pop all edges from stack
				boolean leftover = false;
				LinkedList<Edge> temp = new LinkedList<>();
				while (st.size() > 0) {
					leftover = true;
					temp.add(st.pop());
				}
				if (leftover) bcc.add(new LinkedList<>(temp));
			}
		}
	}

	public static void main(String args[]) {
		long sum = 0;
		int n = 1; //number of times for getting average runtime
		
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("testout.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.setOut(out);

		for (int i = 1; i <= n; i++) {
			if (i != 1) System.out.println("=====================================");
			System.out.println("Run " + i + ":");
			sum += run();
		}
		if (n == 1) return;
		// run more than one time
		System.out.println("=====================================");
		System.out.println("Average runtime is " 
			+ (long) sum / n + " nanoseconds.");
	}
	
	public static long run() {
		String[] arr = null;
		String input = "16\r\n" + 
				" 1  6\r\n" + 
				" 1 14\r\n" + 
				" 2  4\r\n" + 
				" 2 10\r\n" + 
				" 3  4\r\n" + 
				" 3 15\r\n" + 
				" 4  6\r\n" + 
				" 4  7\r\n" + 
				" 4 10\r\n" + 
				" 5 14\r\n" + 
				" 0  1\r\n" + 
				" 0  5\r\n" + 
				" 0  6\r\n" + 
				" 0 14\r\n" + 
				" 1  5\r\n" + 
				" 6 14\r\n" + 
				" 7  9\r\n" + 
				" 8  9\r\n" + 
				" 8 12\r\n" + 
				" 8 13\r\n" + 
				"10 15\r\n" + 
				"11 12\r\n" + 
				"11 13\r\n" + 
				"12 13\r\n" + 
				"";
		arr = input.split("\\n");
		int num_vertex = Integer.valueOf(arr[0].trim());
		Graph g = new Graph(num_vertex);

		for (String edge : arr) {
			edge = edge.trim();
			if (edge.equals(arr[0].trim())) continue;
			String[] temp;
			temp = edge.split(" ");
			g.addEdge(Integer.valueOf(temp[0].trim()), 
					Integer.valueOf(temp[temp.length-1].trim()));
		}

		long startTime = System.nanoTime();
		g.BCC();
	    long stopTime = System.nanoTime();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("The elapsed time for finding biconnected components is " 
	    + elapsedTime + " nanoseconds.");
	    //nanoseconds
	    
		System.out.println("Number of vertices: " + g.V);
		System.out.println("Number of edges: " + g.E);
		System.out.println("Number of biconnected components: " + g.bcc.size());
		System.out.println("Number of articulation points: " + g.art.size());

		System.out.print("The articulations points are: ");
		Iterator<Integer> iter = g.art.iterator();
		while (iter.hasNext()) {
			System.out.print(iter.next() + " ");
		}
		System.out.println("");

		System.out.println("The biconnected components are: ");
		for (List<Edge> coms : g.bcc) {
			for (Edge edge : coms) {
				System.out.print(edge.toString() + " ");
			}
			System.out.println("");
		}
		return elapsedTime;
	}
}
