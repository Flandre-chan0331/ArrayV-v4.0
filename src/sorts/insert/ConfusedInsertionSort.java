package sorts.insert;

import main.ArrayVisualizer;
import sorts.templates.Sort;


final public class ConfusedInsertionSort extends Sort {
    public ConfusedInsertionSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Confused Insertion");
        this.setRunAllSortsName("Confused Insertion Sort");
        this.setRunSortName("Confused Insertion Sort");
        this.setCategory("Insertion Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private boolean insertPartial(int[] array, int start, int end, int lim) {
    	boolean c = true, k=false;
    	int l=1;
    	for(int i=start+1; i<end; i++) {
    		if(--l > lim) {
    			if((i & l) != i && (i & l) != l)
    				i^=l;
    			if(i>=end)
    				break;
    			k=!k;
    		}
    		l=1;
    		int t=array[i], j=i-1;
    		while(j>=start && Reads.compareValues(array[j],t) == (k ? -1 : 1)) {
    			Writes.write(array, j+1, array[j], 1, true, false);
    			j--;
    			++l;
    		}
    		c = c && j == i - 1;
			Writes.write(array, j+1, t, 1, true, false);
    	}
    	return c;
    }
    
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	while(!insertPartial(array, 0, currentLength, (int) Math.cbrt(currentLength)));
    }
}