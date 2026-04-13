package com.qvc.survivors.controller;

import org.hid4java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polls gamepad state each frame via hid4java (hidapi).
 * Supports 8BitDo, DualSense, DualShock 4, Xbox, and Switch Pro controllers.
 * Gracefully degrades if no gamepad is available.
 */
public class GamepadHandler {
    private static final Logger log = LoggerFactory.getLogger(GamepadHandler.class);
    private static final double DEAD_ZONE = 0.2;

    // Known controller vendor IDs
    private static final int VID_8BITDO = 0x2DC8;
    private static final int VID_SONY = 0x054C;
    private static final int VID_MICROSOFT = 0x045E;
    private static final int VID_NINTENDO = 0x057E;

    // Known Sony product IDs
    private static final int PID_DUALSENSE = 0x0CE6;
    private static final int PID_DUALSENSE_EDGE = 0x0DF2;
    private static final int PID_DUALSHOCK4 = 0x05C4;
    private static final int PID_DUALSHOCK4_V2 = 0x09CC;

    private HidServices hidServices;
    private HidDevice device;
    private boolean available;
    private boolean enabled = true;
    private ControllerMapping mapping;
    private double rescanTimer = 0;
    private int debugReportCount = 0;
    private static final double RESCAN_INTERVAL = 3.0;

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
            HidServicesSpecification spec = new HidServicesSpecification();
            spec.setAutoStart(false);
            this.hidServices = HidManager.getHidServices(spec);
            this.hidServices.start();
            scanForGamepad();
        } catch (Throwable t) {
            log.warn("Gamepad support unavailable: {}", t.getMessage());
            available = false;
        }
    }

    private void scanForGamepad() {
        if (hidServices == null) return;
        try { hidServices.scan(); } catch (Exception ignored) {}
        for (HidDevice dev : hidServices.getAttachedHidDevices()) {
            int vid = dev.getVendorId();
            int pid = dev.getProductId();
            // Check known gamepad vendors
            if (vid == VID_8BITDO || vid == VID_SONY || vid == VID_MICROSOFT || vid == VID_NINTENDO) {
                if (dev.open()) {
                    this.device = dev;
                    this.mapping = ControllerMapping.detect(vid, pid);
                    this.available = true;
                    this.debugReportCount = 0;
                    log.info("Gamepad connected: {} (VID:{} PID:{})", dev.getProduct(),
                            String.format("0x%04X", vid), String.format("0x%04X", pid));
                    return;
                }
            }
            // Generic gamepad via HID usage page: 1 = Generic Desktop, usage 4 = Joystick, 5 = Gamepad
            if (dev.getUsagePage() == 1 && (dev.getUsage() == 4 || dev.getUsage() == 5)) {
                if (dev.open()) {
                    this.device = dev;
                    this.mapping = ControllerMapping.detect(vid, pid);
                    this.available = true;
                    this.debugReportCount = 0;
                    log.info("Generic gamepad connected: {} (VID:{} PID:{})", dev.getProduct(),
                            String.format("0x%04X", vid), String.format("0x%04X", pid));
                    return;
                }
            }
        }
        if (device == null) {
            log.info("No gamepad detected (will rescan every {}s)", RESCAN_INTERVAL);
            available = false;
        }
    }

    public void update(double deltaTime) {
        // Hot-plug: rescan periodically when no controller
        if (!available) {
            rescanTimer += deltaTime;
            if (rescanTimer >= RESCAN_INTERVAL) {
                rescanTimer = 0;
                scanForGamepad();
            }
            return;
        }
        if (!enabled || device == null) return;

        // Save previous state for edge detection
        prevDpadUp = dpadUp;
        prevDpadDown = dpadDown;
        prevDpadLeft = dpadLeft;
        prevDpadRight = dpadRight;
        prevA = buttonA;
        prevB = buttonB;
        prevStart = buttonStart;

        // Read HID report (non-blocking, 0ms timeout)
        byte[] report = new byte[64];
        int bytesRead = device.read(report, 0);
        if (bytesRead < 0) {
            log.info("Gamepad disconnected");
            device.close();
            device = null;
            available = false;
            resetState();
            return;
        }
        if (bytesRead == 0) return;

        // Debug: log first 10 reports for mapping diagnosis
        if (debugReportCount < 10) {
            debugReportCount++;
            StringBuilder sb = new StringBuilder("HID report #").append(debugReportCount)
                .append(" (").append(bytesRead).append("B, ").append(mapping.type).append("): ");
            for (int i = 0; i < Math.min(bytesRead, 24); i++) {
                sb.append(String.format("%02X ", report[i]));
            }
            log.info(sb.toString());
        }

        // Parse report using controller-specific mapping
        mapping.parse(report, bytesRead, this);
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

    public boolean isConnected() { return available && device != null; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void cleanup() {
        resetState();
        if (device != null) { device.close(); device = null; }
        if (hidServices != null) { hidServices.shutdown(); }
        available = false;
    }

    // -----------------------------------------------------------------------
    // Controller-specific HID report mapping
    // -----------------------------------------------------------------------
    static class ControllerMapping {
        enum Type { GENERIC, EIGHT_BITDO, DUALSENSE, DUALSHOCK4, XBOX, SWITCH_PRO }

        private final Type type;

        private ControllerMapping(Type type) {
            this.type = type;
        }

        static ControllerMapping detect(int vid, int pid) {
            if (vid == VID_8BITDO) return new ControllerMapping(Type.EIGHT_BITDO);
            if (vid == VID_SONY && (pid == PID_DUALSENSE || pid == PID_DUALSENSE_EDGE))
                return new ControllerMapping(Type.DUALSENSE);
            if (vid == VID_SONY && (pid == PID_DUALSHOCK4 || pid == PID_DUALSHOCK4_V2))
                return new ControllerMapping(Type.DUALSHOCK4);
            if (vid == VID_MICROSOFT) return new ControllerMapping(Type.XBOX);
            if (vid == VID_NINTENDO) return new ControllerMapping(Type.SWITCH_PRO);
            return new ControllerMapping(Type.GENERIC);
        }

        void parse(byte[] report, int length, GamepadHandler h) {
            switch (type) {
                case DUALSENSE -> parseDualSense(report, h);
                case DUALSHOCK4 -> parseDualShock4(report, h);
                case EIGHT_BITDO -> parse8BitDo(report, h);
                case XBOX -> parseXbox(report, h);
                case SWITCH_PRO -> parseSwitchPro(report, h);
                default -> parseGeneric(report, length, h);
            }
        }

        // PS5 DualSense (Bluetooth)
        private static void parseDualSense(byte[] r, GamepadHandler h) {
            int offset = (r[0] == 0x01) ? 1 : 0;
            h.leftStickX = (r[offset] & 0xFF) / 127.5 - 1.0;
            h.leftStickY = (r[offset + 1] & 0xFF) / 127.5 - 1.0;
            h.rightStickX = (r[offset + 2] & 0xFF) / 127.5 - 1.0;
            h.rightStickY = (r[offset + 3] & 0xFF) / 127.5 - 1.0;
            int buttons = r[offset + 4] & 0xFF;
            int hat = buttons & 0x0F;
            parseHat(hat, h);
            h.buttonA = (buttons & 0x20) != 0; // Cross
            h.buttonB = (buttons & 0x40) != 0; // Circle
            h.buttonX = (buttons & 0x10) != 0; // Square
            h.buttonY = (buttons & 0x80) != 0; // Triangle
            int buttons2 = r[offset + 5] & 0xFF;
            h.leftBumper = (buttons2 & 0x01) != 0;
            h.rightBumper = (buttons2 & 0x02) != 0;
            h.buttonSelect = (buttons2 & 0x10) != 0;
            h.buttonStart = (buttons2 & 0x20) != 0;
        }

        // PS4 DualShock 4 (Bluetooth) -- same layout as DualSense
        private static void parseDualShock4(byte[] r, GamepadHandler h) {
            parseDualSense(r, h);
        }

        // 8BitDo SN30 Pro (macOS Bluetooth mode)
        private static void parse8BitDo(byte[] r, GamepadHandler h) {
            int offset = 0;
            if (r.length > 8 && r[0] == 0x01) offset = 1;
            h.leftStickX = (r[offset + 3] & 0xFF) / 127.5 - 1.0;
            h.leftStickY = (r[offset + 4] & 0xFF) / 127.5 - 1.0;
            h.rightStickX = (r[offset + 5] & 0xFF) / 127.5 - 1.0;
            h.rightStickY = (r[offset + 6] & 0xFF) / 127.5 - 1.0;
            int buttons = ((r[offset + 1] & 0xFF) << 8) | (r[offset] & 0xFF);
            h.buttonA = (buttons & 0x0001) != 0;
            h.buttonB = (buttons & 0x0002) != 0;
            h.buttonX = (buttons & 0x0008) != 0;
            h.buttonY = (buttons & 0x0010) != 0;
            h.leftBumper = (buttons & 0x0040) != 0;
            h.rightBumper = (buttons & 0x0080) != 0;
            h.buttonSelect = (buttons & 0x0004) != 0;
            h.buttonStart = (buttons & 0x0020) != 0;
            int hat = r[offset + 2] & 0x0F;
            parseHat(hat, h);
        }

        // Xbox controller (Bluetooth)
        private static void parseXbox(byte[] r, GamepadHandler h) {
            int offset = (r[0] == 0x01) ? 1 : 0;
            h.leftStickX = (r[offset] & 0xFF) / 127.5 - 1.0;
            h.leftStickY = (r[offset + 1] & 0xFF) / 127.5 - 1.0;
            h.rightStickX = (r[offset + 2] & 0xFF) / 127.5 - 1.0;
            h.rightStickY = (r[offset + 3] & 0xFF) / 127.5 - 1.0;
            int buttons = ((r[offset + 5] & 0xFF) << 8) | (r[offset + 4] & 0xFF);
            h.buttonA = (buttons & 0x0001) != 0;
            h.buttonB = (buttons & 0x0002) != 0;
            h.buttonX = (buttons & 0x0008) != 0;
            h.buttonY = (buttons & 0x0010) != 0;
            h.leftBumper = (buttons & 0x0040) != 0;
            h.rightBumper = (buttons & 0x0080) != 0;
            h.buttonSelect = (buttons & 0x0004) != 0;
            h.buttonStart = (buttons & 0x0020) != 0;
            int hat = r[offset + 6] & 0x0F;
            parseHat(hat, h);
        }

        // Nintendo Switch Pro (Bluetooth) -- uses same generic format as fallback
        private static void parseSwitchPro(byte[] r, GamepadHandler h) {
            parseGeneric(r, r.length, h);
        }

        // Generic fallback
        private static void parseGeneric(byte[] r, int length, GamepadHandler h) {
            int offset = 0;
            if (length > 8 && r[0] < 10) offset = 1;
            if (offset + 6 < length) {
                h.leftStickX = (r[offset] & 0xFF) / 127.5 - 1.0;
                h.leftStickY = (r[offset + 1] & 0xFF) / 127.5 - 1.0;
                h.rightStickX = (r[offset + 2] & 0xFF) / 127.5 - 1.0;
                h.rightStickY = (r[offset + 3] & 0xFF) / 127.5 - 1.0;
            }
            if (offset + 5 < length) {
                int buttons = r[offset + 4] & 0xFF;
                h.buttonA = (buttons & 0x01) != 0;
                h.buttonB = (buttons & 0x02) != 0;
                h.buttonX = (buttons & 0x04) != 0;
                h.buttonY = (buttons & 0x08) != 0;
                h.buttonStart = (buttons & 0x10) != 0;
                h.buttonSelect = (buttons & 0x20) != 0;
            }
            if (offset + 6 < length) {
                int hat = r[offset + 5] & 0x0F;
                parseHat(hat, h);
            }
        }

        // Shared hat-switch parser (0=N, 1=NE, 2=E ... 7=NW, 8+=neutral)
        private static void parseHat(int hat, GamepadHandler h) {
            h.dpadUp = (hat == 0 || hat == 1 || hat == 7);
            h.dpadRight = (hat == 1 || hat == 2 || hat == 3);
            h.dpadDown = (hat == 3 || hat == 4 || hat == 5);
            h.dpadLeft = (hat == 5 || hat == 6 || hat == 7);
        }
    }
}
