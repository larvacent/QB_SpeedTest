package com.quickbird.speedtestengine.storage.database;

import android.database.sqlite.SQLiteDatabase;

public interface DBManager {

    /**
     * 关闭数据库连接
     */
    public void closeDB();
    
    /**
     * 获取当前SQLiteDatabase
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getCurDB();
    
    /**
     * 删除记录
     * @param tableName 表名
     * @param _id 主键
     * @note 只对主键是“_id”的表起作用
     * @see delete(String whereClause, String[] whereArgs, String tableName)
     */
    boolean deleteById(String tableName , long id);

    /**
     * 用SQL语句插入单条记录 
     * @param sql
     * @param tableName 数据库名称
     * @return
     */
    public boolean insertWithSql(String sql,String tableName);
    
    /**
     * 删除
     * @param whereClause
     * @param whereArgs
     * @param tableName
     * @return
     */
    public boolean delete(String whereClause, String[] whereArgs, String tableName);
    
}
