package physicsEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
	public static BufferedImage s;
	public static BufferedImage logo;
	public static Queue<Coord> tp = new LinkedList<Coord>();
	public static int fps = 0;
	
	public Enviroment() {
		try {
			s = ImageIO.read(this.getClass().getResource("panel.png"));
			logo = ImageIO.read(this.getClass().getResource("hiffinLogo.png"));
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
	    	obj.draw(g2d, obj.getC(), false);
	    	
	    	generateOuterCollider(g2d, obj);
	    	if(obj instanceof Polygon) generateTrails(g2d, (Polygon)obj);
	    	if(obj instanceof Circle) {
	    		g2d.setPaint(new Color(0, 0, 0));
	    	//	g2d.fillRect((int)((Circle) obj).getX(), (int)((Circle) obj).getY(), 1, 1);
	    		g2d.drawLine((int)((Circle) obj).getX(), (int)((Circle) obj).getY(), (int)(Math.cos(obj.getRotation()) * ((Circle) obj).getRadius() + ((Circle) obj).getX()), (int)(Math.sin(obj.getRotation()) * ((Circle) obj).getRadius() + ((Circle) obj).getY()));
	    	}
	    } 
	    
	    Font f = new Font("Courier New", Font.BOLD, 40);
	    g2d.setFont(f);
		g2d.setColor(Color.RED);
		g2d.drawString(Integer.toString(fps), 3, 8 + g2d.getFontMetrics().getHeight() / 2);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Textures");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new Enviroment());
	    frame.setSize(1280 + 16, 896 + 39);
	    frame.setVisible(true);
	    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	    frame.setIconImage(logo);
	    	    
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
	    
	    Circle c = new Circle(500, 400, 120, Color.black, Math.toRadians(270), 20);
	    objects.add(c);
	    
	    runAnimation(frame);
	}
	
	public static void runAnimation(JFrame frame) {
	    int frames = 0;
	    long totalTime = 0;
	    long curTime = System.currentTimeMillis();
	    long lastTime = curTime;
	    // Start the loop.
	    while (true) {
		    try {
		    	frame.repaint();
				// Calculations for FPS.
				lastTime = curTime;
				curTime = System.currentTimeMillis();
				totalTime += curTime - lastTime;
				if (totalTime > 1000) {
					totalTime -= 1000;
					fps = frames;
					frames = 0;
				}
				frames++;
				for(Collider<Object> p : objects) {
            	  p.update();
            	  p.rotate(.1);
                }
				Thread.sleep(1);
		    } catch (InterruptedException e) {
		    	
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
    	ArrayList<Collider<Object>> objects = new ArrayList<>();
    	for(CollisionEvent ce : collisions) 
    		objects.add(ce.getP2());
    	
    	if(!collisions.isEmpty()) {
	    	g2d.fillPolygon(xPoints, yPoints, coords.size());
	    	ArrayList<Coord> plen = obj.generateCollisionPoints();
	    	
	    	for(Coord pl : plen) {
	    		g2d.setPaint(new Color(0, 255, 255, 255));
	    		g2d.fillRect((int)pl.getX(), (int)pl.getY(), 2, 2);
	    		
	    		for(Collider<Object> obj2 : objects) {
		    			if(!(obj == obj2)) {
		    			CollisionPointEvent cpe = obj2.isPointInCollider(pl.getX(), pl.getY(), obj);
			    		if(cpe != null) {
			    			obj2.addCollision(cpe);
			    			obj2.draw(g2d, new Color(255, 0, 0, 50), true);
			    		}
	    			}
	    		}
	    	}
    	}
    }
}

