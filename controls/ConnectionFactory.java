import com.sun.javafx.scene.EventHandlerProperties;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectionFactory {
    private ConnectionParameters connectionParameters = null;

    private ArrayList<Connection> connectionArray = new ArrayList<>();
    private ArrayList<StringProperty>tabStyleProperty = new ArrayList<>();
    BooleanProperty connAvailableDisconnect = new SimpleBooleanProperty(true);
    private IntegerProperty currIndex = new SimpleIntegerProperty(0);
    private IntegerProperty eventUpdate = new SimpleIntegerProperty(0);
    //
    private Stage stageParam = null;
    private Scene sceneConnection = null;
    private ConnectionParameters parameters = null;
    private boolean isFirstElement = false;
    private boolean lastOperationRemove = false;
    private boolean lastOperationInsert = false;
    private String colorStypeConnected = "-fx-background-color: #d7efff";
    private String colorStyleDisonnected = "-fx-background-color: #9e9c9e";
    private String colorStyleDefault = colorStypeConnected;

    public ConnectionFactory() {
        try {
            FXMLLoader fxmlParam = new FXMLLoader();
            //-- param connection
            fxmlParam.setLocation(getClass().getResource("connectionParameters.fxml"));
            sceneConnection = new Scene(fxmlParam.load());
            stageParam = new Stage();
            stageParam.setScene(sceneConnection);
            parameters = (ConnectionParameters)fxmlParam.getController();
            stageParam.initModality(Modality.APPLICATION_MODAL);
            stageParam.setTitle("Connection");
        } catch (IOException ex) {

        }
    }

    public void addConnection() {
        stageParam.show();
        //-- closing dialog event
        stageParam.setOnHidden(event -> {
            //-- if port is selected and ckicked button 'accept'
            if(parameters.getPortIsSelected()) {
                Connection connection = new Connection(
                            parameters.getParamPortName(),
                            parameters.getParamPortStopBit(),
                            parameters.getParamPortParity(),
                            parameters.getParamPortBaudrate()
                    );
                if(connectionArray.isEmpty()) {
                    isFirstElement = true;
                } else{
                    isFirstElement = false;
                }
                lastOperationInsert = true;
                lastOperationRemove = false;
                connectionArray.add(connection);
                //-- tab style
                if(connection.getConnectionEstab().get()) {
                    tabStyleProperty.add(new SimpleStringProperty(colorStypeConnected));
                } else {
                    tabStyleProperty.add(new SimpleStringProperty(colorStyleDisonnected));
                }
                //-- update index selected elemnt
                currIndex.setValue(currIndex.getValue()+1);
                eventUpdate.setValue(eventUpdate.getValue()+1);
            }
            connAvailableDisconnect.set(connectionArray.isEmpty());
        });
    }

    public void removeConnection(int index) {
        connectionArray.remove(index);
        tabStyleProperty.remove(index);
        if(connectionArray.isEmpty()) {
            isFirstElement = true;
        } else {
            isFirstElement = false;
        }
        connAvailableDisconnect.set(connectionArray.isEmpty());
        lastOperationInsert = false;
        lastOperationRemove = true;
        //-- update index selected elemnt
        currIndex.setValue(index);
        eventUpdate.setValue(eventUpdate.getValue()+1);
    }

    public IntegerProperty getEventUpdate() {
        return eventUpdate;
    }

    public boolean getIsFirstElement() {
        return isFirstElement;
    }

    public boolean getLastOperationInsert() {
        return lastOperationInsert;
    }

    public boolean getLastOperationRemove() {
        return lastOperationRemove;
    }

    public StringProperty getStyleProperty(int index) {
        return tabStyleProperty.get(index);
    }

    public Connection getConnectionItem(int index) {
        if(index >=0) {
            if(!connectionArray.isEmpty()) {
                return connectionArray.get(index);
            } else {
                return  null;
            }
        } else {
            return null;
        }
    }

    public int getCurrentConnetionIndex() {
        return currIndex.get();
    }
    public void setCurrentConnetionIndex(int index) {
        currIndex.setValue(index);
    }

    public int getSize() {
        return connectionArray.size();
    }

    public String getColorStypeConnected() {
        return colorStypeConnected;
    }

    public String getColorStyleDisonnected() {
        return colorStyleDisonnected;
    }
    public String getColorStyleDefault() {
        return colorStyleDefault;
    }
}


//    private ConnectionParameters connectionParam;
//    FXMLLoader fxmlConnectionParam = new FXMLLoader();
//    Stage stageConnectionParam = new Stage();

//    boolean isFirstConnetion = false;

//    @FXML
//    public void initialize() {
//        try {
////            FXMLLoader fxmlConnectionFactory = new FXMLLoader();
////            Stage stageConnectionFactory = new Stage();
////            //-- ConnectionFactory
////            fxmlConnectionFactory.setLocation(getClass().getResource("connectionParameters.fxml"));
////            Scene sceneConnection = new Scene(fxmlConnectionFactory.load());
////            stageConnectionParam.setScene(sceneConnection);
////            connectionParam = (ConnectionParameters)fxmlConnectionParam.getController();
////            stageConnectionParam.initModality(Modality.APPLICATION_MODAL);
////            stageConnectionParam.setTitle("Connection");
//        } catch (IOException ex) {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Error opening port @" + ex.toString());
//            alert.showAndWait();
//            System.err.print(ex);
//        }
//        initData();
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        tableView.getSelectionModel().setCellSelectionEnabled(true);
//        tableDateTime.setCellValueFactory(new PropertyValueFactory<ConnectionData, String>("DateTime"));
//        tableDataPackets.setCellValueFactory(new PropertyValueFactory<ConnectionData, String>("DataPackets"));
//        // заполняем таблицу данными
//        tableView.setItems(connectionData);
//    }