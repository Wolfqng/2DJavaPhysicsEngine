package physicsEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import physicsEngine.events.CollisionEvent;
import physicsEngine.events.CollisionPointEvent;

public class Enviroment extends JPanel {
	private static final long serialVersionUID = 1L;
	public static ArrayList<Polygon> objects = new ArrayList<>();
	BufferedImage s;
	public Queue<double[]> tp = new LinkedList<double[]>();
	
	public Enviroment() {
		try {
			s = ImageIO.read(this.getClass().getResource("panel.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  
	public void paintComponent(Graphics g) {
	    Graphics2D g2d = (Graphics2D) g;
	    for(int i = 0; i < (int)Math.ceil(this.getWidth() / 64.0); i++) {
	    	for(int j = 0; j < (int)Math.ceil(this.getHeight() / 64.0); j++) {
			    TexturePaint slatetp = new TexturePaint(s, new Rectangle(i * 64, j * 64, s.getWidth(), s.getHeight()));
			    g2d.setPaint(slatetp);
			    g2d.fillRect(i * 64, j * 64, s.getWidth(), s.getHeight());
	    	}
	    }
	    
	    g2d.setColor(Color.BLACK);
	    //Width 5, JOIN_BEVEL
	    BasicStroke stroke = new BasicStroke(5, 2, 2);
	    g2d.setStroke(stroke);
	    for(Polygon obj : objects) {
	    	g2d.setColor(obj.getC());
	    	g2d.drawPolygon(Polygon.doubleToIntArray(obj.getxPoints()), Polygon.doubleToIntArray(obj.getyPoints()), obj.getxPoints().length);
	    	
	    	generateOuterCollider(g2d, obj);
	    	//generateTrails(g2d, obj);
	    }
	    
	  }
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Textures");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new Enviroment());
	    frame.setSize(1280 + 16, 896 + 39);  //Should be 1280, 896 although
	    frame.setVisible(true);
	    
	    
	    //Initializing some polygons
	    double[] xp = new double[]{90, 90, 200, 200};
	    double[] yp = new double[]{90, 200, 300, 90};
	    objects.add(new Polygon(xp, yp, 10, 5, Color.black));
	    int xoff = 200;
	    double[] xp1 = new double[]{100 + xoff, 140 + xoff, 210 + xoff, 210 + xoff, 300 + xoff, 350 + xoff};
	    double[] yp1 = new double[]{50, 200, 360, 90, 50, -20};
	    objects.add(new Polygon(xp1, yp1, 10, 5, Color.green));
	    int xoff2 = 100;
	    int yoff = 200;
	    double[] xp2 = new double[]{100 + xoff2, 140 + xoff2, 210 + xoff2, 210 + xoff2, 300 + xoff2};
	    double[] yp2 = new double[]{50 + yoff, 200 + yoff, 360 + yoff, 90 + yoff, 50 + yoff};
	    objects.add(new Polygon(xp2, yp2, 10, 5, Color.blue));
	    
	    Object monitor = new Object();
        synchronized(monitor) {
            while(true) {
                frame.repaint();
                for(Polygon p : objects) {
                	p.update();
                	p.rotate(.1);
                }

                try{Thread.sleep(1);}catch(InterruptedException ex){Thread.currentThread().interrupt();}
            }
        }
	}
	
	//generate trail points
    public void generateTrails(Graphics2D g2d, Polygon obj) {
    	g2d.setPaint(new Color(255, 0, 0, 100));
		for(int i = 0; i < obj.getxPoints().length; i++) {
			double[] coord = new double[2];
			coord[0] = obj.getxPoints()[i];
			coord[1] = obj.getyPoints()[i];
			tp.add(coord);
		}
    	
    	
    	if(tp.size() > 10000) tp.remove();
    	ArrayList<double[]> coords = new ArrayList<double[]>(tp);
    	for(double[] coord : coords) 
    		g2d.fillRect((int)coord[0], (int)coord[1], 1, 1);
    }
    
    public void generateOuterCollider(Graphics2D g2d, Polygon obj) {
    	g2d.setPaint(new Color(255, 100, 255, 100));
    	double[][] coords = obj.outerColliderPoints();
    	g2d.drawPolygon(Polygon.doubleToIntArray(coords[0]), Polygon.doubleToIntArray(coords[1]), coords[0].length);
		
    	ArrayList<CollisionEvent> collisions = obj.getOuterCollisions();
    	if(!collisions.isEmpty()) {
        	g2d.fillPolygon(Polygon.doubleToIntArray(coords[0]), Polygon.doubleToIntArray(coords[1]), coords[0].length);
    	
	    	double[][] plen = Polygon.generateCollisionPoints(obj);
	    	
	    	for(int i = 0; i < plen[0].length; i++) {
	    		g2d.setPaint(new Color(0, 0, 255, 255));
	    		g2d.fillRect((int)plen[0][i], (int)plen[1][i], 2, 2);
	    		
	    		for(Polygon obj2 : objects) { //You should change objects to a list that the first object is colliding with
		    			if(!(obj == obj2)) {
			    		CollisionPointEvent cpe = Polygon.isPointInCollider(plen[0][i], plen[1][i], obj2, obj);
			    		if(cpe != null) {
			    			//System.out.println(cpe);
			    			g2d.setPaint(new Color(255, 0, 0, 100));
			    			g2d.fillPolygon(Polygon.doubleToIntArray(obj.getxPoints()), Polygon.doubleToIntArray(obj.getyPoints()), obj.getxPoints().length);
			    		}
	    			}
	    		}
	    	}
    	}
    }
    
}
