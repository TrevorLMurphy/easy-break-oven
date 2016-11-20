import java.io.IOException;
import java.util.ArrayList;

public class Evaluator {

    private Environment e;

    public Evaluator(Environment e) {
        this.e = e;
    }

    public Lexeme eval(Lexeme tree, Lexeme env) {
        if (tree == null) {
            return null;
        }
        switch (tree.type) {
            case "STATEMENTS": return evalStatements(tree, env);
            case "STATEMENT": return eval(tree.left, env);
            case "COMMENT": return null;
            case "INTEGER":
            case "STRING":
            case "BOOLEAN":
            case "NULL":
            case "ARRAY": return tree;
            case "ARRAY_ACCESS": return evalArray(tree, env);
            case "VARIABLE": return e.lookupEnv(tree, env);
            case "OPAREN": return eval(tree.right, env);
            case "PLUS":
            case "MINUS":
            case "MULT":
            case "DIVIDE":
            case "GREATER":
            case "LESS":
            case "GEQUAL":
            case "LEQUAL":
            case "NOTEQUAL":
            case "RAISE":
            case "EQUAL": return evalOperator(tree, env);
            case "AND": return evalAnd(tree, env);
            case "OR": return evalOr(tree, env);
            case "ASSIGN": return evalVarAssign(tree, env);
            case "LET": return evalVarDef(tree, env);
            case "FUNCTION": return evalFuncDef(tree, env);
            case "IF": return evalIf(tree, env);
            case "WHILE": return evalWhile(tree, env);
            case "FUNC_CALL": return evalFuncCall(tree, env);
            case "RETURN": return evalReturn(tree, env);
            case "DOT": return evalDot(tree, env);
            default:
                System.out.println("BAD EXPRESSION!");
                System.exit(0);
        }
        return null;
    }

    private Lexeme evalAdd(Lexeme tree) {
        Lexeme array = tree.left;
        Lexeme newItem = tree.right.left;
        array.arrayVal.add(newItem);
        return null;
    }

    private Lexeme evalPop(Lexeme tree) {
        return null;
    }

    private Lexeme evalStatements(Lexeme tree, Lexeme env) {
        Lexeme statements = tree;
        Lexeme currentStatement = tree.left;
        Lexeme result = null;
        while (currentStatement != null) {
            result = eval(currentStatement, env);
            if (result != null) {
                return result;
            }
            statements = statements.right;
            currentStatement = statements.left;
        }
        return result;
    }

    private Lexeme evalReturn(Lexeme tree, Lexeme env) {
        return eval(tree.left, env);
    }

    private Lexeme evalArray(Lexeme tree, Lexeme env) {
        int index = eval(tree.right, env).intVal;
        ArrayList array = (eval(tree.left, env).arrayVal);
        if (index >= array.size()) {
            System.out.println("Index out of bounds!");
            System.exit(0);
        }
        return (Lexeme) array.get(index);
    }

    private Lexeme evalLength(Lexeme eargs) {
        switch (eargs.left.type) {
            case "STRING":
                return new Lexeme("INTEGER", eargs.left.strVal.length());
            case "ARRAY":
                return new Lexeme("INTEGER", eargs.left.arrayVal.size());
            default:
                System.out.println("TRYING TO TAKE LENGTH OF TYPE " + eargs.left.type + " DOES NOT WORK!");
                System.exit(0);
                return null;
        }
    }

    private Lexeme evalReadInput(Lexeme eargs) {
        if (eargs != null) {
            System.out.println("ERROR: READ DOES NOT TAKE ANY ARGUMENTS!");
            System.exit(0);
        }
        ArrayList<Lexeme> inputArray = new ArrayList<>();
        int ch;
        try {
            while ((ch = System.in.read()) != -1) {
                if (ch != '\n' && ch != '\r') {
                    switch (ch) {
                        case '^':
                            inputArray.add(new Lexeme("RAISE"));
                            break;
                        case '+':
                            inputArray.add(new Lexeme("PLUS"));
                            break;
                        case '*':
                            inputArray.add(new Lexeme("MULT"));
                            break;
                        case '-':
                            inputArray.add(new Lexeme("MINUS"));
                            break;
                        case '/':
                            inputArray.add(new Lexeme("DIVIDE"));
                            break;
                        default:
                            if (Character.isDigit(ch)) {
                                String num = "";
                                while (Character.isDigit(ch) && ch != -1) {
                                    num += (char) ch;
                                    ch = System.in.read();
                                }
                                inputArray.add(new Lexeme("INTEGER", Integer.parseInt(num)));
                            }
                            break;
                    }
                }
            }
            return new Lexeme("ARRAY", inputArray);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private Lexeme evalPrintln(Lexeme eargs) {
        Lexeme arg;
        while (eargs != null) {
            arg = eargs.left;
            System.out.println(arg);
            eargs = eargs.right;
        }
        return null;
    }

    private Lexeme evalPrint(Lexeme eargs) {
        Lexeme arg;
        while (eargs != null) {
            arg = eargs.left;
            System.out.print(arg + " ");
            eargs = eargs.right;
        }
        return null;
    }

    private Lexeme evalOperator(Lexeme tree, Lexeme env) {
        Lexeme left = eval(tree.left, env);
        Lexeme right = eval(tree.right, env);
        switch (tree.type) {
            case "PLUS":
                if (left.isInt()) return new Lexeme("INTEGER", left.intVal + right.intVal);
            case "MINUS":
                if (left.isInt()) return new Lexeme("INTEGER", left.intVal - right.intVal);
            case "MULT":
                if (left.isInt()) return new Lexeme("INTEGER", left.intVal * right.intVal);
            case "RAISE":
                if (left.isInt()) return new Lexeme("INTEGER", (int) Math.pow(left.intVal, right.intVal));
            case "DIVIDE":
                if (left.isInt()) return new Lexeme("INTEGER", left.intVal / right.intVal);
            case "GEQUAL":
                if (left.isInt()) return new Lexeme("BOOLEAN", left.intVal >= right.intVal);
            case "GREATER":
                if (left.isInt()) return new Lexeme("BOOLEAN", left.intVal > right.intVal);
            case "LESS":
                if (left.isInt()) return new Lexeme("BOOLEAN", left.intVal < right.intVal);
            case "LEQUAL":
                if (left.isInt()) return new Lexeme("BOOLEAN", left.intVal <= right.intVal);
            case "EQUAL":
                if (left.isInt()) {
                    return new Lexeme("BOOLEAN", left.intVal == right.intVal);
                } else if (left.isString()) {
                    return new Lexeme("BOOLEAN", left.strVal.equals(right.strVal));
                } else {
                    return new Lexeme("BOOLEAN", left.type.equals(right.type));
                }
            case "NOTEQUAL":
                if (left.isInt()) {
                    return new Lexeme("BOOLEAN", left.intVal != right.intVal);
                } else {
                    return new Lexeme("BOOLEAN", !left.type.equals(right.type));
                }

            default:
                System.out.println("Defaulted on the evalOperator function for some reason...");
                System.exit(0);
                return null;
        }
    }

    private Lexeme evalAnd(Lexeme tree, Lexeme env) {
        return null;
    }

    private Lexeme evalOr(Lexeme tree, Lexeme env) {
        return null;
    }

    private Lexeme evalVarAssign(Lexeme tree, Lexeme env) {
        Lexeme result = eval(tree.right, env);
        if (tree.left.type.equals("VARIABLE")) {
            e.updateEnv(tree.left, result, env);
        } else if (tree.left.type.equals("DOT")) {
            Lexeme object = eval(tree.left.left, env);
            e.updateEnv(tree.left.right, result, object);
        } else {
            System.out.println("THIS ASSIGNMENT DOESN'T WORK!");
            System.exit(0);
        }
        return null;
    }

    private Lexeme evalWhile(Lexeme tree, Lexeme env) {
        Lexeme whileExpression = tree.left;
        Lexeme whileBody = tree.right;
        Lexeme local = e.extendEnv(env, e.getVars(env), e.getVals(env));
        while (eval(whileExpression, local).boolVal) {
            eval(whileBody, local);
        }
        return null;
    }

    private Lexeme evalIf(Lexeme tree, Lexeme env) {
        Lexeme ifExpression = tree.left.left;
        Lexeme ifBody = tree.left.right;
        Lexeme elseStatement = tree.right;
        Lexeme local = e.extendEnv(env, e.getVars(env), e.getVals(env));
        if (eval(ifExpression, local).boolVal) {
            return eval(ifBody, local);
        } else {
            return eval(elseStatement, local);
        }
    }

    private Lexeme evalFuncDef(Lexeme tree, Lexeme env) {
        if (tree.left == null) {
            return evalLambda(tree, env);
        }
        Lexeme closure = e.cons("CLOSURE", env, tree);
        e.insert(tree.left, closure, env);
        return null;
    }

    private Lexeme evalLambda(Lexeme tree, Lexeme env) {
        Lexeme args = e.getVals(env);
        Lexeme params = tree.right.left;
        Lexeme body = tree.right.right;
        Lexeme local = e.extendEnv(env, params, args);
        return eval(body, local);
    }

    private Lexeme evalArgs(Lexeme args, Lexeme env) {
        if (args == null) return null;
        return e.cons("GLUE", eval(args.left, env), evalArgs(args.right, env));
    }

    private Lexeme evalFuncCall(Lexeme tree, Lexeme env) {
        String funcName = "";
        if (tree.left != null) funcName = tree.left.varVal;
        Lexeme args = tree.right;
        Lexeme eargs = evalArgs(args, env);
        switch (funcName) {
            case "print":
                return evalPrint(eargs);
            case "println":
                return evalPrintln(eargs);
            case "addItem":
                return evalAdd(eargs);
            case "length":
                return evalLength(eargs);
            case "read":
                return evalReadInput(eargs);
            case "type":
                return evalType(eargs);
        }
        Lexeme closure = eval(tree.left, env);

        Lexeme params = closure.right.right.left;
        Lexeme body = closure.right.right.right;
        Lexeme senv = closure.left;

        Lexeme xenv = e.extendEnv(senv, params, eargs);
        e.insert(new Lexeme("VARIABLE", "this"), xenv, xenv);
        return eval(body, xenv);
    }

    private Lexeme evalType(Lexeme eargs) {
        return new Lexeme("STRING", "\"" + eargs.left.type + "\"");
    }

    private Lexeme evalVarDef(Lexeme tree, Lexeme env) {
        Lexeme val = eval(tree.right, env);
        e.insert(tree.left, val, env);
        return null;
    }

    private Lexeme evalDot(Lexeme tree, Lexeme env) {
        Lexeme object = eval(tree.left, env);
        Lexeme local = e.extendEnv(object, e.getVars(env), e.getVals(env));
        return eval(tree.right, local);
    }
}
