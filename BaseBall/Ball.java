import java.awt.*;
import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * The Ball class is in charge of the pitch being dropped from the pitchers mound
 * 
 * @author Justin Montagne, Sam Goldstein, Arizona Belden, and Anthony Russo
 * @version Spring 2022
 */
 class Ball extends AnimatedGraphicsObject{
      // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    // pixels to move each frame
    public int ySpeed= 4;

    // latest location of the ball
    private Point2D.Double upperLeft;

    // Bottom of the panel
    private int bottom;

    //Image of the baseball
    private static Image baseballPic;

    //File name for the baseball
    private static final String ballPicFilename = "baseball.gif";



    //container (or panel in this case)
    private JComponent container;

    /**
     * Construct a new Ball object.
     * 
     * @param startTopCenter the initial Point2D.Double at which the top of the
     *                       ball should be drawn
     * @param container      the Swing component in which this ball is being
     *                       drawn to allow it to call that component's repaint
     *                       method
     */
    public Ball(Point2D.Double startTopCenter, JComponent container, int ySpeed) {
        super(container);
        Random r = new Random();

        //Original point is based on the center of the ball so calcuate the upperleft corner
        upperLeft = new Point2D.Double(startTopCenter.x - 100 / 2, startTopCenter.y);
        this.bottom = container.getHeight();
        this.container = container;
        this.ySpeed = r.nextInt(9) + 5;
    }

    /**
     * Draw the ball at its current location.
     * 
     * @param g the Graphics object on which the ball should be drawn
     */
    public void paint(Graphics g) {

       g.drawImage(baseballPic, (int)upperLeft.x, (int)upperLeft.y, null);

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
        while (upperLeft.y < bottom) {

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the ball down
            // by a pixel
            upperLeft.setLocation(upperLeft.x, upperLeft.y + ySpeed);

            container.repaint();
        }

        done = true;
    }
  /**
     * Set the Image to be used by all Ball objects, to be
     * called by the main method before the GUI gets set up
     */
    public static void loadBallPic() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();

         Ball.baseballPic = toolkit.getImage(ballPicFilename).getScaledInstance(30, 30, Image.SCALE_DEFAULT);


    }

    /**
     * Gets the location of the ball
     * This is used to check which zone the ball is in
     */
    public Point2D.Double getLocation() {

       

        return new Point2D.Double(upperLeft.x + 15, upperLeft.y + 15);
    }




}