package com.ysd.overview.common.kit;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.jfinal.kit.StrKit;

/**
 * 图片缩放工具
 */
public class ImgKit {

	private final static String[] imgExts = new String[]{"jpg", "jpeg", "png", "bmp"};
	
	public static String getExtName(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return null;
		}
		int index = fileName.lastIndexOf('.');
		if (index < 0 || (index + 1) >= fileName.length()) {
			return null;
		}
		return fileName.substring(index + 1);
	}
	
	/**
	 * 通过文件扩展名，判断是否为支持的图像文件
	 */
	public static boolean isImage(String fileName) {
		String extName = getExtName(fileName);
		if (StrKit.isBlank(extName)) {
			return false;
		}
		extName = extName.toLowerCase();
		return ArrayUtils.contains(imgExts, extName);
	}
	
	public static boolean notImage(String fileName) {
		return !isImage(fileName);
	}
	
	/**
	 * 高保真缩放
	 */
	public static void resize(File srcFile, int toWidth, int toHeight, String saveFile) {
		float quality = 0.8f;
		try {
			BufferedImage ret = resize(ImageIO.read(srcFile), toWidth, toHeight);
			saveWithQuality(ret, quality, saveFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BufferedImage resize(BufferedImage bi, int toWidth, int toHeight) {
		Graphics g = null;
		try {
			// 从 BufferedImage 对象中获取一个经过缩放的 image
			Image scaledImage = bi.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH);
			// 创建 BufferedImage 对象，存放缩放过的 image
			BufferedImage ret = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
			g = ret.getGraphics();
			g.drawImage(scaledImage, 0, 0, null);
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (g != null) {
				g.dispose();
			}
		}
	}
	
	/**
	 * 使用参数为宽：200, 高：200, 质量：0.8，
	 * 生成大小为：6.79 KB (6,957 字节)
	 *
	 * 如果使用参数为宽：120, 高：120, 质量：0.8，
	 * 则生成的图片大小为：3.45 KB (3,536 字节)
	 *
	 * 如果使用参数为宽：300, 高：300, 质量：0.5，
	 * 则生成的图片大小为：7.54 KB (7,725 字节)
	 *
	 *
	 * 建议使用 0.8 的 quality 并且稍大点的宽高
	 * 只选用两种质量：0.8 与 0.9，这两个差别不是很大，
	 * 但是如果尺寸大些的话，选用 0.8 比 0.9 要划算，因为占用的空间差不多的时候，尺寸大些的更清晰
	 */
	public static void saveWithQuality(BufferedImage im, float quality, File outputImageFile) {
		ImageWriter writer = null;
		FileOutputStream newImage = null;
		try {
			// 输出到文件流
			newImage = new FileOutputStream(outputImageFile);
			
			writer = ImageIO.getImageWritersBySuffix("jpg").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			ImageOutputStream os = ImageIO.createImageOutputStream(newImage);
			writer.setOutput(os);
			writer.write((IIOMetadata) null, new IIOImage(im, null, null), param);
			os.flush();
			os.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (writer != null) {
				try {writer.dispose();} catch (Throwable e) {}
			}
			if (newImage != null) {
				try {newImage.close();} catch (IOException e) {throw new RuntimeException(e);}
			}
		}
	}
	
	public static void saveWithQuality(BufferedImage im, float quality, String outputImageFile) {
		saveWithQuality(im, quality, new File(outputImageFile));
	}
	
	public static final String RANDOM_SEED = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
	public static String getRandomName(int bit) {
        if (bit <= 0) bit = 6;
        return RandomStringUtils.random(bit, RANDOM_SEED);
    }
	
	public static void zoom(File srcFile, String savePath, String fileName) {
		File dir = new File(savePath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Directory " + savePath + " not exists and can not create directory.");
			}
		}
		File saveFile = new File(savePath, fileName);
		int maxWidth = 800;
		float quality = 0.8f;
		try {
			BufferedImage srcImage = ImageIO.read(srcFile);
			int srcWidth = srcImage.getWidth();
			int srcHeight = srcImage.getHeight();
			
			// 当宽度在 maxWidth 范围之内，不改变图像宽高，只进行图像高保真保存
			if (srcWidth <= maxWidth) {
				//saveWithQuality(srcImage, quality, saveFile);
				BufferedImage ret = resize(srcImage, srcWidth, srcHeight);
				saveWithQuality(ret, quality, saveFile);
			}
			// 当宽度超出 maxWidth 范围，将宽度变为 maxWidth，而高度按比例变化
			else {
				float scalingRatio = (float)maxWidth / (float)srcWidth;	// 计算缩放比率
				float maxHeight = ((float)srcHeight * scalingRatio);	// 计算缩放后的高度
				BufferedImage ret = resize(srcImage, maxWidth, (int)maxHeight);
				saveWithQuality(ret, quality, saveFile);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
