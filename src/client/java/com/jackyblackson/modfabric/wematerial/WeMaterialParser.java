package com.jackyblackson.modfabric.wematerial;

import java.util.ArrayList;
import java.util.List;

public class WeMaterialParser {
    protected int currentIndex = -1;

    protected String expression;

    public WeMaterialParser (String expression, int currentIndex) {
        this.expression = expression;
        this.currentIndex = currentIndex;
    }

    protected static WorldEditMaterial parse(String expression) {
        return new WeMaterialParser(expression, -1).parse();
    }

    private WorldEditMaterial parse() {
        WorldEditMaterial result = new WorldEditMaterial(new ArrayList<>(3));
        var nodeStrList = splitItemInSameLevel(readInThisLevel());
        System.out.println(nodeStrList);
        for(var nodeStr : nodeStrList) {
            try {
                var parseResult = WeMaterialNodeParser.parseNode(nodeStr);
                result.addNode(parseResult);
            } catch (Exception ignored) {

            }
        }

        // Post process the implicit equal possibilities
        Double knownPossibilitiesSum = 0D;
        int itemCount = 0;
        for (var node : result.getMaterialNodes()){
            if(!node.selector.equals("$none") && node.percentage >= 0D) {
                knownPossibilitiesSum += node.percentage;
                itemCount++;
            }
        }
        if(itemCount < result.getMaterialNodes().size()){
            double implicitPossibility = (100D - knownPossibilitiesSum) / (result.getMaterialNodes().size() - itemCount);
//            System.out.println("implicit count: " + itemCount);
//            System.out.println("possibilities: " + (result.getMaterialNodes().size() - itemCount));
            for (var node : result.getMaterialNodes()) {
                if (node.percentage < 0D) {
                    node.percentage = implicitPossibility;
                }
            }
        }
        return result;
    }

    protected List<String> splitItemInSameLevel(String exp) {
        int pre = 0;

        int layer = 0;
        int i = 0;
        List<Integer> commaPoints = new ArrayList<>();
        for (i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '[') {
                layer++;
            } else if (exp.charAt(i) == ']') {
                layer--;
                if(layer < 0) {
                    break;
                }
            }
            if(layer == 0 && exp.charAt(i) == ',') {
                commaPoints.add(i);
            }
        }

        List<String> result = new ArrayList<>();
        for(Integer point : commaPoints) {
            result.add(exp.substring(pre, point));
            pre = point+1;
        }
        result.add(exp.substring(pre));

        return result;
    }

    protected String readInThisLevel() {
        int layer = 0;
        int i = 0;
        String seq = this.expression.substring(currentIndex+1);
        for (i = 0; i < seq.length(); i++) {
            if (seq.charAt(i) == '[') {
                layer++;
            } else if (seq.charAt(i) == ']') {
                layer--;
                if(layer < 0) {
                    break;
                }
            }
        }
        currentIndex = currentIndex+1 + i-1;
        return seq.substring(0, i);
    }

    public static void main(String[] args) {
        var parser = new WeMaterialParser("12345[face=north,top=true,con=west]", 5);
        var list = parser.splitItemInSameLevel(parser.readInThisLevel());
        for(var item : list) {
            System.out.println(item);
        }
        System.out.println("-----");
        System.out.println("Current index is " + parser.currentIndex + ", current chat is " + parser.currentChar());
    }

    protected char nextChar() {
        return this.expression.charAt(currentIndex+1);
    }

    protected char currentChar() {
        return this.expression.charAt(currentIndex);
    }

    public String readUntil(char ender) {
        String seq = this.expression.substring(currentIndex+1);
        int i = 0;
        for(i = 0; i < seq.length() && seq.charAt(i) != ender; i++);
        currentIndex = currentIndex+1 + i-1;
        return seq.substring(0, i);
    }

    public Double readDouble() {

        String seq = this.expression.substring(currentIndex+1);
        int i = 0;
        boolean encounteredDot = false;

        for (i = 0; i < seq.length(); i++) {
            char c = seq.charAt(i);
            if (c == '.') {
                if (!encounteredDot){
                    encounteredDot = true;
                } else {
                    return null;
                }
            }
            if(!Character.isDigit(c) & c != '.') {
                break;
            }
        }

        // If it finds no double, then return nothing
        if(i == 0) {
            return null;
        }

        currentIndex = currentIndex+1 + i-1;
        return Double.parseDouble(seq.substring(0, i));
    }
}
