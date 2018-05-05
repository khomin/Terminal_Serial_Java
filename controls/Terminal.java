import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.EventHandler;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static javax.print.attribute.standard.MediaSizeName.D;

public class Terminal {
    @FXML
    public Button buttonConnect;
    @FXML
    public Button buttonDisconnect;
    @FXML
    public Button buttonFlush;
    @FXML
    public Button buttonSettings;
    @FXML
    public MenuItem menuClose;
    @FXML
    public MenuItem menuAbout;
    @FXML
    public Button buttonSelectBinMode;
    @FXML
    public Button buttonSelectAsciMode;
    @FXML
    public Button buttonResumeSerial;
    @FXML
    public Button buttonPauseSerial;

    @FXML
    public TabPane tabConnects;
    //--
    private ConnectionFactory connectionFactory = new ConnectionFactory();
    //--
    private EditTabHeader editTabHeader = null;
    private Scene editHeaderScene = null;
    private Stage editHeaderStage = null;
    private FXMLLoader editHeaderfxml = null;
    @FXML
    public void initialize() {
        //-- gui
        buttonDisconnect.disableProperty().bind(connectionFactory.connAvailableDisconnect);
        buttonFlush.disableProperty().bind(connectionFactory.connAvailableDisconnect);
        buttonSettings.disableProperty().bind(connectionFactory.connAvailableDisconnect);
        buttonSelectAsciMode.disableProperty().bind(connectionFactory.connAvailableDisconnect);
        buttonSelectBinMode.disableProperty().bind(connectionFactory.connAvailableDisconnect);
        buttonResumeSerial.disableProperty().bind(connectionFactory.connAvailableDisconnect);
        buttonPauseSerial.disableProperty().bind(connectionFactory.connAvailableDisconnect);

        tabConnects.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            if(connectionFactory.getSize() != 0) {
                int index = obs.getValue().intValue();
                if(index != -1) {
                    if (connectionFactory.getConnectionItem(index).getConnectionEstab().get()) {
                        buttonSelectAsciMode.disableProperty().bind(connectionFactory.getConnectionItem(index).getDataModeIsAscii().not().or(connectionFactory.connAvailableDisconnect));
                        buttonSelectBinMode.disableProperty().bind(connectionFactory.getConnectionItem(index).getDataModeIsAscii().or(connectionFactory.connAvailableDisconnect));
                        buttonResumeSerial.disableProperty().bind(connectionFactory.getConnectionItem(index).getDataIsRunMode().not().or(connectionFactory.connAvailableDisconnect));
                        buttonPauseSerial.disableProperty().bind(connectionFactory.getConnectionItem(index).getDataIsRunMode().or(connectionFactory.connAvailableDisconnect));
                    }
                } else {
                    tabConnects.getTabs().removeAll();
                    if(connectionFactory.getSize()==0) {
                        buttonConnect.disableProperty().unbind();
                        Tab tab1 = new Tab();
                    }
                }
            }
        });

        connectionFactory.getEventUpdate().addListener(observable -> {
            int index = connectionFactory.getCurrentConnetionIndex();
            if (index == -1) {
                index = 0;
            }

            if(connectionFactory.getLastOperationInsert()) {
                if (connectionFactory.getIsFirstElement()) {
                    tabConnects.getTabs().get(index - 1).textProperty().bindBidirectional(connectionFactory.getConnectionItem(index -1).getHeader());
                } else {
                    Tab tab = new Tab("");
                    tabConnects.getTabs().addAll(tab);
                    tab.textProperty().bindBidirectional(connectionFactory.getConnectionItem(index - 1).getHeader());
                }
                //-- style tab property
                connectionFactory.addConnectionTabAnimation(index -1, tabConnects.getTabs().get(index - 1));
                //
                tabConnects.selectionModelProperty().get().select(
                        tabConnects.selectionModelProperty().get().getSelectedIndex() + 1);
                //
                tabConnects.getSelectionModel().getSelectedItem().setContent(
                        connectionFactory.getConnectionItem(index-1).getConnectionDataStage());
                //
                buttonSelectAsciMode.disableProperty().bind(connectionFactory.getConnectionItem(index-1).getDataModeIsAscii().not().or(connectionFactory.connAvailableDisconnect));
                buttonSelectBinMode.disableProperty().bind(connectionFactory.getConnectionItem(index-1).getDataModeIsAscii().or(connectionFactory.connAvailableDisconnect));
                buttonResumeSerial.disableProperty().bind(connectionFactory.getConnectionItem(index-1).getDataIsRunMode().not().or(connectionFactory.connAvailableDisconnect));
                buttonPauseSerial.disableProperty().bind(connectionFactory.getConnectionItem(index-1).getDataIsRunMode().or(connectionFactory.connAvailableDisconnect));
            }

            if(connectionFactory.getLastOperationRemove()) {
                if (connectionFactory.getIsFirstElement()) {
                    Tab tab1 = new Tab("");
                    tabConnects.getTabs().get(0).textProperty().unbind();
                    tabConnects.getTabs().get(0).textProperty().set("");
                    tabConnects.getSelectionModel().getSelectedItem().setContent(null);
                } else {
                    tabConnects.getTabs().get(index).textProperty().unbind();
                    tabConnects.getTabs().remove(index);
                    tabConnects.selectionModelProperty().get().select(
                            tabConnects.selectionModelProperty().get().getSelectedIndex()-1
                    );
                }
            }
        });

        try {
            editHeaderfxml = new FXMLLoader();
            editHeaderfxml.setLocation(getClass().getResource("editTabHader.fxml"));
            editHeaderScene = new Scene(editHeaderfxml.load());
            editHeaderStage = new Stage();
            editHeaderStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
            editHeaderStage.setScene(editHeaderScene);
            editTabHeader = (EditTabHeader)editHeaderfxml.getController();
            editHeaderStage.initModality(Modality.APPLICATION_MODAL);
            editHeaderStage.setTitle("Edit tab header");
        } catch (IOException ex) {

        }
        //-- edit tab header
        tabConnects.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        if(connectionFactory.getSize() != 0) {
                            editTabHeader.setEditHeader(tabConnects.getSelectionModel().getSelectedItem().getText());
                            editHeaderStage.show();
                            editHeaderStage.setOnHiding(event -> {
                                tabConnects.getSelectionModel().getSelectedItem().setText(editTabHeader.getEditHeader());
                            });
                        }
                    }
                }
            }
        });
    }

    public void onButtonConnect() {
        connectionFactory.addConnection();
    }

    public void onButtonDisconnect() {
        connectionFactory.getConnectionItem(tabConnects.getSelectionModel().getSelectedIndex()).closePort();
        connectionFactory.removeConnection(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onButtonFlush() {
        connectionFactory.flushConnectionData(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onButtonSettings() {
        connectionFactory.settingsConnectionEdit(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onButtonSelectBinMode() {
        connectionFactory.setDataAsciiMode(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onButtonSelectAsciMode(){
        connectionFactory.setDataBinMode(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onButtonResumeSerial() {
        connectionFactory.setPauseSerialMode(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPauseSerial() {
        connectionFactory.setResumeSerialMode(tabConnects.getSelectionModel().getSelectedIndex());
    }

    public void onMenuClose() {
        System.exit(0);
    }

    public void onMenuAbout() {
        Stage stage = (Stage)buttonConnect.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("About the programm");
        alert.setHeaderText("");
        alert.setContentText("This is simple serial port terminal\nversion 0.1\n\nKhomin Vladimir\nkhominvladimir@yandex.ru\nhttps://vk.com/id100603673\n05.05.2018");
        stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.show();
    }
}