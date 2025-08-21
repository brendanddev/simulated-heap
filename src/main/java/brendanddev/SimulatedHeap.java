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
    private AllocationStrategy strategy = AllocationStrategy.FIRST_FIT;

    private final byte[] heap;
    private final List<MemoryBlock> blocks;
    private final Map<Integer, MemoryBlock> allocations = new HashMap<>();
    private final RootSet rootSet = new RootSet();

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
     * Returns the map of all allocated memory blocks in the heap.
     */
    public Map<Integer, MemoryBlock> getAllocations() {
        return allocations;
    }

    /**
     * Finds a memory block by its exact starting address.
     * 
     * @param startAddress The starting address of the memory block to find.
     * @return The MemoryBlock if found, or null if no block exists at that address.
     */
    public MemoryBlock findBlock(int startAddress) {
        return allocations.get(startAddress);
    }

    /**
     * Determines the allocation strategy to use for memory allocation.
     */
    public Integer malloc(int size) {
        switch (strategy) {
            case FIRST_FIT:
                return mallocFirstFit(size);
            case BEST_FIT:
                return mallocBestFit(size);
            default:
                throw new IllegalStateException("Unknown allocation strategy: " + strategy);
        }
    }

    /**
     * Allocates a block of memory of the given size using the first-fit strategy.
     * 
     * @param size The number of bytes to allocate.
     * @return The starting index of the allocated memory block, or null if allocation fails.
     */
    public Integer mallocFirstFit(int size) {
        // Loop through all blocks to find a free block big enough
        for (MemoryBlock block : blocks) {
            // Check if the block is free and has enough size
            if (block.isFree() && block.getSize() >= size) {
                // If block is larger than requested size, split it
                // Prevents wasting extra memory in a large block
                if (block.getSize() > size) {
                    // Create new block for leftover memory
                    // Its start is right after the allocated portion
                    MemoryBlock newBlock = new MemoryBlock(block.getStart() + size, block.getSize() - size);
                    
                    // Insert new block into list, right after the current block
                    blocks.add(blocks.indexOf(block) + 1, newBlock);
                    // Reduce the size of the original block to match the requested size
                    block.setSize(size);
                }
                // Mark the block as allocated so it wont be used again until freed
                block.setFree(false);

                // Track blocks that have been allocated in map
                allocations.put(block.getStart(), block);

                // Return the starting index as the 'pointer' to the allocated block
                return block.getStart();
            }
        }
        // No suitable block found
        return null;
    }

    /**
     * Allocates a block of memory of the given size using the best-fit strategy.
     * 
     * @param size The number of bytes to allocate.
     * @return The starting index of the allocated memory block, or null if allocation fails.
     */
    public Integer mallocBestFit(int size) {

        // Track best fitting block
        MemoryBlock bestBlock = null;

        // Loop through all blocks to find the smallest free block that fits the requested size
        for (MemoryBlock block : blocks) {
            // Check if the block is free and has enough size
            if (block.isFree() && block.getSize() >= size) {
                // If this is the first block or smaller than the current best, update bestBlock
                if (bestBlock == null || block.getSize() < bestBlock.getSize()) {
                    bestBlock = block;
                }
            }
        }

        // If no suitable block was found, return null
        if (bestBlock == null) {
            return null;
        }

        // If the best block is larger than requested size, split it
        if (bestBlock.getSize() > size) {
            // Create new block for leftover memory
            MemoryBlock newBlock = new MemoryBlock(bestBlock.getStart() + size, bestBlock.getSize() - size);
            
            // Insert new block into list, right after the best block
            blocks.add(blocks.indexOf(bestBlock) + 1, newBlock);
            // Reduce the size of the best block to match the requested size
            bestBlock.setSize(size);
        }

        // Mark block as allocated
        bestBlock.setFree(false);
        allocations.put(bestBlock.getStart(), bestBlock);

        // Return the starting index as the 'pointer' to the allocated block
        return bestBlock.getStart();
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
        if (block == null || block.isFree()) {
            throw new IllegalArgumentException("Invalid free: No allocated block at address " + address);
        }

        // Mark block as free and remove from map
        block.setFree(true);
        allocations.remove(address);

        // Merge with next block if free
        int index = blocks.indexOf(block);
        if (index + 1 < blocks.size() && blocks.get(index + 1).isFree()) {
            block.setSize(block.getSize() + blocks.get(index + 1).getSize());
            blocks.remove(index + 1);
        }

        // Merge with previous block if free
        if (index > 0 && blocks.get(index - 1).isFree()) {
            blocks.get(index - 1).setSize(blocks.get(index - 1).getSize() + block.getSize());
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
        if (block == null || block.isFree()) {
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
        if (block == null || block.isFree()) {
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
            if (!block.isFree() && address >= block.getStart() && address < block.getStart() + block.getSize()) {
                return block;
            }
        }
        return null;
    }

    /**
     * Sets the allocation strategy for future malloc calls.
     * 
     * @param strategy The allocation strategy to use (FIRST_FIT or BEST_FIT).
     */
    public void setAllocationStrategy(AllocationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Prints a visual representation of the heap memory.
     * See HeapVisualizer for details on how it visualizes the heap.
     */
    public void printHeap() {
        visualizer.printHeapVisual();
    }

}
