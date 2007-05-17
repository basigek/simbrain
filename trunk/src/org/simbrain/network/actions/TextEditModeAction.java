/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005-2006 Jeff Yoshimi <www.jeffyoshimi.net>
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

package org.simbrain.network.actions;

import javax.swing.KeyStroke;

import org.simbrain.network.EditMode;
import org.simbrain.network.NetworkPanel;
import org.simbrain.resource.ResourceManager;

/**
 * Text edit mode action.
 */
public class TextEditModeAction extends EditModeAction {

    /**
     * Create a new text edit mode action with the specified network panel.
     *
     * @param networkPanel network panel, must not be null
     */
    public TextEditModeAction(final NetworkPanel networkPanel) {
        super("Text", networkPanel, EditMode.TEXT);
        putValue(SMALL_ICON, ResourceManager.getImageIcon("Text.png"));
        putValue(SHORT_DESCRIPTION, "Text Mode (t)");

        networkPanel.getInputMap().put(KeyStroke.getKeyStroke('t'), this);
        networkPanel.getActionMap().put(this, this);
    }

}
