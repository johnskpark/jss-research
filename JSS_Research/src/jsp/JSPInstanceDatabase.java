/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 */

package jsp;

    public class JSPInstanceDatabase {
        public static String[] Instances = {"JSP",
        "DMU1","DMU2","DMU3","DMU4","DMU5","DMU6","DMU7","DMU8","DMU9","DMU10",
        "DMU11","DMU12","DMU13","DMU14","DMU15","DMU16","DMU17","DMU18","DMU19","DMU20",
        "DMU21","DMU22","DMU23","DMU24","DMU25","DMU26","DMU27","DMU28","DMU29","DMU30",
        "DMU31","DMU32","DMU33","DMU34","DMU35","DMU36","DMU37","DMU38","DMU39","DMU40",
        "DMU41","DMU42","DMU43","DMU44","DMU45","DMU46","DMU47","DMU48","DMU49","DMU50",
        "DMU51","DMU52","DMU53","DMU54","DMU55","DMU56","DMU57","DMU58","DMU59","DMU60",
        "DMU61","DMU62","DMU63","DMU64","DMU65","DMU66","DMU67","DMU68","DMU69","DMU70",
        "DMU71","DMU72","DMU73","DMU74","DMU75","DMU76","DMU77","DMU78","DMU79","DMU80",
        "la01","la02","la03","la04","la05","la06","la07","la08","la09","la10",
        "la11","la12","la13","la14","la15","la16","la17","la18","la19","la20",
        "la21","la22","la23","la24","la25","la26","la27","la28","la29","la30",
        "la31","la32","la33","la34","la35","la36","la37","la38","la39","la40",
        "orb1","orb2","orb3","orb4","orb5","orb6","orb7","orb8","orb9","orb10",
        "ta01","ta02","ta03","ta04","ta05","ta06","ta07","ta08","ta09","ta10",
        "ta11","ta12","ta13","ta14","ta15","ta16","ta17","ta18","ta19","ta20",
        "ta21","ta22","ta23","ta24","ta25","ta26","ta27","ta28","ta29","ta30",
        "ta31","ta32","ta33","ta34","ta35","ta36","ta37","ta38","ta39","ta40",
        "ta41","ta42","ta43","ta44","ta45","ta46","ta47","ta48","ta49","ta50",
        "ta51","ta52","ta53","ta54","ta55","ta56","ta57","ta58","ta59","ta60",
        "ta61","ta62","ta63","ta64","ta65","ta66","ta67","ta68","ta69","ta70",
        "ta71","ta72","ta73","ta74","ta75","ta76","ta77","ta78","ta79","ta80",
        "mt06","mt10","mt20","abz5","abz6","la21_","la22_","la23_","la24_"
    };

    public static double[] BestKnownCmax = {0,
        2501,2651,2731,2601,2749,2834,2677,2901,2739,2716,  //DMU - instances (1-80)
        3395,3418,3681,3394,3332,3726,3697,3844,3650,3604,
        4380,4725,4668,4648,4164,4647,4848,4692,4691,4732,
        5640,5927,5728,5385,5635,5621,5851,5713,5747,5577,
        2839,3066,3121,3112,2930,3424,3353,3317,3369,3379,
        3839,4012,4108,4165,4099,4366,4182,4214,4199,4259,
        4886,5004,5049,5130,5072,5357,5484,5423,5419,5492,
        6050,6223,5935,6015,6010,6329,6399,6508,6593,6435,
        666,655,597,590,593,926,890,863,951,958,            //la - instances (81-120)
        1222,1039,1150,1292,1207,945,784,848,842,902,
        1046,927,1032,935,977,1218,1235,1216,1152,1355,
        1784,1850,1719,1721,1888,1268,1397,1196,1233,1222,
        1059,888,1005,1005,887,1010,397,899,934,944,        //orb - instances (121-130)
        1231,1244,1218,1175,1224,1238,1227,1217,1274,1241,  //ta -instances (131-210)
        1323,1351,1282,1345,1304,1302,1462,1369,1297,1318,
        1539,1511,1472,1602,1504,1539,1616,1591,1514,1473,
        1764,1774,1778,1828,2007,1819,1771,1673,1795,1631,
        1859,1867,1809,1927,1997,1940,1789,1912,1915,1807,
        2760,2756,2717,2839,2679,2781,2943,2885,2655,2723,
        2868,2869,2755,2702,2725,2845,2825,2784,3071,2995,
        5464,5181,5568,5339,5392,5342,5436,5394,5358,5193,
        55,930,1165,                                        //mt - instances (211-213)
        1234,943,                                            //abz - instances (214-215)
        -1,-1,-1,-1
    };

    public static int[] RandomSeed = { 26633, 66758 ,82732 ,82914 ,95521 ,18888 ,
            58562 ,33965 ,75225, 23538, 68043, 59047, 34289, 14021, 87558, 37487, 63978,
            37204 ,43385, 76139, 13412 ,34429 ,44121 ,71721 ,33151 ,29012,
            31344 ,55124 ,14516, 85128,42614,81642,	80277,	68944,	35293,	77467,	99582,
            23608,	83282,	59001,	98404,	86610,	54636,	87506,	80376,	19684,
            26262,	1686,	8545,	55655,	44852,	57930,	92639,	50937,	55376,
            39702,	5454,	26178,	59471,	23131,	85626,	51669,	46265,	60028,	99234,
            59863,	58072,	49135,	91403,	88148,	62683,	37656,	48579,	39940,	61642,
            86494,	84390,	94243,	50368,	62241,	6046,	56906,	24588,	70177,	97080,	67890,
            30832,	57496,	92857,	45921,	25474,	97013,	54993,	89949,	4933,	44917,	16031,
            77070,	28548,	83622

    };
}
