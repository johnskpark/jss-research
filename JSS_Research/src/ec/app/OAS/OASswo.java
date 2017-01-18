/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;


import ec.app.QCSP.Core.IndexMaxPQ;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author nguyensu
 */
public class OASswo {
    public int n;
    double[] r,p,due,d_,e,w,si,pen;
    double[][] s;
    double p_;
    double s_;
    Random rnd;
    public int[] bestOrder; public double bestFINAL = Double.NEGATIVE_INFINITY;
    public OASswo(int seed){
        rnd = new Random(seed);
    }
    public static void main(String[] args) throws IOException {
        int N = 10; int Tao = 1; int R = 1; int instance = 1;
        OASswo oas = new OASswo(156);
        oas.readBenchmark("/"+N+"orders/Tao"+Tao+"/R"+R+"/Dataslack_"+N+"orders_Tao"+Tao+"R"+R+"_"+instance+".txt");
        double best = oas.localsearch();
        System.out.println("Done!!! with best objective = " + best);
    }
    public double localsearch(){
        double best = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 10000; i++) {
            double obj = generateSchedule();
            if (best < obj) best = obj;
            System.out.println(obj);
        }
        return best;
    }
    public double generateSchedule(){
        double ready = 0;
        double revenue = 0;
        double bestRevenue = Double.NEGATIVE_INFINITY;
        int prev = -1;
        IndexMaxPQ<Double> jobs = new IndexMaxPQ<Double>(n);
        double[] rev = new double[n];
        for (int i = 0; i < pen.length;i++) {
            jobs.insert(i, pen[i]);
        }
        for (int i:jobs){
            /*
            double setup = si[i];
            if (prev != -1) setup = s[prev][i];
            if (ready + setup + p[i] > d_[i]) {
                continue;
            }
             //*/
            if (r[i]>ready) ready = r[i];
            if (prev == -1) ready += si[i];
            else ready += s[prev][i];
            ready += p[i];
            revenue += e[i] - w[i]*maxPlus(ready-due[i]);
            rev[i] = e[i] - w[i]*maxPlus(ready-due[i]);
            if (bestRevenue < revenue) bestRevenue = revenue;
            if (ready>d_[i]) pen[i] += rev[i];
            prev = i;
        }
        if (bestFINAL > bestRevenue) bestFINAL = bestRevenue;
        return bestRevenue;
    }
    public double maxPlus (double x) {
        if (x>0) return x;
        return 0;
    }
    public int readBenchmark(String problemFile) throws IOException {
        //problemFile = "/benchmark/"+problemFile;
        InputStream _read = getClass().getResourceAsStream(problemFile);//new FileReader(problemFile);
        //BufferedReader buffread = new BufferedReader(read);
        InputStreamReader _inputFileReader = new InputStreamReader(_read);
        BufferedReader _buffread   = new BufferedReader(_inputFileReader);
        String line = _buffread.readLine();
        StringTokenizer str= new StringTokenizer(line,",");
        n = str.countTokens() - 2;
        r = new double[n]; p = new double[n]; due = new double[n]; d_ = new double[n]; e = new double[n]; w = new double[n]; s = new double[n][n]; si = new double[n];
        pen = new double[n];
        str.nextToken();
        for (int i = 0; i < n; i++) {
            r[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            p[i] = Integer.parseInt(str.nextToken());
            p_ += p[i];
        }
        p_ = p_ / (double) n;
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            due[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            d_[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            e[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            w[i] = Double.parseDouble(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        int counts = 0;
        for (int i = 0; i < n; i++) {
            si[i] = Double.parseDouble(str.nextToken());
            s_ += si[i]; counts++;
        }
        for (int i = 0; i < n; i++) {
            line = _buffread.readLine(); str= new StringTokenizer(line,",");
            str.nextToken();
            for (int j = 0; j < n; j++) {
                s[i][j] = Double.parseDouble(str.nextToken());
                s_ += s[i][j]; counts++;
            }
        }
        s_ = s_ / (double) counts;
        bestOrder = new int[n];
        return 1;
    }
    public boolean[] getSolution(int size){
        boolean[] a = new boolean[size];
        // insert integers 0..N-1
        for (int i = 0; i < size; i++)
            a[i] = rnd.nextBoolean();
        return a;
    }
    public static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    public static double min(double a, double b){
        if (a<b) return a;
        else return b;
    }
    public static double div(double a, double b){
        if (b!=0)
            return a/b;
        else
            return 1;
    }
    public static double IF(double a, double b, double c){
        if (a>=0){
            return b;
        }else{
            return c;
        }
    }
}
