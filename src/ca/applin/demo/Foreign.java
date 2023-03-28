package ca.applin.demo;

import java.lang.foreign.*;
import java.lang.invoke.*;
import java.util.*;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

/**
 * <h2><a href="https://openjdk.org/jeps/434">JEP 434</a>: Foreign Function & Memory API (Second Preview)</h2>
 * This JEP proposes to incorporate refinements based on feedback, and to re-preview the API in JDK 20. In this version:
 * <ul>
 *     <li>The MemorySegment and MemoryAddress abstractions are unified (memory addresses are now modeled by zero-length memory segments);</li>
 *     <li>The sealed MemoryLayout hierarchy is enhanced to facilitate usage with pattern matching in switch expressions and statements (JEP 433), and</li>
 *     <li>MemorySession has been split into Arena and SegmentScope to facilitate sharing segments across maintenance boundaries.</li>
 * </ul>
 */
public class Foreign {
    public static void main(String[] args) throws Exception {
        /*
        typedef struct vec3 {
            long double x
            long double y
            long double z;
        } vec3'
         */
        MemoryLayout vec3Layout = MemoryLayout.structLayout(
                ValueLayout.JAVA_DOUBLE.withName("x"),
                ValueLayout.JAVA_DOUBLE.withName("y"),
                ValueLayout.JAVA_DOUBLE.withName("z")
        ).withName("vec3");

        try (Arena offHead = Arena.openConfined()){

            // vec3* vectors = malloc(64*sizeof(vec3));
            MemorySegment segment = offHead.allocateArray(vec3Layout, 64);

            System.out.println(segment);
            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 3; j++) {
                    segment.setAtIndex(
                            ValueLayout.JAVA_DOUBLE, (long)i*3+j, (long)i*3+j);
                }
            }
            for (int i = 0; i < 64; i++) {
                System.out.printf("Value at index %d: %.2f%n",
                        i,
                        segment.getAtIndex(ValueLayout.JAVA_DOUBLE, i));
            }
//            segment.getAtIndex(, 0)
            printLayout(vec3Layout);
        }
        qsortExample();
    }

    static void printLayout(MemoryLayout layout) {
        switch (layout) {
            case SequenceLayout seq -> System.out.println("sequence: " + seq);
            case StructLayout struct -> System.out.println("struct: " + struct);
            default -> System.out.println("unknown");
        }
    }

    static void qsortExample() throws Exception {
        // lets try cto call the C method
        //    void qsort(void *base, size_t nmemb, size_t size, int (*compar)(const void *, const void *));

        Linker linker = Linker.nativeLinker();
        FunctionDescriptor qsortDescriptor = FunctionDescriptor.ofVoid(ADDRESS, JAVA_LONG, JAVA_LONG, ADDRESS);
        MethodHandle qsort = linker.downcallHandle(
                linker.defaultLookup().find("qsort").orElseThrow(),
                qsortDescriptor
        );

        // create a method handle pointing to java implementation of qsortCompare
        MethodType qsortCompareMethodType = MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class);
        MethodHandle compareHandle = MethodHandles.lookup()
                .findStatic(Foreign.class, "qsortCompare", qsortCompareMethodType);

        // Java description of a C function implemented by a Java method!
        FunctionDescriptor qsortCompareDescriptor = FunctionDescriptor.of(JAVA_INT, ADDRESS.asUnbounded(), ADDRESS.asUnbounded());

        // function pointer for qsortCompare. C type: int (*compar)(const void *, const void *)
        MemorySegment compareFunc = linker.upcallStub(compareHandle, qsortCompareDescriptor, SegmentScope.auto());

        try (Arena arena = Arena.openConfined()) {
            MemorySegment array = arena.allocateArray(JAVA_INT,
                    0, 9, 3, 4, 6, 5, 1, 8, 2, 7);
            qsort.invoke(array, 10L, ValueLayout.JAVA_INT.byteSize(), compareFunc);
            int[] sorted = array.toArray(JAVA_INT); // [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 ]
            System.out.println(Arrays.toString(sorted));
        } catch (Throwable t) {}

    }

    static int qsortCompare(MemorySegment elem1, MemorySegment elem2) {
        return Integer.compare(elem1.get(JAVA_INT, 0), elem2.get(JAVA_INT, 0));
    }

}
