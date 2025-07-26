# Change Log

All notable changes to the "fancy-entity-renderer" Minecraft mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Crystal Nest Semantic Versioning](https://crystalnest.it/#/versioning).

## [Unreleased]

- Nothing new.

## [v0.3.0] - 2025/07/26

- Removed spectator setting in favor of the new `RenderMode`.
- Add new `RenderMode` to choose between normal mode, invisible mode, spectator mode, and ghost mode.
- It's now possible to set parrots for either shoulders.
- Removed crouching setting in favor of the new `Pose` based system [#7](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/7).
- Added support for arms poses.
- Added support for dynamic movements [#13](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/13).
- Fixed name tag breaking the Minecraft title rendering.

## [v0.2.0] - 2025/04/11

- The default player model is now slim.
- Implemented [#3](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/3), parsing of item strings to set wearable items.
- Implemented [#4](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/4), improved rendering of entities on fire.
- Implemented [#5](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/5), fixed spectator rendering.
- Implemented [#14](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/14), compatibility with Soul Fire'd when rendering entities on fire.
- Minimum NeoForge version for Minecraft 1.21.4 is now v21.4.87-beta.

## [v0.1.0] - 2025/03/17

- Add `FancyPlayerWidget` to render player entities in any Screen, Menu, GUI, or HUD.
- Add `Rotation` class to the API to easily handle 3-axis rotations in either radians or degrees.
- It's possible to render the player widget at any x, y coordinates and with any width or height.
- It's possible to set custom, single model parts (arms, legs, head) rotations.
- It's possible to make the head and/or the whole model follow the mouse cursor.
- It's possible to render the player as a baby.
- It's possible to add any parrot variant to either shoulders.
- It's possible to set any custom name and toggle its visibility.
- It's possible to choose whether to use the slim or wide model.
- It's possible to set any custom skin.
- It's possible to set cape visibility.
- It's possible to copy the local player.
- It's possible to copy a remote player from its UUID or username.
- It's possible to set items in either hands.
- It's possible to set wearable items in any body part.
- It's possible to render the model upside-down.
- It's possible to choose whether the player is crouching (**planned for removal** in future versions in favor of a more powerful pose system setting).
- It's possible to set the player on fire (**experimental**, doesn't look that good currently).
- It's possible to make the player move idly (**experimental**, has not been tested with custom rotations).
- It's possible to make the player have the glowing effect (**experimental**, does not work as of now).
- It's possible to render the player as in spectator mode (**experimental**, currently it only makes the player a floating head).

[Unreleased]: https://github.com/crystal-nest/fancy-entity-renderer
[README]: https://github.com/crystal-nest/fancy-entity-renderer#readme

[v0.3.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.0
[v0.2.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.2.0
[v0.1.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.1.0
