package org.simbrain.workspace;

/**
 * Interface for workspace component listeners.
 * 
 * @author Matt Watson
 */
public interface WorkspaceComponentListener {
    /**
     * Called when the target workspace component is updated.
     */
    void componentUpdated();
    
    void setName(String name);
}