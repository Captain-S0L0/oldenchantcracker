package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.thingmanagers.Enchantment;
import com.terriblefriends.oldenchcracker.thingmanagers.Item;
import com.terriblefriends.oldenchcracker.versions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(1);

    /*
    ZERO b1.9pre4 - fortune + silk
    ONE b1.9pre5 - wooden tool enchantability increased
    TWO 1.1 - bows can be enchanted, level formula change
    THREE 12w18a - internal server
    FOUR 12w22a (1.3) - 50 -> 30 level change
    FIVE 12w49a (1.4.6) - enchanted books added
    SIX 12w50a (1.4.6) - chestplates can receive thorns, shears can receive silk
    SEVEN 13w36a (1.7) - fishing rods can be enchanted, armor + bows can receive unbreaking
    EIGHT 13w37a (1.7) - changes to how enchantments are selected for books
    14w02a (1.8) - end of this tool
     */

    private static final String[] VERSION_STRINGS = new String[]{"b1.9pre4", "b1.9pre5 - 1.0", "1.1 - 12w17b (1.3)", "12w18a (1.3) - 12w21b (1.3)", "12w22a (1.3) - 12w48a (1.4.6)", "12w49a", "12w50a (1.4.6) - 1.6.4", "13w36a - 13w36b", "13w37a (1.7) - 1.7.10"};
    private static Version VERSION = new Zero();
    private static final HashMap<Enchantment, JComponent[]> MANIPULATOR_ENCHANTMENT_SELECTOR_COMPONENTS = new HashMap<>();
    private static int BOOKSHELVES = VERSION.getMaxBookShelves();
    private static final JTabbedPane TABS = new JTabbedPane();
    private static final JPanel HELP_PANEL = new JPanel();
    private static final JPanel SETUP_PANEL = new JPanel();
    private static final JPanel GALACTIC_CRACKER_PANEL = new JPanel();
    private static final JPanel LEVEL_CRACKER_PANEL = new JPanel();
    private static final JPanel EXTREMES_CRACKER_PANEL = new JPanel();
    private static final JPanel MANIPULATOR_PANEL = new JPanel();
    private static long RNG_SEED = -1;
    private static long POST_RNG_SEED = -1;
    private static int MAX_ADVANCES = 10000;
    private static boolean ADVANCED_ADVANCES = false;
    private static Future THREAD_TASK = null;

    public static void main(String[] args) {

        System.out.println("Preparing to do a little trolling...");

        JFrame frame = new JFrame("Pre 1.8 Enchantment Cracker");

        URL iconUrl = Main.class.getResource("/icon.png");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));

        frame.setSize(625,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createHelpPanel();
        createSetupPanel();
        TABS.addTab("Help", HELP_PANEL);
        TABS.addTab("Setup", SETUP_PANEL);

        frame.add(TABS);

        frame.setVisible(true);
    }

    private static void createHelpPanel() {
        HELP_PANEL.setName("Help Panel");
        HELP_PANEL.setLayout(null);
        HELP_PANEL.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel helpLabel;
        helpLabel = new JLabel("Welcome to Enchantment Cracking before Minecraft 1.8, by Captain_S0L0!");
        setPosition(helpLabel, 10, 10);
        HELP_PANEL.add(helpLabel);

        helpLabel = new JLabel("You can find a tutorial on its usage here.");
        setPosition(helpLabel, 10, 30);
        helpLabel.setForeground(Color.BLUE.darker());
        helpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://youtu.be/TvSVEXHQ4FA"));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        HELP_PANEL.add(helpLabel);

        helpLabel = new JLabel("The github page can be found here.");
        setPosition(helpLabel, 10, 50);
        helpLabel.setForeground(Color.BLUE.darker());
        helpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Captain-S0L0/oldenchantcracker"));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        HELP_PANEL.add(helpLabel);
    }

    private static void createSetupPanel() {
        SETUP_PANEL.setName("Setup Panel");
        SETUP_PANEL.setLayout(null);
        SETUP_PANEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel setupLabel;
        setupLabel = new JLabel("Version:");
        setPosition(setupLabel, 10,10);
        SETUP_PANEL.add(setupLabel);

        setupLabel = new JLabel("Shelves:");
        setPosition(setupLabel, 10,40);
        SETUP_PANEL.add(setupLabel);

        JTextField setupBookSelector = new JTextField(3);

        JButton setupFinalizeButton = new JButton("Start!");
        setPosition(setupFinalizeButton, 30, 75);
        SETUP_PANEL.add(setupFinalizeButton);

        JComboBox<String> setupVersionSelector = new JComboBox<>(VERSION_STRINGS);
        setupVersionSelector.addActionListener((event) -> {
            Object selection = setupVersionSelector.getSelectedItem();
            for (int i = 0; i < VERSION_STRINGS.length; i++) {
                if (Objects.equals(selection, VERSION_STRINGS[i])) {
                    switch (i) {
                        case 0:
                            VERSION = new Zero();
                            break;
                        case 1:
                            VERSION = new One();
                            break;
                        case 2:
                            VERSION = new Two();
                            break;
                        case 3:
                            VERSION = new Three();
                            break;
                        case 4:
                            VERSION = new Four();
                            break;
                        case 5:
                            VERSION = new Five();
                            break;
                        case 6:
                            VERSION = new Six();
                            break;
                        case 7:
                            VERSION = new Seven();
                            break;
                        case 8:
                            VERSION = new Eight();
                            break;

                    }
                    BOOKSHELVES = VERSION.getMaxBookShelves();
                    setupBookSelector.setText(String.valueOf(BOOKSHELVES));
                }
            }
        });
        setPosition(setupVersionSelector, 63,7);
        SETUP_PANEL.add(setupVersionSelector);

        setupFinalizeButton.addActionListener((event) -> {
            Set<Component> tabsToClose = new HashSet<>();
            for (Component c : TABS.getComponents()) {
                if (c.getName().equals("Galactic Cracker Panel") ||
                        c.getName().equals("Level Cracker Panel") ||
                        c.getName().equals("Manipulator Panel") ||
                        c.getName().equals("Extremes Cracker Panel")) {
                    tabsToClose.add(c);
                }
            }
            for (Component c : tabsToClose) {
                TABS.remove(c);
            }
            /*setupFinalizeButton.setEnabled(false);
            setupVersionSelector.setEnabled(false);
            setupBookSelector.setEditable(false);*/

            if (VERSION.getCrackType() == Version.CrackType.LEVELS) {
                createLevelCrackerPanel();
                TABS.addTab("Level Cracker", LEVEL_CRACKER_PANEL);
                TABS.setSelectedComponent(LEVEL_CRACKER_PANEL);
            }
            else if (VERSION.getCrackType() == Version.CrackType.EXTREMES) {
                createExtremesCrackerPanel();
                TABS.addTab("Extremes Cracker", EXTREMES_CRACKER_PANEL);
                TABS.setSelectedComponent(EXTREMES_CRACKER_PANEL);
            }
            else {
                createGalacticCrackerPanel();
                TABS.addTab("Galactic Cracker", GALACTIC_CRACKER_PANEL);
                TABS.setSelectedComponent(GALACTIC_CRACKER_PANEL);

                createExtremesCrackerPanel();
                TABS.addTab("Extremes Cracker", EXTREMES_CRACKER_PANEL);
            }

            createManipulatorPanel();
            TABS.addTab("Manipulator", MANIPULATOR_PANEL);
        });

        setupBookSelector.setText(String.valueOf(BOOKSHELVES));
        setupBookSelector.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int i;
                String s = setupBookSelector.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    i = Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                    return;
                }
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57 || i > VERSION.getMaxBookShelves()) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String text = setupBookSelector.getText();
                if (text == null || text.length() == 0) {
                    setupFinalizeButton.setEnabled(false);
                    BOOKSHELVES = -1;
                }
                else {
                    try {
                        BOOKSHELVES = Integer.parseInt(text);
                        setupFinalizeButton.setEnabled(true);
                    }
                    catch (NumberFormatException nfe) {
                        setupFinalizeButton.setEnabled(false);
                        e.consume();
                    }
                }
            }
        });
        setupBookSelector.setTransferHandler(null);
        setPosition(setupBookSelector, 70, 39);
        SETUP_PANEL.add(setupBookSelector);
    }

    private static void createGalacticCrackerPanel() {
        GALACTIC_CRACKER_PANEL.removeAll();
        GALACTIC_CRACKER_PANEL.setName("Galactic Cracker Panel");
        GALACTIC_CRACKER_PANEL.setLayout(null);
        GALACTIC_CRACKER_PANEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        URL galacticChartUrl = Main.class.getResource("/galacticChart.png");
        ImageIcon chartImage = new ImageIcon(galacticChartUrl);
        JLabel chartLabel = new JLabel(chartImage);
        setPosition(chartLabel, 400,25);
        GALACTIC_CRACKER_PANEL.add(chartLabel);


        JCheckBox[] fourWordCheck = new JCheckBox[3];
        JTextField[] slotLevels = new JTextField[3];
        JTextField[][] slotWords = new JTextField[3][4];

        JLabel galacticCrackerLabel;
        galacticCrackerLabel = new JLabel("4 words?");
        setPosition(galacticCrackerLabel, 10,5);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);
        galacticCrackerLabel = new JLabel("Word 1:");
        setPosition(galacticCrackerLabel, 80,5);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);
        galacticCrackerLabel = new JLabel("Word 2:");
        setPosition(galacticCrackerLabel, 140,5);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);
        galacticCrackerLabel = new JLabel("Word 3:");
        setPosition(galacticCrackerLabel, 200,5);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);
        galacticCrackerLabel = new JLabel("Word 4:");
        setPosition(galacticCrackerLabel, 260,5);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);
        galacticCrackerLabel = new JLabel("Level:");
        setPosition(galacticCrackerLabel, 325,5);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);
        galacticCrackerLabel = new JLabel("(THX Crafterdark)");
        setPosition(galacticCrackerLabel, 440, 490);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerLabel);

        JLabel galacticCrackerResultMessage = new JLabel("(you can manually set a seed here if you're cool)");
        galacticCrackerResultMessage.setBounds(25, 130, 400, galacticCrackerResultMessage.getPreferredSize().height);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerResultMessage);

        JTextField galacticCrackerResult = new JTextField(20);
        galacticCrackerResult.addActionListener((event) -> {
            if (galacticCrackerResult.isEditable()) {
                try {
                    RNG_SEED = Long.parseLong(galacticCrackerResult.getText());
                    galacticCrackerResultMessage.setText("Manually set seed!");
                    System.out.println("manually set seed to " + RNG_SEED);
                }
                catch (NumberFormatException nfe) {
                    galacticCrackerResultMessage.setText("Error! Enter an actual number!");
                }
            }
        });
        setPosition(galacticCrackerResult, 25, 155);
        GALACTIC_CRACKER_PANEL.add(galacticCrackerResult);

        JButton crackButton = new JButton("Crack RNG");

        crackButton.addActionListener((event) -> {
            if (THREAD_TASK == null) {
                galacticCrackerResultMessage.setText("Calculating...");
                crackButton.setEnabled(false);
                int[][] wordToInt = new int[3][4];
                int[] levelsToInt = new int[3];
                for (int slot = 0; slot < 3; slot++) {
                    fourWordCheck[slot].setEnabled(false);
                    slotLevels[slot].setEditable(false);
                    levelsToInt[slot] = Integer.parseInt(slotLevels[slot].getText());
                    for (int word = 0; word < 4; word++) {
                        if (slotWords[slot][word].getText().length() != 0) {
                            wordToInt[slot][word] = Integer.parseInt(slotWords[slot][word].getText());
                        } else {
                            wordToInt[slot][word] = -1;
                        }
                        slotWords[slot][word].setEditable(false);
                    }
                }


                GalacticCracker galacticCracker = new GalacticCracker(wordToInt, levelsToInt, BOOKSHELVES, VERSION);
                THREAD_TASK = THREAD_POOL.submit(galacticCracker);
                while (!THREAD_TASK.isDone()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    THREAD_TASK.get();

                    if (galacticCracker.getFailed()) {
                        throw new InterruptedException("Cracker Failed - No Valid Seeds Found");
                    }
                    else {
                        galacticCrackerResultMessage.setText("Cracked RNG!");

                        RNG_SEED = galacticCracker.getResult();

                        galacticCrackerResult.setText(String.valueOf(RNG_SEED));

                    }
                    THREAD_TASK = null;
                }
                catch (ExecutionException | InterruptedException e) {
                    THREAD_TASK = null;
                    System.out.println("Error cracking RNG!");
                    e.printStackTrace();
                    galacticCrackerResultMessage.setText("Error cracking RNG!");
                }
                crackButton.setEnabled(true);
                for (int slot = 0; slot < 3; slot++) {
                    fourWordCheck[slot].setEnabled(true);
                    slotLevels[slot].setEditable(true);
                    for (int word = 0; word < 4; word++) {
                        if (slotWords[slot][word].getText().length() != 0) {
                            slotWords[slot][word].setEditable(true);
                        }
                    }
                }

            }
            else {
                galacticCrackerResultMessage.setText("Error! Already cracking RNG!");
            }

        });
        crackButton.setEnabled(false);
        setPosition(crackButton, 20, 100);
        GALACTIC_CRACKER_PANEL.add(crackButton);

        for (int y = 0; y < 3; y++) {
            JCheckBox check = new JCheckBox();
            check.setBounds(25,25+(y*20),check.getPreferredSize().width, check.getPreferredSize().height);
            int checkFinalY = y;
            check.addActionListener((event) -> {
                boolean bool = check.isSelected();
                slotWords[checkFinalY][3].setEditable(bool);
                if (!bool) {
                    slotWords[checkFinalY][3].setText("");
                }
                boolean valid = true;
                for (int slot = 0; slot < 3; slot++) {
                    if (slotLevels[slot].getText().length() == 0) {
                        valid = false;
                    }
                    for (int wordC = 0; wordC < 4; wordC++) {
                        if (slotWords[slot][wordC].getText().length() == 0 && slotWords[slot][wordC].isEditable()) {
                            valid = false;
                        }
                    }
                }
                crackButton.setEnabled(valid);
            });
            fourWordCheck[y] = check;
            GALACTIC_CRACKER_PANEL.add(check);

            for (int x = 0; x < 5; x++) {
                JTextField word = new JTextField(3);
                word.setTransferHandler(null);
                int maxNumber;
                if (x == 4) {
                    maxNumber = VERSION.getMaxLevels();
                }
                else {
                    maxNumber = 54;
                }

                word.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        int i;
                        String s = word.getText();
                        if (s == null) {
                            s = "";
                        }
                        s+=e.getKeyChar();
                        try {
                            i = Integer.parseInt(s);
                        }
                        catch (NumberFormatException nfe) {
                            e.consume();
                            return;
                        }
                        if (e.getKeyChar() < 48 || e.getKeyChar() > 57 || i > maxNumber) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        boolean valid = true;
                        for (int slot = 0; slot < 3; slot++) {
                            if (slotLevels[slot].getText().length() == 0) {
                                valid = false;
                            }
                            for (int wordC = 0; wordC < 4; wordC++) {
                                String s = slotWords[slot][wordC].getText();
                                if ((s == null || s.length() == 0) && slotWords[slot][wordC].isEditable()) {
                                    valid = false;
                                }
                            }
                        }
                        crackButton.setEnabled(valid);
                    }
                });
                word.setBounds(85 + (x*60), 25+(y*20), word.getPreferredSize().width, word.getPreferredSize().height);
                GALACTIC_CRACKER_PANEL.add(word);
                if (x == 3) {
                    word.setEditable(false);
                }
                if (x == 4) {
                    slotLevels[y] = word;
                }
                else {
                    slotWords[y][x] = word;
                }
            }
        }

        JButton galacticCrackerClearButton = new JButton("Clear Data");
        setPosition(galacticCrackerClearButton, 125, 100);
        galacticCrackerClearButton.addActionListener((event) -> {
            for (JCheckBox box : fourWordCheck) {
                box.setSelected(false);
            }
            for (JTextField[] words : slotWords) {
                for (int i = 0; i < 4; i++) {
                    words[i].setText("");
                    if (i == 3) {
                        words[i].setEditable(false);
                    }
                }
            }
            for (JTextField field : slotLevels) {
                field.setText("");
            }
            crackButton.setEnabled(false);
            galacticCrackerResultMessage.setText("Reset!");
            RNG_SEED = -1;
        });
        GALACTIC_CRACKER_PANEL.add(galacticCrackerClearButton);
    }

    private static void createExtremesCrackerPanel() {
        EXTREMES_CRACKER_PANEL.removeAll();
        EXTREMES_CRACKER_PANEL.setName("Extremes Cracker Panel");
        EXTREMES_CRACKER_PANEL.setLayout(null);
        EXTREMES_CRACKER_PANEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label;
        label = new JLabel("Is low?");
        setPosition(label, 10,5);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("Advances:");
        setPosition(label, 80,5);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("Search Delay (ms)");
        setPosition(label, 150,5);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("Auto Search Type");
        setPosition(label, 150,45);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("Robot X");
        setPosition(label, 275,5);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("Robot Y");
        setPosition(label, 350,5);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("PRSC Attempts");
        setPosition(label, 410,5);
        EXTREMES_CRACKER_PANEL.add(label);
        label = new JLabel("Test Search");
        setPosition(label, 300,50);
        EXTREMES_CRACKER_PANEL.add(label);

        JTextField autoSearchDelayTextField = new JTextField(5);
        autoSearchDelayTextField.setTransferHandler(null);
        autoSearchDelayTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = autoSearchDelayTextField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                    return;
                }
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                    e.consume();
                }
            }
        });
        autoSearchDelayTextField.setText("200");
        autoSearchDelayTextField.setBounds(165, 25, autoSearchDelayTextField.getPreferredSize().width, autoSearchDelayTextField.getPreferredSize().height);
        EXTREMES_CRACKER_PANEL.add(autoSearchDelayTextField);

        JTextField autoSearchXTextField = new JTextField(5);
        autoSearchXTextField.setTransferHandler(null);
        autoSearchXTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = autoSearchDelayTextField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                    return;
                }
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                    e.consume();
                }
            }
        });
        autoSearchXTextField.setText("0");
        autoSearchXTextField.setBounds(270, 25, autoSearchXTextField.getPreferredSize().width, autoSearchXTextField.getPreferredSize().height);
        EXTREMES_CRACKER_PANEL.add(autoSearchXTextField);

        JTextField autoSearchYTextField = new JTextField(5);
        autoSearchYTextField.setTransferHandler(null);
        autoSearchYTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = autoSearchDelayTextField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                    return;
                }
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                    e.consume();
                }
            }
        });
        autoSearchYTextField.setText("0");
        autoSearchYTextField.setBounds(345, 25, autoSearchYTextField.getPreferredSize().width, autoSearchYTextField.getPreferredSize().height);
        EXTREMES_CRACKER_PANEL.add(autoSearchYTextField);

        JTextField autoSearchScreenshotsTextField = new JTextField(5);
        autoSearchScreenshotsTextField.setTransferHandler(null);
        autoSearchScreenshotsTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = autoSearchDelayTextField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                    return;
                }
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                    e.consume();
                }
            }
        });
        autoSearchScreenshotsTextField.setText("50");
        autoSearchScreenshotsTextField.setBounds(425, 25, autoSearchScreenshotsTextField.getPreferredSize().width, autoSearchScreenshotsTextField.getPreferredSize().height);
        EXTREMES_CRACKER_PANEL.add(autoSearchScreenshotsTextField);

        JCheckBox autoSearchTestMode = new JCheckBox();
        setPosition(autoSearchTestMode, 320, 70);
        EXTREMES_CRACKER_PANEL.add(autoSearchTestMode);

        JLabel resultMessage = new JLabel("(you can manually set a seed here if you're cool)");
        resultMessage.setBounds(25, 80+(VERSION.getExtremesNeeded()*20), 600, resultMessage.getPreferredSize().height);
        EXTREMES_CRACKER_PANEL.add(resultMessage);

        JTextField crackerResult = new JTextField(20);
        crackerResult.addActionListener((event) -> {
            if (crackerResult.isEditable()) {
                try {
                    RNG_SEED = Long.parseLong(crackerResult.getText());
                    resultMessage.setText("Manually set seed!");
                    System.out.println("manually set seed to " + RNG_SEED);
                }
                catch (NumberFormatException nfe) {
                    resultMessage.setText("Error! Enter an actual number!");
                }
            }
        });
        setPosition(crackerResult, 25, 105+(VERSION.getExtremesNeeded()*20));
        EXTREMES_CRACKER_PANEL.add(crackerResult);

        JCheckBox[] isLowCheckBoxes = new JCheckBox[VERSION.getExtremesNeeded()];
        JTextField[] advancesTextFields = new JTextField[VERSION.getExtremesNeeded()];

        JButton crackButton = new JButton("Crack RNG");

        crackButton.addActionListener((event) -> {
            if (THREAD_TASK == null) {
                resultMessage.setText("Calculating...");
                crackButton.setEnabled(false);
                int[] advances = new int[VERSION.getExtremesNeeded()];
                boolean[] isLow = new boolean[VERSION.getExtremesNeeded()];

                for (int slot = 0; slot < VERSION.getExtremesNeeded(); slot++) {
                    isLowCheckBoxes[slot].setEnabled(false);
                    advancesTextFields[slot].setEditable(false);
                    advances[slot] = Integer.parseInt(advancesTextFields[slot].getText());
                    isLow[slot] = isLowCheckBoxes[slot].isSelected();
                }

                ExtremesCracker cracker = new ExtremesCracker(advances, isLow, VERSION);
                THREAD_TASK = THREAD_POOL.submit(cracker);
                while (!THREAD_TASK.isDone()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    THREAD_TASK.get();

                    if (cracker.getFailed()) {
                        throw new InterruptedException("Cracker Failed - No Valid Seeds Found");
                    }
                    else {
                        resultMessage.setText("Cracked RNG!");

                        RNG_SEED = cracker.getResult();

                        crackerResult.setText(String.valueOf(RNG_SEED));

                    }
                    THREAD_TASK = null;
                }
                catch (ExecutionException | InterruptedException e) {
                    THREAD_TASK = null;
                    System.out.println("Error cracking RNG!");
                    e.printStackTrace();
                    resultMessage.setText("Error cracking RNG!");
                }
                crackButton.setEnabled(true);
                for (int slot = 0; slot < VERSION.getExtremesNeeded(); slot++) {
                    if (slot != 0) {
                        advancesTextFields[slot].setEditable(true);
                    }
                    isLowCheckBoxes[slot].setEnabled(true);

                }

            }
            else {
                resultMessage.setText("Error! Already cracking RNG!");
            }

        });
        crackButton.setEnabled(false);
        setPosition(crackButton, 20, 50+(VERSION.getExtremesNeeded()*20));
        EXTREMES_CRACKER_PANEL.add(crackButton);

        for (int y = 0; y < VERSION.getExtremesNeeded(); y++) {
            JTextField word = new JTextField(5);
            word.setTransferHandler(null);

            word.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    String s = autoSearchDelayTextField.getText();
                    if (s == null) {
                        s = "";
                    }
                    s+=e.getKeyChar();
                    try {
                        Integer.parseInt(s);
                    }
                    catch (NumberFormatException nfe) {
                        e.consume();
                        return;
                    }
                    if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                        e.consume();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    boolean valid = true;
                    for (int slot = 0; slot < VERSION.getExtremesNeeded(); slot++) {
                        String s = advancesTextFields[slot].getText();
                        if (s == null || s.length() == 0) {
                            valid = false;
                            break;
                        }
                    }
                    crackButton.setEnabled(valid);
                }
            });

            if (y == 0) {
                word.setText("0");
                word.setEditable(false);
            }

            word.setBounds(85, 25+(y*20), word.getPreferredSize().width, word.getPreferredSize().height);
            advancesTextFields[y] = word;
            EXTREMES_CRACKER_PANEL.add(word);

            JCheckBox check = new JCheckBox();
            check.setBounds(25,25+(y*20),check.getPreferredSize().width, check.getPreferredSize().height);
            isLowCheckBoxes[y] = check;
            EXTREMES_CRACKER_PANEL.add(check);
        }

        JButton clearButton = new JButton("Clear Data");
        setPosition(clearButton, 125, 50+(VERSION.getExtremesNeeded()*20));
        clearButton.addActionListener((event) -> {
            for (JCheckBox box : isLowCheckBoxes) {
                box.setSelected(false);
            }
            for (int i = 1; i < advancesTextFields.length; i++) {
                advancesTextFields[i].setText("");
            }
            crackButton.setEnabled(false);
            resultMessage.setText("Reset!");
            RNG_SEED = -1;
        });
        EXTREMES_CRACKER_PANEL.add(clearButton);

        String[] autoSearchTypes = new String[]{"ALT+PRINTSCREEN", "Robot"};

        JComboBox<String> autoSearchType = new JComboBox<>(autoSearchTypes);
        setPosition(autoSearchType, 150,67);
        EXTREMES_CRACKER_PANEL.add(autoSearchType);

        JButton enableSearcherButton = new JButton("Enable Auto Searcher");
        setPosition(enableSearcherButton, 230, 50+(VERSION.getExtremesNeeded()*20));
        enableSearcherButton.addActionListener((event) -> {
            try {
                String s = autoSearchDelayTextField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText("Error: Set a valid delay!");
                    return;
                }
                int delay = Integer.parseInt(s);
                s = autoSearchXTextField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText("Error: Set a valid x pos!");
                    return;
                }
                int x = Integer.parseInt(s);
                s = autoSearchYTextField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText("Error: Set a valid y pos!");
                    return;
                }
                int y = Integer.parseInt(s);
                s = autoSearchScreenshotsTextField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText("Error: Set a valid y pos!");
                    return;
                }
                int screenshots = Integer.parseInt(s);

                resultMessage.setText("Set MC window to default size and GUI size 2, place & hover over item in table slot");
                crackerResult.setText("F4 start, F8 pause, F12 force stop");

                boolean windows = autoSearchType.getSelectedItem().equals(autoSearchTypes[0]);

                new AutoExtremeSearcher(VERSION, delay, x, y, windows, screenshots, autoSearchTestMode.isSelected(), advancesTextFields, isLowCheckBoxes, crackButton, resultMessage);
            }
            catch (NumberFormatException e) {
                resultMessage.setText("Error: failed to parse integer option!");
                return;
            }
        });
        EXTREMES_CRACKER_PANEL.add(enableSearcherButton);
    }

    private static void createLevelCrackerPanel() {
        LEVEL_CRACKER_PANEL.removeAll();
        LEVEL_CRACKER_PANEL.setName("Level Cracker Panel");
        LEVEL_CRACKER_PANEL.setLayout(null);
        LEVEL_CRACKER_PANEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel levelCrackerLabel;
        levelCrackerLabel = new JLabel("Levels");
        setPosition(levelCrackerLabel, 10,5);
        LEVEL_CRACKER_PANEL.add(levelCrackerLabel);
        levelCrackerLabel = new JLabel("Slot 1:");
        setPosition(levelCrackerLabel, 75,5);
        LEVEL_CRACKER_PANEL.add(levelCrackerLabel);
        levelCrackerLabel = new JLabel("Slot 2:");
        setPosition(levelCrackerLabel, 135,5);
        LEVEL_CRACKER_PANEL.add(levelCrackerLabel);
        levelCrackerLabel = new JLabel("Slot 3:");
        setPosition(levelCrackerLabel, 195,5);
        LEVEL_CRACKER_PANEL.add(levelCrackerLabel);

        JLabel levelCrackerResultMessage = new JLabel("(you can manually set a seed here if you're cool)");
        levelCrackerResultMessage.setBounds(25, 275, 400, levelCrackerResultMessage.getPreferredSize().height);
        LEVEL_CRACKER_PANEL.add(levelCrackerResultMessage);

        JTextField levelCrackerResult = new JTextField(20);
        levelCrackerResult.addActionListener((event) -> {
            if (levelCrackerResult.isEditable()) {
                try {
                    RNG_SEED = Long.parseLong(levelCrackerResult.getText());
                    levelCrackerResultMessage.setText("Manually set seed!");
                    System.out.println("manually set seed to " + RNG_SEED);
                }
                catch (NumberFormatException nfe) {
                    levelCrackerResultMessage.setText("Error! Enter an actual number!");
                }
            }
        });
        setPosition(levelCrackerResult, 25, 300);
        LEVEL_CRACKER_PANEL.add(levelCrackerResult);

        JTextField[][] cycleLevels = new JTextField[10][3];

        JButton levelCrackButton = new JButton("Crack RNG");
        levelCrackButton.setEnabled(false);
        levelCrackButton.addActionListener((event) -> {
            if (THREAD_TASK == null) {
                levelCrackerResultMessage.setText("Calculating...");
                levelCrackButton.setEnabled(false);
                int[][] levelData = new int[10][3];
                for (int cycle = 0; cycle < 10; cycle++) {
                    for (int slot = 0; slot < 3; slot++) {
                        levelData[cycle][slot] = Integer.parseInt(cycleLevels[cycle][slot].getText());
                        cycleLevels[cycle][slot].setEditable(false);
                    }
                }

                LevelCracker levelCracker = new LevelCracker(levelData, VERSION);
                THREAD_TASK = THREAD_POOL.submit(levelCracker);
                while (!THREAD_TASK.isDone()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    THREAD_TASK.get();

                    if (levelCracker.getFailed()) {
                        throw new InterruptedException("Cracker Failed - No Valid Seeds Found");
                    }
                    else {
                        levelCrackerResultMessage.setText("Cracked RNG!");

                        RNG_SEED = levelCracker.getResult();

                        levelCrackerResult.setText(String.valueOf(RNG_SEED));

                    }
                    THREAD_TASK = null;
                }
                catch (ExecutionException | InterruptedException e) {
                    THREAD_TASK = null;
                    System.out.println("Error cracking RNG!");
                    e.printStackTrace();
                    levelCrackerResultMessage.setText("Error cracking RNG!");
                }
                levelCrackButton.setEnabled(true);
                for (int cycle = 0; cycle < 10; cycle++) {
                    for (int slot = 0; slot < 3; slot++) {
                        cycleLevels[cycle][slot].setEditable(true);
                    }
                }

            }
            else {
                levelCrackerResultMessage.setText("Error! Already cracking RNG!");
            }
        });
        setPosition(levelCrackButton, 25, 240);
        LEVEL_CRACKER_PANEL.add(levelCrackButton);

        for (int cycle = 0; cycle < 10; cycle++) {
            levelCrackerLabel = new JLabel("Cycle "+(cycle+1)+":");
            setPosition(levelCrackerLabel, 10, 25+(cycle*20));
            LEVEL_CRACKER_PANEL.add(levelCrackerLabel);

            for (int slot = 0; slot < 3; slot++) {
                JTextField word = new JTextField(3);
                word.setTransferHandler(null);

                word.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        String s = word.getText();
                        int i;
                        if (s == null) {
                            s = "";
                        }
                        s+=e.getKeyChar();
                        try {
                            i = Integer.parseInt(s);
                        }
                        catch (NumberFormatException nfe) {
                            e.consume();
                            return;
                        }
                        if (e.getKeyChar() < 48 || e.getKeyChar() > 57 || i > 8) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        boolean valid = true;
                        for (int cycle = 0; cycle < 10; cycle++) {
                            for (int slot = 0; slot < 3; slot++) {
                                String s = cycleLevels[cycle][slot].getText();
                                if (s == null || s.length() == 0) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (!valid) {
                                break;
                            }
                        }
                        levelCrackButton.setEnabled(valid);
                    }
                });
                setPosition(word, 75 + (slot*60), 25+(cycle*20));
                LEVEL_CRACKER_PANEL.add(word);
                cycleLevels[cycle][slot] = word;
            }
        }

        JButton levelCrackerClearButton = new JButton("Clear Data");
        setPosition(levelCrackerClearButton, 150, 240);
        levelCrackerClearButton.addActionListener((event) -> {
            for (int cycle = 0; cycle < 10; cycle++) {
                for (int slot = 0; slot < 3; slot++) {
                    cycleLevels[cycle][slot].setText("");
                }
            }
            levelCrackButton.setEnabled(false);
        });
        LEVEL_CRACKER_PANEL.add(levelCrackerClearButton);
    }

    private static void createManipulatorPanel() {
        MANIPULATOR_PANEL.removeAll();
        MANIPULATOR_PANEL.setName("Manipulator Panel");
        MANIPULATOR_PANEL.setLayout(null);
        MANIPULATOR_PANEL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel manipulatorLabel;
        manipulatorLabel = new JLabel("Item:");
        setPosition(manipulatorLabel, 10,10);
        MANIPULATOR_PANEL.add(manipulatorLabel);

        manipulatorLabel = new JLabel("Material:");
        setPosition(manipulatorLabel, 10,40);
        MANIPULATOR_PANEL.add(manipulatorLabel);

        JComboBox<String> manipulatorMaterialSelector = new JComboBox<>(VERSION.getMaterialStrings(VERSION.getItemStrings()[0]));
        setPosition(manipulatorMaterialSelector, 65,37);
        MANIPULATOR_PANEL.add(manipulatorMaterialSelector);

        JComboBox<String> manipulatorItemSelector = new JComboBox<>(VERSION.getItemStrings());
        manipulatorItemSelector.addActionListener((event) -> {
            String item = (String)manipulatorItemSelector.getSelectedItem();
            manipulatorMaterialSelector.removeAllItems();
            for (String s : VERSION.getMaterialStrings(item)) {
                manipulatorMaterialSelector.addItem(s);
            }

            generateManipulatorEnchantmentSelector(item);
        });
        setPosition(manipulatorItemSelector, 65,7);
        MANIPULATOR_PANEL.add(manipulatorItemSelector);

        manipulatorLabel = new JLabel("Exactly:");
        setPosition(manipulatorLabel, 10,70);
        MANIPULATOR_PANEL.add(manipulatorLabel);

        JCheckBox manipulatorExactly = new JCheckBox();
        setPosition(manipulatorExactly, 63, 67);
        MANIPULATOR_PANEL.add(manipulatorExactly);

        manipulatorLabel = new JLabel("Advanced Advances:");
        setPosition(manipulatorLabel, 90,70);
        MANIPULATOR_PANEL.add(manipulatorLabel);

        JCheckBox manipulatorConsiderAllCalls = new JCheckBox();
        setPosition(manipulatorConsiderAllCalls, 210, 67);
        MANIPULATOR_PANEL.add(manipulatorConsiderAllCalls);

        manipulatorConsiderAllCalls.addActionListener((event) -> {
            ADVANCED_ADVANCES = manipulatorConsiderAllCalls.isSelected();
        });

        manipulatorLabel = new JLabel("Max Advances:");
        setPosition(manipulatorLabel, 10,100);
        MANIPULATOR_PANEL.add(manipulatorLabel);

        JTextField setupMaxAdvancesField = new JTextField(5);

        JLabel manipulatorResultMessage = new JLabel("Doing nothing...");
        manipulatorResultMessage.setBounds(10, 200, 600, manipulatorResultMessage.getPreferredSize().height);
        MANIPULATOR_PANEL.add(manipulatorResultMessage);

        JButton manipulatorFinalizeEnchantButton = new JButton("Enchantment Done!");
        setPosition(manipulatorFinalizeEnchantButton, 50, 155);
        manipulatorFinalizeEnchantButton.setEnabled(false);
        manipulatorFinalizeEnchantButton.addActionListener((event) -> {
            manipulatorResultMessage.setText("Doing nothing...");
            System.out.println("Set RNG to post enchant seed "+ POST_RNG_SEED);
            RNG_SEED = POST_RNG_SEED;
            POST_RNG_SEED = -1;
            manipulatorFinalizeEnchantButton.setEnabled(false);
        });
        MANIPULATOR_PANEL.add(manipulatorFinalizeEnchantButton);

        JButton manipulatorFindEnchantButton = new JButton("Find Enchants");
        setPosition(manipulatorFindEnchantButton, 60, 130);
        MANIPULATOR_PANEL.add(manipulatorFindEnchantButton);
        manipulatorFindEnchantButton.addActionListener((event) -> {
            if (RNG_SEED == -1) {
                manipulatorResultMessage.setText("You haven't cracked the RNG seed yet!");
                return;
            }

            if (THREAD_TASK == null) {
                manipulatorResultMessage.setText("Calculating...");
                manipulatorFindEnchantButton.setEnabled(false);

                HashMap<Integer, EnchantData> desiredEnchants = new HashMap<>();
                for (Enchantment enchant : MANIPULATOR_ENCHANTMENT_SELECTOR_COMPONENTS.keySet()) {
                    JComponent[] components = MANIPULATOR_ENCHANTMENT_SELECTOR_COMPONENTS.get(enchant);
                    if (((JCheckBox)components[1]).isSelected()) {
                        int level = Integer.parseInt((String)((JComboBox<String>)components[2]).getSelectedItem());
                        desiredEnchants.put(enchant.getId(), new EnchantData(enchant, level));
                        System.out.println("search for enchantment "+enchant.getName()+" @ level "+level);
                    }
                }

                if (desiredEnchants.size() == 0) {
                    manipulatorResultMessage.setText("No enchants selected!");
                }
                else {

                    EnchantFinder finder = new EnchantFinder((String)manipulatorItemSelector.getSelectedItem(), (String)manipulatorMaterialSelector.getSelectedItem(), RNG_SEED, VERSION, BOOKSHELVES, MAX_ADVANCES, desiredEnchants, manipulatorExactly.isSelected(), ADVANCED_ADVANCES);
                    THREAD_TASK = THREAD_POOL.submit(finder);
                    while (!THREAD_TASK.isDone()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        THREAD_TASK.get();

                        if (finder.getFailed()) {
                            throw new InterruptedException("Finder Failed");
                        } else {
                            if (ADVANCED_ADVANCES) {
                                System.out.println(finder.getResultAdvances() + " raw advances required");
                                int swapDifferent = (finder.getResultAdvances() / 3);
                                int swap = 0;
                                int shift = 0;
                                if (finder.getResultAdvances() == 1) {
                                    swap += 1;
                                } else {
                                    int mod = finder.getResultAdvances() % 3;
                                    if (mod == 1) {
                                        swapDifferent -= 1;
                                        shift += 2;
                                    } else if (mod == 2) {
                                        shift += 1;
                                    }
                                }
                                int outIn = swapDifferent % 2 == 1 ? 1 : 0;
                                swapDifferent -= outIn;

                                manipulatorResultMessage.setText("Swap Different: " + swapDifferent + ", Shift Remove + Insert: " + shift + ", Swap Same: " + swap + ", OutIn: " + outIn + ", Slot: " + (finder.getResultSlot() + 1) + " " + Arrays.toString(finder.getResultLevels()));
                            }
                            else {
                                manipulatorResultMessage.setText("Advances: " + finder.getResultAdvances() + ", Slot: " + (finder.getResultSlot() + 1) + " " + Arrays.toString(finder.getResultLevels()));
                            }
                            POST_RNG_SEED = finder.getResultSeed();
                            manipulatorFinalizeEnchantButton.setEnabled(true);
                        }
                        THREAD_TASK = null;
                    } catch (ExecutionException | InterruptedException e) {
                        if (finder.getResultAdvances() == -9001) {
                            manipulatorResultMessage.setText("Failed to find enchant in "+ MAX_ADVANCES +" advances!");
                        }
                        else {
                            System.out.println("Error searching!");
                            e.printStackTrace();
                            manipulatorResultMessage.setText("Error searching!");
                        }

                        THREAD_TASK = null;
                    }
                }
                manipulatorFindEnchantButton.setEnabled(true);
            }
            else {
                manipulatorResultMessage.setText("Error! Already searching!");
            }
        });

        setupMaxAdvancesField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = setupMaxAdvancesField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                    return;
                }
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                    e.consume();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!setupMaxAdvancesField.isEditable()) {
                    return;
                }
                String text = setupMaxAdvancesField.getText();
                if (text.length() == 0) {
                    manipulatorFindEnchantButton.setEnabled(false);
                    MAX_ADVANCES = -1;
                }
                else {
                    manipulatorFindEnchantButton.setEnabled(true);
                    MAX_ADVANCES = Integer.parseInt(text);
                }
            }
        });
        setupMaxAdvancesField.setTransferHandler(null);
        setupMaxAdvancesField.setText(String.valueOf(MAX_ADVANCES));
        setPosition(setupMaxAdvancesField, 100, 99);
        MANIPULATOR_PANEL.add(setupMaxAdvancesField);

        generateManipulatorEnchantmentSelector((String)manipulatorItemSelector.getSelectedItem());
    }

    private static void setPosition(JComponent component, int x, int y) {
        //we make everything slightly wider to font changes with resolution don't screw everything up, and I'm not rewriting this to use a layout manager
        int width = component.getPreferredSize().width + 5;
        component.setBounds(x, y, width, component.getPreferredSize().height);
    }

    private static void generateManipulatorEnchantmentSelector(String itemName) {
        for (JComponent[] components : MANIPULATOR_ENCHANTMENT_SELECTOR_COMPONENTS.values()) {
            for (JComponent component : components) {
                MANIPULATOR_PANEL.remove(component);
            }
        }
        MANIPULATOR_ENCHANTMENT_SELECTOR_COMPONENTS.clear();

        Item item = VERSION.getItem(itemName);
        if (item != null) {
            JLabel label;
            JCheckBox enchantSelect;
            JComboBox<String> levelSelect;
            JComponent[] components;
            int y = 10;
            for (int id : item.getValidEnchants()) {
                components = new JComponent[3];
                Enchantment enchant = VERSION.getEnchant(id);
                label = new JLabel(enchant.getName());
                setPosition(label, 300, y);
                components[0] = label;
                MANIPULATOR_PANEL.add(label);
                enchantSelect = new JCheckBox();
                setPosition(enchantSelect, 450, y-2);
                components[1] = enchantSelect;
                MANIPULATOR_PANEL.add(enchantSelect);

                String[] levels = new String[0];
                switch (enchant.getMaxLevel()) {
                    case 1:
                        levels = new String[]{"1"};
                        break;
                    case 2:
                        levels = new String[]{"1", "2"};
                        break;
                    case 3:
                        levels = new String[]{"1", "2", "3"};
                        break;
                    case 4:
                        levels = new String[]{"1", "2", "3", "4"};
                        break;
                    case 5:
                        levels = new String[]{"1", "2", "3", "4", "5"};
                        break;
                }

                levelSelect = new JComboBox<>(levels);
                levelSelect.setSelectedIndex(levels.length-1);
                setPosition(levelSelect, 480, y-3);
                components[2] = levelSelect;
                MANIPULATOR_PANEL.add(levelSelect);
                y+= 30;

                MANIPULATOR_ENCHANTMENT_SELECTOR_COMPONENTS.put(enchant, components);
            }
            MANIPULATOR_PANEL.revalidate();
            MANIPULATOR_PANEL.repaint();
        }
    }
}