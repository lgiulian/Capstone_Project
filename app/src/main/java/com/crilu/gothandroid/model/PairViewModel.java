package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.model.GamesPair;

import java.util.ArrayList;
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
        List<Vector<String>> games = new ArrayList<>();
        if (mGamesPair != null) {
            games = mGamesPair.getTblGames();
        }
        return games;
    }

    public List<Vector<String>> getPairablePlayers() {
        List<Vector<String>> players = new ArrayList<>();
        if (mGamesPair != null) {
            players = mGamesPair.getTblPairablePlayers();
        }
        return players;
    }
}
