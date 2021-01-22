package dev.krmn.wanted.ast;

import java.util.Map;

public class Literal implements Node {
    private double value;

    public Literal(double value) {
        this.value = value;
    }

    @Override
    public double eval(Map<String, Double> variables) {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
