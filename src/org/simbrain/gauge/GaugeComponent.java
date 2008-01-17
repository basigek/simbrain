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
package org.simbrain.gauge;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.simbrain.gauge.core.Gauge;
import org.simbrain.gauge.core.Variable;
import org.simbrain.workspace.Consumer;
import org.simbrain.workspace.Coupling;
import org.simbrain.workspace.Producer;
import org.simbrain.workspace.WorkspaceComponent;

/**
 * <b>GaugeComponent</b> wraps a Gauge object in a Simbrain workspace frame, which also
 * stores information about the variables the Gauge is representing.
 */
public class GaugeComponent extends WorkspaceComponent<GaugeComponentListener> {
    /** the static logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(GaugeComponent.class);
    
    /** Consumer list. */
    private Collection<Consumer> consumers = new ArrayList<Consumer>();
    
    /**
     * Creates a new gauge component.
     * 
     * @param name The name of the component.
     */
    public GaugeComponent(final String name) {
        super(name);
    }

    /** Current gauge. */
    private Gauge gauge = new Gauge();
    
    /**
     * Returns the underlying Gauge.
     * 
     * @return The underlying Gauge.
     */
    public Gauge getGauge() {
        return gauge;
    }
    
    /**
     * Returns a properly initialized xstream object.
     * @return the XStream object
     */
//    private XStream getXStream() {
//        XStream xstream = new XStream(new DomDriver());
//        xstream.omitField(Projector.class, "logger");
//        xstream.omitField(Dataset.class, "logger");
//        xstream.omitField(Dataset.class, "distances");
//        xstream.omitField(Dataset.class, "dataset");
//        return xstream;
//    }

    /**
     * Update couplings.
     *
     * @param dims dimensions to update
     */
    public void resetCouplings(final int dims) {
        consumers.clear();
        for (int i = 0; i < dims; i++) {
            consumers.add(new Variable(gauge, this, i));
        }
    }
    
    /**
     * Wires the provided producers to gauge consumers.
     * If the number of producers has changed since the last
     * wire-up or this is the first wire-up, the component
     * the Gauge is refreshed
     * 
     * @param producers The producers too wire up with couplings.
     */
    @SuppressWarnings("unchecked")
    void wireCouplings(final Collection<? extends Producer> producers) {
        /* Handle Coupling wire-up */
        
        LOGGER.debug("wiring " + producers.size() + " producers");
        
        int oldDims = gauge.getDimensions();
        
        int newDims = producers.size();

        resetCouplings(newDims);
        
        Iterator<? extends Producer> producerIterator = producers.iterator();
        
        for (Consumer consumer : consumers) {
            if (producerIterator.hasNext()) {
                Coupling<?> coupling = new Coupling(producerIterator.next()
                    .getDefaultProducingAttribute(), consumer.getDefaultConsumingAttribute());
                getWorkspace().addCoupling(coupling);
            }
        }

        /* If the new data is inconsistent with the old, reset the gauge */
        if (oldDims != newDims) {
            gauge.init(newDims);
            for (GaugeComponentListener listener : getListeners()) {
                listener.dimensionsChanged(newDims);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void update() {
        gauge.updateCurrentState();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileExtension() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(final File openFile) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final File saveFile) {
        // TODO Auto-generated method stub
        
    }
}