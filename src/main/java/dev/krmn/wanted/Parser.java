package dev.krmn.wanted;

import dev.krmn.wanted.ast.InfixExpression;
import dev.krmn.wanted.ast.Literal;
import dev.krmn.wanted.ast.Node;
import dev.krmn.wanted.ast.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Parser {
    private static final Pattern tokenPattern = Pattern.compile("level|random|[0-9]+(\\.[0-9]*[1-9])?|\\(|\\)|\\+|-|\\*|/|\\^");
    private static final Map<String, Integer> precedenceMap = new HashMap<String, Integer>() {
        {
            put("+", 0);
            put("-", 0);
            put("*", 1);
            put("/", 1);
            put("^", 2);
            put(")", 3);
        }
    };

    private final String exp;
    private String peekToken;
    private String curToken;
    private int pos = 0;

    public Parser(String exp) {
        this.exp = exp;
    }

    public Node parse() {
        pos = 0;
        nextToken();
        nextToken();
        if (curToken == null) {
            throw new RuntimeException("Failed to parse.");
        }

        return parseNode(-1);
    }

    private Node parseNode(int precedence) {
        Node left;
        char c = curToken.charAt(0);
        if (c >= '0' && c <= '9') {
            left = parseLiteral();
        } else if (c == '(') {
            nextToken();
            left = parseNode(-1);
        } else {
            left = parseVariable();
        }

        while (peekToken != null && precedence < precedenceMap.get(peekToken)) {
            nextToken();
            if (curToken.equals(")")) {
                break;
            }

            left = parseInfixExpression(left);
        }

        return left;
    }

    private Literal parseLiteral() {
        return new Literal(Double.parseDouble(curToken));
    }

    private Variable parseVariable() {
        return new Variable(curToken);
    }

    private InfixExpression parseInfixExpression(Node left) {
        String operator = curToken;
        nextToken();

        return new InfixExpression(operator, left, parseNode(precedenceMap.get(operator)));
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = readToken();
    }

    private String readToken() {
        if (pos >= exp.length()) {
            return null;
        }

        Matcher matcher = tokenPattern.matcher(exp.substring(pos));
        if (matcher.lookingAt()) {
            pos += matcher.end();
            return matcher.group();
        }

        pos++;
        return readToken();
    }
}
