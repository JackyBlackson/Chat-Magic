package com.jackyblackson.modfabric.wematerial;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class WeMaterialNodeParser extends WeMaterialParser{
    public WeMaterialNodeParser(String expression, int currentIndex) {
        super(expression, currentIndex);
    }

    protected static WorldEditMaterialNode parseNode(String exp) {
        return new WeMaterialNodeParser(exp, -1).parseNode();
    }

    public static void main(String[] args) {
//        var parser = new WeMaterialNodeParser("#stone[789.114][1234,1245,1256]", -1);
//        System.out.println(parser.expression);
//        System.out.println(parser.parseNode());

        System.out.println(WorldEditMaterial.of("10%#perlin[1][5][stone,air,water,grass,cloud],5%glass,andesite[foo=1,bar=2,114=514,1919,810]"));
    }

    private WorldEditMaterialNode parseNode() {
        if(expression.isBlank()) {
            return null;
        }

        WorldEditMaterialNode node = new WorldEditMaterialNode(expression);
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
            while(currentIndex+1 < expression.length()) {
                currentIndex++;
                String param = readInThisLevel();
                try{
                    var doubleParam = Double.parseDouble(param);
                    node.paramsList.add(doubleParam);
                } catch (Exception e) {
                    node.paramsList.add(WeMaterialParser.parse(param));
                }
                currentIndex++;
            }
        }
        return node;
    }
}
