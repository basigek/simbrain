
package org.simnet.interfaces;

import java.util.EventListener;

/**
 * Model listener.
 */
public interface NetworkListener extends EventListener {

    /**
     * Notify this listener of a NeuronChanged event.
     *
     * @param e event
     */
    void neuronChanged(NetworkEvent e);

    /**
     * Notify this listener of a Neuron added event.
     *
     * @param e event
     */
    void neuronAdded(NetworkEvent e);

    /**
     * Notify this listener of a Neuron removed event.
     *
     * @param e event
     */
    void neuronRemoved(NetworkEvent e);
    
    /**
     * Notify this listener of a Neuron removed event.
     *
     * @param e event
     */
    void synapseRemoved(NetworkEvent e);
    
    /**
     * Notify this listener of a Neuron removed event.
     *
     * @param e event
     */
    void synapseAdded(NetworkEvent e);
    
    /**
     * Notify this listener of a Neuron removed event.
     *
     * @param e event
     */
    void synapseChanged(NetworkEvent e);
}