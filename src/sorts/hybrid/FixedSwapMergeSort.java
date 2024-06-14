package sorts.hybrid;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/**
 * @author Gaming32
 * @author Harumi
 * 
 */
public class FixedSwapMergeSort extends Sort {

    public FixedSwapMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Fixed SwapMerge");
        this.setRunAllSortsName("Fixed Swap-Merge Sort");
        this.setRunSortName("Fixed Swap-Mergesort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    private int binSearch(int[] array, int a, int b, int val) {
        while (a < b) {
            int m = a + (b - a) / 2;
            Highlights.markArray(2, m);
            Delays.sleep(0.25);
            if (Reads.compareValues(val, array[m]) < 0) b = m;
            else a = m + 1;
        }
        return a;
    }
    
    public void insertSort(int[] array, int a, int b) {
        for (int i = a + 1; i < b; i++) {
            int current = array[i];
            int dest = binSearch(array, a, i, current);
            int pos = i;
            while (pos > dest) Writes.write(array, pos, array[--pos], 0.25, true, false);
            if (pos < i) Writes.write(array, pos, current, 0.25, true, false);
        }
    }
    
    public void merge(int[] array, int a, int m, int b) {
        int i = a, j = m;
        while (i < j && j < b) {
            Highlights.markArray(3, i);
            Delays.sleep(0.025);
            if (Reads.compareValues(array[i], array[j]) > 0)
                Writes.multiSwap(array, j++, i, 0.025, true, false);
            i++;
        }
        Highlights.clearMark(3);
    }
    
    public void mergeSort(int[] array, int a, int b) {
        if(b - a < 32) {
            insertSort(array, a, b);
            return;
        }
        int m = a + (b - a) / 2;
        mergeSort(array, a, m);
        mergeSort(array, m, b);
        merge(array, a, m, b);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        mergeSort(array, 0, sortLength);

    }

}
