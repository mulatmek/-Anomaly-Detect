package test;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.Vector;

public class TimeSeries {
	Map <String,Vector<Float> > my_map=new HashMap<>();
	Map <Vector<Float>,String > my_second_map=new HashMap<>();
	String line ="";
	String header []=null;
	String temp[]=null;
	Vector<Vector<Float>> calVal;
	Vector<Float> temp_vec;
	int numOf_vec;
	int rowCounter=0;

	public TimeSeries(String csvFileName) {



		try {
			BufferedReader lineRead =new BufferedReader(new FileReader(csvFileName));
			calVal = new Vector<>();
			while ((line =lineRead.readLine())!=null) {
				if(rowCounter==0) {
					header = line.split(",");
					numOf_vec =header.length;
				}

				else{
					temp=line.split(",");
					for (int i=0;i<temp.length;i++) {
						if(calVal.size()!=temp.length) {
							temp_vec = new Vector<>();
							float f = Float.parseFloat(temp[i]);
							temp_vec.add(f);
							calVal.add(temp_vec);
						}
						else{
							float f = Float.parseFloat(temp[i]);
							calVal.get(i).add(f);
						}

					}

				}
				rowCounter++;
				if(rowCounter== numOf_vec -1){
				for (int i=0;i<temp.length;i++) {
					my_map.put(header[i], calVal.get(i));
				}
				}
			}

		}catch (IOException e){
		}

	}

 	public Vector<Float> get_column(String key){
		return my_map.get(key);
 }
	public Point[] convert_columns_to_point_array(String first,String second,Point [] point_array){
		for(int j=0 ;j<rowCounter-1;j++){
			Point a =new Point(this.get_index(first,j),this.get_index(second,j));
			point_array[j]=a;
		}
		return point_array;
	}
	public float get_index(String key,int index){
		return my_map.get(key).get(index);
	}
	public float [] convert_vec_to_array(Vector<Float> vec){
		float [] temp=new float[vec.size()];
		for (int i=0;i<vec.size();i++){
			temp[i]=vec.get(i);
		}
		return temp;
	}

}
