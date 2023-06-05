package sorts.hybrid;

import java.util.Random;

import main.ArrayVisualizer;
import sorts.insert.BinaryInsertionSort;
import sorts.templates.Sort;

/*

Coded for ArrayV by Kiriko-chan
in collaboration with aphitorite

-----------------------------
- Sorting Algorithm Scarlet -
-----------------------------

 */

/**
 * @author Kiriko-chan
 * @author aphitorite
 *
 */
public final class ShuffleQuickHeapSort extends Sort {

    public ShuffleQuickHeapSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Shuffle Quick Heap");
        this.setRunAllSortsName("Shuffle Quick Heap Sort");
        this.setRunSortName("Shuffle Quick Heapsort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    void medianOfThree(int[] array, int a, int b) {
        int m = a + (b - 1 - a) / 2;

        if (Reads.compareIndices(array, a, m, 1, true) == 1)
            Writes.swap(array, a, m, 1, true, false);

        if (Reads.compareIndices(array, m, b - 1, 1, true) == 1) {
            Writes.swap(array, m, b - 1, 1, true, false);

            if (Reads.compareIndices(array, a, m, 1, true) == 1)
                return;
        }

        Writes.swap(array, a, m, 1, true, false);
    }

    public void shuffle(int[] array, int a, int b) {
        Random rng = new Random();
        for (int i = a; i < b; i++) {
            Writes.swap(array, i, i + rng.nextInt(b - i), 0.75, true, false);
        }
    }
    
    public int partition(int[] array, int a, int b, int p) {
        int i = a, j = b;
        Highlights.markArray(3, p);

        while (true) {
            do {
                i++;
                Highlights.markArray(1, i);
                Delays.sleep(0.5);
            } while (i < j && Reads.compareIndices(array, i, p, 0, false) > 0);

            do {
                j--;
                Highlights.markArray(2, j);
                Delays.sleep(0.5);
            } while (j >= i && Reads.compareIndices(array, j, p, 0, false) < 0);

            if (i < j)
                Writes.swap(array, i, j, 1, true, false);
            else {
                Highlights.clearMark(3);
                return j;
            }
        }
    }
    
    void siftDown(int[] array, boolean max, int p, int n, int r) {
        int t = array[p+r];
        int cmp = max ? 1 : -1;
        
        while(2*r+2 < n) {
            int nxt = p+2*r+1;
            nxt = (Reads.compareValues(array[nxt+1], array[nxt]) == cmp) ? nxt+1 : nxt;
            
            if(Reads.compareValues(array[nxt], t) == cmp) {
                Writes.write(array, p+r, array[nxt], 0.5, true, false);
                r = nxt-p;
            }
            else {
                Writes.write(array, p+r, t, 0.5, true, false);
                return;
            }
        }
        int nxt = p+2*r+1;
            
        if(nxt-p < n && Reads.compareValues(array[nxt], t) == cmp) {
            Writes.write(array, p+r, array[nxt], 0.5, true, false);
            r = nxt-p;
        }
        Writes.write(array, p+r, t, 0.5, true, false);
    }
    
    void siftDownEasy(int[] array, boolean max, int p, int n, int t) {
        int r = 0;
        int cmp = max ? 1 : -1;
        
        while(2*r+2 < n) {
            int nxt = p+2*r+1;
            nxt = (Reads.compareValues(array[nxt+1], array[nxt]) == cmp) ? nxt+1 : nxt;
            
            Writes.write(array, p+r, array[nxt], 0.5, true, false);
            r = nxt-p;
        }
        int nxt = p+2*r+1;
            
        if(nxt-p < n) {
            Writes.write(array, p+r, array[nxt], 0.5, true, false);
            r = nxt-p;
        }
        Writes.write(array, p+r, t, 0.5, true, false);
    }
    
    void heapSort(int[] array, boolean max, int a, int b, int p) {
        int n = b-a;
        
        for(int i = (n-1)/2; i >= 0; i--)
            this.siftDown(array, max, a, n, i);
        
        if(max) {
            for(int i = 0; i < n; i++) {
                int t = array[--p];
                Highlights.markArray(2, p);
                Writes.write(array, p, array[a], 1, false, false);
                this.siftDownEasy(array, max, a, n, t);
            }
        }
        else {
            for(int i = 0; i < n; i++) {
                int t = array[p];
                Highlights.markArray(2, p);
                Writes.write(array, p++, array[a], 1, false, false);
                this.siftDownEasy(array, max, a, n, t);
            }
        }
    }
    
    public void sort(int[] array, int a, int b) {
        int start = a, end = b;
        boolean badPartition = false, shuffle = false;
        while(end-start > 32) {
            if(badPartition) {
                shuffle(array, start, end);
                shuffle = true;
            }
            medianOfThree(array, start, end);
            int p = partition(array, start, end, start);
            Writes.swap(array, start, p, 1, true, false);
            int left = p - start, right = end - p - 1;
            badPartition = !shuffle && (left < (end - start) / 16 || right < (end - start) / 16);
            if(left <= right) {
                this.heapSort(array, true, start, p, end);
                end -= left;
                Writes.swap(array, --end, p, 1, true, false);
            }
            else {
                this.heapSort(array, false, p+1, end, start);
                start += right;
                Writes.swap(array, start++, p, 1, true, false);
            }
        }
        BinaryInsertionSort smallSort = new BinaryInsertionSort(arrayVisualizer);
        smallSort.customBinaryInsert(array, start, end, 0.25);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        sort(array, 0, sortLength);

    }

}
