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

package com.eldorado.remoteresources;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.eldorado.remoteresources.ui.actions.ClientManipulationScript;

public class UpdateScriptFile extends JFrame {

	// TODO: remove me (this class) when I won't be useful anymore! =)

	private static String linha;
	private static String tempo;
	private static Vector<String> vetScript;
	private static final int OPCAO_SLEEP = 1;
	private static final int OPCAO_PAUSE = 2;
	private final static JTextArea actionsText = new JTextArea();
	private final JButton saveButton = new JButton("Save");
	private final JRadioButton sleepRadio = new JRadioButton("Tag Sleep", true);
	private final JRadioButton pauseRadio = new JRadioButton("Tag Pause");
	private final static ClientManipulationScript cScript = new ClientManipulationScript();
	private static File f = null;

	private static void addPauseInScript(String l) {
		vetScript.insertElementAt("[PAUSE]", Integer.parseInt(l));
	}

	private static void addSleepInScript(String l, String t) {
		vetScript.insertElementAt("[SLEEP]:" + t, Integer.parseInt(l));
	}

	private static void addPauseInScript() {
		vetScript.add("[PAUSE]");
	}

	private static void addSleepInScript(String t) {
		vetScript.add("[SLEEP]:" + t);
	}

	private void update(String path, int op) {

		if (op == OPCAO_SLEEP) {

			linha = JOptionPane.showInputDialog(null,
					"Digite o numero da linha:", "Script", 1);
			tempo = JOptionPane.showInputDialog(null, "Digite um tempo:",
					"Script", 1);
			addSleepInScript(linha, tempo);
		}

		if (op == OPCAO_PAUSE) {
			linha = JOptionPane.showInputDialog(null,
					"Digite o numero da linha:", "Script", 1);
			addPauseInScript(linha);
		}

	}

	private static void refreshText() {
		Iterator<String> it = vetScript.iterator();
		actionsText.setText("");
		int c = 0;
		while (it.hasNext()) {
			actionsText.append(c + "   " + it.next().toString().concat("\n"));
			c++;
		}
	}

	public static void main(String[] args) {
		f = new File(
				"C:\\Users\\vmfw37\\Documents\\RR\\projeto_v5\\ScriptFile_A953@localhost__localhost_20121003.txt");
		vetScript = cScript.readScriptFile(f);
		refreshText();
		new UpdateScriptFile();
	}

	public UpdateScriptFile() {

		ActionListener radioSleepListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton radioAbstractButton = (AbstractButton) actionEvent
						.getSource();

				if (radioAbstractButton.getName().equals("sleep")) {
					update(null, OPCAO_SLEEP);
					refreshText();
				}
			}
		};

		ActionListener radioPauseListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton radioAbstractButton = (AbstractButton) actionEvent
						.getSource();

				if (radioAbstractButton.getName().equals("pause")) {
					update(null, OPCAO_PAUSE);
					refreshText();
				}
			}
		};

		ActionListener buttonSaveChangesListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				cScript.updateScriptFile(f, vetScript);
			}
		};

		sleepRadio.setName("sleep");
		sleepRadio.addActionListener(radioSleepListener);
		pauseRadio.setName("pause");
		pauseRadio.addActionListener(radioPauseListener);
		saveButton.setName("Save");
		saveButton.setPreferredSize(new Dimension(150, 75));
		saveButton.addActionListener(buttonSaveChangesListener);

		ButtonGroup myButtonGroup = new ButtonGroup();
		myButtonGroup.add(sleepRadio);
		myButtonGroup.add(pauseRadio);
		actionsText.setAutoscrolls(true);
		actionsText.setEditable(false);
		actionsText.setFont(new Font("Serif", Font.ITALIC, 16));
		actionsText.setLineWrap(true);
		actionsText.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(actionsText);
		areaScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(680, 500));

		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(sleepRadio);
		getContentPane().add(pauseRadio);
		getContentPane().add(areaScrollPane);
		getContentPane().add(saveButton);
		setSize(900, 700);
		setTitle("Choose what kind of tag (sleep/pause) you wanto to add in Script File");
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
