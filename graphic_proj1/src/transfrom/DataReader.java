package transfrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import obj.Point;
import obj.Polygon;

public class DataReader {
	static String[] parseData;
	static int polygon_num;
	static int point_num;
	static List<Point> points; // points data in
	static List<Polygon> polygons;
	static Point center; 
	static String datapath;
	
	public DataReader(String datapath){
		this.datapath = datapath;
		read(this.datapath);
	}
	
	public static void read(String datapath) {

		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(datapath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder strbuilder = new StringBuilder();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				strbuilder.append(str+" ");
			}

			// System.out.println(strbuilder.toString());
			parseData = strbuilder.toString().split("\\s+|\\r+|\\n+|\\t+");
			inputStream.close();
			bufferedReader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		convert();
	}

	private static void convert() {
		points = new ArrayList<Point>();
		polygons = new ArrayList<Polygon>();

		point_num = Integer.parseInt(parseData[1]);
		polygon_num = Integer.parseInt(parseData[2]);

		center = new Point(0,0,0);
		// parseData into polygon
		int i;
		for (i = 3; i < point_num * 3 + 3; i += 3) {
			points.add(new Point(Double.parseDouble(parseData[i]), Double.parseDouble(parseData[i + 1]),
					Double.parseDouble(parseData[i + 2])));
			
			//Find the center of the model
			center.x += Double.parseDouble(parseData[i]);
			center.y += Double.parseDouble(parseData[i+1]);
			center.z += Double.parseDouble(parseData[i+2]);
		}
		
		center.x /= point_num;
		center.y /= point_num;
		center.z /= point_num;
				
		
		// parseData into polygon
		i = point_num * 3 + 3;
		int pindex = 0;
		while (i < parseData.length) {
			int degree = Integer.parseInt(parseData[i]);
			//System.out.println(degree);
			
			Polygon p = new Polygon(degree,pindex++);
			for (int j = 1; j <= degree; j++) {
				p.addPoint(Integer.parseInt(parseData[i+j])-1); 
				
			}
			//System.out.println(" ");
			polygons.add(p);
			i += (degree + 1);
		}
		
	}

	public int getPolygon_num() {
		return polygon_num;
	}

	public int getPoint_num() {
		return point_num;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<Polygon> getPolygons() {
		return polygons;
	}

}
