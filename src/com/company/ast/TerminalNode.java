package com.company.ast;

public class TerminalNode extends ExprNode {

    public ExprNode next;
    public ExprNode current;

    public TerminalNode(ExprNode next,ExprNode current) {
        this.current=current;
        this.next = next;
    }
}
