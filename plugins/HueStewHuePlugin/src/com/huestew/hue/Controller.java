/**
 * 
 */
package com.huestew.hue;

import java.util.ArrayList;
import java.util.List;

import com.huestew.studio.controller.LightBank;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import javafx.collections.FXCollections;

/**
 * @author Adam
 *
 */
public class Controller {

	HuePlugin plugin;
	PHHueSDK phHueSDK;

	public Controller(HuePlugin plugin) {
		this.plugin = plugin;
		this.phHueSDK = PHHueSDK.getInstance();
	}

	public void findBridges() {
		phHueSDK = PHHueSDK.getInstance();
		PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
		sm.search(true, true);
	}

	/**
	 * @return the listener
	 */
	public PHSDKListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(PHSDKListener listener) {
		this.listener = listener;
	}

	private PHSDKListener listener = new PHSDKListener() {

		@Override
		public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
			plugin.bridgeChoiceBox.setDisable(false);

			ArrayList<String> ips = new ArrayList<String>();
			for (PHAccessPoint point : accessPointsList) {
				ips.add(point.getIpAddress());
			}

			plugin.bridgeChoiceBox.setItems(FXCollections.observableArrayList(ips));

			plugin.bridgeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, oldValue, newValue) -> {

				PHAccessPoint point = accessPointsList.get(newValue.intValue());
				phHueSDK.connect(point);
				plugin.bridgeChoiceBox.setDisable(true);

				plugin.updateStatus("Connecting....");

			});

			plugin.updateStatus("Pick an access point.");
		}

		@Override
		public void onAuthenticationRequired(PHAccessPoint accessPoint) {

			System.out.println(accessPoint.getIpAddress() + " requires auth");
			phHueSDK.startPushlinkAuthentication(accessPoint);

			plugin.updateStatus("Access point requires authentication. Press the button on the unit. You have 30 seconds.");

		}

		@Override
		public void onBridgeConnected(PHBridge bridge, String username) {

			System.out.println("CONNCETD!");

			String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();

			plugin.getProperties().setProperty("username", username);
			plugin.getProperties().setProperty("lastIp", lastIpAddress);

			phHueSDK.setSelectedBridge(bridge);

			plugin.updateStatus("Connected.");

			loadLights();

		}

		@Override
		public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectionLost(PHAccessPoint arg0) {
			System.out.println("Philips Hue connection lost");
		}

		@Override
		public void onConnectionResumed(PHBridge arg0) {
			System.out.println("Philips Hue connection resumed");

		}

		@Override
		public void onError(int errorNum, String arg1) {
			System.err.println("Philips Hue error:");
			System.err.println(errorNum + ": " + arg1);
			if(errorNum == 46){
				plugin.initView();
			}
		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> arg0) {
			// TODO Auto-generated method stub

		}

	};

	public boolean connectToLastKnownAccessPoint() {
		String username = plugin.getProperties().getProperty("username", null);
		String lastIpAddress = plugin.getProperties().getProperty("lastIp", null);

		if (username == null || lastIpAddress == null) {
			System.out.println("Missing username or IP");
			return false;
		}
		
		System.out.println("Connecting with username " + username);
		PHAccessPoint accessPoint = new PHAccessPoint();
		accessPoint.setIpAddress(lastIpAddress);
		accessPoint.setUsername(username);
		phHueSDK.connect(accessPoint);
		return true;
	}

	public void loadLights() {
		PHBridge bridge = phHueSDK.getSelectedBridge(); 
		List<PHLight> allLights = bridge.getResourceCache().getAllLights();
		for(PHLight phLight : allLights){
			System.out.println(phLight.getName());
			HueLight light = new HueLight(phLight, "Philips Hue: " + phLight.getName());
			LightBank.getInstance().addLight(light);
		}

	}

}
