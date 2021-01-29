package com.mdr.MoteurDeRecherche.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 
 * Algorithm Aho-Ullman
 *
 */
public class Automaton {
	private int start, end;
	private ArrayList<HashMap<Integer,ArrayList<Integer>>>   ndfa;
	private ArrayList<HashMap<String,Integer>>               dfa;
	private HashMap<Integer, HashMap<String,Integer>>        minDfa;
	
	public Automaton(String regex) {
		start=end=0;
		ndfa = new ArrayList<HashMap<Integer,ArrayList<Integer>>>();
		dfa = new ArrayList<HashMap<String,Integer>>();
		minDfa = new HashMap<Integer, HashMap<String,Integer>>();
		this.initialaze(new RegEx(regex));
	}
	
	/**
	 * 	Parsing of the regEx tree (Step 2)
	 *  Transform the NDFA to a DFA without epsilon transition (Step 3)
	 *  Transform the DFA to a minimalist DFA (Step 4) 
	 * @param regex : the regular expression
	 */
	private void initialaze(RegEx regex) {
		//Step 2
		try{
			this.parser(regex.parse(), 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Step 3
		this.determination();
		
		//Step 4
		this.minimization();
	}

	/**
	 * Use the automaton on the line to find matching pattern
	 * @param word for the match
	 * @return True if there is a match
	 */
	public boolean match(String word){
		char[] chars = word.toCharArray();
		int current_state = 0;
		String letter;
		HashMap<String,Integer> map;
		int cpt = chars.length; //match all the word not prefixe

		for(char c : chars){
			letter = String.valueOf(c);
			map = minDfa.get(current_state);
			cpt--;
			if(map.containsKey(letter)){
				current_state = map.get(letter);
				if(cpt == 0 && minDfa.get(current_state).get("finite") == 1){
					return true;
				}
			}
		}

		return false;
	}
	
	
	
	//-----------------------------------------------------------------------
	//--------------------    STEP 2 : PARSING    ---------------------------
	//-----------------------------------------------------------------------

	/**
	 * Transform the regEx tree into a nondeterministic finite automaton 
	 * with epsilon transition.
	 * @param tree : the RegEx tree
	 * @param index of the ndfa
	 * @return initialize the ndfa according to the regEx.
	 */
	private int parser(RegExTree tree, int index) {
		int oldS, oldE;
		//if the root is a letter
		if(tree.getSubTrees().isEmpty()){
			//beginning of a new automaton
			start = index;
			end = (index+1);
			
			addNDFA(start, tree.getRoot(),new ArrayList<Integer>(){
				private static final long serialVersionUID = 1L;
				{add(end);}});
			addNDFA(end, 35, new ArrayList<Integer>());
			return index+2;
			
		//if the root is a *	
		}else if(tree.getSubTrees().size()==1) { 
			//parsing of the only child of the root
			index = parser(tree.getSubTrees().get(0), index);
			//Initialize old start and old end
			oldS = start;
			oldE = end;
			//Initialize new start && end
			start = index;
			end = index+1;
			
			addNDFA(start,35,new ArrayList<Integer>(){
				private static final long serialVersionUID = 1L;
				{add(oldS); add(end);}}); 
			addNDFA(end, 35, new ArrayList<Integer>());
			addNDFA(oldE,35,new ArrayList<Integer>(){
				private static final long serialVersionUID = 1L;
				{add(oldS); add(end);}});
			return index+2;
			
		//if the root has 2 children
		} else {
			//parsing the left subTree of the root
			index = parser(tree.getSubTrees().get(0), index);
			int startG=start;
			int endG=end;
			
			//parsing the right subTree of the root
			index = parser(tree.getSubTrees().get(1), index);
			//save the start and end of the right subTree
			int startD=start;
			int endD=end;
			
			
			
			//if the root is a |
			if(tree.getRoot() == RegEx.ALTERN){
				//Initialize new start && end
				start=index;
				end=index+1;
				
				addNDFA(start, 35, new ArrayList<Integer>(){
					private static final long serialVersionUID = 1L;
					{add(startD); add(startG);}});    
				addNDFA(end, 35, new ArrayList<Integer>());                               
				addNDFA(endD, 35, new ArrayList<Integer>(){
					private static final long serialVersionUID = 1L;
					{add(end);}});
				addNDFA(endG, 35, new ArrayList<Integer>(){
					private static final long serialVersionUID = 1L;
					{add(end);}});
				return index+2;
				
			//if the root is a .
			}else {
				addNDFA(endG,35,new ArrayList<Integer>(){
					private static final long serialVersionUID = 1L;
					{add(startD);}}); //Concatenation
				//Initialize the new start
				start=startG;
			}
		}
		return index;
	}
	
	/**
	 * Add into the automaton the elements
	 * @param index
	 * @param key
	 * @param vals
	 */
	private void addNDFA(int index, int key, ArrayList<Integer> vals) {
		HashMap<Integer, ArrayList<Integer>> map;
		
		//element already exist but there are values to add
		if(!vals.isEmpty() && ndfa.size()>index) { 
			map = ndfa.get(index);
			if(map.containsKey(key)) {
				for(Integer val : map.get(key)) {
					if(!vals.contains(val))
						vals.add(val);
				}
			}
			map.put(key,vals);
			ndfa.remove(index);
			ndfa.add(index, map);
		
		//next element that don't exist
		}else if (ndfa.size()==index) { 
			map= new HashMap<Integer,ArrayList<Integer>>();
			if(!vals.isEmpty()) 
				map.put(key, vals);
			ndfa.add(map);
		}
	}
	
	
	/**
	 * Print the nondeterministic finite automaton
	 */
	@SuppressWarnings("unused")
	private void showNDFA() {
		System.out.println("\n ------- NONDETERMINISTIC FINITE AUTOMATON --------");
		for(int i=0; i<ndfa.size(); i++) {
			System.out.print(" "+i+" -> ");
			HashMap<Integer,ArrayList<Integer>> map = ndfa.get(i);
			Set<Integer> keys = map.keySet();
			if( keys.size() == 0)
				System.out.println("      final");
			for(int k : keys) {
				System.out.print("      "+Character.toString((char)k) +" : { ");
				for(int v : map.get(k))
					System.out.print(v+" ");
				System.out.print("}      ");
			}
			System.out.println("");
		}
		System.out.println("start : "+start+"     end : "+end+"\n");
	}

	
	
	//-----------------------------------------------------------------------
	//--------------------    STEP 3 : NDFA->DFA    -------------------------
	//-----------------------------------------------------------------------
	
	
	/**
	 * Transform the nondeterministic finite automaton 
	 * with epsilon transition into a deterministic finite automaton.
	 * start = 0     end = map.size()
	 */
	private void determination() {
		int index=0, tmp;
		HashMap<Integer, Set<Integer>> cache = new HashMap<Integer, Set<Integer>>();	
		Set<Integer> res = new HashSet<Integer>();

		//Begin with the start state
		Set<Integer> states = epsClosure(new ArrayList<Integer>() { 
			private static final long serialVersionUID = 1L;
			{add(start);}});
		cache.put(index, states);
		addDFA(index, states.contains(this.end), -1, -1);
		
		//----------------------------
		while(index < dfa.size()) {
			//Follow all possible transition
			for(Integer letter: getLetters(states)) {
				res = new HashSet<Integer>();
				for(Integer state: states) {
					if(ndfa.get(state).containsKey(letter)) {
						for(Integer x : ndfa.get(state).get(letter)) {
							 res.addAll(epsClosure(
									 new ArrayList<Integer>() {
										 private static final long serialVersionUID = 1L; 
										 {add(x);}}));
						}
					}
				}
				
				tmp = isState(cache, res);
				//The new state already exist in dfa 
				if(tmp != -1) {
					//Update the parent
					this.addDFA(index, false, letter, tmp);
				
				}else {
					cache.put(dfa.size(), res);
					//Create a new state in dfa 
					this.addDFA(dfa.size(), res.contains(this.end), letter, -1);
					//Update the parent
					this.addDFA(index, false, letter, dfa.size()-1);
				}
				//System.out.println(index+" "+cache.get(index)+" -> "+ letter+"  "+res);
			} 
			index++;
			states=cache.get(index);
		}
		
	}
	
	/**
	 * Check if the Set of Integer (that represent a state) already exist in the DFA 
	 * @param map of all the existing states in the DFA
	 * @param vals represent the state that we want to check
	 * @return if the state already exist, it return the Index of the state in dfa else -1
	 */
	private int isState(HashMap<Integer, Set<Integer>> map, Set<Integer> vals) {
		int index = -1;
		boolean exist=false;
		for(Integer n: map.keySet()) {
			Set<Integer> state = map.get(n);
			if(state.size() == vals.size()) {
				exist=true;
				for(Integer val : vals) {
					if(!state.contains(val)) exist=false;
				}
			}
			if(exist) {
				index=n;
				break;
			}
		}
		return index;
	}
	
	/**
	 * Get all the name of transition (without epsilon)
	 * @param states : list of state 
	 * @return all the name/letter of transition of a set of state
	 */
	private Set<Integer> getLetters(Set<Integer> states) {
		Set<Integer> result = new HashSet<Integer>();
		for(Integer state: states) {
			if(state < ndfa.size()) {
				result.addAll(ndfa.get(state).keySet());
			}
		}
		result.remove(35); //remove epsilon transition
		return result;
	}
	
	/**
	 * Get the epsilon clojure of all the set of state
	 * @param states : list of state 
	 * @return all the epsilon transition of the list of state
	 */
	private Set<Integer> epsClosure(ArrayList<Integer> states){
		Set<Integer> result = new HashSet<Integer>();
		result.addAll(states);
		for(Integer state : states) {
			if(ndfa.size()>state && ndfa.get(state).containsKey(35)) {
				result.addAll(epsClosure(ndfa.get(state).get(35)));
			}
		}
		return result;
	}
	
	/**
	 * Add in the DFA
	 * @param index : of the current state where this will be add
	 * @param finite : if it's finite == 1 else 0
	 * @param letter : transition's name
	 * @param child : target state of the transition
	 */
	private void addDFA(int index, boolean finite, int letter, int child) {
		HashMap<String, Integer> map;		
		//Initialize new state and if it's a finite state
		if(index == dfa.size() && child == -1) {
			map = new HashMap<String, Integer>();
			if(finite) 
				map.put("finite",1);
			else 
				map.put("finite",0);
			dfa.add(map);
		
		//Add transaction into a state that already exist
		}else {
			map = dfa.get(index);
			map.put(Character.toString((char)letter), child);
			dfa.remove(index);
			dfa.add(index, map);
		}	
	}

	/**
	 * Print the deterministic finite automaton
	 */
	@SuppressWarnings("unused")
	private void showDFA() {
		System.out.println("------- DETERMINISTIC FINITE AUTOMATON --------");
		int index=0;
		for(HashMap<String, Integer> map : dfa) {
			System.out.print(index+" -> ");
			for(String key: map.keySet()) {
				System.out.print(key+" : "+map.get(key)+"     ");
			}
			System.out.println("");
			index++;
		}
		System.out.println("");
	}
	
	
	
	//-----------------------------------------------------------------------
	//------------------    STEP 4 : DFA -> Minimization    -------------------
	//-----------------------------------------------------------------------
	
	/**
	 * Minimization of the DFA
	 */
	private void minimization() {
		HashMap<Integer, Integer> cache = new HashMap<Integer, Integer>();
		List<Integer> states = IntStream.range(0,dfa.size())
				.boxed().collect(Collectors.toList());
		ArrayList<Integer> rest;
		int state1, state2, index = 0, target;
		
		while(states.size() > 1) {
			//System.out.println("tour : "+states.size()+"  ");
			rest = new ArrayList<Integer>();
			state1 = states.get(0);
			for(int i = 1; i<states.size(); i++) {
				//System.out.print(state1+" equ ");
				state2 = states.get(i);
				//System.out.print(state2+" ? ");
				if(equivalent(dfa.get(state1), dfa.get(state2))) {
					cache.put(state2, state1);
					//System.out.println(" oui ");
				}else {
					rest.add(state2);
					//System.out.println(" non ");
				}
			}
			states = rest;
			//System.out.println("");
		}
		
		//No minimization
		if(cache.keySet().isEmpty()) {
			for(HashMap<String,Integer> state: dfa) {
				minDfa.put(index, state);
				index++;
			}
		}else {
			for(HashMap<String,Integer> state: dfa) {
				//if the state is in the cache then it's been reduce
				if(!cache.containsKey(index)) {
					//Redirect states 
					for(String letter: state.keySet()) {
						target = state.get(letter);
						if(cache.containsKey(target)) { //Replace
							state.put(letter,cache.get(target));
						}
					}
					minDfa.put(index, state);
				}
				index++;
			}
		}
	}
	
	/**
	 * Check if the two state are equivalent or not
	 * @param state1
	 * @param state2
	 * @return true if it's equivalent (same transaction and both finite or not finite)
	 */
	private boolean equivalent (HashMap<String,Integer> state1, HashMap<String,Integer> state2) {
		if(state1.get("finite") != state2.get("finite") || state1.keySet().size() != state2.keySet().size())
			return false;
		//Same number of transition 
		for(String letter : state1.keySet()) {
			if(!state2.containsKey(letter)) {
				return false;
			}else if(state1.get(letter) != state2.get(letter)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Print the miniDFA
	 */
	@SuppressWarnings("unused")
	private void showMini() {
		System.out.println("------- MINI DETERMINISTIC FINITE AUTOMATON --------");
		HashMap<String,Integer> vals;
		for(Integer state: minDfa.keySet()) {
			System.out.print(state+" -> ");
			vals = minDfa.get(state);
			for(String key: vals.keySet()) {
				System.out.print(key+" : "+vals.get(key)+"     ");
			}
			System.out.println("");
		}
		System.out.println(" ");
	}
	
	public HashMap<Integer, HashMap<String,Integer>> getMin(){
		return minDfa;
	}
}
