package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.model.GamesResults;

import java.util.List;
import java.util.Vector;

public class ResultViewModel extends AndroidViewModel {

    private GamesResults mGamesResults;

    public ResultViewModel(@NonNull Application application) {
        super(application);
        this.mGamesResults = GothandroidApplication.getGamesResultsInstance();
    }

    public GamesResults getGamesResults() {
        return mGamesResults;
    }

    public List<Vector<String>> getGames() {
        return mGamesResults.getTblGames();
    }

}
