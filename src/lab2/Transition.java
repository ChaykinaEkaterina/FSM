package lab2;

public class Transition {
	
	public int current;
	public char symbol;
	public boolean isFinal;
	public int next;
	
	public Transition(int cur, char sym, boolean fin, int next) {
		this.current = cur;
		this.symbol = sym;
		this.isFinal = fin;
		this.next = next; 
	}
}
