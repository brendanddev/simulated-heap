# Simulated Heap 

A project that simulates **manual memory management**, similar to how low-level languages like C handle heap memory.  
Instead of letting the JVM handle allocation and garbage collection automatically, this project demonstrates how a heap could be built and managed by hand.

---

## Features 

- **Custom Heap Simulation**: Backed by a simple `byte[]` array
- **Memory Blocks**: Tracks allocated and free chunks of memory (`MemoryBlock` class)
- **Manual Allocation (`malloc`)**: First-fit or Best-fit allocation strategies can be used to allocate memory blocks.
- **Manual Deallocation (`free`)**: Supports freeing blocks and coalescing adjacent free blocks to reduce fragmentation.
- **Simulated Pointers**: Each `MemoryBlock` can reference other blocks.
- **Root Set Management**: `RootSet` tracks reachable memory blocks to prevent collection.
- **Heap Visualization**: `printHeap()` shows the current state of the heap for debugging.  
- **Garbage Collection**:
- **Demo Program**: `Main.java` runs a sequence of allocations/frees to show heap changes in action.  

---

## Example Usage  

```java
SimulatedHeap heap = new SimulatedHeap(32);

int a = heap.malloc(8);   // allocate 8 bytes
int b = heap.malloc(8);   // allocate another 8 bytes
heap.printHeap();         // shows two allocated blocks + one free block

heap.free(a);             // free the first 8-byte block
heap.printHeap();

int c = heap.malloc(4);   // allocate 4 bytes inside the first free region
heap.printHeap();

heap.free(b);             // free the second block
heap.free(c);             // free the 4-byte block
heap.printHeap();         // entire heap is free again
```