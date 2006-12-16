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
package org.simbrain.world.visionworld.views;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import org.simbrain.world.visionworld.EditablePixelMatrix;
import org.simbrain.world.visionworld.SensorMatrix;
import org.simbrain.world.visionworld.VisionWorld;
import org.simbrain.world.visionworld.VisionWorldModel;

import org.simbrain.world.visionworld.nodes.EditablePixelMatrixImageNode;
import org.simbrain.world.visionworld.nodes.PixelMatrixImageNode;
import org.simbrain.world.visionworld.nodes.SensorMatrixNode;

/**
 * Stacked view.
 */
public final class StackedView
    extends JPanel {

    /** Canvas. */
    private final StackedViewCanvas canvas;

    /** Vision world. */
    private final VisionWorld visionWorld;

    /** View padding. */
    private static final double VIEW_PADDING = 10.0d;


    /**
     * Create a new stacked view.
     *
     * @param visionWorld vision world, must not be null
     */
    public StackedView(final VisionWorld visionWorld) {
        super();
        if (visionWorld == null) {
            throw new IllegalArgumentException("visionWorld must not be null");
        }
        this.visionWorld = visionWorld;
        canvas = new StackedViewCanvas();
        setLayout(new BorderLayout());
        add("Center", canvas);
    }


    /**
     * Stacked view canvas.
     */
    private class StackedViewCanvas
        extends PCanvas {

        /**
         * Create a new stacked view canvas.
         */
        StackedViewCanvas() {
            super();

            setOpaque(true);
            setBackground(Color.WHITE);
            setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
            setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
            setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
            removeInputEventListener(getPanEventHandler());
            removeInputEventListener(getZoomEventHandler());

            createNodes();
        }


        /**
         * Create nodes.
         */
        private void createNodes() {
            VisionWorldModel model = visionWorld.getModel();
            PLayer layer = getLayer();
            if (model.getPixelMatrix() instanceof EditablePixelMatrix) {
                EditablePixelMatrix editablePixelMatrix = (EditablePixelMatrix) model.getPixelMatrix();
                EditablePixelMatrixImageNode editablePixelMatrixNode = new EditablePixelMatrixImageNode(editablePixelMatrix);
                editablePixelMatrixNode.addInputEventListener(new FocusHandler(editablePixelMatrixNode));
                layer.addChild(editablePixelMatrixNode);
            } else {
                PixelMatrixImageNode pixelMatrixNode = new PixelMatrixImageNode(model.getPixelMatrix());
                layer.addChild(pixelMatrixNode);
            }
            double x = 0.0d;
            double y = 0.0d;
            for (SensorMatrix sensorMatrix : model.getSensorMatrices()) {
                SensorMatrixNode sensorMatrixNode = new SensorMatrixNode(sensorMatrix);
                sensorMatrixNode.addInputEventListener(new MouseoverHighlighter(sensorMatrixNode));
                sensorMatrixNode.setTransparency(0.8f);
                x -= sensorMatrixNode.getWidth() / 10.0d;
                y += sensorMatrixNode.getHeight() / 10.0d;
                sensorMatrixNode.offset(x, y);
                layer.addChild(sensorMatrixNode);
            }
        }

        /**
         * Center camera.
         */
        private void centerCamera() {
            PLayer layer = getLayer();
            PCamera camera = getCamera();
            PBounds fullBounds = layer.getFullBoundsReference();
            PBounds paddedBounds = new PBounds(fullBounds.getX() - VIEW_PADDING,
                                               fullBounds.getY() - VIEW_PADDING,
                                               fullBounds.getHeight() + (2 * VIEW_PADDING),
                                               fullBounds.getWidth() + (2 * VIEW_PADDING));
            camera.animateViewToCenterBounds(paddedBounds, true, 0L);
        }

        /** {@inheritDoc} */
        public void repaint() {
            super.repaint();
            if (getLayer().getChildrenCount() > 0) {
                centerCamera();
            }
        }
    }

    /**
     * Mouseover highlighter.
     */
    private class MouseoverHighlighter
        extends PBasicInputEventHandler {

        /** Node for this mouseover highlighter. */
        private final SensorMatrixNode node;


        /**
        * Create a new mouseover highlighter for the specified node.
        *
        * @param node node
        */
        MouseoverHighlighter(final SensorMatrixNode node) {
            super();
            this.node = node;
        }


        /** {@inheritDoc} */
        public void mouseEntered(final PInputEvent event) {
            node.setTransparency(1.0f);
            node.moveToFront();
        }

        /** {@inheritDoc} */
        public void mouseExited(final PInputEvent event) {
            node.setTransparency(0.8f);
        }
    }

    /**
     * Focus handler.
     */
    private class FocusHandler
        extends PBasicInputEventHandler {

        /** Node for this focus handler. */
        private final EditablePixelMatrixImageNode node;


        /**
         * Create a new focus handler for the specified node.
         *
         * @param node node
         */
        FocusHandler(final EditablePixelMatrixImageNode node) {
            super();
            this.node = node;
            node.setOutlinePaint(Color.BLACK);
        }


        /** {@inheritDoc} */
        public void mouseEntered(final PInputEvent event) {
            node.setFocus(true);
            node.moveToFront();
            node.setOutlinePaint(Color.RED);
            node.repaint();
        }

        /** {@inheritDoc} */
        public void mouseExited(final PInputEvent event) {
            node.setFocus(false);
            node.setOutlinePaint(Color.BLACK);
            node.repaint();
        }
    }
}
