package com.terriblefriends.oldenchcracker.cracker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.terriblefriends.oldenchcracker.EnchantCrackerI18n;
import com.terriblefriends.oldenchcracker.version.Version;

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
import java.util.Arrays;

public class AutoExtremeSearcher implements NativeKeyListener {

    private static final boolean[] ONE = new boolean[]{
            false, false, true, false, false,
            false, true, true, false, false,
            false, false, true, false, false,
            false, false, true, false, false,
            false, false, true, false, false,
            false, false, true, false, false,
            true, true, true, true, true
    };

    private static final boolean[] FIVE = new boolean[]{
            true, true, true, true, true,
            true, false, false, false, false,
            true, true, true, true, false,
            false, false, false, false, true,
            false, false, false, false, true,
            true, false, false, false, true,
            false, true, true, true, false
    };

    private static final boolean[] SIX = new boolean[]{
            false, false, true, true, false,
            false, true, false, false, false,
            true, false, false, false, false,
            true, true, true, true, false,
            true, false, false, false, true,
            true, false, false, false, true,
            false, true, true, true, false
    };

    private Robot ROBOT;
    private int delay;
    private int extremesNeeded;
    private JTextField[] advancesTextFields;
    private JCheckBox[] isLowCheckBoxes;
    private JButton crackButton;
    private JButton setupButton;
    private boolean windowsMode;
    private int x;
    private int y;
    private JLabel resultMessage;
    private int screenshotAttempts;
    private boolean testMode;

    private boolean initialized;
    private boolean setup;
    private boolean hasStarted;
    private boolean paused;
    private boolean hasFoundAnything;
    private Thread cycleThread;

    public void init() {
        boolean errored = false;
        try {
            ROBOT = new Robot();
        }
        catch (AWTException | SecurityException ex) {
            ex.printStackTrace(System.err);
            errored = true;
        }

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            ex.printStackTrace(System.err);
            errored = true;
        }
        this.initialized = !errored;
    }

    public void close() {
        try {
            GlobalScreen.unregisterNativeHook();
        }
        catch (NativeHookException ignored) {

        }
    }

    public boolean getInitialized() {
        return this.initialized;
    }

    public void setup(Version version, int delay, int x, int y, boolean windowsMode, int screenshotAttempts, boolean testMode, JTextField[] advancesTextFields, JCheckBox[] isLowCheckBoxes, JButton crackButton, JButton setupButton, JLabel resultMessage) {
        this.extremesNeeded = version.getExtremesNeeded();
        this.delay = delay;
        this.advancesTextFields = advancesTextFields;
        this.isLowCheckBoxes = isLowCheckBoxes;
        this.crackButton = crackButton;
        this.setupButton = setupButton;
        this.windowsMode = windowsMode;
        this.x = x;
        this.y = y;
        this.resultMessage = resultMessage;
        this.screenshotAttempts = screenshotAttempts;
        this.testMode = testMode;
        this.hasStarted = false;
        this.paused = false;
        this.hasFoundAnything = false;

        GlobalScreen.addNativeKeyListener(this);

        this.setup = true;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent keyEvent) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(keyEvent.getKeyCode()));

        if (!this.paused && keyEvent.getKeyCode() == NativeKeyEvent.VC_F4) {
            if (!this.hasStarted) {
                this.hasStarted = true;
                this.cycleThread = new Thread(() -> {
                    try {
                        int cycles = 0;
                        int collected = 0;

                        while (this.hasStarted) {
                            if (this.paused) {
                                Thread.sleep(1000);
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

                            boolean[] digitTwo = new boolean[35];

                            int startX = this.windowsMode ? 559 : 0;
                            int startY = this.windowsMode ? 227 : 0;

                            boolean foundAnything = false;

                            int index = 0;
                            for (int y = 0; y < 7; y++) {
                                for (int x = 0; x < 5; x++) {
                                    Color color = new Color(bufferedImage.getRGB(startX + (x * 2), startY + (y * 2)));
                                    boolean found = (color.getRed() == 128 && color.getGreen() == 255 && color.getBlue() == 32);
                                    if (found) {
                                        foundAnything = true;
                                    }
                                    digitTwo[index] = found;
                                    index++;
                                }
                            }

                            boolean[] digitOne = new boolean[35];

                            index = 0;
                            for (int y = 0; y < 7; y++) {
                                for (int x = 0; x < 5; x++) {
                                    Color color = new Color(bufferedImage.getRGB(startX + 12 + (x * 2), startY + (y * 2)));
                                    boolean found = (color.getRed() == 128 && color.getGreen() == 255 && color.getBlue() == 32);
                                    if (found) {
                                        foundAnything = true;
                                    }
                                    digitOne[index] = found;
                                    index++;
                                }
                            }

                            boolean singleDigit = true;

                            if (!this.hasFoundAnything && !foundAnything) {
                                this.exit(EnchantCrackerI18n.translate("panel.extremes.search.error.nopixels"));
                                return;
                            }
                            else {
                                this.hasFoundAnything = true;
                            }

                            //if we encounter a blank page (e.g. lag), just wait until the thing loads
                            if (foundAnything) {
                                for (boolean b : digitTwo) {
                                    if (b) {
                                        singleDigit = false;
                                        break;
                                    }
                                }

                                if (this.testMode && Arrays.equals(digitOne, FIVE)) {
                                    this.exit(EnchantCrackerI18n.translate("panel.extremes.search.testsuccess"));
                                    return;
                                }

                                if (Arrays.equals(digitTwo, FIVE)) {
                                    this.isLowCheckBoxes[collected].setSelected(false);
                                    if (collected != 0) {
                                        this.advancesTextFields[collected].setText(String.valueOf(cycles));
                                    }
                                    collected++;
                                    cycles = 0;
                                    if (collected == this.extremesNeeded) {
                                        this.exit(EnchantCrackerI18n.translate("panel.extremes.search.success"));
                                        this.crackButton.setEnabled(true);
                                        return;
                                    }
                                } else if (singleDigit && this.extremesNeeded == 5 && Arrays.equals(digitOne, ONE)) {
                                    this.isLowCheckBoxes[collected].setSelected(true);
                                    if (collected != 0) {
                                        this.advancesTextFields[collected].setText(String.valueOf(cycles));
                                    }
                                    collected++;
                                    cycles = 0;
                                    if (collected == this.extremesNeeded) {
                                        this.exit(EnchantCrackerI18n.translate("panel.extremes.search.success"));
                                        this.crackButton.setEnabled(true);
                                        return;
                                    }
                                } else if (this.extremesNeeded == 11 && Arrays.equals(digitTwo, ONE) && Arrays.equals(digitOne, SIX)) {
                                    this.isLowCheckBoxes[collected].setSelected(true);
                                    if (collected != 0) {
                                        this.advancesTextFields[collected].setText(String.valueOf(cycles));
                                    }
                                    collected++;
                                    cycles = 0;
                                    if (collected == this.extremesNeeded) {
                                        this.exit(EnchantCrackerI18n.translate("panel.extremes.search.success"));
                                        this.crackButton.setEnabled(true);
                                        return;
                                    }
                                } else {
                                    cycles++;
                                }

                                ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                Thread.sleep(10);
                                ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            }
                            else {
                                System.out.println(EnchantCrackerI18n.translate("panels.extremes.search.error.emptypage"));
                            }

                            Thread.sleep(this.delay);

                        }
                    } catch (Exception e) {
                        this.exit(EnchantCrackerI18n.translate("panels.extremes.search.error.unknown"));
                        e.printStackTrace(System.err);
                    }
                });
                this.cycleThread.start();
            }
        }
        else if (!this.paused && keyEvent.getKeyCode() == NativeKeyEvent.VC_F12) {
            this.exit(EnchantCrackerI18n.translate("panel.extremes.search.terminated"));
        }
        else if (keyEvent.getKeyCode() == NativeKeyEvent.VC_F8) {
            this.paused = !this.paused;
            this.resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.paused."+this.paused));
        }
    }

    private BufferedImage robotScreenshot() {
        Rectangle capture = new Rectangle(this.x, this.y, this.x+20, this.y+12);
        return ROBOT.createScreenCapture(capture);
    }

    private BufferedImage altPrintscreenScreenshot() {
        ROBOT.keyPress(KeyEvent.VK_ALT);
        ROBOT.keyPress(KeyEvent.VK_PRINTSCREEN);
        ROBOT.keyRelease(KeyEvent.VK_ALT);
        ROBOT.keyRelease(KeyEvent.VK_PRINTSCREEN);

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

    public void exit(String message) {
        if (this.setup) {
            if (this.hasStarted) {
                this.cycleThread.interrupt();
                ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                ROBOT.keyRelease(KeyEvent.VK_ALT);
                ROBOT.keyRelease(KeyEvent.VK_PRINTSCREEN);
                this.hasStarted = false;
                this.paused = false;
                this.hasFoundAnything = false;
            }
            this.resultMessage.setText(message);
            this.setupButton.setEnabled(true);
            GlobalScreen.removeNativeKeyListener(this);
        }
        this.setup = false;
    }

    private Image saveImageFromClipboard() {
        try {
            int counter = 0;
            while (counter < this.screenshotAttempts) {

                Thread.sleep(100);

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
        catch (InterruptedException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }



    private static BufferedImage convertToBufferedImage(Image img) {
        BufferedImage bImage;
        if(img != null) {
            if (img instanceof BufferedImage)
            {
                bImage = (BufferedImage) img;
            }
            else
            {
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
