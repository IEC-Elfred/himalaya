package com.uniqueAndroid.ximalaya.presenters;

import com.uniqueAndroid.ximalaya.base.BaseApplication;
import com.uniqueAndroid.ximalaya.data.ISubDaoCallback;
import com.uniqueAndroid.ximalaya.data.SubscriptionDao;
import com.uniqueAndroid.ximalaya.interfaces.ISubscriptionCallback;
import com.uniqueAndroid.ximalaya.interfaces.ISubscriptionPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private static SubscriptionPresenter sInstance = null;
    private final SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<Long, Album>();
    private List<ISubscriptionCallback> mCallback = new ArrayList<ISubscriptionCallback>();

    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
    }

    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                //只调用不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public static SubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                sInstance = new SubscriptionPresenter();
            }
        }
        return sInstance;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallback.contains(iSubscriptionCallback)) {
            mCallback.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallback.remove(iSubscriptionCallback);
    }


    @Override
    public void addSubscription(Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        //不为空表示已经订阅
        return result != null;
    }

    @Override
    public void onAddResult(boolean isSuccess) {
        listSubscriptions();
        //添加结果回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback iSubscriptionCallback : mCallback) {
                    iSubscriptionCallback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        listSubscriptions();
        //删除订阅的回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback iSubscriptionCallback : mCallback) {
                    iSubscriptionCallback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(List<Album> result) {
        mData.clear();
        //加载数据的回调
        for (Album album : result) {
            mData.put(album.getId(), album);
        }
        //Todo 通知UI更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback iSubscriptionCallback : mCallback) {
                    iSubscriptionCallback.onSubscriptionLoaded(result);
                }
            }
        });
    }
}
