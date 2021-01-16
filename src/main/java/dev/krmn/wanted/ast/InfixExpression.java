package dev.krmn.wanted.ast;

import java.util.Map;

public class InfixExpression implements Node {
    private String operator;
    private Node left;
    private Node right;

    public InfixExpression(String operator, Node left, Node right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public double eval(Map<String, Double> variables) {
        double l = left.eval(variables);
        double r = right.eval(variables);

        switch (operator) {
            case "+":
                return l + r;
            case "-":
                return l - r;
            case "*":
                return l * r;
            case "/":
                return l / r;
            case "^":
                return Math.pow(l, r);
        }

        return 0;
    }
}
