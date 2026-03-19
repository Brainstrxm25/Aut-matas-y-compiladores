# JavaLexer (Proyecto)

> **Materia:** Autómatas y Compiladores  
> **Proyecto:** Analizador Léxico Robusto con Manejo de Errores y Reportes Dinámicos  
> **Alumno:** Navarrete Torres Oscar

---

##  Definición de Tokens y Tabla de Especificación 

| Categoría | Ejemplos de lexemas detectados |
| :--- | :--- |
| **KEYWORD** | `public`, `class`, `static`, `void`, `if`, `return`, `import`, `package` |
| **IDENTIFIER** | `main`, `args`, `System`, `x`, `miVariable`, `JavaLexer` |
| **INTEGERLIT** | `42`, `0`, `1_000_000L`, `7` |
| **FLOATLIT** | `3.14`, `1.5f`, `2.5e-3d`, `0.5` |
| **HEXADECIMALLIT** | `0xFF`, `0x1A_2B`, `0x7E` |
| **BINARYLIT** | `0b1010`, `0B11110000` |
| **OCTALLIT** | `0755`, `0644` |
| **STRINGLIT** | `"Hola Mundo"`, `"Resultado: %d\n"`, `""` |
| **CHARLIT** | `'a'`, `'\n'`, `'\\'`, `'\''` |
| **OPERATOR** | `==`, `!=`, `&&`, `||`, `++`, `--`, `+=`, `=`, `+`, `-`, `.` |
| **SEPARATOR** | `(`, `)`, `{`, `}`, `[`, `]`, `;`, `,` |
| **ANNOTATION** | `@Override`, `@Test`, `@SuppressWarnings` |
| **LINECOMMENT** | `// esto es un comentario de una línea` |
| **BLOCKCOMMENT** | `/* bloque de comentario multilínea */` |
| **JAVADOC** | `/** comentario de documentación oficial */` |

---


## Tabla de Patrones 

| Token / Grupo | Expresión Regular (Regex) | Descripción Técnica |
| :--- | :--- | :--- |
| **JAVADOC** | `/\*\*[\s\S]*?\*/` | Captura comentarios de documentación. Se evalúa antes que BlockComment |
| **BLOCKCOMMENT** | `/\*[\s\S]*?\*/` | Captura bloques multilínea usando cuantificadores no codiciosos (`*?`) |
| **LINECOMMENT** | `//[^\n]*` | Consume todo desde `//` hasta el final de la línea |
| **ANNOTATION** | `@[a-zA-Z_$][a-zA-Z0-9_$]*` | Identifica metadatos del lenguaje |
| **STRINGLIT** | `"([^"\\\\]|\\.)*"` | Permite secuencias de escape (como `\"`) sin romper la cadena |
| **CHARLIT** | `'([^'\\]|\\.)'` | Captura caracteres simples o escapados entre comillas simples |
| **FLOATLIT** | `[0-9][0-9_]*\.[0-9][0-9_]*([eE][+-]?[0-9]+)?[fFdD]?` | Soporta puntos decimales, exponentes y sufijos de precisión |
| **HEXADECIMALLIT**| `0[xX][0-9a-fA-F][0-9a-fA-F_]*[lL]?` | Captura números base 16 antes que los enteros decimales |
| **BINARYLIT** | `0[bB][01][01_]*[lL]?` | Captura números en base 2 (binarios) |
| **OCTALLIT** | `0[0-7]+[lL]?` | Captura números en base 8 que inician con el prefijo `0` |
| **INTEGERLIT** | `0\|[1-9][0-9_]*[lL]?` | Enteros decimales estándar (base 10) |
| **KEYWORD** | `\b(abstract\|class\|...)\b` | Palabras reservadas con límites de palabra (`\b`) |
| **IDENTIFIER** | `[a-zA-Z_$][a-zA-Z0-9_$]*` | Nombres de variables, métodos y clases |
| **OPERATOR** | `==\|!=\|<=|>=|&&|...\|[+\-*/%<>=!&\|^~?:.]` | Operadores los más largos van primero (**Maximal Munch**) |
| **SEPARATOR** | `[(){}\[\];,]` | Delimitadores de sintaxis y puntuación |
| **WHITESPACE** | `[ \t\r\n]+` | Espacios y saltos (utilizados para el rastreo de línea/columna)|

---
```mermaid
classDiagram
    class JavaLexer {
        -List~LexicalError~ errorList
        -String KW_PATTERN
        -Pattern MASTER
        +tokenize(String source) List~Token~
        +determineType(Matcher m) TokenType
        +main(String[] args) void
    }

    class Token {
        <<record>>
        +TokenType type
        +String lexeme
        +int line
        +int column
        +toString() String
    }

    class LexicalError {
        -int line
        -int column
        -String illegal
        +toString() String
    }

    class TokenType {
        <<enumeration>>
        KEYWORD
        IDENTIFIER
        OPERATOR
        SEPARATOR
        FLOATLIT
        HEXADECIMALLIT
        BINARYLIT
        OCTALLIT
        STRINGLIT
        CHARLIT
        INTEGERLIT
        JAVADOC
        BLOCKCOMMENT
        LINECOMMENT
        ANNOTATION
        UNKNOWN
    }

    class JavaLexerTest {
        <<Test Class>>
        +testKeywords()
        +testIdentifiers()
        +testNumbers()
        +testStringsAndChars()
        +testComments()
        +testErrorHandling()
        +testCoordinates()
    }

    %% Relaciones
    JavaLexer ..> Token : "crea"
    JavaLexer ..> LexicalError : "registra"
    JavaLexer --> TokenType : "clasifica con"
    JavaLexerTest ..> JavaLexer : "ejecuta pruebas sobre"
    Token --> TokenType : "tiene un"
   ```
   

## Pruebas unitarias

Las pruebas unitarias son conocidas para verificar que partes especificas del código se comporten de la manera esperada en este caso el método `Tokenize()`

En el caso de este programa se dividieron las pruebas en cuatro etapas 

En la primera evaluamos que el analizador no se confunda con el 0 , es decir, en java hay muchos literales que comienzan con el carácter 0 por ejemplo: `0x1A` y podría confundir el 0 con un `INTEGERLIT` si eso pasa:

2.  Captura el `0`
    
3.  Se queda con `x1A` colgando
    
4.  Como `x1A` no es un número válido, nos lanzaría un **Error**

| Paso | Entrada | Proceso | Resultado esperado|
|--|--|--|--|
|  1|0x1A|el analizador léxico compara todos los grupos | Token `HEXADECIMALLIT`
|2|0b1010|el analizador léxico encuentra el prefijo 0b | Token `BBINARYLIT`
|3|3.14|el analizador encuentra el `.` después del entero | Token `FLOATLIT`
---
Si llega a fallar la salida se vería así:

| TIPO | LEXEMA | LINEA | COLUMNA |
| :--- | :--- | :--- | :--- |
| **INTEGERLIT** | `0` | 1 | 1 |
| **ERROR** | `x` | 1 | 2 |
| **IDENTIFIER** | `1A` | 1 | 3 |

En la segunda etapa se verifica que al enumerar la posición de la linea de código y columna  no pierda la cuenta al encontrar múltiples saltos de línea (`\n`) o espacios en blanco
 
| Paso | Entrada | Proceso | Resultado esperado | 
| :--- | :--- | :--- | :--- | 
| 1 | `int x;` | Escaneo de la primera línea de código. | `int` (L:1, C:1) | 
| 2 | `\n` | Detección de salto de línea en el flujo | `currentLine++` | 
| 3 | ` float y;` | Cálculo de columna restando el último `\n`. | `float` (L:2, C:3) | 
--- 

En la tercera etapa probamos la resistencia del programa a caracteres que no pertenecen a java el resultado ideal seria que el programa registre el error y continúe con el siguiente token válido
 | Paso | Entrada | Proceso | Resultado esperado | 
 | -- | -- | -- | -- | 
 | 1 | `int x = 5; ¿` | Encuentra un carácter ilegal al final de la sentencia | Tokens válidos + 1 Error |
 | 2 | `! @ #` | Encuentra una cadena de símbolos inválidos seguidos | Lista de 3 Errores | 
 | 3 | `¿int?` | Error pegado a un token (prefijo y sufijo) | Error `¿`, Token `int`, Error `?` |
 ---
Si llegase a fallar la salida se vería de la siguiente manera: 
 
 | TIPO | LEXEMA | LINEA | COLUMNA | 
 | -- | -- | -- | -- |
  | **KEYWORD** | `int` | 1 | 1 |
  | **IDENTIFIER** | `x` | 1 | 5 | 
  | **UNKDOWN** | *(¿)* | *(-)* | *(-)* | 
  ---
 En la cuarta etapa se verifica  que el analizador siempre tome el "bocado más grande" de caracteres y no confunda palabras reservadas con identificadores comunes

| Paso | Entrada | Proceso | Resultado esperado |
 | -- | -- | -- | -- | 
 | 1 | `while` | Coincidencia exacta con la lista de palabras clave | Token `KEYWORD` |
 | 2 | `whiles` | No hay coincidencia exacta (límite de palabra `\b`) | Token `IDENTIFIER` | 
 | 3 | `==` | El motor prioriza el operador de 2 caracteres sobre el de 1 | Token `OPERATOR` (==) |
  ---
  
  | Tipo | Lexema | Linea | Columna | 
  | -- | -- | -- | -- |
  | **OPERATOR** | `=` | 1 | 1 |
  | **OPERATOR** | `=` | 1 | 2 | 

## Cuestionario

 1. ¿Qué pasa si eliminas los `\b` del patrón KEYWORD?
Ocurriría un **error de segmentación incorrecta** el "word boundary" (`\b`) garantiza que la coincidencia sea una palabra completa sin él, el lexer encontraría la subcadena `int` dentro de la palabra `inteligencia`, clasificando erróneamente el inicio como una **KEYWORD** y dejando el resto (`eligencia`) como un identificador huérfano o un error léxico

2. ¿Por qué el patrón KEYWORD debe evaluarse antes que IDENTIFIER?
Porque las palabras reservadas (keywords) cumplen con todas las reglas sintácticas de un identificador (comienzan con letra/símbolo y siguen con caracteres alfanuméricos) si el **IDENTIFIER** tuviera prioridad, "absorbería" palabras como `if`, `while` o `class`, impidiendo que el compilador reconozca la estructura lógica del lenguaje

 3. ¿Podrías construir una sola ER que reconozca ambos tokens a la vez? Sí, utilizando **Grupos Nombrados** y el operador de alternancia (`|`) dentro de un *MASTER PATTERN*  aunque la ER sea una sola cadena técnica, la prioridad se establece por el orden de los grupos dentro de dicha expresión

4. ¿Cómo manejarías el operador ternario `?:` en tu analizador?
El lexer debe tratar el `?` y el `:` como dos tokens independientes de tipo **OPERATOR**. Aunque funcionan juntos lógicamente, están separados por expresiones en el código fuente. La validación de que ambos existan y estén en la estructura correcta es responsabilidad del **Analizador Sintáctico (Parser)**.

5. El operador `>>>` debe reconocerse antes que `>>`. ¿Por qué?
Por el principio de **Maximal Munch** (Bocado Máximo). El lexer siempre debe intentar capturar la secuencia de caracteres más larga posible que coincida con un patrón. Si buscara `>>` primero, al encontrar `>>>` se detendría en el segundo carácter, dejando el tercer `>` como un token individual, lo cual rompería la lógica del operador de desplazamiento sin signo

6. ¿Qué diferencia hay entre un separador y un operador desde la perspectiva de la gramática?

| Tipo | Función Principal | Características |
| -- | -- | -- |
| **Operador** | Indica una computación o acción sobre datos | Posee reglas de precedencia y asociatividad (aritmética, lógica, asignación) |
| **Separador** | Define la estructura y límites del código | No realiza cálculos; define bloques, listas de parámetros y fin de sentencias |

---

7. ¿Por qué `08` NO es un octal válido en Java? ¿Cómo ajustarías la ER? En el sistema octal (base 8), los dígitos válidos son únicamente del **0 al 7**. El dígito `8` es ilegal la expresión regular `0[0-7]+` maneja esto correctamente; si aparece un 8, el patrón no coincidirá y el carácter será procesado por el sistema de errores

8. ¿Cómo extenderías el patrón STRING para soportar Text Blocks (`"""..."""`)? Se añadiría una nueva alternativa al inicio del patrón maestro: `(?<TEXTBLOCK>"""[\s\S]*?""")` es fundamental usar el cuantificador **no-codicioso** (`*?`) para asegurar que el lexer se detenga en el primer cierre de triples comillas

9. ¿Tiene sentido que el analizador léxico verifique si un número está en rango?
No el lexer solo valida la sintaxis léxica. Verificar si un valor excede el límite de un tipo de dato (**overflow**), como un `int` de 32 bits, es una tarea del **Analizador Semántico**

10. ¿Cómo manejaría tu lexer un comentario de bloque no cerrado al final del archivo? Dado que la ER busca el cierre `*/`, si este no existe antes del fin del archivo, el patrón fallará el texto desde `/*` hasta el final será procesado por el mecanismo de **Panic Mode Recovery**, reportando cada carácter como un error léxico individual 

11. ¿Deberían los JavaDoc producir un token diferente a los block comments? Para un compilador que genera bytecode, no, pero para herramientas de **extracción de metadatos** (como `javadoc`), es esencial diferenciarlos mediante el prefijo `/**` para procesar la documentación técnica

12. ¿Cuál es la diferencia entre recuperación de errores en el lexer vs en el parser?

* **Lexer (Panic Mode):** Salta el carácter ilegal y busca el siguiente token válido
* **Parser (Sincronización):** Intenta "sincronizarse" saltando hasta encontrar un punto de anclaje (como un `;` o `}`) para validar el resto de la estructura gramatical