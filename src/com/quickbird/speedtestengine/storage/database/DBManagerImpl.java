package com.quickbird.speedtestengine.storage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.quickbird.speedtestengine.utils.DebugUtil;

public class DBManagerImpl implements DBManager{
    protected DBOpenHelper dbOpenHelper;
    protected SQLiteDatabase db;
    
    public DBManagerImpl(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }
    
    public DBManagerImpl(Context context, String dbName, int version) {
        dbOpenHelper = new DBOpenHelper(context, dbName, version);
    }

    /**
     * 打开可写数据库连接
     */
    protected void openWrite() {
        db = dbOpenHelper.getWritableDatabase();
    }

    /**
     * 打开可读数据库连接
     */
    protected void openRead() {
        db = dbOpenHelper.getReadableDatabase();
    }

    /**
     * 关闭数据库连接
     */
    @Override
    public void closeDB() {
        if (db != null && db.isOpen()) {
            try {
                db.close();
            } catch (Exception e) {
            }
        }
    }
    
    @Override
    public SQLiteDatabase getCurDB(){
        if(db != null && db.isOpen()){
            return db;
        }else{
            return null;
        }
    }
    
    /**
     * 是否为空数据表
     * @return booleanValue  true:空表；false:非空
     */
    public synchronized boolean isNullTable(String tableName) {
        Cursor c = null;
        try {
            openRead();
            c = db.rawQuery("select * from " + tableName + " ", null);
            return !(c != null && c.getCount()>0);
        } catch (Exception e) {
            DebugUtil.e("isNull() error! " + e.getMessage());
        } finally{
            if(c!=null){
                c.close();
            }
            closeDB();
        }
        return true;
    }

    @Override
    public boolean deleteById(String tableName, long id) {

        try {
            openWrite();
            db.delete(tableName, " _id=? ", new String[]{""+id});
        } catch (Exception e) {
            DebugUtil.e("DBManagerImpl.deleteById(String tableName, long id) error!!! ");
//          e.printStackTrace();
            return false;
        } finally {
            closeDB();
        }
        return true;
    
    }

    @Override
    public boolean insertWithSql(String sql, String tableName) {
        DebugUtil.i("DBManagerImpl.insert(String sql, String tableName) sql is: "+sql);
        try {
            openWrite();
            db.execSQL(sql);
            DebugUtil.i( "DB Insert into "+tableName);
        } catch (Exception e) {
            DebugUtil.e("DBManagerImpl.insert(String sql, String tableName) error!!! ");
//          e.printStackTrace();
            return false;
        }finally{
            closeDB();
        }
        return true;
    }

    @Override
    public boolean delete(String whereClause, String[] whereArgs, String tableName) {
        try {
            openWrite();
            db.delete(tableName, whereClause, whereArgs);
        } catch (Exception e) {
            DebugUtil.e("DBManagerImpl.delete(String whereClause, String[] whereArgs, String tableName) error!!! ");
//          e.printStackTrace();
            return false;
        }finally{
            closeDB();
        }
        return true;
    }
    
}
