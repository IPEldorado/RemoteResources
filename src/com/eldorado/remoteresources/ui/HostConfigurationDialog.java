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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.Host;

/**
 * This dialog is responsible for show and configure devices
 * 
 * @author Michel Silva Fornaciali
 * 
 */
public class HostConfigurationDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1135525002778480921L;

	private final Host host;

	private boolean okPressed = false;

	private JButton okButton;

	private BasePanel content = null;

	private GridBagLayout contentLayout = null;

	private int gridy = 0;

	static Point mouseDownCompCoords;

	public HostConfigurationDialog(Host host) {
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		this.host = host;
		initComponents();

		// Methods to move the window
		mouseDownCompCoords = null;

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDownCompCoords = null;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseDownCompCoords = e.getPoint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point currCoords = e.getLocationOnScreen();
				setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y
						- mouseDownCompCoords.y);
			}
		});
	}

	private void initComponents() {
		content = new BasePanel();
		content.setBorderSize(0);
		content.setArc(0);
		content.setBorderColor(Color.BLACK);
		content.setBackground(Color.LIGHT_GRAY);
		content.setOpaque(false);
		content.setLayout(contentLayout = new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Host information
		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostConfigurationDialog_Title));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = gridy++;
		contentLayout.addLayoutComponent(label, constraints);
		content.add(label);

		createInfoSection();

		// Devices
		JLabel label2 = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostConfigurationDialog_DeviceList));
		label2.setFont(label2.getFont().deriveFont(Font.BOLD, 12));

		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.anchor = GridBagConstraints.PAGE_START;
		constraints2.weightx = 1;
		constraints2.gridx = 0;
		constraints2.gridy = gridy++;
		contentLayout.addLayoutComponent(label2, constraints2);
		content.add(label2);

		createDevicesSection();

		// Buttons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		okButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_OK));

		okButton.addActionListener(this);

		okButton.setBackground(Color.LIGHT_GRAY);

		buttonsPanel.add(okButton);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = gridy++;
		constraints.weightx = 1;
		constraints.weighty = 1;
		contentLayout.addLayoutComponent(buttonsPanel, constraints);
		content.add(buttonsPanel);

		setContentPane(content);
	}

	private void createDevicesSection() {

		BasePanel deviceSection = new BasePanel();
		deviceSection.setBorderSize(1);
		deviceSection.setArc(10);
		deviceSection.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		GridBagLayout deviceSectionLayout;
		deviceSection.setLayout(deviceSectionLayout = new GridBagLayout());

		Iterator<Device> i = host.getDevices().iterator();

		if (!i.hasNext()) {
			createInfoLine(
					deviceSection,
					deviceSectionLayout,
					"",
					RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.HostConfigurationDialog_NoDeviceMessage));
		} else {
			while (i.hasNext()) {
				Device d = i.next();

				createInfoLine(
						deviceSection,
						deviceSectionLayout,
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_DeviceName),
						d.getName());
				createInfoLine(
						deviceSection,
						deviceSectionLayout,
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_DeviceSerialNumber),
						d.getSerialNumber());
				createInfoLine(
						deviceSection,
						deviceSectionLayout,
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.DeviceConfigurationDialog_DeviceHost),
						d.getHost());
				if (i.hasNext()) {
					JSeparator separator = new JSeparator();
					GridBagConstraints constraints = new GridBagConstraints();
					constraints.gridx = 0;
					constraints.gridwidth = GridBagConstraints.REMAINDER;
					constraints.fill = GridBagConstraints.HORIZONTAL;
					deviceSectionLayout.addLayoutComponent(separator,
							constraints);
					deviceSection.add(separator);
				}
			}
		}

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = gridy++;
		constraints.weightx = 1;
		constraints.insets = new Insets(4, 4, 4, 4);

		contentLayout.addLayoutComponent(deviceSection, constraints);
		content.add(deviceSection);
	}

	private void createInfoSection() {

		BasePanel infoSection = new BasePanel();
		infoSection.setBorderSize(1);
		infoSection.setArc(10);
		infoSection.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		GridBagLayout infoSectionLayout;
		infoSection.setLayout(infoSectionLayout = new GridBagLayout());

		createInfoLine(
				infoSection,
				infoSectionLayout,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostConfigurationDialog_HostNickname),
				host.getName());
		createInfoLine(
				infoSection,
				infoSectionLayout,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostConfigurationDialog_HostAddress),
				host.getHostname());
		createInfoLine(
				infoSection,
				infoSectionLayout,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.HostConfigurationDialog_HostPort),
				Integer.toString(host.getPort()));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = gridy++;
		constraints.weightx = 1;
		constraints.insets = new Insets(4, 4, 4, 4);

		contentLayout.addLayoutComponent(infoSection, constraints);
		content.add(infoSection);
	}

	private JLabel createInfoLine(JPanel parent, GridBagLayout parentLayout,
			String label, String content) {
		JLabel infoLineLabel = new JLabel(label);
		infoLineLabel.setFont(infoLineLabel.getFont().deriveFont(Font.BOLD,
				infoLineLabel.getFont().getSize() + 2));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(2, 2, 2, 2);
		parentLayout.addLayoutComponent(infoLineLabel, constraints);
		parent.add(infoLineLabel);

		JLabel infoLineText = new JLabel(content);
		infoLineText.setFont(infoLineText.getFont().deriveFont(
				(float) (infoLineText.getFont().getSize() + 2)));
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.fill = GridBagConstraints.HORIZONTAL;

		parentLayout.addLayoutComponent(infoLineText, constraints);
		parent.add(infoLineText);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridx = 0;
		constraints.weightx = 1;

		return infoLineText;
	}

	public boolean open() {
		pack();
		setMinimumSize(new Dimension(getSize().width + 120, getSize().height));

		pack();
		setVisible(true);
		return okPressed;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			okPressed = true;
			dispose();
		}
	}

}
