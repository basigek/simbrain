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
package org.simbrain.network.groups;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.simbrain.network.interfaces.RootNetwork;
import org.simbrain.network.interfaces.Synapse;

/**
 * A group of synapses.
 */
public class SynapseGroup extends Group {

    /** Set of synapses. */
    private final List<Synapse> synapseList = new CopyOnWriteArrayList<Synapse>();
    
    /**
     * Construct a synapse group from a list of synapses.
     *
     * @param net parent network
     * @param list list of synapses
     */
    public SynapseGroup(final RootNetwork net, final List<Synapse> list) {
        super(net);
        for (Synapse synapse : list) {
            addSynapse(synapse);
        }        
    }

    /**
     * Construct a new synapse group.
     *
     * @param net parent network
     */
    public SynapseGroup(final RootNetwork net) {
        super(net);
    }

    @Override
    public void delete() {
        if (isMarkedForDeletion()) {
            return;
        } else {
            setMarkedForDeletion(true);
        }
        for(Synapse synapse : synapseList) {
            getParentNetwork().removeSynapse(synapse);
        }
        if (hasParentGroup()) {
            if (getParentGroup() instanceof Subnetwork) {
                ((Subnetwork) getParentGroup()).removeSynapseGroup(this);
            } 
            if (getParentGroup().isEmpty() && getParentGroup().isDeleteWhenEmpty()) {
                getParentNetwork().removeGroup(getParentGroup());
            }            
        }
    }
    
    /**
     * @return a list of weights
     */
    public List<Synapse> getSynapseList() {
        return Collections.unmodifiableList(synapseList);
    }

    /**
     * Add a synapse.
     * 
     * @param synapse synapse to add
     * @param fireEvent whether to fire a synapse added event
     */
    public void addSynapse(final Synapse synapse, final boolean fireEvent) {
        synapse.setId(getParentNetwork().getSynapseIdGenerator().getId());
        synapseList.add(synapse);
        synapse.setParentGroup(this);
        if (fireEvent) {
            getParentNetwork().fireSynapseAdded(synapse);            
        }
    }

    /**
     * Add a synapse.
     * 
     * @param synapse synapse to add
     */
    public void addSynapse(final Synapse synapse) {
        addSynapse(synapse, true);
    }
    
    /**
     * Remove the provided synapse.
     *
     * @param toDelete the synapse to delete
     */
    public void removeSynapse(Synapse toDelete) {
        synapseList.remove(toDelete);
        getParentNetwork().fireSynapseRemoved(toDelete);
        getParentNetwork().fireGroupChanged(this, this, "synapseRemoved");
        if (isEmpty() && isDeleteWhenEmpty()) {
            delete();
        }
    }

    /**
     * Update group. Override for special updating.
     */
    public void update() {
        updateAllSynapses();
    }

    /**
     * Update all synapses.
     */
    public void updateAllSynapses() {
        for (Synapse synapse : synapseList) {
            synapse.update();
        }
    }

    @Override
    public String toString() {
        String ret = new String();
        ret += ("Synapse Group [" + getLabel() + "] Synapse group with "
                + this.getSynapseList().size() + " synapse(s)\n");
        return ret;
    }
    
    @Override
    public boolean isEmpty() {
        return synapseList.isEmpty();
    }
}