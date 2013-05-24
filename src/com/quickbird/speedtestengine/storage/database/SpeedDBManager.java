package com.quickbird.speedtestengine.storage.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.utils.DebugUtil;

/*******************************************************************
 * Copyright @ 2013 ChenFengYun (BeiJing) Technology LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　TestSpeed
 * <P>
 * FileName:　　　　SpeedDBManager.java
 * <P>
 * Description:　　　
 * <P>
 * Author:　　　　　　XD.LIU
 * <P>
 * Create Date:　　　2013-1-28 上午11:40:11
 ********************************************************************/

public class SpeedDBManager extends DBManagerImpl {
    private final String TABLENAME = DBOpenHelper.TABLE_SPEEDVALUE;
    // 数据库列
    private final String[] columns = new String[] { "_id", "testDateTime",
            "testTime", "networkType", "internalIP", "externalIP", "server",
            "latitude", "longitude", "locationDesc", "ping", "downloadSpeed",
            "uploadSpeed", "costTime", "downloadByte", "rank" };

    public SpeedDBManager(Context context) {
        super(context);
    }

    /**
     * 插入测速结果
     * 
     * @param msg
     * @return 插入是否成功
     */
    public boolean insertSpeedValue(SpeedValue speed) {
        try {
            openWrite();
            ContentValues initialValues = new ContentValues();
            initialValues.put("testDateTime", speed.getTestDateTime());
            initialValues.put("testTime", speed.getTestTime());
            initialValues.put("networkType", speed.getNetworkType());
            initialValues.put("internalIP", speed.getInternalIP());
            initialValues.put("externalIP", speed.getExternalIP());
            initialValues.put("server", speed.getServer());
            initialValues.put("latitude", speed.getLatitude());
            initialValues.put("longitude", speed.getLongitude());
            initialValues.put("locationDesc", speed.getLocationDesc());
            initialValues.put("ping", speed.getPing());
            initialValues.put("downloadSpeed", speed.getDownloadSpeed());
            initialValues.put("uploadSpeed", speed.getUploadSpeed());
            initialValues.put("costTime", speed.getCostTime());
            initialValues.put("downloadByte", speed.getDownloadByte());
            initialValues.put("rank", speed.getRank());
            long i = db.insert(TABLENAME, null, initialValues);
            DebugUtil.i("DB Insert into " + TABLENAME + " :" + i);
        } catch (Exception e) {
            DebugUtil.e("SpeedDBManager.insert() error!!! "+e.getMessage());
            return false;
        } finally {
            closeDB();
        }
        return true;
    }

    /**
     * 批量插入测速结果
     * 
     * @param list
     * @return 插入是否成功
     */
    public boolean batchInsertSpeedValue(List<SpeedValue> list) {
        for (SpeedValue values : list) {
            insertSpeedValue(values);
        }
        return true;
    }

    /**
     * 删除
     * 
     * @param whereClause
     * @param whereArgs
     * @return 删除是否成功
     */
    public boolean delete(String whereClause, String[] whereArgs) {
        boolean bool = false;
        try {
            openWrite();
            if(db.delete(TABLENAME, whereClause, whereArgs)<=0)
                bool = false;
            else
                bool = true;
        } catch (Exception e) {
            DebugUtil.e("SpeedDBManager.delete() error!!! ");
            e.printStackTrace();
            return false;
        } finally {
            closeDB();
        }
        return bool;
    }
    
    /**
     * @return 删除是否成功
     */
    public boolean deleteAllSpeedValues() {
        boolean bool = false;
        try {
            openWrite();
            dbOpenHelper.reCreate(db);
            bool = true;
        } catch (Exception e) {
            DebugUtil.e("update() error! " + e.getMessage());
            return false;
        } finally {
            closeDB();
        }
        return bool;
    }
    
    /**
     * 按照主键删除记录
     */
    public boolean deleteBySpeedId(int speedId) {
        boolean bool = false;
        try {
            openWrite();
            if (db.delete(TABLENAME, "_id = ?", new String[] { speedId + "" }) <= 0)
                bool = false;
            else
                bool = true;
        } catch (Exception e) {
            DebugUtil.e("update() error! " + e.getMessage());
            return false;
        }finally{
            closeDB();
        }
        return bool;
    }
    
    /**
     * 按照日期删除记录
     * @param speedId
     */
    public boolean deleteByDate(long testDateTime) {
        boolean bool = false;
        try {
            openWrite();
            if(db.delete(TABLENAME, "testDateTime = ?", new String[] { testDateTime + "" })<=0)
                bool = false;
            else
                bool = true;
        } catch (Exception e) {
            DebugUtil.e("update() error! " + e.getMessage());
            return false;
        }finally{
            closeDB();
        }
        return bool;
    }

    /**
     * 获取保存的测速记录数量
     * 
     * @return
     */
    public int getSpeedValuesCount() {
        Cursor cursor = null;
        try {
            openRead();
            cursor = db.query(TABLENAME, columns, null, null, null, null,
                    " _id desc ");
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            DebugUtil.e("speedValuesCount error!!! " + e.getMessage());
        } finally {
            // 使用完cursor后进行关闭
            if (cursor != null) {
                cursor.close();
            }
            closeDB();
        }
        return 0;
    }

    /**
     * 获取所有测速记录
     * 
     * @param realId
     * @return
     * @note &nbsp; 注意 db没有close，用完记得关闭
     */
    public List<SpeedValue> getAllSpeedValues() {
        List<SpeedValue> list = new ArrayList<SpeedValue>();
        Cursor cursor = null;
        try {
            openRead();
            cursor = db.query(TABLENAME, columns, null, null, null, null,
                    " _id desc ");
            while (cursor != null && cursor.moveToNext()) {
                SpeedValue speed = convertSpeedValue(cursor);
                list.add(speed);
            }
        } catch (Exception e) {
            DebugUtil.e("SpeedDBManager.queryAll() error!!! " + e.getMessage());
        } finally {
            // 使用完cursor后进行关闭
            if (cursor != null) {
                cursor.close();
            }
            closeDB();
        }
        return list;
    }
    
    public SpeedValue getSpeedValueById(int speedId) {
        SpeedValue speedValue= new SpeedValue();
        Cursor cursor = null;
        try {
            openRead();
            DebugUtil.d("speedId:"+speedId);
            cursor = db.query(TABLENAME, columns, " _id = ? ", new String[]{ speedId+""}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                speedValue = convertSpeedValue(cursor);
            }
        } catch (Exception e) {
            DebugUtil.e("SpeedDBManager.getSpeedValueById error!!! " + e.getMessage());
        } finally {
            // 使用完cursor后进行关闭
            if (cursor != null) {
                cursor.close();
            }
            closeDB();
        }
        return speedValue;
    }
    
    public List<SpeedValue> getSpeedValueByTime(long testTime) {
        List<SpeedValue> list = new ArrayList<SpeedValue>();
        Cursor cursor = null;
        try {
            openRead();
            
            cursor = db.query(TABLENAME, columns, "testTime = ? ", new String[]{testTime + ""}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                SpeedValue speed = convertSpeedValue(cursor);
                list.add(speed);
            }
        } catch (Exception e) {
            DebugUtil.e("SpeedDBManager.queryAll() error!!! " + e.getMessage());
        } finally {
            // 使用完cursor后进行关闭
            if (cursor != null) {
                cursor.close();
            }
            closeDB();
        }
        return list;
    }

    /**
     * 根据游标生成测速记录
     * 
     * @param c
     * @return
     */
    private SpeedValue convertSpeedValue(Cursor c) {
        SpeedValue speed = new SpeedValue();
        speed.setSpeedId(c.getInt(0));
        speed.setTestDateTime(c.getString(1));
        speed.setTestTime(c.getLong(2));
        speed.setNetworkType(c.getString(3));
        speed.setInternalIP(c.getString(4));
        speed.setExternalIP(c.getString(5));
        speed.setServer(c.getString(6));
        speed.setLatitude(c.getDouble(7));
        speed.setLongitude(c.getDouble(8));
        speed.setLocationDesc(c.getString(9));
        speed.setPing(c.getInt(10));
        speed.setDownloadSpeed(c.getInt(11));
        speed.setUploadSpeed(c.getInt(12));
        speed.setCostTime(c.getInt(13));
        speed.setDownloadByte(c.getInt(14));
        speed.setRank(c.getInt(15));
        return speed;
    }
    
    public List<SpeedValue> getAllSpeedValuesByDate() {
        return null;
    }
    
    public List<SpeedValue> getAllSpeedValuesBySpeed() {
        return null;
    }
    
    public List<SpeedValue> getAllSpeedValuesByRank() {
        return null;
    }
    
}
