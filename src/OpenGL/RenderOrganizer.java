package OpenGL;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import OpenGL.Objects.VAO;



//*Bind the VAO
//*Bind the texture
//*Bind the ShaderProgram
//*keep vao bound for objects that use the same vao(model), texture, and shaderProgram
//*Switch VAO when moving to a different Model, switch texture when new texture is needed, switch shaderProgram when shaderProgram is needed

public class RenderOrganizer {
	/***********OpenGL Optimization*******/
		private static final int MAX_VERTEX_ATTRIB_ARRAYS = 6;	//positions, texCoords, normals, ...
		private static final int MAX_TEXTURE_UNITS = 5;	//Texture0, Texture1, Texture2, Texture3, ...
	
		//Shader
		private static int currentBound_Shader = 0;
		//VAO
		private static VAO currentBound_VAO = null;
		//Texture
		private static int currentActiveTexture = 0;
		private static int[] currentBound_Texture = new int[MAX_TEXTURE_UNITS];

	/*
	 *************************************************************
	 * Methods
	 *************************************************************
	 */
		public static void bindShader(int shaderID) {
			if(currentBound_Shader != shaderID) {
				glUseProgram(shaderID);
				currentBound_Shader = shaderID;
			}
		}
		
		public static void bindVAO(VAO vao, int numVertexAttribs) {
			if(currentBound_VAO != vao) {
				//bind VAO
				glBindVertexArray(vao.getVAOID());
				currentBound_VAO = vao;
				
				//Enable and Disable Vertex Attrib Arrays
				for(int i = 0; i < MAX_VERTEX_ATTRIB_ARRAYS; i++) {
					if(i < numVertexAttribs) {
						glEnableVertexAttribArray(i);
					}else {
						glDisableVertexAttribArray(i);
					}
				}
			}	
		}
		
		public static void bindTexture(int textureUnit, int textureID) {
			//GLActiveTexture
			if(textureUnit != currentActiveTexture) {
				glActiveTexture(GL_TEXTURE0 + textureUnit);
				currentActiveTexture = textureUnit;
			}
			//BindTexture
			if(textureID != currentBound_Texture[textureUnit]) {
				glBindTexture(GL_TEXTURE_2D, textureID);
				currentBound_Texture[textureUnit] = textureID;
			}
		}
		
		public static void bindCubeTexture(int textureUnit, int textureID) {
			//GLActiveTexture
			if(textureUnit != currentActiveTexture) {
				glActiveTexture(GL_TEXTURE0 + textureUnit);
				currentActiveTexture = textureUnit;
			}
			//BindTexture
			if(textureID != currentBound_Texture[textureUnit]) {
				glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
				currentBound_Texture[textureUnit] = textureID;
			}
			
		}
		
		public static void setActiveTexture(int textureUnit) {
			//GLActiveTexture
			if(textureUnit != currentActiveTexture) {
				glActiveTexture(GL_TEXTURE0 + textureUnit);
				currentActiveTexture = textureUnit;
			}
		}
}
