import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class ConnectionParameters {
    @FXML
    public ComboBox comPortList;
    @FXML
    public ComboBox comPortStopBit;
    @FXML
    public ComboBox comPortParity;
    @FXML
    public TextField comPortHeader;
    @FXML
    public ComboBox comPortBaudrate;
    @FXML
    public Button buttonAccept;
    @FXML
    public Button buttonExit;

    private boolean portIsSelected = false;

    @FXML
    public void initialize() {
        String [] portNames = SerialPortList.getPortNames();
        for(int i = 0; i < portNames.length; i++){
            System.out.println(portNames[i]);
        }
        comPortList.setItems(FXCollections.observableArrayList(portNames));
        comPortList.setValue(portNames[0].toString());
        ObservableList<String> baudList =
                FXCollections.observableArrayList(
                        "600", "1200","2400", "4800", "9600","19200",
                        "38400", "57600", "115200", "256000"
                );
        comPortBaudrate.setItems(baudList);
        comPortBaudrate.setValue("19200");

        ObservableList<String> parityList =
                FXCollections.observableArrayList(
                        "none", "odd","even", "mark", "space"
                );
        comPortParity.setItems(parityList);
        comPortParity.setValue("none");

        ObservableList<String> stopBitList =
                FXCollections.observableArrayList("1", "1.5","2" );
        comPortStopBit.setItems(stopBitList);
        comPortStopBit.setValue("1");
        //
        comPortHeader.setText(portNames[0].toString());
        //
        comPortList.setOnAction(event -> {
            comPortHeader.setText(comPortList.getSelectionModel().getSelectedItem().toString());
        });
    }

    public void onAccept() {
        Stage stage;
        //TODO: save config xml
        portIsSelected = true;
        stage = (Stage)buttonAccept.getScene().getWindow();
        stage.close();
    }

    public void onExit() {
        Stage stage;
        stage = (Stage)buttonExit.getScene().getWindow();
        portIsSelected = false;
        stage.close();
    }

    public String getParamPortName() {
        return comPortList.getSelectionModel().getSelectedItem().toString();
    }

    public int getParamPortStopBit() {
        return Integer.parseInt(comPortStopBit.getSelectionModel().getSelectedItem().toString());
    }

    public int getParamPortParity() {
        return comPortParity.getSelectionModel().getSelectedIndex();
    }

    public int getParamPortBaudrate() {
        return Integer.parseInt(comPortBaudrate.getSelectionModel().getSelectedItem().toString());
    }

    public boolean getPortIsSelected() {
        return portIsSelected;
    }
}
