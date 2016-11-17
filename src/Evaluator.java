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
            case "PRINT": return evalPrint(tree.right, env);
            default:
                System.out.println("BAD EXPRESSION!");
                System.exit(0);
        }
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

    private Lexeme evalPrint(Lexeme tree, Lexeme env) {
        System.out.println(eval(tree, env));
        return null;
    }

    private Lexeme evalOperator(Lexeme tree, Lexeme env) {
        Lexeme left = eval(tree.left, env);
        Lexeme right = eval(tree.right, env);
        switch (tree.type) {
            case "PLUS": return new Lexeme("INTEGER", left.intVal + right.intVal);
            case "MINUS": return new Lexeme("INTEGER", left.intVal - right.intVal);
            case "MULT": return new Lexeme("INTEGER", left.intVal * right.intVal);
            case "DIVIDE": return new Lexeme("INTEGER", left.intVal / right.intVal);
            case "GEQUAL": return new Lexeme("BOOLEAN", left.intVal >= right.intVal);
            case "GREATER": return new Lexeme("BOOLEAN", left.intVal > right.intVal);
            case "LESS": return new Lexeme("BOOLEAN", left.intVal < right.intVal);
            case "LEQUAL": return new Lexeme("BOOLEAN", left.intVal <= right.intVal);
            case "EQUAL": return new Lexeme("BOOLEAN", left.intVal == right.intVal);
            case "NOTEQUAL": return new Lexeme("BOOLEAN", left.intVal != right.intVal);
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
        Lexeme var = tree.left;
        Lexeme val = eval(tree.right, env); // This could be an expression, so evaluate it.
        e.updateEnv(var, val, env);
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
        Lexeme closure = eval(tree.left, env);
        Lexeme args = tree.right;
        Lexeme params = closure.right.right.left;
        Lexeme body = closure.right.right.right;
        Lexeme senv = closure.left;
        Lexeme eargs = evalArgs(args, env);
        Lexeme xenv = e.extendEnv(senv, params, eargs);
        return eval(body, xenv);
    }

    private Lexeme evalVarDef(Lexeme tree, Lexeme env) {
        Lexeme val = eval(tree.right, env);
        e.insert(tree.left, val, env);
        return null;
    }
}
