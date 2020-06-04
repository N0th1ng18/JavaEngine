package OpenGL.DataStructures;

public class Matrix3 {
	
	/*
	 * 
	 *	|tangent.x		tangent.y		tangent.z	|
	 *	|bitangent.x	bitangent.y		bitangent.z	|
	 *	|normal.x		normal.y		normal.z 	|
	 * 
	 */
	
	public Vector3D tangent, bitangent, normal;
	
	public Matrix3() {
		tangent = new Vector3D();
		bitangent = new Vector3D();
		normal = new Vector3D();
	}
}
