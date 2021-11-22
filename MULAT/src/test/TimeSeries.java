package test;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
public class TimeSeries {
	Map <String,Vector<Float> > my_map=new HashMap<>();
	String line ="";
	String header []=null;
	String temp[]=null;
	Vector<Vector<Float>> calVal;
	Vector<Float> temp_vec;
	
	public TimeSeries(String csvFileName) {


		int rowCounter=0;
		try {
			BufferedReader lineRead =new BufferedReader(new FileReader(csvFileName));
			calVal = new Vector<>();
			while ((line =lineRead.readLine())!=null) {
				if(rowCounter==0) {
					header = line.split(",");
				}




				if(rowCounter>0){
					temp=line.split(",");
					temp_vec=new Vector<>();
					for (int i=0;i<temp.length;i++) {

						float f = Float.parseFloat(temp[i]);
						temp_vec.add(f);
					}
					calVal.add(temp_vec);
				}
				rowCounter++;
				if(rowCounter==4){
				for (int i=0;i<3;i++) {
					my_map.put(header[i], calVal.get(i));
				}
				}
			}





		}catch (IOException e){
		}

	}

 public Vector<Float> return_col(String key){
		return my_map.get(key);
 }
	
}
