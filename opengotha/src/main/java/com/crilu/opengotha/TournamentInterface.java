package com.crilu.opengotha;

import java.util.ArrayList;
import java.util.HashMap;

public interface TournamentInterface {
    
    TournamentParameterSet getTournamentParameterSet();
    void setTournamentParameterSet(TournamentParameterSet tournamentParameterSet);
    TeamTournamentParameterSet getTeamTournamentParameterSet() ;
    void setTeamTournamentParameterSet(TeamTournamentParameterSet teamTournamentParameterSet);

    Player[] getByePlayers();

    Player getByePlayer(int roundNumber);
    ArrayList<Player> alNotFINRegisteredPlayers();
    ArrayList<Player> alNotPairedPlayers(int roundNumber);
    ArrayList<Player> alNotParticipantPlayers(int roundNumber);

    ArrayList<Player> getPlayersWhoDidNotShowUp(int roundNumber);

    String getFullName();
    String getShortName();
    void setShortName(String shortName);

    int tournamentType();
    boolean isOpen();
    void close();
    boolean isHasBeenSavedOnce();
    void setHasBeenSavedOnce(boolean hasBeenSavedOnce);

    void adjustCategoryLimits();
    
    boolean addPlayer(Player p)  throws TournamentException;
    boolean fastAddPlayer(Player p)  throws TournamentException;
    boolean isPlayerImplied(Player p);
    boolean isPlayerImpliedInRound(Player p, int r);
    boolean removePlayer(Player p)  throws TournamentException;
    void removeAllPlayers() ;
    void modifyPlayer(Player p, Player modifiedPlayer )  throws TournamentException;
    Player getPlayerByKeyString(String keyString);
    Player getPlayerByObsoleteCanonicalName(String canonicalName);
    Player homonymPlayerOf(Player p) ;
    
    int numberOfPlayers();
    int numberOfPlayersStrongerOrEqualTo(int rank);
    int numberOfPlayersInCategory(int numCat, ArrayList<ScoredPlayer> alSP);
    ArrayList<Player> playersList();
    HashMap<String, Player> playersHashMap();

    int mms2(Player p, int roundNumber);

    boolean addGame(Game g)  throws TournamentException;
    boolean removeGame(Game g)  throws TournamentException;
    void removeAllGames() ;
    void exchangeGameColors(Game g);
    boolean setGameHandicap(Game g, int handicap);
    Game getGame(int roundNumber, int tableNumber) ;
    Game getGame(int roundNumber, Player player) ;
    
    Player opponent(Game g, Player p);
    int getWX2(Game g, Player p);

    ArrayList<Game> gamesList();
    ArrayList<Game> gamesList(int roundNumber);
    ArrayList<Game> gamesListBefore(int roundNumber);
    ArrayList<Game> gamesPlayedBy(Player p);
    ArrayList<Game> gamesPlayedBy(Player p1, Player p2);
    ArrayList<Game> duplicateGames();

    void updateNumberOfRoundsIfNecesary();

    ArrayList<Game> makeAutomaticPairing(ArrayList<Player> alPlayersToPair, int roundNumber);

    void setByePlayer(Player p, int roundNumber);
    void chooseAByePlayer(ArrayList<Player> alPlayers, int roundNumber);
    void assignByePlayer(Player p, int roundNumber);
    void unassignByePlayer(int roundNumber);
    
    void renumberTablesByBestMMS(int roundNumber, ArrayList<Game> alGamesToRenumber);
    void setResult(Game g, int result);
    void setRoundNumber(Game g, int rn);
    
    int presumablyCurrentRoundNumber();

    int getTeamSize();
    void setTeamSize(int teamSize);
    boolean addTeam(Team t);
    boolean removeTeam(Team t) ;
    void removeAllTeams() ;
    Team getTeamByName(String name);
    Team getTeamOfPlayer(Player player, int roundNumber);
    void setTeamMember(Team team, int roundNumber, int boardMember, Player player);
    void modifyTeamName(Team team, String newName);
    void unteamTeamMember(Team team, int roundNumber, int boardNumber);
    void unteamTeamMembers(Team team, int roundNumber);
    void unteamAllTeams(int roundNumber);
    void cleanTeams();
    void reorderTeamMembersByRating(Team team, int roundNumber);
    void reorderTeamMembersByRating(int roundNumber);
    void renumberTeamsByTotalRating();
    boolean isTeamComplete(Team team, int roundNumber);
    ArrayList<Game> incoherentTeamGames();
    Team opponentTeam(Team team, int roundNumber);
    int nbWX2Team(Team team, Team opponentTeam, int roundNumber);

    ArrayList<Player> playersList(Team team, int boardNumber);
    boolean[] membership(Player p, Team t, int boardNumber);
    
    int numberOfTeams();
    ArrayList<Team> teamsList();
    HashMap<String, Player> teamablePlayersHashMap(int roundNumber);

    ArrayList<Match> matchesList(int roundNumber);
    ArrayList<Match> matchesListUpTo(int roundNumber);
    Match getMatch(int roundNumber, int tableNumber);

    void pairTeams(Team team0, Team team1, int roundNumber);
//    void renumberMatchTablesByBestScore(int roundNumber, ArrayList<Match> alMatchesToRenumber) ;

    int findFirstAvailableTableNumber(int roundNumber);

    /** returns an ordered scored players list after roundNumber round, ordered according to tps */
    ArrayList<ScoredPlayer> orderedScoredPlayersList(
            int roundNumber, PlacementParameterSet pps);
    /** Fills  alSPlayers with pairing information ; group infos and DU DD infos */
    void fillPairingInfo(int roundNumber);

    ScoredTeamsSet getAnUpToDateScoredTeamsSet(TeamPlacementParameterSet tpps, int roundNumber);

    long getLastTournamentModificationTime();
    void setLastTournamentModificationTime(long lastTournamentModificationTime);
    long getCurrentTournamentTime();
    
    boolean isChangeSinceLastSave();
    void setChangeSinceLastSaveAsFalse();

    String egfClass();
    int egfAdjustedTime();
    
    boolean addClubsGroup(ClubsGroup cg);
    void removeClubsGroup(ClubsGroup cg);
    ClubsGroup getClubsGroupByName(String name);
    ArrayList<ClubsGroup> clubsGroupsList();
    void addClubToClubsGroup(String groupName, String clubName);
    void removeClubFromClubsGroup(String groupName, String clubName);
    boolean playersAreInCommonGroup(Player p1, Player p2);

    void setTournamentIdentity(String identity);
    String getTournamentIdentity();
}