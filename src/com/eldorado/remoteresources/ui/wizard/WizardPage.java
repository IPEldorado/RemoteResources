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

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.eldorado.remoteresources.ui.wizard.IWizard.WizardStatus;

/**
 * This class is a basic implementation of the {@link IWizardPage} interface
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public abstract class WizardPage extends JPanel implements IWizardPage {

	private static final long serialVersionUID = 7968343445732154001L;

	private IWizardPage nextPage = null;

	private IWizardPage previousPage = null;

	private String errorMessage = null;

	private String description = null;

	private WizardStatus errorLevel = WizardStatus.OK;

	private IWizard wizard = null;

	private boolean activatedOnce = false;

	@Override
	public boolean hasNextPage() {
		return nextPage != null;
	}

	@Override
	public boolean hasPreviousPage() {
		return previousPage != null;
	}

	@Override
	public boolean isPageValid() {
		return activatedOnce && (errorLevel != WizardStatus.ERROR);
	}

	@Override
	public void setNextPage(IWizardPage page) {
		nextPage = page;
	}

	@Override
	public IWizardPage getNextPage() {
		return nextPage;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public void setErrorLevel(WizardStatus level) {
		errorLevel = level;
	}

	@Override
	public WizardStatus getErrorLevel() {
		return errorLevel;
	}

	@Override
	public void setPreviousPage(IWizardPage page) {
		previousPage = page;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return previousPage;
	}

	@Override
	public void setWizard(IWizard wizard) {
		this.wizard = wizard;
	}

	@Override
	public IWizard getWizard() {
		return wizard;
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		activatedOnce = true;
		validatePage();
	}

	public void setActivatedOnce() {
		activatedOnce = true;
	}

	protected void validatePage() {
		getWizard().updateDialog();
	}

	@Override
	public List<SwingWorker<Boolean, Void>> getPreHideTasks() {
		return null;
	}

	@Override
	public List<SwingWorker<Boolean, Void>> getPreShowTasks() {
		return null;
	}

}
