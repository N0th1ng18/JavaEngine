package OpenGL.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import Main.Main;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;
import OpenGL.DataStructures.MatrixMath;
import OpenGL.DataStructures.Vector2D;
import OpenGL.DataStructures.Vector3D;

public class Camera {
	//Entities index
	public int index = -1;
	
	
	//Structure
	public int shaderID;
	
	//Camera
	float[][] scaleMatrixf;
	float[][] rotXMatrixf;
	float[][] rotYMatrixf;
	float[][] rotZMatrixf;
	float[][] translationMatrixf;
	float[][] viewMatrixf;
	FloatBuffer viewMatrix;
	
	//Movement
	public Vector3D pos;
	public Vector3D vel;
	private Vector3D accel;
	public Vector3D posI;
	//Rotate'
	private Vector3D rotAngle;
	
	//Scale
	private Vector3D scale;
	//Physics
	private float mass;
	private Vector3D netForce;
	private float movForce;
	private float movTheta, movPhi;
	private float movAcceleration;
	private float movFriction;
	
	//Projection
	FloatBuffer projectionMatrix;
	private float fov;
	private float aspect;
	private float near;
	private float far;
	private float yScale;
	private float xScale;
	private float frustrumLength;
	
	//Movement Keys
	boolean forward;
	boolean backward;
	boolean left;
	boolean right;
	
	//Mouse
	public int mouseX;
	public int mouseY;
	int mouseDeltaX;
	int mouseDeltaY;
	double mouseSensitivity;
	
	public Camera(float fov, float aspect, float near, float far, int shaderID) {
	 	this.shaderID = shaderID;
	 	this.fov = (float) ((fov * OpenGL.HEIGHT)/OpenGL.WIDTH);
	 	this.aspect = aspect;
	 	this.near = near;
	 	this.far = far;
	 
	 	//Projection Buffer
	 	projectionMatrix = BufferUtils.createFloatBuffer(16);
	 	//Projection Calculations
	    yScale = (float) (1.0f / (Math.tan(Math.toRadians(this.fov / 2.0f))));
	    xScale = yScale / aspect;
	    frustrumLength = far - near;
	    
	    //Camera buffers
		scaleMatrixf = new float[4][4];
		rotXMatrixf = new float[4][4];
		rotYMatrixf = new float[4][4];
		rotZMatrixf = new float[4][4];
		translationMatrixf = new float[4][4];
		viewMatrixf = new float[4][4];
		viewMatrix = BufferUtils.createFloatBuffer(16);
		
		//Movement
		pos = new Vector3D(0.0f, 0.0f, 0.0f);
		vel = new Vector3D(0.0f, 0.0f, 0.0f);
		accel = new Vector3D(0.0f, 0.0f, 0.0f);
		posI = new Vector3D(0.0f, 0.0f, 0.0f);
		//Rotate'
		rotAngle = new Vector3D(0.0f, 0.0f, 0.0f);
		//Scale
		scale = new Vector3D(1.0f, 1.0f, 1.0f);
		//Physics
		mass = 100.0f;
		netForce = new Vector3D(0.0f, 0.0f, 0.0f);
		movForce = 0.0f;
		movTheta = 0.0f; 
		movPhi = 0.0f;
		movAcceleration = 10.0f;
		movFriction = 10.0f;
		//Mouse
		mouseSensitivity = 0.05;
		mouseX = 0;
		mouseY = 0;
		mouseDeltaX = 0;
		mouseDeltaY = 0;
	}
	
	public void update(double time, double dt) {
		//Projection - Update fov
	    yScale = (float) (1.0f / (Math.tan(Math.toRadians(fov / 2.0f))));
	    xScale = yScale / aspect;
	    frustrumLength = far - near;
		
		//Keyboard Movement
		switch(forward + "-" + backward + "-" + left + "-" + right){
		case "false-false-false-false"://none
			movForce = 0.0f;
			break;
		case "false-false-true-false"://left
			movTheta = 90.0f;
			movPhi = 270.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "false-false-false-true"://right
			movTheta = 90.0f;
			movPhi = 90.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "true-false-false-false"://forward
			movTheta = 90.0f-rotAngle.x;
			movPhi = 180.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "false-true-false-false"://backward
			movTheta = 90.0f+rotAngle.x;
			movPhi = 0.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "true-false-true-false"://left & forward
			movTheta = 90.0f-rotAngle.x;
			movPhi = 225.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "false-true-true-false"://left & backward
			movTheta = 90.0f+rotAngle.x;
			movPhi = 315.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "true-false-false-true"://right & forward
			movTheta = 90.0f-rotAngle.x;
			movPhi = 135.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "false-true-false-true"://right & backward
			movTheta = 90.0f+rotAngle.x;
			movPhi = 45.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "false-false-true-true"://left & right
			movForce = 0.0f;
			break;
		case "true-true-false-false"://forward & backward
			movForce = 0.0f;
			break;
		case "true-true-true-false"://left    & forward & backward
			movTheta = 90.0f;
			movPhi = 270.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "true-true-false-true"://right    & forward & backward
			movTheta = 90.0f;
			movPhi = 90.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "false-true-true-true"://left & right &    backward
			movTheta = 90.0f+rotAngle.x;
			movPhi = 0.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "true-false-true-true"://left & right &    forward
			movTheta = 90.0f-rotAngle.x;
			movPhi = 180.0f+rotAngle.y;
			movForce = movAcceleration;
			break;
		case "true-true-true-true"://left & right & forward & backward
			movForce = 0.0f;
			break;
		}
		
		//Forces
		resetNetForce();
		addForce(movForce, movTheta, movPhi);
		//Friction
		addForceVec(-vel.x * movFriction, -vel.y * movFriction, -vel.z * movFriction);
		
		/*
		 * Integration Method
		 */
		eulerIntegration();
		
		Main.console.out("Camera Pos: " + pos.x +", "+ pos.y + ", "+ pos.z, 0, 20);
		Main.console.out("Camera rotAngle: " + rotAngle.x +", "+ rotAngle.y + ", "+ rotAngle.z, 0, 40);
		Main.console.out("Camera movAcceleration: " + movAcceleration, 0, 60);
	}
	
	public void render(double alpha) {
		
		//Bind Shader, VAO, Texture
		RenderOrganizer.bindShader(shaderID);
		
		//Projection Render
		createProjectionMatrix3D();
		
		//Interpolate
		posI.x = (float) (pos.x + (vel.x * alpha));
		posI.y = (float) (pos.y + (vel.y * alpha));
		posI.z = (float) (pos.z + (vel.z * alpha));
		
		//Camera Render
		scale(scale.x, scale.y, scale.z);
		rotate(rotAngle.x, rotAngle.y, rotAngle.z);
		translate(posI.x, posI.y, posI.z);
		
		/*
		 * Send Uniform Data
		 */
		projectionMatrix(shaderID);
		viewMatrix(shaderID);
	}
	
	public void mouseInput(double mousePosX, double mousePosY){
		//Figure out a way to make mousePosX not grow
		
		//Add Change in oldmousepos - currentmousepos
		mouseDeltaX += (int)mousePosX - mouseX;
		mouseDeltaY += (int)mousePosY - mouseY;
		
		//save currentmousepos
		mouseX = (int)mousePosX;
		mouseY = (int)mousePosY;
		//rotate camera based on delta * mouseSensitivity
		rotAngle.x += -mouseDeltaY * mouseSensitivity;
		rotAngle.y += -mouseDeltaX * mouseSensitivity;
		//set mouse Delta to 0
		mouseDeltaX = 0;
		mouseDeltaY = 0;
		
		//make sure rotAngle doesnt grow forever
		if(rotAngle.x > 90.0f)rotAngle.x = 90.0f;
		if(rotAngle.x < -90.0f)rotAngle.x = -90.0f;
		if(rotAngle.y > 360.0f)rotAngle.y -= 360.0f;
		if(rotAngle.y < -360.0f)rotAngle.y += 360.0f;
	}
	
	public void keyboardInput(int key, int action){
		/*Movements*/	
		if(key == GLFW_KEY_W && action == GLFW_PRESS )	forward = true;
		if(key == GLFW_KEY_W && action == GLFW_RELEASE )	forward = false;
		if(key == GLFW_KEY_S && action == GLFW_PRESS )	backward = true;
		if(key == GLFW_KEY_S && action == GLFW_RELEASE )	backward = false;
		if(key == GLFW_KEY_A && action == GLFW_PRESS )	left = true;
		if(key == GLFW_KEY_A && action == GLFW_RELEASE )	left = false;
		if(key == GLFW_KEY_D && action == GLFW_PRESS )	right = true;
		if(key == GLFW_KEY_D && action == GLFW_RELEASE )	right = false;

		if(key == GLFW_KEY_KP_ADD && action == GLFW_REPEAT)		movAcceleration += 10f;
		if(key == GLFW_KEY_KP_ADD && action == GLFW_PRESS)	movAcceleration += 1f;
		if(key == GLFW_KEY_KP_SUBTRACT && action == GLFW_REPEAT)	movAcceleration -= 10f;
		if(key == GLFW_KEY_KP_SUBTRACT && action == GLFW_PRESS)	movAcceleration -= 1f;
		
		if(movAcceleration <= 0)	movAcceleration = 0;
	}
	
	
	/*
	 **************************************************************************
	 * METHODS
	 ************************************************************************** 
	 */
	
	public void eulerIntegration() {
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
	
	public void addForce(float force, float theta, float phi){
		theta = (float)Math.toRadians(theta);
		phi = (float)Math.toRadians(phi);
		
		
		netForce.x = (float) (netForce.x + (force * Math.sin(theta)*Math.sin(phi)));
		netForce.y = (float) (netForce.y + (force * Math.cos(theta)));
		netForce.z = (float) (netForce.z + (force * Math.sin(theta)*Math.cos(phi)));
	}
	
	public void addForceVec(float x, float y, float z){
		
		netForce.x = netForce.x + x;
		netForce.y = netForce.y + y;
		netForce.z = netForce.z + z;
	}
	
	public void resetNetForce(){
		netForce.x = 0.0f;
		netForce.y = 0.0f;
		netForce.z = 0.0f;
	}
	
	public void projectionMatrix(int shaderID) {
	    //Send Projection Matrix to GPU
	    OpenGL.matrix4fToGPU(shaderID, "projectionMatrix", projectionMatrix);
	}
	
	public void viewMatrix(int shaderID){
		
		//T * RZ * RY * RX * S
		viewMatrixf = MatrixMath.inverse(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(scaleMatrixf, rotXMatrixf), rotYMatrixf), rotZMatrixf), translationMatrixf));
		viewMatrix = MatrixMath.floatArraytoFloatBuffer(viewMatrixf, viewMatrix);
		viewMatrix.flip();
		//Send Matrices to GPU
		OpenGL.matrix4fToGPU(shaderID, "viewMatrix", viewMatrix);
	}
	
	public void viewMatrixSkyBox(int shaderID){
		
		//T * RZ * RY * RX * S
		viewMatrixf = MatrixMath.inverse(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(scaleMatrixf, rotXMatrixf), rotYMatrixf), rotZMatrixf));
		//viewMatrixf = MatrixMath.inverse(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(MatrixMath.multiply(scaleMatrixf, rotXMatrixf), rotYMatrixf), rotZMatrixf), translationMatrixf));
		viewMatrix = MatrixMath.floatArraytoFloatBuffer(viewMatrixf, viewMatrix);
		viewMatrix.flip();
		//Send Matrices to GPU
		OpenGL.matrix4fToGPU(shaderID, "viewMatrix", viewMatrix);
	}
	
	private void createProjectionMatrix3D(){
		
	     projectionMatrix.put(xScale).put(0.0f).put(0.0f).put(0.0f);
	     projectionMatrix.put(0.0f).put(yScale).put(0.0f).put(0.0f);
	     projectionMatrix.put(0.0f).put(0.0f).put(-((far + near) / frustrumLength)).put(-((2 * far * near) / frustrumLength));
	     projectionMatrix.put(0.0f).put(0.0f).put(-1.0f).put(0.0f);
	     projectionMatrix.flip();
	}
	
	private void createProjectionMatrix2D(){
		
	     projectionMatrix.put((float)(0.00001 * OpenGL.HEIGHT)).put(0.0f).put(0.0f).put(0.0f);
	     projectionMatrix.put(0.0f).put((float)(0.00001 * OpenGL.WIDTH)).put(0.0f).put(0.0f);
	     projectionMatrix.put(0.0f).put(0.0f).put(0.0f).put(0.0f);
	     projectionMatrix.put(0.0f).put(0.0f).put(0.0f).put(1.0f);
	     projectionMatrix.flip();
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
	
}
