package sorts.quick;

import main.ArrayVisualizer;
import sorts.templates.Sort;
import utils.Rotations;

public class InPlaceLogSort extends Sort {

    public InPlaceLogSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        enableSort(false);
    }

    static final int INSERT_THRESHOLD = 32;

    int productLog(int n) {
        int r = 1;
        while ((r << r) + r - 1 < n) r++;
        return r;
    }

    int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    boolean pivCmp(int v, int piv, boolean eqLower) {
        int c = Reads.compareValues(v, piv);
        return c < 0 || (eqLower && c == 0);
    }
    
    void insertTo(int[] array, int a, int b) {
        Highlights.clearMark(2);
        int temp = array[a];
        int d = (a > b) ? -1 : 1;
        for (int i = a; i != b; i += d)
            Writes.write(array, i, array[i + d], 0.5, true, false);
        if (a != b) Writes.write(array, b, temp, 0.5, true, false);
    }

    void pivBufXor(int[] array, int pa, int pb, int v, int wLen) {
        while (wLen-- > 0) {
            if ((v & 1) != 0) Writes.swap(array, pa + wLen, pb + wLen, 1, true, false);
            v >>= 1;
        }
    }

    // @param bit - < pivot means this bit
    int pivBufGet(int[] array, int pa, int piv, boolean bias, int wLen, int bit) {
        int r = 0;
        while (wLen-- > 0) {
            r <<= 1;
            r |= (this.pivCmp(array[pa++], piv, bias) ? 0 : 1) ^ bit;
        }
        return r;
    }

    void pushBackwards(int[] array, int a, int b, int s) {
        while (s > 0) Writes.swap(array, a + --s, --b, 1, true, false);
    }

    protected void multiSwap(int[] array, int a, int b, int s) {
        if (a == b) return;
        for (int i = 0; i < s; i++)
            Writes.swap(array, a + i, b + i, 1, true, false);
    }
    
    protected void rotate(int[] array, int a, int l, int r) {
        Highlights.clearAllMarks();
        Rotations.cycleReverse(array, a, l, r, 1, true, false);
    }
    
    protected void rotateIndexed(int[] array, int a, int m, int b) {
        rotate(array, a, m - a, b - m);
    }
    
    protected int binSearch(int[] array, int a, int b, int val, boolean left) {
        while (a < b) {
            int m = a + (b - a) / 2;
            Highlights.markArray(2, m);
            Delays.sleep(0.25);
            int c = Reads.compareValues(val, array[m]);
            if (c < 0 || (left && c == 0)) b = m;
            else a = m+1;
        }
        return a;
    }

    protected int findKeys(int[] array, int a, int b, int nKeys, int n) {
        int p = a, pEnd = a + nKeys;
        Highlights.clearMark(2);
        for (int i = pEnd; i < b && nKeys < n; i++) {
            Highlights.markArray(1, i);
            Delays.sleep(1);
            int loc = binSearch(array, p, pEnd, array[i], true);
            if (pEnd == loc || Reads.compareValues(array[i], array[loc]) != 0) {
                rotateIndexed(array, p, pEnd, i);
                int inc = i - pEnd;
                loc += inc;
                p += inc;
                pEnd += inc;
                insertTo(array, pEnd, loc);
                nKeys++;
                pEnd++;
            }
        }
        rotateIndexed(array, a, p, pEnd);
        return nKeys;
    }

    public void insertSort(int[] array, int a, int b) {
        for (int i = a + 1; i < b; i++)
            insertTo(array, i, binSearch(array, a, i, array[i], false));
    }

    // median of 3
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
        if (b - a < 3) return a + (b - a) / 2;
        int t = (b - a) / 3;
        int l = medP3(array, a, a + t, --d), c = medP3(array, a + t, b - t, d), r = medP3(array, b - t, b, d);
        // median
        return medOf3(array, l, c, r);
    }

    public int medOfMed(int[] array, int a, int b) {
        int log5 = 0, exp5 = 1, exp5_1 = 0;
        int[] indices = new int[5];
        int n = b - a;
        while (exp5 < n) {
            exp5_1 = exp5;
            log5++;
            exp5 *= 5;
        }
        if (log5 < 1) return a;
        // fill indexes, recursing if required
        if (log5 == 1) for (int i = a, j = 0; i < b; i++, j++) indices[j] = i;
        else {
            n = 0;
            for (int i = a; i < b; i += exp5_1) {
                indices[n] = medOfMed(array, i, Math.min(b, i + exp5_1));
                n++;
            }
        }
        // sort - insertion sort is good enough for 5 elements
        for (int i = 1; i < n; i++) {
            for(int j = i; j > 0; j--) {
                if (Reads.compareIndices(array, indices[j], indices[j - 1], 0.5, true) < 0) {
                    int t = indices[j];
                    indices[j] = indices[j - 1];
                    indices[j - 1] = t;
                } else break;
            }
        }
        // return median
        return indices[(n - 1) / 2];
    }

    protected void mergePartitions(int[] array, int[] cnt, int a, int m, int b, int piv, boolean bias) {
        int m1 = binSearch(array, m, b, piv, !bias);
        int aCnt = m1 - m, mCnt = b - m1;
        rotateIndexed(array, a + cnt[0], m, m1);
        cnt[0] += aCnt;
        cnt[1] += mCnt;
    }
    
    protected void blockCycle(int[] array, int p, int n, int p1, int bLen, int wLen, int piv, boolean pCmp, int bit) {
        for (int i = 0; i < n; i++) {
            int dest = this.pivBufGet(array, p + i * bLen, piv, pCmp, wLen, bit);
            while (dest != i) {
                this.multiSwap(array, p + i * bLen, p + dest * bLen, bLen);
                dest = this.pivBufGet(array, p + i * bLen, piv, pCmp, wLen, bit);
            }
            this.pivBufXor(array, p + i * bLen, p1 + i * bLen, i, wLen);
        }
    }

    protected int[] partition(int[] array, int a, int b, int key, int bLen, int piv, boolean eqLower) {
        int l = 0, r = 0, lb = 0, rb = 0, t = a;
        boolean chkeq = false;
        for (int i = a; i < b; i++) {
            int cmp = Reads.compareIndexValue(array, i, piv, 1, true);
            if (cmp != 0) chkeq = true;
            if (cmp < 0 || eqLower && cmp == 0) {
                if (t + l != i) Writes.swap(array, t + l, i, 0.5, true, false);
                l++;
                if (l == bLen) {
                    t += bLen;
                    l = 0;
                    lb++;
                }
            } else {
                Writes.swap(array, key + r++, i, 0.5, true, false);
                if (r == bLen) {
                    multiSwap(array, t, i - l + 1, l);
                    multiSwap(array, key, t, bLen);
                    t += bLen;
                    r = 0;
                    rb++;
                }
            }
        }
        int min = Math.min(lb, rb), t1 = t, m = a + lb * bLen, tF = m;
        int wLen = log2(min - 1) + 1;
        boolean mR = false;
        if (min > 0) {
            if (bLen <= wLen) {
                // continually rotate blocks into bigger chunks (aphitorite's IPSC, modified for
                // binary partition merging)
                int nxt = t, i, ll = 0, rr = 0;
                int[] cnt = new int[2];
                tF = t;
                while (bLen <= wLen) {
                    cnt[0] = cnt[1] = 0;
                    bLen *= 2;
                    for (nxt = i = a; i < t; i += bLen) {
                        if (i + bLen / 2 < t && !pivCmp(array[i], piv, eqLower) && pivCmp(array[i + bLen / 2], piv, eqLower))
                            rotate(array, i, bLen / 2, Math.min(bLen / 2, t - i - bLen / 2));
                        mergePartitions(array, cnt, nxt, i, Math.min(i + bLen, t), piv, eqLower);
                        ll += cnt[0] / bLen;
                        nxt += cnt[0] - (cnt[0] %= bLen);
                        rotate(array, nxt, cnt[0], cnt[1] - (cnt[1] % bLen));
                        rr += cnt[1] / bLen;
                        nxt += cnt[1] - (cnt[1] %= bLen);
                    }
                    lb = ll;
                    rb = rr;
                    min = Math.min(ll, rr);
                    wLen = log2(min - 1) + 1;
                    t = nxt;
                    rotateIndexed(array, t + cnt[0], t + cnt[0] + cnt[1], tF);
                    tF -= cnt[1];
                    if (cnt[0] + cnt[1] > 0) mR = true;
                }
            }
            int bCnt = lb + rb;
            for (int i = 0, j = 0, k = 0; i < min; i++) {
                while (!this.pivCmp(array[a + j * bLen + wLen], piv, eqLower)) j++;
                while (this.pivCmp(array[a + k * bLen + wLen], piv, eqLower)) k++;
                this.pivBufXor(array, a + (j++) * bLen, a + (k++) * bLen, i, wLen);
            }
            if (lb < rb) {
                for (int i = bCnt - 1, j = 0; j < rb; i--) // swap right to left
                    if (!this.pivCmp(array[a + i * bLen + wLen], piv, eqLower))
                        this.multiSwap(array, a + i * bLen, a + (bCnt - (++j)) * bLen, bLen);
                this.blockCycle(array, a, lb, m, bLen, wLen, piv, eqLower, 0);
            } else {
                for (int i = 0, j = 0; j < lb; i++) // swap left to right
                    if (this.pivCmp(array[a + i * bLen + wLen], piv, eqLower))
                        this.multiSwap(array, a + i * bLen, a + (j++) * bLen, bLen);
                this.blockCycle(array, m, rb, a, bLen, wLen, piv, eqLower, 1);
            }
        }
        multiSwap(array, t1 + l, key, r);
        multiSwap(array, t1, key, l);
        if (l != 0) pushBackwards(array, tF, t1 + l, tF == m ? rb * bLen : t1 - tF);
        multiSwap(array, tF, key, l);
        if (mR) {
            rotateIndexed(array, m, t, tF + l);
            l += tF - t;
        }
        return new int[] { m + l, chkeq ? 0 : 1 };
    }

    protected void sortHelper(int[] array, int a, int b, int key, int bLen, boolean bad) {
        while (b - a > INSERT_THRESHOLD) {
            int pIdx;
            if (bad) {
                pIdx = medOfMed(array, a, b);
                bad = false;
            } else pIdx = medP3(array, a, b, 2);
            int[] pr = partition(array, a, b, key, bLen, array[pIdx], false);
            if (pr[1] != 0) return;
            int m = pr[0];
            int l = m - a, r = b - m;
            if (m == a) {
                m = partition(array, a, b, key, bLen, array[pIdx], true)[0];
                l = m - a; r = b - m;
                bad = l < r / 8;
                a = m;
                continue;
            }
            bad = l < r / 8 || r < l / 8;
            if (r < l) {
                sortHelper(array, m, b, key, bLen, bad);
                b = m;
            } else {
                sortHelper(array, a, m, key, bLen, bad);
                a = m;
            }
        }
        insertSort(array, a, b);
    }

    public void sort(int[] array, int a, int b) {
        
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        // TODO Auto-generated method stub

    }

}
