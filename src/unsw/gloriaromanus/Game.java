package unsw.gloriaromanus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONObject;

/**
 * Stores a game
 */
public class Game {
    private Campaign campaign;
    private String campaignName;

    /**
     * Creates a new game
     */
    public Game() {
        this.campaign = null;
        this.campaignName = "";
    }

    /**
     * Returns the campaign
     * @return Campaign
     */
    public Campaign getCampaign() {
        return campaign;
    }

    /**
     * Starts a new campaign.
     * @param playerA String
     * @param playerB String
     * @return Campaign
     */
    public Campaign startCampaign(String playerA, String playerB, String campaignName) {
        this.campaign = new Campaign(playerA, playerB);
        this.campaignName = campaignName;
        return campaign;
    }

    /**
     * Starts a new campaign, with a goal picked.
     * @param playerA String
     * @param playerB String
     * @param goal String
     * @return Campaign
     */
    public Campaign startCampaign(String playerA, String playerB, String goal,String campaignName) {
        this.campaign = new Campaign(playerA, playerB, goal);
        this.campaignName = campaignName;
        return campaign;
    }

    /**
     * Loads the saved campaign.
     * @return Campaign
     */
    public Campaign loadCampaign(String campaignName) {
        String fileName = new String("/tmp/" + campaignName + ".ser");
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            campaign = (Campaign) in.readObject();
            in.close();
            file.close();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Campaign class not found");
            c.printStackTrace();
            return null;
        }
        this.campaignName = campaignName;
        return campaign;
    }

    /**
     * Returns true if campaign name exists and false otherwise
     */
    public boolean isCampaign(String name) {
        String fileName = new String("/tmp/" + name + ".ser");
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            in.close();
            file.close();
            return true;
        } catch (IOException i) {
            return false;
        } 
    }

    /**
     * Save the campaign campaign.
     */
    public void saveCampaign() {
        String fileName = new String("/tmp/" + campaignName + ".ser");
        try {
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(campaign);
            out.close();
            file.close();
            System.out.println("Campaign \"" + campaignName + "\" saved");
        } catch (IOException e) {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
    }

    /**
     * Leave the campaign.
     */
    public void leaveCampaign() {
        this.campaign = null;
    }

    /**
     * Converts the Game data to JSON format.
     * @return
     */
    private JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        if (campaign != null)
            obj.put("campaign", campaign.toJSON());
        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString(2);
    }
}
