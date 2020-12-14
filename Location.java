package simulationCovid;

public class Location {
	private int x,y;

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	public boolean equals(Location otherL) {
		return (this.x == otherL.x && this.y == otherL.y);
	}
}
