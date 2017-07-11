package symbol;

import java.util.ArrayList;

import DFA.DFAnode;


//Two fields:
//symbolType: tells us whether we have a terminal or nonterminal
//symbolIndex: tells us which nonTerminal we have, or the value of the terminal 
//
//what is token? the terminal value//I should drop this, it should be in the terminalSymboltable

public class Symbol{
	public enum SymbolType {
		terminal, nonTerminal
	};  
	public SymbolType symbolType;
	public int symbolIndex;
	public String token;


	public Symbol(){
		symbolType = SymbolType.nonTerminal;
	};
	
	public Symbol(char c){
		symbolType = SymbolType.terminal;
		token = "" + c;
	}

	Symbol(ArrayList<DFAnode> lexicon2, ArrayList<Character> lexicon, char c, String s){
		symbolIndex = lexicon2.size();
		lexicon2.add(new DFAnode());
		symbolType = SymbolType.terminal;
		lexicon.add(c);
	}

	public boolean equals(Symbol s){
		if(s == null) return false;
		return symbolType==s.symbolType && symbolIndex == s.symbolIndex;
	}
	
	public boolean isNonTerminal(){
		if(symbolType == SymbolType.nonTerminal) return true;
		else return false;
	}
	
	public boolean isTerminal(){
		if(symbolType == SymbolType.terminal) return true;
		else return false;
	}

	public Symbol terminal(){
		symbolType = SymbolType.terminal;
		return this;
	}

	public Symbol token(String s){
		token = s;
		return this;
	}

	public Symbol symbolIndex(int i){
		symbolIndex = i;
		return this;
	}
}