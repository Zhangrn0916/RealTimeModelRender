# RealTimeModelRender
A Model Renderer Project of Computer graphic 

Lab1: View transform 
Implement a system to read in a geometric data of a polygonal object and display view transform view of the model,
also includes rotation and backfacing culling features in ViewTransform.java
The camera position (C) is set by myself and the reference point(P_ref) is the geometric center of the model


Data format:
a) the word "data" followed by the number of points and the number of polygons
b) points given by: the x, y, z coordinates
c) polygons given by: number of points in the polygon followed by vertex number in
clockwise order (when looking from outside the object)


Lab2 Scan_convert and Z-buffer Algorithm
Based on Lab1, Using scan convert algorithm to color the polyygons of each object.
Implemented the z-buffer algorithm to remove the hiding part of the behind object.
Each polygon is colored differently in Main.java

Limitation: scan_convert can process the points that are out of view frustum
so when using bishop as data src, should adjust the Camera point to prevent exception.

Lab3 Iillumination model and Shading Model:
Implemented Phong Illumintaion Model on Object.
In this project, I assume  that the lightpoint and eyepoint are infinitely far awary.
And there is only one light source.

Apply Phong Illumintaion Model with Constant Shading and Gouraud Shading in ViewTransform.java
/** Phong Shading method is ongoing. **//

Would implement this model in C++ in the future

