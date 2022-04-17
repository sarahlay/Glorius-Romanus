package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
public class RecruitmentMenuController extends MenuController{
    @FXML
    private TextField province;
    @FXML
    private ChoiceBox<String> unit_choicebox;
    @FXML
    private TextField unit_name;
    @FXML
    private Label treasury;
    @FXML
    private TextArea troops;

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    public void setProvince(String p) {
        province.setText(p);
    }

    public String getProvince() {
        return province.getText();
    }

    public String getUnitType() {
        return unit_choicebox.getValue();
    }

    public String getUnitName() {
        return unit_name.getText();
    }

    public void displayTroops() {
        troops.clear();
        List<Battalion> battalions = getCampaign().getBattalions(province.getText());
        for (Battalion b : battalions) {
            troops.appendText(b.toString()+"\n");
        }
    }

    public void displayTreasury() {
        treasury.setText(String.valueOf(getCampaign().getTreasury()));
    }

    @FXML
    public void initialize() {
        ObservableList<String> units = FXCollections.observableArrayList(
            "Artillery", 
            "Chariots", 
            "Elephants", 
            "Melee Cavalry", 
            "Melee Infantry"
        );
        unit_choicebox.getItems().addAll(units);
    }

    @FXML
    public void clickedRecruitButton(ActionEvent e) throws IOException {
        getParent().clickedRecruitButton(e, getUnitType(), getUnitName());
    }
}
