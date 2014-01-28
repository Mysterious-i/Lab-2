/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;
public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	
	public static ColorSensor colorSensor;
	public static int sensorValue;
	
	
	
	//variables
	private final int LIGHT_THRESHOLD = 400;
	private final static double OFFSET = 10.0;
	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor cS) {
		this.odometer = odometer;
		this.colorSensor=cS;
		

	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			LCD.drawInt(colorSensor.getRawLightValue(), 3, 4);
			
				 if (!SquareDriver.isTurning) {
	
	                 //if the light value read by the color sensor is below the light threshold
	                 if (colorSensor.getRawLightValue() < LIGHT_THRESHOLD) {
	                      	Sound.beep();
	                        
	                         // check if the robot is going up-down , correct X value else correy Y
	                         if (headingUpDown(odometer.getTheta())) {
	                                 odometer.setX(getGridLine(odometer.getX()));
	
	                         } else {
	                                 odometer.setY(getGridLine(odometer.getY()));
	                         }
	                 }
				
	
				// this ensure the odometry correction occurs only once every period
				correctionEnd = System.currentTimeMillis();
				if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
					try {
						Thread.sleep(CORRECTION_PERIOD
								- (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
			 }
		}
	}
	
	
	//method which determines whether the robot is travelling "up/down" or "left/right" 
    private boolean headingUpDown(double theta) {
            long value = Math.round(theta / ((Math.PI) / 2));
            return ((value % 2) == 1);
    }

    // depending on the heading of the robot find the closest grid line it just crossed.
    public static double getGridLine(double coordinate) {
            return Math.round(((coordinate - OFFSET - 15) / 30)) * 30 + 15 + OFFSET;
    }
}