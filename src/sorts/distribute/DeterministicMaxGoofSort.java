package sorts.distribute;

import java.util.BitSet;

import main.ArrayVisualizer;
import sorts.templates.BogoSorting;

/*

/------------------/
|   SORTS GALORE   |
|------------------|
|  courtesy of     |
|  meme man        |
|  (aka gooflang)  |
/------------------/

i'm 100% sure someone can make it so much worse

 */
public final class DeterministicMaxGoofSort extends BogoSorting {

    public DeterministicMaxGoofSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Deterministic Max Goof");
        this.setRunAllSortsName("Deterministic Max Goof Sort");
        this.setRunSortName("Deterministic Max Goofsort");
        this.setCategory("Impractical Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(8);
        this.setBogoSort(false);
    }
    
    boolean isPermutation(int[] a, int[] b, int len) {
        BitSet flags = new BitSet(len);
        boolean permutation = true;
        for (int i = 0; i < len; i++) {
            int select = 0;
            boolean any = false;
            for (int j = 0; j < len; j++) {
                if (flags.get(j)) continue;
                if (Reads.compareValues(a[j], b[i]) == 0) {
                    select = j;
                    any = true;
                    break;
                }
            }
            if (any) flags.set(select);
            else {
                permutation = false;
                break;
            }
        }
        return permutation;
    }
    
    public void dMaxGoof(int[] array, int[] aux, int a, int b) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = a; i < b; i++) {
            if (array[i] < min) min = array[i];
            if (array[i] > max) max = array[i];
        }
        Writes.arraycopy(array, a, aux, 0, b - a, 0, true, true);
        for (int i = a; i < b; i++) Writes.write(array, i, max, delay, true, false);
        while (!isPermutation(array, aux, b - a)) {
            Writes.write(array, a, array[a]-1, delay, true, false);
            for (int i = a; i < b; i++) {
                if (array[i] == min-1) {
                    Writes.write(array, i, max, delay, true, false);
                    if (i < b - 1) Writes.write(array, i+1, array[i+1]-1, delay, true, false);
                }
            }
        }
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
        int[] aux = Writes.createExternalArray(currentLength);
        while(!isArraySorted(array, currentLength)) {
            dMaxGoof(array, aux, 0, currentLength);
        }
        Writes.deleteExternalArray(aux);
    }

}
