# TDA367 - Group 20

The repository for Aranäskvartetten's TDA367 project.

### Tech

The project uses Maven for dependency management. Dependencies that are accessed through Maven are:

* JSON - saving data in JSON format
* Xuggler - Converting loaded mp3 files to wav

### Installation

Clone the repo. If you have Maven properly set up, the project should compile in your favorite IDE.

### Plugins

TDA367 has a plugin functionality accessible to developers. Currently existing plugins are:

* PhilipsHuePlugin - for syncing lights to Philips Hue light bulbs
* TextLights - very basic plugin, mostly for testing plugin functionality. Shows light state changes in the console.

#### Writing plugins

Plugins can be written by making a Java project with TDA367 as a dependency. Write a class that implements the interface Plugin. This is the main class, that will interact with the main program. 

```
public class MyPlugin implements Plugin {
    ...
}
```

You also want to create a class that implements Light. This way you can create custom lights and register them in the main program.

```
public class MyCustomLight implements Light {
    ...
}
```

A plugin needs to register any lights that it creates/facilitates through the LightBank. This is easily done with:

```
LightBank.getInstance().addLight(Light light);
```

#### plugin.properties

All plugins require a file in the root of the .jar called "plugin.properties". The main program will look for this file when loading a plugin. This file must contain a pointer to the main class in the plugin, in this example that is "MyPlugin".

```
main=com.myplugin.MyPlugin
```

The plugin will not load unless this file exists, with the correct pointer.