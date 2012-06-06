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
package org.simbrain.network.gui.dialogs.neuron;

import javax.swing.JTextField;

import org.simbrain.network.core.Network;
import org.simbrain.network.gui.NetworkUtils;
import org.simbrain.network.neuron_update_rules.BinaryRule;
import org.simbrain.util.LabelledItemPanel;

/**
 * <b>BinaryNeuronPanel</b> creates a dialog for setting preferences of binary
 * neurons.
 */
public class BinaryRulePanel extends AbstractNeuronPanel {

    /** Threshold for this neuron. */
    private JTextField tfThreshold = new JTextField();

    /** Bias for this neuron. */
    private JTextField tfBias = new JTextField();

    /** Main tab for neuron prefernces. */
    private LabelledItemPanel mainTab = new LabelledItemPanel();

    /**
     * Creates binary neuron preferences panel.
     */
    public BinaryRulePanel(Network network) {
        super(network);
        this.add(mainTab);
        mainTab.addItem("Threshold", tfThreshold);
        mainTab.addItem("Bias", tfBias);
    }

    /**
     * Populate fields with current data.
     */
    public void fillFieldValues() {
        BinaryRule neuronRef = (BinaryRule) ruleList.get(0);

        tfThreshold.setText(Double.toString(neuronRef.getThreshold()));
        tfBias.setText(Double.toString(neuronRef.getBias()));

        // Handle consistency of multiple selections
        if (!NetworkUtils.isConsistent(ruleList, BinaryRule.class,
                "getThreshold")) {
            tfThreshold.setText(NULL_STRING);
        }
        if (!NetworkUtils.isConsistent(ruleList, BinaryRule.class, "getBias")) {
            tfBias.setText(NULL_STRING);
        }
    }

    /**
     * Fill field values to default values for binary neuron.
     */
    public void fillDefaultValues() {
        BinaryRule neuronRef = new BinaryRule();
        tfThreshold.setText(Double.toString(neuronRef.getThreshold()));
        tfBias.setText(Double.toString(neuronRef.getBias()));
    }

    /**
     * Called externally when the dialog is closed, to commit any changes made.
     */
    public void commitChanges() {
        for (int i = 0; i < ruleList.size(); i++) {
            BinaryRule neuronRef = (BinaryRule) ruleList.get(i);

            if (!tfThreshold.getText().equals(NULL_STRING)) {
                neuronRef
                        .setThreshold(Double.parseDouble(tfThreshold.getText()));
            }
            if (!tfBias.getText().equals(NULL_STRING)) {
                neuronRef.setBias(Double.parseDouble(tfBias.getText()));
            }
        }
    }
}
