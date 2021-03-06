/*#H*/#include <iostream>
#include <stdlib.h>
#include <ScriptManager>
using namespace std;

// ArrayBar display colours
const int BLACK = 0;
const int BLUE = 1;
const int GREEN = 2;
const int RED = 4;
const int CYAN = 3;
const int MAGENTA = 5;
const int YELLOW = 6;
const int WHITE = 7;

// Teaching Machine control routines  
void setColour(int i, int colour); // set i'th element to colour
void setColour(int from, int to, int colour); // set all elements between i and j to colour



void improvedBubbleSort(int anArray[], int n);
bool bubbleOnce(int array[], int n, int top);
void swap(int& arg1, int& arg2);
const int SIZE = 10;

// Values in array will be between MIN_VALUE and MAX_VALUE
const int MIN_VALUE = 10;
const int MAX_VALUE = 50;

int main(){
    int array[SIZE];
     
// For technical reasons this is actually quite poor
    for (int i = 0; i < SIZE; i++)
        array[i]= MIN_VALUE + rand()%(MAX_VALUE - MIN_VALUE);

    improvedBubbleSort(array, SIZE);
    
    for (int i = 0; i < SIZE; i++)
        cout << array[i] << "  ";
    cout << endl;
    return 0;
}/*#DA*/
/** improvedBubbleSort **************************************
* @params: anArray - array of positive integers
*          size       - the size of the array @pre: non-negative
*
* @modifies: values in anArray are sorted in ascending order
************************************************************/

void improvedBubbleSort(int anArray[], int size){
     bool notDone = true;
     for(int i = 0; i < size-1 && notDone; i++){
         notDone = bubbleOnce(anArray, size, i);
/*#HA*/         setColour(i, YELLOW);     
/*#DA*/     }
/*#HA*/     setColour(size-1, YELLOW);     
/*#DA*/}

/** bubbleOnce **********************************************
* @params: array - array of positive integers
*          n       - the size of the array @pre: non-negative
*          top    - the top of the sub-array to bubble
*
* @modifies: element in array from top to n-1 (sub-array)
*            pairs in sub-array will be swapped if uppermost
*            larger. Smallest value will bubble to top
* @returns: true if any swaps were carried out 
************************************************************/
bool bubbleOnce(int array[], int n, int top){
     bool swapsMade = false;
     for(int i = n-1; i > top; i--){
/*#HA*/         int k = i - 1;
         setColour(k, i, CYAN); 
/*#DA*/         if (array[i-1] > array[i]){
/*#HA*/                   setColour(k, i, RED); 
/*#DA*/             swap(array[i-1], array[i]);
             swapsMade = true;
         }
/*#HA*/                   setColour(k, i, GREEN); 
/*#DA*/     }
     return swapsMade;
}/*#HA*/

/** swap **************************************************
* @params: arg1 - reference to any integer
*          arg2 - reference to any integer
*
* @modifies: the values in the original variables referred 
*            to by arg1 and arg2 are swapped
***********************************************************/
void swap(int& arg1, int& arg2){
     int hold = arg1;
     arg1 = arg2;
     arg2 = hold;
}


/*** Teaching Machine control routines ****************************************/ 

// set i'th element to colour     
void setColour(int i, int colour){
     ScriptManager::relay("MUN.PlugIn.ArrayBar", "setColour", i, colour);
}

 // set all elements between i and j to colour      
void setColour(int from, int to, int colour){
     ScriptManager::relay("MUN.PlugIn.ArrayBar", "setColour", from, to, colour);
}
