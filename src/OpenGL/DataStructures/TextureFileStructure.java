package OpenGL.DataStructures;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class TextureFileStructure {
	BufferedImage image;
	ByteBuffer buffer;
	int width; 
	int height;
	
	public TextureFileStructure() {
		image = null;
		buffer = null;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	
}
