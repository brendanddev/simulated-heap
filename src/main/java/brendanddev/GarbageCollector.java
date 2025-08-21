package brendanddev;

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
    
}
