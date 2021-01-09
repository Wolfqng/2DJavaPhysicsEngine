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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Enviroment extends JPanel {
	private static final long serialVersionUID = 1L;
	public static ArrayList<Polygon> objects = new ArrayList<>();
	BufferedImage s;
	
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
	    g2d.setStroke(new BasicStroke(5));
	    g2d.drawRect(20, 20, 80, 80);
	    for(Polygon obj : objects) {
	    	g2d.setColor(obj.getC());
	    	obj.rotate(5);
	    	g2d.drawPolygon(Polygon.copyFromDoubleArray(obj.getxPoints()), Polygon.copyFromDoubleArray(obj.getyPoints()), obj.getxPoints().length);
	    }
	    
	  }
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Textures");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new Enviroment());
	    frame.setSize(1296, 935);  //Should be 1280, 896 although
	    frame.setVisible(true);
	    
	    double[] xp = new double[]{50, 90, 90, 50};
	    double[] yp = new double[]{50, 50, 90, 90};
	    objects.add(new Polygon(xp, yp, 5, 5, Color.black));
	}
}
