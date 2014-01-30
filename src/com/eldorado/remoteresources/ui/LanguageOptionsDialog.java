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
import java.awt.Component;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization.RemoteResourcesLocalization_Languages;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;

public class LanguageOptionsDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 9203428177127128010L;

	private BasePanel content = null;

	private GridBagLayout contentLayout = null;

	JComboBox<String> languages = null;

	private JButton okButton;

	private JButton cancelButton;

	private boolean isDisplayed = false;

	static Point mouseDownCompCoords;

	public LanguageOptionsDialog() {
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
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

		JLabel label = new JLabel(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.LanguageOptionsDialog_Title));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 14));

		String[] options = { "English", "Português" };
		languages = new JComboBox<String>(options);
		languages.setSelectedIndex(0);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		cancelButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_Cancel));
		okButton = new JButton(
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.UI_OK));

		cancelButton.addActionListener(this);
		okButton.addActionListener(this);

		cancelButton.setBackground(Color.LIGHT_GRAY);
		okButton.setBackground(Color.LIGHT_GRAY);

		buttonsPanel.add(cancelButton);
		buttonsPanel.add(okButton);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 0;
		content.add(label, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 1;
		content.add(languages, constraints);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 2;
		content.add(buttonsPanel, constraints);

		setContentPane(content);
	}

	public boolean open() {
		pack();
		setVisible(true);
		isDisplayed = true;
		return isDisplayed;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			dispose();
		} else if (e.getSource() == okButton) {
			RemoteResourcesLocalization_Languages l = RemoteResourcesLocalization_Languages.en_US;
			switch (languages.getSelectedIndex()) {
			case 0: // English/Default
				l = RemoteResourcesLocalization_Languages.en_US;
				break;
			case 1: // Portuguese
				l = RemoteResourcesLocalization_Languages.pt_BR;
				break;
			}
			// maybe show warning dialog before!
			RemoteResourcesConfiguration.getInstance().set(
					RemoteResourcesConfiguration.PREFERRED_LANG, l.toString());
			dispose();
			JOptionPane
					.showConfirmDialog(
							null,
							RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.LanguageOptionsRestart),
							"Remote Resources", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public boolean isDisplayed() {
		return isDisplayed;
	}

	@Override
	public void dispose() {
		super.dispose();
		isDisplayed = false;
	}

	@Override
	public void setLocationRelativeTo(Component c) {
		super.setLocationRelativeTo(c);
		setLocation(c.getLocationOnScreen().x + (c.getWidth() / 2),
				c.getLocationOnScreen().y - 75);
	}
}
