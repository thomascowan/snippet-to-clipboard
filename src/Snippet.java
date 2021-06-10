import java.awt.FlowLayout;  
import java.awt.event.*; 
import java.awt.*;

import javax.swing.*;
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  

import static java.awt.GraphicsDevice.WindowTranslucency.*;


public class Snippet extends JFrame implements MouseListener{
    Label l;
    Point a = new Point(0,0);
    Point b = new Point(0,0);
    Rectangle snipArea = new Rectangle(0,0);

    public Snippet() {
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
                Snippet tw = new Snippet();

                // Set the window to 55% opaque (45% translucent).
                tw.setOpacity(0.55f);
                tw.setExtendedState(JFrame.MAXIMIZED_BOTH); 
                // tw.setUndecorated(true);
                // Display the window.
                tw.setVisible(true);
            }
        });
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

    void drawRectangles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
 
        g2d.draw(new Rectangle(10,10,100,100));
        
        setVisible(true);
 
    }
 
    public void paint(Graphics g) {
        System.out.println("painting");
        super.paint(g);
        drawRectangles(g);
    }
 

    
    public void mousePressed(MouseEvent e) {
        a = MouseInfo.getPointerInfo().getLocation();
        System.out.println("h");
    }  

    public void mouseReleased(MouseEvent e) {
        b = MouseInfo.getPointerInfo().getLocation();
        snipArea = setRectangle(a,b);
    }

    public void mouseClicked(MouseEvent e) {
        System.out.println("h");}  

    public void mouseEntered(MouseEvent e) {}  

    public void mouseExited(MouseEvent e) {}  

}
