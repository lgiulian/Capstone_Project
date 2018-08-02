package com.crilu.opengotha.model;

import com.crilu.opengotha.DPParameterSet;
import com.crilu.opengotha.Game;
import com.crilu.opengotha.GeneralParameterSet;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.PlacementParameterSet;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.PublishParameterSet;
import com.crilu.opengotha.ScoredPlayer;
import com.crilu.opengotha.StandingsTableModel;
import com.crilu.opengotha.TeamTournamentParameterSet;
import com.crilu.opengotha.Tournament;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.TournamentParameterSet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class GothaModel {

    public interface GothaListener {
		void updateTitle();
		void warnPreliminaryRegisteringStatus(String message);
		void controlPannelModelUpdated();
		void updateTime(String time);
		void roundNumberChanged(int round);
        void onCurrentTournamentChanged();
	}

    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    public static final int BIG_FRAME_WIDTH = 1000;
    public static final int BIG_FRAME_HEIGHT = 600;
    public static final int MEDIUM_FRAME_WIDTH = 796;
    public static final int MEDIUM_FRAME_HEIGHT = 553;
    public static final int SMALL_FRAME_WIDTH = 540;
    public static final int SMALL_FRAME_HEIGHT = 350;
    private static final int NUM_COL = 0;
    private static final int PL_COL = 1;
    private static final int NAME_COL = 2;
//    private static final int RANK_COL = 3;
    private static final int GRADE_COL = 3;
//    private static final int COUNTRY_COL = RANK_COL + 1;
    private static final int COUNTRY_COL = GRADE_COL + 1;
    private static final int CLUB_COL = COUNTRY_COL + 1;
    private static final int NBW_COL = CLUB_COL + 1;
    private static final int ROUND0_RESULT_COL = NBW_COL + 1;
    private static final int CRIT0_COL = ROUND0_RESULT_COL + Gotha.MAX_NUMBER_OF_ROUNDS;
    
    private static final int TEAM_PL_COL = 0;
    private static final int TEAM_NAME_COL = 1;
    private static final int TEAM_ROUND0_RESULT_COL = 2;
    private static final int TEAM_CRIT0_COL = TEAM_ROUND0_RESULT_COL + Gotha.MAX_NUMBER_OF_ROUNDS;
    // Teams Panel constants
    protected static final int TM_NUMBER_OF_COLS = 8;
    protected static final int TM_TEAM_NUMBER_COL = 0;
    protected static final int TM_TEAM_NAME_COL = 1;
    protected static final int TM_BOARD_NUMBER_COL = 2;
    protected static final int TM_PL_NAME_COL = 3;
    protected static final int TM_PL_COUNTRY_COL = 4;
    protected static final int TM_PL_CLUB_COL = 5;
    protected static final int TM_PL_RATING_COL = 6;
    protected static final int TM_PL_ROUNDS_COL = 7;
    /**
     * should stay between 0 and 9
     */
    private static final int MAX_NUMBER_OF_RECENT_TOURNAMENTS = 6;
    private int displayedRoundNumber = 0;
    
    private long lastDisplayedStandingsUpdateTime = 0;
    private int lastDisplayedTeamsStandingsUpdateTime;
    private int[] displayedCriteria = new int[PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA];
    private boolean bDisplayTemporaryParameterSet = false;
    
    private List<GothaListener> mCallbacks = new ArrayList<>();
    private List<Vector<String>> controlPanelModel;
    // header: "Num", "Pl", "Name", "Gr", "Co", "Cl", "NBW", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12", "R13", "R14", "R15", "R16", "R17", "R18", "R19", "R20", "crit1", "crit2", "crit3", "crit4", "crit5", "crit6"
    private StandingsTableModel tblStandings = new StandingsTableModel();
    
    /**
     * current Tournament
     */
    private TournamentInterface tournament = null;

    public void startTournament(String name, String shortName, String location, String director,
                                Date startDate, Date endDate, int numberOfRounds, int numberOfCategories, int system) {
    	
    	int nbRounds = numberOfRounds;
        if (numberOfRounds < 0) {
            nbRounds = 1;
        }
        if (numberOfRounds > Gotha.MAX_NUMBER_OF_ROUNDS) {
            nbRounds = Gotha.MAX_NUMBER_OF_ROUNDS;
        }
        
        TournamentParameterSet tps = new TournamentParameterSet();
        tps.initBase(shortName, name, location, director, startDate, endDate, nbRounds, numberOfCategories);

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

        tournament = new Tournament();
        tournament.setTournamentParameterSet(tps);
        tournament.setTeamTournamentParameterSet(ttps);
        this.lastDisplayedStandingsUpdateTime = 0;
        this.lastDisplayedTeamsStandingsUpdateTime = 0;
        this.tournamentChanged();
    }

    public void openTournamentFromFile(File f, String tournamentIdentity) throws IOException, ClassNotFoundException {
        tournament = Gotha.getTournamentFromFile(f);
        tournament.setTournamentIdentity(tournamentIdentity);
        this.tournamentChanged();
    }

    private void tournamentChanged() {
        updateTitle();
        if (tournament == null) {
            updateDisplayCriteria();
            updateStandingsComponents();
            updateControlPanel();
            return;
        }
        tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());

        currentTournamentChanged();
        updateAllViews();
    }

    private void updateAllViews() {
        updateTitle();
    }

    private void updateControlPanel() {
    	if (tournament == null) {
    	    return;
        }

        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        // here the model was DefaultTableModel model = (DefaultTableModel) tblControlPanel.getModel();
        // with header: "Round", "Participants", "Assigned players", "Entered results"
        controlPanelModel = new ArrayList<>();
        
        ArrayList<Player> alPlayers = tournament.playersList();

        for (int r = 0; r < tps.getGeneralParameterSet().getNumberOfRounds(); r++) {
            // Number of participants
            int nbParticipants = 0;
            for (Player p : alPlayers) {
                if (p.getParticipating()[r]) {
                    nbParticipants++;
                    // Assigned players, games, etc.
                }
            }
            ArrayList<Game> alGames = tournament.gamesList(r);
            int nbGames = alGames.size();
            int nbAssignedPlayers = 2 * nbGames;
            if (tournament.getByePlayer(r) != null) {
                nbAssignedPlayers++;
            }
            int nbEntResults = 0;
            for (Game g : alGames) {
                int result = g.getResult();
                if (result != Game.RESULT_UNKNOWN) {
                    nbEntResults++;
                }
            }

            Vector<String> row = new Vector<String>();
            row.add("" + (r + 1));

            row.add("" + nbParticipants);
            row.add("" + nbAssignedPlayers);
            row.add("" + nbEntResults + "/" + nbGames);

            controlPanelModel.add(row);
            for (GothaListener callback : mCallbacks) {
    			callback.controlPannelModelUpdated();
            }
        }

        int nbPreliminary = 0;
        int nbFinal = 0;
        for (Player p : alPlayers) {
            if (p.getRegisteringStatus().compareTo("PRE") == 0) {
                nbPreliminary++;

            }
            if (p.getRegisteringStatus().compareTo("FIN") == 0) {
                nbFinal++;

            }
        }
        if (nbPreliminary == 1) {
        	for (GothaListener callback : mCallbacks) {
    			callback.warnPreliminaryRegisteringStatus("Warning!" + nbPreliminary
                    + "player has a Preliminary registering status");
    		}
        }
        if (nbPreliminary > 1) {
        	for (GothaListener callback : mCallbacks) {
    			callback.warnPreliminaryRegisteringStatus("Warning!" + nbPreliminary
                        + "players have a Preliminary registering status");
    		}
        }
        
        // Aceess to opengotha.info
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        boolean bExportHFOG = pubPS.isExportHFToOGSite();
        if (bExportHFOG) {
            String dirName = new SimpleDateFormat("yyyyMMdd").format(gps.getBeginDate()) + tournament.getShortName() + "/";
            String strURL = "http://opengotha.info/tournaments/" + dirName;
            try {
                // TODO: create QRCode
            } catch (Exception ex) {
                Logger.getLogger(GothaModel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    private void updateStandingsComponents() {
    	if (tournament == null) {
            return;
        }

        int nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (displayedRoundNumber > nbRounds - 1) {
            displayedRoundNumber = nbRounds - 1;
        }
        for (GothaListener callback : mCallbacks) {
			callback.roundNumberChanged(displayedRoundNumber + 1);
        }

        // Define displayedTPS
        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        TournamentParameterSet displayedTPS = new TournamentParameterSet(tps);
        PlacementParameterSet displayedPPS = displayedTPS.getPlacementParameterSet();
        displayedPPS.setPlaCriteria(displayedCriteria);

        int gameFormat = tps.getDPParameterSet().getGameFormat();
        int numberOfDisplayedRounds = 8;
        if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) {
            numberOfDisplayedRounds = 10;
        }

        lastDisplayedStandingsUpdateTime = tournament.getCurrentTournamentTime();
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = new ArrayList<ScoredPlayer>();
        alOrderedScoredPlayers = tournament.orderedScoredPlayersList(displayedRoundNumber, displayedTPS.getPlacementParameterSet());
        // Eliminate non-players
        alOrderedScoredPlayers = eliminateNonImpliedPlayers(alOrderedScoredPlayers);
        boolean bFull = true;
        if (gameFormat == DPParameterSet.DP_GAME_FORMAT_SHORT) {
            bFull = false;
        }
        String[][] hG = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, displayedRoundNumber, displayedTPS, bFull);

        List<String> columnModel = tblStandings.getColumnHeaders();

        String strNumHeader = "Num";
        if (!tps.getDPParameterSet().isDisplayNumCol()) {
            strNumHeader = "";
        }
        columnModel.set(NUM_COL, strNumHeader);

        String strPlHeader = "Pl";
        if (!tps.getDPParameterSet().isDisplayPlCol()) {
            strPlHeader = "";
        }
        columnModel.set(PL_COL, strPlHeader);
        
        columnModel.set(NAME_COL, "Name");
//        columnModel.set(RANK_COL, "Rk");
        columnModel.set(GRADE_COL, "Gr");
        String strCoHeader = "Co";
        if (!tps.getDPParameterSet().isDisplayCoCol()) {
            strCoHeader = "";
        }
        columnModel.set(COUNTRY_COL, strCoHeader);

        String strClHeader = "Cl";
        if (!tps.getDPParameterSet().isDisplayClCol()) {
            strClHeader = "";
        }
        columnModel.set(CLUB_COL, strClHeader);
        
        columnModel.set(NBW_COL, "NBW");

        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
        	columnModel.set(ROUND0_RESULT_COL + r, "R" + (r + 1));
        }
        for (int c = 0; c < PlacementParameterSet.PLA_MAX_NUMBER_OF_CRITERIA; c++) {
        	columnModel.set(CRIT0_COL + c, PlacementParameterSet.criterionShortName(displayedCriteria[c]));
        }

        List<List<Object>> model = tblStandings.getTableValues();
        
        String[] strPlace = ScoredPlayer.catPositionStrings(alOrderedScoredPlayers, displayedRoundNumber, displayedTPS);
        for (int iSP = 0; iSP < alOrderedScoredPlayers.size(); iSP++) {
        	model.add(new ArrayList<>());
            int iCol = 0;
            ScoredPlayer sp = alOrderedScoredPlayers.get(iSP);
            String strNum = "" + (iSP + 1);
            if (!tps.getDPParameterSet().isDisplayNumCol()) {
                strNum = "";
            }
            model.get(iSP).set(iCol++, strNum);
            
            String strPl = "" + strPlace[iSP];
            if (!tps.getDPParameterSet().isDisplayPlCol()) {
                strPl = "";
            }
            model.get(iSP).set(iCol++, strPl);
  
            model.get(iSP).set(iCol++, sp.fullName());
                        
//            model.setValueAt(Player.convertIntToKD(sp.getRank()), iSP, iCol++);
            model.get(iSP).set(iCol++, sp.getStrGrade());

            String strCo = sp.getCountry();
            if (!tps.getDPParameterSet().isDisplayCoCol()) {
                strCo = "";
            }
            model.get(iSP).set(iCol++, strCo);
            
            String strCl = sp.getClub();
            if (!tps.getDPParameterSet().isDisplayClCol()) {
                strCl = "";
            }
            model.get(iSP).set(iCol++, strCl);
           
            
            model.get(iSP).set(iCol++, sp.formatScore(PlacementParameterSet.PLA_CRIT_NBW, this.displayedRoundNumber));
            for (int r = 0; r <= displayedRoundNumber; r++) {
            	model.get(iSP).set(iCol++, (hG[r][iSP]));
            }
            for (int r = displayedRoundNumber + 1; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            	model.get(iSP).set(iCol++, "");
            }
            for (int c = 0; c < displayedCriteria.length; c++) {
            	model.get(iSP).set(iCol++, sp.formatScore(displayedCriteria[c], this.displayedRoundNumber));
            }
        }

        java.util.Date dh = new java.util.Date(lastDisplayedStandingsUpdateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        String strTime = sdf.format(dh);
        for (GothaListener callback : mCallbacks) {
			callback.updateTime("updated at : " + strTime);
        }
    }

    private ArrayList<ScoredPlayer> eliminateNonImpliedPlayers(ArrayList<ScoredPlayer> alSP) {
        HashMap<Player, Boolean> hmPlayersImplied = new HashMap<Player, Boolean>();
        ArrayList<Game> alG = tournament.gamesList();
        for (Game g : alG) {
            Player wP = g.getWhitePlayer();
            hmPlayersImplied.put(wP, true);
            Player bP = g.getBlackPlayer();
            hmPlayersImplied.put(bP, true);
        }
        for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
            Player byeP = tournament.getByePlayer(r);
            if (byeP != null) {
                hmPlayersImplied.put(byeP, true);
            }
        }
        for (Iterator<ScoredPlayer> it = alSP.iterator(); it.hasNext();) {
            ScoredPlayer sP = it.next();
            Boolean b = hmPlayersImplied.get(sP);
            if (b == null) {
                continue;
            }
            if (!b) {
                it.remove();
            }
        }
        return alSP;
    }
    
    private void updateDisplayCriteria() {
    	// TODO: code deleted. Compare with original code if is needed

        PlacementParameterSet displayedPPS = null;
        if (tournament != null) {
            displayedPPS = tournament.getTournamentParameterSet().getPlacementParameterSet();
            displayedCriteria[0] = displayedPPS.getPlaCriteria()[0];
            displayedCriteria[1] = displayedPPS.getPlaCriteria()[1];
            displayedCriteria[2] = displayedPPS.getPlaCriteria()[2];
            displayedCriteria[3] = displayedPPS.getPlaCriteria()[3];
        }
    }

    private void updateTitle() {
    	for (GothaListener callback : mCallbacks) {
			callback.updateTitle();
		}
    }

    private void currentTournamentChanged() {
        for (GothaListener callback : mCallbacks) {
            callback.onCurrentTournamentChanged();
        }
    }
    private void closeTournament() {
        if (tournament == null) {
            return;
        }
        tournament.close();
        this.tournamentChanged();
        tournament = null;
        this.tournamentChanged();
        
        Preferences prefsRoot = Preferences.userRoot();
        Preferences gothaPrefs = prefsRoot.node(Gotha.strPreferences);
        gothaPrefs.put("tournamentCopy", "");
    }

	public List<Vector<String>> getControlPanelModel() {
		return controlPanelModel;
	}
    
    public boolean addGothaListener(GothaListener listener) {
    	return mCallbacks.add(listener);
    }
    
    public boolean removeGothaListener(GothaListener listener) {
    	return mCallbacks.remove(listener);
    }

    public TournamentInterface getTournament() {
        return tournament;
    }

    public void setTournament(TournamentInterface tournament) {
        this.tournament = tournament;
        currentTournamentChanged();
    }

}
