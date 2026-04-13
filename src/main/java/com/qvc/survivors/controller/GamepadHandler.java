package com.qvc.survivors.controller;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polls gamepad state each frame via JInput.
 * Gracefully degrades if no gamepad library or controller is available.
 */
public class GamepadHandler {
    private static final Logger log = LoggerFactory.getLogger(GamepadHandler.class);

    private static final double DEAD_ZONE = 0.2;

    private boolean available;
    private boolean enabled = true;
    private Controller gamepad;

    // Stick axes (-1.0 to 1.0)
    private double leftStickX, leftStickY;
    private double rightStickX, rightStickY;

    // Buttons (current frame)
    private boolean buttonA, buttonB, buttonX, buttonY;
    private boolean buttonStart, buttonSelect;
    private boolean dpadUp, dpadDown, dpadLeft, dpadRight;
    private boolean leftBumper, rightBumper;

    // Previous frame state for edge detection
    private boolean prevDpadUp, prevDpadDown, prevDpadLeft, prevDpadRight;
    private boolean prevA, prevB, prevStart;

    public GamepadHandler() {
        try {
            Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            for (Controller c : controllers) {
                Controller.Type type = c.getType();
                if (type == Controller.Type.GAMEPAD || type == Controller.Type.STICK) {
                    this.gamepad = c;
                    this.available = true;
                    log.info("Gamepad detected: {}", c.getName());
                    break;
                }
            }
            if (this.gamepad == null) {
                log.info("No gamepad detected");
                this.available = false;
            }
        } catch (Throwable t) {
            log.warn("Gamepad support unavailable: {}", t.getMessage());
            this.available = false;
        }
    }

    public void update() {
        if (!available || !enabled || gamepad == null) return;

        // Save previous state for edge detection
        prevDpadUp = dpadUp;
        prevDpadDown = dpadDown;
        prevDpadLeft = dpadLeft;
        prevDpadRight = dpadRight;
        prevA = buttonA;
        prevB = buttonB;
        prevStart = buttonStart;

        if (!gamepad.poll()) {
            // Controller disconnected
            available = false;
            resetState();
            log.info("Gamepad disconnected");
            return;
        }

        for (Component comp : gamepad.getComponents()) {
            Component.Identifier id = comp.getIdentifier();
            float value = comp.getPollData();

            // Analog sticks
            if (id == Component.Identifier.Axis.X) {
                leftStickX = value;
            } else if (id == Component.Identifier.Axis.Y) {
                leftStickY = value;
            } else if (id == Component.Identifier.Axis.RX) {
                rightStickX = value;
            } else if (id == Component.Identifier.Axis.RY) {
                rightStickY = value;
            }
            // D-pad (hat switch)
            else if (id == Component.Identifier.Axis.POV) {
                dpadUp = (value == Component.POV.UP || value == Component.POV.UP_LEFT || value == Component.POV.UP_RIGHT);
                dpadDown = (value == Component.POV.DOWN || value == Component.POV.DOWN_LEFT || value == Component.POV.DOWN_RIGHT);
                dpadLeft = (value == Component.POV.LEFT || value == Component.POV.UP_LEFT || value == Component.POV.DOWN_LEFT);
                dpadRight = (value == Component.POV.RIGHT || value == Component.POV.UP_RIGHT || value == Component.POV.DOWN_RIGHT);
            }
            // Buttons: standard XInput mapping
            else if (id == Component.Identifier.Button._0) {
                buttonA = value > 0.5f;
            } else if (id == Component.Identifier.Button._1) {
                buttonB = value > 0.5f;
            } else if (id == Component.Identifier.Button._2) {
                buttonX = value > 0.5f;
            } else if (id == Component.Identifier.Button._3) {
                buttonY = value > 0.5f;
            } else if (id == Component.Identifier.Button._4) {
                leftBumper = value > 0.5f;
            } else if (id == Component.Identifier.Button._5) {
                rightBumper = value > 0.5f;
            } else if (id == Component.Identifier.Button._6) {
                buttonSelect = value > 0.5f;
            } else if (id == Component.Identifier.Button._7) {
                buttonStart = value > 0.5f;
            }
        }
    }

    private void resetState() {
        leftStickX = leftStickY = rightStickX = rightStickY = 0;
        buttonA = buttonB = buttonX = buttonY = false;
        buttonStart = buttonSelect = false;
        dpadUp = dpadDown = dpadLeft = dpadRight = false;
        leftBumper = rightBumper = false;
    }

    private double applyDeadZone(double value) {
        return Math.abs(value) < DEAD_ZONE ? 0.0 : value;
    }

    // Movement from left stick (dead zone applied)
    public double getMoveX() { return applyDeadZone(leftStickX); }
    public double getMoveY() { return applyDeadZone(leftStickY); }

    // Right stick
    public double getAimX() { return applyDeadZone(rightStickX); }
    public double getAimY() { return applyDeadZone(rightStickY); }

    // Button states
    public boolean isConfirmPressed() { return buttonA; }
    public boolean isBackPressed() { return buttonB; }
    public boolean isPausePressed() { return buttonStart; }

    // D-pad
    public boolean isDpadUp() { return dpadUp; }
    public boolean isDpadDown() { return dpadDown; }
    public boolean isDpadLeft() { return dpadLeft; }
    public boolean isDpadRight() { return dpadRight; }

    // Edge detection ("just pressed" this frame)
    public boolean isDpadUpJustPressed() { return dpadUp && !prevDpadUp; }
    public boolean isDpadDownJustPressed() { return dpadDown && !prevDpadDown; }
    public boolean isDpadLeftJustPressed() { return dpadLeft && !prevDpadLeft; }
    public boolean isDpadRightJustPressed() { return dpadRight && !prevDpadRight; }
    public boolean isConfirmJustPressed() { return buttonA && !prevA; }
    public boolean isBackJustPressed() { return buttonB && !prevB; }
    public boolean isPauseJustPressed() { return buttonStart && !prevStart; }

    public boolean isConnected() { return available && gamepad != null; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void cleanup() {
        // JInput doesn't require explicit cleanup, but reset state
        resetState();
        gamepad = null;
        available = false;
    }
}
