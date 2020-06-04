package OpenGL.Objects;

import OpenGL.MeshLoader;
import OpenGL.DataStructures.MeshStructure;

public class Model {
	
	public VAO vao;//create vao class and vao manager class
	
	public Model(String location, int type) {
		
		MeshStructure meshStruct = MeshLoader.loadMesh(location);
		vao = new VAO(meshStruct, type);
	}
	
	public Model(float[] verticies, int dimension, int type) {
		vao = new VAO(verticies, dimension, type);
	}
	
	public Model(float[] verticies, float[] texCoords, int type) {
		vao = new VAO(verticies, texCoords, type);
	}
	
	public Model(MeshStructure meshStruct, int type) {
		
		vao = new VAO(meshStruct, type);
	}
	
	public Model(float terrainWidth, int widthOfInstances, String location, int type) {
		
		MeshStructure meshStruct = MeshLoader.loadMesh(location);
		vao = new VAO(terrainWidth, widthOfInstances, meshStruct, type);
	}

}
