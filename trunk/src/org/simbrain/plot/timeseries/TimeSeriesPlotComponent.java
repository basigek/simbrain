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
package org.simbrain.plot.timeseries;

import java.awt.EventQueue;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.simbrain.plot.ChartListener;
import org.simbrain.workspace.Consumer;
import org.simbrain.workspace.Producer;
import org.simbrain.workspace.WorkspaceComponent;

/**
 * Represents time series data.
 * 
 * TODO:    Ability to add and remove TimeSeriesConsumers
 *          Custom component listener to reflect number of consumers
 *          Ability to reset the plot.
 */
public class TimeSeriesPlotComponent extends WorkspaceComponent<ChartListener> {

    /** The data model. */
    private TimeSeriesModel model;

    /** Maximum iteration size if this chart is fixed width. */
    private int maxSize = 100;

    /** Whether this chart if fixed width or not. */
    private boolean fixedWidth = true;

    /**
     * Create new time series plot component.
     *
     * @param name name
     */
    public TimeSeriesPlotComponent(final String name) {
        super(name);
        model = new TimeSeriesModel(this);
    }

    /**
     * Creates a new time series component from a specified model.
     * Used in deserializing.
     *
     * @param name chart name
     * @param model chart model
     */
    public TimeSeriesPlotComponent(final String name, final TimeSeriesModel model) {
        super(name);
        this.model = model;
        this.model.setParent(this);
    }

    /**
     * Initializes a JFreeChart with specific number of data sources.
     *
     * @param name name of component
     * @param numDataSources number of data sources to initialize plot with
     */
    public TimeSeriesPlotComponent(final String name, final int numDataSources) {
        super(name);
        model = new TimeSeriesModel(this);
        model.addDataSources(numDataSources);
    }

    /**
     * @return the model.
     */
    public TimeSeriesModel getModel() {
        return model;
    }

    /**
     * Standard method call made to objects after they are deserialized.
     * See:
     * http://java.sun.com/developer/JDCTechTips/2002/tt0205.html#tip2
     * http://xstream.codehaus.org/faq.html
     * 
     * @return Initialized object.
     */
    private Object readResolve() {
        System.out.println("ReadResolve.");
        return this;
    }

    /**
     * Opens a saved time series plot.
     * @param input stream
     * @param name name of file
     * @param format format
     * @return bar chart component to be opened
     */
    public static TimeSeriesPlotComponent open(final InputStream input,
            final String name, final String format) {
        TimeSeriesModel dataModel = (TimeSeriesModel) TimeSeriesModel.getXStream().fromXML(input);
        return new TimeSeriesPlotComponent(name, dataModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final OutputStream output, final String format) {
        TimeSeriesModel.getXStream().toXML(model, output);
    }

    /**
     * Update chart settings. Called, e.g., when things are modified using a
     * dialog.
     */
    public void updateSettings() {
        for (ChartListener listener : this.getListeners()) {
            listener.chartSettingsUpdated();
        }
    }

    @Override
    public boolean hasChangedSinceLastSave() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void closing() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends Consumer> getConsumers() {
        return (List<? extends Consumer>) model.getConsumers();
    }

    @Override
    public List<? extends Producer> getProducers() {
        return Collections.<Producer>emptyList();
    }
    
    @Override
    public void update() {

        // Trim appropriately if fixed width
        if (fixedWidth) {
//            System.out.println("Dataset Size: " + dataset.getSeries(0).getItemCount());
            for (Iterator iterator = model.getDataset().getSeries().iterator(); iterator.hasNext(); ) {
                XYSeries series = (XYSeries) iterator.next();
                if (series.getItemCount() > maxSize) {
                    series.remove(0);
                }
            }
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Add the data
                for (TimeSeriesConsumer consumer : model.getConsumers()) {
                    model.getDataset().getSeries(consumer.getIndex()).add(
                            getWorkspace().getTime(), consumer.getValue());
                }
            }
        });
    }

    /**
     * @return the maxSize
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * @return the fixedWidth
     */
    public boolean isFixedWidth() {
        return fixedWidth;
    }

    /**
     * @param fixedWidth the fixedWidth to set
     */
    public void setFixedWidth(final boolean fixedWidth) {
        this.fixedWidth = fixedWidth;
    }
 
    @Override
    public String getXML() {
        return TimeSeriesModel.getXStream().toXML(model);
    }
}
