package com.ysd.springcloud.kit;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class FileKit {
	
	private final static String[] imgExts = new String[] {
			"jpg", "jpeg", "png", "bmp", "gif"
	};
	
	private final static String[] officeExts = new String[] {
			"doc", "docx", "xls", "xlsx", "ppt", "pptx"
	};
	
	private final static String[] otherExts = new String[] { "pdf", "txt" };
	
	/**
	 * 通过文件扩展名，判断是否为支持文件预览
	 */
	public static boolean toPreview(String extName) {
		return isImage(extName) 
				|| isOffice(extName) 
				|| ArrayUtils.contains(otherExts, extName);
	}
	
	public static boolean isImage(String extName) {
		return ArrayUtils.contains(imgExts, extName);
	}
	
	public static boolean isOffice(String extName) {
		return ArrayUtils.contains(officeExts, extName);
	}
	
	public static String getFileType(String extName) {
		if (isImage(extName)) return "picture";
		if (isOffice(extName)) return "office";
		if ("txt".equals(extName)) return "text";
		return "other";
	}
	
	public static long writeTo(InputStream in, String savePath, String fileName) throws IOException {
		File dir = new File(savePath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Directory " + savePath + " not exists and can not create directory.");
			}
		}
		return writeTo(in, new File(savePath, fileName));
	}

	public static long writeTo(InputStream in, File file) throws IOException {
		long written = 0;
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			written = write(in, out);
		} finally {
			if (out != null) out.close();
		}
		return written;
	}
	
	public static long write(InputStream in, OutputStream out) throws IOException {
		long size = 0;
		int read;
		byte[] buf = new byte[8 * 1024];
		while ((read = in.read(buf)) != -1) {
			out.write(buf, 0, read);
			size += read;
		}
		return size;
	}
	
	public static boolean deleteQuietly(final String filePath) {
		File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        try {
            if (file.isDirectory()) cleanDirectory(file);
        } catch (final Exception ignored) {}
        
        try {
            return file.delete();
        } catch (final Exception ignored) {
            return false;
        }
    }
	
	public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            //String message = directory + " does not exist";
            //throw new IllegalArgumentException(message);
        	return;
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (File file : files) {
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }
        if (null != exception)  throw exception;
    }
	
	public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent){
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }
	
	public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }
	
	public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        if (File.separatorChar == '\\') {
            return false;
        }
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }
        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }
    }
	
}
