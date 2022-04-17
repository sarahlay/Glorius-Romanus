package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.*;

public class UnitTest{
    /**
     * Testing a simple game.
     */
    @Test
    public void simpleTest(){
        Game game = new Game();
        Campaign campaign = game.startCampaign("Sarah", "Jo", "Procrastination");

        // Year 0 - Setting Tax rate & Collecting wealth
        assertEquals(campaign.getPlayer(), "Sarah");
        assertTrue(campaign.setTax("high"));
        //campaign.printProvinces();
        assertTrue(campaign.setTax("Macedonia", "veryHigh"));
        assertTrue(campaign.setTax("Sicilia", "veryHigh"));
        assertEquals(campaign.endTurn(), "success");

        assertEquals(campaign.getPlayer(), "Jo");
        campaign.setTax("normal");
        campaign.setTax("Asia", "veryHigh");
        campaign.setTax("Cyprus", "veryHigh");
        assertEquals(campaign.endTurn(), "success");

        // Year 2 - Sarah [Rome]
        // Army of Elephants in Macedonia
        assertTrue(campaign.recruitTroops("Macedonia", "JUPITER", "Elephants"));
        assertTrue(campaign.recruitTroops("Macedonia", "JUPITER", "Elephants"));

        // Training spots full -> ready in year 5
        assertFalse(campaign.recruitTroops("Macedonia", "JUPITER", "Elephants"));
        campaign.endTurn();

        // Year 3 - Jo [Gaul]
        assertTrue(campaign.recruitTroops("Achaia", "APOLLO", "Melee Cavalry"));
        assertTrue(campaign.recruitTroops("Achaia", "APOLLO", "Melee Cavalry"));

        // Training spots full -> ready in year 4
        assertFalse(campaign.recruitTroops("Achaia", "APOLLO", "Artillery"));
        campaign.endTurn();

        // Year 4 - Sarah [Rome]
        campaign.endTurn();

        // Year 5 - Jo [Gaul]
        assertEquals(campaign.invadeProvince("Achaia", "Macedonia"), "lose");
        campaign.endTurn();

        // Year 6 - Sarah [Rome]
        assertTrue(campaign.recruitTroops("Macedonia", "JUPITER", "Melee Cavalry"));
        assertTrue(campaign.recruitTroops("Macedonia", "JUPITER", "Melee Cavalry"));
        campaign.endTurn();
        campaign.endTurn();

        // Year 7 - Sarah [Rome]
        assertEquals(campaign.invadeProvince("Macedonia", "Achaia"), "win");

    }

    /**
     * Testing invalid instructions
     */
    @Test
    public void invalidTest() {
        Game game = new Game();
        Campaign campaign = game.startCampaign("Sarah", "Jo", "Procrastination");

        // Setting invalid tax
        assertFalse(campaign.setTax("invalidProvince", "high"));
        assertFalse(campaign.setTax("Macedonia", "invalidRate"));
        assertFalse(campaign.setTax("invalidRate"));

        // Set low tax
        assertTrue(campaign.setTax("low"));

        // Recruiting invalid troop
        assertFalse(campaign.recruitTroops("Macedonia", "INVALID", "invalidTroop"));

        // Invading with an invalid army
        assertEquals(campaign.invadeProvince("Achaia", "INVALID"), "invalid adjacency");

        // Invading with an invalid province
        campaign.endTurn(); // collecting wealth - Sarah
        campaign.endTurn(); // collecting wealth - Jo

        // Recruiting troops - Sarah
        assertTrue(campaign.recruitTroops("Macedonia", "JUPITER", "Artillery"));
        campaign.endTurn(); // training troops - Sarah
        campaign.endTurn();

        // Invading an invalid province
        assertEquals(campaign.invadeProvince("INVALID", "JUPITER"), "invalid destination");

        // Invading a non adjacent province
        assertEquals(campaign.invadeProvince("Asia", "JUPITER"), "invalid adjacency");

        // Moving to an invalid province
        assertFalse(campaign.moveTroops("JUPITER", "XI"));

    }

    /**
     * Testing provinces have been read correctly
     */
    @Test
    public void provinceTest() {
        Game game = new Game();
        Campaign campaign = game.startCampaign("Sarah", "Jo", "Procrastination");

        // Counting Provinces
        int provinces = 0;
        provinces += campaign.countProvinces();
        campaign.endTurn();
        provinces += campaign.countProvinces();
        assertEquals(provinces, 53);
    }

    /**
     * Testing movement of troops
     */
    @Test
    public void movementTest() {
        Game game = new Game();
        Campaign campaign = game.startCampaign("Sarah", "Jo", "Procrastination");

        // Default tax rates ("normal")
        // Collecting taxes in year 0 & 1
        campaign.endTurn();
        campaign.endTurn();

        assertTrue(campaign.recruitTroops("Lugdunensis", "JUPITER", "Melee Infantry"));
        assertTrue(campaign.recruitTroops("Lugdunensis", "JUPITER", "Melee Cavalry"));
        campaign.endTurn();

        assertTrue(campaign.recruitTroops("Asia", "APOLLO", "Artillery"));
        assertTrue(campaign.recruitTroops("Asia", "APOLLO", "Chariots"));
        campaign.endTurn();

        assertTrue(campaign.moveTroops("Lugdunensis", "Narbonensis"));
        assertEquals(campaign.getProvince("JUPITER"), "Narbonensis");
        campaign.endTurn();

        assertTrue(campaign.moveTroops("Asia", "Bithynia et Pontus"));
        assertEquals(campaign.getProvince("APOLLO"), "Bithynia et Pontus");
        campaign.endTurn();

        // Testing not adjacent not owned province
        assertFalse(campaign.moveTroops("Narbonensis", "Bithynia et Pontus"));

        // Testing adjacent, not owned province
        assertFalse(campaign.moveTroops("Narbonensis", "Alpes Cottiae"));

        // Testing not adjacent, owned province
        assertFalse(campaign.moveTroops("Narbonensis", "XI"));


    }


    /**
     * Testing conquer victory condition.
     */
    @Test
    public void treasuryGoalTest() {
        Game game = new Game();
        Campaign campaign = game.startCampaign("Sarah", "Jo", "TREASURY", "test");

        for(int i = 0; i < 20; i++) {
            campaign.endTurn();
        }
        assertEquals(campaign.endTurn(), "win");
        assertTrue(campaign.getTreasury() > 100000);
    }

    @Test
    public void saveGameTest() {
        Game game = new Game();
        Campaign campaign = game.startCampaign("Sarah", "Jo", "Procrastination");

        game.saveCampaign();
        game.leaveCampaign();
        assertEquals(game.getCampaign(), null);

        game.loadCampaign("Procrastination");
        //assertEquals(game.getCampaign(), campaign);
        campaign = game.getCampaign();

        assertEquals(campaign.getPlayer(), "Sarah");
        campaign.endTurn();
        assertEquals(campaign.getPlayer(), "Jo");

    }

}

