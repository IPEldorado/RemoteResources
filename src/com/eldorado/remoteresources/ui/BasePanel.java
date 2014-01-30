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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * A base panel with rounded corners
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class BasePanel extends JPanel {

	private Color borderColor = Color.WHITE;

	private int arc = 10;

	private int size = 0;

	public BasePanel() {
		setDoubleBuffered(true);
	}

	private static final long serialVersionUID = -1486022252620340274L;

	public void setBorderColor(Color newColor) {
		borderColor = newColor;
	}

	public void setArc(int arc) {
		this.arc = arc;
	}

	public void setBorderSize(int size) {
		this.size = size;
	}

	@Override
	protected void paintComponent(Graphics g) {
		int w = getWidth() - (2 * size);
		int h = getHeight() - (2 * size);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(getBackground());
		g2.fillRoundRect(size, size, w, h, arc, arc);

		g2.setStroke(new BasicStroke(3f));
		g2.setColor(borderColor);
		g2.drawRoundRect(size, size, w, h, arc, arc);

		g2.dispose();
	}

}
