import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.util.Duration;
import jssc.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class Connection {
    private String portName = "Undefined";
    private int portStopBit = SerialPort.STOPBITS_1;
    private int portParity = SerialPort.PARITY_NONE;
    private int portBaudrate = SerialPort.BAUDRATE_115200;
    private StringProperty header = new SimpleStringProperty("");
    private SerialPort comPort = null;
    private StringProperty comDataBuff = new SimpleStringProperty("");
    private BooleanProperty connectionEstab = new SimpleBooleanProperty(false);
    private BooleanProperty connectionPaused = new SimpleBooleanProperty(false);
    private ConnectionData connectionData = new ConnectionData();

    private FXMLLoader fxmlDataTab = new FXMLLoader();
    private Stage stageConnectionParam = new Stage();

    public Connection(String portName,
                            int portStopBits,
                            int portParity,
                            int portBaudrate) {
        this.portName = portName;
        this.portStopBit = portStopBits;
        this.portParity = portParity;
        this.portBaudrate = portBaudrate;
        this.header.set(portName);

        if(openPort()) {
            int mask = SerialPort.MASK_RXCHAR;
            try {
                comPort.addEventListener(this::serialEvent);
                connectionEstab.set(true);
                connectionPaused.set(false);
            } catch(SerialPortException ex ) {
                System.err.print(ex.getMessage());
            }
        } else {
            connectionEstab.set(false);
            connectionPaused.set(false);
        }

        //-- ConnectionFactory
        try {
            fxmlDataTab.setLocation(getClass().getResource("connection.fxml"));
            Scene sceneConnection = new Scene(fxmlDataTab.load());
            stageConnectionParam.setScene(sceneConnection);
            connectionData = (ConnectionData) fxmlDataTab.getController();
            stageConnectionParam.initModality(Modality.APPLICATION_MODAL);
            stageConnectionParam.setTitle("Connection");
        } catch (IOException ex) {
            System.err.print(ex);
        }
    }

    public boolean openPort() {
        boolean res = false;
        try {
            if(portName.isEmpty()) {
                return false;
            }
            comPort = new SerialPort(portName);
            res = comPort.openPort();
            comPort.setParams(portBaudrate,
                        portParity,
                        portStopBit,
                        portParity);

            } catch(SerialPortException ex) {
                System.err.print(ex);
            }
        return res;
    }

    public StringProperty getHeader() {
        return header;
    }

    public void closePort()  {
        try {
            if(comPort != null) {
                if(comPort.isOpened()) {
                    comPort.closePort();
                }
            }
        } catch (SerialPortException ex) {
            System.err.print(ex);
        }
    }

    public BooleanProperty getConnectionEstab() {
        return connectionEstab;
    }
    public BooleanProperty getConnectionPaused() {
        return connectionPaused;
    }

    public SerialPort getComPort() {
        return comPort;
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] buffer = {0};
        int buffLen = 0;
        try {
            if(serialPortEvent.isRXCHAR()) {
                if ((connectionEstab.get()) == true) {
                    if (connectionPaused.get() == false) {
                        if (comPort != null) {
                            buffLen = comPort.getInputBufferBytesCount();
                            if (buffLen != 0) {
                                buffer = comPort.readBytes();
                                try {
                                    comDataBuff.setValue(comDataBuff.get() + new String(buffer, "UTF-8"));
                                    System.out.print(comDataBuff.get());
                                } catch (UnsupportedEncodingException ex) {
                                    System.err.print(ex);
                                }
                            }
                        }
                    }
                }
            }
            if(serialPortEvent.isBREAK()) {
                System.out.print(serialPortEvent);
            }
            if(serialPortEvent.isERR()) {
                System.out.print(serialPortEvent);
            }
        } catch (SerialPortException e) {
            System.out.println("Error while reading a string.");
        }
    }

    //-- serialPortlstener
//    static class SerialPortReader implements SerialPortEventListener {
//        private SerialPort comPort = null;
//        private StringProperty comData = null;
//        private BooleanProperty connectionEstab = null;
//        private BooleanProperty connectioPause = null;
//        private ObservableList<ConnectionData> dataToForm = null;
//
//        public SerialPortReader(SerialPort comPort,
//                                StringProperty comData,
//                                BooleanProperty connectionEstab,
//                                BooleanProperty connectioPause) {
//            this.comPort = comPort;
//            this.dataToForm = dataToForm;
//            this.comData = comData;
//            this.connectionEstab = connectionEstab;
//            this.connectioPause = connectioPause;
//        }
//
//        public void serialEvent(SerialPortEvent event) {
//            byte[] buffer = {0};
//            int buffLen = 0;
//            try {
//
//            } catch (SerialPortException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}

//    @FXML
//    private TableView<ConnectionData> tableView;
//    @FXML
//    private TableColumn<ConnectionData, String> tableDateTime;
//    @FXML
//    private TableColumn<ConnectionData, String> tableDataPackets;
//
//    private ObservableList<ConnectionData> connectionData = FXCollections.observableArrayList();
//
//    public BooleanProperty startAvailable = new SimpleBooleanProperty(false);
//    public BooleanProperty pauseAvailable = new SimpleBooleanProperty(false);
//
//    private StringProperty connectionDataBuff = null;
//    private int connectionDataLastBuffSize = 0;
//    private ConnectionState connectionState = new ConnectionState();

//    @FXML
//    public void initialize() {

//        connectionDataBuff = new SimpleStringProperty("");
//
//        try {
//            boolean isFirstConnetion = false;
//            Scene sceneConnection = new Scene(fxmlLoader.load());
//            Stage stage = new Stage();
//            stage.setScene(sceneConnection);
//            connectionParameters = (ConnectionParameters) fxmlLoader.getController();
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setTitle("Connection");
//            stage.show();
//            stage.setOnHidden(event -> {
//                if(connectionParameters.getPortIsSelected()) {
//                    if(connectionParameters.openPort()) {
//                        int mask = SerialPort.MASK_RXCHAR;
//                        try {
//                            connectionParameters.getComPort().addEventListener(
//                                    new SerialPortReader(connectionParameters.getComPort(),
//                                            connectionDataBuff,
//                                            connectionState));
//                            connectionState.setCurrentState(ConnectionState.State.connectionActive);
//                        } catch(SerialPortException ex ) {
//                            System.err.print(ex.getMessage());
//                        }
//                    } else {
//                        connectionState.setCurrentState(ConnectionState.State.connectionDestroid);
//                    }
//                } else {
//                    connectionState.setCurrentState(ConnectionState.State.connectionDestroid);
//                }
//            });
//        } catch (IOException ex) {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Error opening port @" + ex.toString());
//            alert.showAndWait();
//            System.err.print(ex);
//        }
//
//        initData();
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        tableView.getSelectionModel().setCellSelectionEnabled(true);
//        tableDateTime.setCellValueFactory(new PropertyValueFactory<ConnectionData, String>("DateTime"));
//        tableDataPackets.setCellValueFactory(new PropertyValueFactory<ConnectionData, String>("DataPackets"));
//        // заполняем таблицу данными
//        tableView.setItems(connectionData);
//    }

//    // подготавливаем данные для таблицы
//    // вы можете получать их с базы данных
//    private void initData() {
//        connectionData.add(new ConnectionData("qwerty", "alex@mail.com"));
//        //
//        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
//        tableView.setOnKeyPressed(event -> {
//            if (keyCodeCopy.match(event)) {
//                copySelectionToClipboard(tableView);
//            }
//        });
//
//        Timeline timeline;
//        timeline = new Timeline();
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.getKeyFrames().add(
//        new KeyFrame(Duration.millis(15),
//                new EventHandler<ActionEvent>() {
//                    public void handle(ActionEvent event) {
//                        if(connectionDataBuff != null) {
//                            if (!connectionDataBuff.get().isEmpty()) {
//                                if (connectionDataBuff.get().length() == connectionDataLastBuffSize) {
//                                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                                    Date date = new Date();
//                                    connectionData.add(new ConnectionData(dateFormat.format(date), connectionDataBuff.get()));
//                                    connectionDataBuff.set("");
//                                } else {
//                                    connectionDataLastBuffSize = connectionDataBuff.get().length();
//                                }
//                            }
//                        }
//                    }
//                }));
//        timeline.playFromStart();
//    }
//
//    @SuppressWarnings("rawtypes")
//    public void copySelectionToClipboard(final TableView<?> table) {
//        final Set<Integer> rows = new TreeSet<>();
//        for (final TablePosition tablePosition : table.getSelectionModel().getSelectedCells()) {
//            rows.add(tablePosition.getRow());
//        }
//        final StringBuilder strb = new StringBuilder();
//        boolean firstRow = true;
//        for (final Integer row : rows) {
//            if (!firstRow) {
//                strb.append('\n');
//            }
//            firstRow = false;
//            boolean firstCol = true;
//            for (final TableColumn<?, ?> column : table.getColumns()) {
//                if (!firstCol) {
//                    strb.append('\t');
//                }
//                firstCol = false;
//                final Object cellData = column.getCellData(row);
//                strb.append(cellData == null ? "" : cellData.toString());
//            }
//        }
//        final ClipboardContent clipboardContent = new ClipboardContent();
//        clipboardContent.putString(strb.toString());
//        Clipboard.getSystemClipboard().setContent(clipboardContent);
//    }
//
//    public void close() {
////        connectionParameters.closePort();
////        connectionState.setCurrentState(ConnectionState.State.connectionDestroid);
//    }
//
//    public String getConnectionHeaderName() {
////        if(connectionParameters != null) {
////            return (connectionParameters.getConnectionHeader());
////        }
//        return "...init...";
//    }
//
////    public void setConnectionHeaderName(String headerName) {
////        connectionParameters.setConnectionHeader(headerName);
////    }
//
//    public String getConnectionPortName() {
////        if(connectionParameters != null) {
////            return (connectionParameters.getConnectionPortName());
////        }
//        return "...init...";
//    }
//
//    public ConnectionState.State getConnectionActive() {
//        return connectionState.getCurrentState();
//    }
//
//    public void setConnectionActive(ConnectionState.State state) {
//        if(state == ConnectionState.State.connectionActive) {
//            state = ConnectionState.State.connectionActive;
//        } else {
//            state = ConnectionState.State.connectionNoActive;
//        }
//        this.connectionState.setCurrentState(state);
//    }
//