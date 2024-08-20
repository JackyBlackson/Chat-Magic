package com.jackyblackson.modfabric.dto;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class ItemTooptipInfo {
    public Map<String, String> fieldsMap;

    public Map<String, Style> styleMap;

    public int startX;

    public int startY;

    private ItemTooptipInfo() {
        this.fieldsMap = new HashMap<>();
        this.styleMap = new HashMap<>();
    }

    public static ItemTooptipInfo create() {
        return new ItemTooptipInfo();
    }

    public ItemTooptipInfo addField(String key, String value, Style style) {
        this.fieldsMap.put(key, value);
        this.styleMap.put(key, style);
        return this;
    }

    public Style getStyle(String key) {
        return this.styleMap.get(key);
    }

    public String getValue(String key) {
        return this.fieldsMap.get(key);
    }

    public Text getStyledText(String key) {
        Style style = this.getStyle(key);
        String value = this.getValue(key);

        if (value == null) {
            value = "undefined";
        }

        String formatString = key + ": " + value;

        if (style == null) {
            return Text.literal(formatString);
        } else {
            return Text.literal(formatString).fillStyle(style);
        }
    }

    public ItemTooptipInfo setCoord(int x, int y) {
        this.startX = x;
        this.startY = y;
        return this;
    }
}
