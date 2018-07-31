package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.model.GamesOptions;

public class GameOptionViewModel extends AndroidViewModel {

    private GamesOptions mGameOptions;

    public GameOptionViewModel(@NonNull Application application) {
        super(application);
        this.mGameOptions = GothandroidApplication.getGamesOptionsInstance();
    }

    public GamesOptions getGameOptions() {
        return mGameOptions;
    }

}
