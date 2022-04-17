package unsw.gloriaromanus.goal;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.gloriaromanus.Faction;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * GoalComposite implementing GoalComponent for the Composite strategy.
 */
public class GoalComposite implements GoalComponent, Serializable {

    private static final long serialVersionUID = -8529089883197991713L;
    private GoalLeaf goal;
    private List<GoalComponent> subgoals;

    /**
     * Creates a GoalComposite.
     * @param goal GoalLeaf
     */
    public GoalComposite(GoalLeaf goal) {
        this.goal = goal;
        this.subgoals = new ArrayList<>();
    }

    /**
     * Adds a subgoal.
     * @param goal GoalComponent 
     */
    public void addSubgoals(GoalComponent goal) {
        this.subgoals.add(goal);
    }

    /**
     * Implements GoalComponent method.
     * @return GoalLeaf
     */
    @Override
    public String getGoal() {
        return goal.getGoal();
    }

    /**
     * Returns list of subgoals
     * @return List<GoalComponent>
     */

    public List<GoalComponent> getSubgoals() {
        return subgoals;
    }

    /**
     * Returns true if "AND" goal and false otherwise
     * @return boolean
     */
    @Override
    public boolean isAndGoal() {
        return goal.isAndGoal();
    }

    /**
     * Returns true if "OR" goal and false otherwise
     * @return boolean
     */
    @Override
    public boolean isOrGoal() {
        return goal.isOrGoal();
    }

    /**
     * Returns true if goal has been achieved and false otherwise.
     * @return boolean
     */
    @Override
    public boolean hasAchievedGoal(Faction faction) {
        switch (goal.getGoal()) {
            case "AND":
                for (GoalComponent s : subgoals)
                    if (!s.hasAchievedGoal(faction)) return false;
                return true;
            case "OR":
                for (GoalComponent s : subgoals)
                    if (s.hasAchievedGoal(faction)) return true;
                return false;
            default:
                return goal.hasAchievedGoal(faction);
        }
    }

    /**
     * Converts GoalComposite into JSON format.
     * @return JSONObject
     */
    @Override
    public JSONObject toJSON() {
        JSONObject obj = goal.toJSON();

        JSONArray array = new JSONArray();
        for (GoalComponent g : subgoals) {
            array.put(g.toJSON());
        }

        obj.put("subgoals", array);
        return obj;
    }
}
