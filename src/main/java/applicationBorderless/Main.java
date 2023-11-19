package applicationBorderless;

import borderless.BorderlessScene;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	
	MainWindowController mainWindowController = new MainWindowController();
	public static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;

		BorderlessScene borderlessScene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED, mainWindowController, 250, 250);

		mainWindowController.setBorderlessScene(borderlessScene);
		mainWindowController.initActions();

		primaryStage.setTitle("Draggable and Undecorated JavaFX Window");
		primaryStage.setScene(borderlessScene);
		primaryStage.setWidth(900);
		primaryStage.setHeight(500);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
