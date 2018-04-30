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

    private SerialPort comPort = null;
    private boolean portSelectAccepted = false;

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

    public boolean openPort() {
        boolean res = false;
        try {
            if(comPortHeader.getText().isEmpty()) { //-- если в настройках нет порта
                return false;
            }
            comPort = new SerialPort(comPortList.getSelectionModel().getSelectedItem().toString());
            res = comPort.openPort();
            comPort.setParams(Integer.parseInt(comPortBaudrate.getSelectionModel().getSelectedItem().toString()),
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
            } catch (
                SerialPortException ex) {
                System.err.print(ex.getMessage());
                Stage stage = (Stage)buttonAccept.getScene().getWindow();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("Warning");
                alert.setContentText("Port busy");
                alert.show();
        }
        return res;
    }

    public void closePort()  {
        try {
            if(comPort != null) {
                if(comPort.isOpened()) {
                    comPort.closePort();
                }
            }
        } catch (SerialPortException ex) {

        }
    }

    public void onAccept() {
        Stage stage;
        //TODO: save config xml
        portSelectAccepted = true;
        stage = (Stage)buttonAccept.getScene().getWindow();
        stage.close();
    }

    public void onExit() {
        Stage stage;
        stage = (Stage)buttonExit.getScene().getWindow();
        portSelectAccepted = false;
        stage.close();
    }

    public String getConnectionHeader() {
        return comPortHeader.getText().toString();
    }

    public void setConnectionHeader(String header) {
        comPortHeader.setText(header);
    }

    public String getConnectionPortName() {
        return comPortList.getSelectionModel().getSelectedItem().toString();
    }

    public boolean getPortIsSelected() {
        return portSelectAccepted;
    }
}
