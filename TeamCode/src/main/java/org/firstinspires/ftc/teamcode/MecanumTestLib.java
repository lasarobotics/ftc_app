package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "mecanum-lib", group = "test")
public class MecanumTestLib extends OpMode {
    private enum ArmState {
        STOP, IN, OUT;
    }

    DcMotor left_back, left_front, right_back, right_front, intake, shooter;
    CRServo arm;
    ArmState arm_state = ArmState.STOP;
    boolean intake_state = false;
    double time_last_arm_toggle = 0;
    double time_last_intake_toggle = 0;
    private float tol = 0.05f;
    private float trigger_tol = 0.25f;
    private boolean a_pressed = false;

    public void init() {
        left_back = hardwareMap.dcMotor.get("left_back");
        left_front = hardwareMap.dcMotor.get("left_front");
        right_back = hardwareMap.dcMotor.get("right_back");
        right_front = hardwareMap.dcMotor.get("right_front");
        intake = hardwareMap.dcMotor.get("intake");
        shooter = hardwareMap.dcMotor.get("shooter");
        arm = hardwareMap.crservo.get("button_presser");
    }

    public void loop() {
        Mecanum.arcade(damp(tol, gamepad1.left_stick_x), damp(tol, gamepad1.left_stick_y), damp(tol, gamepad1.right_stick_x), left_front, right_front, left_back, right_back);
        arm_state = gamepad1.dpad_left ? ArmState.IN : gamepad1.dpad_right ? ArmState.OUT : ArmState.STOP;

        if(gamepad1.right_trigger > trigger_tol) {
            if(getRuntime() - time_last_intake_toggle > 0.25) {
                time_last_intake_toggle = getRuntime();
                intake_state = !intake_state;
            }
        }

        if(a_pressed) {
            if(!gamepad1.a) {
                //a -> !a
                a_pressed = false;
            }
        } else {
            if(gamepad1.a) {
                //!a -> a
                a_pressed = true;
            }
        }

        switch(arm_state) {
            case STOP:
                arm.setPower(0);
                break;
            case IN:
                arm.setPower(1);
                break;
            case OUT:
                arm.setPower(-1);
                break;
        }

        intake.setPower(intake_state ? 1 : 0);
        shooter.setPower(gamepad1.dpad_down ? -1 : 0);
    }

    public void stop() {
        left_back.setPower(0);
        left_front.setPower(0);
        right_back.setPower(0);
        right_front.setPower(0);
        intake.setPower(0);
        shooter.setPower(0);
        arm.setPower(0);
    }

    private float damp(float tol, float val) {
        return Math.abs(val) < tol ? 0 : val;
    }
}