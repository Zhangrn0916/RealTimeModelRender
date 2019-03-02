package obj;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Polygon {
	private int degree;
	private int polygon_index;
	private boolean backfacing;
	
	public int getDegree() {
		return degree;
	}
	
	private List<Integer> pointIndex;  // Point's consequence is clockwise
	
	public List<Integer> getPointIndex() {
		return pointIndex;
	}

	public Polygon(int degree,int pindex) {
		super();
		this.degree = degree;
		this.polygon_index = pindex;
		pointIndex = new ArrayList<>();
		boolean backfacing = false;
	}
	
	public Vector getNormal(List<RealMatrix> pointslist){
		if(degree <= 1){
			return null;
		}
		
		if(degree >= 2){
			
			Vector v1 = new Vector(pointslist.get(pointIndex.get(0)),
								   pointslist.get(pointIndex.get(1)));
			
			Vector v2 = new Vector(pointslist.get(pointIndex.get(1)),
								   pointslist.get(pointIndex.get(2)));
			
			return v1.crossPruduct(v2);
		}else{
			return new Vector(pointslist.get(pointIndex.get(0)),
					pointslist.get(pointIndex.get(1)));
		}
	}
	
	public boolean addPoint(int p){
		if(pointIndex.size() >= degree){
			return false;
		}
		pointIndex.add(p);
		return true;
	}
	
	public void setBackfacing(boolean is){
		this.backfacing = is;
	}	
	
	
	public boolean getBackfacing(){
		return backfacing;
	}

	public int getIndex() {
		return polygon_index;
	}

	
}
