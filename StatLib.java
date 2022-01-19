//319330080 mulat_mekonen
package test;



public class StatLib {



    // simple average
    public static float avg(float[] x){
        float sum=0;
        for(int i=0;i<x.length;i++)
            sum+=x[i];
        return sum/x.length;
    }

    // returns the variance of X and Y
    public static float var(float[] x){
        float avg=avg(x);
        float sum=0;
        for(int i=0;i<x.length;i++){
            sum+=x[i]*x[i];
        }
        return (sum/x.length )-(avg*avg) ;
    }

    // returns the covariance of X and Y
    public static float cov(float[] x, float[] y){
        float sum=0;
        for(int i=0;i<x.length;i++){
            sum+=x[i]*y[i];
        }
        return (sum/x.length)- (avg(x)*avg(y));
    }


    // returns the Pearson correlation coefficient of X and Y
    public static float pearson(float[] x, float[] y){
        return (float)(cov(x,y)/Math.sqrt(var(x)*var(y)));
    }

    // performs a linear regression and returns the line equation
    public static Line linear_reg(Point[] points){
        float x[]=new float[points.length];
        float y[]=new float[points.length];
        for(int i=0;i<points.length;i++){
            x[i]=points[i].x;
            y[i]=points[i].y;
        }
        float a=cov(x,y)/var(x);
        float b=avg(y) - a*(avg(x));

        return new Line(a,b);
    }

    // returns the deviation between point p and the line equation of the points
    public static float dev(Point p,Point[] points){
        Line line=linear_reg(points);
        return dev(p,line);
    }

    // returns the deviation between point p and the line
    public static float dev(Point p,Line l){
             float dev =   Math.abs(p.y-l.f(p.x));
             return dev;
    }

}
