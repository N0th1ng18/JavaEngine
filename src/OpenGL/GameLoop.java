package OpenGL;

abstract public class GameLoop{
	
	//Loop Variables
	protected int FPS;
	protected int UPDATES;
	double updatesPerSecond = 60.0;
	public static boolean running = true;
	boolean showFPS = true;
	
	
	/*
	 * Abstract Methods
	 */
	protected abstract void update(double time, double dt);
	protected abstract void render(double alpha);
	
	/*
	 * Loop
	 */
	protected void loop() {

		/**********************************************/
		double time = 0.0;
		double dt = 1.0/updatesPerSecond;
		
		double accumulator = 0.0;
		double alpha = accumulator / dt;
		
		double newFrameTime = getNanoTimeInSeconds();
		double oldFrameTime = getNanoTimeInSeconds();
		double frameTime;
		/***********************************************/
		
		/*FPS*/
		double fps = getNanoTimeInSeconds();
		int renderCounter = 0;
		int updateCounter = 0;
		/***************************************/
		
		while(running) {
			newFrameTime = getNanoTimeInSeconds();
			frameTime = newFrameTime - oldFrameTime;
			oldFrameTime = newFrameTime;
			
			/*
			 * Spiral of Death
			 */
				if(frameTime > 2.5) {
					frameTime = 2.5;
				}
			//*****************************
			
			accumulator += frameTime;/*Adding length of frame*/
			
			while(accumulator >= dt) {
				
				update(time, dt);
				
				if(showFPS == true) {
					updateCounter++;
				}
				
				time += dt;
				accumulator -= dt;/*subtracting dt intervals*/
			}
			
			alpha = accumulator / dt;
			
			render(alpha);
			
			if(showFPS == true) {
				renderCounter++;
			}
			
			if(showFPS == true && (getNanoTimeInSeconds() - fps) >= 1.0) {
				FPS = renderCounter;
				UPDATES = updateCounter;
				renderCounter = 0;
				updateCounter = 0;
				fps = getNanoTimeInSeconds();
			}
		}
	}
	
	
	/*
	 * Other Methods
	 */
	private double getNanoTimeInSeconds() {
		return System.nanoTime()/(1000000000.0);
	}
}
