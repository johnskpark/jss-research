package ec.app.TwoDStripPacking.BestFit;

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
public class Piece {
	double width,height;
	int number;
	double blame;
        double score;
        double previousX;
        double previousY;
       

	public Piece(double w, double h, int n) {
		width = w;
		height = h;
		number = n;
		blame = 0;
                previousX = 0;
                previousY = 0;
	}
	public void incrementBlame() {
		blame += getArea();
	}
	public Piece clone() {
		Piece p = new Piece(this.width, this.height, this.number);
		p.blame = this.blame;
                p.previousX = this.previousX;
                p.previousY = this.previousY;
		return p;
	}

	public Piece cloneOtherOrientation() {
		Piece p = new Piece(this.height, this.width, this.number);
		p.blame = this.blame;
		return p;
	}
	public double getArea() {
		return width*height;
	}
	public void print() {
		System.out.println(width + " " + height + " " + number + " " + blame);
	}
}
