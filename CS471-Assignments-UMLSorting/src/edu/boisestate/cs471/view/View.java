package edu.boisestate.cs471.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import edu.boisestate.cs471.controller.Controller;
import edu.boisestate.cs471.model.Model;
import edu.boisestate.cs471.util.ComboBoxModelWrapper;
import edu.boisestate.cs471.util.UserCommand;
import edu.boisestate.cs471.util.interfaces.IViewUpdateListener;

public class View implements IViewUpdateListener {

    private JFrame mFrame;
    private final Controller mController;
    private JButton mBtnNext;
    private JButton mBtnPrevious;
    private JComboBox<String> mAlgorithmSelect;
    private ComboBoxModelWrapper<String> mAlgorithmChoices;
    private Visualizer mVisualizer;
    private JButton mBtnShuffle;
    private JButton mBtnIterate;
    private JButton mBtnPause;
    private JButton mBtnPlay;
    private JLabel mLblIterationCounter;
    private JLabel mLblIterationCounterNumber;
    private JLabel mLblIsSorted;
    private JMenu mMenuSettings;
    private JMenuItem mMenuItemSampleCount;
    private JMenu mMenuLanguage;
    private JMenuItem mMenuItemEnglish;
    private JMenuItem mMenuItemSpanish;



    /**
     * Create the application.
     *
     * @param ctrl The Controller of the Model-View-Controller.
     */
    public View(final Controller ctrl) {
        mController = ctrl;
        createComponents();
        setDynamicText();
        setButtonStates();

        mController.registerListener(this);
        addMenuListeners();
    }

    /**
     * Make the view visible to the user.
     */
    public final void show() {
        mFrame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void createComponents() {
        mFrame = new JFrame();
        mFrame.setBounds(100, 100, 450, 368);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setMinimumSize(new Dimension(450, 368));
        mFrame.setIconImage(createImageIcon("/images/ic_trending_up_black_24dp_2x.png").getImage());

        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0};
        gridBagLayout.rowHeights = new int[] {0, 250, 10, 10, 0};
        gridBagLayout.columnWeights = new double[] {1.0};
        gridBagLayout.rowWeights = new double[] {0.0, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, 1.0};
        mFrame.getContentPane().setLayout(gridBagLayout);

        GridBagConstraints constraints;

        final JPanel navigationPanel = new JPanel();
        constraints = buildConstraints(0, 0, 0);
        mFrame.getContentPane().add(navigationPanel, constraints);
        navigationPanel.setLayout(new BorderLayout(0, 0));

        mBtnPrevious = new JButton("", createImageIcon("/images/ic_navigate_before_black_24dp_1x.png"));
        mBtnPrevious.setFocusable(false);
        mBtnPrevious.setActionCommand(UserCommand.SELECT_PREVIOUS.toString());
        mBtnPrevious.addActionListener(mController);
        navigationPanel.add(mBtnPrevious, BorderLayout.WEST);

        mBtnNext = new JButton("", createImageIcon("/images/ic_navigate_next_black_24dp_1x.png"));
        mBtnNext.setFocusable(false);
        mBtnNext.setActionCommand(UserCommand.SELECT_NEXT.toString());
        mBtnNext.addActionListener(mController);
        navigationPanel.add(mBtnNext, BorderLayout.EAST);

        mAlgorithmSelect = new JComboBox<String>();
        mAlgorithmChoices = new ComboBoxModelWrapper<>(mController.getModel().getAllAlgorithmNames());
        mAlgorithmSelect.setModel(mAlgorithmChoices);
        mAlgorithmSelect.setActionCommand(UserCommand.SELECT_COMBO_BOX.toString());
        mAlgorithmSelect.addActionListener(mController);
        navigationPanel.add(mAlgorithmSelect, BorderLayout.CENTER);

        mVisualizer = new Visualizer();
        mVisualizer.setBackground(Color.WHITE);
        constraints = buildConstraints(15, 0, 1);
        mFrame.getContentPane().add(mVisualizer, constraints);

        // ==== Iteration Count ====
        final JPanel iterationCounter = new JPanel();
        final GridBagLayout iterationCounterLayout = new GridBagLayout();
        iterationCounterLayout.columnWidths = new int[] {0, 0, 0};
        iterationCounterLayout.rowHeights = new int[] {0};
        iterationCounterLayout.columnWeights = new double[] {Double.MIN_VALUE, Double.MIN_VALUE, 1.0};
        iterationCounterLayout.rowWeights = new double[] {1.0};
        iterationCounter.setLayout(iterationCounterLayout);
        mFrame.add(iterationCounter, buildConstraints(2, 0, 2));

        mLblIterationCounter = new JLabel("Iteration:");
        iterationCounter.add(mLblIterationCounter, buildConstraints(2, 0, 0));
        mLblIterationCounterNumber = new JLabel();
        iterationCounter.add(mLblIterationCounterNumber, buildConstraints(2, 1, 0));
        mLblIsSorted = new JLabel();
        iterationCounter.add(mLblIsSorted, buildConstraints(2, 3, 0));

        // ==== Action buttons ====
        final JPanel buttonBar = new JPanel();
        final GridBagLayout buttonBarLayout = new GridBagLayout();
        buttonBarLayout.columnWidths = new int[] {0, 0, 0, 0};
        buttonBarLayout.rowHeights = new int[] {10};
        buttonBarLayout.columnWeights = new double[] {1.0, 1.0, 1.0, 1.0};
        buttonBarLayout.rowWeights = new double[] {Double.MIN_VALUE};
        buttonBar.setLayout(buttonBarLayout);

        mBtnShuffle = new JButton("", createImageIcon("/images/ic_shuffle_black_24dp_1x.png"));
        mBtnShuffle.setActionCommand(UserCommand.RANDOMIZE.toString());
        mBtnShuffle.addActionListener(mController);
        constraints = buildConstraints(2, 0, 0);
        buttonBar.add(mBtnShuffle, constraints);

        mBtnPause = new JButton("", createImageIcon("/images/ic_pause_black_24dp_1x.png"));
        mBtnPause.setActionCommand(UserCommand.PAUSE.toString());
        mBtnPause.addActionListener(mController);
        constraints = buildConstraints(2, 1, 0);
        buttonBar.add(mBtnPause, constraints);

        mBtnPlay = new JButton("", createImageIcon("/images/ic_play_arrow_black_24dp_1x.png"));
        mBtnPlay.setActionCommand(UserCommand.PLAY.toString());
        mBtnPlay.addActionListener(mController);
        constraints = buildConstraints(2, 2, 0);
        buttonBar.add(mBtnPlay, constraints);

        mBtnIterate = new JButton("", createImageIcon("/images/ic_skip_next_black_24dp_1x.png"));
        mBtnIterate.setActionCommand(UserCommand.ITERATE.toString());
        mBtnIterate.addActionListener(mController);
        constraints = buildConstraints(2, 3, 0);
        buttonBar.add(mBtnIterate, constraints);

        constraints = buildConstraints(0, 0, 3);
        mFrame.getContentPane().add(buttonBar, constraints);


        final JMenuBar menuBar = new JMenuBar();
        mMenuSettings = new JMenu("Settings");
        mMenuItemSampleCount = new JMenuItem("Sample Size");
        mMenuSettings.add(mMenuItemSampleCount);

        mMenuLanguage = new JMenu("Language");
        ButtonGroup languageButtonGroup = new ButtonGroup();
        mMenuItemEnglish = new JRadioButtonMenuItem("English", true);
        mMenuLanguage.add(mMenuItemEnglish);
        languageButtonGroup.add(mMenuItemEnglish);

        mMenuItemSpanish = new JRadioButtonMenuItem("Spanish");
        mMenuLanguage.add(mMenuItemSpanish);
        languageButtonGroup.add(mMenuItemSpanish);
        mMenuSettings.add(mMenuLanguage);

        menuBar.add(mMenuSettings);

        mFrame.setJMenuBar(menuBar);
        mFrame.pack();
    }

    private void addMenuListeners() {
        mMenuItemSampleCount.addActionListener(event -> {
            String userInput = null;
            switch (mController.getModel().getCurrentLanguage()) {
                case "Spanish":
                    userInput = JOptionPane.showInputDialog(mFrame, "Por favor, especifique un número del 10 al 100.",
                            "Tamaño de la muestra", JOptionPane.QUESTION_MESSAGE);
                    break;
                case "english":
                default:
                    userInput = JOptionPane.showInputDialog(mFrame, "Please specify a number from 10 to 100.",
                            "Sample Size", JOptionPane.QUESTION_MESSAGE);
                    break;
            }
            if (null == userInput) {
                // User canceled
                return;
            }

            int newSize;
            try {
                newSize = Integer.parseInt(userInput);
            }
            catch (final NumberFormatException e) {
                newSize = -1;
            }
            if (newSize < 10 || newSize > 100) {
                switch (mController.getModel().getCurrentLanguage()) {
                    case "Spanish":
                        JOptionPane.showMessageDialog(null, "Por favor, especifique un número del 10 al 100.",
                                "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
                        break;
                    case "english":
                    default:
                        JOptionPane.showMessageDialog(null, "Please specify a number from 10 to 100.", "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                }

            }
            else {
                mController.updateSampleSize(newSize);
            }
        });

        mMenuItemEnglish.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected English");
                mController.updateLanguage("english");
            }
        });

        mMenuItemSpanish.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected Spanish");
                mController.updateLanguage("Spanish");
            }
        });

    }

    private void setDynamicText() {
        final Model model = mController.getModel();

        mAlgorithmChoices.setData(model.getAllAlgorithmNames());
        if (mAlgorithmSelect.getSelectedIndex() != model.getSelectedIndex()) {
            mAlgorithmSelect.setSelectedIndex(model.getSelectedIndex());
        }
        mLblIterationCounterNumber.setText(String.valueOf(model.getIterationCount()));

        mLblIsSorted.setText(model.getSortedMessage());
        mFrame.repaint();
    }

    private void setMenuText(final String newLanguage) {
        switch (newLanguage) {
            case "Spanish":
                mMenuSettings.setText("Configuraciones");
                mMenuItemSampleCount.setText("Tamaño de la muestra");
                mMenuLanguage.setText("Idioma");
                mMenuItemEnglish.setText("Inglés");
                mMenuItemSpanish.setText("Español");
                break;
            case "English":
            default:
                mMenuSettings.setText("Settings");
                mMenuItemSampleCount.setText("Sample size");
                mMenuLanguage.setText("Language");
                mMenuItemEnglish.setText("English");
                mMenuItemSpanish.setText("Spanish");
                break;
        }
    }

    private void setButtonStates() {
        mBtnIterate.setEnabled(mController.getModel().isIterateEnabled());
        mBtnPause.setEnabled(mController.getModel().isPauseEnabled());
        mBtnPlay.setEnabled(mController.getModel().isPlayEnabled());
        mFrame.repaint();
    }

    private void setStaticLabels(final String language) {
        switch (language) {
            case "Spanish":
                mLblIterationCounter.setText("Iteración:");
                break;
            case "english":
            default:
                mLblIterationCounter.setText("Iteration: ");
                break;
        }
    }


    private GridBagConstraints buildConstraints(final int offset, final int gridX, final int gridY) {
        final GridBagConstraints ret = new GridBagConstraints();
        ret.insets = new Insets(offset, offset, offset, offset);
        ret.fill = GridBagConstraints.BOTH;
        ret.gridx = gridX;
        ret.gridy = gridY;
        return ret;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid. From:
     * https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
     *
     * @param path The path to the image, typically something like "/images/file_name.png".
     * @return A new image icon, or null on error.
     */
    protected ImageIcon createImageIcon(final String path) {
        final java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        }
        else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void onDynamicTextChanged() {
        setDynamicText();
    }

    @Override
    public void onLocalizationChanged(final String newLanguage) {
        setDynamicText();
        setMenuText(newLanguage);
        setStaticLabels(newLanguage);
    }

    @Override
    public void onButtonsChanged() {
        setButtonStates();
    }

    @Override
    public void onSortDataRangeChanged(final int newSize) {
        final Model model = mController.getModel();
        mVisualizer.setDataRange(newSize);
        mLblIterationCounterNumber.setText(String.valueOf(model.getIterationCount()));
        mFrame.repaint();
    }

    @Override
    public void onSortDataUpdated() {
        final Model model = mController.getModel();
        mVisualizer.updateData(model.getDataValues(), model.getDataColors());
        mLblIterationCounterNumber.setText(String.valueOf(model.getIterationCount()));
        mFrame.repaint();
    }

}
