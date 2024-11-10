/*
  Name: Marcos Ibáñez Matles
 */

import java.util.*;

public class Graph {
    private final HashMap<String, Node> nodes; // hashmap containing nodes in the graph, mapped by their unique IDs
    private final HashMap<String, Edge> edges; // hashmap containing edges in the graph, mapped by their unique IDs

    // getters and setters
    public HashMap<String, Edge> getEdges() {
        return edges;
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    /**
     * Constructs a new graph object with empty maps of nodes and edges.
     */
    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * Adds a node to the graph.
     *
     * @param node The node to be added to the graph.
     */
    public void addNode(Node node) {
        this.nodes.put(node.getId(), node);
    }

    /**
     * Adds an edge to the graph.
     *
     * @param edge The edge to be added to the graph.
     */
    public void addEdge(Edge edge) {
        edge.getA().addEdge(edge);
        edge.getB().addEdge(edge);
        this.edges.put(edge.getId(), edge);
    }

    /**
     * Finds a node in the graph by ID.
     *
     * @param id The ID of the node to find.
     * @return The node object with the given ID, or null if no such node exists.
     */
    public Node find(String id) {
        return this.nodes.get(id);
    }

    /**
     * Performs Dijkstra's algorithm on the graph starting from a given node.
     *
     * @param source The source node for the algorithm.
     * @throws NullPointerException if source is null
     */
    public static void dijkstra(Node source) {
        int relaxations = 0;
        if (source == null) {
            throw new NullPointerException("Cannot perform Dijkstra's Algorithm, source is null");
        }
        source.setDist(0);
        HashSet<Node> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(Node::getDist));
        queue.add(source);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            visited.add(current);
            for (Edge e : current.getEdges()) {
                Node next = e.getNext(current);
                if (visited.contains(next)) {
                    continue;
                }
                double newDist = current.getDist() + e.getWeight();
                if (newDist < next.getDist()) {
                    next.setDist(newDist);
                    next.setPrev(current);
                    queue.add(next);
                    relaxations++;
                }
            }
        }
    }

    /**
     * Finds the shortest path between two nodes in the graph using Dijkstra's algorithm.
     *
     * @param source The source node of the path.
     * @param target The target node of the path.
     * @return A Path object representing the shortest path between source and target nodes.
     * @throws NullPointerException if target is null
     */
    public static Path shortestPath(Node source, Node target) {
        if (target == null) {
            throw new NullPointerException("Cannot find shortest path, target is null");
        }
        LinkedList<Node> path = new LinkedList<>();
        for (Node node = target; node != null; node = node.getPrev()) {
            path.addFirst(node);
        }
        if (!path.contains(source)) {
            return new Path(source, target, null);
        }
        return new Path(source, target, path);
    }

    /**
     * Resets the distance and previous node values of all nodes in the graph.
     */
    public void resetNodes() {
        for (Node node : this.getNodes().values()) {
            node.setDist(Double.POSITIVE_INFINITY);
            node.setPrev(null);
        }
    }
}
