package OpenGL.Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import OpenGL.OpenGL;
import OpenGL.DataStructures.Text;
import OpenGL.DataStructures.Vector2D;
import OpenGL.Objects.VAOTextVT;

public class Font {
	public static final int RIGHT_ALIGNED = 0;
	public static final int CENTER_ALIGNED = 1;
	public static final int LEFT_ALIGNED = 2;
	public static final int FULL_CENTER_ALIGNED = 3;
	
	public static final int VERTEX_DIMENSION = 2;
	public static final int VERTICIES_PER_QUAD = 6;
	public static final int MAX_STRING_LENGTH = 300;
	public static final int MAX_CHARACTERS = MAX_STRING_LENGTH * VERTICIES_PER_QUAD * VERTEX_DIMENSION;
	
	public VAOTextVT info;
	
	private float size;
	private float padding;//Same for all directions
	private float lineHeight;
	private int base;
	private float scaleW;
	private float scaleH;
	
	public float scaleW_V;
	public float scaleH_V;
	public float scaleW_T;
	public float scaleH_T;
	
	public float spaceLength;
	public float desiredLineHeight;
	public float desiredCharacterPadding;
	public float desiredTexturePadding;
	public int alignment;
	
	private ArrayList<Character> characters;
	
	public Font(String location, float size,
			float lineHeight, 
			float desiredCharacterPadding, 
			float desiredTexturePadding, 
			int alignment) {
		
		info = new VAOTextVT(MAX_CHARACTERS, MAX_CHARACTERS);			//Quad -> s.length * verticies(6) * 2(x,y);
		
		characters = new ArrayList<Character>();
		
		/************READ FILE************/
		File file = new File(location);
		try{
			Scanner sc = new Scanner(file);
			
			while(sc.hasNext() == true){
				String s = sc.next();
				
				String[] splitString = s.split("=");
				
				//Read Font Information
				switch(splitString[0]) {
				case "size":
					this.size = Integer.parseInt(splitString[1]);
					break;
				case "padding":
					this.padding = (float)(Integer.parseInt(splitString[1].split(",")[0]));
					break;
				case "lineHeight":
					this.lineHeight = Integer.parseInt(splitString[1]);
					break;
				case "base":
					this.base = Integer.parseInt(splitString[1]);
					break;
				case "scaleW":
					this.scaleW = (float)(Integer.parseInt(splitString[1]));
					break;
				case "scaleH":
					this.scaleH = (float)(Integer.parseInt(splitString[1]));
					break;
				}
				
				//Read Each Character Information
				if(splitString[0].equals("char")) {
					
					s = sc.next(); splitString = s.split("=");
					int id = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int x = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int y = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int width = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int height = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int xOffset = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int yOffset = Integer.parseInt(splitString[1]);
					s = sc.next(); splitString = s.split("=");
					int xAdvance = Integer.parseInt(splitString[1]);
					
					Character c = new Character(id, x, y, width, height, xOffset, yOffset, xAdvance);
					characters.add(c);
				}
				
			}
			
			sc.close();
			
		} catch (IOException e){
			System.err.println("Model file wasn't read properly: "+location);
		}
		/*********************************/
		
		if(size > 0)
			this.size = size * 0.1f;
		if(lineHeight > 0)
			this.lineHeight = lineHeight;
		this.desiredCharacterPadding = desiredCharacterPadding;
		this.desiredTexturePadding = desiredTexturePadding;
		this.spaceLength = 0.0f;
		
		this.scaleW_V = scaleW / this.size * (float)OpenGL.aspectRatio;
		this.scaleH_V = scaleH / this.size;
		this.scaleW_T = scaleW;
		this.scaleH_T = scaleH;
		
		this.alignment = alignment;
	}


	
	/*
	 * Create Verticies and Texture Coordinates from a string
	 */
	public void stringToVAOInfo(ArrayList<Text> texts) {
		
		int curIndex = 0;
		for(int k=0; k < texts.size(); k++) {	
			int characterCount = 0;
			String s = texts.get(k).text;
			if(s.length() > MAX_STRING_LENGTH) {
				s = s.substring(0, MAX_STRING_LENGTH);
			}
			
			//Pre calculate width and height of texts
			float length = 0;
			float height = 0;
			for(int i=0; i < s.length(); i++) {
				for(int j=0; j < getCharacters().size(); j++) {
					Character c = getCharacters().get(j);
					if((int)(s.charAt(i)) == c.getID()) {
						length += (c.xAdvance + desiredCharacterPadding) / scaleW_V;
						if(c.height / scaleH_V > height) {
							height = c.height / scaleH_V;
						}
					}
				}
			}
			
			//Convert posX, posY to OpenGL Space
			texts.get(k).textPos.x = (float) ((2.0f * texts.get(k).textPos.x) / OpenGL.WIDTH);
			texts.get(k).textPos.y = (float) -((2.0f * texts.get(k).textPos.y) / OpenGL.HEIGHT);
			
			Vector2D pointer = new Vector2D(0.0f, 0.0f);							//Pointer that points to current character position
			
				for(int i=0; i < s.length(); i++) {									//Go through String of Characters
					for(int j=0; j < getCharacters().size(); j++) {					//For Each Character, search for corresponding font character
						Character c = getCharacters().get(j);
						
						if((int)(s.charAt(i)) == c.getID()) {						//Found corresponding font character	
							
							if(c.getID() == 10) {									//Check for \n (new line)
								pointer.x = 0.0f;
								pointer.y -= (getLineHeight()) / scaleH_V;
								break;
							}
							
							c.characterToQuadInfo(this);	
							
							float offsetX = 0;
							float offsetY = 0;
							
							switch(alignment) {
							case LEFT_ALIGNED:
								offsetX = pointer.x + c.getxOffset() / scaleW_V + texts.get(k).textPos.x;
								offsetY = pointer.y - c.getyOffset() / scaleH_V + texts.get(k).textPos.y;
								break;
							case CENTER_ALIGNED:
								offsetX = pointer.x + c.getxOffset() / scaleW_V + texts.get(k).textPos.x - (length/2f);
								offsetY = pointer.y - c.getyOffset() / scaleH_V + texts.get(k).textPos.y;
								break;
							case RIGHT_ALIGNED:
								offsetX = pointer.x + c.getxOffset() / scaleW_V + texts.get(k).textPos.x - (length);
								offsetY = pointer.y - c.getyOffset() / scaleH_V + texts.get(k).textPos.y;
								break;
							case FULL_CENTER_ALIGNED:
								offsetX = pointer.x + c.getxOffset() / scaleW_V + texts.get(k).textPos.x - (length/2f);
								offsetY = pointer.y - c.getyOffset() / scaleH_V + texts.get(k).textPos.y + (height/2f);
								break;
							}
							
							//Verticies
							info.verticies[0+(i*6*2)+curIndex] = offsetX; 								info.verticies[1+(i*6*2)+curIndex] = offsetY;
							info.verticies[2+(i*6*2)+curIndex] = offsetX; 								info.verticies[3+(i*6*2)+curIndex] = offsetY - c.cHeight_V + 2*c.yPad_V;
							info.verticies[4+(i*6*2)+curIndex] = offsetX + c.cWidth_V - 2*c.xPad_V; 	info.verticies[5+(i*6*2)+curIndex] = offsetY;
							info.verticies[6+(i*6*2)+curIndex] = offsetX; 								info.verticies[7+(i*6*2)+curIndex] = offsetY - c.cHeight_V + 2*c.yPad_V;
							info.verticies[8+(i*6*2)+curIndex] = offsetX + c.cWidth_V - 2*c.xPad_V; 	info.verticies[9+(i*6*2)+curIndex] = offsetY - c.cHeight_V + 2*c.yPad_V;
							info.verticies[10+(i*6*2)+curIndex] = offsetX + c.cWidth_V - 2*c.xPad_V; 	info.verticies[11+(i*6*2)+curIndex] = offsetY;
						
						
							//TextCoords
							info.texCoords[0+(i*6*2)+curIndex] = c.cPosX_T + c.xPad_T; 					info.texCoords[1+(i*6*2)+curIndex] = c.cPosY_T + c.yPad_T;
							info.texCoords[2+(i*6*2)+curIndex] = c.cPosX_T + c.xPad_T; 					info.texCoords[3+(i*6*2)+curIndex] = c.cPosY_T + c.cHeight_T - c.yPad_T;
							info.texCoords[4+(i*6*2)+curIndex] = c.cPosX_T + c.cWidth_T - c.xPad_T; 	info.texCoords[5+(i*6*2)+curIndex] = c.cPosY_T + c.yPad_T;
							info.texCoords[6+(i*6*2)+curIndex] = c.cPosX_T + c.xPad_T; 					info.texCoords[7+(i*6*2)+curIndex] = c.cPosY_T + c.cHeight_T - c.yPad_T;
							info.texCoords[8+(i*6*2)+curIndex] = c.cPosX_T + c.cWidth_T - c.xPad_T; 	info.texCoords[9+(i*6*2)+curIndex] = c.cPosY_T + c.cHeight_T - c.yPad_T;
							info.texCoords[10+(i*6*2)+curIndex] = c.cPosX_T + c.cWidth_T - c.xPad_T; 	info.texCoords[11+(i*6*2)+curIndex] = c.cPosY_T + c.yPad_T;
						
							pointer.x += (c.getxAdvance() + desiredCharacterPadding) / scaleW_V;
							
							characterCount++;
							break;
							
						}
					}
				}
			curIndex += characterCount * 6 * 2;
		}
		info.vertexCount = curIndex/2;
	}
	
	

	
	public float getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public float getPadding() {
		return padding;
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}

	public float getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public float getScaleW() {
		return scaleW;
	}

	public void setScaleW(float scaleW) {
		this.scaleW = scaleW;
	}

	public float getScaleH() {
		return scaleH;
	}

	public void setScaleH(float scaleH) {
		this.scaleH = scaleH;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
	}
	
	
	
	
}
