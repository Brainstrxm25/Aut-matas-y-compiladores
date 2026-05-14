%{
#include "parser3.tab.h"
#include <stdio.h>
int line_num = 1; /* Variable para contar los renglones */
%}

%option noyywrap case-insensitive

%%

"SELECT"    { return SELECT; }
"INSERT"    { return INSERT; }
"INTO"      { return INTO; }
"VALUES"    { return VALUES; }
"UPDATE"    { return UPDATE; }
"SET"       { return SET; }
"DELETE"    { return DELETE; }
"FROM"      { return FROM; }
"WHERE"     { return WHERE; }

"*"         { return ASTERISCO; }
"="         { return EQUALS; }
","         { return COMA; }
"("         { return P_I; }
")"         { return P_D; }
";"         { return PUNTO_COMA; }

[a-zA-Z_][a-zA-Z0-9_]*  { return IDENTIFICADOR; }
[0-9]+                  { return NUMBER; }
'[^']*'                 { return STRING; }

[ \t\r]+    { /* Ignorar espacios, tabulaciones y retornos */ }
\n          { line_num++; }
.           { printf("Línea %d: [ERROR LÉXICO] Carácter '%s' no reconocido.\n", line_num, yytext); }

%%