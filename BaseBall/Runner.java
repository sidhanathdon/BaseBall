import java.awt.*;
import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * The Runner class is responsible for the runners moving to the next base
 *
 * @author Justin Montagne, Sam Goldstein, Arizona Belden, and Anthony Russo
 * @version Spring 2022
 */
public class Runner extends AnimatedGraphicsObject {
    // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    //color of the runner
    private Color color;

    //current base the runner is on
    private int curBase;

    //current point of the runner
    private Point2D.Double upperLeft;
    //endpoint of the runner
    private Point2D.Double endPoint;

    //pixels to move
    private double xSpeed;
    private double ySpeed;

    //The location of the bases
    private Point2D.Double firstBase = new Point2D.Double(480, 475);
    private Point2D.Double secondBase = new Point2D.Double(390, 370);
    private Point2D.Double thirdBase = new Point2D.Double(290, 475);
    private Point2D.Double homePlate = new Point2D.Double(385, 585);

    // panel
    private JComponent container;

    /**
     * Construct a new Runner object.
     *
     * @param numBases  Number of bases the runner can reach
     *
     * @param container the Swing component in which this runner is being
     *                  drawn to allow it to call that component's repaint
     *                  method
     */
    public Runner(Color color, int curBase, JComponent container) {
        super(container);

        this.color = color;
        this.container = container;
        this.curBase = curBase;

        //Calculate where the runner should start and end
        if (curBase == 1) {
            upperLeft = firstBase;
            endPoint = secondBase;
            
        } else if (curBase == 2) {
            upperLeft = secondBase;
            endPoint = thirdBase;
            
        } else if (curBase == 3) {
            upperLeft = thirdBase;
            endPoint = homePlate;
            
        } else if (curBase == 0) {
            upperLeft = homePlate;
            endPoint = firstBase;
            
        }

        //Calculate the speed of the ball
        double xMove = endPoint.x - upperLeft.x;
        double yMove = endPoint.y - upperLeft.y;

        ySpeed = yMove / 45;
        xSpeed = xMove / 45;
    }

    /**
     * Draw the runner at its current location.
     *
     * @param g the Graphics object on which the runner should be drawn
     */
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval((int)upperLeft.x, (int)upperLeft.y, 20, 20);
    }
    /**
     * Checks if the runner is within 5 pixels of the base
     * @param s runner
     * @param e base
     * @return if runner is near the base
     */
    public boolean near(Point2D.Double s, Point2D.Double e) {
        if (s.x > e.x - 5 && s.x < e.x + 5) {
            if (s.y > e.y - 5 && s.y < e.y + 5)
                return true;
        }
        return false;
    }

    /**
     * This object's run method, which manages the life of the runner
     */
    @Override
    public void run() {

        // the run method is what runs in this object's thread for the
        // time it is "alive"

        // this runners life as a thread will continue as long
        //as the runner isnt near the base (within 5 pixels)
        while (!near(upperLeft, endPoint)) {

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the runner toward the next base
            
            upperLeft.setLocation(upperLeft.x + xSpeed, upperLeft.y + ySpeed);
            
            // if we want to see the runner move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }

        done = true;
    }

}
