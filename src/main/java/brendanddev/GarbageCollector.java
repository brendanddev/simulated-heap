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
        // Mark phase
        for (int rootAddr : rootSet.getRoots()) {
            mark(rootAddr);
        }

        // Sweep phase
        sweep();
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

    /**
     * Performs the sweep phase of the mark and sweep garbage collector.
     * 
     * This method iterates through all memory blocks in the heap and frees any allocated blocks
     * that were not marked as reachable (garbage) and resets the mark flag on all other blocks
     * for the next collection cycle.
     */
    private void sweep() {
        for (MemoryBlock block : heap.getBlocks()) {
            // Check if the block is allocated but not marked as reachable
            if (!block.isFree() && !block.isMarked()) {
                // Free unmarked allocated blocks in the heap
                heap.free(block.getStart());
            } else {
                // The block is either free or still reachable
                // Reset the mark flag for future garbage collection cycles
                block.unmark();
            }
        }
    }


    
}
