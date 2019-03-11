package transfrom;

import java.awt.Panel;
import java.util.*;

import javax.swing.JFrame;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import com.jogamp.opengl.GL;
//OpenGL package
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import obj.EdgeItem;
import obj.Pixel;
import obj.Point;
import obj.Polygon;
import obj.Vector;

public class ViewTransform {

	private static Point C;
	private static Point P_ref;
	private static Vector V_up;
	private static Point center;

	private static double d;
	private static double h;
	private static double f;

	private static final boolean doBackface = true;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	private static Map<String, Vector> uvnMap;
	private static List<Point> original_points;
	private static List<Polygon> polygons;
	
	
	// 0:constant shading
	// 1:gouraud shading
	// 2:phong shading
	private static int shading_flag = 1;

	// Used for constant shading
	private static float[] polygon_Intensity;

	// Used for Gouraud shading
	private static float point_Intensity[];

	// Light Parameter
	private static Vector L;
	private static Vector V;
	// Light color
	private static float object_color_r;
	private static float object_color_g;
	private static float object_color_b;

	//Roughness constant on surface
	private static int roughnessN = 16;

	// Specular Light Parameters
	private static float Is = (float) 0.7;
	private static float ks = (float) 0.7;

	// Diffuse Light Parameters
	private static float Id = (float) 0.45;
	private static float kd = (float) 0.7;

	// Ambient Light Parameters
	static float Ia = (float) 0.3;
	private static float ka = (float) 0.25;

	static String datapath;

	public static void main(String[] args) {

		Pixel zbuffer[][] = new Pixel[WIDTH][HEIGHT];
		zbuffer = renderIntoPixel("src/datasrc/ball.d.txt");

		DrawFrame frame = new DrawFrame();
		// draw graphic
		frame.drawGraphics(zbuffer);
		frame.setVisible(true);

	}

	public static Pixel[][] renderIntoPixel(String datapath) {

		// initialize all the parameters
		initial(new DataReader(datapath));

		// Get U V N vector according to C p_ref and V_up
		uvnMap = getUVNMap(C, P_ref, V_up);

		//double rotate_theta = Math.random() * 2 * Math.PI;
		double rotate_theta = 0.5 * 2 * Math.PI;

		if (uvnMap != null) {

			// get the rotated points
			List<RealMatrix> rotate_points = counterClockwiseRotate(original_points, P_ref, rotate_theta);

			// Calculate Shading color
			if (shading_flag == 0) {
				constantShading(rotate_points);
			} else if (shading_flag == 1) {
				gouraudShading(rotate_points);
			} else {
				// phong shading to be implemented
			}

			// Get perspective points according to U V N vector
			List<RealMatrix> pers_points = pers_transform(rotate_points, uvnMap);

			// Back facing culling
			if (doBackface) {
				resetBackfacing();
				backfacingCulling(pers_points);
			}

			// Convert pers_points into Pixel value
			List<Point> pixel_points = new ArrayList<Point>();
			for (int j = 0; j < pers_points.size(); j++) {
				double newx = ((pers_points.get(j).getEntry(0, 0) + 1.0) / 2.0) * WIDTH;
				double newy = ((pers_points.get(j).getEntry(1, 0) + 1.0) / 2.0) * HEIGHT;
				Point added = new Point(newx, newy, pers_points.get(j).getEntry(2, 0));
				pixel_points.add(added);
			}

			// scan_convert and calculate Z buffer value of each point
			Pixel[][] zbuffer = new Pixel[WIDTH][HEIGHT];
			zbuffer = scan_convert(pixel_points, polygons);

			return zbuffer;
		}

		return null;
	}

	private static void initial(DataReader dr) {

		// Set Camera position
		C = new Point(-3, -3, -3);
		P_ref = dr.center;
		V_up = new Vector(0.0, 0.0, 1.0);

		// Set h , d , f
		d = 10.0;
		h = 10.0;
		f = 80.0; // F should be larger than all the z value of model

		original_points = dr.getPoints();
		polygons = dr.getPolygons();

		polygon_Intensity = new float[polygons.size()];

//		object_color_r = (float) Math.random();
//		object_color_g = (float) Math.random();
//		object_color_b = (float) Math.random();
		
		object_color_r = (float) 1.0;
		object_color_g = 0;
		object_color_b = 0;
		
		V = new Vector(P_ref, C).unify();
		L = new Vector(0, 2, -7).unify();

		point_Intensity = new float[original_points.size()];
	}

	// Get a Map contains U V N vector
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

	private static Pixel[][] scan_convert(List<Point> pixel_points, List<Polygon> polygons) {

		// Store pixel infomation
		Pixel[][] zbuffer = new Pixel[WIDTH][HEIGHT];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				zbuffer[i][j] = new Pixel();
			}
		}

		for (int j = 0; j < polygons.size(); j++) {

			// ignore back facint polygon
			if (polygons.get(j).getBackfacing()) {
				System.out.println("is backfacing");
				continue;
			}
			System.out.println("not back facing");

			// Build Edgetable
			List<List<EdgeItem>> edgeTable = new ArrayList<List<EdgeItem>>();

			// Initialize Edge table
			for (int j2 = 0; j2 < HEIGHT; j2++) {
				edgeTable.add(new ArrayList<EdgeItem>());
			}

			double startY = HEIGHT - 1;
			double endY = 0;
			int pointnum = polygons.get(j).getDegree();

			// Insert Edge into Edge table
			for (int k = 0; k < pointnum; k++) {

				// Set parameter to creat new edgeItem
				int p1_index;
				int p2_index;
				if (k != pointnum - 1) {
					p1_index = polygons.get(j).getPointIndex().get(k);
					p2_index = polygons.get(j).getPointIndex().get(k + 1);
				} else {
					p1_index = polygons.get(j).getPointIndex().get(k);
					p2_index = polygons.get(j).getPointIndex().get(0);
				}

				EdgeItem edge = new EdgeItem(pixel_points.get(p1_index), p1_index, pixel_points.get(p2_index), p2_index,
						j);

				double miny = pixel_points.get(p1_index).y < pixel_points.get(p2_index).y
						? (pixel_points.get(p1_index).y) : (pixel_points.get(p2_index).y);
				double maxy = edge.getY_max();

				// ignore those edge parallel to x-axis
				if (edge.getX_delta() != 0) {
					// System.out.println("miny: "+miny);
					edgeTable.get(Math.round((float) miny)).add(edge);
				}

				if (startY > miny) {
					startY = miny;
				}

				if (endY < maxy) {
					endY = maxy;
				}
			}

			List<EdgeItem> aetRow = new ArrayList<EdgeItem>();

			// Scan Convert
			// Apply scan convert
			int iteratorY = Math.round((float) startY);
			while (iteratorY < HEIGHT && iteratorY <= (int) endY
					&& (!aetRow.isEmpty() || !edgeTable.get(iteratorY).isEmpty())) {

				if (aetRow.isEmpty() && edgeTable.get(iteratorY).isEmpty()) {
					iteratorY++;
					continue;
				}

				// Move Edge from ET to AET
				for (int k = 0; k < edgeTable.get(iteratorY).size(); k++) {
					EdgeItem e = edgeTable.get(iteratorY).get(k);
					aetRow.add(e);
				}
				edgeTable.get(iteratorY).clear();

				// Remove ymax == y
				for (int it = aetRow.size() - 1; it >= 0; it--) {
					if (Math.round((float) aetRow.get(it).getY_max()) == iteratorY) {
						aetRow.remove(it);
					}
				}

				Collections.sort(aetRow);

				for (int l = 0; l < aetRow.size(); l += 2) {

					// Calculate Z value
					// choose intersection to calculate Z
					double xa = aetRow.get(l).getX();
					double xb = aetRow.get(l + 1).getX();

					// get Z here
					Point ap1 = aetRow.get(l).getP1();
					Point ap2 = aetRow.get(l).getP2();
					double za = ap1.z - (ap1.z - ap2.z) * (ap1.y - iteratorY) / (ap1.y - ap2.y);

					Point bp1 = aetRow.get(l + 1).getP1();
					Point bp2 = aetRow.get(l + 1).getP2();
					double zb = bp1.z - (bp1.z - bp2.z) * (bp1.y - iteratorY) / (bp1.y - bp2.y);

					double delta_z = (za - zb) / (xa - xb);

					// Gouraud Shading

					if (shading_flag == 0) {      // Constant Shading
						for (int xp = Math.round((float) xa); xp < Math.round((float) xb); xp++) {
							double zp = za + (xp - xa) * delta_z;
							// Add polygon_index and corresponding zp to pixel
							if (zbuffer[xp][iteratorY].getColored() == false || zbuffer[xp][iteratorY].getZ() >= zp) {

								zbuffer[xp][iteratorY].setRGBZ(getColorByIntensity(object_color_r, polygon_Intensity[j]), 
										getColorByIntensity(object_color_g, polygon_Intensity[j]),
										getColorByIntensity(object_color_b, polygon_Intensity[j]), zp);
							}
						}
					} else if (shading_flag == 1) {  //Gouraud Shading
						// Interpolate Intensity
						float Ixa = (float) (point_Intensity[aetRow.get(l).p1index] * (iteratorY - ap2.y)
								/ (ap1.y - ap2.y)
								+ point_Intensity[aetRow.get(l).p2index] * (ap1.y - iteratorY) / (ap1.y - ap2.y));

						float Ixb = (float) (point_Intensity[aetRow.get(l + 1).p1index] * (iteratorY - bp2.y)
								/ (bp1.y - bp2.y)
								+ point_Intensity[aetRow.get(l + 1).p2index] * (bp1.y - iteratorY) / (bp1.y - bp2.y));

						double delta_I = (Ixb - Ixa) / (xb - xa);

						for (int xp = Math.round((float) xa); xp < Math.round((float) xb); xp++) {

							double zp = za + (xp - xa) * delta_z;
							float Ip = (float) (Ixa + (xp - xa) * delta_I);

							// Add polygon_index and corresponding zp to pixel
							if (zbuffer[xp][iteratorY].getColored() == false || zbuffer[xp][iteratorY].getZ() >= zp) {
								zbuffer[xp][iteratorY].setRGBZ(getColorByIntensity(object_color_r, Ip),
										getColorByIntensity(object_color_g, Ip),
										getColorByIntensity(object_color_b, Ip), zp);
							}

						}
					}

				}

				for (int it = aetRow.size() - 1; it >= 0; it--) {
					aetRow.get(it).setNextX();
				}

				// Resort AET
				Collections.sort(aetRow);

				iteratorY++;
			}
		}

		// Return Zbuffer to render
		return zbuffer;
	}

	// Lab3
	
	private static void constantShading(List<RealMatrix> points){
		
		for(int i=0;i<polygons.size();i++){
			Vector norm = polygons.get(i).getNormal(points);
			
			// get Intensity of points, only one light source
			polygon_Intensity[i] = Illumintation.getAmbient(Ia, ka)
					+ Illumintation.getDiffuse(Id, kd, norm, L.getReverse())
					+ Illumintation.getSpecular(Is, ks, norm, L.getReverse(), V.getReverse(), roughnessN);
			
		}
	}
	
	
	
	private static void gouraudShading(List<RealMatrix> points) {
		// Gouraud shading
		List<List<Integer>> points_in_polygons = new ArrayList<List<Integer>>();
		for (int i = 0; i < points.size(); i++) {
			points_in_polygons.add(new ArrayList<Integer>());
		}

		// Store
		for (int i = 0; i < polygons.size(); i++) {
			List<Integer> pindex = polygons.get(i).getPointIndex();
			for (int j = 0; j < pindex.size(); j++) {
				points_in_polygons.get(pindex.get(j)).add(i);
			}
		}

		// for each vertex get normal
		for (int i = 0; i < points.size(); i++) {

			Vector norm = null;
			if (!points_in_polygons.get(i).isEmpty()) {
				norm = polygons.get(points_in_polygons.get(i).get(0)).getNormal(points);
			} else {
				continue;
			}

			// Get normal of vertex
			for (int j = 1; j < points_in_polygons.get(i).size(); j++) {
				norm = norm.add(polygons.get(points_in_polygons.get(i).get(j)).getNormal(points));
			}

			// Unify normal
			norm.unify();

			// get Intensity of points, only one light source
			point_Intensity[i] = Illumintation.getAmbient(Ia, ka)
					+ Illumintation.getDiffuse(Id, kd, norm, L.getReverse())
					+ Illumintation.getSpecular(Is, ks, norm, L.getReverse(), V.getReverse(), roughnessN);

		}
	}

	//Calculate the RGB value with Light Intentsity
	private static float getColorByIntensity(float color, float Icurr) {
		if (Icurr >= 0.5) {
			if (Icurr > 1) {
				return (float) 1.0;
			} else {
				return (float) (((Icurr - 0.5) / 0.5) * (1.0 - color) + color);
			}
		} else {
			if(Icurr < 0 ){
				return 0;
			}else{
				return (float) (color - ((0.5 - Icurr) / 0.5) * color);
			}
		}
	}

}
