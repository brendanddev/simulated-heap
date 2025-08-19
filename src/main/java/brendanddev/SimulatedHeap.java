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

    public Integer malloc(int size) {
        // TODO: Implement memory allocation logic
        return null;
    }

    public void free(int address) {
        // TODO: Implement memory deallocation logic
    }

    








    
}
