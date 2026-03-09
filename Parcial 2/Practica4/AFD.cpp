#include <iostream>
#include <map>
#include <vector>
#include <set>
using namespace std;

int main() {

    int N, S, D, q0, T, C;
    vector<string> cadenas(C);
    vector<char> alfabeto(S);
    map<pair<int,char>, int> transicion;
    set<int> finales;

    cin >> N >> S >> D >> q0 >> T >> C;
    
    for(int i = 0; i < S; i++)
        cin >> alfabeto[i];

    for(int i = 0; i < T; i++){
        int x;
        cin >> x;
        finales.insert(x);
    }

    for(int i = 0; i < D; i++){
        int I, J;
        char X;

        cin >> I >> X >> J;

        transicion[{I,X}] = J;
    }


    for(int i = 0; i < C; i++){
        cin >> cadenas[i];
    }

    for(string cadena : cadenas){

        int estado = q0;

        for(char simbolo : cadena){
            estado = transicion[{estado,simbolo}];
        }

        if(finales.count(estado))
            cout << cadena << " ACEPTADA" << endl;
        else
            cout << cadena << " RECHAZADA" << endl;
    }

}