package brendanddev;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents each chunk of memory in the simulated heap.
 * 
 * Each MemoryBlock keeps track of its starting index in the heap array, the size of the memory block, whether 
 * it is currently free or allocated, and references to other blocks to simulate pointers.
 */
public class MemoryBlock {

    private int start;
    private int size;
    private boolean free;
    private boolean marked;
    // List of integer references to simulate pointers
    private final List<Integer> references = new ArrayList<>();

    /**
     * Constructs a new MemoryBlock.
     * 
     * @param start The starting index of the memory block in the heap array.
     * @param size The size of the memory block in bytes.
     *              (The number of bytes this block represents)
     */
    public MemoryBlock(int start, int size) {
        this.start = start;
        this.size = size;
        this.free = true;
        this.marked = false;
    }


    /**
     * Returns a string representation of the MemoryBlock.
     */
    @Override
    public String toString() {
        return "MemoryBlock{" +
                "start=" + start +
                ", size=" + size +
                ", free=" + free +
                '}';
    }

    
}
