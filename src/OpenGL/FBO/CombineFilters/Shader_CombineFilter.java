package OpenGL.FBO.CombineFilters;


import static org.lwjgl.opengl.GL20.*;
import OpenGL.OpenGL;


public class Shader_CombineFilter {
	int shaderID;
	String vertexShaderLocation;
	String FragmentShaderLocation;
	int location_texture;
	int location_highlightTexture;
	
	public Shader_CombineFilter(String vertexShaderLocation, String FragmentShaderLocation){
		this.vertexShaderLocation = vertexShaderLocation;
		this.FragmentShaderLocation = FragmentShaderLocation;
		createShaderProgram();
	}
	
	public int getID(){
		return shaderID;
	}
	public void createShaderProgram(){
		//vertexShader
		int vertexShader = OpenGL.loadShader(vertexShaderLocation, 0);
		int fragmentShader = OpenGL.loadShader(FragmentShaderLocation, 4);
		
		//Attach the above shader to a program
		int shaderID = glCreateProgram();
		glAttachShader(shaderID, vertexShader);
		glAttachShader(shaderID, fragmentShader);
		
		glBindAttribLocation(shaderID, 0, "position");
		glBindAttribLocation(shaderID, 1, "texCoords");
		glBindAttribLocation(shaderID, 2, "normals");
		
		//Flag the shaders for deletion
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		
		//Link the Program
		glLinkProgram(shaderID);
		
		location_texture = glGetUniformLocation(shaderID, "texture");
		location_highlightTexture = glGetUniformLocation(shaderID, "highlightTexture");
		
		
		glUseProgram(shaderID);
		glUniform1i(location_texture, 0);
		glUniform1i(location_highlightTexture, 1);
		
		this.shaderID = shaderID;
	}
}
