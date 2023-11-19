package browserr;

import tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebBrowserTabContextMenu extends ContextMenu {

	@FXML
	private MenuItem newTab;
	@FXML
	private MenuItem reloadTab;
	@FXML
	private MenuItem closeOtherTabs;
	@FXML
	private MenuItem closeTabsRight;
	@FXML
	private MenuItem closeTabsLeft;
	@FXML
	private MenuItem closeTab;
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final WebBrowserTabController webBrowserTabController;
	private final WebBrowserController webBrowserController;

	public WebBrowserTabContextMenu(WebBrowserTabController webBrowserTabController, WebBrowserController webBrowserController) {
		this.webBrowserTabController = webBrowserTabController;
		this.webBrowserController = webBrowserController;
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(InfoTool.FXMLS + "WebBrowserTabContextMenu.fxml"));
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

		newTab.setOnAction(a -> webBrowserController.createAndAddNewTab());

		reloadTab.setOnAction(a -> webBrowserTabController.reloadWebSite());

		closeTabsRight.setOnAction(a -> webBrowserController.closeTabsToTheRight(webBrowserTabController.getTab()));

		closeTabsLeft.setOnAction(a -> webBrowserController.closeTabsToTheLeft(webBrowserTabController.getTab()));

		closeOtherTabs.setOnAction(a -> {
			webBrowserController.closeTabsToTheLeft(webBrowserTabController.getTab());
			webBrowserController.closeTabsToTheRight(webBrowserTabController.getTab());
		});

		closeTab.setOnAction(a -> webBrowserController.removeTab(webBrowserTabController.getTab()));
		
	}
}
