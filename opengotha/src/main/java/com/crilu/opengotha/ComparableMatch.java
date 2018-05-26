
package com.crilu.opengotha;

import java.util.ArrayList;

/**
 *
 * @author Luc
 */
public class ComparableMatch{
    public int board0TableNumber;
    public ScoredTeam wst;
    public ScoredTeam bst;

    public static ComparableMatch buildComparableMatch(int board0TableNumber, ScoredTeam wst, ScoredTeam bst) {
        ComparableMatch cm = new ComparableMatch();
        cm.board0TableNumber = board0TableNumber;
        cm.wst = wst;
        cm.bst = bst;

        return cm;
    }

    /**
     *
     * @param alMatches
     * @param tournament
     * @param roundNumber
     * @return
     */
    public static ArrayList<ComparableMatch> buildComparableMatchesArray(ArrayList<Match> alMatches, TournamentInterface tournament, int roundNumber){
        final int DEFAULT_ROUND_NUMBER = 0;
        ArrayList<ComparableMatch> alCM = new ArrayList<ComparableMatch>();

        TeamPlacementParameterSet tpps = null;
        ScoredTeamsSet sts = null;
        tpps = tournament.getTeamTournamentParameterSet().getTeamPlacementParameterSet();
        sts = tournament.getAnUpToDateScoredTeamsSet(tpps, roundNumber);
        ArrayList<ScoredTeam> alOrderedScoredTeams = sts.getOrderedScoredTeamsList();

        for(Match m : alMatches){
            Team wt = m.getWhiteTeam();
            Team bt = m.getBlackTeam();
            Player player0 = wt.getTeamMember(DEFAULT_ROUND_NUMBER, 0);
            Game g = null;
            g = tournament.getGame(roundNumber, player0);
            int b0TN = - 1;
            if (g != null) b0TN = g.getTableNumber();
            int iwt = sts.findTeamOutOf(wt, alOrderedScoredTeams);
            int ibt = sts.findTeamOutOf(bt, alOrderedScoredTeams);
            ScoredTeam wst = alOrderedScoredTeams.get(iwt);
            ScoredTeam bst = alOrderedScoredTeams.get(ibt);

            ComparableMatch cm = null;
            cm = ComparableMatch.buildComparableMatch(b0TN, wst, bst);
            alCM.add(cm);
        }

        return alCM;
    }

}
