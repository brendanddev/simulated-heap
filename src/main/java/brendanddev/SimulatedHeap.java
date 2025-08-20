package brendanddev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulates a Heap in Java using a byte array.
 * Handles memory allocation and deallocation manually with MemoryBlock objects.
 */
public class SimulatedHeap {

    private final HeapVisualizer visualizer = new HeapVisualizer(this);
    private final byte[] heap;
    private final List<MemoryBlock> blocks;
    private final Map<Integer, MemoryBlock> allocations = new HashMap<>();

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
     * Returns the internal list of memory blocks.
     */
    public List<MemoryBlock> getBlocks() {
        return blocks;
    }

    /**
     * Returns the underlying byte array representing the heap memory.
     */
    public byte[] getHeapArray() {
        return heap;
    }

    /** 
     * Returns the total size of the heap in bytes.
     */
    public int getHeapSize() {
        return heap.length;
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

                // Track blocks that have been allocated in map
                allocations.put(block.start, block);

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
        // Get the block from the allocations map
        MemoryBlock block = allocations.get(address);
        if (block == null || block.free) {
            throw new IllegalArgumentException("Invalid free: No allocated block at address " + address);
        }

        // Mark block as free and remove from map
        block.free = true;
        allocations.remove(address);

        // Merge with next block if free
        int index = blocks.indexOf(block);
        if (index + 1 < blocks.size() && blocks.get(index + 1).free) {
            block.size += blocks.get(index + 1).size;
            blocks.remove(index + 1);
        }

        // Merge with previous block if free
        if (index > 0 && blocks.get(index - 1).free) {
            blocks.get(index - 1).size += block.size;
            blocks.remove(index);
        }
    }

    /**
     * Writes a byte value to the specified address in the simulated heap.
     * 
     * @param address The index in the heap array where the value should be written.
     * @param value The byte value to write to the heap.
     * @throws IllegalArgumentException if the address is out of bounds of the heap array.
     */
    public void write(int address, byte value) {
        MemoryBlock block = findBlockContaining(address);
        if (block == null || block.free) {
            throw new IllegalArgumentException("Cannot write to free or invalid address: " + address);
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
        MemoryBlock block = findBlockContaining(address);
        if (block == null || block.free) {
            throw new IllegalArgumentException("Cannot read from free or invalid address: " + address);
        }
        return heap[address];
    }

    /**
     * Helper to find the block that contains a given address.
     * 
     * @param address The address to search for in the heap.
     * @return The MemoryBlock that contains the address, or null if not found.
     */
    private MemoryBlock findBlockContaining(int address) {
        for (MemoryBlock block : blocks) {
            if (!block.free && address >= block.start && address < block.start + block.size) {
                return block;
            }
        }
        return null;
    }

    /**
     * Prints a visual representation of the heap memory.
     * See HeapVisualizer for details on how it visualizes the heap.
     */
    public void printHeap() {
        visualizer.printHeapVisual();
    }

}
