
%{
	#include<stdio.h>
	#include<stdlib.h>
	#include "parser.tab.h"

%}
DIGITO [0-9]
ID [a-zA-Z][a-zA-Z0-9]*

%%
{DIGITO}+ {yylval.num = atoi(yytext); return ENTERO;}
{DIGITO}+"."{DIGITO}+ {yylval.num=atof(yytext); return DECIMAL;}
"+" {return SUMA;}
"-" {return RESTA;}
"*" {return MULTI;}
"/" {return DIVI;}
">=" {return MAI;}
"<=" {return MEI;}
">" {return MAQ;}
"<" {return MEQ;}
"==" {return EQ;}
"!=" {return DIF;}
\n {return EOL;}
"(" {return P_I;}
")" {return P_D;}
{ID} {return ID;}
[ \t\r] {}

. {printf("error léxico: %s\n", yytext);}
%%
int yywrap(void){return 1;}