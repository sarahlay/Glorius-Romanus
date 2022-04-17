package unsw.gloriaromanus;

import java.net.URL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
public class startMenuController extends MenuController{
    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    @FXML
    private TextField playerA;

    @FXML
    private TextField playerB;

    @FXML
    private ChoiceBox<String> taxA;

    @FXML
    private ChoiceBox<String> taxB;

    @FXML
    private TextField newCampaign;

    @FXML
    private TextField savedCampaign;

    @FXML
    private ChoiceBox<String> victoryChoiceBox;

    @FXML
    public void startGame(ActionEvent a) {
        if (playerA.getText().trim().isEmpty() ||
            playerB.getText().trim().isEmpty() ||
            newCampaign.getText().trim().isEmpty()) {
            return;
        } else if (victoryChoiceBox.getValue() == null) {
            getGame().startCampaign(playerA.getText(), playerB.getText(), newCampaign.getText());
            getCampaign().setAllTax(taxA.getValue(), taxB.getValue());
        } else {
            getGame().startCampaign(playerA.getText(), playerB.getText(), victoryChoiceBox.getValue(), newCampaign.getText());
        }
        closeMenu(a);
    }

    @FXML
    public void loadGame(ActionEvent a) {
        String name = savedCampaign.getText();
        if (savedCampaign.getText() == null || !getGame().isCampaign(name)) {
            return;
        }
        getGame().loadCampaign(savedCampaign.getText());
        closeMenu(a);
    }

    @FXML
    private void initialize() {
        ObservableList<String> victoryConditions = FXCollections.observableArrayList("TREASURY","WEALTH","CONQUEST");
        ObservableList<String> taxChoices = FXCollections.observableArrayList("low","normal","high","veryHigh");    
        victoryChoiceBox.getItems().addAll(victoryConditions);
        taxA.getItems().addAll(taxChoices);
        taxB.getItems().addAll(taxChoices);
    }
    
    private void closeMenu(ActionEvent a) {
        try {
            this.clickedSwitchMenu(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
