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
package org.simbrain.console;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JInternalFrame;

import org.simbrain.workspace.Workspace;
import org.simbrain.workspace.WorkspaceComponent;
import org.simbrain.workspace.WorkspaceComponentListener;

import bsh.Interpreter;
import bsh.util.JConsole;

/**
 * Component corresponding to a beanshell window.
 */
public class ConsoleComponent extends  WorkspaceComponent<WorkspaceComponentListener> {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public ConsoleComponent(String name) {
        super(name);
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getFileExtension() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void open(File openFile) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void save(File saveFile) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void update() {
        // TODO Auto-generated method stub
        
    }
}
