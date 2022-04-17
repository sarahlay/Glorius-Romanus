package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MovementMenuController extends MenuController{
    @FXML
    private TextField origin;
    @FXML
    private TextField destination;

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    public void setOrigin(String p) {
        origin.setText(p);
    }

    public String getOrigin() {
        return origin.getText();
    }

    public void setDestination(String p) {
        destination.setText(p);
    }

    public String getDestination() {
        return destination.getText();
    }

    public boolean containsOrigin() {
        return !origin.getText().equals("");
    }

    public boolean containsDestination() {
        return !destination.getText().equals("");
    }

    @FXML
    public void clickedMoveButton(ActionEvent e) throws IOException {
        getParent().clickedMoveButton(e);
    }
}
