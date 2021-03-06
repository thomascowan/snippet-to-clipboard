package snip2clip;


import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*; 
import java.awt.*;

import javax.swing.*;
import javax.swing.JFrame;  

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

//import com.asprise.ocr.*;
import net.sourceforge.tess4j.*;


import static java.awt.GraphicsDevice.WindowTranslucency.*;


public class snip2clip extends JFrame implements MouseListener{

	private static final long serialVersionUID = 1L;
    Point a = new Point(0,0);
    Point b = new Point(0,0);
    Rectangle snipArea = new Rectangle(0,0);
    static snip2clip tw = null;
    boolean closing = false;
    Rectangle old = new Rectangle(0,0);
    
    public snip2clip() {
        super("TranslucentWindow");
        setLayout(new GridBagLayout());
        addMouseListener(this);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
            System.err.println("Translucency is not supported");
            System.exit(0);
        }
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	if(tw==null) tw = new snip2clip();
                tw.setOpacity(0.55f);
                tw.setExtendedState(JFrame.MAXIMIZED_BOTH); 
                tw.setVisible(true);
            }
        });
    }
 
    public void paint(Graphics g) {
    	int w = MouseInfo.getPointerInfo().getDevice().getDisplayMode().getWidth();
        int h = MouseInfo.getPointerInfo().getDevice().getDisplayMode().getHeight();
        BufferedImage bf = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bf.createGraphics();
        if(!a.equals(new Point(0,0))) {
        	Point drag = MouseInfo.getPointerInfo().getLocation();
        	Rectangle currRect = setRectangle(a,drag);
        	g2d.setColor(new Color(30,30,30));
        	g2d.fillRect(currRect.x, currRect.y, currRect.width, currRect.height);
        }
        g.drawImage(bf,0,0,w,h,null);
        repaint();
    }
    
    public void mousePressed(MouseEvent e) {
        a = MouseInfo.getPointerInfo().getLocation();
    }  

    public void mouseReleased(MouseEvent e) {
        b = MouseInfo.getPointerInfo().getLocation();
        snipArea = selectText(a,b);
        OCR();
    }

    public void mouseClicked(MouseEvent e) {}  

    public void mouseEntered(MouseEvent e) {}  

    public void mouseExited(MouseEvent e) {
    	if(closing) return;
        showOnScreen(getScreenID(), tw);
    }
    
    public int getScreenID() {
        return Integer.parseInt(
        		MouseInfo.getPointerInfo().getDevice().toString().substring(
        		MouseInfo.getPointerInfo().getDevice().toString().length()-2, 
        		MouseInfo.getPointerInfo().getDevice().toString().length()-1)
        );
    }

	public void OCR(){
        try{
            Robot rbt = new Robot();
            setVisible(false);
            if(snipArea.width<5 || snipArea.height<5) {
                System.err.println("Snip size not large enough");
                System.exit(0);
            }
            BufferedImage screenFullImage = rbt.createScreenCapture(snipArea);
            ImageIO.write(screenFullImage, "png", new File("./ScreenSnippet.png"));

        	closing = true;
        	File imageFile = new File("ScreenSnippet.png");
            ITesseract instance = new Tesseract();
            instance.setDatapath("tessdata");

            try {
            	StringSelection selection = new StringSelection(instance.doOCR(imageFile));
            	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            	clipboard.setContents(selection, selection);
            	System.out.println("Copied to clipboard!");
            	Files.delete(FileSystems.getDefault().getPath("ScreenSnippet.png"));
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
                System.exit(0);
            }
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
            System.exit(0);
        }
    }
	
	public static void showOnScreen( int screen, JFrame frame ) {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gd = ge.getScreenDevices();
	    if( screen > -1 && screen < gd.length ) {
	        frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
	    } else if( gd.length > 0 ) {
	        frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
	    } else {
	        throw new RuntimeException( "No Screens Found" );
	    }
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
        while(X > MouseInfo.getPointerInfo().getDevice().getDisplayMode().getWidth()) X -=MouseInfo.getPointerInfo().getDevice().getDisplayMode().getWidth();
        while(Y > MouseInfo.getPointerInfo().getDevice().getDisplayMode().getHeight()) Y -=MouseInfo.getPointerInfo().getDevice().getDisplayMode().getHeight();
        while(X < 0) X +=MouseInfo.getPointerInfo().getDevice().getDisplayMode().getWidth();
        while(Y < 0) Y +=MouseInfo.getPointerInfo().getDevice().getDisplayMode().getHeight();
        return new Rectangle(X,Y,width,height);
    }

    public Rectangle selectText(Point a, Point b){
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
