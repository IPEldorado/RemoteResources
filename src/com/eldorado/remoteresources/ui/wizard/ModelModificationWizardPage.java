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

import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;

/**
 * Convenience class to force addition of model
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public abstract class ModelModificationWizardPage extends WizardPage {

	private static final long serialVersionUID = 6919221516473592545L;

	/**
	 * The model to validate against
	 */
	private final PersistentDeviceModel model;

	public ModelModificationWizardPage(PersistentDeviceModel model) {
		this.model = model;
	}

	public PersistentDeviceModel getModel() {
		return model;
	}

}
