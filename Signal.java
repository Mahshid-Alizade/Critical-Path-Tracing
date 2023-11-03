import java.util.ArrayList;

public class Signal {

	int value;//0 or 1
	int number;//Name of each signal
	String type;//"input" "output" "middle"
	boolean sensitive;//Is sensitive or not?
	boolean critical;//Is critical or not?
	ArrayList<Signal> children = new ArrayList<Signal>();//Inputs of a gate
	ArrayList<Signal> parent = new ArrayList<Signal>();//Output of a gate
	ArrayList<Signal> sensitiveParent = new ArrayList<Signal>();//List of sensitive parent
	ArrayList<Signal> fanoutParent = new ArrayList<Signal>();//List of Critical parent
	int parentCount = 0;
	
	public Signal(int number, String type) {
		this.number = number;
		this.type = type;
		this.sensitive = false;
		this.critical = false;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return(this.value) ;
	}
	
}
