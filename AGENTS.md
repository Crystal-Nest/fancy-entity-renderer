# Repository Guidelines

## Project Structure & Module Organization
- "Fancy Entity Renderer" is a Minecraft Java 1.20.1 mod that uses the MultiLoader layout with shared logic under `common` and loader-specific wrappers under `fabric` and `neoforge`.

## Environment
- You are running inside WSL on a Windows machine.

## General Guidelines and Reminders
- Always keep in mind that you are an AI. Time does not work the same for you as it does for humans.
- What would take humans weeks or more to implement/write will take you only some minutes, so you should never take the "faster" route just to get the job done quicker. Take the BEST route, even if it takes a bit longer.

## Coding Style
- Target Java 17 (Minecraft 1.20.1 uses Java 17) with 4-space indentation and UTF-8 encoding (WITHOUT BOM), matching the Gradle toolchain configuration.
- Prefer explicit nullability annotations from `jsr305`.
- Always use proper class imports. Don't call classes by their full classpath in code, if not needed.

## Minecraft & Library Sources
- There is a directory with full Minecraft sources of all common versions for you to look up things when working with Minecraft code.
- The sources directory is located at `E:\CODING\WORKSPACES\IntelliJ\Minecraft Mods\.MINECRAFT_SOURCES`.
- The top level of the sources directory has subdirectories for each Minecraft version, such as `1.19.2`, `1.20.1`, `1.21.1`, `1.21.11`, `26.1.1`, etc.
- In each Minecraft version subdirectory is a `minecraft` directory and a `libraries` directory.
- The `minecraft` directory has subdirectories for the different launchers, so you can compare launcher-specific changes to Minecraft code, such as `fabric`, `forge`, `neoforge`, etc., and inside each is the Minecraft source code.
- The `libraries` directory contains the source code of some important libraries used by Minecraft, such as the source code for LWJGL.
- Make sure to check the Minecraft source code when working with Minecraft code, instead of guessing.

## Gradle
- Do NOT directly run or compile the project via Gradle, except the user explicitly tells you to do so. Using IntelliJ run configurations are an exception here.

## Testing
- To check if the project compiles and to look for errors/issues in general, use the `get_run_configurations` and `execute_run_configuration` tools to get and execute the Fabric and NeoForge client run configurations inside the open IntelliJ IDE.
- Never try to run the `common` module directly, because it is only the base for the `fabric` and `neoforge` ones.
- When running a "run configuration", always set a timeout of 80 seconds by default, and only if that's not enough after trying the first time, add 30 more seconds and so on.
- Do not treat "the game is not crashing" as "everything works". When the client successfully launched in testing, check its log files for remaining errors and other problems/issues.
- You are allowed to add temporary testing code and classes to the project, to be able to better analyze and test specific parts. This can be simple debug logging (always use the INFO level), but it can also be code for you to automatically open menus, launch things, or whatever you need for testing. Just make sure to remove the testing code after.
- You can take screenshots by using OS-level tools/utils, if needed.
- Manually kill/close lingering game processes from running "run configurations". They do not automatically close, not even on timeout. They only close automatically when crashing (obviously).