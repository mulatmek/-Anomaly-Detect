package test;

import java.util.*;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	public SimpleAnomalyDetector(){
		this.th =0.9f;
		this.l_cor =new ArrayList<>();
	}
	ArrayList<CorrelatedFeatures> l_cor;
	public  float th;

	public float getTh() {
		return th;
	}

	public void setTh(float th) {
		this.th = th;
	}

	@Override
	public void learnNormal(TimeSeries ts) {
		CorrelatedFeatures cor;
		Line l;
		Point point_Array [];
		String first,second;
		float max=0;
		Vector<Float> person_vec=new Vector<>();
		Vector<Point> pointVector=new Vector<>();
		l_cor =new ArrayList<>();
		float Pearson_correlation;
		float[] x,y;
		for(int i =0;i<ts.calVal.size();i++){
			for(int j =0;j<ts.calVal.size();j++) {
				if (i == j||j<i)
					continue;
				 x=ts.convert_vec_to_array(ts.calVal.get(i));
				 y=ts.convert_vec_to_array(ts.calVal.get(j));
				 Pearson_correlation = Math.abs(StatLib.pearson(x, y));
				 if(Pearson_correlation>getTh()) {
					 person_vec.add(Pearson_correlation);
					 Point temp = new Point(i, j);
					 pointVector.add(temp);
				 }
			}
		}
		for(int i =0;i<pointVector.size();i++){
				Point p= pointVector.get(i);
				point_Array=new Point[ts.rowCounter-1];
				char ch=(char) (p.x+65);
				first =String.valueOf(ch);
				ch=(char) (p.y+65);
				second =String.valueOf(ch);
				point_Array=ts.convert_columns_to_point_array(first,second,point_Array);
				l= StatLib.linear_reg(point_Array);
				max =StatLib.dev(point_Array[0],l);

				for(int k=0 ;k<point_Array.length;k++){
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
		ArrayList<AnomalyReport> al =new ArrayList<>();
		String desc;
		float [] x;
		float [] y;
		for(CorrelatedFeatures cor:l_cor) {
			x=ts.convert_vec_to_array(ts.get_column(cor.feature1));
			y=ts.convert_vec_to_array(ts.get_column(cor.feature2));

			for (int i=0;i< x.length;i++) {
				if (Math.abs(y[i] - cor.lin_reg.f(x[i])) > cor.threshold) {
					desc = cor.feature1 + "-" + cor.feature2;
					al.add(new AnomalyReport(desc, i + 1));
				}
			}
		}
				return  al;
	}
	public List<CorrelatedFeatures> getNormalModel(){
		return l_cor;
	}
}
