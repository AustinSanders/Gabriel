package c2demo.nad;

public class LocatedDrawableThing{

	private String id;
	private int x;
	private int y;
	private double scale;
	private DrawableThing d;

	public LocatedDrawableThing(String id, int x, int y, DrawableThing d){
		this(id, x, y, 1.0d, d);
	}
	
	public LocatedDrawableThing(String id, int x, int y, double scale, DrawableThing d){
		this.id = id;
		this.x = x;
		this.y = y;
		this.d = d;
		this.scale = scale;
	}
	
	public LocatedDrawableThing duplicate(){
		return new LocatedDrawableThing(id, getX(), getY(), getScale(), getDrawableThing());
	}
	
	public String getId(){
		return id;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public double getScale(){
		return scale;
	}
	
	public DrawableThing getDrawableThing(){
		return d;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setScale(double scale){
		this.scale = scale;
	}
	
	public void setDrawableThing(DrawableThing d){
		this.d = d;
	}

}
