package transfrom;

import obj.Pixel;

public class Main {

	static final int WIDTH = 800;
	static final int HEIGHT = 800;

	public static void main(String[] args) {
		ViewTransform vt1 = new ViewTransform("src/datasrc/house.d.txt");
		ViewTransform vt2 = new ViewTransform("src/datasrc/house.d.txt");

		Pixel zbuffer1[][] = vt1.getZbuffer();
		Pixel zbuffer2[][] = vt2.getZbuffer();
		
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				zbuffer2[x][y].setZ(zbuffer2[x][y].getZ()+0.7);
			}
		}	


		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				if (zbuffer1[x][y].getZ() > zbuffer2[x][y].getZ()) {
					zbuffer1[x][y].setRGBZ(zbuffer2[x][y].getR(), zbuffer2[x][y].getG(), zbuffer2[x][y].getB(),
							zbuffer2[x][y].getZ());
				}
			}
		}
		
		DrawFrame frame = new DrawFrame();
		//draw graphic 
		frame.drawGraphics(zbuffer1);
		frame.setVisible(true);

	} 

}
