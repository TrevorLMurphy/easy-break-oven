import java.io.PushbackReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser {

    private Lexeme lexeme;
    private Lexer lexer;

    public Parser(String fileName) {
        PushbackReader br = null;
        try {
            br = new PushbackReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lexer = new Lexer(br);
    }

    public Lexeme parse() {
        advance();
        return statements();
    }

    private boolean check(String type) {
        return lexeme.type.equals(type);
    }

    private Lexeme advance() {
        Lexeme prevLexeme = lexeme;
        lexeme = lexer.lex();
        return prevLexeme;
    }

    private Lexeme match(String type) {
        matchNoAdvance(type);
        return advance();
    }

    private void matchNoAdvance(String type) {
        if (!check(type)) {
            System.out.println("SYNTAX ERROR: Line " + lexer.lineNumber);
            if (type.equals("SEMI")) {
                System.out.println("Looks like you're missing a semicolon!");
            } else {
                System.out.println("GOT " + type + "..." + "NEEDED " + lexeme.type);
            }
            System.exit(0);
        }
    }

    private boolean varDefPending() {
        return check("LET");
    }

    private boolean printPending() {
        return check("PRINT");
    }

    private boolean statementPending() {
        return  expressionPending() || // This also handles func calls and var assigns
                varDefPending() ||
                printPending() ||
                check("COMMENT") ||
                ifPending() ||
                whilePending() ||
                funcDefPending() ||
                returnPending();
    }

    private Lexeme block() {
        match("OBRACE");
        Lexeme tree = statements();
        match("CBRACE");
        return tree;
    }

    private boolean ifPending() {
        return check("IF");
    }

    private Lexeme ifStatement() {
        Lexeme tree = match("IF");
        match("OPAREN");
        tree.left = new Lexeme("GLUE");
        tree.left.left = expression();
        match("CPAREN");
        tree.left.right = block();
        if (elsePending()) {
            tree.right = elseStatement();
        }
        return tree;
    }

    private boolean elsePending() {
        return check("ELSE");
    }

    private Lexeme elseStatement() {
        match("ELSE");
        Lexeme tree;
        if (ifPending()) {
            tree = ifStatement();
        } else {
            tree = block();
        }
        return tree;
    }

    private boolean whilePending() {
        return check("WHILE");
    }

    private Lexeme whileLoop() {
        Lexeme tree = match("WHILE");
        match("OPAREN");
        tree.left = expression();
        match("CPAREN");
        tree.right = block();
        return tree;
    }

    private boolean varPending() {
        return check("VARIABLE");
    }

    private Lexeme varAssign(Lexeme temp) {
        Lexeme tree;
        if (check("DOT")) { // Object assignment
            Lexeme dotTree = match("DOT");
            dotTree.left = temp;
            dotTree.right = match("VARIABLE");
            tree = match("ASSIGN");
            tree.left = dotTree;
            tree.right = expression();
        } else {
            tree = match("ASSIGN");
            tree.left = temp;
            if (check("OBRACK")) {
                tree.right = buildArray();
            } else tree.right = expression();
        }
        return tree;
    }

    private Lexeme dot(Lexeme var) {
        Lexeme tree = match("DOT");
        tree.left = var;
        tree.right = expression();
        if (check("ASSIGN")) {
            Lexeme dotTree = match("ASSIGN");
            dotTree.left = tree;
            dotTree.right = expression();
            return dotTree;
        } else {
            return tree;
        }
    }

    private Lexeme funcCall(Lexeme temp) {
        Lexeme tree = new Lexeme("FUNC_CALL");
        tree.left = temp;
        match("OPAREN");
        tree.right = optArgList();
        match("CPAREN");
        return tree;
    }

    private Lexeme optArgList() {
        if (argListPending()) {
            return argList();
        } else return null;
    }

    private Lexeme argList() {
        Lexeme tree = new Lexeme("GLUE");
        tree.left = expression();
        if (check("COMMA")) {
            match("COMMA");
            tree.right = argList();
        }
        return tree;
    }

    private boolean argListPending() {
        return unaryPending();
    }

    private boolean returnPending() {
        return check("RETURN");
    }

    private Lexeme returnStatement() {
        Lexeme tree = match("RETURN");
        tree.left = expression();
        return tree;
    }

    private Lexeme statement() {
        Lexeme tree = new Lexeme("STATEMENT");
        if (check("COMMENT")) {
            tree.left = match("COMMENT");
        } else if (varDefPending()) {
            tree.left = varDef();
            match("SEMI");
        } else if (funcDefPending()) {
            tree.left = functionDef();
        } else if (ifPending()) {
            tree.left = ifStatement();
        } else if (varPending()) {
            Lexeme temp = match("VARIABLE");
            if (check("OPAREN")) {
                tree.left = funcCall(temp);
                match("SEMI");
            } else if (check("DOT")) {
                tree.left = dot(temp);
                match("SEMI");
            } else {
                tree.left = varAssign(temp);
                match("SEMI");
            }
        } else if (whilePending()) {
            tree.left = whileLoop();
        } else if (returnPending()) {
            tree.left = returnStatement();
            match("SEMI");
        }
        return tree;
    }

    private Lexeme statements() {
        Lexeme tree = new Lexeme("STATEMENTS");
        if (statementPending()) {
            tree.left = statement();
            tree.right = statements();
        }
        return tree;
    }

    private Lexeme unary() {
        Lexeme tree;
        if (check("INTEGER")) {
            tree = match("INTEGER");
        } else if (check("STRING")) {
            tree = match("STRING");
        } else if (check("BOOLEAN")) {
            tree = match("BOOLEAN");
        } else if (check("NULL")) {
            tree = match("NULL");
        } else { // Must be a variable
            tree = match("VARIABLE");
        }
        return tree;
    }

    private boolean unaryPending() {
        return  check("INTEGER") ||
                check("STRING") ||
                check("VARIABLE") ||
                check("BOOLEAN") ||
                check("NULL");
    }

    private Lexeme operator() {
        return advance();
    }

    private boolean operatorPending() {
        return  check("PLUS") ||
                check("MINUS") ||
                check("MULT") ||
                check("DIVIDE") ||
                check("EQUAL") ||
                check("NOTEQUAL") ||
                check("GREATER") ||
                check("GEQUAL") ||
                check("LESS") ||
                check("LEQUAL") ||
                check("AND") ||
                check("OR") ||
                check("RAISE");
    }

    private Lexeme arrayAccess() {
        Lexeme tree = new Lexeme("ARRAY_ACCESS");
        match("OBRACK");
        tree.right = unary();
        match("CBRACK");
        return tree;
    }

    private Lexeme expression() {
        Lexeme tree;
        if (funcDefPending()) {
            tree = functionDef();
            return tree;
        }
        tree = unary();
        while (check("DOT")) {
            Lexeme temp = match("DOT");
            temp.left = tree;
            Lexeme temp2 = match("VARIABLE");
            if (check("OPAREN")) temp.right = funcCall(temp2);
            else temp.right = temp2;
            tree = temp;
        }
        if (check("OBRACK")) {
            Lexeme temp = arrayAccess();
            temp.left = tree;
            tree = temp;
        } else if (check("OPAREN")) {
            tree = funcCall(tree);
        }
        while (operatorPending()) {
            Lexeme temp = operator();
            temp.left = tree;
            temp.right = expression();
            tree = temp;
        }
        return tree;
    }

    private boolean expressionPending() {
        return unaryPending();
    }

    private Lexeme functionDef() {
        Lexeme tree = match("FUNCTION");
        if (check("VARIABLE")) {
            tree.left = match("VARIABLE");
        }
            match("OPAREN");
            tree.right = new Lexeme("GLUE");
            tree.right.left = optParameterList();
            match("CPAREN");
            tree.right.right = block();
        return tree;
    }

    private boolean funcDefPending() {
        return check("FUNCTION");
    }

    private Lexeme optParameterList() {
        if (parameterListPending()) {
            return parameterList();
        } else return null;
    }

    private Lexeme parameterList() {
        Lexeme tree = new Lexeme("GLUE");
        tree.left = match("VARIABLE");
        if (check("COMMA")) {
            match("COMMA");
            tree.right = parameterList();
        }
        return tree;
    }

    private boolean parameterListPending() {
        return check("VARIABLE");
    }

    private boolean arrayPending() {
        return check("OBRACK");
    }

    private Lexeme buildArray() {
        match("OBRACK");
        ArrayList<Lexeme> array = new ArrayList<Lexeme>();
        while (true) {
            if (unaryPending()) array.add(unary());
            if (!check("COMMA")) {
                match("CBRACK");
                return new Lexeme ("ARRAY", array);
            }
            match("COMMA");
        }
    }

    private Lexeme varDef() {
        Lexeme tree = match("LET");
        tree.left = match("VARIABLE");
        match("ASSIGN");
        if (arrayPending()) {
            tree.right = buildArray();
        } else {
            tree.right = expression();
        }
        return tree;
    }
}
