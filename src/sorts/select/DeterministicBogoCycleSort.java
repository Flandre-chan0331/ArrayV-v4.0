package sorts.select;

import main.ArrayVisualizer;
import sorts.templates.BogoSorting;

public final class DeterministicBogoCycleSort extends BogoSorting {

    public DeterministicBogoCycleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Deterministic Bogo Cycle");
        this.setRunAllSortsName("Deterministic Bogo Cycle Sort");
        this.setRunSortName("Deterministic Bogo Cyclesort");
        this.setCategory("Impractical Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    boolean correctPos(int[] array, int idx, int a, int b) {
        int lower = a, upper = a;
        Highlights.markArray(2, idx);
        
        for(int i = a; i < b && lower <= idx; i++) {
            if(i == idx) continue;
            
            Highlights.markArray(1, i);
            int cmp = Reads.compareValues(array[i], array[idx]);
            lower += cmp < 0 ? 1 : 0;
            upper += cmp <= 0 ? 1 : 0;
        }
        return idx >= lower && idx <= upper;
    }
    
    void dualSwap(int[] array, int[] idx, int o, int a, int b) {
        if (a == b) return;
        Writes.swap(array, o + idx[a], o + idx[b], 0, true, false);
        Writes.swap(idx, a, b, delay, false, true);
    }
    
    void dualReversal(int[] array, int[] idx, int o, int a, int b) {
        int i = a, j = b;
        while (i < j) {
            dualSwap(array, idx, o, i, j);
            i++;
            j--;
        }
    }
    
    void nextPermutation(int[] array, int[] idx, int a, int n) {
        if (n < 2) return;
        int i = n - 1;
        while (i > 0) {
            if (idx[i - 1] < idx[i]) {
                break;
            }
            i--;
        }
        if (i == 0) {
            dualReversal(array, idx, a, 0, n - 1);
            return;
        }
        int j = n - 1;
        while (j > i - 1) {
            if (idx[j] > idx[i - 1]) {
                break;
            }
            j--;
        }
        dualSwap(array, idx, a, i - 1, j);
        dualReversal(array, idx, a, i, n - 1);
    }
    
    public void sort(int[] array, int a, int b) {
        if (isRangeSorted(array, a, b)) return;
        int length = b - a;
        int[] idx = Writes.createExternalArray(length);
        
        for(int i = 0; i < length; i++)
            Writes.write(idx, i, i, 0, false, true);
        
        int size = length;
        do {
            int c = 0;
            
            for (int i = 0; i < size; i++)
                if (!correctPos(array, a + idx[i], a, b))
                    Writes.write(idx, c++, idx[i], 0, false, true);
            size = c;
            nextPermutation(array, idx, a, size);
        } while (size > 1);
        Writes.deleteExternalArray(idx);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        sort(array, 0, sortLength);

    }

}
