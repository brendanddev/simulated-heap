package brendanddev;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulates a Heap in Java using a byte array.
 * Handles memory allocation and deallocation manually with MemoryBlock objects.
 */
public class SimulatedHeap {

    private final byte[] heap;
    private final List<MemoryBlock> blocks;

    /**
     * Constructs a new SimulatedHeap with the specified size.
     * 
     * @param size The size of the heap in bytes.
     */
    public SimulatedHeap(int size) {
        heap = new byte[size];
        blocks = new ArrayList<>();
        blocks.add(new MemoryBlock(0, size));
    }

    /**
     * Allocates a block of memory of the given size using the first-fit strategy.
     * 
     * @param size The number of bytes to allocate.
     * @return The starting index of the allocated memory block, or null if allocation fails.
     */
    public Integer malloc(int size) {
        // Loop through all blocks to find a free block big enough
        for (MemoryBlock block : blocks) {
            // Check if the block is free and has enough size
            if (block.free && block.size >= size) {
                // If block is larger than requested size, split it
                // Prevents wasting extra memory in a large block
                if (block.size > size) {
                    // Create new block for leftover memory
                    // Its start is right after the allocated portion
                    MemoryBlock newBlock = new MemoryBlock(block.start + size, block.size - size);
                    
                    // Insert new block into list, right after the current block
                    blocks.add(blocks.indexOf(block) + 1, newBlock);
                    // Reduce the size of the original block to match the requested size
                    block.size = size;
                }
                // Mark the block as allocated so it wont be used again until freed
                block.free = false;

                // Return the starting index as the 'pointer' to the allocated block
                return block.start;
            }
        }
        // No suitable block found
        return null;
    }

    public void free(int address) {
        // TODO: Implement memory deallocation logic
    }










    
}
