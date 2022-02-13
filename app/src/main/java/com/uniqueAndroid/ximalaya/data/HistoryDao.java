package com.uniqueAndroid.ximalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.utils.Constants;
import com.uniqueAndroid.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper mXimalayaDBHelper;
    private IHistoryDaoCallback mCallback;
    private Object mLock = new Object();

    public HistoryDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                //避免重复
                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=? ", new String[]{track.getDataId() + ""});
                db.beginTransaction();
                ContentValues values = new ContentValues();
                //封装数据
                values.put(Constants.HISTORY_TRACK_ID, track.getDataId());
                values.put(Constants.HISTORY_TITLE, track.getTrackTitle());
                values.put(Constants.HISTORY_COVER, track.getCoverUrlLarge());
                values.put(Constants.HISTORY_PLAY_COUNT, track.getPlayCount());
                values.put(Constants.HISTORY_DURATION, track.getDuration());
                values.put(Constants.HISTORY_UPDATE_TIME, track.getUpdatedAt());
                values.put(Constants.HISTORY_AUTHOR,track.getAnnouncer().getNickname());
                //插入数据
                db.insert(Constants.HISTORY_TB_NAME, null, values);
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                mCallback.onHistoryAdd(isSuccess);
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock){

            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=? ", new String[]{track.getDataId() + ""});
                LogUtil.d(TAG, "delete --> " + delete);
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryDel(isSuccess);
                }
            }
        }
    }

    @Override
    public void clearHistory() {
        synchronized (mLock){

            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.HISTORY_TB_NAME, null, null);
                LogUtil.d(TAG, "delete --> " + delete);
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoriesClean(isSuccess);
                }
            }
        }
    }

    @Override
    public void listHistories() {
        synchronized (mLock){

            //从数据表中查出所有的历史记录
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();
            try {
                db = mXimalayaDBHelper.getReadableDatabase();
                db.beginTransaction();
                Cursor query = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                while (query.moveToNext()) {
                    Track track = new Track();
                    //
                    String coverUrl = query.getString(query.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(coverUrl);
                    track.setCoverUrlSmall(coverUrl);
                    track.setCoverUrlMiddle(coverUrl);
                    //
                    String title = query.getString(query.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    //
                    int duration = query.getInt(query.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    //
                    long updateTime = query.getInt(query.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    //
                    int playCount = query.getInt(query.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    //
                    int trackId = query.getInt(query.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    String author = query.getString(query.getColumnIndex(Constants.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    track.setDataId(trackId);
                    histories.add(track);
                }
                db.setTransactionSuccessful();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoriesLoaded(histories);
                }
            }
        }
    }
}
