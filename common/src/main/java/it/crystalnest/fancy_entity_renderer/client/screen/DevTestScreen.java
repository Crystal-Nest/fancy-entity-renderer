package it.crystalnest.fancy_entity_renderer.client.screen;

import it.crystalnest.fancy_entity_renderer.api.entity.RenderMode;
import it.crystalnest.fancy_entity_renderer.api.entity.player.FancyPlayerWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Small in-dev showcase screen for FER player widgets.
 */
public final class DevTestScreen extends OptionsSubScreen {
  private static final int COLUMN_WIDTH = 17 * 4;
  private static final int COLUMN_HEIGHT = 24 * 4;
  private static final int ENTRY_HEIGHT = COLUMN_HEIGHT + 64;
  private static final int COLUMN_CENTER_OFFSET = 80;

  private FancyWidgetList list;

  /**
   * @param parent parent screen.
   * @param options game options.
   */
  public DevTestScreen(Screen parent, Options options) {
    super(parent, options, Component.literal("FER Dev Test"));
  }

  /**
   * Builds a {@link FancyPlayerWidget} with the provided configuration.
   *
   * @param configuration configuration function.
   * @return new {@link FancyPlayerWidget}.
   */
  private static FancyPlayerWidget buildWidget(UnaryOperator<FancyPlayerWidget> configuration) {
    return configuration.apply(new FancyPlayerWidget(0, 0, COLUMN_WIDTH, COLUMN_HEIGHT).setShowName(true).setSlim(true));
  }

  @Override
  protected void init() {
    super.init();
    list = new FancyWidgetList(minecraft, width, height, 32, height - 32, ENTRY_HEIGHT);
    addWidget(list);

    List<FancyPlayerWidget> widgets = List.of(
      buildWidget(widget -> widget
        .setName("Suffering Follower")
        .setPinName(true)
        .setBodyFollowsMouse(true)
        .setHeadFollowsMouse(true)
        .setArrowCount(10)
        .setStingerCount(10)
      ),
      buildWidget(widget -> widget
        .setName("Burning Man")
        .setOnFire(true)
        .setSlim(false)
      ),
      buildWidget(widget -> widget
        .setName("Wacky")
        .setHeadRotation(15, 0, 0)
        .setBodyRotation(0, 15, 0)
        .setRightArmRotation(0, 0, 15)
        .setLeftArmRotation(0, 15, 0)
        .setRightLegRotation(15, 0, 0)
        .setLeftLegRotation(0, 15, 0)
      ),
      buildWidget(widget -> widget
        .setName("Armored Wacky")
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
        .setRightHandItem(Items.NETHERITE_AXE)
        .setLeftHandItem(Items.SHIELD)
      ),
      buildWidget(widget -> widget
        .setName("Missing")
        .setShowHead(false)
        .setShowBody(false)
        .setShowLeftArm(false)
        .setShowRightArm(false)
        .setShowRightLeg(false)
        .setShowLeftLeg(false)
      ),
      buildWidget(widget -> widget
        .setName("Armored Follower")
        .setSlim(false)
        .setHeadFollowsMouse(true)
        .setHeadWearable(Items.CHAINMAIL_HELMET)
        .setChestWearable(Items.ELYTRA)
        .setLegsWearable(Items.DIAMOND_LEGGINGS)
        .setFeetWearable(Items.DIAMOND_BOOTS)
        .setRightHandItem(Items.DIAMOND_SWORD)
        .setLeftParrot(Parrot.Variant.BLUE)
      ),
      buildWidget(widget -> widget
        .setName("Spectator")
        .setRenderMode(RenderMode.SPECTATOR)
      ),
      buildWidget(widget -> widget
        .setName("Ghost")
        .setRenderMode(RenderMode.GHOST)
      ),
      buildWidget(widget -> widget
        .setName("Head Over Heels 4 U")
        .setUpsideDown(true)
        .setBodyFollowsMouse(true)
        .setHeadFollowsMouse(true)
        .setParrots(Parrot.Variant.RED_BLUE, Parrot.Variant.GREEN)
      ),
      buildWidget(widget -> widget
        .setName("Copycat")
        .copyLocalPlayer()
      ),
      buildWidget(widget -> widget
        .copyPlayer("Crystal_Spider_")
        .setMovementSpeed(1)
        .setMoving(true)
      ),
      buildWidget(widget -> widget
        .setName("Catfish")
        .copyPlayer("Deadmau5")
        .setMovementSpeed(1)
        .setWalkingSpeed(1)
      ),
      buildWidget(widget -> widget
        .setName("Sleepy")
        .setLeftParrot(Parrot.Variant.YELLOW_BLUE)
        .setPose(Pose.SLEEPING)
      ),
      buildWidget(widget -> widget
        .setName("Baby Sleepy")
        .setBaby(true)
        .setLeftParrot(Parrot.Variant.YELLOW_BLUE)
        .setPose(Pose.SLEEPING)
      ),
      buildWidget(widget -> widget
        .setName("Swim Swim")
        .setRightHandItem(Items.NAUTILUS_SHELL)
        .setPose(Pose.SWIMMING)
        .setMovementSpeed(1)
        .setWalkingSpeed(1)
        .setMoving(true)
      ),
      buildWidget(widget -> widget
        .setName("Baby Swim Swim")
        .setBaby(true)
        .setRightHandItem(Items.NAUTILUS_SHELL)
        .setPose(Pose.SWIMMING)
        .setMovementSpeed(1)
        .setWalkingSpeed(1)
        .setMoving(true)
      ),
      buildWidget(widget -> widget
        .setName("Vortex")
        .setRightHandItem(Items.TRIDENT)
        .setPose(Pose.SPIN_ATTACK)
        .setMovementSpeed(0.5F)
        .setMoving(true)
      ),
      buildWidget(widget -> widget
        .setName("Baby Vortex")
        .setBaby(true)
        .setRightHandItem(Items.TRIDENT)
        .setPose(Pose.SPIN_ATTACK)
        .setMovementSpeed(0.5F)
        .setMoving(true)
      ),
      buildWidget(widget -> widget
        .setName("Croucher")
        .setHeadWearable(Items.GOLDEN_HELMET)
        .setParrots(Parrot.Variant.GREEN, Parrot.Variant.GRAY)
        .setPose(Pose.CROUCHING)
        .setSlim(false)
      ),
      buildWidget(widget -> widget
        .setName("Baby Croucher")
        .setBaby(true)
        .setHeadWearable(Items.GOLDEN_HELMET)
        .setChestWearable(Items.GOLDEN_CHESTPLATE)
        .setLegsWearable(Items.LEATHER_LEGGINGS)
        .setFeetWearable(Items.IRON_BOOTS)
        .setParrots(Parrot.Variant.GREEN, Parrot.Variant.GRAY)
        .setPose(Pose.CROUCHING)
      ),
      buildWidget(widget -> widget
        .setName("Done For")
        .setChestWearable(Items.ELYTRA)
        .setParrots(Parrot.Variant.GREEN, Parrot.Variant.GRAY)
        .setPose(Pose.DYING)
      ),
      buildWidget(widget -> widget
        .setName("Baby Done For")
        .setBaby(true)
        .setSlim(false)
        .copyPlayer("Crystal_Spider_")
        .setChestWearable(Items.ELYTRA)
        .setParrots(Parrot.Variant.GREEN, Parrot.Variant.GRAY)
        .setPose(Pose.DYING)
      )
    );

    Iterator<FancyPlayerWidget> iterator = widgets.iterator();
    while (iterator.hasNext()) {
      FancyPlayerWidget left = iterator.next();
      FancyPlayerWidget right = iterator.hasNext() ? iterator.next() : null;
      list.addFancyEntry(new FancyWidgetEntry(left, right));
    }

    addRenderableWidget(
      Button.builder(Component.translatable("gui.done"), button -> onClose())
        .bounds(width / 2 - 100, height - 27, 200, 20)
        .build()
    );
  }

  @Override
  public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
    renderBackground(gfx);
    list.render(gfx, mouseX, mouseY, partialTick);
    gfx.drawCenteredString(font, title, width / 2, 20, 16777215);
    super.render(gfx, mouseX, mouseY, partialTick);
  }

  /**
   * Scrollable list that hosts paired player widgets.
   */
  private static final class FancyWidgetList extends ContainerObjectSelectionList<FancyWidgetEntry> {
    private FancyWidgetList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
      super(minecraft, width, height, top, bottom, itemHeight);
      centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
      return 400;
    }

    @Override
    protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
    }

    private void addFancyEntry(FancyWidgetEntry entry) {
      addEntry(entry);
    }
  }

  /**
   * List row with one or two centered widgets.
   */
  private static final class FancyWidgetEntry extends ContainerObjectSelectionList.Entry<FancyWidgetEntry> {
    private final List<AbstractWidget> children;

    private FancyWidgetEntry(AbstractWidget leftWidget, @Nullable AbstractWidget rightWidget) {
      children = rightWidget == null ? List.of(leftWidget) : List.of(leftWidget, rightWidget);
    }

    @Override
    public void render(GuiGraphics gfx, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTick) {
      int rowCenter = rowLeft + rowWidth / 2;
      for (int i = 0; i < children.size(); i++) {
        AbstractWidget widget = children.get(i);
        int centeredY = rowTop + (rowHeight - widget.getHeight()) / 2;
        int columnCenter = i == 0 ? (rowCenter - COLUMN_CENTER_OFFSET) : (rowCenter + COLUMN_CENTER_OFFSET);
        int widgetX = columnCenter - widget.getWidth() / 2;
        widget.setPosition(widgetX, centeredY);
        widget.render(gfx, mouseX, mouseY, partialTick);
      }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
      return children;
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
      return children;
    }
  }
}
