package obj;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Vector {
	
	//A vector from point b to a
	public Vector(Point a,Point b) {
		this.x = a.x-b.x;
		this.y = a.y-b.y;
		this.z = a.z-b.z;
	}
	
	//vecotr A-B
	public Vector(RealMatrix a,RealMatrix b) {
		this.x = a.getEntry(0, 0)-b.getEntry(0, 0);
		this.y = a.getEntry(1, 0)-b.getEntry(1, 0);
		this.z = a.getEntry(2, 0)-b.getEntry(2, 0);
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//Unify vector's length
	public Vector unify() {
		double l = Math.sqrt( Math.pow(x,2) +Math.pow(y,2)+Math.pow(z,2));
		this.x = this.x /= l;
		this.y = this.y /= l;
		this.z = this.z /= l;
		return this;
	}
	
	
	//This cross-multiply vector a
	public Vector crossPruduct(Vector a) {
		double xx = this.y * a.z - this.z * a.y;
		double yy = this.z * a.x - this.x* a.z;
		double zz = this.x * a.y - this.y* a.x;
		return new Vector(xx,yy,zz);
	}
	
	
	public double dotPruduct(Vector a) {
		return this.x * a.x + this.y * a.y + this.z*a.z;
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z*this.z);
	}

	public double x;
	public double y;
	public double z;
}
