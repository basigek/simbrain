/*
 * Copyright (C) 2005,2007 The Authors. See http://www.simbrain.net/credits This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.simbrain.network.connections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.simbrain.network.core.Neuron;
import org.simbrain.network.core.Synapse;
import org.simbrain.network.groups.SynapseGroup;
import org.simbrain.network.util.OrientationComparator;

/**
 * Connect each source neuron to a single target.
 *
 * @author Jeff Yoshimi
 * @author Zach Tosi
 */
public class OneToOne implements ConnectNeurons {

    public static boolean DEFAULT_BIDIRECT_PREF;

    public static OrientationComparator DEFAULT_ORIENTATION =
        OrientationComparator.X_ORDER;

    /**
     * If true, synapses are added in both directions.
     */
    private boolean useBidirectionalConnections = false;

    /** Orientation of how to connect neurons. */
    private OrientationComparator connectOrientation = DEFAULT_ORIENTATION;

    /**
     * Use this connection object to make connections.
     *
     * @param sourceNeurons the starting neurons
     * @param targetNeurons the targeted neurons
     * @return the new synapses
     */
    public List<Synapse> connectOneToOne(List<Neuron> sourceNeurons,
        final List<Neuron> targetNeurons) {
        return connectOneToOne(sourceNeurons, targetNeurons,
            useBidirectionalConnections, true);
    }

    /**
     * Returns a sorted list of neurons, given a comparator.
     *
     * @param neuronList
     *            the base list of neurons.
     * @param comparator
     *            the comparator.
     * @return the sorted list.
     */
    private static List<Neuron> getSortedNeuronList(
        final List<Neuron> neuronList,
        final OrientationComparator comparator) {
        ArrayList<Neuron> list = new ArrayList<Neuron>();
        list.addAll(neuronList);
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * {@inheritDoc}
     * Source and target neuron groups must have the same number of neurons.
     * A synapse is created such that every source neuron is connected to
     * exactly one target neuron (and vice versa if connections are 
     * bidirectional). 
     */
    @Override
    public void connectNeurons(SynapseGroup synGroup) {
        List<Synapse> syns = connectOneToOne(synGroup.getSourceNeurons(),
            synGroup.getTargetNeurons(), useBidirectionalConnections, false);
        for (Synapse s : syns) {
            synGroup.addNewSynapse(s);
        }
    }

    /**
     *
     * @param sourceNeurons the starting neurons
     * @param targetNeurons the targeted neurons
     * @param useBidirectionalConnections the useBidirectionalConnections to set
     * @param looseSynapses 
     * @return
     */
    public static List<Synapse> connectOneToOne(
        final List<Neuron> sourceNeurons, final List<Neuron> targetNeurons,
        final boolean useBidirectionalConnections,
        final boolean looseSynapses) {

        double srcWidth = OrientationComparator.findMaxX(sourceNeurons)
            - OrientationComparator.findMinX(sourceNeurons);
        double srcHeight = OrientationComparator.findMaxY(sourceNeurons)
            - OrientationComparator.findMinY(sourceNeurons);
        double tarWidth = OrientationComparator.findMaxX(targetNeurons)
            - OrientationComparator.findMinX(targetNeurons);
        double tarHeight = OrientationComparator.findMaxY(targetNeurons)
            - OrientationComparator.findMinY(targetNeurons);

        // Sort by axis of maximal variance
        boolean srcSortX = srcWidth > srcHeight;
        boolean tarSortX = tarWidth > tarHeight;

        OrientationComparator srcComparator;
        OrientationComparator tarComparator;

        // srcSortX XOR tarSortX means that one should be sorted vertically
        // and the other horizonally.
        if (srcSortX != tarSortX) {
            double midpointXSrc = OrientationComparator
                .findMidpointX(sourceNeurons);
            double midpointXTar = OrientationComparator
                .findMidpointX(targetNeurons);
            double midpointYSrc = OrientationComparator
                .findMidpointY(sourceNeurons);
            double midpointYTar = OrientationComparator
                .findMidpointY(targetNeurons);

            if (srcSortX) { // source is horizontal
                // Go over source in regular or reverse order based on the
                // relative positions of the source and target midpoints.
                srcComparator =
                    midpointXSrc > midpointXTar
                        ? OrientationComparator.X_REVERSE
                        : OrientationComparator.X_ORDER;
                // Go over target in regular or reverse order based on the
                // relative positions of the source and target midpoints.
                tarComparator =
                    midpointYSrc > midpointYTar
                        ? OrientationComparator.Y_ORDER
                        : OrientationComparator.Y_REVERSE;
            } else {// source is vertical
                srcComparator =
                    midpointYSrc > midpointYTar
                        ? OrientationComparator.Y_REVERSE
                        : OrientationComparator.Y_ORDER;
                tarComparator =
                    midpointXSrc > midpointXTar
                        ? OrientationComparator.X_ORDER
                        : OrientationComparator.X_REVERSE;
            }
        } else {
            // Either we are sorting both vertically or both horizontally...
            srcComparator = srcSortX ? OrientationComparator.X_ORDER
                : OrientationComparator.Y_ORDER;
            tarComparator = tarSortX ? OrientationComparator.X_ORDER
                : OrientationComparator.Y_ORDER;
        }

        ArrayList<Synapse> syns = new ArrayList<Synapse>();

        Iterator<Neuron> targets = getSortedNeuronList(targetNeurons,
            tarComparator).iterator();

        for (Iterator<Neuron> sources = getSortedNeuronList(sourceNeurons,
            srcComparator).iterator(); sources.hasNext();) {
            Neuron source = sources.next();
            if (targets.hasNext()) {
                Neuron target = targets.next();
                Synapse synapse = new Synapse(source, target);
                if (looseSynapses) {
                    source.getNetwork().addSynapse(synapse);
                }
                syns.add(synapse);
                // Allow neurons to be connected back to source.
                if (useBidirectionalConnections) {
                    Synapse espanys = new Synapse(target, source);
                    if (looseSynapses) {
                        source.getNetwork().addSynapse(espanys);
                    }
                    syns.add(espanys);
                }
            } else {
                break;
            }
        }
        return syns;

    }

    /**
     * @return the useBidirectionalConnections
     */
    public boolean isUseBidirectionalConnections() {
        return useBidirectionalConnections;
    }

    /**
     * @param useBidirectionalConnections
     *            the useBidirectionalConnections to set
     */
    public void setUseBidirectionalConnections(
        final boolean useBidirectionalConnections) {
        this.useBidirectionalConnections = useBidirectionalConnections;
    }

    /**
     * @return the connectOrientation
     */
    public OrientationComparator getConnectOrientation() {
        return connectOrientation;
    }

    /**
     * @param connectOrientation
     *            the connectOrientation to set
     */
    public void setConnectOrientation(
        final OrientationComparator connectOrientation) {
        this.connectOrientation = connectOrientation;
    }

    /**
     * Used for populating combo box with orientation types.
     *
     * @return Array of connection types.
     */
    public static OrientationComparator[] getOrientationTypes() {
        return OrientationComparator.values();
    }

    /**
     * Returns a short name for this connection type, used in combo boxes.
     *
     * @return the name for this connection type
     */
    public static String getName() {
        return "One to one";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

}