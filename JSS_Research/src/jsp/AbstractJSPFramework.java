/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */
package jsp;

/**
 *
 * @author nguyensu
 */
public abstract class AbstractJSPFramework {
    public abstract void setNonDelayFactor(double nd);
    public abstract void setInitalPriority(Machine M);
    public abstract int getCriticalMachineID();
    public abstract void calculatePriority(Machine M);
    public abstract void setPriorityType(Machine.priorityType pt);
    public abstract int getBottleneckMachineID();
    public abstract double getCriticalMachineIdleness();
    public abstract Machine[] getMachines();
}
