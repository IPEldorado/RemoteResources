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

import java.util.List;

import javax.swing.SwingWorker;

import com.eldorado.remoteresources.ui.wizard.IWizard.WizardStatus;

/**
 * This interface defines a minimum wizard page
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public interface IWizardPage {

	public boolean hasNextPage();

	public boolean hasPreviousPage();

	public boolean isPageValid();

	public void setNextPage(IWizardPage page);

	public IWizardPage getNextPage();

	public void setPreviousPage(IWizardPage page);

	public IWizardPage getPreviousPage();

	public String getDescription();

	public void setDescription(String description);

	public void setErrorMessage(String errorMessage);

	public String getErrorMessage();

	public void setErrorLevel(WizardStatus level);

	public WizardStatus getErrorLevel();

	public List<SwingWorker<Boolean, Void>> getPreShowTasks();

	public List<SwingWorker<Boolean, Void>> getPreHideTasks();

	public void setWizard(IWizard wizard);

	public IWizard getWizard();

}
