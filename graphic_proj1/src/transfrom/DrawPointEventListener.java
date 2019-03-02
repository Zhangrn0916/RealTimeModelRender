package transfrom;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import obj.Pixel;
import obj.Polygon;

public class DrawPointEventListener implements GLEventListener {
	
	Pixel[][] zbuffer;

	public DrawPointEventListener (Pixel[][] zbuffer) {
		this.zbuffer = zbuffer;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		//gl.glClear(GL.GL_COLOR_BUFFER_BIT); 
		
		for(int y=0;y<zbuffer[0].length;y++){
			float y1 = (float) ((y*1.0-400)/800);
			for(int x=0;x<zbuffer.length;x++){	
				gl.glBegin(GL.GL_POINTS);
				float x1 = (float) ((x*1.0-400)/800);
		        gl.glColor3f(zbuffer[x][y].getR(),zbuffer[x][y].getG(),zbuffer[x][y].getB());

		        gl.glVertex3f(x1,y1,0);
		        gl.glEnd();
			}
		}			
		gl.glFlush();

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// method body
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// method body
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
	}
}