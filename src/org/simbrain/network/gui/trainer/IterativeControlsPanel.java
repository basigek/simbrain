/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.gui.trainer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.trainers.ErrorListener;
import org.simbrain.network.trainers.IterableTrainer;
import org.simbrain.resource.ResourceManager;
import org.simbrain.util.LabelledItemPanel;
import org.simbrain.util.SimpleFrame;
import org.simbrain.util.Utils;
import org.simbrain.util.genericframe.GenericFrame;

/**
 * Component for choosing what kind of supervised learning to use. Can be
 * initialized with a set or trainer types.
 *
 * @author Jeff Yoshimi
 */
public class IterativeControlsPanel extends JPanel {

    /** Reference to trainer object. */
    private final IterableTrainer trainer;

    /** Reference to training set panel. */
    private final TrainingSetPanel trainingSetPanel;

    /** Current number of iterations. */
    private JLabel iterationsLabel = new JLabel("--- ");

       /** Reference to network panel. */
    private final NetworkPanel panel;

    /** Flag for showing updates in GUI. */
    private final JCheckBox showUpdates = new JCheckBox("Show updates");

    /** Parent frame. */
    private GenericFrame parentFrame;

    /** Error progress bar. */
    private JProgressBar errorBar;

    /** Validation progress bar. */
    private JProgressBar validationBar;

    /** Number of "ticks" in progress bars. */
    private int numTicks = 1000;

    /**
     * Construct a rule chooser panel.
     *
     * @param networkPanel the parent network panel
     * @param trainer the trainer this panel represents
     */
    public IterativeControlsPanel(final NetworkPanel networkPanel,
            final IterableTrainer trainer) {

        this.trainer = trainer;
        this.panel = networkPanel;
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        GridBagConstraints controlPanelConstraints = new GridBagConstraints();

        // Run Tools
        JPanel runTools = new JPanel();
        runTools.add(new JButton(runAction));
        runTools.add(new JButton(stepAction));
        runTools.add(showUpdates);
        controlPanelConstraints.weightx = 0.5;
        controlPanelConstraints.gridx = 0;
        controlPanelConstraints.gridy = 0;
        controlPanel.add(runTools, controlPanelConstraints);

        // Separator
        controlPanelConstraints.weightx = 1;
        controlPanelConstraints.weighty = 1;
        controlPanelConstraints.gridx = 0;
        controlPanelConstraints.gridy = 1;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(200, 15));
        // separator.setBackground(Color.red);
        controlPanel.add(separator, controlPanelConstraints);

        // Labels
        LabelledItemPanel labelPanel = new LabelledItemPanel();
        labelPanel.addItem("Iterations:", iterationsLabel);
        numTicks = 10;
        errorBar = new JProgressBar(0, numTicks);
        errorBar.setStringPainted(true);
        labelPanel.addItem("Error:", errorBar);
        validationBar = new JProgressBar(0, numTicks);
        validationBar.setStringPainted(true);
        labelPanel.addItem("Validation Error:", validationBar);
        controlPanelConstraints.weightx = 0.5;
        controlPanelConstraints.gridx = 0;
        controlPanelConstraints.gridy = 2;
        controlPanel.add(labelPanel, controlPanelConstraints);

        // Separator
        controlPanelConstraints.weightx = 0.5;
        controlPanelConstraints.weighty = 0.5;
        controlPanelConstraints.gridx = 0;
        controlPanelConstraints.gridy = 3;
        JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator2.setPreferredSize(new Dimension(200, 15));
        // separator.setBackground(Color.red);
        controlPanel.add(separator2, controlPanelConstraints);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton propertiesButton = new JButton(
                TrainerGuiActions.getPropertiesDialogAction(trainer));
        propertiesButton.setHideActionText(true);
        buttonPanel.add(propertiesButton);
        JButton randomizeButton = new JButton(randomizeAction);
        randomizeButton.setHideActionText(true);
        buttonPanel.add(randomizeButton);
        JButton plotButton = new JButton(TrainerGuiActions.getShowPlotAction(
                networkPanel, trainer));
        plotButton.setHideActionText(true);
        buttonPanel.add(plotButton);
        controlPanelConstraints.weightx = 0.5;
        controlPanelConstraints.gridx = 0;
        controlPanelConstraints.gridy = 4;
        controlPanel.add(buttonPanel, controlPanelConstraints);

        // Set up control panel
        int width = 290;
        int height = 260;
        controlPanel.setMaximumSize(new Dimension(width, height));
        controlPanel.setPreferredSize(new Dimension(width, height));
        controlPanel.setMinimumSize(new Dimension(width, height));

        // Training Set Panel
        if (trainer != null) {
            trainingSetPanel = new TrainingSetPanel(
                    trainer.getTrainableNetwork(), 3);
        } else {
            trainingSetPanel = new TrainingSetPanel();
        }

        // Layout the whole panel
        setLayout(new GridBagLayout());
        GridBagConstraints wholePanelConstraints = new GridBagConstraints();
        // Control Panel
        wholePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        wholePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        wholePanelConstraints.insets = new Insets(10, 10, 10, 10);
        wholePanelConstraints.weightx = 0;
        wholePanelConstraints.weighty = 0.5;
        wholePanelConstraints.gridx = 0;
        wholePanelConstraints.gridy = 0;
        add(controlPanel, wholePanelConstraints);
        // Training Set
        wholePanelConstraints.anchor = GridBagConstraints.PAGE_START;
        wholePanelConstraints.fill = GridBagConstraints.BOTH;
        wholePanelConstraints.insets = new Insets(10, 10, 10, 10);
        wholePanelConstraints.weightx = 1;
        wholePanelConstraints.weighty = 0.5;
        wholePanelConstraints.gridx = 1;
        wholePanelConstraints.gridy = 0;
        add(trainingSetPanel, wholePanelConstraints);

        // Add listener
        if (trainer != null) {
            trainer.addErrorListener(new ErrorListener() {

                public void errorUpdated() {
                    iterationsLabel.setText("" + trainer.getIteration());
                    updateError();
                }

            });
        }

    }

    /**
     * Update the error field.
     */
    private void updateError() {
        errorBar.setValue((int) (numTicks * trainer.getError()));
        validationBar.setValue((int) (numTicks * trainer.getError()));
        errorBar.setString("" + Utils.round(trainer.getError(), 4));
        validationBar.setString("" + Utils.round(trainer.getError(), 4));
    }

    /**
     * A "play" action, that can be used to repeatedly iterate iterable training
     * algorithms.
     *
     */
    Action runAction = new AbstractAction() {

        // Initialize
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon("Play.png"));
            // putValue(NAME, "Open (.csv)");
            // putValue(SHORT_DESCRIPTION, "Import table from .csv");
        }

        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent arg0) {
            if (trainer.isUpdateCompleted()) {
                // Start running
                trainer.setUpdateCompleted(false);
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    public void run() {
                        while (!trainer.isUpdateCompleted()) {
                            trainer.iterate();
                            if (showUpdates.isSelected()) {
                                panel.getNetwork().setUpdateCompleted(false);
                                panel.getNetwork().fireNetworkChanged();
                                while (panel.getNetwork().isUpdateCompleted() == false) {
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        {
                            putValue(SMALL_ICON,
                                    ResourceManager.getImageIcon("Play.png"));
                        }
                    }
                });
                putValue(SMALL_ICON, ResourceManager.getImageIcon("Stop.png"));
            } else {
                // Stop running
                trainer.setUpdateCompleted(true);
                putValue(SMALL_ICON, ResourceManager.getImageIcon("Play.png"));
            }

        }

    };

    /**
     * A step action, for iterating iteratable learning algorithms one time.
     */
    Action stepAction = new AbstractAction() {

        // Initialize
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon("Step.png"));
            // putValue(NAME, "Open (.csv)");
            // putValue(SHORT_DESCRIPTION, "Import table from .csv");
        }

        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent arg0) {
            trainer.apply();

            if (showUpdates.isSelected()) {
                panel.getNetwork().fireNetworkChanged();
            }
        }

    };

    /**
     * Action for randomizing the underlying network.
     */
    Action randomizeAction = new AbstractAction() {

        // Initialize
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon("Rand.png"));
            putValue(NAME, "Randomize");
            putValue(SHORT_DESCRIPTION, "Randomize network");
        }

        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent arg0) {
            if (trainer != null) {
                trainer.randomize();
                panel.getNetwork().fireNetworkChanged();
            }
        }
    };

    /**
     * @param parentFrame the parentFrame to set
     */
    public void setFrame(GenericFrame parentFrame) {
        this.parentFrame = parentFrame;
        trainingSetPanel.setFrame(parentFrame);
    }

    /**
     * Test method for GUI layout.
     */
    public static void main(String args[]) {
        IterativeControlsPanel test = new IterativeControlsPanel(null, null);
        test.errorBar.setValue(5);
        SimpleFrame.displayPanel(test);
    }

}