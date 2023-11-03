import java.awt.List;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files

public class main {
	
	
	static int inputsCount;
	static int outputsCounts;
	static ArrayList<Signal> inputs = new ArrayList<Signal>();
	static ArrayList<Integer> outputs = new ArrayList<Integer>();
	static ArrayList<Signal> allSignals = new ArrayList<Signal>();
	static Signal signal;
	static String testVector;
	static String fileName;
	
	public static void main(String[] args) {
		
		//get file
		Scanner sc= new Scanner(System.in);
		System.out.println("Enter the '.bench' file name (ex: c499)");
		fileName = sc.nextLine().trim();
		
		//read and parse the input file
		//set value of each signal
		//find and set sensitive signals
		readFile(fileName);
		
		//find and set critical signals
		findCriticalPath(outputs);
		
		//print output
		printStuckAtFaults();
		//printArray(allSignals);
		
	}
	
	//print critical path
	public static void printStuckAtFaults() {
		System.out.println("\nwith this test vector : " + testVector + "\n");
		for(int i=0 ; i<allSignals.size(); i++) {
			int value = 0;
			if(allSignals.get(i).value == 0)
				value = 1;
			else
				value = 0;
			
			//fanoutParent size is 0 when the signal is not Fan-out
			if(allSignals.get(i).critical && allSignals.get(i).fanoutParent.size() == 0)
				System.out.println("Stuck-at-"+ value + " in signal " + allSignals.get(i).number + ",");
			
			else if(allSignals.get(i).critical &&  allSignals.get(i).fanoutParent.size() > 0)
				for(int j=0; j<allSignals.get(i).fanoutParent.size() ; j++ )
					System.out.println("Stuck-at-"+ value + " in signal " + allSignals.get(i).number + " child of " + allSignals.get(i).fanoutParent.get(j).number + ",");
		}
		System.out.println("\nwill be discovered. ");
	}
	
	//find critical path
	public static void findCriticalPath(ArrayList<Integer> output) {
		for(int i=0 ; i<output.size() ; i++) {
			isCritical(getSignal(output.get(i)));
		}
	}
	
	//recursive function, check children are critical or not?
	public static void isCritical(Signal s) {
		for(int i=0 ; i< s.children.size() ; i++) {
			
			//Check if the child of this signal is sensitive
			if(s.children.get(i).sensitive && s.children.get(i).sensitiveParent.contains(s)) {
				
				//Fan-out signals
				if(s.children.get(i).parentCount > 1) {
					s.children.get(i).critical = true;
					//Remember which parent is critical
					s.children.get(i).fanoutParent.add(s);
					//Don't check Fan-out signals' children
					continue;
				}
				s.children.get(i).critical = true;
				isCritical(s.children.get(i));
			}
		}
	}
	
	//Set inputs value
	public static void setInputsValue(ArrayList<Signal> array,String testVector) {
		for(int i=0 ; i<array.size() ; i++) {
			array.get(i).setValue(Integer.parseInt(testVector.charAt(i)+""));
		}
	}
	
	//Print signal array in customized form
	public static void printArray(ArrayList<Signal> array ) {
		for(int i=0 ; i<array.size() ; i++ ) {
			System.out.println(array.get(i).number + " " + array.get(i).type + " " + array.get(i).value + " sensitive : " + array.get(i).sensitive + " critical : " + array.get(i).critical);
			
			if(array.get(i).parentCount > 0)
				for(int j=0 ; j < array.get(i).parentCount ; j++)
					System.out.println("parrent count " + array.get(i).parentCount + " parent : " + array.get(i).parent.get(j).number);
			
			if(array.get(i).fanoutParent.size() > 0)
				for(int j=0 ; j < array.get(i).fanoutParent.size() ; j++)
					System.out.println("critical parent : " + array.get(i).fanoutParent.get(j).number);
			
			for(int j=0 ; j<array.get(i).children.size() ; j++)
				System.out.println(array.get(i).children.get(j).number);
		}
	}
	
	//Generate test vector with the entry length
	public static String getTV(int Length){
		
	    String S = "";

	    for(int i = 0; i < Length; i++)
	    {
	        int x = (1 + (int)(Math.random() * 100)) % 2;
	        S = S + String.valueOf(x);
	    }
	 
	    return S;
	    //return("001000011");//test
	    //return("111");//test2
	    //return("1011");//test3
	    //return("100100010111000100111110010000101110");//c432
	    //return "10101011010010110101100010111000101010101";//c499
	}
	
	//Read and parse the input file
	public static void readFile(String fileName) {
		//input counter
		int i = 0;
		
		    try {
		      File myObj = new File("C:\\Users\\user\\eclipse-workspace\\Test Project\\src\\" + fileName + ".bench");
		      Scanner myReader = new Scanner(myObj);
		      
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        
		        //get inputs number
		        if(data.startsWith("#") && data.contains("inputs")) {
		        	inputsCount = Integer.parseInt(data.split(" ")[1].trim());
		        	
		        	//generate test vector
		    		testVector = getTV(inputsCount);
		        }
		        
		        //get outputs number
		        if(data.startsWith("#") && data.contains("outputs")) {
		        	outputsCounts = Integer.parseInt(data.split(" ")[1].trim());
		        }
		        
		        //get input signals and set value
		        if(data.startsWith("INPUT")) {
		        	signal = new Signal(Integer.parseInt(data.split("\\(")[1].split("\\)")[0].trim()), "input");
		        	signal.setValue(Integer.parseInt(testVector.charAt(i)+""));
		        	inputs.add(signal);
		        	allSignals.add(signal);
		        	i++;
		        }
		        
		        //get output signals
		        if(data.startsWith("OUTPUT")) {
		        	outputs.add(Integer.parseInt(data.split("\\(")[1].split("\\)")[0].trim()));
		        }
		        
		        //gates and middle signals
		        if(data.contains("=")) {
		        	
		        	int number = Integer.parseInt(data.split("=")[0].trim());
		        	
		        	if(outputs.contains(number)) {
		        		signal = new Signal(number,"output");
		        		signal.sensitive = true;
		        		signal.critical = true;
		        	}else {
		        		signal = new Signal(number,"middle");
		        	}
		        	String s = data.split("=")[1].trim();
		        	doOperation(signal, s);
		        	allSignals.add(signal);
		        }
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		}
	
	//Gets a number 
	//Returns the relevant signal object
	public static Signal getSignal(int number) {
			for(int i=0; i<allSignals.size(); i++) { 
				if(allSignals.get(i).number == number) {
					return allSignals.get(i);
				}
			}
			System.out.println(number +" not found in allsignal array" );
			return null;
		}

	//Does operations and sets value of each signal
	//Finds and sets sensitive signals
	public static void doOperation(Signal signal, String s) {
			
			//1 and 0 counter
			int zeroCounter = 0;
			ArrayList<Signal> zeroSignals = new ArrayList<Signal>();
			int oneCounter = 0;
			ArrayList<Signal> oneSignals = new ArrayList<Signal>();
			int inputs_number = 0;
		
			//operator type
			String operator = s.split("\\(")[0];
			
			//operands in string format
			String temp_operands[] = s.split("\\(")[1].split("\\)")[0].split(",");
			inputs_number = temp_operands.length;
			
			//operands in integer format
			Integer operands[] = new Integer[temp_operands.length];

			for(int i=0 ; i<temp_operands.length ; i++) {
				operands[i] = Integer.parseInt(temp_operands[i].trim());
				
				//add children of each node
				signal.children.add(getSignal(operands[i]));
				getSignal(operands[i]).parentCount ++;
				getSignal(operands[i]).parent.add(signal);
				if(getSignal(operands[i]).value == 0) {
					zeroCounter ++;
					zeroSignals.add(getSignal(operands[i]));
				}else {
					oneCounter ++;
					oneSignals.add(getSignal(operands[i]));
				}
			}
			
			int result = -1;
			if(operator.equals("AND")) {
				result = 1;
				for(int i=0; i<operands.length ; i++) {

					if(getSignal(operands[i]).value == 0) {
						result = 0;
						break;
				}}
				
				//sensitive or not?
				if(zeroCounter == 1) {
					zeroSignals.get(0).sensitive = true;
					zeroSignals.get(0).sensitiveParent.add(signal);
				}else if(oneCounter == inputs_number) {
					for(int j=0; j<oneSignals.size(); j++) {
						oneSignals.get(j).sensitive = true;
						oneSignals.get(j).sensitiveParent.add(signal);
					}
				}
			}else if(operator.equals("NAND")) {
				result = 0;
				for(int i=0; i<operands.length ; i++) {

					if(getSignal(operands[i]).value == 0) {
						result = 1;
						break;
				}}
				
				//sensitive or not?
				if(zeroCounter == 1) {
					zeroSignals.get(0).sensitive = true;
					zeroSignals.get(0).sensitiveParent.add(signal);
				}else if(oneCounter == inputs_number) {
					for(int j=0; j<oneSignals.size(); j++) {
						oneSignals.get(j).sensitive = true;
						oneSignals.get(j).sensitiveParent.add(signal);
					}
				}
			}else if(operator.equals("OR")) {
				result = 0;
				for(int i=0; i<operands.length ; i++) {

						if(getSignal(operands[i]).value == 1) {
						result = 1;
						break;
				}}
				
				//sensitive or not?
				if(oneCounter == 1) {
					oneSignals.get(0).sensitive = true;
					oneSignals.get(0).sensitiveParent.add(signal);
				}else if(zeroCounter == inputs_number) {
					for(int j=0; j<zeroSignals.size(); j++) {
						zeroSignals.get(j).sensitive = true;
						zeroSignals.get(j).sensitiveParent.add(signal);
					}
				}
			}else if(operator.equals("NOR")) {
				result = 1;
				for(int i=0; i<operands.length ; i++) {

						if(getSignal(operands[i]).value == 1) {
						result = 0;
						break;
				}}
				
				//sensitive or not?
				if(oneCounter == 1) {
					oneSignals.get(0).sensitive = true;
					oneSignals.get(0).sensitiveParent.add(signal);
				}else if(zeroCounter == inputs_number) {
					for(int j=0; j<zeroSignals.size(); j++) {
						zeroSignals.get(j).sensitive = true;
						zeroSignals.get(j).sensitiveParent.add(signal);
					}
				}
			}else if(operator.equals("XOR")) {
				for(int i=0; i<operands.length ; i++) {

					if(result == -1) {
						result = getSignal(operands[i]).value;
						
					}else if(result == 0) {
						if(getSignal(operands[i]).value == 0) {
							result = 0;
						}else {
							result = 1;
						}
					}else if(result == 1) {
						if(getSignal(operands[i]).value == 0) {
							result = 1;
						}else {
							result = 0;
						}
				}}
				
				//all inputs of XOR are sensitive
				for(int j=0; j<operands.length; j++) {
					getSignal(operands[j]).sensitive = true;
					getSignal(operands[j]).sensitiveParent.add(signal);
				}
			}else if(operator.equals("NOT")) {

				if(getSignal(operands[0]).value == 0) {
					result = 1;
				}else {
					result = 0;
				}
				
				//sensitive or not?
				getSignal(operands[0]).sensitive = true;
				getSignal(operands[0]).sensitiveParent.add(signal);
			}
			
			signal.setValue(result);
		}


}
