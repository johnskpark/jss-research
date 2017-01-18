package ec.app.OAS;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


/**
 *
 * @author nguyensu
 */

public class MOGPoasBenchmark {
private static int nObj = 2;
public static double[][][][][] Time;
    /**
     * @param args the command line arguments
     */
    public static double[][][][] upperBound = new double[6][5][5][10];
    public static double[][][][] Tabu = new double[6][5][5][10];
    public void getRefSolution() throws IOException{
        InputStream _read = getClass().getResourceAsStream("RefSol.txt");
        //BufferedReader buffread = new BufferedReader(read);
        InputStreamReader _inputFileReader = new InputStreamReader(_read);
        BufferedReader _buffread   = new BufferedReader(_inputFileReader);
        String line = _buffread.readLine(); _buffread.readLine();
        for (int i = 0; i < upperBound.length; i++) {
            for (int j = 0; j < upperBound[i].length; j++) {
                for (int k = 0; k < upperBound[i][j].length; k++) {
                    _buffread.readLine();
                    for (int l = 0; l < upperBound[i][j][k].length; l++) {
                        line = _buffread.readLine();
                        StringTokenizer str= new StringTokenizer(line,"\t ");
                        str.nextToken();
                        upperBound[i][j][k][l] = Double.parseDouble(str.nextToken());
                        Tabu[i][j][k][l] = Double.parseDouble(str.nextToken());
                    }
                }
            }
        }
        StringTokenizer str= new StringTokenizer(line,",");
    }
    public static double getTabu(int NN, int Tao, int R, int Ins){
        int n = 0,t = 0,r = 0;
        if (NN == 10) n = 0; if (NN == 15) n = 1; if (NN == 20) n = 2; if (NN == 25) n = 3; if (NN == 50) n = 4; if (NN == 100) n = 5;
        if (Tao == 1) t = 0; if (Tao == 3) t = 1; if (Tao == 5) t = 2; if (Tao == 7) t = 3; if (Tao == 9) t = 4;
        if (R == 1) r = 0; if (R == 3) r = 1; if (R == 5) r = 2; if (R == 7) r = 3; if (R == 9) r = 4;
        return Tabu[n][t][r][Ins-1];
    }
    public static double getUB(int NN, int Tao, int R, int Ins){
        int n = 0,t = 0,r = 0;
        if (NN == 10) n = 0; if (NN == 15) n = 1; if (NN == 20) n = 2; if (NN == 25) n = 3; if (NN == 50) n = 4; if (NN == 100) n = 5;
        if (Tao == 1) t = 0; if (Tao == 3) t = 1; if (Tao == 5) t = 2; if (Tao == 7) t = 3; if (Tao == 9) t = 4;
        if (R == 1) r = 0; if (R == 3) r = 1; if (R == 5) r = 2; if (R == 7) r = 3; if (R == 9) r = 4;
        return upperBound[n][t][r][Ins-1];
    }
}
