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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.eldorado.remoteresources.utils.ImageUtils;

public class ScreenPanel extends Canvas {

	private static final long serialVersionUID = 3614027524294740275L;

	private boolean isAnimatedGif = false;

	public ScreenPanel() {
		setIgnoreRepaint(true);
	}

	/**
	 * 
	 * @param image
	 */
	public void drawScreen(Image image, boolean isAnimatedGif) {
		this.isAnimatedGif = isAnimatedGif;
		drawScreen(image);
	}

	public synchronized void stopAnimation() {
		isAnimatedGif = false;
	}

	public void drawScreen(Image image) {
		Dimension parentSize = getParent().getSize();
		Image imageToPaint = image;

		BufferStrategy strategy = getBufferStrategy();
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

		if (image != null) {
			if (image instanceof BufferedImage) {
				float resizeFactor = ImageUtils.calculateResizeFactor(
						(BufferedImage) image, parentSize);
				imageToPaint = ImageUtils.resizeImage((BufferedImage) image,
						resizeFactor);
			}

			int xLocation = (getParent().getSize().width / 2)
					- (imageToPaint.getWidth(null) / 2);
			int yLocation = (getParent().getSize().height / 2)
					- (imageToPaint.getHeight(null) / 2);

			this.setSize(new Dimension(imageToPaint.getWidth(null),
					imageToPaint.getHeight(null)));

			this.setLocation(xLocation, yLocation);

			g.drawImage(imageToPaint, 0, 0, this);
		} else {
			g.setColor(getParent().getBackground());
			g.fillRect(0, 0, parentSize.width, parentSize.height);
		}

		g.dispose();
		strategy.show();
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
			int h) {
		if (isAnimatedGif) {
			drawScreen(img);
		}
		return isAnimatedGif;
	}
}
