#include <iostream>
using namespace std;

/***** complex boolean expressions ********

  In this example we examine complex bool
  expressions. In particular we look at
  the SHORT-CIRCUIT property.

**********************************************/



int main(){
	bool flag;
	int number;

	cout << "Please input an integer: ";
	cin >> number;

	//	flag = a + b.c

	flag = number == 4 || number >= 7 && number <= 10;

	cout << "The flag is " << flag <<'\n';
	return 0;
}
