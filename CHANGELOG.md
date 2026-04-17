# Change Log

All notable changes to the "fancy-entity-renderer" Minecraft mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Crystal Nest Semantic Versioning](https://crystalnest.it/#/versioning).

## [Unreleased]

- Nothing new.

## [v0.6.0] - 2026/04/12

- Ported to 26.1.2.

## [v0.6.0] - 2026/04/09

- Ported to 26.1.x.
- Added dev testing menu screens.
- Added lookup provider for in-GUI contexts, allowing to use data-driven elements even when no world is loaded.
- Fixed [#20](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/20), baby armor layer not rotating along with the model.
- Added proper handling of `deadmau5` ears.
- Improved nametag handling, especially for swimming and sleeping poses.
- Fix some issues where calling methods in a different order than expected could cause some properties to not be applied correctly.

## [v0.5.4] - 2026/02/11

- 1.21.10+ only.
- Fixed [#21](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/21), incompatibility with owo-lib.

## [v0.4.5] - 2026/02/11

- 1.21.6, 1.21.7, and 1.21.8 only.
- Fixed [#21](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/21), incompatibility with owo-lib.

## [v0.5.3] - 2026/01/19

- Fixed a bug where it was impossible to control the visibility of the nametag when mimicking a player.

## [v0.5.2] - 2026/01/11

- 1.21.10+ only.
- Fixed [#20](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/20), armor layer not rotating along with the model.

## [v0.4.4] - 2026/01/09

- 1.21 and 1.21.1 only.
- Fixed head z rotation.

## [v0.5.1] - 2025/12/22

- Ported to 1.21.11.

## [v0.5.1] - 2025/12/08

- Fixed player model becoming black after exiting a world.

## [v0.5.0] - 2025/12/07

- Ported to 1.21.10.
- Removed `FancyProfileFetcher` (superseded by `ProfileResolver`).
- Renamed `Rotation#createFromDeg` and `Rotation#createFromRad` to `Rotation#fromDeg` and `Rotation#fromRad`.
- `FancyPlayerModel#getBabyArmorModel(boolean)` now returns an `ArmorModelSet<PlayerModel>`.
- Glowing now renders the player as a full, solid, colored, emissive texture.
- Removed instance-specific renderers from `FancyPlayerWidget` and "global" instances are saved in `EntityRenderDispatcher` via mixin.
- Renderer instances in `EntityRenderDispatcher` now properly update their context on resource reload.
- Removed static `RENDER_CONTEXT` from `FancyPlayerRenderer`.
- Removed private reference to the widget render state in `FancyPlayerRenderer`.
- The constructor of `FancyPlayerRenderer` now requires a `EntityRendererProvider.Context` instance instead of a `FancyPlayerRenderState` one.
- Added `FancyPlayerRenderer#mimicRenderState(FancyPlayerRenderState, float, boolean, boolean)` to specifically update the render state when mimicking an actual player.
- Added `FancyPlayerRenderer#updateRenderState(FancyPlayerRenderState)` to update the render state (when not mimicking a player).

## [v0.4.3] - 2025/12/06

- 1.21/1.21.1 and 1.21.6/1.21.7/1.21.8 only.
- Added compatibility with Soul Fire'd v6.0.0+ and Prometheus 1.1.0+.

## [v0.4.2] - 2025/11/26

- 1.21/1.21.1 only.
- Fix a compatibility with Ore Harvester (and probably a few other mods).

## [v0.4.1] - 2025/10/23

- 1.21/1.21.1 only.
- Fix crash on server.

## [v0.4.0] - 2025/09/29

- 1.21.6+ only.
- Renamed `bodyRot` to `modelRot` to avoid overriding the already existing property in `LivingEntityRenderState`.
- Implemented [#17](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/17), there is now a flag to prevent the name tag from rotating around the Y axis with the player.
- Implemented [#18](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/18), there are now flags to individually change the visibility of different body parts or layers.
- Added new property (`walkSpeed`) to allow players to walk while moving (before they could only play the idle animation when standing or crouching).
- Fixed a (Vanilla) bug that would prevent multiple widgets from being rendered at a time.

## [v0.4.0] - 2025/09/28

- Backported to 1.21 and 1.21.1.
- For 1.21 and 1.21.1 only some features are missing:
  * Mimic player;
  * Attack/swing animation;
  * Arms poses;
- Renamed `bodyRot` to `modelRot` to avoid overriding the already existing property in `LivingEntityRenderState`.
- Implemented [#17](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/17), there is now a flag to prevent the name tag from rotating around the Y axis with the player.
- Implemented [#18](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/18), there are now flags to individually change the visibility of different body parts or layers.
- Added new property (`walkSpeed`) to allow players to walk while moving (before they could only play the idle animation when standing or crouching).

## [v0.3.5] - 2025/09/11

- Fixed a bug with NeoForge that prevented the game to load correctly.
- Ported to 1.21.6, 1.21.7, and 1.21.8.

## [v0.3.4] - 2025/08/30

- A few private fields of `FancyPlayerWidget` are now protected for better accessibility when subclassing.
- Added setter for arrow count, allowing to set arrows stuck into the player's body.
- Added setter for stinger count, allowing to set stingers stuck into the player's body.
- Added ability to fully mimic a client player, with possibility to whitelist only some poses.

## [v0.3.3] - 2025/08/29

- Ported to 1.21.5.
- Fixed [#15](https://github.com/Crystal-Nest/fancy-entity-renderer/issues/15), Soul Fire'd dependency dragging along on Maven.
- Fixed Soul Fire'd declared dependency in mod loaders and mod publishing platforms.
- Dropped support for Forge.

## [v0.3.2] - 2025/07/27

- `ItemStack`s can now be used to set arm items too, parity with wearables.

## [v0.3.1] - 2025/07/27

- Removed leftover debug outline.
- Fixed interaction between swimming pose and parrots.

## [v0.3.0] - 2025/07/26

- Removed spectator setting in favor of the new `RenderMode`.
- Added new `RenderMode` to choose between normal mode, invisible mode, spectator mode, and ghost mode.
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

- Added `FancyPlayerWidget` to render player entities in any Screen, Menu, GUI, or HUD.
- Added `Rotation` class to the API to easily handle 3-axis rotations in either radians or degrees.
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

[v0.6.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.6.0
[v0.5.4]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.5.4
[v0.5.3]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.5.3
[v0.5.2]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.5.2
[v0.5.1]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.5.1
[v0.5.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.5.0
[v0.4.5]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.4.5
[v0.4.4]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.4.4
[v0.4.3]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.4.3
[v0.4.2]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.4.2
[v0.4.1]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.4.1
[v0.4.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.4.0
[v0.3.5]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.5
[v0.3.4]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.4
[v0.3.3]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.3
[v0.3.2]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.2
[v0.3.1]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.1
[v0.3.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.3.0
[v0.2.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.2.0
[v0.1.0]: https://github.com/crystal-nest/fancy-entity-renderer/releases?q=0.1.0
