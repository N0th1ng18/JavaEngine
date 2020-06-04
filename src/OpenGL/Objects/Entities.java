package OpenGL.Objects;

import java.util.ArrayList;

import OpenGL.DataStructures.Vector3D;
import OpenGL.Terrain.Terrain;
import UserInterface.Button;

public class Entities {
	
	public static int activeCameraIndex;
	public static ArrayList<Camera> cameras = new ArrayList<Camera>();
	public static ArrayList<Object> objects = new ArrayList<Object>();
	public static ArrayList<Terrain> terrains = new ArrayList<Terrain>();
	public static ArrayList<Light> lights = new ArrayList<Light>();
	public static ArrayList<SkyBox> skyBoxes = new ArrayList<SkyBox>();
	public static ArrayList<Text2D> text2Ds = new ArrayList<Text2D>();
	public static ArrayList<Button> buttons = new ArrayList<Button>();
	
	//Global Entity Settings
	public static Vector3D fogColor = new Vector3D(.6f, .6f, .6f);//.8, .8, .8
	
	
	/*
	 **************************************************************************
	 * Cameras
	 ************************************************************************** 
	 */
	public static void setActiveCamera(int index) {
		activeCameraIndex = index;
	}
	public static void addCamera(Camera c) {
		//Adds to end
		c.index = cameras.size();
		cameras.add(c);
	}
	
	public static void deleteCamera(int index) {
		for(int i = index+1; i < cameras.size(); i++) {//Shifts Left
			cameras.get(i).index--;
		}
		cameras.remove(index);
	}
	
	public static void updateCameras(double time, double dt) {
		for(int i = 0; i < cameras.size(); i++) {
			cameras.get(i).update(time, dt);
		}
	}
	
	public static void renderCameras(double alpha) {
		for(int i = 0; i < cameras.size(); i++) {
			cameras.get(i).render(alpha);
		}
	}
	
	/*
	 **************************************************************************
	 * Objects
	 ************************************************************************** 
	 */
	public static void addObject(Object o) {
		//Adds to end
		o.index = objects.size();
		objects.add(o);
	}
	
	public static void deleteObject(int index) {
		for(int i = index+1; i < objects.size(); i++) {//Shifts Left
			objects.get(i).index--;
		}
		objects.remove(index);
	}
	
	public static void updateObjects(double time, double dt) {
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).update(time, dt);
		}
	}
	
	public static void renderObjects(double alpha) {
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).render(alpha);
		}
	}
	
	/*
	 **************************************************************************
	 * Terrains
	 ************************************************************************** 
	 */
	public static void addTerrain(Terrain t) {
		//Adds to end
		t.index = terrains.size();
		terrains.add(t);
	}
	
	public static void deleteTerrain(int index) {
		for(int i = index+1; i < terrains.size(); i++) {//Shifts Left
			terrains.get(i).index--;
		}
		terrains.remove(index);
	}
	
	public static void updateTerrains(double time, double dt) {
		for(int i = 0; i < terrains.size(); i++) {
			terrains.get(i).update(time, dt);
		}
	}
	
	public static void renderTerrains(double alpha) {
		for(int i = 0; i < terrains.size(); i++) {
			terrains.get(i).render(alpha);
		}
	}
	
	/*
	 **************************************************************************
	 * Lights
	 ************************************************************************** 
	 */
	
	public static void addLight(Light l) {
		//Adds to end
		l.index = lights.size();
		lights.add(l);
	}
	
	public static void deleteLight(int index) {
		for(int i = index+1; i < lights.size(); i++) {//Shifts Left
			lights.get(i).index--;
		}
		lights.remove(index);
	}
	
	public static void updateLights(double time, double dt) {
		for(int i = 0; i < lights.size(); i++) {
			lights.get(i).update(time, dt);
		}
	}
	
	public static void renderLights(double alpha) {
		for(int i = 0; i < lights.size(); i++) {
			lights.get(i).render(alpha);
		}
	}
	
	
	
	
	
	
	
	
	
	
	/*
	 **************************************************************************
	 * SkyBoxes
	 ************************************************************************** 
	 */
	public static void addSkyBox(SkyBox s) {
		//Adds to end
		s.index = skyBoxes.size();
		skyBoxes.add(s);
	}
	
	public static void deleteSkyBox(int index) {
		for(int i = index+1; i < skyBoxes.size(); i++) {//Shifts Left
			skyBoxes.get(i).index--;
		}
		skyBoxes.remove(index);
	}
	
	public static void updateSkyBox(double time, double dt) {
		for(int i = 0; i < skyBoxes.size(); i++) {
			skyBoxes.get(i).update(time, dt);
		}
	}
	
	public static void renderSkyBox(double alpha) {
		for(int i = 0; i < skyBoxes.size(); i++) {
			skyBoxes.get(i).render(alpha);
		}
	}
	
	/*
	 **************************************************************************
	 * Text2D
	 ************************************************************************** 
	 */
	public static void addText2D(Text2D t) {
		//Adds to end
		t.index = skyBoxes.size();
		text2Ds.add(t);
	}
	
	public static void deleteText2D(int index) {
		for(int i = index+1; i < text2Ds.size(); i++) {//Shifts Left
			text2Ds.get(i).index--;
		}
		text2Ds.remove(index);
	}
	
	public static void updateText2D(double time, double dt) {
		for(int i = 0; i < text2Ds.size(); i++) {
			text2Ds.get(i).update(time, dt);
		}
	}
	
	public static void renderText2D(double alpha) {
		for(int i = 0; i < text2Ds.size(); i++) {
			text2Ds.get(i).render(alpha);
		}
	}
	
	/*
	 **************************************************************************
	 * Buttons
	 ************************************************************************** 
	 */
	public static void addButton(Button b) {
		//Adds to end
		b.index = buttons.size();
		buttons.add(b);
	}
	
	public static void deleteButtons(int index) {
		for(int i = index+1; i < buttons.size(); i++) {//Shifts Left
			buttons.get(i).index--;
		}
		buttons.remove(index);
	}
	
	public static void updateButtons(double time, double dt) {
		for(int i = 0; i < buttons.size(); i++) {
			buttons.get(i).update(time, dt);
		}
	}
	
	public static void renderButtons(double alpha) {
		for(int i = 0; i < buttons.size(); i++) {
			buttons.get(i).render(alpha);
		}
	}
}
