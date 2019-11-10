package com.company;


import Semantic.HexNumber;
import Semantic.Varibales;
import com.company.ast.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private void error(String message) {
        if (pos < tokens.size()) {
            Token t = tokens.get(pos);
            throw new RuntimeException(message + " в позиции " + t.pos);
        } else {
            throw new RuntimeException(message + " в конце файла");
        }
    }
    private static void semanticError(String message){
        throw new RuntimeException("Семантическая ошибка "+message);
    }

    private Token match(TokenType... expected) {
        if (pos < tokens.size()) {
            Token curr = tokens.get(pos);
            if (Arrays.asList(expected).contains(curr.type)) {
                pos++;
                return curr;
            }
        }
        return null;
    }

    private Token require(TokenType... expected) {
        Token t = match(expected);
        if (t == null)
            error("Ожидается " + Arrays.toString(expected));
        return t;
    }

    private ExprNode parseElem() {
        /*
        Token enp=match(TokenType.ENP);
        if(enp!=null)
            return new ENPNode();

        Token terminal=match(TokenType.Terminal);
        if(terminal!=null) {
            ExprNode e=parseExpression();
            return new TerminalNode(e);
        }
        */
        Token num = match(TokenType.NUMBER);
        if (num != null)
            return new NumberNode(num);
        Token id = match(TokenType.ID);
        if (id != null)
            return new VarNode(id);
        //error("Ожидается число или переменная");
        return null;
    }

    private ExprNode parseMnozh() {
        if (match(TokenType.LPAR) != null) {
            ExprNode e = parseConditionalExpresstion();
            require(TokenType.RPAR);
            return e;
        } else {
            return parseElem();
        }
    }

    private ExprNode parseCicle(){
        Token op;
        ExprNode e1=null;
        while ((op=match(TokenType.WHILE))==null)
            e1=parseExpression();
        ConditionalExprNode e2=(ConditionalExprNode) (parseConditionalExpresstion());
        e1=new CicleNode(e2,e1);
        return e1;
    }

    private ExprNode parseConditionalExpresstion(){
        Token op;
        ExprNode e1=parseMnozh();
        ExprNode e2;
        while ((op=match(TokenType.LESS,TokenType.MORE,TokenType.EQUAL))!=null){
                    e2=parseElem();
                    e1=new ConditionalExprNode(op,e1,e2);
        }

        return e1;
    }


    public ExprNode parseExpression() {

        ExprNode e1 = parseMnozh();
        ExprNode e2;
        String id="";
        Token op;
        Token t;
        while (((op = match(TokenType.INCR, TokenType.DECR,TokenType.Terminal,TokenType.PRINT,TokenType.DO,TokenType.MUL)) != null)) {
            switch (op.type) {
                case Terminal:
                    e1 = new TerminalNode(parseExpression(), e1);
                break;
                case MUL:
                        e1 = new BinOpNode(op, e1, parseElem());
                    break;
                case INCR:
                    id=op.text.substring(0,op.text.length()-2);
                    e1=new PostfixExpressionNode(op,e1,id);
                break;

                case DECR:
                    id=op.text.substring(0,op.text.length()-2);
                    e1=new PostfixExpressionNode(op,e1,id);
                    break;

                case PRINT:
                    id=op.text.substring(6,op.text.length());
                    e1=new PrintExpressionNode(id);
                    break;

                case DO:
                    e1=parseCicle();
                    break;
            }
        }
        return e1;
    }

    /*
    public ExprNode parseStatement(){
        ExprNode e1=parseExpression();
        Token op;
    }
    */

    public static void eval(ExprNode node) {
        if (node instanceof NumberNode) {
            NumberNode num = (NumberNode) node;
            return;
        }
         else if (node instanceof TerminalNode){
            TerminalNode terminal=(TerminalNode)node;
            if(terminal.next==null) {
                if(terminal.current!=null)
                    eval(terminal.current);
                return;
            }
            else{
                if(terminal.current!=null)
                    eval(terminal.current);
                 eval(terminal.next);
                 return;
            }

             // Das ist костыль при допиливании убрать
        }
        else if (node instanceof PostfixExpressionNode){
            PostfixExpressionNode pf=(PostfixExpressionNode)node;
            if(!Varibales.hasVar(pf.id))
                semanticError("Varibale "+pf.id+" must be init");
            Integer i=Varibales.getValue(pf.id);
            switch (pf.op.type) {
                case INCR:
                    Varibales.putVar(pf.id, ++i);
                    break;
                case DECR:
                    Varibales.putVar(pf.id, --i);
                    break;
                default:
                    semanticError("Unknown postfix operation");
                    break;

            }
            //eval(pf.last);
            return;
        }
        else if (node instanceof PrintExpressionNode){
            PrintExpressionNode pr=(PrintExpressionNode)node;
            if(!Varibales.hasVar(pr.id))
                semanticError("Varibale "+pr.id+" must be init");
            Integer i=Varibales.getValue(pr.id);
            System.out.println(HexNumber.toHexString(i));
            //eval(pr.prev);
            return;
        }
        else if (node instanceof CicleNode){
            CicleNode cicleNode=(CicleNode)node;
            Integer rightArg=null;
            Integer leftArg=null;

            switch (cicleNode.conditional.op.type){
                case MORE:
                    do {
                        if(((CicleNode) node).conditional.rightArg instanceof VarNode) {
                            if(!Varibales.hasVar((( VarNode)(((CicleNode) node).conditional.rightArg)).id.text))
                                semanticError("Varibale "+((( VarNode)(((CicleNode) node).conditional.rightArg)).id.text)+" must be init");
                            rightArg = Varibales.getValue(((VarNode) (((CicleNode) node).conditional.rightArg)).id.text);
                        }
                        else if(((CicleNode) node).conditional.rightArg instanceof NumberNode){
                            rightArg=HexNumber.toDecimal(((NumberNode) ((CicleNode) node).conditional.rightArg).number.text);

                        }

                        if(((CicleNode) node).conditional.leftArg instanceof VarNode) {
                            if(!Varibales.hasVar((( VarNode)(((CicleNode) node).conditional.leftArg)).id.text))
                                semanticError("Varibale "+((( VarNode)(((CicleNode) node).conditional.leftArg)).id.text)+" must be init");
                            leftArg = Varibales.getValue(((VarNode) (((CicleNode) node).conditional.leftArg)).id.text);
                        }
                        else if(((CicleNode) node).conditional.leftArg instanceof NumberNode){
                            leftArg=HexNumber.toDecimal(((NumberNode) ((CicleNode) node).conditional.leftArg).number.text);

                        }
                        eval(((CicleNode) node).body);
                    }while (leftArg>rightArg);
                    break;
                case LESS:
                    do {
                        if(((CicleNode) node).conditional.rightArg instanceof VarNode) {
                            if(!Varibales.hasVar((( VarNode)(((CicleNode) node).conditional.rightArg)).id.text))
                                semanticError("Varibale "+((( VarNode)(((CicleNode) node).conditional.rightArg)).id.text)+" must be init");
                            rightArg = Varibales.getValue(((VarNode) (((CicleNode) node).conditional.rightArg)).id.text);
                        }
                        else if(((CicleNode) node).conditional.rightArg instanceof NumberNode){
                            rightArg=HexNumber.toDecimal(((NumberNode) ((CicleNode) node).conditional.rightArg).number.text);

                        }

                        if(((CicleNode) node).conditional.leftArg instanceof VarNode) {
                            if(!Varibales.hasVar((( VarNode)(((CicleNode) node).conditional.leftArg)).id.text))
                                semanticError("Varibale "+((( VarNode)(((CicleNode) node).conditional.leftArg)).id.text)+" must be init");
                            leftArg = Varibales.getValue(((VarNode) (((CicleNode) node).conditional.leftArg)).id.text);
                        }
                        else if(((CicleNode) node).conditional.leftArg instanceof NumberNode){
                            leftArg=HexNumber.toDecimal(((NumberNode) ((CicleNode) node).conditional.leftArg).number.text);

                        }
                        eval(((CicleNode) node).body);
                    }while (leftArg<rightArg);
                    break;
                case EQUAL:
                    do {
                        if(((CicleNode) node).conditional.rightArg instanceof VarNode) {
                            if(!Varibales.hasVar((( VarNode)(((CicleNode) node).conditional.rightArg)).id.text))
                                semanticError("Varibale "+((( VarNode)(((CicleNode) node).conditional.rightArg)).id.text)+" must be init");
                            rightArg = Varibales.getValue(((VarNode) (((CicleNode) node).conditional.rightArg)).id.text);
                        }
                        else if(((CicleNode) node).conditional.rightArg instanceof NumberNode){
                            rightArg=HexNumber.toDecimal(((NumberNode) ((CicleNode) node).conditional.rightArg).number.text);

                        }

                        if(((CicleNode) node).conditional.leftArg instanceof VarNode) {
                            if(!Varibales.hasVar((( VarNode)(((CicleNode) node).conditional.leftArg)).id.text))
                                semanticError("Varibale "+((( VarNode)(((CicleNode) node).conditional.leftArg)).id.text)+" must be init");
                            leftArg = Varibales.getValue(((VarNode) (((CicleNode) node).conditional.leftArg)).id.text);
                        }
                        else if(((CicleNode) node).conditional.leftArg instanceof NumberNode){
                            leftArg=HexNumber.toDecimal(((NumberNode) ((CicleNode) node).conditional.leftArg).number.text);

                        }
                        eval(((CicleNode) node).body);
                    }while (leftArg==rightArg);
                    break;

                default:
                    semanticError("Unknown condition operation");
                    break;



            }

            return;
        }
        else if(node instanceof VarNode)
            return;
        else if (node instanceof BinOpNode){
            BinOpNode binNode=(BinOpNode)node;
            Integer right=null;
            Integer left=null;
            if(binNode.left instanceof VarNode) {
                if(!Varibales.hasVar(((VarNode) binNode.left).id.text))
                    semanticError("Varibales must init");
                left = Varibales.getValue(((VarNode) binNode.left).id.text);
            }
            else if(binNode.left instanceof NumberNode)
                left= HexNumber.toDecimal(((NumberNode) binNode.left).number.text);
            else
                semanticError("Unknown argument of multiplicate ");

            if(binNode.right instanceof VarNode) {
                if(!Varibales.hasVar(((VarNode) binNode.right).id.text))
                    semanticError("Varibales must init");
                right = Varibales.getValue(((VarNode) binNode.right).id.text);
            }
            else if(binNode.left instanceof NumberNode)
                right=HexNumber.toDecimal(((NumberNode) binNode.right).number.text);
            else
                semanticError("Unknown argument of multiplicate ");
            Varibales.putVar(((VarNode)binNode.left).id.text,left*right);
            return;
        }

        throw new IllegalStateException();
    }

    private static void initVar(){
        String key="";
        String value="";
        String nextc="";
        do {
            System.out.println("Введите next или end");
            nextc= new Scanner(System.in).nextLine();
            if(nextc.equals("end"))
                break;
            System.out.println("Введите имя переменной");
            key = new Scanner(System.in).nextLine();
            System.out.println("Введите значение переменной");
            value = new Scanner(System.in).nextLine();

            Varibales.putVar(key,HexNumber.toDecimal(value));
        }while (true);

    }

    public static void main(String[] args) {
        String text = "x;y;" +
                "do;" +
                "print x;"+
                    "x++;"+
                "do;"+
                    "print y;"+
                    "y--;"+
                    "while(y>0);"+
                "while(x<4);";
        String text1= "res;i;n;" +
                "do;" +
                "res*i;"+
                "i++;"+
                "while(i<n);" +
                "print res;";

        Lexer l = new Lexer(text1);
        List<Token> tokens = l.lex();
        tokens.removeIf(t -> t.type == TokenType.SPACE);

        Parser p = new Parser(tokens);
        ExprNode node = p.parseExpression();
        initVar();
        eval(node);

    }
}
