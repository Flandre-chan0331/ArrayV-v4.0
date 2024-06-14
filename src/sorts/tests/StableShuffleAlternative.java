package sorts.tests;

import java.util.Random;
import main.ArrayVisualizer;
import sorts.hybrid.EctaSort;
import sorts.templates.Sort;

/*

Coded for ArrayV by Harumi
extending code by thatsOven and aphitorite

+---------------------------+
| Sorting Algorithm Scarlet |
+---------------------------+

 */

/**
 * @author Harumi
 * @author thatsOven
 * @author aphitorite
 *
 */
public class StableShuffleAlternative extends Sort {

    public StableShuffleAlternative(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Stable Shuffle (Alternative)");
        this.setRunAllSortsName("Stable Shuffle (Alternative)");
        this.setRunSortName("Stable Shuffle (Alternative)");
        this.setCategory("Tests");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    Random rng = new Random();
    static final int WLEN = 3;
    
    public int randInt(int a, int b) {
        return rng.nextInt(b - a) + a;
    }
    
    boolean getBit(int[] bits, int idx) {
        return ((bits[idx >> WLEN]) >> (idx & ((1 << WLEN) - 1)) & 1) == 1;
    }
    
    void setBit(int[] bits, int idx, boolean r) {
        if (r)
            Writes.write(bits, idx >> WLEN, bits[idx >> WLEN] | (1 << (idx & ((1 << WLEN) - 1))), 0, false, true);
        else
            Writes.write(bits, idx >> WLEN, bits[idx >> WLEN] & ~(1 << (idx & ((1 << WLEN) - 1))), 0, false, true);
    }
    
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
    
    int partition(int[] array, int[] aux, int a, int b, int piv, boolean eqLower) {
        Highlights.clearMark(2);
        int j = 0;
        for (int i = a; i < b; i++) {
            int cmp = Reads.compareIndexValue(array, i, piv, 0.25, true);
            if (cmp < 0 || (eqLower && cmp == 0))
                 Writes.write(array, a++, array[i], 0.25, true, false);
            else Writes.write(aux, j++, array[i], 0, true, true);
        }

        Writes.arraycopy(aux, 0, array, a, j, 0.5, true, false);
        return a;
    }
    
    void interweave(int[] array, int[] aux, int[] bits, int a, int m, int b) {
        int n = b - a;
        for (int i = 0; i < n; ++i)
            setBit(bits, i, false);

        for (int i = m - a; i < n; ++i) {
            int j = randInt(0, i+1);
            setBit(bits, getBit(bits, j) ? i : j, true);
        }
        int s = a;
        for (int i = 0; i < n; i++) {
            if (getBit(bits, i))
                 Writes.write(aux, i, array[m++], 0.5, true, true);
            else Writes.write(aux, i, array[a++], 0.5, true, true);
        }
        Writes.arraycopy(aux, 0, array, s, n, 0.5, true, false);
    }
    
    void shuffleEasy(int[] array, int a, int b) {
        for(int j = a + 1; j < b; j++)
            for(int i = j, rIdx = a + rng.nextInt(j - a + 1); i > rIdx; i--)
                if(Reads.compareIndices(array, i-1, i, 0.5, true) != 0)
                    Writes.swap(array, i-1, i, 0.5, false, false);
        Highlights.clearMark(2);
    }
    
    void shuffleRec(int[] array, int[] buf, int[] bits, int a, int b, boolean badPartition) {
        if (b - a <= 16) {
            shuffleEasy(array, a, b);
            return;
        }
        int p;
        if (badPartition) {
            p = medOfMed(array, a, b);
            badPartition = false;
        } else p = medP3(array, a, b, 2);
        int m = this.partition(array, buf, a, b, array[p], false);
        int l = m - a,
            r = b - m;
        if (m == a) {
            m = this.partition(array, buf, a, b, array[p], true);
            l = m - a; r = b - m;
            badPartition = l < r / 8;
        } else {
            badPartition = l < r / 8 || r < l / 8;
            this.shuffleRec(array, buf, bits, a, m, badPartition);
        }        
        this.shuffleRec(array, buf, bits, m, b, badPartition);
        this.interweave(array, buf, bits, a, m, b);
    }
    
    public void shuffle(int[] array, int a, int b) {
        int n = b - a;
        int[] aux  = Writes.createExternalArray(n);
        int[] bits = Writes.createExternalArray(((n-1) >> WLEN) + 1);

        this.shuffleRec(array, aux, bits, a, b, false);

        Writes.deleteExternalArrays(aux, bits);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        shuffle(array, 0, sortLength);
        arrayVisualizer.setExtraHeading(" / Testing Output...");
        EctaSort e = new EctaSort(arrayVisualizer);
        e.runSort(array, sortLength, bucketCount);
        arrayVisualizer.setExtraHeading("");
    }

}
