import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Compiler {

    static char token;
    static int i;
    static char [] charStream;
    static String inputFileName;
    static Set<Character> letters = new HashSet<>();

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

        charStream = new char[0];

        try {
            charStream = readInputFile(inputFile);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (charStream.length == 0) {
            System.err.println("File is empty.");
            System. exit(0);
        }

        i = 0;
        token = charStream[i];
        program();

    }

    static void nextToken() {
        if (i + 1 < charStream.length) {
            i++;
            token = charStream[i];
        } else {
            token = '\0';
        }
    }

    // Routines for recursive descending parser LL(1)
    static void program() {

        stmtlist();

        if (token != '!') {
            System.err.println("Program must end with !");
            System. exit(0);
        }
        else
            System.out.println(inputFileName + " compiled successfully to: ");

    }

    static void stmtlist() {
        stmt();
        morestmts();
    }

    static void morestmts() {

    }

    /*
     *********************************************
     *  314 Principles of Programming Languages  *
     *********************************************
     */

    /* -------------------------------------------------

                CFG for tinyL LANGUAGE

         <program> ::= <stmt_list> !
        <stmt list> ::= <stmt> <morestmts>
        <morestmts> ::= ; <stmt list> | ε
        <stmt> 	::= <assign> | <read> | <print>
        <assign> ::= <variable> = <expr>
        <read> 	::= ? <variable>
        <print> ::= % <variable>
        <expr> ::= 	+ <expr> <expr> |
                    − <expr> <expr> |
                    ∗ <expr> <expr> |
                    & <expr> <expr> |
                    | <expr> <expr> |
                    <variable> |
                    <digit>
        <variable> 	::= a | b | c | d | e | f
        <digit> 	::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

         NOTE: tokens are exactly a single character long

         Example expressions:

                a=+2+25;%a!
                a=|2&3|25;%a!


     ---------------------------------------------------
     */

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
            System.err.println("Program must contain at least 1 statement.");
            System. exit(0);
        }

    }

    static void assign() {

        System.out.println("assignment");
        variable();

        if (token != '=') {
            System.err.println("Incomplete statement.");
            System. exit(0);
        }

        nextToken();
        expr();

    }

    static void expr() {

        if (token == '+') {
            add();
        }
        else if (token == '-') {
            subtract();
        }
        else if (token == '*') {
            multiply();
        }
        else if (token == '&') {
            bitAnd();
        }
        else if (token == '|') {
            bitOr();
        }
        else if (letters.contains(token)) {
            variable();
        }
        else if (Character.isDigit(token)) {
            integer();
        }

    }

    private static void integer() {

        StringBuilder integer = new StringBuilder();
        while (Character.isDigit(token)) {
            integer.append(token);
            nextToken();
        }
        System.out.println("Integer " + integer);

    }

    private static void bitOr() {
    }

    private static void bitAnd() {
        
    }

    private static void multiply() {
        
    }

    private static void subtract() {
        
    }

    private static void add() {

        System.out.println("addition");

        nextToken();
        expr();
        expr();

    }

    static void variable() {
        System.out.println("variable " + token);
        nextToken();
    }

    static void read() {
        // YOUR CODE GOES HERE
    }

    static void print() {
        // YOUR CODE GOES HERE
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