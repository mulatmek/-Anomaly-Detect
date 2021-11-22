package test;

public class main {
    public static void main(String[] args) {
        TimeSeries t =new TimeSeries("C:\\Users\\Mulat\\Desktop\\mukim\\a.csv");
        System.out.println(t.get_column("c"));
        System.out.println(t.get_index("c",5));
    }



}
