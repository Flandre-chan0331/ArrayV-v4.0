package sorts.quick;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public final class OptimizedStableQuickSort extends Sort {

    public OptimizedStableQuickSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        enableSort(false);
    }
    
    static final int M = 7;
    
    int threshold = 32;
    int highlight = 0;
    
    protected int medOf3(int[] array, int i0, int i1, int i2) {
        int tmp;
        if(Reads.compareIndices(array, i0, i1, 1, true) > 0) {
            tmp = i1;
            i1 = i0;
        } else tmp = i0;
        if(Reads.compareIndices(array, i1, i2, 1, true) > 0) {
            if(Reads.compareIndices(array, tmp, i2, 1, true) > 0) return tmp;
            return i2;
        }
        return i1;
    }
    
    public int medP3(int[] array, int a, int b, int d) {
        if (b - a == 3 || (b - a > 3 && d == 0))
            return medOf3(array, a, a + (b - a) / 2, b - 1);
        else if (b - a < 3) return a + (b - a) / 2;
        int t = (b - a) / 3;
        int l = medP3(array, a, a + t, --d), c = medP3(array, a + t, b - t, d), r = medP3(array, b - t, b, d);
        // median
        return medOf3(array, l, c, r);
    }

    public int medOfMed(int[] array, int a, int b) {
        if (b - a <= 6) return a + (b - a) / 2;
        int p = 1;
        while (6 * p < b - a) p *= 3;
        int l = medP3(array, a, a + p, -1), c = medOfMed(array, a + p, b - p), r = medP3(array, b - p, b, -1);
        // median
        return medOf3(array, l, c, r);
    }
    
    protected int binSearch(int[] array, int a, int b, int val, boolean left) {
        while (a < b) {
            int m = a + (b - a) / 2;
            Highlights.markArray(2, highlight + m);
            Delays.sleep(0.25);
            int c = Reads.compareValues(val, array[m]);
            if (c < 0 || (left && c == 0)) b = m;
            else a = m + 1;
        }
        return a;
    }

    protected int leftExpSearch(int[] array, int a, int b, int val, boolean left) {
        int i = 1;
        if (left)
            while (a - 1 + i < b && Reads.compareValues(val, array[a - 1 + i]) > 0) i *= 2;
        else
            while (a - 1 + i < b && Reads.compareValues(val, array[a - 1 + i]) >= 0) i *= 2;
        int a1 = a + i / 2, b1 = Math.min(b, a - 1 + i);
        return binSearch(array, a1, b1, val, left);
    }

    protected int rightExpSearch(int[] array, int a, int b, int val, boolean left) {
        int i = 1;
        if (left)
            while (b - i >= a && Reads.compareValues(val, array[b - i]) <= 0) i *= 2;
        else
            while (b - i >= a && Reads.compareValues(val, array[b - i]) < 0) i *= 2;
        int a1 = Math.max(a, b - i + 1), b1 = b - i / 2;
        return binSearch(array, a1, b1, val, left);
    }

    protected void insertTo(int[] array, int a, int b) {
        Highlights.clearMark(2);
        int temp = array[a];
        int d = (a > b) ? -1 : 1;
        for (int i = a; i != b; i += d)
            Writes.write(array, i, array[i + d], 0.5, true, false);
        if (a != b) Writes.write(array, b, temp, 0.5, true, false);
    }
    
    // galloping mode code refactored from TimSorting.java
    protected void mergeFWExt(int[] array, int[] tmp, int a, int m, int b) {
        int len1 = m - a, t = a;
        Highlights.clearMark(2);
        Writes.arraycopy(array, a, tmp, 0, len1, 1, true, true);
        int i = 0, mGallop = M, l = 0, r = 0;
        while (true) {
            do {
                if (Reads.compareValues(tmp[i], array[m]) <= 0) {
                    Writes.write(array, a++, tmp[i++], 1, true, false);
                    l++;
                    r = 0;
                    if (i == len1) return;
                } else {
                    Highlights.markArray(2, m);
                    Writes.write(array, a++, array[m++], 1, true, false);
                    r++;
                    l = 0;
                    if (m == b) {
                        while (i < len1) Writes.write(array, a++, tmp[i++], 1, true, false);
                        return;
                    }
                }
            } while ((l | r) < mGallop);
            do {
                l = leftExpSearch(array, m, b, tmp[i], true) - m;
                for (int j = 0; j < l; j++)
                    Writes.write(array, a++, array[m++], 1, true, false);
                Writes.write(array, a++, tmp[i++], 1, true, false);
                if (i == len1) return;
                if (m == b) {
                    while (i < len1) Writes.write(array, a++, tmp[i++], 1, true, false);
                    return;
                }
                highlight = t;
                r = leftExpSearch(tmp, i, len1, array[m], false) - i;
                highlight = 0;
                for (int j = 0; j < r; j++)
                    Writes.write(array, a++, tmp[i++], 1, true, false);
                Writes.write(array, a++, array[m++], 1, true, false);
                if (i == len1) return;
                if (m == b) {
                    while (i < len1) Writes.write(array, a++, tmp[i++], 1, true, false);
                    return;
                }
                mGallop--;
            } while ((l | r) >= M);
            if (mGallop < 0) mGallop = 0;
            mGallop += 2;
        }
    }

    protected void mergeBWExt(int[] array, int[] tmp, int a, int m, int b) {
        int len2 = b - m, t = a;
        Highlights.clearMark(2);
        Writes.arraycopy(array, m, tmp, 0, len2, 1, true, true);
        int i = len2 - 1, mGallop = M, l = 0, r = 0;
        m--;
        while (true) {
            do {
                if (Reads.compareValues(tmp[i], array[m]) >= 0) {
                    Writes.write(array, --b, tmp[i--], 1, true, false);
                    l++;
                    r = 0;
                    if (i < 0) return;
                } else {
                    Highlights.markArray(2, m);
                    Writes.write(array, --b, array[m--], 1, true, false);
                    r++;
                    l = 0;
                    if (m < a) {
                        while (i >= 0) Writes.write(array, --b, tmp[i--], 1, true, false);
                        return;
                    }
                }
            } while ((l | r) < mGallop);
            do {
                l = (m + 1) - rightExpSearch(array, a, m + 1, tmp[i], false);
                for (int j = 0; j < l; j++)
                    Writes.write(array, --b, array[m--], 1, true, false);
                Writes.write(array, --b, tmp[i--], 1, true, false);
                if (i < 0) return;
                if (m < a) {
                    while (i >= 0) Writes.write(array, --b, tmp[i--], 1, true, false);
                    return;
                }
                highlight = t;
                r = (i + 1) - rightExpSearch(tmp, 0, i + 1, array[m], true);
                highlight = 0;
                for (int j = 0; j < r; j++)
                    Writes.write(array, --b, tmp[i--], 1, true, false);
                Writes.write(array, --b, array[m--], 1, true, false);
                if (i < 0) return;
                if (m < a) {
                    while (i >= 0) Writes.write(array, --b, tmp[i--], 1, true, false);
                    return;
                }
            } while ((l | r) >= M);
            if (mGallop < 0) mGallop = 0;
            mGallop += 2;
        }
    }

    protected void merge(int[] array, int[] buf, int a, int m, int b) {
        if (Reads.compareIndices(array, m - 1, m, 0.0, true) <= 0) return;
        a = leftExpSearch(array, a, m, array[m], false);
        b = rightExpSearch(array, m, b, array[m - 1], true);
        Highlights.clearMark(2);
        if (m - a > b - m) mergeBWExt(array, buf, a, m, b);
        else mergeFWExt(array, buf, a, m, b);
    }

    protected int findRun(int[] array, int a, int b, int mRun) {
        int i = a + 1;
        if (i < b) {
            if (Reads.compareIndices(array, i - 1, i++, 0.5, true) > 0) {
                while (i < b && Reads.compareIndices(array, i - 1, i, 0.5, true) > 0) i++;
                if (i - a < 4) Writes.swap(array, a, i - 1, 1.0, true, false);
                else Writes.reversal(array, a, i - 1, 1.0, true, false);
            } else
                while (i < b && Reads.compareIndices(array, i - 1, i, 0.5, true) <= 0) i++;
        }
        Highlights.clearMark(2);
        while (i - a < mRun && i < b) {
            insertTo(array, i, rightExpSearch(array, a, i, array[i], false));
            i++;
        }
        return i;
    }

    public void insertSort(int[] array, int a, int b) {
        // technically an insertion sort
        findRun(array, a, b, b - a);
    }

    public void mergeSort(int[] array, int[] buf, int a, int b) {
        int len = b - a;
        if (len <= threshold) {
            insertSort(array, a, b);
            return;
        }
        int mRun = 16;
        int[] runs = Writes.createExternalArray((len - 1) / mRun + 2);
        int r = a, rf = 0;
        while (r < b) {
            Writes.write(runs, rf++, r, 0.5, false, true);
            r = findRun(array, r, b, mRun);
        }
        while (rf > 1) {
            for (int i = 0; i < rf - 1; i += 2) {
                int eIdx;
                if (i + 2 >= rf) eIdx = b;
                else eIdx = runs[i + 2];
                merge(array, buf, runs[i], runs[i + 1], eIdx);
            }
            for (int i = 1, j = 2; i < rf; i++, j+=2, rf--)
                Writes.write(runs, i, runs[j], 0.5, false, true);
        }
        Writes.deleteExternalArray(runs);
    }
    
    int partition(int[] array, int[] buf, int left, int right, int pivIdx) {
        Highlights.clearMark(2);
        int a = left, b = right;
        int piv = array[pivIdx];
        // determines which elements do not need to be moved
        for (; a < pivIdx; a++) {
            Highlights.markArray(1, a);
            Delays.sleep(0.25);
            if (Reads.compareValues(array[a], piv) > 0) break;
        }
        for (; b > pivIdx + 1; b--) {
            Highlights.markArray(1, b - 1);
            Delays.sleep(0.25);
            if (Reads.compareValues(array[b - 1], piv) < 0) break;
        }
        // partitions the list stably
        int j = a, k = 0;
        for (int i = a; i < pivIdx; i++) {
            Highlights.markArray(2, j);
            if (Reads.compareIndexValue(array, i, piv, 0, true) <= 0)
                Writes.write(array, j++, array[i], 0.5, false, false);
            else Writes.write(buf, k++, array[i], 0.5, false, true);
        }
        for (int i = pivIdx + 1; i < b; i++) {
            Highlights.markArray(2, j);
            if (Reads.compareIndexValue(array, i, piv, 0, true) < 0)
                Writes.write(array, j++, array[i], 0.5, false, false);
            else Writes.write(buf, k++, array[i], 0.5, false, true);
        }
        // write the pivot at its correct location
        Writes.write(array, j, piv, 0.5, true, false);
        Writes.arraycopy(buf, 0, array, j + 1, k, 0.5, true, false);
        return 0;
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        // TODO Auto-generated method stub

    }

}
