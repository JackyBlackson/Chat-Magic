package com.jackyblackson.modfabric.utils;

public class ColorUtils {

    /**
     * 将十六进制颜色代码转换为 RGBA 颜色数组。
     * @param hex 颜色的十六进制字符串，例如 "#FF5733" 或 "FF5733"。
     * @return 包含四个元素的数组，分别表示红色、绿色、蓝色和 alpha 值。
     */
    public static int[] hexToRGBA(String hex) {
        // 去掉十六进制字符串的 # 符号
        hex = hex.replace("#", "");
        // 确保字符串长度为 6 或 8（包括 alpha 通道）
        if (hex.length() == 6) {
            hex = hex + "FF"; // 默认 alpha 为 255
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        int a = Integer.parseInt(hex.substring(6, 8), 16);
        return new int[]{r, g, b, a};
    }

    /**
     * 将 RGBA 颜色数组转换为十六进制颜色代码。
     * @param r 红色值（0-255）。
     * @param g 绿色值（0-255）。
     * @param b 蓝色值（0-255）。
     * @param a alpha 值（0-255）。
     * @return 十六进制颜色字符串，例如 "#FF5733FF"。
     */
    public static String rgbaToHex(int r, int g, int b, int a) {
        return String.format("#%02X%02X%02X%02X", r, g, b, a);
    }

    /**
     * 将 RGBA 颜色数组转换为整数编码。
     * @param r 红色值（0-255）。
     * @param g 绿色值（0-255）。
     * @param b 蓝色值（0-255）。
     * @param a alpha 值（0-255）。
     * @return 整数编码。
     */
    public static int rgbaToInt(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * 将 RGB 三个分量转换为 int 格式的 RGB 颜色值
     * @param red 红色分量 (0-255)
     * @param green 绿色分量 (0-255)
     * @param blue 蓝色分量 (0-255)
     * @return int 格式的 RGB 颜色值
     */
    public static int rgbToInt(int red, int green, int blue) {
        // 确保 RGB 值在 0-255 范围内
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        // 将 RGB 组合成一个 int
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * 将整数编码转换为 RGBA 颜色数组。
     * @param color 整数编码。
     * @return 包含四个元素的数组，分别表示红色、绿色、蓝色和 alpha 值。
     */
    public static int[] intToRGBA(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        return new int[]{r, g, b, a};
    }
}

