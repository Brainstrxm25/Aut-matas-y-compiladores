#include <iostream>
#include <string>
#include <sstream>
#include <regex>

using namespace std;

int main() {

    string entrada;
    cout << "Ingrese una cadena: ";
    getline(cin, entrada);

    // Expresiones regulares
    regex numero("^[0-9]+$");
    regex minusculas("^[a-z]+$");
    regex mayusculas("^[A-Z]+$");
    regex identificador("^[a-zA-Z_][a-zA-Z0-9_]*$");
    regex simbolo("^(==|!=|<=|>=|&&|\\|\\||[+\\-*/=<>!;,(){}])$");
    regex reservada("^(int|float|double|char|if|else|for|while|return|void|main)$");

    int contNumero = 0,contMinus = 0,contMayus = 0,contIdent = 0,contSimbolo = 0,contReservada = 0;

    string token;
    stringstream cadena(entrada);

    while (cadena >> token) {

        if (regex_match(token, numero)) {
            cout << token << " -> Numero entero" << endl;
            contNumero++;
        }
        else if (regex_match(token, reservada)) {
            cout << token << " -> Palabra reservada" << endl;
            contReservada++;
        }
        else if (regex_match(token, mayusculas)) {
            cout << token << " -> Palabra en mayusculas" << endl;
            contMayus++;
        }
        else if (regex_match(token, identificador)) {
            cout << token << " -> Identificador" << endl;
            contIdent++;
        }
        else if (regex_match(token, simbolo)) {
            cout << token << " -> Simbolo" << endl;
            contSimbolo++;
        }
        else if (regex_match(token, minusculas)) {
            cout << token << " -> Palabra en minusculas" << endl;
            contMinus++;
        }
        else {
            cout << token << " -> No clasificado" << endl;
        }
    }

    cout << "palabras reservadas: " <<contReservada <<endl;
    cout << "numeros enteros: " << contNumero <<endl;
    cout << "palabras en minusculas: " << contMinus <<endl;
    cout << "palabras en mayusculas: " << contMayus <<endl;
    cout << "identificadores: " << contIdent <<endl;
    cout << "simbolos: " << contSimbolo <<endl;

    return 0;
}

