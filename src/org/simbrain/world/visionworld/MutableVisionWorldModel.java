/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2006 Jeff Yoshimi <www.jeffyoshimi.net>
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
package org.simbrain.world.visionworld;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.simbrain.world.visionworld.filter.UniformFilter;

/**
 * Mutable implementation of VisionWorldModel.
 */
public final class MutableVisionWorldModel
    extends AbstractVisionWorldModel {

    /** Pixel matrix. */
    private PixelMatrix pixelMatrix;

    /** Sensor matrix. */
    private SensorMatrix sensorMatrix;

    /** Empty pixel matrix. */
    private static final PixelMatrix EMPTY_PIXEL_MATRIX = new EmptyPixelMatrix();

    /** Empty sensor matrix. */
    private static final SensorMatrix EMPTY_SENSOR_MATRIX = new EmptySensorMatrix();


    /**
     * Create a new mutable vision world model.
     */
    public MutableVisionWorldModel() {
        super();
        this.pixelMatrix = EMPTY_PIXEL_MATRIX;
        this.sensorMatrix = EMPTY_SENSOR_MATRIX;
    }

    /**
     * Create a new immutable vision world model with the specified
     * pixel matrix and sensor matrix.
     *
     * @param pixelMatrix pixel matrix for this immutable vision world model,
     *    must not be null
     * @param sensorMatrix sensor matrix for this immutable vision world model,
     *    must not be null
     */
    public MutableVisionWorldModel(final PixelMatrix pixelMatrix,
                                     final SensorMatrix sensorMatrix) {
        super();
        if (pixelMatrix == null) {
            throw new IllegalArgumentException("pixelMatrix must not be null");
        }
        if (sensorMatrix == null) {
            throw new IllegalArgumentException("sensorMatrix must not be null");
        }
        this.pixelMatrix = pixelMatrix;
        this.sensorMatrix = sensorMatrix;
        
        pixelMatrix.addListener(listener, true);
    }

    PixelMatrix.Listener listener = new PixelMatrix.Listener() {
        public void imageChanged() {
            firePixelMatrixChanged(pixelMatrix, pixelMatrix);
        }
    };
    
    /** {@inheritDoc} */
    public PixelMatrix getPixelMatrix() {
        return pixelMatrix;
    }

    /** {@inheritDoc} */
    public void setPixelMatrix(final PixelMatrix pixelMatrix) {
        if (pixelMatrix == null) {
            throw new IllegalArgumentException("pixelMatrix must not be null");
        }
        PixelMatrix oldPixelMatrix = this.pixelMatrix;
        this.pixelMatrix = pixelMatrix;
        if (!oldPixelMatrix.equals(this.pixelMatrix)) {
            firePixelMatrixChanged(oldPixelMatrix, this.pixelMatrix);
        }
    }

    /** {@inheritDoc} */
    public SensorMatrix getSensorMatrix() {
        return sensorMatrix;
    }

    /** {@inheritDoc} */
    public void setSensorMatrix(final SensorMatrix sensorMatrix) {
        if (sensorMatrix == null) {
            throw new IllegalArgumentException("sensorMatrix must not be null");
        }
        SensorMatrix oldSensorMatrix = this.sensorMatrix;
        this.sensorMatrix = sensorMatrix;
        if (!oldSensorMatrix.equals(this.sensorMatrix)) {
            fireSensorMatrixChanged(oldSensorMatrix, this.sensorMatrix);
        }
    }

    /**
     * Empty pixel matrix.
     */
    private static class EmptyPixelMatrix
        implements PixelMatrix {

        /** Empty image. */
        private final Image emptyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);


        /** {@inheritDoc} */
        public int getWidth() {
            return 0;
        }

        /** {@inheritDoc} */
        public int getHeight() {
            return 0;
        }

        /** {@inheritDoc} */
        public Image getImage() {
            return emptyImage;
        }

        /** {@inheritDoc} */
        public Color getPixel(final int x, final int y) {
            return null;
        }

        /** {@inheritDoc} */
        public void setPixel(final int x, final int y, final Color color) {
            throw new UnsupportedOperationException("setPixel operation not supported"
                                                    + " by this pixel matrix");
        }

        /** {@inheritDoc} */
        public Image view(final ReceptiveField receptiveField) {
            if (receptiveField == null) {
                throw new IllegalArgumentException("receptiveField must not be null");
            }
            return emptyImage;
        }

        public void addListener(Listener listener, boolean weak) {
            /* no implementation */
        }

        public void removeListener(Listener listener) {
            /* no implementation */
        }
    }

    /**
     * Empty sensor matrix.
     */
    private static class EmptySensorMatrix
        implements SensorMatrix {

        /** {@inheritDoc} */
        public int rows() {
            return 0;
        }

        /** {@inheritDoc} */
        public int columns() {
            return 0;
        }

        /** {@inheritDoc} */
        public int getReceptiveFieldHeight() {
            return 0;
        }

        /** {@inheritDoc} */
        public int getReceptiveFieldWidth() {
            return 0;
        }

        /** {@inheritDoc} */
        public Filter getDefaultFilter() {
            return new UniformFilter(0);
        }

        /** {@inheritDoc} */
        public Sensor getSensor(final int row, final int column) {
            throw new IndexOutOfBoundsException("EmptySensorMatrix has no rows or columns");
        }
    }
}