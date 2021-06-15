package snip2clip;

import java.awt.FlowLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*; 
import java.awt.*;

import javax.swing.*;
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.asprise.ocr.*;

// import net.sourceforge.tess4j.*;
  

import static java.awt.GraphicsDevice.WindowTranslucency.*;


public class snip2clip extends JFrame implements MouseListener{
    Label l;
    Point a = new Point(0,0);
    Point b = new Point(0,0);
    Rectangle snipArea = new Rectangle(0,0);
    

    public snip2clip() {
        super("TranslucentWindow");
        setLayout(new GridBagLayout());
        addMouseListener(this);

        setSize(300,200);
        setLocationRelativeTo(null);
        setBackground(new Color(0,0,0));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        // Determine if the GraphicsDevice supports translucency.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        //If translucent windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
            System.err.println(
                "Translucency is not supported");
                System.exit(0);
        }
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	snip2clip tw = new snip2clip();

                // Set the window to 55% opaque (45% translucent).
                tw.setOpacity(0.55f);
                tw.setExtendedState(JFrame.MAXIMIZED_BOTH); 
                // tw.setUndecorated(true);
                // Display the window.
                tw.setVisible(true);
            }
        });
    }

    void drawRectangles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
 
        // g2d.draw(new Rectangle(10,10,100,100));
        
        setVisible(true);
 
    }
 
    public void paint(Graphics g) {
        System.out.println("painting");
        super.paint(g);
        drawRectangles(g);
    }
 

    
    public void mousePressed(MouseEvent e) {
        a = MouseInfo.getPointerInfo().getLocation();
        System.out.println(a.toString());
    }  

    public void mouseReleased(MouseEvent e) {
        b = MouseInfo.getPointerInfo().getLocation();
        System.out.println(b.toString());
        snipArea = setRectangle(a,b);
        getSnippet();
    }

    public void mouseClicked(MouseEvent e) {
        System.out.println("h");}  

    public void mouseEntered(MouseEvent e) {}  

    public void mouseExited(MouseEvent e) {}  

    public void OCRImage(String fileName){
        // AsposeOCR api = new AsposeOCR();
    	Ocr.setUp();
    	Ocr ocr = new Ocr();
    	ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
    	String s = ocr.recognize(new File[] {new File("ScreenSnippet.png")}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT);
//    	System.out.println(s);
    	ocr.stopEngine();
    	copyToClipboard(s);
    }

    private void copyToClipboard(String s) {
		// TODO Auto-generated method stub
    	StringSelection selection = new StringSelection(s);
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	clipboard.setContents(selection, selection);
    	System.out.println("Copied to clipboard!");
	}

	public boolean getSnippet(){
        try{
            Robot rbt = new Robot();
            String format = "png";
            String fileName = "./ScreenSnippet." + format;
            setVisible(false);
            
            BufferedImage screenFullImage = rbt.createScreenCapture(snipArea);
            ImageIO.write(screenFullImage, format, new File(fileName));
            System.out.println("saved the image!\t" + fileName);
            OCRImage(fileName);
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }

        

        return true;
    }

    public Rectangle setRectangle(Point a, Point b){
        int X, Y, width, height = 0;
        if(a.getX()<=b.getX()){
            X = (int) a.getX();
            width = (int) (b.getX() - a.getX());
        } else {
            X = (int) b.getX();
            width = (int) (a.getX() - b.getX());
        }
        if(a.getY()<=b.getY()){
            Y = (int) a.getY();
            height = (int) (b.getY() - a.getY());
        } else {
            Y = (int) b.getY();
            height = (int) (a.getY() - b.getY());
        }
        return new Rectangle(X,Y,width,height);
    }

}
