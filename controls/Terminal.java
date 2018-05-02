import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
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
    public Button buttonStart;
    @FXML
    public Button buttonStop;
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

        tabConnects.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            if(connectionFactory.getSize() == 0) {
                int indexTab = obs.getValue().intValue();
                if(indexTab != -1) {
                    if (connectionFactory.getConnectionItem(indexTab).getConnectionEstab().get()) {
                        buttonStop.disableProperty().bind(connectionFactory.getConnectionItem(indexTab).getConnectionPaused().not());
                        buttonStart.disableProperty().bind(connectionFactory.getConnectionItem(indexTab).getConnectionPaused());
                    }
                } else {
                    tabConnects.getTabs().removeAll();
                    if(connectionFactory.getSize()==0) {
                        buttonConnect.disableProperty().unbind();
                        Tab tab1 = new Tab();
                        buttonStop.disableProperty().unbind();
                        buttonStart.disableProperty().unbind();
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
                Tab tab = new Tab("");
                if (connectionFactory.getIsFirstElement()) {
                    tabConnects.getTabs().get(index - 1).textProperty().bindBidirectional(connectionFactory.getConnectionItem(0).getHeader());
                } else {
                    tabConnects.getTabs().addAll(tab);
                    tab.textProperty().bindBidirectional(connectionFactory.getConnectionItem(index - 1).getHeader());
                }
                //-- style tab property
                tabConnects.getTabs().get(index-1).
                        styleProperty().bindBidirectional(connectionFactory.getStyleProperty(index - 1));
                tabConnects.selectionModelProperty().get().select(tabConnects.selectionModelProperty().get().getSelectedIndex() + 1);
            }

            if(connectionFactory.getLastOperationRemove()) {
                if (connectionFactory.getIsFirstElement()) {
                    Tab tab1 = new Tab("");
                    tabConnects.getTabs().get(0).textProperty().unbind();
                    tabConnects.getTabs().get(0).textProperty().set("");
                    tabConnects.getTabs().get(0).styleProperty().set(connectionFactory.getColorStyleDefault());
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

    public void onButtonStopStart() {

    }

    public void onButtonFlush() {

    }

    public void onButtonSettings() {

    }

    public void onMenuClose() {

    }

    private void onStartStopButtonSetMode(boolean started) {

    }

    public void onMenuAbout() {
        Stage stage = (Stage)buttonConnect.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("About the programm");
        alert.setHeaderText("");
        alert.setContentText("This is simple serial port terminal\nversion 0.0\n\nKhomin Vladimir\nkhominvladimir@yandex.ru\nhttps://vk.com/id100603673\n29.04.2018");
        alert.show();
    }
}

//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("connection.fxml"));
//            try {
//                boolean isFirstConnetion = false;
//                Scene sceneConnection = new Scene(fxmlLoader.load());
//                Stage stage = new Stage();
//                stage.setScene(sceneConnection);
//                if(connections.isEmpty()) {
//                    isFirstConnetion = true;
//                }
//                connections.add((Connection) (Connection) fxmlLoader.getController());
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.setTitle("Connection");
//                Tab tab = new Tab("   ");
//                VBox vbox = (VBox)stage.getScene().lookup("#vbox");
//                vbox.prefHeightProperty().bind(sizeTabProperty);
//                tab.setContent(vbox);
//                tabConnectuins.getTabs().addAll(tab);
//
//
//
//                if(isFirstConnetion) {
//                    tabConnectuins.getTabs().remove(0);
//                }
//                SingleSelectionModel<Tab> selectionModel = tabConnectuins.getSelectionModel();
//                selectionModel.select(connections.size()-1); //select by index starting with 0
//                selectionModel.clearSelection(); //clear your selection
//            } catch (IOException ex) {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Error");
//                alert.setHeaderText(null);
//                alert.setContentText("Error opening port @" + ex.toString());
//                alert.showAndWait();
//                System.err.print(ex);
//            }
//        paramConnAvailableBinding.setValue(connections.isEmpty());


//
//        Timeline timeline;
//        timeline = new Timeline();
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.getKeyFrames().add(
//                new KeyFrame(Duration.millis(300),
//                        new EventHandler<ActionEvent>() {
//                            public void handle(ActionEvent event) {
//                                int index = 0;
//                                boolean needDeleteNoActiveConnect = false;
//                                for(Connection con:connections) {
//                                    if (con != null) {
//                                        String portName = con.getConnectionPortName();
//                                        String portHeader = con.getConnectionHeaderName();
//                                        if(portHeader.equals(portName)) {
//                                            tabConnectuins.getTabs().get(index).setText(portName);
//                                        } else {
//                                            tabConnectuins.getTabs().get(index).setText(portHeader + "\n(" + portName + ")");
//                                        }
//                                        if(con.getConnectionActive() == ConnectionState.State.connectionDestroid) {
//                                            needDeleteNoActiveConnect = true;
//                                            conPauseAvailableBinding.set(false);
//                                        }
//                                    }
//                                    index++;
//                                }
//                                if(needDeleteNoActiveConnect) {
//                                    onButtonDisconnect();
//                                }
//                            }
//                        }));
//        timeline.playFromStart();
//
//        //-- Edit tab connection header
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("editTabHader.fxml"));
//        try {
//            sceneEditConnectionHeader = new Scene(fxmlLoader.load());
//            editHeaderStage.setScene(sceneEditConnectionHeader);
//            editTabHeader = (EditTabHeader) fxmlLoader.getController();
//            editHeaderStage.initModality(Modality.APPLICATION_MODAL);
//            editHeaderStage.setTitle("Edit header connection");
//        } catch (IOException ex) {
//            System.err.print(ex);
//        }