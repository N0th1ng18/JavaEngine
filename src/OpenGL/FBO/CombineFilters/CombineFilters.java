package OpenGL.FBO.CombineFilters;

import OpenGL.Objects.Texture;
import OpenGL.Objects.VAO;
import OpenGL.OpenGL;
import OpenGL.RenderOrganizer;
import OpenGL.FBO.FBORenderObject;
import OpenGL.FBO.FrameBuffer;


public class CombineFilters {
	public FrameBuffer frameBuffer;
	public FBORenderObject fBORenderObject;
	public float width;
	public float height;
	
	public CombineFilters(float width, float height, VAO vao, Texture texture, int shaderID){
		this.width = width;
		this.height = height;
		frameBuffer = new FrameBuffer(width, height);
		fBORenderObject = new FBORenderObject(vao, texture, shaderID);
	}
	
	public void render(double alpha, int originalColorTextureID, int highLightColorTextureID){
		OpenGL.clearBuffer();
			RenderOrganizer.bindShader(fBORenderObject.getShaderID());
			RenderOrganizer.bindVAO(fBORenderObject.getVAO(), fBORenderObject.getNumVertexAttribs());
			RenderOrganizer.bindTexture(0, originalColorTextureID);
			RenderOrganizer.bindTexture(1, highLightColorTextureID);
			RenderOrganizer.setActiveTexture(0);
			fBORenderObject.render(alpha);	
	}
}
