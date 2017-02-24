package DFA;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.*;

import Node.*;
import symbol.Symbol;

class Vertex extends Node{
	static int i=0;
	int id;
	//Vertex parent;  
    ArrayList<Edge> edges;
    boolean accept;
    
    Vertex(){
        	edges = new ArrayList<Edge>();
        	id = i;
        	i++;
        	accept = false;
    }
    Vertex(Node v){
    	symbol = v.symbol;
       	edges = new ArrayList<Edge>();
       	id = i;
    	i++;
    	accept = false;
    }

    void join(Vertex child, String s, boolean sign){
    	Edge e = new Edge(child,s,sign);
    	edges.add(e);
//    	child.parent = this;
    }
    public void detach(Vertex v){
		for(Edge e : edges){
			if(e.vertex == v){
				edges.remove(e);
				break;
			}
		}
	}
}

class NFA{
	Vertex start;
	Vertex end;
	Node node;
	NFA(Node n){
		this.node  = n;
		start = new Vertex();
		end = new Vertex();
	}
	void join(Vertex child, String s, boolean sign){
		end.join(child,s,sign);
	}
}

class Edge{
	Vertex vertex;
	String label;
	boolean positive;
	Edge(Vertex child, String s){
		vertex = child;
		label = s;
		positive = true;
	}
	Edge(Vertex child, String s,boolean sign){
		vertex = child;
		label = s;
		positive = sign;
	}
}


public class DFAnode extends Vertex{
	ArrayList<Vertex> nfanodes;
	int number;
	
	public DFAnode(){
		nfanodes = new ArrayList<Vertex>();
	}

	public static DFAnode generateDFA(Node root, ArrayList<ArrayList<Symbol>> Grammar, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		Vertex start = new Vertex();//Vertex start = new Vertex();
		Vertex accept = new Vertex();
		accept.accept= true;
		NFA v = new NFA(root);  //v.node âž� root
		start.join(v.start, "",true);  //Edge(v.start,É›) âˆˆ start.edges âˆ§  start = v.parent
		v.end.join(accept, "",true); //Edge(accept,É›) âˆˆ v.edges âˆ§  v = accept.parent
	
		Thompson(v,Grammar,0);
		ArrayList<ArrayList<Integer>> stack = new ArrayList<>();
		System.out.println("************ DFAnode calling printDFA for NFA**************");
		printDFA(stack,start,dictionary,lexicon);
		System.out.println("************ DFAnode leaving printDFA for NFA**************");
		
		Vertex.i =0;
		ArrayList<DFAnode> DFA = new ArrayList<DFAnode>();
		DFAnode dnode = new DFAnode();
		dnode.nfanodes.add(start);
		dnode.eclosure();
		DFA.add(dnode);
		
		for(int i=0;i<DFA.size();i++){
			DFAnode.convertNFAtoDFA(DFA,DFA.get(i));
		}
		
		stack.clear();
		System.out.println("************ DFAnode calling printDFA**************");
		printDFA(stack,dnode,dictionary,lexicon);
		System.out.println("************ DFAnode leaving printDFA**************");
		return dnode;
	}
	
	
	public static DFAnode  easyDFA(String label){
		Vertex start = new Vertex();//Vertex start = new Vertex();
		Vertex accept = new Vertex();
		accept.accept= true;
		Vertex u = new Vertex();
		Vertex v = new Vertex();
		start.join(u, "",true);
		u.join(v, label,true);
		v.join(accept, "",true); 
		DFAnode dnode = new DFAnode();
		dnode.nfanodes.add(start);
		dnode.eclosure();
		
		ArrayList<DFAnode> DFA = new ArrayList<DFAnode>();
		DFA.add(dnode);
		for(int i=0;i<DFA.size();i++){
			DFAnode.convertNFAtoDFA(DFA,DFA.get(i));
		}
		return dnode;
	}
	
	
	

	public void eclosure(){
		for(int v=0;v<this.nfanodes.size();v++){
			for(int e=0;e<this.nfanodes.get(v).edges.size();e++){
				if((this.nfanodes.get(v).edges.get(e).label == "") && !this.nfanodes.contains(this.nfanodes.get(v).edges.get(e).vertex)){
					this.nfanodes.add(this.nfanodes.get(v).edges.get(e).vertex);
					if(this.nfanodes.get(v).edges.get(e).vertex.accept) this.accept = true; 
					this.eclosure();
				}
			}
		}
	}
	
	public static boolean checkProduction(NFA v, ArrayList<Symbol> rule){
		if(!v.node.symbol.equals(rule.get(0))) return false;
		if(v.node.children.size()!=rule.size()-1) return false;
		for(int j=0; j < v.node.children.size();j++){
			if(v.node.children.get(j).symbol.equals(rule.get(j+1))==false) return false;
		}
		return true;
	}

	public static void printLevel(int level, int index, NFA v){
		String space ="";
		for(int i=0;i<level;i++){
			space += ".";
		}
		System.out.print(space + v.start.id  + " " +v.end.id + " r "  + index + ", " + v.node.symbol.token + "-->");
		for(int j=0; j<v.node.children.size();j++){
			System.out.print( v.node.children.get(j).symbol.token + " " );
		}
		System.out.println("");
	}
	
	public static void Thompson(NFA v, ArrayList<ArrayList<Symbol>> Grammar, int level){
		outer:
		for(int index=0; index< Grammar.size(); index++){
			if(checkProduction(v,Grammar.get(index)) ){
				switch(index){
				case 0:   //S->R
					printLevel(level,index,v);
					v.node = v.node.children.get(0);
					Thompson(v,Grammar,level+1);
					break outer;
				case 1: //R->D
					printLevel(level,index,v);
					v.node = v.node.children.get(0);
					Thompson(v,Grammar,level+1);
					break outer;
				case 3: //D->K
					printLevel(level,index,v);
					v.node = v.node.children.get(0);
					Thompson(v,Grammar,level+1);
					break outer;
				case 7: //K->C
					printLevel(level,index,v);
					v.node = v.node.children.get(0);
					Thompson(v,Grammar,level+1);
					break outer;
				case 2:  //Disjunct
					printLevel(level,index,v);
					NFA sibling1 = new NFA(v.node.children.get(0));
					NFA sibling2 = new NFA(v.node.children.get(2));
					sibling1.end.join(v.end, "",true);
					sibling2.end.join(v.end, "",true);
					System.out.println("V: " + v.start.id + ", "  +  v.end.id);
					v.start.join(sibling1.start, "",true);
					v.start.join(sibling2.start, "",true);
					System.out.println("S1: " + sibling1.start.id + ", "  +  sibling1.end.id);
					System.out.println("S2: " + sibling2.start.id + ", "  +  sibling2.end.id);
					Thompson(sibling1,Grammar,level+1);
					Thompson(sibling2,Grammar,level+1);
					break outer;
				case 5:  //Star
					printLevel(level,index,v);
					NFA insideS = new NFA(v.node.children.get(0));
					v.start.join(insideS.end,"",true);
					insideS.end.join(v.end, "",true);
					v.end.join(insideS.start,"",true);
					insideS.end.join(v.start, "",true);
					Thompson(insideS,Grammar,level+1);
					break outer;
				case 6:  //Parentheses
					printLevel(level,index,v);
					v.node = v.node.children.get(1);
					Thompson(v,Grammar,level+1);
					break outer;
				case 4:   //Concatenation
					printLevel(level,index,v);
					NFA second = new NFA(v.node.children.get(1));
					v.node = v.node.children.get(0);
					for(Edge e : v.end.edges)
						second.end.edges.add(e);
					v.end.edges.clear();
					v.end.join(second.start,"",true);
					Thompson(v,Grammar,level+1);
					System.out.println(v.end.id + "-> " + second.start.id);
					Thompson(second,Grammar,level+1);
					
					break outer;
				case 8:   //Terminal
					printLevel(level,index,v);
					v.start.join(v.end, v.node.children.get(0).symbol.token, true);
					break outer;
				case 9:  //Question
					printLevel(level,index,v);
					v.start.join(v.end,"",true);
					NFA insideQ = new NFA(v.node.children.get(0));
					v.start.join(insideQ.start,"", true);
					insideQ.end.join(v.end, "", true);
					Thompson(insideQ,Grammar,level+1);
					break outer;
				case 10:   //Not
					printLevel(level,index,v);
					v.start.join(v.end, "^" + v.node.children.get(2).symbol.token,true);
					break outer;
				case 11:   //
					printLevel(level,index,v);
					v.node = v.node.children.get(1);
					Thompson(v,Grammar,level+1);
					break outer;
				case 12:   //Range
					printLevel(level,index,v);
					v.start.join(v.end,  v.node.children.get(0).symbol.token + v.node.children.get(2).symbol.token,false);
					break outer;
				}
			}
		}
	}
		
	public static void convertNFAtoDFA(ArrayList<DFAnode> DFA, DFAnode dnode){
		HashSet<String> Completed = new HashSet<String>();
		for(Vertex v: dnode.nfanodes){
			for(Edge e : v.edges){
				if(!e.label.equals("")){
					if(!Completed.contains(e.label + e.positive)){
						DFAnode e_succ = new DFAnode();
						for(Vertex u : dnode.nfanodes){
							for(Edge f : u.edges){
								if(f.label.equals(e.label)&& (f.positive == e.positive)){
									e_succ.nfanodes.add(f.vertex);
								}
								break;
							}
						}
						Completed.add(e.label + e.positive);
						e_succ.eclosure();
						boolean match = false;
						for(DFAnode y  : DFA){
							if(y.nfanodes.equals(e_succ.nfanodes)){
								match = true;
								Edge e_succEdge = new Edge(y,e.label,e.positive);
								dnode.edges.add(e_succEdge);
								break;
							}
						}
						if(!match){
							Edge e_succEdge = new Edge(e_succ,e.label,e.positive);
							dnode.edges.add(e_succEdge);
							DFA.add(e_succ);
						}
					}
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
	
	public static void printNFA(ArrayList<ArrayList<Integer>> stack, Vertex current, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		 int a,b;		
		for(Edge e : current.edges){
			a = current.hashCode();
			b = e.vertex.hashCode();
			if(a>b) {
				int c = b;
				b=a;
				a=c;
			}
			boolean match = false;
			for(ArrayList<Integer> s : stack){
				if((s.get(0)==a ) && (s.get(1) ==b)){
					match =  true;
				}
			}
			if(!match){
				System.out.println(a + "-> " +b);
				ArrayList<Integer> temp = new ArrayList<>();
				temp.add(a);
				temp.add(b);
				stack.add(temp);
				for(Edge e1 : current.edges){
					printNFA(stack,e1.vertex,dictionary,lexicon);
				}
			}
		}
	}
	
	public static void printDFA(ArrayList<ArrayList<Integer>> stack, Vertex current, ArrayList<Character> dictionary, ArrayList<Character> lexicon){
		 int a,b,c;
		 
		for(Edge e : current.edges){
			a = current.id;
			b = e.vertex.id;
			c = e.label.hashCode();
			boolean match = false;
			for(ArrayList<Integer> s : stack){
				if((s.get(0)==a ) && (s.get(1) ==b) && (s.get(2) ==c)){
					match =  true;
				}
			}
			if(!match){
				if(current.accept) System.out.println(a + "--> " +  (e.label.equals(" ") ? "_" : e.label) + " -->" +b + "   *" ); 
				System.out.println(a + "--> " + (e.label.equals(" ") ? "_" : e.label) + " -->" +b);
				ArrayList<Integer> temp = new ArrayList<>();
				temp.add(a);
				temp.add(b);
				temp.add(c);
				stack.add(temp);
				for(Edge e1 : current.edges){
					printDFA(stack,e1.vertex,dictionary,lexicon);
				}
			}
		}
	}

	public static Symbol giveToken(PushbackInputStream data, ArrayList<DFAnode> recognizers){
		Symbol y = new Symbol();
		y.category = "terminal";
		
			boolean match = true;
			String lastgood = "";
			String accum = "";
			char c='0';
			int index=0;
			Vertex v = (Vertex)recognizers.get(index);
			String j = "";
			
			outer:
			while(true){
				if(match){
					j = "";
					c = readChar(data);
					while(c== '\r' || c == '\n' || c == '\t' || (accum.equals("") && c == ' ')){
						c = readChar(data);
					}
					if(c=='\\'){
						c = readChar(data);
						j = "\\" + c;
					}
					else j = "" + c;
					accum += c;
					match = false;
				}
				if(!(i==-1 || i == 255)){ 
					for(Edge e : v.edges){
						if(e.label.equals(j)  || (e.label.charAt(0)=='^' && e.label.charAt(1)!=c) || (e.positive==false && (c>=e.label.charAt(0) && c<= e.label.charAt(1) )) ){  //so far so good
							if(e.vertex.accept){     //so far, so better: we are at an acceptance state, so save the accumulated string--it will be our token if we can't make a longer one
								lastgood = accum;
							}
							v= e.vertex;
							match = true;
							break;  //don't bother testing the remaining edges; this is a DFA, so there is only one edge with this label.
						}
					}
				}
				if(!match){  //accum isn't recognized
					int z = accum.length() - lastgood.length();
					while(z>0){
						try{
							data.unread(accum.charAt(accum.length()-z));
						} catch (Exception e) {
							e.printStackTrace();
						}
						z--;
					}
					accum ="";
					match = true;
					if(lastgood!=""){  //this is our recognizers, we've just come to far
						y.token = lastgood;
						y.number = index;
						break outer;  //we've got our token; let's exit
					}
					else {
						index++;
						if(index==recognizers.size()) return null;
						 v = (Vertex)recognizers.get(index);
					}
				}
			} //end while
		
		return y;
	}
	
	static char readChar(PushbackInputStream data){
		char c = 0xffff;
		byte b;
		int j,k;
		String text = null;
		int count=0;
		
		try {
			b = (byte)data.read();
			text = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
			j = (b<0 ? 256+b  : b);
			if(((1<<7)&j)!=0){  //1xxxxxx
				if(((1<<5)&j)!=0){ //111xxxxx
					if(((1<<4)&j)!=0){ //1111xxxx
							count = 4;
					} else { //1110
						count = 3;
					}
				}else { //110
					count = 2;
				}
			} else { //0
				count =1;
			}
			byte[] bytes  = new byte[count];
			bytes[0] = b;
			for(int i=1 ;i < count; i++){
				b = (byte)data.read();
				bytes[i] = b;
			}
			text = new String(bytes, "UTF-8");
			c = text.charAt(0);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	
}
