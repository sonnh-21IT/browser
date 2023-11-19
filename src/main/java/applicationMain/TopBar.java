
package applicationMain;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import tools.InfoTool;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopBar extends BorderPane {
	@FXML
	private Label label;
	private Logger logger = Logger.getLogger(getClass().getName());
	public TopBar() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(InfoTool.FXMLS + "TopBar.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "", ex);
		}
	}
	@FXML
	private void initialize() {
		setRight(new CloseAppBox());
	}
	public void setTitle(String title) {
		label.setText(title);
	}
}
