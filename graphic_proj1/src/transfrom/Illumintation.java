package transfrom;

import obj.Vector;

public class Illumintation {
	
	public static float getAmbient(float Ia , float ka){
		return Ia*ka;
	}
	
	//Using Phong Illumintaion Model
	public static float getDiffuse(float Id,float kd, Vector N,Vector L){
		float idiffuse = (float) (kd*Id*(N.unify().dotPruduct(L.unify())));
		if(idiffuse<0){
			idiffuse = 0;
		}else if(idiffuse > 1){
			idiffuse = (float) 1.0;
		}
		return idiffuse;
	}
	
	public static float getSpecular(float Is,float ks, Vector N,Vector L,Vector V,int n){
		
		Vector H = L.add(V);
		float Ispecular = (float) (ks*Is*Math.pow((N.unify().dotPruduct(H.unify())),(double)n)) ;
		if(Ispecular<0){
			Ispecular = 0;
		}else if(Ispecular > 1){
			Ispecular = (float) 1.0;
		}
		return Ispecular;
		
	}

}
