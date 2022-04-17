package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VictoryMenuController extends MenuController {
/*
    @FXML
    private Label player;

    @FXML
    private void initialize() {
        player.setText(getCampaign().getPlayer());
    }
*/
    @FXML
    public void clickedLeaveGame(ActionEvent e) {
        getParent().closeVictoryMenu();
        leaveGame(e);
    }
    
    @FXML
    public void clickedContinue(ActionEvent e) {
        getParent().closeVictoryMenu();
        saveGame(e);
        getParent().openMenus();
    }
}
