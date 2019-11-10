package com.company.ast;


import com.company.Token;

public class PostfixExpressionNode extends ExprNode {

    public final Token op;
    public final ExprNode last;
    public final String id;


    public PostfixExpressionNode(Token op, ExprNode last,String id) {
        this.op = op;
        this.last = last;
        this.id=id;
    }
}
