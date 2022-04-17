package unsw.gloriaromanus;

import org.json.JSONObject;

import unsw.gloriaromanus.observer.*;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of units within a province.
 */
public class Army implements Subject, Serializable {

    private static final long serialVersionUID = -6764158512096573286L;
    private Province province;
    private List<Battalion> battalions;
    private List<Observer> observers = new ArrayList<Observer>();

    /**
     * Creates an army.
     * @param province Province class
     * @param battalions List<Battalions> class
     */
    public Army(Province province, List<Battalion> battalions) {
        this.province = province;
        this.battalions = battalions;
    }

    /**
     * Creates an army.
     * @param name String
     * @param category String
     * @param type String
     * @param sale int
     * @param province Province
     */
    public Army(Province province) {
        this.battalions = new ArrayList<> ();
        this.province = province;
    }

    // Setters and Getters
    /**
     * Sets the province.
     * @param province Province
     */
    public void setProvince(Province province) {
        this.province = province;
    }

    /**
     * Returns a battalion from the list.
     * @param battalion String - Battalion name
     * @return Battalion
     */
    public Battalion getBattalion(String battalion) {
        for (Battalion b: battalions)
            if (b.getName().equals(battalion)) return b;
        return null;
    }

    /**
     * Returns the list of battalions.
     * @return List<Battalion>
     */
    public List<Battalion> getBattalions() {
        return battalions;
    }

    /**
     * Returns the strength of the army as calculated
     * by individual battalion strength.
     * @param year
     * @return int
     */
    public int getStrength(int year) {
        int strength = 0;
        for (Battalion b: battalions) {
            if (b.isReady(year)) strength += b.getStrength();
        }
        return strength;
    }

    /**
     * Returns the number of battalions in army.
     * @return
     */
    public int getSize() {
        return battalions.size();
    }

    /**
     * Returns the province where the army is based.
     * @return Province
     */
    public Province getProvince() {
        return province;
    }

    /**
     * Creates a new battalion and adds to the Army.
     * @param battalion String
     * @param category String
     * @param type String
     * @param sale int
     */
    public Battalion createBattalion(String battalion, String category, String type, int sale) {
        // TO DO: Factory pattern
        Battalion b = null;
        switch (category) {
            case "artillery":
                b = new Artillery(battalion, type, sale);
                break;
            case "cavalry":
                b = new Cavalry(battalion, type, sale);
                break;
            case "infantry":
                b = new Infantry(battalion, type, sale);
        }
        battalions.add(b);
        return b;
    }

    /**
     * Removes a random proportion of the army from 0% to 100%
     * @param proportion int
     */
    public void decimateArmy(int proportion) {
        int initial = battalions.size();
        while (battalions.size() > proportion * initial / 100) {
            battalions.remove(0);
        }
        notifyObservers();
    }

    /**
     * Checks if the Army contains a given battalion.
     * @param battalion String
     * @return true if the army contains the battalion and false otherwise.
     */
    public boolean hasBattalion(String battalion) {
        return getBattalion(battalion) != null;
    }

    /**
     * Converts the data to JSON format.
     * @return JSONObject
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("province", province.getName());
        JSONArray array = new JSONArray();
        for (Battalion b : battalions) {
            array.put(b.toJSON());
        }
        obj.put("battalions", array);
        return obj;
    }

    /**
     * Implementing attach function
     * for the subject interface.
     * @param o
     */
    @Override
    public void attach(Observer o) {
        if (!observers.contains(o)) observers.add(o);
    }

    /**
     * Implementing detach function
     * for the subject interface.
     */
    @Override
    public void detach(Observer o) {
        if (observers.contains(o)) observers.remove(o);
    }

    /**
     * Implementing the notifyObservers function
     * for the subject interface.
     */
    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(this);
        }
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
