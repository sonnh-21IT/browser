package marquee;

import tools.InfoTool;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


import java.io.IOException;
public class FXMarquee extends Pane {
	
	@FXML
	private Label label;
	private static final double OFFSET = 5;
	
	private Timeline timeline = new Timeline();
	
	private boolean animationAllowed = true;

	public FXMarquee() {
		
		// FXMLLOADER
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(InfoTool.FXMLS + "FXMarquee.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

	@FXML
	private void initialize() {
		
		//Clip
		Rectangle rectangle = new Rectangle(25, 25);
		rectangle.widthProperty().bind(widthProperty());
		rectangle.heightProperty().bind(heightProperty());
		setClip(rectangle);
		
		// Text
		//text.setManaged(false)
		
		startAnimation();
	}
	public FXMarquee setText(String value) {
		
		// text
		label.setText(value);
		
		return this;
	}

	public StringProperty textProperty() {
		return label.textProperty();
	}

	private final void startAnimation() {
		KeyFrame updateFrame = new KeyFrame(Duration.millis(35), new EventHandler<ActionEvent>() {
			
			private boolean rightMovement;
			
			@Override
			public void handle(ActionEvent event) {
				double textWidth = label.getLayoutBounds().getWidth();
				double paneWidth = getWidth();
				double layoutX = label.getLayoutX();
				
				if (2 * OFFSET + textWidth <= paneWidth && layoutX >= OFFSET) {
					// stop, if the pane is large enough and the position is
					// correct
					label.setLayoutX(OFFSET);
					timeline.stop();
				} else {
					if ( ( rightMovement && layoutX >= OFFSET ) || ( !rightMovement && layoutX + textWidth + OFFSET <= paneWidth )) {
						// invert movement, if bounds are reached
						rightMovement = !rightMovement;
					}
					// update position
					if (rightMovement) {
						layoutX += 1;
					} else {
						layoutX -= 1;
					}
					label.setLayoutX(layoutX);
				}
			}
		});
		timeline.getKeyFrames().add(updateFrame);
		timeline.setCycleCount(Animation.INDEFINITE);
		
		// listen to bound changes of the elements to start/stop the
		// animation
		InvalidationListener listener = o -> checkAnimationValidity(animationAllowed);
		
		label.layoutBoundsProperty().addListener(listener);
		widthProperty().addListener(listener);
		
	}

	public void checkAnimationValidity(boolean continueAnimation) {
		animationAllowed = continueAnimation;
		if (animationAllowed) {
			double textWidth = label.getLayoutBounds().getWidth();
			double paneWidth = getWidth();
			label.setLayoutX(5);
			if (textWidth + 2 * OFFSET > paneWidth && timeline.getStatus() != Animation.Status.RUNNING)
				timeline.play();
		} else {
			label.setLayoutX(OFFSET);
			timeline.stop();
		}
	}

	public Label getLabel() {
		return label;
	}
	
}
