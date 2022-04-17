package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public abstract class MenuController {
    private GloriaRomanusController parent;

    @FXML
    private Label year;

    @FXML
    private Label faction;

    @FXML
    private Label player;

    @FXML
    private Label treasury;

    @FXML
    private Label goal;

    @FXML
    private Label wealth;

    @FXML
    private Label provinces;

    @FXML
    private TextArea output_terminal;

    public void setParent(GloriaRomanusController parent) {
        if (parent == null){
            System.out.println("GOT NULL");
        }
        this.parent = parent;
    }

    public GloriaRomanusController getParent(){
        return parent;
    }

    public Game getGame() {
        return parent.getGame();
    }

    public Campaign getCampaign() {
        return getGame().getCampaign();
    }

    public void appendToTerminal(String message) {
        output_terminal.appendText(message + "\n");
    }

    public void update() {
        Campaign campaign = getCampaign();
        year.setText(String.valueOf(campaign.getYear()));
        faction.setText(campaign.getFaction());
        player.setText(campaign.getPlayer());
        treasury.setText(String.valueOf(campaign.getTreasury()));
        goal.setText(getCampaign().getGoal().toString());
        wealth.setText(String.valueOf(campaign.getWealth()));
        provinces.setText(String.valueOf(campaign.getProvinces().size()));
    }

    @FXML
    public void clickedSwitchMenu(ActionEvent e) throws Exception {
        parent.switchMenu();
    }

    @FXML
    public void saveGame(ActionEvent e) {
        getGame().saveCampaign();
    }

    @FXML
    public void leaveGame(ActionEvent e) {
        getParent().openMainMenu();
    }
}
