package OpenGL.FBO.HorizontalGaussianBlur;

import OpenGL.Objects.Texture;
import OpenGL.Objects.VAO;
import OpenGL.FBO.FBORenderObject;
import OpenGL.FBO.FrameBuffer;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;


public class HorizontalGaussianBlur {
	public FrameBuffer frameBuffer;
	public FBORenderObject fBORenderObject;
	public float width;
	public float height;
	
	public HorizontalGaussianBlur(float width, float height, VAO vao, Texture texture, int shaderID){
		this.width = width;
		this.height = height;
		frameBuffer = new FrameBuffer(width, height);
		fBORenderObject = new FBORenderObject(vao, texture, shaderID);
	}
	
	public void render(double alpha, int colorTextureID){
		RenderOrganizer.bindShader(fBORenderObject.getShaderID());
		RenderOrganizer.bindVAO(fBORenderObject.getVAO(), fBORenderObject.getNumVertexAttribs());
		RenderOrganizer.bindTexture(0, colorTextureID);
		sendUniformDataToGPU();
		fBORenderObject.render(alpha);	
	}
	
	public void sendUniformDataToGPU() {
		OpenGL.floatToGPU(fBORenderObject.getShaderID(), "widthOfFBO", width);
	}
}
