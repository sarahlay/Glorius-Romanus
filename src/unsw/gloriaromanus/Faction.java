package unsw.gloriaromanus;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Stores the faction.
 */
public class Faction implements Serializable {

    private static final long serialVersionUID = 4007771450402042661L;
    private String player;
    private String name;
    private int treasury;
    private List<Province> provinces;

    /**
     * Creates a faction.
     * @param player String
     * @param name String
     */
    public Faction(String player, String name) {
        this.player = player;
        this.name = name;
        this.treasury = 0;
        this.provinces = new ArrayList<>();
        try {
            setProvinces();
        } catch (Exception e) {
            System.out.println("Province error");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Returns a list of provinces.
     * @return List<Province>
     */
    public List<Province> getProvinces() {
        return provinces;
    }

    /**
     * Returns name of the player
     * @return String
     */
    public String getName() {
        return player;
    }

    /**
     * Returns name of the faction
     * @return
     */
    public String getFaction() {
        return name;
    }

    /**
     * Returns the treasury of the faction.
     * @return int
     */
    public int getTreasury() {
        return treasury;
    }


    /**
     * Returns wealth of the faction,
     * calculated by the sum of wealth in each province.
     * @return int
     */
    public int getWealth() {
        int wealth = 0;
        for (Province p : provinces)
            wealth += p.getWealth();
        return wealth;
    }

    /**
     * Returns the province given a name
     * @param province String
     * @return Province
     */
    public Province getProvince(String province) {
        for (Province p : provinces) {
            if (p.getName().equals(province)) {
                return p;
            }
        }
        return null;
    }

    public void printProvinces() {
        for (Province p : provinces)
            System.out.println(p.getName());
    }

    /**
     * Returns an Army class given a string.
     * @param army Army
     * @return String
     */
    public Army getArmy(String army) {
        for (Province p : provinces) {
            Army a = p.getArmy(army);
            if (a == null) continue;
            return a;
        }
        return null;
    }

    /**
     * Sets all tax rates
     * @param tax
     */
    public boolean setTax(String tax) {
        if (!(tax.equals("low") || tax.equals("normal") ||
            tax.equals("high") || tax.equals("veryHigh")))
            return false;
        for (Province p : provinces) {
            p.setTaxRate(tax);
        }
        return true;
    }

    /**
     * Sets the tax rate of a specific province
     * @param province
     * @param tax
     */
    public boolean setTax(String province, String tax) {
        if (!(tax.equals("low") || tax.equals("normal") ||
            tax.equals("high") || tax.equals("veryHigh")))
            return false;
        Province p = getProvince(province);
        if (p != null) {
            p.setTaxRate(tax);
            return true;
        }
        return false;
    }

    /**
     * Deducts the expense from the treasury
     * @param cost
     */
    public void expense(int cost) {
        this.treasury -= cost;
    }

    /**
     * Initialises provinces from JSON file
     * @throws IOException
     */
	private void setProvinces() throws IOException {
        String file = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
        JSONObject obj = new JSONObject(file);
        JSONArray array = obj.getJSONArray(name);
        for (int i = 0; i < array.length(); i++) {
            provinces.add(new Province(array.getString(i), this));
        }
    }

    /**
     * Returns a list of all battalions owned
     * @return List<Battalion>
     */
    public List<Battalion> getBattalions(String province) {
        return getProvince(province).getBattalions();
    }

    /**
     * Collects Tax from provinces;
     */
    public void collectTax() {
        for (Province p : provinces) {
            this.treasury = treasury + p.collectTax();
        }
    }

    /**
     * Returns number of provinces in faction.
     * @return int
     */
    public int countProvinces() {
        return provinces.size();
    }

    /**
     * Moves troops from origin province to destination.
     * @param army String
     * @param destination String
     * @return boolean - true if successful and false otherwise.
     */
    public boolean moveTroops(String base, String destination) {
        Province origin = getProvince(base);
        Province dest = getProvince(destination);

        if (dest != null && origin != null) {
            Army a = origin.joinForces();
            if (origin.isAdjacent(destination)) {
                a.setProvince(dest);
                dest.addArmy(a);
                origin.removeArmy(a);
                return true;
            }
        }
        return false;
    }

    /**
     * Recruits a new army the a given province.
     * @param province String
     * @param battalion String
     * @param year int
     * @return boolean - true if successful and false otherwise.
     */
    public boolean recruitTroops(String province, String battalion, String type, int year) {
        Province p = getProvince(province);
        if (p != null) {
            try {
                return p.recruitTroops(battalion, type, year);
            } catch (Exception e) {
            }
        }
        System.out.println("PROVINCE NOT FOUND");
        return false;
    }

    /**
     * Adds a province to the faction
     * @param province Province
     */
    public void addProvince(Province province) {
        provinces.add(province);
    }

    /**
     * Removes a province from the faction
     * @param province Province
     */
    public void removeProvince(Province province) {
        provinces.remove(province);
    }

    /**
     * Removes an army from the faction
     * @param army Army
     */
    public void removeArmy(Army army) {
        for (Province p : provinces) {
            p.removeArmy(army);
        }
    }

    /**
     * Converts the Faction to JSON format.
     * @return JSONObject
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("treasury", treasury);
        JSONArray array = new JSONArray();
        for (Province p : provinces) {
            array.put(p.toJSON());
        }
        obj.put("provinces", array);
        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
