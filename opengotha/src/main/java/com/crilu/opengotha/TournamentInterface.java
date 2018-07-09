package com.crilu.opengotha;

import java.util.ArrayList;
import java.util.HashMap;

public interface TournamentInterface {
    
    public TournamentParameterSet getTournamentParameterSet();
    public void setTournamentParameterSet(TournamentParameterSet tournamentParameterSet);
    public TeamTournamentParameterSet getTeamTournamentParameterSet() ;
    public void setTeamTournamentParameterSet(TeamTournamentParameterSet teamTournamentParameterSet);

    public Player[] getByePlayers();

    public Player getByePlayer(int roundNumber);
    public ArrayList<Player> alNotFINRegisteredPlayers();
    public ArrayList<Player> alNotPairedPlayers(int roundNumber);
    public ArrayList<Player> alNotParticipantPlayers(int roundNumber);

    public ArrayList<Player> getPlayersWhoDidNotShowUp(int roundNumber);

    public String getFullName();
    public String getShortName();
    public void setShortName(String shortName);

    public int tournamentType();
    public boolean isOpen();
    public void close();
    public boolean isHasBeenSavedOnce();
    public void setHasBeenSavedOnce(boolean hasBeenSavedOnce);

    public void adjustCategoryLimits();
    
    public boolean addPlayer(Player p)  throws TournamentException;
    public boolean fastAddPlayer(Player p)  throws TournamentException;
    public boolean isPlayerImplied(Player p);
    public boolean isPlayerImpliedInRound(Player p, int r);
    public boolean removePlayer(Player p)  throws TournamentException;
    public void removeAllPlayers() ;
    public void modifyPlayer(Player p, Player modifiedPlayer )  throws TournamentException;
    public Player getPlayerByKeyString(String keyString);
    public Player getPlayerByObsoleteCanonicalName(String canonicalName);
    public Player homonymPlayerOf(Player p) ;
    
    public int numberOfPlayers();
    public int numberOfPlayersStrongerOrEqualTo(int rank);
    public int numberOfPlayersInCategory(int numCat, ArrayList<ScoredPlayer> alSP);
    public ArrayList<Player> playersList();
    public HashMap<String, Player> playersHashMap();

    public int mms2(Player p, int roundNumber);

    public boolean addGame(Game g)  throws TournamentException;
    public boolean removeGame(Game g)  throws TournamentException;
    public void removeAllGames() ;
    public void exchangeGameColors(Game g);
    public boolean setGameHandicap(Game g, int handicap);
    public Game getGame(int roundNumber, int tableNumber) ;
    public Game getGame(int roundNumber, Player player) ;
    
    public Player opponent(Game g, Player p);
    public int getWX2(Game g, Player p);

    public ArrayList<Game> gamesList();
    public ArrayList<Game> gamesList(int roundNumber);
    public ArrayList<Game> gamesListBefore(int roundNumber);
    public ArrayList<Game> gamesPlayedBy(Player p);
    public ArrayList<Game> gamesPlayedBy(Player p1, Player p2);
    public ArrayList<Game> duplicateGames();

    public void updateNumberOfRoundsIfNecesary();

    public ArrayList<Game> makeAutomaticPairing(ArrayList<Player> alPlayersToPair, int roundNumber);

    public void setByePlayer(Player p, int roundNumber);
    public void chooseAByePlayer(ArrayList<Player> alPlayers, int roundNumber);
    public void assignByePlayer(Player p, int roundNumber);
    public void unassignByePlayer(int roundNumber);
    
    public void renumberTablesByBestMMS(int roundNumber, ArrayList<Game> alGamesToRenumber);
    public void setResult(Game g, int result);
    public void setRoundNumber(Game g, int rn);
    
    public int presumablyCurrentRoundNumber();

    public int getTeamSize();
    public void setTeamSize(int teamSize);
    public boolean addTeam(Team t);
    public boolean removeTeam(Team t) ;
    public void removeAllTeams() ;
    public Team getTeamByName(String name);
    public Team getTeamOfPlayer(Player player, int roundNumber);
    public void setTeamMember(Team team, int roundNumber, int boardMember, Player player);
    public void modifyTeamName(Team team, String newName);
    public void unteamTeamMember(Team team, int roundNumber, int boardNumber);
    public void unteamTeamMembers(Team team, int roundNumber);
    public void unteamAllTeams(int roundNumber);
    public void cleanTeams();
    public void reorderTeamMembersByRating(Team team, int roundNumber);
    public void reorderTeamMembersByRating(int roundNumber);
    public void renumberTeamsByTotalRating();
    public boolean isTeamComplete(Team team, int roundNumber);
    public ArrayList<Game> incoherentTeamGames();
    public Team opponentTeam(Team team, int roundNumber);
    public int nbWX2Team(Team team, Team opponentTeam, int roundNumber);

    public ArrayList<Player> playersList(Team team, int boardNumber);
    public boolean[] membership(Player p, Team t, int boardNumber);
    
    public int numberOfTeams();
    public ArrayList<Team> teamsList();
    public HashMap<String, Player> teamablePlayersHashMap(int roundNumber);

    public ArrayList<Match> matchesList(int roundNumber);
    public ArrayList<Match> matchesListUpTo(int roundNumber);
    public Match getMatch(int roundNumber, int tableNumber);

    public void pairTeams(Team team0, Team team1, int roundNumber);
//    public void renumberMatchTablesByBestScore(int roundNumber, ArrayList<Match> alMatchesToRenumber) ;

    public int findFirstAvailableTableNumber(int roundNumber);

    /** returns an ordered scored players list after roundNumber round, ordered according to tps */
    public ArrayList<ScoredPlayer> orderedScoredPlayersList(
            int roundNumber, PlacementParameterSet pps);
    /** Fills  alSPlayers with pairing information ; group infos and DU DD infos */
    public void fillPairingInfo(int roundNumber);

    public ScoredTeamsSet getAnUpToDateScoredTeamsSet(TeamPlacementParameterSet tpps, int roundNumber);

    public long getLastTournamentModificationTime();
    public void setLastTournamentModificationTime(long lastTournamentModificationTime);
    public long getCurrentTournamentTime();
    
    public boolean isChangeSinceLastSave();
    public void setChangeSinceLastSaveAsFalse();

    public String egfClass();
    public int egfAdjustedTime();
    
    public boolean addClubsGroup(ClubsGroup cg);
    public void removeClubsGroup(ClubsGroup cg);
    public ClubsGroup getClubsGroupByName(String name);
    public ArrayList<ClubsGroup> clubsGroupsList();
    public void addClubToClubsGroup(String groupName, String clubName);
    public void removeClubFromClubsGroup(String groupName, String clubName);
    public boolean playersAreInCommonGroup(Player p1, Player p2);

    public void setTournamentIdentity(String identity);
    public String getTournamentIdentity();
}