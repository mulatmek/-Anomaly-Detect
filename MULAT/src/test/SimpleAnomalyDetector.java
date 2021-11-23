package test;

import java.util.*;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {


	ArrayList<CorrelatedFeatures> l_cor;

	@Override
	public void learnNormal(TimeSeries ts) {
		CorrelatedFeatures cor;
		Line l;
		Vector<Float> person_vec=new Vector<>();
		Vector<Point> pointVector=new Vector<>();
		l_cor =new ArrayList<>();
		Point point_Array [];
		String first,second;
		float max=0;
		float Pearson_correlation;
		for(int i =0;i<ts.calVal.size();i++){
			for(int j =0;j<ts.calVal.size();j++) {
				if (i == j||j<i)
					continue;
				 Pearson_correlation = Math.abs(StatLib.pearson(ts.convert_vec_to_array(ts.calVal.get(i)), ts.convert_vec_to_array(ts.calVal.get(j ))));
				 if(Pearson_correlation>0.9) {
					 person_vec.add(Pearson_correlation);
					 Point temp = new Point(i, j);
					 pointVector.add(temp);
				 }
			}
		}


		for(int i =0;i<pointVector.size();i++){
			point_Array=new Point[ts.rowCounter-1];
			//####	point_Array[i]=pointVector.get(i);
			char ch=(char) (pointVector.get(i).x+65);
			first =String.valueOf(ch);
			ch=(char) (pointVector.get(i).y+65);
			second =String.valueOf(ch);
			for(int j=0 ;j<ts.rowCounter-1;j++){
				Point a =new Point(ts.get_index(first,j),ts.get_index(second,j));
				point_Array[j]=a;
			}
			l= StatLib.linear_reg(point_Array);
			for(int k=0 ;k<point_Array.length;k++){
				 max =StatLib.dev(point_Array[0],l);
				if (StatLib.dev(point_Array[k],l)>max)
					max=StatLib.dev(point_Array[k],l);
			}
			max*=1.1;
			cor=new CorrelatedFeatures(first,second,person_vec.get(i),l,max);
			l_cor.add(cor);


		}
	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		ArrayList<AnomalyReport> v=new ArrayList<>();

		for(CorrelatedFeatures c : l_cor) {
			ArrayList<float []> x=new ArrayList<>();
			x.add(ts.convert_vec_to_array(ts.get_column(c.feature1)));
			ArrayList<float []> y=new ArrayList<>();
			y.add(ts.convert_vec_to_array(ts.get_column(c.feature1)));
			for(int i=0;i<x.get(0).length;i++){
				if(Math.abs(y.get(0)[i] - c.lin_reg.f( x.get(0) [i])) >c.threshold)
				{
					String d=c.feature1 + "-" + c.feature2;
					v.add(new AnomalyReport(d,(i+1)));
				}
			}
		}
		return v;
	}

	
	public List<CorrelatedFeatures> getNormalModel(){
		return l_cor;
	}
}
