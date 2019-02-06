package transfrom;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.*;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import obj.Point;
import obj.Polygon;
import obj.Vector;

public class ViewTranform {

	static Point C;
	static Point P_ref;
	static Vector V_up;
	static Point center;

	static double d;
	static double h;
	static double f;

	static boolean doBackface = true;
	
	static Map<String, Vector> uvnMap;
	static List<Point> original_points;
	static List<Polygon> polygons;
	
	public static void main(String[] args) {
		
		//A frame to show model
		DrawFrame frame = new DrawFrame();

		// Set Camera position
		C = new Point(20.0, 10.0, 70.0);

		// initialize all the parameters
		initial(new DataReader());

		// Get U V N vector according to C p_ref and V_up
		uvnMap = getUVNMap(C, P_ref, V_up);
		
		int rotate_count = 50;
		
		for(int i=0;i<=rotate_count+1;i++){
			
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if( i == rotate_count+1){
				i = 0;
			}
			double rotate_theta = (Math.PI*i*2)/rotate_count;
			
			if (uvnMap != null) {

				//get the rotated points
				List<RealMatrix> rotate_points = counterClockwiseRotate(original_points, P_ref, rotate_theta);

				// Get perspective points according to U V N vector
				List<RealMatrix> pers_points = pers_transform(rotate_points, uvnMap);

				// Back facing culling
				if (doBackface) {
					resetBackfacing();
					backfacingCulling(pers_points);
				}
				
				//draw graphic 
				frame.drawGraphics(polygons, pers_points);
				frame.setVisible(true);

			}
		}
	}	

	// Transform world coordinate into perspective's
	private static List<RealMatrix> pers_transform(List<RealMatrix> points, Map<String, Vector> uvnMap) {

		Vector N = uvnMap.get("n");
		Vector U = uvnMap.get("u");
		Vector V = uvnMap.get("v");

		// Transform from world coordinate to camera's
		// Get M_view
		RealMatrix M_view = getM_view(U, V, N);

		// M_view * points
		List<RealMatrix> v_points = new ArrayList<RealMatrix>();
		for (int i = 0; i < points.size(); i++) {
			RealMatrix tmp_matrix = M_view.multiply(points.get(i));
			v_points.add(tmp_matrix);
		}

		// Get M_pers
		RealMatrix M_pers = getM_pers(h, d, f);

		// Scale and Unify
		List<RealMatrix> pers_points = new ArrayList<RealMatrix>();
		for (int i = 0; i < v_points.size(); i++) {
			// M_pers * view points
			double tmp_double[][] = M_pers.multiply(v_points.get(i)).getData();

			// divide W
			double w = tmp_double[3][0];
			for (int j = 0; j < 4; j++) {
				tmp_double[j][0] /= w;
			}

			RealMatrix m = new Array2DRowRealMatrix(tmp_double);
			pers_points.add(m);
		}

		return pers_points;

	}

	private static void initial(DataReader dr) {

		
		P_ref = dr.center;
		V_up = new Vector(0.0, 0.0, 1.0);

		// Set h , d , f
		d = 10.0;
		h = 10.0;
		f = 80.0; // F should be larger than all the z value of model

		original_points = dr.getPoints();
		polygons = dr.getPolygons();

	}

	//Get a Map contains U V N vector
	private static Map<String, Vector> getUVNMap(Point C, Point Pref, Vector V_up) {

		if (C == null || Pref == null || V_up == null) {
			System.out.println("getUVNMap: Null Parameter");
			return null;
		}

		// Get U,V,N
		Map<String, Vector> result = new HashMap<String, Vector>();

		Vector N = new Vector(P_ref, C).unify();
		Vector U = N.crossPruduct(V_up).unify();
		Vector V = U.crossPruduct(N).unify();

		result.put("n", N);
		result.put("u", U);
		result.put("v", V);

		return result;
	}

	// Calculate the M_view
	private static RealMatrix getM_view(Vector U, Vector V, Vector N) {

		if (U == null || V == null || N == null) {
			return null;
		}

		double[][] R = { { U.x, U.y, U.z, 0 }, { V.x, V.y, V.z, 0 }, { N.x, N.y, N.z, 0 }, { 0, 0, 0, 1 } };
		double[][] T = { { 1, 0, 0, -C.x }, { 0, 1, 0, -C.y }, { 0, 0, 1, -C.z }, { 0, 0, 0, 1 } };

		RealMatrix matrixR = new Array2DRowRealMatrix(R);
		RealMatrix matrixT = new Array2DRowRealMatrix(T);

		return matrixR.multiply(matrixT);
	}

	// Calculate the M_pers
	private static RealMatrix getM_pers(double h, double d, double f) {

		double[][] Pers = { { d / h, 0, 0, 0 }, { 0, d / h, 0, 0 }, { 0, 0, f / (f - d), (d * f) / (d - f) },
				{ 0, 0, 1, 0 } };

		return new Array2DRowRealMatrix(Pers);
	}

	private static void backfacingCulling(List<RealMatrix> pers_points) {
		for (int i = 0; i < polygons.size(); i++) {
			Vector normal = polygons.get(i).getNormal(pers_points);
			if (normal.z >= 0) {
				polygons.get(i).setBackfacing(true);
			}
		}
	}

	private static void resetBackfacing() {
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).setBackfacing(false);
		}
	}

	// Rotate model around z-axis counterclockwise
	private static List<RealMatrix> counterClockwiseRotate(List<Point> original_points, Point center, double theta) {
		List<RealMatrix> rotate_result = new ArrayList<RealMatrix>();

		List<Point> tmp = new ArrayList<Point>();
		tmp = original_points;

		// Rotate matrix
		double[][] rotate = { { Math.cos(theta), -Math.sin(theta), 0, 0 }, { Math.sin(theta), Math.cos(theta), 0, 0 },
				{ 0, 0, 1, 0 }, { 0, 0, 0, 1 }, };

		RealMatrix M_rotate = new Array2DRowRealMatrix(rotate);

		// Make w = 0, otherwise the model be scaled half
		RealMatrix addCenter = center.toMatrix();
		addCenter.addToEntry(3, 0, -1.0);

		for (int i = 0; i < tmp.size(); i++) {
			Point point = new Point(original_points.get(i).x, original_points.get(i).y, original_points.get(i).z);
			point.x -= center.x;
			point.y -= center.y;
			point.z -= center.z;

			RealMatrix tmp_rotate = M_rotate.multiply(point.toMatrix());
			rotate_result.add(tmp_rotate.add(addCenter));
		}

		return rotate_result;
	}
}
