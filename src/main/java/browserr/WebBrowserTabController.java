package browserr;

import marquee.FXMarquee;
import tools.InfoTool;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;
import net.sf.image4j.codec.ico.ICODecoder;
import org.apache.commons.validator.routines.UrlValidator;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebBrowserTabController extends StackPane {

	private final Logger logger = Logger.getLogger(getClass().getName());
	@FXML
	private VBox errorPane;
	@FXML
	private JFXButton tryAgain;
	@FXML
	private ProgressIndicator tryAgainIndicator;
	@FXML
	private BorderPane borderPane;
	@FXML
	private JFXButton backwardButton;
	@FXML
	private JFXButton reloadButton;
	@FXML
	private JFXButton forwardButton;
	@FXML
	private JFXButton homeButton;
	@FXML
	private TextField searchBar;
	@FXML
	private JFXButton copyText;
	@FXML
	private JFXButton goButton;
	@FXML
	private JFXButton openInDefaultBrowser;
	@FXML
	private ToggleGroup searchEngineGroup;
	@FXML
	private CheckMenuItem movingTitleAnimation;
	@FXML
	private MenuItem printPage;
	@FXML
	private MenuItem notebookpage;
	@FXML
	private MenuItem findinpange;
	@FXML
	private MenuItem downloadPage;
	@FXML
	private CheckMenuItem cookieStorage;
	@FXML
	private WebView webView;
	WebEngine browser;
	private WebHistory history;
	private ObservableList<Entry> historyEntryList;
	
	private final Tab tab;
	private String firstWebSite;
	
	private final WebBrowserController webBrowserController;
	
	private final ImageView facIconImageView = new ImageView();

	public WebBrowserTabController(WebBrowserController webBrowserController, Tab tab, String firstWebSite) {
		this.webBrowserController = webBrowserController;
		this.tab = tab;
		this.firstWebSite = firstWebSite;
		this.tab.setContent(this);

		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(InfoTool.FXMLS + "WebBrowserTabController.fxml"));
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

		tryAgain.setOnAction(a -> checkForInternetConnection());
		
		//-------------------WebEngine------------------------
		browser = webView.getEngine();
		browser.getLoadWorker().exceptionProperty().addListener(error -> {
			System.out.println("WebEngine exception occured" + error.toString());
			checkForInternetConnection();
		});
		//Add listener to the WebEngine
		browser.getLoadWorker().stateProperty().addListener((observable , oldState , newState) -> {
			if (newState == State.SUCCEEDED) {
				errorPane.setVisible(false);
				
			} else if (newState == State.FAILED) {
				errorPane.setVisible(true);
			}
		});
		
		browser.setOnError(error -> {
			System.out.println("WebEngine error occured");
			checkForInternetConnection();
		});
		
		//handle pop up windows
		browser.setCreatePopupHandler(l -> webBrowserController.createAndAddNewTab().getWebView().getEngine());
		//History
		setHistory(browser.getHistory());
		historyEntryList = getHistory().getEntries();
		SimpleListProperty<Entry> list = new SimpleListProperty<>(historyEntryList);

		tab.setTooltip(new Tooltip(""));
		tab.getTooltip().textProperty().bind(browser.titleProperty());

		StackPane stack = new StackPane();

		ProgressBar indicator = new ProgressBar();
		indicator.progressProperty().bind(browser.getLoadWorker().progressProperty());
		indicator.visibleProperty().bind(browser.getLoadWorker().runningProperty());
		indicator.setMaxSize(30, 11);

		Label label = new Label();
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setAlignment(Pos.CENTER);
		label.setStyle("-fx-background-color:#202020; -fx-font-weight:bold; -fx-text-fill: white; -fx-font-size:10;");
		label.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100).asString("%.00f %%"));
		
		FXMarquee marquee = new FXMarquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		
		stack.getChildren().addAll(indicator, label);
		stack.setManaged(false);
		stack.setVisible(false);
		
		// stack
		indicator.visibleProperty().addListener(l -> {
			if (indicator.isVisible()) {
				stack.setManaged(true);
				stack.setVisible(true);
			} else {
				stack.setManaged(false);
				stack.setVisible(false);
			}
		});

		facIconImageView.setFitWidth(25);
		facIconImageView.setFitHeight(25);
		facIconImageView.setSmooth(true);

		Label iconLabel = new Label();
		iconLabel.setGraphic(facIconImageView);
		iconLabel.setStyle("-fx-background-color:#202020");
		iconLabel.visibleProperty().bind(indicator.visibleProperty().not());
		iconLabel.managedProperty().bind(facIconImageView.imageProperty().isNotNull().and(indicator.visibleProperty().not()));

		JFXButton closeButton = new JFXButton("X");
		int maxSize = 25;
		closeButton.setMinSize(maxSize, maxSize);
		closeButton.setPrefSize(maxSize, maxSize);
		closeButton.setMaxSize(maxSize, maxSize);
		closeButton.setStyle("-fx-background-radius:0; -fx-font-size:8px");
		closeButton.setOnAction(a -> this.webBrowserController.removeTab(tab));

		HBox hBox = new HBox();
		hBox.setOnMouseClicked(m -> {
			if (m.getButton() == MouseButton.MIDDLE)
				this.webBrowserController.removeTab(tab);
		});
		hBox.getChildren().addAll(iconLabel, stack, marquee, closeButton);
		tab.setGraphic(hBox);

		tab.setContextMenu(new WebBrowserTabContextMenu(this, webBrowserController));

	}
	public String getSearchEngineHomeUrl(String searchProvider) {
		switch (searchProvider.toLowerCase()) {
			case "bing":
				return "http://www.bing.com";
			case "duckduckgo":
				return "https://duckduckgo.com";
			case "yahoo":
				return "https://search.yahoo.com";
			default:
				return "https://www.google.com";
		}
	}

	public String getSelectedEngineHomeUrl() {
		return getSearchEngineHomeUrl( ( (RadioMenuItem) searchEngineGroup.getSelectedToggle() ).getText());
	}

	private void loadWebSite(String webSite) {
		String load = !new UrlValidator().isValid(webSite) ? null : webSite;

		try {

			String finalWebsiteFristPart = ( load != null ) ? load : getSelectedEngineHomeUrl();

			String finalWebsiteSecondPart = "";
			if (searchBar.getText().isEmpty())
				finalWebsiteSecondPart = "";
			else {
				switch ( ( (RadioMenuItem) searchEngineGroup.getSelectedToggle() ).getText()) {
					case "bing":
					case "duckduckgo":
						finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
					case "yahoo": //I need to find a solution for this
						finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
					default: //then google
						finalWebsiteSecondPart = "//search?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
				}

			}

			browser.load(finalWebsiteFristPart + finalWebsiteSecondPart);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}

	}

	public void loadDefaultWebSite() {
		browser.load(getSelectedEngineHomeUrl());
	}
	public void reloadWebSite() {
		if (!getHistory().getEntries().isEmpty())
			browser.reload();
		else
			loadDefaultWebSite();
	}
	public void goBack() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() > 0 ? -1 : 0);
	}
	public void goForward() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() < historyEntryList.size() - 1 ? 1 : 0);
	}
	public WebView getWebView() {
		return webView;
	}

	public Tab getTab() {
		return tab;
	}
	public VBox getErrorPane() {
		return errorPane;
	}

	void checkForInternetConnection() {

		//tryAgainIndicator
		tryAgainIndicator.setVisible(true);

		//Check for Internet connection
		Thread thread = new Thread(() -> {
			boolean hasInternet = InfoTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {

				//Visibility of error pane
				errorPane.setVisible(!hasInternet);

				//Visibility of Try Again Indicator
				tryAgainIndicator.setVisible(false);

				//Reload the Website if it has Internet
				if (hasInternet)
					reloadWebSite();
			});
		}, "Internet Connection Tester Thread");
		thread.setDaemon(true);
		thread.start();
	}

	public WebHistory getHistory() {
		return history;
	}

	public void setHistory(WebHistory history) {
		this.history = history;
	}

	public void setMovingTitleEnabled(boolean value) {
		movingTitleAnimation.setSelected(value);
	}
}
