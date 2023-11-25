package browserr;

import tools.InfoTool;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebBrowserController extends StackPane {
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public static boolean MOVING_TITLES_ENABLED = true;

    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXButton youtube;
    @FXML
    private JFXButton facebook;
    @FXML
    private JFXButton gmail;
    @FXML
    private JFXButton googleMaps;
    @FXML
    private JFXButton addTab;

	public WebBrowserController() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(InfoTool.FXMLS + "WebBrowserController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	@FXML
	private void initialize() {

		tabPane.getTabs().clear();
		createAndAddNewTab();

		addTab.setOnAction(a -> createAndAddNewTab());

		youtube.setOnAction(a -> createTabAndSelect("https://www.youtube.com/"));
		
		facebook.setOnAction(a -> createTabAndSelect("https://www.facebook.com"));

		gmail.setOnAction(a -> createTabAndSelect("https://www.gmail.com"));
		
		googleMaps.setOnAction(a -> createTabAndSelect("https://maps.google.com/"));
		
	}

	public void createTabAndSelect(String url) {
		tabPane.getSelectionModel().select(createAndAddNewTab(url).getTab());
	}


	public WebBrowserTabController createAndAddNewTab(String... webSite) {

		WebBrowserTabController webBrowserTab = createNewTab(webSite);

		tabPane.getTabs().add(webBrowserTab.getTab());

		return webBrowserTab;
	}

	public WebBrowserTabController createNewTab(String... webSite) {

		Tab tab = new Tab("");
		WebBrowserTabController webBrowserTab = new WebBrowserTabController(this, tab, webSite.length == 0 ? null : webSite[0]);
		tab.setOnClosed(c -> {

			if (tabPane.getTabs().isEmpty())
				createAndAddNewTab();

			webBrowserTab.browser.load("about:blank");
//			java.net.CookieHandler.setDefault(new java.net.CookieManager());

		});

		return webBrowserTab;
	}
	public void closeTabsToTheRight(Tab givenTab) {
		if (tabPane.getTabs().size() <= 1)
			return;
		int start = tabPane.getTabs().indexOf(givenTab);

		tabPane.getTabs().stream()
				.filter(tab -> tabPane.getTabs().indexOf(tab) > start)
				.collect(Collectors.toList()).forEach(this::removeTab);
		
	}

	public void closeTabsToTheLeft(Tab givenTab) {
		//Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;
		
		//The start
		int start = tabPane.getTabs().indexOf(givenTab);
		
		//Remove the appropriate items
		tabPane.getTabs().stream()
				//filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) < start)
				//Collect the all to a list
				.collect(Collectors.toList()).forEach(this::removeTab);
		
	}

	public void removeTab(Tab tab) {
		tabPane.getTabs().remove(tab);
		tab.getOnClosed().handle(null);
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void setMovingTitlesEnabled(boolean value) {
		MOVING_TITLES_ENABLED = value;
		tabPane.getTabs().forEach(tab -> ( (WebBrowserTabController) tab.getContent() ).setMovingTitleEnabled(value));
	}
	
}
