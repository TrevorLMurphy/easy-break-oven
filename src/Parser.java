import java.io.PushbackReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
        Lexeme tree = statements();
        return tree;
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
            System.out.println("GOT " + type + "..." + "NEEDED " + lexeme.type);
            System.exit(-1);
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
                funcDefPending();
    }

    private boolean blockPending() {
        return check("OBRACE");
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
        Lexeme tree = match("ASSIGN");
        tree.left = temp;
        tree.right = expression();
        match("SEMI");
        return tree;
    }

    private Lexeme funcCall(Lexeme temp) {
        Lexeme tree = new Lexeme("FUNC_CALL");
        tree.left = temp;
        match("OPAREN");
        tree.right = optArgList();
        match("CPAREN");
        match("SEMI");
        return tree;
    }

    private Lexeme optArgList() {
        if (argListPending()) {
            return argList();
        } else return null;
    }

    private Lexeme argList() {
        Lexeme tree = new Lexeme("GLUE");
        tree.left = unary();
        if (check("COMMA")) {
            match("COMMA");
            tree.right = argList();
        }
        return tree;
    }

    private boolean argListPending() {
        return unaryPending();
    }

    private Lexeme statement() {
        Lexeme tree = new Lexeme("STATEMENT");
        if (check("COMMENT")) {
            tree.left = match("COMMENT");
        } else if (varDefPending()) {
            tree.left = varDef();
        } else if (funcDefPending()) {
            tree.left = functionDef();
        } else if (printPending()) {
            tree.left = print();
        } else if (ifPending()) {
            tree.left = ifStatement();
        } else if (varPending()) {
            Lexeme temp = match("VARIABLE");
            if (check("OPAREN")) tree.left = funcCall(temp);
            else tree.left = varAssign(temp);
        } else if (whilePending()) {
            tree.left = whileLoop();
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

    private Lexeme print() {
        Lexeme tree = match("PRINT");
        match("OPAREN");
        tree.right = expression();
        match("CPAREN");
        match("SEMI");
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
        } else { // Must be a variable
            tree = match("VARIABLE");
        }
        return tree;
    }

    private boolean unaryPending() {
        return  check("INTEGER") ||
                check("STRING") ||
                check("VARIABLE");
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
                check("OR");
    }

    private Lexeme expression() {
        Lexeme tree = unary();
        while (operatorPending()) {
            Lexeme temp = operator();
            temp.left = tree;
            temp.right = unary();
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
            match("OPAREN");
            tree.right = new Lexeme("GLUE");
            tree.right.left = optParameterList();
            match("CPAREN");
            tree.right.right = block();
        }
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

    private Lexeme varDef() {
        Lexeme tree = match("LET");
        tree.left = match("VARIABLE");
        match("ASSIGN");
        if (funcDefPending()) {
            tree.right = functionDef();
        } else {
            tree.right = expression();
        }
        match("SEMI");
        return tree;
    }
}
