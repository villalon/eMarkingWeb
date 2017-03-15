/**
 * 
 */
package cl.uai.client.marks;

/**
 * @author Jorge
 *
 */
public class Point {
	private int x;
	private int y;
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Point(int _x, int _y) {
		this.x = _x;
		this.y = _y;
	}
	
	@Override
	public String toString() {
		return this.x + ", " + this.y;
	}
}

