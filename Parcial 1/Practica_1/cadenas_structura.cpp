#include <iostream>
#include <sstream>
#include <string>
#include <cctype>

using namespace std;

bool esNumero(const string& s) {
    for (int i = 0; i < s.length(); i++) {
        if (!isdigit(s[i])) return false;
    }
    return true;
}

bool esMinuscula(const string& s) {
    for (int i = 0; i < s.length(); i++) {
        if (!islower(s[i])) return false;
    }
    return true;
}

bool esMayuscula(const string& s) {
    for (int i = 0; i < s.length(); i++) {
        if (!isupper(s[i])) return false;
    }
    return true;
}

bool esPalabraReservada(const string& s) {
    string reservadas[] = {
        "int", "float", "double", "char",
        "if", "else", "for", "while",
        "return", "void", "main"
    };

    int total = 11;

    for (int i = 0; i < total; i++) {
        if (s == reservadas[i])
            return true;
    }
    return false;
}

bool esOperador(const string& s) {
    string operadores[] = {
        "+", "-", "*", "/", "%",
        "=", "==", "!=", "<", ">", "<=", ">=",
        "&&", "||", "!"
    };

    int total = 14;

    for (int i = 0; i < total; i++) {
        if (s == operadores[i])
            return true;
    }
    return false;
}


bool esIdentificador(const string& s) {
    
    if (esPalabraReservada(s))
        return false;

    // Primer car�cter: letra o _
    if (!isalpha(s[0]) && s[0] != '_')
        return false;

    // Resto de caracteres
    for (int i = 0; i < s.length(); i++) {
        if (!isalnum(s[i]) && s[i] != '_')
            return false;
    }

    return true;
}



int main() {
    string entrada,victoria= "Si funciono!!";
    cout << "Ingrese una cadena: ";
    getline(cin, entrada);

    stringstream ss(entrada);
    string token;

    while (ss >> token) {
        if (esNumero(token)) {
            cout << token << " es un numero entero" << endl;
        }
        else if (esMayuscula(token)) {
            cout << token << " es una palabra en mayusculas" << endl;
        }
        else if (esPalabraReservada(token)){
        	cout << token << " es una palabra reservada" << endl;
		}
        else if (esIdentificador(token)) {
            cout << token << " es un identificador" << endl;
        }
        else if (esMinuscula(token)) {
            cout << token << " es una palabra en minusculas" << endl;
    }
    	else if(esOperador(token) ){
    		cout << token << " es un operador " << endl;
		}
        else {
            cout << token << " no reconocido" << endl;
        }
    }

    cout << victoria;
}

