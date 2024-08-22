package com.jackyblackson.modfabric.dto;

public record MaterialDisplayInfo(int startX, int startY, int endX, int endY, int bottomEndX) {
    public int getTotalWidth() {
        return Math.abs(startX - endX);
    }

    public int getBottomWidth() {
        return Math.abs(startX - bottomEndX);
    }

    public int getHeight() {
        return Math.abs(startY - endY);
    }
}
