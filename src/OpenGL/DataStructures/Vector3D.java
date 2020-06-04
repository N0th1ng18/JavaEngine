package OpenGL.DataStructures;

public class Vector3D {
	public float x;
	public float y;
	public float z;
	
	public Vector3D(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3D(){
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}
	
	public float getMag() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalize() {
		float mag = getMag();
		if(mag != 0.0f) {
			this.x = x / mag;
			this.y = y / mag;
			this.z = z / mag;
		}
	}
	
	public Vector3D add(Vector3D v2) {
		x += v2.x;
		y += v2.y;
		z += v2.z;
		return this;
	}
	
	public Vector3D sub(Vector3D v2) {
		x -= v2.x;
		y -= v2.y;
		z -= v2.z;
		return this;
	}
	
	public Vector3D scale(float s) {
		x = x * s;
		y = y * s;
		z = z * s;
		return this;
	}
	
	public static Vector3D add(Vector3D v1, Vector3D v2) {
		Vector3D v = new Vector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
		return v;
	}
	
	public static Vector3D sub(Vector3D v1, Vector3D v2) {
		Vector3D v = new Vector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
		return v;
	}
	
	public static Vector3D div(Vector3D v1, Vector3D v2) {
		Vector3D v = null;
		if(v2.x != 0.0f || v2.y != 0.0f ||v2.z != 0.0f) {
			v = new Vector3D(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
			return v;
		}else {
			return v1;
		}
	}
	public static Vector3D divScalar(Vector3D v1, float s) {
		Vector3D v = null;
		if(s != 0) {
			v = new Vector3D(v1.x / s, v1.y / s, v1.z / s);
			return v;
		}else {
			return v1;
		}
	}
	
	public static Vector3D mul(Vector3D v1, Vector3D v2) {
		Vector3D v = null;
		v = new Vector3D(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
		return v;
	}
	
	public static Vector3D mulScalar(Vector3D v1, float s) {
		Vector3D v = null;
		v = new Vector3D(v1.x * s, v1.y * s, v1.z * s);
		return v;
	}
	
	public static float dot(Vector3D v1, Vector3D v2) {
		
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	
	public static Vector3D cross(Vector3D v1, Vector3D v2) {
		
		Vector3D v = new Vector3D();
        float x,y;

        x = v1.y*v2.z - v1.z*v2.y;
        y = v2.x*v1.z - v2.z*v1.x;
        v.z = v1.x*v2.y - v1.y*v2.x;
        v.x = x;
        v.y = y;
		
        return v;
	}
	
}
