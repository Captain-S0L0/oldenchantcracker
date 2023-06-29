package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.versions.One;
import com.terriblefriends.oldenchcracker.versions.Two;
import com.terriblefriends.oldenchcracker.versions.Version;
import com.terriblefriends.oldenchcracker.versions.Zero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private static final int maxLevels = 50;
    /*
    b1.9pre4 - fortune + silk
    b1.9pre5 - wooden tool enchantability increased
    1.1 - bows can be enchanted, level formula change
    12w18a - internal server
    12w22a (1.3) - 50 -> 30 level change
    13w50a (1.4.6) - chestplates can receive thorns
    13w36a (1.7) - fishing rods can be enchanted, armor + bows can receive unbreaking
    14w02a (1.8) - end of this tool
     */

    private static final String[] versionStrings = new String[]{"b1.9pre4", "b1.9pre5 - 1.0", "1.1 - 12w17a (1.3)"};
    private static Version version = new Zero();
    private static final HashMap<EnchantmentList.Enchant, JComponent[]> manipulatorEnchantmentSelectorComponents = new HashMap<>();
    private static String item = "";
    private static String material = "";
    private static int bookshelves = version.getMaxBookShelves();
    private static final JTabbedPane tabs = new JTabbedPane();
    private static final JPanel setupPanel = new JPanel();
    private static final JPanel crackerPanel = new JPanel();
    private static final JPanel manipulatorPanel = new JPanel();
    private static long rngSeed = -1;
    private static long postRngSeed = -1;
    private static int maxAdvances = 10000;
    private static Future threadTask = null;

    public static void main(String[] args) {

        System.out.println("Hello world!");

        JFrame frame = new JFrame("Pre 1.3 Enchantment Cracker");

        URL iconUrl = Main.class.getResource("/icon.png");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));

        frame.setSize(625,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createSetupMenu();
        tabs.addTab("Setup", setupPanel);

        frame.add(tabs);

        frame.setVisible(true);
    }

    private static void createSetupMenu() {
        setupPanel.setLayout(null);
        setupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel setupLabel;
        setupLabel = new JLabel("Version:");
        setPosition(setupLabel, 10,10);
        setupPanel.add(setupLabel);

        setupLabel = new JLabel("Shelves:");
        setPosition(setupLabel, 10,40);
        setupPanel.add(setupLabel);

        setupLabel = new JLabel("Tool by @captain_s0l0, Discord");
        setPosition(setupLabel, 10,150);
        setupPanel.add(setupLabel);

        JTextField setupBookSelector = new JTextField(3);
        setupBookSelector.setText(String.valueOf(bookshelves));
        setPosition(setupBookSelector, 70, 39);
        setupPanel.add(setupBookSelector);

        JButton setupFinalizeButton = new JButton("Start!");
        setPosition(setupFinalizeButton, 30, 75);
        setupPanel.add(setupFinalizeButton);

        JComboBox<String> setupVersionSelector = new JComboBox<>(versionStrings);
        setupVersionSelector.addActionListener((event) -> {
            Object selection = setupVersionSelector.getSelectedItem();
            for (int i = 0; i < versionStrings.length; i++) {
                if (Objects.equals(selection, versionStrings[i])) {
                    Main.setVersion(i);
                    setupBookSelector.setText(String.valueOf(bookshelves));
                }
            }
        });
        setPosition(setupVersionSelector, 63,7);
        setupPanel.add(setupVersionSelector);

        setupFinalizeButton.addActionListener((event) -> {
            setupFinalizeButton.setEnabled(false);
            setupVersionSelector.setEnabled(false);
            setupBookSelector.setEditable(false);
            createCrackerPanel();
            createManipulatorMenu();
            tabs.addTab("RNG Cracker", crackerPanel);
            tabs.addTab("Manipulator", manipulatorPanel);
            tabs.setSelectedComponent(crackerPanel);
        });

        setupBookSelector.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57 || Integer.parseInt(setupBookSelector.getText()+e.getKeyChar()) > version.getMaxBookShelves()) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!setupBookSelector.isEditable()) {
                    return;
                }
                String text = setupBookSelector.getText();
                if (text.length() == 0) {
                    setupFinalizeButton.setEnabled(false);
                    Main.setBookshelves(-1);
                }
                else {
                    setupFinalizeButton.setEnabled(true);
                    Main.setBookshelves(Integer.parseInt(text));
                }
            }
        });
    }

    private static void createCrackerPanel() {
        crackerPanel.setLayout(null);
        crackerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        URL galacticChartUrl = Main.class.getResource("/galacticChart.png");
        ImageIcon chartImage = new ImageIcon(galacticChartUrl);
        JLabel chartLabel = new JLabel(chartImage);
        setPosition(chartLabel, 400,25);
        crackerPanel.add(chartLabel);


        JCheckBox[] fourWordCheck = new JCheckBox[3];
        JTextField[] slot1Words = new JTextField[4];
        JTextField[] slot2Words = new JTextField[4];
        JTextField[] slot3Words = new JTextField[4];
        JTextField[] slotLevels = new JTextField[3];
        JTextField[][] slotWords = new JTextField[3][];
        slotWords[0] = slot1Words;
        slotWords[1] = slot2Words;
        slotWords[2] = slot3Words;

        JLabel crackerLabel;
        crackerLabel = new JLabel("4 words?");
        setPosition(crackerLabel, 10,5);
        crackerPanel.add(crackerLabel);
        crackerLabel = new JLabel("Word 1:");
        setPosition(crackerLabel, 80,5);
        crackerPanel.add(crackerLabel);
        crackerLabel = new JLabel("Word 2:");
        setPosition(crackerLabel, 140,5);
        crackerPanel.add(crackerLabel);
        crackerLabel = new JLabel("Word 3:");
        setPosition(crackerLabel, 200,5);
        crackerPanel.add(crackerLabel);
        crackerLabel = new JLabel("Word 4:");
        setPosition(crackerLabel, 260,5);
        crackerPanel.add(crackerLabel);
        crackerLabel = new JLabel("Level:");
        setPosition(crackerLabel, 325,5);
        crackerPanel.add(crackerLabel);
        crackerLabel = new JLabel("(THX @crafterdark, Discord)");
        setPosition(crackerLabel, 420, 490);
        crackerPanel.add(crackerLabel);

        JLabel crackerResultMessage = new JLabel("(you can manually set a seed here if you're cool)");
        crackerResultMessage.setBounds(25, 130, 400, crackerResultMessage.getPreferredSize().height);
        crackerPanel.add(crackerResultMessage);

        JTextField crackerResult = new JTextField(20);
        crackerResult.addActionListener((event) -> {
            if (crackerResult.isEditable()) {
                rngSeed = Long.parseLong(crackerResult.getText());
                crackerResultMessage.setText("Manually set seed!");
                System.out.println("manually set seed to " + rngSeed);
            }
        });
        setPosition(crackerResult, 25, 155);
        crackerPanel.add(crackerResult);

        JButton crackButton = new JButton("Crack RNG");

        crackButton.addActionListener((event) -> {

            if (threadTask == null) {
                crackerResultMessage.setText("Calculating...");
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


                Cracker cracker = new Cracker(wordToInt, levelsToInt, bookshelves, version);
                threadTask = threadPool.submit(cracker);
                while (!threadTask.isDone()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    threadTask.get();

                    if (cracker.getFailed()) {
                        throw new InterruptedException("Cracker Failed - No Valid Seeds Found");
                    }
                    else {
                        crackerResultMessage.setText("Cracked RNG!");

                        rngSeed = cracker.getResult();

                        crackerResult.setText(String.valueOf(rngSeed));

                    }
                    threadTask = null;
                }
                catch (ExecutionException | InterruptedException e) {
                    threadTask = null;
                    System.out.println("Error cracking RNG!");
                    e.printStackTrace();
                    crackerResultMessage.setText("Error cracking RNG!");
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
                crackerResultMessage.setText("Error! Already cracking RNG!");
            }

        });
        crackButton.setEnabled(false);
        crackButton.setBounds(20, 100, crackButton.getPreferredSize().width, crackButton.getPreferredSize().height);
        crackerPanel.add(crackButton);

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
            crackerPanel.add(check);

            for (int x = 0; x < 5; x++) {
                JTextField word = new JTextField(3);
                word.setTransferHandler(null);
                int maxNumber;
                if (x == 4) {
                    maxNumber = maxLevels;
                }
                else {
                    maxNumber = 54;
                }

                word.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (e.getKeyChar() < 48 || e.getKeyChar() > 57 || Integer.parseInt(word.getText()+e.getKeyChar()) > maxNumber) {
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
                                if (slotWords[slot][wordC].getText().length() == 0 && slotWords[slot][wordC].isEditable()) {
                                    valid = false;
                                }
                            }
                        }
                        crackButton.setEnabled(valid);
                    }
                });
                word.setBounds(85 + (x*60), 25+(y*20), word.getPreferredSize().width, word.getPreferredSize().height);
                crackerPanel.add(word);
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

        JButton crackerClearButton = new JButton("Clear Data");
        setPosition(crackerClearButton, 125, 100);
        crackerClearButton.addActionListener((event) -> {
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
            crackerResultMessage.setText("Reset!");
            rngSeed = -1;
        });
        crackerPanel.add(crackerClearButton);
    }

    private static void createManipulatorMenu() {
        item = version.getItems()[0];
        material = version.getMaterials()[0];

        manipulatorPanel.setLayout(null);
        manipulatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel manipulatorLabel;
        manipulatorLabel = new JLabel("Item:");
        setPosition(manipulatorLabel, 10,10);
        manipulatorPanel.add(manipulatorLabel);

        JComboBox<String> manipulatorItemSelector = new JComboBox<>(version.getItems());
        manipulatorItemSelector.addActionListener((event) -> {
            Object selection = manipulatorItemSelector.getSelectedItem();
            for (int i = 0; i < version.getItems().length; i++) {
                if (Objects.equals(selection, version.getItems()[i])) {
                    Main.setItem(version.getItems()[i]);
                    generateManipulatorEnchantmentSelector();
                    break;
                }
            }
        });
        setPosition(manipulatorItemSelector, 65,7);
        manipulatorPanel.add(manipulatorItemSelector);

        manipulatorLabel = new JLabel("Material:");
        setPosition(manipulatorLabel, 10,40);
        manipulatorPanel.add(manipulatorLabel);

        JComboBox<String> manipulatorMaterialSelector = new JComboBox<>(version.getMaterials());
        manipulatorMaterialSelector.addActionListener((event) -> {
            Object selection = manipulatorMaterialSelector.getSelectedItem();
            for (int i = 0; i < version.getMaterials().length; i++) {
                if (Objects.equals(selection, version.getMaterials()[i])) {
                    Main.setMaterial(version.getMaterials()[i]);
                    generateManipulatorEnchantmentSelector();
                    break;
                }
            }
        });
        setPosition(manipulatorMaterialSelector, 65,37);
        manipulatorPanel.add(manipulatorMaterialSelector);

        manipulatorLabel = new JLabel("Exactly:");
        setPosition(manipulatorLabel, 10,70);
        manipulatorPanel.add(manipulatorLabel);

        JCheckBox manipulatorExactly = new JCheckBox();
        setPosition(manipulatorExactly, 63, 67);
        manipulatorPanel.add(manipulatorExactly);

        manipulatorLabel = new JLabel("Max Advances:");
        setPosition(manipulatorLabel, 90,70);
        manipulatorPanel.add(manipulatorLabel);

        JTextField setupMaxAdvancesField = new JTextField(5);
        setupMaxAdvancesField.setText(String.valueOf(maxAdvances));
        setPosition(setupMaxAdvancesField, 190, 69);
        manipulatorPanel.add(setupMaxAdvancesField);

        JLabel manipulatorResultMessage = new JLabel("Doing nothing...");
        manipulatorResultMessage.setBounds(10, 170, 300, manipulatorResultMessage.getPreferredSize().height);
        manipulatorPanel.add(manipulatorResultMessage);

        JButton manipulatorFinalizeEnchantButton = new JButton("Enchantment Done!");
        setPosition(manipulatorFinalizeEnchantButton, 50, 125);
        manipulatorFinalizeEnchantButton.setEnabled(false);
        manipulatorFinalizeEnchantButton.addActionListener((event) -> {
            manipulatorResultMessage.setText("Doing nothing...");
            System.out.println(postRngSeed);
            rngSeed = postRngSeed;
            postRngSeed = -1;
            manipulatorFinalizeEnchantButton.setEnabled(false);
        });
        manipulatorPanel.add(manipulatorFinalizeEnchantButton);

        JButton manipulatorFindEnchantButton = new JButton("Find Enchants");
        setPosition(manipulatorFindEnchantButton, 60, 100);
        manipulatorPanel.add(manipulatorFindEnchantButton);
        manipulatorFindEnchantButton.addActionListener((event) -> {
            if (rngSeed == -1) {
                manipulatorResultMessage.setText("You haven't cracked the RNG seed yet!");
                return;
            }

            if (threadTask == null) {
                manipulatorResultMessage.setText("Calculating...");
                manipulatorFindEnchantButton.setEnabled(false);

                HashMap<Integer, EnchantData> desiredEnchants = new HashMap<>();
                for (EnchantmentList.Enchant enchant : manipulatorEnchantmentSelectorComponents.keySet()) {
                    JComponent[] components = manipulatorEnchantmentSelectorComponents.get(enchant);
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

                    EnchantFinder finder = new EnchantFinder(item, material, rngSeed, version, bookshelves, desiredEnchants, manipulatorExactly.isSelected());
                    threadTask = threadPool.submit(finder);
                    while (!threadTask.isDone()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        threadTask.get();

                        if (finder.getFailed()) {
                            throw new InterruptedException("Finder Failed");
                        } else {
                            manipulatorResultMessage.setText("Found candidate! Advances: " + finder.getResultAdvances() + ", Slot: " + (finder.getResultSlot()+1) + " "+Arrays.toString(finder.getResultLevels()));
                            postRngSeed = finder.getResultSeed();
                            manipulatorFinalizeEnchantButton.setEnabled(true);
                        }
                        threadTask = null;
                    } catch (ExecutionException | InterruptedException e) {
                        if (finder.getResultAdvances() == -9001) {
                            manipulatorResultMessage.setText("Failed to find enchant in "+maxAdvances+" advances!");
                        }
                        else {
                            System.out.println("Error searching!");
                            e.printStackTrace();
                            manipulatorResultMessage.setText("Error searching!");
                        }

                        threadTask = null;
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
                if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
                    e.consume();
                }
                else {
                    try {
                        if (Integer.parseInt(setupMaxAdvancesField.getText() + e.getKeyChar()) < 0) {
                            e.consume();
                        }
                    }
                    catch (NumberFormatException exception) {
                        e.consume();
                    }
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
                    Main.setMaxAdvances(-1);
                }
                else {
                    manipulatorFindEnchantButton.setEnabled(true);
                    Main.setMaxAdvances(Integer.parseInt(text));
                }
            }
        });

        generateManipulatorEnchantmentSelector();
    }

    private static void setPosition(JComponent component, int x, int y) {
        component.setBounds(x, y, component.getPreferredSize().width, component.getPreferredSize().height);
    }

    private static void setVersion(int i) {
        switch (i) {
            case 0:
                version = new Zero();
                break;
            case 1:
                version = new One();
                break;
            case 2:
                version = new Two();


        }
        item = version.getItems()[0];
        material = version.getMaterials()[0];
        bookshelves = version.getMaxBookShelves();
    }

    private static void setItem(String s) {
        item = s;
    }

    private static void setMaterial(String s) {
        material = s;
    }

    private static void setBookshelves(int i) {
        bookshelves = i;
    }

    private static void setMaxAdvances(int i) {
        maxAdvances = i;
    }

    public static int getMaxAdvances() {
        return maxAdvances;
    }



    private static void generateManipulatorEnchantmentSelector() {
        for (JComponent[] components : manipulatorEnchantmentSelectorComponents.values()) {
            for (JComponent component : components) {
                manipulatorPanel.remove(component);
            }
        }
        manipulatorEnchantmentSelectorComponents.clear();

        ItemList.Item item = version.getItem(Main.item);
        if (item != null) {
            JLabel label;
            JCheckBox enchantSelect;
            JComboBox<String> levelSelect;
            JComponent[] components;
            int y = 10;
            for (int id : item.getValidEnchants()) {
                components = new JComponent[3];
                EnchantmentList.Enchant enchant = version.getEnchant(id);
                label = new JLabel(enchant.getName());
                setPosition(label, 300, y);
                components[0] = label;
                manipulatorPanel.add(label);
                enchantSelect = new JCheckBox();
                setPosition(enchantSelect, 450, y-2);
                components[1] = enchantSelect;
                manipulatorPanel.add(enchantSelect);

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
                setPosition(levelSelect, 475, y-3);
                components[2] = levelSelect;
                manipulatorPanel.add(levelSelect);
                y+= 30;

                manipulatorEnchantmentSelectorComponents.put(enchant, components);
            }
            manipulatorPanel.revalidate();
            manipulatorPanel.repaint();
        }
    }
}