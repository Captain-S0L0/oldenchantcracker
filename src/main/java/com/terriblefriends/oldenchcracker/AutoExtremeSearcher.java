package com.terriblefriends.oldenchcracker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.terriblefriends.oldenchcracker.versions.Version;

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
    private static Robot ROBOT;

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

    static {
        try {
            ROBOT = new Robot();
        }
        catch (AWTException | SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to create robot!");
        }

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException("failed to create native hook!");
        }
    }

    private final Version version;
    private final int delay;
    private final int extremesNeeded;
    private final JTextField[] advancesTextFields;
    private final JCheckBox[] isLowCheckBoxes;
    private final JButton crackButton;
    private final boolean windowsMode;
    private final int x;
    private final int y;
    private final JLabel resultMessage;
    private final int screenshotAttempts;

    private boolean hasStarted = false;
    private boolean paused = false;

    public AutoExtremeSearcher(Version version, int delay, int x, int y, boolean windowsMode, int screenshotAttempts, JTextField[] advancesTextFields, JCheckBox[] isLowCheckBoxes, JButton crackButton, JLabel resultMessage) {
        this.extremesNeeded = version.getExtremesNeeded();
        this.version = version;
        this.delay = delay;
        this.advancesTextFields = advancesTextFields;
        this.isLowCheckBoxes = isLowCheckBoxes;
        this.crackButton = crackButton;
        this.windowsMode = windowsMode;
        this.x = x;
        this.y = y;
        this.resultMessage = resultMessage;
        this.screenshotAttempts = screenshotAttempts;

        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent keyEvent) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(keyEvent.getKeyCode()));

        if (keyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            if (!this.hasStarted) {
                this.hasStarted = true;
                Thread cycleThread = new Thread(() -> {
                    try {
                        int cycles = 0;
                        int collected = 0;

                        while (this.hasStarted) {
                            if (this.paused) {
                                Thread.sleep(1000);
                                continue;
                            }


                            ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            Thread.sleep(10);
                            ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            Thread.sleep(this.delay);

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
                                this.exit("Error! Window not correct size (856x512)!");
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

                            if (!foundAnything) {
                                this.exit("Error! Failed to find pixels! (either GUI isn't 2 or your X & Y are wrong)");
                                return;
                            }

                            for (boolean b : digitTwo) {
                                if (b) {
                                    singleDigit = false;
                                    break;
                                }
                            }

                            if (Arrays.equals(digitTwo, FIVE)) {
                                this.isLowCheckBoxes[collected].setSelected(false);
                                if (collected != 0) {
                                    this.advancesTextFields[collected].setText(String.valueOf(cycles));
                                }
                                System.out.println("high "+cycles);
                                collected++;
                                cycles = 0;
                                if (collected == this.extremesNeeded) {
                                    this.exit("Auto Searcher complete!");
                                    this.crackButton.setEnabled(true);
                                    return;
                                }
                            }
                            else if (singleDigit && this.extremesNeeded == 5 && Arrays.equals(digitOne, ONE)) {
                                this.isLowCheckBoxes[collected].setSelected(true);
                                if (collected != 0) {
                                    this.advancesTextFields[collected].setText(String.valueOf(cycles));
                                }
                                System.out.println("low "+cycles);
                                collected++;
                                cycles = 0;
                                if (collected == this.extremesNeeded) {
                                    this.exit("Auto Searcher complete!");
                                    this.crackButton.setEnabled(true);
                                    return;
                                }
                            }
                            else if (this.extremesNeeded == 11 && Arrays.equals(digitTwo, ONE) && Arrays.equals(digitOne, SIX)) {
                                this.isLowCheckBoxes[collected].setSelected(true);
                                if (collected != 0) {
                                    this.advancesTextFields[collected].setText(String.valueOf(cycles));
                                }
                                System.out.println("low "+cycles);
                                collected++;
                                cycles = 0;
                                if (collected == this.extremesNeeded) {
                                    this.exit("Auto Searcher complete!");
                                    this.crackButton.setEnabled(true);
                                    return;
                                }
                            }
                            else {
                                cycles++;
                            }
                        }
                    } catch (Exception e) {
                        this.exit("Error! Something happened! (check console)");
                        e.printStackTrace();
                    }
                });
                cycleThread.start();
            }
        }
        else if (keyEvent.getKeyCode() == NativeKeyEvent.VC_SPACE) {
            this.exit("Auto Searcher force stopped!");
        }
        else if (keyEvent.getKeyCode() == NativeKeyEvent.VC_P) {
            paused = !paused;
            this.resultMessage.setText("Auto Searcher paused = "+paused);
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
            this.exit("Error! Failed to fetch screenshot from clipboard (took too long)!");
            return null;
        }
        BufferedImage bufferedImage = convertToBufferedImage(image);
        if (bufferedImage == null) {
            this.exit("Error! Failed to convert to buffered image!");
            return null;
        }
        return bufferedImage;
    }

    private void exit(String message) {
        ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        ROBOT.keyRelease(KeyEvent.VK_ALT);
        ROBOT.keyRelease(KeyEvent.VK_PRINTSCREEN);
        GlobalScreen.removeNativeKeyListener(this);
        this.resultMessage.setText(message);
        this.hasStarted = false;
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
                        e.printStackTrace();
                    }

                }
                counter++;
            }
            return null;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
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
