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

    @NotNull
    protected  final String id;


    public Double getPercentage() {
        return percentage;
    }

    public String getSelector() {
        return selector;
    }

    public WorldEditMaterial getParentMaterial() {
        return parentMaterial;
    }

    public List<Object> getParamsList() {
        return paramsList;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("\""+this.id+"\": " + percentage + "% " + selector + "\n");
        for (var param : paramsList) {
            result.append("[").append(param.toString()).append("]").append("\n");
        }
        return result.toString();
    }

    public String getId() {
        return id;
    }

    protected WorldEditMaterialNode(String expression, String id) {
        this.expression = expression;
        this.percentage = -1D;
        this.selector = "";
        this.paramsList = new ArrayList<>(3);
        this.id = id;
    }

    public void setParentMaterial(WorldEditMaterial material) {
        this.parentMaterial = material;
    }





}
