# Aphorea Mod
This is the code of AphoreaMod! (https://steamcommunity.com/sharedfiles/filedetails/?id=3268603061)

All code was created by Save by modifying and expanding the original game files. Of course, a large part of the code still belongs to the original game. You are free to use this code for learning purposes or for creating Necesse-related content

Most of the Sprites were created by Pooper

Save Steam profile: https://steamcommunity.com/id/Aiz_Save/

Pooper Steam profile: https://steamcommunity.com/profiles/76561198448686767

All AphoreaMod content is licensed under **CC BY-NC-SA 4.0**



## Using Aphorea Mod as a Dependency

Since this mod is not publicly available on Maven Central, the best way to use it as a dependency for other mods is:

1. Download the latest `.jar` file of the mod
2. Add the `.jar` file to a folder inside your mod project (for example, `libs/`)

3. In your `build.gradle`, add the following to the `repositories` section:

    ```groovy
    flatDir {
        dirs 'libs'  // Replace 'libs' with your folder name
    }
    ```

4. Then, add the dependency in the `dependencies` section:

    ```groovy
    compileOnly files('libs/your-mod-file.jar')  // Replace with your actual jar filename
    ```

5. Finally, add the `.jar` file to your IDE's libraries so you can easily use it while coding
6. In your `build.gradle`, make sure to include `"aphoreateam.aphoreamod"` in `project.ext.modDependencies = []`
7. Once you launch the mod, don’t forget to add the dependency to Steam
