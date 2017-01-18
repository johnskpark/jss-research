package ec.app.TwoDStripPacking.BestFit;



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
public class BestFitEfficientPenalty {

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
        double stepSofar = 0;

	static Random rng;

	static final int maxnumberofpieces = 1000;
	static final int maxnumberofinstances = 13;
	static final boolean postprocessing = false;

	static long timelimit;
	int[] NumberOfInstancesInSet = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,13,3,3,3,3,3,3,3,6,6
        ,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10
        ,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5};
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
		timelimit = 6000;//Long.parseLong(argv[0])*3;
		int instanceset = 9;// Integer.parseInt(argv[1]);
		int ins = 1;//Integer.parseInt(argv[2]);
		BestFitEfficientPenalty problem = new BestFitEfficientPenalty(instanceset, ins);
		System.out.println(problem.run(instanceset, ins));
	}

	public BestFitEfficientPenalty(int instanceset, int instance) {
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
			while (!(line.startsWith("#"))) {//counts the number of pieces in the instance
				line = _buffread.readLine();
				NumberOfPiecesInSet[instanceSet][ins]++;
			} NumberOfPiecesInSet[instanceSet][ins] -= 2;//to get rid of the # and the sheet size
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
			while (i < NumberOfPiecesInSet[instanceSet][ins]) {
				String[] d = splitline(buffread.readLine());//detects the delimiter and splits the line				
				try {
					int index = 0;
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
			double binwidth = 1;
			String[] d = splitline(buffread.readLine());//detects the delimiter and splits the line
			binwidth = Double.parseDouble(d[0]);//read the bin width
			containerY[instanceSet][ins] = Integer.MAX_VALUE;
			containerX[instanceSet][ins] = binwidth;

			line = buffread.readLine();
			if (!(line.startsWith("#"))) {
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
                                    p1.score = p1.blame;
                                    // end GP evaluation
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
			//assign blame if it's gone over the top
			if (lowestslot.y+p.height > theoreticalOptimalSolution[instanceset][ins]) {
                                data.updateData(p.width, p.height, lowestslot.y, lowestslot.getWidth()-p.width, containerX[instanceset][ins], theoreticalOptimalSolution[instanceset][ins]*1.5,p.blame/stepSofar,p.previousX,p.previousY);
                                ind.trees[0].child.eval(state,thread,data,stack,((GPIndividual)ind),gpproblem);
				                        //System.out.println(data.tempVal);
                                if (Double.isNaN(data.tempVal)){
                                    blamememory[p.number] += p.height;
                                    
                                } else blamememory[p.number] =  Math.abs(data.tempVal)/stepSofar;
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
		Vector<pieceCoordinate> besteverpieceorder =  new Vector<pieceCoordinate>();
		PriorityQueue<Slot> besteverheap = null;
		double besteverresult = Double.POSITIVE_INFINITY;	
		int[] counts = new int[3];
		double[] results = new double[3];
		long overallstarttime = System.currentTimeMillis();
		for (int policy = 1; policy <= 1; policy++) {

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
                        boolean improve = true;
//--------------------->
                        int MaxStep = 100;
			while (counts[policy]<MaxStep){//(System.currentTimeMillis() - starttime < timelimit) {
				counts[policy]++;
				pieceorder = new Vector<pieceCoordinate>();
				TreeSet<Piece> tempPieceArray = new TreeSet<Piece>(new PieceComparator());
				Iterator<Piece> i = pieceArray[instanceset][ins].iterator();
				while (i.hasNext()){
					Piece p = i.next();
					tempPieceArray.add(p.clone());
				}
                                stepSofar = counts[policy];
				PriorityQueue<Slot> heap = packAllPieces(tempPieceArray, policy, instanceset, ins);
				double result = getResult(instanceset, ins);//all the pieces have been packed so get the solution height
				if (result < bestresult) {
					bestresult = result;
					bestpieceorder = new Vector<pieceCoordinate>();
					for (pieceCoordinate p : pieceorder) {
						bestpieceorder.add(p.deepclone());
					}
					bestheap = heap;
				} 
				Iterator<Piece> i1 = pieceArray[instanceset][ins].iterator();
				while (i1.hasNext()){
					Piece pc = i1.next();
					pc.blame = blamememory[pc.number];
                                        pc.previousX = previousX[pc.number];
                                        pc.previousY = previousY[pc.number];
				}
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
}
