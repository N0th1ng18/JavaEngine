package OpenGL.FBO.MultiSample;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import OpenGL.Objects.VAO;
import OpenGL.FBO.FrameBuffer;
import OpenGL.OpenGL;


public class MultiSample {
	public FrameBuffer frameBuffer;
	public float width;
	public float height;
	
	public MultiSample(float width, float height, int numberOfSamples, VAO vao, int shaderID){
		this.width = width;
		this.height = height;
		frameBuffer = new FrameBuffer(width, height, numberOfSamples);
	}
	
	public void renderMultiSample(FrameBuffer outputFrameBufferObject){
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, outputFrameBufferObject.getFBO());
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer.getFBO());
		glBlitFramebuffer(0, 0, (int)width, (int)height, 0, 0, (int)outputFrameBufferObject.getWidth(), (int)outputFrameBufferObject.getHeight(), 
				GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);	
	}
	
	public void renderMultiSampleToScreen(){
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.frameBuffer.getFBO());
		glDrawBuffer(GL_BACK);
		glBlitFramebuffer(0, 0, (int)width, (int)height, 0, 0, (int)OpenGL.WIDTH, (int)OpenGL.HEIGHT, 
				GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}
}
