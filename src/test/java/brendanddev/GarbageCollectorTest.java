package brendanddev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import brendanddev.gc.GarbageCollector;
import brendanddev.gc.RootSet;
import brendanddev.heap.MemoryBlock;
import brendanddev.heap.SimulatedHeap;

/**
 * Unit tests for the GarbageCollector class.
 * Tests that the garbage collector correctly identifies and collects unreachable memory blocks in the heap.
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

    /**
     * Tests that garbage collection does not collect blocks that are still reachable.
     */
    @Test
    public void testNoGarbageToCollect() {
        Integer ptrA = heap.malloc(16);
        Integer ptrB = heap.malloc(16);
        
        // Add both to root set
        rootSet.add(ptrA);
        rootSet.add(ptrB);
        
        int blocksBeforeGC = heap.getAllocations().size();
        gc.collect();
        int blocksAfterGC = heap.getAllocations().size();
        
        assertEquals(blocksBeforeGC, blocksAfterGC, "No blocks should be collected when all are reachable");
    }

    /**
     * Tests that all blocks are collected when none are reachable.
     */
    @Test
    public void testCollectAllGarbage() {
        // Allocate blocks but don't add any to root set
        heap.malloc(16);
        heap.malloc(32);
        heap.malloc(8);
        
        int allocatedBefore = heap.getAllocations().size();
        assertTrue(allocatedBefore > 0, "Should have allocated blocks");
        
        gc.collect();
        
        int allocatedAfter = heap.getAllocations().size();
        assertEquals(0, allocatedAfter, "All unreachable blocks should be collected");
    }

    /**
     * Tests that blocks with reference chains are correctly marked and not collected.
     */
    @Test
    public void testSimpleReferenceChain() {
        // Create chain: A -> B -> C
        Integer ptrA = heap.malloc(16);
        Integer ptrB = heap.malloc(16);
        Integer ptrC = heap.malloc(16);
        
        // Set up reference chain
        heap.findBlock(ptrA).addReference(ptrB);
        heap.findBlock(ptrB).addReference(ptrC);
        
        // Only add A to root set
        rootSet.add(ptrA);
        
        gc.collect();
        
        // All blocks should be reachable through the chain
        assertNotNull(heap.findBlock(ptrA), "Block A should be reachable");
        assertNotNull(heap.findBlock(ptrB), "Block B should be reachable through A");
        assertNotNull(heap.findBlock(ptrC), "Block C should be reachable through B");
    }

    /**
     * Tests that an isolated block not part of a refernece chain is correctly identified and collected
     * by the garbage collector.
     */
    @Test
    public void testBrokenReferenceChain() {

        // a --> b --> c  d
        Integer ptrA = heap.malloc(16);
        Integer ptrB = heap.malloc(16);
        Integer ptrC = heap.malloc(16);
        Integer ptrD = heap.malloc(16);
        
        // Set up partial reference chain
        heap.findBlock(ptrA).addReference(ptrB);
        heap.findBlock(ptrB).addReference(ptrC);
        // ptrD has no references to it
        
        rootSet.add(ptrA);
        
        gc.collect();
        
        // A, B, C should be reachable, D should be collected
        assertNotNull(heap.findBlock(ptrA), "Block A should be reachable");
        assertNotNull(heap.findBlock(ptrB), "Block B should be reachable");
        assertNotNull(heap.findBlock(ptrC), "Block C should be reachable");
        assertNull(heap.findBlock(ptrD), "Block D should be collected");
    }


    
}
