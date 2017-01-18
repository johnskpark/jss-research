package ec.app.TwoDStripPacking.BestFit;



import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Vector;
/**
 * 
 * @author Dr Matthew Hyde
 * University of Nottingham
 * http://www.cs.nott.ac.uk/~mvh/
 * 
 * Copyright (c) 2002-2003 University of Nottingham, Computer Science. All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software for academic research purposes, without fee, and without written agreement is hereby granted.
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
public class boxPrinter {

	public static void printtoVRML(Vector<pieceCoordinate> pieceorder, PriorityQueue<Slot> slots, int instanceset, int ins, 
			double width, String set, String orderFile, double result) {
		int type = 0;

		if (type == 0) {
			//for SVG
			try {
				double offset = 1;
				FileWriter f = new FileWriter(orderFile+set+instanceset+"s"+ins+".svg");
				PrintWriter buffprint = new PrintWriter(f);
				buffprint.println("<?xml version=\"1.0\" standalone=\"no\"?>");

				buffprint.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"");
				buffprint.println("\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");

				buffprint.println("<svg viewBox=\"0 0 " + (width+(offset*2)) + " " + (result+(offset*2)) + "\" width=\"100%\" height=\"100%\" version=\"1.1\"");
				buffprint.println("xmlns=\"http://www.w3.org/2000/svg\">");
				buffprint.println();
				buffprint.println("<rect x=\"" + offset + "\" y=\"" + offset 
						+ "\" width=\"" + width + "\" height=\"" + result + "\"");
				buffprint.println("style=\"fill:rgb(255,255,255);");
				buffprint.println("stroke-width:0.1;stroke:rgb(0,0,0)\"/>");
				buffprint.println();
				for (int y = 0; y < pieceorder.size(); y++) {
					pieceCoordinate t = pieceorder.elementAt(y);
					//String colour = t.colour+ ","+t.colour+ ","+t.colour;
					int mod = 12;
					String colour = "";
					if (y % mod == 0) {
						colour = "255,0,0";
					} else if (y % mod == 1) {
						colour = "0,255,0";
					} else if (y % mod == 2) {
						colour = "0,0,255";
					} else if (y % mod == 3) {
						colour = "0,255,255";
					} else if (y % mod == 4) {
						colour = "255,0,255";
					} else if (y % mod == 5) {
						colour = "255,255,0";
					} else if (y % mod == 6) {
						colour = "255,125,0";
					} else if (y % mod == 7) {
						colour = "255,0,125";
					} else if (y % mod == 8) {
						colour = "0,255,125";
					} else if (y % mod == 9) {
						colour = "125,255,0";
					} else if (y % mod == 10) {
						colour = "125,0,255";
					} else if (y % mod == 11) {
						colour = "0,125,255";
					}
					
					double ynew = (result-(t.starty+t.height));
					buffprint.println("<rect x=\"" + (offset+(t.startx)) + "\" y=\"" + (offset+(ynew)) 
							+ "\" width=\"" + (t.width) + "\" height=\"" + (t.height) + "\"");
					buffprint.println("style=\"fill:rgb(" + colour + ");");
					buffprint.println("stroke-width:0.05;stroke:rgb(0,0,0)\"/>");
					buffprint.println();
				}
				Iterator<Slot> i = slots.iterator();
				while (i.hasNext()) {
					Slot s = i.next();
					double y1new = (result-(s.y));
					buffprint.println("<line x1=\"" + (offset+(s.x)) + "\" y1=\"" + (offset+(y1new)) + 
							"\" x2=\"" + (offset+(s.endx)) + "\" y2=\"" + (offset+(y1new)) + "\"");
					//buffprint.println("style=\"fill:rgb(0,0,0);");
					buffprint.println("style=\"");
					buffprint.println("stroke-width:0.5;stroke:black\"/>");
					buffprint.println();
				}

				buffprint.println("</svg>");
				buffprint.close();
				f.close();
			} catch (IOException a) {
				System.err.println(a.getMessage());
				System.exit(0);
			} // end catch*/
		} else {

			//this is for VRML
			try {
				FileWriter f = new FileWriter(orderFile+set+instanceset+"s"+ins+".wrl");
				PrintWriter buffprint = new PrintWriter(f);
				buffprint.println("#VRML V2.0 utf8");
				double reducer = 5;

				double x = width;
				double y1 = result;
				double z = 1;

				buffprint.println("Transform {");
				buffprint.println("  translation " + ((x/2)/reducer) + " " + ((y1/2)/reducer)  + " " + ((z/2)/reducer));
				buffprint.println("  children [ ");
				buffprint.println("    Shape {");
				buffprint.println("      appearance Appearance {");
				buffprint.println("        material Material {");
				buffprint.println("          transparency 0.8");
				buffprint.println("          diffuseColor 1 1 1");
				buffprint.println("        }");
				buffprint.println("      }");
				buffprint.println("      geometry Box {");
				buffprint.println("        size " + ((x)/reducer) + " " + ((y1)/reducer) + " " + ((z)/reducer));
				buffprint.println("      }");
				buffprint.println("    }");
				buffprint.println("  ]");
				buffprint.println("}");
				buffprint.println("");
				buffprint.println("Viewpoint {");				
				buffprint.println("	 position " + ((x/2))/reducer + " " + ((y1/2)/reducer) + " " + "10");
				//buffprint.println("  orientation 1 0 0 -0.5");
				buffprint.println("}");

				buffprint.println();
				//double colormod = 0.7/((double)pieceorder.size()/3.0);
				for (int y = 0; y < pieceorder.size(); y++) {
					String colour = "";
					/*if (y <= ((double)pieceorder.size()/3)) {
					colour = 1.3-(0.3 + colormod*(double)y) + " 0 0";
				} else if (y <= ((double)pieceorder.size()/3)*2.0) {
					colour = "0 " + (1.3-(0.3 + colormod*(double)(y - (pieceorder.size()/3)))) + " 0";
				} else {
					colour =  "0 0 " + (1.3-(0.3 + colormod*(double)(y - ((pieceorder.size()/3)*2))));
				}*/
					int mod = 12;
					if (y % mod == 0) {
						colour = "1 0 0";
					} else if (y % mod == 1) {
						colour = "0 1 0";
					} else if (y % mod == 2) {
						colour = "0 0 1";
					} else if (y % mod == 3) {
						colour = "0 1 1";
					} else if (y % mod == 4) {
						colour = "1 0 1";
					} else if (y % mod == 5) {
						colour = "1 1 0";
					} else if (y % mod == 6) {
						colour = "1 0.5 0";
					} else if (y % mod == 7) {
						colour = "1 0 0.5";
					} else if (y % mod == 8) {
						colour = "0 1 0.5";
					} else if (y % mod == 9) {
						colour = "0.5 1 0";
					} else if (y % mod == 10) {
						colour = "0.5 0 1";
					} else if (y % mod == 11) {
						colour = "0 0.5 1";
					}
					//print the pieces
					pieceCoordinate t = pieceorder.elementAt(y);
					buffprint.println("Transform {");
					buffprint.println("  translation " + (((t.startx+(t.width/2)))/reducer) + " " + (((t.starty+(t.height/2))/reducer))  + " " + ((z/2))/reducer);
					buffprint.println("  children [ ");
					buffprint.println("    Shape { ");
					buffprint.println("      appearance Appearance { ");
					buffprint.println("        material Material { ");			
					buffprint.println("          diffuseColor " + colour);
					buffprint.println("        }");
					buffprint.println("      }");
					buffprint.println("      geometry Box {");
					buffprint.println("        size " + ((t.width)/reducer) + " " + ((t.height)/reducer) + " " + (((z/2))/reducer));
					buffprint.println("      }");
					buffprint.println("    }");
					buffprint.println("  ]");
					buffprint.println("}");

					buffprint.println("");
				}

				buffprint.println("Background {");
				buffprint.println("  skyColor 0 0 0");
				buffprint.println("}");

				/*buffprint.println("DEF angled Viewpoint {");
			buffprint.println("  position 0 5 8");
			buffprint.println("  orientation 1 0 0 -0.7");
			buffprint.println("}");*/

				buffprint.close();
				f.close();
			} catch (IOException a) {
				System.err.println(a.getMessage());
				System.exit(0);
			} // end catch*/
		}
	}


}
