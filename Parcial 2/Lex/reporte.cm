# C/C++ Lexer con Arquitectura SOLID (Proyecto)

> **Materia:** Autómatas y Compiladores  
> **Proyecto:** Analizador Léxico en Flex/C con Inyección de Dependencias  
> **Alumno:** Navarrete Torres Oscar

---

## Definición de Tokens y Tabla de Especificación

| Categoría | Ejemplos de lexemas detectados |
| :--- | :--- |
| **KEYWORD** | `int`, `return`, `class`, `constexpr`, `if`, `while`, `namespace`, `public` |
| **IDENTIFIER** | `main`, `contador`, `_variable`, `var123` |
| **INTLIT** | `42`, `0`, `1000uL`, `7` |
| **FLOATLIT** | `3.14`, `1.5f`, `2.5e-3`, `0.5F` |
| **HEXLIT** | `0xFF`, `0x1A`, `0x7euL` |
| **BINLIT** | `0b1010`, `0B11110000` |
| **OCTALLIT** | `0755`, `0644` |
| **STRINGLIT** | `"Hola Mundo"`, `"Resultado: %d\n"`, `""` |
| **CHARLIT** | `'a'`, `'\n'`, `'\\'`, `'\''` |
| **OPERATOR** | `==`, `!=`, `<<=`, `>>=`, `++`, `--`, `+=`, `=`, `+`, `-`, `->`, `::` |
| **SEPARATOR** | `(`, `)`, `{`, `}`, `[`, `]`, `;`, `,` |
| **LINECOMMENT** | `// esto es un comentario de una línea` |
| **BLOCKCOMMENT** | `/* bloque de comentario multilínea */` |
| **ERROR** | Caracteres no reconocidos por la gramática (ej. `@`, `¿`, `$`) |

---

## Tabla de Patrones

| Token / Grupo | Expresión Regular (Regex) | Descripción Técnica |
| :--- | :--- | :--- |
| **BLOCKCOMMENT**| `"/*"([^*]\|\*+[^*/])*\*+"/"` | Captura comentarios multilinea en C. Requiere un bucle interno en C para contabilizar los `\n` y no perder la cuenta de líneas. |
| **LINECOMMENT** | `"//"[^\n]*` | Consume todo desde `//` hasta encontrar un salto de línea. |
| **STRINGLIT** | `\"([^\"\\]\|\\.)*\"` | Permite secuencias de escape (como `\"` o `\n`) sin romper la captura de la cadena. |
| **CHARLIT** | `\'([^\'\\]\|\\.)\'` | Captura caracteres simples o escapados entre comillas simples. |
| **FLOATLIT** | `{DIGIT}+\.{DIGIT}*([eE][+-]?{DIGIT}+)?[fFlL]?` | Soporta puntos decimales, notación científica y sufijos de tipo. |
| **HEXLIT** | `0[xX]{HEX}+[uUlL]*` | Captura números base 16. Se declara antes para evitar colisiones con enteros. |
| **BINLIT** | `0[bB][01]+[uUlL]*` | Captura números en base 2 con prefijo `0b`. |
| **OCTALLIT** | `0[0-7]+[uUlL]*` | Captura números en base 8 que inician con el prefijo `0`. |
| **INTLIT** | `(0\|[1-9]{DIGIT}*)[uUlL]*` | Enteros decimales estándar (base 10) con sufijos opcionales. |
| **KEYWORD** | `(alignas\|class\|int\|return\|...)` | Lista estricta de palabras reservadas de C y C++. |
| **IDENTIFIER** | `{LETTER}({LETTER}\|{DIGIT})*` | Nombres de variables y funciones que inician con letra o guion bajo. |
| **OPERATOR** | `("::"\|"->"\|"<<="\|"++"\|...\|"+")` | Uso de cadenas literales ordenadas de mayor a menor longitud (**Maximal Munch**). |
| **SEPARATOR** | `("("|")"\|"{"\|"}"\|"["\|"]"\|";"\|",")` | Delimitadores de sintaxis y puntuación. |
| **WHITESPACE** | `[ \t\r]+` y `\n` | Espacios y saltos de línea (utilizados para el rastreo dinámico de columnas y líneas). |

---

## Arquitectura del Lexer (Enfoque SRP y Callbacks)



```mermaid
classDiagram
    class MainOrchestrator {
        <<Control>>
        +FILE* in_file
        +FILE* out_file
        +main() int
    }

    class FlexScanner {
        <<Motor Léxico>>
        +int line_num
        +int col_num
        +dispatch_token(type, lexeme) void
        +yylex() int
    }

    class TokenProcessor {
        <<Interface / Callback>>
        +(*ProcessToken)(TokenType, lexeme, line, col, context)
    }

    class FileTokenProcessor {
        <<Implementación Concreta>>
        +file_token_processor(type, lexeme, line, col, context) void
    }

    class TokenType {
        <<Enumeración>>
        KEYWORD
        IDENTIFIER
        OPERATOR
        FLOATLIT
        HEXLIT
        ERROR
        ...
    }

    %% Relaciones
    MainOrchestrator --> FlexScanner : "configura yyin y ejecuta yylex()"
    MainOrchestrator --> FileTokenProcessor : "inyecta dependencia"
    FlexScanner --> TokenProcessor : "notifica hallazgos"
    TokenProcessor <|.. FileTokenProcessor : "implementa"
    FlexScanner --> TokenType : "clasifica"