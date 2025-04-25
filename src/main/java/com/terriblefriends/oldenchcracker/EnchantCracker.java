package com.terriblefriends.oldenchcracker;

import com.terriblefriends.oldenchcracker.cracker.*;
import com.terriblefriends.oldenchcracker.thingmanager.*;
import com.terriblefriends.oldenchcracker.version.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class EnchantCracker {
    private static final int EMPTY_VALUE = -1;
    private final JTabbedPane tabs = new JTabbedPane();
    private final AutoExtremeSearcher extremeSearcher = new AutoExtremeSearcher();
    private final List<EnchantData> desiredEnchants = new ArrayList<>();
    private Version version = null;
    private int bookshelves = EMPTY_VALUE;
    private long rngSeed = SeedResults.SEED_RESULT_UNSET.getValue();
    private long postManipulateRngSeed = SeedResults.SEED_RESULT_UNSET.getValue();
    private int maxAdvances = 10000;


    public void init() {
        JFrame frame = new JFrame(EnchantCrackerI18n.translate("program.name"));
        URL iconUrl = Main.class.getResource("/graphic/icon.png");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));

        frame.setSize(600,600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                EnchantCracker.this.extremeSearcher.exit(EnchantCrackerI18n.translate("panel.extremes.search.terminated"));
                EnchantCracker.this.extremeSearcher.close();
                System.exit(0);
            }
        });

        frame.add(this.tabs);

        this.extremeSearcher.init();

        if (version != null) {
            createHelpPanel();
            createSetupPanel();
            this.createVersionPanels();
        }
        else {
            this.version = new Zero();
            this.bookshelves = this.version.getMaxBookShelves();

            createHelpPanel();
            createSetupPanel();
        }

        frame.setVisible(true);
    }

    public void setRngSeed(long seed) {
        this.rngSeed = seed;
    }

    public void setMaxAdvances(int advances) {
        this.maxAdvances = advances;
    }

    public void setVersion(Version version) {
        this.version = version;
        this.bookshelves = version.getMaxBookShelves();
    }

    public void setBookshelves(int bookshelves) {
        if (bookshelves <= this.version.getMaxBookShelves()) {
            this.bookshelves = bookshelves;
        }
    }

    public void createVersionPanels() {
        for (int tab = this.tabs.getTabCount() - 1; tab > 1; tab--) {
            this.tabs.remove(tab);
        }

        if (this.version.getCrackType() == Version.CrackType.LEVELS) {
            createLevelCrackerPanel();
        }
        else if (this.version.getCrackType() == Version.CrackType.EXTREMES) {
            createExtremesCrackerPanel();
        }
        else if (this.version.getCrackType() == Version.CrackType.GALACTIC) {
            createGalacticCrackerPanel();

            if (this.extremeSearcher.getInitialized()) {
                createExtremesCrackerPanel();
            }
        }

        createManipulatorPanel();
    }

    private void createHelpPanel() {
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel helpLabel;
        helpLabel = new JLabel(EnchantCrackerI18n.translate("panel.help.welcome"));
        helpPanel.add(helpLabel);

        helpLabel = new JLabel(EnchantCrackerI18n.translate("panel.help.tutorial"));
        helpLabel.setForeground(Color.BLUE.darker());
        helpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(EnchantCrackerI18n.translate("panel.help.tutorial.link")));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        helpPanel.add(helpLabel);

        helpLabel = new JLabel(EnchantCrackerI18n.translate("panel.help.github"));
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
        helpPanel.add(helpLabel);

        this.tabs.addTab(EnchantCrackerI18n.translate("panel.help"), helpPanel);
    }

    private void createSetupPanel() {
        JPanel setupPanel = new JPanel();
        JLabel setupLabel;

        GroupLayout layout = new GroupLayout(setupPanel);
        setupPanel.setLayout(layout);
        setupPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Version Select
        JPanel versionSelectPanel = new JPanel();

        setupLabel = new JLabel(EnchantCrackerI18n.translate("panel.setup.version"));
        versionSelectPanel.add(setupLabel);
        String[] versionStrings = new String[]{
                EnchantCrackerI18n.translate("version.0"),
                EnchantCrackerI18n.translate("version.1"),
                EnchantCrackerI18n.translate("version.2"),
                EnchantCrackerI18n.translate("version.3"),
                EnchantCrackerI18n.translate("version.4"),
                EnchantCrackerI18n.translate("version.5"),
                EnchantCrackerI18n.translate("version.6"),
                EnchantCrackerI18n.translate("version.7"),
                EnchantCrackerI18n.translate("version.8"),
                EnchantCrackerI18n.translate("version.9")
        };
        JComboBox<String> setupVersionSelector = new JComboBox<>(versionStrings);
        versionSelectPanel.add(setupVersionSelector);

        versionSelectPanel.setMaximumSize(versionSelectPanel.getPreferredSize());
        // End Version Select

        // Book Select
        JPanel bookSelectPanel = new JPanel();

        setupLabel = new JLabel(EnchantCrackerI18n.translate("panel.setup.shelves"));
        bookSelectPanel.add(setupLabel);

        String[] setupBookSelectorStrings = new String[this.version.getMaxBookShelves() + 1];
        for (int i = 0; i <= this.version.getMaxBookShelves(); i++) {
            setupBookSelectorStrings[i] = Integer.toString(this.version.getMaxBookShelves() - i);
        }
        JComboBox<String> setupBookSelector = new JComboBox<>(setupBookSelectorStrings);
        bookSelectPanel.add(setupBookSelector);

        /*JTextField setupBookSelector = new JTextField(3);
        setupBookSelector.setTransferHandler(null);
        setupBookSelector.setText(String.valueOf(this.bookshelves));
        bookSelectPanel.add(setupBookSelector);*/

        bookSelectPanel.setMaximumSize(bookSelectPanel.getPreferredSize());
        //End Book Select

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(versionSelectPanel)
                                .addComponent(bookSelectPanel)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(versionSelectPanel)
                        .addComponent(bookSelectPanel)
        );

        setupVersionSelector.addActionListener((event) -> {
            Object selection = setupVersionSelector.getSelectedItem();
            for (int i = 0; i < versionStrings.length; i++) {
                if (Objects.equals(selection, versionStrings[i])) {
                    switch (i) {
                        case 0:
                            this.version = new Zero();
                            break;
                        case 1:
                            this.version = new One();
                            break;
                        case 2:
                            this.version = new Two();
                            break;
                        case 3:
                            this.version = new Three();
                            break;
                        case 4:
                            this.version = new Four();
                            break;
                        case 5:
                            this.version = new Five();
                            break;
                        case 6:
                            this.version = new Six();
                            break;
                        case 7:
                            this.version = new Seven();
                            break;
                        case 8:
                            this.version = new Eight();
                            break;
                        case 9:
                            this.version = new Nine();
                            break;

                    }
                    this.bookshelves = this.version.getMaxBookShelves();

                    setupBookSelector.removeAllItems();
                    for (int j = 0; j <= this.version.getMaxBookShelves(); j++) {
                        setupBookSelector.addItem(Integer.toString(this.version.getMaxBookShelves() - j));
                    }
                    setupBookSelector.setSelectedIndex(0);

                    this.createVersionPanels();

                    break;
                }
            }
        });

        setupBookSelector.addActionListener((event) -> {
            String s = (String)setupBookSelector.getSelectedItem();
            if (s != null) {
                this.bookshelves = Integer.parseInt(s);
            }
        });

        this.tabs.addTab(EnchantCrackerI18n.translate("panel.setup"), setupPanel);
    }

    private void createGalacticCrackerPanel() {
        JPanel crackerPanel = new JPanel();
        JLabel crackerLabel;

        crackerPanel.setFocusCycleRoot(true);
        GroupLayout layout = new GroupLayout(crackerPanel);
        crackerPanel.setLayout(layout);

        // Galactic Chart
        JPanel galacticPanel = new JPanel();
        galacticPanel.setLayout(new BoxLayout(galacticPanel, BoxLayout.Y_AXIS));

        URL galacticChartUrl = Main.class.getResource("/graphic/galacticChart.png");
        if (galacticChartUrl == null) {
            throw new RuntimeException("Failed to get resource /graphic/galacticChart.png!");
        }
        crackerLabel = new JLabel(new ImageIcon(galacticChartUrl));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        galacticPanel.add(crackerLabel);
        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.galactic.thanks"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        galacticPanel.add(crackerLabel);

        galacticPanel.setMaximumSize(galacticPanel.getPreferredSize());
        // End Galactic Chart

        // 4 Word Checks
        JPanel fourWordCheckPanel = new JPanel();
        fourWordCheckPanel.setLayout(new BoxLayout(fourWordCheckPanel, BoxLayout.Y_AXIS));

        JCheckBox[] fourWordChecks = new JCheckBox[3];
        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.galactic.fourwords"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fourWordCheckPanel.add(crackerLabel);

        for (int slot = 0; slot < 3; slot++) {
            JCheckBox box = new JCheckBox();
            box.setAlignmentX(Component.CENTER_ALIGNMENT);
            fourWordCheckPanel.add(box);
            fourWordChecks[slot] = box;
        }

        fourWordCheckPanel.setMaximumSize(fourWordCheckPanel.getPreferredSize());
        // End 4 Word Checks

        // Words
        JTextField[][] galacticWordFields = new JTextField[3][4];
        JPanel[] wordPanels = new JPanel[4];

        for (int word = 0; word < 4; word++) {
            JPanel wordPanel = new JPanel();
            wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));

            crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.galactic.word."+word));
            crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            wordPanel.add(crackerLabel);

            wordPanel.add(Box.createRigidArea(new Dimension(0,1)));

            for (int slot = 0; slot < 3; slot++) {
                JTextField textField = new JTextField(3);
                textField.setTransferHandler(null);
                if (word == 3) {
                    textField.setEditable(false);
                }
                textField.setAlignmentX(Component.CENTER_ALIGNMENT);
                wordPanel.add(textField);
                wordPanel.add(Box.createRigidArea(new Dimension(0,1)));
                galacticWordFields[slot][word] = textField;
            }

            wordPanel.setMaximumSize(wordPanel.getPreferredSize());
            wordPanels[word] = wordPanel;
        }
        // End Words

        // Levels
        JTextField[] slotLevelFields = new JTextField[3];

        JPanel levelPanel = new JPanel();
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.galactic.levels"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelPanel.add(crackerLabel);

        levelPanel.add(Box.createRigidArea(new Dimension(0,1)));

        for (int slot = 0; slot < 3; slot++) {
            JTextField textField = new JTextField(3);
            textField.setTransferHandler(null);
            textField.setAlignmentX(Component.CENTER_ALIGNMENT);
            levelPanel.add(textField);
            levelPanel.add(Box.createRigidArea(new Dimension(0,1)));
            slotLevelFields[slot] = textField;
        }

        levelPanel.setMaximumSize(levelPanel.getPreferredSize());
        // End Levels

        JLabel crackerResultMessage = new JLabel(EnchantCrackerI18n.translate("cracker.waiting"));

        JButton clearButton = new JButton(EnchantCrackerI18n.translate("panel.cracker.clear"));

        JButton crackButton = new JButton(EnchantCrackerI18n.translate("panel.cracker.crack"));
        crackButton.setEnabled(false);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(fourWordCheckPanel)
                                        .addComponent(wordPanels[0])
                                        .addComponent(wordPanels[1])
                                        .addComponent(wordPanels[2])
                                        .addComponent(wordPanels[3])
                                        .addComponent(levelPanel)
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(crackButton)
                                        .addComponent(clearButton)
                                )
                                .addComponent(crackerResultMessage)
                        )
                        .addComponent(galacticPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup()
                                                .addComponent(fourWordCheckPanel)
                                                .addComponent(wordPanels[0])
                                                .addComponent(wordPanels[1])
                                                .addComponent(wordPanels[2])
                                                .addComponent(wordPanels[3])
                                                .addComponent(levelPanel)
                                        )
                                        .addGroup(layout.createParallelGroup()
                                                .addComponent(crackButton)
                                                .addComponent(clearButton)
                                        )
                                        .addComponent(crackerResultMessage)
                                )
                                .addComponent(galacticPanel)
                        )
        );

        crackButton.addActionListener((event) -> {
            int[][] wordToInt = new int[3][4];
            int[] levelsToInt = new int[3];
            for (int slot = 0; slot < 3; slot++) {
                fourWordChecks[slot].setEnabled(false);
                slotLevelFields[slot].setEditable(false);
                levelsToInt[slot] = Integer.parseInt(slotLevelFields[slot].getText());
                for (int word = 0; word < 4; word++) {
                    if (galacticWordFields[slot][word].getText().length() != 0) {
                        wordToInt[slot][word] = Integer.parseInt(galacticWordFields[slot][word].getText());
                    } else {
                        wordToInt[slot][word] = EMPTY_VALUE;
                    }
                    galacticWordFields[slot][word].setEditable(false);
                }
            }

            GalacticCracker galacticCracker = new GalacticCracker(wordToInt, levelsToInt, this.bookshelves, this.version);

            crackerResultMessage.setText(EnchantCrackerI18n.translate("cracker.active"));
            crackButton.setEnabled(false);
            clearButton.setEnabled(false);

            Thread waitThread = new Thread(() -> {
                try {
                    galacticCracker.run();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    crackerResultMessage.setText(EnchantCrackerI18n.translate("cracker.error.unknown"));
                }

                if (galacticCracker.getFailed()) {
                    crackerResultMessage.setText(EnchantCrackerI18n.translate("cracker.error.noseeds"));
                } else {
                    crackerResultMessage.setText(EnchantCrackerI18n.translate("cracker.success"));

                    this.rngSeed = galacticCracker.getResult();

                    System.out.printf(EnchantCrackerI18n.translate("cracker.seed") + "%n", galacticCracker.getResult());
                }

                crackButton.setEnabled(true);
                clearButton.setEnabled(true);

                for (int slot = 0; slot < 3; slot++) {
                    fourWordChecks[slot].setEnabled(true);
                    slotLevelFields[slot].setEditable(true);
                    for (int word = 0; word < 4; word++) {
                        if (word != 3 || fourWordChecks[slot].isSelected()) {
                            galacticWordFields[slot][word].setEditable(true);
                        }
                    }
                }
            });

            waitThread.start();

        });

        Runnable checkCrackButton = () -> {
            boolean valid = true;
            for (int slot = 0; slot < 3; slot++) {
                if (slotLevelFields[slot].getText().length() == 0) {
                    valid = false;
                }
                for (int wordC = 0; wordC < 4; wordC++) {
                    if (galacticWordFields[slot][wordC].getText().length() == 0 && galacticWordFields[slot][wordC].isEditable()) {
                        valid = false;
                    }
                }
            }
            crackButton.setEnabled(valid);
        };

        for (int check = 0; check < fourWordChecks.length; check++) {
            JCheckBox box = fourWordChecks[check];
            int checkIndex = check;
            box.addActionListener((event) -> {
                boolean enabled = box.isSelected();
                galacticWordFields[checkIndex][3].setEditable(enabled);
                if (!enabled) {
                    galacticWordFields[checkIndex][3].setText("");
                }
                checkCrackButton.run();
            });
        }

        for (JTextField levels : slotLevelFields) {
            levels.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    int i;
                    String s = levels.getText();
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
                    if (i > EnchantCracker.this.version.getMaxLevels()) {
                        e.consume();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    checkCrackButton.run();
                }
            });
        }

        for (JTextField[] slotWords : galacticWordFields) {
            for (JTextField word : slotWords) {
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
                        if (i > 54) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        checkCrackButton.run();
                    }
                });
            }
        }

        clearButton.addActionListener((event) -> {
            for (JCheckBox box : fourWordChecks) {
                box.setSelected(false);
            }
            for (JTextField[] words : galacticWordFields) {
                for (int i = 0; i < 4; i++) {
                    words[i].setText("");
                    if (i == 3) {
                        words[i].setEditable(false);
                    }
                }
            }
            for (JTextField field : slotLevelFields) {
                field.setText("");
            }
            crackButton.setEnabled(false);
            crackerResultMessage.setText(EnchantCrackerI18n.translate("panel.cracker.cleared"));
            this.rngSeed = SeedResults.SEED_RESULT_UNSET.getValue();
        });

        crackerPanel.setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                for (int slot = 0; slot < 3; slot++) {
                    if (slotLevelFields[slot].equals(aComponent)) {
                        if (slot == 2) {
                            if (crackButton.isEnabled()) {
                                return crackButton;
                            }
                            else {
                                return clearButton;
                            }
                        }
                        else {
                            return galacticWordFields[slot+1][0];
                        }
                    }

                    for (int word = 0; word < 4; word++) {
                        if (galacticWordFields[slot][word].equals(aComponent)) {
                            if (word == 2) {
                                if (galacticWordFields[slot][3].isEditable()) {
                                    return galacticWordFields[slot][3];
                                }
                                else {
                                    return slotLevelFields[slot];
                                }
                            }
                            if (word == 3) {
                                return slotLevelFields[slot];
                            }
                            else {
                                return galacticWordFields[slot][word+1];
                            }
                        }
                    }
                }

                if (crackButton.equals(aComponent)) {
                    return clearButton;
                }
                if (clearButton.equals(aComponent)) {
                    return aContainer.getParent();
                }

                return null;
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                for (int slot = 0; slot < 3; slot++) {
                    if (slotLevelFields[slot].equals(aComponent)) {
                        if (galacticWordFields[slot][3].isEditable()) {
                            return galacticWordFields[slot][3];
                        }
                        else {
                            return galacticWordFields[slot][2];
                        }
                    }

                    for (int word = 0; word < 4; word++) {
                        if (galacticWordFields[slot][word].equals(aComponent)) {
                            if (word == 0) {
                                if (slot == 0) {
                                    return aContainer.getParent();
                                }
                                else {
                                    return slotLevelFields[slot-1];
                                }
                            }
                            else {
                                return galacticWordFields[slot][word-1];
                            }
                        }
                    }
                }

                if ((clearButton.equals(aComponent) && !crackButton.isEnabled()) || crackButton.equals(aComponent)) {
                    if (galacticWordFields[2][3].isEditable()) {
                        return galacticWordFields[2][3];
                    }
                    else {
                        return galacticWordFields[2][2];
                    }
                }
                if (clearButton.equals(aComponent)) {
                    return crackButton;
                }

                return null;
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return galacticWordFields[0][0];
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return clearButton;
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return galacticWordFields[0][0];
            }
        });

        this.tabs.addTab(EnchantCrackerI18n.translate("panel.galactic"), crackerPanel);
    }

    private void createExtremesCrackerPanel() {
        JPanel crackerPanel = new JPanel();
        JLabel crackerLabel;

        GroupLayout layout = new GroupLayout(crackerPanel);
        crackerPanel.setLayout(layout);

        // Low
        JCheckBox[] lowChecks = new JCheckBox[this.version.getExtremesNeeded()];

        JPanel lowPanel = new JPanel();
        lowPanel.setLayout(new BoxLayout(lowPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.low"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lowPanel.add(crackerLabel);

        for (int extreme = 0; extreme < this.version.getExtremesNeeded(); extreme++) {
            JCheckBox box = new JCheckBox();
            box.setAlignmentX(Component.CENTER_ALIGNMENT);
            lowPanel.add(box);
            lowChecks[extreme] = box;
        }

        lowPanel.setMaximumSize(lowPanel.getPreferredSize());
        // End Low

        // Advances
        JTextField[] advancesFields = new JTextField[this.version.getExtremesNeeded()];

        JPanel advancesPanel = new JPanel();
        advancesPanel.setLayout(new BoxLayout(advancesPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.advances"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        advancesPanel.add(crackerLabel);

        advancesPanel.add(Box.createRigidArea(new Dimension(0,1)));

        for (int extreme = 0; extreme < this.version.getExtremesNeeded(); extreme++) {
            JTextField field = new JTextField(5);
            if (extreme == 0) {
                field.setEditable(false);
                field.setText("0");
            }
            field.setTransferHandler(null);
            field.setAlignmentX(Component.CENTER_ALIGNMENT);
            advancesPanel.add(field);
            advancesPanel.add(Box.createRigidArea(new Dimension(0,1)));
            advancesFields[extreme] = field;
        }

        advancesPanel.setMaximumSize(advancesPanel.getPreferredSize());
        // End Advances

        // Search Delay
        JPanel searchDelayPanel = new JPanel();
        searchDelayPanel.setLayout(new BoxLayout(searchDelayPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.searchdelay"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchDelayPanel.add(crackerLabel);

        JTextField searchDelayField = new JTextField(5);
        searchDelayField.setText("200");
        searchDelayField.setTransferHandler(null);
        searchDelayField.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchDelayPanel.add(searchDelayField);

        searchDelayPanel.setMaximumSize(searchDelayPanel.getPreferredSize());
        // End Search Delay

        // X
        JPanel xPanel = new JPanel();
        xPanel.setLayout(new BoxLayout(xPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.x"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        xPanel.add(crackerLabel);

        JTextField xField = new JTextField(5);
        xField.setText("0");
        xField.setTransferHandler(null);
        xField.setAlignmentX(Component.CENTER_ALIGNMENT);
        xPanel.add(xField);

        xPanel.setMaximumSize(xPanel.getPreferredSize());
        // End X

        // Y
        JPanel yPanel = new JPanel();
        yPanel.setLayout(new BoxLayout(yPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.y"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        yPanel.add(crackerLabel);

        JTextField yField = new JTextField(5);
        yField.setText("0");
        yField.setTransferHandler(null);
        yField.setAlignmentX(Component.CENTER_ALIGNMENT);
        yPanel.add(yField);

        yPanel.setMaximumSize(yPanel.getPreferredSize());
        // End Y

        // PRTSCR Attempts
        JPanel prtscrAttemptsPanel = new JPanel();
        prtscrAttemptsPanel.setLayout(new BoxLayout(prtscrAttemptsPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.prtscr"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        prtscrAttemptsPanel.add(crackerLabel);

        JTextField prtscrAttemptsField = new JTextField(5);
        prtscrAttemptsField.setText("5");
        prtscrAttemptsField.setTransferHandler(null);
        prtscrAttemptsField.setAlignmentX(Component.CENTER_ALIGNMENT);
        prtscrAttemptsPanel.add(prtscrAttemptsField);

        prtscrAttemptsPanel.setMaximumSize(prtscrAttemptsPanel.getPreferredSize());
        // End PRTSCR Attempts

        // Search Type
        JPanel searchTypePanel = new JPanel();
        searchTypePanel.setLayout(new BoxLayout(searchTypePanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.typeselect"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchTypePanel.add(crackerLabel);

        String[] autoSearchTypes = new String[]{"Windows 10/11 PRTSCR", "Robot"};

        JComboBox<String> searchTypeComboBox = new JComboBox<>(autoSearchTypes);
        searchTypeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchTypePanel.add(searchTypeComboBox);

        searchTypePanel.setMaximumSize(searchTypePanel.getPreferredSize());
        // End Search Type

        // Test Mode
        JPanel testModePanel = new JPanel();
        testModePanel.setLayout(new BoxLayout(testModePanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.extremes.test"));
        crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        testModePanel.add(crackerLabel);

        JCheckBox testModeCheck = new JCheckBox();
        testModeCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        testModePanel.add(testModeCheck);

        testModePanel.setMaximumSize(testModePanel.getPreferredSize());
        // End Test Mode

        JButton enableSearcherButton = new JButton(EnchantCrackerI18n.translate("panel.extremes.search.enable"));

        JLabel resultMessage = new JLabel(EnchantCrackerI18n.translate("cracker.waiting"));

        JButton clearButton = new JButton(EnchantCrackerI18n.translate("panel.cracker.clear"));

        JButton crackButton = new JButton(EnchantCrackerI18n.translate("panel.cracker.crack"));
        crackButton.setEnabled(false);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lowPanel)
                                        .addComponent(advancesPanel)
                                        .addGroup(layout.createParallelGroup()
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(searchDelayPanel)
                                                        .addComponent(xPanel)
                                                        .addComponent(yPanel)
                                                        .addComponent(prtscrAttemptsPanel)
                                                )
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(searchTypePanel)
                                                        .addComponent(testModePanel)
                                                )
                                        )
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(crackButton)
                                        .addComponent(clearButton)
                                        .addComponent(enableSearcherButton)
                                )
                                .addComponent(resultMessage)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(lowPanel)
                                .addComponent(advancesPanel)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup()
                                                .addComponent(searchDelayPanel)
                                                .addComponent(xPanel)
                                                .addComponent(yPanel)
                                                .addComponent(prtscrAttemptsPanel)
                                        )
                                        .addGroup(layout.createParallelGroup()
                                                .addComponent(searchTypePanel)
                                                .addComponent(testModePanel)
                                        )
                                )
                        )
                        .addGroup(layout.createParallelGroup()
                                .addComponent(crackButton)
                                .addComponent(clearButton)
                                .addComponent(enableSearcherButton)
                        )
                        .addComponent(resultMessage)
        );

        crackButton.addActionListener((event) -> {
            int[] advances = new int[version.getExtremesNeeded()];
            boolean[] isLow = new boolean[version.getExtremesNeeded()];

            for (int slot = 0; slot < version.getExtremesNeeded(); slot++) {
                lowChecks[slot].setEnabled(false);
                advancesFields[slot].setEditable(false);
                advances[slot] = Integer.parseInt(advancesFields[slot].getText());
                isLow[slot] = lowChecks[slot].isSelected();
            }

            ExtremesCracker cracker = new ExtremesCracker(advances, isLow, version);

            resultMessage.setText(EnchantCrackerI18n.translate("cracker.active"));
            crackButton.setEnabled(false);
            clearButton.setEnabled(false);

            Thread waitThread = new Thread(() -> {
                try {
                    cracker.run();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    resultMessage.setText(EnchantCrackerI18n.translate("cracker.error.unknown"));
                }

                if (cracker.getFailed()) {
                    resultMessage.setText(EnchantCrackerI18n.translate("cracker.error.noseeds"));
                } else {
                    resultMessage.setText(EnchantCrackerI18n.translate("cracker.success"));

                    this.rngSeed = cracker.getResult();

                    System.out.printf(EnchantCrackerI18n.translate("cracker.seed") + "%n", cracker.getResult());
                }

                crackButton.setEnabled(true);
                clearButton.setEnabled(true);
                for (int slot = 0; slot < version.getExtremesNeeded(); slot++) {
                    if (slot != 0) {
                        advancesFields[slot].setEditable(true);
                    }
                    lowChecks[slot].setEnabled(true);

                }
            });

            waitThread.start();

        });

        searchDelayField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = searchDelayField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                }
            }
        });

        xField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = xField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                }
            }
        });

        yField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = yField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                }
            }
        });

        prtscrAttemptsField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = prtscrAttemptsField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                }
            }
        });

        for (int extreme = 1; extreme < version.getExtremesNeeded(); extreme++) {
            JTextField field = advancesFields[extreme];
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    String s = field.getText();
                    if (s == null) {
                        s = "";
                    }
                    s+=e.getKeyChar();
                    try {
                        Integer.parseInt(s);
                    }
                    catch (NumberFormatException nfe) {
                        e.consume();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (!field.isEditable()) {
                        return;
                    }

                    boolean valid = true;
                    for (int slot = 0; slot < version.getExtremesNeeded(); slot++) {
                        String s = advancesFields[slot].getText();
                        if (s == null || s.length() == 0) {
                            valid = false;
                            break;
                        }
                    }
                    crackButton.setEnabled(valid);
                }
            });
        }

        clearButton.addActionListener((event) -> {
            for (JCheckBox box : lowChecks) {
                box.setSelected(false);
            }
            for (JTextField field : advancesFields) {
                if (field.isEditable()) {
                    field.setText("");
                }
            }
            crackButton.setEnabled(false);
            resultMessage.setText(EnchantCrackerI18n.translate("panel.cracker.cleared"));
            this.rngSeed = SeedResults.SEED_RESULT_UNSET.getValue();
        });

        enableSearcherButton.addActionListener((event) -> {
            try {
                String s = searchDelayField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.error.delay"));
                    return;
                }
                int delay = Integer.parseInt(s);
                s = xField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.error.x"));
                    return;
                }
                int x = Integer.parseInt(s);
                s = yField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.error.y"));
                    return;
                }
                int y = Integer.parseInt(s);
                s = prtscrAttemptsField.getText();
                if (s == null || s.length() == 0) {
                    resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.error.prtscr"));
                    return;
                }
                int screenshots = Integer.parseInt(s);

                resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.instructions"));

                this.extremeSearcher.exit(EnchantCrackerI18n.translate("panel.extremes.search.terminated"));
                this.extremeSearcher.setup(this.version, delay, x, y, searchTypeComboBox.getSelectedIndex() == 0, screenshots, testModeCheck.isSelected(), advancesFields, lowChecks, crackButton, enableSearcherButton, resultMessage);
                enableSearcherButton.setEnabled(false);
            }
            catch (NumberFormatException e) {
                resultMessage.setText(EnchantCrackerI18n.translate("panel.extremes.search.error.parse"));
            }
        });

        this.tabs.addTab(EnchantCrackerI18n.translate("panel.extremes"), crackerPanel);
    }

    private void createLevelCrackerPanel() {
        JPanel crackerPanel = new JPanel();
        JLabel crackerLabel;

        GroupLayout layout = new GroupLayout(crackerPanel);
        crackerPanel.setLayout(layout);

        // Labels
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.levels.levels"));
        labelPanel.add(crackerLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,2)));

        for (int c = 0; c < 10; c++) {
            crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.levels.cycle."+c));
            labelPanel.add(crackerLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        }

        labelPanel.setMaximumSize(labelPanel.getPreferredSize());
        // End Labels

        // Slots
        JTextField[][] levelFields = new JTextField[3][10];
        JPanel[] slotPanels = new JPanel[10];

        for (int slot = 0; slot < 3; slot++) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            crackerLabel = new JLabel(EnchantCrackerI18n.translate("panel.levels.slot."+slot));
            crackerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(crackerLabel);

            for (int c = 0; c < 10; c++) {
                JTextField field = new JTextField(3);
                field.setTransferHandler(null);
                field.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(field);
                levelFields[slot][c] = field;
            }

            panel.setMaximumSize(panel.getPreferredSize());

            slotPanels[slot] = panel;
        }
        // End Slots

        JLabel resultMessage = new JLabel(EnchantCrackerI18n.translate("cracker.waiting"));

        JButton clearButton = new JButton(EnchantCrackerI18n.translate("panel.cracker.clear"));

        JButton crackButton = new JButton(EnchantCrackerI18n.translate("panel.cracker.crack"));
        crackButton.setEnabled(false);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(labelPanel)
                                        .addComponent(slotPanels[0])
                                        .addComponent(slotPanels[1])
                                        .addComponent(slotPanels[2])
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(crackButton)
                                        .addComponent(clearButton)
                                )
                                .addComponent(resultMessage)
                        )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelPanel)
                        .addComponent(slotPanels[0])
                        .addComponent(slotPanels[1])
                        .addComponent(slotPanels[2])
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(crackButton)
                        .addComponent(clearButton)
                )
                .addComponent(resultMessage)
        );


        crackButton.addActionListener((event) -> {
            int[][] cycleData = new int[10][3];
            for (int slot = 0; slot < 3; slot++) {
                for (int cycle = 0; cycle < 10; cycle++) {
                    cycleData[cycle][slot] = Integer.parseInt(levelFields[slot][cycle].getText());
                    levelFields[slot][cycle].setEditable(false);
                }
            }

            LevelCracker cracker = new LevelCracker(cycleData, version);

            resultMessage.setText(EnchantCrackerI18n.translate("cracker.active"));
            crackButton.setEnabled(false);
            clearButton.setEnabled(false);

            Thread waitThread = new Thread(() -> {
                try {
                    cracker.run();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    resultMessage.setText(EnchantCrackerI18n.translate("cracker.error.unknown"));
                }

                if (cracker.getFailed()) {
                    resultMessage.setText(EnchantCrackerI18n.translate("cracker.error.noseeds"));
                } else {
                    resultMessage.setText(EnchantCrackerI18n.translate("cracker.success"));

                    this.rngSeed = cracker.getResult();

                    System.out.printf(EnchantCrackerI18n.translate("cracker.seed") + "%n", cracker.getResult());
                }

                crackButton.setEnabled(true);
                clearButton.setEnabled(true);
                for (int slot = 0; slot < 3; slot++) {
                    for (int cycle = 0; cycle < 10; cycle++) {
                        levelFields[slot][cycle].setEditable(true);
                    }
                }
            });

            waitThread.start();

        });

        Runnable checkCrackButton = () -> {
            boolean valid = true;
            for (int slot = 0; slot < 3; slot++) {
                for (int cycle = 0; cycle < 10; cycle++) {
                    String s = levelFields[slot][cycle].getText();
                    if (s == null || s.length() == 0) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    break;
                }
            }
            crackButton.setEnabled(valid);
        };

        for (int slot = 0; slot < 3; slot++) {
            JPanel slotPanel = slotPanels[slot];
            slotPanel.setFocusCycleRoot(true);
            slotPanel.setFocusTraversalPolicy(new FocusTraversalPolicy() {
                @Override
                public Component getComponentAfter(Container aContainer, Component aComponent) {
                    for (int slot = 0; slot < 3; slot++) {
                        for (int cycle = 0; cycle < 10; cycle++) {
                            if (levelFields[slot][cycle].equals(aComponent)) {
                                if (!(slot == 2 && cycle == 9)) {
                                    if (slot == 2) {
                                        return levelFields[0][cycle + 1];
                                    } else {
                                        return levelFields[slot + 1][cycle];
                                    }
                                }
                                else {
                                    if (crackButton.isEnabled()) {
                                        return crackButton;
                                    }
                                    else {
                                        return clearButton;
                                    }
                                }
                            }
                        }
                    }

                    return null;
                }

                @Override
                public Component getComponentBefore(Container aContainer, Component aComponent) {
                    for (int slot = 0; slot < 3; slot++) {
                        for (int cycle = 0; cycle < 10; cycle++) {
                            if (levelFields[slot][cycle].equals(aComponent)) {
                                if (!(slot == 0 && cycle == 0)) {
                                    if (slot == 0) {
                                        return levelFields[2][cycle - 1];
                                    } else {
                                        return levelFields[slot - 1][cycle];
                                    }
                                }
                                else {
                                    return aContainer.getParent().getParent();
                                }
                            }
                        }
                    }

                    return null;
                }

                @Override
                public Component getFirstComponent(Container aContainer) {
                    return levelFields[0][0];
                }

                @Override
                public Component getLastComponent(Container aContainer) {
                    return clearButton;
                }

                @Override
                public Component getDefaultComponent(Container aContainer) {
                    return levelFields[0][0];
                }
            });

            for (int cycle = 0; cycle < 10; cycle++) {
                JTextField word = levelFields[slot][cycle];
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
                        if (i > 8) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (!word.isEditable()) {
                            return;
                        }
                        checkCrackButton.run();
                    }
                });
            }
        }

        clearButton.addActionListener((event) -> {
            for (int slot = 0; slot < 3; slot++) {
                for (int cycle = 0; cycle < 10; cycle++) {
                    levelFields[slot][cycle].setText("");
                }
            }
            crackButton.setEnabled(false);
        });

        this.tabs.addTab(EnchantCrackerI18n.translate("panel.levels"), crackerPanel);
    }

    private void createManipulatorPanel() {
        JPanel manipulatorPanel = new JPanel();
        JLabel manipulatorLabel;

        GroupLayout layout = new GroupLayout(manipulatorPanel);
        manipulatorPanel.setLayout(layout);

        JPanel leftPanel = new JPanel();
        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        GroupLayout leftLayout = new GroupLayout(leftPanel);
        leftPanel.setLayout(leftLayout);

        // Item
        JPanel itemPanel = new JPanel();

        manipulatorLabel = new JLabel(EnchantCrackerI18n.translate("panel.manipulator.item"));
        itemPanel.add(manipulatorLabel);

        int itemCount = this.version.getItemStrings().length;

        String[] itemStrings = new String[itemCount];
        for (int i = 0; i < itemCount; i++) {
            itemStrings[i] = EnchantCrackerI18n.translate(this.version.getItemStrings()[i]);
        }

        JComboBox<String> itemComboBox = new JComboBox<>(itemStrings);
        itemPanel.add(itemComboBox);

        itemPanel.setMaximumSize(itemPanel.getPreferredSize());
        // End Item

        // Material
        JPanel materialPanel = new JPanel();

        manipulatorLabel = new JLabel(EnchantCrackerI18n.translate("panel.manipulator.material"));
        materialPanel.add(manipulatorLabel);

        List<String> materialStringsRaw = new ArrayList<>(Arrays.asList(this.version.getMaterialStrings(this.version.getItemStrings()[0])));

        String[] materialStrings = new String[materialStringsRaw.size()];
        for (int i = 0; i < materialStringsRaw.size(); i++) {
            materialStrings[i] = EnchantCrackerI18n.translate(materialStringsRaw.get(i));
        }

        JComboBox<String> materialComboBox = new JComboBox<>(materialStrings);
        materialPanel.add(materialComboBox);

        materialPanel.setMaximumSize(materialPanel.getPreferredSize());
        // End Material

        // Settings
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));

        manipulatorLabel = new JLabel(EnchantCrackerI18n.translate("panel.manipulator.exactly"));
        settingsPanel.add(manipulatorLabel);
        JCheckBox exactlyCheck = new JCheckBox();
        settingsPanel.add(exactlyCheck);
        manipulatorLabel = new JLabel(EnchantCrackerI18n.translate("panel.manipulator.advanced"));
        settingsPanel.add(manipulatorLabel);
        JCheckBox advancedCheck = new JCheckBox();
        settingsPanel.add(advancedCheck);

        settingsPanel.setMaximumSize(settingsPanel.getPreferredSize());
        // End Settings

        // Max Advances
        JPanel advancesPanel = new JPanel();

        manipulatorLabel = new JLabel(EnchantCrackerI18n.translate("panel.manipulator.maxadvances"));
        advancesPanel.add(manipulatorLabel);
        JTextField advancesField = new JTextField(7);
        advancesField.setText(String.valueOf(this.maxAdvances));
        advancesField.setTransferHandler(null);
        advancesPanel.add(advancesField);

        advancesPanel.setMaximumSize(advancesPanel.getPreferredSize());
        // End Max Advances

        // Max Level
        JPanel maxLevelPanel = new JPanel();

        manipulatorLabel = new JLabel(EnchantCrackerI18n.translate("panel.manipulator.maxlevel"));
        maxLevelPanel.add(manipulatorLabel);

        String[] maxLevelStrings = new String[this.version.getMaxLevels()];
        for (int i = 0; i < this.version.getMaxLevels(); i++) {
            maxLevelStrings[i] = Integer.toString(this.version.getMaxLevels() - i);
        }

        JComboBox<String> maxLevelSelect = new JComboBox<>(maxLevelStrings);
        maxLevelPanel.add(maxLevelSelect);

        maxLevelPanel.setMaximumSize(advancesPanel.getPreferredSize());
        // End Max Level

        JButton findButton = new JButton(EnchantCrackerI18n.translate("panel.manipulator.enchantfind"));
        findButton.setEnabled(false);

        JButton finalizeButton = new JButton(EnchantCrackerI18n.translate("panel.manipulator.enchantdone"));
        finalizeButton.setEnabled(false);

        JLabel resultMessage = new JLabel(EnchantCrackerI18n.translate("manipulator.waiting"));

        Runnable updateButtons = () -> {
            String text = advancesField.getText();
            if (text == null) {
                text = "";
            }
            if (text.length() > 0) {
                try {
                    this.maxAdvances = Integer.parseInt(text);
                    if (this.desiredEnchants.size() > 0) {
                        findButton.setEnabled(true);
                        return;
                    }
                }
                catch (NumberFormatException ignored) {

                }
            }
            findButton.setEnabled(false);
            this.maxAdvances = EMPTY_VALUE;
        };

        JPanel selectorPanel = new JPanel();
        selectorPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane selectorScrollPlane = new JScrollPane(selectorPanel);
        selectorScrollPlane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        updateEnchantmentSelectorPanel(this.version.getItemStrings()[itemComboBox.getSelectedIndex()], selectorScrollPlane, selectorPanel, updateButtons);

        leftLayout.setAutoCreateGaps(true);

        leftLayout.setHorizontalGroup(leftLayout.createSequentialGroup()
                .addGroup(leftLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(itemPanel)
                        .addComponent(materialPanel)
                        .addComponent(settingsPanel)
                        .addComponent(advancesPanel)
                        .addComponent(maxLevelPanel)
                        .addComponent(findButton)
                        .addComponent(finalizeButton)
                )
        );

        leftLayout.setVerticalGroup(leftLayout.createSequentialGroup()
                .addComponent(itemPanel)
                .addComponent(materialPanel)
                .addComponent(settingsPanel)
                .addComponent(advancesPanel)
                .addComponent(maxLevelPanel)
                .addComponent(findButton)
                .addComponent(finalizeButton)
        );

        leftPanel.setMaximumSize(leftPanel.getPreferredSize());

        selectorScrollPlane.setMaximumSize(new Dimension(selectorScrollPlane.getPreferredSize().width, leftPanel.getPreferredSize().height));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(leftPanel)
                                .addComponent(selectorScrollPlane)
                        )
                        .addComponent(resultMessage)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(leftPanel)
                        .addComponent(selectorScrollPlane)
                )
                .addComponent(resultMessage)
        );

        findButton.addActionListener((event) -> {
            if (this.rngSeed == SeedResults.SEED_RESULT_UNSET.getValue()) {
                resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.noseed"));
                return;
            }

            if (this.desiredEnchants.size() == 0) {
                resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.noenchants"));
            } else {
                if (Objects.equals(materialStringsRaw.get(materialComboBox.getSelectedIndex()), "material.book") && !(this.version instanceof Nine) && this.desiredEnchants.size() > 1) {
                    resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.impossiblebook"));
                    return;
                }

                int requiredEnchantability = 0;
                for (EnchantData data : this.desiredEnchants) {
                    List<EnchantData> copyData = new ArrayList<>(this.desiredEnchants);
                    copyData.remove(data);
                    for (EnchantData d2 : copyData) {
                        if (!data.getEnchant().isCompatibleEnchant(d2.getEnchant().getId())) {
                            resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.conflicting"));
                            return;
                        }
                        if (data.getEnchant().getMinEnchantability(data.getLevel()) > d2.getEnchant().getMaxEnchantability(d2.getLevel())
                                || data.getEnchant().getMaxEnchantability(data.getLevel()) < d2.getEnchant().getMinEnchantability(d2.getLevel())
                        ) {
                            resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.impossible"));
                            return;
                        }
                    }

                    if (this.version.getMaxEnchantability(this.version.getMaterialEnchantability(materialStringsRaw.get(materialComboBox.getSelectedIndex())), this.version.getMaxLevels()) < data.getEnchant().getMinEnchantability(data.getLevel())) {
                        resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.impossible"));
                        return;
                    }

                    int enchantability = data.getEnchant().getMinEnchantability(data.getLevel());
                    if (enchantability > requiredEnchantability) {
                        requiredEnchantability = enchantability;
                    }
                }

                int maxLevels = Integer.parseInt((String)maxLevelSelect.getSelectedItem());
                int materialEnchantability = this.version.getMaterialEnchantability(materialStringsRaw.get(materialComboBox.getSelectedIndex()));

                if (requiredEnchantability > this.version.getMaxEnchantability(materialEnchantability, maxLevels)) {
                    resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.impossible"));
                    return;
                }

                resultMessage.setText(EnchantCrackerI18n.translate("manipulator.active"));
                findButton.setEnabled(false);
                advancesField.setEditable(false);
                exactlyCheck.setEnabled(false);
                advancedCheck.setEnabled(false);
                itemComboBox.setEnabled(false);
                materialComboBox.setEnabled(false);

                for (Component p : selectorPanel.getComponents()) {
                    if (p instanceof JPanel) {
                        for (Component c : ((JPanel)p).getComponents()) {
                            if (c instanceof JCheckBox || c instanceof JComboBox) {
                                c.setEnabled(false);
                            }
                        }
                    }
                }

                EnchantFinder finder = new EnchantFinder(this.version.getItemStrings()[itemComboBox.getSelectedIndex()], materialEnchantability, this.rngSeed, this.version, this.bookshelves, this.maxAdvances, this.desiredEnchants, exactlyCheck.isSelected(), advancedCheck.isSelected(), maxLevels);

                Thread waitThread = new Thread(() -> {
                    try {
                        finder.run();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.unknown"));
                    }

                    if (finder.getFailed()) {
                        if (finder.getResultAdvances() == FinderResults.FINDER_RESULT_NOTFOUND.getValue()) {
                            resultMessage.setText(String.format(EnchantCrackerI18n.translate("manipulator.error.notfound"), this.maxAdvances));
                        } else {
                            resultMessage.setText(EnchantCrackerI18n.translate("manipulator.error.unknown"));
                        }
                    } else {
                        StringBuilder resultBuilder = new StringBuilder();

                        for (EnchantData data : finder.getResultEnchants()) {
                            resultBuilder.append(String.format(EnchantCrackerI18n.translate("manipulator.found.enchantment"), EnchantCrackerI18n.translate(data.getEnchant().getName()), EnchantCrackerI18n.translate("enchantment.level."+(data.getLevel() - 1))));
                        }
                        resultBuilder.append("</HTML>");

                        if (advancedCheck.isSelected()) {
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

                            resultMessage.setText(String.format(EnchantCrackerI18n.translate("manipulator.found.advanced") + resultBuilder, swapDifferent, shift, outIn, swap, finder.getResultSlot() + 1, Arrays.toString(finder.getResultLevels())));
                        } else {
                            resultMessage.setText(String.format(EnchantCrackerI18n.translate("manipulator.found.simple") + resultBuilder, finder.getResultAdvances(), finder.getResultSlot()+1, Arrays.toString(finder.getResultLevels())));
                        }
                        this.postManipulateRngSeed = finder.getResultSeed();
                        finalizeButton.setEnabled(true);
                    }

                    findButton.setEnabled(true);
                    advancesField.setEditable(true);
                    exactlyCheck.setEnabled(true);
                    advancedCheck.setEnabled(true);
                    itemComboBox.setEnabled(true);
                    materialComboBox.setEnabled(true);

                    for (Component p : selectorPanel.getComponents()) {
                        if (p instanceof JPanel) {
                            for (Component c : ((JPanel)p).getComponents()) {
                                if (c instanceof JCheckBox || c instanceof JComboBox) {
                                    c.setEnabled(true);
                                }
                            }
                        }
                    }
                });

                waitThread.start();

            }
        });

        itemComboBox.addActionListener((event) -> {
            String item = this.version.getItemStrings()[itemComboBox.getSelectedIndex()];
            String oldMaterial = (String)materialComboBox.getSelectedItem();
            materialComboBox.removeAllItems();

            materialStringsRaw.clear();
            Collections.addAll(materialStringsRaw, this.version.getMaterialStrings(item));

            String[] materialStringsN = new String[materialStringsRaw.size()];

            for (int i = 0; i < materialStringsRaw.size(); i++) {
                materialStringsN[i] = EnchantCrackerI18n.translate(materialStringsRaw.get(i));
            }

            int i = 0;
            for (String s : materialStringsN) {
                materialComboBox.addItem(s);
                if (s.equals(oldMaterial)) {
                    materialComboBox.setSelectedIndex(i);
                }
                i++;
            }

            materialPanel.setMaximumSize(materialPanel.getPreferredSize());

            updateEnchantmentSelectorPanel(item, selectorScrollPlane, selectorPanel, updateButtons);
        });

        finalizeButton.addActionListener((event) -> {
            resultMessage.setText(EnchantCrackerI18n.translate("manipulator.waiting"));
            System.out.printf(EnchantCrackerI18n.translate("manipulator.seed") + "%n", this.postManipulateRngSeed);
            this.rngSeed = this.postManipulateRngSeed;
            this.postManipulateRngSeed = SeedResults.SEED_RESULT_UNSET.getValue();
            finalizeButton.setEnabled(false);
        });

        advancesField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String s = advancesField.getText();
                if (s == null) {
                    s = "";
                }
                s+=e.getKeyChar();
                try {
                    Integer.parseInt(s);
                }
                catch (NumberFormatException nfe) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateButtons.run();
            }
        });

        this.tabs.addTab(EnchantCrackerI18n.translate("panel.manipulator"), manipulatorPanel);
    }

    private void updateEnchantmentSelectorPanel(String itemName, JScrollPane selectorScrollPlane, JPanel selectorPanel, Runnable updateButtons) {
        this.desiredEnchants.clear();

        selectorPanel.removeAll();

        JLabel label;
        Item item = this.version.getItem(itemName);

        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));
        selectorPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));

        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.Y_AXIS));

        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        checkPanel.add(Box.createRigidArea(new Dimension(0,2)));

        int countToAdd = item.getValidEnchants().length;

        for (int i = 0; i < countToAdd; i++) {
            Enchantment enchantment = this.version.getEnchant(item.getValidEnchants()[i]);
            label = new JLabel(EnchantCrackerI18n.translate(enchantment.getName()));
            labelPanel.add(label);
            labelPanel.add(Box.createRigidArea(new Dimension(0,9)));

            JCheckBox selectCheck = new JCheckBox();
            checkPanel.add(selectCheck);
            checkPanel.add(Box.createRigidArea(new Dimension(0,4)));

            String[] levelStrings = new String[enchantment.getMaxLevel()];
            for (int l = 0; l < enchantment.getMaxLevel(); l++) {
                levelStrings[l] = EnchantCrackerI18n.translate("enchantment.level." + l);
            }
            JComboBox<String> levelComboBox = new JComboBox<>(levelStrings);
            levelComboBox.setSelectedIndex(levelStrings.length - 1);
            comboBoxPanel.add(levelComboBox);

            selectCheck.addItemListener((event) -> {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    this.desiredEnchants.add(new EnchantData(enchantment, levelComboBox.getSelectedIndex() + 1));
                } else {
                    this.desiredEnchants.removeIf((enchantData -> enchantData.getEnchant() == enchantment));
                }
                updateButtons.run();
            });

            levelComboBox.addActionListener((event) -> {
                if (selectCheck.isSelected()) {
                    for (EnchantData data : this.desiredEnchants) {
                        if (data.getEnchant() == enchantment) {
                            data.setLevel(levelComboBox.getSelectedIndex() + 1);
                        }
                    }
                }
            });
        }

        labelPanel.setMaximumSize(labelPanel.getPreferredSize());
        checkPanel.setMaximumSize(checkPanel.getPreferredSize());
        comboBoxPanel.setMaximumSize(comboBoxPanel.getPreferredSize());

        labelPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        checkPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        comboBoxPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        selectorPanel.add(labelPanel);
        selectorPanel.add(checkPanel);
        selectorPanel.add(comboBoxPanel);

        selectorPanel.setMaximumSize(selectorPanel.getPreferredSize());

        selectorPanel.repaint();

        selectorScrollPlane.setMaximumSize(new Dimension(selectorScrollPlane.getPreferredSize().width, selectorScrollPlane.getMaximumSize().height));
        selectorScrollPlane.repaint();
    }
}
