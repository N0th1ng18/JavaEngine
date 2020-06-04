package OpenGL.Objects;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;

import OpenGL.DataStructures.MeshStructure;
import OpenGL.Terrain.Terrain;

public class VAO {
	
	public static final int OBJECT_VBO = 0;
	public static final int TERRAIN_VBO = 1;
	public static final int SKYBOX_VBO = 2;
	public static final int TEXT2D_VBO = 3;
	public static final int Button_VBO = 4;
	
	int vao;
	public ArrayList<Integer> vbo  = new ArrayList<Integer>();
	public int indicesLength;
	public int vertexCount;

	
	/*
	 * ***********************************************************
	 */

	public VAO(MeshStructure meshStructure, int type) {
		this.vao = VAO_Manager.createVAO();
		
		switch(type) {
		case OBJECT_VBO:
			objectVBO(meshStructure);
			break;
		case TERRAIN_VBO:
			terrainVBO(meshStructure);
			break;
		}
		
		this.indicesLength = meshStructure.indices.length;
	}
	
	public VAO(float terrainWidth, int widthOfInstances, MeshStructure meshStructure, int type) {
		this.vao = VAO_Manager.createVAO();
		
		switch(type) {
		case TERRAIN_VBO:
			terrainVBO(meshStructure);
			break;
		}
		
		this.indicesLength = meshStructure.indices.length;
	}
	
	public VAO(float[] verticies, int dimension, int type) {
		this.vao = VAO_Manager.createVAO();
		
		switch(type) {
		case SKYBOX_VBO:
			skyBoxVBO(verticies, dimension);
			break;
		case Button_VBO:
			buttonVBO(verticies);
			break;
		}
		
		vertexCount = verticies.length/dimension;
	}
	
	public VAO(float[] verticies, float[] texCoords, int type) {
		this.vao = VAO_Manager.createVAO();
		
		switch(type) {
		case TEXT2D_VBO:
			text2DVBO(verticies, texCoords);
			break;
		}
		
		vertexCount = verticies.length/2;
	}
	
	/*
	 * ***********************************************************
	 */
	
	
	public int getVAOID(){
		return vao;
	}
	public int getVBOID(int index){
		return vbo.get(index);
	}
	
	
	
	/*
	 ***************************************************
	 * Types of VBOs
	 ***************************************************
	 */
	
	
	public void objectVBO(MeshStructure meshStructure) {
		glBindVertexArray(vao);
		
		vbo.add(VAO_Manager.createVBO(0, 3, GL_FLOAT, false, 0, 0, meshStructure.vertices, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(1, 2, GL_FLOAT, false, 0, 0, meshStructure.texCoords, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(2, 3, GL_FLOAT, false, 0, 0, meshStructure.normals, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(3, 3, GL_FLOAT, false, 0, 0, meshStructure.tangents, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(4, 3, GL_FLOAT, false, 0, 0, meshStructure.bitangents, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createIndicesVB0andBindIndices(5, 3, GL_BYTE, false, 0, 0, meshStructure.indices, GL_STATIC_DRAW));

		glBindVertexArray(0);
	
	}
	
	public void terrainVBO(MeshStructure meshStructure) {
		glBindVertexArray(vao);
		
		vbo.add(VAO_Manager.createVBO(0, 3, GL_FLOAT, false, 0, 0, meshStructure.vertices, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(1, 2, GL_FLOAT, false, 0, 0, meshStructure.texCoords, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(2, 3, GL_FLOAT, false, 0, 0, meshStructure.normals, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(3, 3, GL_FLOAT, false, 0, 0, meshStructure.tangents, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(4, 3, GL_FLOAT, false, 0, 0, meshStructure.bitangents, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createIndicesVB0andBindIndices(5, 3, GL_BYTE, false, 0, 0, meshStructure.indices, GL_STATIC_DRAW));

		glBindVertexArray(0);
	
	}
	
	public void terrainVBOInstanced(MeshStructure meshStructure, float terrainWidth, int widthOfInstances) {
		Terrain.createOffsets(meshStructure, terrainWidth, widthOfInstances);
		
		glBindVertexArray(vao);
		
		vbo.add(VAO_Manager.createVBO(0, 3, GL_FLOAT, false, 0, 0, meshStructure.vertices, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBO(1, 2, GL_FLOAT, false, 0, 0, meshStructure.texCoords, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createVBOInstanced(2, 3, GL_FLOAT, false, 0, 0, meshStructure.offsets, GL_STATIC_DRAW));
		vbo.add(VAO_Manager.createIndicesVB0andBindIndices(3, 3, GL_BYTE, false, 0, 0, meshStructure.indices, GL_STATIC_DRAW));

		glBindVertexArray(0);
	}
	
	public void skyBoxVBO(float[] verticies, int dimension) {
		glBindVertexArray(vao);

		vbo.add(VAO_Manager.createVBO(0, dimension, GL_FLOAT, false, 0, 0, verticies, GL_STATIC_DRAW));
		
		glBindVertexArray(0);
	}
	
	public void text2DVBO(float[] verticies, float[] texCoords) {
		glBindVertexArray(vao);

		vbo.add(VAO_Manager.createVBO(0, 2, GL_FLOAT, false, 0, 0, verticies, GL_DYNAMIC_DRAW));
		vbo.add(VAO_Manager.createVBO(1, 2, GL_FLOAT, false, 0, 0, texCoords, GL_DYNAMIC_DRAW));
		
		glBindVertexArray(0);
	}
	
	public void buttonVBO(float[] verticies) {
		glBindVertexArray(vao);

		vbo.add(VAO_Manager.createVBO(0, 2, GL_FLOAT, false, 0, 0, verticies, GL_STATIC_DRAW));
		
		glBindVertexArray(0);
	}
}
