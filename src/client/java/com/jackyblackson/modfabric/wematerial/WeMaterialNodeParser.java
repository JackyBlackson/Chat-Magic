package com.jackyblackson.modfabric.wematerial;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class WeMaterialNodeParser extends WeMaterialParser{
    public WeMaterialNodeParser(String expression, int currentIndex) {
        super(expression, currentIndex);
    }

    protected static WorldEditMaterialNode parseNode(String exp, String parentMaterialId) {
        return new WeMaterialNodeParser(exp, -1).parseNode(parentMaterialId);
    }

    private WorldEditMaterialNode parseNode(String nodeId) {
        if(expression.isBlank()) {
            return null;
        }

        WorldEditMaterialNode node = new WorldEditMaterialNode(expression, nodeId);
        //while(this.currentIndex < this.expression.length()) {
        if(expression.contains("%")){
            Double percentage = readDouble();
            if (percentage == null) {
                node.percentage = -1D;
            } else if (nextChar() != '%') {
                return null;  //TODO: Throw exceptions to indicate what it breaks here
            } else {
                node.percentage = percentage;
            }
            if (node.percentage >= 0D) {
                currentIndex++;     // Jump the % here
            }
        }

        if(nextChar() != '#') {                                 // Plain mc identifier
            String itemIdStr = readUntil('[');
            if(itemIdStr == null || itemIdStr.isBlank()) {
                node.selector = "$none";
            }
            else {
                node.selector = "$item";
                node.paramsList.add(itemIdStr);
            }

            // Read block states
            if(currentIndex < expression.length()-1) {
                currentIndex++;
                node.paramsList.add(splitItemInSameLevel(readInThisLevel()));
            }
        } else {                    // WorldEdit selector exp
            currentIndex++;     // Skip that '#'
            String selector = readUntil('[');
            // Current char is last chat in the selector
            if(selector == null || selector.isBlank()) {
                node.selector = "$none";
            } else {
                node.selector = "#" + selector;
            }
            // Read param lists
            int paramIndex = 0;
            while(currentIndex+1 < expression.length()) {
                currentIndex++;
                String param = readInThisLevel();
                try{
                    var doubleParam = Double.parseDouble(param);
                    node.paramsList.add(doubleParam);
                } catch (Exception e) {
                    node.paramsList.add(WeMaterialParser.parse(param, nodeId + "-" + paramIndex));
                }
                currentIndex++;
                paramIndex++;
            }
        }
        return node;
    }
}
