

import java.util.*;
import java.io.*;

public class ReadFile{
	private ArrayList <String> lines;

	public ReadFile(String fileName){
		lines = new ArrayList <String> ();
		this.readFile(fileName);
	}
	private void readFile(String fileName){
		try{
			FileReader reader = new FileReader(fileName);
	    	BufferedReader br = new BufferedReader(reader);
		    try {
		    	String line = br.readLine();
		    	while(line != null){
		    		lines.add(line);
			        line = br.readLine();
			    }
			    br.close();
		    }catch(IOException exp){
		    	System.out.println("I can not read this file!");
		    }
	    }catch(FileNotFoundException er){
	    	System.out.println("I can not find this file!");
	    }
	}
	public ArrayList <String> get_lines(){
		return lines;
	}
}
