package ec.app.TwoDStripPacking.BestFit;



import SmallStatistics.SmallStatistics;
import ec.EvolutionState;
import ec.app.TwoDStripPacking.twoDStripData;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

/**
 * 
 * @author Dr Matthew Hyde
 * University of Nottingham
 * http://www.cs.nott.ac.uk/~mvh/
 * 
 * Copyright (c) 2010-2011 University of Nottingham, Computer Science. All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written
 * agreement is hereby granted, provided that the above copyright notice
 * and the following two paragraphs appear in all copies of this
 * software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF NOTTINGHAM, SCHOOL OF COMPUTER 
 * SCIENCE BE LIABLE TO ANY PARTY FOR DIRECT,INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS SOFTWARE AND 
 * ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF NOTTINGHAM, SCHOOL OF 
 * COMPUTER SCIENCE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF NOTTINGHAM, SCHOOL OF COMPUTER SCIENCE, 
 * SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND 
 * THE UNIVERSITY OF NOTTINGHAM, SCHOOL OF COMPUTER SCIENCE, HAS NO OBLIGATION 
 * TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS
 * 
 * This software was used to generate the results in the following paper.
 * @ARTICLE{hyde10cor,
 * author = {Burke, E. K. and Hyde, M. and Kendall, G.},
 * title = {A Squeaky Wheel Optimisation Methodology for Two Dimensional Strip Packing},
 * journal = {Computers and Operations Research},
 * year = {2010},
 * volume = {38},
 * pages = {1035--1044},
 * doi = {http://dx.doi.org/10.1016/j.cor.2010.10.005},
 * }
 * modified by Su Nguyen, Victoria University of Wellington,
 * for evolving iterative constructive rules
 */
public class BestFitEfficientTest {
    static int[] index = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,13,3,3,3,3,3,3,3,6,6,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,5,5,5,5,5,5,5,5,5,5,5,5,5,5,7,3,13,12,10,16};
    static double[] optimum = {40,50,50,80,100,100,100,80,40,50,50,80,100,100,100,80,0,20,15,30,60,90,120,240,100,100};
    static String[] nameSet = {"eN1-","eN2-","eN3-","eN4-","eN5-","eN6-","eN7-","eN8-","tN1-","tN2-","tN3-","tN4-","tN5-","tN6-","tN7-","tN8-","N","c1p","c2p","c3p","c4p","c5p","c6p","c7p","nice","path",
    "c1s1i","c1s2i","c1s3i","c1s4i","c1s5i"
    ,"c2s1i","c2s2i","c2s3i","c2s4i","c2s5i"
    ,"c3s1i","c3s2i","c3s3i","c3s4i","c3s5i"
    ,"c4s1i","c4s2i","c4s3i","c4s4i","c4s5i"
    ,"c5s1i","c5s2i","c5s3i","c5s4i","c5s5i"
    ,"c6s1i","c6s2i","c6s3i","c6s4i","c6s5i"
    ,"c7s1i","c7s2i","c7s3i","c7s4i","c7s5i"
    ,"c8s1i","c8s2i","c8s3i","c8s4i","c8s5i"
    ,"c9s1i","c9s2i","c9s3i","c9s4i","c9s5i"
    ,"c10s1i","c10s2i","c10s3i","c10s4i","c10s5i"
    ,"n1_","n2_","n3_","n4_","n5_","n6_","n7_"
    ,"t1_","t2_","t3_","t4_","t5_","t6_","t7_"
    ,"cx_","cgcut_","gcut_","ngcut_","beng_","zdf_"};
	static final int LEFT = 0;
	static final int RIGHT = 1;
	static final int TALLEST = 1;
	static final int SMALLEST = 2;
	static final int policies = 1;


	Vector<pieceCoordinate> pieceorder;

	long[][] NumberOfPiecesInSet;

	String orderFile = "order";
	String rfolder = "TwoDimentionalStripPacking/box";
        boolean drawBox = false;

	TreeSet<Piece>[][] pieceArray;
	double[][] theoreticalOptimalSolution;
        double[][] totalArea;
	double[][] containerX;
	double[][] containerY;
	double[][] threshold;
	double[] blamememory;
        double[] previousX;
        double[] previousY;
        double[] priority;
        boolean localsearch = false;
        static double mutationRate = 0;
        private static cern.jet.random.engine.RandomEngine engineJob;
        private static cern.jet.random.AbstractDistribution noise;

        double stepSofar = 0;
        static int maxstep = 0;
        static int stepBestFound = 0;
        static long timeBestFound = 0;
	static Random rng = new Random(100);

	static final int maxnumberofpieces = 1000;
	static final int maxnumberofinstances = 16;
	static final boolean postprocessing = false;

	static long timelimit;
	int[] NumberOfInstancesInSet = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,13,3,3,3,3,3,3,3,6,6
        ,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10
        ,5,5,5,5,5,5,5,5,5,5,5,5,5,5,7,3,13,12,10,16};
	String[] InstanceFileRoot = {
			"/instances/generalise/eN1-",
                        "/instances/generalise/eN2-",
                        "/instances/generalise/eN3-",
                        "/instances/generalise/eN4-",
                        "/instances/generalise/eN5-",
                        "/instances/generalise/eN6-",
                        "/instances/generalise/eN7-",
                        "/instances/generalise/eN8-",
			"/instances/generalise/tN1-",
                        "/instances/generalise/tN2-",
                        "/instances/generalise/tN3-",
                        "/instances/generalise/tN4-",
                        "/instances/generalise/tN5-",
                        "/instances/generalise/tN6-",
                        "/instances/generalise/tN7-",
                        "/instances/generalise/tN8-",
                        "/instances/generalise/N",
                        "/instances/generalise/c1p",
                        "/instances/generalise/c2p",
                        "/instances/generalise/c3p",
                        "/instances/generalise/c4p",
                        "/instances/generalise/c5p",
                        "/instances/generalise/c6p",
                        "/instances/generalise/c7p",
			"/instances/generalise/v1p",
			"/instances/generalise/v2p",
                        "/instances/martello/class1/subset1/class_",
                        "/instances/martello/class1/subset2/class_",
                        "/instances/martello/class1/subset3/class_",
                        "/instances/martello/class1/subset4/class_",
                        "/instances/martello/class1/subset5/class_",
                        "/instances/martello/class2/subset1/class_",
                        "/instances/martello/class2/subset2/class_",
                        "/instances/martello/class2/subset3/class_",
                        "/instances/martello/class2/subset4/class_",
                        "/instances/martello/class2/subset5/class_",
                        "/instances/martello/class3/subset1/class_",
                        "/instances/martello/class3/subset2/class_",
                        "/instances/martello/class3/subset3/class_",
                        "/instances/martello/class3/subset4/class_",
                        "/instances/martello/class3/subset5/class_",
                        "/instances/martello/class4/subset1/class_",
                        "/instances/martello/class4/subset2/class_",
                        "/instances/martello/class4/subset3/class_",
                        "/instances/martello/class4/subset4/class_",
                        "/instances/martello/class4/subset5/class_",
                        "/instances/martello/class5/subset1/class_",
                        "/instances/martello/class5/subset2/class_",
                        "/instances/martello/class5/subset3/class_",
                        "/instances/martello/class5/subset4/class_",
                        "/instances/martello/class5/subset5/class_",
                        "/instances/martello/class6/subset1/class_",
                        "/instances/martello/class6/subset2/class_",
                        "/instances/martello/class6/subset3/class_",
                        "/instances/martello/class6/subset4/class_",
                        "/instances/martello/class6/subset5/class_",
                        "/instances/martello/class7/subset1/class_",
                        "/instances/martello/class7/subset2/class_",
                        "/instances/martello/class7/subset3/class_",
                        "/instances/martello/class7/subset4/class_",
                        "/instances/martello/class7/subset5/class_",
                        "/instances/martello/class8/subset1/class_",
                        "/instances/martello/class8/subset2/class_",
                        "/instances/martello/class8/subset3/class_",
                        "/instances/martello/class8/subset4/class_",
                        "/instances/martello/class8/subset5/class_",
                        "/instances/martello/class9/subset1/class_",
                        "/instances/martello/class9/subset2/class_",
                        "/instances/martello/class9/subset3/class_",
                        "/instances/martello/class9/subset4/class_",
                        "/instances/martello/class9/subset5/class_",
                        "/instances/martello/class10/subset1/class_",
                        "/instances/martello/class10/subset2/class_",
                        "/instances/martello/class10/subset3/class_",
                        "/instances/martello/class10/subset4/class_",
                        "/instances/martello/class10/subset5/class_",
                        "/instances/NT/n1_",
                        "/instances/NT/n2_",
                        "/instances/NT/n3_",
                        "/instances/NT/n4_",
                        "/instances/NT/n5_",
                        "/instances/NT/n6_",
                        "/instances/NT/n7_",
                        "/instances/NT/t1_",
                        "/instances/NT/t2_",
                        "/instances/NT/t3_",
                        "/instances/NT/t4_",
                        "/instances/NT/t5_",
                        "/instances/NT/t6_",
                        "/instances/NT/t7_",
                        "/instances/CX/cx_",
                        "/instances/2sp/cgcut",
                        "/instances/2sp/gcut",
                        "/instances/2sp/ngcut",
                        "/instances/2sp/beng",
                        "/instances/ZDF/zdf",
	};
        int NumberOfInstanceSets = NumberOfInstancesInSet.length;
        //GP related
        public GPIndividual ind;
        public twoDStripData data;
        public int thread;
        public EvolutionState state;
        public ADFStack stack;
        public GPProblem gpproblem;
        //end GP declaration
        public void setupGPIndividual(GPIndividual i,twoDStripData d, int th, EvolutionState s, ADFStack st, GPProblem gp){
            ind = i;
            data = d;
            thread = th;
            state = s;
            stack = st;
            gpproblem = gp;
        }
	public static void main(String argv[]) {
            engineJob = new cern.jet.random.engine.MersenneTwister(99999);
            noise = new cern.jet.random.Normal(0, 1, engineJob);
            SmallStatistics result = new SmallStatistics();
            String summary ="\n";
            String report = "";
            String reportStep = "";
            String reportTime = "";
            int[] TestInstanceSet = new int[96];
            maxstep = 20000;
            timelimit = 5000;
            mutationRate = 1.00;
            for (int i = 0; i < TestInstanceSet.length; i++) TestInstanceSet[i] = i;
            //int[] instanceSet = {1,9};
            for (int i = 16; i < 96; i++) {
                double average =0;
                double averageStep =0;
                double averageTime =0;
                for (int j = 0; j < index[i]; j++) {
                //for (int j = 1; j < 2; j++) {
                     BestFitEfficientTest problem = new BestFitEfficientTest(TestInstanceSet[i], j);
                     double obj = problem.run(TestInstanceSet[i], j);
                     double step = stepBestFound;
                     double time = (double)timeBestFound/1000;
                     System.out.println(nameSet[TestInstanceSet[i]] + (1+j) + "-" + obj + "-" + stepBestFound + "-"+ time);
                     if (i<26||i>=76) {
                         summary += nameSet[TestInstanceSet[i]] + (1+j) + " & " + obj + " \\hline"+"\n";
                         result.add(obj);
                         report+=obj +",";
                         reportStep += step +",";
                         reportTime += time +",";
                     } else {
                         average += obj;
                         averageStep +=step;
                         averageTime +=time;
                         //System.out.println(obj);
                     }
                }
                if (i>=26&&i<76) {
                    average /= 10;
                    averageStep /= 10;
                    averageTime /= 10;
                    summary += nameSet[TestInstanceSet[i]] + " & " + average + " \\hline"+"\n";
                    result.add(average);
                    report+=average +",";
                    reportStep += averageStep + ",";
                    reportTime += averageTime +",";
                }
            }
            System.out.println(report + "\n" + reportStep+ "\n" + reportTime);
	}

	public BestFitEfficientTest(int instanceset, int instance) {
		pieceArray = new TreeSet[NumberOfInstanceSets][maxnumberofinstances];
		NumberOfPiecesInSet = new long[NumberOfInstanceSets][];
		containerY = new double[NumberOfInstanceSets][maxnumberofinstances];
		containerX = new double[NumberOfInstanceSets][maxnumberofinstances];
		theoreticalOptimalSolution = new double[NumberOfInstanceSets][maxnumberofinstances];
                totalArea = new double[NumberOfInstanceSets][maxnumberofinstances];
		threshold = new double[NumberOfInstanceSets][maxnumberofinstances];
		NumberOfPiecesInSet[instanceset] = new long[NumberOfInstancesInSet[instanceset]];
		String InstanceFile;
		if (NumberOfInstancesInSet[instanceset] == 1) {
			InstanceFile = InstanceFileRoot[instanceset] + ".txt";//the file of this instance
		} else {
			InstanceFile = InstanceFileRoot[instanceset] + (instance+1) + ".txt";//the file of this instance
		}
		readInFromFile(instanceset, instance, InstanceFile);
	}

	private void readInFromFile(int instanceSet, int ins, String InstanceFile) {
		pieceArray[instanceSet][ins] = new TreeSet<Piece>(new PieceComparator());
		String problemFile = InstanceFile;
		double totalvolume = 0;
		try {
			InputStream _read = getClass().getResourceAsStream(problemFile);//new FileReader(problemFile);
			//BufferedReader buffread = new BufferedReader(read);
                        InputStreamReader _inputFileReader = new InputStreamReader(_read);
                        BufferedReader _buffread   = new BufferedReader(_inputFileReader);
			String line = "";
			NumberOfPiecesInSet[instanceSet][ins] = 0;
                        if (instanceSet>=90){
                            NumberOfPiecesInSet[instanceSet][ins] = Long.parseLong(_buffread.readLine());
                            containerX[instanceSet][ins] = Double.parseDouble(_buffread.readLine());
                        } else{
			while (!(line.startsWith("#"))) {//counts the number of pieces in the instance
				line = _buffread.readLine();
				NumberOfPiecesInSet[instanceSet][ins]++;
			} NumberOfPiecesInSet[instanceSet][ins] -= 2;//to get rid of the # and the sheet size
                        }
			_buffread.close();
                        _inputFileReader.close();
                        _read.close();
			InputStream read = getClass().getResourceAsStream(problemFile);//new FileReader(problemFile);
			//BufferedReader buffread = new BufferedReader(read);
                        InputStreamReader inputFileReader = new InputStreamReader(read);
                        BufferedReader buffread   = new BufferedReader(inputFileReader);
			double partw = 1;//holds the width line
			double parth = 1;//holds the height line
			int i = 0;
                        if (instanceSet>=90){
                            buffread.readLine();buffread.readLine();
                        }
			while (i < NumberOfPiecesInSet[instanceSet][ins]) {
				String[] d = splitline(buffread.readLine());//detects the delimiter and splits the line				
				try {
					int index = 0;
                                        if (instanceSet>=90) index = 1;
					partw = Double.parseDouble(d[index]);index++;
					parth = Double.parseDouble(d[index]);index++;
				} catch (NumberFormatException nf) {
					System.out.println("The numbers in the file are not in the correct format, cannot read as doubles");
					System.exit(0);
				}
                                Piece piece;
				if (instanceSet<26||instanceSet>=76) piece = new Piece(partw, parth, i);
                                else piece = new Piece(parth, partw, i);
				pieceArray[instanceSet][ins].add(piece);
				totalvolume += (piece.width * piece.height);
				i++;
			}//end looping the lines of the file to get the piece sizes
			//now find the dimensions of the bin(s)
			if (instanceSet<90){
                            double binwidth = 1;
                            String[] d = splitline(buffread.readLine());//detects the delimiter and splits the line
                            binwidth = Double.parseDouble(d[0]);//read the bin width
                            containerY[instanceSet][ins] = Integer.MAX_VALUE;
                            containerX[instanceSet][ins] = binwidth;
                        }
			line = buffread.readLine();
			if (instanceSet<90&&!(line.startsWith("#"))) {
				System.out.println("wrong number of pieces or bins, the last line is not hash symbol");
				System.exit(0);
			}
			buffread.close();
			read.close();
		} catch (IOException a) {
			System.err.println(a.getMessage());
			System.exit(0);
		}
                totalArea[instanceSet][ins] = totalvolume;
		if (InstanceFile.startsWith("instances/generalise/v")) {
			theoreticalOptimalSolution[instanceSet][ins] = ((double)totalvolume / containerX[instanceSet][ins]);
		} else {
			theoreticalOptimalSolution[instanceSet][ins] = (int) ((Math.ceil((double)totalvolume / containerX[instanceSet][ins])));
		}
//---------------->//System.out.println(InstanceFile + ", np = " + NumberOfPiecesInSet[instanceSet][ins] + ", op = " + theoreticalOptimalSolution[instanceSet][ins]);
	}//end method readinfromfile

	private String[] splitline(String line) {
		String[] d = line.split(":");//split the line into its components
		if (d.length == 1) {//check if the file is delimited by a space, not a colon
			d = line.split(" ");
		}//end if the file uses spaces not colons
		if (d.length == 1) {//then the file is delimited by a tab, not a space or colon
			d = line.split("	");
		}//end if the file uses tabs for spacing
		return d;
	}//end method splitline

	private double getResult(int instanceset, int ins) {
		double result = 0.0;
		ListIterator<pieceCoordinate> x = pieceorder.listIterator();
		while (x.hasNext()) {
			pieceCoordinate s = x.next();
			if (s.starty+s.height > result) { result = s.starty+s.height;}
		}
		return result;
	}

	private void mergeSlots(PriorityQueue<Slot> heap, Slot s) {
		if (s.previous.y == s.y) {
			s.x = s.previous.x;
			heap.remove(s.previous);
			s.previous.previous.next = s;
			s.previous = s.previous.previous;
		}
		if (s.next.y == s.y) {
			s.endx = s.next.endx;
			heap.remove(s.next);
			s.next.next.previous = s;
			s.next = s.next.next;
		}
	}

	private PriorityQueue<Slot> packAllPieces(TreeSet<Piece> pieces, int policy, int instanceset, int ins) {
		boolean allpacked = false;//are all the pieces packed?
		PriorityQueue<Slot> heap = new PriorityQueue<Slot>(1, new SlotComparatorY());
		Slot first = new Slot(0.0, containerX[instanceset][ins], 0.0, null, null);
		Slot firstslot = new Slot(0, 0, Double.MAX_VALUE, null, first);
		Slot lastslot = new Slot(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, first, null);
		first.previous = firstslot;
		first.next = lastslot;
		heap.add(first);
		while (!allpacked) {//while there are still pieces left to pack
			Slot lowestslot = heap.peek();
			int leftorright = LEFT;
			if (policy == TALLEST) {
				if (lowestslot.next.y > lowestslot.previous.y) {
					leftorright = RIGHT;
				}
			} else if (policy == SMALLEST) {
				if (lowestslot.next.y < lowestslot.previous.y) {
					leftorright = RIGHT;
				}
			}

			Piece p = null;
			double highestScore = Double.NEGATIVE_INFINITY;
			Iterator<Piece> pi = pieces.iterator();
			while (pi.hasNext()) {
				Piece p1 = pi.next();
				if (p1.width <= lowestslot.getWidth()) {//if it fits
                                    //evaluate GP tree
                                    //data.updateData(p1.width, p1.height, lowestslot.y, lowestslot.getWidth()-p1.width, containerX[instanceset][ins], theoreticalOptimalSolution[instanceset][ins]*1.5,p1.blame/stepSofar,p1.previousX,p1.previousY);
                                    double pW = p1.width;
                                    double pH = p1.height;
                                    double pA = p1.height*p1.width;
                                    double sH = lowestslot.y;
                                    double sWL = lowestslot.getWidth()-p1.width;
                                    double shW = containerX[instanceset][ins];
                                    double shH = theoreticalOptimalSolution[instanceset][ins]*1.5;
                                    double pP = p1.blame/stepSofar;
                                    //mutationRate = 0.01;
                                    //if (rng.nextDouble()<mutationRate) pP = Utility.truncatedGaussianMutation(pP, 0, pH, noise);
                                    double ppX = p1.previousX;
                                    double ppY = p1.previousY;
                                    
                                    //rule 301
                                    //p1.score = div(((sWL / 0.7537635029930001) - 2*shH) , ((IF(pP - sWL, (pP - sH) + pP, ppX) + min(pP,((pP - sH) + pP))) * (sWL * shH))) + (div((pP - shH),((min(1,sWL) / 0.7537635029930001) / 0.7537635029930001)) + (max(min((pP - sWL),(sWL - shH)),max((shH * pP),(pH + shW))) * 0.7214136979439266));
                                    //rule 15
                                    //p1.score = max((min(sWL,0.7545891719321856) / max(IF(ppX / 0.7545891719321856, pP, pP),(min(sWL,0.7545891719321856) + (pH + 0.8357355315149528)))),(pP / 0.4352426220509018)) + ((((IF(pH + 0.8357355315149528, 0.4352426220509018, max(pH,ppY)) + ppX) + IF(min(0.4352426220509018,0.7545891719321856), sH, min(min(sWL,0.7545891719321856),0.7545891719321856))) - min(sWL,0.7545891719321856)) - (min(sWL,0.7545891719321856) / max((IF(ppX, pP, shW) + min(sWL,max(pH,ppY))),(min((pP / 0.4352426220509018),0.7545891719321856) + (pH + 0.8357355315149528)))));
                                    //rule best

                                    if (sWL == 0&&(lowestslot.y+pH==lowestslot.previous.y||lowestslot.y+pH==lowestslot.next.y)){
                                        p = p1;
                                        if (lowestslot.y+pH==lowestslot.previous.y) leftorright = LEFT;
                                        leftorright = RIGHT;
                                        break;
                                    }
                                    double x = min(0.3407970682920939, sWL);
                                    if (!localsearch) p1.score = (2*shH - 0.3407970682920939 - 2*shH * x  + shW * x - 0) * (-div(0, x) - x + shW + pP - 0.8563810916971323 / pH);
                                    else p1.score = priority[p1.number];
                                    if (p1.score > highestScore) {
                                            highestScore = p1.score;
                                            p = p1;
                                    }

				}
			}

			if (p == null) {
				//raise the slot
				double nearestheight = lowestslot.previous.y;
				if (nearestheight > lowestslot.next.y) {
					nearestheight = lowestslot.next.y;
				}
				heap.remove(lowestslot);
				Slot raisedslot = new Slot(lowestslot.x, lowestslot.endx, nearestheight, lowestslot.previous, lowestslot.next);
				heap.add(raisedslot);
				lowestslot.previous.next = raisedslot;
				lowestslot.next.previous = raisedslot;
				mergeSlots(heap, raisedslot);
				continue;
			}
                        priority[p.number] = pieces.size();
			//assign blame if it's gone over the top
			if (lowestslot.y+p.height > theoreticalOptimalSolution[instanceset][ins]) {
				blamememory[p.number] +=  p.height;//p.width*(lowestslot.y+p.height - theoreticalOptimalSolution[instanceset][ins]);
                                previousX[p.number] = lowestslot.x;
                                previousY[p.number] = lowestslot.y;
			}

			//remove the piece in both orientations
			pieces.remove(p);
			pieces.remove(new Piece(p.height, p.width, p.number));

			//modify the slot heap and the list pointers
			Slot newslot;
			if (leftorright == LEFT) {
				pieceorder.add(new pieceCoordinate(lowestslot.x, lowestslot.y, p.width, p.height, p.number));
				newslot = new Slot(lowestslot.x, lowestslot.x+p.width, lowestslot.y+p.height, lowestslot.previous, lowestslot);
				lowestslot.previous.next = newslot;
				lowestslot.previous = newslot;
				lowestslot.modifyX(LEFT, p.width);
			} else {
				pieceorder.add(new pieceCoordinate(lowestslot.endx-p.width, lowestslot.y, p.width, p.height, p.number));
				newslot = new Slot(lowestslot.endx-p.width, lowestslot.endx, lowestslot.y+p.height, lowestslot, lowestslot.next);
				lowestslot.next.previous = newslot;
				lowestslot.next = newslot;
				lowestslot.modifyX(RIGHT, p.width);
			}
			if (lowestslot.getWidth() == 0.0) {
				heap.remove(lowestslot);
			}
			heap.add(newslot);
			mergeSlots(heap, newslot);

			if (pieces.isEmpty() ) {
				allpacked = true;
			}
		}

		return heap;
	}

	public void sanitycheck(Vector<pieceCoordinate> pieces) {
		ListIterator<pieceCoordinate> i = pieces.listIterator();
		while (i.hasNext()) {
			pieceCoordinate c = i.next();
			ListIterator<pieceCoordinate> i2 = pieces.listIterator();
			while (i2.hasNext()) {
				pieceCoordinate c2 = i2.next();
				if (c.number != c2.number) {
					Rectangle2D.Double r1 = new Rectangle2D.Double(c.startx, c.starty, c.width, c.height);
					Rectangle2D.Double r2 = new Rectangle2D.Double(c2.startx, c2.starty, c2.width, c2.height);
					if (r1.intersects(r2)) {
						System.out.println(r1.x + "," + r1.y + " " + r1.width + "," + r1.height);
						System.out.println(r2.x + "," + r2.y + " " + r2.width + "," + r2.height);
						System.out.println("overlapping");
						System.exit(-1);
					}
				}
			}
		}
	}

	public double run(int instanceset, int ins) {
		String resultstring = "";
		blamememory = new double[(int)NumberOfPiecesInSet[instanceset][ins]];
                previousX = new double[(int)NumberOfPiecesInSet[instanceset][ins]];
                previousY = new double[(int)NumberOfPiecesInSet[instanceset][ins]];
                priority = new double[(int)NumberOfPiecesInSet[instanceset][ins]];
                int count = 0;
                int fix = 0;
                int countNonImprove = 0;
                double[] priorityBest = new double[(int)NumberOfPiecesInSet[instanceset][ins]];
		Vector<pieceCoordinate> besteverpieceorder =  new Vector<pieceCoordinate>();
		PriorityQueue<Slot> besteverheap = null;
		double besteverresult = Double.POSITIVE_INFINITY;	
		int[] counts = new int[3];
		double[] results = new double[3];
		long overallstarttime = System.currentTimeMillis();
		for (int policy = 1; policy <= 2; policy++) {
			Arrays.fill(blamememory, 0);
			Iterator<Piece> i1p = pieceArray[instanceset][ins].iterator();
			while (i1p.hasNext()){
				Piece p = i1p.next();
				p.blame = 1;
			}
			long starttime = System.currentTimeMillis();
			Vector<pieceCoordinate> bestpieceorder =  new Vector<pieceCoordinate>();
			PriorityQueue<Slot> bestheap = null;
			double bestresult = Double.POSITIVE_INFINITY;
			boolean foundidealsolution = false;
//--------------------->
			//while (counts[policy]<maxstep){
                        while (System.currentTimeMillis() - starttime < timelimit) {
                                counts[policy]++;
				pieceorder = new Vector<pieceCoordinate>();
				TreeSet<Piece> tempPieceArray = new TreeSet<Piece>(new PieceComparator());
				Iterator<Piece> i = pieceArray[instanceset][ins].iterator();
				while (i.hasNext()){
					Piece p = i.next();
					tempPieceArray.add(p.clone());
				}
                                stepSofar = counts[policy];
				PriorityQueue<Slot> heap = packAllPieces(tempPieceArray, 1, instanceset, ins);
				double result = getResult(instanceset, ins);//all the pieces have been packed so get the solution height
                                if (countNonImprove==-1) {
                                    fix = 0;
                                    count = fix+1;
                                    localsearch = true;
                                    countNonImprove = 0;
                                }
                                if (mutationRate==-1) System.out.println(result);
                                if (localsearch){
                                    System.arraycopy(priorityBest, 0, priority, 0, priority.length);
                                    //System.out.println(fix + "swap" + (fix+count));
                                    swap(priority, fix, fix + count);
                                    count++;
                                    if (fix + count==priorityBest.length-1) {
                                        fix++;
                                        if (fix==priorityBest.length-1){
                                            fix = 0; count = 0; localsearch = false;
                                        } else count=0;
                                    }
                                }
				if (result < bestresult) {
                                        System.arraycopy(priority, 0, priorityBest, 0, priority.length);
					bestresult = result;
					bestpieceorder = new Vector<pieceCoordinate>();
					for (pieceCoordinate p : pieceorder) {
						bestpieceorder.add(p.deepclone());
					}
					bestheap = heap;
                                        stepBestFound = counts[policy];
                                        timeBestFound = System.currentTimeMillis() - starttime;
				} else countNonImprove++;
				Iterator<Piece> i1 = pieceArray[instanceset][ins].iterator();
				while (i1.hasNext()){
					Piece pc = i1.next();
					pc.blame = blamememory[pc.number];
                                        pc.previousX = previousX[pc.number];
                                        pc.previousY = previousY[pc.number];
                                        //System.out.print(pc.blame + "\t");
				}
                                 //System.out.println("");
				if (result-theoreticalOptimalSolution[instanceset][ins] == 0.0) {
					foundidealsolution = true;
					break;
				}
			}
			results[policy] = bestresult;
			if (bestresult < besteverresult) {
				besteverresult = bestresult;
				besteverpieceorder = new Vector<pieceCoordinate>();
				for (pieceCoordinate p : bestpieceorder) {
					besteverpieceorder.add(p.deepclone());
				}
				besteverheap = bestheap;
			}
			if (foundidealsolution) {
				break;
			}
		}
		long overallendtime = (System.currentTimeMillis()-overallstarttime);

		//System.out.println("best ever " + besteverresult + " " + counts[0] + " " + results[0] + " " + counts[1] + " " + results[1] + " " + counts[2] + " " + results[2] + " " + (double)overallendtime/1000.0);
		//resultstring = resultstring + " " + (besteverresult-theoreticalOptimalSolution[instanceset][ins]);
		if (drawBox) {
                    boxPrinter.printtoVRML(besteverpieceorder, besteverheap, instanceset, ins, containerX[instanceset][ins], "r", (rfolder+"/"+orderFile), besteverresult);
                    try {
                            FileWriter f = new FileWriter(rfolder+"/res"+orderFile+"r"+instanceset+"s"+ins+".txt");
                            PrintWriter b = new PrintWriter(f);
                            b.println(besteverresult);
                            b.close();
                            f.close();
                    } catch (IOException a) {
                            System.err.println(a.getMessage());
                            System.exit(0);
                    }
                }
		//System.out.println("end");
		//resultstring += "\n";
		return besteverresult;
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
    public static void swap(double[] X, int p1, int p2){
        double temp = X[p1];
        X[p1]=X[p2];
        X[p2]=temp;
    }
}
