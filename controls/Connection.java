import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.TreeSet;

public class Connection {
    private ConnectionParameters connectionParameters = null;
    @FXML
    private TableView<ConnectionData> tableView;
    @FXML
    private TableColumn<ConnectionData, String> tableDateTime;
    @FXML
    private TableColumn<ConnectionData, String> tableDataPackets;

    private boolean connectIsNoActive = false;

    private ObservableList<ConnectionData> usersData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("connectionParameters.fxml"));
        try {
            boolean isFirstConnetion = false;
            Scene sceneConnection = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(sceneConnection);
            connectionParameters = (ConnectionParameters) fxmlLoader.getController();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Connection");
            stage.show();
            stage.setOnHidden(event -> {
                if(connectionParameters.getPortIsSelected()) {
                    if(!connectionParameters.openPort()) {
                       connectIsNoActive = true;
                    }
                } else {
                    connectIsNoActive = true;
                }
            });
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error opening port @" + ex.toString());
            alert.showAndWait();
            System.err.print(ex);
        }

        initData();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableDateTime.setCellValueFactory(new PropertyValueFactory<ConnectionData, String>("DateTime"));
        tableDataPackets.setCellValueFactory(new PropertyValueFactory<ConnectionData, String>("DataPackets"));
        // заполняем таблицу данными
        tableView.setItems(usersData);
    }

    // подготавливаем данные для таблицы
    // вы можете получать их с базы данных
    private void initData() {
        usersData.add(new ConnectionData("qwerty", "alex@mail.com"));
        usersData.add(new ConnectionData("dsfsdfw", "bob@mail.com"));
        usersData.add(new ConnectionData("dsfdsfwe", "Jeck@mail.com"));
        usersData.add(new ConnectionData("iueern", "mike@mail.com"));
        usersData.add(new ConnectionData("woeirn", "colin@mail.com"));

        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        tableView.setOnKeyPressed(event -> {
            if (keyCodeCopy.match(event)) {
                copySelectionToClipboard(tableView);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public void copySelectionToClipboard(final TableView<?> table) {
        final Set<Integer> rows = new TreeSet<>();
        for (final TablePosition tablePosition : table.getSelectionModel().getSelectedCells()) {
            rows.add(tablePosition.getRow());
        }
        final StringBuilder strb = new StringBuilder();
        boolean firstRow = true;
        for (final Integer row : rows) {
            if (!firstRow) {
                strb.append('\n');
            }
            firstRow = false;
            boolean firstCol = true;
            for (final TableColumn<?, ?> column : table.getColumns()) {
                if (!firstCol) {
                    strb.append('\t');
                }
                firstCol = false;
                final Object cellData = column.getCellData(row);
                strb.append(cellData == null ? "" : cellData.toString());
            }
        }
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(strb.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public void close() {
        connectionParameters.closePort();
    }

    public String getConnectionHeaderName() {
        if(connectionParameters != null) {
            return (connectionParameters.getConnectionHeader());
        }
        return "...init...";
    }

    public void setConnectionHeaderName(String headerName) {
        connectionParameters.setConnectionHeader(headerName);
    }

    public String getConnectionPortName() {
        if(connectionParameters != null) {
            return (connectionParameters.getConnectionPortName());
        }
        return "...init...";
    }

    public boolean getConnectionIsNoActive() {
        return connectIsNoActive;
    }

}