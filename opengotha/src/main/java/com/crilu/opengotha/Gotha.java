package com.crilu.opengotha;

import java.rmi.RemoteException;
import java.util.Date;

import info.vannier.gotha.TeamTournamentParameterSet;
import info.vannier.gotha.Tournament;
import info.vannier.gotha.TournamentInterface;
import info.vannier.gotha.TournamentParameterSet;

public class Gotha {

    private int lastDisplayedStandingsUpdateTime;
    private int lastDisplayedTeamsStandingsUpdateTime;

    /**
     * current Tournament
     */
    private TournamentInterface tournament = null;

    public void startTournament(String name, String shortName, String location, String director,
                                Date startDate, Date endDate, int numberOfRounds, int numberOfCategories, int system) {
        TournamentInterface tournament;
        TournamentParameterSet tps = new TournamentParameterSet();
        tps.initBase(shortName, name, location, director, startDate, endDate, numberOfRounds, numberOfCategories);

        switch (system) {
            case TournamentParameterSet.TYPE_MCMAHON:
                tps.initForMM();
                break;
            case TournamentParameterSet.TYPE_SWISS:
                tps.initForSwiss();
                break;
            case TournamentParameterSet.TYPE_SWISSCAT:
                tps.initForSwissCat();
                break;
            default:
                tps.initForMM();

        }

        TeamTournamentParameterSet ttps = new TeamTournamentParameterSet();
        ttps.init();

        // close previous Tournament if necessary
        closeTournament();

        try {
            tournament = new Tournament();
            tournament.setTournamentParameterSet(tps);
            tournament.setTeamTournamentParameterSet(ttps);
            this.lastDisplayedStandingsUpdateTime = 0;
            this.lastDisplayedTeamsStandingsUpdateTime = 0;
            this.tournamentChanged();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void tournamentChanged() {
        updateTitle();
        if (tournament == null) {
            try {
                updateDisplayCriteria();
                updateStandingsComponents();
                updateControlPanel();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            return;
        }
        try {
            tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        updateAllViews();
    }

    private void updateAllViews() {
        // TODO: to be implemented
    }

    private void updateControlPanel() throws RemoteException {
        // TODO: to be implemented
    }

    private void updateStandingsComponents() throws RemoteException {
        // TODO: to be implemented
    }

    private void updateDisplayCriteria() throws RemoteException {
        // TODO: to be implemented
    }

    private void updateTitle() {
        // TODO: to be implemented
    }

    private void closeTournament() {
        // TODO: to be implemented
    }
}
