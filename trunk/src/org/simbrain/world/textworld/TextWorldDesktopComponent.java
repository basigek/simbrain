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
package org.simbrain.world.textworld;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.simbrain.workspace.gui.GuiComponent;
import org.simbrain.workspace.gui.GenericFrame;

/**
 * <b>TextWorldComponent</b> is the container for the world component.   Handles toolbar buttons, and serializing of world
 * data.  The main environment codes is in {@link TextWorld}.
 */
public class TextWorldDesktopComponent extends GuiComponent<TextWorldComponent> implements ActionListener {

    private static final long serialVersionUID = 1L;

    /** Instance of world of type TextWorld. */
    private TextWorld world;

    /** Menu Bar. */
    private JMenuBar menuBar = new JMenuBar();

    /** File menu for saving and opening world files. */
    private JMenu file = new JMenu("File  ");

    /** Opens an existing world file. */
    private JMenuItem open = new JMenuItem("Open");

    /** Saves the world. */
    private JMenuItem save = new JMenuItem("Save");

    /** Saves the world as a new file name. */
    private JMenuItem saveAs = new JMenuItem("Save As");

    /** Closes the current world. */
    private JMenuItem close = new JMenuItem("Close");

    /** Edit menu Item. */
    private JMenu edit = new JMenu("Edit  ");

    /** Opens the dialog to define TextWorld Dictionary. */
    private JMenu dictionary = new JMenu("Dictionary");

    /** Opens the dialog to define TextWorld Dictionary. */
    private JMenuItem loadDictionary = new JMenuItem("Load dictionary");

    /** Opens user preferences dialog. */
    private JMenuItem preferences = new JMenuItem("Preferences");

    /** Opens the help dialog for TextWorld. */
    private JMenu help = new JMenu("Help");

    /** Instance of the TextWorld dictionary. */
    private Dictionary theDictionary;

    /**
     * Creates a new frame of type TextWorld.
     * @param ws Workspace to add frame to
     */
    public TextWorldDesktopComponent(GenericFrame frame, TextWorldComponent component) {
        super(frame, component);
        this.setPreferredSize(new Dimension(450,400));
        theDictionary = new Dictionary(this);
        init();
    }

    /**
     * Creates instance of text frame and sets parameters.
     */
    private void init() {
        world = new TextWorld(this);
        addMenuBar();
        add(world);
        getParentFrame().pack();
    }

    /**
     * Adds menu bar to the top of TextWorldComponent.
     */
    private void addMenuBar() {
        open.addActionListener(this);
        open.setActionCommand("open");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        save.addActionListener(this);
        save.setActionCommand("save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveAs.addActionListener(this);
        saveAs.setActionCommand("saveAs");
        close.addActionListener(this);
        close.setActionCommand("close");
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menuBar.add(file);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.add(close);

        loadDictionary.addActionListener(this);
        loadDictionary.setActionCommand("loadDictionary");
        loadDictionary.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        preferences.addActionListener(this);
        preferences.setActionCommand("prefs");
        preferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menuBar.add(edit);
        dictionary.add(loadDictionary);
        edit.add(dictionary);
        edit.add(preferences);

        menuBar.add(help);

        getParentFrame().setJMenuBar(menuBar);
    }

    /**
     * Gets a particular instance of TextWorld.
     * @return TextWorld
     */
    public TextWorld getWorld() {
        return world;
    }

    /**
     * Responds to action events.
     * @param arg0 ActionEvent
     */
    public void actionPerformed(final ActionEvent arg0) {
        Object o = arg0.getActionCommand();
        if (o == "prefs") {
            world.showTextWorldDialog();
        }
        if (o == "loadDictionary") {
            theDictionary.loadDictionary();
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

}