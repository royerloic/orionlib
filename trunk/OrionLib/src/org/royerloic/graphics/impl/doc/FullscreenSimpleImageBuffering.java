/*
*	Java Source Code for fast 2D true color animation and games
*	yov408 Technologies Production - http://www.yov408.com
*	Full Screen Image Buffering
*	** Most of the code taken from JavaHelp.com **
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;

public class essai{

	public static void main(String[] argv){

		boolean casse_toi=false;


		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
	
	   	Frame frame = new Frame(gd.getDefaultConfiguration());
    	Window win = new Window(frame);
    
		win.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent evt) {
	            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
    	            .getDefaultScreenDevice();
        	    gd.setFullScreenWindow(null);
        	}
    	});

    
    	try {
	        
       	gd.setFullScreenWindow(win);
       	win.requestFocus();

 		DisplayMode[] affichages = gd.getDisplayModes();
		int i;
		for(i=0;i<affichages.length;i++){
			if((affichages[i].getWidth()==640)&&(affichages[i].getHeight()==480)&&(affichages[i].getBitDepth()==16)){
				gd.setDisplayMode(affichages[i]);
			}
		}

       	int screenWidth = win.getWidth();
       	int screenHeight = win.getHeight();

		Graphics graphicwindow = win.getGraphics();
		graphicwindow.setColor(Color.white);
       	graphicwindow.fillRect(0,0,screenWidth,screenHeight);

		BufferedImage offscreen = new BufferedImage(screenWidth,screenHeight,BufferedImage.TYPE_INT_RGB);
		Graphics2D offscreen_high = offscreen.createGraphics();
		int frames=0;
		int j;

		while(!casse_toi){
			frames++;
			offscreen_high.setColor(Color.black);
	       	offscreen_high.fillRect(0,0,screenWidth,screenHeight);
			
			for(i=50;i<400;i++){
			for(j=50;j<400;j++){
				offscreen.setRGB(i,j,Color.red.getRGB());
			}
			}

			String s = "frame"+frames;
			offscreen_high.setColor(Color.red);
	 		offscreen_high.drawString(s,20,20);
			graphicwindow.drawImage(offscreen,0,0,null); 
		}


    	}
		catch (Throwable e)
		{
        
    	}

	}


}