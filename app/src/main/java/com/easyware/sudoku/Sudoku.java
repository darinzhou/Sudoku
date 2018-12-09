package com.easyware.sudoku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

public class Sudoku implements Parcelable, Serializable {
	
	public enum Level {
    	Easy(1), Medium(2), Hard(3), Tough(4);
    	
		private int value;
		private int blankNum;
		
		private Level(int v) {
			switch (v) {
				case 1:
					value = v;
					blankNum = 30;
					break;
				case 2:
					value = v;
					blankNum = 40;
					break;
				case 3:
					value = v;
					blankNum = 50;
					break;
				case 4:
					value = v;
					blankNum = 60;
					break;
				default:
					value = v;
					blankNum = 30;
					break;
			}
		}
		
		public int getBlankNum() {
			return blankNum;
		}
		
		public int getValue() {
			return value;
		}
	
		public static Level getEnum(int v) {
			for (Level l : values()) {
				if (l.getValue() == v)
					return l;
			}
			
			return Easy;
		}
	};

    public final static class CheckResult
    {
        private int[] pos;
        private int val;
        public CheckResult(int[] pos, int val)
        {
            this.pos = pos;
            this.val = val;
        }
        public int[] getPos()
        {
            return pos;
        }
        public int getVal()
        {
            return val;
        }
        public boolean succeed()
        {
            return (val == 0) || (pos == null);
        }
    }

    public final static int GRID_SIZE = 3;
    public final static int BOARD_SIZE = GRID_SIZE * GRID_SIZE;
    public final static int MID1 = GRID_SIZE;
    public final static int MID2 = GRID_SIZE * 2;
    public final static int CELL_NUM = BOARD_SIZE*BOARD_SIZE;

    // Sudoku templates
    private final static int[][][] mTemplates = {
										        	{
										                {1, 2, 3, 4, 5, 6, 7, 8, 9},
										                {4, 5, 6, 7, 8, 9, 1, 2, 3},
										                {7, 8, 9, 1, 2, 3, 4, 5, 6}, 
										                {2, 3, 4, 5, 6, 7, 8, 9, 1},
										                {5, 6, 7, 8, 9, 1, 2, 3, 4},
										                {8, 9, 1, 2, 3, 4, 5, 6, 7},
										                {3, 4, 5, 6, 7, 8, 9, 1, 2},
										                {6, 7, 8, 9, 1, 2, 3, 4, 5},
										                {9, 1, 2, 3, 4, 5, 6, 7, 8}
										            },
										            {
										                {7, 2, 6, 4, 9, 3, 8, 1, 5},
										                {3, 1, 5, 7, 2, 8, 9, 4, 6},
										                {4, 8, 9, 6, 5, 1, 2, 3, 7}, 
										                {8, 5, 2, 1, 4, 7, 6, 9, 3},
										                {6, 7, 3, 9, 8, 5, 1, 2, 4},
										                {9, 4, 1, 3, 6, 2, 7, 5, 8},
										                {1, 9, 4, 8, 3, 6, 5, 7, 2},
										                {5, 6, 7, 2, 1, 4, 3, 8, 9},
										                {2, 3, 8, 5, 7, 9, 4, 6, 1}
										            },
										            {
										                {5, 1, 2, 6, 8, 7, 9, 3, 4},
										                {6, 7, 8, 3, 4, 9, 2, 1, 5},
										                {4, 3, 9, 5, 1, 2, 6, 8, 7},
										                {9, 2, 3, 4, 7, 6, 8, 5, 1},
										                {7, 8, 4, 1, 2, 5, 3, 9, 6},
										                {1, 5, 6, 8, 9, 3, 7, 4, 2},
										                {3, 6, 7, 9, 5, 1, 4, 2, 8},
										                {8, 9, 5, 2, 6, 4, 1, 7, 3},
										                {2, 4, 1, 7, 3, 8, 5, 6, 9}
										            },
										            {
										                {5, 9, 8, 6, 7, 4, 3, 1, 2},
										                {4, 6, 2, 5, 3, 1, 8, 7, 9},
										                {7, 3, 1, 2, 9, 8, 4, 6, 5},
										                {3, 1, 6, 8, 5, 9, 2, 4, 7},
										                {2, 7, 5, 1, 4, 3, 9, 8, 6},
										                {9, 8, 4, 7, 2, 6, 5, 3, 1},
										                {8, 2, 9, 3, 1, 7, 6, 5, 4},
										                {6, 4, 7, 9, 8, 5, 1, 2, 3},
										                {1, 5, 3, 4, 6, 2, 7, 9, 8}
										            },
										            {
										                {1, 9, 3, 5, 4, 6, 7, 8, 2},
										                {8, 2, 6, 1, 9, 7, 3, 4, 5},
										                {5, 4, 7, 3, 2, 8, 6, 1, 9},
										                {3, 6, 5, 2, 8, 9, 4, 7, 1},
										                {7, 1, 9, 6, 3, 4, 2, 5, 8},
										                {2, 8, 4, 7, 5, 1, 9, 3, 6},
										                {9, 7, 2, 8, 1, 3, 5, 6, 4},
										                {6, 5, 1, 4, 7, 2, 8, 9, 3},
										                {4, 3, 8, 9, 6, 5, 1, 2, 7}
										            },
										            {
										                {2, 4, 6, 1, 7, 3, 5, 9, 8},
										                {9, 5, 7, 4, 8, 6, 3, 1, 2},
										                {8, 3, 1, 9, 5, 2, 7, 6, 4},
										                {7, 2, 4, 8, 6, 5, 9, 3, 1},
										                {6, 8, 9, 3, 1, 7, 2, 4, 5},
										                {3, 1, 5, 2, 9, 4, 6, 8, 7},
										                {5, 9, 2, 6, 4, 8, 1, 7, 3},
										                {1, 7, 8, 5, 3, 9, 4, 2, 6},
										                {4, 6, 3, 7, 2, 1, 8, 5, 9}
										            }
										        };
        
    // Sudoku grid blocks
    private final static int[][] mGrids = {
			                                {0,  1,  2,  9,  10, 11, 18, 19, 20},
			                                {3,  4,  5,  12, 13, 14, 21, 22, 23},
			                                {6,  7,  8,  15, 16, 17, 24, 25, 26}, 
			                                {27, 28, 29, 36, 37, 38, 45, 46, 47},
			                                {30, 31, 32, 39, 40, 41, 48, 49, 50},
			                                {33, 34, 35, 42, 43, 44, 51, 52, 53},
			                                {54, 55, 56, 63, 64, 65, 72, 73, 74},
			                                {57, 58, 59, 66, 67, 68, 75, 76, 77},
			                                {60, 61, 62, 69, 70, 71, 78, 79, 80}
			                            };

    private Random mRnd = new Random();
    private int[][] mBoard;
    private int[] mSolution;
    private int[] mPuzzle;
    private int[] mGame;
    private Level mLevel;
    private int mBlankNum;
    private boolean mIsSolved;
    
	private long mTimeUsed;
	private int mActiveCell;
	private boolean mIsTimerOn;
    
    public Sudoku(Level level)
    {
        this.mLevel = level;

        create();
        mBlankNum = mLevel.getBlankNum();
    }

	public Sudoku()
    {
    	this(Level.Medium);
    }

    // Constructor to use when re-constructing object from a parcel
    public Sudoku(Parcel in) {
    	readFromParcel(in);
    }
    
    public Level getLevel() {
    	return mLevel;
    }
    
    private void generateSolution()
    {
        mSolution = new int[CELL_NUM];
        for (int i=0; i<BOARD_SIZE; ++i)
            for (int j=0; j<BOARD_SIZE; ++j)
                mSolution[i*BOARD_SIZE+j] = mBoard[i][j];
    }

    private boolean shouldBeBlank(int rnd)
    {
        if (mLevel == Level.Easy)
        {
            if (rnd % 4 == 0)
                return true;
        }
        else if (mLevel == Level.Medium)
        {
            if (rnd % 3 == 0)
                return true;
        }
        else if (mLevel == Level.Hard)
        {
            if (rnd % 3 == 0)
                return true;
        }
        else if (mLevel == Level.Tough)
        {
            if (rnd % 2 == 0)
                return true;
        }
        return false;
    }

    private void generatePuzzle()
    {
        Random rnd = new Random();
        int blankCount = 0;

        mPuzzle = new int[CELL_NUM];
        mGame = new int[CELL_NUM];
        for (int i = 0; i < BOARD_SIZE; ++i)
        {
            for (int j = 0; j < BOARD_SIZE; ++j)
            {
                int index = i * BOARD_SIZE + j;

                if (blankCount < mLevel.getBlankNum())
                {
                    if (shouldBeBlank(rnd.nextInt(1000)) || 
                   		((mLevel != Level.Easy) && (index + 1) % 3 == 0 && index > 1 && mPuzzle[index - 1] != 0 && mPuzzle[index - 2] != 0) ||
                		((mLevel != Level.Easy) && (index/9 + 1) %3 == 0 && index >= 18 && mPuzzle[index - 9] != 0 && mPuzzle[index - 18] != 0))
                    {
                        blankCount++;
                        mPuzzle[index] = mGame[index] = 0;
                    }
                    else
                    {
                        mPuzzle[index] = mGame[index] = mBoard[i][j];
                    }
                }
                else
                    mPuzzle[index] = mGame[index] = mBoard[i][j];
            }
        }

        // not enough blanks yet, continue seting
        if (blankCount < mLevel.getBlankNum())
        {
            for (int i = CELL_NUM - 1; i >= 0; --i)
            {
                if (blankCount >= mLevel.getBlankNum())
                    break;
                if (mPuzzle[i]!=0 && shouldBeBlank(rnd.nextInt(10000)))
                {
                    blankCount++;
                    mPuzzle[i] = mGame[i] = 0;
                }
            }
        }
        
        // balance between grids
        
        int[] nCounts = new int[BOARD_SIZE];
        int[] newNumCells = new int[CELL_NUM];
        int[] newBlankCells = new int[CELL_NUM];
        
        int inn, inb;
        inn = inb = 0;
        int pivot = (CELL_NUM - mLevel.getBlankNum())/BOARD_SIZE+1;
        
        for (int i=0; i<BOARD_SIZE; ++i) {
        	for (int j=0; j<BOARD_SIZE; ++j) {
        		if (mPuzzle[mGrids[i][j]] != 0) {
        			nCounts[i]++;
        		}
        	}
        }
        
        for (int i=0; i<BOARD_SIZE; ++i) {
        	int delta = nCounts[i] - pivot;
        	if (delta > 0) {
        		for (int j=0; j<BOARD_SIZE; ++j) {
        			if (mPuzzle[mGrids[i][j]] != 0) {
	        			if (rnd.nextInt(2) == 0) {
	        				newBlankCells[inb++] = mGrids[i][j];
	        				delta--;
	        			}
        			}
        			if (delta == 0)
        				break;
        		}
        	}
        	else if (delta < 0) {
        		for (int j=0; j<BOARD_SIZE; ++j) {
        			int index = i*BOARD_SIZE+j;
        			if (mPuzzle[mGrids[i][j]] == 0) {
	        			if (rnd.nextInt(2) == 0 && 
 	        				!((index + 1) % 3 == 0 && index > 1 && mPuzzle[index - 1] != 0 && mPuzzle[index - 2] != 0) &&
 	                		!((index/9 + 1) %3 == 0 && index >= 18 && mPuzzle[index - 9] != 0 && mPuzzle[index - 18] != 0)) {
	        				newNumCells[inn++] = mGrids[i][j];
	        				delta++;
	        			}
        			}
        			if (delta == 0)
        				break;
        		}
        	}
        }
        
        int[] nbcs;
        int[] nncs;
        int mn;
        if (inn > inb) {
        	mn = inb;
        	nbcs = newBlankCells;
        	nncs = new int[inb];
        	for (int i=0; i<inb; ++i) {
        		int r = rnd.nextInt(inn);
        		nncs[i] = newNumCells[r];
        		for (int j=r;j<inn;++j)
        			newNumCells[j] = newNumCells[j+1];
        		inn--;
        	}
        }
        else if (inn < inb) {
        	mn = inn;
        	nncs = newNumCells;
        	nbcs = new int[inn];
        	for (int i=0; i<inn; ++i) {
        		int r = rnd.nextInt(inb);
        		nbcs[i] = newBlankCells[r];
        		for (int j=r;j<inb;++j)
        			newBlankCells[j] = newBlankCells[j+1];
        		inb--;
        	}
        }
        else {
        	mn = inn;
        	nbcs = newBlankCells;
        	nncs = newNumCells;
        }
        
        for (int i=0; i<mn; ++i) {
        	mPuzzle[nbcs[i]] = mGame[nbcs[i]] = 0;
        	int ii = nncs[i]/BOARD_SIZE;
        	int jj = nncs[i]%BOARD_SIZE;
        	mPuzzle[nncs[i]] = mGame[nncs[i]] = mBoard[ii][jj];
        }
    }

    private void switchTwoRows(int a, int b)
    {
        if (a == b)
            return;

        for (int i = 0; i < BOARD_SIZE; ++i)
        {
            int tmp = mBoard[a][i];
            mBoard[a][i] = mBoard[b][i];
            mBoard[b][i] = tmp;
        }
    }

    private void switchTwoGridRows(int a, int b)
    {
        if (a == b)
            return;

        for (int i = 0; i < BOARD_SIZE; ++i)
        {
            for (int j = 0; j < GRID_SIZE; ++j)
            {
                int aIndex = a*GRID_SIZE+j;
                int bIndex = b*GRID_SIZE+j;

                int tmp = mBoard[aIndex][i];
                mBoard[aIndex][i] = mBoard[bIndex][i];
                mBoard[bIndex][i] = tmp;
            }
        }
    }

    private void switchTwoCols(int a, int b)
    {
        if (a == b)
            return;

        for (int i = 0; i < BOARD_SIZE; ++i)
        {
            int tmp = mBoard[i][a];
            mBoard[i][a] = mBoard[i][b];
            mBoard[i][b] = tmp;
        }
    }

    private void switchTwoGridCols(int a, int b)
    {
        if (a == b)
            return;

        for (int i = 0; i < BOARD_SIZE; ++i)
        {
            for (int j = 0; j < GRID_SIZE; ++j)
            {
                int aIndex = a * GRID_SIZE + j;
                int bIndex = b * GRID_SIZE + j;

                int tmp = mBoard[i][aIndex];
                mBoard[i][aIndex] = mBoard[i][bIndex];
                mBoard[i][bIndex] = tmp;
            }
        }
    }

    private void rotateBoard(int mode)
    {
        if (mode % 2 == 0)
            return;

        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
            {
                int tmp = mBoard[i][j];
                mBoard[i][j] = mBoard[j][i];
                mBoard[j][i] = tmp;
            }

    }

    private void switchRows(int mode, int start)
    {
        int a, b;

        switch (mode)
        {
            case 1:
                a = start; b = start+1;
                switchTwoRows(a, b);
                break;
            case 2:
                a = start; b = start+2;
                switchTwoRows(a, b);
                break;
            case 3:
                a = start+1; b = start+2;
                switchTwoRows(a, b);
                break;
            case 4:
                a = start; b = start+1;
                switchTwoRows(a, b);
                a = start; b = start+2;
                switchTwoRows(a, b);
                break;
            case 5:
                a = start; b = start+1;
                switchTwoRows(a, b);
                a = start+1; b = start+2;
                switchTwoRows(a, b);
                break;
            default:
                break;
        }
    }

    private void switchCols(int mode, int start)
    {
        int a, b;

        switch (mode)
        {
            case 1:
                a = start; b = start + 1;
                switchTwoCols(a, b);
                break;
            case 2:
                a = start; b = start + 2;
                switchTwoCols(a, b);
                break;
            case 3:
                a = start + 1; b = start + 2;
                switchTwoCols(a, b);
                break;
            case 4:
                a = start; b = start + 1;
                switchTwoCols(a, b);
                a = start; b = start + 2;
                switchTwoCols(a, b);
                break;
            case 5:
                a = start; b = start + 1;
                switchTwoCols(a, b);
                a = start + 1; b = start + 2;
                switchTwoCols(a, b);
                break;
            default:
                break;
        }
    }

    private void switchGridRows(int mode)
    {
        int a, b;
        int start = 0;

        switch (mode)
        {
            case 1:
                a = start; b = start + 1;
                switchTwoGridRows(a, b);
                break;
            case 2:
                a = start; b = start + 2;
                switchTwoGridRows(a, b);
                break;
            case 3:
                a = start + 1; b = start + 2;
                switchTwoGridRows(a, b);
                break;
            case 4:
                a = start; b = start + 1;
                switchTwoGridRows(a, b);
                a = start; b = start + 2;
                switchTwoGridRows(a, b);
                break;
            case 5:
                a = start; b = start + 1;
                switchTwoGridRows(a, b);
                a = start + 1; b = start + 2;
                switchTwoGridRows(a, b);
                break;
            default:
                break;
        }
    }

    private void switchGridCols(int mode)
    {
        int a, b;
        int start = 0;

        switch (mode)
        {
            case 1:
                a = start; b = start + 1;
                switchTwoGridCols(a, b);
                break;
            case 2:
                a = start; b = start + 2;
                switchTwoGridCols(a, b);
                break;
            case 3:
                a = start + 1; b = start + 2;
                switchTwoGridCols(a, b);
                break;
            case 4:
                a = start; b = start + 1;
                switchTwoGridCols(a, b);
                a = start; b = start + 2;
                switchTwoGridCols(a, b);
                break;
            case 5:
                a = start; b = start + 1;
                switchTwoGridCols(a, b);
                a = start + 1; b = start + 2;
                switchTwoGridCols(a, b);
                break;
            default:
                break;
        }
    }

    private void selectTemplate(int mode)
    {
    	if (mode >= 0 && mode < mTemplates.length)
    		mBoard = mTemplates[mode];
    	else
    		mBoard = mTemplates[0];
    }

    public void create()
    {
        selectTemplate(mRnd.nextInt(mTemplates.length));
        
        rotateBoard(mRnd.nextInt());
        
        switchGridRows(mRnd.nextInt(5)+1);
        switchGridCols(mRnd.nextInt(5)+1);
        
        switchRows(mRnd.nextInt(5)+1, 0);
        switchRows(mRnd.nextInt(5)+1, 3);
        switchRows(mRnd.nextInt(5)+1, 6);
        
        switchCols(mRnd.nextInt(5)+1, 0);
        switchCols(mRnd.nextInt(5)+1, 3);
        switchCols(mRnd.nextInt(5)+1, 6);

        // recreate solution and puzzle after creating board
        generateSolution();
        generatePuzzle();
    }

    public int[] getPuzzle()
    {
        return mPuzzle;
    }

    public int[] getGame()
    {
        return mGame;
    }

    public int[] getSolution()
    {
        return mSolution;
    }

    public int getGameAt(int index)
    {
        return mGame[index];
    }
    
    public boolean setGameAt(int index, int value)
    {
    	// do not change solved game
    	if (mIsSolved)
    		return false;
    	
    	// invalid value
    	if (value < 0 || value > 9)
    		return false;
    	
    	// cannot change the pre-set numbers
    	if (mPuzzle[index] != 0)
    		return false;
    	
    	boolean isBlankBeforeSet = (mGame[index] == 0);
    	
    	// now set the value
        mGame[index] = value;
        
        // update blank num of this game
        if (value == 0 && !isBlankBeforeSet)
        	mBlankNum++;
        else if (value != 0 && isBlankBeforeSet)
        	mBlankNum--;
        
        return true;
    }
    
    public int getBlankNum() {
    	return mBlankNum;
    }
    
    public int getFilledCellNum() {
    	return mLevel.getBlankNum() - mBlankNum;
    }

    public int getPuzzleAt(int index)
    {
        return mPuzzle[index];
    }

    public int getSolutionAt(int index)
    {
        return mSolution[index];
    }

    public CheckResult checkValid()
    {
    	return checkValid(mGame);
    }
    
    public boolean fullyFilled()
    {
    	return fullyFilled(mGame);
    }
    
    public boolean isSolved()
    {
    	if (mIsSolved)
    		return true;
    	mIsSolved = isSolved(mGame);
    	return mIsSolved;
    }
    
    public boolean getIsSolved()
    {
    	return mIsSolved;
    }

    public void clearAll() {
    	for (int i=0; i<CELL_NUM; ++i) {
    		if (mPuzzle[i] == 0)
    			mGame[i] = 0;
    		mIsSolved = false;
    		mBlankNum = mLevel.getBlankNum();
    	}
    }
    
    public int[] getCandidates(int index) {
    	List<Integer> al = getCandidates(index, mGame);
    	if (!al.contains(mSolution[index]))
    		al.add(mSolution[index]);

    	int[] ca = new int[al.size()];
    	for (int i = 0; i< al.size(); ++i) {
    		ca[i] = al.get(i).intValue();
    	}
    
    	return ca;
    }
    
    public void setTimeUsed(long timeUsed) {
    	mTimeUsed = timeUsed;
    }
    public void setActiveCell(int activeCell) {
    	mActiveCell = activeCell;
    }
    public void setIsTimerOn(boolean isTimerOn) {
    	mIsTimerOn = isTimerOn;
    }
    
    public long getTimeUsed() {
    	return mTimeUsed;
    }
    public int getActiveCell() {
    	return mActiveCell;
    }
    public boolean getIsTimerOn() {
    	return mIsTimerOn;
    }

    // return 0 means no duplicates
    // otherwise, the returned value is the first duplicate found
    // 0 is in array is ignored
    public static int checkDuplicates(int[] array)
    {
        boolean[] bitmap = new boolean[BOARD_SIZE + 1];
        for (int i : array)
        {
            // ignore 0 in array
            if (i == 0)
                continue;

            if (!bitmap[i])
                bitmap[i] = true;
            else
                return i;
        }
        return 0;
    }

    public static boolean isSolved(int[] array)
    {
        if (!fullyFilled(array))
            return false;
        return checkValid(array).succeed();
    }

    public static boolean fullyFilled(int[] array)
    {
        // all filled?
        for (int i : array)
        {
            if (i == 0)
                return false;
        }
        return true;
    }

    public static CheckResult checkValid(int[] array)
    {
        int v;
        int[] row = new int[BOARD_SIZE];
        int[] col = new int[BOARD_SIZE];
        int[] rowPos = new int[BOARD_SIZE];
        int[] colPos = new int[BOARD_SIZE];

        int[][] grids = new int[BOARD_SIZE][];
        int[] idxGrids = {0,0,0,0,0,0,0,0,0};
        for (int i=0; i<BOARD_SIZE; ++i) {
        	grids[i] = new int[BOARD_SIZE];
        	idxGrids[i] = 0;
        }
        
        for (int i = 0; i < BOARD_SIZE; ++i)
        {
            for (int j = 0; j < BOARD_SIZE; ++j)
            {
                int index = i * BOARD_SIZE + j;

                row[j] = array[index];
                rowPos[j] = index;
                col[j] = array[i + j * BOARD_SIZE];
                colPos[j] = i + j * BOARD_SIZE;

                // small grids
                int g = 0;
                if (j < MID1 && i < MID1)
                {
                    g = 0;
                }
                else if (j >= MID1 && j < MID2 && i < MID1)
                {
                    g = 1;
                }
                else if (j >= MID2 && i < MID1)
                {
                    g = 2;
                }
                // row2
                else if (j < MID1 && i >= MID1 && i < MID2)
                {
                    g = 3;
                }
                else if (j >= MID1 && j < MID2 && i >= MID1 && i < MID2)
                {
                    g = 4;
                }
                else if (j >= MID2 && i >= MID1 && i < MID2)
                {
                    g = 5;
                }
                // row3
                else if (j < MID1 && i >= MID2)
                {
                    g = 6;
                }
                else if (j >= MID1 && j < MID2 && i >= MID2)
                {
                    g = 7;
                }
                else if (j >= MID2 && i >= MID2)
                {
                    g = 8;
                }

                grids[g][idxGrids[g]] = array[index];
                idxGrids[g] += 1;
            }

            // each row
            v = checkDuplicates(row);
            if (v != 0)
                return new CheckResult(rowPos, v);

            // each column
            v = checkDuplicates(col);
            if (v != 0)
                return new CheckResult(colPos, v);
        }

        // each small block
        for (int i=0; i<BOARD_SIZE; ++i) {
            v = checkDuplicates(grids[i]);
            if (v != 0)
                return new CheckResult(mGrids[i], v);
        }

        // to here, is completed successfully
        return new CheckResult(null, 0);
    }
    
    public static List<Integer> getCandidates(int index, int[] array)
    {
        int[] row = new int[BOARD_SIZE];
        int[] col = new int[BOARD_SIZE];
        int[] grid = new int[BOARD_SIZE];
        List<Integer> al = new ArrayList<Integer>();
        
        int i = index / BOARD_SIZE;
        int j = index % BOARD_SIZE;
        int g = 0;
        
        if (j < MID1 && i < MID1)
        {
            g = 0;
        }
        else if (j >= MID1 && j < MID2 && i < MID1)
        {
            g = 1;
        }
        else if (j >= MID2 && i < MID1)
        {
            g = 2;
        }
        // row2
        else if (j < MID1 && i >= MID1 && i < MID2)
        {
            g = 3;
        }
        else if (j >= MID1 && j < MID2 && i >= MID1 && i < MID2)
        {
            g = 4;
        }
        else if (j >= MID2 && i >= MID1 && i < MID2)
        {
            g = 5;
        }
        // row3
        else if (j < MID1 && i >= MID2)
        {
            g = 6;
        }
        else if (j >= MID1 && j < MID2 && i >= MID2)
        {
            g = 7;
        }
        else if (j >= MID2 && i >= MID2)
        {
            g = 8;
        }
        
        for (int ii = 0; ii < BOARD_SIZE; ++ii)
        {
        	int idx = i*BOARD_SIZE + ii;
        	if (idx != index)
        		row[ii] = array[idx];
        	
        	idx = ii*BOARD_SIZE + j;
        	if (idx != index)
        		col[ii] = array[idx];
        	
        	idx = mGrids[g][ii];
        	if (idx != index)
        		grid[ii] = array[idx];
        }

        for (i=1; i<=9; ++i) {
        	if (isCandidate(i, row) && isCandidate(i, col) && isCandidate(i, grid) && !al.contains(i))
        		al.add(i);
        }
        return al;
    }

    public static boolean isCandidate(int v, int[] array) {
    	for (int i: array) {
    		if (i == v)
    			return false;
    	}
    	return true;
    }

    // implements Parcelable
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order	
		
		// level
		arg0.writeInt(mLevel.getValue());
		// solution
		arg0.writeIntArray(mSolution);
		// puzzle
		arg0.writeIntArray(mPuzzle);
		// game
		arg0.writeIntArray(mGame);
		// blankNum
		arg0.writeInt(mBlankNum);
		// isSolved
		arg0.writeInt(mIsSolved ? 1 : 0);
		
		// timeUsed
		arg0.writeLong(mTimeUsed);
		// activeCell
		arg0.writeInt(mActiveCell);
		// isTimerOn
		arg0.writeInt(mIsTimerOn ? 1 : 0);
	}

	/** 
	 * Called from the constructor to create this 
	 * object from a parcel. 
	 *
	 * @param in - parcel from which to re-create object 
	 **/
	public void readFromParcel(Parcel in) {   
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		
		// level
		int level = in.readInt();
		mLevel = Level.getEnum(level);
		// solution
		in.readIntArray(mSolution);
		// puzzle
		in.readIntArray(mPuzzle);
		// game
		in.readIntArray(mGame);
		// bankNum
		mBlankNum = in.readInt();
		// isSolved
		mIsSolved = (in.readInt() == 1 ? true : false);

		// timeUsed
		mTimeUsed = in.readLong();
		// activeCell
		mActiveCell = in.readInt();
		// isTimerOn
		mIsTimerOn = (in.readInt() == 1) ? true : false;
	}

	/** 
	* This field is needed for Android to be able to 
	* create new objects, individually or as arrays. 
	* 
	* This also means that you can use use the default 
	* constructor to create the object and use another 
	* method to initialize it as necessary. 
	**/
	public static final Creator<Sudoku> CREATOR =
		new Creator<Sudoku>() {
			public Sudoku createFromParcel(Parcel in) {
				return new Sudoku(in);
			}   
			public Sudoku[] newArray(int size) {
				return new Sudoku[size];
			}
		};


	/**
	 *  implementing Serializable requires this field
	 */
	private static final long serialVersionUID = -5843077108709962140L;

}
