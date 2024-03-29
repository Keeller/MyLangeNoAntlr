package com.company.ast;


import com.company.Token;

public class NumberNode extends ExprNode {

    public final Token number;

    public NumberNode(Token number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return number.text;
    }
}
