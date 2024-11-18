import java.io.*;
import java.util.*;

public class Compiler {

    static char token;
    static int i;
    static int register;
    static char [] charStream;
    static String inputFileName;
    final static String outputFileName = "tinyL.out";
    static Set<Character> letters = new HashSet<>();
    static ArrayList<String> targetCode = new ArrayList<>();
    static PrintWriter output;

    public static void main (String[] args) {

        System.out.println("------------------------------------------------");
        System.out.println("CS314 compiler for tinyL");
        System.out.println("------------------------------------------------");

        // Check for the correct number of arguments
        if (args.length != 1) {
            System.err.println("Use of command:\n  java Main <tinyL file>");
            System. exit(0);
        }

        for (char ch : "abcdef".toCharArray()) {
            letters.add(ch);
        }

        // Get the input file name from the arguments
        inputFileName = args[0];
        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);

        charStream = new char[0];

        try {
            charStream = readInputFile(inputFile);
            output = new PrintWriter(outputFile);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (charStream.length == 0) {
            System.err.println("File is empty.");
            System. exit(0);
        }

        i = 0;
        token = charStream[i];

        register = 1;

        program();

        for (String o : targetCode) {
            output.println(o);
            System.out.println(o);
        }

        output.close();

    }

    static void codeGen(String opCode, String field1, String field2, String field3) {

        StringBuilder instruction = new StringBuilder(opCode + " " + field1);

        // For LOADI, prefix field2 with '#' to indicate a constant
        if (field2 != null) {
            if (opCode.equals("LOADI")) {
                instruction.append(" #").append(field2);  // Prefix '#' for constants
            } else {
                instruction.append(" ").append(field2);
            }
        }
        if (field3 != null) {
            instruction.append(" ").append(field3);
        }

        targetCode.add(instruction.toString());
    }

    static void nextToken() {
        if (i + 1 < charStream.length) {
            i++;
            token = charStream[i];
        } else {
            token = '\0'; // end of file
        }
    }

    static int nextRegister() {
        return register++;
    }

    // Routines for recursive descending parser LL(1)
    static void program() {

        stmtlist();

        if (token != '!') {
            System.err.println("Program must end with !");
            System. exit(0);
        }
        else
            System.out.println("Code written to file \n" + outputFileName);

    }

    static void stmtlist() {
        stmt();
        morestmts();
    }

    static void morestmts() {

        if (token == ';') {
            nextToken();
            stmtlist();
        }
        else if (token != '!') {
            System.err.println("Incomplete Statement");
            System. exit(0);
        }

    }

    static void stmt() {

        if (letters.contains(token)) {
            assign();
        }
        else if (token == '?') {
            read();
        }
        else if (token == '%') {
            print();
        }
        else {
            System.err.println("Incomplete Statement.");
            System. exit(0);
        }

    }

    static int expr() {

        if (token == '+') {
            nextToken();
            return add();
        }
        else if (token == '-') {
            nextToken();
            return subtract();
        }
        else if (token == '*') {
            nextToken();
            return multiply();
        }
        else if (token == '&') {
            nextToken();
            return bitAnd();
        }
        else if (token == '|') {
            nextToken();
            return bitOr();
        }
        else if (letters.contains(token)) {
            return variable();
        }
        else if (Character.isDigit(token)) {
            return digit();
        }

        return '0';

    }

    static void assign() {

        char id = token;
        nextToken();

        if (token != '=') {
            System.err.println("Incomplete statement.");
            System. exit(0);
        }

        nextToken();
        int resultReg = expr();

        codeGen("STORE", String.valueOf(id), "r"+Integer.toString(resultReg), null);

        nextToken();

    }

    // Cannot move to next token before returning current value. Must move to next token outside of this method.
    static int variable() {

        char var = token;
        int reg = nextRegister();  // Get the next register number

        codeGen("LOAD", "r"+Integer.toString(reg), String.valueOf(var), null);  // Use the register number as an integer in the output

        return reg;
    }

    private static int digit() {

        int digitValue = token - '0';

        int reg = nextRegister();

        codeGen("LOADI", "r"+Integer.toString(reg), String.valueOf(digitValue), null);

        return reg;
    }

    private static int add() {

        int leftReg = expr();
        nextToken();
        int rightReg = expr();

        int resultReg = nextRegister();

        codeGen("ADD", "r"+Integer.toString(resultReg), "r"+Integer.toString(leftReg), "r"+Integer.toString(rightReg));

        return resultReg;

    }

    private static int bitOr() {

        int leftReg = expr();
        nextToken();
        int rightReg = expr();

        int resultReg = nextRegister();

        codeGen("OR", "r"+Integer.toString(resultReg), "r"+Integer.toString(leftReg), "r"+Integer.toString(rightReg));

        return resultReg;

    }

    private static int bitAnd() {

        int leftReg = expr();
        nextToken();
        int rightReg = expr();

        int resultReg = nextRegister();

        codeGen("AND", "r"+Integer.toString(resultReg), "r"+Integer.toString(leftReg), "r"+Integer.toString(rightReg));

        return resultReg;

    }

    private static int multiply() {

        int leftReg = expr();
        nextToken();
        int rightReg = expr();

        int resultReg = nextRegister();

        codeGen("MUL", "r"+Integer.toString(resultReg), "r"+Integer.toString(leftReg), "r"+Integer.toString(rightReg));

        return resultReg;

    }

    private static int subtract() {

        int leftReg = expr();
        nextToken();
        int rightReg = expr();

        int resultReg = nextRegister();

        codeGen("SUB", "r"+Integer.toString(resultReg), "r"+Integer.toString(leftReg), "r"+Integer.toString(rightReg));

        return resultReg;

    }

    static void read() {

        nextToken();

        if (!letters.contains(token)) {
            System.err.println("Bad variable name.");
            System. exit(0);
        }
        else {
            char var = token;
            nextToken();
            codeGen("READ", String.valueOf(var), null, null);
        }

    }

    static void print() {

        nextToken();

        if (!letters.contains(token)) {
            System.err.println("Bad variable name.");
            System. exit(0);
        }
        else {
            char var = token;
            nextToken();
            codeGen("WRITE", String.valueOf(var), null, null);
        }

    }

    static char [] readInputFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim()); // remove whitespace
            }
        }
        return sb.toString().toCharArray();
    }

}
