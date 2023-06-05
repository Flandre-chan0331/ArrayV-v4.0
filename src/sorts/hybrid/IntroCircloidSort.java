package sorts.hybrid;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public final class IntroCircloidSort extends Sort {

    public IntroCircloidSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Intro Circloid");
        this.setRunAllSortsName("Introspective Circloid Sort");
        this.setRunSortName("Introspective Circloid Sort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    protected void insertTo(int[] array, int a, int b) {
        Highlights.clearMark(2);
        if (a != b) {
            int temp = array[a];
            int d = (a > b) ? -1 : 1;
            for (int i = a; i != b; i += d)
                Writes.write(array, i, array[i + d], 0.5, true, false);
            Writes.write(array, b, temp, 0.5, true, false);
        }
    }

    protected int expSearch(int[] array, int a, int b, int val) {
        int i = 1;
        while (b - i >= a && Reads.compareValues(val, array[b - i]) < 0) i *= 2;
        int a1 = Math.max(a, b - i + 1), b1 = b - i / 2;
        while (a1 < b1) {
            int m = a1 + (b1 - a1) / 2;
            Highlights.markArray(2, m);
            Delays.sleep(0.25);
            if (Reads.compareValues(val, array[m]) < 0) b1 = m;
            else a1 = m + 1;
        }
        return a1;
    }

    protected void insertSort(int[] array, int a, int b) {
        for (int i = a + 1; i < b; i++)
            insertTo(array, i, expSearch(array, a, i, array[i]));
    }
    
    protected boolean circle(int[] array, int left, int right) {
        int a = left;
        int b = right;
        boolean swapped = false;
        while (a < b) {
            if (Reads.compareIndices(array, a, b, 0.25, true) > 0) {
                Writes.swap(array, a, b, 1, true, false);
                swapped = true;
            }
            a++;
            b--;
            if(a == b) b++;
        }
        return swapped;
    }
    
    public boolean circlePass(int[] array, int left, int right) {
        if (left >= right) return false;
        int mid = left + (right - left) / 2; //avoid integer overflow
        boolean l = this.circlePass(array, left, mid);
        boolean r = this.circlePass(array, mid+1, right);
        return this.circle(array, left, right) || l || r;
    }
    
    public void sort(int[] array, int a, int b) {
        int length = b - a;
        int threshold = 0, n = 1;
        for(; n < length; n*=2, threshold++);
        threshold /= 2;
        int iterations = 0;
        do {
            iterations++;
            if(iterations >= threshold) {
                insertSort(array, a, b);
                break;
            }
        } while(circlePass(array, a, b - 1));
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        sort(array, 0, length);
    }

}
