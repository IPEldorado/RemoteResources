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

package com.eldorado.remoteresources.utils;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * 
 *
 */
public class ImageUtils {

	/**
	 * 
	 * @param image
	 * @param scale
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage image, double scale) {
		if ((scale == 1.0) || (image == null)) {
			return image;
		}

		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
				.getType();

		float width = image.getWidth();
		float height = image.getHeight();

		int newHeight = (int) Math.round(height * scale);
		int newWidth = Math.round((width * newHeight) / height);

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();

		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, newWidth, newHeight, null);
		g.dispose();

		return resizedImage;
	}

	public static float calculateResizeFactor(BufferedImage image,
			Dimension sizeToFit, int border) {
		if (image == null) {
			return 1;
		}

		double height = sizeToFit.getHeight() - (2 * border);
		double scaleHeight = (float) (height / image.getHeight());

		double width = sizeToFit.getWidth() - (2 * border);
		double scaleWidth = (float) (width / image.getWidth());

		float scale = (float) (scaleHeight <= scaleWidth ? scaleHeight
				: scaleWidth);

		return scale;
	}

	public static float calculateResizeFactor(BufferedImage image,
			Dimension sizeToFit) {
		return calculateResizeFactor(image, sizeToFit, 10);
	}

	/**
	 * <b>Compression temporarily disabled due to problems on full hd
	 * devices.</b> Compress a BufferedImage from chimpchat into a compressed Xz
	 * file with a Jpeg image within
	 * 
	 * @param sourceImage
	 *            the source image
	 * @param quality
	 *            the quality rate: 100 for max QUALITY, 0 for max COMPRESSION
	 * @return a byte array representing the image
	 * @throws IOException
	 *             if some error occurs during the conversion
	 */

	public static ByteArrayOutputStream compress(BufferedImage sourceImage,
			int quality) throws IOException {

		ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
		ByteArrayOutputStream xzOutput = new ByteArrayOutputStream();
		ImageWriter imgWriter = ImageIO.getImageWritersByFormatName("jpeg")
				.next();
		ImageWriteParam parameters = imgWriter.getDefaultWriteParam();
		parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		parameters.setCompressionQuality((quality > 100 ? 100
				: (quality < 0 ? 0 : quality)) / 100f);
		ImageOutputStream imgOutputStream = new MemoryCacheImageOutputStream(
				imageOutput);
		BufferedImage newImage = new BufferedImage(sourceImage.getWidth(),
				sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		ColorConvertOp colorConvertion = new ColorConvertOp(null);
		colorConvertion.filter(sourceImage, newImage);
		IIOImage image = new IIOImage(newImage, null, null);
		imgWriter.setOutput(imgOutputStream);
		imgWriter.write(null, image, parameters);
		/*
		 * XZOutputStream xzStream = new XZOutputStream(xzOutput, new
		 * LZMA2Options(LZMA2Options.PRESET_MAX));
		 */
		xzOutput.write(imageOutput.toByteArray());
		xzOutput.close();
		return xzOutput;
	}

	/**
	 * <b>Compression temporarily disabled due to problems on full hd
	 * devices.</b> Decompress a previously compressed Xz stream
	 * 
	 * @param xzStream
	 *            the compressed file
	 * @return the original file (notice that if file was compressed using JPEG,
	 *         you will not be able to retrieve the original file)
	 * @throws IOException
	 *             if any error occurs during decompression
	 */

	public static ByteArrayInputStream decompress(ByteArrayInputStream xzStream)
			throws IOException {
		ByteArrayInputStream xzInputStream = xzStream;
		byte firstByte = (byte) xzInputStream.read();
		byte[] buffer = new byte[xzInputStream.available()];
		buffer[0] = firstByte;
		xzInputStream.read(buffer, 1, buffer.length - 2);
		xzInputStream.close();
		return new ByteArrayInputStream(buffer);
	}

	public static boolean isEqual(BufferedImage newImage,
			BufferedImage previousImage) {
		boolean equal = true;

		if ((newImage != null) && (previousImage != null)) {
			DataBuffer newDb = newImage.getData().getDataBuffer();
			DataBuffer prevDb = previousImage.getData().getDataBuffer();

			for (int i = 0; (i < newDb.getSize()) && equal; i++) {
				if (newDb.getElem(i) != prevDb.getElem(i)) {
					return false;
				}
			}

		} else {
			equal = false;
		}

		return equal;
	}
}
