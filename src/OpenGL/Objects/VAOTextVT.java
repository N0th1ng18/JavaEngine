package OpenGL.Objects;

public class VAOTextVT {
	public float[] verticies;
	public float[] texCoords;
	
	public int vertexCount;
	
	public VAOTextVT(int vLength, int tLength) {
		verticies = new float[vLength];
		texCoords = new float[tLength];
	}
}