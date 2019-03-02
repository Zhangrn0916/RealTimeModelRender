package transfrom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.math3.linear.RealMatrix;

import com.jogamp.opengl.GL;

import obj.Pixel;
import obj.Polygon;

public class DrawPanel extends JPanel {

	private DrawFrame frame;
	Pixel[][] zbuffer;
	List<RealMatrix> pers_points;
	List<Polygon> polygons;

	public DrawPanel(DrawFrame frame) {
		super();
		this.frame = frame;
	}
	
	void renewal(Pixel[][] zbuffer){
;		this.zbuffer = zbuffer;
	}
	
	void renewal2(List<Polygon> polygons,List<RealMatrix> pers_points){
		this.pers_points = pers_points;
		this.polygons = polygons;
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
       
        int width = frame.WIDTH;
        int height = frame.HEIGHT;
		
		for(int y=0;y<zbuffer[0].length;y++){
			//float y1 = (float) ((y*1.0-400)/800);
			for(int x=0;x<zbuffer.length;x++){
				int r=(int)(zbuffer[x][y].getR()*255);
				int g2=(int)(zbuffer[x][y].getG() *255);
				int b=(int)(zbuffer[x][y].getB() *255);
				Color mycolor = new Color(r, g2, b);
				g2d.setColor(mycolor);
				g2d.drawLine(x, y, x, y);
			}
		}
//        for(int i=0;i<polygons.size();i++){
//        	if(polygons.get(i).getBackfacing()){
//        		continue;
//        	}else{
//        		int nPoints = polygons.get(i).getDegree();
//        		int[] xPoints = new int[nPoints];
//        		int[] yPoints = new int[nPoints];
//        		List<Integer> pointIndex = polygons.get(i).getPointIndex();
//        		
//        		for(int j=0;j<nPoints ;j++){
//        			double x = pers_points.get(pointIndex.get(j)).getEntry(0, 0);
//        			double y = pers_points.get(pointIndex.get(j)).getEntry(1, 0);
//        			
//        			//convert x y into width and height of the frame
//        			xPoints[j] = (int)(width * ((x+1.0)/2.0));
//        			yPoints[j] = (int)(height * ((y+1.0)/2.0));
//        			
//        		}
//        		g2d.drawPolygon(xPoints, yPoints, nPoints );
//        	}
//        
//        }

}

}
