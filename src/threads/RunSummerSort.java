package threads;

import java.lang.reflect.Constructor;

import main.ArrayVisualizer;
import panes.JErrorPane;
import sorts.hybrid.OptimizedRaikoSort;
import sorts.templates.Sort;
import utils.Distributions;
import utils.Shuffles;
import utils.StopSort;

/*
 * 
MIT License

Copyright (c) 2021 Josiah (Gaming32) Glosson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

final public class RunSummerSort extends MultipleSortThread {
    final Class<? extends Sort> SORT_CLASS = OptimizedRaikoSort.class;
    final int                  SORT_LENGTH = 4096;
    final double                SORT_SPEED = 4;
    final int                 BUCKET_COUNT = 0;

    public RunSummerSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.sortCount = 50;
        this.categoryCount = this.sortCount;
    }

    protected synchronized void runIndividualSort(Sort sort, int bucketCount, int[] array, int defaultLength, double defaultSpeed, boolean slowSort, String shuffleName) throws Exception {
        Delays.setSleepRatio(2.5);
        
        int sortLength;
        if(slowSort) {
            sortLength = this.calculateLengthSlow(defaultLength, sort.getUnreasonableLimit());
        }
        else {
            sortLength = this.calculateLength(defaultLength);
        }
        if(sortLength != arrayVisualizer.getCurrentLength()) {
            arrayFrame.setLengthSlider(sortLength);
        }
        
        arrayManager.refreshArray(array, arrayVisualizer.getCurrentLength(), this.arrayVisualizer);
        
        arrayVisualizer.setHeading(shuffleName + " (" + this.sortNumber + " / " + this.sortCount + ")");
        
        double sortSpeed = this.calculateSpeed(defaultSpeed, arrayVisualizer.getCurrentLength());
        Delays.setSleepRatio(sortSpeed);
        
        Timer.enableRealTimer();
        
        // arrayVisualizer.toggleVisualUpdates(true);
        try {
            sort.runSort(array, arrayVisualizer.getCurrentLength(), bucketCount);
        }
        catch (StopSort e) { }
        catch (Exception e) {
            JErrorPane.invokeErrorMessage(e);
        }
        // arrayVisualizer.toggleVisualUpdates(false);
        
        arrayVisualizer.endSort();
        Thread.sleep(1000);
        
        this.sortNumber++;
    }

    protected synchronized Sort createSortInstance() {
        Constructor<? extends Sort> constructor;
        try {
            constructor = SORT_CLASS.getDeclaredConstructor(ArrayVisualizer.class);
        } catch (Exception e) {
            JErrorPane.invokeErrorMessage(e);
            throw new RuntimeException("Class " + SORT_CLASS.getName() + " is not an accessible sort class.");
        }
        try {
            return constructor.newInstance(arrayVisualizer);
        } catch (Exception e) {
            JErrorPane.invokeErrorMessage(e);
            throw new RuntimeException("Unable to initialize class " + SORT_CLASS.getName() + ".");
        }
    }

    protected synchronized void runSort(int[] array, String shuffleName) throws Exception {
        Sort sort = createSortInstance();
        RunSummerSort.this.runIndividualSort(sort, BUCKET_COUNT, array, SORT_LENGTH, SORT_SPEED, false, shuffleName);
    }

    @Override
    protected synchronized void executeSortList(int[] array) throws Exception {
        arrayVisualizer.getArrayManager().setDistribution(Distributions.LINEAR); // 1
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.RANDOM);
        RunSummerSort.this.runSort(array, "Random");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.REVERSE); // 2
        RunSummerSort.this.runSort(array, "Reversed");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.ALMOST); // 3
        RunSummerSort.this.runSort(array, "Almost Sorted");

        arrayVisualizer.getArrayFrame().setUniqueSlider(arrayVisualizer.getCurrentLength() / 64); // 4
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.RANDOM);
        RunSummerSort.this.runSort(array, "Many Similar");

        arrayVisualizer.getArrayFrame().setUniqueSlider(arrayVisualizer.getCurrentLength()); // 5
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUFFLED_TAIL);
        RunSummerSort.this.runSort(array, "Scrambled Tail");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUFFLED_HEAD); // 6
        RunSummerSort.this.runSort(array, "Scrambled Head");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.FINAL_MERGE); // 7
        RunSummerSort.this.runSort(array, "Final Merge");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SAWTOOTH); // 8
        RunSummerSort.this.runSort(array, "Sawtooth Input");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.HALF_ROTATION); // 9
        RunSummerSort.this.runSort(array, "Final Merge of Reversed Array");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.FINAL_MERGE) // 10
                                         .addSingle(Shuffles.REVERSE)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Reversed Final Merge");

        //arrayVisualizer.getArrayFrame().setUniqueSlider(arrayVisualizer.getCurrentLength() / 2);
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.ORGAN); // 11
        RunSummerSort.this.runSort(array, "Pipe Organ");

        //arrayVisualizer.getArrayFrame().setUniqueSlider(arrayVisualizer.getCurrentLength());
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.FINAL_RADIX); // 12
        RunSummerSort.this.runSort(array, "Final Radix Pass");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.PAIRWISE); // 13
        RunSummerSort.this.runSort(array, "Final Pairwise Pass");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.BST_TRAVERSAL); // 14
        RunSummerSort.this.runSort(array, "Binary Search Tree");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.HEAPIFIED); // 15
        RunSummerSort.this.runSort(array, "Heap");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.REVERSE) // 16
                                         .addSingle(Shuffles.SMOOTH)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Smooth Heap");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.REVERSE) // 17
                                         .addSingle(Shuffles.POPLAR)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Poplar Heap");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.PARTIAL_REVERSE); // 18
        RunSummerSort.this.runSort(array, "Half-Reversed Input");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.DOUBLE_LAYERED); // 19
        RunSummerSort.this.runSort(array, "Evens Reversed, Odds In-Order");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUFFLED_ODDS); // 20
        RunSummerSort.this.runSort(array, "Evens In-Order, Scrambled Odds");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.INTERLACED); // 21
        RunSummerSort.this.runSort(array, "Evens Ascending, Odds Descending");

        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.BELL_CURVE); // 22
        RunSummerSort.this.runSort(array, "Bell Curve");

        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.PERLIN_NOISE_CURVE); // 23
        RunSummerSort.this.runSort(array, "Perlin Noise Curve");

        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.PERLIN_NOISE); // 24
        RunSummerSort.this.runSort(array, "Perlin Noise");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.TRIANGULAR); // 25
        RunSummerSort.this.runSort(array, "Triangular Input");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUFFLED_HALF); // 26
        RunSummerSort.this.runSort(array, "Scrambled Second Half");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUFFLED_HALF) // 27
                                         .addSingle(Shuffles.HALF_ROTATION)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Scrambled First Half");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUFFLED_TAIL) // 28
                                         .addSingle(Shuffles.SHUFFLED_HEAD)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Both Sides Scrambled");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.NOISY); // 29
        RunSummerSort.this.runSort(array, "Noisy Input");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.PARTITIONED); // 30
        RunSummerSort.this.runSort(array, "Partitioned");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SAWTOOTH) // 31
                                         .addSingle(Shuffles.REVERSE)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Reversed Sawtooth");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.FINAL_BITONIC); // 32
        RunSummerSort.this.runSort(array, "Final Bitonic Pass");
        // Diamond
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SAWTOOTH) // 33
                                         .addSingle(Shuffles.REVERSE)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Diamond");

        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.INV_BST); // 34
        RunSummerSort.this.runSort(array, "Inverted Binary Search Tree");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.REC_RADIX); // 35
        RunSummerSort.this.runSort(array, "Recursive Final Radix");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.REC_REV); // 36
        RunSummerSort.this.runSort(array, "Recursive Reversal");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.TRI_HEAP); // 37
        RunSummerSort.this.runSort(array, "Triangular Heap");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.BIT_REVERSE); // 38
        RunSummerSort.this.runSort(array, "Bit Reversed");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.BLOCK_RANDOMLY); // 39
        RunSummerSort.this.runSort(array, "Block Shuffled");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.BLOCK_REVERSE); // 40
        RunSummerSort.this.runSort(array, "Block Reversed");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.QSORT_BAD); // 41
        RunSummerSort.this.runSort(array, "Quicksort Killer");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.PDQ_BAD); // 42
        RunSummerSort.this.runSort(array, "PDQ Killer");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.GRAIL_BAD); // 43
        RunSummerSort.this.runSort(array, "Grailsort Killer");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SHUF_MERGE_BAD); // 44
        RunSummerSort.this.runSort(array, "Shuffle Merge Killer");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.CIRCLE); // 45
        RunSummerSort.this.runSort(array, "Circle Pass");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.SORTED); // 46
        RunSummerSort.this.runSort(array, "Already Sorted");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Shuffles.NAIVE); // 47
        RunSummerSort.this.runSort(array, "Naive Random");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.RANDOM); // 48
        RunSummerSort.this.runSort(array, "Natural Random");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.MODULO); // 49
        RunSummerSort.this.runSort(array, "Modulo Function");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.CUBIC) // 50
                                         .addSingle(Shuffles.RANDOM)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Cubic Input");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.QUINTIC) // 51
                                         .addSingle(Shuffles.RANDOM)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Quintic Input");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.SINE); // 52
        RunSummerSort.this.runSort(array, "Sine Wawe");
        
        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.COSINE); // 53
        RunSummerSort.this.runSort(array, "Cosine Wawe");

        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.SIMILAR) // 54
                                         .addSingle(Shuffles.RANDOM)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Few Unique");

        arrayVisualizer.getArrayManager().setShuffleSingle(Distributions.EQUAL) // 54
                                         .addSingle(Shuffles.ALREADY)
                                         .setSleepRatio(2);
        RunSummerSort.this.runSort(array, "Few Unique");

    } // 54 shuffles
    
    @Override
    protected synchronized void runThread(int[] array, int current, int total, boolean runAllActive) throws Exception {
        if(arrayVisualizer.isActive())
            return;

        Sounds.toggleSound(true);
        arrayVisualizer.setSortingThread(new Thread("RSS") {
            @Override
            public void run() {
                try{
                    if(runAllActive) {
                        RunSummerSort.this.sortNumber = current;
                        RunSummerSort.this.sortCount = total;
                    }
                    else {
                        RunSummerSort.this.sortNumber = 1;
                    }

                    arrayManager.toggleMutableLength(false);

                    Sort tempSort = createSortInstance();

                    arrayVisualizer.setCategory(tempSort.getRunAllSortsName());

                    RunSummerSort.this.executeSortList(array);
                    
                    if(!runAllActive) {
                        arrayVisualizer.setCategory("Run " + tempSort.getRunAllSortsName());
                        arrayVisualizer.setHeading("Done");
                    }
                    
                    arrayManager.toggleMutableLength(true);
                }
                catch (Exception e) {
                    JErrorPane.invokeErrorMessage(e);
                }
                Sounds.toggleSound(false);
                arrayVisualizer.setSortingThread(null);
            }
        });

        arrayVisualizer.runSortingThread();
    }
}