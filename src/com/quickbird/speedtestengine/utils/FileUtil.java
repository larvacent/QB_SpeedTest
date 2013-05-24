package com.quickbird.speedtestengine.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.quickbird.speedtest.exception.NoSdcardException;

/**
 * 文件操作类
 * 
 */
public class FileUtil {

	/** 项目工程名 （项目不同注意修改） */
	private static final String PROJECT_NAME = "/testspeed";
	/** 安装目录 （项目不同注意修改） */
	private static String base = "/data/data/com.quickbird.speedtest";

	private static String rootPath = base + PROJECT_NAME;
	private static String logPath;
	private static String textPath;
	private static String imagePath;
	private static String objPath;
	/**
	 * 如果没有存储卡时，是否将数据写到手机安装目录上 （手机上空间有限不能写太多数据）
	 */
	private static boolean writeCacheWithoutSdCard = true;
	

	static {
		try {
			initDir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化日志目录
	 */
	private static void initDir() throws IOException, NoSdcardException {
		// 存储卡是否可用
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// 示例：/data/data/com.ytxt.cxlm
			base = Environment.getExternalStorageDirectory().getPath();
			rootPath = base + PROJECT_NAME;

			// 如果没有SDCARD，请到手机安装目录
			// 便注意，太大的多媒体文件不要保存到安装目录
		} else {
			// 当没有存储卡时，如果不将数据写到手机安装目录下，
			// 则抛出异常，并在适当的地方捕获异常，提示用户“没有存储卡”
			/*
			 * if (!writeCacheWithoutSdCard) throw new
			 * NoSdcardException("No Sdcard !");
			 */

		}

		createDirIfNotExist(rootPath);// demo: /sdcard/cxlm

		logPath = rootPath + "/log";
		createDirIfNotExist(logPath);// demo: /sdcard/cxlm/log

		imagePath = rootPath + "/image";
		createDirIfNotExist(imagePath);// demo: /sdcard/cxlm/image

		textPath = rootPath + "/cache";
		createDirIfNotExist(textPath);// demo: /sdcard/cxlm/cache

		objPath = rootPath + "/obj";
		createDirIfNotExist(objPath);// demo: /sdcard/cxlm/obj
	}

	public static String getLogPath() {
		return logPath;
	}

	public static String getTextPath() {
		return textPath;
	}

	public static String getImagePath() {
		return imagePath;
	}

	public static String getObjPath() {
		return objPath;
	}

	public static boolean getWriteCacheWithoutSdCard() {
		return writeCacheWithoutSdCard;
	}

	/**
	 * 如果路径所指向的目录不存在，则创建该目录
	 * 
	 * @param path
	 *            目录（文件夹）路径
	 */
	public static void createDirIfNotExist(String path) {
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}

	public static void createFileIfNotExist(String path) throws IOException {
		File file = new File(path);
		if (!file.exists())
			file.createNewFile();
	}

	/**
	 * 获得根目录
	 * 
	 * @return
	 */
	public static String getRoot() {
		return base;
	}

	/**
	 * 文件或文件夹是否存在
	 * 
	 * @param filepath
	 *            路径
	 * @return
	 */
	public static boolean exist(String filepath) throws IOException {
		File file = new File(filepath);
		return file.exists();
	}

	public static boolean writeData(String filePath, byte[] data) throws NoSdcardException,
			IOException {
		// initDir();
		if (filePath != null) {
			FileOutputStream fos = null;
			try {
				File fo = new File(filePath);
				if (!fo.exists()) {
					fo.createNewFile();
				}
				if (fo.length() > 0) {
					fos = new FileOutputStream(fo, true);// 追加到文件尾
				} else {
					fos = new FileOutputStream(fo, false);
				}
				fos.write(data);
				fos.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} finally {
				if (fos != null) {
					try {
						fos.close();
						fos = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	/**
	 * 读取文件
	 * 
	 * @param filePath
	 *            文件完整路径
	 * @return String
	 * @throws Exception
	 */
	public static String readFileAsString(String filePath) throws Exception {
		byte[] data = readFileAsBytes(filePath);
		return new String(data, "utf-8");
	}

	/**
	 * 读取文件
	 * 
	 * @param filePath
	 *            文件完整路径
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream readFileAsStream(String filePath) throws Exception {
		File file = new File(filePath);
		InputStream inSream = new FileInputStream(file);
		return inSream;
	}

	/**
	 * 序列化一个对象
	 * 
	 * @param obj
	 *            要序列化的java对象（需实现Searialize接口）
	 * @param filePath
	 *            文件完整路径
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveObject(Object obj, String filePath) throws IOException,
			NoSdcardException {
		File file = new File(filePath);
		if (!file.exists())
			file.createNewFile();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(obj);
		out.close();
	}

	/**
	 * 读一个对象（反序列化）
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readObject(String filePath) throws ClassNotFoundException, IOException,
			NoSdcardException {
		Object obj = null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
		obj = in.readObject();
		in.close();
		return obj;
	}

	/**
	 * 删除文件或目录 注意：如果是目录，当目录下有文件或子目录时则会删除失败
	 * 
	 * @param path
	 *            文件或目录路径
	 */
	public static boolean deleteFileIfExist(String path) {
		boolean success = false;
		File file = new File(path);
		if (file.exists()) {
			success = file.delete();
		}
		return success;
	}

	/**
	 * 将inputStream 转换为 byte[]
	 * 
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] streamToBytes(InputStream inStream) throws IOException {
		if (inStream == null)
			return null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 读取文件
	 * 
	 * @param filePath
	 *            文件完整路径
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] readFileAsBytes2(String filePath) throws IOException {
		byte[] buffer = null;
		FileInputStream fis = new FileInputStream(filePath);
		buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
		return buffer;
	}

	/**
	 * 读取文件
	 * 
	 * @param filePath
	 *            文件完整路径
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] readFileAsBytes(String filePath) throws IOException, NoSdcardException {
		File file = new File(filePath);
		InputStream inSream = new FileInputStream(file);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inSream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		return data;
	}

	/**
	 * 获取完全路径
	 * 
	 * @param path
	 *            相对root的路径
	 * @return 返回基于root的完全路径
	 */
	private static String getAllPath(String path) {
		if (path.startsWith(base)) {
			return path;
		} else if (base.charAt(0) != '/') {
			return new StringBuffer(base).append('/').append(path.trim()).toString();
		}
		return new StringBuffer(base).append(path.trim()).toString();
	}

	/**
	 * 删除指定目录下的所有目录和文件
	 * 
	 * @param path
	 *            目录文件路径
	 */
	public static void deleteAllBottomDirOrFile(String path) {
		File[] files = list(getAllPath(path));
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				deleteAllBottomDirOrFile(file.getPath());
			}
			file.delete();
		}

	}

	/**
	 * 删除指定目录及其子目录下的所有文件
	 * 
	 * @param path
	 *            目录路径
	 */
	public static void deleteAllFile(String path) {
		if (!exists(path)) {
			return;
		}
		File[] files = list(getAllPath(path));
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					deleteAllFile(file.getPath());
				} else {
					file.delete();
				}
			}
	}

	/**
	 * 删除某个目录包括其下所有的目录和文件
	 * 
	 * @param path
	 *            目录路径
	 */
	public void deleteAllDirAndFile(String path) {
		path = getAllPath(path);
		deleteAllBottomDirOrFile(path);
		deleteFileIfExist(path);
	}

	/**
	 * 查看目录或文件是否存在
	 * 
	 * @param path
	 *            目录或文件路径
	 * @return true-存在 false-不存在
	 */
	public static boolean exists(String path) {
		boolean isExists = false;
		if ("".equals(path) || path == null) {
			return false;
		}
		File file = new File(getAllPath(path));
		if (file.exists()) {
			isExists = true;
		}
		return isExists;
	}

	/**
	 * 列出目录下的所有文件和目录
	 * 
	 * @param path
	 *            目录路径
	 * @return File[] 该目录下的所有文件和目录.如果指定的路径不是目录则返回NULL
	 */
	public static File[] list(String path) {
		if (path == null || path.length() == 0) {
			return null;
		}
		File file = new File(getAllPath(path));
		if (!file.isDirectory()) {
			return null;
		}
		File[] files = file.listFiles();

		// 如果path指向的File是空文件夹则删除之
		if (files != null && files.length == 0)
			file.delete();
		return files;
	}

	/** 根据图片文件的完整路径创建Bitmap */
	public static Bitmap createBitmapByFilepath(String filePath) {
		try {
			// 根据绝对路径读取文件的输入流
			InputStream is = readFileAsStream(filePath);
			// 用输入流创建一个Bitmap
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
