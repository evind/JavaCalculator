# JavaCalculator

## Introduction
This was a project assigned to me as part of my Object-Orientated Programming class in 2nd Year Software Development. The aim of the project was to develop a java calculator application to perform basic functions and have an appropriate Graphical User Interface.

The calculator makes use of an implmentation of Djikstra's Shunting-Yard algorithm to convert infix notation to Reverse Polish Notation.

## Features
The following is a list of implemented and function features.
* Decimal number input	
* Memory Clear (MC), Memory Recall (MR), Memory Store (MS), Memory Plus(M+), Memory Minus(M-)
* Clear Screen (C), Backspace (<-), Open Parenthesis and Close Parenthesis
* Multiplication (*), Division (/), Modulo (%), Addition (+), Subtraction (-)
* Square (^2), Power(x^y), Negate number ((-))
* Equals

The following limitations are in place:
* Operators cannot be entered in series, eg:  “4+2-*/3” is not possible to input
* Only 1 decimal point can be entered per number input, eg: “0.123.34” is not possible to input
* Closing parenthesis are prevented from being input if there are not matching opening parenthesis, eg: “(((0))))))” is not possible to input
