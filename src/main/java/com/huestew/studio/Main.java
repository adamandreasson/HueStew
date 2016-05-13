package com.huestew.studio;

import com.huestew.studio.controller.MainViewController;
import com.huestew.studio.util.Util;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		MainViewController controller = (MainViewController) Util.loadFxml("main.fxml");
		HueStew.getInstance().getView().setMainViewController(controller);
		primaryStage.setTitle("HueStew Studio");
		controller.setStage(primaryStage);
		Scene scene = new Scene(controller.getView(),1280,720);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/icon_256x256.png"));
		primaryStage.show();

		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			HueStew.getInstance().getView().handleKeyboardEvent(event);
			Toolbox.getSelectedTool().doAction(event);
		});
		primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			Toolbox.getSelectedTool().doAction(event);
		});

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	HueStew.getInstance().autoSave();
            }
        });     


	}

	public static void main(String[] args) {
		launch(args);
	}
}
