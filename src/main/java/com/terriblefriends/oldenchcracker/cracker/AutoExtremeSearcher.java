package com.terriblefriends.oldenchcracker.cracker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;
import com.terriblefriends.oldenchcracker.version.Version;
import com.terriblefriends.oldenchcracker.version.Versions;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AutoExtremeSearcher implements NativeKeyListener {

    private static final long[] NUMBERS = new long[] {
            0b01110100011001110101110011000101110L,
            0b00100011000010000100001000010011111L,
            0b01110100010000100110010001000111111L,
            0b01110100010000100110000011000101110L,
            0b00011001010100110001111110000100001L,
            0b11111100001111000001000011000101110L,
            0b00110010001000011110100011000101110L,
            0b11111100010000100010001000010000100L,
            0b01110100011000101110100011000101110L,
            0b01110100011000101111000010001001100L
    };

    private static final int PAUSE_DELAY_MILLIS = 500;

    private static final int[] COLOR_ACTIVE = new int[]{128, 255, 32};
    private static final int[] COLOR_INACTIVE = new int[]{64, 127, 16};

    private Robot robot;
    private int delay;
    private Version version;
    private JTextField[] advancesTextFields;
    private JCheckBox[] isLowCheckBoxes;
    private JButton crackButton;
    private JButton setupButton;
    private boolean windowsMode;
    private int robotX;
    private int robotY;
    private JLabel resultMessage;
    private int screenshotAttempts;
    private boolean testMode;
    private Runnable runnableEnableFields;

    private boolean initialized;
    private volatile boolean setup;
    private volatile boolean run;
    private volatile boolean running;
    private volatile boolean paused;

    public void init() {
        try {
            robot = new Robot();
        }
        catch (AWTException | SecurityException ex) {
            ex.printStackTrace(System.err);
            return;
        }

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            
            ex.printStackTrace(System.err);
            return;
        }
        this.initialized = true;
    }

    public boolean getInitialized() {
        return this.initialized;
    }

    public void setup(Version version, int delay, int x, int y, boolean windowsMode, int screenshotAttempts, boolean testMode, JTextField[] advancesTextFields, JCheckBox[] isLowCheckBoxes, JButton crackButton, JButton setupButton, JLabel resultMessage, Runnable runnableEnableFields) {
        this.version = version;
        this.delay = delay;
        this.advancesTextFields = advancesTextFields;
        this.isLowCheckBoxes = isLowCheckBoxes;
        this.crackButton = crackButton;
        this.setupButton = setupButton;
        this.windowsMode = windowsMode;
        this.robotX = x;
        this.robotY = y;
        this.resultMessage = resultMessage;
        this.screenshotAttempts = screenshotAttempts;
        this.testMode = testMode;
        this.runnableEnableFields = runnableEnableFields;
        this.setup = true;

        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent keyEvent) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(keyEvent.getKeyCode()));

        if (!this.running && keyEvent.getKeyCode() == NativeKeyEvent.VC_F4) {
            this.run = true;
            this.paused = false;
            this.running = true;

            new Thread(() -> {
                try {
                    int cycles = 0;
                    int collected = 0;
                    boolean hasFoundAnything = false;

                    while (this.run) {
                        if (this.paused) {
                            Thread.sleep(PAUSE_DELAY_MILLIS);
                            continue;
                        }

                        BufferedImage bufferedImage;
                        if (this.windowsMode) {
                            bufferedImage = this.altPrintscreenScreenshot();
                        }
                        else {
                            bufferedImage = this.robotScreenshot();
                        }

                        if (bufferedImage == null) {
                            return;
                        }

                        if (this.windowsMode && (bufferedImage.getWidth() != 856 || bufferedImage.getHeight() != 512)) {
                            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.windowsize"));
                            return;
                        }

                        int startX = this.windowsMode ? 559 : 0;
                        int startY = this.windowsMode ? 227 : 0;

                        long digitTwo = 0L;
                        long digitOne = 0L;

                        // scan in 5x7 areas for each pixel of each digit as bits in a long
                        int index = 34;
                        for (int y = 0; y < 7; y++) {
                            for (int x = 0; x < 5; x++) {
                                Color color = new Color(bufferedImage.getRGB(startX + (x * 2), startY + (y * 2)));
                                boolean found = (color.getRed() == COLOR_ACTIVE[0] && color.getGreen() == COLOR_ACTIVE[1] && color.getBlue() == COLOR_ACTIVE[2]) || (color.getRed() == COLOR_INACTIVE[0] && color.getGreen() == COLOR_INACTIVE[1] && color.getBlue() == COLOR_INACTIVE[2]);
                                digitTwo |= (found ? 1L : 0L) << index--;
                            }
                        }

                        index = 34;
                        for (int y = 0; y < 7; y++) {
                            for (int x = 0; x < 5; x++) {
                                Color color = new Color(bufferedImage.getRGB(startX + 12 + (x * 2), startY + (y * 2)));
                                boolean found = (color.getRed() == COLOR_ACTIVE[0] && color.getGreen() == COLOR_ACTIVE[1] && color.getBlue() == COLOR_ACTIVE[2]) || (color.getRed() == COLOR_INACTIVE[0] && color.getGreen() == COLOR_INACTIVE[1] && color.getBlue() == COLOR_INACTIVE[2]);
                                digitOne |= (found ? 1L : 0L) << index--;
                            }
                        }

                        //if we encounter a blank page (e.g. lag), just wait until the thing loads
                        if (digitOne == 0 && digitTwo == 0) {
                            if (!hasFoundAnything) {
                                this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.nopixels"));
                                return;
                            }
                            else {
                                System.out.println(EnchantCrackerI18n.translate("panel.extremes.search.error.emptypage"));
                                Thread.sleep(this.delay);
                                continue;
                            }
                        }
                        else {
                            hasFoundAnything = true;
                        }

                        int result = 0;

                        for (int i = 0; i < NUMBERS.length; i++) {
                            if (NUMBERS[i] == digitOne) {
                                result += i;
                            }
                            if (NUMBERS[i] == digitTwo) {
                                result += i * 10;
                            }
                        }

                        //System.out.println(Long.toString(digitOne, 2));
                        //System.out.println(Long.toString(digitTwo, 2));
                        //System.out.println(result);

                        if (this.testMode) {
                            this.exit(String.format(EnchantCrackerI18n.translate("panel.extremes.search.testsuccess"), result));
                            return;
                        }

                        // for versions 0-1 high is 15 low is 1
                        // for versions 2-4 high is 15 low is 4
                        if (result > 15) {
                            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.bookshelves"));
                            return;
                        }

                        int lowLevel = 4;
                        if (this.version == Versions.ZERO || this.version == Versions.TWO) {
                            lowLevel = 1;
                        }

                        if (result == 15 || result == lowLevel) {
                            this.isLowCheckBoxes[collected].setSelected(result == lowLevel);
                            if (collected != 0) {
                                this.advancesTextFields[collected].setText(String.valueOf(cycles));
                            }
                            collected++;
                            cycles = 0;
                        } else {
                            cycles++;
                        }

                        if (collected == this.version.getExtremesNeeded()) {
                            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.success"));
                            this.crackButton.setEnabled(true);
                            return;
                        }

                        // double left click to cycle levels
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        Thread.sleep(10);
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                        Thread.sleep(this.delay);
                    }
                }
                catch (InterruptedException ignored) {}
                catch (Exception e) {
                    this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.unknown"));
                    e.printStackTrace(System.err);
                }
                finally {
                    this.exitInternal();
                }
            }).start();
        }
        else if (keyEvent.getKeyCode() == NativeKeyEvent.VC_F12) {
            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.terminated"));
        }
        else if (this.running && keyEvent.getKeyCode() == NativeKeyEvent.VC_F8) {
            this.paused = !this.paused;
            this.resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.paused."+this.paused));
        }
    }

    public void exit(String message) {
        // avoid NPEs
        if (!this.setup) {
            return;
        }

        this.setup = false;

        // tell search thread to stop
        this.run = false;
        this.resultMessage.setText(message);

        // if thread was started, it will clean these up when it stops, otherwise do it here
        if (!this.running) {
            this.exitInternal();
        }
    }

    private void exitInternal() {
        GlobalScreen.removeNativeKeyListener(this);

        // release all keys
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_PRINTSCREEN);

        this.setupButton.setEnabled(true);

        this.runnableEnableFields.run();

        this.setup = false;
        this.running = false;
    }

    public void close() {
        this.exit(EnchantCrackerI18n.translate("panel.extremes.search.terminated"));
        // wait until search thread terminates
        while (this.running) {
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ignored) {}
        }
        try {
            GlobalScreen.unregisterNativeHook();
        }
        catch (NativeHookException ignored) {}
    }

    private BufferedImage robotScreenshot() {
        Rectangle capture = new Rectangle(this.robotX, this.robotY, this.robotX+20, this.robotY +12);
        return robot.createScreenCapture(capture);
    }

    private BufferedImage altPrintscreenScreenshot() {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_PRINTSCREEN);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_PRINTSCREEN);

        Image image = saveImageFromClipboard();
        if (image == null) {
            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.clipboardfetch"));
            return null;
        }
        BufferedImage bufferedImage = convertToBufferedImage(image);
        if (bufferedImage == null) {
            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.badclipboard"));
            return null;
        }
        return bufferedImage;
    }

    private Image saveImageFromClipboard() {
        int counter = 0;
        while (counter < this.screenshotAttempts) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException ignored) {}

            Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (trans != null && trans.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                try {
                    Image img = (Image) trans.getTransferData(DataFlavor.imageFlavor);
                    trans = new StringSelection("");
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
                    return img;
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace(System.err);
                }

            }
            counter++;
        }
        return null;
    }

    private static BufferedImage convertToBufferedImage(Image img) {
        BufferedImage bImage;
        if(img != null) {
            if (img instanceof BufferedImage) {
                bImage = (BufferedImage) img;
            }
            else {
                // Create a buffered image with transparency
                bImage  = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            }

            return bImage;
        }
        else {
            return null;
        }
    }
}
