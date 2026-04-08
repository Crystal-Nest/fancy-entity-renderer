package it.crystalnest.fancy_entity_renderer.client.screen;

import it.crystalnest.fancy_entity_renderer.api.entity.RenderMode;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * Small in-dev showcase screen for FER player widgets.
 */
public final class DevTestScreen extends OptionsSubScreen {
  public DevTestScreen(Screen parent, Options options) {
    super(parent, options, Component.literal("FER Dev Test"));
  }

  private static FancyPlayerWidget buildWidget(int columnWidth, int columnHeight, Function<FancyPlayerWidget, FancyPlayerWidget> configuration) {
    return configuration.apply(new FancyPlayerWidget(0, 0, columnWidth, columnHeight));
  }

  @Override
  protected void addOptions() {
    assert list != null;

    int columnWidth = 17 * 4;
    int columnHeight = 24 * 4;

    // TODO:
    //  Fix name tag positioning when sleeping and swimming
    //  Use the correct provider in the pause menu screen
    //  Finish with GuiItemContext

    List<FancyPlayerWidget> widgets = List.of(
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Suffering Follower")
        .setShowName(true)
        .setPinName(true)
        .setBodyFollowsMouse(true)
        .setHeadFollowsMouse(true)
        .setSlim(true)
        .setArrowCount(10)
        .setStingerCount(10)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Burning Man")
        .setShowName(true)
        .setOnFire(true)
        .setSlim(false)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Wacky")
        .setShowName(true)
        .setHeadRotation(15, 0, 0)
        .setBodyRotation(0, 15, 0)
        .setRightArmRotation(0, 0, 15)
        .setLeftArmRotation(0, 15, 0)
        .setRightLegRotation(15, 0, 0)
        .setLeftLegRotation(0, 15, 0)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Armored Wacky")
        .setShowName(true)
        .setHeadRotation(0, 15, 0)
        .setBodyRotation(0, 0, 15)
        .setRightArmRotation(0, 15, 0)
        .setLeftArmRotation(15, 0, 0)
        .setRightLegRotation(0, 15, 0)
        .setLeftLegRotation(0, 0, 15)
        .setHeadWearable(Items.NETHERITE_HELMET)
        .setChestWearable(Items.NETHERITE_CHESTPLATE)
        .setLegsWearable(Items.NETHERITE_LEGGINGS)
        .setFeetWearable(Items.NETHERITE_BOOTS)
        .setRightHandItem(Items.MACE)
        .setLeftHandItem(Items.SHIELD)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Missing")
        .setShowName(true)
        .setShowHead(false)
        .setShowBody(false)
        .setShowLeftArm(false)
        .setShowRightArm(false)
        .setShowRightLeg(false)
        .setShowLeftLeg(false)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Armored Follower")
        .setShowName(true)
        .setSlim(false)
        .setHeadFollowsMouse(true)
        .setHeadWearable(Items.CHAINMAIL_HELMET)
        .setChestWearable(Items.ELYTRA)
        .setLegsWearable(Items.DIAMOND_LEGGINGS)
        .setFeetWearable(Items.DIAMOND_BOOTS)
        .setRightHandItem(Items.DIAMOND_SWORD)
        .setLeftParrot(Parrot.Variant.BLUE)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Spectator")
        .setShowName(true)
        .setRenderMode(RenderMode.SPECTATOR)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Ghost")
        .setShowName(true)
        .setRenderMode(RenderMode.GHOST)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Head Over Heels 4 U")
        .setShowName(true)
        .setUpsideDown(true)
        .setBodyFollowsMouse(true)
        .setHeadFollowsMouse(true)
        .setParrots(Parrot.Variant.RED_BLUE, Parrot.Variant.GREEN)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Copycat")
        .setShowName(true)
        .copyLocalPlayer()
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setShowName(true)
        .copyPlayer("Crystal_Spider_")
        .setMovementSpeed(1)
        .setMoving(true)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Catfish")
        .setShowName(true)
        .copyPlayer("Deadmau5")
        .setMovementSpeed(1)
        .setWalkingSpeed(1)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Sleepy")
        .setShowName(true)
        .setSlim(true)
        .setLeftParrot(Parrot.Variant.YELLOW_BLUE)
        .setPose(Pose.SLEEPING)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Baby Sleepy")
        .setShowName(true)
        .setSlim(true)
        .setBaby(true)
        .setLeftParrot(Parrot.Variant.YELLOW_BLUE)
        .setPose(Pose.SLEEPING)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Swim Swim")
        .setShowName(true)
        .setSlim(true)
        .setRightHandItem(Items.NAUTILUS_SHELL)
        .setPose(Pose.SWIMMING)
        .setMovementSpeed(1)
        .setWalkingSpeed(1)
        .setMoving(true)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Baby Swim Swim")
        .setShowName(true)
        .setSlim(true)
        .setBaby(true)
        .setRightHandItem(Items.NAUTILUS_SHELL)
        .setPose(Pose.SWIMMING)
        .setMovementSpeed(1)
        .setWalkingSpeed(1)
        .setMoving(true)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Vortex")
        .setShowName(true)
        .setSlim(true)
        .setRightHandItem(Items.TRIDENT)
        .setRightArmPose(HumanoidModel.ArmPose.THROW_TRIDENT)
        .setPose(Pose.SPIN_ATTACK)
        .setMovementSpeed(0.5F)
        .setMoving(true)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Baby Vortex")
        .setShowName(true)
        .setSlim(true)
        .setBaby(true)
        .setRightHandItem(Items.TRIDENT)
        .setRightArmPose(HumanoidModel.ArmPose.THROW_TRIDENT)
        .setPose(Pose.SPIN_ATTACK)
        .setMovementSpeed(0.5F)
        .setMoving(true)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Croucher")
        .setShowName(true)
        .setSlim(true)
        .setHeadWearable(Items.GOLDEN_HELMET)
        .setParrots(Parrot.Variant.DEFAULT, Parrot.Variant.GRAY)
        .setPose(Pose.CROUCHING)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Baby Croucher")
        .setShowName(true)
        .setSlim(true)
        .setBaby(true)
        .setHeadWearable(Items.GOLDEN_HELMET)
        .setChestWearable(Items.GOLDEN_CHESTPLATE)
        .setLegsWearable(Items.LEATHER_LEGGINGS)
        .setFeetWearable(Items.IRON_BOOTS)
        .setParrots(Parrot.Variant.DEFAULT, Parrot.Variant.GRAY)
        .setPose(Pose.CROUCHING)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Done For")
        .setShowName(true)
        .setSlim(true)
        .setChestWearable(Items.ELYTRA)
        .setParrots(Parrot.Variant.DEFAULT, Parrot.Variant.GRAY)
        .setPose(Pose.DYING)
      ),
      buildWidget(columnWidth, columnHeight, widget -> widget
        .setName("Baby Done For")
        .setShowName(true)
        .setSlim(true)
        .setBaby(true)
        .copyPlayer("Crystal_Spider_")
        .setChestWearable(Items.ELYTRA)
        .setParrots(Parrot.Variant.DEFAULT, Parrot.Variant.GRAY)
        .setPose(Pose.DYING)
      ),
      buildWidget(columnWidth, columnHeight, widget -> {
          // Dyed leather helmet.
          ItemStack dyed = Items.LEATHER_HELMET.getDefaultInstance();
          dyed.set(DataComponents.DYED_COLOR, DyedItemColor.applyDyes((DyedItemColor) null, List.of(DyeColor.PURPLE)));
          // Trimmed netherite chestplate.
          ItemStack trimmed = Items.NETHERITE_CHESTPLATE.getDefaultInstance();
          trimmed.set(
            DataComponents.TRIM,
            new ArmorTrim(
              FancyPlayerWidget.getGuiProvider().lookupOrThrow(Registries.TRIM_MATERIAL).getOrThrow(TrimMaterials.AMETHYST),
              FancyPlayerWidget.getGuiProvider().lookupOrThrow(Registries.TRIM_PATTERN).getOrThrow(TrimPatterns.SILENCE)
            )
          );
          // Enchanted diamond sword.
          ItemStack enchanted = Items.DIAMOND_SWORD.getDefaultInstance();
          enchanted.enchant(FancyPlayerWidget.getGuiProvider().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), 5);
          return widget
            .setName("Fancy")
            .setShowName(true)
            .setSlim(true)
            .setHeadWearable(dyed)
            .setChestWearable(trimmed)
            .setLegsWearable("diamond_leggings[trim={material:\"minecraft:gold\",pattern:\"minecraft:silence\"}] 1")
            .setRightHandItem(enchanted)
            .setHeadFollowsMouse(true)
            .setBodyFollowsMouse(true);
        }
      )
    );
    for (int i = 0; i < widgets.size(); i += 2) {
      MyEntry entry = MyEntry.create(widgets.get(i), i < widgets.size() - 1 ? widgets.get(i + 1) : null, this);
      entry.setX(list.getX() + list.getWidth() / 2 - columnWidth / 2);
      entry.setWidth(columnWidth);
      entry.setY(list.getNextY());
      entry.setHeight(columnHeight + 48);
      list.children.add(entry);
    }
  }

  public static class MyEntry extends OptionsList.Entry {
    public MyEntry(List<OptionsList.OptionInstanceWidget> widgets, Screen screen) {
      super(widgets, screen);
    }

    public static MyEntry create(AbstractWidget leftWidget, @Nullable AbstractWidget rightWidget, Screen screen) {
      return rightWidget == null ? new MyEntry(List.of(new OptionsList.OptionInstanceWidget(leftWidget)), screen) : new MyEntry(List.of(new OptionsList.OptionInstanceWidget(leftWidget), new OptionsList.OptionInstanceWidget(rightWidget)), screen);
    }

    @Override
    public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
      for (int i = 0; i < children.size(); i++) {
        AbstractWidget widget = children.get(i).widget();
        // Vertical centering
        int centeredY = getContentY() + (getHeight() - widget.getHeight()) / 2;
        // Determine horizontal center of the respective column.
        // -80 is the center of the standard left vanilla column.
        // +80 is the center of the standard right vanilla column.
        int columnCenter = i == 0 ? (screen.width / 2 - 80) : (screen.width / 2 + 80);
        // Offset by half of the widget's width to precisely center it inside its column
        int widgetX = columnCenter - widget.getWidth() / 2;
        widget.setPosition(widgetX, centeredY);
        widget.extractRenderState(graphics, mouseX, mouseY, a);
      }
    }
  }
}
