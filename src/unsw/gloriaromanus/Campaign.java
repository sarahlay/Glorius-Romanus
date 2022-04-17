package unsw.gloriaromanus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Stores a campaign.
 */
public class Campaign implements Serializable {

    private static final long serialVersionUID = 4927636259609181309L;
    private Goal goal;
    private int year;
    private Faction player;
    private List<Faction> factions;
    private boolean isVictorious;

    /**
     * Creates new campaign with a randomly decided goal.
     * @param playerA String - will play as Rome
     * @param playerB String - will play as Gaul
     */
    public Campaign(String playerA, String playerB) {
        this.goal = new Goal();
        this.year = 0;
        this.factions = new ArrayList<> ();
        factions.add(new Faction(playerA, "Rome"));
        factions.add(new Faction(playerB, "Gaul"));
        this.player = factions.get(0);
        this.isVictorious = false;
    }

    /**
     * Creates new campaign with a predecided goal.
     * @param playerA String - will play as Rome
     * @param playerB String - will play as Gaul
     * @param goal String
     */
    public Campaign(String playerA, String playerB, String goal) {
        this.goal = new Goal(goal);
        this.year = 0;
        this.factions = new ArrayList<> ();
        factions.add(new Faction(playerA, "Rome"));
        factions.add(new Faction(playerB, "Gaul"));
        this.player = factions.get(0);
    }

    /**
     * Returns the Goal
     * @return Goal
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Returns current year
     * @return int
     */
    public int getYear() {
        return year;
    }

    /**
     * Returns the current player name
     * @return String
     */
    public String getPlayer() {
        return player.getName();
    }

    public Faction getObj() {
        return player;
    }

    /**
     * Returns the total wealth of a faction's provinces
     * @return int
     */
    public int getWealth() {
        return player.getWealth();
    }

    /**
     * Returns a list of provinces for the current player
     * @return List<Province>
     */
    public List<Province> getProvinces() {
        return player.getProvinces();
    }

    /**
     * Returns a list of provinces for the enemy player
     * @return List<Province>
     */
    public List<Province> getEnemyProvinces() {
        return factions.get((year + 1) % 2).getProvinces();
    }

    /**
     * Returns name of enemy faction
     * @return String
     */
    public String getEnemyFaction() {
        return factions.get((year + 1) % 2).getFaction();
    }

    /**
     * Sets all tax to given rate
     * Options:
     *  - "low" 
     *  - "normal" 
     *  - "high" 
     *  - "veryHigh"
     */
    public boolean setTax(String rate) {
        return player.setTax(rate);
    }

    /**
     * Sets tax rate of specific province
     * @param province String
     * @param rate String
     */
    public boolean setTax(String province, String rate) {
        return player.setTax(province, rate);
    }

    /**
     * Sets the tax rate of both factions
     * @param taxA
     * @param taxB
     */
    public void setAllTax(String taxA, String taxB) {
        factions.get(0).setTax(taxA);
        factions.get(1).setTax(taxB);
    }

    /**
     * Gets the province of a given battalion.
     * @param battalion String
     * @return String province
     */
    public String getProvince(String battalion) {
        return player.getArmy(battalion).getProvince().getName();
    }

    /**
     * Returns name of current players faction
     * @return String
     */
    public String getFaction() {
        return player.getFaction();
    }

    /**
     * Returns a list of all owned battalions
     * @return List<Battalion>
     */
    public List<Battalion> getBattalions(String province) {
        return player.getBattalions(province);
    }


    /**
     * Returns the treasury amount of the current player/faction.
     * @return int
     */
    public int getTreasury() {
        return player.getTreasury();
    }

    /**
     * Returns the total number of provinces in the faction.
     * @return int
     */
    public int countProvinces() {
        return player.countProvinces();
    }

    /**
     * Ends a player's turn,
     * checks victory condition,
     * increases year, and
     * collects tax.
     * @return
     * String "win" for winning the game or "success" for a successful endTurn attempt.
     */
    public String endTurn() {
        this.year++;
        player = factions.get(year % 2);
        player.collectTax();

        if (hasAchievedVictory(player)) {
            isVictorious = true;
            return "win";
        }

        return "success";
    }

    /**
     * Moves the troops from current location to a given destination.
     * @param battalion String
     * @param destination String
     * @return boolean
     */
    public boolean moveTroops(String origin, String destination) {
        return (player.moveTroops(origin, destination));
    }

    /**
     * Recruit a new battalion into the province.
     * @param province String
     * @param battalion String
     * @param type String
     * @return boolean - true for success and false for fail.
     */
    public boolean recruitTroops(String province, String battalion, String type) {
        return player.recruitTroops(province, battalion, type, year);
    }

    /**
     * Checks if the victory condition has been fulfilled for the given faction.
     * @param faction FAction
     * @return boolean
     */
    public boolean hasAchievedVictory(Faction faction) {
        return goal.hasAchievedVictory(faction);
    }

    /**
     * Invade a given destination province with the given army
     * @param destination String
     * @param army String
     * @return String - "invalid", "win" or "lose"
     */
    public String invadeProvince(String destination, String base) {
        Province origin = player.getProvince(base);

        // Finding enemy
        Faction enemy = factions.get((year + 1) % 2);

        // Finding province
        Province battlefield = enemy.getProvince(destination);
        if (battlefield == null) {
            return "invalid destination";
        }
        if (!battlefield.isAdjacent(base)) {
            return "invalid adjacency";
        }

        // Obtain armies
        Army invader = origin.joinForces();
        Army defender = battlefield.joinForces();

        String result;
        if (invader == null || invader.getStrength(year) == 0) {
            return "no troops available";
        } else if (defender == null) {
            result = "win"; 
        } else {
            result = battle(invader, defender);
        }

        switch(result) {
            case "win":
                // change province ownership
                enemy.removeProvince(battlefield);
                player.addProvince(battlefield);
                battlefield.setFaction(player);

                // change army location
                origin.removeArmy(invader);
                battlefield.addArmy(invader);
                invader.setProvince(battlefield);

                // remove proportion of winning army
                int proportion = (defender.getStrength(year) * 100) / (invader.getStrength(year) + defender.getStrength(year));
                invader.decimateArmy(proportion);

                // destroy losing army
                battlefield.removeArmy(defender);
                return "win";
            case "lose":
                // remove attackers from existence
                player.removeArmy(invader);
                battlefield.removeArmy(invader);

                // remove proportion of winning army
                proportion = (invader.getStrength(year))/(invader.getStrength(year) + defender.getStrength(year));
                defender.decimateArmy(proportion);
        }
        return "lose";
    }

    /**
     * Battle resolver, calculated by the strength of the armies.
     * Strength = number of units per battalion * attack * defense
     * @param invader Army
     * @param defender Army
     * @return String - "win" or "lose"
     */
    public String battle(Army invader, Army defender) {
        if (invader.getStrength(year) > defender.getStrength(year)) {
            return "win";
        } else {
            return "lose";
        }
    }

    /**
     * Converts the campaign object to JSON.
     * @return JSONObject
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("goal", goal.toJSON());
        obj.put("year", year);

        JSONArray array = new JSONArray();
        for (Faction f : factions) {
            array.put(f.toJSON());
        }
        obj.put("factions", array);

        return obj;
    }

    public boolean hasWon() {
        return isVictorious;
    }
}