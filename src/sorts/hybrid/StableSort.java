package sorts.hybrid;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public class StableSort extends Sort {

    public StableSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Stable");
        this.setRunAllSortsName("Stable Sort");
        this.setRunSortName("Stablesort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    static final int MIN_RUN = 7; // original impl: 7
    
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
    
    public void insertSort(int[] array, int a, int b, double sleep, boolean auxwrite) {
        for (int i = a + 1; i < b; i++) {
            int current = array[i];
            int dest = binSearch(array, a, i, current);
            int pos = i;
            while (pos > dest) Writes.write(array, pos, array[--pos], sleep, true, auxwrite);
            if (pos < i) Writes.write(array, pos, current, sleep, true, auxwrite);
        }
    }
    
    protected void mergeTo(int[] from, int[] to, int a, int m, int b, int p, boolean aux) {
        int i = a, j = m;
        while(i < m && j < b) {
            Highlights.markArray(2, i);
            Highlights.markArray(3, j);
            if(Reads.compareValues(from[i], from[j]) <= 0)
                Writes.write(to, p++, from[i++], 1, true, aux);
            else
                Writes.write(to, p++, from[j++], 1, true, aux);
        }
        Highlights.clearMark(3);
        while(i < m) {
            Highlights.markArray(2, i);
            Writes.write(to, p++, from[i++], 1, true, aux);
        }
        while(j < b) {
            Highlights.markArray(2, j);
            Writes.write(to, p++, from[j++], 1, true, aux);
        }
        Highlights.clearMark(2);
    }
    
    public void mergePP(int[] array, int[] tmp, int a, int b, int o, boolean startaux) {
        int j = MIN_RUN, len = b - a;
        for (int s = startaux ? o : a, i = s; i < s + len; i += j)
            insertSort(startaux ? tmp : array, i, Math.min(i + j, s + len), 0.5, startaux);
        boolean toAux = !startaux;
        for (; j < len; j *= 2) {
            int t = toAux ? o : a, f = toAux ? a : o, i;;
            for (i = 0; i + 2 * j <= len; i += 2 * j)
                mergeTo(toAux ? array : tmp, toAux ? tmp : array, f + i, f + i + j,
                        f + i + 2 * j, t + i, toAux);
            if (i + j < len)
                mergeTo(toAux ? array : tmp, toAux ? tmp : array, f + i, f + i + j, f + len, t + i, toAux);
            else
                Writes.arraycopy(toAux ? array : tmp, f + i, toAux ? tmp : array, t + i, len - i, 1, true, toAux);
            toAux = !toAux;
        }
        if (!toAux) Writes.arraycopy(tmp, o, array, a, len, 1, true, false);
    }
    
    protected void mergeFWExt(int[] array, int[] tmp, int a, int m, int b) {
        int s = m - a;
        Writes.arraycopy(array, a, tmp, 0, s, 1, true, true);
        int i = 0, j = m;
        while (i < s && j < b) {
            Highlights.markArray(2, j);
            if (Reads.compareValues(tmp[i], array[j]) <= 0)
                Writes.write(array, a++, tmp[i++], 1, true, false);
            else
                Writes.write(array, a++, array[j++], 1, true, false);
        }
        Highlights.clearMark(2);
        while (i < s) Writes.write(array, a++, tmp[i++], 1, true, false);
    }
    
    public void mergeSort(int[] array, int a, int b) {
        int len = b - a;
        int[] buf = Writes.createExternalArray((len + 1) / 2);
        mergePP(array, buf, a, a + len / 2, 0, false);
        mergePP(array, buf, a + len / 2, b, 0, false);
        mergeFWExt(array, buf, a, a + len / 2, b);
        Writes.deleteExternalArray(buf);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        mergeSort(array, 0, sortLength);

    }

}
