import java.awt.*;
import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * The hit class is in charge of the hit moving to the point calculated in the BaseballGame class
 * 
 * @author Sidhanath Verekar
 */
class Hit extends AnimatedGraphicsObject {
    // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    // pixels to move each frame
    public double ySpeed;

    public double xSpeed;

    // latest location of the ball
    private Point2D.Double upperLeft;
    // Endpoint of the hit
    private Point2D.Double endPoint;

    private static Image baseballPic;

    private static final int SIZE = 30;

    private static final String ballPicFilename = "baseball.gif";

    // who do we live in so we can repaint?
    private JComponent container;

    /**
     * Construct a new Hit object.
     * 
     * @param startTopCenter the initial point at which the top of the
     *                       ball should be drawn
     * @param container      the Swing component in which this ball is being
     *                       drawn to allow it to call that component's repaint
     *                       method
     */
    public Hit(Point2D.Double upperLeft, JComponent container, Point2D.Double endPoint) {
        super(container);

        this.upperLeft = upperLeft;
        this.container = container;
        this.endPoint = endPoint;
        double xMove = endPoint.x - upperLeft.x;
        double yMove = endPoint.y - upperLeft.y;

        ySpeed = yMove / 45;
        xSpeed = xMove / 45;

    }

    /**
     * Draw the ball at its current location.
     * 
     * @param g the Graphics object on which the ball should be drawn
     */
    public void paint(Graphics g) {

        // g.fillOval(upperLeft.x, upperLeft.y, SIZE, SIZE);

        g.drawImage(baseballPic, (int) upperLeft.x, (int) upperLeft.y, null);
        // g.fillOval((int)endPoint.x, (int)endPoint.y, SIZE, SIZE);

    }

    /**
     * This object's run method, which manages the life of the ball as it
     * moves down the screen.
     */
    @Override
    public void run() {

        // the run method is what runs in this object's thread for the
        // time it is "alive"

        // this Ball's life as a thread will continue as long as this
        // ball is still located on the visible part of the screen
        while (!near(upperLeft, endPoint)) {

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the ball down
            // by a pixel
            upperLeft.setLocation(upperLeft.x + xSpeed, upperLeft.y + ySpeed);

            // if we want to see the ball move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }

        done = true;
    }

    /**
     * Checks if the S (Ball) is near the endPoint e
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

    /**
     * Set the Image to be used by all Ball objects, to be
     * called by the main method before the GUI gets set up
     */
    public static void loadBallPic() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Hit.baseballPic = toolkit.getImage(ballPicFilename).getScaledInstance(30, 30, Image.SCALE_DEFAULT);

    }

    /**
     * Set the Image to be used by all Ball objects, to be
     * called by the main method before the GUI gets set up
     */
    public Point2D.Double getLocation() {

        return new Point2D.Double(upperLeft.x + 15, upperLeft.y + 15);
    }
}
