package lab2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FSM {
	/*
	 * TODO: 2. fix transitions search in hasHangs function 
	 * 3. fix transitions search in checkLine function
	 * 4. determinized to non-determinized function 
	 * 5. apply OOP stuff
	 */
	
	public static Transition checkTransition(String line) {
		if (!line.isEmpty()) {
			if (line.charAt(0) == 'q') {

				// System.out.println(line);
				if (line.indexOf(',') == -1) {
					System.out.println("Missing comma sign");
					return null;
				}
				

				int cur = 0;
				char sym = ' ';
				boolean isFinal = false;
				int next = 0;

				try {
					cur = Integer.parseInt(line.substring(1, line.indexOf(',')));
				} catch (NumberFormatException e) {
					System.out.println("State number should be integer");
					return null;
				}

				if (line.charAt(line.indexOf(',') + 2) != '=') {
					System.out.println("Missing equal sign");
					return null;
				}

				sym = line.substring(line.indexOf(',') + 1, line.indexOf(',') + 2).charAt(0);

				if (line.charAt(line.indexOf(',') + 3) != 'q' && line.charAt(line.indexOf(',') + 3) != 'f') {
					System.out.println("Next state must start with q or f");
					return null;
				}

				if (line.charAt(line.indexOf(',') + 3) == 'q')
					isFinal = false;
				else if (line.charAt(line.indexOf(',') + 3) == 'f')
					isFinal = true;

				try {
					next = Integer.parseInt(line.substring(line.indexOf(',') + 4, line.length()));
				} catch (NumberFormatException e) {
					System.out.println("Number of the next state must be integer: "
							+ line.substring(line.indexOf(',') + 4, line.length()));
					return null;
				} catch (IndexOutOfBoundsException e1) {
					System.out.println("Failed reading number of the next state");
					return null;
				}

				return new Transition(cur, sym, isFinal, next);

			} else {
				System.out.println("Line must start with q");
				return null;
			}
		}

		else {
			System.out.println("Line must not be empty");
			return null;
		}
	}

	public static boolean isDeterministic(List<Transition> transitions) {
		for (int i = 1; i < transitions.size(); i++) {
			if (transitions.get(i - 1).current == transitions.get(i).current
					&& transitions.get(i - 1).symbol == transitions.get(i).symbol)
				return false;
		}
		return true;
	}

	public static boolean hasHangs(List<Transition> transitions, List<Integer> initialStates) {

		for (int i = 0; i < transitions.size(); i++) {
			if (!transitions.get(i).isFinal) {
				boolean connected = false;
				
				if (initialStates.contains(transitions.get(i).next)) 
					connected = true;
				
				//System.out.println(transitions.get(i).current+""+ transitions.get(i).symbol+""+transitions.get(i).next+" "+initialStates.contains(transitions.get(i).next));

				if (!connected)
					return true;
			}
		}

		return false;
	}

	public static boolean checkLine(List<Transition> transitions, String line) {

		int currentState = 0;

		for (int i = 0; i < line.length(); i++) {
			int index = -1;

			for (int j = 0; j < transitions.size(); j++)
				if (transitions.get(j).current == currentState && transitions.get(j).symbol == line.charAt(i)) {
					index = j;
					break;
				}

			if (index < 0)
				return false;
			if (transitions.get(index).isFinal && i == line.length() - 1)
				return true;
			currentState = transitions.get(index).next;
		}
		return false;
	}
	
	public static Set<Integer> findNext(List<Integer> current, char a, List<Transition> transitions, List<Integer> initialStates){
		Set <Integer> next = new HashSet<Integer>();
		
		for (int i =0; i< current.size(); i++) {
			int start = initialStates.indexOf(current.get(i));
			while (transitions.get(start).current == current.get(i)) {
				if (transitions.get(start).symbol == a)
					{
					next.add(transitions.get(start).next);
					}
			start++;			
			}
			//System.out.println(next);
		}
		return next;
	}
	
	public static List<Transition> Determinize(List<Integer> current, List<Transition> transitions, List<Integer> initialStates, Set<Character> alphabet) {
		//List <Integer> current = new ArrayList<>();
		//current.add(0);
		List<Transition> newTransitions = new ArrayList<>();
		boolean isAddedNew = true;
		List<Integer> newNext = new ArrayList<>();
		
		//while (isAddedNew) {
			
			for (int i = 0; i < alphabet.size(); i++) {
				if (!current.containsAll(findNext(current, (char) alphabet.toArray()[i], transitions, initialStates)))
				current.addAll(findNext(current, (char) alphabet.toArray()[i], transitions, initialStates));
				//if (!newNextString.isEmpty()) newTransitions.add(new Transition(transitions.get(index).current, transitions.get(index).symbol, isFinal, Integer.parseInt(newNextString)));
				System.out.println(current);
				if (current.size()!=0) Determinize(current,transitions,initialStates,alphabet);
			}
			
			//System.out.println(current);
		//}
		return newTransitions;
	}

	public static void main(String[] args) {
		File file = new File("test2.txt");
		List<Transition> transitions = new ArrayList<>();

		Set<Character> alphabet = new HashSet<Character>();
		List<Integer> initialStates = new ArrayList<Integer>();
		List<Integer> nextStates = new ArrayList<Integer>();

		try {
			BufferedReader machine = new BufferedReader(new FileReader(file));
			String line;
			while ((line = machine.readLine()) != null) {
				// System.out.println(line);
				Transition tempTransition = checkTransition(line);
				if (tempTransition != null) {
					transitions.add(tempTransition);
					alphabet.add(tempTransition.symbol);
					initialStates.add(tempTransition.current);
					nextStates.add(tempTransition.next);
				} else {
					System.out.println("Cannot process the line");
					machine.close();
					return;
				}
			}
			machine.close();
			
			if (transitions.isEmpty()) {
				System.out.println("Automat is empty");
				return;
			}

			List<Transition> sortedTransitions = new ArrayList<>(transitions);
			sortedTransitions.sort(new Comparator<Transition>() {
				public int compare(Transition t1, Transition t2) {
					return ((Integer) t1.current).compareTo((Integer) t2.current);
				}
			});

			System.out.println("Sorted transitions");
			for (int i = 0; i < sortedTransitions.size(); i++)
				System.out.println("" + sortedTransitions.get(i).current + sortedTransitions.get(i).symbol
						+ sortedTransitions.get(i).next);

			File mess = new File("message.txt");
			BufferedReader message = new BufferedReader(new FileReader(mess));
			line = message.readLine();

			// Checking if all symbols in given line are valid (are in the alphabet)
			for (int i = 0; i < line.length(); i++) {
				if (!alphabet.contains(line.charAt(i))) {
					System.out.println("Incorrect transition symbol: " + line.charAt(i));
					message.close();
					return;
				}
			}
			System.out.println("Success: all symbols in given message are valid");
			message.close();

			if (isDeterministic(sortedTransitions))
				System.out.println("Automat is determinized");
			else
				System.out.println("Automat is not determinized");

			if (!hasHangs(sortedTransitions, initialStates))
				System.out.println("Automat has no hangs");
			else
				System.out.println("Automat has hangs");

			if (checkLine(sortedTransitions, line))
				System.out.println("Line can be processed");
			else
				System.out.println("Failed processing the line");
			
			//List <Integer> current = new ArrayList<>();
			//current.add(0);
			
			//List<Transition> newTransitions = Determinize(current, sortedTransitions, initialStates, alphabet);
			//for (int i = 0; i < newTransitions.size(); i++) {
			//	System.out.println("" + newTransitions.get(i).current + newTransitions.get(i).symbol
			//			+ newTransitions.get(i).next);
				
			//}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
