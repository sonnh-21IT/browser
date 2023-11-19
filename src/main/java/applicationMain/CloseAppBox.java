package applicationMain;

import tools.InfoTool;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class CloseAppBox extends StackPane {

	@FXML
	private JFXButton minimize;
	@FXML
	private JFXButton maxOrNormalize;
	@FXML
	private JFXButton exitApplication;

	public CloseAppBox() {

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(InfoTool.FXMLS + "CloseAppBox.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

	@FXML
	private void initialize() {

		minimize.setOnAction(ac -> Main.window.setIconified(true));
		maxOrNormalize.setOnAction(ac -> Main.borderlessScene.maximizeStage());
		exitApplication.setOnAction(ac -> System.exit(0));
		
	}
	
}
