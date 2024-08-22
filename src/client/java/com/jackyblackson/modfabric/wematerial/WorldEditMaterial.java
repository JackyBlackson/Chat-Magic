package com.jackyblackson.modfabric.wematerial;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorldEditMaterial {
    private List<WorldEditMaterialNode> materialNodes;

    private final String id;

    public WorldEditMaterial(List<WorldEditMaterialNode> materialNodes, String materialId) {
        this.materialNodes = materialNodes;
        this.id = materialId;
    }

    public String getId() {
        return id;
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
    of(@NotNull String expression, String idPrefix) {
        return WeMaterialParser.parse(expression, idPrefix+"#");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\"" + this.id + "\": {\n");
        for (var node : materialNodes) {
            sb.append(node.toString());
        }
        sb.append("}\n");
        return sb.toString();
    }
}
