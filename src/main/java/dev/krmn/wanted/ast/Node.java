package dev.krmn.wanted.ast;


import java.util.Map;

public interface Node {
    double eval(Map<String, Double> variables);
}
