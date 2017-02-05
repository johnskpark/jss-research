/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.app.GPjsp;

import jsp.AbstractJSPFramework;
import jsp.DynamicJSPFramework;
import jsp.DynamicJSPFrameworkBreakdown;
import jsp.Job;

/**
 *
 * @author nguyensu
 */
public class JSPVariables {
    public int mm = -1;
    public double kTH = -1;
    public double APR = -1;
    public double TAPR = -1;
    public double M = -1;
    public double NJQ = -1;
    public double N = -1;
    public double NO = -1;
    public double OT = -1;
    public double TOT = -1;
    public double PEF = -1;
    public double RWL = -1;
    public double TRWL = -1;
    public double SAPR = -1;
    public double TSAPR = -1;
    public double SAR = -1;
    public double SAW =-1;
    public double SER =-1;
    public double SL =-1;
    public double TAW = -1;
    public double LOT = -1;
    public double TLOT = -1;
    public double CPOT = -1;
    public double QWL = -1;
    public double TQWL = -1;
    public double OTR = -1;
    public double PRR = -1;
    public double TOTR = -1;
    public double SOTR = -1;
    public double TSOTR = -1;
    public double SU = -1;
    public double LR = -1;
    public double SLR = -1;
    public double PO = -1;
    public double Nq = -1;
    public double W = -1;
    // variables for machine breakdown.
    public double prevDTime = -1;
    public double prevATime = -1;
    public double DTime = -1;
    public double ATime = -1;
    public double sampleInterBreakdownTimes = -1;
    public double sampleRepairTimes = -1;
    public double breakdownRate = -1;
    public double meanRepairTime = -1;
    public double tempDuate = -1;
    public double nextArrivalTime = -1;
    public double nextReadyTime = -1; // TODO
    public void gatherStatFromJSPModel(AbstractJSPFramework jsp, int m, Job newjob, int i, double pef) {
    	if (jsp instanceof DynamicJSPFramework) {
    		gatherStatFromDynamicJSPModel((DynamicJSPFramework) jsp, m, newjob, i, pef);
    	} else if (jsp instanceof DynamicJSPFrameworkBreakdown) {
    		gatherStatFromDynamicJSPModel((DynamicJSPFrameworkBreakdown) jsp, m, newjob, i, pef);
    	}
    }

    private void gatherStatFromDynamicJSPModel(DynamicJSPFramework jspDynamic, int m, Job newJob, int i, double pef) {
        kTH = (double)i;
        if (i!=-1) mm = newJob.getKthMachine(i);
        if (i!=-1) APR = jspDynamic.getMachines()[mm].getAverageProcessingTimeinQueue(newJob.getReleaseTime());
        if (i==-1) TAPR = jspDynamic.getTotalAverageProcessingTime_Route(newJob);
        M = m;
        if (i!=-1) NJQ = jspDynamic.getMachines()[mm].getNumberofJobInQueue(newJob.getReleaseTime());
        N = jspDynamic.getNumberofJobs();
        NO = newJob.getNumberOperations();
        if (i!=-1) OT = newJob.getKthOperationProcessingTime(i);
        if (i==-1) TOT = newJob.getTotalProcessingTime();
        if (i!=-1) PEF = pef;
        if (i!=-1) RWL = jspDynamic.getMachines()[mm].getRemainingWorkload();
        if (i==-1) TRWL = jspDynamic.getTotalRemainingWorkload_Route(newJob);
        if (i!=-1) SAPR = jspDynamic.getMachines()[mm].getSampleAverageProcessingTime();
        if (i!=-1) TSAPR = jspDynamic.getTotalSampleAverageProcessingTime_Route(newJob);
        SAR = jspDynamic.getMovingAverageArrivalRate();
        if (i!=-1) SAW = jspDynamic.getMachines()[mm].getSampleAverageWaitingTime();
        SER = jspDynamic.getMovingAverageErrorDD();
        SL = jspDynamic.getMovingAverageJobLength();
        if (i!=-1) TAW = jspDynamic.getTotalAverageWaiting_Route(newJob);
        if (i!=-1) LOT = jspDynamic.getMachines()[mm].getLeftoverTimetoProcessCurrentJob(newJob.getReleaseTime());
        if (i==-1) TLOT = jspDynamic.getLeftoverProcessingTime_Route(newJob);
        if (i!=-1) CPOT = jspDynamic.getMachines()[mm].getCompletedPartialTimeCurrentJob(newJob.getReleaseTime());
        if (i!=-1) QWL = jspDynamic.getMachines()[mm].getQueueWorkload(newJob.getReleaseTime());
        if (i==-1) TQWL = jspDynamic.getQueueWorkLoad_Route(newJob);
        if (i!=-1) OTR = jspDynamic.getMachines()[mm].getOTRatio(newJob.getKthOperationProcessingTime(i));
        if (i!=-1) {
            double estimatedPriority = (newJob.getKthOperationProcessingTime(i)+max(1,(((tempDuate - PEF)/newJob.getKthRemainProcessingTime(i)))));
            PRR = 1 - jspDynamic.getMachines()[mm].getPriorityRatio(estimatedPriority,newJob.getReleaseTime()+ PEF);
        }
        if (i==-1) TOTR = jspDynamic.getAverageOTRatio_Route(newJob);
        if (i!=-1) SOTR = jspDynamic.getMachines()[mm].getSampledOTRatio(newJob.getKthOperationProcessingTime(i));
        if (i==-1) TSOTR = jspDynamic.getAverageSampledOTRatio_Route(newJob);
        Nq = jspDynamic.getTotalNumberOfJobInQueue(newJob.getReleaseTime());
        W = newJob.getWeight();
    }

    private void gatherStatFromDynamicJSPModel(DynamicJSPFrameworkBreakdown jspDynamic, int m, Job newJob, int i, double pef) {
        kTH = (double)i;
        if (i!=-1) mm = newJob.getKthMachine(i);
        if (i!=-1) APR = jspDynamic.getMachines()[mm].getAverageProcessingTimeinQueue(newJob.getReleaseTime());
        if (i==-1) TAPR = jspDynamic.getTotalAverageProcessingTime_Route(newJob);
        M = m;
        if (i!=-1) NJQ = jspDynamic.getMachines()[mm].getNumberofJobInQueue(newJob.getReleaseTime());
        N = jspDynamic.getNumberofJobs();
        NO = newJob.getNumberOperations();
        if (i!=-1) OT = newJob.getKthOperationProcessingTime(i);
        if (i==-1) TOT = newJob.getTotalProcessingTime();
        if (i!=-1) PEF = pef;
        if (i!=-1) RWL = jspDynamic.getMachines()[mm].getRemainingWorkload();
        if (i==-1) TRWL = jspDynamic.getTotalRemainingWorkload_Route(newJob);
        if (i!=-1) SAPR = jspDynamic.getMachines()[mm].getSampleAverageProcessingTime();
        if (i!=-1) TSAPR = jspDynamic.getTotalSampleAverageProcessingTime_Route(newJob);
        SAR = jspDynamic.getMovingAverageArrivalRate();
        if (i!=-1) SAW = jspDynamic.getMachines()[mm].getSampleAverageWaitingTime();
        SER = jspDynamic.getMovingAverageErrorDD();
        SL = jspDynamic.getMovingAverageJobLength();
        if (i!=-1) TAW = jspDynamic.getTotalAverageWaiting_Route(newJob);
        if (i!=-1) LOT = jspDynamic.getMachines()[mm].getLeftoverTimetoProcessCurrentJob(newJob.getReleaseTime());
        if (i==-1) TLOT = jspDynamic.getLeftoverProcessingTime_Route(newJob);
        if (i!=-1) CPOT = jspDynamic.getMachines()[mm].getCompletedPartialTimeCurrentJob(newJob.getReleaseTime());
        if (i!=-1) QWL = jspDynamic.getMachines()[mm].getQueueWorkload(newJob.getReleaseTime());
        if (i==-1) TQWL = jspDynamic.getQueueWorkLoad_Route(newJob);
        if (i!=-1) OTR = jspDynamic.getMachines()[mm].getOTRatio(newJob.getKthOperationProcessingTime(i));
        if (i!=-1) {
            double estimatedPriority = (newJob.getKthOperationProcessingTime(i)+max(1,(((tempDuate - PEF)/newJob.getKthRemainProcessingTime(i)))));
            PRR = 1 - jspDynamic.getMachines()[mm].getPriorityRatio(estimatedPriority,newJob.getReleaseTime()+ PEF);
        }
        if (i==-1) TOTR = jspDynamic.getAverageOTRatio_Route(newJob);
        if (i!=-1) SOTR = jspDynamic.getMachines()[mm].getSampledOTRatio(newJob.getKthOperationProcessingTime(i));
        if (i==-1) TSOTR = jspDynamic.getAverageSampledOTRatio_Route(newJob);
        Nq = jspDynamic.getTotalNumberOfJobInQueue(newJob.getReleaseTime());
        W = newJob.getWeight();

        // variables for machine breakdown
        if (i!=-1) {
        	prevDTime = jspDynamic.getMachines()[mm].getPrevDeactivationTime();
        	prevATime = jspDynamic.getMachines()[mm].getPrevActivationTime();
        	DTime = jspDynamic.getMachines()[mm].getDeactivationTime();
        	ATime = jspDynamic.getMachines()[mm].getActivationTime();
        	sampleInterBreakdownTimes = jspDynamic.getMachines()[mm].getSampleAvgInterBreakdownTimes();
        	sampleRepairTimes = jspDynamic.getMachines()[mm].getSampleAvgRepairTimes();
        	breakdownRate = jspDynamic.getBreakdownRate();
        	meanRepairTime = jspDynamic.getMeanRepairTimes();
        }
    }

    public static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
}
