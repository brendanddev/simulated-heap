package brendanddev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import brendanddev.heap.SimulatedHeap;

/**
 * Unit tests for the SimulatedHeap class.
 * Tests basic allocation, writing, reading, and freeing of memory blocks.
 */
public class SimulatedHeapTest {

    /**
     * Tests malloc and free functionality in the SimulatedHeap.
     */
    @Test
    public void testMallocAndFree() {
        SimulatedHeap heap = new SimulatedHeap(64);

        // Allocate a block of 16 bytes
        Integer ptr = heap.malloc(16);
        assertNotNull(ptr, "Allocation should succeed");

        // Write and read a value
        heap.write(ptr, (byte) 42);
        assertEquals(42, heap.read(ptr));

        // Free the allocated block
        heap.free(ptr);
        assertThrows(IllegalArgumentException.class, () -> heap.read(ptr), "Reading freed memory should fail");
    }

    
}
