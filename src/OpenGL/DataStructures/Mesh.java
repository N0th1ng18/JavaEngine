package OpenGL.DataStructures;

import java.util.ArrayList;

public class Mesh {
	public ArrayList<Vertex> vertexList; //VertexList holding all verticies
	/*
	 * VertexList -> | v1 | v2 | v3 | v4 | ... | vn |
	 */
	public ArrayList<Integer> indices; //faceList holding Indicies
	/*
	 * face -> int a, int b, int c
	 * faceList -> | f1 | f2 | f3 | f4 | ... | fn |
	 */
	
	public Mesh() {
		vertexList = new ArrayList<Vertex>();
		indices = new ArrayList<Integer>();	
	}
	
}
