import DFA.DFAnode;
import Earley.Earley;
import Machine.LayerQueue;
import Machine.Machine;
import Machine.Symbolizer;
import SymbolTable.SymbolTable;
import symbol.Symbol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class WyrmChild {
    static PrintWriter out;
    static PushbackInputStream data = null;
    static FileInputStream lexiconStream = null;
    static FileInputStream grammarStream = null;
    static int hmIndex = 0;
    static HashMap<String, Integer> hm = new HashMap<String, Integer>();
    static int i = 0; //debug

    public static void main(String[] args) throws UnknownHostException, IOException {
        String lexiconFilename = args[0];
        String grammarFilename = args[1];


        try {
            lexiconStream = new FileInputStream(grammarFilename);
            grammarStream = new FileInputStream(grammarFilename);
            //out = new PrintWriter("C:\\Users\\grahamr9\\workspace\\Apiary\\output.txt");
            //data = new PushbackInputStream(in);

        } catch (Exception e) {
            System.out.println("File error in WyrmChild");
        }
        //Scanner scanner = new Scanner("C:\\Users\\grahamr9\\workspace\\Apiary\\lexical.txt", data);
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        ArrayList<String> header = new ArrayList<String>();

        Machine machine = new Machine();
        machine.initialize();
        machine.run(grammarStream);
        LayerQueue layerQueue = new LayerQueue(machine.tokenQueue);
        Symbolizer symbolizer = new Symbolizer(layerQueue.layers);
        symbolizer.categorizeTokens();
        ArrayList<ArrayList<Symbol>> Grammar = symbolizer.symbolMatrix;

        SymbolTable nonTerminalSymbolTable = new SymbolTable("nonTerminal");
        SymbolTable TerminalSymbolTable = new SymbolTable("terminal");

        Symbol N = TerminalSymbolTable.add('N'); //"[ \r\n\t]*\"([^\\]|\\\\"|\\\\\)*\"[ \r\n\t]*"
        Symbol S = TerminalSymbolTable.add('S'); //"[ \r\n\t]*(true|false|null|-?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?)[ \r\n\t]*"



        //How does the lexical and grammar specification overlap?

        scanner.build();
        for (int i = scanner.recognizers.size(); i < TerminalSymbolTable.rows.size(); i++) {
            scanner.recognizers.add(DFAnode.easyDFA(TerminalSymbolTable.rows.get(i).token));
        }

        Earley Parser = new Earley(Grammar, nonTerminalSymbolTable, TerminalSymbolTable);

        Symbol y = new Symbol();
        boolean quitFlag = false;
        boolean completeFlag = false;

        while (!quitFlag) {
            Parser.reset();
            completeFlag = false;
            while (!quitFlag && !completeFlag) {
                if ((y = scanner.giveToken()) == null) { //getToken(data))==null){
                    quitFlag = true;
                } else {
                    System.out.println("Reading: " + y.token + ", Hit count: " + i);
                    i++;
                }
                completeFlag = Parser.parseSymbol(y);
            }

            System.out.println("***********Holistic calling build parse tree***************");
            Parser.buildParseTree(Grammar, Grammar.get(0).get(0), Parser.inputIndex);
            System.out.println("***********in Holistic***************");


        }

        out.close();


    }