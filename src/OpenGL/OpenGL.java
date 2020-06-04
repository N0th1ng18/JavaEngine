package OpenGL;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.imageio.ImageIO;
import OpenGL.GameLoop;
import OpenGL.DataStructures.TextureFileStructure;
import OpenGL.DataStructures.Vector2D;
import OpenGL.DataStructures.Vector3D;



abstract public class OpenGL extends GameLoop{
	
	public static double WIDTH;
	public static double HEIGHT;
	
	public static long window;
	
	//Settings
	public static double aspectRatio;
	public static float mipMappingValue = 0.0f;//-0.4f; //mipMapping vs anisotropicFiltering
    public static float anisotropicFilterValue = 4.0f;  //mipMapping vs anisotropicFiltering
	
	/************************************************************** 
	 * 
	 * Init
	 * 
	 **************************************************************/
		public static void initOpenGL() {
				
				//Error Setup**********************
				GLFWErrorCallback.createPrint(System.err).set();// Setup an error callback. The default implementation will print the error message in System.err.
				if ( !glfwInit() )// Initialize GLFW. Most GLFW functions will not work before doing this.
					throw new IllegalStateException("Unable to initialize GLFW");
			//*********************************
			
			//Window init()********************
				glfwDefaultWindowHints(); // optional, the current window hints are already the default
				glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
				glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
				GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
				WIDTH = vidmode.width();
				HEIGHT = vidmode.height();
				aspectRatio = WIDTH/HEIGHT;
				glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate()); // Refresh rate
				// Create the window
				window = glfwCreateWindow((int)(WIDTH), (int)(HEIGHT), "ProjectEngine", glfwGetPrimaryMonitor(), NULL);
				if ( window == NULL )
					throw new RuntimeException("Failed to create the GLFW window");
				//Screen Position
				glfwSetWindowPos(window,(vidmode.width() - (int)(WIDTH)) / 2,(vidmode.height() - (int)(HEIGHT)) / 2);// Center our window
				//Screen Properties
				glfwMakeContextCurrent(window);// Make the OpenGL context current
				glfwSwapInterval(0);// Enable v-sync
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
				glfwShowWindow(window);
			//**********************************
			
				
				
			//OpenGL init()*********************
				GL.createCapabilities();
				
				glShadeModel(GL_SMOOTH);
				glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
				glEnable(GL_CULL_FACE);
				glCullFace(GL_BACK);
				glFrontFace(GL_CCW);
				//glEnable( GL_BLEND );
				//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				//glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
												 //GL_POINT
												 //GL_LINE
												 //GL_FILL (Default)
				glClearColor(1.5f, 0.5f, 0.5f, 1.0f);// Set the clear color
				glClearDepth (1.0f); 
				glDepthFunc(GL_LEQUAL);//lequal
				glEnable(GL_DEPTH_TEST);
				//glEnable(GL30.GL_CLIP_DISTANCE0);
				
				//
				
				System.out.println("OS Name: " + System.getProperty("os.name"));
				System.out.println("OS Version: " + System.getProperty("os.version"));
				System.out.println("LWJGL Version: " + Version.getVersion());
				System.out.println("OpenGL version: " + glGetString(GL_VERSION));
				System.out.println("Max Texture Units Supported: " + glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS));
				System.out.println("Max Vertex Attribs: " + glGetInteger(GL_MAX_VERTEX_ATTRIBS));
				System.out.println("Max Patch Vertices: " + glGetInteger(GL_MAX_PATCH_VERTICES));
				System.out.println("Max Tess Gen Level: " + glGetInteger(GL_MAX_TESS_GEN_LEVEL));
			//**********************************
			
		}
		
	
	/************************************************************** 
	 * 
	 * Input Update
	 * 
	 **************************************************************/
		public static void pollInput() {
			glfwPollEvents();
		}	
		public static void setCursorPos(double x, double y) {
			glfwSetCursorPos (window, x, y);
		}
		
		public static void disableCursor() {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		}
		
		public static void enableCursor() {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		
		public static void setCursorPosCallback(GLFWCursorPosCallbackI mouseInputMethod) {
			glfwSetCursorPosCallback(window, mouseInputMethod);
		}
		
		public static void setKeyCallback(GLFWKeyCallbackI keyboardInputMethod) {
			glfwSetKeyCallback(window, keyboardInputMethod);
		}	
		
		
	/************************************************************** 
	 * 
	 * Update
	 * 
	 **************************************************************/
	
	
	
	/************************************************************** 
	 * 
	 * Render
	 * 
	 **************************************************************/
		public static void bindFrameBuffer(int fbo) {
			glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		}
		public static void unBindFrameBuffer() {
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}
		public static void viewport(int width, int height) {
			glViewport(0, 0, width, height);
		}
		public static void clearBuffer() {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
		public static void swapBuffers() {
			glfwSwapBuffers(window);
		}
		public static void matrix4fToGPU(int shaderID, CharSequence name, FloatBuffer buffer) {
			glUniformMatrix4fv(glGetUniformLocation(shaderID, name), false, buffer);
		}
		public static void vector2DToGPU(int shaderID, CharSequence name, Vector2D vec) {
			glUniform2f(glGetUniformLocation(shaderID, name), vec.x, vec.y);
		}
		public static void vector3DToGPU(int shaderID, CharSequence name, Vector3D vec) {
			glUniform3f(glGetUniformLocation(shaderID, name), vec.x, vec.y, vec.z);
		}
		public static void floatToGPU(int shaderID, CharSequence name, float a) {
			glUniform1f(glGetUniformLocation(shaderID, name), a);
		}
		public static void intToGPU(int shaderID, CharSequence name, int a) {
			glUniform1i(glGetUniformLocation(shaderID, name), a);
		}
		public static void drawElements(int mode, int count, int type, long indices) {
			glDrawElements(mode, count, type, indices);
		}
		public static void drawElementsInstanced(int mode, int count, int type, long indices, int numOfInstances) {
			glDrawElementsInstanced(mode, count, type, indices, numOfInstances);
		}
		public static void wireFrame(boolean b) {
			if(b) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			}else {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}
		}
		
	/************************************************************* 
	 * 
	 * Terminate Program
	 * 
	 *************************************************************/
		public static void terminate() {
			glfwSetWindowShouldClose(window, true);
		
			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
			
			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}

	
	/**************************************************************
	 * 
	 * Model Methods
	 * 
	 **************************************************************/
		
		//PUT MODEL LOADER HERE
		
	/**************************************************************
	 * 
	 * Texture Methods
	 * 
	 **************************************************************/
	    public static int loadTexture(String path) {
	  	   
	    	TextureFileStructure textureFileStruct = readTextureFile(path);
	    
	        // Create a new texture object in memory and bind it
	        int texId = glGenTextures();
	        RenderOrganizer.bindTexture(0, texId);
	         
	        // All RGB bytes are aligned to each other and each component is 1 byte
	        //GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
	         
	        // Setup the ST coordinate system
	    	float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	    	glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, color);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

	        // Upload the texture data and generate mip maps (for scaling)
	        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureFileStruct.getWidth()-1, textureFileStruct.getHeight()-1, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureFileStruct.getBuffer());
	        glGenerateMipmap(GL_TEXTURE_2D);
	        
	        // Setup what to do when the texture has to be scaled
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, mipMappingValue); 
	        
	        
	        if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
	        	float value = Math.min(anisotropicFilterValue, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
	        	GL11.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, value);
	        }else {
	        	  System.out.println("TextureFilterAnisotropic Not Supported!");
	        }
	        
	        RenderOrganizer.bindTexture(0, 0);
	         
	        return texId;
	    }
	    
	    public static int loadCubeTexture(String[] filename) {
	        // Create a new texture object in memory and bind it
	        int texId = glGenTextures();
	        RenderOrganizer.bindCubeTexture(0, texId);
	        
		  	for(int i = 0; i < filename.length; i++) {
		  		TextureFileStructure modelFileStruct = readTextureFile(filename[i]);
		        // Upload the texture data -> Right, Left, Top, Bottom, Back, Front
		        glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, modelFileStruct.getWidth()-1, modelFileStruct.getHeight()-1, 0, GL_RGBA, GL_UNSIGNED_BYTE, modelFileStruct.getBuffer());
		  	}
		  	
	        // Setup what to do when the texture has to be scaled
	        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		  	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		  	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); 
		  	
		  	RenderOrganizer.bindCubeTexture(0, 0);
		  	
	        return texId;
	    }
	    
	    public static TextureFileStructure readTextureFile(String filename) {
	    	
	    	BufferedImage image = null;
	    	try {
	    		image = ImageIO.read(new File(filename));
	    	} catch (IOException e) {
	    	}
	    	if(image == null){
	    		System.err.println("Texture Load Error: "+filename+" was not found");
	    		return null;
	    	}
	    	   
	        int[] pixels = new int[image.getWidth() * image.getHeight()];
	        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

	          ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB
	          
	          for(int y = 0; y < image.getHeight()-1; y++){
	              for(int x = 0; x < image.getWidth()-1; x++){
	                  int pixel = pixels[y * image.getWidth() + x];
	                  buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
	                  buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
	                  buffer.put((byte) (pixel & 0xFF));               // Blue component
	                  buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
	              }
	          }
	          
	          buffer.flip();
	          
	          
	          TextureFileStructure textureFileStruct = new TextureFileStructure();
	          textureFileStruct.setImage(image);
	          textureFileStruct.setBuffer(buffer);
	          textureFileStruct.setWidth(image.getWidth());
	          textureFileStruct.setHeight(image.getHeight());
	          
	          return textureFileStruct;
	          
	    }
		
		
	/**************************************************************
	 * 
	 * Shader Methods
	 * 
	 **************************************************************/
		public static int loadShader(String location, int type){
			
			StringBuilder stringBuilder = new StringBuilder();
			try{
				BufferedReader reader = new BufferedReader(new FileReader(location));
				String  line;
				while((line = reader.readLine()) != null){
					stringBuilder.append(line).append('\n');
				}
				reader.close();
			} catch (IOException e){
				System.err.println("Shader wasn't loaded properly at "+location+" location.");
			}
			
			int id = 0;
			switch(type) {
			case 0:
				id = glCreateShader(GL_VERTEX_SHADER);
				break;
			case 1:
				id = glCreateShader(GL_TESS_CONTROL_SHADER);
				break;
			case 2:
				id = glCreateShader(GL_TESS_EVALUATION_SHADER);
				break;
			case 3:
				id = glCreateShader(GL_GEOMETRY_SHADER);
				break;
			case 4:
				id = glCreateShader(GL_FRAGMENT_SHADER);
				break;
			default:
				System.err.println("Shader Type "+type+" does not exist.");
				return 0;
			}
			
			glShaderSource(id,stringBuilder);
			glCompileShader(id);
			if(glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE){
				System.err.println("Shader wasn't able to be compiled correctly at "+location+" location. \n"+glGetShaderInfoLog(id));
			}
			//---------------------------------------------------
			
			return id;
		}
	
	/**************************************************************
	 * 
	 * Other Methods
	 * 
	 **************************************************************/
		public static void culling(boolean b) {
			if(b == true)
				glEnable(GL_CULL_FACE);
			else {
				glDisable(GL_CULL_FACE);
			}
		}
		public static void enableBlend() {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		public static void disableBlend() {
			GL11.glDisable(GL11.GL_BLEND);
		}
		public static void disableDepthTest() {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
		public static void enableDepthTest() {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		public static void enableDepthMask() {
			glDepthMask(true);
		}
		public static void disableDepthMask() {
			glDepthMask(false);
		}
		public static void enableSeamlessCubeMap() {
			 glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		}
		public static void disableSeamlessCubeMap() {
			 glDisable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		}
		public static void addTextureToShader(int shaderID, String name, int textureUnit) {
			int location  = glGetUniformLocation(shaderID, name);
			glUseProgram(shaderID);
			glUniform1i(location, textureUnit);
			glUseProgram(0);
		}
		
}
