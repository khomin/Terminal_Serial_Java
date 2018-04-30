import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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

public class TerminalController {
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
    public TabPane tabConnectuins;
    //--
    private ArrayList<Connection> connections = new ArrayList<>();
    //
    BooleanProperty paramConnAvailableBinding = new SimpleBooleanProperty(connections.isEmpty());
    DoubleProperty sizeTabProperty = new SimpleDoubleProperty();

    Scene sceneEditConnectionHeader;
    EditTabHeader editTabHeader;
    Stage editHeaderStage = new Stage();

    @FXML
    public void initialize() {
        buttonDisconnect.disableProperty().bind(paramConnAvailableBinding);
        buttonFlush.disableProperty().bind(paramConnAvailableBinding);
        buttonSettings.disableProperty().bind(paramConnAvailableBinding);
        sizeTabProperty.set(tabConnectuins.getHeight());

        Timeline timeline;
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(300),
                        new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event) {
                                int index = 0;
                                boolean needDeleteNoActiveConnect = false;
                                for(Connection con:connections) {
                                    if (con != null) {
                                        String portName = con.getConnectionPortName();
                                        String portHeader = con.getConnectionHeaderName();
                                        if(portHeader.equals(portName)) {
                                            tabConnectuins.getTabs().get(index).setText(portName);
                                        } else {
                                            tabConnectuins.getTabs().get(index).setText(portHeader + "\n(" + portName + ")");
                                        }
                                        if(con.getConnectionIsNoActive()) {
                                            needDeleteNoActiveConnect = true;
                                        }
                                    }
                                    index++;
                                }
                                if(needDeleteNoActiveConnect) {
                                    onButtonDisconnect();
                                }
                            }
                        }));
        timeline.playFromStart();

        //-- Edit tab connection header
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("editTabHader.fxml"));
        try {
            sceneEditConnectionHeader = new Scene(fxmlLoader.load());
            editHeaderStage.setScene(sceneEditConnectionHeader);
            editTabHeader = (EditTabHeader) fxmlLoader.getController();
            editHeaderStage.initModality(Modality.APPLICATION_MODAL);
            editHeaderStage.setTitle("Edit header connection");
        } catch (IOException ex) {
            System.err.print(ex);
        }
    }

    public void onButtonConnect() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("connection.fxml"));
            try {
                boolean isFirstConnetion = false;
                Scene sceneConnection = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(sceneConnection);
                if(connections.isEmpty()) {
                    isFirstConnetion = true;
                }
                connections.add((Connection) (Connection) fxmlLoader.getController());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Connection");
                Tab tab = new Tab("   ");
                VBox vbox = (VBox)stage.getScene().lookup("#vbox");
                vbox.prefHeightProperty().bind(sizeTabProperty);
                tab.setContent(vbox);
                tabConnectuins.getTabs().addAll(tab);

                tabConnectuins.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                for(int tabIterator=0; tabIterator<tabConnectuins.getTabs().size(); tabIterator++) {
                                    if(tab.equals(tabConnectuins.getTabs().get(tabIterator))) {
                                        tab.setText(connections.get(tabIterator).getConnectionHeaderName());
                                    }
                                }
                                editTabHeader.setEditHeader(tab.getText());
                                editHeaderStage.show();
                                editHeaderStage.setOnHiding(event -> {
                                    for(int tabIterator=0; tabIterator<tabConnectuins.getTabs().size(); tabIterator++) {
                                        if(tab.equals(tabConnectuins.getTabs().get(tabIterator))) {
                                            connections.get(tabIterator).setConnectionHeaderName(editTabHeader.getEditHeader());
                                            tab.setText(connections.get(tabIterator).getConnectionHeaderName());
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

                if(isFirstConnetion) {
                    tabConnectuins.getTabs().remove(0);
                }
                SingleSelectionModel<Tab> selectionModel = tabConnectuins.getSelectionModel();
                selectionModel.select(connections.size()-1); //select by index starting with 0
                selectionModel.clearSelection(); //clear your selection
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error opening port @" + ex.toString());
                alert.showAndWait();
                System.err.print(ex);
            }
        paramConnAvailableBinding.setValue(connections.isEmpty());
    }

    public void onButtonDisconnect() {
        tabConnectuins.getTabs().remove(connections.size()-1);
        connections.get(connections.size()-1).close();
        connections.remove(connections.size()-1);
        paramConnAvailableBinding.setValue(connections.isEmpty());
        if(connections.isEmpty()) {
            Tab tab1 = new Tab();
            tabConnectuins.getTabs().addAll(tab1);
        }
    }

    public void onButtonFlush() {

    }

    public void onButtonSettings() {

    }

    public void onMenuClose() {

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
//
//    public class updateConnection implements ActionListener {
////        Scanner scanner = null;
//        public updateConnection() {
////            this.scanner = inScanner;
//        }
//        public void actionPerformed(ActionEvent event) {
//            int index = 0;
//            for(Connection con:connections) {
//                if(con != null) {
//                    String header = con.getConnectionName();
//                    tabConnectuins.getTabs().get(index).setText("-fx-text-base-color: green;");
////                    tab.setText(header);
////                    tabConnectuins.getTabs().get(index).textProperty().setValue(header);
//                }
//                index++;
//            }
//            ArrayList<String> result = scanner.stickMaker.getPrintStickerInformation();
//            log_text_field.setText(null);
//            if(result.size() != 0) {
//                for(int counter = 0; counter < result.size(); counter++) {
//                    log_text_field.insertText(0, result.get(counter).toString());
//                    counter++;
//                }
//            }
//            if(scanner.stickMaker.availablePrintSticker()) {
//                stiker_no_ready.set(false);
//            } else {
//                stiker_no_ready.set(true);
//            }
//        }
//    }
//}