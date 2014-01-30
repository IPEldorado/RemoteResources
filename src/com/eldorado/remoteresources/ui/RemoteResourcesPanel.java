/*
 * (C) Copyright 2014 Instituto de Pesquisas Eldorado (http://www.eldorado.org.br/).
 *
 * This file is part of the software Remote Resources
 *
 * All rights reserved. This file and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package com.eldorado.remoteresources.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.android.client.screencapture.CaptureManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.actions.AddDeviceAction;
import com.eldorado.remoteresources.ui.model.LocalhostDevicesWrapper;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * The remote resource main UI.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class RemoteResourcesPanel extends JFrame {

	private static final long serialVersionUID = 3608919633692686901L;

	public static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(1024, 768);

	private BasePanel leftContainer;

	private JTabbedPane viewContainer;

	private BasePanel rightContainer;

	private BasePanel buttonBar;

	private BasePanel menuBar;

	private BasePanel remoteDeviceViewArea;

	private DevicesContainer localDevicesListArea;

	private GridBagConstraints localDevicesConstraints;

	private DevicesContainer remoteDevicesListArea;

	private GridBagConstraints remoteDevicesConstraints;

	private HostsContainer hostListArea;

	private GridBagConstraints hostsConstraints;

	private final PersistentDeviceModel model = new PersistentDeviceModel();

	static {
		/**
		 * Do not remove this TODO yet... we may need it
		 */
		// TODO: check if the style configuration must be done
		// Font defaultFont =
		// ResourcesLoader.getFontResource("Roboto-Bold").deriveFont(12f);
		//
		// for (Object object :
		// Collections.list(UIManager.getLookAndFeelDefaults().keys())) {
		// if(UIManager.getLookAndFeelDefaults().get(object) instanceof
		// FontUIResource){
		// UIManager.getLookAndFeelDefaults().put(object.toString(),
		// defaultFont);
		// }
		// }
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// do nothing
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// do nothing
			e.printStackTrace();
		} catch (InstantiationException e) {
			// do nothing
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// do nothing
			e.printStackTrace();
		}
	}

	public RemoteResourcesPanel() {
		initComponents();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setTitle("Remote Resources");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setSize(MINIMUM_WINDOW_SIZE);
		setMinimumSize(MINIMUM_WINDOW_SIZE);

		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int option = JOptionPane
						.showOptionDialog(
								null,
								RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_ExitMessage),
								"Remote Resources",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE,
								null,
								new String[] {
										RemoteResourcesLocalization
												.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_ExitMessageYes),
										RemoteResourcesLocalization
												.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_ExitMessageNo) },
								null);

				if (option == JOptionPane.YES_OPTION) {
					model.save(getDeviceListFile());
					ClientConnectionManager.getInstance().close();
					RemoteResourcesConfiguration.getInstance().store();
					System.exit(0);
				}
			}
		});
		ClientConnectionManager.getInstance().addClientChangedListener(
				new LocalhostDevicesWrapper(model));
		model.load(getDeviceListFile());
	}

	private void initComponents() {
		createContainers();
		addLeftPanelHeader();
		createViews();
		createMenuBar();
		pack();
	}

	private void createContainers() {
		// create left and right panels and add them
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));

		leftContainer = new BasePanel();
		leftContainer.setLayout(new BorderLayout());
		leftContainer.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
		leftContainer.setPreferredSize(new Dimension(280, Integer.MAX_VALUE));
		leftContainer.setMinimumSize(new Dimension(280, 0));

		rightContainer = new BasePanel();
		rightContainer.setLayout(new BorderLayout());

		getContentPane().add(leftContainer);
		getContentPane().add(rightContainer);

		remoteDeviceViewArea = new BasePanel();

		buttonBar = new ButtonBarPanel();

		rightContainer.add(remoteDeviceViewArea);
		rightContainer.add(buttonBar, BorderLayout.PAGE_END);
		remoteDeviceViewArea.add(CaptureManager.getInstance().getCanvas());

	}

	private void addLeftPanelHeader() {

		BasePanel panel = new BasePanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 4));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		BorderLayout layout = new BorderLayout();
		panel.setLayout(layout);

		JLabel title = new JLabel();
		title.setText(RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_HeaderTitle));
		Font currentFont = title.getFont();
		title.setFont(currentFont.deriveFont(Font.BOLD, 16));
		panel.add(title, BorderLayout.LINE_START);

		JButton addButton = new JButton();
		addButton.setAction(new AddDeviceAction(model));
		addButton.setContentAreaFilled(false);
		addButton.setBorder(BorderFactory.createEmptyBorder());
		panel.add(addButton, BorderLayout.LINE_END);

		leftContainer.add(panel, BorderLayout.PAGE_START);
	}

	private void createViews() {

		createHostView();
		BasePanel hosts = new BasePanel();
		hosts.setLayout(new GridBagLayout());
		hosts.setBackground(null);
		hosts.add(hostListArea, hostsConstraints);

		createDeviceView();
		BasePanel devices = new BasePanel();
		devices.setLayout(new GridBagLayout());
		devices.setBackground(null);
		devices.add(localDevicesListArea, localDevicesConstraints);
		devices.add(remoteDevicesListArea, remoteDevicesConstraints);

		viewContainer = new JTabbedPane();
		viewContainer.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
		viewContainer
				.addTab("Devices",
						ResourcesLoader.getIcon("cellphone", 32),
						devices,
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_Tooltip_Switch2DeviceView));
		viewContainer
				.addTab("Hosts",
						ResourcesLoader.getIcon("computer", 32),
						hosts,
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_Tooltip_Switch2HostView));

		leftContainer.add(viewContainer);
	}

	private void createMenuBar() {
		menuBar = new MenuBarPanel();
		leftContainer.add(menuBar, BorderLayout.PAGE_END);
	}

	private void createHostView() {
		hostListArea = new HostsContainer(model);
		hostsConstraints = new GridBagConstraints();
		hostsConstraints.fill = GridBagConstraints.BOTH;
		hostsConstraints.weighty = 1;
		hostsConstraints.weightx = 1;
		hostsConstraints.gridy = 0;
	}

	private void createDeviceView() {
		localDevicesListArea = new DevicesContainer(
				model,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_LocalTitle));
		localDevicesListArea.setHostToShow(PersistentDeviceModel.LOCALHOST);
		remoteDevicesListArea = new DevicesContainer(
				model,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.RemoteResourcesPanel_UI_RemoteTitle));
		remoteDevicesListArea.setHostToIgnore(PersistentDeviceModel.LOCALHOST);

		localDevicesConstraints = new GridBagConstraints();
		localDevicesConstraints.fill = GridBagConstraints.BOTH;
		localDevicesConstraints.weightx = 1;
		localDevicesConstraints.gridy = 0;

		remoteDevicesConstraints = new GridBagConstraints();
		remoteDevicesConstraints.fill = GridBagConstraints.BOTH;
		remoteDevicesConstraints.weighty = 1;
		remoteDevicesConstraints.weightx = 1;
		remoteDevicesConstraints.gridy = 1;
	}

	private File getDeviceListFile() {
		return new File(RemoteResourcesConfiguration.getRemoteResourcesDir()
				+ File.separator + "devices.cfg");
	}

}
