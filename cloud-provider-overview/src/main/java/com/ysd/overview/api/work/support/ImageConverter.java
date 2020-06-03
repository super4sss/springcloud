package com.ysd.overview.api.work.support;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

/**
 * 图像转换器（将ffmpeg图像转为java图像）
 */
public class ImageConverter {

	/**
	 * 24位BGR转BufferedImage
	 * @param src -源数据
	 * @param width -宽度
	 * @param height-高度
	 * @return
	 */
	public static BufferedImage BGR2BufferedImage(ByteBuffer src, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		DataBufferByte db = (DataBufferByte) image.getRaster().getDataBuffer();
		ByteBuffer.wrap(db.getData()).put(src);
		return image;
	}
	
	public static void saveImage(BufferedImage image, int width, int height, String file) throws IOException {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		bi.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		ImageIO.write(bi, "jpg", new File(file));
	}
	
}
