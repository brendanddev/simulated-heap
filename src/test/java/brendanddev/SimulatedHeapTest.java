package brendanddev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;

import brendanddev.heap.AllocationStrategy;
import brendanddev.heap.SimulatedHeap;

/**
 * Unit tests for the SimulatedHeap class.
 * Tests basic allocation, writing, reading, and freeing of memory blocks.
 */
public class SimulatedHeapTest {

    private SimulatedHeap heap;

    /**
     * Sets up a new SimulatedHeap instance before each test.
     */
    @BeforeEach
    public void setUp() {
        heap = new SimulatedHeap(128);
    }

    /**
     * Tests malloc and free functionality in the SimulatedHeap.
     */
    @Test
    public void testMallocAndFree() {
        // Allocate 16 bytes
        Integer ptr = heap.malloc(16);
        assertNotNull(ptr, "Allocation should succeed");
        assertEquals(0, ptr, "First allocation should start at address 0");

        heap.write(ptr, (byte) 42);
        assertEquals(42, heap.read(ptr), "Should read back written value");

        heap.free(ptr);
        assertThrows(IllegalArgumentException.class, () -> heap.read(ptr), "Reading freed memory should fail");
    }

    /**
     * Tests that multiple allocations work as expected, are distinct, and do not overlap.
     */
    @Test
    public void testMultipleAllocations() {
        Integer ptr1 = heap.malloc(16);
        Integer ptr2 = heap.malloc(24);
        Integer ptr3 = heap.malloc(8);

        assertNotNull(ptr1);
        assertNotNull(ptr2);
        assertNotNull(ptr3);

        // All pointers should be different
        assertNotEquals(ptr1, ptr2);
        assertNotEquals(ptr2, ptr3);
        assertNotEquals(ptr1, ptr3);

        // Should be able to write to all blocks
        heap.write(ptr1, (byte) 1);
        heap.write(ptr2, (byte) 2);
        heap.write(ptr3, (byte) 3);

        assertEquals(1, heap.read(ptr1));
        assertEquals(2, heap.read(ptr2));
        assertEquals(3, heap.read(ptr3));
    }

    /**
     * Tests that allocations are aligned to 8-byte boundaries.
     */
    @Test
    public void testMemoryAlignment() {
        for (int size = 1; size <= 32; size++) {
            // Reset for each test
            heap = new SimulatedHeap(128);
            Integer ptr = heap.malloc(size);
            assertNotNull(ptr, "Allocation of size " + size + " should succeed");
            assertEquals(0, ptr % 8, "Address " + ptr + " should be 8-byte aligned for size " + size);
        }
    }

    /**
     * Tests the First-fit allocation strategy by allocating multiple blocks,
     * freeing some to create holes, and verifying that the allocator reuses the 
     * first available block rather than later ones.
     */
    @Test 
    public void testFirstFitStrategy() {
        heap.setAllocationStrategy(AllocationStrategy.FIRST_FIT);

        Integer ptr1 = heap.malloc(32);  // [0-31]
        Integer ptr2 = heap.malloc(32);  // [32-63]
        Integer ptr3 = heap.malloc(32);  // [64-95]
        
        // Free [0-31], creates hole at beginning
        heap.free(ptr1);
        // Free [64-95], creates hole at end
        heap.free(ptr3);
        
        // First fit should use the first available hole (at beginning)
        Integer ptr4 = heap.malloc(16);
        assertEquals(ptr1, ptr4, "First fit should reuse first available block");
    }

    /**
     * Tests the Best-fit allocation strategy by ensuring that the allocator
     * chooses the smallest suitable free block for a new allocation request.
     */
    @Test
    public void testBestFitStrategy() {
        heap.setAllocationStrategy(AllocationStrategy.BEST_FIT);
        
        Integer ptr1 = heap.malloc(16);  // [0-15]
        Integer ptr2 = heap.malloc(32);  // [16-47]
        Integer ptr3 = heap.malloc(8);   // [48-55]
        
        // Creates 16-byte hole
        heap.free(ptr1);
        // Creates 32-byte hole
        heap.free(ptr2);
        
        // Request 16 bytes - should use the 16-byte hole (best fit)
        Integer ptr4 = heap.malloc(16);
        assertEquals(ptr1, ptr4, "Best fit should choose smallest suitable block");
    }

    /**
     * Tests the Worst-fit allocation strategy by ensuring that the allocator
     * chooses the largest suitable free block for a new allocation request.
     */
    @Test
    public void testWorstFitStrategy() {
        heap.setAllocationStrategy(AllocationStrategy.WORST_FIT);
        
        // Fill most of the heap with controlled allocations
        Integer ptr1 = heap.malloc(16);  // [0-15]
        Integer ptr2 = heap.malloc(32);  // [16-47]
        Integer ptr3 = heap.malloc(16);  // [48-63]
        Integer ptr4 = heap.malloc(64);  // [64-127]

        // Free two blocks to create holes (16 and 32 bytes)
        heap.free(ptr1);
        heap.free(ptr2);

        Integer ptr5 = heap.malloc(8);
        assertEquals(ptr2, ptr5, "Worst fit should choose the 32-byte block");
    }

    /**
     * Tests the Next-fit allocation strategy by ensuring that the allocator
     * continues searching from the last allocated block for the next allocation request.
     */
    @Test
    public void testNextFitStrategy() {
        heap.setAllocationStrategy(AllocationStrategy.NEXT_FIT);
        
        // Fill up some memory to test next-fit behavior
        Integer ptr1 = heap.malloc(16);  // [0-15]
        Integer ptr2 = heap.malloc(16);  // [16-31]
        Integer ptr3 = heap.malloc(16);  // [32-47]
        
        // Free first block
        heap.free(ptr1);
        // Free third block
        heap.free(ptr3);
        
        // Next allocation should continue from where last allocation was made
        Integer ptr4 = heap.malloc(8);
        Integer ptr5 = heap.malloc(8);
        
        // Verify next-fit behavior
        assertNotNull(ptr4);
        assertNotNull(ptr5);
    }

    /**
     * Tests the memory coalescing functionality of the heap allocator.
     * 
     * Verifies that when adjacent free blocks are released, they merge into a single larger
     * free block rather than staying fragmented.
     */
    @Test
    public void testMemoryCoalescing() {
        Integer ptr1 = heap.malloc(16);  // [0-15]
        Integer ptr2 = heap.malloc(16);  // [16-31]
        Integer ptr3 = heap.malloc(16);  // [32-47]
        
        int initialBlockCount = heap.getBlocks().size();

        // Free middle block then first block
        heap.free(ptr2);
        heap.free(ptr1);
        
        // Should have fewer blocks after coalescing
        assertTrue(heap.getBlocks().size() < initialBlockCount, 
                  "Blocks should coalesce when adjacent blocks are freed");
        
        // Free last block, should coalesce with the large free block
        heap.free(ptr3);
        
        // Should end up with a single large free block
        assertEquals(1, heap.getBlocks().size(), "All blocks should coalesce into one");
        assertTrue(heap.getBlocks().get(0).isFree(), "Final block should be free");
    }



    
}
