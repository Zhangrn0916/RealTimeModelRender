package obj;

import java.util.ArrayList;
import java.util.List;

public class Pixel {
	float R;
	float G;
	float B;
	
	//Stort the polygons that have point on this pixel
	boolean colored;
	//Store Z buffer value corresponding to the polygon above 
	double z;
	
	public boolean getColored(){
		return colored;
	}
	
	public Pixel(){
		this.R = 0;
		this.G = 0;
		this.B = 0;
		colored = false;
		z = 2.0;
	}
	
	public double getZ(){
		return z;
	}
	
	public void setRGBZ(float R,float G,float B,double z){
		this.R = R;
		this.G = G;
		this.B = B;
		colored = true;
		this.z = z;
	}
	
	public float getR() {
		return R;
	}


	public float getG() {
		return G;
	}


	public float getB() {
		return B;
	}


}
