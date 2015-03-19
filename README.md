# README #

This is the Gases Framework. It allows you to add gases to Minecraft that will also interact with the systems included in the Gases Framework. Additional possibilities is the creation of reactions between gases and the world (or other gases), and the creation of special lanterns.

To use the Gases Framework in your mod, you must install the [Gases Framework API](https://bitbucket.org/jamieswhiteshirt/gases-framework-api). Consult the javadoc documentation or the [information database](http://jamieswhiteshirt.com/minecraft/mods/gases/information/) for information on how to use it. You should also have a look at how [Glenn's Gases](https://bitbucket.org/jamieswhiteshirt/glenns-gases) uses it.

**To be able to run and develop the Gases Framework in your workspace using Eclipse, you must follow these steps:**

* Download a forge distribution and copy the eclipse folder into this workspace.
* Run *gradlew setupDevWorkspace* in your workspace.
* Run *gradlew eclipse* in your workspace.
* Open the workspace with Eclipse.
* Open your run configurations and add the following VM argument to both Client and Server: *-Dfml.coreMods.load=glenn.gasesframework.common.core.GFFMLLoadingPlugin*

If you would like to compile a development build of Gases Framework, run *gradlew jar* in your workspace. The jar will be located in /build/libs/ .