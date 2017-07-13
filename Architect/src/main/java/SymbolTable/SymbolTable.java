package SymbolTable;

import java.util.ArrayList;
import symbol.Symbol;
import symbol.Symbol.SymbolType;

/*
 * Symbol table should be given a file which it reads to build the symbols
 * 
 * 
 */


public class SymbolTable{
	public ArrayList<Symbol> rows;
	public ArrayList<Character> dictionary;
	
	public SymbolTable(String str){
		rows = new ArrayList<Symbol>();
		dictionary = new ArrayList<Character>();
	}
	public Symbol getSymbol(int index, char c){   //for nonTerminals that come from lexicalInputFile 
		dictionary.add(c);
		return rows.get(index);
	}
	
	public Symbol add(char c){  //generates symbols for terminals in production
		//check whether c is already in the dictionary
		if(dictionary.contains(c)){
			return rows.get(dictionary.indexOf(c));
		} else {
			Symbol y = new Symbol();
			dictionary.add(c);
			y.symbolType = SymbolType.terminal;
			y.symbolIndex = rows.size();
			y.token = "" + c;
			rows.add(y);
			return y;
		}
	}
}

