package com.company;

import java.util.regex.Pattern;

public enum TokenType {
    INCR("\\+\\+"),
    DECR("--"),
    NUMBER("[0-9][0-9a-fA-F]*"),
    PRINT("print"),
    DO("do"),
    //CONDITIONAL_STATEMENT("[a-zA-Z_0-9]+[<>=][a-zA-Z_0-9]+"),
    WHILE("while"),
    MORE(">"),
    LESS("<"),
    EQUAL("="),
    ID("[a-zA-Z_][a-zA-Z_0-9]*"),
   // ADD("\\+"),
   // SUB("-"),
    MUL("\\*"),
   // DIV("/"),
    LPAR("\\("),
    RPAR("\\)"),
    Terminal(";"),
    SPACE("[ \t\r\n]+");

    final Pattern pattern;

    TokenType(String regexp) {
        pattern = Pattern.compile(regexp);
    }
}
