package unsw.gloriaromanus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import unsw.gloriaromanus.goal.*;

/**
 * Stores a Goal and calls the Composite pattern.
 */
public class Goal implements Serializable {

    private static final long serialVersionUID = 3663299224766220529L;
    private GoalComponent victoryCondition;
    private List<String> availableGoals;
    private int operations;

    /**
     * Creates a new ramdomised goal.
     */
    public Goal() {
        this.operations = 0;
        setAvailableGoals();
        setVictoryCondition();
    }

    /**
     * Creates a new given goal
     * @param goal String;
     */
    public Goal(String goal) {
        this.victoryCondition = new GoalLeaf(goal);
    }

    /**
     * Returns the victory condition.
     * @return GoalComponent
     */
    public GoalComponent getVictoryCondition() {
        return victoryCondition;
    }

    /**
     * Set the available goals.
     */
    private void setAvailableGoals() {
        availableGoals = new ArrayList<>();
        availableGoals.add("AND");
        availableGoals.add("OR");
        availableGoals.add("CONQUEST");
        availableGoals.add("TREASURY");
        availableGoals.add("WEALTH");
    }

    /**
     * Setting the victory condition.
     */
    private void setVictoryCondition() {
        victoryCondition = generateCondition();
    }

    /**
     * Generates a victory condition recursively
     * @return
     */
    private GoalComponent generateCondition() {
        if (operations == 2) {
            availableGoals.remove("AND");
            availableGoals.remove("OR");
        }
        GoalLeaf goal = pickGoal();

        if (goal.isAndGoal() || goal.isOrGoal()) {
            operations++;
            GoalComposite composite = new GoalComposite(goal);
            composite.addSubgoals(generateCondition());
            composite.addSubgoals(generateCondition());
            return composite;
        }

        availableGoals.remove(goal.getGoal());
        return goal;
    }

    /**
     * Picks a random goal from the availableGoals list.
     * @return GoalLeaf
     */
    private GoalLeaf pickGoal() {
        Random generator = new Random();
        int index = generator.nextInt(availableGoals.size());
        return new GoalLeaf(availableGoals.get(index));
    }

    /**
     * Converts the Goals data to JSON format
     * @return JSONObject
     */
    public JSONObject toJSON() {
        return victoryCondition.toJSON();
    }

    /**
     * Returns Goal as a string
     * @return String
     */
    public String toString() {
        return toJSON().toString();
    }

    /**
     * Returns true if the faction has achieved the victory condition and false otherwise.
     * @param faction Faction
     * @return boolean
     */
    public boolean hasAchievedVictory(Faction faction) {
        return victoryCondition.hasAchievedGoal(faction);
    }
}