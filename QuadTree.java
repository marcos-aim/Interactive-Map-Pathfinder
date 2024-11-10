/*
  Name: Marcos Ibáñez Matles
  NetID: mibanezm
  Project 3
  CSC 172
 */

import java.awt.*;
import java.util.ArrayList;

public class QuadTree {
    private static final int MAX_CAPACITY = 4; // maximum capacity per Quad Tree
    private final Rectangle boundary; // the boundary of this QuadTree
    private final ArrayList<Node> nodes; // the nodes in this QuadTree
    private final QuadTree[] children; // the children of this QuadTree

    /**
     * Constructs a new QuadTree with the specified boundary.
     *
     * @param boundary the boundary of this QuadTree
     */
    public QuadTree(Rectangle boundary) {
        this.boundary = boundary;
        this.nodes = new ArrayList<>();
        this.children = new QuadTree[MAX_CAPACITY];
    }

    /**
     * Inserts a new node into this QuadTree.
     *
     * @param node the node to be inserted
     * @return true if the insertion is successful, false otherwise
     */
    public boolean insert(Node node) {
        if (!boundary.contains(node.getScaledX(), node.getScaledY())) {
            return false;
        }
        if (nodes.size() < MAX_CAPACITY) {
            nodes.add(node);
            return true;
        }
        if (children[0] == null) {
            split();
        }
        for (QuadTree child : children) {
            if (child.insert(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private helper method to split the QuadTree into 4 new Quadrants.
     */
    private void split() {
        int x = boundary.x;
        int y = boundary.y;
        int halfWidth = boundary.width / 2;
        int halfHeight = boundary.height / 2;

        children[0] = new QuadTree(new Rectangle(x, y, halfWidth, halfHeight));
        children[1] = new QuadTree(new Rectangle(x + halfWidth, y, halfWidth, halfHeight));
        children[2] = new QuadTree(new Rectangle(x, y + halfHeight, halfWidth, halfHeight));
        children[3] = new QuadTree(new Rectangle(x + halfWidth, y + halfHeight, halfWidth, halfHeight));

        for (Node node : nodes) {
            for (QuadTree child : children) {
                child.insert(node);
            }
        }
        nodes.clear();
    }

    /**
     * Finds the nearest node to the specified position (mouseX, mouseY).
     *
     * @param mouseX the x-coordinate of the position
     * @param mouseY the y-coordinate of the position
     * @return the nearest node to the position, null if not found
     */
    public Node findNearest(int mouseX, int mouseY) {
        Node nearest = null;
        double minDist = Double.MAX_VALUE;

        // check if this QuadTree's boundary intersects with the mouse position
        if (!boundary.intersects(mouseX - 10, mouseY - 10, 20, 20)) {
            return null;
        }

        // check all nodes in this QuadTree
        for (Node node : nodes) {
            double dist = distance(node.getScaledX(), node.getScaledY(), mouseX, mouseY);
            if (dist < minDist) {
                minDist = dist;
                nearest = node;
            }
        }

        // recursively check children QuadTrees
        for (QuadTree child : children) {
            if (child != null) {
                Node childNearest = child.findNearest(mouseX, mouseY);
                if (childNearest != null) {
                    double dist = distance(childNearest.getScaledX(), childNearest.getScaledY(), mouseX, mouseY);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = childNearest;
                    }
                }
            }
        }
        return nearest;
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the Euclidean distance between the two points
     */
    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
