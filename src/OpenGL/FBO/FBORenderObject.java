package OpenGL.FBO;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

import OpenGL.Objects.VAO;
import OpenGL.Objects.Texture;
import Main.Main;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;
import OpenGL.DataStructures.MatrixMath;
import OpenGL.DataStructures.Vector3D;


public class FBORenderObject{
	//Entities index
	public int index = -1;
	
	//Object Structure
	private VAO vao;
	private int numVertexAttribs;
	private int shaderID;
	private Texture texture;
	
	//Movement Matricies
	private float[][] scaleMatrixf;
	private float[][] rotXMatrixf;
	private float[][] rotYMatrixf;
	private float[][] rotZMatrixf;
	private float[][] translationMatrixf;
	private float[][] transformationMatrixf;
	private FloatBuffer transformationMatrix;
	
	//Movement
	public Vector3D pos;
	public Vector3D vel;
	private Vector3D accel;
	private Vector3D posI;
	//Rotate'
	private Vector3D rotAngle;
	//Scale
	public Vector3D scale;
	//Physics
	private float mass;
	private Vector3D netForce;
	
	public FBORenderObject(VAO vao, Texture texture, int shaderID) {
		//Object Structure
		this.vao = vao;
		this.texture = texture;
		this.shaderID = shaderID;
		
		//Movement Matricies
		scaleMatrixf = new float[4][4];
		rotXMatrixf = new float[4][4];
		rotYMatrixf = new float[4][4];
		rotZMatrixf = new float[4][4];
		translationMatrixf = new float[4][4];
		transformationMatrixf = new float[4][4];
		transformationMatrix = BufferUtils.createFloatBuffer(16);
		
		//Going to be read from a file *********************************
		numVertexAttribs = 3;
		//Movement
		pos = new Vector3D(0.0f, 0.0f, 0.0f);
		vel = new Vector3D(0.0f, 0.0f, 0.0f);
		accel = new Vector3D(0.0f, 0.0f, 0.0f);
		posI = new Vector3D(0.0f, 0.0f, 0.0f);
		//Rotate
		rotAngle = new Vector3D(0.0f, 0.0f, 0.0f);
		//Scale
		scale = new Vector3D(1.0f, 1.0f, 1.0f);
		//Physics
		mass = 10.0f;
		netForce = new Vector3D(0.0f, 0.0f, 0.0f);
	}
	
	public void update(double time, double dt) {
		
		/*
		 * Integration Method
		 */
		accel.x = netForce.x / mass;
		accel.y = netForce.y / mass;
		accel.z = netForce.z / mass;
		vel.x = vel.x + accel.x;
		vel.y = vel.y + accel.y;
		vel.z = vel.z + accel.z;
		pos.x = pos.x + vel.x;
		pos.y = pos.y + vel.y;
		pos.z = pos.z + vel.z;
	}
	
	public void render(double alpha) {
		
		/*
		 * Interpolate
		 */
		posI.x = (float) (pos.x + (vel.x * alpha));
		posI.y = (float) (pos.y + (vel.y * alpha));
		posI.z = (float) (pos.z + (vel.z * alpha));
		
		/*
		 * Transformation Matrix
		 */
		scale(scale.x, scale.y, scale.z);
		rotate(rotAngle.x, rotAngle.y, rotAngle.z);
		translate(posI.x, posI.y, posI.z);
		transformationMatrix();
		
		
		/*
		 * Draw
		 */
		OpenGL.drawElements(GL_TRIANGLES, vao.indicesLength, GL_UNSIGNED_INT, 0L);
	}
	
	
	
	/*
	 **************************************************************************
	 * METHODS
	 ************************************************************************** 
	 */

	private void transformationMatrix(){
		//T * RZ * RY * RX * S
		transformationMatrixf = MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(scaleMatrixf, rotXMatrixf), rotYMatrixf), rotZMatrixf), translationMatrixf);
		
		//Convert to Float Buffer
		transformationMatrix = MatrixMath.floatArraytoFloatBuffer(transformationMatrixf, transformationMatrix);
		transformationMatrix.flip();
		
		//Send Transformation Matrix to GPU
		OpenGL.matrix4fToGPU(shaderID, "transformationMatrix", transformationMatrix);
	}
	
	
	private void scale(float sX, float sY, float sZ){
		scaleMatrixf[0][0] = sX;   scaleMatrixf[0][1] = 0.0f; scaleMatrixf[0][2] = 0.0f; scaleMatrixf[0][3] = 0.0f; 
		scaleMatrixf[1][0] = 0.0f; scaleMatrixf[1][1] = sY;   scaleMatrixf[1][2] = 0.0f; scaleMatrixf[1][3] = 0.0f; 
		scaleMatrixf[2][0] = 0.0f; scaleMatrixf[2][1] = 0.0f; scaleMatrixf[2][2] = sZ;   scaleMatrixf[2][3] = 0.0f; 
		scaleMatrixf[3][0] = 0.0f; scaleMatrixf[3][1] = 0.0f; scaleMatrixf[3][2] = 0.0f; scaleMatrixf[3][3] = 1.0f;
	}
	
	private void rotate( float rX, float rY, float rZ){
		rX = (float) Math.toRadians(rX);
		rY = (float) Math.toRadians(rY);
		rZ = (float) Math.toRadians(rZ);
		
		rotXMatrixf[0][0] = 1.0f; 				 rotXMatrixf[0][1] = 0.0f; 				   rotXMatrixf[0][2] = 0.0f; 				 rotXMatrixf[0][3] = 0.0f; 
		rotXMatrixf[1][0] = 0.0f; 				 rotXMatrixf[1][1] = (float)Math.cos(rX);  rotXMatrixf[1][2] = (float)Math.sin(rX);  rotXMatrixf[1][3] = 0.0f; 
		rotXMatrixf[2][0] = 0.0f; 				 rotXMatrixf[2][1] = (float)-Math.sin(rX); rotXMatrixf[2][2] = (float)Math.cos(rX);  rotXMatrixf[2][3] = 0.0f; 
		rotXMatrixf[3][0] = 0.0f; 				 rotXMatrixf[3][1] = 0.0f; 				   rotXMatrixf[3][2] = 0.0f; 				 rotXMatrixf[3][3] = 1.0f; 
		
		rotYMatrixf[0][0] = (float)Math.cos(rY); rotYMatrixf[0][1] = 0.0f; 				   rotYMatrixf[0][2] = (float)-Math.sin(rY); rotYMatrixf[0][3] = 0.0f; 
		rotYMatrixf[1][0] = 0.0f; 				 rotYMatrixf[1][1] = 1.0f; 				   rotYMatrixf[1][2] = 0.0f; 				 rotYMatrixf[1][3] = 0.0f; 
		rotYMatrixf[2][0] = (float)Math.sin(rY); rotYMatrixf[2][1] = 0.0f; 				   rotYMatrixf[2][2] = (float)Math.cos(rY);  rotYMatrixf[2][3] = 0.0f; 
		rotYMatrixf[3][0] = 0.0f; 				 rotYMatrixf[3][1] = 0.0f; 				   rotYMatrixf[3][2] = 0.0f; 				 rotYMatrixf[3][3] = 1.0f; 
        
		rotZMatrixf[0][0] = (float)Math.cos(rZ); rotZMatrixf[0][1] = (float)Math.sin(rZ);  rotZMatrixf[0][2] = 0.0f; 				 rotZMatrixf[0][3] = 0.0f; 
		rotZMatrixf[1][0] = (float)-Math.sin(rZ);rotZMatrixf[1][1] = (float)Math.cos(rZ);  rotZMatrixf[1][2] = 0.0f; 				 rotZMatrixf[1][3] = 0.0f; 
		rotZMatrixf[2][0] = 0.0f; 				 rotZMatrixf[2][1] = 0.0f; 			   	   rotZMatrixf[2][2] = 1.0f; 				 rotZMatrixf[2][3] = 0.0f; 
		rotZMatrixf[3][0] = 0.0f; 				 rotZMatrixf[3][1] = 0.0f; 			   	   rotZMatrixf[3][2] = 0.0f; 				 rotZMatrixf[3][3] = 1.0f; 
		
	}
	private void translate(float tX, float tY, float tZ){
	    translationMatrixf[0][0] = 1.0f; translationMatrixf[0][1] = 0.0f; translationMatrixf[0][2] = 0.0f; translationMatrixf[0][3] = 0.0f; 
	    translationMatrixf[1][0] = 0.0f; translationMatrixf[1][1] = 1.0f; translationMatrixf[1][2] = 0.0f; translationMatrixf[1][3] = 0.0f; 
	    translationMatrixf[2][0] = 0.0f; translationMatrixf[2][1] = 0.0f; translationMatrixf[2][2] = 1.0f; translationMatrixf[2][3] = 0.0f; 
	    translationMatrixf[3][0] = tX;   translationMatrixf[3][1] = tY;   translationMatrixf[3][2] = tZ;   translationMatrixf[3][3] = 1.0f; 
	}

	public VAO getVAO() {
		return vao;
	}

	public void setVAO(VAO vao) {
		this.vao = vao;
	}

	public int getShaderID() {
		return shaderID;
	}

	public void setShaderID(int shaderID) {
		this.shaderID = shaderID;
	}

	public int getNumVertexAttribs() {
		return numVertexAttribs;
	}

	public void setNumVertexAttribs(int numVertexAttribs) {
		this.numVertexAttribs = numVertexAttribs;
	}
	
	
	
}
