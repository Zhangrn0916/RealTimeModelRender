package obj;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Point {

	public double x;
	public double y;
	public double z;
		
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Convert Points into realMatrix
	public RealMatrix toMatrix(){
		double[][] m = {
				{this.x},{this.y},{this.z},{1.0}
			};
		return new Array2DRowRealMatrix(m);
	}

}
