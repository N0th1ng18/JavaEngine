package Main;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Random;

import OpenGL.FBO.BrightnessFilter.BrightnessFilter;
import OpenGL.FBO.CombineFilters.CombineFilters;
import OpenGL.FBO.CombineFilters.Shader_CombineFilter;
import OpenGL.FBO.ContrastFilter.ContrastFilter;
import OpenGL.FBO.HorizontalGaussianBlur.HorizontalGaussianBlur;
import OpenGL.FBO.MultiSample.MultiSample;
import OpenGL.FBO.VerticalGaussianBlur.VerticalGaussianBlur;
import OpenGL.Font.Font;
import OpenGL.OpenGL;
import OpenGL.DataStructures.MeshStructure;
import OpenGL.DataStructures.Vector2D;
import OpenGL.DataStructures.Vector3D;
import OpenGL.Objects.Camera;
import OpenGL.Objects.CubeTexture;
import OpenGL.Objects.Entities;
import OpenGL.Objects.Image;
import OpenGL.Objects.Light;
import OpenGL.Objects.Model;
import OpenGL.Objects.Shader;
import OpenGL.Objects.SkyBox;
import OpenGL.Objects.Text2D;
import OpenGL.Objects.Texture;
import OpenGL.Objects.TexturePack;
import OpenGL.Objects.VAO;
import OpenGL.Terrain.Terrain;
import OpenGL.DataStructures.Vector3D;
import UserInterface.Button;
import OpenGL.Objects.Object;

public class Main extends OpenGL{

	/*
	 * Things to do
	 * 2: Attaching Object to Camera
	 * 3: Multiple cameras (Active Camera)
	 * 
	 * 4: transparency	
	 * 
	 * TESSELLATION
	 * 	1) PN-AEN
	 * 	2) Dominant UV
	 * 	3) Mesh Dicing
	 *  4) anti-shimmering using different mip levels
	 *  	-fix interpolation
	 *  	-add control point fix
	 *  5)	silhouette edge tessellation
	 *  
	 *  
	 * 4: Make 2 types of Models 
	 * 		-Static Models where the normal, tangent, and bitangent are pre calculated and never change
	 * 		-Dynamic Models where the normal, tanget, and bitangent are calculated each frame in the shader
	 * 4: Water using dynamic models
	 * 5: Text - 
	 * 			FIX HOW TO STORE AND CHANGE VBO OF TEXT DATA WITH SAME FONT
	 * 			FIX ENTIRE FONT/CHARACTER/STRING SETUP
	 * 			Make Console Dynamic text2D seperate from other texts
	 * 			Make Static Text - VBO is not changing every Update
	 * 			3D text
	 * 6: Shadows
	 * 7: Clean up Shaders and other openGL things in Terminate
	 * 		- delete VAO's
	 * 8: Upload entities from file and to file
	 */
	public static boolean wireframe = false;
	public static boolean editorMode = false;
	public static boolean debugMode = false;
	public static Text2D console;
	
	ContrastFilter contrastFilter;
	BrightnessFilter brightnessFilter;
	HorizontalGaussianBlur horizontalGaussianBlur;
	VerticalGaussianBlur verticalGaussianBlur;
	CombineFilters combineFilters;
	MultiSample multiSample;
	
	public Camera camera;
	
	public Terrain terrain;
	Shader terrainDebugShader;
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.init();
		main.loop();
		terminate();
		
	}	
	
	
	private void init() {
		//OpenGL init
		initOpenGL();
		
		//Game Init
			//Models
			Model quadModel = new Model("src/OpenGL/Objects/Models/quad.obj", VAO.OBJECT_VBO);
			Model barrelModel = new Model("src/OpenGL/Objects/Models/barrel.obj", VAO.OBJECT_VBO);
			//Model boxModel = new Model("src/OpenGL/Objects/Models/crate.obj");
			//Shaders
			Shader objShader = new Shader("src/OpenGL/Objects/Shaders/VertexShader.txt", null, null, null, "src/OpenGL/Objects/Shaders/FragmentShader.txt", Shader.OBJECT_SHADER);
			//Textures
			Texture barrelTexture = new Texture("src/OpenGL/Objects/Textures/barrel.png",
					"src/OpenGL/Objects/Textures/barrelNormal.png",
					null,
					null,
					null);
			barrelTexture.reflectivity = 1.2f;
			 
			Texture crateTexture = new Texture("src/OpenGL/Objects/Textures/crate.png",
					"src/OpenGL/Objects/Textures/crateNormal.png", null, null, null);
			crateTexture.reflectivity = 0.8f;
			crateTexture.shineDamper = 5.2f;
			
			Texture mudTexture = new Texture("src/OpenGL/Objects/Textures/mud.png", null, null, null, null);
			Texture plantAtlas = new Texture("src/OpenGL/Objects/Textures/fern.png", null, null, null, null);
			plantAtlas.setAtlasRows(2);
			
			
			//Cameras
			camera = new Camera(60f, (float)(WIDTH/HEIGHT), 1.0f, 1000.0f, objShader.getID());
			camera.pos.x = 0.0f;
			camera.pos.y = 10.0f;
			camera.pos.z = 0.0f;
			//camera.vel.z = 0.5f;
			Entities.addCamera(camera);
			Entities.setActiveCamera(camera.index);
			
			
			//Lights
			Light light1 = new Light(barrelModel.vao, barrelTexture, 0, objShader.getID());
			light1.pos.y = 200.0f;
			light1.pos.x = -100.0f;
			light1.pos.z = 50.0f;
			//light1.vel.y = 2.0f;
			//light1.lightColor.x = 0.6f;
			//light1.lightColor.y = 0.6f;
			//light1.lightColor.z = 1.0f;
			light1.attenuationRadius = 3000f;
			Entities.addLight(light1);
			
			//Objects
			Object object1 = new Object(quadModel.vao, barrelTexture, 0, objShader.getID());
			object1.pos.x = 1f;
			object1.pos.y = 8.0f;
			object1.pos.z = -128f;
			Entities.addObject(object1);
			
			Object object2 = new Object(barrelModel.vao, crateTexture, 0, objShader.getID());
			object2.pos.x = 1f;
			object2.pos.y = 8.0f;
			object2.pos.z = -60f;
			//object2.scale.x = .07f;
			//object2.scale.y = .07f;
			//object2.scale.z = .07f;
			Entities.addObject(object2);
			
			
			//Text
			Font font = new Font("src/OpenGL/Font/Fonts/Courier New.fnt", 4f, 80.0f, -25.0f, 10.0f, Font.LEFT_ALIGNED);
			Model textVAO = new Model(font.info.verticies, font.info.texCoords, VAO.TEXT2D_VBO);
			Texture textTexture = new Texture("src/OpenGL/Font/Fonts/Courier New.png", null, null, null, null);
			Shader textShader = new Shader("src/OpenGL/Objects/Shaders/VertexShader_Text2D.txt", null, null, null, "src/OpenGL/Objects/Shaders/FragmentShader_Text2D.txt", Shader.TEXT2D_SHADER);
			console = new Text2D(textVAO.vao, textTexture, font, textShader.getID());
			Entities.addText2D(console);
			
			Font font2 = new Font("src/OpenGL/Font/Fonts/Courier New.fnt", 4f, 80.0f, -25.0f, 10.0f, Font.FULL_CENTER_ALIGNED);
			Model buttonVAO = new Model(font2.info.verticies, font2.info.texCoords, VAO.TEXT2D_VBO);
			Text2D buttonText = new Text2D(buttonVAO.vao, textTexture, font2, textShader.getID());
			Entities.addText2D(buttonText);
			
			//Buttons
			Button button = new Button("This Button", buttonText);
			Entities.addButton(button);
			Button button2 = new Button("That Button", buttonText);
			button2.pos.x = 600;
			Entities.addButton(button2);
			
			//SkyBox
			float SIZE = 10;
			
			float[] VERTICES = {        
			    -SIZE,  SIZE, -SIZE,
			    -SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,
			     SIZE,  SIZE, -SIZE,
			    -SIZE,  SIZE, -SIZE,

			    -SIZE, -SIZE,  SIZE,
			    -SIZE, -SIZE, -SIZE,
			    -SIZE,  SIZE, -SIZE,
			    -SIZE,  SIZE, -SIZE,
			    -SIZE,  SIZE,  SIZE,
			    -SIZE, -SIZE,  SIZE,

			     SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,

			    -SIZE, -SIZE,  SIZE,
			    -SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE, -SIZE,  SIZE,
			    -SIZE, -SIZE,  SIZE,

			    -SIZE,  SIZE, -SIZE,
			     SIZE,  SIZE, -SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			    -SIZE,  SIZE,  SIZE,
			    -SIZE,  SIZE, -SIZE,

			    -SIZE, -SIZE, -SIZE,
			    -SIZE, -SIZE,  SIZE,
			     SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,
			    -SIZE, -SIZE,  SIZE,
			     SIZE, -SIZE,  SIZE
			};
			Model skyBoxModel = new Model(VERTICES, 3, VAO.SKYBOX_VBO);
			//Model skyBoxModel = new Model("src/OpenGL/Objects/Models/cube.obj");
			Shader skyBoxShader = new Shader("src/OpenGL/Objects/Shaders/VertexShader_SkyBox.txt", null, null, null,"src/OpenGL/Objects/Shaders/FragmentShader_SkyBox.txt", Shader.OBJECT_SHADER);
			CubeTexture skyBoxTexture = new CubeTexture("src/OpenGL/Objects/Textures/blueSky/right.png"
														, "src/OpenGL/Objects/Textures/blueSky/left.png"
														, "src/OpenGL/Objects/Textures/blueSky/top.png"
														, "src/OpenGL/Objects/Textures/blueSky/bottom.png"
														, "src/OpenGL/Objects/Textures/blueSky/back.png"
														, "src/OpenGL/Objects/Textures/blueSky/front.png");
			SkyBox skybox = new SkyBox(skyBoxModel.vao, skyBoxTexture, skyBoxShader.getID());
			Entities.addSkyBox(skybox);
			
			
			//Terrain
			
			Texture terrainTexture = new Texture(
					"src/OpenGL/Objects/Textures/Terrain/Organic/Organic_Texture.png",
					"src/OpenGL/Objects/Textures/Terrain/Organic/Organic_Normal.png",
					"src/OpenGL/Objects/Textures/Terrain/Organic/Organic_Displacement.png",
					"src/OpenGL/Objects/Textures/Terrain/Organic/Organic_Specular.png",
					null);
			
			terrainTexture.shineDamper = 15.0f;
			terrainTexture.reflectivity = 5.0f;
			//terrainTexture.ambient = 1.0f;
			Model terrainModel = new Model("src/OpenGL/Objects/Models/flat.obj", VAO.TERRAIN_VBO);
			Shader terrainShader = new Shader("src/OpenGL/Objects/Shaders/VertexShader_Terrain.txt", 
					"src/OpenGL/Objects/Shaders/TessControlShader_Terrain.txt", 
					"src/OpenGL/Objects/Shaders/TessEvalShader_Terrain.txt", 
					null, 
					"src/OpenGL/Objects/Shaders/FragmentShader_Terrain.txt", Shader.TERRAIN_SHADER);
			
			terrainDebugShader = new Shader("src/OpenGL/Objects/Shaders/VertexShader_TerrainDebug.txt", 
					null, 
					null, 
					"src/OpenGL/Objects/Shaders/GeometryShader_TerrainDebug.txt", 
					"src/OpenGL/Objects/Shaders/FragmentShader_TerrainDebug.txt", Shader.TERRAIN_SHADER);
			
			terrain = new Terrain(terrainModel.vao, terrainTexture, terrainShader.getID());
			Entities.addTerrain(terrain);
			
			
			//FrameBufferObjects
				//FBO Model
				Model sceneModel = new Model("src/OpenGL/Objects/Models/quad.obj", VAO.OBJECT_VBO);
				
				//ContrastFilter
				Shader contrastFilterShader = new Shader("src/OpenGL/FBO/ContrastFilter/VertexShader_Contrast.txt", null, null, null, "src/OpenGL/FBO/ContrastFilter/FragmentShader_Contrast.txt", Shader.OBJECT_SHADER);
				contrastFilter = new ContrastFilter((float)OpenGL.WIDTH, (float)OpenGL.HEIGHT, sceneModel.vao, null, contrastFilterShader.getID());
				
				//BrightnessFilter
				Shader brightnessFilterShader = new Shader("src/OpenGL/FBO/BrightnessFilter/VertexShader_BrightnessFilter.txt", null, null, null, "src/OpenGL/FBO/BrightnessFilter/FragmentShader_BrightnessFilter.txt", Shader.OBJECT_SHADER);
				brightnessFilter = new BrightnessFilter((float)OpenGL.WIDTH, (float)OpenGL.HEIGHT, sceneModel.vao, null, brightnessFilterShader.getID());
				
				//GaussianBlur
				Shader horizontalGaussianBlurShader = new Shader("src/OpenGL/FBO/HorizontalGaussianBlur/VertexShader_HorizontalGaussianBlur.txt", null, null, null, "src/OpenGL/FBO/HorizontalGaussianBlur/FragmentShader_HorizontalGaussianBlur.txt", Shader.OBJECT_SHADER);
				horizontalGaussianBlur = new HorizontalGaussianBlur((float)OpenGL.WIDTH, (float)OpenGL.HEIGHT, sceneModel.vao, null, horizontalGaussianBlurShader.getID());
				Shader verticalGaussianBlurShader = new Shader("src/OpenGL/FBO/VerticalGaussianBlur/VertexShader_VerticalGaussianBlur.txt", null, null, null, "src/OpenGL/FBO/VerticalGaussianBlur/FragmentShader_VerticalGaussianBlur.txt", Shader.OBJECT_SHADER);
				verticalGaussianBlur = new VerticalGaussianBlur((float)OpenGL.WIDTH, (float)OpenGL.HEIGHT, sceneModel.vao, null, verticalGaussianBlurShader.getID());

				//CombineFilters
				Shader_CombineFilter combineFiltersShader = new Shader_CombineFilter("src/OpenGL/FBO/CombineFilters/VertexShader_CombineFilters.txt", "src/OpenGL/FBO/CombineFilters/FragmentShader_CombineFilters.txt");
				combineFilters = new CombineFilters((float)OpenGL.WIDTH, (float)OpenGL.HEIGHT, sceneModel.vao, null, combineFiltersShader.getID());
			
				//MultiSample
				Shader MultiSampleShader = new Shader("src/OpenGL/FBO/MultiSample/VertexShader_MultiSample.txt", null, null, null, "src/OpenGL/FBO/MultiSample/FragmentShader_MultiSample.txt", Shader.OBJECT_SHADER);
				multiSample = new MultiSample((float)OpenGL.WIDTH, (float)OpenGL.HEIGHT, 8, sceneModel.vao, MultiSampleShader.getID());
			
				
		//Input init
		setCursorPosCallback((window, posX, posY) -> {
			
			if(editorMode == false) {
				//MouseMethod
				camera.mouseInput(posX, posY);
			}else {
				button.mouseInput(posX, posY);
				button2.mouseInput(posX, posY);
			}
				
		});
		setKeyCallback((window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ){
				running = false;
			} else if( key == GLFW_KEY_F1 && action == GLFW_RELEASE ) {
				if(editorMode == true) {
					editorMode = false;
					disableCursor();
					setCursorPos(camera.mouseX, camera.mouseY);
				} else {
					editorMode = true;
					enableCursor();
				}
			}
			
			if(key == GLFW_KEY_F2 && action == GLFW_RELEASE ) {
				if(wireframe == false) {
					OpenGL.wireFrame(true);
					wireframe = true;
				}else {
					OpenGL.wireFrame(false);
					wireframe = false;
				}
			}
			
			if(key == GLFW_KEY_F3 && action == GLFW_RELEASE ) {
				if(debugMode == false) {
					debugMode = true;
				}else {
					debugMode = false;
				}
			}
			
			if(key == GLFW_KEY_F5 && action == GLFW_PRESS ) terrain.testMinus = true;
			if(key == GLFW_KEY_F5 && action == GLFW_RELEASE ) terrain.testMinus = false;
			if(key == GLFW_KEY_F6 && action == GLFW_PRESS ) terrain.testPlus = true;
			if(key == GLFW_KEY_F6 && action == GLFW_RELEASE ) terrain.testPlus = false;
			
			//KeyboardMethod
			camera.keyboardInput(key, action);
		});
		
		setCursorPos(0, 0);
	}


	protected void update(double time, double dt) {
		Entities.updateSkyBox(time, dt);
		Entities.updateCameras(time, dt);
		Entities.updateLights(time, dt);
		Entities.updateObjects(time, dt);
		Entities.updateTerrains(time, dt);
		Entities.updateButtons(time, dt);
		Entities.updateText2D(time, dt);
		
		console.out("FPS: "+FPS+ " UPS: "+UPDATES, 0, 0);
	}

	protected void render(double alpha) {
		//************************RENDER***************************
		//OpenGL.bindFrameBuffer(0);
		multiSample.frameBuffer.bindFrameBuffer();
		clearBuffer();
			Entities.renderCameras(alpha);
			Entities.renderSkyBox(alpha);
			Entities.renderLights(alpha);
			Entities.renderObjects(alpha);
			Entities.renderTerrains(alpha);
			Entities.renderButtons(alpha);
			Entities.renderText2D(alpha);
			
			if(debugMode == true) {
				int[] savedIDObjects = new int[Entities.objects.size()];
				for(int i=0; i < Entities.objects.size(); i++) {
					Entities.objects.get(i).debugMode = true;
					savedIDObjects[i] = Entities.objects.get(i).shaderID;
					Entities.objects.get(i).shaderID = terrainDebugShader.getID();
				}
				Entities.renderObjects(alpha);
				for(int i=0; i < Entities.objects.size(); i++) {
					Entities.objects.get(i).shaderID = savedIDObjects[i];
					Entities.objects.get(i).debugMode = false;
				}
				
				
				int[] savedIDTerrain = new int[Entities.terrains.size()];
				for(int i=0; i < Entities.terrains.size(); i++) {
					Entities.terrains.get(i).debugMode = true;
					savedIDTerrain[i] = Entities.terrains.get(i).shaderID;
					Entities.terrains.get(i).shaderID = terrainDebugShader.getID();
				}
				Entities.renderTerrains(alpha);
				
				for(int i=0; i < Entities.terrains.size(); i++) {
					Entities.terrains.get(i).shaderID = savedIDTerrain[i];
					Entities.terrains.get(i).debugMode = false;
				}
			}
		
		multiSample.renderMultiSampleToScreen();
		/*
		contrastFilter.frameBuffer.bindFrameBuffer(brightnessFilter.frameBuffer.getFBO(), (int)brightnessFilter.width, (int)brightnessFilter.height);
		clearBuffer();
			
			contrastFilter.render(alpha, contrastFilter.frameBuffer.getColorTexture());
			
		brightnessFilter.frameBuffer.bindFrameBuffer(horizontalGaussianBlur.frameBuffer.getFBO(), (int)horizontalGaussianBlur.width, (int)horizontalGaussianBlur.height);
		clearBuffer();
			
			brightnessFilter.render(alpha, brightnessFilter.frameBuffer.getColorTexture());
			
		horizontalGaussianBlur.frameBuffer.bindFrameBuffer(verticalGaussianBlur.frameBuffer.getFBO(), (int)verticalGaussianBlur.width, (int)verticalGaussianBlur.height);
		clearBuffer();
		
			horizontalGaussianBlur.render(alpha, horizontalGaussianBlur.frameBuffer.getColorTexture());
			
		verticalGaussianBlur.frameBuffer.bindFrameBuffer(combineFilters.frameBuffer.getFBO(), (int)combineFilters.width, (int)combineFilters.height);
		clearBuffer();
		
			verticalGaussianBlur.render(alpha, verticalGaussianBlur.frameBuffer.getColorTexture());
			
		combineFilters.frameBuffer.bindScreenFBO();
		
			combineFilters.render(alpha, contrastFilter.frameBuffer.getColorTexture(), combineFilters.frameBuffer.getColorTexture());
		*/
		//**********************************************************
		
		
		
		
		swapBuffers();
		
		/*
		 * Input
		 */
		pollInput();
	}

}
