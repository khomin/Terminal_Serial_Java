//package sample.pojo;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.arrays.ArrayData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionData {
    @FXML
    public TextArea receiveData;
    @FXML
    public TextField sendData;
    @FXML
    public Button sendButton;

    private StringProperty sendDataProperty = new SimpleStringProperty("");
    BlockingQueue<String> rxDataQueue = new LinkedBlockingQueue<>();

    @FXML
    public void initialize() {
        sendData.textProperty().bindBidirectional(sendDataProperty);
        sendButton.disableProperty().bind(sendDataProperty.isEmpty());

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater(() -> new MessageConsumer(rxDataQueue, receiveData, rxDataQueue.size()).start());
                while(true) {
                    if(rxDataQueue.size() != 0) {
                        rxDataQueue.put(rxDataQueue.take().toString());
                        rxDataQueue.remove(0);
                    }
                    Thread.sleep(100);
                }
            }
        };
        new Thread(task).start();
    }

    public class MessageConsumer extends AnimationTimer {
        private final BlockingQueue<String> messageQueue ;
        private final TextArea textArea ;
        private int messagesReceived = 0 ;
        public MessageConsumer(BlockingQueue<String> messageQueue, TextArea textArea, int numMessages) {
            this.messageQueue = messageQueue ;
            this.textArea = textArea ;
        }
        @Override
        public void handle(long now) {
            List<String> messages = new ArrayList<>();
            messagesReceived += messageQueue.drainTo(messages);
            messages.forEach(msg -> textArea.appendText(msg));
        }
    }

    public Button getPropertySendButton() {
        return sendButton;
    }

    public String getSendDataProperty() {
        String sendBuff = sendDataProperty.get();
        sendDataProperty.set("");
        return sendBuff;
    }

    public void clearReceiveData() {
        receiveData.textProperty().setValue("");
    }

    public void setReceiveData(byte[] buffer) {
        try {
            rxDataQueue.add(new String(buffer, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            System.err.print(ex);
        }
    }
}