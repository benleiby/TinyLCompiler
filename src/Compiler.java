import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Compiler {

    static char token;
    static int i;
    static char [] charStream;
    static String inputFileName;
    final static String outputFileName = "tinyL.out";
    static Set<Character> letters = new HashSet<>();
    static ArrayList<String> riscCommands = new ArrayList<>();

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
            token = '\0'; // end of file
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

        if (token == ';') {
            nextToken();
            stmtlist();
        }
        else if (token != '!') {
            System.err.println("Incomplete Statement");
            System. exit(0);
        }
        else
            System.out.println("end of statement list");

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

        System.out.println("bit OR");

        nextToken();
        expr();
        expr();

    }

    private static void bitAnd() {

        System.out.println("bit AND");

        nextToken();
        expr();
        expr();

    }

    private static void multiply() {

        System.out.println("multiplication");

        nextToken();
        expr();
        expr();

    }

    private static void subtract() {

        System.out.println("subtraction");

        nextToken();
        expr();
        expr();

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

        System.out.println("read");
        nextToken();

        if (letters.contains(token)) {
            System.out.println("variable " + token);
        }

        nextToken();

    }

    static void print() {

        System.out.println("print");
        nextToken();

        if (letters.contains(token)) {
            System.out.println("variable " + token);
        }

        nextToken();

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