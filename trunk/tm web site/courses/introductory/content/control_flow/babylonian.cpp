/*#HA*/ /*******  while loop demonstration ********
    A little program to demonstrate the
	babylonian algorithm

*******************************************/

#include <cmath>
#include <iostream>
using namespace std;    // cout is in the std namespace

double babylonian(double num, double threshold, double estimate);

int main(){

	double root;
	root = babylonian(14, .00001, 3);
	cout << "The babylonian algorithm estimates the root of 14 to be " << root ;
	cout << endl;

	return 0;
}
/*#DA*/
/** babylonian **************************************************
*
* @param: num @pre: num must be non-negative
*         threshold - relative magnitude of last change
*         estimate - a guess @pre must be positive
*
* @returns: the final estimate of the square root of num
****************************************************************/
double babylonian(double num, double threshold, double estimate){
	double deltaG = 1;
	double lastGuess = 0;
	double result;
	
	while(deltaG > threshold){
		result = num / estimate;
		lastGuess = estimate;  // Save old estimate
		estimate = (lastGuess + result)/2;  // new estimate
		deltaG = fabs(lastGuess - estimate)/estimate;
	}
	return estimate;
}
