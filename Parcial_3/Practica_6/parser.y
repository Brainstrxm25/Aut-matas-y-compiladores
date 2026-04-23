%{
	#include<stdio.h>
	#include <stdlib.h>
	int yylex();
	void yyerror( const char *s);

%}
%union {
	float num;
	char *id;
	}

// tokens
%token <num> ENTERO DECIMAL
%token <id> ID
%token SUMA RESTA MULTI DIVI
%token P_I P_D EOL DIF
%token MAQ MEQ MAI MEI EQ
%type <num> expresion termino factor 

%% 
call:
	call input
	|
	;
input:
	expresion EOL {printf("Resultado: %.2f\n",$1);}
	|EOL
	;
expresion:
	 expresion SUMA termino {$$= $1+$3;}
	|expresion RESTA termino {$$= $1-$3;}
	|termino		 {$$=$1;}
	;
termino:
	termino MULTI factor {$$=$1*$3;}
	|termino DIVI factor { if($3==0){printf("ERROR: division por 0\n");$$=0;}else{ $$=$1/$3;}}
	|factor		     {$$=$1;}
	;
factor:
	P_I expresion P_D {$$=$2;}
	|ENTERO		{$$=$1;}
	|DECIMAL	{$$=$1;}
	;

%%
void yyerror(const char*s){
	printf("Error sintantico: %s\n",s);
}

int main(){
	printf("Ingrese una expresión mateatica:\n");
	yyparse();
	return 0;
}