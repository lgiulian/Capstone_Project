/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crilu.opengotha;

/**
 *
 * @author Luc
 */
public class TestCharSet {
     /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        String strFrench = "Aéèçàù";
        String sF = Gotha.forceToASCII(strFrench);
        String strTurc = "UIĞÜŞİÖÇ lığüşiöç";
        String sT = Gotha.forceToASCII(strTurc);
    }
}
