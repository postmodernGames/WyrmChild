package scanner;

import java.util.*;
import java.io.*;
import symbol.*;
import symbol.Symbol.SymbolType;
import DFA.*;
import Earley.Earley;
import Node.Node;
import SymbolTable.SymbolTable;
	
public class Scanner
{
	public ArrayList<DFAnode> recognizers = new ArrayList<>();
	static PrintWriter out;
	String lexicalInputFilename;
	PushbackInputStream dataFile;
	SymbolTable nst = new SymbolTable("nonTerminal");
	SymbolTable tst = new SymbolTable("terminal");
	
	String line;
	int i=0;
	
	public Scanner(String in, PushbackInputStream data){
		lexicalInputFilename = in;
		dataFile = data;
	}
	
	/*
	public Symbol getSymbol(int index, char c){
		return tst.getSymbol(index, c);
	}
*/
	public void build(){
		
		Symbol S = nst.add('S');  //0
		Symbol R = nst.add('R'); //1
		Symbol D = nst.add('D'); //2
		Symbol K = nst.add('K'); //3
		Symbol C = nst.add('C'); //4
		Symbol P = nst.add('P'); //5
		Symbol T = tst.add('T'); 
				/*
		ArrayList<ArrayList<Symbol>> Grammar = new ArrayList<ArrayList<Symbol>>();
		Grammar.add(new ArrayList<>(Arrays.asList(S,R)));  //0
		Grammar.add(new ArrayList<>(Arrays.asList(R,D))); //1
		Grammar.add(new ArrayList<>(Arrays.asList(R,R,tst.add('|'),D))); //2
		Grammar.add(new ArrayList<>(Arrays.asList(D,K,tst.add('*'))));  //3
		Grammar.add(new ArrayList<>(Arrays.asList(K,tst.add('('),R,tst.add(')'))));  //4
		Grammar.add(new ArrayList<>(Arrays.asList(C,C,P)));  //5
		Grammar.add(new ArrayList<>(Arrays.asList(K,C)));  //6
		Grammar.add(new ArrayList<>(Arrays.asList(D,K)));  //7
		Grammar.add(new ArrayList<>(Arrays.asList(C,P)));  //8
		Grammar.add(new ArrayList<>(Arrays.asList(D,K,tst.add('?'))));  //9
		Grammar.add(new ArrayList<>(Arrays.asList(K,tst.add('['),tst.add('^'),T,tst.add(']'))));  //10
	*/
		
		ArrayList<ArrayList<Symbol>> Grammar = new ArrayList<ArrayList<Symbol>>();
		Grammar.add(new ArrayList<>(Arrays.asList(S,R)));  //0
		Grammar.add(new ArrayList<>(Arrays.asList(R,D))); //1
		Grammar.add(new ArrayList<>(Arrays.asList(R,R,tst.add('|'),D))); //2
		Grammar.add(new ArrayList<>(Arrays.asList(D,K)));  //3
		Grammar.add(new ArrayList<>(Arrays.asList(D,D,K))); //4
		Grammar.add(new ArrayList<>(Arrays.asList(K,C,tst.add('*'))));  //5
		Grammar.add(new ArrayList<>(Arrays.asList(C,tst.add('('),R,tst.add(')'))));  //6
		Grammar.add(new ArrayList<>(Arrays.asList(K,C)));  //7
		Grammar.add(new ArrayList<>(Arrays.asList(C,T)));  //8
		Grammar.add(new ArrayList<>(Arrays.asList(K,C,tst.add('?'))));  //9
		Grammar.add(new ArrayList<>(Arrays.asList(C,tst.add('['),tst.add('^'),T,tst.add(']'))));  //10
		Grammar.add(new ArrayList<>(Arrays.asList(C,tst.add('['),P,tst.add(']'))));  //11
		Grammar.add(new ArrayList<>(Arrays.asList(P,T,tst.add('-'),T))); //12
		
		
		Earley Scanner = new Earley(Grammar, nst, tst);
	
		Node root = new Node(S);
		
		try(BufferedReader br = new BufferedReader(new FileReader(lexicalInputFilename))){
			Scanner.reset();
			for(line= new String(); (line = br.readLine()) != null; ) {
		    	while(line.length()>0){
		    		Symbol y = readToken(tst);
		 //   		System.out.println("Reading: " + y.token + ", Hit count: " + i);
					i++;
					Scanner.processSymbol(y);
		    	}
		    	Scanner.processSymbol(null);
//				System.out.println("***********calling Build Parse Tree in scanner***************");
				root.buildParseTree(Grammar.get(0), Scanner.inputIndex-1, Grammar, Scanner);
//				System.out.println("***********   Using printTree to  in Scanner ***************");
				
	//			printTree(root,nst.dictionary,tst.dictionary);
	//			System.out.println("Out of printtree in Scanner");
				Scanner.reset();
				DFAnode dnode = DFAnode.generateDFA(root,Grammar, nst.dictionary, tst.dictionary);
				//recognizers.add(DFAnode.generateDFA(root,Grammar, nst.dictionary, tst.dictionary));
				recognizers.add(dnode);
			}
		} catch(Exception e){
			System.out.println("The world belongs to Scanner " + e.getMessage());
		}
	}
	
	Symbol readToken(SymbolTable tst){
		char c = line.charAt(0);
		line = line.substring(1);
		Symbol y = new Symbol();	
		y.token = "" + c;
		if(c=='\\') {
			y.token += line.charAt(0);
			line = line.substring(1);
		}
		y.symbolType = SymbolType.terminal;
		
		y.symbolIndex = 0;
		for(Symbol s : tst.rows){
			if(y.token.equals(s.token)){
				y.symbolIndex = s.symbolIndex;
				break;
			}
		}
    	return y;
	}
	
	public static void printTree(Node current, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		if(current.children.size()!=0){
		//	System.out.print((current.symbol.category== "nonTerminal" ? dictionary.get(current.symbol.number) : lexicon.get(current.symbol.number)) + "-> ");
			for(Node c : current.children){
		//		System.out.print(  (c.symbol.category== "nonTerminal" ? dictionary.get(c.symbol.number) : lexicon.get(c.symbol.number)));
			}
			System.out.println("");
			for(Node c : current.children){
				printTree(c,dictionary,lexicon);
			}
		} else {
			System.out.println("T -> " + current.symbol.token);
		}
	}
	
	public Symbol giveToken(){
		return DFAnode.giveToken(dataFile, recognizers);
	}

}