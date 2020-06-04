package OpenGL.Objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	
		private BufferedImage img;
		
	public Image(String imageLocation) {
		img = null;
		try {
			img = ImageIO.read(new File(imageLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public int getHeight() {
		return img.getHeight();
	}
	public int getWidth() {
		return img.getWidth();
	}
	public int getRGB(int x, int y) {
		return img.getRGB(x, y);
	}
}
