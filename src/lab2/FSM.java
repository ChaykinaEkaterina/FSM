package lab2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FSM {
	/* TODO: 
	 * 1. transitions validation 
	 * 2. hangs check
	 * 3. fix transitions search in checkLine functions 
	 * 4. determinized to non-determinized function 
	 * 5. apply OOP stuff
	 */
	
	public static Transition checkTransition(String line) {
		if (!line.isEmpty()) {
			if (line.charAt(0) == 'q') {
				if (line.indexOf(',') == -1 || line.indexOf('=') == -1) {
					System.out.println("Missing comma or equal sign");
					return null;
				}
				
				int cur=0;
				char sym = ' ';
				boolean isFinal = false;
				int next = 0;
				
				try {
				cur = Integer.parseInt(line.substring(1, line.indexOf(',')));
				}
				catch (NumberFormatException e)
				{
					System.out.println("State number should be integer");
					return null;
				}
				
				if ( line.substring(line.indexOf(',')+1, line.indexOf('=')).length() != 1 && line.substring(line.indexOf(',')+1, line.indexOf('=')).length() != 0) {
					System.out.println("Transition symbol must be a single character");
					return null;
				}
				
				if (line.substring(line.indexOf(',')+1, line.indexOf('=')).length() == 0) sym = Character.MIN_VALUE;
				else sym = line.substring(line.indexOf(',')+1, line.indexOf('=')).charAt(0);
				
				if (line.charAt(line.indexOf('=')+1) != 'q' && line.charAt(line.indexOf('=')+1) != 'f') {
					System.out.println("Next state must start with q or f");
					return null;
				}
				
                if (line.charAt(line.indexOf('=')+1) == 'q') isFinal = false;
                else if (line.charAt(line.indexOf('=')+1) == 'f') isFinal = true;
                
                try {
                	next = Integer.parseInt(line.substring(line.indexOf('=')+2, line.length()));
                }
                catch (NumberFormatException e) {
                	System.out.println("Number of the next state must be integer: "+ line.substring(line.indexOf('=')+2, line.length()));
                	return null;
                }
                catch (IndexOutOfBoundsException e1) {
                	System.out.println("Failed reading number of the next state");
                	return null;
                }
                
                return new Transition(cur, sym, isFinal, next);    
				
				
			}
			else {
				System.out.println("Line must start with q");
				return null;
			}
		}
		
		else {
			System.out.println("Line must not be empty");
			return null;
		}
	}
	
	public static boolean isDeterminized(List<Transition> transitions) {
		for (int i = 1; i < transitions.size(); i++) {
			if ( transitions.get(i-1).current == transitions.get(i).current && transitions.get(i-1).symbol == transitions.get(i).symbol ) 
				return false;
		}
		return true;
	}
	
	
	public static boolean checkLine(List<Transition> transitions, String line) {
		
		int currentState = 0;
		
		for (int i = 0; i < line.length(); i++) {
			int index = -1;
			//System.out.println(line.charAt(i));
			
			//TODO: fix transition search 
			for (int j = 0; j < transitions.size(); j++)
				if (transitions.get(j).current == currentState && transitions.get(j).symbol == line.charAt(i)) {
					index = j;
					break;
				}
			
			//System.out.println(""+transitions.get(index).current + transitions.get(index).symbol+ transitions.get(index).next);
			if (index<0) return false;
			if (transitions.get(index).isFinal && i == line.length()-1) return true;
			currentState = transitions.get(index).next;
		}
		return false;
	}
	

	public static void main(String[] args) {
		File file = new File("test2.txt");
		List<Transition> transitions = new ArrayList<>();
		
		Set<Character> alphabet = new HashSet<Character>();
		//List<Character> alphabet = new ArrayList<>();
		 
        try 
        {
        	BufferedReader machine = new BufferedReader(new FileReader(file));
            String line;
            while ((line = machine.readLine()) != null) {
                System.out.println(line);
                
                Transition tempTransition = checkTransition(line);
                if (tempTransition!=null) 
                	{
                	transitions.add(tempTransition);
                	alphabet.add(tempTransition.symbol);
                	}
                else {
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
            
            for (int i = 0; i < transitions.size(); i++)
                System.out.println(""+transitions.get(i).current + transitions.get(i).symbol + transitions.get(i).next);
            
            System.out.println();
            
            
    		List<Transition> sortedTransitions = new ArrayList<>(transitions);
    		sortedTransitions.sort(new Comparator<Transition>() {
                public int compare(Transition t1, Transition t2) {
                    // Intentional: Reverse order for this demo
                    return ((Integer)t1.current).compareTo((Integer)t2.current);
                }
            });
            
            for (int i = 0; i < sortedTransitions.size(); i++)
                System.out.println(""+sortedTransitions.get(i).current + sortedTransitions.get(i).symbol + sortedTransitions.get(i).next);
            
            //for (int i = 0; i < alphabet.size(); i++)
            //    System.out.println(alphabet.toArray()[i]);

            
            File mess = new File("message.txt");
            BufferedReader message = new BufferedReader(new FileReader(mess));
            line = message.readLine();
            
            
            //Checking if all symbols in given line are valid (are in the alphabet)
            for (int i = 0 ; i < line.length(); i++) {
            	if (!alphabet.contains(line.charAt(i))) {
            		System.out.println("Incorrect transition symbol: "+ line.charAt(i));
            		message.close();
            		return;
            	}
            }
            System.out.println("Success: all symbols in given message are valid");
            message.close();
            
            if (isDeterminized(sortedTransitions)) System.out.println("Automat is determinized");
            else System.out.println("Automat is not determinized");
            
            if (checkLine(sortedTransitions, line)) System.out.println("Line can be processed");
            else System.out.println("Failed processing the line");
            
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

}
