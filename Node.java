/*
  Name: Marcos Ibáñez Matles
 */

import java.util.LinkedList;

public class Node {
    private final String id; // unique identifier of the node
    private final double latitude; // latitude of the node's location
    private final double longitude; // longitude of the node's location
    private final LinkedList<Edge> edges; // list of edges connected to the node
    private double dist; // distance of the node from a given source node in Dijkstra's algorithm
    private Node prev; // previous node in the shortest path from a given source node in Dijkstra's algorithm
    private int scaledX; // x-coordinate of the node's scaled screen location
    private int scaledY; // y-coordinate of the node's scaled screen location
    private static final double EARTH_RADIUS_MI = 3958.8; // radius of the Earth in miles

    // getters and setters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public int getScaledX() {
        return scaledX;
    }

    public void setScaledX(int scaledX) {
        this.scaledX = scaledX;
    }

    public int getScaledY() {
        return scaledY;
    }

    public void setScaledY(int scaledY) {
        this.scaledY = scaledY;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    /**
     * Creates a new Node object with the specified parameters.
     *
     * @param id        The unique identifier of the node.
     * @param latitude  The latitude coordinate of the node.
     * @param longitude The longitude coordinate of the node.
     */
    public Node(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = new LinkedList<>();
        this.dist = Double.POSITIVE_INFINITY;
    }

    /**
     * Adds an edge to the list of edges connected to this node.
     *
     * @param e The edge to add.
     */
    public void addEdge(Edge e) {
        this.edges.add(e);
    }

    /**
     * Calculates the distance between two nodes using the Haversine formula.
     *
     * @param a The first node.
     * @param b The second node.
     * @return The distance between the two nodes in miles.
     */
    public static double dist(Node a, Node b) {
        double latA = Math.toRadians(a.getLatitude());
        double lonA = Math.toRadians(a.getLongitude());
        double latB = Math.toRadians(b.getLatitude());
        double lonB = Math.toRadians(b.getLongitude());

        double dLat = latB - latA;
        double dLon = lonB - lonA;

        double step1 = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) + Math.cos(latA) * Math.cos(latB) *
                (Math.sin(dLon / 2) * Math.sin(dLon / 2));

        double step2 = Math.asin(Math.sqrt(step1));

        return 2 * EARTH_RADIUS_MI * step2;
    }

    public String toString() {
        return this.getEdges().toString();
    }
}
