import java.util.regex.*;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.io.IOException;

public class Ejemplo {

    public enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, SEPARATOR,
        FLOATLIT, HEXADECIMALLIT, BINARYLIT, OCTALLIT, STRINGLIT, CHARLIT, 
        INTEGERLIT, JAVADOC, BLOCKCOMMENT, LINECOMMENT, UNKNOWN // UNKNOWN se usa para errores
    }

    public record Token(TokenType type, String lexeme, int line, int column) {
        @Override
        public String toString() {
            // Si el tipo es UNKNOWN, le damos un formato de error más visible
            String prefix = (type == TokenType.UNKNOWN) ? " [!] ERROR: " : " ";
            return String.format("%s%-15s | %-20s | L:%-3d | C:%-3d", 
                prefix, type, lexeme.replace("\n", "\\n"), line, column);
        }
    }

    private static final String KW_PATTERN =
        "\\b(abstract|assert|boolean|break|byte|case|catch|char|" +
        "class|const|continue|default|do|double|else|enum|extends|" +
        "final|finally|float|for|if|implements|import|instanceof|" +
        "int|interface|long|new|package|private|protected|public|" +
        "return|short|static|super|switch|synchronized|this|throw|" +
        "throws|try|void|volatile|while|true|false|null)\\b";

    private static final Pattern MASTER = Pattern.compile(
        "(?<JAVADOC>/\\*\\*[\\s\\S]*?\\*/)|" +
        "(?<BLOCKCOMMENT>/\\*[\\s\\S]*?\\*/)|" +
        "(?<LINECOMMENT>//[^\\n]*)|" +
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
        "[+\\-*/%<>=!&|^~?:.])|" +
        "(?<SEPARATOR>[(){}\\[\\];,])|" +
        "(?<WHITESPACE>[ \\t\\r\\n]+)",
        Pattern.MULTILINE
    );

    public static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        Matcher m = MASTER.matcher(source);
        int currentLine = 1;
        int lastNewLinePos = -1;
        int pos = 0;

        while (m.find()) {
            // DETECCIÓN DE ERRORES: Si hay un hueco entre el último match y el actual
            if (m.start() > pos) {
                String errorLexeme = source.substring(pos, m.start());
                // Procesamos el lexema desconocido caracter por caracter para no perder la cuenta de columnas
                for (int i = 0; i < errorLexeme.length(); i++) {
                    char c = errorLexeme.charAt(i);
                    if (!Character.isWhitespace(c)) {
                        int col = (pos + i) - lastNewLinePos;
                        tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c), currentLine, col));
                    }
                    if (c == '\n') {
                        currentLine++;
                        lastNewLinePos = pos + i;
                    }
                }
            }

            int col = m.start() - lastNewLinePos;
            String match = m.group();

            if (m.group("WHITESPACE") != null) {
                actualizarPosicion(match, m.start()); // Solo actualizamos lineas/columnas
            } else {
                TokenType type = determineType(m);
                if (type != null) {
                    tokens.add(new Token(type, match, currentLine, col));
                }
            }
            
            // Actualizar lineas y columnas basado en el match actual (por si hay comentarios multilínea)
            for (int i = 0; i < match.length(); i++) {
                if (match.charAt(i) == '\n') {
                    currentLine++;
                    lastNewLinePos = m.start() + i;
                }
            }
            pos = m.end();
        }

        // Error al final del archivo (si sobran caracteres que no coinciden con nada)
        if (pos < source.length()) {
            String residue = source.substring(pos);
            for (int i = 0; i < residue.length(); i++) {
                if (!Character.isWhitespace(residue.charAt(i))) {
                    tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(residue.charAt(i)), currentLine, (pos + i) - lastNewLinePos));
                }
            }
        }

        return tokens;
    }

    private static void actualizarPosicion(String match, int startPos) {
    }

    private static TokenType determineType(Matcher m) {
        if (m.group("JAVADOC") != null) return TokenType.JAVADOC;
        if (m.group("BLOCKCOMMENT") != null) return TokenType.BLOCKCOMMENT;
        if (m.group("LINECOMMENT") != null) return TokenType.LINECOMMENT;
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
        String fileName = "Ejemplo.java";
        try {
            String content = Files.readString(Paths.get(fileName));
            List<Token> tokens = tokenize(content);

            System.out.println(String.format("%-15s | %-20s | %-8s | %-8s", "TIPO", "LEXEMA", "LINEA", "COLUMNA"));
            System.out.println("-".repeat(70));
            
            for (Token t : tokens) {
                System.out.println(t);
            }

            long errores = tokens.stream().filter(t -> t.type() == TokenType.UNKNOWN).count();
            System.out.println("\nRESUMEN:");
            System.out.println("Total de tokens: " + tokens.size());
            System.out.println("Errores encontrados: " + errores);

        } catch (IOException e) {
            System.err.println("Error: No se pudo encontrar el archivo " + fileName);
        }
    }
}