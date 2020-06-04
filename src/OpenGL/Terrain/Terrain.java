package OpenGL.Terrain;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL40.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

import Main.Main;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;
import OpenGL.DataStructures.MatrixMath;
import OpenGL.DataStructures.MeshStructure;
import OpenGL.DataStructures.Vector2D;
import OpenGL.DataStructures.Vector3D;
import OpenGL.Objects.Entities;
import OpenGL.Objects.Texture;
import OpenGL.Objects.VAO;


public class Terrain extends Entities{
	//Entities index
	public int index = -1;
	
	public boolean debugMode = false;
	
	//Terrain Structure
	private VAO vao;
	private int numVertexAttribs;
	public int shaderID;
	private Texture texture;
	private float displacementIntensity;
	private int textureIndex;
	private Vector2D atlasOffsets;
	
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
	private Vector3D rotAngleI;
	private Vector3D rotAngleVel;
	private float rotAngleAccel;
	private float rotAngleNetForce;
	//Scale
	public Vector3D scale;
	
	public float test = 1f;
	public boolean testPlus = false;
	public boolean testMinus = false;
	
	public Terrain(VAO vao, Texture texture, int shaderID) {
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
		numVertexAttribs = 5; //positions
		//Terrain Properties
		displacementIntensity = 2.0f;
		
		//Movement
		pos = new Vector3D(0.0f, 0.0f, 0.0f);
		vel = new Vector3D(0.0f, 0.0f, 0.0f);
		accel = new Vector3D(0.0f, 0.0f, 0.0f);
		posI = new Vector3D(0.0f, 0.0f, 0.0f);
		//Rotate'
		rotAngle = new Vector3D(0.0f, 0.0f, 0.0f);
		rotAngleVel = new Vector3D(0.0f, 0.0f, 0.0f);
		rotAngleAccel = 0.0f;
		rotAngleNetForce = 0.0f;
		rotAngleI = new Vector3D(0.0f, 0.0f, 0.0f);
		//Scale
		scale = new Vector3D(10.0f, 10.0f, 10.0f);
		
		//Set up Textures
		OpenGL.addTextureToShader(shaderID, "colorTexture", 0);
		OpenGL.addTextureToShader(shaderID, "normalMap", 1);
		OpenGL.addTextureToShader(shaderID, "displacementMap", 2);
		OpenGL.addTextureToShader(shaderID, "specularMap", 3);
		
	}
	
	public void update(double time, double dt) {
		if(testPlus == true) {
			test += 2.0f;
		}
		
		if(testMinus == true && test > 1f) {
			test -= 2.0f;
		}
		
		/*
		 * Integration Method
		 */
			//Rotation
			rotAngle.x = rotAngle.x + rotAngleVel.x;
			rotAngle.y = rotAngle.y + rotAngleVel.y;
			rotAngle.z = rotAngle.z + rotAngleVel.z;
			//Movement
			vel.x = vel.x + accel.x;
			vel.y = vel.y + accel.y;
			vel.z = vel.z + accel.z;
			pos.x = pos.x + vel.x;
			pos.y = pos.y + vel.y;
			pos.z = pos.z + vel.z;

	}
	
	public void render(double alpha) {
		//Bind Shader, VAO, Texture
		RenderOrganizer.bindShader(shaderID);
		RenderOrganizer.bindVAO(vao, numVertexAttribs);
		RenderOrganizer.bindTexture(0, texture.getTextureID());
		texture.loadTextureCharacteristics(shaderID);
		texture.loadLightCharacteristics(shaderID);
		RenderOrganizer.bindTexture(1, texture.getNormalMapID());
		RenderOrganizer.bindTexture(2, texture.getDisplacementMapID());
		OpenGL.floatToGPU(shaderID, "displacementIntensity", displacementIntensity);
		RenderOrganizer.bindTexture(3, texture.getSpecularMapID());
		
		/*
		 * Interpolate
		 */
		posI.x = (float) (pos.x + (vel.x * alpha));
		posI.y = (float) (pos.y + (vel.y * alpha));
		posI.z = (float) (pos.z + (vel.z * alpha));
		rotAngleI.x = (float) (rotAngle.x + (rotAngleVel.x * alpha));
		rotAngleI.y = (float) (rotAngle.y + (rotAngleVel.y * alpha));
		rotAngleI.z = (float) (rotAngle.z + (rotAngleVel.z * alpha));
		
		/*
		 * Transformation Matrix
		 */
		scale(scale.x, scale.y, scale.z);
		rotate(rotAngleI.x, rotAngleI.y, rotAngleI.z);
		translate(posI.x, posI.y, posI.z);
		createTransformationMatrix();
		
		/*
		 * Send Uniform Data
		 */
		projectionMatrix();
		viewMatrix();
		transformationMatrix(shaderID);
		cameraPosition();
		Lighting(shaderID);
		Fog(shaderID);
		
		OpenGL.floatToGPU(shaderID, "test", test);
		
		/*
		 * Draw
		 */
		if(debugMode == false) {
			glPatchParameteri(GL_PATCH_VERTICES, 3);//every 3 verts spawns a triangle
			OpenGL.drawElements(GL_PATCHES, vao.indicesLength, GL_UNSIGNED_INT, 0L);
		}else{
			OpenGL.drawElements(GL_POINTS, vao.indicesLength, GL_UNSIGNED_INT, 0L);
		}
		
	}
	
	public void Fog(int shaderID) {
		OpenGL.vector3DToGPU(shaderID, "fogColor", Entities.fogColor);
	}
	
	private void cameraPosition() {
		if(shaderID != cameras.get(activeCameraIndex).shaderID) {
			OpenGL.vector3DToGPU(shaderID, "cameraPos", cameras.get(activeCameraIndex).posI);
		}
	}
	
	public void Lighting(int shaderID) {
		//load Light Characteristics from texture -> when texture was bound
		OpenGL.intToGPU(shaderID, "numLights", lights.size());
		for(int i=0; i < lights.size(); i++) {
			OpenGL.vector3DToGPU(shaderID, "lightPosition["+lights.get(i).index+"]", lights.get(i).posI);
			OpenGL.vector3DToGPU(shaderID, "lightColor["+lights.get(i).index+"]", lights.get(i).lightColor);
			OpenGL.floatToGPU(shaderID, "attenuationRadius["+lights.get(i).index+"]", lights.get(i).attenuationRadius);
		}
	}
	
	private void projectionMatrix() {
		if(shaderID != cameras.get(activeCameraIndex).shaderID) {
			cameras.get(activeCameraIndex).projectionMatrix(shaderID);
		}
	}
	
	private void viewMatrix() {
		if(shaderID != cameras.get(activeCameraIndex).shaderID) {
			cameras.get(activeCameraIndex).viewMatrix(shaderID);
		}
	}
	
	private void transformationMatrix(int shaderID){
		//Send Transformation Matrix to GPU
		OpenGL.matrix4fToGPU(shaderID, "transformationMatrix", transformationMatrix);
	}
	
	private void createTransformationMatrix(){
		//T * RZ * RY * RX * S
		transformationMatrixf = MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(scaleMatrixf, rotXMatrixf), rotYMatrixf), rotZMatrixf), translationMatrixf);
		
		//Convert to Float Buffer
		transformationMatrix = MatrixMath.floatArraytoFloatBuffer(transformationMatrixf, transformationMatrix);
		transformationMatrix.flip();
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
	
	public static void createOffsets(MeshStructure mesh, float terrainWidth, int widthOfInstances) {
		
		float[] offsets = new float[widthOfInstances*widthOfInstances*3];
		
		for(int i=0, j=0, k=0; i < offsets.length; i+=3) {
			offsets[i] = (float)j * terrainWidth;
			offsets[i+1] = 0.0f;
			offsets[i+2] = (float)k * terrainWidth;
					
			if(j == widthOfInstances-1) {
				j=0;
				k++;
			}else {
				j++;
			}
		}
		
		mesh.offsets = offsets;
	}
	
}

