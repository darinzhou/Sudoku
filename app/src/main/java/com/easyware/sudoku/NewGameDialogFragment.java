package com.easyware.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class NewGameDialogFragment extends DialogFragment {
    
	/* The activity that creates an instance of this dialog fragment must     
	* implement this interface in order to receive event callbacks.     
	* Each method passes the DialogFragment in case the host needs to query it. 
	*/    
	public interface NewGameDialogListener {        
		public void onNewGameButtonClick(final int which);        
	}        
	
	// Use this instance of the interface to deliver action events    
	private NewGameDialogListener mListener;        
	
	// Override the Fragment.onAttach() method to instantiate the NewGameDialogListener    
	@Override    
	public void onAttach(Activity activity) {        
		super.onAttach(activity);        
		// Verify that the host activity implements the callback interface        
		try {            
			// Instantiate the NoticeDialogListener so we can send events to the host            
			mListener = (NewGameDialogListener) activity;        
		} 
		catch (ClassCastException e) {            
			// The activity doesn't implement the interface, throw exception            
			throw new ClassCastException(activity.toString() + " must implement NewGameDialogListener");        
		}   		
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	     getDialog().setTitle(getString(R.string.new_game));
	     View view = inflater.inflate(R.layout.new_game, container, false);
	     
		// buttons
		
		final Button btnEasy = (Button) view.findViewById(R.id.btnEasy);
		btnEasy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onNewGameButtonClick(0);
				dismiss();
			}
		});
		
		final Button btnMedium = (Button) view.findViewById(R.id.btnMedium);
		btnMedium.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!MainActivity.showFreeVersionNotice(getActivity())) {
					mListener.onNewGameButtonClick(1);
					dismiss();
				}
			}
		});

		final Button btnHard = (Button) view.findViewById(R.id.btnHard);
		btnHard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!MainActivity.showFreeVersionNotice(getActivity())) {
					mListener.onNewGameButtonClick(2);
					dismiss();
				}
			}
		});

		final Button btnTough = (Button) view.findViewById(R.id.btnTough);
		btnTough.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!MainActivity.showFreeVersionNotice(getActivity())) {
					mListener.onNewGameButtonClick(3);
					dismiss();
				}
			}
		});

		return view;
	}

}
