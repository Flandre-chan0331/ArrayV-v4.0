package sorts.exchange;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/**
 * @author Potassium
 *
 */
final public class TypeNCashewSort extends Sort {
    public TypeNCashewSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Type-N Cashew");
        this.setRunAllSortsName("Type-N Cashew Sort");
        this.setRunSortName("Type-N Cashew Sort");
        this.setCategory("Exchange Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        setQuestion("Enter the repeat count:", 2);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) throws Exception {
        for (int start = 0, end = length - 1; start < end;) {
            int consecSorted = 1;
            for (int k = 0; k < bucketCount; k++) {
                for (int i = start; i < end; i++) {
                    if (Reads.compareIndices(array, i, i + 1, 0.025, true) > 0) {
                        Writes.swap(array, i, i + 1, 0.075, true, false);
                        consecSorted = 1;
                    } else consecSorted++;
                }
                end -= consecSorted;
            }
            for (int l = 0; l < bucketCount; l++) {
                for (int i = end; i > start; i--) {
                    if (Reads.compareIndices(array, i, i - 1, 0.025, true) < 0) {
                        Writes.swap(array, i, i - 1, 0.075, true, false);
                        consecSorted = 1;
                    } else consecSorted++;
                }
                start += consecSorted;
            }
        }
    }
}