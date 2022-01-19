package test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Commands {
	public ArrayList<Command> commands =new ArrayList<>();
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);
		int x= 80;

		public default TimeSeries uploadFile()  {
			String line;
			try {
				PrintWriter out = new PrintWriter(new FileWriter("f.csv"));
				Scanner in = new Scanner(new FileReader("f.csv"));
				while ((line = this.readText()).compareTo("done") != 0) {
					out.write(line);
					out.write("\n");
				}
				out.close();
			}
			catch (IOException e){
				e.printStackTrace();
			}
			TimeSeries ts = new TimeSeries("f.csv");
			return ts;
		}
	}

	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}

	// you may add other helper classes here


	// the shared state of all commands
	private class SharedState{
		// implement here whatever you need
		SimpleAnomalyDetector sad;
		TimeSeries train_csv;
		TimeSeries test_csv;
		List<AnomalyReport> anomalies;
		//CLI menu;

		public SharedState() {
			sad = new SimpleAnomalyDetector();
			train_csv = new TimeSeries();
			test_csv = new TimeSeries();
			anomalies = new ArrayList<>();
		}

	}

	private  SharedState sharedState=new SharedState();


	// Command abstract class
	public abstract class Command{
		protected String description;

		public Command(String description) {
			this.description=description;
		}

		public abstract void execute() throws IOException;
	}

	// Command class for example:
	public class ExampleCommand extends Command{

		public ExampleCommand() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}
	}




	// implement here all other commands
	public class FIRST extends Command{

		public FIRST() {
			super("upload a time series csv file");
		}

		@Override
		public void execute() throws IOException {
			dio.write("Please upload your local train CSV file.\n");
			sharedState.train_csv= dio.uploadFile();
		//	System.out.println(sharedState.train_csv.get_column("A"));
			dio.write("Upload complete.\n");
			dio.write("Please upload your local test CSV file.\n");
			sharedState.test_csv= dio.uploadFile();
			dio.write("Upload complete.\n");
		}
	}
	public class SECOND extends Command{

		public SECOND() {
			super("algorithm settings");
		}

		@Override
		public void execute() {
			dio.write("The current correlation threshold is "+ sharedState.sad.getTh()+"\n");
			boolean flag = true;
			while(flag) {
				dio.write("Type a new threshold\n");
				String line = dio.readText();
				float input = Float.parseFloat(line);
			if(input>1 ||input <0)
				dio.write("please choose a value between 0 and 1.");
			else if(input>=0 && input<=1)
				flag = false;
				sharedState.sad.setTh(input);
			}

		}
	}

	public class THIRD extends Command{

		public THIRD() {
			super("detect anomalies");
		}

		@Override
		public void execute() {
			sharedState.sad.learnNormal(sharedState.train_csv);
			sharedState.anomalies = sharedState.sad.detect(sharedState.test_csv);
			dio.write("anomaly detection complete.\n");
		}
	}

	public class FOURTH extends Command{

		public FOURTH() {
			super("display results");
		}

		@Override
		public void execute() {
			for(AnomalyReport ar: sharedState.anomalies) {
				dio.write(ar.timeStep+"\t"+ar.description+"\n");
			}
			dio.write("Done.\n");
		}
	}

		//command 5;
		public class FIFTH extends Command{

			List<List<Long>> detected_anomalies;
			List<List<Long>> expected_anomalies;

			public FIFTH() {
				super("upload anomalies and analyze results");
				detected_anomalies = new ArrayList<List<Long>>();
				expected_anomalies = new ArrayList<List<Long>>();
				for(int i=0; i<2; i++) {
					detected_anomalies.add(new ArrayList<Long>());
					expected_anomalies.add(new ArrayList<Long>());
				}
			}


			@Override
			public void execute() {
				dio.write("Please upload your local anomalies file.\n");
				String line = dio.readText();

				while (line.compareTo("done") != 0 ) {
					int i = 0;
					for(String time_step: line.split(",")) {
						expected_anomalies.get(i).add(Long.parseLong(time_step));
						i++;
					}
					line = dio.readText();
				}

				int i = 0;
				while (i < sharedState.anomalies.size()) {
					int j=i;
					//System.out.println(sharedState.anomalies.get(i).timeStep+"\t"+sharedState.anomalies.get(i).description+"\n");
					String prev = sharedState.anomalies.get(i).description;
					//String current = sharedState.anomalies.get(j).description;
					while (j < sharedState.anomalies.size() && (sharedState.anomalies.get(j).description.compareTo(prev) == 0)) {
						j++;
					}
					detected_anomalies.get(0).add(sharedState.anomalies.get(i).timeStep);
					detected_anomalies.get(1).add(sharedState.anomalies.get(j-1).timeStep);
					i=j;
				}

				int sum = 0;
				for (i=0; i < expected_anomalies.get(0).size(); i++) {
					sum += (expected_anomalies.get(1).get(i) - expected_anomalies.get(0).get(i) + 1);
				}

				float N = sharedState.test_csv.getRowSize() - sum;
				float P = expected_anomalies.get(0).size();
				float FP=0;
				float TP=0;
				boolean is_expected;

				for (i=0; i < detected_anomalies.get(0).size(); i++) {
					Long detected_start = detected_anomalies.get(0).get(i);
					Long detected_end = detected_anomalies.get(1).get(i);
					//System.out.println("detected start:"+detected_start+" "+"end:"+detected_end);

					is_expected = false;

					for(int j=0; j<expected_anomalies.get(0).size(); j++) {
						Long expected_start = expected_anomalies.get(0).get(j);
						Long expected_end = expected_anomalies.get(1).get(j);
						//System.out.println("expected start:"+expected_start+" "+"end:"+expected_end);

						if((detected_start >= expected_start &&  detected_start <= expected_end) ||
								(detected_end >= expected_start &&  detected_end <= expected_end)||
								(detected_start <= expected_start &&  detected_end >= expected_end) ) {
							TP+=1;
							//System.out.println(TP);
							is_expected = true;
						}

					}

					if (!is_expected) {
						FP+=1;
						//System.out.println(FP);
					}

				}



				float f = FP/N;
				BigDecimal bd = new BigDecimal(f).setScale(3, RoundingMode.DOWN);
				f = bd.floatValue();

				float t = TP/P;
				BigDecimal bd1 = new BigDecimal(t).setScale(3, RoundingMode.DOWN);
				t = bd1.floatValue();

				dio.write("Upload complete.\n");
				dio.write("True Positive Rate: "+ t +"\n");
				dio.write("False Positive Rate: "+ f +"\n");

				for(i=0; i < 2; i++ ) {
					try {
						detected_anomalies.get(i).clear();
						expected_anomalies.get(i).clear();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}

		public class EXIT extends Command{

			public EXIT() {
				super("exit");
			}

			@Override
			public void execute() {
				dio.write("bye\n");
			}
		}


	}



