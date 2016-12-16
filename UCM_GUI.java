//********************************************************************
//  UCM_GUI.java       Author: Brian Davis   12/26/2012
//
//  Physics applet for uniform circular motion.
//********************************************************************

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.UIManager.*;
import java.text.DecimalFormat;

public class UCM_GUI extends JFrame implements ChangeListener, ActionListener, ComponentListener, WindowListener
{
   	private JPanel primary, controls, sandbox, animation;
   	private JSlider velocity, mass, radius, framesPerSecond;
   	private JLabel vLabel, mLabel, rLabel, fLabel, fpsLabel;
   	private JTextField force;
   	private JButton startButton, stopButton;
   	private javax.swing.Timer timer;
   	private int x, y, r, theta, v, m;
   	private double f, delay;
   	private int myWidth = 800, myHeight = 600; // defaults; can be resized
   	private int CONTROLS = 150, sWidth = myWidth - CONTROLS; // to adjust for controls + sandbox splitting width
   	private boolean timerUponRestart = false;
   	private DecimalFormat format = new DecimalFormat("#0.00"); //TODO: add jitter to force calculation?

	public static void main (String[] args)
   	{      
      	UCM_GUI gui = new UCM_GUI("Uniform Circular Motion");
   	}
   	
   	public UCM_GUI(String title)
   	{
   		super(title);
   		
		//TODO: set the values to take doubles and be reasonable (i.e., in this calculation do it in cm/s, but display m/s)
   		velocity = new JSlider(JSlider.VERTICAL, 1, 10, 5); //(h/v, min, max, initial)
   		velocity.setMajorTickSpacing(9);
        velocity.setMinorTickSpacing(1);
        velocity.setPaintTicks(true);
        velocity.setPaintLabels(true);
        velocity.setPreferredSize(new Dimension(75,myHeight/4));
        velocity.addChangeListener(this);
   		
   		mass = new JSlider(JSlider.VERTICAL, 1, 10, 5);
   		mass.setMajorTickSpacing(9);
        mass.setMinorTickSpacing(1);
        mass.setPaintTicks(true);
        mass.setPaintLabels(true);
        mass.setPreferredSize(new Dimension(75,myHeight/4));
        mass.addChangeListener(this);
   		
   		radius = new JSlider(JSlider.VERTICAL, 1, 10, 5);
   		radius.setMajorTickSpacing(9);
        radius.setMinorTickSpacing(1);
        radius.setPaintTicks(true);
        radius.setPaintLabels(true);
        radius.setPreferredSize(new Dimension(75,myHeight/4));
        radius.addChangeListener(this);
        
        framesPerSecond = new JSlider(JSlider.HORIZONTAL, 10, 60, 24);
   		framesPerSecond.setMajorTickSpacing(10);
        framesPerSecond.setMinorTickSpacing(1);
        framesPerSecond.setPaintTicks(true);
        framesPerSecond.setPaintLabels(true);
        framesPerSecond.addChangeListener(this);
 
        vLabel = new JLabel("Velocity (m/s)", JLabel.CENTER);
        vLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mLabel = new JLabel("Mass (kg)", JLabel.CENTER);
        mLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rLabel = new JLabel("Radius (m)", JLabel.CENTER);
        rLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fLabel = new JLabel("Force (N)", JLabel.CENTER);      
        fpsLabel = new JLabel("Frames Per Second", JLabel.CENTER);
        fpsLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        Font font = new Font("Serif", Font.PLAIN, 25);
        velocity.setFont(font);
        vLabel.setFont(font);
        mass.setFont(font);
        mLabel.setFont(font);
        radius.setFont(font);
        rLabel.setFont(font);
        fLabel.setFont(font);
        //framesPerSecond.setFont(font);
        //fLabel.setFont(font);
   		
	    controls = new JPanel();
	    controls.setPreferredSize(new Dimension(CONTROLS,myHeight));
	    controls.setBorder(BorderFactory.createLineBorder(Color.black));
		controls.add(vLabel);
	    controls.add(velocity);
	    controls.add(mLabel);
	    controls.add(mass);
	    controls.add(rLabel);
	    controls.add(radius);
	    
	    startButton = new JButton("Start animation");
	    stopButton = new JButton("Stop animation");
	    startButton.addActionListener(this);
	    stopButton.addActionListener(this);
	    force = new JTextField(" --- ", 5);
	    
	    animation = new JPanel();
	    animation.setPreferredSize(new Dimension(sWidth,100));
	    animation.add(startButton);
		animation.add(stopButton);
		animation.add(fpsLabel);
	    animation.add(framesPerSecond);
	    animation.add(fLabel);
	    animation.add(force);
	    animation.setOpaque(false);
	    
	    sandbox = new DrawPanel();
	    sandbox.add(animation);
		
      	primary = new JPanel();
      	primary.setPreferredSize(new Dimension(myWidth,myHeight));
      	primary.setLayout(new BorderLayout());
      	primary.add(controls, BorderLayout.WEST);
      	primary.add(sandbox, BorderLayout.CENTER);
      	
      	updatePhysics(); //This loads the values from the sliders
      	timer = new Timer((int)delay, this);
      	sandbox.repaint();
      	  		
   		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
   		getContentPane().add(primary);
      	primary.addComponentListener(this); // to handle resizing
      	addWindowListener(this); // to pause upon minimizing to save memory
      	pack();
      	setVisible(true);
   	}

   	// this method loads the values from the sliders.
   	public void updatePhysics()
   	{
   		v = velocity.getValue();
		m = mass.getValue();
		r = radius.getValue();
		delay = 1000/framesPerSecond.getValue();
		
		f = 1.0*m*v*v/r; // calculate centripetal force
		force.setText(format.format(f));
   	}
    
    // advances theta one ... well, one d theta for each dt
  	public void animate()
   	{   	
		updatePhysics();
		
		// Update position
		theta = (theta + (int)(v*delay/r)) % 360; // v = r*dtheta/dt, so dtheta is v*dt/r.
   		
   		sandbox.repaint();
   	}

   	// Listener for sliders
  	public void stateChanged(ChangeEvent e) 
  	{
    	updatePhysics();
		timer.setDelay((int)delay);
		
		sandbox.repaint();
	}
   
  	// listener for buttons and timer
   	public void actionPerformed (ActionEvent event)
  	{
     	Object source = event.getSource();
     	
     	if (source == startButton)
     		timer.start();
     	else if (source == stopButton)
     		timer.stop();
     	else if (source == timer)
     		animate();		
  	}

  	// listener for resizing
    public void componentResized(ComponentEvent e)
    {
    	Dimension newSize = this.getContentPane().getSize();
    	myWidth = newSize.width;
    	myHeight = newSize.height;
    	sWidth = myWidth - CONTROLS;
    }
    public void componentShown(ComponentEvent e) {}//unused
    public void componentHidden(ComponentEvent e) {} //unused
    public void componentMoved(ComponentEvent e) {} //unused
    
    // listener for minimizing (to save memory)
    public void	windowDeactivated(WindowEvent e) // Invoked when a Window is no longer the active Window.
 	{
        if (timer.isRunning())
        {
        	timerUponRestart = true;
        	timer.stop();
        }
 	}
    public void windowActivated(WindowEvent e) // Invoked when the Window is set to be the active Window.
	{
	 	if (timerUponRestart)
    		timer.start();
	} 
	public void	windowDeiconified(WindowEvent e) // Invoked when a window is changed from a minimized to a normal state.
 	{
 		windowActivated(e);
 	}
 	public void	windowIconified(WindowEvent e) // Invoked when a window is changed from a normal to a minimized state.
 	{
 		windowDeactivated(e);
 	}	
 	public void	windowClosed(WindowEvent e) {} // Invoked when a window has been closed as the result of calling dispose on the window.
 	public void	windowClosing(WindowEvent e) {} // Invoked when the user attempts to close the window from the window's system menu.
 	public void	windowOpened(WindowEvent e) {} // Invoked the first time a window is made visible.
	{
		//TODO: pop up "click me" button
	}
          
   	//drawing space
	private class DrawPanel extends JPanel 
	{
		// drawing constants
		double ecc = 0.33; //eccentricity of ellipse (0.5 or 0.33 is isometric view?)
		int scale = 50; // to make the radius show up better on the screen
		int weight = 4; // to make the orbiting mass show up better
		int hangerWeight = 20; // to make the hanging weights show up better
		Color darkGray = new Color(50,50,0);
		Color brown = new Color(165,42,42);
		Color darkBrown = new Color(120,30,30);
		Color darkGold = new Color(210,129,21);
		Color gold = new Color (255,215,0);	
		Color background = new Color(0,125,225);		
		int tubeHeight = 200; 
		int tubeWidth = 15;
		int hangerWidth = 36; 
		int postWidth = 4;
		int hook = 55, hookSpace = 35, realHook = hook - hookSpace;
		int washerHeight = 7, medHeight = 5, tinyHeight = 3;
		
		// drawing/coordinate variables
		int orbitCenterX, orbitCenterY, tubeLeft, tubeRight, tubeBottom, postLeft, postHeight;
		int hangerHeight, hangerBottom, littleX, littleY, numTiny, numMed, numBig;
		int roomTiny, roomMed, roomBig, roomToHang, mediumStart, tinyStart, tinyFinish;
		double radx, rady, sine, cosine;
		
	    @Override
	    public void paintComponent(Graphics g) 
	    {
	    	// erase to start fresh!
	        super.paintComponent(g);
	        this.setBackground(background);
	        
	        // update size variables
	        numBig = (int)(f / 100); // for each 100 N add big weight
	        numMed = (int)((f % 100) / 10); // for each 10 N left over, add medium weight
	        numTiny = (int)((f % 10) / 1); // for each 1 N left over, add tiny weight
	        roomBig = numBig * (washerHeight-2); //Not sure why this is the case. It's a fudge factor but it works.
	        roomMed = numMed * medHeight;
	        roomTiny = numTiny * tinyHeight;
	        roomToHang = roomBig + roomMed + roomTiny;
	        orbitCenterY = myHeight/3; 
			orbitCenterX = sWidth/2;
		 	tubeLeft = orbitCenterX-tubeWidth/2;
		 	tubeRight = orbitCenterX+tubeWidth/2;
		 	tubeBottom = orbitCenterY+tubeHeight;		 			 	
	        hangerHeight = 120;
			hangerBottom = tubeBottom+hangerHeight;
			mediumStart = hangerBottom-roomBig;
			tinyStart = mediumStart-roomMed;
			tinyFinish = tinyStart-roomTiny;
			postLeft = orbitCenterX-postWidth/2;
			postHeight = hangerHeight - hook;
			
	        // for drawing thick lines
	        Graphics2D g2 = (Graphics2D) g; 

			// calculate position of orbiting mass
			sine = Math.sin(theta*Math.PI/180.0);
			cosine = Math.cos(theta*Math.PI/180.0);
			x = orbitCenterX + (int)(r*scale*cosine); 
			y = orbitCenterY - (int)(r*scale*sine*ecc); // java y is reversed
			
			// draw mass hanger and masses
			// hanger
			drawThickWasher(orbitCenterX,hangerBottom+washerHeight/2,hangerWidth,washerHeight,brown,darkGold,brown,g);// hanger bottom
			// hook
			g.setColor(brown);			
        	g2.setStroke(new BasicStroke(3));
			g2.drawArc(tubeLeft-1,tubeBottom+hookSpace,tubeWidth,realHook,-90,250); //hook body (thick Arc)
			g2.setStroke(new BasicStroke(1));
			g.setColor(darkGold);
			g.drawArc(tubeLeft-3,tubeBottom+hookSpace-2,tubeWidth+3,realHook+3,-80,250); // outside outline
			g.setColor(darkBrown);
			g.drawArc(tubeLeft-1,tubeBottom+hookSpace+1,tubeWidth-2,realHook-3,-110,270); // inside outline
			// string
			g.setColor(Color.black);
			g.drawLine(orbitCenterX,tubeBottom,orbitCenterX,tubeBottom+hookSpace);
			// masses
			for (int count = 0; count < numBig; count++)
				drawThickWasher(orbitCenterX,hangerBottom-count*washerHeight,(int)(hangerWeight*1.5)-1,washerHeight,Color.lightGray,darkGray,brown,g);
			for (int count = 0; count < numMed; count++)
				drawThickWasher(orbitCenterX,mediumStart-count*medHeight,hangerWeight-1,medHeight,Color.lightGray,darkGray,brown,g);
			for (int count = 0; count < numTiny; count++)
				drawThickWasher(orbitCenterX,tinyStart-count*tinyHeight,(int)(hangerWeight*0.5)-1,tinyHeight,Color.lightGray,darkGray,brown,g);
			// hanger post
			g.setColor(brown); // post body
			g.fillRect(postLeft,tubeBottom+hook,postWidth,postHeight-roomToHang+4);
			g.fillOval(postLeft,tinyFinish-(int)(postWidth*ecc/2),postWidth,(int)(postWidth*ecc+1));
			g.setColor(darkGold);
			//g.drawArc(postLeft-1,tinyFinish-(int)(postWidth*ecc/2),postWidth,(int)(postWidth*ecc),180,180);
			g.drawLine(postLeft-1,tubeBottom+hook,postLeft-1,tinyFinish+2);
			g.drawLine(postLeft+postWidth,tubeBottom+hook,postLeft+postWidth,tinyFinish+2);
			
			// draw white tube: first bottom circle, then rectangle, then outlines.
			// top circle drawn later (at the very end) to make it look less stupid
			g.setColor(Color.white);
			g.fillOval(tubeLeft,tubeBottom-(int)(tubeWidth*ecc/2),tubeWidth,(int)(tubeWidth*ecc));
			g.fillRect(tubeLeft,orbitCenterY,tubeWidth,tubeHeight);
			g.setColor(Color.black);
			g.drawArc(tubeLeft-1,tubeBottom-(int)(tubeWidth*ecc/2),tubeWidth,(int)(tubeWidth*ecc),180,180);	
			g.drawLine(tubeLeft-1,orbitCenterY,orbitCenterX-tubeWidth/2-1,tubeBottom);
			g.drawLine(tubeRight,orbitCenterY,tubeRight,tubeBottom);
						
			// draw orbiting mass
			drawThickWasher(x,y,m*weight,washerHeight,Color.lightGray,darkGray,background,g);
			//g.drawLine(x,y,x+(int)(radx/Math.sqrt(2)),y-(int)(rady/Math.sqrt(2))); //  string around washer	
			
			// string to middle 
			g.drawLine(x,y,orbitCenterX,orbitCenterY);	
			g.setColor(Color.lightGray);
			g.fillOval(tubeLeft,orbitCenterY-(int)(tubeWidth*ecc/2),tubeWidth,(int)(tubeWidth*ecc));
			g.setColor(Color.black);
			g.drawOval(tubeLeft-1,orbitCenterY-(int)(tubeWidth*ecc/2),tubeWidth,(int)(tubeWidth*ecc));
			littleX = orbitCenterX + (int)(tubeWidth*cosine);
			littleY = orbitCenterY - (int)(tubeWidth*sine*ecc); // java y is reversed
			g.drawLine(littleX,littleY,orbitCenterX,orbitCenterY+(int)(tubeWidth/2*ecc)); // where it intersects the arc to the middle
	    }
	    
	    private void drawThickWasher (int centerX, int centerY, int width, int thickness, Color color, Color outlineColor, Color holeColor, Graphics g)
	    {
	    	radx = width;
			rady = radx*ecc;
			for (int i=thickness/2+1;i>=0;i--)
			{
				g.setColor(color);
				g.fillOval((int)(centerX-radx),(int)(centerY-rady+i),(int)(radx*2),(int)(rady*2)); // washer body
				g.setColor(outlineColor);
				g.drawOval((int)(centerX-radx),(int)(centerY-rady+i),(int)(radx*2),(int)(rady*2)); // washer outline
			}
			g.setColor(holeColor);
			g.fillOval((int)(centerX-radx/6),(int)(centerY-rady/6),(int)(radx/3),(int)(rady/3)); // donut hole
			g.setColor(outlineColor);
			g.drawOval((int)(centerX-radx/6),(int)(centerY-rady/6),(int)(radx/3),(int)(rady/3)); // donut hole outline
	    	
	    }
	}
}