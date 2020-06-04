package OpenGL.DataStructures;

import java.nio.FloatBuffer;


public class MatrixMath {
	
	public static void main(String[] args) {
		MatrixMath m = new MatrixMath();
		
		float[][] matrix = new float[4][4];
		
		matrix[0][0] = 1.0f; matrix[0][1] = 2.0f; matrix[0][2] = 0.0f; matrix[0][3] = 0.0f; 
		matrix[1][0] = 0.0f; matrix[1][1] = 2.0f; matrix[1][2] = 0.0f; matrix[1][3] = 0.0f; 
		matrix[2][0] = 0.0f; matrix[2][1] = 0.0f; matrix[2][2] = 1.0f; matrix[2][3] = 0.0f; 
		matrix[3][0] = 0.0f; matrix[3][1] = 0.0f; matrix[3][2] = 0.0f; matrix[3][3] = 1.0f;
		
		matrix = inverse(matrix);
		
		for(int i=0; i < 4; i++) {
				System.out.println(matrix[i][0] +" "+matrix[i][1] +" "+matrix[i][2] +" "+matrix[i][3]);
		}
		
	}
	
	public static float[][] multiply(float[][] a, float[][] b) {
		int rowsInA = a.length;
		int columnsInA = a[0].length; // same as rows in B
		int columnsInB = b[0].length;
		float[][] c = new float[rowsInA][columnsInB];
		for (int i = 0; i < rowsInA; i++) {
			for (int j = 0; j < columnsInB; j++) {
				for (int k = 0; k < columnsInA; k++) {
					c[i][j] = c[i][j] + a[i][k] * b[k][j];
				}
			}
		}
		return c;
	}
	  
	public static float[][] setIdentity(float[][] a){
		
		a[0][0] = 1.0f;a[0][1] = 0.0f;a[0][2] = 0.0f;a[0][3] = 0.0f;
		a[1][0] = 0.0f;a[1][1] = 1.0f;a[1][2] = 0.0f;a[1][3] = 0.0f;
		a[2][0] = 0.0f;a[2][1] = 0.0f;a[2][2] = 1.0f;a[2][3] = 0.0f;
		a[3][0] = 0.0f;a[3][1] = 0.0f;a[3][2] = 0.0f;a[3][3] = 1.0f;
		
		
		return a;
	}
	
	
	public static FloatBuffer floatArraytoFloatBuffer(float[][] floatArray, FloatBuffer floatBuffer){
			
		floatBuffer.put(floatArray[0][0]).put(floatArray[0][1]).put(floatArray[0][2]).put(floatArray[0][3]);
		floatBuffer.put(floatArray[1][0]).put(floatArray[1][1]).put(floatArray[1][2]).put(floatArray[1][3]);
		floatBuffer.put(floatArray[2][0]).put(floatArray[2][1]).put(floatArray[2][2]).put(floatArray[2][3]);
		floatBuffer.put(floatArray[3][0]).put(floatArray[3][1]).put(floatArray[3][2]).put(floatArray[3][3]);
		    
		return floatBuffer;
	}
	 
	public static float[][] inverse(float[][] a)
	{
		float s0 = a[0][0] * a[1][1] - a[1][0] * a[0][1];
		float s1 = a[0][0] * a[1][2] - a[1][0] * a[0][2];
		float s2 = a[0][0] * a[1][3] - a[1][0] * a[0][3];
		float s3 = a[0][1] * a[1][2] - a[1][1] * a[0][2];
		float s4 = a[0][1] * a[1][3] - a[1][1] * a[0][3];
		float s5 = a[0][2] * a[1][3] - a[1][2] * a[0][3];

		float c5 = a[2][2] * a[3][3] - a[3][2] * a[2][3];
		float c4 = a[2][1] * a[3][3] - a[3][1] * a[2][3];
		float c3 = a[2][1] * a[3][2] - a[3][1] * a[2][2];
		float c2 = a[2][0] * a[3][3] - a[3][0] * a[2][3];
		float c1 = a[2][0] * a[3][2] - a[3][0] * a[2][2];
		float c0 = a[2][0] * a[3][1] - a[3][0] * a[2][1];

		// Should check for 0 determinant
		float invdet = 1.0f / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);

		float[][] b = new float[4][4];

		b[0][0] = ( a[1][1] * c5 - a[1][2] * c4 + a[1][3] * c3) * invdet;
		b[0][1] = (-a[0][1] * c5 + a[0][2] * c4 - a[0][3] * c3) * invdet;
		b[0][2] = ( a[3][1] * s5 - a[3][2] * s4 + a[3][3] * s3) * invdet;
		b[0][3] = (-a[2][1] * s5 + a[2][2] * s4 - a[2][3] * s3) * invdet;

		b[1][0] = (-a[1][0] * c5 + a[1][2] * c2 - a[1][3] * c1) * invdet;
		b[1][1] = ( a[0][0] * c5 - a[0][2] * c2 + a[0][3] * c1) * invdet;
		b[1][2] = (-a[3][0] * s5 + a[3][2] * s2 - a[3][3] * s1) * invdet;
		b[1][3] = ( a[2][0] * s5 - a[2][2] * s2 + a[2][3] * s1) * invdet;

		b[2][0] = ( a[1][0] * c4 - a[1][1] * c2 + a[1][3] * c0) * invdet;
		b[2][1] = (-a[0][0] * c4 + a[0][1] * c2 - a[0][3] * c0) * invdet;
		b[2][2] = ( a[3][0] * s4 - a[3][1] * s2 + a[3][3] * s0) * invdet;
		b[2][3] = (-a[2][0] * s4 + a[2][1] * s2 - a[2][3] * s0) * invdet;

		b[3][0] = (-a[1][0] * c3 + a[1][1] * c1 - a[1][2] * c0) * invdet;
		b[3][1] = ( a[0][0] * c3 - a[0][1] * c1 + a[0][2] * c0) * invdet;
		b[3][2] = (-a[3][0] * s3 + a[3][1] * s1 - a[3][2] * s0) * invdet;
		b[3][3] = ( a[2][0] * s3 - a[2][1] * s1 + a[2][2] * s0) * invdet;

		return b;
	}
}
