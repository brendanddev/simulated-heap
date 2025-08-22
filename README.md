# Simulated Heap 

A project that simulates **manual memory management**, similar to how low-level languages like C handle heap memory.  
Instead of letting the JVM handle allocation and garbage collection automatically, this project demonstrates how a heap could be built and managed by hand.

---

## Features 

- **Custom Heap Simulation**: Backed by a simple `byte[]` array
- **Memory Blocks**: Tracks allocated and free chunks of memory (`MemoryBlock` class)
- **Manual Allocation (`malloc`)**: First-fit, Best-fit, Worst-fit, or Next-fit allocation strategies can be used to allocate memory blocks.
- **Manual Deallocation (`free`)**: Supports freeing blocks and coalescing adjacent free blocks to reduce fragmentation.
- **Simulated Pointers**: Each `MemoryBlock` can reference other blocks.
- **Root Set Management**: `RootSet` tracks reachable memory blocks to prevent collection.
- **Garbage Collection**: Implements a mark-and-sweep GC that frees unreachable memory while preserving reachable blocks.
- **Heap Visualization**: `printHeap()` shows the current state of the heap for debugging.
- **Garbage Collection Visualization**: `GCVisualizer` provides a graphical view of heap changes during GC cycles.
- **Memory Alignment**: All allocations are aligned to 8-byte boundaries (configurable via `ALIGNMENT_SIZE`), ensuring proper memory layout.
- **Unit Tests**: Automated tests verify allocation, freeing, reading/writing, and memory alignment.
- **Demo Program**: `Main.java` runs a sequence of allocations/frees to show heap changes in action.  

---

## Installation
1. Clone the repository:
   ```bash
   git clone 
   cd simulated-heap
   ```

2. Build the project using Maven:
   ```bash
   mvn clean compile
   ```

3. Run the demo program:
   ```bash
    mvn exec:java -Dexec.mainClass="brendanddev.Main"
    ```

---

## Project Structure

```
simulated-heap/
│
├─ src/main/java/brendanddev/
│   ├─ heap/                 # Core memory management
│   │   ├─ SimulatedHeap.java
│   │   ├─ MemoryBlock.java
│   │   └─ AllocationStrategy.java
│   │
│   ├─ gc/                   # Garbage collection components
│   │   ├─ GarbageCollector.java
│   │   └─ RootSet.java
│   │
│   ├─ visualization/        # Visualization tools
│   │   ├─ HeapVisualizer.java
│   │   └─ GCVisualizer.java
│   │
│   └─ Main.java             # Demo entry point
│
├─ src/test/java/brendanddev/
│   ├─ SimulatedHeapTest.java  # Unit tests for SimulatedHeap
│   └─ GarbageCollectorTest.java # Unit tests for GarbageCollector
│
├─ pom.xml
└─ README.md
```

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

// Demonstrate Garbage Collection
RootSet rootSet = heap.getRootSet();
rootSet.add(b);            // mark block 'b' as reachable
GarbageCollector gc = new GarbageCollector(heap, rootSet);
gc.collect();
heap.printHeap();          // only reachable blocks remain allocated
```

---

## Testing

Use Maven to run all tests:

```bash
mvn test
```