package brendanddev.gc;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the root set in the simulated heap.
 * 
 * The root set is tracked as a set of memory block starting addresses that are considered reachable
 * by the garbage collector. Blocks in the root set are protected from collection, while blocks not 
 * in the root set and not referenced elsewhere can be collected/freed.
 * 
 * This root set only tracks starting addresses of memory blocks, not the entire block objects.
 */
public class RootSet {

    // Memory block starting addresses that are considered roots
    private final Set<Integer> roots = new HashSet<>();

    /**
     * Adds a block's address to the root set.
     * This means the block is considered reachable for the garbage collector.
     * 
     * @param address The starting index of the memory block to add to the root set.
     */
    public void add(int address) {
        roots.add(address);
    }

    /**
     * Removes a block's address from the root set.
     * Once removed, the block can be collected if nothing else references it.
     *
     * @param address The starting index of the memory block to remove from the root set.
     */
    public void remove(int address) {
        roots.remove(address);
    }

    /**
     * Checks if a block's address is in the root set.
     * 
     * @param address The starting index of the memory block to check.
     * @return true if the address is in the root set, false otherwise.
     */
    public boolean contains(int address) {
        return roots.contains(address);
    }

    /**
     * Clears the root set, removing all addresses.
     * This can be used to reset the root set for garbage collection.
     */
    public void clear() {
        roots.clear();
    }

    /**
     * Returns the current set of root addresses.
     * These are the starting indices of memory blocks considered reachable.
     *
     * @return A set of root memory block addresses.
     */
    public Set<Integer> getRoots() {
        return roots;
    }


    
}
