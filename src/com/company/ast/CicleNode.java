package com.company.ast;

public class CicleNode extends ExprNode {
    public final ConditionalExprNode conditional;
    public final ExprNode body;

    public CicleNode(ConditionalExprNode conditional, ExprNode body) {
        this.conditional = conditional;
        this.body = body;
    }
}
