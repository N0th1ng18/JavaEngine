package OpenGL.Font;

import OpenGL.OpenGL;
import OpenGL.DataStructures.Vector2D;

public class Character {
	int id;
	float posX;
	float posY;
	float width;
	float height;
	float xOffset;
	float yOffset;
	float xAdvance;
	
	//Verticies Quad Info
	public float cWidth_V;
	public float cHeight_V;
	public float xPad_V;
	public float yPad_V;
	
	//TextCoords Quad Info
	public float cWidth_T;
	public float cHeight_T;
	public float cPosX_T;
	public float cPosY_T;
	public float xPad_T;
	public float yPad_T;
	
	
	public Character(int id, int posX, int posY, int width, int height, int xOffset, int yOffset, int xAdvance) {
		this.id = id;
		this.posX = (float)posX;
		this.posY = (float)posY;
		this.width = (float)width;
		this.height = (float)height;
		this.xOffset = (float)xOffset;
		this.yOffset = (float)yOffset;
		this.xAdvance = (float)xAdvance;
	}
	
	
	public void characterToQuadInfo(Font font) {
		float padding = font.getPadding();
		
		//Verticies
		cWidth_V = width / font.scaleW_V;
		cHeight_V = height / font.scaleH_V;
		xPad_V = (padding - font.desiredTexturePadding) / font.scaleW_V;
		yPad_V = (padding - font.desiredTexturePadding) / font.scaleH_V;
		
		//TexCoords
		cWidth_T = width / font.scaleW_T;
		cHeight_T = height / font.scaleH_T;
		cPosX_T = posX / font.scaleW_T;
		cPosY_T = posY / font.scaleH_T;
		xPad_T = (padding - font.desiredTexturePadding) / font.scaleW_T;
		yPad_T = (padding - font.desiredTexturePadding) / font.scaleH_T;
	}
	
	

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getxOffset() {
		return xOffset;
	}

	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}

	public float getxAdvance() {
		return xAdvance;
	}

	public void setxAdvance(float xAdvance) {
		this.xAdvance = xAdvance;
	}
	
}