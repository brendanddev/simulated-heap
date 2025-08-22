package brendanddev.heap;

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

    // Getters and setters
    public int getStart() { return start; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public boolean isFree() { return free; }
    public void setFree(boolean free) { this.free = free; }    

    // Flag bit as marked or unmarked for garbage collection
    public void mark() { this.marked = true; }
    public void unmark() { this.marked = false; }
    public boolean isMarked() { return marked; }

    // References
    public List<Integer> getReferences() { return references; }
    public void addReference(int address) { references.add(address); }
    public void removeReference(int address) { references.remove(Integer.valueOf(address)); }


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
