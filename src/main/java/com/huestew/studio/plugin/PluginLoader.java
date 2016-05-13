/**
 * 
 */
package com.huestew.studio.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 * @author Adam
 *
 */
public class PluginLoader {

	public PluginLoader(File rootPath, PluginHandler pluginHandler) {

		if(!rootPath.exists())
			return;
		
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				for (File file : rootPath.listFiles()) {

					if (!file.toString().endsWith(".jar"))
						continue;

					Properties prop = loadPluginProperties(file);

					if(prop != null){
						try {
							Plugin plugin = load(file, prop);
							
							if(plugin != null){
								pluginHandler.addPlugin(plugin);
							}
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		});
		
		thread.start();

	}

	private Properties loadPluginProperties(File file) {

		InputStream in = null;
		URL inputURL = null;

		String inputFile = "jar:" + file.toURI() + "!/plugin.properties";
		if (inputFile.startsWith("jar:")) {
			try {
				inputURL = new URL(inputFile);
				JarURLConnection conn = (JarURLConnection) inputURL.openConnection();
				in = conn.getInputStream();
			} catch (MalformedURLException e1) {
				System.err.println("Malformed input URL: " + inputURL);
				return null;
			} catch (IOException e1) {
				System.err.println("IO error open connection");
				return null;
			}
		}

		Properties prop = new Properties();
		try {
			prop.load(in);
			return prop;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;

	}

	private Plugin load(File file, Properties prop) throws MalformedURLException {

		URL[] url = { file.toURI().toURL() };

		URLClassLoader child = new URLClassLoader(url, this.getClass().getClassLoader());
		Class<?> classToLoad = null;
		try {
			classToLoad = Class.forName(prop.getProperty("main"), true, child);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Class<? extends Plugin> pluginClass = null;
		try {
			pluginClass = classToLoad.asSubclass(Plugin.class);
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}

		Plugin plugin = null;
		try {
			plugin = pluginClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		plugin.onEnable();
		return plugin;
	}

}
