package com.uniqueAndroid.ximalaya.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.uniqueAndroid.ximalaya.utils.Constants;

public class XimalayaDBHelper extends SQLiteOpenHelper {

    public XimalayaDBHelper(Context context) {
        //factory是游标factory
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    public XimalayaDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public XimalayaDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public XimalayaDBHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        //订阅相关的字段
        //图片、title、描述、播放量、节目数量、作者名称、专辑id
        String subTbSQL = "create table " + Constants.SUB_TB_NAME + "(" + Constants.SUB_ID + " integer primary key autoincrement," + Constants.SUB_COVER_URL + " varchar," + Constants.SUB_TITLE + " varchar," + Constants.SUB_DESCRIPTION + " varchar," + Constants.SUB_PLAY_COUNT + " integer," + Constants.SUB_TB_TRACK_COUNT + " integer," + Constants.SUB_AUTHOR_NAME + " varchar," + Constants.SUB_ALBUM_ID + " integer);\n";
        db.execSQL(subTbSQL);

        //创建历史记录表
        String historyTbSql = "create table " + Constants.HISTORY_TB_NAME + "(" +
                Constants.HISTORY_ID + " integer primary key autoincrement," +
                Constants.HISTORY_TRACK_ID + " integer," +
                Constants.HISTORY_TITLE + "varchar," +
                Constants.HISTORY_COVER + "varchar," +
                Constants.HISTORY_PLAY_COUNT + " integer," +
                Constants.HISTORY_DURATION + " integer," +
                Constants.HISTORY_UPDATE_TIME + " integer);\n";
        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
