package sorts.quick;

import java.util.Random;
import main.ArrayVisualizer;
import sorts.insert.InsertionSort;
import sorts.select.MaxHeapSort;
import sorts.templates.Sort;

/*

Ported to ArrayV by Kiriko-chan
in collaboration with thatsOven

-----------------------------
- Sorting Algorithm Scarlet -
-----------------------------

 */

/**
 * @author Kiriko-chan
 * @author thatsOven
 *
 */
public final class ShuffleQuickSort extends Sort {

    public ShuffleQuickSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Shuffle Quick");
        this.setRunAllSortsName("Shuffle Quick Sort");
        this.setRunSortName("Shuffle Quicksort");
        this.setCategory("Quick Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    MaxHeapSort heapSorter;
    InsertionSort insSort;
    
    public static int floorLog(int n) {
        int log = 0;
        while ((n >>= 1) != 0) ++log;
        return log;
    }
    
    public void shuffle(int[] array, int a, int b) {
        Random rng = new Random();
        for(int i = a; i < b; i++) {
            Writes.swap(array, i, i + rng.nextInt(b - i), 0.75, true, false);
        }
    }
    
    void medianOfThree(int[] array, int a, int b) {
        int m = a + (b - 1 - a) / 2;

        if (Reads.compareIndices(array, a, m, 1, true) > 0)
            Writes.swap(array, a, m, 1, true, false);

        if (Reads.compareIndices(array, m, b - 1, 1, true) > 0) {
            Writes.swap(array, m, b - 1, 1, true, false);

            if (Reads.compareIndices(array, a, m, 1, true) > 0)
                return;
        }

        Writes.swap(array, a, m, 1, true, false);
    }
    
    public int partition(int[] array, int a, int b, int p) {
        int i = a, j = b;
        Highlights.markArray(3, p);
        
        while(true) {
            do {
                i++;
                Highlights.markArray(1, i);
                Delays.sleep(0.5);
            }
            while(i < j && Reads.compareIndices(array, i, p, 0, false) < 0);
            
            do {
                j--;
                Highlights.markArray(2, j);
                Delays.sleep(0.5);
            }
            while(j >= i && Reads.compareIndices(array, j, p, 0, false) > 0);
                
            if(i < j) Writes.swap(array, i, j, 1, true, false);
            else {
                Highlights.clearMark(3);
                return j;
            }
        }
    }
    
    protected void quickSort(int[] array, int a, int b, int badAllowed) {
        while(b - a > 16) {
            medianOfThree(array, a, b);
            int p = partition(array, a, b, a);
            int l = p - a, r = b - p - 1;
            while(l < (b - a) / 16 || r < (b - a) / 16) {
                if(--badAllowed <= 0) {
                    heapSorter.customHeapSort(array, a, b, 1);
                    return;
                }
                shuffle(array, a, b);
                medianOfThree(array, a, b);
                p = partition(array, a, b, a);
                l = p - a;
                r = b - p - 1;
            }
            Writes.swap(array, a, p, 1, true, false);
            quickSort(array, a, p, badAllowed);
            a = p + 1;
        }
        insSort.customInsertSort(array, a, b, 0.5, false);
    }
    
    public void customSort(int[] array, int a, int b) {
        heapSorter = new MaxHeapSort(arrayVisualizer);
        insSort = new InsertionSort(arrayVisualizer);
        quickSort(array, a, b, floorLog(b - a));
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        heapSorter = new MaxHeapSort(arrayVisualizer);
        insSort = new InsertionSort(arrayVisualizer);
        quickSort(array, 0, sortLength, floorLog(sortLength));

    }

}
