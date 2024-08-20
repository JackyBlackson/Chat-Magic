package com.jackyblackson.modfabric.mixin.client;

import com.jackyblackson.modfabric.dto.ItemTooptipInfo;
import com.jackyblackson.modfabric.styles.ChatMagicStyles;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jackyblackson.modfabric.utils.ColorUtils.rgbaToInt;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Shadow
    protected TextFieldWidget chatField;

    @Shadow
    ChatInputSuggestor chatInputSuggestor;


    @Unique
    private final Pattern itemExpressionPattern = Pattern.compile("(\\d+%)?(minecraft:[a-z0-9_]+|\\b[a-z0-9_]+\\b)(\\[.*?\\])?");

    @Unique
    private List<ItemTooptipInfo> tooltipInfoList;


    @Unique
    private int colorIndex = 0;

    @Unique
    private int itemSize = 16;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Nullable
    @Shadow
    protected abstract Style getTextStyleAt(double x, double y);





    @Unique
    private boolean parseAndRenderBarChart(DrawContext context, String inputText, int startX, int startY, int mouseX, int mouseY) {
        Matcher matcher = itemExpressionPattern.matcher(inputText);
        int x = startX + 2;
        int y = startY + 8;
        int totalHeight = 0;

        boolean isItem = false;


        while (matcher.find()) {

            int color = ChatMagicStyles.colors.get(this.colorIndex % ChatMagicStyles.colors.size());

            String percentageString = matcher.group(1);
            String itemIdString = matcher.group(2);
            String blockStateString = matcher.group(3);

            // Parse percentage
            int percentage = percentageString != null ? Integer.parseInt(percentageString.replace("%", "")) : 100;

            // Parse item ID
            Identifier itemId = Identifier.tryParse(itemIdString);
            if (itemId == null || !Registries.ITEM.containsId(itemId)) {
                continue;
            } else {
                isItem = true;
            }

            // Create ItemStack
            ItemStack itemStack = new ItemStack(Registries.ITEM.get(itemId));

            // Calculate bar width
            int barHeight = (percentage * 100) / 50; // Example scaling factor, adjust as needed
            totalHeight += barHeight;

            // Render item icon
            boolean shouldDrawTooltip = renderItemModel(context, itemStack,
                    x+ itemSize + 2, y - itemSize + 2,
                    mouseX, mouseY,
                    "",
                    itemId
                    );
            if(shouldDrawTooltip) {
                ItemTooptipInfo info = ItemTooptipInfo.create()
                        .setCoord(x+ itemSize + 1 + itemSize, y)
                        .addField("ItemId", itemId.toString(), ChatMagicStyles.ITEM_ID_STYLE)
                        .addField("Input", itemIdString, ChatMagicStyles.INPUT_STYLE);
                if(blockStateString != null) {
                    info.addField("State", blockStateString, ChatMagicStyles.BLOCKSTATE_STYLE);
                }
                this.tooltipInfoList.add(info);
            }

            // Draw a remainder on the right of the item, when custom blockstate is defined
            if(blockStateString != null) {
                context.drawBorder(x + itemSize * 2 + 2, y - itemSize/2 + 1, 2, 2, color);
            }

            //context.drawItem(itemStack, x+ itemSize + 2, y - itemSize + 2);

            // Render percentage text
            context.drawTextWithShadow(this.textRenderer, Text.literal(percentageString==null?"100%":percentageString), x, y - 12, -1);

            // Render bar chart
            context.fill(x, y, x + itemSize, y - barHeight, color);
            y -= barHeight; // Move position for the next bar
            this.colorIndex++;
        }

        return isItem;
    }

    @Unique
    private void renderChatFieldWithItem(DrawContext context, int mouseX, int mouseY, float delta) {
        String inputText = this.chatField.getText();

        String[] words = inputText.split(" ");

        int lastX = 0;
        int y = this.chatField.getY() - 36;

        for(String word : words) {
            // Parse and render item expressions
            boolean isItem = parseAndRenderBarChart(context, word, lastX, y, mouseX, mouseY);

            if (isItem) {
                lastX += 2 * itemSize + 6;
            } else {
                lastX = context.drawTextWithShadow(this.textRenderer, Text.literal(word), this.chatField.getX() + lastX, y, -1);
            }

            //for the space
            lastX += 2;
        }
        if(!inputText.isBlank() && !inputText.isEmpty()){
            context.fill(0, y + 14, lastX + 4, y - (100 * 100) / 50 - 4, rgbaToInt(0, 0, 0, 150));
        }
    }


    @Unique
    private boolean renderItemModel(
            DrawContext context, ItemStack itemStack, int startX, int startY, int mouseX, int mouseY, String originalText, Identifier id) {

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);  // 设置颜色
        context.drawItem(itemStack, startX, startY);  // 渲染物品模型
        context.drawItemInSlot(this.textRenderer, itemStack, startX, startY);
        if(
                (startX <= mouseX && mouseX <= startX + itemSize)
                &&
                (startY <= mouseY && mouseY <= startY + itemSize)
        ) {
            return true;
        }
        return false;
    }

    @Unique
    private void renderTooltip(DrawContext context) {

        for(ItemTooptipInfo info : this.tooltipInfoList) {

            List<Text> tooltipTexts = new ArrayList<>();

            for(var entry : info.fieldsMap.entrySet()) {
                tooltipTexts.add(info.getStyledText(entry.getKey()));
            }

            context.drawTooltip(this.textRenderer, tooltipTexts, info.startX, info.startY);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        assert this.client != null;
        this.colorIndex = 0;
        this.tooltipInfoList = new ArrayList<>();
        context.fill(2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));

        this.renderChatFieldWithItem(context, mouseX, mouseY, delta);
        this.renderTooltip(context);

        this.chatField.render(context, mouseX, mouseY, delta);
        this.chatInputSuggestor.render(context, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);

        MessageIndicator messageIndicator = this.client.inGameHud.getChatHud().getIndicatorAt((double)mouseX, (double)mouseY);
        if (messageIndicator != null && messageIndicator.text() != null) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(messageIndicator.text(), 210), mouseX, mouseY);
        } else {
            Style style = this.getTextStyleAt((double)mouseX, (double)mouseY);
            if (style != null && style.getHoverEvent() != null) {
                context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
            }
        }

        ci.cancel();
    }
}

