package com.crilu.gothandroid.model;

import android.arch.lifecycle.ViewModel;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.model.GamesPair;
import com.crilu.opengotha.model.PlayersManager;

import java.util.List;

public class PlayersManagerViewModel extends ViewModel {
    private PlayersManager mPlayersManager;

    public PlayersManagerViewModel() {
        mPlayersManager = new PlayersManager(GothandroidApplication.getGothaModelInstance().getTournament());
    }

    public PlayersManager getPlayersManager() {
        return mPlayersManager;
    }

    public List<Player> getPlayersList() {
        return GothandroidApplication.getGothaModelInstance().getTournament().playersList();
    }

    public int getNumberOfRounds() {
        return GothandroidApplication.getGothaModelInstance().getTournament().getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
    }
}
