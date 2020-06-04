package OpenGL.DataStructures;

public class Vector2D {
	public float x;
	public float y;
	
	public Vector2D(float x, float y){
		this.x = x;
		this.y = y;
	}
	public Vector2D(){
		this.x = 0.0f;
		this.y = 0.0f;
	}
	
	public Vector2D add(Vector2D v2) {
		x += v2.x;
		y += v2.y;
		return this;
	}
	
	public Vector2D sub(Vector2D v2) {
		x -= v2.x;
		y -= v2.y;
		return this;
	}
	
	public static Vector2D add(Vector2D v1, Vector2D v2) {
		Vector2D v = new Vector2D(v1.x + v2.x, v1.y + v2.y);
		return v;
	}
	
	public static Vector2D sub(Vector2D v1, Vector2D v2) {
		Vector2D v = new Vector2D(v1.x - v2.x, v1.y - v2.y);
		return v;
	}
	
	public Vector2D scale(float s) {
		x = x * s;
		y = y * s;
		return this;
	}
}
