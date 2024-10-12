import java.awt.Graphics;
import javax.swing.JComponent;

/**
 * An abstract class that provides some instance variables and
 * default versions of methods that are needed by threaded objects
 * representing animated graphics objects.
 * 
 * @author Jim Teresco, modified by Justin Montagne, Sam Goldstein, Arizona Belden, and Anthony Russo
 * @version Spring 2022
 */
public abstract class AnimatedGraphicsObject extends Thread {

    /**
     * Is the animation done, meaning the object can safely
     * never be drawn again?
     */
    protected boolean done;

    /** The container on which we will call repaint after changes are made */
    protected JComponent container;

    /**
     * Construct an AnimatedGraphicsObject. All derived classes must
     * call this as a superconstructor or explicitly set the container
     * variable.
     * 
     * @param container the Swing component on which we will need to
     *                  call repaint when the object needs to be redrawn
     */
    public AnimatedGraphicsObject(JComponent container) {

        this.container = container;
    }

    /**
     * A utility method to have the thread sleep without the need to
     * place the call in a try-catch block.
     * 
     * @param millis the number of milliseconds for the thread to sleep
     */
    public static void sleepWithCatch(long millis) {

        try {
            sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Accessor method to check the value of the done variable.
     * The done variable should only be set to true when it this
     * object is guaranteed never to need to be painted again.
     * 
     * @return whether this object's lifetime is done and can safely
     *         never be painted again
     */
    public boolean done() {

        return done;
    }

    /**
     * Required paint method that will be called when this graphical
     * object needs to be drawn on the given Graphics object.
     * 
     * @param g the Graphics object in which to draw
     */
    public abstract void paint(Graphics g);

    /**
     * Abstract run method to ensure that derived classes override
     * the default provided by Thread.
     */
    @Override
    public abstract void run();

    
  

       


}

