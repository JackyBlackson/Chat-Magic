package com.jackyblackson.modfabric.styles;

import com.jackyblackson.modfabric.utils.ColorUtils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;

import java.util.List;

import static com.jackyblackson.modfabric.utils.ColorUtils.rgbaToInt;

public class ChatMagicStyles {
    public static final Style ITEM_ID_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(147, 146, 147));

    public static final Style INPUT_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(97, 182, 209))
            .withItalic(true);

    public static final Style BLOCKSTATE_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(251, 96, 131))
            .withBold(true)
            .withUnderline(true);

    public static final Style BLOCKSTATE_UNKNOWN_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(180, 65, 100));

    public static final Style SELECTOR_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(228, 151, 102))
            .withBold(true)
            // .withClickEvent(ClickEvent)
            .withUnderline(true);

    public static final Style SELECTOR_DOUBLE_PARAM_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(97, 182, 209))
            .withBold(true)
            .withUnderline(true);


    public static final Style SELECTOR_MATERIAL_PARAM_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(168, 219, 111))
            .withBold(true)
            .withUnderline(true);

    public static final Style SELECTOR_UNKNOWN_PARAM_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(147, 146, 147))
            .withBold(true)
            .withUnderline(true);

    public static final Style UNKNOWN_ITEM_ID_STYLE = Style.EMPTY
            .withColor(ColorUtils.rgbToInt(97, 182, 209))
            .withBold(true)
            .withItalic(true);
}
