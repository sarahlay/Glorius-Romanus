package unsw.gloriaromanus.goal;

import org.json.JSONObject;

import unsw.gloriaromanus.Faction;

/**
 * GoalComponent - Interface for composite strategy.
 */
public interface GoalComponent {
    String getGoal();
    boolean isAndGoal();
    boolean isOrGoal();
    boolean hasAchievedGoal(Faction faction);
    JSONObject toJSON();
}