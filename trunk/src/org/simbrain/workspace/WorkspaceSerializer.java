/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2003 Jeff Yoshimi <www.jeffyoshimi.net>
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


package org.simbrain.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.simbrain.coupling.*;
import org.simbrain.gauge.GaugeFrame;
import org.simbrain.network.NetworkFrame;
import org.simbrain.network.pnodes.PNodeNeuron;
import org.simbrain.util.Utils;
import org.simbrain.world.Agent;
import org.simbrain.world.WorldFrame;


/**
 * WorkspaceSerializer handles workspace persistence.  It contains static methods for reading and writing
 * workspace files, and also serves as a buffer for Castor initialization.
 */
public class WorkspaceSerializer {
	
	private static final String FS = System.getProperty("file.separator");
	
	//Holders for unmarshalling
	private ArrayList networkList = new ArrayList();
	private ArrayList worldList = new ArrayList();
	private ArrayList gaugeList = new ArrayList();
	private CouplingList couplingList = new CouplingList();
	
	/**
	 * Read in workspace file
	 * 
	 * @param wspace reference to current workspace
	 * @param f file containing new workspace information
	 */
	public static void readWorkspace(Workspace wspace, File f) {
		wspace.clearWorkspace();
		WorkspaceSerializer w_serializer = new WorkspaceSerializer();
		try {
			Reader reader = new FileReader(f);
			Mapping map = new Mapping();
			map.loadMapping("." + FS + "lib" + FS + "workspace_mapping.xml");
			Unmarshaller unmarshaller = new Unmarshaller(w_serializer);
			unmarshaller.setMapping(map);
			//unmarshaller.setDebug(true);
			w_serializer = (WorkspaceSerializer)unmarshaller.unmarshal(reader);
			
			// For each network n
			//	coupling_list.getCouplings(n)
			//	Go through networks
			//	    getCouplings which match that network's name
			//			create couplings with that network (from name), that neuron (from name), and that agent (from name)
			//		setCouplings(coupling)
			
		} catch (java.io.FileNotFoundException e) {
		    JOptionPane.showMessageDialog(null, "Could not read workspace file \n"
			        + f, "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();       
		    return;
		} catch (NullPointerException e){
		    JOptionPane.showMessageDialog(null, "Could not find workspace file \n"
			        + f, "Warning", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		catch (Exception e){
		    e.printStackTrace();
		    return;
		}
		for(int i = 0; i < w_serializer.getWorldList().size(); i++) {
			WorldFrame wld = (WorldFrame)w_serializer.getWorldList().get(i);
			wld.init();
			wld.setWorkspace(wspace);
			wld.setBounds(wld.getXpos(), wld.getYpos(), wld.getThe_width(), wld.getThe_height());
			wld.readWorld(new File(wld.getGenericPath()));		
			wspace.addWorld(wld);
		}

		for(int i = 0; i < w_serializer.getNetworkList().size(); i++) {
			NetworkFrame net = (NetworkFrame)w_serializer.getNetworkList().get(i);
			net.init();
			net.setWorkspace(wspace);
			net.setBounds(net.getXpos(), net.getYpos(), net.getThe_width(), net.getThe_height());
			net.getNetPanel().open(new File(net.getGenericPath()));
			wspace.addNetwork(net);
		}

		for(int i = 0; i < w_serializer.getGaugeList().size(); i++) {
			GaugeFrame gauge = (GaugeFrame)w_serializer.getGaugeList().get(i);
			gauge.init();
			gauge.setWorkspace(wspace);
			gauge.setBounds(gauge.getXpos(), gauge.getYpos(), gauge.getThe_width(), gauge.getThe_height());
			//gauge.readWorld(new File(wld.getGenericPath()));		
			wspace.addGauge(gauge);
		}
		
		//
		//Create couplings
		//
		CouplingList couplings = w_serializer.getCouplingList();	
		
		// For each stored coupling description
		for (int i = 0; i < couplings.size(); i++) {
			Coupling c = couplings.getCoupling(i);
			for(int j = 0; j < wspace.getNetworkList().size(); j++) {
				NetworkFrame net = (NetworkFrame)wspace.getNetworkList().get(j);
				// if the network name matches
				if(net.getNetPanel().getCurrentFile().getName().equals(c.getNetworkName())) {
					for(int k = 0; k < net.getNetPanel().getPNodeNeurons().size(); k++) {
						PNodeNeuron pn = (PNodeNeuron)net.getNetPanel().getPNodeNeurons().get(k);
						// and the neuron name matches, create a coupling
						if(c.getNeuronName().equals(pn.getNeuron().getId())) {
							if (c instanceof MotorCoupling) {
								MotorCoupling new_coupling = new MotorCoupling(pn, ((MotorCoupling)c).getCommandArray());
								new_coupling.setWorldName(c.getWorldName());
								new_coupling.setAgentName(c.getAgentName());
								pn.setMotorCoupling(new_coupling);
							} else if (c instanceof SensoryCoupling) {
								SensoryCoupling new_coupling = new SensoryCoupling(pn, ((SensoryCoupling)c).getSensorArray());
								new_coupling.setWorldName(c.getWorldName());
								new_coupling.setAgentName(c.getAgentName());
								pn.setSensoryCoupling(new_coupling);
							}
							break;
						}
					}
				}
			}
		}
		
		//Now attach agents to couplings
		CouplingList couplings2 = wspace.getCouplingList();		
		// For each stored coupling description
		for (int i = 0; i < couplings2.size(); i++) {
			Coupling c = couplings2.getCoupling(i);
			for(int j = 0; j < wspace.getWorldList().size(); j++) {
				WorldFrame wld = (WorldFrame)wspace.getWorldList().get(j);
				// if the world name matches
				if(wld.getWorld().getName().equals(c.getWorldName())) {
					for(int k = 0; k < wld.getAgentList().size(); k++) {
						Agent a = (Agent)wld.getAgentList().get(k);
						// and the agent name matches, add this agent to the coupling
						if (c.getAgentName().equals(a.getName())) {
							c.setAgent(a);
							c.getAgent().getParent().addCommandTarget(c.getNeuron().getParentPanel());
							break;
						}
					}
				}
			}
		}
			
		wspace.repaintAllNetworkPanels();
		wspace.setTitle(f.getName());

	}
	
	
	/**
	 * Reads in a simulation file, which is essentially two or three lines,
	 * containing the names of a network, a world, and a gauge file, respectively.  The gauge
	 * file can be omitted.  This method calls the read methods in the network, world, and gauge 
	 * packages.
	 * 
	 * @param theFile the simulation file to be read
	 */
	public static void readWorkspaceOld(Workspace ws, File theFile) {
		
		ws.clearWorkspace();
		
		FileInputStream f = null;
		String line = null;
		try {
			f = new FileInputStream(theFile);
		}catch (java.io.FileNotFoundException e) {
		    JOptionPane.showMessageDialog(null, "Could not read simulation file \n"
			        + f, "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();       
		    return;
		} catch (NullPointerException e){
		    JOptionPane.showMessageDialog(null, "Could not find simulation file \n"
			        + f, "Warning", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		catch (Exception e){
		    e.printStackTrace();
		    return;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(f));

		if (f == null) {
			return;
		}

		String localDir = new String(System.getProperty("user.dir"));		
		
		//Read in network file
		try {
			line = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("br.readLine");
		}

		line.replace('/', FS.charAt(0));	// For windows machines..
	    File netFile = new File(localDir + line);
	    NetworkFrame network = new NetworkFrame(ws);
		network.getNetPanel().open(netFile);
		ws.addNetwork(network);

		//Read in world file
		try {
			line = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("br.readLine");
		}
		
		line.replace('/', FS.charAt(0));	// For windows machines..	
		File worldFile = new File(localDir + line);
		WorldFrame world = new WorldFrame(ws);
		world.readWorldOld(worldFile);
		ws.addWorld(world);
		
		// Gauge files not currently dealt with
		//		do {
		//			try {
		//				line = br.readLine();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//			if (line != null) {
		//				line.replace('/', FS.charAt(0));	// For windows machines..	
		//				File gaugeFile = new File(localDir + line);
		//				netPanel.addGauge(gaugeFile);
		//				
		//			}
		//		} while(line != null);
		
	}

	/**
	 * Save workspace information
	 * 
	 * @param ws reference to current workspace
	 * @param theFile file to save information to
	 */
	public static void writeWorkspace(Workspace ws, File theFile) {

		WorkspaceSerializer serializer = new WorkspaceSerializer();
		
		for(int i = 0; i < ws.getNetworkList().size(); i++) {
			NetworkFrame net = (NetworkFrame)ws.getNetworkList().get(i);
			net.initBounds();
			net.getNetPanel().getNetwork().updateIds();
		}
		for(int i = 0; i < ws.getWorldList().size(); i++) {
			WorldFrame wld = (WorldFrame)ws.getWorldList().get(i);
			wld.initBounds();
		}
		for(int i = 0; i < ws.getGaugeList().size(); i++) {
			GaugeFrame gauge = (GaugeFrame)ws.getGaugeList().get(i);
			gauge.initBounds();
		}
		
		
		serializer.setNetworkList(ws.getNetworkList());
		serializer.setWorldList(ws.getWorldList());
		serializer.setGaugeList(ws.getGaugeList());
		serializer.setCouplingList(ws.getCouplingList());
		serializer.getCouplingList().initCastor();
		
		LocalConfiguration.getInstance().getProperties().setProperty("org.exolab.castor.indent", "true");
	
		try {
			FileWriter writer = new FileWriter(theFile);
			Mapping map = new Mapping();
			map.loadMapping("." + FS + "lib" + FS + "workspace_mapping.xml");
			Marshaller marshaller = new Marshaller(writer);
			marshaller.setMapping(map);
			//marshaller.setDebug(true);
			marshaller.marshal(serializer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ws.setTitle(theFile.getName());
		
	}

	
	/**
	 * Writes a simulation file which contains two lines containing the names of a network, 
	 * and a world file.  Gauge files are not currently written.
	 * This method calls the write methods in the network and world packages
	 * 
	 * @param simFile The file to be written to
	 */
	public static void writeWorkspaceOld(Workspace ws, File simFile) {
		
		FileOutputStream f = null;

		try {
			f = new FileOutputStream(simFile);
		} catch (Exception e) {
			System.out.println("Could not open file stream: " + e.toString());
		}

		if (f == null) {
			return;
		}

		PrintStream ps = new PrintStream(f);
		String localDir = new String(System.getProperty("user.dir"));

		// Get relative path for network file
		// TODO: Replace with for loop through network files
		NetworkFrame network = ws.getLastNetwork();
		String absoluteNetPath = network.getNetPanel().getCurrentFile().getAbsolutePath();
		String relativeNetPath = Utils.getRelativePath(localDir, absoluteNetPath);
		//Save network file
		ps.println("" + relativeNetPath);

		// Get relative path for world file
		WorldFrame world = ws.getLastWorld();
		String absoluteWldPath = world.getCurrentFile().getAbsolutePath();
		String relativeWldPath = Utils.getRelativePath(localDir, absoluteWldPath);
		//Save world file		
		ps.println("" + relativeWldPath);
		
		ps.close();
		//System.gc();
				
		// Note Gauge data not currently saved
		

	}


	////////////////////////////

	/**
	 * @return Returns the networkList.
	 */
	public ArrayList getNetworkList() {
		return networkList;
	}
	/**
	 * @param networkList The networkList to set.
	 */
	public void setNetworkList(ArrayList networkList) {
		this.networkList = networkList;
	}
	/**
	 * @return Returns the worldList.
	 */
	public ArrayList getWorldList() {
		return worldList;
	}
	/**
	 * @param worldList The worldList to set.
	 */
	public void setWorldList(ArrayList worldList) {
		this.worldList = worldList;
	}
	/**
	 * @return Returns the gaugeList.
	 */
	public ArrayList getGaugeList() {
		return gaugeList;
	}
	/**
	 * @param gaugeList The gaugeList to set.
	 */
	public void setGaugeList(ArrayList gaugeList) {
		this.gaugeList = gaugeList;
	}
	/**
	 * @return Returns the couplingList.
	 */
	public CouplingList getCouplingList() {
		return couplingList;
	}
	/**
	 * @param couplingList The couplingList to set.
	 */
	public void setCouplingList(CouplingList couplingList) {
		this.couplingList = couplingList;
	}

	
}
