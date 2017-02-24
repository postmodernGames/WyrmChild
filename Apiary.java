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
import Socket.SimpleSocket;

public class Apiary
{
	static PrintWriter  out;
	static PushbackInputStream data = null;
	static FileInputStream in = null;
	static int hmIndex =0;
	static HashMap<String, Integer> hm = new HashMap<String, Integer>();
	static int i =0; //debug
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		SimpleSocket socket = new SimpleSocket();
		socket.communicate("toronto-stg.kuali.co", "/api/cm/courses", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4NzdhY2E2YWFjODQ5MGJkNTU3MDY2MiIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTE1NzczOTkwLCJpYXQiOjE0ODQyMzc5OTB9.TTLFeBOvsrT7QYcIzjF8IHwmUFHUapb4iLKMcN3uPMU");
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
		
			scanner.build();
			for(int i = scanner.recognizers.size(); i<TerminalSymbolTable.rows.size(); i++){
				scanner.recognizers.add(DFAnode.easyDFA(TerminalSymbolTable.rows.get(i).token));
			}
			
			Earley Parser = new Earley(Grammar, nonTerminalSymbolTable, TerminalSymbolTable);
			
		
			Symbol y = new Symbol();
			boolean quitFlag = false;
			boolean completeFlag = false;
			
			if(!removeChar(in,'[')){
				System.out.println("Error: the first non-whitespace character was not a '['");
			}
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
					completeFlag = Parser.processSymbol(y);	
				}
				
				System.out.println("***********Holistic calling build parse tree***************");
				root.buildParseTree(Grammar.get(0),Parser.inputIndex-1,Grammar, Parser);
				System.out.println("***********in Holistic***************");
				
				
				ArrayList<String> record = new ArrayList<String>();
				record.clear();
				descent(header, record, root.children.get(0).children.get(0).children.get(1),"");
				printRow("",record);
				
			}		
			printRow("",header);
			out.close();
			
		
	}

	public static void printRow(String title, ArrayList<String >record){
		//System.out.print(title + ",");
		//out.print(title + ",");
		for(String item : record){
			System.out.print(item + ",");
			out.print(item.replace('\t', ' ') + "\t");
		}
		System.out.print("\n");
		out.print("\n");
		out.flush();
	}
	
	public static void descent(ArrayList<String> header, ArrayList<String> record, Node current, String title){
		boolean flag = false;
		if(current.symbol.number==5 && current.symbol.category == "nonTerminal"){  //Symbol is M 
			if(title.equals("")){
				title = current.children.get(0).symbol.token;
			}
			else {
				title += "; " + current.children.get(0).symbol.token;
				title = title.replace("\"","");
				title = '"' + title + '"';
			}
			descent(header,record,current.children.get(2),title);
		} else if(current.symbol.number == 6  && current.symbol.category == "nonTerminal" && current.children.get(0).symbol.category == "terminal"){
			processEntry(header, record, title,  current.children.get(0).symbol.token);
		} else {
			if(current.children!=null){
				for(Node node : current.children){
					descent(header,record,node,title);
				}
			}
		}
	}
	
	public static void ensureSize(ArrayList<String> list, int size) {
	    // Prevent excessive copying while we're adding
	    list.ensureCapacity(size);
	    while (list.size() < size) {
	        list.add("");
	    }
	}
	
	public static void processEntry(ArrayList<String> header, ArrayList<String> record, String title,  String s2){
		int column;
		column = getColumn(hm,header,title);
		ensureSize(record,column+1);
		if(!record.get(column).equals("")){
			s2 = record.get(column).trim() + "; " + s2.trim();
		}
		record.set(column, s2);
	}
	
	public static int getColumn(HashMap<String,Integer> h, ArrayList<String> header, String item){
		 
		if(!h.containsKey(item)){
			h.put(item, hmIndex);
			hmIndex++;
			header.add(item);
		}
		return h.get(item);
	}
	
	static ArrayList<Node> prepareTarget( ArrayList<ArrayList<Symbol>> Grammar, int ruleIndex){
			ArrayList<Node> target = new ArrayList<Node>();
			for(int j=1;j< Grammar.get(ruleIndex).size(); j++){
				target.add(new Node(Grammar.get(ruleIndex).get(j)));
			}
			return target;
	}
	
	static void breadthSearchFrom(Node node, Node[] target, ArrayList<Node> stack, int follow){
		boolean match = false;
		if(node.children!=null){
			if(node.children.size() == target.length){
				match = true;
				for(int j = 0;j<target.length;j++){
					if(!node.children.get(j).symbol.equals(target[j].symbol)) match = false;
				}
				if(match){
					stack.add(node);
					if(follow>-1) breadthSearchFrom(node.children.get(follow),target,stack,follow);
				}
			}
			if(!match){
				for(int j = 0;j<node.children.size();j++){
					breadthSearchFrom(node.children.get(j),target,stack, follow);
				}
			}
		}
	}
	
	public static boolean removeChar(FileInputStream in, char c){
		char z = 'a';
		try {
			z = (char)in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Pattern pat = Pattern.compile("\\s");
		Matcher mat = pat.matcher(z+"");
		while(mat.matches()){
			try {
				z = (char)in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mat = pat.matcher(z+"");
		}
		return z == c;
	}

}
