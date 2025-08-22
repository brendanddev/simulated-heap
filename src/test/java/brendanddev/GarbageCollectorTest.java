package brendanddev;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import brendanddev.gc.GarbageCollector;
import brendanddev.gc.RootSet;
import brendanddev.heap.MemoryBlock;
import brendanddev.heap.SimulatedHeap;

/**
 * Unit tests for the GarbageCollector class.
 * Tests 
 * 
 */
public class GarbageCollectorTest {

    private SimulatedHeap heap;
    private RootSet rootSet;
    private GarbageCollector gc;

    /**
     * Sets up a new SimulatedHeap, RootSet, and GarbageCollector before each test.
     */
    @BeforeEach
    public void setUp() {
        heap = new SimulatedHeap(256);
        rootSet = new RootSet();
        gc = new GarbageCollector(heap, rootSet);
    }

    /**
     * Tests basic garbage collection functionality by checking whether unreachable blocks are 
     * collected while reachable blocks remain allocated.
     */
    @Test
    public void testBasicGarbageCollection() {
        // Allocate two blocks
        Integer ptrA = heap.malloc(16);
        Integer ptrB = heap.malloc(16);
        
        // Only add A to root set --> b is unreachable
        rootSet.add(ptrA);
        
        // Run garbage collection
        gc.collect();
        
        // A should still be allocated, B should be freed
        MemoryBlock blockA = heap.findBlock(ptrA);
        MemoryBlock blockB = heap.findBlock(ptrB);
        
        assertNotNull(blockA, "Root set block should still exist");
        assertFalse(blockA.isFree(), "Root set block should not be freed");
        assertNull(blockB, "Unreachable block should be collected");
    }
    
}
