package positioningmap;

public abstract class PositioningMapElement {

	private String name;
	private double xmin;
	private double xmax;
	private double ymin;
	private double ymax;
	private MyColor color;

	
	public PositioningMapElement(String name, double xmin, double xmax, double ymin, double ymax, MyColor color) {
		this.name = name;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.color = color;
	}

	public int x() {
		return calcX(this.xmin);
	}

	protected abstract int calcX(double xmin2);
	protected abstract int calcY(double xmax2);
	protected abstract int calcWidht(double d);
	protected abstract int calcHeight(double d);
	
	public int y() {
		return calcY(this.ymin) - height();
	}

	public int width() {
		return calcWidht(this.xmax - this.xmin);
	}

	public int height() {
		return calcHeight(this.ymax - this.ymin);
	}

	public String text() {
		return this.name;
	}

	public MyColor color() {
		return color;
	}

}
class MyColor {
	public MyColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	int r;
	int g;
	int b;
}
