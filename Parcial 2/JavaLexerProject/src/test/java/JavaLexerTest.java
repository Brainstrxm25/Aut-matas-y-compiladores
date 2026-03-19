import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class JavaLexerTest {

    @Test
    void testLiterals() {
        // Probamos Hexadecimal, Binario, Octal, Float y String
        String code = "0xFF 0b101 0755 3.14 \"Hola\"";
        List<JavaLexer.Token> tokens = JavaLexer.tokenize(code);
        
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.HEXADECIMALLIT), "Falta HEXADECIMALLIT");
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.BINARYLIT), "Falta BINARYLIT");
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.OCTALLIT), "Falta OCTALLIT");
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.FLOATLIT), "Falta FLOATLIT");
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.STRINGLIT), "Falta STRINGLIT");
    }

    @Test
    void testCommentsAndAnnotations() {
        String code = "/** javadoc */ @Override // comentario";
        List<JavaLexer.Token> tokens = JavaLexer.tokenize(code);
        
        assertEquals(JavaLexer.TokenType.JAVADOC, tokens.get(0).type());
        assertEquals(JavaLexer.TokenType.ANNOTATION, tokens.get(1).type());
        assertEquals(JavaLexer.TokenType.LINECOMMENT, tokens.get(2).type());
    }

    @Test
    void testOperatorsAndSeparators() {
        String code = "x += 5;";
        List<JavaLexer.Token> tokens = JavaLexer.tokenize(code);
        
        // x (ID), += (OPERATOR), 5 (INT), ; (SEPARATOR)
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.OPERATOR && t.lexeme().equals("+=")));
        assertTrue(tokens.stream().anyMatch(t -> t.type() == JavaLexer.TokenType.SEPARATOR && t.lexeme().equals(";")));
    }



    @Test
    void testLineAndColumn() {
        String code = "int a;\n  int b;";
        List<JavaLexer.Token> tokens = JavaLexer.tokenize(code);
        
        // Buscamos el token del identificador 'b'
        JavaLexer.Token tokenB = tokens.stream()
            .filter(t -> t.lexeme().equals("b"))
            .findFirst()
            .orElseThrow();
            
        assertEquals(2, tokenB.line(), "La variable 'b' debe estar en la línea 2");
        assertEquals(7, tokenB.column(), "La columna de 'b' es incorrecta");
    }
}