#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <regex>
using namespace std;

const regex regexCURP("^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[0-9]{2}$");
const regex regexRFC("^([A-ZÑ&]{3,4})([0-9]{2})([0-1][0-9])([0-3][0-9])[A-Z0-9]{3}$");
const regex regexINE("^[0-9]{13}$");

bool validarCURP(const string& curp) { return regex_match(curp, regexCURP); }
bool validarRFC(const string& rfc) { return regex_match(rfc, regexRFC); }
bool validarINE(const string& ine) { return regex_match(ine, regexINE); }

int main() {
    string nombreArchivo= "campos.txt";
    

    ifstream archivoEntrada(nombreArchivo);
    if (!archivoEntrada.is_open()) {
        cerr << "No se pudo abrir el archivo de entrada." << endl;
        return 1;
    }

    ofstream archivoSalida("resultado.txt");
    if (!archivoSalida.is_open()) {
        cerr << "No se pudo crear el archivo de salida." << endl;
        return 1;
    }

    string linea;
    while (getline(archivoEntrada, linea)) {
        if (linea.empty()) continue;

        string cabecera = linea;

        if (cabecera == "###CURP" || cabecera == "###RFC" || cabecera == "###INE") {

            // Leer número de campos
            if (!getline(archivoEntrada, linea)) {
                cerr << "Faltó indicar el número de campos después de " << cabecera << endl;
                break;
            }

            int cantidad = stoi(linea);
            vector<string> campos;

            // Leer campos
            for (int i = 0; i < cantidad; i++) {
                if (getline(archivoEntrada, linea)) {
                    campos.push_back(linea);
                } else {
                    cerr << "No hay suficientes campos para " << cabecera << endl;
                    break;
                }
            }

            //  escribir en archivo de salida
            for (const string& campo : campos) {
                bool valido = false;

                if (cabecera == "###CURP") {
                    valido = validarCURP(campo);
                    archivoSalida << campo << " - " << (valido ? "CURP válido" : "CURP no válido") << endl;
                } else if (cabecera == "###RFC") {
                    valido = validarRFC(campo);
                    archivoSalida << campo << " - " << (valido ? "RFC válido" : "RFC no válido") << endl;
                } else if (cabecera == "###INE") {
                    valido = validarINE(campo);
                    archivoSalida << campo << " - " << (valido ? "INE válido" : "INE no válido") << endl;
                }
            }
        }
    }

    archivoEntrada.close();
    archivoSalida.close();

    cout << "Archivo de salida 'resultado.txt' creado correctamente." << endl;
    return 0;
}

