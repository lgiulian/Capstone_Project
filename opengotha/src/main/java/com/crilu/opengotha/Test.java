package com.crilu.opengotha;

import java.util.Date;

import com.crilu.opengotha.model.GothaModel;

public class Test {

	public static void main(String[] args) {
		GothaModel gotha = new GothaModel();
		gotha.startTournament("name", "shortName", "location", "director",
                new Date(), new Date(), 5, 1, TournamentParameterSet.TYPE_MCMAHON);

	}

}
