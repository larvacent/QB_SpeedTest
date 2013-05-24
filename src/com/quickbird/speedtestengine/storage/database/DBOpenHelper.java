package com.quickbird.speedtestengine.storage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*******************************************************************
 * Copyright @ 2012 晨风云（北京）科技有限公司 LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　Speedy
 * <P>
 * FileName:　　　　　DBOpenHelper.java
 * <P>
 * Description:　　　SQLiteOpenHelper
 * <P>
 * Author:　　　　　　XD.LIU
 * <P>
 * Create Date:　　　2012-6-27 下午8:01:31
 ********************************************************************/

public class DBOpenHelper extends SQLiteOpenHelper {
    /**
     * speedtest数据库
     */
    private final static String DB_NAME = "db_speedtest";
    /**
     * 数据库版本号
     */
    private static final int version = 2;
    /**
     * 数据表
     */
    public static final String TABLE_SPEEDVALUE = "speedhistory";
    /**
     * 数据库操作
     */
    private static final String CREATE_SPEEDTEST_RECORD_TABLE="create table if not exists " + TABLE_SPEEDVALUE 
            + "("
            + " _id integer primary key autoincrement," 
            + " testDateTime varchar(50),"     // 测速时刻
            + " testTime integer,"          // 测速时刻
            + " networkType varchar(20),"   // 网络连接方式
            + " internalIP varchar(20),"    // 内部IP
            + " externalIP varchar(20),"    // 外部IP
            + " server varchar(20),"        // 连接服务器地址
            + " latitude double(20),"      // 经度
            + " longitude double(20),"     // 纬度
            + " locationDesc varchar(50),"  // 地理位置描述
            + " ping integer,"              // 网络延迟时间
            + " downloadSpeed integer,"     // 下载速度
            + " uploadSpeed integer,"       // 上传速度
            + " costTime integer,"          // 测速消耗时间
            + " downloadByte integer,"          // 测速下载数据
            + " rank integer"              // 速度排名
            + ");";
    
    private static SQLiteDatabase db;
    /**
     * 数据库
     */
    private static DBOpenHelper dboh;
    private String dbName;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    public DBOpenHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
        this.dbName = dbName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDBTable(db);
    }
    
    
    private void createDBTable(SQLiteDatabase db) {
        db.execSQL(CREATE_SPEEDTEST_RECORD_TABLE);
    }

    /**
     * 关闭数据库连接
     */
    public static void closeDB() {
        if (db != null && db.isOpen()) {
            db.close();
            try {
                dboh.clone();
            } catch (CloneNotSupportedException e) {
            } finally {
                db = null;
            }

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reCreate(db);
    }

    /**
     * 更新数据库版本号时需修改此方法 重新创建数据表
     * 
     * @param db
     */
    public void reCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEEDVALUE);
        db.execSQL(CREATE_SPEEDTEST_RECORD_TABLE);
    }
    
}
