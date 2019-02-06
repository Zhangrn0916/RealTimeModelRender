package transfrom;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.commons.math3.linear.RealMatrix;

import obj.Polygon;

public class DrawFrame extends JFrame {

	public static final String TITLE = "3D ViewTransform";
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	
	DrawPanel panel;

	public DrawFrame() {
		super();
		initFrame();
		panel = new DrawPanel(this);
	}
	
	public void drawGraphics(List<Polygon> polygons,List<RealMatrix> pers_points){
		panel.renewal(polygons,pers_points);
        setContentPane(panel);
	}
	

	private void initFrame() {
		setTitle(TITLE);
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

}
