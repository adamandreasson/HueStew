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
 * Loads plugins from a given path into the {@link PluginHandler}
 * 
 * @author Adam
 *
 */
public class PluginLoader {

	public PluginLoader(File rootPath, PluginHandler pluginHandler) {

		if (!rootPath.exists())
			return;

		Thread thread = new Thread(() -> {
			for (File file : rootPath.listFiles()) {

				if (!file.toString().endsWith(".jar"))
					continue;

				Properties prop = loadPluginProperties(file);

				if (prop != null) {
					try {
						Plugin plugin = load(file, prop);

						if (plugin != null) {
							pluginHandler.addPlugin(plugin);
						}
					} catch (MalformedURLException | ClassCastException | ReflectiveOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		thread.start();

	}

	/**
	 * Load the properties file within the plugin
	 * 
	 * @param file
	 *            The plugin file to load from. Jar file!
	 * @return Properties loaded from within the plugin jar
	 */
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

	/**
	 * Load a plugin from a file with given properties
	 * 
	 * @param file
	 *            The file to load the plugin from, .jar file
	 * @param prop
	 *            The properties to use when loading. Needed to find the main
	 *            class in the plugin
	 * @return A new Plugin instance
	 */
	private Plugin load(File file, Properties prop)
			throws MalformedURLException, ClassCastException, ReflectiveOperationException {

		URL[] url = { file.toURI().toURL() };

		URLClassLoader child = new URLClassLoader(url, this.getClass().getClassLoader());
		Class<?> classToLoad = Class.forName(prop.getProperty("main"), true, child);

		Class<? extends Plugin> pluginClass = classToLoad.asSubclass(Plugin.class);

		Plugin plugin = pluginClass.newInstance();
		plugin.onEnable();
		return plugin;
	}

}
