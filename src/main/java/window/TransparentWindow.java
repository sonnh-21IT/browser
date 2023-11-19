package window;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransparentWindow extends StackPane {
	@FXML
	private StackPane stackPane;
	private Logger logger = Logger.getLogger(getClass().getName());

	private Stage window = new Stage();

	public enum ConsoleTab {
		CONSOLE, SPEECH_RECOGNITION;
	}
	public TransparentWindow() {
		
		// ------------------------------------FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransparentWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
		//Window
		window.setTitle("Transparent Window");
		window.initStyle(StageStyle.TRANSPARENT);
		window.initModality(Modality.NONE);
		window.setScene(new Scene(this, Color.TRANSPARENT));
	}
	@FXML
	private void initialize() {
		
	}

	public Stage getWindow() {
		return window;
	}

	public void close() {
		window.close();
	}

	public void show() {
		if (!window.isShowing())
			window.show();
		else
			window.requestFocus();
	}
	
}
