package sorts.hybrid;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/*

Coded for ArrayV by Ayako-chan
in collaboration with aphitorite and Gaming32

+---------------------------+
| Sorting Algorithm Scarlet |
+---------------------------+

 */

/**
 * @author Ayako-chan
 * @author aphitorite
 * @author Gaming32
 *
 */
public final class ColleiSort extends Sort {

    public ColleiSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Collei");
        this.setRunAllSortsName("Collei Sort");
        this.setRunSortName("Colleisort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    protected int binSearch(int[] array, int a, int b, int val, boolean left) {
        while (a < b) {
            int m = a + (b - a) / 2;
            Highlights.markArray(2, m);
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
    
    protected void multiSwap(int[] array, int a, int b, int len, boolean fw) {
        if (a == b) return;
        if (fw)
            for (int i = 0; i < len; i++)
                Writes.swap(array, a + i, b + i, 1, true, false);
        else
            for (int i = len - 1; i >= 0; i--)
                Writes.swap(array, a + i, b + i, 1, true, false);
    }

    protected void insertTo(int[] array, int a, int b) {
        Highlights.clearMark(2);
        int temp = array[a];
        int d = (a > b) ? -1 : 1;
        for (int i = a; i != b; i += d)
            Writes.write(array, i, array[i + d], 0.5, true, false);
        if (a != b)
            Writes.write(array, b, temp, 0.5, true, false);
    }
    
    protected void insertToBWAux(int[] array, int a, int b) {
        Highlights.clearMark(2);
        int temp = array[a];
        for (int i = a; i < b; i++)
            Writes.write(array, i, array[i + 1], 0.5, false, true);
        if (a != b)
            Writes.write(array, b, temp, 0.5, false, true);
    }
    
    protected void shiftFWExt(int[] array, int a, int m, int b) {
        Highlights.clearMark(2);
        while (m < b) Writes.write(array, a++, array[m++], 1, true, false);
    }

    protected void rotate(int[] array, int a, int m, int b) {
        Highlights.clearAllMarks();
        if (a >= m || m >= b) return;
        int l = m - a, r = b - m;
        if (l % r == 0 || r % l == 0) {
            while (l > 1 && r > 1)
                if (r < l) {
                    this.multiSwap(array, m - r, m, r, false);
                    b -= r;
                    m -= r;
                    l -= r;
                } else {
                    this.multiSwap(array, a, m, l, true);
                    a += l;
                    m += l;
                    r -= l;
                }
            if (r == 1) this.insertTo(array, m, a);
            else if (l == 1) this.insertTo(array, a, b - 1);
        } else {
            int p0 = a, p1 = m - 1, p2 = m, p3 = b - 1;
            int tmp;
            while (p0 < p1 && p2 < p3) {
                tmp = array[p1];
                Writes.write(array, p1--, array[p0], 0.5, true, false);
                Writes.write(array, p0++, array[p2], 0.5, true, false);
                Writes.write(array, p2++, array[p3], 0.5, true, false);
                Writes.write(array, p3--, tmp, 0.5, true, false);
            }
            while (p0 < p1) {
                tmp = array[p1];
                Writes.write(array, p1--, array[p0], 0.5, true, false);
                Writes.write(array, p0++, array[p3], 0.5, true, false);
                Writes.write(array, p3--, tmp, 0.5, true, false);
            }
            while (p2 < p3) {
                tmp = array[p2];
                Writes.write(array, p2++, array[p3], 0.5, true, false);
                Writes.write(array, p3--, array[p0], 0.5, true, false);
                Writes.write(array, p0++, tmp, 0.5, true, false);
            }
            if (p0 < p3) { // don't count reversals that don't do anything
                if (p3 - p0 >= 3) Writes.reversal(array, p0, p3, 1, true, false);
                else Writes.swap(array, p0, p3, 1, true, false);
                Highlights.clearMark(2);
            }
        }
    }
    
    protected void mergeFromBuf(int[] array, int[] buf, int a, int m, int b, int bufLen) {
        int i = 0;
        while(i < bufLen && m < b)
            if(Reads.compareValues(buf[i], array[m]) <= 0)
                Writes.write(array, a++, buf[i++], 1, true, false);
            else
                Writes.write(array, a++, array[m++], 1, true, false);
        while(i < bufLen) Writes.write(array, a++, buf[i++], 1, true, false);
    }
    
    protected void mergeFWExt(int[] array, int[] tmp, int a, int m, int b) {
        int s = m - a;
        Writes.arraycopy(array, a, tmp, 0, s, 1, true, true);
        int i = 0, j = m;
        while (i < s && j < b)
            if (Reads.compareValues(tmp[i], array[j]) <= 0)
                Writes.write(array, a++, tmp[i++], 1, true, false);
            else
                Writes.write(array, a++, array[j++], 1, true, false);
        while (i < s) Writes.write(array, a++, tmp[i++], 1, true, false);
    }

    protected void mergeBWExt(int[] array, int[] tmp, int a, int m, int b) {
        int s = b - m;
        Writes.arraycopy(array, m, tmp, 0, s, 1, true, true);
        int i = s - 1, j = m - 1;
        while (i >= 0 && j >= a)
            if (Reads.compareValues(tmp[i], array[j]) >= 0)
                Writes.write(array, --b, tmp[i--], 1, true, false);
            else
                Writes.write(array, --b, array[j--], 1, true, false);
        while (i >= 0) Writes.write(array, --b, tmp[i--], 1, true, false);
    }
    
    protected void inPlaceMergeFW(int[] array, int a, int m, int b) {
        while (a < m && m < b) {
            int i = leftExpSearch(array, m, b, array[a], true);
            rotate(array, a, m, i);
            int t = i - m;
            m = i;
            a += t + 1;
            if (m >= b) break;
            a = leftExpSearch(array, a, m, array[m], false);
        }
    }

    protected void inPlaceMergeBW(int[] array, int a, int m, int b) {
        while (b > m && m > a) {
            int i = rightExpSearch(array, a, m, array[b - 1], false);
            rotate(array, i, m, b);
            int t = m - i;
            m = i;
            b -= t + 1;
            if (m <= a) break;
            b = rightExpSearch(array, m, b, array[m - 1], true);
        }
    }
    
    protected void merge(int[] array, int[] buf, int a, int m, int b) {
        Highlights.clearMark(2);
        if (Math.min(m - a, b - m) <= 8) {
            if (m - a > b - m)
                inPlaceMergeBW(array, a, m, b);
            else
                inPlaceMergeFW(array, a, m, b);
        } else if (m - a > b - m)
            mergeBWExt(array, buf, a, m, b);
        else
            mergeFWExt(array, buf, a, m, b);
    }
    
    protected boolean getSubarray(int[] tags, int t, int mKey) {
        return Reads.compareOriginalValues(tags[t], tags[mKey]) < 0;
    }

    // returns mKey final position
    protected int blockSelect(int[] array, int[] tags, int p, int r, int d, int lCnt, int bCnt, int bLen) {
        int mKey = lCnt;
        for (int j = 0, k = lCnt + 1; j < k - 1; j++) {
            int min = j;
            for (int i = Math.max(lCnt - r, j + 1); i < k; i++) {
                int cmp = Reads.compareIndices(array, p + d + i * bLen, p + d + min * bLen, 0, false);
                if (cmp < 0 || (cmp == 0 && Reads.compareOriginalValues(tags[i], tags[min]) < 0)) min = i;
            }
            if (min != j) {
                multiSwap(array, p + j * bLen, p + min * bLen, bLen, true);
                Writes.swap(tags, j, min, 1, false, true);
                if (k < bCnt && min == k - 1) k++;
            }
            if (min == mKey) mKey = j;
        }
        return mKey;
    }

    protected int mergeBlocks(int[] array, int a, int m, int b, int p, boolean fwEq) {
        int i = a, j = m;
        while (i < m && j < b) {
            int cmp = Reads.compareIndices(array, i, j, 0.0, true);
            if (cmp < 0 || (fwEq && cmp == 0))
                Writes.write(array, p++, array[i++], 1.0, true, false);
            else
                Writes.write(array, p++, array[j++], 1.0, true, false);
        }
        if(i > p) shiftFWExt(array, p, i, m);
        return j;
    }

    // is never called if m-a || b-m <= bLen
    // should never be called if (m-a)%bLen != 0
    protected void blockMerge(int[] array, int[] buf, int[] tags, int a, int m, int b, int bLen) {
        if (Math.min(m - a, b - m) <= bLen) {
            merge(array, buf, a, m, b);
            return;
        }
        int b1 = b - (b - m - 1) % bLen - 1,
                i = a + bLen, j = a, key = -1,
                lCnt = (m - i) / bLen, bCnt = (b1 - i) / bLen, l = -1, r = lCnt - 1;
        for (int k = 0; k < bLen; k++) {
            Writes.write(buf, k, array[m - bLen + k], 0.5, true, false);
            Writes.write(array, m - bLen + k, array[a + k], 0.5, true, false);
        }
        for (int k = 0; k < bCnt; k++)
            Writes.write(tags, k, k, 0, true, true);
        insertToBWAux(tags, 0, lCnt - 1);
        int mKey = blockSelect(array, tags, i, 1, bLen - 1, lCnt, bCnt, bLen);
        boolean frag = true;
        while(l < lCnt && r < bCnt) {
            if(frag) {
                do {
                    j += bLen;
                    l++;
                    key++;
                } while (l < lCnt && getSubarray(tags, key, mKey));
                if (l == lCnt) {
                    i = mergeBlocks(array, i, j, b, i - bLen, true);
                    mergeFromBuf(array, buf, i - bLen, i, b, bLen);
                } else i = mergeBlocks(array, i, j, j + bLen - 1, i - bLen, true);
            } else {
                do {
                    j += bLen;
                    r++;
                    key++;
                } while (r < bCnt && !getSubarray(tags, key, mKey));
                if (r == bCnt) {
                    shiftFWExt(array, i - bLen, i, b);
                    Writes.arraycopy(buf, 0, array, b - bLen, bLen, 1, true, false);
                } else i = mergeBlocks(array, i, j, j + bLen - 1, i - bLen, false);
            }
            frag = !frag;
        }
    }
    
    protected void smartBlockMerge(int[] array, int[] buf, int[] tags, int a, int m, int b, int bLen) {
        if (Reads.compareIndices(array, m - 1, m, 0.0, true) <= 0) return;
        a = leftExpSearch(array, a, m, array[m], false);
        b = rightExpSearch(array, m, b, array[m - 1], true);
        if (Reads.compareValues(array[a], array[b - 1]) > 0) {
            rotate(array, a, m, b);
            return;
        }
        if (Math.min(m - a, b - m) <= bLen) merge(array, buf, a, m, b);
        else {
            int a1 = a + (m - a) % bLen;
            blockMerge(array, buf, tags, a1, m, b, bLen);
            if (a1 > a) mergeFWExt(array, buf, a, a1, b);
        }
    }
    
    protected int findRun(int[] array, int a, int b, int mRun) {
        int i = a + 1;
        if (i < b)
            if (Reads.compareIndices(array, i - 1, i++, 0.5, true) > 0) {
                while (i < b && Reads.compareIndices(array, i - 1, i, 0.5, true) > 0) i++;
                if (i - a < 4) Writes.swap(array, a, i - 1, 1.0, true, false);
                else Writes.reversal(array, a, i - 1, 1.0, true, false);
            } else
                while (i < b && Reads.compareIndices(array, i - 1, i, 0.5, true) <= 0) i++;
        Highlights.clearMark(2);
        while (i - a < mRun && i < b) {
            insertTo(array, i, rightExpSearch(array, a, i, array[i], false));
            i++;
        }
        return i;
    }
    
    /**
     * Sorts the range {@code [a, b)} of {@code array} using Extra-Adaptive Sqrtsort.
     * @param array the array
     * @param a the start of the range, inclusive
     * @param b the end of the range, exclusive
     */
    public void mergeSort(int[] array, int a, int b) {
        int len = b - a;
        if (len <= 32) {
            // insertion sort
            findRun(array, a, b, b - a);
            return;
        }
        int mRun = 16;
        int[] runs = Writes.createExternalArray((b - a - 1) / mRun + 2);
        int r = a, rf = 0;
        while (r < b) {
            Writes.write(runs, rf++, r, 0.5, false, true);
            r = findRun(array, r, b, mRun);
        }
        int bLen = 1;
        while (bLen * bLen < len) bLen *= 2;
        int[] buf = Writes.createExternalArray(bLen);
        int[] tags = Writes.createExternalArray(len / bLen);
        while (rf > 1) {
            for (int i = 0; i < rf - 1; i += 2) {
                int eIdx = (i + 2 >= rf) ? b : runs[i + 2];
                smartBlockMerge(array, buf, tags, runs[i], runs[i + 1], eIdx, bLen);
            }
            for (int i = 1, j = 2; i < rf; i++, j+=2, rf--) {
                Writes.write(runs, i, runs[j], 0.5, false, true);
            }
        }
        Writes.deleteExternalArray(tags);
        Writes.deleteExternalArray(buf);
        Writes.deleteExternalArray(runs);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        mergeSort(array, 0, sortLength);

    }

}
