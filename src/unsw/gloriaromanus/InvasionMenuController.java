package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InvasionMenuController extends MenuController{
    @FXML
    private TextField invading_province;
    @FXML
    private TextField opponent_province;

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    public void setInvadingProvince(String p) {
        invading_province.setText(p);
    }

    public void setOpponentProvince(String p) {
        opponent_province.setText(p);
    }

    public String getInvadingProvince() {
        return invading_province.getText();
    }

    public String getOpponentProvince() {
        return opponent_province.getText();
    }

    @FXML
    public void clickedInvadeButton(ActionEvent e) throws IOException {
        getParent().clickedInvadeButton(e);
    }
}
