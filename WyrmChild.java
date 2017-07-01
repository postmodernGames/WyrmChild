import java.util.*;
import java.util.regex.*;

import DFA.DFAnode;

import java.io.*;
import java.net.UnknownHostException;

import Earley.Earley;
import SymbolTable.SymbolTable;
import scanner.Scanner;
import symbol.*;
import Node.*;


public class WyrmChild
{
	static PrintWriter out;
	static PushbackInputStream data = null;
	static FileInputStream in = null;
	static int hmIndex =0;
	static HashMap<String, Integer> hm = new HashMap<String, Integer>();
	static int i =0; //debug
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		

		try {
			in = new FileInputStream("C:\\Users\\grahamr9\\workspace\\Apiary\\socketOut.txt");
			out = new PrintWriter("C:\\Users\\grahamr9\\workspace\\Apiary\\output.txt");
			data = new PushbackInputStream(in);
			
		} catch(Exception e){
			System.out.println("File error in Holisitic");
		}
			Scanner scanner = new Scanner("C:\\Users\\grahamr9\\workspace\\Apiary\\lexical.txt", data);
			HashMap<String, Integer> hm = new HashMap<String, Integer>();	
			ArrayList<String> header = new ArrayList<String>();
			
				
			SymbolTable nonTerminalSymbolTable = new SymbolTable("nonTerminal");
			SymbolTable TerminalSymbolTable = new SymbolTable("terminal");
			
			Symbol P = nonTerminalSymbolTable.add('P');
			Symbol J = nonTerminalSymbolTable.add('J');
			Symbol O = nonTerminalSymbolTable.add('O');
			Symbol A = nonTerminalSymbolTable.add('A');
			Symbol L = nonTerminalSymbolTable.add('L'); //4
			Symbol M = nonTerminalSymbolTable.add('M'); //5
			Symbol V = nonTerminalSymbolTable.add('V');  //6
			Symbol K = nonTerminalSymbolTable.add('K');
			Symbol N = TerminalSymbolTable.add('N'); //"[ \r\n\t]*\"([^\\]|\\\\"|\\\\\)*\"[ \r\n\t]*"
			Symbol S = TerminalSymbolTable.add('S'); //"[ \r\n\t]*(true|false|null|-?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?)[ \r\n\t]*"
		
			ArrayList<ArrayList<Symbol>> Grammar = new ArrayList<ArrayList<Symbol>>();
			Grammar.add(new ArrayList<>(Arrays.asList(P,J)));  //0
			Grammar.add(new ArrayList<>(Arrays.asList(J,O))); //1
			Grammar.add(new ArrayList<>(Arrays.asList(J,A))); //2
			Grammar.add(new ArrayList<>(Arrays.asList(O,TerminalSymbolTable.add('{'),L,TerminalSymbolTable.add('}')))); //3
			Grammar.add(new ArrayList<>(Arrays.asList(O,TerminalSymbolTable.add('{'),TerminalSymbolTable.add('}')))); //4
			Grammar.add(new ArrayList<>(Arrays.asList(A,TerminalSymbolTable.add('['),K,TerminalSymbolTable.add(']')))); //5
			Grammar.add(new ArrayList<>(Arrays.asList(A,TerminalSymbolTable.add('['),TerminalSymbolTable.add(']')))); //6
			Grammar.add(new ArrayList<>(Arrays.asList(L,L,TerminalSymbolTable.add(','),M)));  //7
			Grammar.add(new ArrayList<>(Arrays.asList(L,M)));  //8
			Grammar.add(new ArrayList<>(Arrays.asList(K,K,TerminalSymbolTable.add(','),V)));  //9
			Grammar.add(new ArrayList<>(Arrays.asList(K,V))); //10
			Grammar.add(new ArrayList<>(Arrays.asList(M,N,TerminalSymbolTable.add(':'),V)));  //11
			Grammar.add(new ArrayList<>(Arrays.asList(V,O))); //12
			Grammar.add(new ArrayList<>(Arrays.asList(V,A))); //13
			Grammar.add(new ArrayList<>(Arrays.asList(V,N)));//14
			Grammar.add(new ArrayList<>(Arrays.asList(V,S)));//15


			//How does the lexical and grammar specification overlap?

			scanner.build();
			for(int i = scanner.recognizers.size(); i<TerminalSymbolTable.rows.size(); i++){
				scanner.recognizers.add(DFAnode.easyDFA(TerminalSymbolTable.rows.get(i).token));
			}
			
			Earley Parser = new Earley(Grammar, nonTerminalSymbolTable, TerminalSymbolTable);

			Symbol y = new Symbol();
			boolean quitFlag = false;
			boolean completeFlag = false;
			
			while(!quitFlag){
				Parser.reset();
				Node root = new Node(P);
				completeFlag = false;
				while(!quitFlag&&!completeFlag){
					if((y = scanner.giveToken())==null) { //getToken(data))==null){
						quitFlag = true;
					}
					else {
						System.out.println("Reading: " + y.token+ ", Hit count: " + i);
						i++;
					}
					completeFlag = Parser.parseSymbol(y);
				}
				
				System.out.println("***********Holistic calling build parse tree***************");
				Parser.buildParseTree(root, Grammar.get(0),Parser.inputIndex-1,Grammar);
				System.out.println("***********in Holistic***************");
				

			}		

			out.close();
			
		
	}