package sorts.misc;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public final class StablePancakeInsertionSort extends Sort {

    public StablePancakeInsertionSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Stable Pancake Insertion");
        this.setRunAllSortsName("Stable Pancake Insertion Sort");
        this.setRunSortName("Stable Pancake Insertionsort");
        this.setCategory("Miscellaneous Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    // offset from the start of the array
    int offset;
    
    int compare(int[] array, int a, int b, double sleep, boolean mark) {
        return Reads.compareIndices(array, offset + a, offset + b, sleep, mark);
    }
    
    void flip(int[] array, int idx) {
        if (idx <= 0) return;
        if (idx >= 3) Writes.reversal(array, offset, offset + idx, 0.125, true, false);
        else Writes.swap(array, offset, offset + idx, 0.125, true, false);
    }
    
    int binSearch(int[] array, int a, int b, int k, boolean d) {
        while(a < b) {
            int m = a + (b - a) / 2;
            if ((compare(array, k, m, 0.125, true) < 0) ^ d) b = m;
            else a = m + 1;
        }
        return a;
    }
    
    public void insertSort(int[] array, int a, int b) {
        boolean bw = false;
        int len = b - a;
        offset = a;
        for (int i = 1; i < len; i++) {
            int j = binSearch(array, 0, i, i, bw);
            flip(array, i);
            flip(array, i - j);
            flip(array, i - j - 1);
            bw = !bw;
        }
        if (bw) flip(array, len - 1);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        insertSort(array, 0, sortLength);

    }

}
