import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.geom.Point2D;

/**
 * A program to create a baseball game
 * 
 * @author Justin Montagne, Sam Goldstein, Arizona Belden, and Anthony Russo
 * @version Spring 2022
 */

public class BaseballGame extends MouseAdapter implements Runnable, ActionListener, KeyListener {

	// The message to be displayed during an event
	private String displayText = "";

	// list of Animated objects currently in existence
	private java.util.List<AnimatedGraphicsObject> list;

	//Game panel
	private JPanel panel;

	//Scoreboard panel
	private JPanel scoreBoard;

	//Team 1 (Away team) score
	private int[] team1Score = new int[6];

	//Team 2 (Home team) score
	private int[] team2Score = new int[6];

	//Checks if a runner should be drawn on this base
	private boolean[] runnerCheck = new boolean[3];

	//Points for the bases
	private static final Point2D.Double firstBase = new Point2D.Double(480, 475);
	private static final Point2D.Double secondBase = new Point2D.Double(390, 370);
	private static final Point2D.Double thirdBase = new Point2D.Double(290, 475);
	private static final Point2D.Double homePlate = new Point2D.Double(385, 585);

	//Points for the fielders
	private static final Point2D.Double LEFT_FIELDER = new Point2D.Double(230, 250);
	private static final Point2D.Double CENTER_FIELDER = new Point2D.Double(390, 200);
	private static final Point2D.Double RIGHT_FIELDER = new Point2D.Double(550, 250);

	//Number of outs
	private int outs;

	//Number of strikes
	private int strikes;

	//The team (1 = away, 2 = home)
	private int team;
	//The current inning
	private int curInning;
	//Number of extra bases the runners should move
	private int numBases;
	//Labels for the scoreboard
	private JLabel labels[][] = new JLabel[3][7];
	//Lock for synchronization
	private Object lock = new Object();
	//Check if power swing is enabled
	private boolean power;
	//Check if game is currently active
	private boolean playing;
	//Text for the end of the game
	private String endGameText;

	
	// 2 singles , 2 doubles, 1 triple, 7 outs , 1 hr for each area of the field
	
	private Point2D.Double[] leftField = new Point2D.Double[] {
			new Point2D.Double(175, 200), new Point2D.Double(200, 325),
			new Point2D.Double(270, 200), new Point2D.Double(150, 250),
			new Point2D.Double(250, 150),
			new Point2D.Double(250, 350), new Point2D.Double(200, 250), new Point2D.Double(250, 250),
			new Point2D.Double(280, 300), new Point2D.Double(225, 225), new Point2D.Double(275, 225),
			new Point2D.Double(200, 300),
			new Point2D.Double(125, 115) };

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] centerField = new Point2D.Double[] {
			new Point2D.Double(450, 250), new Point2D.Double(325, 225),
			new Point2D.Double(350, 175), new Point2D.Double(455, 165),
			new Point2D.Double(425, 100),
			new Point2D.Double(435, 240), new Point2D.Double(365, 230), new Point2D.Double(425, 165),
			new Point2D.Double(400, 200), new Point2D.Double(375, 300), new Point2D.Double(400, 250),
			new Point2D.Double(400, 150),
			new Point2D.Double(385, 0) };

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] rightField = new Point2D.Double[] {
			new Point2D.Double(570, 325), new Point2D.Double(500, 325),
			new Point2D.Double(600, 250), new Point2D.Double(500, 225),
			new Point2D.Double(520, 150),
			new Point2D.Double(520, 350), new Point2D.Double(520, 250), new Point2D.Double(570, 250),
			new Point2D.Double(530, 275), new Point2D.Double(550, 250), new Point2D.Double(535, 300),
			new Point2D.Double(500, 250),
			new Point2D.Double(660, 115) };

	//Image of the field
	private Image field;
	//Number of clicks
	private int clickCount;
	//Should the runners be drawn on the bases?
	private boolean draw;
	//Which zone the player hit in
	private int location;
	//Should the fielders be drawn?
	private boolean drawFielders;
	//Should the teams be changed after animations are done?
	private boolean teamChange;
	//Color of the fielders and runners
	private Color fielderColor;
	private Color runnerColor;
	//Start menu stuff
	private JPanel startMenu;
	private JComboBox<String> colors;
	private JComboBox<String> colors2;
	private JTextField team1Name;
	private JTextField team2Name;
	private JPanel startButton;
	private JButton start;
	private JPanel startPanel;
	private JFrame startFrame;

	//Frame for the game
	private JFrame frame;

	//Option menu stuff
	private JFrame optionFrame;
	private JButton continueGame;
	private JPanel optionPanel;
	private JButton skipInning;
	private JButton endGame;

	//Name of each team
	private String teamName1;
	private String teamName2;

	//Color of each team
	private Color team1Color;
	private Color team2Color;
	//Check if the fielder should be drawn
	private boolean isOut;
	//Check if the runner was caught while stealing
	private boolean caughtRunning;
	//Which runner was caught
	private int runnerCaught;
	//String for the teams changing
	private String colorSwap;


	public BaseballGame() {
		//Construct labels
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 3; j++) {
				labels[j][i] = new JLabel();
			}
		}
		//Get the field image
		try {
			field = ImageIO.read(new File("Field.jpg")).getScaledInstance(800, 800, Image.SCALE_DEFAULT);
			;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		//Initializatin of variables
		for (int j = 0; j < 3; j++) {
			runnerCheck[j] = false;
		}
		numBases = 0;
		draw = false;
		drawFielders = false;
		team = 1;
		curInning = 0;
		teamChange = false;
		playing = true;
		colorSwap = "";

	}

	/**
	 * The run method to set up the graphical user interface
	 */
	@Override
	public void run() {

		// set up the GUI "look and feel" which should match
		// the OS on which we are running
		JFrame.setDefaultLookAndFeelDecorated(true);

		
		frame = new JFrame("BaseballGame");
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		startFrame = new JFrame("Baseball Options");
		startFrame.setPreferredSize(new Dimension(600, 600));
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		optionFrame = new JFrame("Game Paused");
		optionFrame.setPreferredSize(new Dimension(400, 400));
		optionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Creating the start menu
		team1Name = new JTextField("Enter Away Team Name");
		team2Name = new JTextField("Enter Home Team Name");
		String[] colorOptions = new String[] { "BLUE", "RED", "MAGENTA", "PINK", "GREEN", "ORANGE", "YELLOW" };
		colors = new JComboBox<>(colorOptions);
		colors2 = new JComboBox<>(colorOptions);

		startPanel = new JPanel(new GridLayout(2, 1));

		startMenu = new JPanel(new GridLayout(4, 2));
		startMenu.add(new JLabel("Away Team Options"));
		startMenu.add(new JLabel("Home Team Options"));
		startMenu.add(team1Name);
		startMenu.add(team2Name);
		startMenu.add(new JLabel("Select Color")).setPreferredSize(new Dimension(400, 20));
		startMenu.add(new JLabel("Select Color")).setPreferredSize(new Dimension(400, 20));

		startMenu.add(colors);
		startMenu.add(colors2);
		startFrame.add(startPanel);
		startButton = new JPanel();
		start = new JButton("Play Ball!");
		startPanel.add(startMenu);
		startPanel.add(startButton);

		startButton.add(start);

		start.addActionListener(this);
		team1Name.addActionListener(this);
		team2Name.addActionListener(this);
		colors2.addActionListener(this);
		colors.addActionListener(this);
		colors.setSelectedItem("BLUE");
		colors.setSelectedItem("RED");

		//Creating the pause menu
		optionPanel = new JPanel(new GridLayout(4, 1));
		continueGame = new JButton("Resume");
		endGame = new JButton("End Game");
		skipInning = new JButton("Advance Inning");
		optionFrame.add(optionPanel);
		JLabel pause = new JLabel("Game Paused");
		pause.setHorizontalAlignment(JLabel.CENTER);
		optionPanel.add(pause);
		optionPanel.add(skipInning);
		optionPanel.add(endGame);
		optionPanel.add(continueGame);
		skipInning.addActionListener(this);
		endGame.addActionListener(this);
		continueGame.addActionListener(this);

		// JPanel with a paintComponent method
		panel = new JPanel(new BorderLayout()) {
			@Override
			public void paintComponent(Graphics g) {

				
				super.paintComponent(g);
				g.drawImage(field, 0, 0, null);

				// border
				g.setColor(Color.BLACK);
				g.drawRect(369, 600, 60, 120);
				if (power == false) {
					// red - early - left
					g.setColor(new Color(230, 0, 0, 100));
					g.fillRect(369, 600, 60, 40);
					// blue - late - right	
					g.setColor(new Color(0, 0, 222, 100));
					g.fillRect(369, 680, 60, 40);
				}
				// Green - middle - center
				g.setColor(new Color(0, 234, 0, 100));
				g.fillRect(369, 640, 60, 40);

				

				// redraw each Animated object's contents, and along the
				// way, remove the ones that are done

				synchronized (lock) {
					int i = 0;
					while (i < list.size()) {
						AnimatedGraphicsObject b = list.get(i);
						if (b.done()) {
							list.remove(i);
						} else {
							b.paint(g);
							i++;
						}
					}
				}
				// checks if the list is empty, redraws the fielders and runners
				if (list.size() == 0) {
					draw = true;
					drawFielders = false;
					isOut = false;
					// checks if the hit required the runners to move more
					if (numBases > 0 && !caughtRunning) {
						numBases--;
						moveRunner();
					//Checks if runners should move but not stay at the end location
					} else if (numBases > 0 && caughtRunning) {
						numBases--;
						caughtRunner(runnerCaught);
						if (numBases == 0) {
							incrementOut();
						} 
					}
					//If no second click set click count back to 0 and then increment the strikes
					if (clickCount == 1) {
						clickCount = 0;
						incrementStrike();
					}
				}
				//If the team should change wait until list size is 0 (All animations done)
				//and then change the color of everything
				if (teamChange) {
					if (list.size() == 0) {
						if (team == 1)
							team = 2;
						else
							team = 1;
						teamChange = false;
					}
				}

				//Sets the color of the fielders and runners to the selected colors
				if (team == 1) {
					runnerColor = team1Color;
					fielderColor = team2Color;
				} else {
					runnerColor = team2Color;
					fielderColor = team1Color;
				}
				//Draws the fielders on the screen
				g.setColor(fielderColor);
				if (drawFielders) {

					if (location == 1 && isOut) {
						g.fillOval(390, 200, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else if (location == 2 & isOut) {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else if (location == 3 & isOut) {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(390, 200, 20, 20);
					} else {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(390, 200, 20, 20);
						g.fillOval(550, 250, 20, 20);
					}
				} else {
					g.fillOval(230, 250, 20, 20);
					g.fillOval(390, 200, 20, 20);
					g.fillOval(550, 250, 20, 20);
				}

				//Draws the runners on the field
				g.setColor(runnerColor);
				if (draw) {

					if (runnerCheck[0]) {
						g.fillOval((int) firstBase.x, (int) firstBase.y, 20, 20);
					}
					if (runnerCheck[1]) {
						g.fillOval((int) secondBase.x, (int) secondBase.y, 20, 20);
					}
					if (runnerCheck[2]) {
						g.fillOval((int) thirdBase.x, (int) thirdBase.y, 20, 20);
					}
				}

				// Checks if list isnt empty to display a message of what happened in that at bat.

				g.setColor(Color.black);
				FontMetrics str = g.getFontMetrics();
				Font newFont = new Font("arial", Font.BOLD, 25);
				int pHeight = this.getHeight();
				int pWidth = this.getWidth();
				int strWidth = str.stringWidth(displayText);
				int ascent = (str.getAscent());
				g.setFont(newFont);
				if (list.size() != 0) {
					int x = 0;
					if (displayText.equals("Out!")) {
						x = 652;

					} else if (displayText.equals("Single!")) {
						x = 635;
					} else if (displayText.equals("Double!")) {
						x = 631;
					} else if (displayText.equals("Triple!")) {
						x = 638;
					} else if (displayText.equals("Homerun!")) {
						x = 620;
					}

					g.drawString(displayText, x, 670 + (100 - ascent) / 2);
				} else {
					displayText = "";
				}
				// Team change
				newFont = new Font("arial", Font.BOLD, 17);
				g.setFont(newFont);
				if (!colorSwap.equals("") && list.size() == 0) {

					g.drawString(colorSwap, 610, 670 + (100 - ascent) / 2);
				}

				//On screen text that is always there
				newFont = new Font("arial", Font.BOLD, 18);
				g.setFont(newFont);

				g.drawString("Outs: " + outs, 15, 75);
				g.drawString("Strikes: " + strikes, 15, 100);
				g.drawString("Controls", 15, 675);
				g.drawString("Pause - E", 15, 700);
				g.drawString("Power - P", 15, 725);
				g.drawString("Steal - S", 15, 750);

				//Box for the contols and the area that displays what happened in the play
				Graphics2D menueBox = (Graphics2D) g;
				menueBox.setStroke(new BasicStroke(5));

				menueBox.drawRect(10, 655, 100, 100);

				// right box - status
				menueBox.drawRect(600, 655, 160, 100);

				//If the game is over, display a message as to who won
				if (!playing) {
					newFont = new Font("Arial", Font.BOLD, 30);
					g.setFont(newFont);

					strWidth = str.stringWidth(endGameText);
					g.drawString(endGameText, pWidth / 2 - (strWidth / 2) - 100, pHeight / 2 -
							ascent);

					strWidth = str.stringWidth("Click anywhere to play again!");
					g.drawString("Click anywhere to play again!", pWidth / 2 - (strWidth / 2) - 120, pHeight / 2 -
							ascent + 40);
				}

				//Set the labels back to default
				for (int i = 0; i < 7; i++) {
					labels[0][i].setBackground(Color.white);
					labels[1][i].setBorder(BorderFactory.createLineBorder(Color.black));
					labels[2][i].setBorder(BorderFactory.createLineBorder(Color.black));
				}
				//Sets the inning background color in the scoreboard to green
				labels[0][curInning + 1].setBackground(Color.green);
				//Sets the border of the current half inninng we are in
				if (!teamChange) {
					if (team == 1) {
						labels[1][curInning + 1].setBorder(BorderFactory.createLineBorder(team1Color, 2));
					} else {
						labels[2][curInning + 1].setBorder(BorderFactory.createLineBorder(team2Color, 2));
					}
				} else {
					//Makes sure it doesn't change which box has a border to early
					labels[1][curInning + 1].setBorder(BorderFactory.createLineBorder(team1Color, 2));
				}

			}
		};
		//Addig everything to the game
		scoreBoard = new JPanel(new GridLayout(3, 7));
		scoreBoard.setOpaque(true);
		frame.add(panel);
		panel.add(scoreBoard, BorderLayout.NORTH);

		//Initializing all the text
		for (int i = 1; i < 6; i++) {
			labels[0][i].setText("" + i);
		}
		labels[0][6].setText("Runs");
		for (int i = 1; i < 7; i++) {
			labels[1][i].setText("" + 0);
			labels[2][i].setText("" + 0);
		}
		
		//Intialize the first column of text
		labels[0][0].setText("Inning");
		labels[1][0].setText("" + teamName1);
		labels[2][0].setText("" + teamName2);

		//Sets the look of the labels
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 7; j++) {
				scoreBoard.add(labels[i][j]);
				labels[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				labels[i][j].setOpaque(true);
				labels[i][j].setBackground(Color.white);

			}
		}
		
		//Puts a border around the scoreboard
		scoreBoard.setBorder(BorderFactory.createLineBorder(Color.black));

		//Mouse listener and key listener
		panel.addMouseListener(this);
		frame.addKeyListener(this);

		// construct the list
		list = new ArrayList<AnimatedGraphicsObject>();

		
		frame.pack();

		frame.setResizable(false);
		startFrame.pack();
		startFrame.setVisible(true);
		optionFrame.pack();

	}

	/**
	 * mousePressed method first checks if the game is going.
	 * Game is being played:
	 * 		First click starts the pitch, second click is the swing
	 * Game is not being played:
	 * 		Click restarts the game
	 * 
	 * @param e mouse event info
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		//Check if the game is currently playing
		if (playing) {

			colorSwap = "";
			// ball drops from the pitchers mound
			if (list.size() == 0) {

				caughtRunning = false;
				Ball newBall = new Ball(new Point2D.Double(433, 470), panel, 5);
				list.add(newBall);

				//Starting the ball
				newBall.start();
				panel.repaint();
				clickCount = 1;

			} else if (list.size() > 0 && clickCount == 1) {
				// mouse clicked for second time while ball is in screen

				Random r = new Random();

				//Check which zone the ball was in on second click
				location = contains(((Ball) list.get(0)).getLocation());

				//Generate the number that will be used to determine a hit or not
				int hit;
				drawFielders = true;
				if (power == true) {
					hit = r.nextInt(4) + 9;
				} else {
					hit = r.nextInt(13);
				}

				//If the ball was hit in the first zone
				if (location == 1) {
					strikes = 0;
					//Hit the ball to leftfield at the random hit spot
					Hit newHit = new Hit(new Point2D.Double(385, 600), panel, leftField[hit]);

					list.add(newHit);
					newHit.start();
					panel.repaint();

					//If the number is 0 or 1, single
					if (hit < 2) {
						displayText = "Single!";
						moveRunner();
						numBases = 0;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is 2 or 3, double
					} else if (hit < 4) {
						displayText = "Double!";
						moveRunner();
						numBases = 1;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is 4, triple
					} else if (hit == 4) {
						displayText = "Triple!";
						moveRunner();
						numBases = 2;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is between 5 and 11, out
					} else if (hit < 12) {
						incrementOut();
						displayText = "Out!";
						Fielder newFielder = new Fielder(LEFT_FIELDER, panel, leftField[hit], fielderColor);
						list.add(newFielder);
						newFielder.start();
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						isOut = true;
					//If number is 12, Homerun
					} else {
						displayText = "Homerun!";
						moveRunner();
						numBases = 3;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;

					}
					list.get(0).done = true;
					panel.repaint();
				//If ball hit in second zone
				} else if (location == 2) {
					strikes = 0;
					Hit newHit = new Hit(new Point2D.Double(385, 620), panel, centerField[hit]);

					list.add(newHit);
					newHit.start();
					panel.repaint();
					//If the number is 0 or 1, single
					if (hit < 2) {
						displayText = "Single!";
						moveRunner();
						numBases = 0;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is 2 or 3, double
					} else if (hit < 4) {
						displayText = "Double!";
						moveRunner();
						numBases = 1;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is 4, triple
					} else if (hit == 4) {
						displayText = "Triple!";
						moveRunner();
						numBases = 2;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is between 5 and 11, out
					} else if (hit < 12) {
						incrementOut();
						displayText = "Out!";
						Fielder newFielder = new Fielder(CENTER_FIELDER, panel, centerField[hit], fielderColor);
						list.add(newFielder);
						newFielder.start();
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						isOut = true;
					//If number is 12, Homerun
					} else {
						displayText = "Homerun!";
						moveRunner();
						numBases = 3;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;

					}
					list.get(0).done = true;
				//If ball hit in third zone
				} else if (location == 3) {
					strikes = 0;
					Hit newHit = new Hit(new Point2D.Double(385, 660), panel, rightField[hit]);

					list.add(newHit);
					newHit.start();
					panel.repaint();

					//If the number is 0 or 1, single
					if (hit < 2) {
						displayText = "Single!";
						moveRunner();
						numBases = 0;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is 2 or 3, double
					} else if (hit < 4) {
						displayText = "Double!";
						moveRunner();
						numBases = 1;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is 4, triple
					} else if (hit == 4) {
						displayText = "Triple!";
						moveRunner();
						numBases = 2;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;
					//If number is between 5 and 11, out
					} else if (hit < 12) {
						incrementOut();
						displayText = "Out!";
						Fielder newFielder = new Fielder(RIGHT_FIELDER, panel, rightField[hit], fielderColor);
						list.add(newFielder);
						newFielder.start();
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						isOut = true;
					//If number is 12, Homerun
					} else {
						displayText = "Homerun!";
						moveRunner();
						numBases = 3;
						Runner newRunner = new Runner(runnerColor, 0, panel);
						list.add(newRunner);
						newRunner.start();
						runnerCheck[0] = true;

					}

					list.get(0).done = true;
				}
				//If ball not hit in any zone
				else {
					list.get(0).done = true;
					incrementStrike();
				}
				clickCount = 0;
			}

		//If you click when game has ended
		} else {
			playing = true;

			curInning = 0;
			team = 1;
			for (int i = 1; i < 6; i++) {

				labels[1][i].setText("0");
				labels[2][i].setText("0");
			}
			outs = 0;
			strikes = 0;

			for (int i = 0; i < 3; i++) {
				runnerCheck[i] = false;

			}
			panel.repaint();

		}
	}

	/**
	 * The moveRunner method stops the runner from being drawn, 
	 * then starts all the runners moving to the next base
	 */
	public void moveRunner() {
		draw = false;
		//Temp array to mark the next base as true when a runner is moving there
		boolean temp[] = new boolean[3];
		for (int i = runnerCheck.length - 1; i >= 0; i--) {
			if (runnerCheck[i]) {
				runnerCheck[i] = false;
				temp[i] = false;
				Runner curRunner = new Runner(runnerColor, i + 1, panel);
				list.add(curRunner);
				curRunner.start();
				//If the runner is on third
				if (i == 2) {
					if (team == 1) {
						//Not the sixth inning (extra innings)
						if (curInning != 5)
							team1Score[curInning]++;
						//Increment the total score of that team
						team1Score[5]++;
						labels[1][curInning + 1].setText("" + team1Score[curInning]);
						labels[1][6].setText("" + team1Score[5]);
					} else {
						if (curInning != 5)
							team2Score[curInning]++;

						team2Score[5]++;
						labels[2][curInning + 1].setText("" + team2Score[curInning]);
						labels[2][6].setText("" + team2Score[5]);

					}
				}
				//Makes sure i isn't 2 so that it doesn't set home to true
				if (i != 2) {
					temp[i + 1] = true;
				}
			}
		}
		//Transfers over the information from temp to runnerCheck
		for (int i = 0; i < runnerCheck.length; i++) {
			if (temp[i]) {
				runnerCheck[i] = true;
				temp[i] = false;
			}
		}

		panel.repaint();
	}

	/**
	 * The contains method checks which zone the point (or pitch) is in
	 * @param p The point of the pitch when called
	 * @return Which zone the ball was hit in
	 */
	public int contains(Point2D.Double p) {

		int c = 0;

		if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 600 && p.y <= 600 + 40) {

			c = 1;

		} else if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 640 && p.y <= 640 + 40) {
			c = 2;

		} else if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 680 && p.y <= 680 + 40) {

			c = 3;

		}
		//If power, select a random zone to say the ball was in so that the hit can go anywhere
		if (power) {
			if (c != 2) {
				c = 0;
			} else {
				Random r = new Random();
				c = r.nextInt(3) + 1;
			}
		}
		return c;

	}

	/**
	 * Increments the outs but also checks if the inning should change or if the game should end
	 */
	private void incrementOut() {
		//if less than 2 outs, increase the number of outs
		if (outs != 2) {
			outs++;
		//If outs = 2
		} else {
			//Change the team
			teamChange = true;
			//Set power to false so next player starts on default hitting mode
			power = false;
			colorSwap = "Changing Sides!";
			//Checks if we need to switch innnings
			if (team == 2) {
				if (curInning < 4) {
					curInning++;
				} else {
					if (team1Score[5] > team2Score[5]) {
						endGameText = teamName1 + " wins!";
						playing = false;
					} else if (team2Score[5] > team1Score[5]) {
						endGameText = teamName2 + " wins!";
						playing = false;
					} else {
						endGameText = "Game is tied! We're going to extras!";
						curInning = 5;
					}
				}

			} else {
				//Checks if the home team is winning after the away team hit in the fifth inning
				if (curInning == 4) {
					if (team2Score[5] > team1Score[5])
						playing = false;
					endGameText = teamName2 + " wins";
				}
			}
			//If we switch innings set the outs to 0 and gets rid of the runners on base
			outs = 0;
			for (int i = 0; i < runnerCheck.length; i++) {
				runnerCheck[i] = false;
			}
		}
		panel.repaint();
	}

	/**
	 * Increments the strikes and checks if the outs should also increment
	 */
	private void incrementStrike() {
		//If less than 2 strikes increase strikes
		if (strikes != 2) {
			strikes++;
		//Increment outs if strikes = 2
		} else {
			incrementOut();
			strikes = 0;

		}

	}

	/**
	 * Moves the runners if someone was caught stealing, only the lead runner is out
	 * @param runner The runner that shouldn't be drawn on the base after they run
	 */
	private void caughtRunner(int runner) {
		draw = false;
		boolean temp[] = new boolean[3];
		for (int i = runnerCheck.length - 1; i >= 0; i--) {
			if (runnerCheck[i]) {
				runnerCheck[i] = false;
				temp[i] = false;
				Runner curRunner = new Runner(runnerColor, i + 1, panel);
				list.add(curRunner);
				curRunner.start();
				if (i != 2) {
					temp[i + 1] = true;
				}
			}
		}
		for (int i = 0; i < runnerCheck.length; i++) {
			if (temp[i]) {
				if (i == runner + 1) {
					runnerCheck[i] = false;
				} else {
					runnerCheck[i] = true;
				}
				temp[i] = false;
			}
		}
		panel.repaint();
	}

	/**
	 * Checks which button was pressed, sets the team color, sets the team name
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//Start menu stuff
		if (e.getSource().equals(start)) {

			startFrame.setVisible(false);
			frame.requestFocus();
			frame.setVisible(true);

			String c1 = (String) colors.getSelectedItem();
			String c2 = (String) colors2.getSelectedItem();

			switch (c1) {
				case "BLUE":
					team1Color = Color.blue;
					break;
				case "RED":
					team1Color = Color.red;
					break;
				case "MAGENTA":
					team1Color = Color.magenta;
					break;
				case "PINK":
					team1Color = Color.pink;
					break;
				case "YELLOW":
					team1Color = Color.yellow;
					break;
				case "ORANGE":
					team1Color = Color.orange;
					break;
				case "GREEN":
					team1Color = Color.green;
					break;

			}
			switch (c2) {
				case "BLUE":
					team2Color = Color.blue;
					break;
				case "RED":
					team2Color = Color.red;
					break;
				case "MAGENTA":
					team2Color = Color.magenta;
					break;
				case "PINK":
					team2Color = Color.pink;
					break;
				case "YELLOW":
					team2Color = Color.yellow;
					break;
				case "ORANGE":
					team2Color = Color.orange;
					break;
				case "GREEN":
					team2Color = Color.green;
					break;

			}

			if (c1.equals(c2)) {
				team2Color = Color.cyan;
			}

			teamName1 = team1Name.getText();
			if (teamName1.equals("Enter Away Team Name"))
				teamName1 = "Away Team";
			labels[1][0].setText(teamName1);

			teamName2 = team2Name.getText();
			if (teamName2.equals("Enter Home Team Name"))
				teamName2 = "Home Team";
			labels[2][0].setText(teamName2);
		}

		//Pause menu stuff
		if (e.getSource().equals(continueGame)) {
			frame.setVisible(true);
			optionFrame.setVisible(false);
		}
		if (e.getSource().equals(endGame)) {
			curInning = 4;
			team = 2;
			outs = 2;
			if (team1Score[5] == team2Score[5]) {
				endGameText = "Game has ended in a tie!";
				playing = false;
			} else {
				incrementOut();
			}
		}
		if (e.getSource().equals(skipInning)) {
			outs = 2;
			incrementOut();
		}

	}

	/**
	 * keyTyped not used
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * Checks which key was pressed
	 * s - steal runners if theyre on base
	 * p - activate power swing
	 * e - pause
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_S) {
			Random rand = new Random();
			int chance = rand.nextInt(10);

			if (list.size() == 0) {
				//If runner on third, guarenteed to be caught stealing
				if (runnerCheck[2]) {
					chance = 5;
				}
				if (chance < 4) {
					numBases = 1;
				} else {
					for (int i = 2; i > 0; i--) {
						if (runnerCheck[i]) {
							caughtRunning = true;
							runnerCaught = i;
							numBases = 1;
							displayText = "Out!";

							break;
						}
					}
				}
			} 

			panel.repaint();

		} else if (e.getKeyCode() == KeyEvent.VK_E) {
			optionFrame.setVisible(true);
			frame.setVisible(false);

		}

		else if (e.getKeyCode() == KeyEvent.VK_P && list.size() == 0) {
			if (power == true) {
				power = false;
			} else {
				power = true;
			}
			panel.repaint();

		}
	}

	/**
	 * keyReleased not used
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Main method for Baseball Game class
	 *
	 */
	public static void main(String args[]) {

		//Loads the baseball picture
		Ball.loadBallPic();
		Hit.loadBallPic();

		// launch the main thread that will manage the GUI
		javax.swing.SwingUtilities.invokeLater(new BaseballGame());

	}
}
