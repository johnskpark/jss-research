/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 * Include the sorting algorithm
 * 
 * This can be considered as a simulation model of JSP
 */

package jsp;

/**
 *
 * @author nguyensu
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultiD_Sorter {

    public static void sort(final double[][] toSort, final int index) {
        List list = Arrays.asList(toSort);
        Collections.sort(list, new Comparator() {
        public int compare(double[] a, double[] b) {
        return Double.compare(a[index], b[index]);
    }
        @Override
        public int compare(Object o1, Object o2) {
            double[] a = (double[])o1;
            double[] b = (double[])o2;
            return compare(a,b);
        }
        });
    }
}
