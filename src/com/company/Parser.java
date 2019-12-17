package com.company;


import Semantic.Evaler;
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

    public static void semanticError(String message) {
        throw new RuntimeException("Семантическая ошибка " + message);
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

    private ExprNode parseCicle() {
        Token op;
        ExprNode e1 = null;
        while ((op = match(TokenType.WHILE)) == null)
            e1 = parseExpression();
        ConditionalExprNode e2 = null;
        try {
            e2 = (ConditionalExprNode) (parseConditionalExpresstion());

            if (e2.leftArg == null)
                error("Can not match left arg in cicle expression ");
            if (e2.op == null)
                error("Can not match op in cicle expression ");
            if (e2.rightArg == null)
                error("Can not match right arg in cicle expression ");

        } catch (ClassCastException | NullPointerException e) {
            error("Excepted conditional");
        }
        e1 = new CicleNode(e2, e1);
        return e1;
    }

    private ExprNode parseConditionalExpresstion() {
        Token op;
        ExprNode e1 = parseMnozh();
        ExprNode e2;
        while ((op = match(TokenType.LESS, TokenType.MORE, TokenType.EQUAL)) != null) {
            e2 = parseElem();
            e1 = new ConditionalExprNode(op, e1, e2);
        }

        return e1;
    }

    public ExprNode parseDecrExpression(ExprNode ex){
        if(tokens.get(pos-2).type==TokenType.ID){
            return new PostfixExpressionNode(tokens.get(pos-1),ex,tokens.get(pos-2).text);
        }
        error("Excepted id on decr op");
        return null;
    }

    public ExprNode parsePrintExpression(){
        Token op;
        if((op=match(TokenType.ID))!=null){
            return new PrintExpressionNode(op.text);
        }
        error("Excepted ID");
        return null;
    }

    public ExprNode parseIncrExpression(ExprNode ex){
        if(tokens.get(pos-2).type==TokenType.ID){
            return new PostfixExpressionNode(tokens.get(pos-1),ex,tokens.get(pos-2).text);
        }
        error("Excepted id on incr op");
        return null;
    }

    public ExprNode parseMultiplyExpression(ExprNode e1){
        if (this.tokens.get(pos-2).type==TokenType.ID)
            return new BinOpNode(tokens.get(pos),e1,parseElem());
        error("Left arg of Multiply must be a varibale");
        return null;
    }

    public ExprNode parseExpression() {

        //ExprNode e1 = parseMnozh();
        ExprNode e1=null;
        ExprNode e2;
        String id = "";
        Token op;
        Token t;
        while (((op = match(TokenType.INCR, TokenType.DECR, TokenType.Terminal, TokenType.PRINT, TokenType.DO, TokenType.MUL,TokenType.ID,TokenType.NUMBER)) != null)) {
            switch (op.type) {
                case Terminal:
                    e1 = new TerminalNode(parseExpression(), e1);
                    break;
                case MUL:
                    e1 = parseMultiplyExpression(e1);
                    break;
                case INCR:
                    //id = op.text.substring(0, op.text.length() - 2);
                    e1 = parseIncrExpression(e1);
                    break;

                case DECR:
                    e1 = parseDecrExpression(e1);
                    break;

                case PRINT:
                    //id = op.text.substring(6, op.text.length());
                    e1 = parsePrintExpression();
                    break;

                case DO:
                    e1 = parseCicle();
                    break;

                case ID:
                    e1=new VarNode(op);
                    break;
                case NUMBER:
                    e1=new NumberNode(op);
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
        } else if (node instanceof TerminalNode) {
            TerminalNode terminal = (TerminalNode) node;
            Evaler.EvalTerminalNode(terminal);
            return;

        } else if (node instanceof PostfixExpressionNode) {
            PostfixExpressionNode pf = (PostfixExpressionNode) node;
            Evaler.EvalPostfixNode(pf);
            //eval(pf.last);
            return;
        } else if (node instanceof PrintExpressionNode) {
            PrintExpressionNode pr = (PrintExpressionNode) node;
            Evaler.EvalPrintExpression(pr);
            //eval(pr.prev);
            return;
        } else if (node instanceof CicleNode) {
            CicleNode cicleNode = (CicleNode) node;
            Evaler.EvalCicleExpression(cicleNode);
            return;
        } else if (node instanceof VarNode) {
            Evaler.evalVarNode((VarNode) node);
            return;
        } else if (node instanceof BinOpNode) {
            BinOpNode binNode = (BinOpNode) node;
            Evaler.EvalBinOpNode(binNode);
            return;
        }

        semanticError("Неизвестный узел " + node.getClass());
    }

    /*
    метод для ввода перееменных по имени перед выполнением программы.(старый вариант)
     */
    @Deprecated
    private static void initVar() {
        String key = "";
        String value = "";
        String nextc = "";
        do {
            System.out.println("Введите next или end");
            nextc = new Scanner(System.in).nextLine();
            if (nextc.equals("end"))
                break;
            System.out.println("Введите имя переменной");
            key = new Scanner(System.in).nextLine();
            System.out.println("Введите значение переменной");
            value = new Scanner(System.in).nextLine();

            Varibales.putVar(key, HexNumber.toDecimal(value));
        } while (true);

    }

    public static void main(String[] args) {
        //Common programm
        String text =
                "do;" +
                        "x;y;"+
                "print x;" +
                "x++;" +
                "do;" +
                "print y;" +
                "y--;" +
                "while(y>0);" +
                "while(x<4);";
        //Factorial
        String text1 = "res;i;n;0;" +
                "do;" +
                "res*i;" +
                "i++;" +
                "while(i<n);" +
                "print res;";

        Lexer l = new Lexer(text1);
        List<Token> tokens = l.lex();
        tokens.removeIf(t -> t.type == TokenType.SPACE);

        Parser p = new Parser(tokens);
        ExprNode node = p.parseExpression();
        //initVar();
        eval(node);

    }
}
