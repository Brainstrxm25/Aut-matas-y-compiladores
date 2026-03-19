import java.util.regex.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.io.*;

public class JavaLexer {

    public enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, SEPARATOR,
        FLOATLIT, HEXADECIMALLIT, BINARYLIT, OCTALLIT, STRINGLIT, CHARLIT,
        INTEGERLIT, JAVADOC, BLOCKCOMMENT, LINECOMMENT, UNKNOWN, ANNOTATION
    }

    public record Token(TokenType type, String lexeme, int line, int column) {
        @Override
        public String toString() {
            return String.format("%-15s | %-20s | L:%-3d | C:%-3d", type, lexeme.replace("\n", "\\n"), line, column);
        }
    }

    public static class LexicalError {
        private final int line, column;
        private final String illegal;

        public LexicalError(String illegal, int line, int col) {
            this.illegal = illegal;
            this.line = line;
            this.column = col;
        }

        @Override
        public String toString() {
            return String.format("Error léxico [L:%d, C:%d]: Carácter ilegal '%s'", line, column, illegal);
        }
    }

    private static List<LexicalError> errorList = new ArrayList<>();

    private static final String KW_PATTERN =
        "\\b(abstract|assert|boolean|break|byte|case|catch|char|" +
        "class|const|continue|default|do|double|else|enum|extends|" +
        "final|finally|float|for|if|implements|import|instanceof|" +
        "int|interface|long|new|package|private|protected|public|" +
        "return|short|static|super|switch|synchronized|this|throw|" +
        "throws|try|void|volatile|while|true|false|null|import|package|class)\\b";

    private static final Pattern MASTER = Pattern.compile(
        "(?<JAVADOC>/\\*\\*[\\s\\S]*?\\*/)|" +
        "(?<BLOCKCOMMENT>/\\*[\\s\\S]*?\\*/)|" +
        "(?<LINECOMMENT>//[^\\n]*)|" +
        "(?<ANNOTATION>@[a-zA-Z_$][a-zA-Z0-9_$]*)|" +
        "(?<STRINGLIT>\"([^\"\\\\]|\\\\.)*\")|" +
        "(?<CHARLIT>'([^'\\\\]|\\\\.)')|" +
        "(?<FLOATLIT>[0-9][0-9_]*\\.[0-9][0-9_]*([eE][+-]?[0-9]+)?[fFdD]?)|" +
        "(?<HEXADECIMALLIT>0[xX][0-9a-fA-F][0-9a-fA-F_]*[lL]?)|" +
        "(?<BINARYLIT>0[bB][01][01_]*[lL]?)|" +
        "(?<OCTALLIT>0[0-7]+[lL]?)|" +
        "(?<INTEGERLIT>0|[1-9][0-9_]*[lL]?)|" + 
        "(?<KEYWORD>" + KW_PATTERN + ")|" +
        "(?<IDENTIFIER>[a-zA-Z_$][a-zA-Z0-9_$]*)|" +
        "(?<OPERATOR>==|!=|<=|>=|&&|\\|\\||<<|>>>|>>|\\+\\+|--|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|" +
        "[+\\-*/%<>=!&#|^~?:.])|" +
        "(?<SEPARATOR>[(){}\\[\\];,])|" +
        "(?<WHITESPACE>[ \\t\\r\\n]+)",
        Pattern.MULTILINE
    );

    public static List<Token> tokenize(String source) {
        errorList.clear();
        List<Token> tokens = new ArrayList<>();
        Matcher m = MASTER.matcher(source);
        int currentLine = 1;
        int lastNewLinePos = -1;
        int pos = 0;

        while (m.find()) {
            if (m.start() > pos) {
                String unknownStr = source.substring(pos, m.start());
                for (int i = 0; i < unknownStr.length(); i++) {
                    char c = unknownStr.charAt(i);
                    if (!Character.isWhitespace(c)) {
                        int col = (pos + i) - lastNewLinePos;
                        errorList.add(new LexicalError(String.valueOf(c), currentLine, col));
                    }
                    if (c == '\n') {
                        currentLine++;
                        lastNewLinePos = pos + i;
                    }
                }
            }

            int col = m.start() - lastNewLinePos;
            String match = m.group();

            if (m.group("WHITESPACE") == null) {
                TokenType type = determineType(m);
                if (type != null) {
                    tokens.add(new Token(type, match, currentLine, col));
                }
            }

            for (int i = 0; i < match.length(); i++) {
                if (match.charAt(i) == '\n') {
                    currentLine++;
                    lastNewLinePos = m.start() + i;
                }
            }
            pos = m.end();
        }
        return tokens;
    }

    private static TokenType determineType(Matcher m) {
        if (m.group("JAVADOC") != null) return TokenType.JAVADOC;
        if (m.group("BLOCKCOMMENT") != null) return TokenType.BLOCKCOMMENT;
        if (m.group("LINECOMMENT") != null) return TokenType.LINECOMMENT;
        if (m.group("ANNOTATION") != null) return TokenType.ANNOTATION;
        if (m.group("KEYWORD") != null) return TokenType.KEYWORD;
        if (m.group("FLOATLIT") != null) return TokenType.FLOATLIT;
        if (m.group("HEXADECIMALLIT") != null) return TokenType.HEXADECIMALLIT;
        if (m.group("BINARYLIT") != null) return TokenType.BINARYLIT;
        if (m.group("OCTALLIT") != null) return TokenType.OCTALLIT;
        if (m.group("INTEGERLIT") != null) return TokenType.INTEGERLIT;
        if (m.group("STRINGLIT") != null) return TokenType.STRINGLIT;
        if (m.group("CHARLIT") != null) return TokenType.CHARLIT;
        if (m.group("IDENTIFIER") != null) return TokenType.IDENTIFIER;
        if (m.group("OPERATOR") != null) return TokenType.OPERATOR;
        if (m.group("SEPARATOR") != null) return TokenType.SEPARATOR;
        return null;
    }

    public static void main(String[] args) {

        Path inputPath = Paths.get("JavaLexerProject/src/Ejemplo.java");
        Path parentDir = inputPath.getParent();
        if (parentDir == null) parentDir = Paths.get(".");

        Path outputPath = parentDir.resolve("resultado_analisis.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            String content = Files.readString(inputPath);
            List<Token> tokens = tokenize(content);

            writer.println("=".repeat(70));
            writer.println(String.format("%-15s | %-20s | %-8s | %-8s", "TIPO", "LEXEMA", "LINEA", "COLUMNA"));
            writer.println("-".repeat(70));

            for (Token t : tokens) {
                writer.println(t);
            }

            if (!errorList.isEmpty()) {
                writer.println("\n" + "! ".repeat(10) + "ERRORES ENCONTRADOS" + " !".repeat(10));
                errorList.forEach(writer::println);
            }

            writer.println("\n" + "=".repeat(70));
            writer.println("ESTADISTICAS DE FRECUENCIA");
            writer.println("=".repeat(70));
            tokens.stream()
                .collect(Collectors.groupingBy(Token::type, Collectors.counting()))
                .forEach((k, v) -> writer.println(String.format("%-15s: %d", k, v)));
           
            writer.println("-".repeat(70));
            writer.println("Total de tokens: " + tokens.size());
            writer.println("Total de errores: " + errorList.size());

            System.out.println("Ya quedo");
            System.out.println("El resultado de la practica esta en: " + outputPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}