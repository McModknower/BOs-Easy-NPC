/*
 * Copyright 2022 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.client.screen.spawner;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.screen.components.Graphics;
import de.markusbordihn.easynpc.client.screen.components.PositiveNumberField;
import de.markusbordihn.easynpc.client.screen.components.SaveButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.data.spawner.SpawnerSettingType;
import de.markusbordihn.easynpc.menu.spawner.SpawnerMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandlerManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpawnerScreen<T extends SpawnerMenu> extends AbstractContainerScreen<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String SPAWNER_PREFIX = "spawner.";
  private final SpawnerMenu spawnerMenu;
  private EditBox delayEdit;
  private Button delaySaveButton;
  private EditBox maxNearbyEntitiesEdit;
  private Button maxNearbyEntitiesSaveButton;
  private EditBox requiredPlayerRangeEdit;
  private Button requiredPlayerRangeSaveButton;
  private EditBox spawnCountEdit;
  private Button spawnCountSaveButton;
  private EditBox spawnerDespawnRangeEdit;
  private Button spawnerDespawnRangeSaveButton;
  private EditBox spawnerRangeEdit;
  private Button spawnerRangeSaveButton;
  private boolean updatedDataFields = false;

  public SpawnerScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.spawnerMenu = menu;
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageHeight = 243;
    this.imageWidth = 260;

    // Core Positions
    this.topPos = ((this.height - this.imageHeight) / 2) + 2;
    this.leftPos = (this.width - this.imageWidth) / 2;
    this.inventoryLabelY = this.imageHeight - 92;

    int settingsLeft = this.leftPos + 196;
    int settingsTop = this.topPos + 20;
    int settingsWidth = 35;

    // Spawner Player Range Edit
    this.requiredPlayerRangeEdit =
        this.addRenderableWidget(
            new PositiveNumberField(
                this.font,
                settingsLeft,
                settingsTop,
                settingsWidth,
                this.spawnerMenu.getRequiredPlayerRange(),
                3));
    this.requiredPlayerRangeEdit.setResponder(
        value -> {
          if (this.requiredPlayerRangeSaveButton != null) {
            this.requiredPlayerRangeSaveButton.active =
                value != null
                    && !value.isEmpty()
                    && Integer.parseInt(value) != this.spawnerMenu.getRequiredPlayerRange();
          }
        });
    this.requiredPlayerRangeSaveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.requiredPlayerRangeEdit.getX() + this.requiredPlayerRangeEdit.getWidth() + 5,
                this.requiredPlayerRangeEdit.getY() - 1,
                button -> {
                  if (this.requiredPlayerRangeEdit != null) {
                    this.changeSpawnerSetting(
                        this.spawnerMenu.getSpawnerPosition(),
                        SpawnerSettingType.REQUIRED_PLAYER_RANGE,
                        Integer.parseInt(this.requiredPlayerRangeEdit.getValue()));
                    this.requiredPlayerRangeSaveButton.active = false;
                  }
                }));

    // Spawner Delay Edit
    settingsTop += 20;
    this.delayEdit =
        this.addRenderableWidget(
            new PositiveNumberField(
                this.font,
                settingsLeft,
                settingsTop,
                settingsWidth,
                this.spawnerMenu.getDelay(),
                4));
    this.delayEdit.setResponder(
        value -> {
          if (this.delaySaveButton != null) {
            this.delaySaveButton.active =
                value != null
                    && !value.isEmpty()
                    && Integer.parseInt(value) != this.spawnerMenu.getDelay();
          }
        });
    this.delaySaveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.delayEdit.getX() + this.delayEdit.getWidth() + 5,
                this.delayEdit.getY() - 1,
                button -> {
                  if (this.delayEdit != null) {
                    this.changeSpawnerSetting(
                        this.spawnerMenu.getSpawnerPosition(),
                        SpawnerSettingType.DELAY,
                        Integer.parseInt(this.delayEdit.getValue()));
                    this.delaySaveButton.active = false;
                  }
                }));

    // Spawner Max Nearby Entities Edit
    settingsTop += 20;
    this.maxNearbyEntitiesEdit =
        this.addRenderableWidget(
            new PositiveNumberField(
                this.font,
                settingsLeft,
                settingsTop,
                settingsWidth,
                this.spawnerMenu.getMaxNearbyEntities(),
                2));
    this.maxNearbyEntitiesEdit.setResponder(
        value -> {
          if (this.maxNearbyEntitiesSaveButton != null) {
            this.maxNearbyEntitiesSaveButton.active =
                value != null
                    && !value.isEmpty()
                    && Integer.parseInt(value) != this.spawnerMenu.getMaxNearbyEntities();
          }
        });
    this.maxNearbyEntitiesSaveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.maxNearbyEntitiesEdit.getX() + this.maxNearbyEntitiesEdit.getWidth() + 5,
                this.maxNearbyEntitiesEdit.getY() - 1,
                button -> {
                  if (this.maxNearbyEntitiesEdit != null) {
                    this.changeSpawnerSetting(
                        this.spawnerMenu.getSpawnerPosition(),
                        SpawnerSettingType.MAX_NEARBY_ENTITIES,
                        Integer.parseInt(this.maxNearbyEntitiesEdit.getValue()));
                    this.maxNearbyEntitiesSaveButton.active = false;
                  }
                }));

    // Spawner Numbers Per Spawn Interval Edit
    settingsTop += 20;
    this.spawnCountEdit =
        this.addRenderableWidget(
            new PositiveNumberField(
                this.font,
                settingsLeft,
                settingsTop,
                settingsWidth,
                this.spawnerMenu.getSpawnCount(),
                3));
    this.spawnCountEdit.setResponder(
        value -> {
          if (this.spawnCountSaveButton != null) {
            this.spawnCountSaveButton.active =
                value != null
                    && !value.isEmpty()
                    && Integer.parseInt(value) != this.spawnerMenu.getSpawnCount();
          }
        });
    this.spawnCountSaveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.spawnCountEdit.getX() + this.spawnCountEdit.getWidth() + 5,
                this.spawnCountEdit.getY() - 1,
                button -> {
                  if (this.spawnCountEdit != null) {
                    this.changeSpawnerSetting(
                        this.spawnerMenu.getSpawnerPosition(),
                        SpawnerSettingType.SPAWN_COUNT,
                        Integer.parseInt(this.spawnCountEdit.getValue()));
                    this.spawnCountSaveButton.active = false;
                  }
                }));

    // Spawner Range Edit
    settingsTop += 20;
    this.spawnerRangeEdit =
        this.addRenderableWidget(
            new PositiveNumberField(
                this.font,
                settingsLeft,
                settingsTop,
                settingsWidth,
                this.spawnerMenu.getSpawnRange(),
                3));
    this.spawnerRangeEdit.setResponder(
        value -> {
          if (this.spawnerRangeSaveButton != null && this.spawnerDespawnRangeEdit != null) {
            this.spawnerRangeSaveButton.active =
                value != null
                    && !value.isEmpty()
                    && Integer.parseInt(value) != this.spawnerMenu.getSpawnRange()
                    && Integer.parseInt(value)
                        < Integer.parseInt(this.spawnerDespawnRangeEdit.getValue());
          }
        });
    this.spawnerRangeSaveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.spawnerRangeEdit.getX() + this.spawnerRangeEdit.getWidth() + 5,
                this.spawnerRangeEdit.getY() - 1,
                button -> {
                  if (this.spawnerRangeEdit != null) {
                    this.changeSpawnerSetting(
                        this.spawnerMenu.getSpawnerPosition(),
                        SpawnerSettingType.SPAWN_RANGE,
                        Integer.parseInt(this.spawnerRangeEdit.getValue()));
                    this.spawnerRangeSaveButton.active = false;
                  }
                }));

    // Spawner Despawn Range Edit
    settingsTop += 20;
    this.spawnerDespawnRangeEdit =
        this.addRenderableWidget(
            new PositiveNumberField(
                this.font,
                settingsLeft,
                settingsTop,
                settingsWidth,
                this.spawnerMenu.getDespawnRange(),
                3));
    this.spawnerDespawnRangeEdit.setResponder(
        value -> {
          if (this.spawnerDespawnRangeSaveButton != null && this.spawnerRangeEdit != null) {
            this.spawnerDespawnRangeSaveButton.active =
                value != null
                    && !value.isEmpty()
                    && Integer.parseInt(value) != this.spawnerMenu.getDespawnRange()
                    && Integer.parseInt(value) > Integer.parseInt(this.spawnerRangeEdit.getValue());
          }
        });
    this.spawnerDespawnRangeSaveButton =
        this.addRenderableWidget(
            new SaveButton(
                this.spawnerDespawnRangeEdit.getX() + this.spawnerDespawnRangeEdit.getWidth() + 5,
                this.spawnerDespawnRangeEdit.getY() - 1,
                button -> {
                  if (this.spawnerDespawnRangeEdit != null) {
                    this.changeSpawnerSetting(
                        this.spawnerMenu.getSpawnerPosition(),
                        SpawnerSettingType.DESPAWN_RANGE,
                        Integer.parseInt(this.spawnerDespawnRangeEdit.getValue()));
                    this.spawnerDespawnRangeSaveButton.active = false;
                  }
                }));
  }

  @Override
  protected void containerTick() {
    super.containerTick();
    if (!this.updatedDataFields) {
      this.updateDataFields();
      this.updatedDataFields = true;
    }
  }

  protected void updateDataFields() {
    if (this.spawnerRangeEdit != null) {
      this.spawnerRangeEdit.setValue(String.valueOf(this.spawnerMenu.getSpawnRange()));
    }
    if (this.spawnerDespawnRangeEdit != null) {
      this.spawnerDespawnRangeEdit.setValue(String.valueOf(this.spawnerMenu.getDespawnRange()));
    }
    if (this.requiredPlayerRangeEdit != null) {
      this.requiredPlayerRangeEdit.setValue(
          String.valueOf(this.spawnerMenu.getRequiredPlayerRange()));
    }
    if (this.delayEdit != null) {
      this.delayEdit.setValue(String.valueOf(this.spawnerMenu.getDelay()));
    }
    if (this.maxNearbyEntitiesEdit != null) {
      this.maxNearbyEntitiesEdit.setValue(String.valueOf(this.spawnerMenu.getMaxNearbyEntities()));
    }
    if (this.spawnCountEdit != null) {
      this.spawnCountEdit.setValue(String.valueOf(this.spawnerMenu.getSpawnCount()));
    }
  }

  protected void changeSpawnerSetting(
      BlockPos blockPos, SpawnerSettingType spawnerSettingType, int value) {
    NetworkMessageHandlerManager.getServerHandler()
        .changeSpawnerSettings(blockPos, spawnerSettingType, value);
  }

  protected void renderLabels(GuiGraphics guiGraphics) {
    int labelOffsetX = -180;
    int labelOffsetY = 4;

    if (this.spawnerRangeEdit != null) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          SPAWNER_PREFIX + SpawnerSettingType.SPAWN_RANGE.name().toLowerCase(java.util.Locale.ROOT),
          this.spawnerRangeEdit.getX() + labelOffsetX,
          this.spawnerRangeEdit.getY() + labelOffsetY);
    }

    if (this.spawnerDespawnRangeEdit != null) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          SPAWNER_PREFIX + SpawnerSettingType.DESPAWN_RANGE.name().toLowerCase(java.util.Locale.ROOT),
          this.spawnerDespawnRangeEdit.getX() + labelOffsetX,
          this.spawnerDespawnRangeEdit.getY() + labelOffsetY);
    }

    if (this.requiredPlayerRangeEdit != null) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          SPAWNER_PREFIX + SpawnerSettingType.REQUIRED_PLAYER_RANGE.name().toLowerCase(java.util.Locale.ROOT),
          this.requiredPlayerRangeEdit.getX() + labelOffsetX,
          this.requiredPlayerRangeEdit.getY() + labelOffsetY);
    }

    if (this.delayEdit != null) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          SPAWNER_PREFIX + SpawnerSettingType.DELAY.name().toLowerCase(java.util.Locale.ROOT),
          this.delayEdit.getX() + labelOffsetX,
          this.delayEdit.getY() + labelOffsetY);
    }

    if (this.maxNearbyEntitiesEdit != null) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          SPAWNER_PREFIX + SpawnerSettingType.MAX_NEARBY_ENTITIES.name().toLowerCase(java.util.Locale.ROOT),
          this.maxNearbyEntitiesEdit.getX() + labelOffsetX,
          this.maxNearbyEntitiesEdit.getY() + labelOffsetY);
    }

    if (this.spawnCountEdit != null) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          SPAWNER_PREFIX + SpawnerSettingType.SPAWN_COUNT.name().toLowerCase(java.util.Locale.ROOT),
          this.spawnCountEdit.getX() + labelOffsetX,
          this.spawnCountEdit.getY() + labelOffsetY);
    }

    Text.drawConfigString(
        guiGraphics,
        this.font,
        SPAWNER_PREFIX + "preset_item",
        this.leftPos + SpawnerMenu.presetItemSlotX - 20,
        this.topPos + SpawnerMenu.presetItemSlotY + 22,
        this.spawnerMenu.getPresetItem().isEmpty()
            ? Constants.FONT_COLOR_RED
            : Constants.FONT_COLOR_GRAY);
  }

  @Override
  public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    Text.drawString(
        guiGraphics,
        this.font,
        this.title,
        this.titleLabelX + 80,
        this.titleLabelY + 2,
        Constants.FONT_COLOR_BLACK);
    Text.drawString(
        guiGraphics,
        this.font,
        "(" + this.spawnerMenu.getSpawnerPosition().toShortString() + ")",
        this.titleLabelX + 180,
        this.titleLabelY + 2,
        Constants.FONT_COLOR_GRAY);
    Text.drawString(
        guiGraphics,
        this.font,
        this.playerInventoryTitle,
        this.inventoryLabelX + 90,
        this.inventoryLabelY - 10,
        Constants.FONT_COLOR_BLACK);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderLabels(guiGraphics);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    // Background
    Graphics.blit(guiGraphics, Constants.TEXTURE_DEMO_BACKGROUND, leftPos, topPos, 0, 0, 210, 160);
    Graphics.blit(
        guiGraphics, Constants.TEXTURE_DEMO_BACKGROUND, leftPos + 153, topPos, 132, 0, 120, 160);
    Graphics.blit(
        guiGraphics, Constants.TEXTURE_DEMO_BACKGROUND, leftPos, topPos + 77, 0, 5, 210, 170);
    Graphics.blit(
        guiGraphics,
        Constants.TEXTURE_DEMO_BACKGROUND,
        leftPos + 153,
        topPos + 77,
        132,
        5,
        120,
        170);

    // Player Inventory Slots and Hotbar Slots
    Graphics.blit(
        guiGraphics,
        Constants.TEXTURE_INVENTORY,
        this.leftPos + 97,
        this.topPos + 152,
        7,
        83,
        162,
        54);
    Graphics.blit(
        guiGraphics,
        Constants.TEXTURE_INVENTORY,
        this.leftPos + 97,
        this.topPos + 212,
        7,
        141,
        162,
        18);

    // Easy NPC Preset Slot.
    Graphics.blit(
        guiGraphics,
        Constants.TEXTURE_INVENTORY,
        leftPos + SpawnerMenu.presetItemSlotX - 1,
        topPos + SpawnerMenu.presetItemSlotY - 1,
        76,
        61,
        18,
        18);

    // Helper Icons for empty Easy NPC Preset Slot.
    if (this.spawnerMenu.getPresetItem().isEmpty()) {
      Graphics.blit(
          guiGraphics,
          Constants.TEXTURE_SPAWNER,
          leftPos + SpawnerMenu.presetItemSlotX + 20,
          topPos + SpawnerMenu.presetItemSlotY + 2,
          0,
          0,
          32,
          12);
      Graphics.blit(
          guiGraphics,
          Constants.TEXTURE_SPAWNER,
          leftPos + SpawnerMenu.presetItemSlotX + 1,
          topPos + SpawnerMenu.presetItemSlotY,
          2,
          12,
          16,
          16);
    }
  }
}
