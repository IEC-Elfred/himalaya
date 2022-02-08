package com.uniqueAndroid.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubDAO {

    void addAlbum(Album album);

    void delAlbum(Album album);

    void listAlbums();

    void setCallback(ISubDaoCallback callback);
}
