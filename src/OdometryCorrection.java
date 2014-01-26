/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;
public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private LightSensor lightSensor;
	
	//variables
	private final int LIGHT_THRESHOLD = 45;
	private final static double OFFSET = 10.0;
	// constructor
	public OdometryCorrection(Odometer odometer, LightSensor lightSensor) {
		this.odometer = odometer;
		this.lightSensor=lightSensor;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			
				 if (!SquareDriver.isTurning) {
	
	                 // First we read the light sensor for a value below our
	                 // threshold, telling us that we're crossing a line
	                 if (lightSensor.readValue() < LIGHT_THRESHOLD) {
	                         Sound.beep();
	                         
	                         // If the heading is "north-south" we correct in y. Otherwise, we correct in x.
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
	
	
	// This method will give us a boolean telling us whether we're heading: true
    // for "up-down", false for "left-right"
    private boolean headingUpDown(double theta) {
            long headingNo = Math.round(theta / ((Math.PI) / 2));
            return ((headingNo % 2) == 1);
    }

    // This method computes the coordinate(depending on the heading of the robot
    // of the closest grid line that the robot has crossed
    public static double getGridLine(double coordinate) {
            return Math.round(((coordinate - OFFSET - 15) / 30)) * 30 + 15 + OFFSET;
    }
}