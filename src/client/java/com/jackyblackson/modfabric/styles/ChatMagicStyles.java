package com.jackyblackson.modfabric.styles;

import com.jackyblackson.modfabric.utils.ColorUtils;
import net.minecraft.text.Style;

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
}
