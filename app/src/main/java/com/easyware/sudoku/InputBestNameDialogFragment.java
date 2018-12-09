package com.easyware.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.widget.EditText;

public class InputBestNameDialogFragment extends DialogFragment {

	private EditText input;
	private String mLevel;
	private String mOrder;
	
	/* The activity that creates an instance of this dialog fragment must     
	* implement this interface in order to receive event callbacks.     
	* Each method passes the DialogFragment in case the host needs to query it. 
	*/    
	public interface InputBestNameDialogListener {        
		public void onInputBestNamePositiveClick(final String name);        
		public void onInputBestNameNegativeClick(DialogFragment dialog);    
	}        
	
	// Use this instance of the interface to deliver action events    
	private InputBestNameDialogListener mListener;       
	
	public Editable getInput() {
		return input.getText();
	}
		
   /**     
    * Create a new instance of MyDialogFragment, providing "level" and "order" as an argument.     
    **/    
	public static InputBestNameDialogFragment newInstance(String level, String order) {        
		InputBestNameDialogFragment f = new InputBestNameDialogFragment();        
		// Supply "level" and "order" input as arguments        
		Bundle args = new Bundle();        
		args.putString("level", level);        
		args.putString("order", order);        
		f.setArguments(args);        
		return f;    
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get arguments
		mLevel = getArguments().getString("level");
		mOrder = getArguments().getString("order");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);        
		// Verify that the host activity implements the callback interface        
		try {            
			// Instantiate the NoticeDialogListener so we can send events to the host            
			mListener = (InputBestNameDialogListener) activity;        
		} 
		catch (ClassCastException e) {            
			// The activity doesn't implement the interface, throw exception            
			throw new ClassCastException(activity.toString() + " must implement InputBestNameDialogListener");        
		}    
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// get activity
		final Activity activity = getActivity();
		// create the EditText control 
		input = new EditText(activity);  
		// dialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.input_name_for_record_title)
			   .setMessage(getResources().getString(R.string.input_name_for_record_msg, mOrder, mLevel))
			   .setView(input)
			   
			   // set action buttons
			   // positive button onClickListener will be override in setOnShowListener later
			   // here only set negative button behavior
			    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
		    			// Send the positive button event back to the host activity
			    		mListener.onInputBestNamePositiveClick(getInput().toString());
					}
				})
		    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {               

		    		@Override               
		    		public void onClick(DialogInterface dialog, int id) {  
		    			// Send the negative button event back to the host activity
			    		mListener.onInputBestNameNegativeClick(InputBestNameDialogFragment.this);
		    		}           
	    		});          
    
		return builder.create();
	}
}
