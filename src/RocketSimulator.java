import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import rocket.UserControlledRocket;
import util.Vector2D;
import userinterface.CustomButton;
import userinterface.TogglePlayButton;
import userinterface.UserInterface;
import world.World;
import rocket.Rocket;
import design.ColorPalette;
import util.KeyboardHandler;

public class RocketSimulator extends Application {

	private final int WIDTH = 1280;
	private final int HEIGHT = 720;

	private Group root;
	private Stage primaryStage;
	private Scene simulationScene;
	private GraphicsContext gc;
	private AnimationTimer animator;

	private UserInterface userInterface;
	
	private KeyboardHandler keyboardHandler;
	
	private MenuManager menuManager = new MenuManager(WIDTH, HEIGHT);
	
	private double maxSpeed = 250;
	private double initialFuel = 10;
	private double initialRocketHeight = 500;

	private UserControlledRocket userRocket;
	private World world;
	private boolean landingHandled = false;

	private Group landingSummary;

	private ColorPalette palette = ColorPalette.EARTH; // default color palette

	ObservableList<ColorPalette> paletteOptions =
    			FXCollections.observableArrayList(
					ColorPalette.EARTH,
					ColorPalette.MARS,
					ColorPalette.NIGHT
	);

	@Override
	public void init() {
		
		root = new Group();

		// Create the World and center the camera on its Rocket
		world = new World(WIDTH, HEIGHT, getPalette());
		world.setCenterOnRocketHorizontally(true);
		world.setCenterOnRocketVertically(true);
		
		// Initialize a rocket so that keyboard handling can be configured
		double rocketX = WIDTH  / 2;
		userRocket = new UserControlledRocket(rocketX, 
			world.getGroundY() - getInitialRocketHeight(), 
			getInitialFuel(), 
			world.getGroundY());
		world.setPrimaryRocket(userRocket);
		keyboardHandler = new KeyboardHandler(userRocket);
		
		animator = new AnimationTimer() {
			
			long startTime;

			private long lastUpdate;

			@Override
			public void start() {
				
				startTime = System.nanoTime();
				lastUpdate = startTime;
				super.start();

			}
			
			public void clearScreen(GraphicsContext gc) {
				
				gc.clearRect(-gc.getTransform().getTx(), -gc.getTransform().getTy(),
						WIDTH, HEIGHT);
				
			}
			
			@Override
			public void handle(long now) {

				// SIMULATION LOOP

				clearScreen(gc);

				double timeSinceLastUpdateSeconds = (now - lastUpdate) / 1_000_000_000.0;
				
				world.draw(gc);
				userInterface.draw(gc);
				
				if (shouldUpdateSimulator()) {
					world.tick(timeSinceLastUpdateSeconds);
				}
				
				if (!world.getPrimaryRocket().isAirborne() && !isLandingHandled()) {

					/*
						If a Rocket just landed, show the landing summary
						and mark the landing as handled
					*/
					landingSummary = getMenuManager().getLandingSummary();
					root.getChildren().add(landingSummary);
					getUserInterface().getTimeIndicator().setForcePaused(true);
					setLandingHandled(true);
					
				}
				userInterface.tick(timeSinceLastUpdateSeconds);
				
				lastUpdate = now;

			}
		};
		
		userInterface = new UserInterface(0, 0, 100, HEIGHT, 
			userRocket, 
			world.getGroundY(),
			getInitialRocketHeight());
		
	}

	/**
	 * Reset objects so that everything is cleared for a new simulation.
	 */
	public void resetConfiguration() {

		world.getObjects().clear();
		getUserInterface().reset();
		if (root.getChildren().contains(landingSummary)) {
			root.getChildren().remove(landingSummary);
		}

	}

	@Override
	public void start(Stage stage) throws Exception {

		setPrimaryStage(stage);
		getPrimaryStage().setTitle("Rocket Simulator");

		setSimulationScene(new Scene(root, WIDTH, HEIGHT));

		getPrimaryStage().setWidth(WIDTH); 
		getPrimaryStage().setHeight(HEIGHT);

		Canvas canvas = new Canvas(getPrimaryStage().getWidth(), getPrimaryStage().getHeight());
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		
		getPrimaryStage().setScene(getSimulationScene());

		getMenuManager().showTitleScreen(getPrimaryStage());

		getPrimaryStage().show();

		addKeyboardHandling(getSimulationScene());

		// These buttons should be added last so they can receive events
		for (CustomButton button : getUserInterface().getButtons()) {

			root.getChildren().add(button);

		}
	}

	private void addKeyboardHandling(Scene scene) {
		scene.setOnKeyPressed(keyboardHandler);
		scene.setOnKeyReleased(keyboardHandler);
	}

	private GraphicsContext getGraphicsContext() {
		return gc;
	}

	private AnimationTimer getAnimator() {
		return animator;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public Scene getSimulationScene() {
		return this.simulationScene;
	}

	public void setSimulationScene(Scene simulationScene) {
		this.simulationScene = simulationScene;
	}

	private UserInterface getUserInterface() {
		return userInterface;
	}

	public double getMaxSpeed() {
		return this.maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getInitialFuel() {
		return this.initialFuel;
	}

	public void setInitialFuel(double initialFuel) {
		this.initialFuel = initialFuel;
	}

	public double getInitialRocketHeight() {
		return this.initialRocketHeight;
	}

	public void setInitialRocketHeight(double initialRocketHeight) {
		this.initialRocketHeight = initialRocketHeight;
	}

	public void setLandingHandled(boolean landingHandled) {
		this.landingHandled = landingHandled;
	}

	public boolean isLandingHandled() {
		return landingHandled;
	}

	private boolean shouldUpdateSimulator() {
		return getUserInterface().getTogglePlayButton().getState().equals("PAUSE");
	}

	private MenuManager getMenuManager() {
		return menuManager;
	}

	public ColorPalette getPalette() {
		return this.palette;
	}

	public void setPalette(ColorPalette palette) {
		this.palette = palette;
		world.setPalette(palette);
	}

	public static void main(String[] args) {

		launch(args);

	}

	/**
	 * A class for serving user menus before and after simulations are started.
	 */
	private class MenuManager {
	
		private int width;
		private int height;
	
		// Hex code for the menu's background color
		private String backgroundColorHex = "#000000";
		private String buttonColorHex = "#A3A3A3";
		private String textColorHex = "#3AF38F";

		private String headingFontFamily = "Tahoma";
		private int headingFontSize = 50;
		private Font headingFont = Font.font(headingFontFamily, FontWeight.BOLD, FontPosture.REGULAR, headingFontSize);

		private String subheadingFontFamily = "Arial";
		private int subheadingFontSize = 26;
		private Font subheadingFont = Font.font(subheadingFontFamily, FontWeight.THIN, FontPosture.ITALIC, subheadingFontSize);

		private String buttonFontFamily = "Tahoma";
		private int buttonFontSize = 18;
		private Font buttonFont = Font.font(buttonFontFamily, FontWeight.BOLD, FontPosture.REGULAR, buttonFontSize);

		private String optionFontFamily = "Tahoma";
		private int optionFontSize = 24;
		private Font optionFont = Font.font(optionFontFamily, FontWeight.BOLD, FontPosture.REGULAR, optionFontSize);

		private String paletteSelectorFontFamily = "Tahoma";
		private double paletteSelectorFontSize = 18;

		private double buttonWidth = 210;
		private double buttonHeight = 50;

		public MenuManager(int width, int height) {
			this.width = width;
			this.height = height;

			setButtonWidth(Math.max(buttonWidth, width / 4.0));
		}

		private int getWidth() {
			return width;
		}

		private int getHeight() {
			return height;
		}

		private double getButtonWidth() {

			return buttonWidth;

		}

		private void setButtonWidth(double buttonWidth) {

			this.buttonWidth = buttonWidth;

		}

		private double getSmallButtonWidth() {

			return getButtonWidth() / 1.5;

		}

        double getButtonHeight() {
			return buttonHeight;
		}

		private void setButtonHeight(double buttonHeight) {
			this.buttonHeight = buttonHeight;
		}

		public void setBackgroundColorHex(String colorCode) {
			this.backgroundColorHex = colorCode;
		}

		public String getBackgroundColorHex() {
			return backgroundColorHex;
		}

		public void setButtonColorHex(String colorCode) {
			this.buttonColorHex = colorCode;
		}

		public String getButtonColorHex() {
			return buttonColorHex;
		}

		public void setTextColorHex(String colorCode) {
			this.textColorHex = colorCode;
		}

		public String getTextColorHex() {
			return textColorHex;
		}
		
		public void showTitleScreen(Stage stage) {
			
			double textButtonMargin = 30; // vertical distance between author and first button
			double buttonMargin = 10; // vertical distance between buttons

			resetConfiguration();
			
			stage.sizeToScene();

			StackPane stackPane = new StackPane();
			stackPane.setStyle("-fx-background-color: " + getBackgroundColorHex());
	
			Scene mainMenuScene = new Scene(stackPane, getWidth(), getHeight());
			stage.setScene(mainMenuScene);
			
			Text title = new Text("Rocket Landing Simulator");
			title.setFont(headingFont);
			title.setFill(Color.web(getTextColorHex()));
			title.setTranslateY(-getHeight() / 4);
	
			Text author = new Text("Muhammed Aky??z");
			author.setFont(subheadingFont);
			author.setFill(Color.web(getTextColorHex()));
			author.setTranslateY(
				title.getTranslateY() + title.getLayoutBounds().getHeight());
	
			stackPane.getChildren().addAll(title, author);
			
			Button startComputerButton = new Button("Automatic Landing");
			startComputerButton.setPrefSize(getButtonWidth(), getButtonHeight());
			startComputerButton.setTranslateY(
				author.getTranslateY() + author.getLayoutBounds().getHeight() + textButtonMargin);
			startComputerButton.setFont(buttonFont);
			startComputerButton.setStyle(
				"-fx-background-color: " + getButtonColorHex() + ";" + 
				"-fx-text-fill: " + getTextColorHex() + ";");
			startComputerButton.setOnAction(event -> startComputerSimulation(stage));
	
			Button startUserButton = new Button("Interactive Landing");
			startUserButton.setPrefSize(getButtonWidth(), getButtonHeight());
			startUserButton.setTranslateY(
				startComputerButton.getTranslateY() + startComputerButton.getPrefHeight() + buttonMargin);
			startUserButton.setOnAction(event -> startUserControlledSimulation(stage));
			startUserButton.setFont(buttonFont);
			startUserButton.setStyle(
				"-fx-background-color: " + getButtonColorHex() + ";" + 
				"-fx-text-fill: " + getTextColorHex() + ";");
			stackPane.getChildren().addAll(startComputerButton, startUserButton);

			Button optionsMenuButton = new Button("Options");
			optionsMenuButton.setPrefSize(getSmallButtonWidth(), getButtonHeight());
			optionsMenuButton.setTranslateY(
				startUserButton.getTranslateY() + startUserButton.getPrefHeight() + buttonMargin);
			optionsMenuButton.setOnAction(event -> showOptionsMenu(stage));
			optionsMenuButton.setFont(buttonFont);
			optionsMenuButton.setStyle(
				"-fx-background-color: " + getButtonColorHex() + ";" +
				"-fx-text-fill: " + getTextColorHex() + ";");
			stackPane.getChildren().add(optionsMenuButton);

		}

		public Button getBackToMainMenuButton(double width, double height) {

			Button backToMainMenu = new Button("Back to Main Menu");
			backToMainMenu.setPrefSize(width, height);
			backToMainMenu.setOnAction(event -> showTitleScreen(getPrimaryStage()));
			backToMainMenu.setFont(buttonFont);
			backToMainMenu.setStyle(
				"-fx-background-color: " + getButtonColorHex() + ";" +
				"-fx-text-fill: " + getTextColorHex() + ";");

			return backToMainMenu;

		}
		

		public void showOptionsMenu(Stage stage) {

			double textButtonMargin = 20;
			double titleOptionMargin = 20;
			double optionBackToMainMenuMargin = 50;

			StackPane stackPane = new StackPane();
			stackPane.setStyle("-fx-background-color: " + getBackgroundColorHex());
	
			Scene optionsMenuScene = new Scene(stackPane, getWidth(), getHeight());
			stage.setScene(optionsMenuScene);
			
			Text title = new Text("Options");
			title.setFont(headingFont);
			title.setTranslateY(-getHeight() / 4);

			stackPane.getChildren().add(title);
			
			Text paletteSelectorText = new Text("Color Palette");
			paletteSelectorText.setFont(optionFont);
			paletteSelectorText.setTranslateY(
				title.getTranslateY() + title.getLayoutBounds().getHeight() + titleOptionMargin);
			paletteSelectorText.setTranslateX(-paletteSelectorText.getLayoutBounds().getWidth() / 2 - textButtonMargin / 2);
			stackPane.getChildren().add(paletteSelectorText);

			ComboBox paletteSelector = new ComboBox<ColorPalette>(paletteOptions);
			paletteSelector.setMinWidth(100);
			paletteSelector.setMinHeight(paletteSelectorText.getLayoutBounds().getHeight());
			paletteSelector.getSelectionModel().select(getPalette());
			paletteSelector.setOnAction((Event event) -> {
				setPalette((ColorPalette) paletteSelector.getSelectionModel().getSelectedItem());
			});
			paletteSelector.setStyle(
				"-fx-font: " + paletteSelectorFontSize + "px \"" + paletteSelectorFontFamily + "\";"
			);
			
			paletteSelector.setTranslateX(paletteSelector.getMinWidth()/2 + textButtonMargin / 2);
			paletteSelector.setTranslateY(paletteSelectorText.getTranslateY());

			stackPane.getChildren().add(paletteSelector);

			Button backToMainMenu = getBackToMainMenuButton(200, 50);
			backToMainMenu.setTranslateY(
				paletteSelectorText.getTranslateY() + 
				paletteSelectorText.getLayoutBounds().getHeight() + 
				optionBackToMainMenuMargin);

			stackPane.getChildren().add(backToMainMenu);

		}

		public Group getLandingSummary() {

			boolean acceptableVelocity = 
				world.getPrimaryRocket().getLandingVelocity() < world.getPrimaryRocket().getAcceptableLandingVelocity();
			boolean acceptableAngle = 
				Math.abs(world.getPrimaryRocket().getDirection() - 90) <= world.getPrimaryRocket().getLandingAngleMargin();
			boolean crash = !(acceptableVelocity && acceptableAngle);

			// distance between largest element and the box edge
			double boxMargin = 16; 
			double boxY = HEIGHT / 4 - 25; // top y coordinate of the box
			double textMargin = 5;
			
			String landingMessage = crash ? "Crash" : "Successful Landing";

			Text landingMessageText = new Text(landingMessage);
			landingMessageText.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.REGULAR, 26));
			landingMessageText.setFill(Color.web(getTextColorHex()));
			landingMessageText.setStyle("-fx-text-fill: " + getTextColorHex() + ";");
			landingMessageText.setTranslateY(boxY + 20 + boxMargin);
			landingMessageText.setTranslateX(WIDTH / 2 - 
			landingMessageText.getLayoutBounds().getWidth() / 2);

			Text velocityTextBox = new Text("Velocity: " + (int) world.getPrimaryRocket().getLandingVelocity());
			velocityTextBox.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.REGULAR, 20));
			velocityTextBox.setTranslateY(
				landingMessageText.getTranslateY() + 
				landingMessageText.getLayoutBounds().getHeight() + textMargin);
			velocityTextBox.setTranslateX(WIDTH / 2 - velocityTextBox.getLayoutBounds().getWidth() / 2);
			if (!acceptableVelocity) {
				velocityTextBox.setFill(Color.YELLOW);
			} else {
				velocityTextBox.setFill(Color.web(getTextColorHex()));
			}
			
			Text angleTextBox = new Text("Angle: " + (int) world.getPrimaryRocket().getDirection() + "\u00B0");
			angleTextBox.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.REGULAR, 20));
			angleTextBox.setTranslateY(
				velocityTextBox.getTranslateY() + 
				velocityTextBox.getLayoutBounds().getHeight() + textMargin);
			angleTextBox.setTranslateX(WIDTH / 2 - angleTextBox.getLayoutBounds().getWidth() / 2);
			if (!acceptableAngle) {
				angleTextBox.setFill(Color.YELLOW);
			} else {
				angleTextBox.setFill(Color.web(getTextColorHex()));
			}

			double fuelConsumedProportion = 
				(getInitialFuel() - world.getPrimaryRocket().getFuel()) / getInitialFuel();
			
			Text fuelUsedText = new Text(
				"Fuel Consumed: " + (int) (fuelConsumedProportion * 100) + "%"
			);
			fuelUsedText.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.REGULAR, 20));
			fuelUsedText.setFill(Color.web(getTextColorHex()));
			fuelUsedText.setTranslateY(
				angleTextBox.getTranslateY() + 
				angleTextBox.getLayoutBounds().getHeight() + textMargin
			);
			fuelUsedText.setTranslateX(WIDTH / 2 - 
				fuelUsedText.getLayoutBounds().getWidth() / 2
			);

			Button backToMainMenu = getBackToMainMenuButton(getButtonWidth(), getButtonHeight());
			backToMainMenu.setTranslateY(
				fuelUsedText.getTranslateY() + 
				fuelUsedText.getLayoutBounds().getHeight()
			);
			backToMainMenu.setTranslateX(WIDTH / 2 - getButtonWidth() / 2);
			
			double backToMainMenuBottomY = backToMainMenu.getTranslateY() + getButtonHeight();
			double backgroundBoxWidth = Math.max(
				Math.max(landingMessageText.getLayoutBounds().getWidth(),
					fuelUsedText.getLayoutBounds().getWidth()), 
				Math.max(velocityTextBox.getLayoutBounds().getWidth(), backToMainMenu.getWidth())
				) + boxMargin;
			double backgroundBoxHeight = backToMainMenuBottomY - boxY + boxMargin;

			Rectangle backgroundBox = new Rectangle(WIDTH / 2 - backgroundBoxWidth / 2, 
														boxY, 
														backgroundBoxWidth, 
														backgroundBoxHeight);
			backgroundBox.setArcWidth(10); // round edges
			backgroundBox.setArcHeight(10); // round edges
			backgroundBox.setStrokeWidth(3);
			backgroundBox.setStroke(Color.BLACK); // TODO use declared variables here
			backgroundBox.setFill(Color.web(getBackgroundColorHex()));

			Group landingSummary = new Group();
			landingSummary.getChildren().addAll(
				backgroundBox, landingMessageText, velocityTextBox, angleTextBox, 
				fuelUsedText, backToMainMenu);
			return landingSummary;

		}

		public void startComputerSimulation(Stage stage) {

			// Create the rocket
			double rocketX = WIDTH  / 2;
			double xVelocity = Math.random() * getMaxSpeed() * 2 - getMaxSpeed();
			Vector2D acceleration = new Vector2D(0.0, World.GRAVITY);
			Rocket autoRocket = new Rocket(rocketX,
									world.getGroundY() - getInitialRocketHeight(),
									getInitialFuel(),
									world.getGroundY());

			autoRocket.getVelocity().setX(xVelocity);
			autoRocket.setAcceleration(acceleration);
			setLandingHandled(false);

			world.getObjects().add(autoRocket);
			world.setPrimaryRocket(autoRocket);
			userInterface.calibrateElements(autoRocket);

			stage.setScene(getSimulationScene());
			animator.start();

		}

		public void startUserControlledSimulation(Stage stage) {

			// Create the rocket
			double xVelocity = Math.random() * getMaxSpeed() * 2 - getMaxSpeed();
			Vector2D acceleration = new Vector2D(0.0, World.GRAVITY);
			double rocketX = WIDTH  / 2;

			userRocket.reset(rocketX, world.getGroundY() - getInitialRocketHeight(), getInitialFuel());

			userRocket.getVelocity().setX(xVelocity);

			userRocket.setAcceleration(acceleration);

			setLandingHandled(false);

			world.getObjects().add(userRocket);
			world.setPrimaryRocket(userRocket);
			userInterface.calibrateElements(userRocket);

			stage.setScene(getSimulationScene());
			animator.start();
		}
	}

}


