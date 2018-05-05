import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.util.Duration;
import java.awt.Color;

public class ConnectionDataAnimationStyle {
    private ObjectProperty<Color> colorStyle = new SimpleObjectProperty<>(Color.decode("#CFD0D7"));
    private Timeline animation;
    // string со стилем, который меняется
    private StringProperty colorStringProperty = createWarningColorStringProperty(colorStyle);

    public ConnectionDataAnimationStyle(Tab tab) {
        createAnimation(tab, colorStyle, colorStringProperty);
    }

    // когда цвет меняется
    private StringProperty createWarningColorStringProperty(final ObjectProperty<Color> warningColor) {
        final StringProperty colorStringProperty = new SimpleStringProperty();
        setColorStringFromColor(colorStringProperty, warningColor);
        warningColor.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observableValue, Color oldColor, Color newColor) {
                setColorStringFromColor(colorStringProperty, warningColor);
            }
        });
        return colorStringProperty;
    }

    // вызывается для изменения цвета стиля
    private void setColorStringFromColor(StringProperty colorStringProperty, ObjectProperty<Color> color) {
        colorStringProperty.set( "rgba("
                        + ((int) (color.get().getRed())) + ","
                        + ((int) (color.get().getGreen())) + ","
                        + ((int) (color.get().getBlue())) + ")"
        );
    }

    private void createAnimation(Tab tab, final ObjectProperty<Color> warningColor, StringProperty colorStringProperty) {
        tab.styleProperty().bind(
                new SimpleStringProperty("-fx-base: ")
                        .concat(colorStringProperty)
                        .concat(";")
        );
        animation = new Timeline(
                new KeyFrame(Duration.seconds(0),   new KeyValue(warningColor, Color.decode("#A9E4FF"), Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(0.10),   new KeyValue(warningColor, Color.decode("#A9E4FF"), Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(0.15),   new KeyValue(warningColor, Color.decode("#CFD0D7"),  Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(0.20),   new KeyValue(warningColor, Color.decode("#CFD0D7"),  Interpolator.LINEAR))
        );
    }

    public void startAnimation() {
        animation.setCycleCount(5);
        animation.setAutoReverse(true);
        animation.play();
    }
}
