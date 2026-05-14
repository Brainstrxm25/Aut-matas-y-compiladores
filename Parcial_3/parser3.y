%{
#include <stdio.h>
#include <stdlib.h>

extern int yylex();
extern int line_num;
extern char* yytext;
extern FILE* yyin; /* Puntero al archivo de entrada para Flex */
void yyerror(const char *s);
%}

/* Declaración de tokens */
%token SELECT INSERT INTO VALUES UPDATE SET DELETE FROM WHERE
%token ASTERISCO EQUALS COMA P_I P_D PUNTO_COMA
%token IDENTIFICADOR NUMBER STRING

%%

/* Regla principal */
program:
    statements
    ;

statements:
    statements statement
    | statement
    ;


statement:
    select_stmt PUNTO_COMA { printf("Línea %d: [CORRECTA] Sentencia SELECT valida.\n", line_num); }
    | insert_stmt PUNTO_COMA { printf("Línea %d: [CORRECTA] Sentencia INSERT valida.\n", line_num); }
    | update_stmt PUNTO_COMA { printf("Línea %d: [CORRECTA] Sentencia UPDATE valida.\n", line_num); }
    | delete_stmt PUNTO_COMA { printf("Línea %d: [CORRECTA] Sentencia DELETE valida.\n", line_num); }
    | error PUNTO_COMA { 
        printf("Línea %d: [ERROR SINTÁCTICO] Falla estructural cerca de '%s'.\n", line_num, yytext); 
        yyerrok; /* Limpia el estado de error de Bison para continuar analizando */ 
    }
    ;

/* ---------------- Reglas para SELECT ---------------- */

select_stmt: SELECT select_list FROM IDENTIFICADOR where_clause;
select_list: ASTERISCO | column_list;
column_list: IDENTIFICADOR | column_list COMA IDENTIFICADOR;

/* ---------------- Reglas para INSERT ---------------- */

insert_stmt: INSERT INTO IDENTIFICADOR P_I column_list P_D VALUES P_I value_list P_D 
           | INSERT INTO IDENTIFICADOR VALUES P_I value_list P_D;
value_list: value | value_list COMA value;
value: NUMBER | STRING | IDENTIFICADOR;

/* ---------------- Reglas para UPDATE ---------------- */

update_stmt: UPDATE IDENTIFICADOR SET assignment_list where_clause;
assignment_list: assignment | assignment_list COMA assignment;
assignment: IDENTIFICADOR EQUALS value;

/* ---------------- Reglas para DELETE ---------------- */

delete_stmt: DELETE FROM IDENTIFICADOR where_clause;

/* ---------------- Cláusula WHERE ---------------- */

where_clause: 
    /* vacío */ 
    | WHERE IDENTIFICADOR EQUALS value;

%%

void yyerror(const char *s) {
    
}

int main(int argc, char **argv) {
    if (argc < 2) {
        printf("Uso incorrecto.\nEjecuta: %s tu_archivo.txt\n", argv[0]);
        return 1;
    }

    /* Abrir el archivo en modo lectura */
    FILE *file = fopen(argv[1], "r");
    if (!file) {
        printf("Error: No se pudo abrir el archivo %s\n", argv[1]);
        return 1;
    }

    /* Redirigir la entrada de Flex (yyin) al archivo en lugar de la consola */
    yyin = file;
    
    printf("=== Iniciando Análisis del Archivo: %s ===\n", argv[1]);
    yyparse();
    
    fclose(file);
    printf("=== Análisis Finalizado ===\n");
    return 0;
}