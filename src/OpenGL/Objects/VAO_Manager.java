package OpenGL.Objects;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class VAO_Manager {
	
	public static int createVAO(){
		int vao = glGenVertexArrays();
		return vao;
	}
	
	static int createVBO(int index, int size, int type, boolean normalized, int stride, int offset, float[] dataArray, int usage){
		int vbo = glGenBuffers();//create Vertex Buffer
		glBindBuffer(GL_ARRAY_BUFFER, vbo);//bind Vertex Buffer
		glBufferData(GL_ARRAY_BUFFER, createBuffer(dataArray),usage);//Add Vertex Buffer data
		glVertexAttribPointer(index, size, type, normalized, stride, offset);
		
		return vbo;
	}
	
	static int createVBOInstanced(int index, int size, int type, boolean normalized, int stride, int offset, float[] dataArray, int usage){
		int vbo = glGenBuffers();//create Vertex Buffer
		glBindBuffer(GL_ARRAY_BUFFER, vbo);//bind Vertex Buffer
		glBufferData(GL_ARRAY_BUFFER, createBuffer(dataArray),usage);//Add Vertex Buffer data
		glVertexAttribPointer(index, size, type, normalized, stride, offset);
		glVertexAttribDivisor(index, 1);
		
		return vbo;
	}
	
	static int createIndicesVB0andBindIndices(int index, int size, int type, boolean normalized, int stride, int offset, int[] dataArray, int usage){
		int vbo = glGenBuffers();//create Vertex Buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);//BIND INDICES TO VAO - USED FOR glDrawElements()
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, createBuffer(dataArray),usage);//Add Vertex Buffer data
		glVertexAttribPointer(index, size, type, normalized, stride, offset);
		
		return vbo;
	}
	
	public static FloatBuffer createBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}
	
	public static IntBuffer createBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}
	
	public static void updateVBO(VAO vao, int vboIndex, int offset, float[] data, FloatBuffer buffer){
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vao.getVBOID(vboIndex));
		glBufferSubData(GL_ARRAY_BUFFER, offset, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
