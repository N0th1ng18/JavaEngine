package OpenGL.FBO;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import OpenGL.OpenGL;
import OpenGL.Objects.Object;


public class FrameBuffer {
	private Object object;
	private int fbo;
	private int fbo_Color_Texture;
	private int fbo_Depth_Texture;
	private int fbo_Depth_Buffer;
	private float width;
	private float height;
	private int numberOfSamples;
	
	public FrameBuffer(float width, float height){
		this.width = width;
		this.height = height;
		
		fbo = createFrameBuffer();
		fbo_Color_Texture = createColorTextureAttachment((int)this.width, (int)this.height);
		fbo_Depth_Texture = createDepthTextureAttachment((int)this.width, (int)this.height);
		fbo_Depth_Buffer = createDepthBufferAttachment((int)this.width, (int)this.height);
	}
	public FrameBuffer(float width, float height, int numberOfSamples){
		this.width = width;
		this.height = height;
		this.numberOfSamples = numberOfSamples;
		
		fbo = createFrameBuffer();
		fbo_Color_Texture = createMultiSampleColorAttachment((int)this.width, (int)this.height);
		fbo_Depth_Buffer = createMultiSampleDepthBufferAttachment((int)this.width, (int)this.height);
	}
	
	
    public void bindFrameBuffer() {
    	OpenGL.bindFrameBuffer(fbo);
    	OpenGL.viewport((int)width, (int)height);
    }
    public void bindFrameBuffer(int fbo, int width, int height) {
    	OpenGL.bindFrameBuffer(fbo);
    	OpenGL.viewport(width, height);
    }
    public void bindScreenFBO() {
    	OpenGL.unBindFrameBuffer();
    	OpenGL.viewport((int)OpenGL.WIDTH, (int)OpenGL.HEIGHT);
    }
    
    
	public void render(double alpha){
		object.render(alpha);	
	}
	
	
	public int getFBO(){
		return fbo;
	}
	public int shaderID(){
		return object.shaderID;
	}
	public int getColorTexture(){
		return fbo_Color_Texture;
	}
	
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
	
	
	
	
	
	
	
	//FBO
	private int createFrameBuffer(){
		int frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);//what color Attachment to render to
		return frameBuffer;
	}
	private int createColorTextureAttachment(int width, int height){
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, color);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_NEAREST);
	    glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0);
	    
	    return texture;
	}
	private int createDepthTextureAttachment(int width, int height){
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT32F, GL_FLOAT, 0);
		float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, color);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_NEAREST);
	    glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0);
	    
	    return texture;
	}
	
	private int createDepthBufferAttachment(int width, int height){
		int depthbuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthbuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbuffer);
		return depthbuffer;
	}
	
	private int createMultiSampleColorAttachment(int width, int height){
		int colorBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, numberOfSamples, GL_RGBA8, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colorBuffer);
		
		return colorBuffer;
	}
	
	private int createMultiSampleDepthBufferAttachment(int width, int height){
		int depthbuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthbuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, numberOfSamples, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbuffer);
		return depthbuffer;
	}
	
}
