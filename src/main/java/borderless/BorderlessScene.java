
package borderless;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class BorderlessScene extends Scene {
	private BorderlessController controller;
	private AnchorPane root;
	private Stage stage;

	public BorderlessScene(Stage stage, StageStyle stageStyle, Parent sceneRoot, double minWidth, double minHeight) {
		super(new Pane());
		try {
			
			// Load the FXML
			FXMLLoader loader = new FXMLLoader();
			System.out.println(getClass().getClassLoader().getResource("fxml/Borderless.fxml"));
			loader.setLocation(getClass().getClassLoader().getResource("fxml/Borderless.fxml"));
			this.root = loader.load();
			
			// Set Scene root
			setRoot(this.root);
			setContent(sceneRoot);
			
			// Initialize the Controller
			this.controller = loader.getController();
			this.controller.setStage(stage);
			this.controller.createTransparentWindow(stage);
			
			// StageStyle
			stage.initStyle(stageStyle);
			this.stage = stage;
			
			// minSize
			stage.setMinWidth(minWidth);
			stage.setMinHeight(minHeight);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setContent(Parent content) {
		this.root.getChildren().remove(0);
		this.root.getChildren().add(0, content);
		AnchorPane.setLeftAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setTopAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setRightAnchor(content, Double.valueOf(0.0D));
		AnchorPane.setBottomAnchor(content, Double.valueOf(0.0D));
	}

	public void setMoveControl(Node node) {
		this.controller.setMoveControl(node);
	}

	public void maximizeStage() {
		controller.maximize();
	}

	public void minimizeStage() {
		controller.minimize();
	}

	public void setResizable(Boolean bool) {
		controller.setResizable(bool);
	}

	public ReadOnlyBooleanProperty maximizedProperty() {
		return controller.maximizedProperty();
	}

	public boolean isMaximized() {
		return controller.maximizedProperty().get();
	}

	public Delta getWindowedSize() {
		if (controller.prevSize.x == null)
			controller.prevSize.x = stage.getWidth();
		if (controller.prevSize.y == null)
			controller.prevSize.y = stage.getHeight();
		return controller.prevSize;
	}

	public Delta getWindowedPositon() {
		if (controller.prevPos.x == null)
			controller.prevPos.x = stage.getX();
		if (controller.prevPos.y == null)
			controller.prevPos.y = stage.getY();
		return controller.prevPos;
	}

	public void removeDefaultCSS() {
		
		this.root.getStylesheets().remove(0);
		
	}

	public void setTransparentWindowStyle(String style) {
		controller.getTransparentWindow().setStyle("");
		controller.getTransparentWindow().setStyle(style);
	}
	
}
