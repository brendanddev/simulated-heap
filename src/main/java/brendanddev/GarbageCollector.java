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

    // Collect garbage
    public void collect() {
    }

    /**
     * Recursively marks a memory block and all blocks reachable from it.
     * 
     * This is the mark phase of a mark and sweep garbage collector.
     * If the block is null, already marked, or free, it returns immediately.
     * 
     * @param address The starting address of the memory block to mark as reachable.
     */
    public void mark(int address) {

        MemoryBlock block = heap.findBlock(address);
        // Base cases
        if (block == null || block.isMarked() || block.isFree()) {
            return;
        }

        // Mark current block as reachable
        block.mark();

        // Recursively mark all referenced blocks by recursing on their linked addresses
        for (int refAddr : block.getReferences()) {
            mark(refAddr);
        }
    }


    
}
