
package applicationMain;

import borderless.BorderlessScene;
import browserr.WebBrowserController;
import tools.InfoTool;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	
	public static Stage window;
	public static TopBar topBar;
	
	public static BorderPane root;
	
	public static BorderlessScene borderlessScene;
	
	private final int screenMinWidth = 800 , screenMinHeight = 600;
	
	@Override
	public void start(Stage primaryStage) {

		topBar = new TopBar();

		root = new BorderPane();
		root.setStyle("-fx-background-color:#202020");
		root.setTop(topBar);
		root.setCenter(new WebBrowserController());

		window = primaryStage;
		window.setTitle("JavaFX Web Browser");
		window.setWidth(getVisualScreenWidth() * 0.9);
		window.setHeight(getVisualScreenHeight() * 0.9);
		window.centerOnScreen();
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("logo.png"));
		window.centerOnScreen();
		window.setOnCloseRequest(cl -> System.exit(0));
		

		System.out.println(getClass().getClassLoader().getResource("css/application.css"));
		borderlessScene = new BorderlessScene(window, StageStyle.UNDECORATED, root, screenMinWidth, screenMinHeight);
		borderlessScene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
		borderlessScene.setMoveControl(topBar);
		window.setScene(borderlessScene);
		
		window.show();
		
	}

	public static double getVisualScreenWidth() {
		return Screen.getPrimary().getVisualBounds().getWidth();
	}

	public static double getVisualScreenHeight() {
		return Screen.getPrimary().getVisualBounds().getHeight();
	}

	public static void main(String[] args) {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		launch(args);
	}
}
