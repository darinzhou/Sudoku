package com.easyware.sudoku;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity 
						  implements OnClickListener,
						  			 SettingsDialogFragment.SettingsDialogListener,
						  			 NewGameDialogFragment.NewGameDialogListener,
						  			 SaveFileDialogFragment.SaveFileDialogListener,
						  			 OpenFileDialogFragment.OpenFileDialogListener,
						  			 InputBestNameDialogFragment.InputBestNameDialogListener,
						  			 BestTimesDialogFragment.BestTimesDialogFragmentListener
{
	//// version control
	public final static boolean mIsFreeVersion = false; 
	public static boolean showFreeVersionNotice(Context context) {
		if (mIsFreeVersion) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.free_version_title)
				   .setMessage(R.string.free_version_msg)
				   .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
					   
				   })
				   .show();
		}
		return mIsFreeVersion;
	}
	////
	
	public final static long MAX_BEST_TIME = 3600*1000*100;	// in ms, 100 hours
	
	public final static String APP_SETTINGS_AUTOCHECK = "APP_SETTINGS_AUTOCHECK";
	public final static String APP_SETTINGS_AUTOHELP = "APP_SETTINGS_AUTOHELP";

	public final static String APP_BEST_TIME_EASY_TIME_1 = "APP_BEST_TIME_EASY_TIME_1";
	public final static String APP_BEST_TIME_EASY_TIME_2 = "APP_BEST_TIME_EASY_TIME_2";
	public final static String APP_BEST_TIME_EASY_TIME_3 = "APP_BEST_TIME_EASY_TIME_3";
	public final static String APP_BEST_TIME_EASY_NAME_1 = "APP_BEST_TIME_EASY_NAME_1";
	public final static String APP_BEST_TIME_EASY_NAME_2 = "APP_BEST_TIME_EASY_NAME_2";
	public final static String APP_BEST_TIME_EASY_NAME_3 = "APP_BEST_TIME_EASY_NAME_3";
	public final static String APP_BEST_TIME_MEDIUM_TIME_1 = "APP_BEST_TIME_MEDIUM_TIME_1";
	public final static String APP_BEST_TIME_MEDIUM_TIME_2 = "APP_BEST_TIME_MEDIUM_TIME_2";
	public final static String APP_BEST_TIME_MEDIUM_TIME_3 = "APP_BEST_TIME_MEDIUM_TIME_3";
	public final static String APP_BEST_TIME_MEDIUM_NAME_1 = "APP_BEST_TIME_MEDIUM_NAME_1";
	public final static String APP_BEST_TIME_MEDIUM_NAME_2 = "APP_BEST_TIME_MEDIUM_NAME_2";
	public final static String APP_BEST_TIME_MEDIUM_NAME_3 = "APP_BEST_TIME_MEDIUM_NAME_3";
	public final static String APP_BEST_TIME_HARD_TIME_1 = "APP_BEST_TIME_HARD_TIME_1";
	public final static String APP_BEST_TIME_HARD_TIME_2 = "APP_BEST_TIME_HARD_TIME_2";
	public final static String APP_BEST_TIME_HARD_TIME_3 = "APP_BEST_TIME_HARD_TIME_3";
	public final static String APP_BEST_TIME_HARD_NAME_1 = "APP_BEST_TIME_HARD_NAME_1";
	public final static String APP_BEST_TIME_HARD_NAME_2 = "APP_BEST_TIME_HARD_NAME_2";
	public final static String APP_BEST_TIME_HARD_NAME_3 = "APP_BEST_TIME_HARD_NAME_3";
	public final static String APP_BEST_TIME_TOUGH_TIME_1 = "APP_BEST_TIME_TOUGH_TIME_1";
	public final static String APP_BEST_TIME_TOUGH_TIME_2 = "APP_BEST_TIME_TOUGH_TIME_2";
	public final static String APP_BEST_TIME_TOUGH_TIME_3 = "APP_BEST_TIME_TOUGH_TIME_3";
	public final static String APP_BEST_TIME_TOUGH_NAME_1 = "APP_BEST_TIME_TOUGH_NAME_1";
	public final static String APP_BEST_TIME_TOUGH_NAME_2 = "APP_BEST_TIME_TOUGH_NAME_2";
	public final static String APP_BEST_TIME_TOUGH_NAME_3 = "APP_BEST_TIME_TOUGH_NAME_3";

	public final static String GAME_STATE_SUDOKU = "GAME_STATE_SUDOKU";
	
	private final int MIN_BUTTON_HEIGHT = 40;

	private Sudoku sudoku;
	private Sudoku.CheckResult mCheckResult;
	
	private Drawable mInitBackground;
	private Drawable mFillableBackground;
	private Drawable mActiveBackground;
	
	private int mGameBackgroundColor;
	private int mCellInitBackgroundColor;
	private int mCellFillableBackgroundColor;
	private int mCellActiveBackgroundColor;
	private int mHintBackgroundColor;
	
	private int mCellNormalTextColor;
	private int mCellErrorTextColor;
	private int mHinttextColor;
	private int mTimerTextColor;
	
	private boolean mIsAutoCheck;
	private boolean mIsAutoHelp;
	
	private TextView mActiveCell;
	private TextView[] mCells;
	private Button[] mNumbers;
	
	private Menu menu;
	private TextView tvHint;
	private TextView tvTimer;

	private ImageButton btnLoad;
	private ImageButton btnSave;
	private ImageButton btnClearAll;
	private ImageButton btnHint;
	private ImageButton btnCheck;
	private int idLoad;
	private int idSave;
	private int idClearAll;
	private int idHint;
	private int idCheck;
	
	// best time records
	private long mEasyBestTimes[] = {MAX_BEST_TIME, MAX_BEST_TIME, MAX_BEST_TIME};
	private long mMediumBestTimes[] = {MAX_BEST_TIME, MAX_BEST_TIME, MAX_BEST_TIME};
	private long mHardBestTimes[] = {MAX_BEST_TIME, MAX_BEST_TIME, MAX_BEST_TIME};
	private long mToughBestTimes[] = {MAX_BEST_TIME, MAX_BEST_TIME, MAX_BEST_TIME};
	private String mEasyBestNames[] = new String[3];
	private String mMediumBestNames[] = new String[3];
	private String mHardBestNames[] = new String[3];
	private String mToughBestNames[] = new String[3];
	private String mKeyTimeTokeep = "";
	private String mKeyNameToKeep = "";
	private long mTimeRecordToKeep;

	// task to update time used in playing this game
	private boolean mIsTimerOn;
	private boolean mIsTimerOnForKeepState;
	private long mTimeUsed;	// milliseconds
	private long mTimeUsedBefore;
	private long mStartTime;
	private Handler mHandlerTimer;
	private Runnable mUpdateTimeTask = new Runnable() {   
		public void run() {       
			final long start = mStartTime - mTimeUsedBefore;       
			mTimeUsed = System.currentTimeMillis() - start;       
			showTimeUsed();
			
			// call it self every seconds
			mHandlerTimer.postDelayed(this, 1000);   
		}
	};
	public void startTimer() {
		mTimeUsed = 0;
		resumeTimer();
	}
	public void resumeTimer() {
		mTimeUsedBefore = mTimeUsed;
		mHandlerTimer = new Handler();
		mStartTime = System.currentTimeMillis();            
		mHandlerTimer.removeCallbacks(mUpdateTimeTask);            
		mHandlerTimer.postDelayed(mUpdateTimeTask, 100);
		mIsTimerOn = true;
	}
	public void stopTimer() {
		mHandlerTimer.removeCallbacks(mUpdateTimeTask);
		mIsTimerOn = false;
	}
	public void showTimeUsed() {
		tvTimer.setText(getUsedTimeString());
	}
	public long getTimeUsed() {
		return mTimeUsed;
	}
	public static String getUsedTimeString(long timeUsed) {
		int seconds = (int) (timeUsed / 1000);  
		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;       
		seconds = (seconds % 3600) % 60; 
		String sHour = String.valueOf(hours);
		if (hours < 10)
			sHour = "0" + sHour;
		String sMin = String.valueOf(minutes);
		if (minutes < 10)
			sMin ="0" + sMin;
		String sSec = String.valueOf(seconds);
		if (seconds < 10)
			sSec = "0" + sSec;
		
		return sHour + ":" + sMin + ":" + sSec;
	}
	public String getUsedTimeString() {
		return getUsedTimeString(mTimeUsed);
	}
	public boolean isTimerOn() {
		return mIsTimerOn;
	}
	public boolean isGamePaused() {
		if (sudoku == null)
			return false;
		return (!sudoku.getIsSolved() && !isTimerOn());
	}
	
	// Returns a valid view id that isn't in use
	private static int mViewId = 1;
	public int generateViewId() {  
	    View v = findViewById(mViewId);  
	    while (v != null) {  
	        v = findViewById(++mViewId);  
	    }  
	    return mViewId++;  
	}
	
	// SPLASH_SCREEN_TIMEOUT
	public final static int SPLASH_SCREEN_TIMEOUT = 3000;
	
	/** 
	 * Simple Dialog used to show the splash screen 
	 */
	protected Dialog mSplashDialog;
	
	/** 
	 * Removes the Dialog that displays the splash screen 
	 */
	protected void removeSplashScreen() {    
		if (mSplashDialog != null) {        
			mSplashDialog.dismiss();        
			mSplashDialog = null;    
			}
		} 
	
	/** Shows the splash screen over the full Activity 
	 */
	protected void showSplashScreen() {    
		mSplashDialog = new Dialog(this, R.style.SplashScreen);    
		mSplashDialog.setContentView(R.layout.splash_screen);    
		mSplashDialog.setCancelable(false);    
		mSplashDialog.show();         
		
		// Set Runnable to remove splash screen just in case    
		final Handler handler = new Handler();    
		handler.postDelayed(new Runnable() {      
			@Override      
			public void run() {        
				removeSplashScreen();      
			}    
		}, SPLASH_SCREEN_TIMEOUT);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		// only show splash screen for new running
//		if (savedInstanceState == null) {
//			showSplashScreen();
//		}
		
		// initialization
		
		tvHint = (TextView)findViewById(R.id.tvHint);
		tvHint.setGravity(Gravity.CENTER_VERTICAL);
		tvTimer = (TextView)findViewById(R.id.tvTimer);
		tvTimer.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		
		// app settings
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mIsAutoCheck = preferences.getBoolean(APP_SETTINGS_AUTOCHECK, false);
		mIsAutoHelp = preferences.getBoolean(APP_SETTINGS_AUTOHELP, false);
		
		// best times
		mEasyBestTimes[0] = preferences.getLong(APP_BEST_TIME_EASY_TIME_1, MAX_BEST_TIME);
		mEasyBestTimes[1] = preferences.getLong(APP_BEST_TIME_EASY_TIME_2, MAX_BEST_TIME);
		mEasyBestTimes[2] = preferences.getLong(APP_BEST_TIME_EASY_TIME_3, MAX_BEST_TIME);
		mMediumBestTimes[0] = preferences.getLong(APP_BEST_TIME_MEDIUM_TIME_1, MAX_BEST_TIME);
		mMediumBestTimes[1] = preferences.getLong(APP_BEST_TIME_MEDIUM_TIME_2, MAX_BEST_TIME);
		mMediumBestTimes[2] = preferences.getLong(APP_BEST_TIME_MEDIUM_TIME_3, MAX_BEST_TIME);
		mHardBestTimes[0] = preferences.getLong(APP_BEST_TIME_HARD_TIME_1, MAX_BEST_TIME);
		mHardBestTimes[1] = preferences.getLong(APP_BEST_TIME_HARD_TIME_2, MAX_BEST_TIME);
		mHardBestTimes[2] = preferences.getLong(APP_BEST_TIME_HARD_TIME_3, MAX_BEST_TIME);
		mToughBestTimes[0] = preferences.getLong(APP_BEST_TIME_TOUGH_TIME_1, MAX_BEST_TIME);
		mToughBestTimes[1] = preferences.getLong(APP_BEST_TIME_TOUGH_TIME_2, MAX_BEST_TIME);
		mToughBestTimes[2] = preferences.getLong(APP_BEST_TIME_TOUGH_TIME_3, MAX_BEST_TIME);
		// best names
		String defaultName = getResources().getString(R.string.anonymous);
		mEasyBestNames[0] = preferences.getString(APP_BEST_TIME_EASY_NAME_1, defaultName);
		mEasyBestNames[1] = preferences.getString(APP_BEST_TIME_EASY_NAME_2, defaultName);
		mEasyBestNames[2] = preferences.getString(APP_BEST_TIME_EASY_NAME_3, defaultName);
		mMediumBestNames[0] = preferences.getString(APP_BEST_TIME_MEDIUM_NAME_1, defaultName);
		mMediumBestNames[1] = preferences.getString(APP_BEST_TIME_MEDIUM_NAME_2, defaultName);
		mMediumBestNames[2] = preferences.getString(APP_BEST_TIME_MEDIUM_NAME_3, defaultName);
		mHardBestNames[0] = preferences.getString(APP_BEST_TIME_HARD_NAME_1, defaultName);
		mHardBestNames[1] = preferences.getString(APP_BEST_TIME_HARD_NAME_2, defaultName);
		mHardBestNames[2] = preferences.getString(APP_BEST_TIME_HARD_NAME_3, defaultName);
		mToughBestNames[0] = preferences.getString(APP_BEST_TIME_TOUGH_NAME_1, defaultName);
		mToughBestNames[1] = preferences.getString(APP_BEST_TIME_TOUGH_NAME_2, defaultName);
		mToughBestNames[2] = preferences.getString(APP_BEST_TIME_TOUGH_NAME_3, defaultName);
		
		// colors
  		InitColors();

  		// main layout
		RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
  		mainLayout.setBackgroundColor(mGameBackgroundColor);

  		// create the board and control buttons
		createUI(mainLayout, this); // run in mainLayout's message queue
		showHint();

		// if there is a state saved, just restore it, otherwise new a game
		if (savedInstanceState != null) {
			sudoku = savedInstanceState.getParcelable(GAME_STATE_SUDOKU);
			
			// run in mainLayout's message queue to wait for createUI
			mainLayout.post(new Runnable()	{
			  	public void run() {
					restoreSudoku();
			  	}
			});
		}
		else {
			// run in mainLayout's message queue to wait for createUI
			mainLayout.post(new Runnable()	{
			  	public void run() {
//			  		// remove splash screen before new game
//			  		removeSplashScreen();
					// new game dialog
					newGame();
			  	}
			});
		}
	}

	public void InitColors() {
		mGameBackgroundColor = Color.WHITE;
		mCellInitBackgroundColor = Color.rgb(224, 224, 224);
		mCellFillableBackgroundColor = Color.WHITE;
		mCellActiveBackgroundColor = Color.CYAN;
		mHintBackgroundColor = Color.BLACK;
		
		mCellNormalTextColor = Color.BLACK;
		mCellErrorTextColor = Color.RED;
		mHinttextColor = Color.GREEN;
		mTimerTextColor = Color.WHITE;
	}

	public void InitBackgrounds() {
		mInitBackground = createDrawableBackground(mCellInitBackgroundColor);
		mFillableBackground = createDrawableBackground(mCellFillableBackgroundColor);
		mActiveBackground = createDrawableBackground(mCellActiveBackgroundColor);
	}
	
	public void createUI(final RelativeLayout mainLayout, final OnClickListener l) {
		mainLayout.post(new Runnable()	{
		  	public void run() {
				int paddingH = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
				int paddingV = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);

		  		// backgrounds
		  		InitBackgrounds();
		  		
				// get height of tiltlebar and statusbar 
			  	Rect rect = new Rect();
			  	mainLayout.getWindowVisibleDisplayFrame(rect);
			  	int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
			  	int statusBarHeight = rect.top;
			  	int titleBarHeight= contentViewTop - statusBarHeight;
				
			  	// get device orientation
				Point outSize = new Point(0, 0);
				boolean isLandscape = isLandscapeMode(outSize);
				int shortEdge = isLandscape ?
							 (outSize.y - paddingV * 2 - statusBarHeight - titleBarHeight) :
							 (outSize.x - paddingH * 2);
						
				// create cells and buttons

				mCells = new TextView[Sudoku.CELL_NUM];
				mNumbers = new Button[10];
				
				int cellWidth = (shortEdge - 2)/Sudoku.BOARD_SIZE;
				int boardWidth = (cellWidth - 1)*Sudoku.BOARD_SIZE + 3;
				int padding = (shortEdge - boardWidth)/2;
				int adjustedPadding = padding;
				
				// adjust board size
				if (!isLandscape) {
					int y0 = padding+cellWidth + padding+boardWidth+padding;
					int heightAdjust = 4;
					int buttonHeight = (outSize.y-titleBarHeight-statusBarHeight-y0-padding)/3+heightAdjust;
					
					if (buttonHeight < MIN_BUTTON_HEIGHT) {
						buttonHeight = MIN_BUTTON_HEIGHT;
						int boardHeight = outSize.y-titleBarHeight-statusBarHeight-padding*4-buttonHeight*3;
						boardWidth = boardHeight;
						cellWidth = (boardWidth-3)/Sudoku.BOARD_SIZE+1;
						adjustedPadding = (shortEdge - boardWidth)/2;
					}
				}
				else {
					int x0 = boardWidth+padding*2;
					int buttonWidth = (outSize.x-x0-padding)/5;

					if (buttonWidth < MIN_BUTTON_HEIGHT) {
						// should never happen??
					}
				}
				
				int x = padding;
				int y = padding;

				if (!isLandscape) {
					// hint
					int hintHeight = (int)(cellWidth*1.2);
					int hintWidth = shortEdge-padding*2-2;
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(hintWidth, hintHeight);
					lp.leftMargin = padding;
					lp.topMargin = padding;
					tvHint.setLayoutParams(lp);
					tvHint.setTextColor(mHinttextColor);
					tvHint.setBackgroundColor(mHintBackgroundColor);
					tvHint.setPadding(8, 0, tvTimer.getMeasuredWidth()+8, 0);
					// timer
					tvTimer.setTextColor(mTimerTextColor);
					tvTimer.setBackgroundColor(mHintBackgroundColor);
					tvTimer.bringToFront();
					
					y += hintHeight + padding;
					int x0 = x;
					int y0 = y+boardWidth;//+padding;
					
					int heightAdjust = 4;
					int buttonWidth = (shortEdge-padding*2)/5;
					int buttonHeight = (outSize.y-titleBarHeight-statusBarHeight-y0-padding)/3+heightAdjust;
					
					// create number buttons
					for (int i=0; i<2; ++i) {
						for (int j=0; j<5; ++j) {
							int index = i*5 + j;
							mNumbers[index] = createButton(x0, y0, buttonWidth, buttonHeight);
							
							int v = index+1;
							if (v != 10) {
								String sv = String.valueOf(v);
								mNumbers[index].setText(sv);
								mNumbers[index].setTag(sv);
							}
							else {
								v = 0;
								mNumbers[index].setText("C");
								mNumbers[index].setTag(String.valueOf(v));
							}
							
							mNumbers[index].setOnClickListener(l);
							mainLayout.addView(mNumbers[index]);
			
							x0 += buttonWidth;
						}

						x0 = x;
						y0 += buttonHeight-heightAdjust;
					}
					
					// create control buttons

					//buttonWidth = (shortEdge-padding*2)/5;

					// Load
					x0 = x;
					btnLoad = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnLoad.setImageResource(R.drawable.load);
					btnLoad.setOnClickListener(l);
					mainLayout.addView(btnLoad);
					idLoad = btnLoad.getId();
					
					// Save
					x0 += buttonWidth;
					btnSave = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnSave.setImageResource(R.drawable.save);
					btnSave.setOnClickListener(l);
					mainLayout.addView(btnSave);
					idSave = btnSave.getId();
					
					// Clear All
					x0 += buttonWidth;
					btnClearAll = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnClearAll.setImageResource(R.drawable.clear_all);
					btnClearAll.setOnClickListener(l);
					mainLayout.addView(btnClearAll);
					idClearAll = btnClearAll.getId();

					// Hint
					x0 += buttonWidth;
					btnHint = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnHint.setImageResource(R.drawable.hint);
					btnHint.setOnClickListener(l);
					mainLayout.addView(btnHint);
					idHint = btnHint.getId();

					// Check
					x0 += buttonWidth;
					btnCheck = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnCheck.setImageResource(R.drawable.check);
					btnCheck.setOnClickListener(l);
					mainLayout.addView(btnCheck);
					idCheck = btnCheck.getId();
				}
				else {
					int x0 = boardWidth+padding*2;
					int x00 = x0;
					int y0 = padding;
					// hint
					int hintWidth = outSize.x - x0 - padding*2-4;
					int hintHeight = (int)(cellWidth*2);
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(hintWidth, hintHeight);
					lp.leftMargin = x0+4;
					lp.topMargin = y0;
					tvHint.setLayoutParams(lp);
					tvHint.setTextColor(Color.GREEN);
					tvHint.setBackgroundColor(Color.BLACK);
					tvHint.setPadding(8, 0, tvTimer.getMeasuredWidth()+8, 0);
					// timer
					tvTimer.setTextColor(mTimerTextColor);
					tvTimer.setBackgroundColor(mHintBackgroundColor);
					tvTimer.bringToFront();
					
					y0 += hintHeight + padding;
					
					int heightAdjust = 0;
					int buttonWidth = (outSize.x-x0-padding)/5;
					int buttonHeight = (outSize.y-titleBarHeight-statusBarHeight-y0-padding)/3+heightAdjust;
					
					// create number buttons
					for (int i=0; i<2; ++i) {
						for (int j=0; j<5; ++j) {
							int index = i*5 + j;
							mNumbers[index] = createButton(x0, y0, buttonWidth, buttonHeight);
							
							int v = index+1;
							if (v != 10) {
								String sv = String.valueOf(v);
								mNumbers[index].setText(sv);
								mNumbers[index].setTag(sv);
							}
							else {
								v = 0;
								mNumbers[index].setText("C");
								mNumbers[index].setTag(String.valueOf(v));
							}
							
							mNumbers[index].setOnClickListener(l);
							mainLayout.addView(mNumbers[index]);
			
							x0 += buttonWidth;
						}

						x0 = x00;
						y0 += buttonHeight-heightAdjust;
					}
					
					// create control buttons

					//buttonWidth = (outSize.x-x0-padding)/5;

					// Load
					x0 = x00;
					btnLoad = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnLoad.setImageResource(R.drawable.load);
					btnLoad.setOnClickListener(l);
					mainLayout.addView(btnLoad);
					idLoad = btnLoad.getId();
					
					// Save
					x0 += buttonWidth;
					btnSave = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnSave.setImageResource(R.drawable.save);
					btnSave.setOnClickListener(l);
					mainLayout.addView(btnSave);
					idSave = btnSave.getId();
					
					// Clear All
					x0 += buttonWidth;
					btnClearAll = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnClearAll.setImageResource(R.drawable.clear_all);
					btnClearAll.setOnClickListener(l);
					mainLayout.addView(btnClearAll);
					idClearAll = btnClearAll.getId();

					// Hint
					x0 += buttonWidth;
					btnHint = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnHint.setImageResource(R.drawable.hint);
					btnHint.setOnClickListener(l);
					mainLayout.addView(btnHint);
					idHint = btnHint.getId();

					// Check
					x0 += buttonWidth;
					btnCheck = createImageButton(x0, y0, buttonWidth, buttonHeight);
					btnCheck.setImageResource(R.drawable.check);
					btnCheck.setOnClickListener(l);
					mainLayout.addView(btnCheck);
					idCheck = btnCheck.getId();
				}
				
				// create cells

				x = adjustedPadding;

				for (int i=0; i<Sudoku.BOARD_SIZE; ++i) {
					for (int j=0; j<Sudoku.BOARD_SIZE; ++j) {
						int index = i*Sudoku.BOARD_SIZE + j;
						mCells[index] = createTextView(x, y, cellWidth, cellWidth, mInitBackground);
						mCells[index].setTag(String.valueOf(index));
						mCells[index].setOnClickListener(l);
						mainLayout.addView(mCells[index]);
		
						x += cellWidth - 1;
						
						int nextCol = j + 1;
						if (nextCol == Sudoku.MID1 || nextCol == Sudoku.MID2)
							x++;
					}
					
					x = adjustedPadding;
					y += cellWidth - 1;
		
					int nextRow = i + 1;
					if (nextRow == Sudoku.MID1 || nextRow == Sudoku.MID2)
						y++;
				}
		  	}
	  	});						  	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		switchPauseResumeMenu();
		return true;
	}

	public void pauseGame() {
		stopTimer();
		menu.findItem(R.id.miPause).setTitle(R.string.resume);
		showHint(R.string.msg_paused);
	}
	public void resumeGame() {
		resumeTimer();
		menu.findItem(R.id.miPause).setTitle(R.string.pause);
		showHint();
	}
	
	public void switchPauseResumeMenu() {
		if (menu != null)
			menu.findItem(R.id.miPause).setTitle(isGamePaused() ? R.string.resume : R.string.pause);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		unmarkErrorCells();
		switch(item.getItemId()) {
		case R.id.miNewGame:
			removeActiveCell();
			newGame();
			break;
		case R.id.miPause:
			if (sudoku != null) {
				if (!isGamePaused()) {
					pauseGame();
				}
				else {
					resumeGame();
				}
			}
			break;
		case R.id.miSettings:
			showHint();
			SettingsDialogFragment settings = SettingsDialogFragment.newInstance(mIsAutoCheck, mIsAutoHelp);
			settings.setCancelable(false);
			settings.show(getSupportFragmentManager(), "SUDOKU_SETTINGS_DIALOG");
			break;
		case R.id.miBestTimes:
			BestTimesDialogFragment bestTimes = new BestTimesDialogFragment();
			bestTimes.show(getSupportFragmentManager(), "SUDOKU_BEST_TIMES_DIALOG");
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		// if sudoku has not created, do nothing for any button click except Load
		if (sudoku == null && id != idLoad)
			return;
		
		unmarkErrorCells();
		
		// if any button other than Load and save was pressed while pausing, the game should be resumed
		if (id != idLoad && id != idSave) {
			if (isGamePaused()) {
				resumeGame();
			}
		}
		
		// response to each button clicking
		if (isCell(id)) {
			activateCell((TextView)v);
			if (mIsAutoHelp) {
				showHintInTwoLines(buildStringCandidates());
			}
			else {
				showHint();
			}
		}
		else if (isNumber(id)) {
			fillActiveCell((Button)v);
			
			if (mIsAutoCheck) {
				mCheckResult = sudoku.checkValid();
				markErrorCells();
			}
			else {
				showHint();
			}
		}
		else if (id == idLoad) {
			removeActiveCell();
			showHint();
			OpenFileDialogFragment openFile = new OpenFileDialogFragment();
			openFile.setCancelable(true);
			openFile.show(getSupportFragmentManager(), "SUDOKU_OPEN_FILE_DIALOG");
		}
		else if (id == idSave) {
			showHint();
			SaveFileDialogFragment saveFile = new SaveFileDialogFragment();
			saveFile.setCancelable(false);
			saveFile.show(getSupportFragmentManager(), "SUDOKU_SAVE_FILE_DIALOG");
		}
		else if (id == idClearAll) {
			Resources res = getResources();
			int msgId = R.string.sure_to_clear_all;
			if (sudoku.getIsSolved()) {
				msgId = R.string.sure_to_replay;
			}
			
	        new AlertDialog.Builder(this)
	        	.setMessage(msgId)
	        	.setCancelable(false)
	        	.setPositiveButton(res.getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						sudoku.clearAll();
						clearAllInputOnBoard();
						// if timer is off, which means the puzzle has been solved in this case
						// then we need to start(not resume) the timer
						if (!isTimerOn())
							startTimer();
						showHint();
					}
				})
				.setNegativeButton(res.getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// no, do nothing
						showHint();
					}
				})
				.show();
		}
		else if (id == idHint) {
			showCandidates();
		}
		else if (id == idCheck) {
			if (sudoku.getIsSolved()) {
				showHint(R.string.msg_solved);
			}
			else {
				mCheckResult = sudoku.checkValid();
				markErrorCells();
			}
		}	
	}
	
	public boolean isCell(int id) {
		for (TextView b : mCells) {
			if (b.getId() == id)
				return true;
		}
		return false;
	}
	
	public boolean isNumber(int id) {
		for (Button b : mNumbers) {
			if (b.getId() == id)
				return true;
		}
		return false;
	}

	public void activateCell(TextView cell) {
		if (mActiveCell != null)
			mActiveCell.setBackground(mFillableBackground);
		cell.setBackground(mActiveBackground);
		mActiveCell = cell;
	}

	public int intFromTag(View v) {
		if (v == null)
			return -1;
		String tag = (String)v.getTag();
		if (tag != null) {
			return Integer.parseInt(tag);
		}
		return -1;
	}
	
	public void removeActiveCell() {
		if (mActiveCell != null)
			mActiveCell.setBackground(mFillableBackground);
		mActiveCell = null;
	}
	
	public void fillActiveCell(Button btn) {
		if (btn == null || mActiveCell == null)
			return;
		
		String s = (String)btn.getTag();
		int v = intFromTag(btn);
		if (v == 0)
			s = " ";
		mActiveCell.setText(s);
		
		// fill and check Sudoku
		sudoku.setGameAt(intFromTag(mActiveCell), v);
		
		// check whether puzzle is sloved
		if (sudoku.isSolved()) {
			// stop timer
			stopTimer();
			// show sloved message
			showHint(R.string.msg_solved);
			
			// remove active cell and make all cells unclickable
			removeActiveCell();
			for (TextView cell : mCells) {
				if (cell.isClickable())
					cell.setClickable(false);
			}
			
			// show dialog to enter winner list
			String level = "";
			String order = "";
			if (sudoku.getLevel() == Sudoku.Level.Easy) {
				level = getResources().getString(R.string.level_easy);
				if (mTimeUsed < mEasyBestTimes[0]) {
					order = getResources().getString(R.string.no_1);
					mEasyBestTimes[2] = mEasyBestTimes[1];
					mEasyBestTimes[1] = mEasyBestTimes[0];
					mEasyBestNames[2] = mEasyBestNames[1];
					mEasyBestNames[1] = mEasyBestNames[0];
					mTimeRecordToKeep = mEasyBestTimes[0] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_EASY_TIME_1;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_EASY_NAME_1;
				}
				else if (mTimeUsed < mEasyBestTimes[1]) {
					order = getResources().getString(R.string.no_2);
					mEasyBestTimes[2] = mEasyBestTimes[1];
					mEasyBestNames[2] = mEasyBestNames[1];
					mTimeRecordToKeep = mEasyBestTimes[1] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_EASY_TIME_2;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_EASY_NAME_2;
				}
				else if (mTimeUsed < mEasyBestTimes[2]) {
					order = getResources().getString(R.string.no_3);
					mTimeRecordToKeep = mEasyBestTimes[2] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_EASY_TIME_3;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_EASY_NAME_3;
				}
			}
			else if (sudoku.getLevel() == Sudoku.Level.Medium) {
				level = getResources().getString(R.string.level_medium);
				if (mTimeUsed < mMediumBestTimes[0]) {
					order = getResources().getString(R.string.no_1);
					mMediumBestTimes[2] = mMediumBestTimes[1];
					mMediumBestTimes[1] = mMediumBestTimes[0];
					mMediumBestNames[2] = mMediumBestNames[1];
					mMediumBestNames[1] = mMediumBestNames[0];
					mTimeRecordToKeep = mMediumBestTimes[0] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_MEDIUM_TIME_1;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_MEDIUM_NAME_1;
				}
				else if (mTimeUsed < mMediumBestTimes[1]) {
					order = getResources().getString(R.string.no_2);
					mMediumBestTimes[2] = mMediumBestTimes[1];
					mMediumBestNames[2] = mMediumBestNames[1];
					mTimeRecordToKeep = mMediumBestTimes[1] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_MEDIUM_TIME_2;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_MEDIUM_NAME_2;
				}
				else if (mTimeUsed < mMediumBestTimes[2]) {
					order = getResources().getString(R.string.no_3);
					mTimeRecordToKeep = mMediumBestTimes[2] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_MEDIUM_TIME_3;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_MEDIUM_NAME_3;
				}
			}
			else if (sudoku.getLevel() == Sudoku.Level.Hard) {
				level = getResources().getString(R.string.level_hard);
				if (mTimeUsed < mHardBestTimes[0]) {
					order = getResources().getString(R.string.no_1);
					mHardBestTimes[2] = mHardBestTimes[1];
					mHardBestTimes[1] = mHardBestTimes[0];
					mHardBestNames[2] = mHardBestNames[1];
					mHardBestNames[1] = mHardBestNames[0];
					mTimeRecordToKeep = mHardBestTimes[0] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_HARD_TIME_1;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_HARD_NAME_1;
				}
				else if (mTimeUsed < mHardBestTimes[1]) {
					order = getResources().getString(R.string.no_2);
					mHardBestTimes[2] = mHardBestTimes[1];
					mHardBestNames[2] = mHardBestNames[1];
					mTimeRecordToKeep = mHardBestTimes[1] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_HARD_TIME_2;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_HARD_NAME_2;
				}
				else if (mTimeUsed < mHardBestTimes[2]) {
					order = getResources().getString(R.string.no_3);
					mTimeRecordToKeep = mHardBestTimes[2] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_HARD_TIME_3;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_HARD_NAME_3;
				}
			}
			else if (sudoku.getLevel() == Sudoku.Level.Tough) {
				level = getResources().getString(R.string.level_tough);
				if (mTimeUsed < mToughBestTimes[0]) {
					order = getResources().getString(R.string.no_1);
					mToughBestTimes[2] = mToughBestTimes[1];
					mToughBestTimes[1] = mToughBestTimes[0];
					mToughBestNames[2] = mToughBestNames[1];
					mToughBestNames[1] = mToughBestNames[0];
					mTimeRecordToKeep = mToughBestTimes[0] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_TOUGH_TIME_1;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_TOUGH_NAME_1;
				}
				else if (mTimeUsed < mToughBestTimes[1]) {
					order = getResources().getString(R.string.no_2);
					mToughBestTimes[2] = mToughBestTimes[1];
					mToughBestNames[2] = mToughBestNames[1];
					mTimeRecordToKeep = mToughBestTimes[1] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_TOUGH_TIME_2;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_TOUGH_NAME_2;
				}
				else if (mTimeUsed < mToughBestTimes[2]) {
					order = getResources().getString(R.string.no_3);
					mTimeRecordToKeep = mToughBestTimes[2] = mTimeUsed;
					mKeyTimeTokeep = MainActivity.APP_BEST_TIME_TOUGH_TIME_3;
					mKeyNameToKeep = MainActivity.APP_BEST_TIME_TOUGH_NAME_3;
				}
			}
			
			// we got a record
			if (order != "") {
				InputBestNameDialogFragment inputName = InputBestNameDialogFragment.newInstance(level, order);
				inputName.show(getSupportFragmentManager(), "SUDOKU_INPUT_NAME_DIALOG");
			}
		}
		else {
			showHint();
		}
	}

	public void markErrorCells() {
		if (mCheckResult == null)
			return;
		
		String s = "";
		if (mCheckResult.getPos() != null) {
	    	for (int i=0; i<mCheckResult.getPos().length; ++i) {
	    		int index = mCheckResult.getPos()[i];
	   			if (sudoku.getGameAt(index) == mCheckResult.getVal())
	   				mCells[index].setTextColor(mCellErrorTextColor);
	    	}
	    	
	    	s = getResources().getString(R.string.error_duplicated, mCheckResult.getVal());
		}
		else {
			s = getResources().getString(R.string.error_not_found);
		}

		if (mIsAutoCheck) {
    		showHintInTwoLines(s);
    	}
    	else {
    		showHint(s);
    	}
}

	public void unmarkErrorCells() {
		if (mCheckResult == null)
			return;
		if (mCheckResult.getPos() != null) {
	    	for (int i=0; i<mCheckResult.getPos().length; ++i) {
	    		int index = mCheckResult.getPos()[i];
	   			if (sudoku.getGameAt(index) == mCheckResult.getVal())
	   				mCells[index].setTextColor(mCellNormalTextColor);
	    	}
		}
    	mCheckResult = null;
    }

	public Drawable createDrawableBackground(int color, int strokeColor, int strokeWidth) {
		GradientDrawable drawable = new GradientDrawable();
	    drawable.setShape(GradientDrawable.RECTANGLE);
	    drawable.setStroke(strokeWidth, strokeColor);
	    drawable.setColor(color);
	    return drawable;
	}
	public Drawable createDrawableBackground(int color, int strokeColor) {
		return createDrawableBackground(color, strokeColor, 1);
	}
	public Drawable createDrawableBackground(int color) {
		return createDrawableBackground(color, Color.BLACK, 1);
	}

	public TextView createTextView(int x, int y, int width, int height, Drawable background) {
		TextView tv = new TextView(this);
		tv.setId(generateViewId());

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
		lp.leftMargin = x;
		lp.topMargin = y;
		tv.setLayoutParams(lp);
		
		tv.setIncludeFontPadding(false);
		tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

		int fontSize = (int)(height*0.5);
		if (width < height)
			fontSize = (int)(width*0.5);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
		tv.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		
		tv.setTextColor(mCellNormalTextColor);
		
		if (background != null)
			tv.setBackground(background);
		
		return tv;
	}
	public TextView createTextView(int x, int y, int width, int height) {
		return createTextView(x, y, width, height, null);
	}
	
	public Button createButton(int x, int y, int width, int height, Drawable background) {
		Button button = new Button(this);
		button.setId(generateViewId());

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
		lp.leftMargin = x;
		lp.topMargin = y;
		button.setLayoutParams(lp);
		
		button.setIncludeFontPadding(false);
		button.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

		int fontSize = (int)(height*0.5);
		if (width < height)
			fontSize = (int)(width*0.5);
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
		button.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		
		if (background != null)
			button.setBackground(background);
		
		return button;
	}
	public Button createButton(int x, int y, int width, int height) {
		return createButton(x, y, width, height, null);
	}
	
	public ImageButton createImageButton(int x, int y, int width, int height, Drawable background) {
		ImageButton button = new ImageButton(this);
		button.setId(generateViewId());

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
		lp.leftMargin = x;
		lp.topMargin = y;
		button.setLayoutParams(lp);
		
		button.setScaleType(ScaleType.CENTER_INSIDE);
		
		if (background != null)
			button.setBackground(background);
		
		return button;
	}
	public ImageButton createImageButton(int x, int y, int width, int height) {
		return createImageButton(x, y, width, height, null);
	}

	public boolean isLandscapeMode(Point outSize) {
		Display display = getWindowManager().getDefaultDisplay();
		display.getRealSize(outSize);
		return (outSize.x > outSize.y);
	}

	public void showHint(int resid) {
		tvHint.setText(getResources().getString(resid));
	}
	
	public void showHint(String s) {
		tvHint.setText(s);
	}

	public void showHint() {
		String s;
		if (sudoku != null) {
			if (sudoku.getIsSolved())
				s = getResources().getString(R.string.msg_solved);
			else if (isGamePaused())
				s = getResources().getString(R.string.msg_paused);
			else
				s = getResources().getString(R.string.msg_blanks, getLevelString(sudoku.getLevel()), sudoku.getLevel().getBlankNum(), sudoku.getBlankNum());
		}
		else
			s = getResources().getString(R.string.msg_new_game);
		
		showHint(s);
	}

	public String getLevelString(Sudoku.Level level) {
		if (level == Sudoku.Level.Easy)
			return getResources().getString(R.string.level_easy);
		if (level == Sudoku.Level.Medium)
			return getResources().getString(R.string.level_medium);
		if (level == Sudoku.Level.Hard)
			return getResources().getString(R.string.level_hard);
		if (level == Sudoku.Level.Tough)
			return getResources().getString(R.string.level_tough);
		return "";
	}
	
	public String buildStringCandidates() {
		if (mActiveCell == null)
			return null;
		int index = Integer.parseInt((String)mActiveCell.getTag());
		int[] candidates = sudoku.getCandidates(index);
		String s="(";
		for (int i=0; i<candidates.length-1; ++i)
			s += candidates[i] + ", ";
		s += candidates[candidates.length-1] + ")";
		return s;
	}
	
	public void showCandidates() {
		String s = buildStringCandidates();
		if (s != null) {
			if (mIsAutoHelp) {
				showHintInTwoLines(s);
			}
			else {
				showHint(s);
			}
		}
	}
	public void showHintInTwoLines(String s) {
		if (s == null || s.length() == 0)
			return;
		
		String msg = (String)tvHint.getText();
		
		int pos = msg.indexOf("\n");
		if (pos != -1)
			msg = msg.substring(0, pos);
		
		msg += "\n" + s;
		showHint(msg);
	}

	public void newGame() {
		NewGameDialogFragment newgame = new NewGameDialogFragment();
		newgame.show(getSupportFragmentManager(), "SUDOKU_NEW_GAME_DIALOG");
	}

	public void restoreSudoku() {
		// if sudoku is null, do nothing but just return
		if (sudoku == null)
			return;
		
		// check timer states before restoring
		if (isTimerOn())
			stopTimer();
		mTimeUsed = sudoku.getTimeUsed();
		mIsTimerOn = sudoku.getIsTimerOn();
		showTimeUsed();
		switchPauseResumeMenu();

		final RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		mainLayout/*new Handler()*/.post(new Runnable() {
	            public void run()
	            {
	            	// fill board
	            	initBoard();
	            	int activeCell = sudoku.getActiveCell();
	            	if (activeCell != -1)
	            		activateCell(mCells[activeCell]);
					// when come back from inactive state, we need to resume the game 
					// if is is not in pause or solved state before becomes inactive
					if (isTimerOn())
						resumeTimer();
	            	// update hint
					showHint();
	            }
	        });
	}

	public void createSudoku(final Sudoku.Level level) {
		final RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		mainLayout/*new Handler()*/.post(new Runnable() {
	            public void run()
	            {
	            	// create Sudoku
	            	sudoku = new Sudoku(level);	            	
	            	// fill board
	            	initBoard();
	            	// start to count time
	            	startTimer();
	            	// update hint
	            	showHint();
	            }
	        });
	}
	
	public void initBoard() {
    	for (int i=0; i<Sudoku.CELL_NUM; ++i) {
			mCells[i].setText(" ");
    		int v = sudoku.getPuzzleAt(i);
    		if (v != 0) {
    			mCells[i].setText(String.valueOf(v));
    			mCells[i].setBackground(mInitBackground);
    			mCells[i].setClickable(false);
    		}
    		else {
    			int v1 = sudoku.getGameAt(i);
    			if (v1 != 0)
        			mCells[i].setText(String.valueOf(v1));
    			mCells[i].setBackground(mFillableBackground);
    			mCells[i].setClickable(true);
    		}
    	}
	}

	public void clearAllInputOnBoard() {
    	for (int i=0; i<Sudoku.CELL_NUM; ++i) {
    		if (sudoku.getPuzzleAt(i) == 0) {
    			mCells[i].setText(" ");
    			mCells[i].setClickable(true);
    		}
    	}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		unmarkErrorCells();
		// if timer is on, we should stop it here before the activity becomes inactive
		mIsTimerOnForKeepState = mIsTimerOn;
		if (isTimerOn())
			pauseGame();
		
		super.onPause();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// we keep the paused state when resume from inactive
		// but need to update the used time, the menu and hint
		showTimeUsed();
		showHint();
		switchPauseResumeMenu();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		if (sudoku != null) {
			// set game states before saveing
			int activeCell = -1;
			if (mActiveCell != null)
				activeCell = Integer.parseInt((String)mActiveCell.getTag());
			sudoku.setTimeUsed(mTimeUsed);
			sudoku.setActiveCell(activeCell);
			sudoku.setIsTimerOn(mIsTimerOnForKeepState);
			
			outState.putParcelable(GAME_STATE_SUDOKU, sudoku);
		}
		
		// always call the super class's onSaveInstanceState here
		super.onSaveInstanceState(outState);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// always call the super class's onSaveInstanceState here
		super.onRestoreInstanceState(savedInstanceState);

		// we restore the states in onCreate instead of here
	}
	
	@Override
	public void onSettingsPositiveClick(DialogFragment dialog) {
		SettingsDialogFragment settings = (SettingsDialogFragment)dialog;
		mIsAutoCheck = settings.isAutoCheck();
		mIsAutoHelp = settings.isAutoHelp();
		
		// save settings
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(APP_SETTINGS_AUTOCHECK, mIsAutoCheck);
		editor.putBoolean(APP_SETTINGS_AUTOHELP, mIsAutoHelp);
		editor.commit();
	}
	@Override
	public void onSettingsNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onNewGameButtonClick(final int which) {
		switch (which) {
		case 0:
			createSudoku(Sudoku.Level.Easy);
			break;
		case 1:
			createSudoku(Sudoku.Level.Medium);
			break;
		case 2:
			createSudoku(Sudoku.Level.Hard);
			break;
		case 3:
			createSudoku(Sudoku.Level.Tough);
			break;
		}
	}
	
	@Override
	public void onSaveFilePositiveClick(final String filename) {

		// set game states before saving
		int activeCell = -1;
		if (mActiveCell != null)
			activeCell = Integer.parseInt((String)mActiveCell.getTag());
		sudoku.setTimeUsed(mTimeUsed);
		sudoku.setActiveCell(activeCell);
		sudoku.setIsTimerOn(mIsTimerOn);

		// saving
		
		ObjectOutputStream objOutputStream = null;
		
		try {  
			objOutputStream = new ObjectOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
			objOutputStream.writeObject(sudoku); // write the class as an 'object'
			objOutputStream.flush(); // flush the stream to insure all of the information was written to 'save.bin'
		} 
		catch (Exception e) {  
			e.printStackTrace();
		}
		finally {
			if (objOutputStream != null) {
				try {
					objOutputStream.close(); // close the stream
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public void onSaveFileNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onOpenFileItemClicked(final String filename) {
		// need the user confirm to quit current game
		if (sudoku != null) {
			String msg = getResources().getString(R.string.sure_to_load_game);
	        new AlertDialog.Builder(this)
	        	.setMessage(msg)
	        	.setCancelable(false)
	        	.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// opening
						
						ObjectInputStream objInputStream = null;
						
						try {  
							objInputStream = new ObjectInputStream(openFileInput(filename));
							sudoku = (Sudoku) objInputStream.readObject(); // read the class as an 'object'
						} 
						catch (Exception e) {  
							e.printStackTrace();
						}
						finally {
							if (objInputStream != null) {
								try {
									objInputStream.close(); // close the stream
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
						// restoring
						
						if (sudoku == null) {
							String msg = getResources().getString(R.string.invalid_game, filename);
					        new AlertDialog.Builder(MainActivity.this)
					        	.setMessage(msg)
					        	.setCancelable(false)
					        	.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										File file = new File(getFilesDir(), filename);
										file.delete();
									}
								})
								.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();
							showHint();
						}
						else {
							restoreSudoku();
						}
					}
				})
				.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			showHint();
		}		
	}

	@Override
	public void onInputBestNamePositiveClick(String name) {
		if (name == null || name.length() == 0)
			name = getResources().getString(R.string.anonymous);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		// best times
		editor.putLong(APP_BEST_TIME_EASY_TIME_1, mEasyBestTimes[0]);
		editor.putLong(APP_BEST_TIME_EASY_TIME_2, mEasyBestTimes[1]);
		editor.putLong(APP_BEST_TIME_EASY_TIME_3, mEasyBestTimes[2]);
		editor.putLong(APP_BEST_TIME_MEDIUM_TIME_1, mMediumBestTimes[0]);
		editor.putLong(APP_BEST_TIME_MEDIUM_TIME_2, mMediumBestTimes[1]);
		editor.putLong(APP_BEST_TIME_MEDIUM_TIME_3, mMediumBestTimes[2]);
		editor.putLong(APP_BEST_TIME_HARD_TIME_1, mHardBestTimes[0]);
		editor.putLong(APP_BEST_TIME_HARD_TIME_2, mHardBestTimes[1]);
		editor.putLong(APP_BEST_TIME_HARD_TIME_3, mHardBestTimes[2]);
		editor.putLong(APP_BEST_TIME_TOUGH_TIME_1, mToughBestTimes[0]);
		editor.putLong(APP_BEST_TIME_TOUGH_TIME_2, mToughBestTimes[1]);
		editor.putLong(APP_BEST_TIME_TOUGH_TIME_3, mToughBestTimes[2]);
		// best names
		editor.putString(APP_BEST_TIME_EASY_NAME_1, mEasyBestNames[0]);
		editor.putString(APP_BEST_TIME_EASY_NAME_2, mEasyBestNames[1]);
		editor.putString(APP_BEST_TIME_EASY_NAME_3, mEasyBestNames[2]);
		editor.putString(APP_BEST_TIME_MEDIUM_NAME_1, mMediumBestNames[0]);
		editor.putString(APP_BEST_TIME_MEDIUM_NAME_2, mMediumBestNames[1]);
		editor.putString(APP_BEST_TIME_MEDIUM_NAME_3, mMediumBestNames[2]);
		editor.putString(APP_BEST_TIME_HARD_NAME_1, mHardBestNames[0]);
		editor.putString(APP_BEST_TIME_HARD_NAME_2, mHardBestNames[1]);
		editor.putString(APP_BEST_TIME_HARD_NAME_3, mHardBestNames[2]);
		editor.putString(APP_BEST_TIME_TOUGH_NAME_1, mToughBestNames[0]);
		editor.putString(APP_BEST_TIME_TOUGH_NAME_2, mToughBestNames[1]);
		editor.putString(APP_BEST_TIME_TOUGH_NAME_3, mToughBestNames[2]);
		// re-write most recent changes
		editor.putString(mKeyNameToKeep, name);
		editor.putLong(mKeyTimeTokeep, mTimeRecordToKeep);
		editor.commit();
		// set most recent best name value
		if (mKeyNameToKeep == APP_BEST_TIME_EASY_NAME_1)
			mEasyBestNames[0] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_EASY_NAME_2)
			mEasyBestNames[1] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_EASY_NAME_3)
			mEasyBestNames[2] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_MEDIUM_NAME_1)
			mMediumBestNames[0] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_MEDIUM_NAME_2)
			mMediumBestNames[1] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_MEDIUM_NAME_3)
			mMediumBestNames[2] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_HARD_NAME_1)
			mHardBestNames[0] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_HARD_NAME_2)
			mHardBestNames[1] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_HARD_NAME_3)
			mHardBestNames[2] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_TOUGH_NAME_1)
			mToughBestNames[0] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_TOUGH_NAME_2)
			mToughBestNames[1] = name;
		else if (mKeyNameToKeep == APP_BEST_TIME_TOUGH_NAME_3)
			mToughBestNames[2] = name;
	}
	@Override
	public void onInputBestNameNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBestNameEasyResetClick() {
		mEasyBestTimes[0] = MAX_BEST_TIME;
		mEasyBestTimes[1] = MAX_BEST_TIME;
		mEasyBestTimes[2] = MAX_BEST_TIME;
	}
	@Override
	public void onBestNameMediumResetClick() {
		mMediumBestTimes[0] = MAX_BEST_TIME;
		mMediumBestTimes[1] = MAX_BEST_TIME;
		mMediumBestTimes[2] = MAX_BEST_TIME;
	}
	@Override
	public void onBestNameHardResetClick() {
		mHardBestTimes[0] = MAX_BEST_TIME;
		mHardBestTimes[1] = MAX_BEST_TIME;
		mHardBestTimes[2] = MAX_BEST_TIME;
	}
	@Override
	public void onBestNameToughResetClick() {
		mToughBestTimes[0] = MAX_BEST_TIME;
		mToughBestTimes[1] = MAX_BEST_TIME;
		mToughBestTimes[2] = MAX_BEST_TIME;
	}
}
