/*
 * TournamentPrinting.java
 */
package com.crilu.opengotha;

import java.util.ArrayList;
import java.util.Collections;

/**
 * TournamentPrinting manages printing jobs.
 *
 * @author LV
 */
public class TournamentPrinting {
    // PL = Players List
    static final int PL_NUMBER_BEG = 0;
    static final int PL_NUMBER_LEN = 4;
    static final int PL_PINLIC_BEG = PL_NUMBER_BEG + PL_NUMBER_LEN + 1;
    static final int PL_PINLIC_LEN = 8;
    static final int PL_NF_BEG = PL_PINLIC_BEG + PL_PINLIC_LEN + 1;
    static final int PL_NF_LEN = 25;
    static final int PL_COUNTRY_BEG = PL_NF_BEG + PL_NF_LEN + 1;
    static final int PL_COUNTRY_LEN = 2;
    static final int PL_CLUB_BEG = PL_COUNTRY_BEG + PL_COUNTRY_LEN + 1;
    static final int PL_CLUB_LEN = 4;
    static final int PL_RANK_BEG = PL_CLUB_BEG + PL_CLUB_LEN + 1;
    static final int PL_RANK_LEN = 3;
    static final int PL_GRADE_BEG = PL_RANK_BEG;
    static final int PL_GRADE_LEN = PL_RANK_LEN;
    static final int PL_RT_BEG = PL_RANK_BEG + PL_RANK_LEN + 1;
    static final int PL_RT_LEN = 4;
    static final int PL_MM_BEG = PL_RT_BEG + PL_RT_LEN + 1;
    static final int PL_MM_LEN = 3;
    static final int PL_PART_BEG = PL_MM_BEG + PL_MM_LEN + 1;
    static final int PL_PART_LEN = Gotha.MAX_NUMBER_OF_ROUNDS;
    static final int PL_PADDING = 0;
    static final int PL_NBCAR = PL_PART_BEG + PL_PART_LEN + PL_PADDING;
    // TL = Teams List
    static final int TL_NUMBER_BEG = 0;
    static final int TL_NUMBER_LEN = 4;
    static final int TL_TEAMNAME_BEG = TL_NUMBER_BEG + TL_NUMBER_LEN + 1;
    static final int TL_TEAMNAME_LEN = 20;
    static final int TL_BOARD_BEG = TL_TEAMNAME_BEG + TL_TEAMNAME_LEN + 1;
    static final int TL_BOARD_LEN = 2;
    static final int TL_NF_BEG = TL_BOARD_BEG + TL_BOARD_LEN + 1;
    static final int TL_NF_LEN = 20;
    static final int TL_COUNTRY_BEG = TL_NF_BEG + TL_NF_LEN + 1;
    static final int TL_COUNTRY_LEN = 2;
    static final int TL_CLUB_BEG = TL_COUNTRY_BEG + TL_COUNTRY_LEN + 1;
    static final int TL_CLUB_LEN = 4;
    static final int TL_RATING_BEG = TL_CLUB_BEG + TL_CLUB_LEN + 1;
    static final int TL_RATING_LEN = 4;
    static final int TL_MEMBER_BEG = TL_RATING_BEG + TL_RATING_LEN + 1;
    static final int TL_MEMBER_LEN = Gotha.MAX_NUMBER_OF_ROUNDS;
    static final int TL_PADDING = 0;
    static final int TL_NBCAR = TL_MEMBER_BEG + TL_MEMBER_LEN + TL_PADDING; //  
    // GL = Games List
    static final int GL_TN_BEG = 0; // Table Number
    static final int GL_TN_LEN = 4;
    static final int GL_WNF_BEG = GL_TN_BEG + GL_TN_LEN + 1;
    static final int GL_WNF_LEN = 33;       // 22 + 1 + 2 + 3 + 1 + 4
    static final int GL_BNF_BEG = GL_WNF_BEG + GL_WNF_LEN + 1;
    static final int GL_BNF_LEN = 33;
    static final int GL_HD_BEG = GL_BNF_BEG + GL_BNF_LEN + 1;
    static final int GL_HD_LEN = 1;
    static final int GL_RES_BEG = GL_HD_BEG + GL_HD_LEN + 1;
    static final int GL_RES_LEN = 3;
    static final int GL_PADDING = 2;
    static final int GL_NBCAR = GL_RES_BEG + GL_RES_LEN + GL_PADDING;
    // RS = Result sheets
    static final int RS_RSBYPAGE = 2; // Actual number of result sheets by page   
//    static final int RS_RS_HEIGHT = 200; // Result sheet height. virtual units. Calibrated for a page of 800 virtual units
    static final int RS_LINE_HEIGHT = 20; // Line height    
    static final int RS_TITLE1 = 10;
    static final int RS_TITLE2 = RS_TITLE1 + RS_LINE_HEIGHT / 2;
    static final int RS_TABLE = RS_TITLE2 + RS_LINE_HEIGHT * 3 / 2;
    static final int RS_COLOR = RS_TABLE + RS_LINE_HEIGHT * 3 / 2;
    static final int RS_PLAYERNAME = RS_COLOR + RS_LINE_HEIGHT;
    static final int RS_ID = RS_PLAYERNAME + RS_LINE_HEIGHT;
    static final int RS_SIGN = RS_ID + RS_LINE_HEIGHT;
    static final int RS_RS_HEIGHT = RS_SIGN + RS_LINE_HEIGHT * 3;
    static final int RS_PAGE_VIRTUAL_HEIGHT = RS_RSBYPAGE * RS_RS_HEIGHT;
    
    static final int RS_PAGE_VIRTUAL_WIDTH = 600;
    static final int RS_COL1 = 20;
    static final int RS_COL2 = 265;
    static final int RS_COL3 = 335;
    static final int RS_COL4 = 580;
    
    static final int RS_LEFTMARGIN = 10;
    
    // Non-playing list
    static final int NPL_REASON_BEG = 0;
    static final int NPL_REASON_LEN = 20;
    static final int NPL_NF_BEG = NPL_REASON_BEG + NPL_REASON_LEN + 1;
    static final int NPL_NF_LEN = 25;
    static final int NPL_RANK_BEG = NPL_NF_BEG + NPL_NF_LEN + 1;
    static final int NPL_RANK_LEN = 3;
    static final int NPL_GRADE_BEG = NPL_RANK_BEG;
    static final int NPL_GRADE_LEN = PL_RANK_LEN;
    static final int NPL_PADDING = 30;
    static final int NPL_NBCAR = NPL_RANK_BEG + NPL_RANK_LEN + NPL_PADDING; // 87
    // ML = Matches List
    static final int ML_TN_BEG = 1; // Table Number
    static final int ML_TN_LEN = 7;
    static final int ML_WTN_BEG = ML_TN_BEG + ML_TN_LEN + 1;
    static final int ML_WTN_LEN = 25;
    static final int ML_BTN_BEG = ML_WTN_BEG + ML_WTN_LEN + 1;
    static final int ML_BTN_LEN = 25;
    static final int ML_HD_BEG = ML_BTN_BEG + ML_BTN_LEN + 1;
    static final int ML_HD_LEN = 1;
    static final int ML_RES_BEG = ML_HD_BEG + ML_HD_LEN + 1;
    static final int ML_RES_LEN = 3;
    static final int ML_PADDING = 2;
    static final int ML_NBCAR = ML_RES_BEG + ML_RES_LEN + ML_PADDING;
    // ST = Standings // Dynamic system (V3.29.03)
    private int stNumBeg = 0;
    static final int ST_NUM_LEN = 4;
    private int stPlBeg;
    static final int ST_PL_LEN = 4;
    private int stNFBeg;
    static final int ST_NF_LEN = 22;
    private int stRkBeg;
    static final int ST_RK_LEN = 3;
    private int stGrBeg;
    static final int ST_GR_LEN = ST_RK_LEN;
    private int stCoBeg;
    static final int ST_CO_LEN = 2;
    private int stClBeg;
    static final int ST_CL_LEN = 4;
    private int stNbWBeg;
    static final int ST_NBW_LEN = 3;
    private int stRound0Beg;
    static final int ST_ROUND_LEN_FULL_FORM = 8;
    static final int ST_ROUND_LEN_SHORT_FORM = 5;
    private int stRoundLen = ST_ROUND_LEN_FULL_FORM;
    private int stCrit0Beg;
    static final int ST_CRIT_LEN = 6;
    static final int ST_PADDING = 1;
    // TST = Team Standings
    static final int TST_NUM_BEG = 0;
    static final int TST_NUM_LEN = 4;
    static final int TST_PL_BEG = TST_NUM_BEG + TST_NUM_LEN + 1;
    static final int TST_PL_LEN = 4;
    static final int TST_TN_BEG = TST_PL_BEG + TST_PL_LEN + 1;
    static final int TST_TN_LEN = 22;
    static final int TST_ROUND0_BEG = TST_TN_BEG + TST_TN_LEN + 1;
    static final int TST_ROUND_LEN = 8;
    static final int TST_CRIT_LEN = 6;
    static final int TST_PADDING = 1;
    static final int TST_NBFXCAR = TST_ROUND0_BEG + ST_PADDING;  // at runtime, numberOfCharactersInALine will be computed by adding round and crit infos
    // TP = Tournament Parameters
    static final int TP_TAB1 = 6;
    static final int TP_TAB2 = 12;
    static final int TP_TAB3 = 18;
    static final int TP_TAB4 = 24;
    static final int TP_NBCAR = 80;
    
    static final int WH_RATIO = 50;          // Width/Height ratio (%)
    static final int LINEFILLING_RATIO = 90; // Line filling ratio (%)
    static final int LHFS_RATIO = 140;       // Line Height/Font Size ratio (%)
    TournamentInterface tournament;
    private TournamentParameterSet tps; // this tps may differ with the tps in tournament. 
                                        // The reason is that some placement Criteria may differ (print Standings with temporary Criteria
    private TeamTournamentParameterSet ttps; 
    
    private int printType;
    private int printSubType;
    

    /**
     * from 0 to ...
     */
    private int roundNumber = -1;
    // For PlayersList and NotPlayingList
    ArrayList<Player> alPlayersToPrint;
    // For TeamsList
    ArrayList<Team> alTeamsToPrint;
    TeamMemberStrings[] arTMS;
    // For Standings
    private ArrayList<ScoredPlayer> alOrderedScoredPlayers;
    private String[][] halfGamesStrings;
    private int[] criteria;
    private String[] strPlace;
    // For TeamsStandings
    private ScoredTeamsSet scoredTeamsSet;
    private ArrayList<ScoredTeam> alOrderedScoredTeams;
    
    // These variables are computed by print method at first call
    // Upper_Left coordinates, width and height of the usable printing page area
    private int usableX = -1;
    private int usableY = -1;
    private int usableWidth = -1;
    private int usableHeight = -1;
    private int fontSize;
    private int lineHeight;
    private int numberOfBodyLinesInAPage;
    private int numberOfPages;
    private int numberOfCharactersInALine;
    
    // Matches List specificities
    private int matchesPerPage;

    private TournamentPrinting(TournamentInterface tournament) {
        this.tournament = tournament;
        this.tps = tournament.getTournamentParameterSet();
        startPrinterJob();
    }
    private TournamentPrinting(TournamentInterface tournament, TournamentParameterSet tps) {
        this.tournament = tournament;
        this.tps = tps;
        startPrinterJob();
    }
    private TournamentPrinting(TournamentInterface tournament, TournamentParameterSet tps, TeamTournamentParameterSet ttps) {
        this.tournament = tournament;
        this.tps = tps;
        this.ttps = ttps;
        startPrinterJob();
    }
    private void startPrinterJob() {}

    public static void printPlayersList(TournamentInterface tournament){
        int playersSortType = PlayerComparator.NAME_ORDER;
        playersSortType = tournament.getTournamentParameterSet().getDPParameterSet().getPlayerSortType();
        printPlayersList(tournament, playersSortType);
    }
    public static void printPlayersList(TournamentInterface tournament, int playersSortType){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(-1);
        tpr.makePrinting(TournamentPublishing.TYPE_PLAYERSLIST, playersSortType, true);        
    }
    public static void printTeamsList(TournamentInterface tournament){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(-1);
        tpr.makePrinting(TournamentPublishing.TYPE_TEAMSLIST, TournamentPublishing.SUBTYPE_DEFAULT, true);        
    }
    public static void printTournamentParameters(TournamentInterface tournament){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(-1);
        tpr.makePrinting(TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS, TournamentPublishing.SUBTYPE_DEFAULT, true);
    }
    public static void printGamesList(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_GAMESLIST, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    public static void printResultSheets(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_RESULTSHEETS, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    public static void printNotPlayingPlayersList(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_NOTPLAYINGLIST, PlayerComparator.NAME_ORDER, true);   
    }
    
    public static void printStandings(TournamentInterface tournament, int roundNumber){
        TournamentParameterSet tps = null;
        tps = tournament.getTournamentParameterSet();
        printStandings(tournament, tps, roundNumber);
    }
    public static void printStandings(TournamentInterface tournament, TournamentParameterSet tps, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament, tps);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_STANDINGS, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    public static void printMatchesList(TournamentInterface tournament, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_MATCHESLIST, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    public static void printTeamsStandings(TournamentInterface tournament, int roundNumber){
        TournamentParameterSet tps = null;
        TeamTournamentParameterSet ttps = null;
        tps = tournament.getTournamentParameterSet();
        ttps = tournament.getTeamTournamentParameterSet();
        printTeamsStandings(tournament, tps, ttps, roundNumber); 
    }
    public static void printTeamsStandings(TournamentInterface tournament, TournamentParameterSet tps,
            TeamTournamentParameterSet ttps, int roundNumber){
        TournamentPrinting tpr = new TournamentPrinting(tournament, tps, ttps);
        
        tpr.setRoundNumber(roundNumber);
        tpr.makePrinting(TournamentPublishing.TYPE_TEAMSSTANDINGS, TournamentPublishing.SUBTYPE_DEFAULT, true);   
    }
    
    /**
     * makePrinting 
     * 1) prepares the objects to be printed (if objects have to be prepared, sorted for example)
     * 2) manages the print Dialog to choose the printer (if askForPrionter == true)
     * 3) calls  printerJob.print()
     * 
     * @param printType
     * @param printSubType
     * @param askForPrinter 
     */
    private void makePrinting(int printType, int printSubType, boolean askForPrinter) {
        this.printType = printType;
        this.printSubType = printSubType;
        switch (printType) {
            case TournamentPublishing.TYPE_PLAYERSLIST:
                preparePrintPlayersList();
                break;
            case TournamentPublishing.TYPE_TEAMSLIST:
                preparePrintTeamsList();
                break;
            case TournamentPublishing.TYPE_TOURNAMENT_PARAMETERS:
                this.preparePrintTournamentParameters();
                break;
            case TournamentPublishing.TYPE_GAMESLIST:
                this.preparePrintGamesList();
                break;
            case TournamentPublishing.TYPE_RESULTSHEETS:
                this.preparePrintResultSheets();
                break;
            case TournamentPublishing.TYPE_NOTPLAYINGLIST:
                this.preparePrintNPPList();
                break;
            case TournamentPublishing.TYPE_STANDINGS:
                this.preparePrintStandings();
                break;
            case TournamentPublishing.TYPE_MATCHESLIST:
                this.preparePrintMatchesList();
                break;
            case TournamentPublishing.TYPE_TEAMSSTANDINGS:
                this.preparePrintTeamsStandings();
                break;
        }

    }
    
    private void preparePrintPlayersList(){
        int playersSortType = printSubType;
        alPlayersToPrint = new ArrayList<Player>(tournament.playersList());
        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(alPlayersToPrint, playerComparator);
    }
    
    private void preparePrintTeamsList(){
        arTMS = TeamMemberStrings.buildTeamMemberStrings(tournament);
    }
    
    private void preparePrintTournamentParameters(){
    }
    
    private void preparePrintGamesList(){
    }
    
    private void preparePrintResultSheets(){
    }
    
    
    
    private void preparePrintNPPList(){
        DPParameterSet dpps = tps.getDPParameterSet();
        
        PlayerComparator playerComparator = new PlayerComparator(PlayerComparator.NAME_ORDER);
        alPlayersToPrint = new ArrayList<Player>();
        // Bye player
        Player bP = tournament.getByePlayer(roundNumber);
        if (dpps.isShowByePlayer() && bP != null) {
            alPlayersToPrint.add(bP);
        }
        // Not paired players
        ArrayList<Player> alNotPairedPlayers = tournament.alNotPairedPlayers(roundNumber);
        if (dpps.isShowNotPairedPlayers() && alNotPairedPlayers != null) {
            Collections.sort(alNotPairedPlayers, playerComparator);
            alPlayersToPrint.addAll(alNotPairedPlayers);
        }
        // Not participating players
        ArrayList<Player> alNotParticipatingPlayers = tournament.alNotParticipantPlayers(roundNumber);
        if (dpps.isShowNotParticipatingPlayers() && alNotParticipatingPlayers != null) {
            Collections.sort(alNotParticipatingPlayers, playerComparator);
            alPlayersToPrint.addAll(alNotParticipatingPlayers);
        }
        // Not FIN Reg
        ArrayList<Player> alNotFINRegisteredPlayers = tournament.alNotFINRegisteredPlayers();
        if (alNotFINRegisteredPlayers != null) {
            Collections.sort(alNotFINRegisteredPlayers, playerComparator);
            alPlayersToPrint.addAll(alNotFINRegisteredPlayers);
        }
    }
    
    private void preparePrintStandings(){
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        int[] crit = this.tps.getPlacementParameterSet().getPlaCriteria();
        int nbCriteria = 0;
        for (int c = 0; c < crit.length; c++){
            if (crit[c] != PlacementParameterSet.PLA_CRIT_NUL) nbCriteria++;
        }
        this.criteria = PlacementParameterSet.purgeUselessCriteria(crit);
//        this.criteria = new int[nbCriteria];
//        int numCrit = 0;
//        for (int c = 0; c < crit.length; c++){
//            if (crit[c] != PlacementParameterSet.PLA_CRIT_NUL) criteria[numCrit++] = crit[c];
//        }
        
        // Do we print by category ?
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        if (pps.getPlaCriteria()[0] == PlacementParameterSet.PLA_CRIT_CAT && gps.getNumberOfCategories() > 1) {
            this.printSubType = TournamentPublishing.SUBTYPE_ST_CAT;
        } else {
             this.printSubType = TournamentPublishing.SUBTYPE_DEFAULT;
        }                                                 
 
        this.alOrderedScoredPlayers = tournament.orderedScoredPlayersList(roundNumber, pps);
        boolean bFull = true;
        if (dpps.getGameFormat() == DPParameterSet.DP_GAME_FORMAT_FULL) bFull = true;
        else bFull = false;
        
        this.halfGamesStrings = ScoredPlayer.halfGamesStrings(alOrderedScoredPlayers, roundNumber, tps, bFull);
    }

    private void preparePrintMatchesList(){
    }
    
    private void preparePrintTeamsStandings(){
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        DPParameterSet dpps = this.tps.getDPParameterSet();
        int[] crit = this.ttps.getTeamPlacementParameterSet().getPlaCriteria();
        int nbCriteria = 0;
        for (int c = 0; c < crit.length; c++){
            if (crit[c] != TeamPlacementParameterSet.TPL_CRIT_NUL) nbCriteria++;
        }
        this.criteria = PlacementParameterSet.purgeUselessCriteria(crit);
//        this.criteria = new int[nbCriteria];
//        int numCrit = 0;
//        for (int c = 0; c < crit.length; c++){
//            if (crit[c] != PlacementParameterSet.PLA_CRIT_NUL) criteria[numCrit++] = crit[c];
//        }
//        
        scoredTeamsSet = null;
        scoredTeamsSet = tournament.getAnUpToDateScoredTeamsSet(tpps, roundNumber);
        alOrderedScoredTeams = scoredTeamsSet.getOrderedScoredTeamsList();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
}