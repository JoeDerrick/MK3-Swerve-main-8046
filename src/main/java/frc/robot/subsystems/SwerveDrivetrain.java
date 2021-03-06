// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
public class SwerveDrivetrain extends SubsystemBase {

  //these are limits you can change!!!
  public static final double kMaxSpeed = Units.feetToMeters(13.6); // 20 feet per second//13.6
  public static final double kMaxAngularSpeed = Math.PI; // 1/2 rotation per second
  public static double feildCalibration = 0;

  //this is where you put the angle offsets you got from the smart dashboard

  public static double frontLeftOffset = 160.0;//203.0
  public static double frontRightOffset = 20.0;
  public static double backLeftOffset = 65.0;//294.0
  public static double backRightOffset = 320.0;

  // note errata Cancoders can't have an ID higher than 15 if they are to be used as remote sensors on talon fx

  //put your can Id's here!
  public static final int frontLeftDriveId = 1; 
  public static final int frontLeftCANCoderId = 11; 
  public static final int frontLeftSteerId = 2;
  //put your can Id's here!
  public static final int frontRightDriveId = 3; 
  public static final int frontRightCANCoderId = 13; 
  public static final int frontRightSteerId = 4; 
  //put your can Id's here!
  public static final int backLeftDriveId = 7; 
  public static final int backLeftCANCoderId = 9; 
  public static final int backLeftSteerId = 8;
  //put your can Id's here!

  public static final int backRightDriveId = 5; 
  public static final int backRightCANCoderId = 15; 
  public static final int backRightSteerId = 6; 
  
  
  //public static AHRS gyro = new AHRS(SPI.Port.kMXP);
  //public static PigeonIMU gyro = new PigeonIMU(10);
  PigeonIMU _pidgey;
  public static TalonSRX _pigeonTalon = new TalonSRX(10);


  

  //maybe add the talon srx and refer t its talon ID
  //add pigeon gyro here

  private SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
    new Translation2d(
      Units.inchesToMeters(10),
      Units.inchesToMeters(10)
    ),
    new Translation2d(
      Units.inchesToMeters(10),
      Units.inchesToMeters(-10)
    ),
    new Translation2d(
      Units.inchesToMeters(-10),
      Units.inchesToMeters(10)
    ),
    new Translation2d(
      Units.inchesToMeters(-10),
      Units.inchesToMeters(-10)
    )
  );// looks like a matrix to set relative places on the chassis.
  // TODO measure the distance from the center of the robot to center of each wheel

 

  private SwerveModuleMK3[] modules = new SwerveModuleMK3[] {

    new SwerveModuleMK3(new TalonFX(frontLeftDriveId), new TalonFX(frontLeftSteerId), new CANCoder(frontLeftCANCoderId), Rotation2d.fromDegrees(frontLeftOffset)), // Front Left
    new SwerveModuleMK3(new TalonFX(frontRightDriveId), new TalonFX(frontRightSteerId), new CANCoder(frontRightCANCoderId), Rotation2d.fromDegrees(frontRightOffset)), // Front Right
    new SwerveModuleMK3(new TalonFX(backLeftDriveId), new TalonFX(backLeftSteerId), new CANCoder(backLeftCANCoderId), Rotation2d.fromDegrees(backLeftOffset)), // Back Left
    new SwerveModuleMK3(new TalonFX(backRightDriveId), new TalonFX(backRightSteerId), new CANCoder(backRightCANCoderId), Rotation2d.fromDegrees(backRightOffset))  // Back Right

  };

  public SwerveDrivetrain() {
   // gyro.reset(); //not sure if I commented this out or not

    /* create the pigeon */
    _pidgey = new PigeonIMU(_pigeonTalon);
   
  }

  public double getAngle() {
    double[] ypr = new double[3];//here is an array  source pigeonAPI:  https://docs.ctre-phoenix.com/en/stable/ch11_BringUpPigeon.html#pigeon-api
    _pidgey.getYawPitchRoll(ypr); // put the data from the pidgey into this array
    return ypr[0];// look at the first column in the array
  }

  public void resetYaw(){
    _pidgey.setYaw(0,50);
    _pidgey.setAccumZAngle(0,50);//50 is ktimeouts
  }

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed Speed of the robot in the x direction (forward).
   * @param ySpeed Speed of the robot in the y direction (sideways).
   * @param rot Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the field.
   * @param calibrateGyro button to recalibrate the gyro offset
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative, boolean calibrateGyro) {
    
    if(calibrateGyro){
      //gyro.reset(); //recalibrates gyro offset
     resetYaw();
    }

    SwerveModuleState[] states =
      kinematics.toSwerveModuleStates(
        fieldRelative // if field relative is true, do the ? line, otherwise do the : line //called a conditional operator condition ? result_if_true : result_if_false
          ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, Rotation2d.fromDegrees(-getAngle()))
          : new ChassisSpeeds(xSpeed, ySpeed, rot));


    // using states generated above normalize using the max wheel speed.
    SwerveDriveKinematics.normalizeWheelSpeeds(states, kMaxSpeed);



    //-----smart Dashboard outputs ----// re-write without the fancy states thing to make it clearer
    for (int i = 0; i < states.length; i++) {
      SwerveModuleMK3 module = modules[i];
      SwerveModuleState state = states[i];
      SmartDashboard.putNumber(String.valueOf(i), module.getRawAngle());
      //SmartDashboard.putNumber(String.valueOf(i), module.)
      //below is a line to comment out from step 5
      module.setDesiredState(state);
      SmartDashboard.putNumber("gyro Angle", getAngle());
    }
  }
  public double getAverageEncoderValue(){
    SwerveModuleMK3 module0 = modules[0];
    SwerveModuleMK3 module1 = modules[1];
    SwerveModuleMK3 module2 = modules[2];
    SwerveModuleMK3 module3 = modules[3];

    return  (module0.getWheelPosition()+module1.getWheelPosition()+module2.getWheelPosition()+module3.getWheelPosition())/4;
  
  }

  public boolean averageDistanceReached(double threshold){

    if (threshold> getAverageEncoderValue()){
      return true;
    }
    else return false;
  }

  public void resetAllDriveEncoders(){
    SwerveModuleMK3 module0 = modules[0];
    SwerveModuleMK3 module1 = modules[1];
    SwerveModuleMK3 module2 = modules[2];
    SwerveModuleMK3 module3 = modules[3];
    module0.resetDriveEncoder();
    module1.resetDriveEncoder();
    module2.resetDriveEncoder();
    module3.resetDriveEncoder();
  }
  

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
