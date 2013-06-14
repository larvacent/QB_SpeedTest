package com.quickbird.speedtestengine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 半角转换为全角
     * 
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     * 
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 拆分形如"/myfolder/3/4/"的目录
     * 
     * @param dirSection
     *            要拆分的目录
     * @return
     */
    public static String[] splitDirString(String dirSection) {
        if (dirSection == null || "".equals(dirSection.trim()))
            return null;
        dirSection = dirSection.trim();
        if ('/' == (dirSection.charAt(0))) {
            dirSection = dirSection.substring(1);
        }
        if (dirSection != null && !"".equals(dirSection)) {
            if ('/' == (dirSection.charAt(dirSection.length() - 1))) {
                dirSection = dirSection.substring(0, dirSection.length() - 1);
            }
        }
        if (dirSection != null && !"".equals(dirSection)) {
            String[] dirs = dirSection.split("/");
            return dirs;
        }
        return null;
    }

    /**
     * 获取时间戳
     * 
     * @return
     */
    public static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date())
                + (int) ((Math.random() * 900000 + 100000));
    }

    /**
     * 系统时间
     * 
     * @return
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyyMMddHHmmss");
    }

    /**
     * 指定格式的系统时间
     * 
     * @return
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }
    
	/**
	 * 判断字符串是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNull(String s) {
		if (s != null && !"".equals(s) && s.length() > 0 && !"null".equalsIgnoreCase(s)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 将空变成空串
	 * 
	 * @param s
	 * @return
	 */
	public static String changeNull2Nullstr(String s) {
		if (isNull(s)) {
			s = "";
		}
		return s;
	}

    /**
     * 一个月以前的指定格式的系统时间
     * 
     * @return
     */
    public static String getMonthBefore(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        return sdf.format(c.getTime());
    }

    public static final InputStream byte2Stream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] stream2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 如果电话号码以+86开头，则去掉此前缀+86
     * 
     * @param aPhoneNumber
     *            电话号码
     * @return
     */
    public static String trimPhonePrefix86(String aPhoneNumber) {
        String phoneNumber = aPhoneNumber;
        if (aPhoneNumber != null && aPhoneNumber.startsWith("+86")) {
            phoneNumber = aPhoneNumber.substring(3);
        }
        return phoneNumber;
    }

    /**
     * 格式化大数据
     * 
     * @param newScale
     *            保留小数位数
     * @param data
     *            数据
     * @return
     */
    public static BigDecimal formatDecimal(int newScale, double data) {
        BigDecimal p = new BigDecimal(data);
        return p.setScale(newScale, BigDecimal.ROUND_HALF_UP);// BigDecimal.ROUND_HALF_UP：代表四舍五入
    }

    /**
     * 格式化流量
     * @param data  数据
     * @return
     */
    public static String formatSpeed(float data) {
        
        return formatSpeed(data, "#0.00");
        
    }
    
    public static String formatSpeed(float data, String format) {
        java.text.DecimalFormat df = new java.text.DecimalFormat(format);

        if (data < 0)
            return "0B/s";

        if (data < 1000.0) {
            if (data == (int) data) {
                df = new java.text.DecimalFormat("#0");
            }
            return df.format(data) + "KB/s";
        }

        if (data < (1000f * 1024.0))
            return df.format(data / 1024) + "MB/s";

        if (data < (1000 * 1024 * 1024.0))
            return df.format(data / (1024 * 1024.0)) + "GB/s";

        return df.format(data / (1024 * 1024 * 1024.0)) + "GB/s";
    }

	public static String formatSpeed(int speed, String format) {
		java.text.DecimalFormat df = new java.text.DecimalFormat(format);
		if (speed < 0)
			return "0B/s";

		if (speed < 1000) {
			return speed + "B/s";
		}

		if (speed < 1000f * 1024.0)
			return df.format(speed / 1024) + "KB/s";

		return df.format(speed / (1024 * 1024.0)) + "MB/s";
	}

    /**
     * 格式化流量 不带单位
     * 
     * @param speed
     *            数据
     * @return
     */
    
    public static Map<String, String> formatSpeedWithUnit(float speed) {

        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        Map<String, String> map = new HashMap<String, String>();

        if (speed < 0){
            map.put("speed", "0");
            map.put("unit", "KB/s");
            return map;
        }

        if (speed < 1000.0) {
            if (speed == (int) speed) {
                df = new java.text.DecimalFormat("#0");
            }
            map.put("speed", df.format(speed));
            map.put("unit", "KB/s");
            return map;
        }

        if (speed < (1024 * 1000.0)) {
            map.put("speed", df.format(speed / 1024));
            map.put("unit", "MB/s");
            return map;
        }

        if (speed < (1024 * 1024 * 1000.0)) {
            map.put("speed", df.format(speed / (1024 * 1024.0)));
            map.put("unit", "GB/s");
            return map;

        }
        map.put("speed", "0");
        map.put("unit", "KB/s");
        return map;
    }

    public static String formatMounth(String mounthStr) {

        return mounthStr.substring(0, 4) + "年" + mounthStr.substring(4, 6)
                + "月";
    }

    public static String formatDate(String mounthStr) {

        return mounthStr.substring(2, 4) + "/" + mounthStr.substring(4, 6)
                + "/" + mounthStr.substring(6, 8) + " "
                + mounthStr.substring(8, 10) + ":"
                + mounthStr.substring(10, 12);
    }

    public static String formatSpeedValue(float speed) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        String str = "";
        if (speed >= 0 && speed < 1024.0) {
            df = new java.text.DecimalFormat("#0");
            str = df.format(speed)+"KB/s";
        } else if (speed >= 1024 && speed < (1024 * 1024.0)) {
            str = df.format(speed / 1024)+"MB/s";
        } else if (speed >= (1024 * 1024) && speed < (1024 * 1024 * 1024.0)) {
            str = df.format(speed / (1024 * 1024.0))+"GB/s";
        } else if (speed >= (1024 * 1024 * 1024)
                && speed < (1024 * 1024 * 1024 * 1024)) {
            str = df.format(speed / (1024 * 1024 * 1024.0))+"TB/s";
        }
        return str;
    }
    
    /**
     * 将年月转化为字符串
     * @param year
     * @param month
     * @return 201201
     */
    public static String getDate(int year, int month) {
        String date = null;
        String a = String.valueOf(year);
        String b = String.valueOf(month);
        if (month > 9) {
            date = a + b;
        } else {
            date = a + "0" + b;
        }
        return date;
    }
    
    /**
     * 获取当月字符串
     * @return
     */
    public static String getCurMonthStr() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		return getDate(year, month);
	}
    
    public static String getLastMonthStr() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		if(--month == 0){
			month = 12;
			year--;
		}
		return getDate(year, month);
	}
}
