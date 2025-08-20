package brendanddev;

import java.util.List;

public class HeapVisualizer {


    private final SimulatedHeap heap;

    public HeapVisualizer(SimulatedHeap heap) {
        this.heap = heap;
    }

    /**
     * Prints a memory map showing the layout with ASCII art
     */
    public void printMemoryMap() {

        // Access blocks through the heap
        List<MemoryBlock> blocks = heap.getBlocks();

        System.out.println("\nMEMORY MAP");
        System.out.println("═".repeat(60));
        
        for (MemoryBlock block : blocks) {
            String status = block.free ? "FREE" : "USED";
            String symbol = block.free ? "░" : "█";
            
            // Create a visual bar proportional to block size
            int barLength = Math.max(1, block.size / Math.max(1, heap.getHeapSize() / 40));
            String bar = symbol.repeat(barLength);
            
            System.out.printf("0x%04X |%s| %s (%d bytes)%n", 
                            block.start, bar, status, block.size);
        }
        System.out.println("═".repeat(60));
    }

    
}
