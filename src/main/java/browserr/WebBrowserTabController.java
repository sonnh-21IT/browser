package browserr;

import marquee.FXMarquee;
import tools.InfoTool;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
import org.apache.commons.validator.routines.UrlValidator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
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
	private JFXButton zoomIn;
	@FXML
	private JFXButton zoomOut;
	@FXML
	private TextField searchBar;
	@FXML
	private JFXButton copyText;
	@FXML
	private JFXButton goButton;
	@FXML
	private CheckMenuItem movingTitleAnimation;
	@FXML
	private WebView webView;
	WebEngine browser;
	private WebHistory history;
	private ObservableList<Entry> historyEntryList;
	private final Tab tab;
	private String firstWebSite;
	private double countClicked = 1;
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

		//Thêm sự kiện lắng nghe đến WebEngine
		browser.getLoadWorker().stateProperty().addListener((observable , oldState , newState) -> {
			if (newState == State.SUCCEEDED) {
				errorPane.setVisible(false);
				
			} else if (newState == State.FAILED) {
				errorPane.setVisible(true);
			}
		});
		
		browser.setOnError(error -> {
			System.out.println("WebEngine error occurred");
			checkForInternetConnection();
		});
		
		//handle pop up windows
		browser.setCreatePopupHandler(l -> webBrowserController.createAndAddNewTab().getWebView().getEngine());
		//Lịch sử
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

		//Duyệt web dựa trên search bar
		browser.getLoadWorker().runningProperty().addListener((observable , oldValue , newValue) -> {

			if (!newValue) // Nếu không chạy
				searchBar.textProperty().unbind();
			else
				searchBar.textProperty().bind(browser.locationProperty());
		});
		searchBar.setOnAction(a -> loadWebSite(searchBar.getText()));
		searchBar.focusedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue)
				Platform.runLater(() -> searchBar.selectAll());

		});

		//Duyệt web
		goButton.setOnAction(searchBar.getOnAction());

		zoomIn.setOnAction(a -> {
			if (countClicked <= 5) {
				countClicked += 0.5;
			}
			zoomInWebsite(countClicked);
		});

		zoomOut.setOnAction(a -> {
			if (countClicked >= 1) {
				countClicked -= 0.5;
			}
			zoomOutWebsite(countClicked);
		});

		homeButton.setOnAction(a -> reloadWebSite());

		//Load lại trang
		reloadButton.setOnAction(a -> reloadWebSite());

		//Lui về trang sau
		backwardButton.setOnAction(a -> goBack());
		backwardButton.disableProperty().bind(getHistory().currentIndexProperty().isEqualTo(0));
		backwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() - 1).getUrl()).getTab());
		});

		//Tiến về trang trước
		forwardButton.setOnAction(a -> goForward());
		forwardButton.disableProperty().bind(getHistory().currentIndexProperty().greaterThanOrEqualTo(list.sizeProperty().subtract(1)));
		forwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() + 1).getUrl()).getTab());
		});

		loadWebSite(firstWebSite);
	}

	public void zoomInWebsite(double number) {
		webView.setZoom(number);
	}
	public void zoomOutWebsite(double number) {
		webView.setZoom(number);
	}
	private void loadWebSite(String webSite) {

		//Tìm kiếm nếu như có URL
		String load = !new UrlValidator().isValid(webSite) ? null : webSite;

		//Load
		try {

			//First Part
			String finalWebsiteFristPart = ( load != null ) ? load : "https://www.google.com";

			//Second Part
			String finalWebsiteSecondPart = "";
			if (searchBar.getText().isEmpty())
				finalWebsiteSecondPart = "";
			else {
				finalWebsiteSecondPart = "//search?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
			}

			//Load trang web
			browser.load(finalWebsiteFristPart + finalWebsiteSecondPart);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}

		webView.setZoom(1);

	}

	public void loadDefaultWebSite() {
		browser.load("https://www.google.com");
		webView.setZoom(1);
	}
	public void reloadWebSite() {
		if (!getHistory().getEntries().isEmpty()) {
			browser.reload();
			webView.setZoom(1);
		}
		else{
			loadDefaultWebSite();
		}
	}
	public void goBack() {
		webView.setZoom(1);
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() > 0 ? -1 : 0);
	}
	public void goForward() {
		webView.setZoom(1);
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

	//Kiểm tra kết nối mạng
	void checkForInternetConnection() {
		//tryAgainIndicator
		tryAgainIndicator.setVisible(true);

		//Kiểm tra kết nối mạng
		Thread thread = new Thread(() -> {
			boolean hasInternet = InfoTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {
				//Hộp lỗi
				errorPane.setVisible(!hasInternet);

				tryAgainIndicator.setVisible(false);
				//Load lại web nếu như có mạng
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
