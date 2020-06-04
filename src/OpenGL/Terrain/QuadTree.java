package OpenGL.Terrain;

public class QuadTree {
	public static final int MAX_LEVEL = 4;

	public Node head;
	int numNodes;
	
	public QuadTree(float width, float length, int levels) {
		head = new Node(0, width, length);
		numNodes = 1;
		addLevels(head, levels);
	}
	
	public void addLevels(Node node, int levels) {
		
		if(levels > 0) {
			node.split();
			numNodes += 4;

			addLevels(node.NW, levels-1);
			addLevels(node.NE, levels-1);
			addLevels(node.SW, levels-1);
			addLevels(node.SE, levels-1);
		}
	}
}








class Node {
	Node NW, NE, SW, SE;
	int level;
	float width, length;
	
	public Node(int level, float width, float length) {
		this.level = level;
		this.width = width;
		this.length = length;
	}
	
	public void split() {
		NW = new Node(level + 1, width/2f, length/2f);
		NE = new Node(level + 1, width/2f, length/2f);
		SW = new Node(level + 1, width/2f, length/2f);
		SE = new Node(level + 1, width/2f, length/2f);
	}
}