package com.example.heartrateandtemperaturemonitor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.heartrateandtemperaturemonitor.utils.TimeCycle;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "my.db";//数据库名称
    private static final int VERSION = 1;//数据库版本 这里定死为1

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table `user` (" +
                "`uid` INTEGER primary key autoincrement," +
                "`uname` VARCHAR(20)," +
                "`upassword` VARCHAR(20)," +
                "`per` INTEGER," +
                "`jphone` VARCHAR(255)," +
                "`createDateTime` VARCHAR(255))";
        sqLiteDatabase.execSQL(sql);//执行sql语句，record
        sql = "create table `history` (" +
                "`hid` INTEGER primary key autoincrement," +
                "`temp` VARCHAR(20)," +
                "`blood` VARCHAR(20)," +
                "`hreat` VARCHAR(255)," +
                "`createDateTime` VARCHAR(255))";
        sqLiteDatabase.execSQL(sql);//执行sql语句，record

        ContentValues values = new ContentValues();
        values.put("uname", "admin");
        values.put("upassword", "123456");
        values.put("per", 1);
        values.put("createDateTime", TimeCycle.getDateTime());
        sqLiteDatabase.insert("user", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}