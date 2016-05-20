/**
 * 
 */
package com.huestew.hue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import com.huestew.studio.HueStew;
import com.huestew.studio.plugin.Plugin;
import com.philips.lighting.hue.sdk.PHHueSDK;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Adam
 *
 */
public class HuePlugin implements Plugin {

	ChoiceBox<String> bridgeChoiceBox;
	Button searchBtn;
	Label statusLabel;
	private Controller controller;
	private Properties properties;

	@Override
	public void sendLightState() {
		System.out.println("Hue plugin sending light state bro");
	}

	@Override
	public void onEnable() {

		loadLibraries();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				loadProperties();
				initHueSetup();
			}
		});

	}

	@Override
	public void onDisable() {
		saveProperties();
	}

	private void loadLibraries() {
		try {
			final File[] libs = new File[] { new File(getDataFolder(), "huelocalsdk.jar"),
					new File(getDataFolder(), "huesdkresources.jar"), new File(getDataFolder(), "tablelayout.jar") };
			for (final File lib : libs) {
				if (!lib.exists()) {
					JarUtils.extractFromJar(lib.getName(), lib.getAbsolutePath());
				}
			}
			for (final File lib : libs) {
				if (!lib.exists()) {
					System.err.println("UNABLE TO LOAD PLUGIN DEPENDENCIES");
					return;
				}
				addClassPath(JarUtils.getJarUrl(lib));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void addClassPath(final URL url) throws IOException {
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		final Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new IOException("Error adding " + url + " to system classloader");
		}
	}

	private String getDataFolder() {
		return "libs";
	}

	private void loadProperties() {

		File propertyFile = new File(HueStew.getInstance().getFileHandler().getAppFilePath("plugins/hue.properties"));
		properties = new Properties();
		
		if (propertyFile.exists()) {

			InputStream input = null;

			try {

				input = new FileInputStream(HueStew.getInstance().getFileHandler().getAppFilePath("plugins/hue.properties"));
				properties.load(input);

			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			try {
				propertyFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void saveProperties() {

		System.out.println("PLUGIN SAVING ROPERTIES");
		OutputStream output = null;

		try {

			output = new FileOutputStream(HueStew.getInstance().getFileHandler().getAppFilePath("plugins/hue.properties"));

			properties.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public Properties getProperties(){
		return properties;
	}

	private void initHueSetup() {

		PHHueSDK phHueSDK = PHHueSDK.create();
		controller = new Controller(this);

		phHueSDK.getNotificationManager().registerSDKListener(controller.getListener());
		
		if(properties.getProperty("username") != null){
			
			boolean success = controller.connectToLastKnownAccessPoint();
			
			if(success)
				return;
			
		}

		initView();

	}

	private void initView() {

		Stage stage = new Stage();
		stage.setTitle("Philips Hue setup");
		
		searchBtn = new Button();
		searchBtn.setText("Find bridges");
		searchBtn.setPrefWidth(140.0);		
		
		searchBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				controller.findBridges();
				searchBtn.setDisable(true);
				updateStatus("Searching for access points...");
			}
		});

		bridgeChoiceBox = new ChoiceBox<String>();
		bridgeChoiceBox.setDisable(true);
		bridgeChoiceBox.setPrefWidth(140.0);

		statusLabel = new Label("Not connected to a bridge");
		statusLabel.setPrefWidth(140.0);
		statusLabel.setPrefHeight(50.0);
		statusLabel.setWrapText(true);
		
		VBox root = new VBox();
		root.setSpacing(20);
		root.setPadding(new Insets(80, 50, 50, 50));
		root.getChildren().add(searchBtn);
		root.getChildren().add(bridgeChoiceBox);
		root.getChildren().add(statusLabel);
		stage.setScene(new Scene(root));
		stage.show();
		stage.sizeToScene();
	}
	
	public void updateStatus(String message){

		if(statusLabel == null)
			return;
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				statusLabel.setText(message);
			}
		});
	}

}
