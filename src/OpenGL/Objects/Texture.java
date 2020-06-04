package OpenGL.Objects;


import OpenGL.OpenGL;

public class Texture {
	private int textureID = -1;
	private int normalMapID = -1;
	private int displacementMapID = -1;
	private int specularMapID = -1;
	private int heightMapID = -1;
	
	public float shineDamper;
	public float reflectivity;
	public float ambient;
	
	private int atlasRows;
	
	public Texture(String texturePath, String normalMapPath, String displacementMapPath, String specularMapPath, String heightMapPath) {
		if(texturePath != null)
			textureID = OpenGL.loadTexture(texturePath);
		
		if(normalMapPath != null)
			normalMapID = OpenGL.loadTexture(normalMapPath);
		
		if(displacementMapPath != null)
			displacementMapID = OpenGL.loadTexture(displacementMapPath);
		
		if(specularMapPath != null)
			specularMapID = OpenGL.loadTexture(specularMapPath);
		
		if(heightMapPath != null)
			heightMapID = OpenGL.loadTexture(heightMapPath);
		
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
	
	
	
	public int getTextureID() {
		return textureID;
	}

	public void setTextureID(int textureID) {
		this.textureID = textureID;
	}
	
	public int getNormalMapID() {
		return normalMapID;
	}
	
	public void setNormalMapID(int normalMapID) {
		this.normalMapID = normalMapID;
	}

	public int getDisplacementMapID() {
		return displacementMapID;
	}

	public void setDisplacementMapID(int displacementMapID) {
		this.displacementMapID = displacementMapID;
	}

	public int getSpecularMapID() {
		return specularMapID;
	}

	public void setSpecularMapID(int specularMapID) {
		this.specularMapID = specularMapID;
	}

	public int getHeightMapID() {
		return heightMapID;
	}

	public void setHeightMapID(int heightMapID) {
		this.heightMapID = heightMapID;
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
