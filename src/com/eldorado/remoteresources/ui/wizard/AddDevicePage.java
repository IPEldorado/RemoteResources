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

package com.eldorado.remoteresources.ui.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.ui.wizard.IWizard.WizardStatus;
import com.eldorado.remoteresources.utils.DeviceUtils;

/**
 * This class is responsible to provide a way to let user choose which device to
 * add
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class AddDevicePage extends ModelModificationWizardPage {

	private static final long serialVersionUID = 1703960613824361258L;

	private final Device device;

	private final Host aHost;

	private final JTable deviceList;

	private Client client;

	private GridBagLayout layout;

	private final JTextField deviceNameText;

	public AddDevicePage(PersistentDeviceModel model, Host aHost, Device device) {
		super(model);
		this.device = device;
		this.aHost = aHost;
		setDescription(RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.AddDevicePage_UI_PageDescription));

		setLayout(layout = new GridBagLayout());

		JLabel deviceNameLabel = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddDevicePage_UI_DeviceNameLabel));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.insets = new Insets(2, 2, 2, 2);
		layout.addLayoutComponent(deviceNameLabel, constraints);
		add(deviceNameLabel);

		deviceNameText = new JTextField();
		deviceNameText
				.setToolTipText(RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddDevicePage_UI_DeviceNameTooltip));
		deviceNameText.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent e) {
						validatePage();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						validatePage();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						validatePage();
					}
				});
		constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		layout.addLayoutComponent(deviceNameText, constraints);
		add(deviceNameText);

		JScrollPane pane = new JScrollPane();
		pane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		layout.addLayoutComponent(pane, constraints);
		add(pane);

		deviceList = new JTable();
		deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		deviceList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						validatePage();
					}
				});
		pane.setViewportView(deviceList);

	}

	public boolean validateDevice() {
		for (Device d : getModel().getDevices()) {
			if (d.getSerialNumber().equals(
					deviceList.getValueAt(deviceList.getSelectedRow(), 1))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void validatePage() {

		String name = deviceNameText.getText();
		String errorMessage = null;
		WizardStatus status = WizardStatus.OK;

		if (name.trim().isEmpty()) {
			errorMessage = RemoteResourcesLocalization
					.getMessage(RemoteResourcesMessages.AddDevicePage_Error_NameIsEmpty);
			status = WizardStatus.ERROR;
		}

		if ((errorMessage == null) && (getModel().getDevice(name) != null)) {
			errorMessage = RemoteResourcesLocalization
					.getMessage(RemoteResourcesMessages.AddDevicePage_Error_DeviceAlreadyExists);
			status = WizardStatus.ERROR;
		}

		if ((errorMessage == null)
				&& (deviceList.getModel().getRowCount() == 0)) {
			errorMessage = RemoteResourcesLocalization
					.getMessage(RemoteResourcesMessages.AddDevicePage_Error_NoAvailableDevice);
			status = WizardStatus.ERROR;
		}
		if ((errorMessage == null)
				&& (deviceList.getSelectionModel().isSelectionEmpty())) {
			errorMessage = RemoteResourcesLocalization
					.getMessage(RemoteResourcesMessages.AddDevicePage_Error_NoSelectedDevice);
			status = WizardStatus.ERROR;
		}

		if ((errorMessage == null) && (validateDevice())) {
			errorMessage = RemoteResourcesLocalization
					.getMessage(RemoteResourcesMessages.AddDevicePage_Error_DeviceAlreadyCreated);
			status = WizardStatus.ERROR;
		}

		if (status != WizardStatus.ERROR) {
			DeviceUtils.getInstance().setDeviceNickname(
					device.getSerialNumber(), name);
			device.setName(name);
			device.setHost(aHost.getName());
			device.setSerialNumber(deviceList.getModel()
					.getValueAt(deviceList.getSelectedRow(), 1).toString());
		}

		setErrorMessage(errorMessage);
		setErrorLevel(status);
		super.validatePage();
	}

	@Override
	public void setVisible(boolean aFlag) {
		// Cells non editable
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Every cell non-editable
			}

		};

		model.addColumn(RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.AddDevicePage_UI_DeviceTableColumn_DeviceModel));
		model.addColumn(RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.AddDevicePage_UI_DeviceTableColumn_DeviceSerialNumber));
		client = ClientConnectionManager.getInstance().getClient(
				aHost.getHostname(), aHost.getPort());
		if (client != null) {
			for (AndroidDeviceClient device : client.getDeviceList().values()) {
				model.addRow(new Object[] { device.getModel(),
						device.getSerialNumber() });
			}
		}
		deviceList.setModel(model);
		super.setVisible(aFlag);
	}

}
