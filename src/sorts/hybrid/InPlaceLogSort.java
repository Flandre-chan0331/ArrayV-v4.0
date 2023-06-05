package sorts.hybrid;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public final class InPlaceLogSort extends Sort {

    public InPlaceLogSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        enableSort(false);
    }

    int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    void set(int[] array, int lo, int hi, int val, int log, boolean bit) {
        while(log-- > 0) {
            if((val % 2 == 1) == bit)
                Writes.swap(array, lo + log, hi + log, 1, true, false);
            val /= 2;
        }
    }

    int get(int[] array, int block, int piv, int log, int bias) {
        int v = 0, s = 1;
        while(log-- > 0) {
            v |= Reads.compareIndexValue(array, block+log, piv, 1, true) > -bias ? s : 0;
            s *= 2;
        }
        return v;
    }
    
    boolean pivCmp(int v, int piv, boolean eqLower) {
        int c = Reads.compareValues(v, piv);
        return c < 0 || (eqLower && c == 0);
    }

    void pivBufXor(int[] array, int pa, int pb, int v, int wLen) {
        while (wLen-- > 0) {
            if (v % 2 == 1) Writes.swap(array, pa + wLen, pb + wLen, 1, true, false);
            v /= 2;
        }
    }

    // @param bit - < pivot means this bit
    int pivBufGet(int[] array, int pa, int piv, boolean bias, int wLen, int bit) {
        int r = 0;
        while (wLen-- > 0) {
            r *= 2;
            r |= (this.pivCmp(array[pa++], piv, bias) ? 0 : 1) ^ bit;
        }
        return r;
    }

    void pushBackwards(int[] array, int a, int b, int s) {
        while(s > 0) Writes.swap(array,a+--s,--b,1,true,false);
    }

    protected void multiSwap(int[] array, int a, int b, int s) {
        if (a == b) return;
        for (int i = 0; i < s; i++)
            Writes.swap(array, a + i, b + i, 1, true, false);
    }
    
    protected void rotate(int[] array, int a, int l, int r) {
        Highlights.clearAllMarks();
        if (l < 1 || r < 1) return;
        int p0 = a, p1 = a + l - 1, p2 = a + l, p3 = a + l + r - 1;
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
    
    protected void rotateIndexed(int[] array, int a, int m, int b) {
        rotate(array, a, m - a, b - m);
    }

    // median of 3
    protected int medOf3(int[] array, int l0, int l1, int l2) {
        int t;
        if(Reads.compareIndices(array, l0, l1, 1, true) > 0) {
            t = l0; l0 = l1; l1 = t;
        }
        if(Reads.compareIndices(array, l1, l2, 1, true) > 0) {
            t = l1; l1 = l2; l2 = t;
            if(Reads.compareIndices(array, l0, l1, 1, true) > 0)
                return l0;
        }
        return l1;
    }

    // median of medians with customizable depth
    protected int medOfMed(int[] array, int start, int end, int depth) {
        if(end-start < 9 || depth <= 0)
            return medOf3(array, start, start+(end-start)/2, end);
        int div = (end - start) / 8;
        int m0 = medOfMed(array, start, start + 2 * div, --depth);
        int m1 = medOfMed(array, start + 3 * div, start + 5 * div, depth);
        int m2 = medOfMed(array, start + 6 * div, end, depth);
        return medOf3(array, m0, m1, m2);
    }

    int binSearch(int[] array, int a, int b, int val, int bias) {
        while(a < b) {
            int m = a + (b - a) / 2;
            if(Reads.compareValues(val, array[m]) < bias)
                b = m;
            else
                a = m+1;
        }
        return a;
    }

    protected void mergePartitions(int[] array, int[] cnt, int a, int m, int b, int piv, int bias) {
        int m1 = binSearch(array, m, b, piv, bias);
        int aCnt = m1-m, mCnt = b-m1;
        rotateIndexed(array, a+cnt[0], m, m1);
        cnt[0] += aCnt;
        cnt[1] += mCnt;
    }
    
    void blockCycle(int[] array, int a, int b, int tag, int piv, int blk, int blog, boolean bias, int bit) {
        int i=a, j=0;
        for(; i<b-blk; i+=blk, j++) {
            int z = pivBufGet(array, i, piv, bias, blog, bit);
            while(z != j) {
                multiSwap(array, i, a+z*blk, blk);
                z = pivBufGet(array, i, piv, bias, blog, bit);
            }
            pivBufXor(array, i, tag+i-a, j, blog);
        }
        pivBufXor(array, i, tag+i-a, j, blog);
    }

    protected int[] partition(int[] array, int start, int end, int key, int bLen, int piv, int bias) {
        int l = 0, r = 0, lb = 0, rb = 0, t = start;
        boolean chkeq = false;
        for(int i = start; i < end; i++) {
            int cmp = Reads.compareIndexValue(array, i, piv, 1, true);
            if(cmp != 0) chkeq = true;
            if(cmp > -bias) {
                Writes.swap(array, key+r++, i, 0.5, true, false);
                if(r == bLen) {
                    multiSwap(array, t, i-l+1, l);
                    multiSwap(array, key, t, bLen);
                    t += bLen; r = 0; rb++;
                }
            } else {
                if (t + l != i) Writes.swap(array, t+l, i, 0.5, true, false);
                l++;
                if(l == bLen) {
                    t += bLen; l = 0; lb++;
                }
            }
        }
        int min = Math.min(lb, rb), t1 = t, mid = start + lb * bLen, tF = mid;
        int j, k, wLen = log2(min-1)+1;
        boolean mR = false;
        if(min > 0) {
            if(bLen <= wLen) {
                // continually rotate blocks into bigger chunks (aphitorite's IPSC, modified for binary partition merging)
                int nxt = t, i, ll = 0, rr = 0;
                int[] cnt = new int[2];
                tF = t;
                while(bLen <= wLen) {
                    cnt[0] = cnt[1] = 0;
                    bLen *= 2;
                    for(nxt = i = start; i < t; i += bLen) {
                        if(i + bLen / 2 < t &&
                            Reads.compareValues(array[i], piv) > -bias &&
                            Reads.compareValues(array[i+bLen/2], piv) <= -bias)
                            rotate(array, i, bLen/2, Math.min(bLen/2, t - i - bLen/2));
                        mergePartitions(array, cnt, nxt, i, Math.min(i+bLen, t), piv, bias);
                        ll += cnt[0] / bLen;
                        nxt += cnt[0] - (cnt[0] %= bLen);
                        rotate(array, nxt, cnt[0], cnt[1] - (cnt[1] % bLen));
                        rr += cnt[1] / bLen;
                        nxt += cnt[1] - (cnt[1] %= bLen);
                    }
                    lb = ll;
                    rb = rr;
                    min = Math.min(ll, rr);
                    wLen = log2(min-1)+1;
                    t = nxt;
                    rotateIndexed(array, t + cnt[0], t + cnt[0] + cnt[1], tF);
                    tF -= cnt[1];
                    if(cnt[0] + cnt[1] > 0) mR = true;
                }
            }
            j = k = start;
            for(int i = 0; i < min; i++) {
                while(Reads.compareValues(array[j+wLen], piv) <= -bias) j += bLen;
                while(Reads.compareValues(array[k+wLen], piv) > -bias) k += bLen;
                set(array, j, k, i, wLen, lb < rb);
                j += bLen;
                k += bLen;
            }
            if(lb < rb) {
                for(j = t - bLen, k = t; j >= start; j -= bLen)
                    if(Reads.compareValues(array[j+wLen], piv) > -bias)
                        multiSwap(array, j, k -= bLen, bLen);
                for(int i = start, h = 0; i < k; i += bLen, h++) {
                    int w = get(array, i, piv, wLen, bias);
                    while(h != w) { // index sort
                        multiSwap(array, start+w*bLen, i, bLen);
                        w = get(array, i, piv, wLen, bias);
                    }
                    set(array, i, k+h*bLen, h, wLen, lb < rb); // compareless clear the block tag
                }
            } else {
                for(j = start, k = start; j < t; j += bLen)
                    if(Reads.compareValues(array[j+wLen], piv) <= -bias) {
                        multiSwap(array, j, k, bLen);
                        k += bLen;
                    }
                for(int h = 0; k < t; k += bLen, h++) {
                    int w = get(array, k, piv, wLen, bias);
                    while(h != w) {
                        multiSwap(array, k+(w-h)*bLen, k, bLen);
                        w = get(array, k, piv, wLen, bias);
                    }
                    set(array, k, start+h*bLen, h, wLen, lb < rb); // compareless clear the block tag
                }
            }
        }
        multiSwap(array, t1+l, key, r);
        multiSwap(array, t1, key, l);
        if(l != 0) pushBackwards(array, tF, t1+l, tF == mid ? rb*bLen : t1 - tF);
        multiSwap(array, tF, key, l);
        if(mR) {
            rotateIndexed(array, mid, t, tF + l);
            l += tF - t;
        }
        return new int[] {mid + l, chkeq ? 0 : 1};
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        // TODO Auto-generated method stub

    }

}
