package obj;

public class EdgeItem implements Comparable<EdgeItem>{
	int polygon_index;
	private double y_max;
	
	//Prevent data type error in double and int
	private double x;
	private double x_delta;
	Point p1;
	Point p2;
	
	public EdgeItem(Point p1,Point p2,int pindex) {
		this.p1 = p1;
		this.p2 = p2;
		this.polygon_index = pindex;
		y_max = (p1.y > p2.y ? p1.y : p2.y);
		
		if(p1.y < p2.y){
			y_max = p2.y;
			x = p1.x;
			x_delta = ((p2.x - p1.x)/(p2.y - p1.y));
		}else{
			y_max = p1.y;
			x = p2.x;
			x_delta = ((p1.x - p2.x)/(p1.y - p2.y));
		}
		
	}
	
	public Point getP1(){
		return p1;
	}
	
	public Point getP2(){
		return p2;
	}


	public double getX() {
		return x;
	}

	public double getY_max() {
		return y_max;
	}

	public double getX_delta() {
		return x_delta;
	}
	
	
	public void setNextX(){
		this.x = this.x + x_delta;
	}

	@Override
	public int compareTo(EdgeItem o) {
		if(this.getX() > o.getX()){
			return 1;
		}else if(this.getX() == o.getX()){
			return 0;
		}else{
			return -1;
		}
	}
	
	public int getPolygonIndex(){
		return polygon_index;
	}

}
