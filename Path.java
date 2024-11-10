/*
  Name: Marcos Ibáñez Matles
 */

import java.util.LinkedList;

public class Path {
    private final Node source; // the source node of the path
    private final Node target; // the target node of the path
    private final LinkedList<Edge> edges; // linked list of edges connecting the nodes in the path
    private final LinkedList<Node> nodes; // linked list of nodes representing the path
    private final double distance; // the total distance of the path in miles

    // getters and setters
    public LinkedList<Node> getNodes() {
        return nodes;
    }

    public double getDistance() {
        return distance;
    }

    /**
     * Creates a Path object that represents a path from a source node to a target node
     * through a given list of nodes.
     *
     * @param source the starting node of the path
     * @param target the destination node of the path
     * @param nodes  a linked list of nodes representing the path
     */
    public Path(Node source, Node target, LinkedList<Node> nodes) {
        this.source = source;
        this.target = target;
        this.nodes = nodes;
        this.edges = this.getEdgesInPath();
        this.distance = this.getTotalMiles();
    }

    /**
     * Returns a linked list of edges connecting the nodes in the path.
     * If there are no nodes in the path, returns null.
     *
     * @return a linked list of edges connecting the nodes in the path
     */
    public LinkedList<Edge> getEdgesInPath() {
        if (this.nodes == null) {
            return null;
        }
        LinkedList<Edge> edgesInPath = new LinkedList<>();
        for (int i = 0; i < this.nodes.size() - 1; i++) {
            Node currentNode = this.nodes.get(i);
            Node nextNode = this.nodes.get(i + 1);
            for (Edge edge : currentNode.getEdges()) {
                if (edge.getNext(currentNode) == nextNode) {
                    edgesInPath.add(edge);
                    break;
                }
            }
        }
        return edgesInPath;
    }

    /**
     * Returns the total miles of the path.
     * If there are no edges in the path, returns Double.POSITIVE_INFINITY.
     *
     * @return the total miles of the path
     */
    public double getTotalMiles() {
        if (this.edges == null) {
            return Double.POSITIVE_INFINITY;
        }
        double totalMiles = 0.0;
        for (Edge edge : this.edges) {
            totalMiles += edge.getWeight();
        }
        return totalMiles;
    }

    /**
     * Prints the details of the path.
     * If there are no nodes in the path, prints a message indicating there is no path between the source and target nodes.
     *
     * @param path the Path object to be printed
     */
    public static void print(Path path) {
        if (path.nodes == null) {
            System.out.printf("(Total Distance: ∞ mi) There is no path between [%s] and [%s].\n",
                    path.source.getId(), path.target.getId());
            return;
        }
        StringBuilder toPrint = new StringBuilder();
        toPrint.append("(Total Distance: ").append(String.format("%.2fmi", path.distance))
                .append(") Intersection list -> [");
        for (Node node : path.nodes) {
            toPrint.append(node.getId()).append(", ");
        }
        toPrint.setCharAt(toPrint.length() - 2, ']');
        System.out.println(toPrint);
    }
}
