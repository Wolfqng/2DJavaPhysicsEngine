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
    public static ArrayList<Collider<Object>> objects = new ArrayList<>();
	BufferedImage s;
	public Queue<Coord> tp = new LinkedList<Coord>();
	
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
	    BasicStroke stroke = new BasicStroke(5, 2, 2);  //Width 5, JOIN_BEVEL
	    g2d.setStroke(stroke);
	    for(Collider<Object> obj : objects) {
	    	g2d.setColor(obj.getC());
	    	if(obj instanceof Polygon) g2d.drawPolygon(Polygon.doubleToIntArray(Coord.getAllX(((Polygon) obj).getCoords())), Polygon.doubleToIntArray(Coord.getAllY(((Polygon)obj).getCoords())), ((Polygon) obj).getCoords().size());
	    	if(obj instanceof Circle) g2d.drawOval((int)((Circle) obj).getX() - (int)((Circle) obj).getRadius(), (int)((Circle) obj).getY() - (int)((Circle) obj).getRadius(), (int)((Circle) obj).getRadius() * 2, (int)((Circle) obj).getRadius() * 2);
	    	

	    	
	    	generateOuterCollider(g2d, obj);
	    	if(obj instanceof Polygon) generateTrails(g2d, (Polygon)obj);
	    	if(obj instanceof Circle) {
	    		g2d.setPaint(new Color(0, 0, 0));
	    		g2d.fillRect((int)((Circle) obj).getX(), (int)((Circle) obj).getY(), 1, 1);
	    	}
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
	    Polygon test = new Polygon(xp2, yp2, 10, 5, Color.blue);
	    objects.add(test);
	    
	    Circle c = new Circle(500, 400, 120, Color.black, 20, 20);
	    objects.add(c);
	    
	    Object monitor = new Object();
        synchronized(monitor) {
            while(true) {
                frame.repaint();
                //ArrayList<Collider<Object>> objects = new ArrayList<>(objects);   //you should pass in this objects array, then
                //only update the main arraylist, use this so you dont edit a objects coordinates then do physics for the next object
                //Hopefully you still understand this.
                for(Collider<Object> p : objects) {
                	p.update();
                	p.rotate(.1);
                	p.setDrawn(false);
                }

                try{Thread.sleep(1);}catch(InterruptedException ex){Thread.currentThread().interrupt();}
            }
        }
	}
	
	//generate trail points
    public void generateTrails(Graphics2D g2d, Polygon obj) {
    	g2d.setPaint(new Color(255, 0, 0, 100));
		for(Coord c : obj.getCoords()) {
			tp.add(new Coord(c.getX(), c.getY()));
			if(tp.size() > 10000 * objects.size()) tp.remove(); //gives each polygon 10000 trail points	
		}
		
    	for(Coord c : tp) 
    		g2d.fillRect((int)c.getX(), (int)c.getY(), 1, 1);
    }
    
    //Generates the outer collider and inner collider visual, really should change, this code should be in each polygon
    public void generateOuterCollider(Graphics2D g2d, Collider<Object> obj) {
    	g2d.setPaint(new Color(255, 100, 255, 100));
    	ArrayList<Coord> coords = obj.outerColliderPoints();
    	int[] xPoints = Polygon.doubleToIntArray(Coord.getAllX(coords));
    	int[] yPoints = Polygon.doubleToIntArray(Coord.getAllY(coords));
    	g2d.drawPolygon(xPoints, yPoints, coords.size());
		
    	ArrayList<CollisionEvent> collisions = obj.getOuterCollisions();
    	if(!collisions.isEmpty()) {
        	g2d.fillPolygon(xPoints, yPoints, coords.size());
        	ArrayList<Coord> plen = obj.generateCollisionPoints();
	    	
	    	for(Coord pl : plen) {
	    		g2d.setPaint(new Color(0, 255, 255, 255));
	    		g2d.fillRect((int)pl.getX(), (int)pl.getY(), 2, 2);
	    		
	    		for(Collider<Object> obj2 : objects) { //You should change objects to a list that the first object is colliding with
		    			if(!(obj == obj2)) {
		    			//if(obj2 instanceof Circle) System.out.println(pl.getX() + " = " + pl.getY() + " = " + obj.getC());
		    			CollisionPointEvent cpe = obj2.isPointInCollider(pl.getX(), pl.getY(), obj);
			    		if(cpe != null) {
			    			obj2.addCollision(cpe);
			    			obj.draw(g2d, new Color(255, 0, 0, 50));
			    		}
	    			}
	    		}
	    	}
    	}
    }
    
    
}
