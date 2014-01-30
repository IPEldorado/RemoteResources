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

package com.eldorado.remoteresources.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.ui.wizard.AddDeviceWizard;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * Action to add a new device
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class AddDeviceAction extends AbstractAction {

	private static final long serialVersionUID = 1948869137704317751L;

	private final PersistentDeviceModel model;

	/**
	 * Create an action to add a new device in the model passed as argument
	 * 
	 * @param model
	 *            the model to add the device to
	 */
	public AddDeviceAction(PersistentDeviceModel model) {
		putValue(
				SHORT_DESCRIPTION,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddDeviceAction_Tooltip_AddDevice));
		putValue(
				LONG_DESCRIPTION,
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.AddDeviceAction_LongTooltip_AddDevice));
		putValue(Action.LARGE_ICON_KEY,
				ResourcesLoader.getIcon("button-add", 32));
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AddDeviceWizard dialog = new AddDeviceWizard(model);
		if (dialog.open()) {
			model.addHost(dialog.getHostToAdd());
		}
	}
}
