package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.model.GamesPair;

import java.util.List;
import java.util.Vector;

public class PairViewModel extends AndroidViewModel {

    private final GamesPair mGamesPair;

    public PairViewModel(@NonNull Application application) {
        super(application);
        this.mGamesPair = GothandroidApplication.getGamesPairInstance();
    }

    public GamesPair getGamesPair() {
        return mGamesPair;
    }

    public List<Vector<String>> getTables() {
        return mGamesPair.getTblGames();
    }

    public List<Vector<String>> getPairablePlayers() {
        return mGamesPair.getTblPairablePlayers();
    }
}
