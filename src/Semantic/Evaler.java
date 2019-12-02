package Semantic;

import com.company.ast.*;

import java.util.Scanner;

import static com.company.Parser.eval;
import static com.company.Parser.semanticError;

public class Evaler {

    public static void EvalTerminalNode(TerminalNode terminal){
        if (terminal.next == null) {
            if (terminal.current != null)
                eval(terminal.current);
            return;
        } else {
            if (terminal.current != null)
                eval(terminal.current);
            eval(terminal.next);
            return;
        }
    }

    public static void EvalPostfixNode(PostfixExpressionNode pf){

        if (!Varibales.hasVar(pf.id))
            semanticError("Varibale " + pf.id + " must be init");
        Integer i = Varibales.getValue(pf.id);
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
    }

    public static void EvalPrintExpression(PrintExpressionNode pr){
        if (!Varibales.hasVar(pr.id))
            semanticError("Varibale " + pr.id + " must be init");
        Integer i = Varibales.getValue(pr.id);
        System.out.println(HexNumber.toHexString(i));
    }

    public static void EvalCicleExpression(CicleNode cicleNode){
        Integer rightArg = null;
        Integer leftArg = null;

        switch (cicleNode.conditional.op.type) {
            case MORE:
                do {
                    if ((cicleNode).conditional.rightArg instanceof VarNode) {
                        if (!Varibales.hasVar(((VarNode) ((cicleNode).conditional.rightArg)).id.text))
                            semanticError("Varibale " + (((VarNode) ((cicleNode).conditional.rightArg)).id.text) + " must be init");
                        rightArg = Varibales.getValue(((VarNode) ((cicleNode).conditional.rightArg)).id.text);
                    } else if ((cicleNode).conditional.rightArg instanceof NumberNode) {
                        rightArg = HexNumber.toDecimal(((NumberNode) (cicleNode).conditional.rightArg).number.text);

                    }

                    if ((cicleNode).conditional.leftArg instanceof VarNode) {
                        if (!Varibales.hasVar(((VarNode) ((cicleNode).conditional.leftArg)).id.text))
                            semanticError("Varibale " + (((VarNode) ((cicleNode).conditional.leftArg)).id.text) + " must be init");
                        leftArg = Varibales.getValue(((VarNode) ((cicleNode).conditional.leftArg)).id.text);
                    } else if ((cicleNode).conditional.leftArg instanceof NumberNode) {
                        leftArg = HexNumber.toDecimal(((NumberNode) (cicleNode).conditional.leftArg).number.text);

                    }
                    eval((cicleNode).body);
                } while (leftArg > rightArg);
                break;
            case LESS:
                do {
                    if ((cicleNode).conditional.rightArg instanceof VarNode) {
                        if (!Varibales.hasVar(((VarNode) ((cicleNode).conditional.rightArg)).id.text))
                            semanticError("Varibale " + (((VarNode) ((cicleNode).conditional.rightArg)).id.text) + " must be init");
                        rightArg = Varibales.getValue(((VarNode) ((cicleNode).conditional.rightArg)).id.text);
                    } else if ((cicleNode).conditional.rightArg instanceof NumberNode) {
                        rightArg = HexNumber.toDecimal(((NumberNode) (cicleNode).conditional.rightArg).number.text);

                    }

                    if ((cicleNode).conditional.leftArg instanceof VarNode) {
                        if (!Varibales.hasVar(((VarNode) ((cicleNode).conditional.leftArg)).id.text))
                            semanticError("Varibale " + (((VarNode) ((cicleNode).conditional.leftArg)).id.text) + " must be init");
                        leftArg = Varibales.getValue(((VarNode) ((cicleNode).conditional.leftArg)).id.text);
                    } else if ((cicleNode).conditional.leftArg instanceof NumberNode) {
                        leftArg = HexNumber.toDecimal(((NumberNode) (cicleNode).conditional.leftArg).number.text);

                    }
                    eval((cicleNode).body);
                } while (leftArg < rightArg);
                break;
            case EQUAL:
                do {
                    if ((cicleNode).conditional.rightArg instanceof VarNode) {
                        if (!Varibales.hasVar(((VarNode) ((cicleNode).conditional.rightArg)).id.text))
                            semanticError("Varibale " + (((VarNode) ((cicleNode).conditional.rightArg)).id.text) + " must be init");
                        rightArg = Varibales.getValue(((VarNode) ((cicleNode).conditional.rightArg)).id.text);
                    } else if ((cicleNode).conditional.rightArg instanceof NumberNode) {
                        rightArg = HexNumber.toDecimal(((NumberNode) (cicleNode).conditional.rightArg).number.text);

                    }

                    if ((cicleNode).conditional.leftArg instanceof VarNode) {
                        if (!Varibales.hasVar(((VarNode) ((cicleNode).conditional.leftArg)).id.text))
                            semanticError("Varibale " + (((VarNode) ((cicleNode).conditional.leftArg)).id.text) + " must be init");
                        leftArg = Varibales.getValue(((VarNode) ((cicleNode).conditional.leftArg)).id.text);
                    } else if ((cicleNode).conditional.leftArg instanceof NumberNode) {
                        leftArg = HexNumber.toDecimal(((NumberNode) (cicleNode).conditional.leftArg).number.text);

                    }
                    eval((cicleNode).body);
                } while (leftArg == rightArg);
                break;

            default:
                semanticError("Unknown condition operation");
                break;


        }

    }

    public static void EvalBinOpNode(BinOpNode binNode){
        Integer right = null;
        Integer left = null;
        if (binNode.left instanceof VarNode) {
            if (!Varibales.hasVar(((VarNode) binNode.left).id.text))
                semanticError("Varibales must init");
            left = Varibales.getValue(((VarNode) binNode.left).id.text);
        } else if (binNode.left instanceof NumberNode)
            left = HexNumber.toDecimal(((NumberNode) binNode.left).number.text);
        else
            semanticError("Unknown argument of multiplicate ");

        if (binNode.right instanceof VarNode) {
            if (!Varibales.hasVar(((VarNode) binNode.right).id.text))
                semanticError("Varibales must init");
            right = Varibales.getValue(((VarNode) binNode.right).id.text);
        } else if (binNode.left instanceof NumberNode)
            right = HexNumber.toDecimal(((NumberNode) binNode.right).number.text);
        else
            semanticError("Unknown argument of multiplicate ");
        Varibales.putVar(((VarNode) binNode.left).id.text, left * right);

    }

    public static void evalVarNode(VarNode node){
        Scanner sc=new Scanner(System.in);
        System.out.println("Please input var "+(node).id.text);
        Varibales.putVar((node).id.text,HexNumber.toDecimal(sc.nextLine()));
        return;
    }
}
