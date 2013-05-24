package com.quickbird.speedtestengine.utils;
/*******************************************************************
 * Copyright @ 2013 ChenFengYun (BeiJing) Technology LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　TestSpeed
 * <P>
 * FileName:　　　　LogUtil.java
 * <P>
 * Description:　　日志工具　
 * <P>
 * Author:　　　　　　XD.LIU
 * <P>
 * Create Date:　　　2013-1-28 上午11:37:27
 ********************************************************************/
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
public class LogUtil {

	/**
	 * 记录日志(在sdcard中写文件)
	 * 
	 * @param logMessage
	 *            日志信息
	 */
	public static void writeLogInSdcard(String logMessage) {
		// 有无sdcard
		if (!Environment.MEDIA_MOUNTED.equals(//
				Environment.getExternalStorageState()))
			return;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		try {
			byte[] data = (date + "\n" + logMessage + "\r\n").getBytes("UTF-8");
			saveBytes(data, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写日志， 每个日志文件最大 2M，写满 fileCount个日记文件后，不再写日志
	 * 
	 * @param content
	 *            日志内容
	 * @param dirSection
	 *            要将日志写在什么目录下
	 */
	private static void saveBytes(byte[] content, String dirSection) {
		if (content == null || "".equals(content))
			return;
		try {
			String dirPath = FileUtil.getLogPath();
			String[] dirs = StringUtil.splitDirString(dirSection);
			if (dirs != null && dirs.length > 0) {
				for (int i = 0; i < dirs.length; i++) {
					dirPath = dirPath + "/" + dirs[i];
					FileUtil.createDirIfNotExist(dirPath);// 如果文件夹不存在，则新建
				}
			}

			String filePath = null;
			File file = null;

			int maxSize = 1000 * 1000 * 2; // 每个日志文件最大 2M
			int fileCount = 20; // 写满 fileCount个日记文件后，不再写日志
			for (int i = 1; i <= fileCount; i++) {
				filePath = dirPath + "/" + "log_" + i + ".txt";// 文件路径
				file = new File(filePath);
				if (file.length() > maxSize) {// 大于2M
					// FileUtil.deleteFileIfExist(filePath);

				} else {
					break;
				}
			}
			FileUtil.writeData(filePath, content);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
