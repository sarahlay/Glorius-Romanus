package unsw.gloriaromanus;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.gloriaromanus.observer.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.Serializable;

/**
 * Stores a province and its' attributes.
 */
public class Province implements Observer, Serializable {

    private static final long serialVersionUID = 1L; // UPDATE
    private String name;
    private Faction faction;
    private int taxRate;
    private int wealth;
    private int wealthGrowth;
    private List <Battalion> training;
    private List <Army> armies;

    /**
     * Creates a new province.
     * @param name String
     * @param faction Faction
     */
    public Province(String name, Faction faction) {
        this.faction = faction;
        this.name = name;
        setTaxRate("normal");
        this.wealth = 1000;
        this.training = new ArrayList<>();
        this.armies = new ArrayList<>();
    }

    /**
     * Sets the tax rate of the province given a tax level.
     * @param tax String
     */
    public void setTaxRate(String tax) {
        switch (tax) {
            case "low":
                taxRate = 10;
                wealthGrowth = 10;
                break;
            case "normal":
                taxRate = 15;
                wealthGrowth = 0;
                break;
            case "high":
                taxRate = 20;
                wealthGrowth = -10;
                break;
            case "veryHigh":
                taxRate = 25;
                wealthGrowth = -30;
        }
    }


    /**
     * Sets the current Faction.
     * @param faction Faction
     */
    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    /**
     * Returns the province name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of battalions
     * @return Army
     */
    public List<Battalion> getBattalions() {
        List<Battalion> list = new ArrayList<>();
        for (Army a : armies) {
            list.addAll(a.getBattalions());
        }
        return list;
    }

    /**
     * Returns an Army class from a given name.
     * @param army String
     * @return Army
     */
    public Army getArmy(String army) {
        for (Army a : armies) {
            if (a.hasBattalion(army)) return a;
        }
        return null;
    }

    /**
     * Returns the province wealth.
     * @return int
     */
    public int getWealth() {
        return wealth;
    }

    /**
     * Retrieves province statistics from the JSON file (BattalionStats.json)
     * @param name String
     * @return JSONObject
     * @throws IOException
     */
    public JSONObject getStats(String name) throws IOException {
        String file = Files.readString(Paths.get("src/unsw/gloriaromanus/BattalionStats.json"));
        JSONObject obj = new JSONObject(file);
        return obj.getJSONObject(name);
    }

    /**
     * Returns true if current province is adjacent to given province.
     * @param province String
     * @return boolean
     */
    public boolean isAdjacent(String province) {
        try {
            String file = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
            JSONObject obj = new JSONObject(file);
            return obj.getJSONObject(name).getBoolean(province);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Recruits a new Army to the province.
     * @param name String
     * @param type String
     * @param sale int
     * @return boolean
     * @throws IOException
     */
    public boolean recruitTroops(String name, String type, int sale) throws IOException {
        trainTroops(sale);
        JSONObject stats = getStats(type);
        int cost = stats.getInt("cost");
        if (cost > faction.getTreasury() || this.training.size() == 2) {
            System.out.println("NOT ENOUGH MONEY OR NOT ENOUGH TRAINING SPOTS");
            return false;
        }
        String category = stats.getString("category");

        // Check for existing army
        Army a = getArmy(name);
        if (a == null) {
            a = new Army(this); 
            addArmy(a);
            faction.expense(cost);
            Battalion b = a.createBattalion(name, category, type, sale);
            this.training.add(b);
        } else {
            Battalion b = a.createBattalion(name, category, type, sale);
            faction.expense(cost);
            this.training.add(b);
        }
        return true;
    }

    /**
     * Adds an army to the province.
     * @param army Army
     */
    public void addArmy(Army army) {
        armies.add(army);
        army.attach(this);
    }

    /**
     * Checks if a troop is ready and removes from training list accordingly.
     * @param year int
     */
    private void trainTroops(int year) {
        for (Battalion b : training) {
            System.out.println("training " + b.getName());
            if (b.isReady(year)) {
                System.out.println("trained " + b.getName());
                training.remove(b);
            }
        }
    }

    /**
     * Merges all Armies in province to form one large army.
     * @return Army
     */
    public Army joinForces() {
        List<Battalion> merged = new ArrayList<>();
        for (Army a: armies) {
            List<Battalion> temp = a.getBattalions();
            merged.addAll(temp);
        }
        armies.clear();
        armies.add(new Army(this, merged));
        return armies.get(0);
    }

    /**
     * Removes an Army from the Province.
     * @param army Army
     */
    public void removeArmy(Army army) {
        armies.remove(army);
    }

    /**
     * Removes an Army from the Province.
     * @param army Army
     */
    public void removeArmy(String army) {
        Army a = getArmy(army);
        if (a != null) removeArmy(a);
    }

    /**
     * Increases Province wealth according to a base rate & set wealthGrowth levels.
     * Collects Tax from the province and deducts from wealth.
     * @return int
     */
    public int collectTax() {
        this.wealth += 1000 * (100 + wealthGrowth) / 100;
        int tax;
        tax = wealth * taxRate / 100;
        this.wealth -= tax;
        return tax;
    }

    /**
     * Converts Province data to JSON format.
     * @return JSONObject
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("province", name);
        // obj.put("taxRate", taxRate);
        JSONArray array = new JSONArray();
        for (Army a : armies)
            array.put(a.toJSON());
        obj.put("armies", array);
        return obj;
    }

    /**
     * Implementing update function for Observer class.
     * Updates the ownership.
     * @param subject Subject
     */
    @Override
    public void update(Subject subject) {
        if (subject instanceof Army) {
            Army a = (Army) subject;
            if (a.getSize() == 0) {
                removeArmy(a);
            }
        }
    }


    @Override
    public String toString() {
        return toJSON().toString();
    }
}
