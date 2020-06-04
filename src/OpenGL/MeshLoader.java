package OpenGL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import OpenGL.DataStructures.Face;
import OpenGL.DataStructures.Matrix3;
import OpenGL.DataStructures.Mesh;
import OpenGL.DataStructures.MeshStructure;
import OpenGL.DataStructures.Vector2D;
import OpenGL.DataStructures.Vector3D;
import OpenGL.DataStructures.Vertex;

public class MeshLoader {
	
	public static MeshStructure loadMesh(String location) {
		
		ArrayList<Vector3D> verticies = new ArrayList<Vector3D>();
		ArrayList<Vector2D> uvs = new ArrayList<Vector2D>();
		ArrayList<Vector3D> normals = new ArrayList<Vector3D>();
		ArrayList<Integer> vertexIndices = new ArrayList<Integer>();
		ArrayList<Integer> uvIndices = new ArrayList<Integer>();
		ArrayList<Integer> normalIndices = new ArrayList<Integer>();
		
		/************READ FILE************/
		File file = new File(location);
		try{
			Scanner sc = new Scanner(file);
			
			while(sc.hasNext() == true){
				String s = sc.next();
				 
				if(s.equals("v")){
					verticies.add(new Vector3D(sc.nextFloat(),sc.nextFloat(),sc.nextFloat()));
				}
				if(s.equals("vt")){
					uvs.add(new Vector2D( sc.nextFloat(), sc.nextFloat()));
				}
				if(s.equals("vn")){
					normals.add(new Vector3D( sc.nextFloat(), sc.nextFloat(), sc.nextFloat()));
				}
				if(s.equals("f")){
					String[] v0 = sc.next().split("/");
					String[] v1 = sc.next().split("/");
					String[] v2 = sc.next().split("/");
					
					vertexIndices.add(Integer.parseInt(v0[0]));
					vertexIndices.add(Integer.parseInt(v1[0]));
					vertexIndices.add(Integer.parseInt(v2[0]));
					uvIndices.add(Integer.parseInt(v0[1]));
					uvIndices.add(Integer.parseInt(v1[1]));
					uvIndices.add(Integer.parseInt(v2[1]));
					normalIndices.add(Integer.parseInt(v0[2]));
					normalIndices.add(Integer.parseInt(v1[2]));
					normalIndices.add(Integer.parseInt(v2[2]));
				}
				
			}
			
			sc.close();
			
		} catch (IOException e){
			System.err.println("Model file wasn't read properly: "+location);
		}
		/*********************************/
		/*********PROCESS INDICES*********/
			ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
			
			for(int i=0; i < vertexIndices.size(); i++) {
				
				// Get the indices of its attributes
				int vertexIndex = vertexIndices.get(i);
				int uvIndex = uvIndices.get(i);
				int normalIndex = normalIndices.get(i);
				
				// Get the attributes thanks to the index
				vertexList.add(new Vertex(verticies.get(vertexIndex - 1), uvs.get(uvIndex - 1), normals.get(normalIndex - 1)));
			}
		
		/*********************************/	
		/**************Build Mesh*************/
			createTangentSpace(vertexList);
			
			Mesh mesh = indexMesh(vertexList);
			
		/*****BUILD VERTICES, TEXTURES, TANGENTS, BITANGENTS, NORMALS****/
			MeshStructure meshStruct = new MeshStructure();
			
			meshStruct.vertices = new float[mesh.vertexList.size() * 3];
				for(int i=0; i < mesh.vertexList.size(); i++){	
					meshStruct.vertices[0+(i*3)] = mesh.vertexList.get(i).position.x;
					meshStruct.vertices[1+(i*3)] = mesh.vertexList.get(i).position.y;
					meshStruct.vertices[2+(i*3)] = mesh.vertexList.get(i).position.z;
				}
				
			meshStruct.texCoords = new float[mesh.vertexList.size() * 2];
				for(int i=0; i < mesh.vertexList.size(); i++){	
					meshStruct.texCoords[0+(i*2)] = mesh.vertexList.get(i).texture.x;
					meshStruct.texCoords[1+(i*2)] = 1.0f - mesh.vertexList.get(i).texture.y;
				}
				
			meshStruct.normals = new float[mesh.vertexList.size() * 3];
			for(int i=0; i < mesh.vertexList.size(); i++){
				meshStruct.normals[0+(i*3)] = mesh.vertexList.get(i).normal.x;
				meshStruct.normals[1+(i*3)] = mesh.vertexList.get(i).normal.y;
				meshStruct.normals[2+(i*3)] = mesh.vertexList.get(i).normal.z;
			}	
				
			meshStruct.tangents = new float[mesh.vertexList.size() * 3];
				for(int i=0; i < mesh.vertexList.size(); i++){
					meshStruct.tangents[0+(i*3)] = mesh.vertexList.get(i).tangent.x;
					meshStruct.tangents[1+(i*3)] = mesh.vertexList.get(i).tangent.y;
					meshStruct.tangents[2+(i*3)] = mesh.vertexList.get(i).tangent.z;
				}
				
			meshStruct.bitangents = new float[mesh.vertexList.size() * 3];
				for(int i=0; i < mesh.vertexList.size(); i++){
					meshStruct.bitangents[0+(i*3)] = mesh.vertexList.get(i).bitangent.x;
					meshStruct.bitangents[1+(i*3)] = mesh.vertexList.get(i).bitangent.y;
					meshStruct.bitangents[2+(i*3)] = mesh.vertexList.get(i).bitangent.z;
				}
				
			meshStruct.indices = new int[mesh.indices.size()];
				for(int i=0; i < mesh.indices.size(); i++){
					meshStruct.indices[i] = mesh.indices.get(i);
				}
			
		/**************************************************************/
		
		return meshStruct;
	}
	
	
	public static void createTangentSpace(ArrayList<Vertex> vertexList) {
		
		calculateNTB(vertexList);
		
		for(int i=0; i < vertexList.size(); i++) {
			
			// Gram-Schmidt Orthogonalize
			//	tangent = (t - n * dot(n, t)).normalize
	        vertexList.get(i).tangent = Vector3D.sub(vertexList.get(i).tangent , Vector3D.mulScalar(vertexList.get(i).normal, Vector3D.dot(vertexList.get(i).normal, vertexList.get(i).tangent)));
	        vertexList.get(i).tangent.normalize();
	        
	        //Handedness
	        if(Vector3D.dot(Vector3D.cross(vertexList.get(i).normal, vertexList.get(i).tangent), vertexList.get(i).bitangent) < 0.0f) {
	        	vertexList.get(i).tangent = Vector3D.mulScalar(vertexList.get(i).tangent, -1.0f);
	        }
	        
	        
	        
	        
	        if(Math.abs(vertexList.get(i).tangent.x) == 0f && Math.abs(vertexList.get(i).tangent.y) == 0f && Math.abs(vertexList.get(i).tangent.z) == 0f) {
	        	System.out.println("shit");
	        }
		}
		
		
	}
	
	public static void calculateNTB(ArrayList<Vertex> vertexList) {
		/* 
		 *    A ____ C		 
		 *     |   /		
		 *     |  /			
		 *     | /			
		 *     |/			
		 *     B
		 */
		
		for(int i=0; i < vertexList.size(); i+=3) {
		
			Vertex vertex0 = vertexList.get(i+0);
			Vertex vertex1 = vertexList.get(i+1);
			Vertex vertex2 = vertexList.get(i+2);
			
			//Positions
			Vector3D v0 = vertex0.position;
			Vector3D v1 = vertex1.position;
			Vector3D v2 = vertex2.position;
			
			//UVs
			Vector2D uv0 = vertex0.texture;
			Vector2D uv1 = vertex1.texture;
			Vector2D uv2 = vertex2.texture;
			
			//Position Delta
			Vector3D deltaPos1 = Vector3D.sub(v1, v0);
			Vector3D deltaPos2 = Vector3D.sub(v2, v0);
			
			//UV delta
			Vector2D deltaUV1 = Vector2D.sub(uv1, uv0);
			Vector2D deltaUV2 = Vector2D.sub(uv2, uv0);
			
			//Fomula
			float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	        Vector3D tangent = Vector3D.mulScalar(Vector3D.sub(Vector3D.mulScalar(deltaPos1, deltaUV2.y), Vector3D.mulScalar(deltaPos2, deltaUV1.y)), r);
	        Vector3D bitangent = Vector3D.mulScalar(Vector3D.sub(Vector3D.mulScalar(deltaPos2, deltaUV1.x), Vector3D.mulScalar(deltaPos1, deltaUV2.x)), r);
	        
	        //Each Vertex of triangle get the same tangent -> will be merged later in the indexer
	        vertex0.tangent = tangent;
	        vertex1.tangent = tangent;
	        vertex2.tangent = tangent;
	        
	        //Each Vertex of triangle get the same bitangent -> will be merged later in the indexer
	        vertex0.bitangent = bitangent;
	        vertex1.bitangent = bitangent;
	        vertex2.bitangent = bitangent;
	        
		}
        
	}
	
	
	public static Mesh indexMesh(ArrayList<Vertex> in_VertexList) {
		// Input	-> in_VertexList
		
		// Output	-> mesh.vertexList
		// Output	-> mesh.indices
		
		Mesh mesh = new Mesh();
		
		for(int i=0; i < in_VertexList.size(); i++) {
			//Search for similar vertex in out_vertexList
			int index = getSimilarVertexIndex(in_VertexList.get(i), mesh.vertexList);
			
			if(index >= 0) {// A similar vertex was found
				mesh.indices.add(index);
				
				//Average Tangents and Bitangents
				mesh.vertexList.get(index).tangent.x += in_VertexList.get(i).tangent.x;
				mesh.vertexList.get(index).tangent.y += in_VertexList.get(i).tangent.y;
				mesh.vertexList.get(index).tangent.z += in_VertexList.get(i).tangent.z;
				mesh.vertexList.get(index).bitangent.x += in_VertexList.get(i).bitangent.x;
				mesh.vertexList.get(index).bitangent.y += in_VertexList.get(i).bitangent.y;
				mesh.vertexList.get(index).bitangent.z += in_VertexList.get(i).bitangent.z;
			}else {// if not, add new vertex to output data
				
				//Add new Vertex with index to output
				mesh.vertexList.add(in_VertexList.get(i));
				mesh.indices.add(mesh.vertexList.size() -1);
			}
			
		}
		
		return mesh;
	}
	
	public static int getSimilarVertexIndex(Vertex in_Vertex, ArrayList<Vertex> out_VertexList) {
		int index = -1;
		
		for(int i=0; i < out_VertexList.size(); i++) {
			if(
					is_near(in_Vertex.position.x, out_VertexList.get(i).position.x) && 
					is_near(in_Vertex.position.y, out_VertexList.get(i).position.y) && 
					is_near(in_Vertex.position.z, out_VertexList.get(i).position.z) && 
					is_near(in_Vertex.texture.x, out_VertexList.get(i).texture.x) && 
					is_near(in_Vertex.texture.y, out_VertexList.get(i).texture.y) && 
					is_near(in_Vertex.normal.x, out_VertexList.get(i).normal.x) && 
					is_near(in_Vertex.normal.y, out_VertexList.get(i).normal.y) && 
					is_near(in_Vertex.normal.z, out_VertexList.get(i).normal.z)
			) {
				
				index = i;
			}
		}
		
		return index;
	}
	
	public static boolean is_near(float v1, float v2) {
		return Math.abs(v1-v2) < 0.01f;
	}
	

}
