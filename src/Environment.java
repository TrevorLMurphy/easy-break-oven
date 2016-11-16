
public class Environment {

    public Lexeme cons(String type, Lexeme carVal, Lexeme cdrVal) {
        return new Lexeme(type, carVal, cdrVal);
    }

    public Lexeme car(Lexeme cell) {
        return cell.left;
    }

    public Lexeme cdr(Lexeme cell) {
        return cell.right;
    }

    public Lexeme setCar(Lexeme cell, Lexeme newVal) {
        cell.left = newVal;
        return cell;
    }

    public Lexeme setCdr(Lexeme cell, Lexeme newVal) {
        cell.right = newVal;
        return cell;
    }

    public Lexeme createEnv() {
        return extendEnv(null, null, null);
    }

    public Lexeme extendEnv(Lexeme env, Lexeme variables, Lexeme values) {
        return cons("ENV", makeTable(variables, values), env);
    }

    public Lexeme makeTable(Lexeme variables, Lexeme values) {
        return cons("TABLE", variables, values);
    }

    private boolean sameVar(Lexeme var1, Lexeme var2) {
        return var1.toString().equals(var2.toString());
    }

    public Lexeme lookupEnv(Lexeme variable, Lexeme env) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while (vars != null) {
                if (sameVar(variable, car(vars))) {
                    return car(vals);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(env);
        }

        System.out.println("VARIABLE: " + variable + " is undefined :(");
        System.exit(-1);
        return null;
    }

    public Lexeme updateEnv(Lexeme variable, Lexeme newVal, Lexeme env) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while (vars != null) {
                if (sameVar(variable, car(vars))) {
                    return setCar(vals, newVal);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(env);
        }

        System.out.println("VARIABLE: " + variable + " is undefined :(");
        System.exit(-1);
        return null;
    }

    public Lexeme insert(Lexeme variable, Lexeme value, Lexeme env) {
        Lexeme table = car(env);
        setCar(table, cons("GLUE", variable, car(table)));
        setCdr(table, cons("GLUE", value, cdr(table)));
        return value;
    }

    public Lexeme getVars(Lexeme env) {
        Lexeme table = car(env);
        return car(table);
    }

    public Lexeme getVals(Lexeme env) {
        Lexeme table = car(env);
        return cdr(table);
    }
}
