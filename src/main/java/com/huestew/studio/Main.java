package com.huestew.studio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.huestew.studio.controller.MainViewController;
import com.huestew.studio.util.FileUtil;
import com.huestew.studio.util.Util;
import com.huestew.studio.util.WaveBuilder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainViewController controller = (MainViewController) Util.loadFxml("main.fxml");
		primaryStage.setTitle("HueStew Studio");
		Scene scene = new Scene(controller.getView(),1280,720);
		primaryStage.setScene(scene);

		primaryStage.getIcons().add(new Image("icon_256x256.png"));
		primaryStage.show();
	}

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		
		// THIS SHOULD ALL BE ELSEWHERE
		Path tmp = Files.createTempDirectory("HueStew_");
		String tmpSongFile = tmp.toString()+"/song.wav";
		FileUtil.convertAudioFile("song.mp3", tmpSongFile);
		System.out.println(tmpSongFile);
		
		WaveBuilder wave = new WaveBuilder(tmpSongFile, "wave.png", 4000, 400);
		
		launch(args);
	}
}
