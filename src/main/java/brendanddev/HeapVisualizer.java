package brendanddev;

import java.util.List;

public class HeapVisualizer {


    private final SimulatedHeap heap;

    public HeapVisualizer(SimulatedHeap heap) {
        this.heap = heap;
    }


    /**
     * Creates a visual bar chart representation of heap memory.
     * Shows allocated blocks as █ and free blocks as ░
     */
    public void printHeapVisual() {

        // Access blocks through the heap
        List<MemoryBlock> blocks = heap.getBlocks();
        
        System.out.println("\nHEAP MEMORY VISUALIZATION");
        System.out.println("═".repeat(60));
        
        // Print header with scale
        System.out.print("Address: ");
        for (int i = 0; i < Math.min(heap.getHeapSize(), 50); i += 5) {
            System.out.printf("%-5d", i);
        }
        System.out.println();
        
        // Print visual representation
        System.out.print("Memory:  ");
        for (int addr = 0; addr < Math.min(heap.getHeapSize(), 50); addr++) {
            boolean isAllocated = false;
            for (MemoryBlock block : blocks) {
                if (addr >= block.start && addr < block.start + block.size && !block.free) {
                    isAllocated = true;
                    break;
                }
            }
            System.out.print(isAllocated ? "█" : "░");
        }
        if (heap.getHeapSize() > 50) {
            System.out.print("... (+" + (heap.getHeapSize() - 50) + " more bytes)");
        }
        System.out.println();
        System.out.println("Legend: █ = Allocated, ░ = Free");
        
        printHeapDetailed();
    }

    /**
     * Prints detailed information about each memory block
     */
    public void printHeapDetailed() {

        // Access blocks through the heap
        List<MemoryBlock> blocks = heap.getBlocks();

        System.out.println("\nDETAILED BLOCK INFORMATION");
        System.out.println("═".repeat(60));
        System.out.printf("%-6s %-8s %-8s %-10s %-10s%n", "Block#", "Start", "Size", "Status", "End");
        System.out.println("─".repeat(60));
        
        for (int i = 0; i < blocks.size(); i++) {
            MemoryBlock block = blocks.get(i);
            String status = block.free ? "[F] FREE" : "[A] ALLOCATED";
            int endAddr = block.start + block.size - 1;
            
            System.out.printf("%-6d %-8d %-8d %-10s %-10d%n", 
                            i, block.start, block.size, status, endAddr);
        }
        printMemoryStats();
    }

    /**
     * Prints memory usage statistics
     */
    public void printMemoryStats() {

        // Access blocks through the heap
        List<MemoryBlock> blocks = heap.getBlocks();

        int totalFree = 0;
        int totalAllocated = 0;
        int freeBlocks = 0;
        int allocatedBlocks = 0;
        
        for (MemoryBlock block : blocks) {
            if (block.free) {
                totalFree += block.size;
                freeBlocks++;
            } else {
                totalAllocated += block.size;
                allocatedBlocks++;
            }
        }
        
        double utilization = (double) totalAllocated / heap.getHeapSize() * 100;
        
        System.out.println("\nMEMORY STATISTICS");
        System.out.println("═".repeat(40));
        System.out.printf("Total Heap Size:    %d bytes%n", heap.getHeapSize());
        System.out.printf("Allocated Memory:   %d bytes (%d blocks)%n", totalAllocated, allocatedBlocks);
        System.out.printf("Free Memory:        %d bytes (%d blocks)%n", totalFree, freeBlocks);
        System.out.printf("Memory Utilization: %.1f%%%n", utilization);
        System.out.printf("Fragmentation:      %d free blocks%n", freeBlocks);
        
        // Visual progress bar for memory usage
        System.out.print("Usage: [");
        int barLength = 20;
        int filledLength = (int) (utilization / 100.0 * barLength);
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                System.out.print("█");
            } else {
                System.out.print("░");
            }
        }
        System.out.printf("] %.1f%%%n", utilization);
        System.out.println("═".repeat(40));
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
