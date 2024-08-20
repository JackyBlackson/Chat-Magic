package com.jackyblackson.modfabric.styles;

import com.jackyblackson.modfabric.utils.ColorUtils;
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

    public static List<Integer> colors = List.of(
            rgbaToInt(100, 100, 255, 255),
            rgbaToInt(137, 255, 241, 255),
            rgbaToInt(255, 136, 196, 255),
            rgbaToInt(213, 255, 136, 255),
            rgbaToInt(136, 255, 153, 255),
            rgbaToInt(255, 182, 136, 255)
    );
}
