package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BasicMenuController extends MenuController{

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    @FXML
    public void endTurn(ActionEvent e) throws IOException {
        boolean hasWonBefore = getParent().hasWon();
        String result = getCampaign().endTurn();
        if (!hasWonBefore && result.equals("win")) {
            getParent().openVictoryMenu();
        }
        update();
    }
}
