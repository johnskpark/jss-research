package ec.app.TwoDStripPacking.BestFit;

import java.util.Comparator;
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
public class PieceComparator implements Comparator<Piece> {

	public int compare(Piece e1, Piece e2) {
		if (e1.width < e2.width) {
			return 1;
		} else if (e1.width > e2.width) {
			return -1;
		} else {
			if (e1.height < e2.height) {
				return 1;
			} else if (e1.height > e2.height) {
				return -1;
			} else {
				if (e1.number < e2.number) {
					return 1;
				} else if (e1.number > e2.number) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}
