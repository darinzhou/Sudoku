package com.easyware.sudoku;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BestTimesDialogFragment extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must     
	* implement this interface in order to receive event callbacks.     
	* Each method passes the DialogFragment in case the host needs to query it. 
	*/    
	public interface BestTimesDialogFragmentListener {        
		public void onBestNameEasyResetClick();        
		public void onBestNameMediumResetClick();        
		public void onBestNameHardResetClick();      
		public void onBestNameToughResetClick();
	}        
	
	// Use this instance of the interface to deliver action events    
	private BestTimesDialogFragmentListener mListener;       

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);        
		// Verify that the host activity implements the callback interface        
		try {            
			// Instantiate the NoticeDialogListener so we can send events to the host            
			mListener = (BestTimesDialogFragmentListener) activity;        
		} 
		catch (ClassCastException e) {            
			// The activity doesn't implement the interface, throw exception            
			throw new ClassCastException(activity.toString() + " must implement BestTimesDialogFragmentListener");        
		}    
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	     getDialog().setTitle(getString(R.string.best_times));
	     View view = inflater.inflate(R.layout.best_times, container, false);
	     
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		// best times
		long easyBestTime1 = preferences.getLong(MainActivity.APP_BEST_TIME_EASY_TIME_1, MainActivity.MAX_BEST_TIME);
		long easyBestTime2 = preferences.getLong(MainActivity.APP_BEST_TIME_EASY_TIME_2, MainActivity.MAX_BEST_TIME);
		long easyBestTime3 = preferences.getLong(MainActivity.APP_BEST_TIME_EASY_TIME_3, MainActivity.MAX_BEST_TIME);
		long mediumBestTime1 = preferences.getLong(MainActivity.APP_BEST_TIME_MEDIUM_TIME_1, MainActivity.MAX_BEST_TIME);
		long mediumBestTime2 = preferences.getLong(MainActivity.APP_BEST_TIME_MEDIUM_TIME_2, MainActivity.MAX_BEST_TIME);
		long mediumBestTime3 = preferences.getLong(MainActivity.APP_BEST_TIME_MEDIUM_TIME_3, MainActivity.MAX_BEST_TIME);
		long hardBestTime1 = preferences.getLong(MainActivity.APP_BEST_TIME_HARD_TIME_1, MainActivity.MAX_BEST_TIME);
		long hardBestTime2 = preferences.getLong(MainActivity.APP_BEST_TIME_HARD_TIME_2, MainActivity.MAX_BEST_TIME);
		long hardBestTime3 = preferences.getLong(MainActivity.APP_BEST_TIME_HARD_TIME_3, MainActivity.MAX_BEST_TIME);
		long toughBestTime1 = preferences.getLong(MainActivity.APP_BEST_TIME_TOUGH_TIME_1, MainActivity.MAX_BEST_TIME);
		long toughBestTime2 = preferences.getLong(MainActivity.APP_BEST_TIME_TOUGH_TIME_2, MainActivity.MAX_BEST_TIME);
		long toughBestTime3 = preferences.getLong(MainActivity.APP_BEST_TIME_TOUGH_TIME_3, MainActivity.MAX_BEST_TIME);

		final String defaultName = getResources().getString(R.string.anonymous);
		String easyBestName1 = preferences.getString(MainActivity.APP_BEST_TIME_EASY_NAME_1, defaultName);
		String easyBestName2 = preferences.getString(MainActivity.APP_BEST_TIME_EASY_NAME_2, defaultName);
		String easyBestName3 = preferences.getString(MainActivity.APP_BEST_TIME_EASY_NAME_3, defaultName);
		String mediumBestName1 = preferences.getString(MainActivity.APP_BEST_TIME_MEDIUM_NAME_1, defaultName);
		String mediumBestName2 = preferences.getString(MainActivity.APP_BEST_TIME_MEDIUM_NAME_2, defaultName);
		String mediumBestName3 = preferences.getString(MainActivity.APP_BEST_TIME_MEDIUM_NAME_3, defaultName);
		String hardBestName1 = preferences.getString(MainActivity.APP_BEST_TIME_HARD_NAME_1, defaultName);
		String hardBestName2 = preferences.getString(MainActivity.APP_BEST_TIME_HARD_NAME_2, defaultName);
		String hardBestName3 = preferences.getString(MainActivity.APP_BEST_TIME_HARD_NAME_3, defaultName);
		String toughBestName1 = preferences.getString(MainActivity.APP_BEST_TIME_TOUGH_NAME_1, defaultName);
		String toughBestName2 = preferences.getString(MainActivity.APP_BEST_TIME_TOUGH_NAME_2, defaultName);
		String toughBestName3 = preferences.getString(MainActivity.APP_BEST_TIME_TOUGH_NAME_3, defaultName);

		final TextView tvEasy1 = (TextView)view.findViewById(R.id.tvEasy_1);
		final TextView tvEasy2 = (TextView)view.findViewById(R.id.tvEasy_2);
		final TextView tvEasy3 = (TextView)view.findViewById(R.id.tvEasy_3);
		final TextView tvMedium1 = (TextView)view.findViewById(R.id.tvMedium_1);
		final TextView tvMedium2 = (TextView)view.findViewById(R.id.tvMedium_2);
		final TextView tvMedium3 = (TextView)view.findViewById(R.id.tvMedium_3);
		final TextView tvHard1 = (TextView)view.findViewById(R.id.tvHard_1);
		final TextView tvHard2 = (TextView)view.findViewById(R.id.tvHard_2);
		final TextView tvHard3 = (TextView)view.findViewById(R.id.tvHard_3);
		final TextView tvTough1 = (TextView)view.findViewById(R.id.tvTough_1);
		final TextView tvTough2 = (TextView)view.findViewById(R.id.tvTough_2);
		final TextView tvTough3 = (TextView)view.findViewById(R.id.tvTough_3);

		final Button btnResetEasy = (Button)view.findViewById(R.id.btnRestEasy);
		final Button btnResetMedium = (Button)view.findViewById(R.id.btnRestMedium);
		final Button btnResetHard = (Button)view.findViewById(R.id.btnRestHard);
		final Button btnResetTough = (Button)view.findViewById(R.id.btnRestTough);

		Boolean b1 = showTextView(tvEasy1, R.string.easy_1, easyBestTime1, easyBestName1);
		Boolean b2 = showTextView(tvEasy2, R.string.easy_2, easyBestTime2, easyBestName2);
		Boolean b3 = showTextView(tvEasy3, R.string.easy_3, easyBestTime3, easyBestName3);
		if (!b1 && !b2 && !b3) {
			btnResetEasy.setVisibility(View.GONE);
			tvEasy1.setText(getResources().getString(R.string.no_records));
			tvEasy1.setVisibility(View.VISIBLE);
		}
		else {
			btnResetEasy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.remove(MainActivity.APP_BEST_TIME_EASY_TIME_1);
					editor.remove(MainActivity.APP_BEST_TIME_EASY_TIME_2);
					editor.remove(MainActivity.APP_BEST_TIME_EASY_TIME_3);
					editor.remove(MainActivity.APP_BEST_TIME_EASY_NAME_1);
					editor.remove(MainActivity.APP_BEST_TIME_EASY_NAME_2);
					editor.remove(MainActivity.APP_BEST_TIME_EASY_NAME_3);
					editor.commit();
					
					tvEasy1.setText(getResources().getString(R.string.no_records));
					tvEasy1.setVisibility(View.VISIBLE);
					tvEasy2.setVisibility(View.GONE);
					tvEasy3.setVisibility(View.GONE);
					btnResetEasy.setVisibility(View.GONE);
					
	    			// Send the event back to the host activity
					mListener.onBestNameEasyResetClick();
				}
				
			});
		}
		
		b1 = showTextView(tvMedium1, R.string.medium_1, mediumBestTime1, mediumBestName1);
		b2 = showTextView(tvMedium2, R.string.medium_2, mediumBestTime2, mediumBestName2);
		b3 = showTextView(tvMedium3, R.string.medium_3, mediumBestTime3, mediumBestName3);
		if (!b1 && !b2 && !b3) {
			btnResetMedium.setVisibility(View.GONE);
			tvMedium1.setText(getResources().getString(R.string.no_records));
			tvMedium1.setVisibility(View.VISIBLE);
		}
		else {
			btnResetMedium.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.remove(MainActivity.APP_BEST_TIME_MEDIUM_TIME_1);
					editor.remove(MainActivity.APP_BEST_TIME_MEDIUM_TIME_2);
					editor.remove(MainActivity.APP_BEST_TIME_MEDIUM_TIME_3);
					editor.remove(MainActivity.APP_BEST_TIME_MEDIUM_NAME_1);
					editor.remove(MainActivity.APP_BEST_TIME_MEDIUM_NAME_2);
					editor.remove(MainActivity.APP_BEST_TIME_MEDIUM_NAME_3);
					editor.commit();
					
					tvMedium1.setText(getResources().getString(R.string.no_records));
					tvMedium1.setVisibility(View.VISIBLE);
					tvMedium2.setVisibility(View.GONE);
					tvMedium3.setVisibility(View.GONE);
					btnResetMedium.setVisibility(View.GONE);

					// Send the event back to the host activity
					mListener.onBestNameMediumResetClick();
				}
				
			});
		}

		b1 = showTextView(tvHard1, R.string.hard_1, hardBestTime1, hardBestName1);
		b2 = showTextView(tvHard2, R.string.hard_2, hardBestTime2, hardBestName2);
		b3 = showTextView(tvHard3, R.string.hard_3, hardBestTime3, hardBestName3);
		if (!b1 && !b2 && !b3) {
			btnResetHard.setVisibility(View.GONE);
			tvHard1.setText(getResources().getString(R.string.no_records));
			tvHard1.setVisibility(View.VISIBLE);
		}
		else {
			btnResetHard.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.remove(MainActivity.APP_BEST_TIME_HARD_TIME_1);
					editor.remove(MainActivity.APP_BEST_TIME_HARD_TIME_2);
					editor.remove(MainActivity.APP_BEST_TIME_HARD_TIME_3);
					editor.remove(MainActivity.APP_BEST_TIME_HARD_NAME_1);
					editor.remove(MainActivity.APP_BEST_TIME_HARD_NAME_2);
					editor.remove(MainActivity.APP_BEST_TIME_HARD_NAME_3);
					editor.commit();
					
					tvHard1.setText(getResources().getString(R.string.no_records));
					tvHard1.setVisibility(View.VISIBLE);
					tvHard2.setVisibility(View.GONE);
					tvHard3.setVisibility(View.GONE);
					btnResetHard.setVisibility(View.GONE);

					// Send the event back to the host activity
					mListener.onBestNameHardResetClick();
				}
				
			});
		}

		b1 = showTextView(tvTough1, R.string.tough_1, toughBestTime1, toughBestName1);
		b2 = showTextView(tvTough2, R.string.tough_2, toughBestTime2, toughBestName2);
		b3 = showTextView(tvTough3, R.string.tough_3, toughBestTime3, toughBestName3);
		if (!b1 && !b2 && !b3) {
			btnResetTough.setVisibility(View.GONE);
			tvTough1.setText(getResources().getString(R.string.no_records));
			tvTough1.setVisibility(View.VISIBLE);
		}
		else {
			btnResetTough.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.remove(MainActivity.APP_BEST_TIME_TOUGH_TIME_1);
					editor.remove(MainActivity.APP_BEST_TIME_TOUGH_TIME_2);
					editor.remove(MainActivity.APP_BEST_TIME_TOUGH_TIME_3);
					editor.remove(MainActivity.APP_BEST_TIME_TOUGH_NAME_1);
					editor.remove(MainActivity.APP_BEST_TIME_TOUGH_NAME_2);
					editor.remove(MainActivity.APP_BEST_TIME_TOUGH_NAME_3);
					editor.commit();
					
					tvTough1.setText(getResources().getString(R.string.no_records));
					tvTough1.setVisibility(View.VISIBLE);
					tvTough2.setVisibility(View.GONE);
					tvTough3.setVisibility(View.GONE);
					btnResetTough.setVisibility(View.GONE);

					// Send the event back to the host activity
					mListener.onBestNameToughResetClick();
				}
				
			});
		}

		return view;
	}

	private boolean showTextView(TextView tv, int resId, long bestTime, String bestName) {
		if (bestTime == MainActivity.MAX_BEST_TIME) {
			tv.setVisibility(View.GONE);
			return false;
		}

		tv.setVisibility(View.VISIBLE);
		tv.setText(getResources().getString(resId, MainActivity.getUsedTimeString(bestTime), bestName));
		return true;
	}
}
