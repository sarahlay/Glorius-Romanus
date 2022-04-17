package unsw.gloriaromanus.goal;

import java.io.Serializable;

import org.json.JSONObject;

import unsw.gloriaromanus.Faction;

/**
 * Goal Leaf implementing GoalComponent for the Composite Strategy.
 */
public class GoalLeaf implements GoalComponent, Serializable {

    private static final long serialVersionUID = -1682841558554978124L;
    private String goal;

    /**
     * Creates a Goal Leaf
     * @param goal String
     */
    public GoalLeaf(String goal) {
        this.goal = goal;
    }

    /**
     * Returns a Goal String
     * @return String
     */
    @Override
    public String getGoal() {
        return goal;
    }

    /**
     * Returns true if goal is "AND" and false otherwise
     * @return boolean
     */
    @Override
    public boolean isAndGoal() {
        return goal.equals("AND");
    }

    /**
     * Returns true if goal is "OR" and false otherwise
     * @return boolean
     */
    @Override
    public boolean isOrGoal() {
        return goal.equals("OR");
    }

    /**
     * Returns true if goal has been achieved and false otherwise
     * @return boolean
     */
    @Override
    public boolean hasAchievedGoal(Faction faction) {
        switch (goal) {
            case "CONQUEST":
                return faction.countProvinces() >= 53;
            case "TREASURY":
                return faction.getTreasury() > 100000;
            case "WEALTH":
                return faction.getWealth() > 400000;
        }
        return false;
    }

    /**
     * Converts GoalLeaf to JSON format.
     * @return JSONObject
     */
    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("goal", goal);
        return obj;
    }
}
