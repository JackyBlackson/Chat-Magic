package com.jackyblackson.modfabric.mixin.client;

import com.jackyblackson.modfabric.config.ChatMagicConfig;
import com.jackyblackson.modfabric.dto.ItemTooptipInfo;
import com.jackyblackson.modfabric.dto.MaterialDisplayInfo;
import com.jackyblackson.modfabric.styles.ChatMagicStyles;
import com.jackyblackson.modfabric.ui.VisualizeScreen;
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
                return sliderMinValue + (this.value * sliderMaxValue);
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                System.out.println("!!!!!!!!! ONCLICK !!!!!!!!!");
            }
        };

        this.addDrawableChild(SliderWidget);

    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        assert this.client != null;
        this.customChatOverlayScreen.init(this.client, this.client.getWindow().getWidth(), this.client.getWindow().getHeight());
        if (this.customChatOverlayScreen != null) {
            this.customChatOverlayScreen.render(context, mouseX, mouseY, delta);
        }
    }
}

