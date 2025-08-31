package com.example.heartrateandtemperaturemonitor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.heartrateandtemperaturemonitor.utils.MToast;
import com.example.heartrateandtemperaturemonitor.utils.TimeCycle;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements DaoBase {
    private Context context;
    private DBHelper helper;
    private SQLiteDatabase db;
    private String TAG = "HistoryDao";

    public HistoryDao(Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }

    @Override
    public int insert(Object object) {
        try {
            History history = (History) object;
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("temp", history.getTemp());
            values.put("blood", history.getBlood());
            values.put("hreat", history.getHreat());
            values.put("createDateTime", TimeCycle.getDateTime().split(" ")[0]);
            db.insert("history", null, values);
            db.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            MToast.mToast(context, "添加错误");
            return -1;
        }
    }

    @Override
    public int delete(String... data) {
        return 0;
    }

    @Override
    public int update(Object object, String... data) {
        try {
            db = helper.getWritableDatabase();
            History history = (History) object;
            ContentValues values = new ContentValues();
            db.update("history", values, "rid=?", data);
            return 1;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            MToast.mToast(context, "修改失败");
            return 0;
        }

    }

    @Override
    public List<Object> query(String... data) {
        try {
            List<Object> result = new ArrayList<Object>();
            db = helper.getReadableDatabase();
            Cursor cursor;
            String sql;
            switch (data.length) {
                case 2:
                    sql = "SELECT * FROM history where datetime(createDateTime) " +
                            "BETWEEN datetime(?) AND datetime(?) ORDER BY createDateTime;";
                    cursor = db.rawQuery(sql, data);
                    Log.d("测试","2");
                    break;
                default:
                    Log.d("测试","1");
                    sql = "SELECT * FROM history ORDER BY createDateTime;";
                    cursor = db.rawQuery(sql, null);
            }
            while (cursor.moveToNext()) {
                History temp = new History();
                temp.setHid(cursor.getInt(cursor.getColumnIndexOrThrow("hid")));
                temp.setTemp(cursor.getString(cursor.getColumnIndexOrThrow("temp")));
                temp.setBlood(cursor.getString(cursor.getColumnIndexOrThrow("blood")));
                temp.setHreat(cursor.getString(cursor.getColumnIndexOrThrow("hreat")));
                temp.setCreateDateTime(cursor.getString(cursor.getColumnIndexOrThrow("createDateTime")));
                result.add(temp);
            }
            cursor.close();
            db.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            MToast.mToast(context, "查询错误");
            return null;
        }
    }
}
