package frc.robot.commands;

import java.util.function.DoubleSupplier;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

import edu.wpi.first.wpilibj2.command.CommandBase;

//import frc.robot.RobotContainer;
//import frc.robot.subsystems.Intake;
import frc.robot.subsystems.SwerveDrivetrain;
// import frc.robot.Robot;
public class AutoDrive extends CommandBase{ 
    private final SwerveDrivetrain m_drivetrain;
    private final Double m_move;
    private final Double m_rotate;
    private final Double m_value; 

    

    public AutoDrive(SwerveDrivetrain drivetrain, Double move, Double rotate, Double value){
        m_drivetrain = drivetrain;
        m_move = move;
        m_rotate = rotate;
        m_value = value;


        addRequirements(drivetrain);
    }

    public void initialize(){
        m_drivetrain.resetAllDriveEncoders();
    }

    public void execute(){
        
    m_drivetrain.resetAllDriveEncoders();
    System.out.println("average drive encoder value" +m_drivetrain.getAverageEncoderValue());
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    final var xSpeed =SwerveDrivetrain.kMaxSpeed*.1;

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    final var ySpeed =SwerveDrivetrain.kMaxSpeed*.1;
    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    final var rot = SwerveDrivetrain.kMaxAngularSpeed*.1;

    m_drivetrain.drive(xSpeed, ySpeed, rot, true, false);

    }

    public boolean isFinished(){
        return m_drivetrain.averageDistanceReached(1000);
    }


    public void end() {

    }

    public void interrupted(){
        end();
    }
}