/*
*	Java Source Code for fast 2D true color animation and games
*	yov408 Technologies Production - http://www.yov408.com
*	This code is free to use and to modify.
*	Applet Double Buffering with Menu
*   ** Some of the source was taken of the Sun Java Tutorials **
*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class essai extends JApplet implements Runnable, MouseMotionListener, ActionListener, ItemListener {

   	Graphics bufferGraphics; 
	Image offscreen; 
	Graphics screen_;

   	int curX=5, curY=5; 
	Thread my_app;
	static JButton button;

	boolean buffer_ready=false;
	
	String newline = "\n";
	
	public void init() {
		button = new JButton("Touch me, please");
		button.setMnemonic(KeyEvent.VK_I);
		button.addActionListener(this);
		addMouseMotionListener(this);
	}

	public void start(){
		my_app = new Thread(this);
		my_app.start();
    }
    
	public void paint(Graphics g) {

		if(bufferGraphics==null){
			setBackground(Color.black); 
       			offscreen = createImage(this.getSize().width,this.getSize().height); 
	  		bufferGraphics = offscreen.getGraphics(); 
	        	screen_ = getGraphics();
			buffer_ready = true;
			System.out.println("Appel du 'paint' de l'applet et graphics nulles");
  	  	}

		System.out.println("Appel du 'paint' de l'applet");
       		bufferGraphics.clearRect(0,0,this.getSize().width,this.getSize().height); 
      		bufferGraphics.setColor(Color.red); 
   		bufferGraphics.drawString("Bad Double-buffered",10,10); 
   		bufferGraphics.fillRect(curX,curY,20,20); 
       	 	g.drawImage(offscreen,0,0,this); 

    }
   

	public void update(Graphics g) 
    	{ 
		System.out.println("Appel de 'update' de l'applet");
         	paint(g); 
    	} 


 
	public void stop(){
		System.out.println("Appel du 'stop' de l'applet");
		my_app = null;
	}




	public void mouseMoved(MouseEvent evt)  
	{ 
        	curX = evt.getX(); 
        	curY = evt.getY(); 
        	Repaindre(screen_); 
    } 
  

	public void mouseDragged(MouseEvent evt)  
     	{ 
     	} 

	public void actionPerformed(ActionEvent e) {
        	System.out.println("Appuie sur le bouton");
	        JMenuItem source = (JMenuItem)(e.getSource());
        	String s = "Action event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")";
		System.out.println(s);		
    }

 
	public void itemStateChanged(ItemEvent e) {
        	JMenuItem source = (JMenuItem)(e.getSource());
        	String s = "Item event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
		System.out.println(s);
    	}

   	public JMenuBar createMenuBar() {
        	JMenuBar menuBar;
        	JMenu menu, submenu;
        	JMenuItem menuItem;
        	JRadioButtonMenuItem rbMenuItem;
        	JCheckBoxMenuItem cbMenuItem;

        	//Create the menu bar.
        	menuBar = new JMenuBar();

        	//Build the first menu.
        	menu = new JMenu("A Menu");
        	menu.setMnemonic(KeyEvent.VK_A);
        	menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        	menuBar.add(menu);

        	//a group of JMenuItems
        	menuItem = new JMenuItem("A text-only menu item",
                                 KeyEvent.VK_T);
        	//menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        	menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        	menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        	menuItem.addActionListener(this);
        	menu.add(menuItem);

        	menuItem = new JMenuItem("Both text and icon");
        	menuItem.setMnemonic(KeyEvent.VK_B);
        	menuItem.addActionListener(this);
        	menu.add(menuItem);


        	//a group of radio button menu items
        	menu.addSeparator();
        	
			ButtonGroup group = new ButtonGroup();

        	rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        	rbMenuItem.setSelected(true);
        	rbMenuItem.setMnemonic(KeyEvent.VK_R);
        	group.add(rbMenuItem);
        	rbMenuItem.addActionListener(this);
        	menu.add(rbMenuItem);

        	rbMenuItem = new JRadioButtonMenuItem("Another one");
        	rbMenuItem.setMnemonic(KeyEvent.VK_O);
        	group.add(rbMenuItem);
        	rbMenuItem.addActionListener(this);
        	menu.add(rbMenuItem);

        	//a group of check box menu items
        	menu.addSeparator();
        	cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        	cbMenuItem.setMnemonic(KeyEvent.VK_C);
        	cbMenuItem.addItemListener(this);
        	menu.add(cbMenuItem);

        	cbMenuItem = new JCheckBoxMenuItem("Another one");
        	cbMenuItem.setMnemonic(KeyEvent.VK_H);
        	cbMenuItem.addItemListener(this);
        	menu.add(cbMenuItem);

        	//a submenu
       		menu.addSeparator();
        	submenu = new JMenu("A submenu");
        	submenu.setMnemonic(KeyEvent.VK_S);

        	menuItem = new JMenuItem("An item in the submenu");
        	menuItem.setAccelerator(KeyStroke.getKeyStroke(
                	KeyEvent.VK_2, ActionEvent.ALT_MASK));
        	menuItem.addActionListener(this);
        	submenu.add(menuItem);

        	menuItem = new JMenuItem("Another item");
        	menuItem.addActionListener(this);
        	submenu.add(menuItem);
        	menu.add(submenu);

        	//Build second menu in the menu bar.
        	menu = new JMenu("Another Menu");
        	menu.setMnemonic(KeyEvent.VK_N);
       		menu.getAccessibleContext().setAccessibleDescription(
        	        "This menu does nothing");
	        menuBar.add(menu);
			menuBar.setOpaque(true);
        	return menuBar;
    	}

	protected String getClassName(Object o) {
        	String classString = o.getClass().getName();
        	int dotIndex = classString.lastIndexOf(".");
        	return classString.substring(dotIndex+1);
    	}

	public static void main(String s[]) {
		JFrame frame = new JFrame( "yov@java" );
		System.out.println("Création de l'applet");
		essai theApplet = new essai();
		System.out.println("Appel de 'init' de l'applet");
		theApplet.init(); 
		System.out.println("Appel de 'start' de l'applet");
		theApplet.start(); 

		System.out.println("ajout de l'applet a la fenetre");
		System.out.println("ajout du bouton a la fenetre");

		JPanel pane = new JPanel(new GridLayout(2, 1));
		pane.setOpaque(true);
		pane.setLayout(null);
       	pane.add(theApplet);
       	pane.add(button);
		frame.getContentPane().add(pane,BorderLayout.CENTER); 
		frame.setJMenuBar(theApplet.createMenuBar());
		theApplet.setBounds(1,60,499,499);
		button.setBounds(10,10,150,40);

		frame.setSize ( 500 , 500 ) ; 
		System.out.println("validate");
		frame.validate(); 
		System.out.println("setvisible = true");
		frame.setVisible( true ); 
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 

     }
	

	public void run(){


		while(my_app != null){

			if(buffer_ready==true){
				curX = (int)(Math.random() * this.getSize().width);
				curY = (int)(Math.random() * this.getSize().height);
				Repaindre(screen_);
				try
      				{
        				Thread.sleep(100);
				}
				catch(Exception e){
					stop();
				}
			}		
		}
	}




	public void Repaindre(Graphics g){
		System.out.println("appel de 'Repaindre'");
	  	bufferGraphics.clearRect(0,0,this.getSize().width,this.getSize().height); 
       	   	bufferGraphics.setColor(Color.red); 
          	bufferGraphics.drawString("Bad Double-buffered",10,10); 
          	bufferGraphics.fillRect(curX,curY,20,20); 
		g.drawImage(offscreen,0,0,this); 
	}


}
