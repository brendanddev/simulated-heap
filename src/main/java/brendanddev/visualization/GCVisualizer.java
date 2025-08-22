package brendanddev.visualization;

import java.util.List;
import brendanddev.heap.SimulatedHeap;
import brendanddev.heap.MemoryBlock;

/**
 * Provides methods to display basic statistics about the SimulatedHeap after or during garbage collection.
 * It reports live memory, garbage memory, free memory, and memory utilization.
 */
public class GCVisualizer {

    private final SimulatedHeap heap;

    /**
     * Constructs an instance of the GCVisualizer.
     * 
     * @param heap The SimulatedHeap instance to visualize.
     */
    public GCVisualizer(SimulatedHeap heap) {
        this.heap = heap;
    }

    /**
     * Prints basic statistics about the heap memory usage.
     */
    public void printHeapStats() {
        List<MemoryBlock> blocks = heap.getBlocks();

        // Counters for statistics
        int totalHeapSize = heap.getHeapSize();
        int allocatedLive = 0;
        int allocatedGarbage = 0;
        int freeMemory = 0;

        for (MemoryBlock block : blocks) {
            if (block.isFree()) {
                freeMemory += block.getSize();
            } else if (block.isMarked()) {
                allocatedLive += block.getSize();
            } else {
                allocatedGarbage += block.getSize();
            }
        }

        System.out.println("\nGC HEAP STATISTICS");
        System.out.println("═".repeat(40));
        System.out.printf("Total Heap Size: %d bytes%n", totalHeapSize);
        System.out.printf("Live Allocated: %d bytes%n", allocatedLive);
        System.out.printf("Garbage Allocated: %d bytes%n", allocatedGarbage);
        System.out.printf("Free Memory: %d bytes%n", freeMemory);
        double utilization = (double) allocatedLive / totalHeapSize * 100;
        System.out.printf("Memory Utilization: %.1f%%%n", utilization);
        System.out.println("═".repeat(40));
    }
    
}
