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
package org.simbrain.network.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

import org.simbrain.network.groups.GeneRec;
import org.simbrain.network.gui.actions.ClampNeuronsAction;
import org.simbrain.network.gui.actions.ClampWeightsAction;
import org.simbrain.network.gui.dialogs.NetworkDialog;
import org.simbrain.network.gui.dialogs.connect.ConnectionDialog;
import org.simbrain.network.gui.dialogs.neuron.NeuronDialog;
import org.simbrain.network.gui.dialogs.synapse.SynapseDialog;
import org.simbrain.network.gui.dialogs.text.TextDialog;
import org.simbrain.network.gui.filters.Filters;
import org.simbrain.network.gui.nodes.ModelGroupNode;
import org.simbrain.network.gui.nodes.NeuronNode;
import org.simbrain.network.gui.nodes.ScreenElement;
import org.simbrain.network.gui.nodes.SelectionHandle;
import org.simbrain.network.gui.nodes.SourceHandle;
import org.simbrain.network.gui.nodes.SubnetworkNode;
import org.simbrain.network.gui.nodes.SynapseNode;
import org.simbrain.network.gui.nodes.TextObject;
import org.simbrain.network.gui.nodes.TimeLabel;
import org.simbrain.network.gui.nodes.UpdateStatusLabel;
import org.simbrain.network.gui.nodes.ViewGroupNode;
import org.simbrain.network.gui.nodes.modelgroups.GeneRecNode;
import org.simbrain.network.gui.nodes.subnetworks.ActorCriticNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.BackpropNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.CompetitiveNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.ElmanNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.HopfieldNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.KwtaNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.LMSNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.SOMNode;
import org.simbrain.network.gui.nodes.subnetworks.StandardNetworkNode;
import org.simbrain.network.gui.nodes.subnetworks.WTANetworkNode;
import org.simbrain.network.interfaces.Group;
import org.simbrain.network.interfaces.Network;
import org.simbrain.network.interfaces.NetworkEvent;
import org.simbrain.network.interfaces.NetworkListener;
import org.simbrain.network.interfaces.Neuron;
import org.simbrain.network.interfaces.RootNetwork;
import org.simbrain.network.interfaces.Synapse;
import org.simbrain.network.networks.Backprop;
import org.simbrain.network.networks.Competitive;
import org.simbrain.network.networks.Elman;
import org.simbrain.network.networks.Hopfield;
import org.simbrain.network.networks.KwtaNetwork;
import org.simbrain.network.networks.LMSNetwork;
import org.simbrain.network.networks.SOM;
import org.simbrain.network.networks.StandardNetwork;
import org.simbrain.network.networks.WinnerTakeAll;
import org.simbrain.network.networks.actorcritic.ActorCritic;
import org.simbrain.network.neurons.LinearNeuron;
import org.simbrain.network.util.SimnetUtils;
import org.simbrain.resource.ResourceManager;
import org.simbrain.util.JMultiLineToolTip;
import org.simbrain.util.SimbrainUtils;
import org.simbrain.util.ToggleButton;
import org.simbrain.workspace.Attribute;
import org.simbrain.workspace.AttributeHolder;
import org.simbrain.workspace.ConsumingAttribute;
import org.simbrain.workspace.ProducingAttribute;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Network panel.
 */
public class NetworkPanel extends PCanvas implements NetworkListener {

    /** The model neural-rootNetwork object. */
    private RootNetwork rootNetwork;

    /** Default edit mode. */
    private static final EditMode DEFAULT_BUILD_MODE = EditMode.SELECTION;

    /** Default offset for new points. */
    private static final int DEFAULT_NEWPOINT_OFFSET = 100;

    /** Default spacing for new points. */
    private static final int DEFAULT_SPACING = 45;

    /** Offset for update label. */
    private static final int UPDATE_LABEL_OFFSET = 20;

    /** Offset for time label. */
    private static final int TIME_LABEL_V_OFFSET = 35;

    /** Offset for time label. */
    private static final int TIME_LABEL_H_OFFSET = 10;

    /** Build mode. */
    private EditMode editMode;

    /** Selection model. */
    private NetworkSelectionModel selectionModel;

    /** Action manager. */
    protected NetworkActionManager actionManager;

    /** Cached context menu. */
    private JPopupMenu contextMenu;

    /** Cached alternate context menu. */
    private JPopupMenu contextMenuAlt;

    /** Last clicked position. */
    private Point2D lastClickedPosition;

    /** Tracks number of pastes that have occurred; used to correctly position pasted objects. */
    private double numberOfPastes = 0;

    /** Last selected Neuron. */
    private NeuronNode lastSelectedNeuron = null;

    /** Label which displays current time. */
    private TimeLabel timeLabel;

    /** Label which displays current update script. */
    private UpdateStatusLabel updateStatusLabel;

    /** Reference to bottom JToolBar. */
    private JToolBar southBar;

    /** Show input labels. */
    private boolean inOutMode = true;

    /** Use auto zoom. */
    private boolean autoZoomMode = true;

    /** Show subnet outline. */
    private boolean showSubnetOutline = false;

    /** Show time. */
    private boolean showTime = true;

    /** Main tool bar. */
    private JToolBar mainToolBar;

    /** Edit tool bar. */
    private JToolBar editToolBar;

    /** Clamp tool bar. */
    private JToolBar clampToolBar;

    /** Source neurons. */
    private Collection<NeuronNode> sourceNeurons = new ArrayList<NeuronNode>();

    /** A list of check boxes pertaining to "clamp" information.
     * They are updated when the rootNetwork clamp status changes. */
    protected ArrayList<JCheckBoxMenuItem> checkBoxes = new ArrayList<JCheckBoxMenuItem>();

    /** A list of toggle buttons pertaining to "clamp" information.
     * They are updated when the rootNetwork clamp status changes. */
    private ArrayList<JToggleButton> toggleButton = new ArrayList<JToggleButton>();

    /** Beginning position used in calculating offsets for multiple pastes. */
    private Point2D beginPosition;

    /** End position used in calculating offsets for multiple pastes. */
    private Point2D endPosition;

    /** x-offset for multiple pastes. */
    private double pasteX = 0;

    /** y-offset for multiple pastes. */
    private double pasteY = 0;

    /** Turn GUI on or off. */
    private boolean guiOn = true;

    /** Turn synapse node on or off. */
    private boolean synapseNodeOn = true;

    /** Text object event handler. */
    private TextEventHandler textHandle = new TextEventHandler(this);

    /** Groups nodes together for ease of use. */
    private ViewGroupNode vgn;

    /** Local thread flag for manually starting and stopping the network. */
    private volatile boolean isRunning;

    /**
     * Create a new rootNetwork panel.
     */
    public NetworkPanel(final RootNetwork rootNetwork) {
        super();

        this.rootNetwork = rootNetwork;

        // always render in high quality
        setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);

        editMode = DEFAULT_BUILD_MODE;
        selectionModel = new NetworkSelectionModel(this);
        actionManager = new NetworkActionManager(this);

        createContextMenu();
        // createContextMenuAlt();

        //initialize toolbars
        JPanel toolbars = new JPanel();
        mainToolBar = this.createMainToolBar();
        editToolBar = this.createEditToolBar();
        clampToolBar = this.createClampToolBar();
        // Construct toolbar pane
        FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
        flow.setHgap(0);
        flow.setVgap(0);
        toolbars.setLayout(flow);
        toolbars.add(getMainToolBar());
        toolbars.add(getEditToolBar());
        toolbars.add(getClampToolBar());
        super.setLayout(new BorderLayout());
        this.add("North", toolbars);

        removeDefaultEventListeners();
        addInputEventListener(new PanEventHandler());
        addInputEventListener(new ZoomEventHandler());
        addInputEventListener(new SelectionEventHandler());
        addInputEventListener(textHandle);
        addInputEventListener(new ContextMenuEventHandler());

        rootNetwork.getParent().addListener(this);

        selectionModel.addSelectionListener(new NetworkSelectionListener()
            {
                /** @see NetworkSelectionListener */
                public void selectionChanged(final NetworkSelectionEvent e) {
                    updateSelectionHandles(e);
                }
            });

        // Format the time Label
        timeLabel = new TimeLabel(this);
        timeLabel.offset(TIME_LABEL_H_OFFSET, getCamera().getHeight() - TIME_LABEL_V_OFFSET);
        getCamera().addChild(timeLabel);
        timeLabel.update();

        // Format the updateScript Label
        updateStatusLabel = new UpdateStatusLabel(this);
        updateStatusLabel.offset(TIME_LABEL_H_OFFSET, getCamera().getHeight()
                - UPDATE_LABEL_OFFSET);
        getCamera().addChild(updateStatusLabel);
        updateStatusLabel.update();

        // register support for tool tips
        // TODO:  might be a memory leak, if not unregistered when the parent frame is removed
        ToolTipManager.sharedInstance().registerComponent(this);

        addKeyListener(new NetworkKeyAdapter(this));


    }

    /**
     * Creates a new rootNetwork JMenu.
     *
     * @return the new rootNetwork menu
     */
    protected JMenu createNewNetworkMenu() {
        JMenu newNetMenu = new JMenu("New Network");
        newNetMenu.add(actionManager.getNewActorCriticNetworkAction());
        newNetMenu.add(actionManager.getNewBackpropNetworkAction());
        newNetMenu.add(actionManager.getNewCompetitiveNetworkAction());
//        newNetMenu.add(actionManager.getNewElmanNetworkAction());
        newNetMenu.add(actionManager.getNewHopfieldNetworkAction());
        newNetMenu.add(actionManager.getNewKwtaNetworkAction());
        newNetMenu.add(actionManager.getNewLMSNetworkAction());
        newNetMenu.add(actionManager.getNewSOMNetworkAction());
        newNetMenu.add(actionManager.getNewStandardNetworkAction());
        newNetMenu.add(actionManager.getNewWTANetworkAction());

        return newNetMenu;
    }

    /**
     * Create a new context menu for this rootNetwork panel.
     */
    public void createContextMenu() {

        contextMenu = new JPopupMenu();

        contextMenu.add(actionManager.getNewNeuronAction());
        contextMenu.add(createNewNetworkMenu());
        contextMenu.addSeparator();

        contextMenu.add(actionManager.getSetSourceNeuronsAction());
        contextMenu.add(actionManager.getShowConnectDialogAction());
        contextMenu.addSeparator();

        for (Action action : actionManager.getClipboardActions()) {
            contextMenu.add(action);
        }
        contextMenu.addSeparator();

        contextMenu.add(actionManager.getShowNetworkPreferencesAction());
    }

    /**
     * Return the context menu for this rootNetwork panel.
     *
     * <p>
     * This context menu should return actions that are appropriate for the
     * rootNetwork panel as a whole, e.g. actions that change modes, actions that
     * operate on the selection, actions that add new components, etc.  Actions
     * specific to a node of interest should be built into a node-specific context
     * menu.
     * </p>
     *
     * @return the context menu for this rootNetwork panel
     */
    JPopupMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * Create the main tool bar.
     *
     * @return the toolbar.
     */
    protected JToolBar createMainToolBar() {

        JToolBar mainTools = new JToolBar();

        for (Action action : actionManager.getNetworkModeActions()) {
            mainTools.add(action);
        }
        mainTools.addSeparator();
        mainTools.add(actionManager.getIterateNetworkAction());
        mainTools.add(new ToggleButton(actionManager.getNetworkControlActions()));
        mainTools.addSeparator();
        mainTools.add(actionManager.getClearNeuronsAction());
        mainTools.add(actionManager.getRandomizeObjectsAction());
        mainTools.addSeparator();

        return mainTools;
    }

    /**
     * Create the edit tool bar.
     *
     * @return the toolbar.
     */
    protected JToolBar createEditToolBar() {

        JToolBar editTools = new JToolBar();

        for (Action action : actionManager.getNetworkEditingActions()) {
            editTools.add(action);
        }

        return editTools;
    }

    /**
     * Create the clamp tool bar.
     *
     * @return the tool bar
     */
    protected JToolBar createClampToolBar() {

        JButton button = new JButton();
        button.setIcon(ResourceManager.getImageIcon("Clamp.png"));
        final JPopupMenu menu = new JPopupMenu();
        for (JToggleButton toggle : actionManager.getClampBarActions()) {
            toggle.setText("");
            menu.add(toggle);
            toggleButton.add(toggle);
        }
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton)e.getSource();
                menu.show(button, 0, button.getHeight());
            }
        });

        button.setComponentPopupMenu(menu);
        JToolBar clampTools = new JToolBar();

        clampTools.add(button);
//        JToggleButton cbW = actionManager.getClampWeightsBarItem();
//        toggleButton.add(cbW);
//        clampTools.add(cbW);
//        cbW.setText("");
//        JToggleButton cbN = actionManager.getClampNeuronsBarItem();
//        toggleButton.add(cbN);
//        cbN.setText("");
//        clampTools.add(cbN);

        return clampTools;
    }

    /**
     * Return the align sub menu.
     *
     * @return the align sub menu
     */
    public JMenu createAlignMenu() {

        JMenu alignSubMenu = new JMenu("Align");

        alignSubMenu.add(actionManager.getAlignHorizontalAction());
        alignSubMenu.add(actionManager.getAlignVerticalAction());

        return alignSubMenu;

    }

    /**
     * Return the space sub menu.
     *
     * @return the space sub menu
     */
    public JMenu createSpacingMenu() {

        JMenu spaceSubMenu = new JMenu("Space");

        spaceSubMenu.add(actionManager.getSpaceHorizontalAction());
        spaceSubMenu.add(actionManager.getSpaceVerticalAction());

        return spaceSubMenu;

    }

    /**
     * Remove the default event listeners.
     */
    private void removeDefaultEventListeners() {

        PInputEventListener panEventHandler = getPanEventHandler();
        PInputEventListener zoomEventHandler = getZoomEventHandler();
        removeInputEventListener(panEventHandler);
        removeInputEventListener(zoomEventHandler);
    }


    //
    // bound properties

    /**
     * Return the current edit mode for this rootNetwork panel.
     *
     * @return the current edit mode for this rootNetwork panel
     */
    public EditMode getEditMode() {
        return editMode;
    }

    /**
     * Set the current edit mode for this rootNetwork panel to <code>editMode</code>.
     *
     * <p>This is a bound property.</p>
     *
     * @param editMode edit mode for this rootNetwork panel, must not be null
     */
    public void setEditMode(final EditMode editMode) {

        if (editMode == null) {
            throw new IllegalArgumentException("editMode must not be null");
        }

        EditMode oldEditMode = this.editMode;
        this.editMode = editMode;
        firePropertyChange("editMode", oldEditMode, this.editMode);
        setCursor(this.editMode.getCursor());
    }

    /**
     * Delete selected itemes.
     */
    public void deleteSelectedObjects() {

        for (PNode selectedNode : getSelection()) {
            if (selectedNode instanceof NeuronNode) {
                NeuronNode selectedNeuronNode = (NeuronNode) selectedNode;
                rootNetwork.deleteNeuron(selectedNeuronNode.getNeuron());
            } else if (selectedNode instanceof SynapseNode) {
                SynapseNode selectedSynapseNode = (SynapseNode) selectedNode;
                rootNetwork.deleteSynapse(selectedSynapseNode.getSynapse());
            } else if (selectedNode instanceof TextObject) {
                getLayer().removeChild(selectedNode);
            } else if (selectedNode instanceof SubnetworkNode) {
                SubnetworkNode selectedSubnet = (SubnetworkNode) selectedNode;
                rootNetwork.deleteNetwork(selectedSubnet.getSubnetwork());
            }
        }
    }

    /**
     * Copy to the clipboard.
     */
    public void copy() {
        Clipboard.clear();
        setNumberOfPastes(0);
        setBeginPosition(SimnetUtils.getUpperLeft((ArrayList) getSelectedModelElements()));
        Clipboard.add((ArrayList) this.getSelectedModelElements());
    }


    /**
     * Cut to the clipboard.
     */
    public void cut() {
        copy();
        deleteSelectedObjects();
    }

    /**
     * Paste from the clipboard.
     */
    public void paste() {
        Clipboard.paste(this);
        numberOfPastes++;
    }

    /**
     * Aligns neurons horizontally.
     */
    public void alignHorizontal() {
        double min = Double.MAX_VALUE;
        for (Neuron neuron : getSelectedModelNeurons()) {
            if (neuron.getY() < min) {
                min = neuron.getY();
            }
        }
        for (Neuron neuron : getSelectedModelNeurons()) {
            neuron.setY(min);
        }
        repaint();
    }

    /**
     * Temporary debugging method for model updates.
     */
    public void updateNodesTemp() {
        for (NeuronNode node : getNeuronNodes()) {
            node.pullViewPositionFromModel();
        }
    }

    /**
     * Aligns neurons vertically.
     */
    public void alignVertical() {

        double min = Double.MAX_VALUE;
        for (Neuron neuron : getSelectedModelNeurons()) {
            if (neuron.getX() < min) {
                min = neuron.getX();
            }
        }
        for (Neuron neuron : getSelectedModelNeurons()) {
            neuron.setX(min);
        }
        repaint();
    }

    /**
     * TODO: Push this and related methods to model?
     *
     * Spaces neurons horizontally.
     */
    public void spaceHorizontal() {
        if (getSelectedNeurons().size() <= 1) {
            return;
        }
        ArrayList<Neuron> sortedNeurons = getSelectedModelNeurons();
        Collections.sort(sortedNeurons, new NeuronComparator(NeuronComparator.Type.COMPARE_X));

        double min = sortedNeurons.get(0).getX();
        double max = (sortedNeurons.get(sortedNeurons.size() - 1)).getX();
        double space = (max - min) / (sortedNeurons.size() - 1);

        int i = 0;
        for (Neuron neuron : sortedNeurons) {
            neuron.setX(min + space * i);
            i++;
        }

        repaint();
    }

    /**
     * Spaces neurons vertically.
     */
    public void spaceVertical() {
        if (getSelectedNeurons().size() <= 1) {
            return;
        }
        ArrayList<Neuron> sortedNeurons = getSelectedModelNeurons();
        Collections.sort(sortedNeurons, new NeuronComparator(NeuronComparator.Type.COMPARE_Y));

        double min = sortedNeurons.get(0).getY();
        double max = (sortedNeurons.get(sortedNeurons.size() - 1)).getY();
        double space = (max - min) / (sortedNeurons.size() - 1);

        int i = 0;
        for (Neuron neuron : sortedNeurons) {
            neuron.setY(min + space * i);
            i++;
        }

        repaint();
    }

    /**
     * Creates and displays the neuron properties dialog.
     */
    public void showSelectedNeuronProperties() {
        NeuronDialog dialog = new NeuronDialog(getSelectedNeurons());
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }

    /**
     * Creates and displays the synapse properties dialog.
     */
    public void showSelectedSynapseProperties() {
        SynapseDialog dialog = new SynapseDialog(getSelectedSynapses());
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }

    /**
     * Creates and displays the text properties dialog.
     */
    public void showSelectedTextProperties() {
        TextDialog dialog = new TextDialog(getSelectedText());
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Creates and displays the connect properties dialog.
     */
    public void showConnectProperties() {
        ConnectionDialog dialog = new ConnectionDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Clear the selection.
     */
    public void clearSelection() {
        selectionModel.clear();
        //TODO: Fire rootNetwork changed
    }

    /**
     * Select all elements.
     */
    public void selectAll() {
        setSelection(getSelectableNodes());
    }

    /**
     * Return true if the selection is empty.
     *
     * @return true if the selection is empty
     */
    public boolean isSelectionEmpty() {
        return selectionModel.isEmpty();
    }

    /**
     * Return true if the specified element is selected.
     *
     * @param element element
     * @return true if the specified element is selected
     */
    public boolean isSelected(final Object element) {
        return selectionModel.isSelected(element);
    }

    /**
     * Return the selection as a collection of selected elements.
     *
     * @return the selection as a collection of selected elements
     */
    public Collection<PNode> getSelection() {
        return selectionModel.getSelection();
    }

    /**
     * Set the selection to the specified collection of elements.
     *
     * @param elements elements
     */
    public void setSelection(final Collection elements) {
        selectionModel.setSelection(elements);
    }

    /**
     * Toggle the selected state of the specified element; if
     * it is selected, remove it from the selection, if it is
     * not selected, add it to the selection.
     *
     * @param element element
     */
    public void toggleSelection(final Object element) {
        if (isSelected(element)) {
            selectionModel.remove(element);
        } else {
            selectionModel.add(element);
        }
    }

    /**
     * Add the specified rootNetwork selection listener.
     *
     * @param l rootNetwork selection listener to add
     */
    public void addSelectionListener(final NetworkSelectionListener l) {
        selectionModel.addSelectionListener(l);
    }

    /**
     * Remove the specified rootNetwork selection listener.
     *
     * @param l rootNetwork selection listener to remove
     */
    public void removeSelectionListener(final NetworkSelectionListener l) {
        selectionModel.removeSelectionListener(l);
    }

    /**
     * Update selection handles.
     *
     * @param event the NetworkSelectionEvent
     */
    private void updateSelectionHandles(final NetworkSelectionEvent event) {

        Set<PNode> selection = event.getSelection();
        Set<PNode> oldSelection = event.getOldSelection();

        Set<PNode> difference = new HashSet<PNode>(oldSelection);
        difference.removeAll(selection);

        for (PNode node : difference) {
            SelectionHandle.removeSelectionHandleFrom(node);
        }
        for (PNode node : selection) {
            if (node instanceof ScreenElement) {
                ScreenElement screenElement = (ScreenElement) node;
                if (screenElement.showSelectionHandle()) {
                    SelectionHandle.addSelectionHandleTo(node);
                }
            }
        }
    }

    /**
     * Returns selected Neurons.
     *
     * @return list of selectedNeurons
     */
    public Collection<NeuronNode> getSelectedNeurons() {
        return SimbrainUtils.select(getSelection(), Filters.getNeuronNodeFilter());
    }

    /**
     * Returns selected Synapses.
     *
     * @return list of selected Synapses
     */
    public Collection<SynapseNode> getSelectedSynapses() {
        return SimbrainUtils.select(getSelection(), Filters.getSynapseNodeFilter());
    }

    /**
     * Returns the selected Text objects.
     *
     * @return list of selected Text objects
     */
    public ArrayList<TextObject> getSelectedText() {
        return new ArrayList(SimbrainUtils.select(getSelection(), Filters.getTextObjectFilter()));
    }

    /**
     * Returns selected Neurons.
     *
     * @return list of selectedNeurons
     */
    public ArrayList<Neuron> getSelectedModelNeurons() {
        ArrayList ret = new ArrayList();
        for (PNode e : getSelection()) {
            if (e instanceof NeuronNode) {
                ret.add(((NeuronNode) e).getNeuron());
            }
        }
        return ret;
    }
    
    public ArrayList<ProducingAttribute<?>> getSelectedProducingAttributes() {
        ArrayList<ProducingAttribute<?>> ret = new ArrayList<ProducingAttribute<?>>();
        for (PNode e : getSelection()) {
            if (e instanceof NeuronNode) {
                ret.add(((NeuronNode) e).getNeuron().getDefaultProducingAttribute());
            }
        }
        return ret;
    }
    
    public ArrayList<ConsumingAttribute<?>> getSelectedConsumingAttributes() {
        ArrayList<ConsumingAttribute<?>> ret = new ArrayList<ConsumingAttribute<?>>();
        for (PNode e : getSelection()) {
            if (e instanceof NeuronNode) {
                ret.add(((NeuronNode) e).getNeuron().getDefaultConsumingAttribute());
            }
        }
        return ret;
    }

    /**
     * Returns model rootNetwork elements corresponding to selected screen elements.
     *
     * @return list of selected  model elements
     */
    public Collection getSelectedModelElements() {
        Collection ret = new ArrayList();
        for (PNode e : getSelection()) {
            if (e instanceof NeuronNode) {
                ret.add(((NeuronNode) e).getNeuron());
            } else if (e instanceof SynapseNode) {
                ret.add(((SynapseNode) e).getSynapse());
            } if (e instanceof SubnetworkNode) {
                ret.add(((SubnetworkNode) e).getSubnetwork());
            }
        }
        return ret;
    }

    /**
     * Returns model rootNetwork elements corresponding to selected screen elements.
     *
     * @return list of selected  model elements
     */
    public Collection getCoupledNodes() {
        Collection ret = new ArrayList();
        for (NeuronNode node : getNeuronNodes()) {
            if (node.getNeuron().isInput() || node.getNeuron().isOutput()) {
                ret.add(node);
            }
        }
        return ret;
    }

    /**
     * Return a collection of all neuron nodes.
     *
     * @return a collection of all neuron nodes
     */
    public Collection<ModelGroupNode> getModelGroupNodes() {
        return getLayer().getAllNodes(Filters.getModelGroupNodeFilter(), null);
    }

    /**
     * Return a collection of all neuron nodes.
     *
     * @return a collection of all neuron nodes
     */
    public Collection<NeuronNode> getNeuronNodes() {
        return getLayer().getAllNodes(Filters.getNeuronNodeFilter(), null);
    }

    /**
     * Return a collection of all synapse nodes.
     *
     * @return a collection of all synapse nodes
     */
    public Collection<SynapseNode> getSynapseNodes() {
        return getLayer().getAllNodes(Filters.getSynapseNodeFilter(), null);
    }

    /**
     * Return a collection of all subnet nodes.
     *
     * @return a collection of all subnet nodes
     */
    public Collection getSubnetNodes() {
        return getLayer().getAllNodes(Filters.getSubnetworkNodeFilter(), null);
    }

    
    /**
     * Return a collection of all parent nodes.
     *
     * @return a collection of all p nodes
     */
    public Collection getParentNodes() {
        return getLayer().getAllNodes(Filters.getParentNodeFilter(), null);
    }

    /**
     * Return a collection of all persistent nodes, that is all neuron
     * nodes and all synapse nodes.
     *
     * @return a collection of all persistent nodes
     */
    public Collection<PNode> getPersistentNodes() {
        return getLayer().getAllNodes(Filters.getNeuronOrSynapseNodeFilter(), null);
    }

    /**
     * Return a collection of all persistent nodes, that is all neuron
     * nodes and all synapse nodes.
     *
     * @return a collection of all persistent nodes
     */
    public Collection<ScreenElement> getSelectableNodes() {
        return getLayer().getAllNodes(Filters.getSelectableFilter(), null);
    }
    

    /**
     * Return a collection of all persistent nodes, that is all neuron
     * nodes and all synapse nodes.
     *
     * @return a collection of all persistent nodes
     */
    public Collection<ScreenElement> getSelectedScreenElements() {
        return new ArrayList<ScreenElement>(SimbrainUtils.select(getSelection(), Filters.getSelectableFilter()));
    }

    /**
     * Called by rootNetwork preferences as preferences are changed.  Iterates through screen elemenets
     * and resets relevant colors.
     */
    public void resetColors() {
        setBackground(NetworkGuiSettings.getBackgroundColor());
        for (Object obj : getLayer().getChildrenReference()) {
            if (obj instanceof ScreenElement) {
                ((ScreenElement) obj).resetColors();
            }
        }
        repaint();
    }

    /**
     * Called by rootNetwork preferences as preferences are changed.  Iterates through screen elemenets
     * and resets relevant colors.
     */
    public void resetSynapseDiameters() {
        for (SynapseNode synapse : getSynapseNodes()) {
            synapse.updateDiameter();
        }
        repaint();
    }

    /**
     * Returns information about the rootNetwork in String form.
     *
     * @return String description about this NeuronNode.
     */
    public String toString() {
        String ret = new String();
        for (PNode node : getPersistentNodes()) {
            ret += node.toString();
        }
        return ret;
    }

    /**
     * @return Returns the rootNetwork.
     */
    public RootNetwork getRootNetwork() {
        return rootNetwork;
    }

    /**
     * Used by Castor.
     *
     * @param rootNetwork The rootNetwork to set.
     */
    public void setRootNetwork(final RootNetwork network) {
        this.rootNetwork = network;
    }

    /**
     * @return Returns the lastClickedPosition.
     */
    public Point2D getLastClickedPosition() {
        if (lastClickedPosition == null) {
            lastClickedPosition = new Point2D.Double(DEFAULT_NEWPOINT_OFFSET, DEFAULT_NEWPOINT_OFFSET);
        }
        return lastClickedPosition;
    }

    /**
     * Centers the neural rootNetwork in the middle of the PCanvas.
     */
    public void centerCamera() {
        PCamera camera = getCamera();

        // TODO: Add a check to see if network is running
        if (autoZoomMode && editMode.isSelection()) {
            PBounds filtered = getLayer().getFullBounds();
            PBounds adjustedFiltered = new PBounds(filtered.getX() - 20, filtered.getY() - 20,
                    filtered.getWidth() + 40, filtered.getHeight() + 40);

            camera.animateViewToCenterBounds(adjustedFiltered, true, 0);
        }
    }

    /**
     * Set the last position clicked on screen.
     *
     * @param lastLeftClicked The lastClickedPosition to set.
     */
    public void setLastClickedPosition(final Point2D lastLeftClicked) {
        // If left clicking somewhere assume not multiple pasting, except after the first paste,
        //    when one is setting the offset for a string of pastes
        if (this.getNumberOfPastes() != 1) {
            this.setNumberOfPastes(0);
        }
        this.lastClickedPosition = lastLeftClicked;
    }
    // (Above?) Needed because when resetting num pastes, must rest begin at end of 
    // click, but condition not fulfilled....
    //public boolean resetPasteTrail = false;

    /** @see NetworkListener */
    public void modelCleared(final NetworkEvent e) {
        // empty
    }

    /**
     * Add a new neuron.
     */
    public void addNeuron() {

        Point2D p;
        // If a neuron is selected, put this neuron to its left
        if (getSelectedNeurons().size() == 1) {
            NeuronNode node = (NeuronNode) getSelectedNeurons().toArray()[0];
            p = new Point((int) node.getNeuron().getX() + DEFAULT_SPACING, (int) node.getNeuron().getY());
        } else {
            p = getLastClickedPosition();
            // Put nodes at last left clicked position, if any
            if (p == null) {
                p = new Point(DEFAULT_NEWPOINT_OFFSET, DEFAULT_NEWPOINT_OFFSET);
            }
        }

        LinearNeuron neuron = new LinearNeuron();
        neuron.setX(p.getX());
        neuron.setY(p.getY());
        neuron.setActivation(0);
        getRootNetwork().addNeuron(neuron);
        repaint();
    }

    public void neuronAdded(final NetworkEvent<Neuron> e) {
        neuronAdded(e.getObject());
    }
    
    /** @inheritDoc org.simnet.interfaces.NetworkListener#neuronAdded */
    public void neuronAdded(final Neuron neuron) {
        if (this.findNeuronNode(neuron) != null) {
            return;
        }
        NeuronNode node = getNeuronNode(this, neuron);
        getLayer().addChild(node);
        selectionModel.setSelection(Collections.singleton(node));
    }

    /** @see NetworkListener */
    public void neuronRemoved(final NetworkEvent<Neuron> e) {
        NeuronNode node = findNeuronNode(e.getObject());
        node.removeFromParent();
        centerCamera();
    }

    /** @see NetworkListener */
    public void neuronChanged(final NetworkEvent<Neuron> e) {
        if (e.getOldObject() == null) {
            // The underlying object has not changed
            NeuronNode node = findNeuronNode(e.getObject());
            node.update();
        } else {
            // The underlying object has changed
            NeuronNode node = findNeuronNode(e.getOldObject());
            node.setNeuron(e.getObject());
            node.update();
        }
        resetColors();
    }

    /** @see NetworkListener */
    public void synapseAdded(final NetworkEvent<Synapse> e) {
        synapseAdded(e.getObject());
    }

    /** @see NetworkListener */
    public void synapseAdded(final Synapse synapse) {
        if (this.findSynapseNode(synapse) != null) {
            return;
        }

        NeuronNode source = findNeuronNode(synapse.getSource());
        NeuronNode target = findNeuronNode(synapse.getTarget());
        if ((source == null) || (target == null)) {
            return;
        }

        SynapseNode node = new SynapseNode(this, source, target, synapse);
        getLayer().addChild(node);
        node.moveToBack();
    }

    /** @see NetworkListener */
    public void synapseRemoved(final NetworkEvent<Synapse> e) {
        SynapseNode toDelete = findSynapseNode(e.getObject());
        if (toDelete != null) {
            toDelete.getTarget().getConnectedSynapses().remove(toDelete);
            toDelete.getSource().getConnectedSynapses().remove(toDelete);
            getLayer().removeChild(toDelete);
        }
    }

    /** @see NetworkListener */
    public void groupAdded(final NetworkEvent<Group> e) {

        // Make a list of neuron and synapse nodes
        ArrayList<PNode> nodes = new ArrayList<PNode>();
        for (Network network : e.getObject().getNetworkList()) {
            SubnetworkNode node = this.findSubnetworkNode(network);
            if (node != null) {
                nodes.add(node);
            }
        }
        for (Neuron neuron : e.getObject().getNeuronList()) {
            NeuronNode node = this.findNeuronNode(neuron);
            if (node != null) {
                nodes.add(node);
            }
        }
        for (Synapse synapse : e.getObject().getWeightList()) {
            SynapseNode node = this.findSynapseNode(synapse);
            if (node != null) {
                nodes.add(node);
            }
        }

        // Populate group node and add it
        ModelGroupNode neuronGroup = getModelGroupNodeFromGroup(e.getObject());
        for (PNode node : nodes) {
            neuronGroup.addReference(node);
        }
        this.getLayer().addChild(neuronGroup);
        neuronGroup.updateBounds();
    }

    /**
     * Returns the appropriate ModelGroupNode.
     *
     * @param group the model group
     * @return the ModelGroupNode
     */
    private ModelGroupNode getModelGroupNodeFromGroup(final Group group) {
        ModelGroupNode ret = null;

        if (group instanceof GeneRec) {
            ret = new GeneRecNode(this, (GeneRec) group);
        }
        return ret;
    }


    /** @see NetworkListener */
    public void groupChanged(final NetworkEvent<Group> e) {
        // Not sure if this method works properly
        //  Performance seems to degrade after this method is called
        //  I suppose the proper way is to compare the group before and after
        //  and just change what changed but I'm not sure of the best way to do that
        ModelGroupNode groupNode = findModelGroupNode(e.getObject());
        groupNode.getOutlinedObjects().clear();
        // Make a list of neuron and synapse nodes
        ArrayList<PNode> nodes = new ArrayList<PNode>();
        for (Network network : e.getObject().getNetworkList()) {
            SubnetworkNode node = this.findSubnetworkNode(network);
            if (node != null) {
                nodes.add(node);
            }
        }
        for (Neuron neuron : e.getObject().getNeuronList()) {
            NeuronNode node = this.findNeuronNode(neuron);
            if (node != null) {
                nodes.add(node);
            }
        }
        for (Synapse synapse : e.getObject().getWeightList()) {
            SynapseNode node = this.findSynapseNode(synapse);
            if (node != null) {
                nodes.add(node);
            }
        }

        // Populate group node and add it
        for (PNode node : nodes) {
            groupNode.addReference(node);
        }
        groupNode.updateBounds();
    }


    /** @see NetworkListener */
    public void groupRemoved(final NetworkEvent<Group> event) {
        ModelGroupNode node = findModelGroupNode(event.getObject());
        node.removeFromParent();
    }

    /** @see NetworkListener */
    public void subnetAdded(final NetworkEvent<Network> e) {
        subnetAdded(e.getObject());
    }
    
    /**
     * Synchronize model and view.
     */
    public void syncToModel() {
        for (Network network : rootNetwork.getNetworkList()) {
            subnetAdded(network);
            for (Neuron neuron : network.getNeuronList()) {
                neuronAdded(neuron);
            }
            for (Synapse synapse : network.getSynapseList()) {
                synapseAdded(synapse);
            }
        }
        for (Neuron neuron : rootNetwork.getNeuronList()) {
            neuronAdded(neuron);
        }
        for (Synapse synapse : rootNetwork.getSynapseList()) {
            synapseAdded(synapse);
        }
    }
    
    public void subnetAdded(final Network network) {

        // Only top-level subnets are added.  Special graphical representation
        //   for subnetworks of subnets is contained in org.simbrain.network.nodes.subnetworks
        if (network.getDepth() > 1) {
            return;
        }

        // Make a list of neuron nodes
        ArrayList<NeuronNode> neuronNodes = new ArrayList<NeuronNode>();
        for (Neuron neuron : network.getFlatNeuronList()) {
            neuronAdded(neuron);
            NeuronNode node = findNeuronNode(neuron);
            if (node != null) {
                neuronNodes.add(node);
            }
        }

        // Find the upper left corner of these nodes and created sbunetwork node
        Point2D upperLeft = getUpperLeft(neuronNodes);
        SubnetworkNode subnetwork = getSubnetworkNodeFromSubnetwork(upperLeft,
                network);

        // Populate subnetwork node and add it
        for (NeuronNode node : neuronNodes) {
            node.translate(-upperLeft.getX()
                    + SubnetworkNode.OUTLINE_INSET_WIDTH, -upperLeft.getY()
                    + SubnetworkNode.OUTLINE_INSET_HEIGHT
                    + SubnetworkNode.TAB_HEIGHT);
            // node.pushViewPositionToModel();
            subnetwork.addChild(node);
        }
        this.getLayer().addChild(subnetwork);
        subnetwork.init();

        // Add synapses
        for (Synapse synapse : network.getFlatSynapseList()) {
            synapseAdded(synapse);
            SynapseNode node = findSynapseNode(synapse);
            if (node != null) {
                this.getLayer().addChild(node);
                node.moveToBack();
            }
        }
        clearSelection();
    }

    /**
     * Convert a subnetwork into a subnetwork node.
     * 
     * @param upperLeft
     *            for intializing location of subnetworknode
     * @param subnetwork
     *            the subnetwork itself
     * @return the subnetworknode
     */
    private SubnetworkNode getSubnetworkNodeFromSubnetwork(final Point2D upperLeft, final Network subnetwork) {
        SubnetworkNode ret = null;

        if (subnetwork instanceof ActorCritic) {
            ret = new ActorCriticNetworkNode(this, (ActorCritic) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof Backprop) {
            ret = new BackpropNetworkNode(this, (Backprop) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof Competitive) {
            ret = new CompetitiveNetworkNode(this, (Competitive) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof LMSNetwork) {
            ret = new LMSNetworkNode(this, (LMSNetwork) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof Elman) {
            ret = new ElmanNetworkNode(this, (Elman) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof SOM) {
            ret = new SOMNode(this, (SOM) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof Hopfield) {
            ret = new HopfieldNetworkNode(this, (Hopfield) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof WinnerTakeAll) {
            ret = new WTANetworkNode(this, (WinnerTakeAll) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof StandardNetwork) {
            ret = new StandardNetworkNode(this, (StandardNetwork) subnetwork, upperLeft.getX(), upperLeft.getY());
        } else if (subnetwork instanceof KwtaNetwork) {
            ret = new KwtaNetworkNode(this, (KwtaNetwork) subnetwork, upperLeft.getX(), upperLeft.getY());
        }
        return ret;
    }

    /**
     * Find the upper left corner of the subnet nodes.
     *
     * @param neuronList the set of neurons to check
     * @return the upper left corner
     */
    private Point2D getUpperLeft(final ArrayList neuronList) {
        double x = Double.MAX_VALUE;
        double y = Double.MAX_VALUE;
        for (Iterator neurons = neuronList.iterator(); neurons.hasNext(); ) {
            NeuronNode neuronNode = (NeuronNode) neurons.next();
            if (neuronNode.getNeuron().getX() < x) {
                x = neuronNode.getNeuron().getX();
            }
            if (neuronNode.getNeuron().getY() < y) {
                y = neuronNode.getNeuron().getY();
            }
        }
        return new Point2D.Double(x, y);
    }

    /** @see NetworkListener */
    public void subnetRemoved(final NetworkEvent<Network> e) {
        SubnetworkNode subnet = this.findSubnetworkNode(e.getObject());
        if (subnet != null) {
            this.getLayer().removeChild(subnet);
        }
        centerCamera();
    }

    /** @see NetworkListener */
    public void couplingChanged(final NetworkEvent<Neuron> e) {
        NeuronNode changed = findNeuronNode(e.getObject());
        changed.updateInLabel();
        changed.updateOutLabel();
    }

    /** @see NetworkListener */
    public void synapseChanged(final NetworkEvent<Synapse> e) {
        if (e.getOldObject() != null) {
            // The underlying object has changed
            findSynapseNode(e.getOldObject()).setSynapse(e.getObject());
        }
        resetColors();
    }

    /** @see NetworkListener */
    public void neuronMoved(final NetworkEvent<Neuron> e) {
        NeuronNode node = this.findNeuronNode(e.getObject());
        if ((node != null) && (!node.isMoving())) {
            node.pullViewPositionFromModel();
        }
     }

    /**
     * Returns the view model group corresponding to the model group, or null if not found.
     *
     * @param group the group to look for
     * @return the corresponding model group
     */
    public ModelGroupNode findModelGroupNode(final Group group) {
        for (ModelGroupNode modelGroup : this.getModelGroupNodes()) {
            if (modelGroup.getGroup() == group) {
                return modelGroup;
            }
        }
        return null;
    }

    /**
     * Find the NeuronNode corresponding to a given model Neuron.
     *
     * @param n the model neuron.
     * @return the correonding NeuronNode.
     */
    public NeuronNode findNeuronNode(final Neuron n) {
        for (Iterator i = getNeuronNodes().iterator(); i.hasNext(); ) {
            NeuronNode node = ((NeuronNode) i.next());
            if (n == node.getNeuron()) {
                return node;
            }
        }
        return null;
    }

    /**
     * Find the SynapseNode corresponding to a given model Synapse.
     *
     * @param s the model synapse.
     * @return the corresponding SynapseNode.
     */
    public SynapseNode findSynapseNode(final Synapse s) {
        for (Iterator i = getSynapseNodes().iterator(); i.hasNext(); ) {
            SynapseNode node = ((SynapseNode) i.next());
            if (s == node.getSynapse()) {
                return node;
            }
        }
        return null;
    }

    /**
     * Find the SubnetworkNode corresponding to a given model subnetwork.
     *
     * @param net the model subnetwork.
     * @return the corresponding subnetwork nodes, null otherwise.
     */
    public SubnetworkNode findSubnetworkNode(final Network net) {
        for (Iterator i = this.getSubnetNodes().iterator(); i.hasNext(); ) {
            SubnetworkNode node = ((SubnetworkNode) i.next());
            if (node.getSubnetwork().getId().equalsIgnoreCase(net.getId())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Ungroup specified object.
     *
     * @param vgn the group to remove.
     * @param selectConstituents whether to select the grouped items or not.
     */
    public void unGroup(final ViewGroupNode vgn , final boolean selectConstituents) {
        for (ScreenElement element : vgn.getGroupedObjects()) {
            element.setPickable(true);
            if (selectConstituents) {
                selectionModel.add(element);
                element.setGrouped(false);
            }
        }
        vgn.removeFromParent();
    }

    /**
     * Create a group of GUI objects.
     */
    public void groupSelectedObjects() {

        ArrayList<ScreenElement> elements = new ArrayList<ScreenElement>();
        ArrayList<ScreenElement> toSearch = new ArrayList<ScreenElement>();

        // Ungroup selected groups
        for (PNode node : this.getSelection()) {
            if (node instanceof ViewGroupNode) {
                unGroup((ViewGroupNode) node, false);
                elements.addAll(((ViewGroupNode)node).getGroupedObjects());
            } else {
                if (node instanceof ScreenElement) {
                    toSearch.add((ScreenElement) node);
                }
            }
         }

        // Now group all elements.
        for (ScreenElement element : toSearch) {
            if (element.isDraggable()) {
                elements.add(element);
                element.setGrouped(true);
            }
        }
        
        
        vgn = new ViewGroupNode(this, elements);
        this.getLayer().addChild(vgn);
        this.setSelection(Collections.singleton(vgn));
    }

    /**
     * @param lastSelectedNeuron The lastSelectedNeuron to set.
     */
    public void setLastSelectedNeuron(final NeuronNode lastSelectedNeuron) {
        this.lastSelectedNeuron = lastSelectedNeuron;
    }

    /**
     * @return Returns the lastSelectedNeuron.
     */
    public NeuronNode getLastSelectedNeuron() {
        return lastSelectedNeuron;
    }


    /**
     * Return height bottom toolbar is taking up.
     * 
     * @return height bottom toolbar is taking up
     */
    private double getToolbarOffset() {
        if (southBar != null) {
            return southBar.getHeight();
        }
        return 0;
    }

    /** @see PCanvas */
    public void repaint() {
        super.repaint();

        if (timeLabel != null) {
            timeLabel.setBounds(TIME_LABEL_H_OFFSET, getCamera().getHeight() - getToolbarOffset(),
                                timeLabel.getHeight(), timeLabel.getWidth());
        }

        if (updateStatusLabel != null) {
            updateStatusLabel.setBounds(TIME_LABEL_H_OFFSET, getCamera().getHeight() - getToolbarOffset(),
                    updateStatusLabel.getHeight(), updateStatusLabel.getWidth());
        }

        if ((rootNetwork != null) && (getLayer().getChildrenCount() > 0)
                && (!editMode.isPan())) {
            centerCamera();
        }
    }

    /**
     * @return Auto zoom mode.
     */
    public boolean getAutoZoomMode() {
        return autoZoomMode;
    }

    /**
     * @param autoZoomMode Auto zoom mode.
     */
    public void setAutoZoomMode(final boolean autoZoomMode) {
        this.autoZoomMode = autoZoomMode;
        repaint();
    }

    /**
     * @param inOutMode The in out mode to set.
     */
    public void setInOutMode(final boolean inOutMode) {
        this.inOutMode = inOutMode;
        for (Iterator i = getCoupledNodes().iterator(); i.hasNext(); ) {
            NeuronNode node = (NeuronNode) i.next();
            node.updateInLabel();
            node.updateOutLabel();
        }
        repaint();
    }

    /**
     * @return Returns the in out mode.
     */
    public boolean getInOutMode() {
        return inOutMode;
    }

    /**
     * @return Returns the numberOfPastes.
     */
    public double getNumberOfPastes() {
        return numberOfPastes;
    }

    /**
     * @param numberOfPastes The numberOfPastes to set.
     */
    public void setNumberOfPastes(final double numberOfPastes) {
        this.numberOfPastes = numberOfPastes;
    }

    /**
     * @return Returns show subnet outline.
     */
    public boolean getShowSubnetOutline() {
        return showSubnetOutline;
    }

    /**
     * @param showSubnetOutline Sets Show subnet outline.
     */
    public void setShowSubnetOutline(final boolean showSubnetOutline) {
        this.showSubnetOutline = showSubnetOutline;
    }

    /**
     * @return Returns Show time.
     */
    public boolean getShowTime() {
        return showTime;
    }

    /**
     * @param showTime Sets the show time.
     */
    public void setShowTime(final boolean showTime) {
        this.showTime = showTime;
        timeLabel.setVisible(showTime);
    }

    /**
     * Update the rootNetwork, gauges, and world. This is where the main control
     * between components happens. Called by world component (on clicks), and
     * the rootNetwork-thread.
     */
    public void networkChanged() {

    	//TODO: Remove the below and does all this always need to happen in updates?
        if (guiOn == false) {
            timeLabel.update(); // Show time only
            return;
        }

        for (PNode node : this.getPersistentNodes()) {
            if (node instanceof NeuronNode) {
                NeuronNode neuronNode = (NeuronNode) node;
                neuronNode.update();
            } else if (node instanceof SynapseNode) {
                if (node.getVisible()) {
                    SynapseNode synapseNode = (SynapseNode) node;
                    synapseNode.updateColor();
                    synapseNode.updateDiameter();
                }
            }
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                timeLabel.update();
            }
        });
        rootNetwork.getParent().setChangedSinceLastSave(true);  
        repaint();
   }

    /**
     * Update clamp toolbar buttons and menu items.
     */
    public void clampBarChanged() {
        for (Iterator j = toggleButton.iterator(); j.hasNext(); ) {
            JToggleButton box = (JToggleButton) j.next();
            if (box.getAction() instanceof ClampWeightsAction) {
                box.setSelected(rootNetwork.getClampWeights());
            } else if (box.getAction() instanceof ClampNeuronsAction) {
                box.setSelected(rootNetwork.getClampNeurons());
            }
        }
    }

    /**
     * Update clamp toolbar buttons and menu items.
     */
    public void clampMenuChanged() {
        for (Iterator j = checkBoxes.iterator(); j.hasNext(); ) {
            JCheckBoxMenuItem box = (JCheckBoxMenuItem) j.next();
            if (box.getAction() instanceof ClampWeightsAction) {
                box.setSelected(rootNetwork.getClampWeights());
            } else if (box.getAction() instanceof ClampNeuronsAction) {
                box.setSelected(rootNetwork.getClampNeurons());
            }
        }
    }

    /**
     * Increases neuron and synapse activation levels.
     */
    public void incrementSelectedObjects() {
        for (Iterator i = getSelection().iterator(); i.hasNext(); ) {
            PNode node = (PNode) i.next();
            if (node instanceof NeuronNode) {
                NeuronNode neuronNode = (NeuronNode) node;
                neuronNode.getNeuron().incrementActivation();
            } else if (node instanceof SynapseNode) {
                SynapseNode synapseNode = (SynapseNode) node;
                synapseNode.getSynapse().incrementWeight();
            }
        }
    }

    /**
     * Decreases neuron and synapse activation levels.
     */
    public void decrementSelectedObjects() {
        for (Iterator i = getSelection().iterator(); i.hasNext(); ) {
            PNode node = (PNode) i.next();
            if (node instanceof NeuronNode) {
                NeuronNode neuronNode = (NeuronNode) node;
                neuronNode.getNeuron().decrementActivation();
                neuronNode.update();
            } else if (node instanceof SynapseNode) {
                SynapseNode synapseNode = (SynapseNode) node;
                synapseNode.getSynapse().decrementWeight();
                synapseNode.updateColor();
                synapseNode.updateDiameter();
            }
        }
    }

    /**
     * Nudge selected object.
     *
     * @param offsetX amount to nudge in the x direction (multipled by nudgeAmount)
     * @param offsetY amount to nudge in the y direction (multipled by nudgeAmount)
     */
    protected void nudge(final int offsetX, final int offsetY) {
        for (Iterator i = getSelectedNeurons().iterator(); i.hasNext(); ) {
            NeuronNode node = (NeuronNode) i.next();
            node.getNeuron().setX(node.getNeuron().getX() + (offsetX
                    * NetworkGuiSettings.getNudgeAmount()));
            node.getNeuron().setY(node.getNeuron().getY() + (offsetY
                    * NetworkGuiSettings.getNudgeAmount()));        }
        repaint();
    }

    /**
     * Close model rootNetwork.
     */
    public void closeNetwork() {
        getRootNetwork().getParent().removeListener(this);
    }

    /**
     * @return Returns the edit tool bar.
     */
    public JToolBar getEditToolBar() {
        return editToolBar;
    }


    /**
     * @return Returns the main tool bar.
     */
    public JToolBar getMainToolBar() {
        return mainToolBar;
    }

    /**
     * @return the clamp tool bar.
     */
    public JToolBar getClampToolBar() {
        return clampToolBar;
    }


    /**
     * Set source neurons to selected neurons.
     */
    public void setSourceNeurons() {
        for (NeuronNode node : sourceNeurons) {
            SourceHandle.removeSourceHandleFrom(node);
        }
        sourceNeurons = this.getSelectedNeurons();
        for (NeuronNode node : sourceNeurons) {
            SourceHandle.addSourceHandleTo(node);
        }
    }

    /**
     * Turns the displaying of synapses on and off (for performance increase or visual clarity).
     *
     * @param synapseNodeOn turn synapse nodes on boolean
     */
    public void setSynapseNodesOn(final boolean synapseNodeOn) {
        this.synapseNodeOn = synapseNodeOn;
        actionManager.getShowNodesAction().setState(synapseNodeOn);

        if (synapseNodeOn) {
            for (Iterator synapseNodes = this.getSynapseNodes().iterator(); synapseNodes
                    .hasNext();) {
                SynapseNode node = (SynapseNode) synapseNodes.next();
                node.setVisible(true);
            }
        } else {
            for (Iterator synapseNodes = this.getSynapseNodes().iterator(); synapseNodes
                    .hasNext();) {
                SynapseNode node = (SynapseNode) synapseNodes.next();
                node.setVisible(false);
            }
        }
    }

    /**
     * @return Returns the sourceNeurons.
     */
    public Collection<NeuronNode> getSourceNeurons() {
        return sourceNeurons;
    }
    
    /**
     * @return the model source neurons (used in connecting groups of neurons)
     */
    public ArrayList<Neuron> getSourceModelNeurons() {
        ArrayList<Neuron> ret = new ArrayList<Neuron>();
        for (NeuronNode neuronNode : sourceNeurons) {
            ret.add(neuronNode.getNeuron());
        }
        return ret;
    }
    

     /**
     * Set the offset used in multiple pastes.
     */
    public void setPasteDelta() {
        if ((beginPosition != null) && (endPosition != null)) {
            setPasteX(beginPosition.getX() - endPosition.getX());
            setPasteY(beginPosition.getY() - endPosition.getY());
            //System.out.println("-->" + getPasteX() + " , " + getPasteY());
        }
    }

    /**
     * @return Returns the beginPosition.
     */
    public Point2D getBeginPosition() {
        return beginPosition;
    }

    /**
     * @param beginPosition The beginPosition to set.
     */
    public void setBeginPosition(final Point2D beginPosition) {
        //System.out.println("Begin position: " + beginPosition);
        this.beginPosition = beginPosition;
    }


    /**
     * @return Returns the endPosition.
     */
    public Point2D getEndPosition() {
        return endPosition;
    }


    /**
     * @param endPosition The endPosition to set.
     */
    public void setEndPosition(final Point2D endPosition) {
        //System.out.println("End position: " + endPosition);
        this.endPosition = endPosition;
        if (this.getNumberOfPastes() == 1) {
            setPasteDelta();
        }
    }


    /**
     * @param pasteX pasteX to set.
     */
    public void setPasteX(final double pasteX) {
        this.pasteX = pasteX;
    }


    /**
     * @return pasteX. pasteX.
     */
    public double getPasteX() {
        return pasteX;
    }


    /**
     * @param pasteY paste_y to set.
     */
    public void setPasteY(final double pasteY) {
        this.pasteY = pasteY;
    }


    /**
     * @return pasteY pasteY;
     */
    public double getPasteY() {
        return pasteY;
    }


    /**
     * @return Returns the guiOn.
     */
    public boolean isGuiOn() {
        return guiOn;
    }


    /**
     * @param guiOn The guiOn to set.
     */
    public void setGuiOn(final boolean guiOn) {
        actionManager.getShowGUIAction().setState(guiOn);
        if (guiOn) {
            for (Iterator iter = this.getLayer().getAllNodes().iterator(); iter.hasNext(); ) {
                PNode pnode = (PNode) iter.next();
                pnode.setTransparency((float)1);
            }
        } else {
            for (Iterator iter = this.getLayer().getAllNodes().iterator(); iter.hasNext(); ) {
                PNode pnode = (PNode) iter.next();
                pnode.setTransparency((float).6);
            }

        }
        this.guiOn = guiOn;
    }

    /**
     * Overridden so that multi-line tooltips can be used.
     */
    public JToolTip createToolTip() {
        return new JMultiLineToolTip();
    }

    /**
     * @return turn synapse nodes on.
     */
    public boolean isSynapseNodesOn() {
        return synapseNodeOn;
    }


    /**
     * @param synapseNodeOn turn synapse nodes on.
     */
    public void setNodesOn(final boolean synapseNodeOn) {
        this.synapseNodeOn = synapseNodeOn;
    }

    /**
     * @return the actionManager
     */
    public NetworkActionManager getActionManager() {
        return actionManager;
    }

    /**
     * @return the contextMenuAlt.
     */
    public JPopupMenu getContextMenuAlt() {
        return contextMenuAlt;
    }

    /**
     * @return the updateStatusLabel.
     */
    public UpdateStatusLabel getUpdateStatusLabel() {
        return updateStatusLabel;
    }


    /**
     * @return the textHandle.
     */
    public TextEventHandler getTextHandle() {
        return textHandle;
    }

    /**
     * @return View Group Node.
     */
    public ViewGroupNode getViewGroupNode() {
        return vgn;
    }

    /**
     * {@inheritDoc}
     */
    public void componentUpdated() {
        /* no implementation */
    }


    /**
     * {@inheritDoc}
     */
    public void setTitle(String name) {
        /* no implementation */
    }


    /**
     * {@inheritDoc}
     */
    public void attributeRemoved(AttributeHolder parent, Attribute attribute) {
        /* no implementation */
    }

    /**
     * Returns a NetworkDialog. Overriden by NetworkPanelDesktop, which returns
     * a NetworkDialog with additional features used in Desktop version of Simbrain.
     *
     * @param networkPanel network panel
     * @return subclass version of network dialog.
     */
    public NetworkDialog getNetworkDialog(final NetworkPanel networkPanel) {
        return new NetworkDialog(networkPanel);
    }
    
    
    /**
     * Returns a neuron node. Overriden by NetworkPanelDesktop, which returns
     * a NeuronNode with additional features used in Desktop version of Simbrain.
     *
     * @param netPanel network panel.
     * @param neuron logical neuron this node represents
     */
    public NeuronNode getNeuronNode(final NetworkPanel net, final Neuron neuron) {
        return new NeuronNode(net, neuron);
    }

}
