import com.sun.javafx.scene.EventHandlerProperties;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectionFactory {
    private ConnectionParameters connectionParameters = null;
    private ArrayList<Connection> connectionArray = new ArrayList<>();
    private ArrayList<ConnectionDataAnimationStyle> connectionDataAnimationStyles = new ArrayList<>();
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

    public ConnectionFactory() {
        try {
            FXMLLoader fxmlParam = new FXMLLoader();
            //-- param connection
            fxmlParam.setLocation(getClass().getResource("connectionParameters.fxml"));
            sceneConnection = new Scene(fxmlParam.load());
            stageParam = new Stage();
            stageParam.setMaxWidth(330);
            stageParam.setMaxHeight(250);
            stageParam.setMinWidth(330);
            stageParam.setMinHeight(250);
            stageParam.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
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
                        parameters.getHeaderName(),
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
                //-- update index selected element
                currIndex.setValue(currIndex.getValue()+1);
                eventUpdate.setValue(eventUpdate.getValue()+1);
            }
            connAvailableDisconnect.set(connectionArray.isEmpty());
        });
    }

    public void addConnectionTabAnimation(int index, Tab tab) {
        connectionArray.get(index).setConnectionAnimation(tab);
    }

    public void settingsConnectionEdit(int index) {
        stageParam.show();
        stageParam.setOnHidden(event -> {
            if(parameters.getPortIsSelected()) {
                connectionArray.get(index).setParamComPort(
                        parameters.getParamPortBaudrate(),
                        parameters.getParamPortStopBit(),
                        parameters.getParamPortParity());
            }
            connAvailableDisconnect.set(connectionArray.isEmpty());
        });
    }

    public void removeConnection(int index) {
        connectionArray.remove(index);
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

    public Connection getConnectionItem(int index) {
        if(!connectionArray.isEmpty()) {
            return connectionArray.get(index);
        } else {
            return  null;
        }
    }

    public void flushConnectionData(int index) {
        connectionArray.get(index).flushConnectionData();
    }

    public void setDataBinMode(int index) {
        getConnectionItem(index).getDataModeIsAscii().set(false);
    }

    public void setDataAsciiMode(int index) {
        getConnectionItem(index).getDataModeIsAscii().set(true);
    }

    public void setResumeSerialMode(int index) {
        getConnectionItem(index).getDataIsRunMode().set(true);
    }

    public void setPauseSerialMode(int index) {
        getConnectionItem(index).getDataIsRunMode().set(false);
    }

    public int getCurrentConnetionIndex() {
        return currIndex.get();
    }

    public int getSize() {
        return connectionArray.size();
    }

    public String getColorStypeConnected() {
        return null;
    }

    public String getColorStyleDefault() {
        return null;
    }
}