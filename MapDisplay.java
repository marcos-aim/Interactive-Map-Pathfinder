/*
  Name: Marcos Ibáñez Matles
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MapDisplay extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
    // Variables for the frame and the map
    private final JFrame frame; // the frame that holds the JPanel
    private int windowHeight; // the height of the window
    private int windowWidth; // the width of the window
    private final StreetMap map; // the street map

    // Variables for the quadtree and scaling factors
    private QuadTree screenQuadrants; // the quadtree that holds nodes visible on screen
    private double latScaleFactor; // the scaling factor for latitude
    private double lonScaleFactor; // the scaling factor for longitude

    // Variables for tracking the first and second nodes and mouse events
    private Node firstNode; // the first node clicked
    private Node secondNode; // the second node clicked
    private boolean isDragging; // whether the mouse is currently dragging

    // Constants for screen buffer and colors
    private int SIDE_SCREEN_BUFFER; // the buffer on the sides of the screen
    private int TOP_SCREEN_BUFFER; // the buffer at the top of the screen
    private static final Color BACKGROUND_COLOR = new Color(245, 240, 228); // the background color
    private static final Color EDGE_COLOR = Color.BLACK; // the color of edges
    private static final Color LABEL_COLOR = Color.BLACK; // the color of labels
    private static final Color PATH_COLOR = Color.RED; // The color of the path
    private static final Color NODE_COLOR = new Color(138, 23, 41); // he color of nodes

    // getters and setters
    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setFirstNode(Node firstNode) {
        this.firstNode = firstNode;
    }

    public void setSecondNode(Node secondNode) {
        this.secondNode = secondNode;
    }

    // Setter for the screen quadrants, and adding nodes to it
    public void setScreenQuadrants(QuadTree screenQuadrants) {
        this.screenQuadrants = screenQuadrants;
        for (Node node : map.getGraph().getNodes().values()) {
            this.screenQuadrants.insert(node);
        }
    }

    /**
     * Constructor for the MapDisplay class.
     *
     * @param map the StreetMapping object representing the map
     */
    public MapDisplay(StreetMap map) {
        this.map = map;
        frame = new JFrame("Street Mapping");
        frame.setPreferredSize(new Dimension(1280, 720));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setBackground(BACKGROUND_COLOR);
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        setFocusable(true);
        repaint();
    }

    /**
     * Draws the graph and labels on the panel using the given Graphics2D object.
     *
     * @param g the Graphics2D object used to draw the graph
     */
    public void drawGraph(Graphics2D g) {
        // draw edges
        g.setColor(EDGE_COLOR);
        for (Edge edge : map.getGraph().getEdges().values()) {
            this.drawEdge(g, edge);
        }

        // draw highlighted path
        g.setColor(PATH_COLOR);
        g.setStroke(new BasicStroke(3));
        if (map.getPath() != null && map.getPath().getNodes() != null) {
            for (Edge edge : map.getPath().getEdgesInPath()) {
                this.drawEdge(g, edge);
            }
        }

        // draw nodes
        g.setColor(NODE_COLOR);
        if (firstNode != null) {
            g.fillOval(firstNode.getScaledX() - 5, firstNode.getScaledY() - 15, 10, 10);
            g.fillPolygon(new int[]{firstNode.getScaledX(), firstNode.getScaledX() + 5, firstNode.getScaledX() - 5},
                    new int[]{firstNode.getScaledY(), firstNode.getScaledY() - 10, firstNode.getScaledY() - 10}, 3);
        }
        if (secondNode != null) {
            g.fillOval(secondNode.getScaledX() - 5, secondNode.getScaledY() - 15, 10, 10);
            g.fillPolygon(new int[]{secondNode.getScaledX(), secondNode.getScaledX() + 5, secondNode.getScaledX() - 5},
                    new int[]{secondNode.getScaledY(), secondNode.getScaledY() - 10, secondNode.getScaledY() - 10}, 3);
        }

        // draw labels
        g.setColor(LABEL_COLOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawLabels(g);
    }

    /**
     * Draws the labels on the screen.
     *
     * @param g the Graphics2D object used for drawing
     */
    public void drawLabels(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, (int) (this.windowWidth * 0.025)));
        FontMetrics metrics = g.getFontMetrics();

        if (map.getPath() != null && firstNode != null && secondNode != null) {
            String label = ("[%s]   -----    (%smi)   ---->   [%s]".formatted(firstNode.getId(),
                    map.getPath().getDistance() == Double.POSITIVE_INFINITY ? "∞" : String.format("%.2f", map.getPath().getDistance()),
                    secondNode.getId()));
            int x = (this.windowWidth - metrics.stringWidth(label)) / 2;
            int y = metrics.getAscent();
            g.drawString(label, x, (int) (y + TOP_SCREEN_BUFFER * 0.3));
        } else if (firstNode != null) {
            String label2 = ("[%s]".formatted(firstNode.getId()));
            int x = (this.windowWidth - metrics.stringWidth(label2)) / 2;
            int y = metrics.getAscent();
            g.drawString(label2, x, (int) (y + TOP_SCREEN_BUFFER * 0.3));
        }
    }

    /**
     * Draws an edge between two nodes on the screen.
     *
     * @param g    the Graphics2D object used for drawing
     * @param edge the Edge object representing the edge to draw
     */
    public void drawEdge(Graphics2D g, Edge edge) {
        Node start = edge.getA();
        Node end = edge.getB();
        start.setScaledX(this.scaleLon(start.getLongitude()));
        start.setScaledY(this.scaleLat(start.getLatitude()));
        end.setScaledX(this.scaleLon(end.getLongitude()));
        end.setScaledY(this.scaleLat(end.getLatitude()));
        g.drawLine(start.getScaledX(), start.getScaledY(), end.getScaledX(), end.getScaledY());
    }

    /**
     * Scales the latitude of a point to its corresponding position on the screen.
     *
     * @param lat the latitude of the point
     * @return the scaled Y coordinate of the point on the screen
     */
    public int scaleLat(double lat) {
        double scaledLatitude = (lat - map.getBottomBound()) * this.latScaleFactor;
        // Flip the Y-axis
        return (int) (this.windowHeight - scaledLatitude - TOP_SCREEN_BUFFER * 1.2);
    }

    /**
     * Scales the longitude of a point to its corresponding position on the screen.
     *
     * @param lon the longitude of the point
     * @return the scaled X coordinate of the point on the screen
     */
    public int scaleLon(double lon) {
        return (int) ((lon - map.getLeftBound()) * this.lonScaleFactor) + SIDE_SCREEN_BUFFER;
    }

    /**
     * This method updates the map display and paints the graph.
     *
     * @param g the graphics context to use for painting
     */
    public void paint(Graphics g) {
        super.paint(g);

        // Get the height and width of the frame where the panel is added
        this.windowHeight = frame.getHeight();
        this.windowWidth = frame.getWidth();

        // Calculate the scaling factors for latitude and longitude
        this.latScaleFactor = windowHeight * 0.85 / (map.getTopBound() - map.getBottomBound());
        this.lonScaleFactor = windowWidth * 0.85 / (map.getRightBound() - map.getLeftBound());
        double scaleFactor = Math.min(latScaleFactor, lonScaleFactor);
        this.latScaleFactor = scaleFactor;
        this.lonScaleFactor = scaleFactor;

        // Calculate the width and height of the map using the scaling factors
        int mapWidth = (int) ((map.getRightBound() - map.getLeftBound()) * scaleFactor);
        int mapHeight = (int) ((map.getTopBound() - map.getBottomBound()) * this.latScaleFactor);

        // Calculate the buffer space on the left and top of the screen to center the map
        SIDE_SCREEN_BUFFER = (this.windowWidth - mapWidth) / 2;
        TOP_SCREEN_BUFFER = (this.windowHeight - mapHeight) / 2;

        // Cast the graphics object to Graphics2D to enable advanced drawing features
        Graphics2D g2 = (Graphics2D) g;
        this.drawGraph(g2);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // If right click, reset Dijkstra and set new source on mouse
        if (firstNode == null || e.getButton() == 1) {
            secondNode = null;
            map.setPath(null);
            firstNode = screenQuadrants.findNearest(e.getX(), e.getY());
            map.getGraph().resetNodes();
            Graph.dijkstra(firstNode);
            repaint();
        }
        // If left click, reset target and set new Path
        else {
            secondNode = screenQuadrants.findNearest(e.getX(), e.getY());
            map.setPath(Graph.shortestPath(firstNode, secondNode));
            Path.print(map.getPath());
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Change target and path if released after dragging
        if (isDragging) {
            secondNode = screenQuadrants.findNearest(e.getX(), e.getY());
            map.setPath(Graph.shortestPath(firstNode, secondNode));
            Path.print(map.getPath());
            repaint();
            isDragging = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Keep changing the path target while dragging
        secondNode = screenQuadrants.findNearest(e.getX(), e.getY());
        map.setPath(Graph.shortestPath(firstNode, secondNode));
        isDragging = true;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void componentResized(ComponentEvent e) {
        // Re-do the quad tree if the screen is resized
        this.setScreenQuadrants(new QuadTree(new Rectangle(this.windowWidth, this.windowHeight)));
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
