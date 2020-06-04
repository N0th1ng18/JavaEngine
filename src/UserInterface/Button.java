package UserInterface;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import Main.Main;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;
import OpenGL.DataStructures.MatrixMath;
import OpenGL.DataStructures.Vector3D;
import OpenGL.Font.Font;
import OpenGL.Objects.Model;
import OpenGL.Objects.Shader;
import OpenGL.Objects.Text2D;
import OpenGL.Objects.VAO;

public class Button {
	//Entities index
	public int index = -1;
	
	//Object Structure
	private VAO vao;
	public int shaderID;
	private int numVertexAttribs;
	private Text2D text;
	private String s;
	private int sIndex;
	private float sPosX;
	private float sPosY;
	
	//Movement Matricies
	private float[][] scaleMatrixf;
	private float[][] translationMatrixf;
	private float[][] transformationMatrixf;
	private FloatBuffer transformationMatrix;
	
	//Movement
	public Vector3D pos;
	public float width;
	public float height;
	//Scale
	public Vector3D scale;
	//Color
	public Vector3D color;
	
	public Button(String s, Text2D text) {
		this.text = text;
		this.s = s;
		
		float[] verticies = {
		        // Left bottom triangle
		        0.0f, 0.0f,
		        0.0f, -1.0f,
		        1.0f, -1.0f,
		        // Right top triangle
		        0.0f, 0.0f,
		        1.0f, -1.0f,
		        1.0f, 0.0f
		};
		
		Model quadModel = new Model(verticies, 2, VAO.Button_VBO);
		Shader objShader = new Shader("src/OpenGL/Objects/Shaders/VertexShader_Button.txt", null, null, null,"src/OpenGL/Objects/Shaders/FragmentShader_Button.txt", Shader.BUTTON_SHADER);
		this.vao = quadModel.vao;
		this.shaderID = objShader.getID();
		
		//Going to be read from a file *********************************
		numVertexAttribs = 1;
		
		//Movement Matricies
		scaleMatrixf = new float[4][4];
		translationMatrixf = new float[4][4];
		transformationMatrixf = new float[4][4];
		transformationMatrix = BufferUtils.createFloatBuffer(16);
		
		//Movement
		width = 200f;
		height = 100f;
		pos = new Vector3D(200.0f, 200.0f, 0.0f);
		//Scale
		scale = new Vector3D(width, height, 1.0f);
		//Color
		color = new Vector3D(0.2f, 0.2f, 0.2f);
		//Text
		sIndex = -1;
	}
	
	public void mouseInput(double posX, double posY) {
		if(posX >= pos.x && posX < pos.x + scale.x && posY >= pos.y && posY < pos.y + scale.y) {
			color.x = 0.4f;
			color.y = 0.4f;
			color.z = 0.4f;
		}else {
			color.x = 0.2f;
			color.y = 0.2f;
			color.z = 0.2f;
		}
		
	}
	
	public void update(double time, double dt) {
		text.out(s, pos.x + width/2f, pos.y + height /2f);
		
	}
	
	public void render(double alpha) {
		//Bind Shader, VAO, Texture
		RenderOrganizer.bindShader(shaderID);
		RenderOrganizer.bindVAO(vao, numVertexAttribs);
		

		
		/*
		 * Transformation Matrix
		 */
		scale(scale.x, scale.y, scale.z);
		translate(pos.x, pos.y, pos.z);
		createTransformationMatrix();
		
		/*
		 * Send Uniform Data
		 */
		transformationMatrix(shaderID);
		buttonCharacteristics(shaderID);
		
		/*
		 * Draw
		 */
		OpenGL.enableBlend();
		OpenGL.disableDepthTest();
		GL11.glDrawArrays(GL_TRIANGLES, 0, vao.vertexCount);
		OpenGL.disableBlend();
		OpenGL.enableDepthTest();
	}
	
	private void buttonCharacteristics(int shaderID) {
		OpenGL.vector3DToGPU(shaderID, "color", color);
		OpenGL.floatToGPU(shaderID, "screenWidth", (float)OpenGL.WIDTH);
		OpenGL.floatToGPU(shaderID, "screenHeight", (float)OpenGL.HEIGHT);
	}
	
	private void transformationMatrix(int shaderID){
		//Send Transformation Matrix to GPU
		OpenGL.matrix4fToGPU(shaderID, "transformationMatrix", transformationMatrix);
	}
	
	private void createTransformationMatrix(){
		//T * RZ * RY * RX * S
		transformationMatrixf = MatrixMath.multiply(scaleMatrixf, translationMatrixf);
		
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
	
	private void translate(float tX, float tY, float tZ){
		//3d Normalised Device Space TO ViewPort Space
		tX = (float) ((2.0f * tX) / OpenGL.WIDTH - 1.0f);
		tY = (float) (1.0f - (2.0f * tY) / OpenGL.HEIGHT);
		
	    translationMatrixf[0][0] = 1.0f; translationMatrixf[0][1] = 0.0f; translationMatrixf[0][2] = 0.0f; translationMatrixf[0][3] = 0.0f; 
	    translationMatrixf[1][0] = 0.0f; translationMatrixf[1][1] = 1.0f; translationMatrixf[1][2] = 0.0f; translationMatrixf[1][3] = 0.0f; 
	    translationMatrixf[2][0] = 0.0f; translationMatrixf[2][1] = 0.0f; translationMatrixf[2][2] = 1.0f; translationMatrixf[2][3] = 0.0f; 
	    translationMatrixf[3][0] = tX;   translationMatrixf[3][1] = tY;   translationMatrixf[3][2] = tZ;   translationMatrixf[3][3] = 1.0f; 
	}

}
