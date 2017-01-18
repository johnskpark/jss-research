/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.QCSP.Core;

/**
 *
 * @author nguyensu
 */
public class KimPark1 {
///////////////////////////////////////////////////////////////////////////////////////
//  Ph. D. Young-Man Park
///////////////////////////////////////////////////////////////////////////////////////
/*
Alpha1 is 3, alpha2 is 0.
rk: The earliest available time of QC k is 0.

Description of Input Data
{li,                //The location of task i
  Type of task,      //Deck=1 or Hold=2
  Pi,                //The time required to perform task i
  Type of operation, //loading=1 or discharging=2
  {0,0,0,0,0},       //Do not consider 
  0,                 //Do not consider
}
 *
 */
    public static int[][] getIN(int instance){
        /* --------(QC1:1, QC2:6)----------------------------------------- */
        //* ------- number of task: 10, Problem no: 13 -------*//
        if (instance == 13) {int[][] prob = { {2,2,41,1}, {10,1,19,1}, {3,1,6,2}, {2,2,12,2}, {6,1,37,2},
         {2,1,34,1}, {7,1,48,2}, {7,2,10,1}, {3,2,56,2}, {5,2,3,2},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 14 -------*//
        if (instance == 14) {int[][] prob = { {4,2,22,1}, {2,2,48,1}, {5,1,58,2}, {10,2,50,2}, {9,1,51,2},
         {3,1,41,1}, {7,2,10,1}, {1,1,22,1}, {4,1,24,2}, {5,1,21,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 15 -------*//
        if (instance == 15) {int[][] prob = { {7,1,29,2}, {5,2,44,1}, {8,1,43,1}, {10,2,36,2}, {10,1,31,1},
         {8,1,7,2}, {2,2,53,1}, {1,2,34,2}, {1,2,36,1}, {8,2,12,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 16 -------*//
        if (instance == 16) {int[][] prob = { {3,1,22,1}, {5,1,28,1}, {2,2,38,2}, {8,2,6,2}, {10,2,42,1},
         {9,1,27,1}, {1,2,1,1}, {2,1,11,2}, {10,1,9,2}, {4,2,3,2},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 17 -------*//
        if (instance == 17) {int[][] prob = { {1,2,15,1}, {1,1,49,2}, {10,1,2,1}, {5,2,19,2}, {9,1,8,1},
         {8,1,49,2}, {4,2,31,2}, {8,2,30,1}, {7,2,21,2}, {10,2,59,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 18 -------*//
        if (instance == 18) {int[][] prob = { {4,1,25,1}, {10,2,16,1}, {6,2,44,2}, {8,1,9,2}, {1,1,1,1},
         {7,1,30,2}, {9,2,13,2}, {3,1,39,1}, {10,1,39,2}, {2,2,17,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 19 -------*//
        if (instance == 19) {int[][] prob = { {2,1,33,1}, {6,1,23,1}, {7,2,50,2}, {3,1,42,1}, {9,1,2,2},
         {10,2,53,1}, {1,1,50,2}, {2,1,10,2}, {6,2,26,1}, {3,2,47,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 20 -------*//
        if (instance == 20) {int[][] prob = { {8,2,33,1}, {10,1,30,2}, {7,2,13,1}, {6,2,50,1}, {2,1,1,2},
         {9,2,4,2}, {10,2,7,2}, {8,1,26,1}, {9,1,55,2}, {5,2,32,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 21 -------*//
        if (instance == 21) {int[][] prob = { {7,2,56,2}, {6,2,9,1}, {3,2,37,2}, {2,1,23,1}, {6,1,27,2},
         {5,2,40,1}, {4,1,51,1}, {9,1,6,2}, {9,2,26,1}, {8,1,25,1},
        }; return prob;}
        //* ------- number of task: 10, Problem no: 22 -------*//
        if (instance == 22) {int[][] prob = { {5,1,60,1}, {4,1,38,2}, {9,2,51,2}, {4,1,6,1}, {3,1,8,2},
         {3,2,46,2}, {4,2,33,2}, {3,1,45,1}, {1,2,34,1}, {6,2,21,2},
        }; return prob;}

        //* --------(QC1:1, QC2:8)----------------------------------------- *//

        //* ------- number of task: 15, Problem no: 23 -------*//
        if (instance == 23) {int[][] prob = { {12,2,11,2}, {6,1,25,2}, {15,2,35,1}, {9,1,27,2}, {14,2,45,2},
         {14,1,53,2}, {11,2,7,1}, {14,1,10,1}, {8,1,8,2}, {13,1,8,2},
         {5,1,21,2}, {3,1,57,2}, {3,2,6,2}, {5,1,17,1}, {7,2,34,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 24 -------*//
        if (instance == 24) {int[][] prob = { {11,1,9,1}, {14,1,3,2}, {15,1,59,2}, {14,2,18,2}, {1,1,24,2},
         {11,2,58,1}, {9,2,2,2}, {6,1,34,1}, {10,2,32,1}, {13,1,26,2},
         {7,1,58,1}, {7,2,52,2}, {5,2,3,1}, {8,1,37,2}, {6,2,10,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 25 -------*//
        if (instance == 25) {int[][] prob = { {5,2,53,2}, {7,2,43,2}, {15,1,53,1}, {8,2,24,1}, {13,2,56,2},
         {3,1,39,2}, {4,1,32,1}, {2,2,6,2}, {12,1,15,1}, {11,2,59,1},
         {15,2,37,1}, {14,1,3,2}, {4,2,14,1}, {8,1,13,1}, {9,1,21,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 26 -------*//
        if (instance == 26) {int[][] prob = { {14,2,39,1}, {11,2,33,1}, {6,2,1,2}, {13,2,26,1}, {4,1,41,2},
         {10,1,60,2}, {3,2,2,1}, {4,2,29,2}, {13,2,37,2}, {2,2,22,2},
         {15,1,15,2}, {7,2,44,2}, {9,1,22,1}, {8,2,29,1}, {8,1,7,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 27 -------*//
        if (instance == 27) {int[][] prob = { {4,1,28,2}, {10,2,44,2}, {7,2,32,1}, {10,1,43,1}, {15,2,45,2},
         {4,1,6,1}, {11,2,23,2}, {9,2,29,1}, {2,1,36,2}, {6,1,19,1},
         {15,1,6,2}, {1,2,59,1}, {1,2,13,2}, {8,1,11,2}, {4,2,24,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 28 -------*//
        if (instance == 28) {int[][] prob = { {3,1,36,2}, {1,1,28,2}, {4,2,30,2}, {14,1,21,1}, {12,1,9,1},
         {2,1,4,2}, {11,1,8,1}, {7,2,7,2}, {6,1,10,2}, {5,1,60,2},
         {8,1,16,1}, {9,1,26,2}, {1,2,17,2}, {15,2,3,1}, {6,2,57,1},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 29 -------*//
        if (instance == 29) {int[][] prob = { {4,1,27,1}, {14,2,46,1}, {2,1,34,2}, {6,1,42,1}, {5,1,53,1},
         {12,1,36,1}, {1,2,22,1}, {1,2,47,2}, {11,1,35,1}, {4,2,13,1},
         {14,1,55,2}, {7,1,18,1}, {12,1,17,2}, {7,2,23,1}, {3,2,50,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 30 -------*//
        if (instance == 30) {int[][] prob = { {9,2,60,1}, {2,2,2,2}, {15,1,54,2}, {14,2,22,1}, {10,1,60,2},
         {14,2,38,2}, {10,1,52,1}, {15,2,16,2}, {1,1,33,2}, {9,1,6,1},
         {11,1,41,1}, {11,2,56,2}, {15,1,34,1}, {4,2,44,1}, {4,1,59,1},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 31 -------*//
        if (instance == 31) {int[][] prob = { {1,2,15,2}, {7,2,54,2}, {1,1,17,2}, {15,1,10,1}, {14,1,37,1},
         {10,2,40,1}, {5,2,7,2}, {7,1,47,1}, {3,2,21,1}, {2,2,22,1},
         {8,2,2,1}, {4,1,8,2}, {6,1,5,2}, {7,1,57,2}, {8,1,9,2},
        }; return prob;}
        //* ------- number of task: 15, Problem no: 32 -------*//
        if (instance == 32) {int[][] prob = { {9,2,32,1}, {10,2,3,1}, {5,2,10,1}, {10,1,19,2}, {7,2,47,1},
         {2,1,39,2}, {8,1,18,1}, {11,2,3,1}, {13,1,13,1}, {6,1,53,2},
         {4,1,56,1}, {1,1,17,2}, {8,1,29,2}, {7,1,10,1}, {11,1,28,1},
        }; return prob;}

        //* --------(QC1:1, QC2:7, QC:14)----------------------------------------- *//

        //* ------- number of task: 20, Problem no: 33 -------*//
        if (instance == 33) {int[][] prob = { {18,2,25,1}, {9,1,38,1}, {19,1,26,2}, {7,2,11,1}, {12,2,26,2},
         {8,2,27,1}, {8,1,55,2}, {12,1,9,2}, {14,2,52,2}, {5,2,29,1},
         {3,2,3,1}, {18,1,36,1}, {10,2,14,2}, {5,2,34,2}, {8,2,25,2},
         {2,1,13,2}, {7,1,34,2}, {1,1,52,1}, {14,1,37,2}, {20,1,32,1},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 34 -------*//
        if (instance == 34) {int[][] prob = { {2,2,54,1}, {14,2,23,2}, {14,2,35,1}, {19,1,30,2}, {13,1,51,2},
         {6,2,8,1}, {7,1,1,2}, {20,1,51,1}, {12,2,31,2}, {4,2,59,2},
         {16,2,54,1}, {9,2,34,2}, {11,1,22,2}, {6,2,30,2}, {15,2,39,2},
         {19,1,44,1}, {9,1,24,2}, {8,2,32,1}, {2,2,46,2}, {11,2,23,2},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 35 -------*//
        if (instance == 35) {int[][] prob = { {3,2,13,2}, {2,1,47,1}, {15,1,10,1}, {19,2,36,2}, {2,2,9,2},
         {15,1,45,2}, {18,1,45,2}, {10,2,4,1}, {11,2,51,2}, {19,2,55,1},
         {5,2,56,2}, {6,2,18,1}, {4,1,49,1}, {9,2,33,2}, {14,1,37,1},
         {7,1,10,1}, {1,1,44,2}, {12,2,20,2}, {6,2,17,2}, {5,1,55,2},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 36 -------*//
        if (instance == 36) {int[][] prob = { {10,1,34,2}, {10,2,29,1}, {19,2,47,1}, {11,1,20,1}, {12,2,35,1},
         {5,1,21,2}, {15,1,23,2}, {8,2,46,2}, {16,1,24,1}, {15,2,33,1},
         {20,1,43,1}, {8,1,49,2}, {4,2,45,1}, {7,2,6,1}, {7,1,28,1},
         {13,2,17,1}, {15,2,44,2}, {3,2,24,1}, {9,2,59,1}, {17,1,23,2},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 37 -------*//
        if (instance == 37) {int[][] prob = { {17,1,5,2}, {7,2,4,2}, {15,2,49,1}, {12,1,29,2}, {8,1,18,2},
         {13,2,9,2}, {6,1,59,2}, {18,2,2,2}, {11,1,11,2}, {10,1,30,1},
         {2,2,14,2}, {14,1,33,1}, {11,2,18,1}, {18,1,25,1}, {2,1,18,2},
         {4,2,10,2}, {8,1,43,1}, {19,1,51,2}, {9,2,14,1}, {1,2,43,1},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 38 -------*//
        if (instance == 38) {int[][] prob = { {11,1,15,1}, {2,1,48,2}, {6,1,18,2}, {19,2,57,2}, {8,1,56,2},
         {6,2,7,1}, {14,1,9,1}, {9,1,21,2}, {7,2,20,1}, {18,1,9,1},
         {13,2,15,1}, {5,2,50,2}, {16,2,21,2}, {13,1,52,2}, {15,2,21,2},
         {15,1,60,2}, {1,1,25,2}, {1,2,26,2}, {4,1,14,1}, {11,2,40,1},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 39 -------*//
        if (instance == 39) {int[][] prob = { {12,2,34,2}, {1,2,17,1}, {11,1,60,1}, {19,2,30,2}, {5,1,9,1},
         {8,1,24,2}, {11,2,15,1}, {3,2,22,2}, {17,1,4,2}, {17,1,42,1},
         {15,2,17,1}, {20,1,19,1}, {13,2,41,1}, {18,1,2,1}, {16,1,19,1},
         {19,2,38,1}, {11,2,22,2}, {14,2,26,1}, {14,1,24,1}, {10,1,16,1},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 40 -------*//
        if (instance == 40) {int[][] prob = { {12,2,29,1}, {12,1,7,1}, {18,2,37,2}, {15,2,53,2}, {7,2,36,2},
         {10,2,13,1}, {17,2,27,2}, {3,2,55,2}, {1,2,6,2}, {14,2,19,1},
         {19,1,28,2}, {6,1,50,2}, {11,2,6,2}, {15,1,13,2}, {17,2,23,1},
         {8,2,22,2}, {19,2,29,2}, {7,1,10,2}, {3,1,36,1}, {9,1,34,1},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 41 -------*//
        if (instance == 41) {int[][] prob = { {18,1,2,2}, {12,1,38,2}, {7,1,17,2}, {2,2,48,2}, {5,1,37,2},
         {15,2,15,1}, {14,1,24,2}, {9,2,8,1}, {1,1,31,1}, {16,2,14,1},
         {15,1,42,2}, {8,2,44,1}, {6,1,22,2}, {13,1,28,2}, {11,2,33,2},
         {13,2,6,1}, {7,1,47,1}, {12,1,18,1}, {1,1,29,2}, {10,1,59,2},
        }; return prob;}
        //* ------- number of task: 20, Problem no: 42 -------*//
        if (instance == 42) {int[][] prob = { {14,2,20,2}, {14,1,6,2}, {15,1,1,2}, {17,2,52,2}, {9,2,23,1},
         {13,2,56,1}, {3,2,21,1}, {17,2,9,1}, {1,1,59,1}, {14,1,15,1},
         {14,2,56,1}, {13,1,36,2}, {7,2,2,1}, {6,2,17,1}, {16,1,23,2},
         {2,1,9,1}, {19,1,55,2}, {11,1,40,2}, {10,2,21,2}, {20,2,12,2},
        }; return prob;}

        //* --------(QC1:1, QC2:9, QC3:17)----------------------------------------- *//

        //* ------- number of task: 25, Problem no: 43 -------*//
        if (instance == 43) {int[][] prob = { {13,2,16,2}, {19,1,23,2}, {9,1,8,1}, {21,1,31,1}, {15,1,4,1},
         {1,1,2,1}, {6,2,15,2}, {6,1,46,2}, {6,1,30,1}, {14,2,58,2},
         {10,1,49,1}, {13,1,13,1}, {19,2,22,1}, {7,1,56,1}, {8,2,13,2},
         {10,2,44,2}, {18,1,59,2}, {13,1,58,2}, {13,2,56,1}, {11,2,13,2},
         {12,1,28,2}, {10,1,31,2}, {2,2,50,2}, {16,1,52,1}, {15,2,60,1},
        }; return prob;}
        //* ------- number of task: 25, Problem no: 44 -------*//
        if (instance == 44) {int[][] prob = { {19,2,2,2}, {4,2,60,1}, {21,2,19,1}, {9,2,22,2}, {19,1,19,1},
         {4,2,39,2}, {17,2,55,2}, {19,2,8,1}, {3,2,7,2}, {1,1,23,1},
         {11,1,18,1}, {8,1,6,2}, {25,2,36,1}, {17,1,49,1}, {12,2,26,2},
         {18,2,44,1}, {23,1,40,2}, {24,1,49,2}, {2,1,39,1}, {18,1,12,2},
         {13,2,57,2}, {7,2,52,1}, {4,1,11,2}, {20,1,44,2}, {9,2,50,1},
        }; return prob;}
        //* ------- number of task: 25, Problem no: 45 -------*//
         if (instance == 45) {int[][] prob = {{1,1,46,1}, {8,2,51,2}, {4,1,43,2}, {22,2,39,1}, {17,1,13,2},
         {9,2,44,1}, {16,1,43,1}, {21,2,2,1}, {18,1,29,2}, {17,2,26,2},
         {6,2,31,1}, {12,2,25,2}, {13,2,30,1}, {5,2,35,1}, {25,1,45,1},
         {25,1,24,2}, {3,2,32,1}, {23,2,59,2}, {24,2,60,1}, {4,2,9,1},
         {14,1,43,1}, {16,2,12,1}, {2,1,36,1}, {24,1,16,2}, {8,2,3,1},
        }; return prob;}
        //* ------- number of task: 25, Problem no: 46 -------*//
        if (instance == 46) {int[][] prob = { {13,1,3,1}, {24,2,29,1}, {18,2,19,1}, {18,2,10,2}, {8,2,24,2},
         {6,1,10,2}, {3,1,5,1}, {8,2,42,1}, {16,1,52,2}, {12,2,41,2},
         {9,2,11,2}, {25,2,44,1}, {12,1,48,1}, {23,2,11,2}, {18,1,47,2},
         {21,1,6,2}, {2,1,58,2}, {9,1,22,1}, {1,2,32,2}, {13,2,20,1},
         {14,1,2,1}, {19,2,49,1}, {10,1,41,1}, {24,2,15,2}, {21,1,16,1},
        }; return prob;}
        //* ------- number of task: 25, Problem no: 47 -------*//
        if (instance == 47) {int[][] prob = { {18,2,53,1}, {9,2,26,2}, {2,2,40,1}, {19,1,13,2}, {19,2,4,2},
         {16,2,9,1}, {3,1,9,2}, {5,2,32,2}, {20,2,10,1}, {12,2,4,2},
         {7,1,55,2}, {11,2,54,1}, {5,1,9,2}, {24,1,48,1}, {24,2,32,1},
         {15,1,44,2}, {5,2,56,1}, {23,1,5,1}, {1,1,44,2}, {17,1,31,1},
         {2,2,47,2}, {9,2,51,1}, {4,1,23,2}, {25,2,53,1}, {11,1,10,1},
        }; return prob;}
        //* ------- number of task: 25, Problem no: 48 -------*//
        if (instance == 48) {int[][] prob = { {4,2,26,2}, {11,2,2,1}, {6,1,54,2}, {10,2,17,2}, {11,1,17,1},
         {22,1,46,2}, {8,2,14,1}, {13,2,6,1}, {15,2,24,1}, {7,2,59,2},
         {5,2,40,1}, {21,1,23,2}, {23,2,19,1}, {23,1,4,1}, {3,2,22,1},
         {4,1,37,2}, {4,2,2,1}, {2,2,36,2}, {25,2,42,1}, {13,1,16,1},
         {10,1,28,2}, {19,1,20,1}, {19,2,38,2}, {17,2,4,1}, {16,1,2,2},
        }; return prob;}
        //* ------- number of task: 25, Problem no: 49 -------*//
        if (instance == 49) {int[][] prob = { {16,2,18,1}, {10,1,54,1}, {17,2,8,1}, {24,1,35,1}, {9,2,51,1},
         {13,2,30,1}, {4,2,1,2}, {25,2,53,2}, {16,2,58,2}, {10,2,26,2},
         {22,1,17,2}, {5,2,28,1}, {19,1,43,1}, {14,1,39,2}, {16,1,37,2},
         {9,1,55,1}, {21,2,9,1}, {17,1,43,1}, {22,1,37,1}, {10,2,22,1},
         {11,1,41,1}, {12,1,39,2}, {13,2,32,2}, {21,1,28,2}, {15,1,51,2},
        }; return prob;}
        System.err.println("no instance");
        return new int[0][0];
    }
}