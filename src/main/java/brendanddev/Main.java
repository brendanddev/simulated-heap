package brendanddev;

/**
 * Demonstrates the SimulatedHeap 
 */
public class Main {


    public static void main(String[] args) {
        // runBasicDemo();
        runGCDemo();
    }

    /**
     * Demonstrates basic memory allocation and deallocation using the SimulatedHeap.
     */
    private static void runBasicDemo() {

        // Create new SimulatedHeap with 32 bytes
        SimulatedHeap heap = new SimulatedHeap(32);
        heap.setAllocationStrategy(AllocationStrategy.FIRST_FIT);

        // Allocates 8 bytes of memory (first block)
        int a = heap.malloc(8);

        // Allocates another 8 bytes of memory (second block)
        int b = heap.malloc(8);

        // At this point:
        // [0-7: allocated] [8-15: allocated] [16-31: free]
        heap.printHeap();

        // Free the first 8 byte block
        heap.free(a);

        // Now:
        // [0-7: free] [8-15: allocated] [16-31: free]
        // Fragmentation occurs, two free blocks are separated by an allocated block
        heap.printHeap();

        // Allocate another 4 bytes of memory
        int c = heap.malloc(4);

        // The allocator will put this 4 byte block inside the first free region (0-7),
        // splitting it into [0-3: allocated] [4-7: free]
        // Current state:
        // [0-3: allocated] [4-7: free] [8-15: allocated] [16-31: free]
        heap.printHeap();

        // Free the second 8 byte block
        heap.free(b);

        // Now:
        // [0-3: allocated] [4-7: free] [8-15: free] [16-31: free]
        // Last three blocks will be merged into one big free block (coalescing)
        heap.printHeap();

        // Free the 4 byte block at the beginning
        heap.free(c);

        // Now:
        // [0-3: free] [4-7: free] [8-15: free] [16-31: free]
        // Entire heap is free again
        heap.printHeap();
    }

    /**
     * Demonstrates the garbage collection process using a simulated heap.
     */
    private static void runGCDemo() {

        // Create a SimulatedHeap with 32 bytes
        SimulatedHeap heap = new SimulatedHeap(32);

        // Set allocation strategy
        heap.setAllocationStrategy(AllocationStrategy.NEXT_FIT);

        // Allocate three blocks
        int a = heap.malloc(8);
        int b = heap.malloc(8);
        int c = heap.malloc(8);

        // Set up references: A -> B
        heap.findBlock(a).addReference(b);
        // C will remain unreferenced -> unreachable

        // Add only A to the root set
        heap.getRootSet().add(a);

        // Create GarbageCollectorVisualizer to visualize garbage collection
        GCVisualizer gcVisualizer = new GCVisualizer(heap);        

        System.out.println("Before GC:");
        heap.printHeap();
        gcVisualizer.printHeapStats();

        // Create GC and run collection
        GarbageCollector gc = new GarbageCollector(heap, heap.getRootSet());
        gc.collect();

        System.out.println("\nAfter GC:");
        heap.printHeap();
        gcVisualizer.printHeapStats();
    }



}