package brendanddev;

import java.util.HashSet;
import java.util.Set;

/**
 * A simulated GarbageCollector that manages memory in a simulated heap.
 */
public class GarbageCollector {

    private final SimulatedHeap heap;
    private final RootSet rootSet;

    /**
     * Constructs an instance of GarbageCollector.
     * 
     * @param heap The simulated heap to manage.
     * @param rootSet The root set containing references to live objects.
     */
    public GarbageCollector(SimulatedHeap heap, RootSet rootSet) {
        this.heap = heap;
        this.rootSet = rootSet;
    }

    // Collect garbage
    public void collect() {
        Set<Integer> reachableAddresses = new HashSet<>();
    }

    // Mark memoiry blocks as reachable
    public void mark(int address, Set<Integer> reachableAddresses) {
    }


    
}
