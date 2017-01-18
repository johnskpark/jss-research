package ec.app.TwoDStripPacking.BestFit;



import SmallStatistics.SmallStatistics;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
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
 */
public class BestFitEfficientOriginal{
    static int[] index = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,13,3,3,3,3,3,3,3,6,6,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
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
    ,"c10s1i","c10s2i","c10s3i","c10s4i","c10s5i"};
	static final int LEFT = 0;
	static final int RIGHT = 1;
	static final int TALLEST = 1;
	static final int SMALLEST = 2;
	static final int policies = 1;

	Vector<pieceCoordinate> pieceorder;

	long[][] NumberOfPiecesInSet;

	String orderFile = "order";
	String rfolder = "TwoDimentionalStripPacking/box";


	TreeSet<Piece>[][] pieceArray;
	double[][] theoreticalOptimalSolution;
	double[][] containerX;
	double[][] containerY;
	double[][] threshold;
	double[] blamememory;

	static Random rng;

	static final int maxnumberofpieces = 1000;
	static final int maxnumberofinstances = 13;
	static final boolean postprocessing = false;

	public static long timelimit;
	int[] NumberOfInstancesInSet = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,13,3,3,3,3,3,3,3,6,6
        ,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
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
	};
        int NumberOfInstanceSets = NumberOfInstancesInSet.length;

	public static void main(String argv[]) {
            SmallStatistics result = new SmallStatistics();
            String summary ="\n";
            int[] TestInstanceSet = new int[76];
            timelimit = 60000;
            for (int i = 0; i < TestInstanceSet.length; i++) TestInstanceSet[i] = i;
            //int[] instanceSet = {1,9};
            for (int i = 16; i < 17; i++) {
                double average =0;
                //for (int j = 0; j < index[i]; j++) {
                for (int j = 12; j < 13; j++) {
                     BestFitEfficientOriginal problem = new BestFitEfficientOriginal(TestInstanceSet[i], j);
                     double obj = problem.run(TestInstanceSet[i], j);
                     if (i<26) {
                         summary += nameSet[TestInstanceSet[i]] + (1+j) + " & " + obj + " \\hline"+"\n";
                         result.add(obj);
                     } else {
                         average += obj;
                         System.out.println(obj);
                     }
                }
                if (i>=26) {
                    average /= 10;
                    summary += nameSet[TestInstanceSet[i]] + " & " + average + " \\hline"+"\n";
                    result.add(average);
                }
            }
            //System.out.println(summary);
                System.out.println("Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + ", Optimal hits = "  + "]" + summary);
	}

	public BestFitEfficientOriginal(int instanceset, int instance) {
		pieceArray = new TreeSet[NumberOfInstanceSets][maxnumberofinstances];
		NumberOfPiecesInSet = new long[NumberOfInstanceSets][];
		containerY = new double[NumberOfInstanceSets][maxnumberofinstances];
		containerX = new double[NumberOfInstanceSets][maxnumberofinstances];
		theoreticalOptimalSolution = new double[NumberOfInstanceSets][maxnumberofinstances];
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
				if (instanceSet<26) piece = new Piece(partw, parth, i);
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

		if (InstanceFile.startsWith("instances/generalise/v")) {
			theoreticalOptimalSolution[instanceSet][ins] = ((double)totalvolume / containerX[instanceSet][ins]);
		} else {
			theoreticalOptimalSolution[instanceSet][ins] = (int) ((Math.ceil((double)totalvolume / containerX[instanceSet][ins])));
		}
		System.out.println(InstanceFile + ", np = " + NumberOfPiecesInSet[instanceSet][ins] + ", op = " + theoreticalOptimalSolution[instanceSet][ins]);
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
			double bestblame = -1;
			Iterator<Piece> pi = pieces.iterator();
			while (pi.hasNext()) {
				Piece p1 = pi.next();
				if (p1.width <= lowestslot.getWidth()) {//if it fits
					if (p1.blame > bestblame) {
						bestblame = p1.blame;
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
				blamememory[p.number] += p.height;
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
				p.blame = 0;
			}
			long starttime = System.currentTimeMillis();
			Vector<pieceCoordinate> bestpieceorder =  new Vector<pieceCoordinate>();
			PriorityQueue<Slot> bestheap = null;
			double bestresult = Double.POSITIVE_INFINITY;
			boolean foundidealsolution = false;

			while (System.currentTimeMillis() - starttime < timelimit) {
				counts[policy]++;
				pieceorder = new Vector<pieceCoordinate>();
				TreeSet<Piece> tempPieceArray = new TreeSet<Piece>(new PieceComparator());
				Iterator<Piece> i = pieceArray[instanceset][ins].iterator();
				while (i.hasNext()){
					Piece p = i.next();
					tempPieceArray.add(p.clone());
				}
				PriorityQueue<Slot> heap = packAllPieces(tempPieceArray, policy, instanceset, ins);
				double result = getResult(instanceset, ins);//all the pieces have been packed so get the solution height
                                System.out.println(result);
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
		resultstring = resultstring + " " + (besteverresult-theoreticalOptimalSolution[instanceset][ins]);
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
		//System.out.println();
		//resultstring += "\n";
		return besteverresult;
	}
}
