package unsw.gloriaromanus;

import org.json.JSONObject;

/**
 * Cavalry unit extends Battalion
 */
public class Cavalry extends Battalion  {

    private static final long serialVersionUID = 1L;
    private int charge;

    /**
     * Stores a cavalry unit
     * @param name String
     * @param type String
     * @param sale int
     */
    public Cavalry(String name, String type, int sale) {
        super(name, type, sale);
        super.setEngagement("Melee");
        try {
            JSONObject stats = super.getStats();
            this.charge = stats.getInt("charge");
        }
        catch (Exception e) {}
    }

    /**
     * Returns the charge statistic.
     * @return int
     *
    public int getChargeStat() {
        return (charge + super.getAttack());
    }*/

    @Override
    public JSONObject toJSON() {
        JSONObject obj = super.toJSON();
        obj.put("charge", charge);
        return obj;
    }

}
