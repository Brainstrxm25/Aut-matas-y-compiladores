#include <iostream>
#include <vector>
#include <set>
using namespace std;

int main(){

int N,S,D,q0,T,C;
cin>>N>>S>>D>>q0>>T>>C;

vector<char> alfabeto(S);
for(int i=0;i<S;i++)
cin>>alfabeto[i];

vector<vector<int>> transicion(N,vector<int>(S));

set<int> finales;

for(int i=0;i<T;i++){
int x;
cin>>x;
finales.insert(x);
}

for(int i=0;i<D;i++){
int I,J;
char X;
cin>>I>>X>>J;

int col=X-'0';
transicion[I][col]=J;
}

vector<string> cadenas(C);

for(int i=0;i<C;i++)
cin>>cadenas[i];

for(string cadena:cadenas){

int estado=q0;

for(char simbolo:cadena){
int col=simbolo-'0';
estado=transicion[estado][col];
}

if(finales.count(estado))
cout<<cadena<<" ACEPTADA"<<endl;
else
cout<<cadena<<" RECHAZADA"<<endl;

}
}