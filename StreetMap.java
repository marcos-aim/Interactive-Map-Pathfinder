/*
  Name: Marcos Ibáñez Matles
 */

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class StreetMap {
    private final Graph graph; // the graph containing the nodes and edges
    private Path path; // the path between two nodes

    private double leftBound; // the left boundary of the graph
    private double rightBound; // the right boundary of the graph
    private double topBound; // the top boundary of the graph
    private double bottomBound; // the bottom boundary of the graph

    // getters and setters
    public double getLeftBound() {
        return leftBound;
    }

    public double getRightBound() {
        return rightBound;
    }

    public double getTopBound() {
        return topBound;
    }

    public double getBottomBound() {
        return bottomBound;
    }

    public Graph getGraph() {
        return graph;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * Constructor for StreetMapping.
     * Initializes the boundaries of the graph to infinity and negative infinity.
     * Calls the method getGraphData to create the graph.
     *
     * @param path the file path containing the graph data
     */
    public StreetMap(String path) {
        this.leftBound = Double.POSITIVE_INFINITY;
        this.rightBound = Double.NEGATIVE_INFINITY;
        this.topBound = Double.NEGATIVE_INFINITY;
        this.bottomBound = Double.POSITIVE_INFINITY;
        this.graph = this.getGraphData(path);
    }

    /**
     * Private helper method to create the graph.
     * Reads data from a file and creates nodes and edges in the graph.
     * Updates the boundaries of the graph.
     *
     * @param path the file path containing the graph data
     * @return the graph created from the file data
     */
    private Graph getGraphData(String path) {
        Graph newGraph = new Graph();
        try {
            Scanner s = new Scanner(new File(path));
            while (s.hasNext()) {
                String[] line = s.nextLine().split("\\s+");
                if (line.length == 0) continue;
                switch (line[0]) {
                    case "i" -> {
                        double lat = Double.parseDouble(line[2]);
                        double lon = Double.parseDouble(line[3]);
                        // Create a new Node object and add it to the graph
                        newGraph.addNode(new Node(line[1], lat, lon));
                        if (lon < this.leftBound) {
                            this.leftBound = lon;
                        }
                        if (lon > this.rightBound) {
                            this.rightBound = lon;
                        }
                        if (lat > this.topBound) {
                            this.topBound = lat;
                        }
                        if (lat < this.bottomBound) {
                            this.bottomBound = lat;
                        }
                    }
                    case "r" -> newGraph.addEdge(new Edge(line[1], newGraph.find(line[2]), newGraph.find(line[3])));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return newGraph;
    }

    /**
     * Method that finds the main directions between two nodes in the graph using Dijkstra's algorithm.
     *
     * @param startID A string representing the ID of the starting node.
     * @param endID   A string representing the ID of the ending node.
     * @return A Path object representing the shortest path between the two nodes.
     */
    public Path mainDirections(String startID, String endID) {
        Graph.dijkstra(this.getGraph().find(startID));
        return Graph.shortestPath(this.getGraph().find(startID), this.getGraph().find(endID));
    }

    public static void main(String[] args) {
        StreetMap streetMap = new StreetMap(args[0]);
        MapDisplay display = null;
        boolean show = false;
        boolean directions = false;
        String id1 = null;
        String id2 = null;

        // Loop through the command-line arguments
        for (int i = 1; i < args.length; i++) {
            // If the argument starts with "--", it is a flag
            if (args[i].startsWith("--")) {
                switch (args[i].substring(2)) {
                    case "show" -> show = true;
                    case "directions" -> directions = true;
                }
            } else {
                if (id1 == null) {
                    id1 = args[i];
                } else if (id2 == null) {
                    id2 = args[i];
                }
            }
        }

        // If the "show" flag is true, create a new MapDisplay and wait for it to load
        if (show) {
            display = new MapDisplay(streetMap);
            try {
                Thread.sleep(100); // wait for map screen to load
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            display.setScreenQuadrants(
                    new QuadTree(new Rectangle(display.getWindowWidth(), display.getWindowHeight())));
        }
        // If the "directions" flag is true and both IDs are set, find the path and print it
        if (directions) {
            if (id1 != null && id2 != null) {
                streetMap.setPath(streetMap.mainDirections(id1, id2));
                Path.print(streetMap.getPath());
                if (display != null) {
                    display.setFirstNode(streetMap.getGraph().find(id1));
                    display.setSecondNode(streetMap.getGraph().find(id2));
                    display.repaint();
                }
            }
        }
    }
}