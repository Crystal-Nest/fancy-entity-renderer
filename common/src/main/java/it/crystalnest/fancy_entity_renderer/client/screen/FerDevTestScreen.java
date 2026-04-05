package it.crystalnest.fancy_entity_renderer.client.screen;

import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Small in-dev showcase screen for FER player widgets.
 */
public final class FerDevTestScreen extends Screen {
  private static final Component TITLE = Component.literal("FER Dev Test");
  private static final Component DESCRIPTION = Component.literal("Quick in-dev showcase for Fancy Entity Renderer player widgets.");
  private static final int PANEL_COLOR = 0xA0101218;
  private static final int CARD_COLOR = 0x80303642;
  private static final int OUTLINE_COLOR = 0xA06D7B8A;

  private final Screen parent;
  private final List<DemoCard> cards = new ArrayList<>();

  public FerDevTestScreen(Screen parent) {
    super(TITLE);
    this.parent = parent;
  }

  @Override
  protected void init() {
    super.init();
    cards.clear();

    addRenderableOnly(new StringWidget(0, 16, width, 12, TITLE, font));
    addRenderableOnly(new MultiLineTextWidget(0, 34, DESCRIPTION, font).setMaxWidth(width - 80).setCentered(true));

    int margin = 28;
    int gapX = 28;
    int titleY = 72;
    int labelHeight = 12;
    int firstRowHeight = 188;
    int secondRowHeight = 156;
    int rowGap = 32;
    int columnWidth = Math.max(120, (width - margin * 2 - gapX) / 2);
    int leftX = margin;
    int rightX = margin + columnWidth + gapX;
    int secondRowY = titleY + labelHeight + firstRowHeight + rowGap;

    addCard(
      leftX,
      titleY,
      Math.min(columnWidth, 158),
      firstRowHeight,
      Component.literal("Heavy Tank"),
      widget -> widget
        .setName("Heavy Tank")
        .setShowName(true)
        .setPinName(true)
        .setBodyRotation(0, -18, 0)
        .setHeadRotation(-6, 12, 0)
        .setHeadWearable(Items.NETHERITE_HELMET)
        .setChestWearable(Items.NETHERITE_CHESTPLATE)
        .setLegsWearable(Items.NETHERITE_LEGGINGS)
        .setFeetWearable(Items.NETHERITE_BOOTS)
        .setRightHandItem(Items.MACE)
        .setLeftHandItem(Items.SHIELD)
    );

    addCard(
      rightX,
      titleY,
      Math.min(columnWidth, 174),
      firstRowHeight + 24,
      Component.literal("Mouse-Follow Rogue"),
      widget -> widget
        .setName("Mouse-Follow Rogue")
        .setShowName(true)
        .setSlim(true)
        .setPose(Pose.CROUCHING)
        .setBodyFollowsMouse(true)
        .setHeadFollowsMouse(true)
        .setMoving(true)
        .setWalkingSpeed(1.3F)
        .setRightHandItem(Items.CROSSBOW)
        .setLeftHandItem(Items.TOTEM_OF_UNDYING)
        .setHeadWearable(Items.CHAINMAIL_HELMET)
        .setChestWearable(Items.ELYTRA)
        .setFeetWearable(Items.CHAINMAIL_BOOTS)
    );

    addCard(
      leftX,
      secondRowY,
      Math.min(columnWidth, 126),
      secondRowHeight,
      Component.literal("Tiny Trouble"),
      widget -> widget
        .setName("Tiny Trouble")
        .setShowName(true)
        .setBaby(true)
        .setSlim(true)
        .setOnFire(true)
        .setBodyRotation(-8, 26, 0)
        .setHeadRotation(6, -18, 0)
        .setRightHandItem(Items.TRIDENT)
        .setLeftHandItem(Items.GOLDEN_APPLE)
        .setHeadWearable(Items.TURTLE_HELMET)
        .setLeftParrot(Parrot.Variant.BLUE)
    );

    addCard(
      rightX,
      secondRowY,
      Math.min(columnWidth, 166),
      secondRowHeight + 16,
      Component.literal("Spin Demo"),
      widget -> widget
        .setName("Spin Demo")
        .setShowName(true)
        .setPose(Pose.SPIN_ATTACK)
        .setAttackArm(HumanoidArm.RIGHT)
        .setAttackTime(0.35F)
        .setMoving(true)
        .setMovementSpeed(1.8F)
        .setGlowing(0x55FFAA)
        .setRightHandItem(Items.DIAMOND_SWORD)
        .setLeftHandItem(Items.SHIELD)
        .setChestWearable(Items.DIAMOND_CHESTPLATE)
        .setLegsWearable(Items.DIAMOND_LEGGINGS)
        .setFeetWearable(Items.DIAMOND_BOOTS)
        .setRightArmPose(HumanoidModel.ArmPose.ITEM)
    );

    addRenderableWidget(
      Button.builder(Component.literal("Back"), button -> onClose())
        .bounds(width - 108, height - 28, 88, 20)
        .build()
    );
  }

  @Override
  public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick) {
    extractTransparentBackground(gfx);
    gfx.fill(12, 12, width - 12, height - 12, PANEL_COLOR);
    for (DemoCard card : cards) {
      gfx.fill(card.x() - 10, card.labelY() - 8, card.x() + card.width() + 10, card.y() + card.height() + 10, CARD_COLOR);
      gfx.outline(card.x() - 10, card.labelY() - 8, card.width() + 20, card.height() + 18 + (card.y() - card.labelY()), OUTLINE_COLOR);
    }
  }

  @Override
  public void onClose() {
    if (minecraft != null) {
      minecraft.setScreen(parent);
    }
  }

  private void addCard(int x, int labelY, int width, int height, Component label, Consumer<FancyPlayerWidget> config) {
    addRenderableOnly(new StringWidget(x, labelY, width, 12, label, font));
    FancyPlayerWidget widget = addRenderableWidget(new FancyPlayerWidget(x, labelY + 18, width, height));
    config.accept(widget);
    cards.add(new DemoCard(x, labelY, labelY + 18, width, height));
  }

  private record DemoCard(int x, int labelY, int y, int width, int height) {}
}
