package com.jackyblackson.modfabric.wematerial;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldEditMaterialNode {

    @NotNull
    protected Double percentage;

    @NotNull
    protected String selector;

    protected WorldEditMaterial parentMaterial;

    @NotNull
    protected List<Object> paramsList;

    @NotNull
    protected final String expression;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(percentage + "% " + selector + "\n");
        for (var param : paramsList) {
            result.append("[").append(param.toString()).append("]").append("\n");
        }
        return result.toString();
    }

    protected WorldEditMaterialNode(String expression) {
        this.expression = expression;
        this.percentage = -1D;
        this.selector = "";
        this.paramsList = new ArrayList<>(3);
    }

    public void setParentMaterial(WorldEditMaterial material) {
        this.parentMaterial = material;
    }





}
