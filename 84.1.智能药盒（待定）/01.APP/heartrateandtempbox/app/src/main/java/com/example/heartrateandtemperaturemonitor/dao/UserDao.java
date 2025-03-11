package com.example.heartrateandtemperaturemonitor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.MailTo;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.heartrateandtemperaturemonitor.utils.MToast;
import com.example.heartrateandtemperaturemonitor.utils.TimeCycle;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class UserDao implements DaoBase {
    private Context context;
    private DBHelper helper;
    private SQLiteDatabase db;
    private String TAG = "UserDao";

    public UserDao(Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }

    /***
     * 添加一条数据
     * @param object
     * @return -1 错误 0重复 1成功
     */
    @Override
    public int insert(Object object) {
        try {
            User user = (User) object;
            if (user.getUname().equals("admin") || query(user.getUname()).size() != 0) return 0;
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("uname", user.getUname());
            values.put("upassword", user.getUpassword());
            values.put("createDateTime", TimeCycle.getDateTime());
            values.put("per", 0);
            db.insert("user", null, values);
            db.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            MToast.mToast(context, "添加失败");
            return -1;
        }

    }

    /***
     * 删除一条数据
     * @param data
     * @return -1 错误 0 失败 1成功
     */
    @Override
    public int delete(String... data) {
        try {
            if (data.length == 0) {
                return 0;
            }
            db = helper.getWritableDatabase();
            db.delete("user", "uid=?", new String[]{data[0]});
            db.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            MToast.mToast(context, "删除失败");
            return -1;
        }
    }

    @Override
    public int update(Object object, String... data) {
        try {
            if (object == null) return 0;
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            User user = (User) object;
            values.put("uname", user.getUname());
            values.put("upassword", user.getUpassword());
            values.put("createDateTime", user.getCreateDateTime());
            values.put("jphone", user.getJphone());
            db.update("user", values, "uid=?", new String[]{data[0]});
            db.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            MToast.mToast(context, "修改失败");
            return -1;
        }
    }

    @Override
    public List<Object> query(String... data) {
        try {
            List<Object> result = new ArrayList<>();
            Cursor cursor;
            String sql;
            db = helper.getReadableDatabase();
            switch (data.length) {
                case 1:
                    sql = "SELECT * from user where uid = ?";
                    cursor = db.rawQuery(sql, new String[]{data[0]});
                    break;
                case 2:
                    if (data[1].equals("name")) {
                        sql = "SELECT * FROM user where uname = ?";
                        cursor = db.rawQuery(sql, new String[]{data[0]});
                    } else {
                        sql = "SELECT * FROM user where uname = ? and upassword =?";
                        cursor = db.rawQuery(sql, data);
                    }
                    break;
                default:
                    sql = "select * from user";
                    cursor = db.rawQuery(sql, null);
            }
            while (cursor.moveToNext()) {
                User user = new User();
                user.setUid(cursor.getInt(cursor.getColumnIndexOrThrow("uid")));
                user.setUname(cursor.getString(cursor.getColumnIndexOrThrow("uname")));
                user.setUpassword(cursor.getString(cursor.getColumnIndexOrThrow("upassword")));
                user.setPer(cursor.getInt(cursor.getColumnIndexOrThrow("per")));
                user.setJphone(cursor.getString(cursor.getColumnIndexOrThrow("jphone")));
                user.setCreateDateTime(cursor.getString(cursor.getColumnIndexOrThrow("createDateTime")));
                result.add(user);
            }
            cursor.close();
            db.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("查询", e.toString());
            MToast.mToast(context, "查询失败");
            return null;
        }
    }
}
