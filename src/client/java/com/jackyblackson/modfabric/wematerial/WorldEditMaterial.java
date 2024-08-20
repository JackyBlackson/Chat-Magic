package com.jackyblackson.modfabric.wematerial;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorldEditMaterial {
    private List<WorldEditMaterialNode> materialNodes;

    public WorldEditMaterial(List<WorldEditMaterialNode> materialNodes) {
        this.materialNodes = materialNodes;
    }

    public List<WorldEditMaterialNode> getMaterialNodes() {
        return materialNodes;
    }

    public void addNode(WorldEditMaterialNode node) {
        node.setParentMaterial(this);
        this.materialNodes.add(node);
    }

    public static void main(String[] args) {

    }

    public static
    WorldEditMaterial
    of(@NotNull String expression) {
        return WeMaterialParser.parse(expression);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n{\n");
        for (var node : materialNodes) {
            sb.append(node.toString());
        }
        sb.append("}\n");
        return sb.toString();
    }
}
