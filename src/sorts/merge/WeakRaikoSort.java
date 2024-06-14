package sorts.merge;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public class WeakRaikoSort extends Sort {
    public WeakRaikoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Weak Raiko");
        this.setRunAllSortsName("Weak Raiko Sort");
        this.setRunSortName("Weak Raikosort");
        this.setCategory("Merge Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    protected boolean keyLessThan(int[] src, int[] pa, int a, int b) {
        int cmp = Reads.compareValues(src[pa[a]], src[pa[b]]);
        return cmp < 0 || (cmp == 0 && Reads.compareOriginalValues(a, b) < 0);
    }

    protected void siftDown(int[] src, int[] heap, int[] pa, int t, int r, int size) {
        while(2*r+2 < size) {
            int nxt = 2*r+1;
            int min = nxt + (this.keyLessThan(src, pa, heap[nxt], heap[nxt+1]) ? 0 : 1);
            if(this.keyLessThan(src, pa, heap[min], t)) {
                Writes.write(heap, r, heap[min], 0.25, true, true);
                r = min;
            } else break;
        }
        int min = 2*r+1;
        if(min < size && this.keyLessThan(src, pa, heap[min], t)) {
            Writes.write(heap, r, heap[min], 0.25, true, true);
            r = min;
        }
        Writes.write(heap, r, t, 0.25, true, true);
    }

    protected void kWayMerge(int[] src, int[] dest, int[] heap, int[] pa, int[] pb, int size, boolean aux) {
        for(int i = 0; i < size; i++)
            Writes.write(heap, i, i, 0, false, true);
        for(int i = (size-1)/2; i >= 0; i--)
            this.siftDown(src, heap, pa, heap[i], i, size);
        for(int i = 0; size > 0; i++) {
            int min = heap[0];
            Highlights.markArray(2, pa[min]);
            Writes.write(dest, i, src[pa[min]], 0.5, !aux, aux);
            Writes.write(pa, min, pa[min]+1, 0, false, true);
            if(pa[min] == pb[min])
                this.siftDown(src, heap, pa, heap[--size], 0, size);
            else 
                this.siftDown(src, heap, pa, heap[0], 0, size);
        }
    }

    protected int findRun(int[] array, int a, int b) {
        int i = a + 1;
        while (i < b) {
            if (Reads.compareIndices(array, i - 1, i, 0.5, true) > 0) break;
            i++;
        }
        Highlights.clearMark(2);
        return i;
    }

    public void mergeSort(int[] array, int a, int b) {
        int len = b - a;
        if (len < 2) return;
        int[] runs = Writes.createExternalArray(len + 1);
        int r = a, rf = 0;
        while (r < b) {
            Writes.write(runs, rf++, r, 0.5, false, true);
            r = findRun(array, r, b);
        }
        Writes.write(runs, rf, b, 0.5, false, true);
        int[] buf = Writes.createExternalArray(len);
        int alloc = 0;
        if (rf > 1) {
            int[] pa   = new int[rf];
            int[] pb   = new int[rf];
            int[] heap = new int[rf];
            alloc = 3 * rf;
            Writes.changeAllocAmount(alloc);
            for (int i = 0; i < rf; i++) {
                Writes.write(pa, i, runs[i], 0, false, true);
                Writes.write(pb, i, runs[i + 1], 0, false, true);
            }
            kWayMerge(array, buf, heap, pa, pb, rf, true);
            Highlights.clearAllMarks();
            Writes.arraycopy(buf, 0, array, a, len, 1, true, false);
        }
        Writes.deleteExternalArray(buf);
        Writes.deleteExternalArray(runs);
        Writes.changeAllocAmount(-alloc);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        mergeSort(array, 0, sortLength);
    }
}
