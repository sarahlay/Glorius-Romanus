package unsw.gloriaromanus;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Battalion - superclass for all battalion categories.
 */
public abstract class Battalion implements Serializable {

    private static final long serialVersionUID = -3417151750032224592L;
    private String name;
    private String type;
    private String engagement;
    private int troops;
    private int attack;
    private int shield;
    private int armour;
    private int ready;

    /**
     * Creates a battalion and obtains statistics from JSON files.
     * @param name String
     * @param type String
     * @param sale int
     */
    public Battalion(String name, String type, int sale) {
        this.name = name;
        this.type = type;
        try {
            JSONObject stats = getStats();
            this.troops = stats.getInt("troops");
            this.attack = stats.getInt("attack");
            this.shield = stats.getInt("shield");
            this.armour = stats.getInt("armour");
            this.ready = sale + stats.getInt("time");
        }
        catch (Exception e) {}
    }

    // Setters and Getters
    /**
     * sets the Engagement type: "Ranged" or "Melee"
     * @param engagement String
     */
    public void setEngagement(String engagement) {
        this.engagement = engagement;
    }

    /**
     * Returns the Battalion name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns Battalion type
     * @return String
     */
    public String getType() {
        return type;
    }


    /**
     * Returns the Battalion's strength
     * @return int
     */
    public int getStrength() {
        return troops * attack * (shield + armour);
    }

    /**
     * Returns true if the battalion has finished training and false otherwise
     * @param current int
     * @return boolean
     */
    public boolean isReady(int current) {
        return (ready <= current);
    }

    /**
     * Obtains the battalion statistics from the JSON file
     * @return JSONObject
     * @throws IOException
     */
    public JSONObject getStats() throws IOException {
        String file = Files.readString(Paths.get("src/unsw/gloriaromanus/BattalionStats.json"));
        JSONObject obj = new JSONObject(file);
        return obj.getJSONObject(type);
    }

    /**
     * Converts the data to JSON format.
     * @return JSONObject.
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("battalion", name);
        obj.put("type", type);
        obj.put("engagement", engagement);
        obj.put("troops", troops);
        obj.put("attack", attack);
        obj.put("shield", shield);
        obj.put("armour", armour);
        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
