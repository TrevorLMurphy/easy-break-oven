import java.io.PushbackReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MurphScanner {

    public MurphScanner(String fileName) {
        PushbackReader br = null;
        try {
            br = new PushbackReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Lexer lexer = new Lexer(br);
        Lexeme lexeme = lexer.lex();

        while (!lexeme.type.equals("END")) {
            System.out.println(lexeme.toString());
            lexeme = lexer.lex();
        }

        System.out.println(lexeme.toString());
    }
}
