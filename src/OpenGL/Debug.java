package OpenGL;

import java.io.*;

public class Debug {
	
	static PrintStream console = System.out;
	
	public static void printSystemOutToFile(String fileLocation) {
	    // Creating a File object that represents the disk file.
	    PrintStream o;
		try {
			
			o = new PrintStream(new File(fileLocation));

		    // Assign o to output stream
		    System.setOut(o);
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printSystemOutToConsole() {
		
	    // Use stored value for output stream
	    System.setOut(console);
	}
	
}
