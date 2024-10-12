import java.awt.*;
import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * The Fielder class is in charge of the fielders who are moving to catch the fielder
 * 
 * @author Justin Montagne, Sam Goldstein, Arizona Belden, and Anthony Russo
 * @version Spring 2022
 */
class Fielder extends AnimatedGraphicsObject {
    // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    // pixels to move each frame
    public double ySpeed;

    public double xSpeed;

    // latest location of the fielder
    private Point2D.Double upperLeft;

    //Where the fielder should stop
    private Point2D.Double endPoint;

    private static final int SIZE = 20;

    private Color color;

    // who do we live in so we can repaint?
    private JComponent container;

    /**
     * Construct a new Fielder object.
     * 
     * @param startTopCenter the initial point at which the top of the
     *                       fielder should be drawn
     * @param container      the Swing component in which this fielder is being
     *                       drawn to allow it to call that component's repaint
     *                       method
     */
    public Fielder(Point2D.Double upperLeft, JComponent container, Point2D.Double endPoint, Color color) {
        super(container);

        this.color = color;
        this.upperLeft = new Point2D.Double(upperLeft.x, upperLeft.y);
        this.container = container;
        this.endPoint = endPoint;
        double xMove = endPoint.x - upperLeft.x;
        double yMove = endPoint.y - upperLeft.y;

        ySpeed = yMove / 45;
        xSpeed = xMove / 45;

    }

    /**
     * Draw the fielder at its current location.
     * 
     * @param g the Graphics object on which the fielder should be drawn
     */
    public void paint(Graphics g) {

        // g.fillOval(upperLeft.x, upperLeft.y, SIZE, SIZE);
        g.setColor(color);
        g.fillOval((int) upperLeft.x, (int) upperLeft.y, SIZE, SIZE);

    }

    /**
     * This object's run method, which manages the life of the fielder as it
     * moves down the screen.
     */
    @Override
    public void run() {

        // the run method is what runs in this object's thread for the
        // time it is "alive"

        // this Fielder's life as a thread will continue as long as this
        // fielder isn't near (within 5 pixels) of the endPoint
        while (!near(upperLeft, endPoint)) {

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the fielder down
            // by a pixel
            upperLeft.setLocation(upperLeft.x + xSpeed, upperLeft.y + ySpeed);

            // if we want to see the fielder move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }
        //Sleep 2/10s of a second before disapearing 
        try{
            sleep(200);
        } catch (InterruptedException e) {
        }

        done = true;
        container.repaint();
    }

    /**
     * Checks if the S (fielder) is near the endPoint e
     * @param s the fielder
     * @param e the endPoint
     * @return If s is within 5 pixels of e
     */
    public boolean near(Point2D.Double s, Point2D.Double e) {
        if (s.x > e.x - 5 && s.x < e.x + 5) {
            if (s.y > e.y - 5 && s.y < e.y + 5)
                return true;
        }

        return false;
    }
}
