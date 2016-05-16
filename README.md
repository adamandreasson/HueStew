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

Plugins can be written by making a Java project with TDA367 as a dependency. Write a class that extends the abstract class Plugin. This is the main class, that will interact with the main program. 

```
public class MyPlugin extends Plugin {
    ...
}
```

You also want to create a class that implements Light. This way you can create custom lights and register them in the main program.

```
public class MyCustomLight implements Light {
    ...
}
```
