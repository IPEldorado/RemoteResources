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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.BasePanel;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * This class provides a wizard to add a new device. It also handles the wizard
 * lifecycle
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class AddDeviceWizard extends JDialog implements ActionListener, IWizard {

	private static final long serialVersionUID = 4390068218769551912L;
	private JButton previousButton;
	private JButton nextButton;
	private JButton finishButton;
	private JButton cancelButton;
	private JLabel messageLabel;
	private final BasePanel content;
	private GridBagLayout contentLayout;
	private JProgressBar progressBar;
	private final List<IWizardPage> pages;
	private IWizardPage currentPage;
	private final Device deviceToAdd = new Device(null, null, null);
	private final Host hostToAdd = new Host(null);
	private boolean finishPressed = false;
	// device model to validate hosts and devices
	private final PersistentDeviceModel model;
	private WorkerRunnable preHideRunnable = null;

	private WorkerRunnable preShowRunnable = null;

	static Point mouseDownCompCoords;

	public AddDeviceWizard(PersistentDeviceModel model) {
		super((Frame) null, true);
		setUndecorated(true);
		setTitle("Add Device");
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		pages = new ArrayList<IWizardPage>();
		content = new BasePanel();
		content.setBorderSize(0);
		content.setArc(0);
		content.setBorderColor(Color.BLACK);
		content.setBackground(Color.LIGHT_GRAY);
		content.setOpaque(false);

		hostToAdd.addDevice(deviceToAdd);
		this.model = model;
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

	private void setMessage(WizardStatus level, String message) {
		messageLabel.setText("<html>" + message + "</html>"); //$NON-NLS-1$ //$NON-NLS-2$
		Icon icon = null;
		switch (level) {
		case OK:
			icon = ResourcesLoader.getIcon("balloon", 24); //$NON-NLS-1$
			content.setBorderColor(Color.BLACK);
			break;
		case INFO:
			icon = ResourcesLoader.getIcon("balloon", 24); //$NON-NLS-1$
			content.setBorderColor(Color.BLACK);
			break;
		case WARNING:
			icon = ResourcesLoader.getIcon("exclamation-circle", 24); //$NON-NLS-1$
			content.setBorderColor(new Color(238, 255, 112));
			break;
		case ERROR:
			icon = ResourcesLoader.getIcon("button-cross", 24); //$NON-NLS-1$
			content.setBorderColor(new Color(255, 79, 100));
			break;

		default:
			break;
		}
		messageLabel.setIcon(icon);
		content.repaint();
	}

	private void initComponents() {

		content.setLayout(contentLayout = new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 16, 16));

		createMessageArea();

		createPages();

		currentPage = pages.get(0);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;

		contentLayout.addLayoutComponent((JPanel) currentPage, constraints);
		content.add((JPanel) currentPage);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 1;
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		contentLayout.addLayoutComponent(progressBar, constraints);
		content.add(progressBar);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		cancelButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_Cancel));
		previousButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_Previous));
		nextButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_Next));
		finishButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_Finish));

		cancelButton.addActionListener(this);
		previousButton.addActionListener(this);
		nextButton.addActionListener(this);
		finishButton.addActionListener(this);

		cancelButton.setBackground(Color.LIGHT_GRAY);
		previousButton.setBackground(Color.LIGHT_GRAY);
		nextButton.setBackground(Color.LIGHT_GRAY);
		finishButton.setBackground(Color.LIGHT_GRAY);

		buttonsPanel.add(cancelButton);
		buttonsPanel.add(previousButton);
		buttonsPanel.add(nextButton);
		buttonsPanel.add(finishButton);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 1;
		contentLayout.addLayoutComponent(buttonsPanel, constraints);
		content.add(buttonsPanel);
		setContentPane(content);
		updateDialog();

		revalidate();
		repaint();
	}

	private void addPage(IWizardPage page) {
		pages.add(page);
		int pageIndex = pages.indexOf(page);
		if (pageIndex > 0) {
			page.setPreviousPage(pages.get(pageIndex - 1));
			pages.get(pageIndex - 1).setNextPage(page);
		}
		page.setWizard(this);
		((JPanel) page).setBackground(Color.LIGHT_GRAY);
	}

	private void createPages() {
		addPage(new AddHostPage(model, hostToAdd));
		addPage(new AddDevicePage(model, hostToAdd, deviceToAdd));
	}

	private void createMessageArea() {
		messageLabel = new JLabel();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 0;
		contentLayout.addLayoutComponent(messageLabel, constraints);
		content.add(messageLabel);
	}

	public boolean open() {
		pack();
		setMinimumSize(new Dimension(getSize().width + 80,
				getSize().height + 80));
		setLocation(getLocation().x - (getSize().width / 2), getLocation().y
				- (getSize().height / 2));
		pack();
		setVisible(true);
		return finishPressed;
	}

	public Host getHostToAdd() {
		return hostToAdd;
	}

	public Device getDeviceToAdd() {
		return deviceToAdd;
	}

	private void internalSwitchToPage(IWizardPage to) {
		content.remove((JPanel) currentPage);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;

		contentLayout.addLayoutComponent((JPanel) to, constraints);
		content.add((JPanel) to);

		currentPage = to;
		((JPanel) to).setVisible(true);
		progressBar.setVisible(false);
		revalidate();
		repaint();
		updateDialog();
	}

	private void switchToPage(final IWizardPage to) {
		if ((currentPage != null) && (to != null)) {
			preHideRunnable = executeTasks(currentPage.getPreHideTasks(),
					new Thread() {
						@Override
						public void run() {
							if ((preHideRunnable != null)
									&& preHideRunnable
											.wasSuccessfullyFinished()) {
								preShowRunnable = executeTasks(
										to.getPreShowTasks(), new Thread() {
											@Override
											public void run() {
												if ((preShowRunnable != null)
														&& preShowRunnable
																.wasSuccessfullyFinished()) {
													internalSwitchToPage(to);
												} else {
													progressBar
															.setVisible(false);
													repaint();
													updateDialog();
												}
											}
										});
								if (preShowRunnable != null) {
									preShowRunnable.start();
								} else {
									internalSwitchToPage(to);
								}
							} else {
								progressBar.setVisible(false);
								updateDialog();
							}
						}
					});

			if (preHideRunnable != null) {
				preHideRunnable.start();
			} else {
				preShowRunnable = executeTasks(to.getPreShowTasks(),
						new Thread() {
							@Override
							public void run() {
								if ((preShowRunnable != null)
										&& preShowRunnable
												.wasSuccessfullyFinished()) {
									internalSwitchToPage(to);
								} else {
									progressBar.setVisible(false);
									repaint();
									updateDialog();
								}
							}
						});
				if (preShowRunnable != null) {
					preShowRunnable.start();
				} else {
					internalSwitchToPage(to);
				}
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			dispose();
		} else if (e.getSource() == nextButton) {
			switchToPage(currentPage.getNextPage());
		} else if (e.getSource() == previousButton) {
			switchToPage(currentPage.getPreviousPage());
		} else if (e.getSource() == finishButton) {
			finishPressed = true;
			dispose();
		}
	}

	@Override
	public void updateDialog() {

		if (currentPage.getErrorMessage() != null) {
			setMessage(currentPage.getErrorLevel(),
					currentPage.getErrorMessage());
		} else {
			setMessage(WizardStatus.OK, currentPage.getDescription());
		}

		nextButton.setEnabled(currentPage.hasNextPage()
				&& currentPage.isPageValid());
		previousButton.setEnabled(currentPage.hasPreviousPage());
		finishButton.setEnabled(canFinish());
		cancelButton.setEnabled(true);
	}

	private boolean canFinish() {
		boolean canFinish = true;
		for (IWizardPage page : pages) {
			canFinish = canFinish && page.isPageValid();
		}
		return canFinish;
	}

	private WorkerRunnable executeTasks(List<SwingWorker<Boolean, Void>> tasks,
			Thread postExecutionThread) {
		if (tasks != null) {
			nextButton.setEnabled(false);
			previousButton.setEnabled(false);
			cancelButton.setEnabled(false);
			finishButton.setEnabled(false);
			progressBar.setVisible(true);
			WorkerRunnable runnable = new WorkerRunnable(tasks, progressBar,
					postExecutionThread);
			return runnable;
		}
		return null;
	}
}
