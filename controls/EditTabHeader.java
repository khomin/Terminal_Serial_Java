import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditTabHeader {
    @FXML
    public TextField editHeaderText;
    @FXML
    public Button buttonAccept;
    @FXML
    public Button buttonExit;

    private StringProperty header = new SimpleStringProperty("");

    @FXML
    void initialize() {
        editHeaderText.textProperty().bindBidirectional(header);
    }

    public void onAccept() {
        Stage stage;
        stage = (Stage)buttonAccept.getScene().getWindow();
        stage.close();
    }

    public void onExit() {
        Stage stage;
        stage = (Stage)buttonExit.getScene().getWindow();
        stage.close();
    }

    public void setEditHeader(String text) {
        header.set(text);
    }

    public String getEditHeader() {
        return header.get();
    }
}
