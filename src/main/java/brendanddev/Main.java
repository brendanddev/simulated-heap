package brendanddev;

/**
 * Demonstrates the SimulatedHeap 
 */
public class Main {


    public static void main(String[] args) {

        // Created a SimulatedHeap with 32 bytes of memory
        SimulatedHeap heap = new SimulatedHeap(32);

        // Set allocation strategy to BEST_FIT
        heap.setAllocationStrategy(AllocationStrategy.BEST_FIT);

        // Create the garbage collector
        GarbageCollector gc = new GarbageCollector(heap, heap.getRootSet());

        // Allocate memory into first and second blocks
        int a = heap.malloc(8); 
        int b = heap.malloc(8);

        // Add only the first block to the root set
        heap.getRootSet().add(a);
        heap.printHeap();

        // Allocate another 4 bytes of memory and free the second block
        int c = heap.malloc(4);
        heap.free(b);

        // At this point 'b' and 'c' are not in the root set, so they are garbage
        // Run gc to clean them up
        gc.collect();

        // Print the heap after garbage collection
        heap.printHeap();

        // Remove the first block 'a' from the root set
        heap.getRootSet().remove(a);

        // Run gc again
        gc.collect();

        heap.printHeap();












        // // Allocates 8 bytes of memory (first block)
        // int a = heap.malloc(8);

        // // Allocates another 8 bytes of memory (second block)
        // int b = heap.malloc(8);

        // // At this point:
        // // [0-7: allocated] [8-15: allocated] [16-31: free]
        // heap.printHeap();

        // // Free the first 8 byte block
        // heap.free(a);

        // // Now:
        // // [0-7: free] [8-15: allocated] [16-31: free]
        // // Fragmentation occurs, two free blocks are separated by an allocated block
        // heap.printHeap();

        // // Allocate another 4 bytes of memory
        // int c = heap.malloc(4);

        // // The allocator will put this 4 byte block inside the first free region (0-7),
        // // splitting it into [0-3: allocated] [4-7: free]
        // // Current state:
        // // [0-3: allocated] [4-7: free] [8-15: allocated] [16-31: free]
        // heap.printHeap();

        // // Free the second 8 byte block
        // heap.free(b);

        // // Now:
        // // [0-3: allocated] [4-7: free] [8-15: free] [16-31: free]
        // // Last three blocks will be merged into one big free block (coalescing)
        // heap.printHeap();

        // // Free the 4 byte block at the beginning
        // heap.free(c);

        // // Now:
        // // [0-3: free] [4-7: free] [8-15: free] [16-31: free]
        // // Entire heap is free again
        // heap.printHeap();
        


    }



}