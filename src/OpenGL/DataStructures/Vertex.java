package OpenGL.DataStructures;

import java.util.ArrayList;

public class Vertex {
	public Vector3D position;
	public Vector2D texture;
	public Vector3D normal; // Normal from file -> is only used to determine solf or hard edge
	public Vector3D tangent;
	public Vector3D bitangent;
	
	public boolean softEdge = false; // sharedVertex with same normal -> average Normals
	public boolean hardEdge = false; // sharedVertex with same normal, but not shared by edge vertex
	public boolean textureSeam = false; // sharedVertex with different texture coordinates -> make new vertex
	
	//public ArrayList<Integer> sharedVertex = new ArrayList<Integer>(); // indexes to Normals for Shared Verticies
	public ArrayList<Integer> textureSeamIndexes = new ArrayList<Integer>(); // Indexes for Texture Seams

	public Vertex(float x, float y, float z){
		this.position = new Vector3D(x, y, z);
		this.texture = null;
		this.normal = null;
	}
	
	public Vertex(Vector3D position, Vector2D texture, Vector3D normal){
		this.position = position;
		this.texture = texture;
		this.normal = normal;
	}
	
	public void setTexture(float tx, float ty) {
		this.texture = new Vector2D(tx, ty);
	}
	
	public void setNormal(float nx, float ny, float nz) {
		this.normal = new Vector3D(nx, ny, nz);
	}
	
	public void setTangent(float tx, float ty, float tz) {
		this.tangent = new Vector3D(tx, ty, tz);
	}
	
	public void setBitangent(float bx, float by, float bz) {
		this.bitangent = new Vector3D(bx, by, bz);
	}
	
	public boolean isSet() {
		return texture != null;
	}
}

