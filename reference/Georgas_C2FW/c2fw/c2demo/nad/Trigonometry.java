package c2demo.nad;

public class Trigonometry{
	
	public static void main(String[] args){
		System.out.println(calculateAngleWorld(9500, 2500));
	}
	
	public static int calculateAngleWorld(int xWorld, int yWorld){
		int worldCenterX = WorldParameters.WORLD_WIDTH / 2;
		int worldCenterY = WorldParameters.WORLD_HEIGHT / 2;
		
		//System.out.println("Calculating angle: " + (xWorld - worldCenterX) + " " + (yWorld - worldCenterY));
		return calculateAngle(xWorld - worldCenterX, yWorld - worldCenterY);
	}
	
	public static int calculateAngle(int xRel, int yRel){
		int q;
		int x = xRel;
		int y = -yRel;
		//System.out.println("Calculating angle: " + (x) + ", " + (y));
		if((x >= 0) && (y >= 0))
		{
			//System.out.println("Case 1");
			double tan = (double)x/(double)y;
			double thetaRadians = Math.atan(tan);
			double degrees = Math.toDegrees(thetaRadians);
			int absDeg = (int)degrees;
			return absDeg;
		}
		else if((x > 0) && (y < 0))
		{
			//System.out.println("Case 2");
			double tan = (double)x/(double)(-y);
			double thetaRadians = Math.atan(tan);
			double degrees = Math.toDegrees(thetaRadians);
			int absDeg = (int)degrees;
			return 180 - absDeg;
		}
		else if((x <= 0) && (y < 0))
		{
			//System.out.println("Case 3");
			double tan = (double)(-x)/(double)(-y);
			double thetaRadians = Math.atan(tan);
			double degrees = Math.toDegrees(thetaRadians);
			int absDeg = (int)degrees;
			return 180 + absDeg;
		}
		else /* if ((x < 0) && (y >= 0)) */
		{
			//System.out.println("Case 4");
			double tan = (double)(-x)/(double)y;
			double thetaRadians = Math.atan(tan);
			double degrees = Math.toDegrees(thetaRadians);
			int absDeg = (int)degrees;
			return 360 - absDeg;
		}
	}
}
