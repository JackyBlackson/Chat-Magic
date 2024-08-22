package com.jackyblackson.modfabric.ui;

import com.jackyblackson.modfabric.config.ChatMagicConfig;
import com.jackyblackson.modfabric.dto.ItemTooptipInfo;
import com.jackyblackson.modfabric.dto.MaterialDisplayInfo;
import com.jackyblackson.modfabric.styles.ChatMagicStyles;
import com.jackyblackson.modfabric.wematerial.WorldEditMaterial;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jackyblackson.modfabric.utils.ColorUtils.rgbaToInt;
import static com.jackyblackson.modfabric.utils.FormatUtils.formatDouble;


@Environment(EnvType.CLIENT)
public class VisualizeScreen extends Screen {



    private final TextFieldWidget chatField;

    private final int itemSize = 16;


    private List<ItemTooptipInfo> tooltipInfoList = new ArrayList<>();


    private String expandNodeId = "";


    private List<Object> materials = new ArrayList<>();

    String oldInput = "";


    int lastX = 0;


    int barChartWidth = 2 * itemSize + 6;





    @Unique
    private void onChatFieldChange(String text) {
        if(!text.trim().startsWith(ChatMagicConfig.prefix)) {
            this.materials.clear();
        } else if(!text.trim().equals(oldInput)){
            String[] args = text.split(" ");
            this.materials.clear();
            int partIndex = 0;
            for (var part : args) {
                if(
                        !part.contains(",") &&
                                !part.contains("%") &&
                                !part.contains("[") &&
                                !part.contains("]") &&
                                !part.contains("#")) {
                    // Parse item ID
                    Identifier itemId = Identifier.tryParse(part);
                    if (itemId == null || !Registries.ITEM.containsId(itemId)) {
                        materials.add(part);
                        continue;
                    }
                }
                {
                    var parsed = WorldEditMaterial.of(part, String.valueOf(partIndex));
                    materials.add(parsed);
                }
                partIndex++;
            }
            oldInput = text.trim();
        }
    }

    @Unique
    private MaterialDisplayInfo parseAndRenderBarChart(DrawContext context, int startX, int startY, int mouseX, int mouseY, WorldEditMaterial target) {
        int x = startX + 3;
        int y = startY + 8;
        int totalHeight = 0;
        int bottomX = x;
        int maxX = x;
        int minY = y;
        boolean isItem = false;
        boolean isFirst = true;

        Random random = new Random(target.getId().hashCode());

        for (var node : target.getMaterialNodes()) {

            int color = rgbaToInt(
                    random.nextInt(40, 200),
                    random.nextInt(40, 200),
                    random.nextInt(40, 200),
                    255
            );
            Double percentage = node.getPercentage();
            var params = node.getParamsList();

            // if it is a none node, then draw the text and skip it
            // TODO: MOVE THE RENDER TO THE END OF THE BAR CHART TO BETTER DISPLAY THE INPUTS
            if (node.getSelector().equals("$none")) {
                if(!params.isEmpty() && params.get(1) instanceof String text){
                    lastX = context.drawTextWithShadow(this.textRenderer, Text.literal(text), this.chatField.getX() + lastX, y, -1);
                }
                if(isFirst) {
                    bottomX = lastX;
                }
                maxX = Math.max(lastX, maxX);
                minY = Math.min(minY, minY - 12);
                continue;
            }


            // if it is an item node
            if (node.getSelector().equals("$item")) {

                String itemIdString = !params.isEmpty() ? (String) params.get(0) : "undefined";
                List<String> blockStateString = params.size() >= 2 ? (List<String>) params.get(1) : null;


                // Parse item ID
                Identifier itemId = Identifier.tryParse(itemIdString);
                if (itemId == null || !Registries.ITEM.containsId(itemId)) {
                    int textEndX = context.drawTextWithShadow(
                            this.textRenderer,
                            Text.literal("?: " + itemIdString).fillStyle(ChatMagicStyles.UNKNOWN_ITEM_ID_STYLE),
                            x + itemSize + 4,
                            y - itemSize + 4,
                            -1);
                    maxX = Math.max(textEndX, maxX);
                } else {// Parse percentage
                    // Create ItemStack
                    ItemStack itemStack = new ItemStack(Registries.ITEM.get(itemId));


                    // Render item icon
                    boolean shouldDrawTooltip = renderItemModel(context, itemStack,
                            x + itemSize + 2, y - itemSize + 2,
                            mouseX, mouseY,
                            itemId
                    );
                    if (shouldDrawTooltip) {
                        ItemTooptipInfo info = ItemTooptipInfo.create()
                                .setCoord(x + itemSize + 1 + itemSize, y)
                                .addField("ItemId", itemId.toString(), ChatMagicStyles.ITEM_ID_STYLE)
                                .addField("Input", itemIdString, ChatMagicStyles.INPUT_STYLE);
                        if (blockStateString != null) {
                            for (String s : blockStateString) {
                                String[] statePair = s.split("=");
                                if (statePair.length == 2)
                                    info.addField(statePair[0], statePair[1], ChatMagicStyles.BLOCKSTATE_STYLE);
                                else info.addField("Unknown", s, ChatMagicStyles.BLOCKSTATE_UNKNOWN_STYLE);
                            }

                        }
                        this.tooltipInfoList.add(info);
                    }

                    // Draw a remainder on the right of the item, when custom blockstate is defined
                    if (blockStateString != null) {
                        context.drawBorder(x + itemSize * 2 + 4 - 1, y - itemSize / 2 , 4, 4, color);
                    }
                }
                if(isFirst) {
                    bottomX = x + itemSize * 2 + 6;
                }
                maxX = Math.max(x + itemSize * 2 + 6, maxX);

                //context.drawItem(itemStack, x+ itemSize + 2, y - itemSize + 2);


            }

            if (node.getSelector().startsWith("#")) {
                int textX = x + itemSize + 4;
                int textY = y - itemSize + 4;
                boolean shouldExpandParams = this.expandNodeId.startsWith(node.getId());
                String selectorDisplay = node.getSelector();
                if(!shouldExpandParams) selectorDisplay = selectorDisplay + " (+" + params.size() + ")";
                int endX = context.drawTextWithShadow(
                        this.textRenderer,
                        Text.literal(selectorDisplay)
                                .fillStyle(
                                        ChatMagicStyles.SELECTOR_STYLE
                                ),
                        textX,
                        textY,
                        -1);
                // if hover on text
                if(
                        (mouseX < endX && mouseX > textX)
                                &&
                                (mouseY > textY && mouseY < textY + 12)
                ) {
                    this.expandNodeId = node.getId();
                }
                textX = endX;
                // if the node should expand recursively
                if(shouldExpandParams) {
                    int pIndex = 0;
                    for(var par : params) {
                        if (par instanceof Double doublePar) {
                            textX+=2;
                            textX = context.drawTextWithShadow(
                                    this.textRenderer,
                                    Text.literal("<" + formatDouble(doublePar) + ">")
                                            .fillStyle(
                                                    ChatMagicStyles.SELECTOR_DOUBLE_PARAM_STYLE
                                            ),
                                    textX,
                                    textY,
                                    -1);
                        } else if (par instanceof WorldEditMaterial material) {
                            textX+=2;
                            if(expandNodeId.startsWith(node.getId() + "-" + pIndex)) {
                                textX = context.drawTextWithShadow(
                                        this.textRenderer,
                                        Text.literal("[")
                                                .fillStyle(
                                                        ChatMagicStyles.SELECTOR_MATERIAL_PARAM_STYLE
                                                ),
                                        textX,
                                        textY,
                                        -1);
                                textX += 2;
                                var chartInfo = parseAndRenderBarChart(
                                        context,
                                        textX, textY,
                                        mouseX, mouseY,
                                        material
                                );
                                textX = chartInfo.bottomEndX();
                                textX += 1;
                                textX = context.drawTextWithShadow(
                                        this.textRenderer,
                                        Text.literal("]")
                                                .fillStyle(
                                                        ChatMagicStyles.SELECTOR_MATERIAL_PARAM_STYLE
                                                ),
                                        textX,
                                        textY,
                                        -1);
                                maxX = Math.max(maxX, chartInfo.endX());
                                minY = Math.min(minY, chartInfo.endY());
                            } else {
                                endX = context.drawTextWithShadow(
                                        this.textRenderer,
                                        Text.literal("[+" + material.getMaterialNodes().size() + " items]")
                                                .fillStyle(
                                                        ChatMagicStyles.SELECTOR_MATERIAL_PARAM_STYLE
                                                ),
                                        textX,
                                        textY,
                                        -1);
                                // if hover on param text
                                if(
                                        (mouseX < endX && mouseX > textX)
                                                &&
                                                (mouseY > textY && mouseY < textY + 12)
                                ) {
                                    this.expandNodeId = node.getId() + "-" + pIndex;
                                }
                                textX = endX;
                            }
                        } else {
                            textX+=2;
                            textX = context.drawTextWithShadow(
                                    this.textRenderer,
                                    Text.literal("(?" + par.toString() + "?)")
                                            .fillStyle(
                                                    ChatMagicStyles.SELECTOR_UNKNOWN_PARAM_STYLE
                                            ),
                                    textX,
                                    textY,
                                    -1);
                        }
                        pIndex++;
                    }
                }

                if(isFirst) {
                    bottomX = textX;
                }
                maxX = Math.max(textX, maxX);
            }

            // Calculate bar width
            int barHeight = (int) ((percentage * ChatMagicConfig.barChartHeight) / 50D); // Example scaling factor, adjust as needed
            totalHeight += barHeight;

            // Render percentage text
            String percentageStr = formatDouble(percentage);
            if(percentageStr.length() <= 2) {
                percentageStr = " " + percentageStr;
            }
            context.drawTextWithShadow(this.textRenderer, Text.literal(percentageStr), x-(percentageStr.length()-2), y - 12, -1);

            // Render bar chart
            context.fill(x, y, x + itemSize, y - barHeight, color);
            y -= barHeight; // Move position for the next bar

            minY = Math.min(minY, y);

            isItem = true;
            isFirst = false;
        }

        return new MaterialDisplayInfo(
                startX, startY,
                maxX, minY,
                bottomX
        );
    }

    @Unique
    private MaterialDisplayInfo renderChatFieldWithItem(DrawContext context, int mouseX, int mouseY, float delta) {

        //TODO: ADJUST parseAndRenderBarChart TO RETURN BOTH HEIGHT AND WIDTH RECURSIVELY
        String inputText = this.chatField.getText();


        lastX = 0;

        int y = this.chatField.getY() - 36;

        int minY = y - 12;

        MaterialDisplayInfo chartInfo = null;
        for(var inputPart : this.materials) {
            // Parse and render item expressions

            if(inputPart instanceof WorldEditMaterial material){
                chartInfo = parseAndRenderBarChart(context, lastX, y, mouseX, mouseY, material);
            } else {
                if(chartInfo != null) {
                    lastX = chartInfo.bottomEndX();
                    chartInfo = null;
                };
                lastX = context.drawTextWithShadow(
                        this.textRenderer,
                        inputPart.toString(),
                        this.chatField.getX() + lastX,
                        y,
                        -1
                );
            }

            // if draw a chart
            if(chartInfo != null){
                lastX = chartInfo.endX();
                minY = Math.min(minY, chartInfo.endY());
            }

            // for the space
            lastX += 2;
        }
        return new MaterialDisplayInfo(
                0, y,
                lastX, minY,
                lastX
        );
    }


    @Unique
    private boolean renderItemModel(
            DrawContext context, ItemStack itemStack, int startX, int startY, int mouseX, int mouseY, Identifier id) {

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);  // 设置颜色
        context.drawItem(itemStack, startX, startY);  // 渲染物品模型
        context.drawItemInSlot(this.textRenderer, itemStack, startX, startY);
        return (startX <= mouseX && mouseX <= startX + itemSize)
                &&
                (startY <= mouseY && mouseY <= startY + itemSize);
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


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {}


    public MaterialDisplayInfo customRender(DrawContext context, int mouseX, int mouseY, float delta) {
        //super.render(context, mouseX, mouseY, delta);
        this.onChatFieldChange(this.chatField.getText());

        this.tooltipInfoList.clear();
        var info = this.renderChatFieldWithItem(context, mouseX, mouseY, delta);
        this.renderTooltip(context);
        return info;
    }

    @Override
    protected void init() {


    }

    public VisualizeScreen(TextFieldWidget chatField) {
        super(Text.literal("Chat Content Visualization"));
        this.chatField = chatField;
    }
}
