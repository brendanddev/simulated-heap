package brendanddev.heap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brendanddev.gc.RootSet;
import brendanddev.visualization.HeapVisualizer;

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
    private static final int ALIGNMENT_SIZE = 8;
    private int lastAllocationIndex = 0;

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
     * Returns the root set of memory blocks.
     */
    public RootSet getRootSet() {
        return rootSet;
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
            case WORST_FIT:
                return mallocWorstFit(size);
            case NEXT_FIT:
                return mallocNextFit(size);
            default:
                throw new IllegalStateException("Unknown allocation strategy: " + strategy);
        }
    }

    /**
     * Allocates a block of memory of the given size using the first-fit strategy,
     * ensuring that the starting address is aligned to ALIGNMENT_SIZE.
     * 
     * @param size The number of bytes to allocate.
     * @return The starting index of the allocated memory block, or null if allocation fails.
     */
    public Integer mallocFirstFit(int size) {
        for (MemoryBlock block : blocks) {
            if (block.isFree()) {
                Integer address = allocateFromBlock(block, size);
                if (address != null) return address;
            }
        }
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

        for (MemoryBlock block : blocks) {
            if (block.isFree() && block.getSize() >= size) {
                if (bestBlock == null || block.getSize() < bestBlock.getSize()) {
                    bestBlock = block;
                }
            }
        }
        if (bestBlock == null) return null;
        return allocateFromBlock(bestBlock, size);
    }

    /**
     * Allocates a block of memory using the worst-fit strategy.
     * 
     * @param size The number of bytes requested to allocate.
     * @return The starting address of the allocated block, or null if no block is large enough.
     */
    public Integer mallocWorstFit(int size) {
        // Keep track of largest suitable free block found so far
        MemoryBlock worstBlock = null;

        for (MemoryBlock block : blocks) {
            // Check if block is free and large enough
            if (block.isFree() && block.getSize() >= size) {
                // If no candidate yet, or this block is bigger than the current worstBlock, pick this one
                if (worstBlock == null || block.getSize() > worstBlock.getSize()) {
                    worstBlock = block;
                }
            }
        }

        // If no free block found that fits, allocation fails
        if (worstBlock == null) return null;

        // If chosen block is larger than needed, split it
        if (worstBlock.getSize() > size) {
            MemoryBlock newBlock = new MemoryBlock(worstBlock.getStart() + size, worstBlock.getSize() - size);
            blocks.add(blocks.indexOf(worstBlock) + 1, newBlock);
            worstBlock.setSize(size);
        }

        // Mark the block as allocated
        worstBlock.setFree(false);
        allocations.put(worstBlock.getStart(), worstBlock);
        return worstBlock.getStart();
    }

    /**
     * Allocates a block of memory using the next-fit strategy.
     * Unlike the first-fit strategy, this approach remembers where the last allocation was made,
     * and continues searching from there, wrapping around if necessary.
     * 
     * @param size The number of bytes to allocate.
     * @return The starting address of the allocated memory block, or null if allocation fails.
     */
    public Integer mallocNextFit(int size) {
        // Total blocks in the heap and starting index for search
        int n = blocks.size();
        int startIndex = lastAllocationIndex;

        // Search through all blocks, wrapping around with modulo
        for (int i = 0; i < n; i++) {
            int index = (startIndex + i) % n;
            MemoryBlock block = blocks.get(index);

            // Check if the block is free and has enough size
            if (block.isFree() && block.getSize() >= size) {
                // If block is larger than needed, split it
                if (block.getSize() > size) {
                    MemoryBlock newBlock = new MemoryBlock(block.getStart() + size, block.getSize() - size);
                    blocks.add(index + 1, newBlock);
                    block.setSize(size);
                }

                // Mark the block as allocated
                block.setFree(false);
                allocations.put(block.getStart(), block);

                // Update the last allocation index
                lastAllocationIndex = index;

                return block.getStart();
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
     * Helper to align an address to the nearest multiple of ALIGNMENT_SIZE.
     * 
     * @param address The address to align.
     * @return The aligned address, or the same address if already aligned.
     */
    private int alignAddress(int address) {
        int remainder = address % ALIGNMENT_SIZE;
        if (remainder == 0) return address;
        return address + (ALIGNMENT_SIZE - remainder);
    }

    /**
     * Allocates memory from a given block while ensuring alignment.
     * 
     * @param block The free MemoryBlock to allocate from.
     * @param size The requested size of memory to allocate.
     * @return
     */
    private Integer allocateFromBlock(MemoryBlock block, int size) {

        int alignedStart = alignAddress(block.getStart());
        int padding = alignedStart - block.getStart();

        // Ensure block can hold the padding + requested size
        if (block.getSize() < padding + size) {
            return null;
        }

        // Handle padding to avoid zero sized blocks
        if (padding > 0 && padding < block.getSize()) {
            // Create a padding block to fill the gap and keep alignment
            MemoryBlock paddingBlock = new MemoryBlock(block.getStart(), padding);
            paddingBlock.setFree(true);

            // Insert the padding block before current block
            int idx = blocks.indexOf(block);
            blocks.add(idx, paddingBlock);

            // Adjust original block to skip the padding
            block.setStart(alignedStart);
            block.setSize(block.getSize() - padding);
        }

        // If block is larger than needed, split off remainder
        if (block.getSize() > size) {
            MemoryBlock remainderBlock = new MemoryBlock(block.getStart() + size, block.getSize() - size);
            blocks.add(blocks.indexOf(block) + 1, remainderBlock);
            block.setSize(size);
        }

        // Mark block as allocated and track it
        block.setFree(false);
        allocations.put(block.getStart(), block);
        return block.getStart();
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
