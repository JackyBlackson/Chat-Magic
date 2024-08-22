package com.jackyblackson.modfabric.mixin.client;

import com.jackyblackson.modfabric.config.ChatMagicConfig;
import com.jackyblackson.modfabric.dto.ItemTooptipInfo;
import com.jackyblackson.modfabric.dto.MaterialDisplayInfo;
import com.jackyblackson.modfabric.styles.ChatMagicStyles;
import com.jackyblackson.modfabric.ui.VisualizeScreen;
import com.jackyblackson.modfabric.utils.ColorUtils;
import com.jackyblackson.modfabric.wematerial.WorldEditMaterial;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jackyblackson.modfabric.utils.ColorUtils.rgbaToInt;
import static com.jackyblackson.modfabric.utils.FormatUtils.formatDouble;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Unique
    SliderWidget SliderWidget;

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private VisualizeScreen customChatOverlayScreen;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.customChatOverlayScreen = new VisualizeScreen(this.chatField);
        assert this.client != null;
        this.customChatOverlayScreen.init(this.client, this.client.getWindow().getWidth(), this.client.getWindow().getHeight());


        // 添加滑块，范围是50到200
        int sliderWidth = 120;
        int sliderHeight = 20;
        int sliderMarginTop = 20;
        int sliderMarginRight = 20;
        double sliderMinValue = 50D;
        double sliderMaxValue = 150D;
        // 返回当前滑块值
        this.SliderWidget = new SliderWidget(
                this.width - sliderMarginRight - sliderWidth,
                sliderMarginTop,
                sliderWidth, sliderHeight,
                Text.literal("Bar Chart Height: " + formatDouble(ChatMagicConfig.barChartHeight)),
                (ChatMagicConfig.barChartHeight - sliderMinValue) / (sliderMaxValue - sliderMinValue)
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Chart Height: " + formatDouble(ChatMagicConfig.barChartHeight)));
            }

            @Override
            protected void applyValue() {
                ChatMagicConfig.barChartHeight = getSliderValue();
            }

            // 返回当前滑块值
            private Double getSliderValue() {
                return sliderMinValue + (this.value * (sliderMaxValue - sliderMinValue));
            }
        };



        this.addDrawableChild(SliderWidget);

        this.addDrawableChild(new SliderWidget(
                this.width - sliderMarginRight - sliderWidth,
                sliderMarginTop + sliderHeight + sliderMarginTop/2,
                sliderWidth, sliderHeight,
                Text.literal("Bg Transparency: " + formatDouble(ChatMagicConfig.backgroundAlpha)),
                (double) (ChatMagicConfig.backgroundAlpha) / 255
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Bg Transparency: " + formatDouble(ChatMagicConfig.backgroundAlpha)));
            }

            @Override
            protected void applyValue() {
                ChatMagicConfig.backgroundAlpha = getSliderValue();
            }

            // 返回当前滑块值
            private int getSliderValue() {
                return (int) (this.value * 255);
            }
        });

    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        assert this.client != null;
        this.customChatOverlayScreen.init(this.client, this.client.getWindow().getWidth(), this.client.getWindow().getHeight());
        if (this.customChatOverlayScreen != null) {

            var info = this.customChatOverlayScreen.customRender(context, mouseX, mouseY, delta);
            int lastX = info.endX();
            if(lastX != 0){
                lastX += 4;
                lastX = context.drawText(this.textRenderer,
                        Text.literal("@ Chat Magic by Jacky_Blackson").fillStyle(
                                Style.EMPTY
                                        .withClickEvent(new ClickEvent(
                                                ClickEvent.Action.OPEN_URL,
                                                "https://modrinth.com/mod/chat-magic"
                                        ))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Text.literal("Click to open publish page!")
                                        ))
                                        .withBold(true)
                                        .withColor(ColorUtils.rgbToInt(147, 147, 147))
                        ),
                        lastX,
                        info.startY(),
                        -1,
                        true);
                context.fill(0, info.startY() + 14, lastX + 4, info.endY() - 4, rgbaToInt(0, 0, 0, ChatMagicConfig.backgroundAlpha));
            }
            // re-render all the stuffs, in order to put colors on the top
            this.customChatOverlayScreen.customRender(context, mouseX, mouseY, delta);
        }
    }
}

