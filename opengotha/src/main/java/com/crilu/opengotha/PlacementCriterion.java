package com.crilu.opengotha;

public class PlacementCriterion implements java.io.Serializable{
    private static final long serialVersionUID = Gotha.GOTHA_DATA_VERSION;

    public int uid;
    public String shortName;
    public String longName;
    public String description;
    public int coef;        // coef used for internal computations. Usually -1, 1, 2 or 4
                            // used at display time for division before displaying

    public PlacementCriterion(int uid, String shortName, String longName, String description, int coef){
        this.uid = uid;
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.coef = coef;
    }

}
