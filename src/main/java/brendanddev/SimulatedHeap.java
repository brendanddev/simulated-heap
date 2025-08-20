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

    /**
     * Deallocates (frees) a block of memory previosuly allocated at the given address.
     * 
     * @param address The starting index of the memory block to free.
     * @throws IllegalArgumentException if the address is invalid or the block is already free.
     */
    public void free(int address) {
        for (int i = 0; i < blocks.size(); i++) {

            MemoryBlock block = blocks.get(i);

            // Find the block that starts at the given address and is currently allocated
            if (block.start == address && !block.free) {
                // Mark block as free
                block.free = true;

                // Merge (coalesce) with adjacent block if its free
                if (i + 1 < blocks.size() && blocks.get(i + 1).free) {
                    // Grow current block by adding the size of the next block
                    block.size += blocks.get(i + 1).size;
                    // Remove adjacent block since its merged
                    blocks.remove(i + 1);
                }

                // Merge (coalesce) with previous block if its free
                if (i > 0 && blocks.get(i - 1).free) {
                    // Grow previous block to absorb current block
                    blocks.get(i - 1).size += block.size;
                    // Remove the current block since it's now merged into the previous one
                    blocks.remove(i);
                }
                // Finished freeing
                return;
            }
        }
        // If no block was found at the given address, this is an invalid free
        throw new IllegalArgumentException("Invalid free: No allocated block at address " + address);
    }

    /**
     * Writes a byte value to the specified address in the simulated heap.
     * 
     * @param address The index in the heap array where the value should be written.
     * @param value The byte value to write to the heap.
     * @throws IllegalArgumentException if the address is out of bounds of the heap array.
     */
    public void write(int address, byte value) {
        if (address < 0 || address >= heap.length) {
            throw new IllegalArgumentException("Invalid write address: " + address);
        }
        heap[address] = value;
    }

    /**
     * Reads a byte value from the simulated heap at the given address.
     * 
     * @param address The index in the heap array from which to read the value.
     * @return The byte value stored at that address.
     * @throws IllegalArgumentException if the address is out of bounds of the heap array.
     */
    public byte read(int address) {
        if (address < 0 || address >= heap.length) {
            throw new IllegalArgumentException("Invalid read address: " + address);
        }
        return heap[address];
    }

    /**
     * Prints the current state of the heap blocks for debugging.
     */
    public void printHeap() {
        System.out.println("Heap Blocks:");
        for (MemoryBlock block : blocks) {
            System.out.println(block);
        }
    }



}
