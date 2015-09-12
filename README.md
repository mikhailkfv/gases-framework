# README #

This is the Gases Framework. It allows you to add gases to Minecraft that will also interact with the systems included in the Gases Framework. Additional possibilities is the creation of reactions between gases and the world (or other gases), and the creation of special lanterns.

To use the Gases Framework in your mod, you must install the [Gases Framework API](https://bitbucket.org/jamieswhiteshirt/gases-framework-api). Consult the javadoc documentation or the [information database](http://jamieswhiteshirt.com/minecraft/mods/gases/information/) for information on how to use it. You should also have a look at how [Glenn's Gases](https://bitbucket.org/jamieswhiteshirt/glenns-gases) uses it.

## To be able to run and develop the Gases Framework in your workspace, you must follow these steps ##

**For Eclipse users:**

* Download a Minecraft Forge distribution and copy its eclipse folder into the workspace.
* Run *gradlew setupDevWorkspace* or *gradlew setupDecompWorkspace* in your workspace.
* Run *gradlew eclipse* in your workspace.
* Open the workspace with Eclipse.
* Open your run configurations and add the following VM argument to both Client and Server: *-Dfml.coreMods.load=glenn.gasesframework.common.core.GFFMLLoadingPlugin*

**For IntelliJ IDEA users:**

* Run *gradlew setupDevWorkspace* or *gradlew setupDecompWorkspace* in your workspace.
* Run *gradlew idea* in your workspace.
* Open the generated project file in IntelliJ IDEA.
* Open Run/Debug Configurations.
* Add the following argument to both Client and Server application VM options: *-Dfml.coreMods.load=glenn.gasesframework.common.core.GFFMLLoadingPlugin*

## Making a development build ##

* Run *gradlew jar* in your workspace. The development build will be placed in the /build/libs folder in your workspace.
