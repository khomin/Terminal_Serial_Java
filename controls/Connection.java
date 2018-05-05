import com.sun.javafx.scene.EventHandlerProperties;
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
import javafx.scene.image.Image;
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
    private BooleanProperty connectionEstab = new SimpleBooleanProperty(false);
    private BooleanProperty dataModeIsAscii = new SimpleBooleanProperty(true);
    private BooleanProperty dataSerialIsRunMode = new SimpleBooleanProperty(true);

    private ConnectionData connectionData = new ConnectionData();
    private ConnectionDataAnimationStyle connectionAnimation;
    private FXMLLoader fxmlDataTab = new FXMLLoader();
    private Stage stageConnectionData = new Stage();

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
                dataSerialIsRunMode.set(true);
            } catch(SerialPortException ex ) {
                System.err.print(ex.getMessage());
            }
        } else {
            connectionEstab.set(false);
            dataSerialIsRunMode.set(false);
        }

        //-- ConnectionFactory
        try {
            fxmlDataTab.setLocation(getClass().getResource("connection.fxml"));
            Scene sceneConnection = new Scene(fxmlDataTab.load());
            stageConnectionData.setScene(sceneConnection);
            stageConnectionData.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
            connectionData = (ConnectionData) fxmlDataTab.getController();
            stageConnectionData.initModality(Modality.APPLICATION_MODAL);
            stageConnectionData.setTitle("Connection");

            EventHandlerProperties eventHandlerProperties;
            connectionData.getPropertySendButton().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            try {
                                comPort.writeString(connectionData.getSendDataProperty());
                                connectionData.getSendDataProperty();
                            } catch (SerialPortException ex) {
                                System.err.print(ex);
                            }
                        }
                    });

        } catch (IOException ex) {
            System.err.print(ex);
        }
    }

    public VBox getConnectionDataStage() {
        return (VBox) stageConnectionData.getScene().lookup("#vbox");
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

    public void setConnectionAnimation(Tab tab) {
        connectionAnimation = new ConnectionDataAnimationStyle(tab);
    }

    public void flushConnectionData() {
        connectionData.clearReceiveData();
    }

    public BooleanProperty getDataModeIsAscii() {
        return dataModeIsAscii;
    }

    public BooleanProperty getDataIsRunMode() {
        return dataSerialIsRunMode;
    }

    public boolean isPortOpen() {
        return comPort.isOpened();
    }

    public BooleanProperty getConnectionEstab() {
        return connectionEstab;
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] buffer = {0};
        if(serialPortEvent.isRXCHAR()) {
            if ((connectionEstab.get()) == true) {
                if(dataSerialIsRunMode.get() == true) {
                    if (comPort != null) {
                        try {
                            buffer = comPort.readBytes();
                            if (buffer != null) {
                                if (buffer.length != 0) {
                                    if(dataModeIsAscii.getValue()) {
                                        connectionData.setReceiveData(buffer);
                                        connectionAnimation.startAnimation();
                                    } else {
                                        connectionData.setReceiveData(bytesToHex(buffer).getBytes());
                                    }
                                } else {
                                    System.err.print("buf == null");
                                }
                            }
                        } catch (SerialPortException e) {
                            e.printStackTrace();
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
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}