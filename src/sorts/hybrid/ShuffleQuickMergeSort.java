package sorts.hybrid;

import java.util.Random;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/*

Coded for ArrayV by Kiriko-chan
in collaboration with aphitorite

-----------------------------
- Sorting Algorithm Scarlet -
-----------------------------

 */

/**
 * @author Kiriko-chan
 * @author aphitorite
 *
 */
public final class ShuffleQuickMergeSort extends Sort {

    public ShuffleQuickMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Shuffle Quick Merge");
        this.setRunAllSortsName("Shuffle Quick Merge Sort");
        this.setRunSortName("Shuffle Quick Mergesort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    void medianOfThree(int[] array, int a, int b) {
        int m = a + (b - 1 - a) / 2;

        if (Reads.compareIndices(array, a, m, 1, true) == 1)
            Writes.swap(array, a, m, 1, true, false);

        if (Reads.compareIndices(array, m, b - 1, 1, true) == 1) {
            Writes.swap(array, m, b - 1, 1, true, false);

            if (Reads.compareIndices(array, a, m, 1, true) == 1)
                return;
        }

        Writes.swap(array, a, m, 1, true, false);
    }

    public void shuffle(int[] array, int a, int b) {
        Random rng = new Random();
        for (int i = a; i < b; i++) {
            Writes.swap(array, i, i + rng.nextInt(b - i), 0.75, true, false);
        }
    }

    public int partition(int[] array, int a, int b, int p) {
        int i = a, j = b;
        Highlights.markArray(3, p);

        while (true) {
            do {
                i++;
                Highlights.markArray(1, i);
                Delays.sleep(0.5);
            } while (i < j && Reads.compareIndices(array, i, p, 0, false) < 0);

            do {
                j--;
                Highlights.markArray(2, j);
                Delays.sleep(0.5);
            } while (j >= i && Reads.compareIndices(array, j, p, 0, false) > 0);

            if (i < j)
                Writes.swap(array, i, j, 1, true, false);
            else {
                Writes.swap(array, p, j, 1, true, false);
                Highlights.clearMark(3);
                return j;
            }
        }
    }

    int leftBinSearch(int[] array, int a, int b, int val) {
        while (a < b) {
            int m = a + (b - a) / 2;

            if (Reads.compareValues(val, array[m]) <= 0)
                b = m;
            else
                a = m + 1;
        }

        return a;
    }

    int rightBinSearch(int[] array, int a, int b, int val) {
        while (a < b) {
            int m = a + (b - a) / 2;

            if (Reads.compareValues(val, array[m]) < 0)
                b = m;
            else
                a = m + 1;
        }

        return a;
    }

    int leftBoundSearch(int[] array, int a, int b, int val) {
        int i = 1;
        while (a - 1 + i < b && Reads.compareValues(val, array[a - 1 + i]) >= 0)
            i *= 2;

        return this.rightBinSearch(array, a + i / 2, Math.min(b, a - 1 + i), val);
    }

    int rightBoundSearch(int[] array, int a, int b, int val) {
        int i = 1;
        while (b - i >= a && Reads.compareValues(val, array[b - i]) <= 0)
            i *= 2;

        return this.leftBinSearch(array, Math.max(a, b - i + 1), b - i / 2, val);
    }

    protected void multiSwap(int[] array, int a, int b, int len) {
        for (int i = 0; i < len; i++)
            Writes.swap(array, a + i, b + i, 1, true, false);
    }

    protected void mergeFW(int[] array, int a, int m, int b, int p) {
        int pLen = m - a;
        multiSwap(array, a, p, pLen);
        int i = 0, j = m, k = a;

        while (i < pLen && j < b) {
            if (Reads.compareValues(array[p + i], array[j]) <= 0)
                Writes.swap(array, k++, p + (i++), 1, true, false);
            else
                Writes.swap(array, k++, j++, 1, true, false);
        }
        while (i < pLen)
            Writes.swap(array, k++, p + (i++), 1, true, false);
    }

    protected void mergeBW(int[] array, int a, int m, int b, int p) {
        int pLen = b - m;
        this.multiSwap(array, m, p, pLen);

        int i = pLen - 1, j = m - 1, k = b - 1;

        while (i >= 0 && j >= a) {
            if (Reads.compareValues(array[p + i], array[j]) >= 0)
                Writes.swap(array, k--, p + (i--), 1, true, false);
            else
                Writes.swap(array, k--, j--, 1, true, false);
        }
        while (i >= 0)
            Writes.swap(array, k--, p + (i--), 1, true, false);
    }

    public void smartMerge(int[] array, int a, int m, int b, int p) {
        if (Reads.compareIndices(array, m - 1, m, 0.5, true) <= 0)
            return;
        a = this.leftBoundSearch(array, a, m, array[m]);
        b = this.rightBoundSearch(array, m, b, array[m - 1]);
        if (b - m < m - a)
            mergeBW(array, a, m, b, p);
        else
            mergeFW(array, a, m, b, p);
    }

    public static int getMinLevel(int n) {
        while (n >= 32)
            n = (n + 1) / 2;
        return n;
    }

    protected void insertTo(int[] array, int a, int b) {
        Highlights.clearMark(2);
        int temp = array[a];
        boolean change = false;
        while (a > b) {
            Writes.write(array, a, array[--a], 0.25, true, false);
            change = true;
        }
        if (change)
            Writes.write(array, b, temp, 0.25, true, false);
    }
    
    protected void insertion(int[] array, int a, int b) {
        int i = a + 1;
        if(Reads.compareIndices(array, i-1, i++, 1, true) > 0) {
            while(i < b && Reads.compareIndices(array, i-1, i, 0.5, true) > 0) i++;
            Writes.reversal(array, a, i-1, 1, true, false);
        }
        else while(i < b && Reads.compareIndices(array, i-1, i, 0.5, true) <= 0) i++;
        Highlights.clearMark(2);
        for(; i < b; i++) {
            insertTo(array, i, rightBinSearch(array, a, i, array[i]));
        }
    }
    
    void buildRuns(int[] array, int a, int b, int mRun) {
        int i = a+1, j = a;
        
        while(i < b) {
            if(Reads.compareIndices(array, i-1, i++, 1, true) == 1) {
                while(i < b && Reads.compareIndices(array, i-1, i, 1, true) == 1) i++;
                Writes.reversal(array, j, i-1, 1, true, false);
            }
            else while(i < b && Reads.compareIndices(array, i-1, i, 1, true) <= 0) i++;
            
            if(i < b) j = i - (i-j-1)%mRun - 1;
            
            while(i-j < mRun && i < b) {
                this.insertTo(array, i, this.rightBinSearch(array, j, i, array[i]));
                i++;
            }
            j = i++;
        }
    }
    
    public void mergeSort(int[] array, int a, int b, int p) {
        int length = b-a;
        if(length < 2) return;
        
        int i, j = getMinLevel(length);
        
        buildRuns(array, a, b, j);
        
        while(j < length) {
            for(i = a; i+2*j <= b; i+=2*j)
                this.smartMerge(array, i, i+j, i+2*j, p);
            if(i + j < b)
                this.smartMerge(array, i, i+j, b, p);

            j *= 2;
        }
    }
    
    public void sort(int[] array, int a, int b) {
        int start = a, end = b;
        boolean badPartition = false, shuffle = false;
        while(end - start > 32) {
            if(badPartition) {
                shuffle(array, start, end);
                shuffle = true;
            }
            medianOfThree(array, start, end);
            int p = partition(array, start, end, start);
            int l = p - start, r = end - p - 1;
            badPartition = !shuffle && (l < (end - start) / 16 || r < (end - start) / 16);
            if(l <= r) {
                this.mergeSort(array, start, p, p+1);
                start = p+1;
            }
            else {
                this.mergeSort(array, p+1, end, start);
                end = p;
            }
        }
        insertion(array, start, end);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        sort(array, 0, sortLength);

    }

}
