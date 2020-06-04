package OpenGL.DataStructures;

public class Text {
	public String text;
	public Vector2D textPos;
	public String prevText;
	public Vector2D prevTextPos;

	public Text(String text, float x, float y) {
		this.text = text;
		this.textPos = new Vector2D(x, y);	
		prevText = "";
		prevTextPos = new Vector2D(0.0f, 0.0f);	
	}
	
	public void saveCurrentState() {
		prevText = text;
		prevTextPos.x = textPos.x;
		prevTextPos.y = textPos.y;
	}
	
}