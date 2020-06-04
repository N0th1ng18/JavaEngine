package OpenGL.Objects;

public class TexturePack {
	
	private Texture[] textures;
	private int numTextures;
	private int size;
	
	public TexturePack(int numTextures) {
		this.numTextures = numTextures;
		textures = new Texture[numTextures];
		size = 0;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean isFull() {
		return size == numTextures;
	}
	
	public void addTexture(Texture t) { 
		if(!isFull()) {
			textures[size++] = t;
		}else {
			System.out.println("StackOverFlowException");
		}
	}
	
	public void removeTexture(int index) {
		if(!isEmpty()) {
			for(int i=index; i < size-1; i++) {
				textures[i] = textures[i+1];
			}
			size--;
		}
	}
	
	public Texture get(int index) {
		return textures[index];
	}
	
	public String toString() {
		String s = "";
		for(int i=0; i < size; i++) {
			s += i + ": " + textures[i] + " Size: "+ size +"\n";
		}
		return s;
	}
}
