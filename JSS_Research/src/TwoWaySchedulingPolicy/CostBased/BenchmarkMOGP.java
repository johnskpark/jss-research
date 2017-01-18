/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TwoWaySchedulingPolicy.CostBased;

/**
 *
 * @author nguyensu
 */
public class BenchmarkMOGP {
    public static double[] Utilisation = {0.85,0.95};
    public static double[] Allowance = {4,6,8};
    public static double[] Mean = {25,50};
    public static String[] rules = {"CR_SPT","CR","EDD","FDD","FIFO","LIFO","LPT","AVPRO","LWKR","LWKR_SPT","MOD","MOPNR","MWKR","NPT","OPFSLK_PT","PW","SL","Slack","Slack_OPN","Slack_RPT_SPT","SPT_PW","SPT_PW_FDD","PT_WINQ","twoPT_WINQ_NPT","PT_WINQ_SL","PT_WINQ_NPT_WSL","ATC","COVERT","RR","SPT","WINQ"};
    public static String[] NAMEFORMAL = {"CR+SPT","CR","EDD","FDD","FIFO","LIFO","LPT","AVPRO","LWKR","LWKR+SPT","MOD","MOPNR","MWKR","NPT","OPFSLK/PT","PW","SL","Slack","Slack/OPN","Slack/RPT+SPT","SPT+PW","SPT+PW+FDD","PT+WINQ","2PT+WINQ+NPT","PT+WINQ+SL","PT+WINQ+NPT+WSL","ATC","COVERT","RR","SPT","WINQ"};
    public static double[][][][][][] stat = new double [rules.length][Mean.length][Utilisation.length][Allowance.length][5][50];
    public static void main(String[] arg) {
        getBenchmarkCase();
    }
    public static void getBenchmarkCase(){
        Benchmark1.getStat(stat);
        Benchmark2.getStat(stat);
        Benchmark3.getStat(stat);
        Benchmark4.getStat(stat);
        Benchmark5.getStat(stat);
        Benchmark6.getStat(stat);
        Benchmark7.getStat(stat);
        Benchmark8.getStat(stat);
        Benchmark9.getStat(stat);
        Benchmark10.getStat(stat);
        Benchmark11.getStat(stat);
        Benchmark12.getStat(stat);
        Benchmark13.getStat(stat);
        int index = getIndexOf("ATC", rules);
    }
    public static int getIndexOf(double o, double[] O){
        for (int i = 0; i < O.length; i++) {
            if (o==O[i]) return i;
        }
        return -1;
    }
    public static int getIndexOf(String o, String[] O){
        for (int i = 0; i < O.length; i++) {
            if (o == null ? O[i] == null : o.equals(O[i])) return i;
        }
        return -1;
    }
}
