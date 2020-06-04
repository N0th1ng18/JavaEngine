package OpenGL.Objects;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import OpenGL.OpenGL;

public class Shader {

	public static final int OBJECT_SHADER = 0;
	public static final int TERRAIN_SHADER = 1;
	public static final int TEXT2D_SHADER = 2;
	public static final int BUTTON_SHADER = 3;
	
	private int shaderID;
	
	public Shader(String vertexShaderLocation, String tessControlShaderLocation, String tessEvaluationShaderLocation, String geometryShaderLocation, String FragmentShaderLocation, int id) {
		
		int vertexShader = -1;
		int tessControlShader = -1;
		int tessEvaluationShader = -1;
		int geometryShader = -1;
		int fragmentShader = -1;
		
		//Attach the above shader to a program
		shaderID = glCreateProgram();
		if(vertexShaderLocation != null) {
			vertexShader = OpenGL.loadShader(vertexShaderLocation, 0);
			glAttachShader(shaderID, vertexShader);
		}
		if(tessControlShaderLocation != null) {
			tessControlShader = OpenGL.loadShader(tessControlShaderLocation, 1);
			glAttachShader(shaderID, tessControlShader);
		}
		if(tessEvaluationShaderLocation != null) {
			tessEvaluationShader = OpenGL.loadShader(tessEvaluationShaderLocation, 2);
			glAttachShader(shaderID, tessEvaluationShader);
		}
		if(geometryShaderLocation != null) {
			geometryShader = OpenGL.loadShader(geometryShaderLocation, 3);
			glAttachShader(shaderID, geometryShader);
		}
		if(FragmentShaderLocation != null) {
			fragmentShader = OpenGL.loadShader(FragmentShaderLocation, 4);
			glAttachShader(shaderID, fragmentShader);
		}
		
		switch(id) {
		case 0://OBJECT SHADER
			objectAttributes();
			break;
		case 1://TERRAIN SHADER
			terrainAttributes();
			break;
		case 2://TEXT2D SHADER
			text2DAttributes();
			break;
		case 3://BUTTON SHADER
			buttonAttributes();
			break;
		}
			
			
			//Flag the shaders for deletion
		if(vertexShaderLocation != null)
			glDeleteShader(vertexShader);
		if(tessControlShaderLocation != null)
			glDeleteShader(tessControlShader);
		if(tessEvaluationShaderLocation != null)	
			glDeleteShader(tessEvaluationShader);
		if(geometryShaderLocation != null)	
			glDeleteShader(geometryShader);
		if(FragmentShaderLocation != null)	
			glDeleteShader(fragmentShader);
		//Link the Program
		glLinkProgram(shaderID);
		glValidateProgram(shaderID);
		
		
		
		
	}
	
	public int getID(){
		return shaderID;
	}
	
	
	/*
	 ***************************************************
	 * Types of Shaders
	 ***************************************************
	 */
	
	
	public void objectAttributes() {
		glBindAttribLocation(shaderID, 0, "position");
		glBindAttribLocation(shaderID, 1, "texCoord");
		glBindAttribLocation(shaderID, 2, "normal");
		glBindAttribLocation(shaderID, 3, "tangent");
		glBindAttribLocation(shaderID, 4, "bitangent");
	}
	
	public void terrainAttributes() {
		glBindAttribLocation(shaderID, 0, "position");
		glBindAttribLocation(shaderID, 1, "texCoord");
		glBindAttribLocation(shaderID, 2, "normal");
		glBindAttribLocation(shaderID, 3, "tangent");
		glBindAttribLocation(shaderID, 4, "bitangent");
	}
	
	public void terrainInstancedAttributes() {
		glBindAttribLocation(shaderID, 0, "position");
		glBindAttribLocation(shaderID, 1, "texCoords");
		glBindAttribLocation(shaderID, 2, "offsets");
	}
	
	public void text2DAttributes() {
		glBindAttribLocation(shaderID, 0, "position");
		glBindAttribLocation(shaderID, 1, "texCoord");
	}
	public void buttonAttributes() {
		glBindAttribLocation(shaderID, 0, "position");
	}
}
