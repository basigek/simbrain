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
package org.simbrain.plot.piechart;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;
import org.simbrain.workspace.SingleAttributeConsumer;

/**
 * Represents one possible data value in a pie chart
 */
public class PieDataConsumer extends SingleAttributeConsumer<Double> {

    /** Reference to gauge. */
    private PieChartComponent plot;
        
    /** Name. */
    private final String name;
    
    /** Index. */
    private Integer index;
    
    /** Value. */
    private Double value = new Double(0);
    
    /**
     * Construct a PieDataConsumer.
     * 
     * @param plot
     *            the parent component
     * @param name
     *            the name of this consumer (displayed in the plot)
     */
    public PieDataConsumer(PieChartComponent plot, String name, Integer index) {
        this.plot = plot;
        this.name = name;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(Double val) {
        value = val;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    public Type getType() {
        return Double.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public PieChartComponent getParentComponent() {
        return plot;
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeDescription() {
        return getDescription();
    }
    
    /**
     * Return index.
     *
     * @return index
     */
    public Integer getIndex() {
            return index;
    }
    
    /**
     * Return current value.
     *
     * @return value
     */
    public Double getValue() {
            return value;
    }
    
}
