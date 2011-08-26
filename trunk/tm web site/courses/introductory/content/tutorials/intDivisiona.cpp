/*#H*/ #include <iostream>
using namespace std;

// standard test functions
void constantClient();
void anxiousClient();
void independentClient();
void expressiveClient();
void confusedClient();

int foo(int a, int b);

/*#DA*/int main(){
    constantClient();
    anxiousClient();
    independentClient();
    expressiveClient();
    confusedClient();
}/*#HA#/
// These are all tests of our service funtion
/*#DB*/void constantClient(){
     foo(3, 4);
     foo(2, 11);
}
void anxiousClient(){
     int a = 4;
     int b = 13;
     foo(a,b);
}
void independentClient(){
     int fred = 7;
     int jarge = -2;
     foo(fred, jarge);
}
void expressiveClient(){
     int x = 6;
     int y = 23;
     foo(x*x/12, y-x);
}
void confusedClient(){
     int a = 4;
     int b = 13;
     foo(b, a);
}/*#HB#/
/*#DC*/ // This is the service function we are tasked to create
int foo(int a, int b){
    return a + b/4;
}/*#HC*/


    
