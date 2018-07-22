package com.crilu.opengotha.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crilu.opengotha.Game;
import com.crilu.opengotha.GameComparator;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.Pairing;
import com.crilu.opengotha.PlacementParameterSet;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.PlayerComparator;
import com.crilu.opengotha.ScoredPlayer;
import com.crilu.opengotha.TournamentException;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.TournamentParameterSet;
import com.crilu.opengotha.TournamentPrinting;

public class GamesPair {

    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    private static final int NAME_COL = 0;
    private static final int RANK_COL = 1;
    private static final int SCORE_COL = 2;
    private static final int COUNTRY_COL = 3;
    private static final int CLUB_COL = 4;
    private static final int TABLE_NUMBER_COL = 0;
    private static final int WHITE_PLAYER_COL = 1;
    private static final int BLACK_PLAYER_COL = 2;
    private static final int HANDICAP_COL = 3;
    
    private int playersSortType = PlayerComparator.SCORE_ORDER;
    private int gamesSortType = GameComparator.TABLE_NUMBER_ORDER;
    /**  current Tournament */
    private TournamentInterface tournament = null;
    /** current Round */
    private int processedRoundNumber = 0;
	

    private List<Vector<String>> tblGames;
    private List<Vector<String>> tblNotPairablePlayers;

    private List<Vector<String>> tblPairablePlayers;
    private List<Vector<String>> tblPreviousGames;
    private int roundNumber;
	private String pairablePlayersNumber;
	private String unPairablePlayersNumber;
	private String gamesNumber;
	private String byePlayerLabel;
	private String byePlayerBtnText;
	private int spnRoundNumber;
	private String previousGamesLabel;

    private OnPairListener pairListener;
	
	public interface OnPairListener {
		void onMessage(String message);
		void onTableGamesUpdated();
		void onSearchResult(int row);
		void onShouldDisplayPanelInternal();
	}
	
    public GamesPair(TournamentInterface tournament) {
//        LogElements.incrementElement("games.pair", "");
        this.tournament = tournament;
        processedRoundNumber = tournament.presumablyCurrentRoundNumber();
        initComponents();
        customInitComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * Unlike initComponents, customInitComponents is editable
     */
    private void customInitComponents() {
        updateAllViews();
    }

    private void updateComponents() {
    	tblPairablePlayers.clear();;
    	tblNotPairablePlayers.clear();
    	tblGames.clear();
    	
        this.roundNumber = this.processedRoundNumber + 1;
        demandedDisplayedRoundNumberHasChanged();

        HashMap<String, Player> hmPlayers = null;
        ArrayList<Game> alActualGames = null;
        Player byePlayer = null;
        hmPlayers = tournament.playersHashMap();
		alActualGames = tournament.gamesList(processedRoundNumber);
		byePlayer = tournament.getByePlayer(processedRoundNumber);

        // Unassigned players
        // hmPairablePlayers will be set by substraction
        // alNotPairablePlayers will be set by addition
        HashMap<String, Player> hmPairablePlayers = new HashMap<String, Player>(hmPlayers);
        ArrayList<Player> alNotPairablePlayers = new ArrayList<Player>();

        for (Player p : hmPlayers.values()) {
            if (p.getRegisteringStatus().compareTo("FIN") != 0
                    || !p.getParticipating()[processedRoundNumber]) {
                alNotPairablePlayers.add(p);
                hmPairablePlayers.remove(p.getKeyString());
            }
        }

        for (Game g : alActualGames) {
            Player wP = g.getWhitePlayer();
            Player bP = g.getBlackPlayer();
            if (wP != null) hmPairablePlayers.remove(wP.getKeyString());
            if (bP != null) hmPairablePlayers.remove(bP.getKeyString());
        }

        Player byeP = null;
        byeP = tournament.getByePlayer(processedRoundNumber);
        if (byeP != null) {
            hmPairablePlayers.remove(byeP.getKeyString());
        }
        ArrayList<Player> alPairablePlayers = new ArrayList<Player>(hmPairablePlayers.values());

        if (playersSortType == PlayerComparator.SCORE_ORDER){
            alPairablePlayers = this.scoreSortedPlayers(alPairablePlayers);
        }
        else{
            PlayerComparator playerComparator = new PlayerComparator(playersSortType);
            Collections.sort(alPairablePlayers, playerComparator);
        }
        this.pairablePlayersNumber = "" + alPairablePlayers.size();

        this.unPairablePlayersNumber = "" + alNotPairablePlayers.size();

        this.gamesNumber = "" + alActualGames.size();

        fillPlayersTable(alPairablePlayers, tblPairablePlayers);
        fillPlayersTable(alNotPairablePlayers, tblNotPairablePlayers);
        fillGamesTable(alActualGames, tblGames);

        // Bye player issues
        if (byePlayer == null) {
            byePlayerLabel = "No bye player";
            byePlayerBtnText = ">>>";

        } else {
            String strRk = Player.convertIntToKD(byePlayer.getRank());
            byePlayerLabel = byePlayer.fullName()
                    + " " + strRk + " " + byePlayer.getCountry() + " " + byePlayer.getClub();

            byePlayerBtnText = "<<<";

        }
        updateTableGames();
    }
    /**
     * From a non sorted alP array, returns a score sorted array
     * Sorting is made according to tournament ppas and processedRoundNumber
     * @param alP
     * @return 
     */
    private ArrayList<Player> scoreSortedPlayers(ArrayList<Player> alP){
        ArrayList<Player> alSSP = new ArrayList<Player>();
        PlacementParameterSet pps;
        ArrayList<ScoredPlayer> alSP = null;
        pps = tournament.getTournamentParameterSet().getPlacementParameterSet();
		alSP = tournament.orderedScoredPlayersList(processedRoundNumber - 1, pps);
        for(ScoredPlayer sp : alSP){
            for(Player p : alP){
                if (p.hasSameKeyString(sp)){
                    alSSP.add(p);
                }
            }
        }
        
        return alSSP;
    }

    private void updatePnlPreviousGames(ArrayList<Player> selectedPlayers) {
        tblPreviousGames.clear();

        ArrayList<Player> alPlayers = selectedPlayers;
        if (alPlayers.size() != 1) {
            System.out.println("Internal Error. At this point, exactly one player should be selected");
            return;
        }

        Player p = alPlayers.get(0);
        this.previousGamesLabel = "Previous games of " + p.getName() + "" + p.getFirstName();


        ArrayList<Game> alG = null;
        alG = tournament.gamesPlayedBy(p);

        for (int r = 0; r < this.processedRoundNumber; r++) {
            String strRound = "" + (r + 1);
            String strOpponent = "Not assigned";
            String strColor = "";
            String strHd = "";
            String strRes = "";
            boolean playerFound = false;
            if (!p.getParticipating()[r]) {
                strOpponent = "not participating";
                playerFound = true;
            }
            if (!playerFound) {
                Player byeP = null;
                byeP = tournament.getByePlayer(r);
                if (p.hasSameKeyString(byeP)) {
                    strOpponent = "Bye player";
                    playerFound = true;
                }
            }
            if (!playerFound) {
                for (Game g : alG) {
                    if (g.getRoundNumber() != r) {
                        continue;
                    }
                    if (g.getWhitePlayer().hasSameKeyString(p)) {
                        Player op = g.getBlackPlayer();
                        strOpponent = op.fullName();
                        strColor = "W";
                        strHd = "" + g.getHandicap();
                        int result = g.getResult();
                        if (result >= Game.RESULT_BYDEF) {
                            result -= Game.RESULT_BYDEF;
                        }
                        switch (result) {
                            case Game.RESULT_WHITEWINS:
                            case Game.RESULT_BOTHWIN:
                                strRes = "+";
                                break;
                            case Game.RESULT_BLACKWINS:
                            case Game.RESULT_BOTHLOSE:
                                strRes = "-";
                                break;
                            case Game.RESULT_EQUAL:
                                strRes = "=";
                                break;
                            default:
                                strRes = "?";
                        }
                        break;
                    } else if (g.getBlackPlayer().hasSameKeyString(p)) {
                        Player op = g.getWhitePlayer();
                        strOpponent = op.fullName();
                        strColor = "B";
                        strHd = "" + g.getHandicap();
                        switch (g.getResult()) {
                            case Game.RESULT_BLACKWINS:
                            case Game.RESULT_BOTHWIN:
                                strRes = "+";
                                break;
                            case Game.RESULT_WHITEWINS:
                            case Game.RESULT_BOTHLOSE:
                                strRes = "-";
                                break;
                            case Game.RESULT_EQUAL:
                                strRes = "=";
                                break;
                            default:
                                strRes = "?";
                        }
                        break;
                    }
                }
            }
            Vector<String> row = new Vector<String>();
            row.add(strRound);
            row.add(strOpponent);
            row.add(strColor);
            row.add(strHd);
            row.add(strRes);

            tblPreviousGames.add(row);
        }


    }

    private void fillGamesTable(ArrayList<Game> alG, List<Vector<String>> table) {
        ArrayList<Game> alDisplayedGames = new ArrayList<Game>(alG);

        GameComparator gameComparator = new GameComparator(gamesSortType);
        Collections.sort(alDisplayedGames, gameComparator);

        for (Game g : alDisplayedGames) {
            Vector<String> row = new Vector<String>();
            row.add("" + (g.getTableNumber() + 1));

            Player wP = g.getWhitePlayer();
            if(wP == null) continue;
            row.add(wP.fullName());

            Player bP = g.getBlackPlayer();
            if(bP == null) continue;
            row.add(bP.fullName());

            row.add("" + g.getHandicap());

            table.add(row);
        }
    }

    private void fillPlayersTable(ArrayList<Player> alP, List<Vector<String>> table) {
        ArrayList<Player> alDisplayedPlayers = new ArrayList<Player>(alP);
        ArrayList<ScoredPlayer> alOrderedScoredPlayers = null;
        TournamentParameterSet tps = null;
        tps = tournament.getTournamentParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        alOrderedScoredPlayers = tournament.orderedScoredPlayersList(processedRoundNumber - 1, tps.getPlacementParameterSet());

        // Prepare hmScoredPlayers
        HashMap<String, ScoredPlayer> hmScoredPlayers = new HashMap<String, ScoredPlayer>();
        if (alOrderedScoredPlayers != null) {
            for (ScoredPlayer sp : alOrderedScoredPlayers) {
                hmScoredPlayers.put(sp.getKeyString(), sp);
            }
        }

        int mainCrit = pps.mainCriterion();
        for (Player p : alDisplayedPlayers) {
            ScoredPlayer sp = hmScoredPlayers.get(p.getKeyString());
            Vector<String> row = new Vector<String>();
            row.add(p.fullName());

            row.add(Player.convertIntToKD(p.getRank()));

            int mainScore2 = sp.getCritValue(mainCrit, processedRoundNumber - 1);

            row.add(Player.convertIntScoreToString(mainScore2, 2));
            row.add(p.getCountry());
            row.add(p.getClub());
            table.add(row);
        }
    }

    /**
     * Produces a list of selected players in tblPairablePlayers
     * If no player is selected, returns the full list
     */
    private ArrayList<Player> selectedPlayersList() {
        ArrayList<Player> alSelectedPlayers = new ArrayList<Player>();

        for (int iRow = 0; iRow < tblPairablePlayers.size(); iRow++) {
            String name = tblPairablePlayers.get(iRow).get(NAME_COL);
            Player p;
            p = tournament.getPlayerByKeyString(name);
            alSelectedPlayers.add(p);
        }
        return alSelectedPlayers;
    }

    /**
     * Produces a list of selected games in tblGames
     * If no game is selected, returns all the games  
     */
    private ArrayList<Game> selectedGamesList(ArrayList<Integer> selectedTablesNumber) {
        ArrayList<Game> alSelectedGames = new ArrayList<Game>();
        for (Integer tableNumber : selectedTablesNumber) {
        	tableNumber--;
        	Game g = null;
            g = tournament.getGame(processedRoundNumber, tableNumber);
            alSelectedGames.add(g);
		}
        return alSelectedGames;
    }

    private void initComponents() {
        tblPairablePlayers = new ArrayList<>();
        tblNotPairablePlayers = new ArrayList<>();
        tblPreviousGames = new ArrayList<>();
        tblGames = new ArrayList<>();
    }

    private void btnPrintActionPerformed() {
        TournamentPrinting.printGamesList(tournament, processedRoundNumber);
    }

    private void modifyHandicapActionPerformed(Game g, int hd) {
        tournament.setGameHandicap(g, hd);
		this.tournamentChanged();
    }

    private void byePlayer(ArrayList<Player> selectedPlayers) {
        // If  no bye player exists, it is a request for choosing one 
		if (tournament.getByePlayer(processedRoundNumber) == null) {
		    ArrayList<Player> alP = selectedPlayers;
		    tournament.chooseAByePlayer(alP, processedRoundNumber);
		} // If a bye player exists, it is a request for removing it
		else {
		    tournament.unassignByePlayer(processedRoundNumber);
		}
		this.tournamentChanged();
    }

    /**
     * For Debug
     */
    private void renumberTablesActionPerformed(ArrayList<Integer> selectedTablesNumber) {
        tournament.renumberTablesByBestMMS(processedRoundNumber, this.selectedGamesList(selectedTablesNumber));
//            tournament.renumberTables(processedRoundNumber);
		this.tournamentChanged();
    }

    private void exchangeColorsActionPerformed(Game g) {
        tournament.exchangeGameColors(g);
		this.tournamentChanged();
    }

    public void unpair(ArrayList<Integer> selectedTablesNumber) {
//        boolean bRemoveAllGames = false;
        ArrayList<Game> alGamesToRemove = selectedGamesList(selectedTablesNumber);

        try {
            // And now, remove games from tournament
            for (Game g : alGamesToRemove) {
                tournament.removeGame(g);
            }
        } catch (TournamentException ex) {
            Logger.getLogger(GamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((tournament.getByePlayer(processedRoundNumber) != null) && (tournament.gamesList(processedRoundNumber).isEmpty())) {
		    tournament.unassignByePlayer(processedRoundNumber);
		}

        this.tournamentChanged();

    }

    public void pair(boolean chooseByePlayer, boolean keepPairingNevertheless) {
        ArrayList<Player> alPlayersToPair = selectedPlayersList();

        // Issue an error message if a player is in "PRE" status
        for (Player p : alPlayersToPair) {
            if (p.getRegisteringStatus().compareTo("FIN") != 0) {
            	informMessage("At least one player is not in a Final registering status"
                        + "\n" + "You should update registering status (Players .. Players Quick Check)");
                return;
            }
        }
        
        if (alPlayersToPair.size() % 2 != 0) {
            // if no possibility to choose a bye player, Error message
            Player bP = null;
            bP = tournament.getByePlayer(processedRoundNumber);
            if (bP != null) {
            	informMessage("Please, select an even number of players");
                return;
            } else { // Gotha may choose a bye player
                informMessage("Odd number of players.\nGotha will choose a bye player.");
                if (chooseByePlayer) {
                    return;
                } else {
                    tournament.chooseAByePlayer(alPlayersToPair, processedRoundNumber);
					// remove bye player from alPlayersToPair
					Player byeP = tournament.getByePlayer(processedRoundNumber);
					Player pToRemove = null;
					for (Player p : alPlayersToPair) {
					    if (p.hasSameKeyString(byeP)) {
					        pToRemove = p;
					    }
					}
					alPlayersToPair.remove(pToRemove);
                }
            }
        }

        ArrayList<Game> alNewGames = null;
        alNewGames = tournament.makeAutomaticPairing(alPlayersToPair, processedRoundNumber);


        // Check if there is a previously paired couple of players
        ArrayList<Game> alOldGames = null;
        alOldGames = tournament.gamesListBefore(processedRoundNumber);
        Game questionableGame = null;
        for (Game newG : alNewGames) {
            Player newWP = newG.getWhitePlayer();
            Player newBP = newG.getBlackPlayer();
            for (Game oldG : alOldGames) {
                Player oldWP = oldG.getWhitePlayer();
                Player oldBP = oldG.getBlackPlayer();
                if (oldWP.hasSameKeyString(newWP) && oldBP.hasSameKeyString(newBP)) {
                    questionableGame = oldG;
                    break;
                }
                if (oldWP.hasSameKeyString(newBP) && oldBP.hasSameKeyString(newWP)) {
                    questionableGame = oldG;
                    break;
                }
            }
        }
        if (questionableGame != null) {
            Player wP = questionableGame.getWhitePlayer();
            Player bP = questionableGame.getBlackPlayer();
            int r = questionableGame.getRoundNumber();

            informMessage(wP.fullName() + " " + "and"
                    + " " + bP.fullName()
                    + " " + "have been already paired in round " + (r + 1)
                    + "\n" + "Do you want to keep this pairing nevertheless ?");
            if (!keepPairingNevertheless) {
                return;
            }
        }

        // Give a table number
        int tN = 0;
        for (Game newG : alNewGames) {
            boolean bTNOK;  // Table number OK

            do {
                bTNOK = true;
                for (Game oldG : tournament.gamesList(processedRoundNumber)) {
				    if (oldG.getRoundNumber() != processedRoundNumber) {
				        continue;
				    }
				    if (oldG.getTableNumber() == tN) {
				        tN++;
				        bTNOK = false;
				    }
				}
            } while (!bTNOK);
            newG.setTableNumber(tN++);
        }

        tournament.renumberTablesByBestMMS(processedRoundNumber, alNewGames);


        for (Game g : alNewGames) {
            try {
                tournament.addGame(g);
            } catch (TournamentException ex) {
                Logger.getLogger(GamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.tournamentChanged();
    }

    private void help() {
        Gotha.displayGothaHelp("Games Pair frame");
}

    private String generateReport(boolean notShownUp, boolean mMSGreaterThan, boolean handicapGreaterThan, boolean intraClub, boolean intraCountry, boolean unbalancedMMSDUDDPlayers, boolean unbalancedWB, String mMSDiffThresholdStr, String handicapThresholdStr, String unbalancedWBStr) {
        String strReport = "";
        strReport += "Round " + (processedRoundNumber + 1) + "\n\n";

        if (notShownUp) {
            strReport += Pairing.notShownUpInPreviousRoundReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (mMSGreaterThan) {
            int mmsDiffThreshold = Integer.parseInt(mMSDiffThresholdStr);
            strReport += Pairing.mmsDiffGreaterThanReport(tournament, processedRoundNumber, mmsDiffThreshold) + "\n\n";
        }
        if (handicapGreaterThan) {
            int handicapThreshold = Integer.parseInt(handicapThresholdStr);
            strReport += Pairing.handicapGreaterThanReport(tournament, processedRoundNumber, handicapThreshold) + "\n\n";
        }
        if (intraClub) {
            strReport += Pairing.intraClubPairingReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (intraCountry) {
            strReport += Pairing.intraCountryPairingReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (unbalancedMMSDUDDPlayers) {
            strReport += Pairing.unbalancedMMSDUDDPlayersReport(tournament, processedRoundNumber) + "\n\n";
        }
        if (unbalancedWB) {
            int unbalancedWBThreshold = Integer.parseInt(unbalancedWBStr);
            strReport += Pairing.unbalancedWBPlayersReport(tournament, processedRoundNumber, unbalancedWBThreshold) + "\n\n";
        }

        return strReport;
    }

    private void cancelActionPerformed(int index2) {
        if (index2 >= 0) {
        	for(int i=0; i<=index2; i++) {
        		this.tblGames.remove(0);
        	}
        }
    }

    private void shiftTablesActionPerformed(ArrayList<Integer> selectedTablesNumber, int newBegTN) {
        ArrayList<Game> alSelectedGames = selectedGamesList(selectedTablesNumber);
        if (alSelectedGames.size() != 1) {
            informMessage("Please, select the starting table to shift");
            return;
        }

        Game g1 = alSelectedGames.get(0);
        // Ask for a new number
        int oldBegTN = g1.getTableNumber();
        String strOldBegTN = "" + (g1.getTableNumber() + 1);

        if (newBegTN <= oldBegTN) {
            informMessage("You can shift table numbers to higher numbers only");
            return;
        }

        ArrayList<Game> alGamesToConsider = null;
        alGamesToConsider = tournament.gamesList(processedRoundNumber);

        ArrayList<Game> alGamesToRemove = new ArrayList<Game>();
        ArrayList<Game> alGamesToAdd = new ArrayList<Game>();

        for (Game g : alGamesToConsider) {
            int oldCurrentTN = g.getTableNumber();
            if (oldCurrentTN < oldBegTN) {
                continue;
            }
            int newCurrentTN = oldCurrentTN + newBegTN - oldBegTN;
            Game newG = new Game(g.getRoundNumber(), newCurrentTN, g.getWhitePlayer(), g.getBlackPlayer(), g.isKnownColor(), g.getHandicap(), g.getResult());

            if (newCurrentTN >= Gotha.MAX_NUMBER_OF_TABLES) {
                informMessage("The table shift you ask for would lead to table numbers greater than "
                        + Gotha.MAX_NUMBER_OF_TABLES
                        + "\nThis is not possible");
                return;
            }

            alGamesToRemove.add(g);
            alGamesToAdd.add(newG);
        }

        for (Game g : alGamesToRemove) {
            try {
                tournament.removeGame(g);
            } catch (TournamentException ex) {
                Logger.getLogger(GamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (Game g : alGamesToAdd) {
            try {
                tournament.addGame(g);
            } catch (TournamentException ex) {
                Logger.getLogger(GamesPair.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.tournamentChanged();
}

    private void changeTableNumberActionPerformed(ArrayList<Integer> selectedTablesNumber, int newTN) {
        ArrayList<Game> alSelectedGames = selectedGamesList(selectedTablesNumber);
        if (alSelectedGames.isEmpty() || alSelectedGames.size() >= 2) {
            informMessage("Please, select one game");
            return;
        }

        Game g1 = alSelectedGames.get(0);
        // Ask for a new number
        int oldTN = g1.getTableNumber();
        if (newTN < 0 || newTN >= Gotha.MAX_NUMBER_OF_TABLES) {
            informMessage("Table number should be a number between 1 and " + Gotha.MAX_NUMBER_OF_TABLES);
            return;
        }
        if (newTN == oldTN) {
            return;
        }

        // If there already a game with tn as table number ?
        Game g2 = null;
        ArrayList<Game> alGames = tournament.gamesList(this.processedRoundNumber);
		for (Game g : alGames) {
		    if (g.getTableNumber() == newTN) {
		        g2 = g;
		    }
		}
        try {
            tournament.removeGame(g1);
            g1.setTableNumber(newTN);
            if (g2 != null) {
                tournament.removeGame(g2);
                g2.setTableNumber(oldTN);
                tournament.addGame(g2);
            }
            tournament.addGame(g1);

        } catch (TournamentException ex) {
            Logger.getLogger(GamesPair.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.tournamentChanged();
    }

    private void sortByRankActionPerformed() {
        playersSortType = PlayerComparator.RANK_ORDER;
        this.updateComponents();

    }

    private void cancel1ActionPerformed() {
    	for(int i=0; i<=tblPairablePlayers.size() - 1; i++) {
    		this.tblPairablePlayers.remove(0);
    	}
    }

    private void sortByNameActionPerformed() {
        playersSortType = PlayerComparator.NAME_ORDER;
        this.updateComponents();
    }

    private void sortByScoreActionPerformed() {
        playersSortType = PlayerComparator.SCORE_ORDER;
        this.updateComponents();
    }

    private void search(String searchPlayer, int startRow) {
        String strSearchPlayer = searchPlayer.toLowerCase();
        if (strSearchPlayer.length() == 0) {
            return;
        }
        int rowNumber = -1;
        int nbRows = tblGames.size();
        for (int iR = 0; iR < nbRows; iR++) {
            int row = (startRow + iR) % nbRows;
            String str = (String) tblGames.get(row).get(WHITE_PLAYER_COL);
            str = str.toLowerCase();
            if (str.indexOf(strSearchPlayer) >= 0) {
                rowNumber = row;
                break;
            }
            str = (String) tblGames.get(row).get(BLACK_PLAYER_COL);
            str = str.toLowerCase();
            if (str.indexOf(strSearchPlayer) >= 0) {
                rowNumber = row;
                break;
            }
        }

        if (rowNumber == -1) {
            informMessage("No player with the specified name is paired in round " + (this.processedRoundNumber + 1));
        } else {
        	sendSearchResult(rowNumber);
        }

        updateTableGames();
    }

    private void jButton1ActionPerformed() {
        TournamentPrinting.printResultSheets(tournament, processedRoundNumber);
    }

    private void tournamentChanged() {
        tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());

        updateAllViews();
    }

    private void updateAllViews() {
        if (!tournament.isOpen()) return;
		this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();

        int nbRounds = Gotha.MAX_NUMBER_OF_ROUNDS;
        nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (this.processedRoundNumber >= nbRounds) {
            informMessage("The number of rounds has been modified."
                    + "\n" + "Current round will be consequently changed");
            this.processedRoundNumber = nbRounds - 1;
        }

        shouldDisplayPanelInternal();
        updateComponents();
    }

    private void demandedDisplayedRoundNumberHasChanged() {
    	int demandedRN = spnRoundNumber - 1;
        int numberOfRounds = 0;
        numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (demandedRN < 0 || demandedRN >= numberOfRounds) {
            spnRoundNumber = processedRoundNumber + 1;
            return;
        }
        if (demandedRN == processedRoundNumber) {
            return;
        }

        processedRoundNumber = demandedRN;
        updateAllViews();
    }

    public List<Vector<String>> getTblGames() {
        return tblGames;
    }

    public List<Vector<String>> getTblPairablePlayers() {
        return tblPairablePlayers;
    }

    private void informMessage(String message) {
    	if (pairListener != null) {
    		pairListener.onMessage(message);
    	}
    }

    private void updateTableGames() {
    	if (pairListener != null) {
    		pairListener.onTableGamesUpdated();
    	}
    }

    private void sendSearchResult(int row) {
    	if (pairListener != null) {
    		pairListener.onSearchResult(row);
    	}
    }
    
    private void shouldDisplayPanelInternal() {
    	if (pairListener != null) {
    		pairListener.onShouldDisplayPanelInternal();
    	}
    }

    public void setPairListener(OnPairListener pairListener) {
        this.pairListener = pairListener;
    }

    public void setSpnRoundNumber(int spnRoundNumber) {
        this.spnRoundNumber = spnRoundNumber;
        demandedDisplayedRoundNumberHasChanged();
    }
}
