/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.QCSP;

import SmallStatistics.SmallStatistics;
import ec.Evolve;
import ec.app.QCSP.Core.QCSP;
import java.text.DecimalFormat;

/**
 *
 * @author nguyensu
 */
public class ExperimentJSPMO {
public static int[] Seed = { 26633, 66758 ,82732 ,82914 ,95521 ,18888 ,
        58562 ,33965 ,75225, 23538, 68043, 59047, 34289, 14021, 87558, 37487, 63978,
        37204 ,43385, 76139, 13412 ,34429 ,44121 ,71721 ,33151 ,29012,
        31344 ,55124 ,14516, 85128
};

    public static void main(String[] args) {//avg TWT -file ecj/ec/app/GPjsp/params/GPqcsp.params
        int nSet = 1;
        int nRep = 30;
        int nInPerSet = 1;
        int fromT = 6;
        int fromInstanceT = 0;
        QCSP.uncertainty =false;
        GPqcsp.local = false;
        QCSP.noiseLevel = 0.3;
        String outname = "";
        String paramLOC = "/local/scratch/sunguyenWORK/myCode/GPjsp/ecj/ec/app/GPjsp/params";
        if (args.length!=0){
            nSet = Integer.parseInt(args[0]);
            fromT = Integer.parseInt(args[1]);
            nRep = Integer.parseInt(args[2]);
            GPqcsp.local = Boolean.parseBoolean(args[3]);
            GPqcsp.timeLimit = Long.parseLong(args[4]);
            QCSP.uncertainty = Boolean.parseBoolean(args[5]);
            QCSP.noiseLevel = Double.parseDouble(args[6]);
            outname = args[7];
            paramLOC = "config";
        }
        double[][][] OBJ = new double[nSet][nRep][nInPerSet];
        double[][][][] OBJTEST = new double[nSet][nRep][nInPerSet][2];
        long[][][] TIME = new long[nSet][nRep][nInPerSet];
        long[][][] BESTTIME = new long[nSet][nRep][nInPerSet];
        GPqcsp.maxStep = 100;
        QCSP.maxRangeLS = 3;
        String report ="";
        QCSP.writeResult("GPdetaileREPORT" + outname, "results from " + nSet + " sets \n", false);
        for (int i = 0; i < nSet; i++) {
            for (int j = 0; j < nInPerSet; j++) {
                for (int k = 0; k < nRep; k++) {
                    String[] params = {"QCSP",""+(i+fromT),""+(j+fromInstanceT),"-file",paramLOC+"/GPqcsp.params","-p","seed.0="+Seed[k]};
                    Evolve.main(params);
                    OBJ[i][k][j] = GPqcsp.best;
                    OBJTEST[i][k][j] = GPqcsp.INS.constructTestSchedule();
                    TIME[i][k][j] = GPqcsp.runningTime;
                    BESTTIME[i][k][j] = GPqcsp.findTime;
                    System.out.println("Instance " + (i+fromT) + "/" + (j+fromInstanceT) + "/" + k + ": " + OBJ[i][k][j] + "(" + BESTTIME[i][k][j] + "/" + TIME[i][k][j] + ")");
                    QCSP.writeResult("GPdetaileREPORT" + outname, "Instance " + (i+fromT) + "/" + (j+fromInstanceT) + "/" + k + ": " + OBJ[i][k][j] + "(" + BESTTIME[i][k][j] + "/" + TIME[i][k][j] + ") \n", true);
                }
            }
        }
        System.out.println("Detailed Result");
        SmallStatistics[][] avgOBJ = new SmallStatistics[nSet][nInPerSet];
        SmallStatistics[][][] avgOBJTEST = new SmallStatistics[nSet][nInPerSet][2];
        SmallStatistics[][] avgREL = new SmallStatistics[nSet][nInPerSet];
        SmallStatistics[][] avgTIME = new SmallStatistics[nSet][nInPerSet];
        SmallStatistics[][] avgBESTTIME = new SmallStatistics[nSet][nInPerSet];
        for (int i = 0; i < nSet; i++) {
            for (int j = 0; j < nInPerSet; j++) {
                avgOBJ[i][j] = new SmallStatistics();
                avgOBJTEST[i][j][0] = new SmallStatistics(); avgOBJTEST[i][j][1] = new SmallStatistics();
                avgREL[i][j] = new SmallStatistics();
                avgTIME[i][j] = new SmallStatistics();
                avgBESTTIME[i][j] = new SmallStatistics();
                for (int k = 0; k < nRep; k++) {
                    System.out.println("Instance " + (i+fromT) + "/" + (j+fromInstanceT) + "/" + k + ": " + OBJ[i][k][j] + "(" + BESTTIME[i][k][j] + "--" + TIME[i][k][j] + ")");
                    avgOBJ[i][j].add(OBJ[i][k][j]);
                    avgOBJTEST[i][j][0].add(OBJTEST[i][k][j][0]); avgOBJTEST[i][j][1].add(OBJTEST[i][k][j][1]);
                    avgREL[i][j].add((OBJ[i][k][j]-QCSP.bestKnown[i+fromT][j+fromInstanceT])/QCSP.bestKnown[i+fromT][j+fromInstanceT]);
                    avgTIME[i][j].add(TIME[i][k][j]);
                    avgBESTTIME[i][j].add(BESTTIME[i][k][j]);
                }
            }
        }
        System.out.println("===================================================================================");
        System.out.println("Summarised Result");
        DecimalFormat df = new DecimalFormat( "0.00" );
        String testReport ="";
        for (int i = 0; i < nSet; i++) {
            for (int j = 0; j < nInPerSet; j++) {
                System.out.println("Instance " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJ[i][j].getMin() + "/" + avgOBJ[i][j].getAverage() + "/" + avgOBJ[i][j].getMax() + " -- " + df.format(avgREL[i][j].getAverage()*100) + "/" + df.format(avgREL[i][j].getMax()*100) +  " (" + avgBESTTIME[i][j].getAverage()
                        + "/" + avgBESTTIME[i][j].getMax() + "--" + avgTIME[i][j].getAverage() + "/" + avgTIME[i][j].getMax() + ")");
                report += "Instance " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJ[i][j].getMin() + "/" + avgOBJ[i][j].getAverage() + "/" + avgOBJ[i][j].getMax() + " -- " + df.format(avgREL[i][j].getAverage()*100) + "/" + df.format(avgREL[i][j].getMax()*100) + " (" + avgBESTTIME[i][j].getAverage()
                        + "/" + avgBESTTIME[i][j].getMax() + "--" + avgTIME[i][j].getAverage() + "/" + avgTIME[i][j].getMax() + ") \n";
                testReport += "Expected " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJTEST[i][j][0].getMin() + "/" + avgOBJTEST[i][j][0].getAverage() + "/" + avgOBJTEST[i][j][0].getMax() + "\n";
                testReport += "Std.Devi " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJTEST[i][j][1].getMin() + "/" + avgOBJTEST[i][j][1].getAverage() + "/" + avgOBJTEST[i][j][1].getMax() + "\n";
            }
        }
        QCSP.writeResult("nGPqcspREPORT" + QCSP.uncertainty + QCSP.noiseLevel + outname, report + "\n === robust test === \n" + testReport, false);
        System.out.println("\n === robust test === \n" + testReport);
        System.out.println("Experiments are done!!!");
    }
}
