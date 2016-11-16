import java.io.IOException;
import java.io.PushbackReader;

public class Lexer {

    private PushbackReader reader;
    public int lineNumber;

    public Lexer(PushbackReader reader) {
        this.reader = reader;
        lineNumber = 1;
    }

    public Lexeme lex() {
        char ch;
        try {

            int r = reader.read();
            r = skipWhiteSpace(r);
            if (r == -1) {
                reader.close();
                return new Lexeme("END");
            }

            ch = (char) r;
            switch (ch) {
                case '#':
                    return skipComment(r);
                case '^':
                   return skipLongComment();
                case '(':
                    return new Lexeme("OPAREN");
                case ')':
                    return new Lexeme("CPAREN");
                case ',':
                    return new Lexeme("COMMA");
                case '+':
                case '*':
                case '-':
                case '/':
                case '<':
                case '>':
                case '=':
                case '!':
                case '&':
                case '|':
                    return lexOperator(readOperator(ch));
                case '{':
                    return new Lexeme("OBRACE");
                case '}':
                    return new Lexeme("CBRACE");
                case ';':
                    return new Lexeme("SEMI");
                default:
                    if (Character.isDigit(ch)) {
                        return lexNumber(readMoreNum(ch));
                    } else if (Character.isAlphabetic(ch)) {
                        return lexKeyword(readMoreKeyword(ch));
                    } else if (ch == '\"') {
                        return new Lexeme("STRING", readMoreString(ch));
                    }
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        return new Lexeme("BAD_CHARACTER");
    }

    public Lexeme skipComment(int r) throws IOException {
        while (r != 10) { // Read until end of line
            r = reader.read();
        }
        return new Lexeme("COMMENT");
    }

    private Lexeme skipLongComment() throws IOException {
        int r = reader.read();
        char ch = (char) r;
        while (ch != '^') {
            if (r == -1) {
                System.out.println("YOU'RE MISSING THE ENDING ^ FOR YOUR BLOCK COMMENT ON LINE " + lineNumber);
                System.exit(-1);
            }
            r = reader.read();
            ch = (char) r;
        }
        return new Lexeme("COMMENT");
    }

    private int skipWhiteSpace(int r) throws IOException {
        if (r == 32) {
            return skipWhiteSpace(reader.read());
        } else if (r == 10) {
            lineNumber += 1;
            return skipWhiteSpace(reader.read());
        } else return r;
    }

    private String readOperator(char ch) throws IOException {
        String operString = "";
        operString += ch;
        char newCh = (char) reader.read();
        if (newCh == ch || newCh == '=') {
            operString += newCh;
            return operString;
        } else {
            reader.unread(newCh);
            return operString;
        }
    }

    private String readMoreNum(char ch) throws IOException {
        String numString = "";
        while (Character.isDigit(ch)) {
            numString += ch;
            ch = (char) reader.read();
        }
        reader.unread(ch);
        return numString;
    }

    private String readMoreKeyword(char ch) throws IOException {
        String keyString = "";
        while (Character.isAlphabetic(ch)) {
            keyString += ch;
            ch = (char) reader.read();
        }
        reader.unread(ch);
        return keyString;
    }

    private String readMoreString(char ch) throws IOException {
        String newString = "";
        newString += ch;
        int r = reader.read();
        ch = (char) r;

        while (ch != '\"') {
            if (r == -1) {
                System.out.println("YOU'RE MISSING AN END QUOTE ON LINE " + lineNumber);
                System.exit(-1);
            }
            newString += ch;
            r = reader.read();
            ch = (char) r;
        }

        newString += ch; // Needed to add the ending quote on
        return newString;
    }

    private Lexeme lexOperator(String oper) {
        switch (oper) {
            case "=": return new Lexeme("ASSIGN");
            case "==": return new Lexeme("EQUAL");
            case "!=": return new Lexeme("NOTEQUAL");
            case "+": return new Lexeme("PLUS");
            case "-": return new Lexeme("MINUS");
            case "*": return new Lexeme("MULT");
            case "/": return new Lexeme("DIVIDE");
            case ">": return new Lexeme("GREATER");
            case ">=": return new Lexeme("GEQUAL");
            case "<": return new Lexeme("LESS");
            case "<=": return new Lexeme("LEQUAL");
            case "&&": return new Lexeme("AND");
            case "||": return new Lexeme("OR");
            default:
                return new Lexeme("BAD_CHARACTER");
        }
    }

    private Lexeme lexNumber(String num) {
        return new Lexeme("INTEGER", Integer.parseInt(num));
    }

    private Lexeme lexKeyword(String key) {
        switch (key) {
            case "let":
                return new Lexeme("LET");
            case "func":
                return new Lexeme("FUNCTION");
            case "if":
                return new Lexeme("IF");
            case "else":
                return new Lexeme("ELSE");
            case "while":
                return new Lexeme("WHILE");
            case "true":
                return new Lexeme("TRUE");
            case "false":
                return new Lexeme("FALSE");
            case "print":
                return new Lexeme("PRINT");
            case "return":
                return new Lexeme("RETURN");
            default:
                return new Lexeme("VARIABLE", key);
        }
    }
}
