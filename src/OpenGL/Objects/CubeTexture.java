package OpenGL.Objects;


import OpenGL.OpenGL;

public class CubeTexture {
	private int textureID;
	
	private float shineDamper;
	private float reflectivity;
	private float ambient;
	
	private int atlasRows;
	
	//Right, Left, Top, Bottom, Back, Front
	public CubeTexture(String right, String left, String top, String bottom, String back, String front) {
		
		String[] locations = new String[6];
		locations[0] = right;
		locations[1] = left;
		locations[2] = top;
		locations[3] = bottom;
		locations[4] = back;
		locations[5] = front;
		
		textureID = OpenGL.loadCubeTexture(locations);
		
		
		atlasRows = 1;
		
		shineDamper = 5.0f;
		reflectivity = 0.1f;
		ambient = 0.1f;
	}
	
	
	
	/*
	 ******************************************************************* 
	 * Methods
	 ******************************************************************* 
	 */
	public void loadTextureCharacteristics(int shaderID){
		//Texture
		OpenGL.floatToGPU(shaderID, "atlasRows", atlasRows);
	}
	
	public void loadLightCharacteristics(int shaderID){
		//Lighting
		OpenGL.floatToGPU(shaderID, "shineDamper", shineDamper);
		OpenGL.floatToGPU(shaderID, "reflectivity", reflectivity);
		OpenGL.floatToGPU(shaderID, "ambient", ambient);
	}
	
	public int getID() {
		return textureID;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getAmbient() {
		return ambient;
	}

	public void setAmbient(float ambient) {
		this.ambient = ambient;
	}
	public int getAtlasRows() {
		return atlasRows;
	}
	public void setAtlasRows(int atlasRows) {
		this.atlasRows = atlasRows;
	}

	
}
