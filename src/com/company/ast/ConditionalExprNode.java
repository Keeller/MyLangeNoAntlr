package com.company.ast;


import com.company.Token;

public class ConditionalExprNode extends ExprNode {
    public final Token op;
    public final ExprNode leftArg;
    public final ExprNode rightArg;

    public ConditionalExprNode(Token op, ExprNode leftArg, ExprNode rightArg) {
        this.op = op;
        this.leftArg = leftArg;
        this.rightArg = rightArg;
    }
}
