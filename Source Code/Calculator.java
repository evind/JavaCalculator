package com.oop.calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Evin Darling (C00144257)
 * Date: March 2017
 * Description: A calculator application using Graphical User Interface
 *              components. Features: User input via buttons. Addition,
 *              Subtraction, Multiplication, Division, Modulo, Square,
 *              Power, negate number, decimal numbers, parenthesis,
 *              memory functions.
 */
public class Calculator extends JFrame implements ActionListener {
    // Constants
    final static private int FRAME_WIDTH = 250;
    final static private int FRAME_HEIGHT = 350;
    final static private int SCREEN_FONT_SIZE = 16;

    // Global variables
    private JButton buttons[] = new JButton[30];    // Array to contain buttons
    private static String[][] buttonNames = new String[][]{ // Array of button text
            {  "MC","MR","MS","M+","M-"},
            {   "C","<-", "(", ")", "%"},
            {   "7", "8", "9", "/","^2"},
            {   "4", "5", "6", "*","^y"},
            {   "1", "2", "3", "-", "Z"},
            { "(-)", "0", ".", "+", "="}
    };
    private String textLine1 = "";  // The first line of text in the JTextArea
    private String textLine2 = "0"; // The second line of text in the JTextArea
    private JTextArea screen = new JTextArea(2, 10);    // Calculator screen
    private Queue infix = new Queue(50);    // Will contain infix expression
    private Queue postfix = new Queue(50);  // Will contain postfix expression
    private Stack operators = new Stack(50);// Will contain operators during infixToPostfix
    private Stack rpn = new Stack(50);      // Will be used to evaluate postfix via reverse polish notation
    private Stack parenthesis = new Stack(50);  // Used to check parenthesis are balanced
    private boolean decimalCheck = false;
    private String[] supportedOperators = {"^","*","/","%","+","-"}; // Operators supported in this calc
    private boolean lastOperatorPower = false;      // Check for if last input is power operator
    private boolean lastOperatorNegative = false;   // Check for if last input is negation
    private double memoryStore = 0.0;               // Stores user defined value via MS button

    // Constructor
    public Calculator()
    {
        // Create and set up the window
        JFrame frame = new JFrame("Calculate!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setResizable(false);

        // Set up the content pane
        addComponentsToPane(frame.getContentPane());

        // Display the window
        frame.pack();
        frame.setVisible(true);
    }

    // Methods
    /**
     * This method will set up the GUI of the calculator.
     * Sets layout manager to GridBagLayout. Adds screen to
     * a JScrollPane with conditional horizontal scrolling.
     * Adds the JScrollPane to content pane. Uses nested for loops
     * to create and add all button components.
     * @param pane - The content pane passed in by constructor
     */
    private void addComponentsToPane(Container pane)
    {
        JButton button;
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int btnIndex = 0;   // Keep track of number buttons created and stored
        JScrollPane scroll = new JScrollPane(screen, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        c.fill = GridBagConstraints.BOTH;   // Fill vertically & horizontally
        c.insets = new Insets(5, 5, 5, 5);  // External padding
        c.weightx = 0.5;                    // Horizontal weight
        c.weighty = 0.5;                    // Vertical weight
        c.gridx = 0;
        c.gridy = 0;                        // Location 0,0 in grid
        c.gridwidth = 5;                    // Take up 5 grid spaces horizontally
        c.gridheight = 1;
        screen.setFont(new Font("Verdana", Font.PLAIN, SCREEN_FONT_SIZE));
        screen.setText(textLine1 + textLine2);
        screen.setEditable(false);
        pane.add(scroll, c);    // Add JScrollPane scroll that contains JTextArea screen
        updateScreen();

        c.gridwidth = 1;                // Reset grid width to 1
        c.insets = new Insets(0,0,0,0); // Reset padding

        // Adds all buttons in a grid
        for (int i = 1; i <= 6; i++) {      // 6 rows
            for (int j = 0; j < 5; j++) {   // 5 columns
                // Leave a button space at this grid location for the
                // enlarged equals button
                if (i == 5 && j == 4) { /* Do nothing */ }
                // Create enlarged equals button
                else if (i == 6 && j == 4) {
                    button = new JButton(buttonNames[i-1][j]);
                    button.setMargin(new Insets(2,2,2,2));  // Reduce default size between margin and label
                    c.fill = GridBagConstraints.BOTH;       // Fill vertically & horizontally
                    c.weightx = 0.5;
                    c.weighty = 0.5;
                    c.gridx = 4;
                    c.gridy = 5;        // Specific location for enlarged equals button
                    c.gridheight = 2;
                    c.insets = new Insets(0, 5, 5, 5);  // Add external padding to bottom, left & right
                    button.addActionListener(this);     // Add the listener
                    buttons[btnIndex] = button;         // Add button to array
                    pane.add(buttons[btnIndex], c);     // Add button to pane
                    btnIndex++;                         // Increment button counter
                }
                // Create normal button
                else {
                    button = new JButton(buttonNames[i-1][j]);
                    button.setMargin(new Insets(2,2,2,2));
                    c.fill = GridBagConstraints.BOTH;
                    c.weightx = 0.5;
                    c.gridx = j;
                    c.gridy = i;        // Grid location determined by nested for loops
                    c.insets = new Insets(0, 5, 5, 0);  // Add external padding to bottom & left
                    if (j == 4) {       // If last button in row, also add padding to right
                        c.insets = new Insets(0, 5, 5, 5);
                    }
                    button.addActionListener(this);     // Add the listener
                    buttons[btnIndex] = button;         // Add button to array
                    pane.add(buttons[btnIndex], c);     // Add button to pane
                    btnIndex++;                         // Increment button counter
                }
            } // End for j
        } // End for i
    }

    /**
     * This method handles the specific functionality of each of the
     * GUI buttons on the calculator based one what button was pressed.
     * Checks if the source of the ActionEvent that fired is from one
     * of the buttons in the buttons array, then executes relevant
     * block of code to perform that function.
     *
     * @param ae - The ActionEvent
     */
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == buttons[5]) {           // CLEAR FUNCTION
            resetDecimalCheck();        // Allow decimals to be input again
            textLine1 = "";             // Reset first text line
            textLine2 = "0";            // Reset second text line
            infix.clearQueue();         // Reset infix queue
            postfix.clearQueue();       // Reset postfix queue
            operators.clearStack();     // Reset operators stack
            rpn.clearStack();           // Reset rpn stack
            parenthesis.clearStack();   // Reset parenthesis stack
        }
        else if (ae.getSource() == buttons[6]) {      // BACKSPACE FUNCTION
            int textLine2Len = textLine2.length();
            if (textLine2Len <= 1) {    // If 1 or fewer chars, reset textLine2
                textLine2 = "0";
            }
            else {                      // Delete last char in textLine2
                if (getLastChar().equals(".")) {
                    resetDecimalCheck();    // If deleting the ".", allow user to input another "."
                }
                textLine2 = textLine2.substring(0, textLine2Len - 1);
            }
            updateScreen();
        }
        else if (ae.getSource() == buttons[28]) {     // EQUALS FUNCTION
            // Prevent function from enqueuing the last 0 on textLine2
            // if the last part of the equation is a ")"
            if (!getLastInfixEnqueued().equals(")")) {
                infix.enqueue(textLine2);
            }
            lastOperatorNegative = false;
            lastOperatorPower = false;
            infixToPostfix();       // Convert the infix expression to postfix
            evaluateExpression();   // Evaluate the expression
        }
        else if (ae.getSource() == buttons[0]) {      // MEMORY CLEAR FUNCTION
            memoryClear();
        }
        else if (ae.getSource() == buttons[2]) {      // MEMORY STORE FUNCTION
            memoryStoreFunction();
        }
        else if (ae.getSource() == buttons[3]) {      // MEMORY + FUNCTION
            memoryPlus();
        }
        else if (ae.getSource() == buttons[4]) {      // MEMORY - FUNCTION
            memoryMinus();
        }
        else if (ae.getSource() == buttons[1]) {      // MEMORY RECALL FUNCTION
            memoryRecall();
        }
        else if (ae.getSource() == buttons[7]) {      // ( INPUT
            if (parenthesisBalanced("(")) { // If legal to input (
                lastOperatorPower = false;  // Toggle lastOperatorPower
                lastOperatorNegative = false;
                buildInfixAndModifyScreen("(");
            }
        }
        else if (ae.getSource() == buttons[8]) {      // ) INPUT
            if (parenthesisBalanced(")")) { // If legal to input )
                lastOperatorPower = false;  // Toggle lastOperatorPower
                lastOperatorNegative = false;
                buildInfixAndModifyScreen(")");
            }
        }
        else if (ae.getSource() == buttons[9]) {      // MOD FUNCTION
            if (preventOperatorsInSeries()) {   // If legal to input operator
                lastOperatorPower = false;
                lastOperatorNegative = false;
                buildInfixAndModifyScreen("%");
            }
        }
        else if (ae.getSource() == buttons[10]) {     // 7 INPUT
            if (textLine2.equals("0")) { textLine2 = "7"; }
            else { textLine2 += "7"; }
        }
        else if (ae.getSource() == buttons[11]) {     // 8 INPUT
            if (textLine2.equals("0")) { textLine2 = "8"; }
            else { textLine2 += "8"; }
        }
        else if (ae.getSource() == buttons[12]) {     // 9 INPUT
            if (textLine2.equals("0")) { textLine2 = "9"; }
            else { textLine2 += "9"; }
        }
        else if (ae.getSource() == buttons[13]) {     // DIVISION FUNCTION
            if (preventOperatorsInSeries()) {   // If legal to input operator
                lastOperatorPower = false;
                lastOperatorNegative = false;
                buildInfixAndModifyScreen("/");
            }
        }
        else if (ae.getSource() == buttons[14]) {     // SQUARE FUNCTION
            lastOperatorPower = true;           // Toggle lastOperatorPower TRUE
            lastOperatorNegative = false;
            buildInfixAndModifyScreen("^");
            textLine2 = "2";                    // Input 2 after ^
        }
        else if (ae.getSource() == buttons[15]) {     // 4 INPUT
            if (textLine2.equals("0")) { textLine2 = "4"; }
            else { textLine2 += "4"; }
        }
        else if (ae.getSource() == buttons[16]) {     // 5 INPUT
            if (textLine2.equals("0")) { textLine2 = "5"; }
            else { textLine2 += "5"; }
        }
        else if (ae.getSource() == buttons[17]) {     // 6 INPUT
            if (textLine2.equals("0")) { textLine2 = "6"; }
            else { textLine2 += "6"; }
        }
        else if (ae.getSource() == buttons[18]) {     // MULTIPLICATION FUNCTION
            if (preventOperatorsInSeries()) {
                lastOperatorPower = false;
                lastOperatorNegative = false;
                buildInfixAndModifyScreen("*");
            }
        }
        else if (ae.getSource() == buttons[19]) {     // POWER OF Y FUNCTION
            lastOperatorPower = true;
            lastOperatorNegative = false;
            buildInfixAndModifyScreen("^");
        }
        else if (ae.getSource() == buttons[20]) {     // 1 INPUT
            if (textLine2.equals("0")) { textLine2 = "1"; }
            else { textLine2 += "1"; }
        }
        else if (ae.getSource() == buttons[21]) {     // 2 INPUT
            if (textLine2.equals("0")) { textLine2 = "2"; }
            else { textLine2 += "2"; }
        }
        else if (ae.getSource() == buttons[22]) {     // 3 INPUT
            if (textLine2.equals("0")) { textLine2 = "3"; }
            else { textLine2 += "3"; }
        }
        else if (ae.getSource() == buttons[23]) {     // SUBTRACTION FUNCTION
            if (preventOperatorsInSeries()) {
                lastOperatorPower = false;
                lastOperatorNegative = false;
                buildInfixAndModifyScreen("-");
            }
        }
        else if (ae.getSource() == buttons[24]) {     // (-) INPUT
            if (!lastOperatorNegative) {
                if (!textLine2.equals("0")) {
                    textLine2 = "-" + textLine2;
                }
            }
            lastOperatorNegative = true;
        }
        else if (ae.getSource() == buttons[25]) {     // 0 INPUT
            if (!textLine2.equals("0")) {   // Prevent input of leading zeroes
                textLine2 += "0";
            }
        }
        else if (ae.getSource() == buttons[26]) {     // . INPUT
            if (!decimalCheck) {        // If legal to input decimal point
                decimalCheck = true;    // Toggle decimalCheck TRUE
                textLine2 += ".";
            }
        }
        else if (ae.getSource() == buttons[27]) {      // ADDITION FUNCTION
            if (preventOperatorsInSeries()) {
                lastOperatorPower = false;
                lastOperatorNegative = false;
                buildInfixAndModifyScreen("+");
            }
        }
        updateScreen();     // After specific button action is performed, update the screen
    }

    /**
     * Updates the screen by setting the text to textLine1 and textLine2
     * separated by a new line.
     */
    private void updateScreen()
    {
        screen.setText(textLine1 + "\n" + textLine2);
    }

    /**
     * This method is used to build a queue containing all elements
     * of the expression the user wished to evaluate, in the standard
     * infix format. This will later be converted to postfix format.
     * It then modifies the screen to display the expression as the
     * user inputs operands and operators.
     *
     * @param operator - the user inputted operator or parenthesis
     */
    public void buildInfixAndModifyScreen(String operator)
    {
        resetDecimalCheck();    // When an operator is input, reset decimal check
        completeDecimal();      // Adds trailing 0 if no input was made after decimal point

        // If a user enters a left parenthesis then attempts
        // to enter a right parenthesis, without having input a value,
        // this block of code will place a zero between the most recent
        // left parenthesis and the next right parenthesis.
        if (operator.equals("(") || operator.equals(")")) {
            if (textLine2.equals("0") && operator.equals("(")) {
                // Do not enqueue the default zero on textLine2 when inputting
                // left parenthesis.
            }
            else if (textLine2.equals("0") && operator.equals(")")) {
                // If textLine2 is zero & input operator is right parenthesis then
                // queue zero only when the last element enqueued is a left parenthesis.
                if (getLastInfixEnqueued().equals("(")) {
                    infix.enqueue(textLine2);   // Enqueue the zero
                }
            }
            else {
                infix.enqueue(textLine2);       // Enqueue the zero
            }
        } else if (isOperator(operator) && textLine2.equals("0")) {
            // Do not enqueue the zero in textLine2 if operator is input
        } else {
            infix.enqueue(textLine2);   // Enqueue the value
        }
        infix.enqueue(operator);            // Enqueue the operator
        textLine1 = infix.dumpElements();   // Dump all elements of infix into textLine1
        textLine2 = "0";                    // Reset textLine2
    }


    /**
     * This method converts the infix expression that is stored in the
     * infix queue into a postfix expression (Reverse Polish Notation
     * using Dijkstra's Shunting-yard Algorithm and stores it in the
     * postfix queue.
     */
    public void infixToPostfix() {
        String token = null;

        while (!infix.isEmpty()) {          // Loop until infix queue is empty
            token = infix.dequeue();        // Dequeue and store the first token

            if (isNumber(token)) {          // When number is encountered, enqueue into postfix
                postfix.enqueue(token);
            } else if (isOperator(token)) { // When operator is encountered
                String topOp = "";
                if (!operators.isEmpty()) { // Peek and store operator at top of operator stack
                    topOp = operators.peek();
                }
                // Loop while precedence level of new operator is >= that of operator on top of stack
                while (isOperator(topOp) && precedenceLevel(token) >= precedenceLevel(topOp)) {
                    postfix.enqueue(operators.pop());   // Pop operator from operators stack into postfix queue
                    if (!operators.isEmpty()) {         // Set topOp to next top element, if stack is not empty
                        topOp = operators.peek();
                    } else {
                        topOp = "";                     // When stack is empty set topOp to empty string
                    }
                }
                operators.push(token);                  // Push the operator token onto operators stack
            } else if (token.equals("(")) {             // When left parenthesis is encountered, push onto stack
                operators.push(token);
            } else if (token.equals(")")) {             // When right parenthesis is encountered
                String topOp = "";
                if (!operators.isEmpty()) {             // Peek and store operator at top of operators stack
                    topOp = operators.peek();
                }
                while (!topOp.equals("(")) {            // Loop until a left parenthesis is encountered
                    postfix.enqueue(operators.pop());   // Pop operators from operators stack into postfix queue
                    topOp = operators.peek();           // Set topOp to next top element, if stack is not empty
                    // If operators is empty before encountering left parenthesis, an error has occured
                    if (operators.isEmpty()) {
                        textLine2 = "Error: mismatched parenthesis";
                        updateScreen();
                    }
                }
                operators.pop();                        // Pop the left parenthesis, not unto the postfix queue
            }
        } // end while loop

        // When operators stack is not empty after infix queue has been emptied
        while (!operators.isEmpty()) {
            String topOp = operators.peek();    // Peek and store operator at top of operators stack
            // If it is a parenthesis, and error has occured
            if (topOp.equals("(") || topOp.equals(")")) {
                textLine2 = "Error: mistmatched parenthesis";
            }
            postfix.enqueue(operators.pop());   // Pop operator into postfix queue
        } // end while loop
    }

    /**
     * This method implements an algorithm to evaluate an expression in
     * Reverse Polish Notation.
     */
    public void evaluateExpression()
    {
        String result = "";
        while (!postfix.isEmpty()){             // Loop until postfix queue is empty
            String token = postfix.dequeue();   // Dequeue postfix and store token

            if (isNumber(token)) {              // When a number is encountered, push onto rpn stack
                rpn.push(token);
            }
            else if (isOperator(token)) {       // When an operator is encountered, pop 2 values and evaluate
                result = operations(token, rpn.pop(), rpn.pop());
                rpn.push(result);               // Push result onto rpn stack
            }
        } // end while loop

        if (rpn.getTop() == 0) {    // When there is only one value left in rpn stack, this is the answer
            textLine2 = rpn.pop();  // Pop answer into testLine2 of screen
            textLine1 = "";
        } else {                    // When there are more than one value left on stack after postfix
            textLine2 = "Error";    // queue has been emptied, an error has occurred.
        }
    }

    /**
     * Calls parseDouble to convert input strings a and b to double.
     * Then calls the appropriate operation to be carried out on x and y
     * based on the input operator.
     *
     * @param operator - mathematical operator
     * @param b - input string to be converted and evaluated
     * @param a - input string to be converted and evaluated
     * @return - the result of the chosen mathematical operation, after
     *           converting back into a string.
     */
    public String operations(String operator, String b, String a)
    {
        double x = 0;
        double y = 0;
        double result = 0.0;
        String rtn = "";

        x = parseDouble(a);     // Parse a and b as doubles
        y = parseDouble(b);

        switch (operator) {     // Choose operation method to call based on operator
            case "*":
                result = multiply(x, y);
                break;
            case "/":
                result = divide(x, y);
                break;
            case "%":
                result = mod(x, y);
                break;
            case "+":
                result = addition(x, y);
                break;
            case "-":
                result = subtraction(x, y);
                break;
            case "^":
                result = power(x, y);
                break;
            default:
                rtn = "Error";
                break;
        }

        rtn += result;
        return rtn;
    }

    /**
     * Multiply two Double values.
     * @param x - first factor to be multiplied by second factor
     * @param y - second factor
     * @return - product of multiplying x by y
     */
    public Double multiply(Double x, Double y)
    {
        return x * y;
    }

    /**
     * Divide two Double values.
     * @param x - dividend
     * @param y - divisor
     * @return - quotient of x / y
     * @throws DivideByZeroException
     */
    public Double divide(Double x, Double y)
            throws DivideByZeroException
    {
        if (y == 0) {
            throw new DivideByZeroException();
        }
        try{
            return x / y;
        } catch (DivideByZeroException dbze) {
            JOptionPane.showMessageDialog(null, dbze, "Error Message"
                    , JOptionPane.ERROR_MESSAGE);
        }
        return x / y;
    }

    /**
     * Modulo operation. Calculates the remainder of the division of x / y.
     * @param x - dividend
     * @param y - divisor
     * @return - remainder of x / y
     */
    public Double mod(Double x, Double y)
    {
        return x % y;
    }

    /**
     * Add two numbers together.
     * @param x - addend x
     * @param y - addend y
     * @return - sum of x + y
     */
    public Double addition(Double x, Double y)
    {
        return x + y;
    }

    /**
     * Subtract one number from another.
     * @param x - minuend
     * @param y - subtrahend
     * @return - difference of x - y
     */
    public Double subtraction(Double x, Double y)
    {
        return x - y;
    }

    /**
     * Calculate a number to the power of another number.
     * @param x - base
     * @param y - exponent
     * @return - result of x^y
     */
    public Double power(Double x, Double y)
    {
        return Math.pow(x, y);
    }

    /**
     * Clear Memory function. Clears the value stored in memoryStore.
     */
    private void memoryClear()
    {
        memoryStore = 0.0;
    }

    /**
     * Memory Recall function. The user can use this function to recall
     * a value previously stored with the Memory Store function and use
     * that value as an operand.
     */
    private void memoryRecall()
    {
        if (memoryStore == 0.0) {       // When memory store is empty, just display zero
            textLine2 = "0";
        } else {
            textLine2 = "";             // Clear textLine2, then append the value in memoryStore
            textLine2 += memoryStore;
        }
    }

    /**
     * Memory Store function. The user can use this function to store a value for
     * later use.
     */
    private void memoryStoreFunction()
    {
        memoryStore = parseDouble(textLine2);
    }

    /**
     * Memory Plus function. Adds the value input on screen to the current value
     * stored in memoryStore.
     */
    private void memoryPlus()
    {
        memoryStore += parseDouble(textLine2);
    }

    /**
     * Memory Minus function. Subtracts the value input on screen from the current
     * value stored in memoryStore.
     */
    private void memoryMinus()
    {
        memoryStore -= parseDouble(textLine2);
    }

    /**
     * Determines whether it is legal to input another operator. This prevents the user
     * from entering three + operators in a row, for example.
     * @return - true if it last input is not an operator or not zero. False otherwise.
     */
    private boolean preventOperatorsInSeries()
    {
        return !lastInputIsOperator() || !textLine2.equals("0");
    }

    /**
     * Resets the decimal check used to prevent the user from entering multiple decimal
     * points into a single operand.
     */
    private void resetDecimalCheck() { decimalCheck = false; }

    /**
     * Checks if the user input a number followed by a decimal point
     * but did not input any digit after the point, and proceeded to
     * press a function button. Adds a 0 after the decimal in this case.
     */
    public void completeDecimal()
    {
        if (getLastChar().equals(".")) {
            textLine2 += 0;
        }
    }

    /**
     * Get the last character on textLine2.
     * @return a substring of textLine2 with the last char trimmed off
     */
    public String getLastChar()
    {
        int length = textLine2.length();
        return textLine2.substring(length-1);
    }

    /**
     * Get and return the last element enqueued in the infix queue.
     * @return - value stored at front of infix queue, or empty string
     */
    public String getLastInfixEnqueued() {
        if (!infix.isEmpty()) {
            return infix.getRearValue();
        }
        else {
            return "";
        }
    }

    /**
     * Test if the last element enqueued on the infix queue is a supported operator.
     * @return - true if supported operator, false otherwise
     */
    public boolean lastInputIsOperator()
    {
        for (int i = 0; i < supportedOperators.length; i++) {
            if (!infix.isEmpty()) {
                if (getLastInfixEnqueued().equals(supportedOperators[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if parenthesis entered so far are balanced. Used to
     * decide if it is legal to input a right parenthesis.
     * @param par - left parenthesis or right parenthesis
     * @return - true if parenthesis so far are balanced, false otherwise
     */
    public boolean parenthesisBalanced(String par)
    {
        if (par.equals("(")) {
            parenthesis.push(par);
            return true;
        }
        else if (par.equals(")")){
            if (parenthesis.isEmpty()) {
                return false;
            }
            else {
                parenthesis.pop();
                return true;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Exception to be thrown in the case where the user attempts to
     * divide by zero.
     */
    public class DivideByZeroException extends ArithmeticException
    {
        public DivideByZeroException()
        {
            super("Attempted to divide by zero");
        }

        public DivideByZeroException(String message)
        {
            super(message);
        }
    }

    /**
     * Attempt to parse a string as a Double.
     * @param s - string to be parsed
     * @return - Double value after parsing
     */
    public double parseDouble(String s)
    {
        Double d = 0.0;
        try {
             d = Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            System.err.print("Error parsing string to double");
        }
        return d;
    }

    /**
     * Classifies the precedence level of the supported operators. This is
     * used in the evaluation of the postfix expression.
     * @param operator - operator to check precedence level of
     * @return - precedence level of supplied operator
     */
    public int precedenceLevel(String operator)
    {
        switch (operator) {
            case "+":
                return 4;
            case "-":
                return 4;
            case "*":
                return 3;
            case "/":
                return 3;
            case "%":
                return 3;
            case "^":
                return 2;
            case "(":
                return 1;
            case ")":
                return 1;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    /**
     * Check if inputted string is a supported operator.
     * @param s - string to be checked
     * @return - true if s is a supported operator, false otherwise
     */
    public boolean isOperator(String s) {
        for (String supportedOperator : supportedOperators) {
            if (s.equals(supportedOperator)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * Check if given string can be parsed as a Double, and thus is a number.
     * @param s - string to be checked
     * @return - true if string can be parsed, and thus is a number. False otherwise
     */
    public boolean isNumber(String s)
    {
        try {
            double d = Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
