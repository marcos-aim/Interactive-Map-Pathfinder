/*
  Name: Marcos Ibáñez Matles
 */

public class Edge {
    private final String id; // the unique identifier for this edge
    private final Node a; // the first node connected by this edge
    private final Node b; // the second node connected by this edge
    private final double weight; // the weight or distance of this edge

    // getters and setters
    public String getId() {
        return id;
    }

    public Node getA() {
        return a;
    }

    public Node getB() {
        return b;
    }

    public double getWeight() {
        return weight;
    }

    /**
     * Creates an Edge object with a unique identifier, first and second nodes connected, and the weight or distance of this edge.
     *
     * @param id a unique identifier for this edge
     * @param a  the first node connected by this edge
     * @param b  the second node connected by this edge
     */
    public Edge(String id, Node a, Node b) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.weight = Node.dist(a, b);
    }

    /**
     * Given a node, returns the other node connected to this edge.
     * If the given node is not part of the edge, returns null.
     *
     * @param x the node to get the next node for
     * @return the other node connected to this edge, or null if x is not part of the edge
     */
    public Node getNext(Node x) {
        if (x == this.a) {
            return this.b;
        }
        if (x == this.b) {
            return this.a;
        }
        return null;
    }

    /**
     * Returns a string representation of this edge in the format "id(weight)mi".
     *
     * @return a string representation of this edge
     */
    public String toString() {
        return this.id + "(" + String.format("%.4f", this.weight) + "mi)";
    }
}
