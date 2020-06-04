package OpenGL.Objects;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import OpenGL.Objects.VAO;
import OpenGL.Objects.Texture;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;
import OpenGL.DataStructures.MatrixMath;
import OpenGL.DataStructures.Text;
import OpenGL.DataStructures.Vector2D;
import OpenGL.DataStructures.Vector3D;
import OpenGL.Font.Font;


public class Text2D extends Entities{
	//Entities index
	public int index = -1;
	
	//Object Structure
	private VAO vao;
	private int numVertexAttribs;
	public int shaderID;
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
	public Vector2D pos;
	public Vector2D vel;
	private Vector2D posI;
	//Rotate'
	private Vector3D rotAngle;
	//Scale
	public Vector3D scale;
	//Physics
	private float mass;
	private Vector3D netForce;
	//Text Color
	FloatBuffer buffer;
	Font font;
	ArrayList<Text> texts;
	ArrayList<Vector2D> textPos;
	ArrayList<String> prevTexts;
	ArrayList<Vector2D> prevTextPos;
	float[] lengths;
	private VAOTextVT VAOInfo;
	private Vector3D textColor;
	private Vector3D outlineColor;
	private float cWidth;
	private float cEdge;
	private float outlineWidth;
	private float outlineEdge;
	
	public Text2D(VAO vao, Texture texture, Font font, int shaderID) {
		//Object Structure
		this.vao = vao;
		this.texture = texture;
		this.shaderID = shaderID;
		this.font = font;
		
		//Movement Matricies
		scaleMatrixf = new float[4][4];
		rotXMatrixf = new float[4][4];
		rotYMatrixf = new float[4][4];
		rotZMatrixf = new float[4][4];
		translationMatrixf = new float[4][4];
		transformationMatrixf = new float[4][4];
		transformationMatrix = BufferUtils.createFloatBuffer(16);
		
		//Going to be read from a file *********************************
		numVertexAttribs = 2;
		//Movement
		pos = new Vector2D(0.0f, 0.0f);
		vel = new Vector2D(0.0f, 0.0f);
		posI = new Vector2D(0.0f, 0.0f);
		//Rotate
		rotAngle = new Vector3D(0.0f, 0.0f, 0.0f);
		//Scale
		scale = new Vector3D(1.0f, 1.0f, 1.0f);
		//Physics
		mass = 10.0f;
		netForce = new Vector3D(0.0f, 0.0f, 0.0f);
		//Text Color
		buffer = BufferUtils.createFloatBuffer(Font.MAX_CHARACTERS);
		texts = new ArrayList<Text>();
		textColor = new Vector3D(0.0f, 0.0f, 1.0f);
		outlineColor = new Vector3D(0.0f, 0.0f, 0.0f);
		cWidth = 0.46f;
		cEdge = 0.08f;
		outlineWidth = 0.45f;
		outlineEdge = 0.4f;
		
	}
	
	public void update(double time, double dt) {
		
		/*
		 * Integration Method
		 */
		pos.x = pos.x + vel.x;
		pos.y = pos.y + vel.y;
		
		updateVBOs();
	}
	
	public void render(double alpha) {
		//Bind Shader, VAO, Texture
		RenderOrganizer.bindShader(shaderID);
		RenderOrganizer.bindVAO(vao, numVertexAttribs);
		RenderOrganizer.bindTexture(0, texture.getTextureID());
		
		/*
		 * Interpolate
		 */
		posI.x = (float) (pos.x + (vel.x * alpha));
		posI.y = (float) (pos.y + (vel.y * alpha));
		
		/*
		 * Transformation Matrix
		 */
		scale(scale.x, scale.y, scale.z);
		rotate(rotAngle.x, rotAngle.y, rotAngle.z);
		translate(posI.x, posI.y);
		createTransformationMatrix();
		
		/*
		 * Send Uniform Data
		 */
		transformationMatrix(shaderID);
		textCharacteristics(shaderID);
		
		
		/*
		 * Draw
		 */
		OpenGL.enableBlend();
		OpenGL.disableDepthTest();
		
		GL11.glDrawArrays(GL_TRIANGLES, 0, vao.vertexCount);
		
		OpenGL.disableBlend();
		OpenGL.enableDepthTest();
	}
	

	/*
	 **************************************************************************
	 * METHODS
	 ************************************************************************** 
	 */
	public void out(String s, float posX, float posY) {
		texts.add(new Text(s, posX, posY));
	}
	
	private void updateVBOs() {
		if(isTextDifferent()) {
				
			for(int k=0; k < texts.size(); k++) {						//Save Current text and pos
				texts.get(k).saveCurrentState();
			}
			
			font.stringToVAOInfo(texts);						//Add in all the texts
			
			vao.vertexCount = font.info.vertexCount;					//Update VertexCount
			VAO_Manager.updateVBO(vao, 0, 0, font.info.verticies, buffer);	//Update VBO holding verticies
			VAO_Manager.updateVBO(vao, 1, 0, font.info.texCoords, buffer);	//Update VBO holding TexCoords
			
		}
		texts.clear();														//Reset ArrayLists, Solves updating every frame issue
	}
	
	private void textCharacteristics(int shaderID) {
		OpenGL.vector3DToGPU(shaderID, "textColor", textColor);
		OpenGL.vector3DToGPU(shaderID, "outlineColor", outlineColor);
		OpenGL.floatToGPU(shaderID, "cWidth", cWidth);
		OpenGL.floatToGPU(shaderID, "cEdge", cEdge);
		OpenGL.floatToGPU(shaderID, "outlineWidth", outlineWidth);
		OpenGL.floatToGPU(shaderID, "outlineEdge", outlineEdge);
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
	private void translate(float tX, float tY){
		//ViewPort Space To 3d Normalised Device Space
		tX = (float) ((2.0f * tX) / OpenGL.WIDTH - 1.0f);
		tY = (float) (1.0f - (2.0f * tY) / OpenGL.HEIGHT);
		
	    translationMatrixf[0][0] = 1.0f; 	translationMatrixf[0][1] = 0.0f; 	translationMatrixf[0][2] = 0.0f; translationMatrixf[0][3] = 0.0f; 
	    translationMatrixf[1][0] = 0.0f; 	translationMatrixf[1][1] = 1.0f;	translationMatrixf[1][2] = 0.0f; translationMatrixf[1][3] = 0.0f; 
	    translationMatrixf[2][0] = 0.0f; 	translationMatrixf[2][1] = 0.0f; 	translationMatrixf[2][2] = 1.0f; translationMatrixf[2][3] = 0.0f; 
	    translationMatrixf[3][0] = tX; 		translationMatrixf[3][1] = tY;  	translationMatrixf[3][2] = 0.0f; translationMatrixf[3][3] = 1.0f; 
	}
	
	private boolean isTextDifferent() {
		for(int i=0; i < texts.size(); i++) {
			if(!texts.get(i).text.equals(texts.get(i).prevText) || texts.get(i).textPos.x != texts.get(i).prevTextPos.x || texts.get(i).textPos.y != texts.get(i).prevTextPos.y) {
				return true;
			}
		}
		return false;
	}
	
}

