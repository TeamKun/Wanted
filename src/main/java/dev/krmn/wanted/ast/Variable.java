package dev.krmn.wanted.ast;

import java.util.Map;

public class Variable implements Node {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public double eval(Map<String, Double> variables) {
        return variables.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
