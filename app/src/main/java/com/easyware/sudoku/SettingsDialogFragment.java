package com.easyware.sudoku;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsDialogFragment extends DialogFragment {
	
    /* The activity that creates an instance of this dialog fragment must     
	* implement this interface in order to receive event callbacks.     
	* Each method passes the DialogFragment in case the host needs to query it. 
	*/    
	public interface SettingsDialogListener {        
		public void onSettingsPositiveClick(DialogFragment dialog);        
		public void onSettingsNegativeClick(DialogFragment dialog);    
	}        
	
	// Use this instance of the interface to deliver action events    
	private SettingsDialogListener mListener;        
	
	private CheckBox mAutoCheck;
	private CheckBox mAutoHelp;
	private boolean mIsAutoCheck;
	private boolean mIsAutoHelp;
	private boolean mIsSelectAll;
	
	public boolean isAutoCheck() {
		return mAutoCheck.isChecked();
	}
	public boolean isAutoHelp() {
		return mAutoHelp.isChecked();
	}

   /**     
    * Create a new instance of MyDialogFragment, providing "autoCheck" and "autoHelp" as an argument.     
    **/    
	public static SettingsDialogFragment newInstance(boolean autoCheck, boolean autoHelp) {        
		SettingsDialogFragment f = new SettingsDialogFragment();        
		// Supply "autoCheck" and "autoHelp" input as arguments       
		Bundle args = new Bundle();        
		args.putBoolean("autoCheck", autoCheck);        
		args.putBoolean("autoHelp", autoHelp);        
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
		mIsAutoCheck = getArguments().getBoolean("autoCheck");
		mIsAutoHelp = getArguments().getBoolean("autoHelp");
	}
	
	// Override the Fragment.onAttach() method to instantiate the SettingsDialogListener    
	@Override    
	public void onAttach(Activity activity) {        
		super.onAttach(activity);        
		// Verify that the host activity implements the callback interface        
		try {            
			// Instantiate the NoticeDialogListener so we can send events to the host            
			mListener = (SettingsDialogListener) activity;        
		} 
		catch (ClassCastException e) {            
			// The activity doesn't implement the interface, throw exception            
			throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener");        
		}    
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {	
		// get the host activity
		Activity activity = getActivity();
		// Get the layout inflater
		LayoutInflater inflater = activity.getLayoutInflater();

		// inflate the view
		// Inflate and set the layout for the dialog    
		// Pass null as the parent view because its going in the dialog layout    
		View view = inflater.inflate(R.layout.settings, null);
		
		// dialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(view)
			   .setTitle(R.string.settings)
				
			   // Add action buttons           
				
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {               
			    	
			    	@Override               
			    	public void onClick(DialogInterface dialog, int id) {     
			    		// Send the positive button event back to the host activity
			    		mListener.onSettingsPositiveClick(SettingsDialogFragment.this);
			    	}           
		    	})           
		    	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {               

		    		@Override               
		    		public void onClick(DialogInterface dialog, int id) {  
		    			// Send the negative button event back to the host activity
			    		mListener.onSettingsNegativeClick(SettingsDialogFragment.this);
		    		}           
	    		});          
    
		mAutoCheck = (CheckBox)view.findViewById(R.id.cbAutoCheck);
		mAutoHelp = (CheckBox)view.findViewById(R.id.cbAutoHelp);
		mAutoCheck.setChecked(mIsAutoCheck);
		mAutoHelp.setChecked(mIsAutoHelp);

		// manage saved games
		
		// get all internal saved game files
		String[] filenames = activity.fileList();
		final ArrayList<String> fileList = new ArrayList<String>();
		for (String fn : filenames)
			fileList.add(fn);
		
		final TextView tvSaveGames = (TextView)view.findViewById(R.id.tvSavedGames);
		final Button btnRemove = (Button)view.findViewById(R.id.btnRemove);
		final Button btnSelectAll = (Button)view.findViewById(R.id.btnSelectAll);
		mIsSelectAll = true;
		
		final ListView lvSavedGames = (ListView)view.findViewById(R.id.lvSavedGames);

		if (fileList.size() == 0) {
			btnRemove.setVisibility(View.GONE);
			btnSelectAll.setVisibility(View.GONE);
			lvSavedGames.setVisibility(View.GONE);
			tvSaveGames.setVisibility(View.GONE);
		}
		else {
			// list events
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_multiple_choice, fileList);
			lvSavedGames.setAdapter(adapter);
	        lvSavedGames.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        
	        // button remove events
	        btnRemove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ArrayList<String> deleteList = new ArrayList<String>();
					int count = lvSavedGames.getAdapter().getCount();
					for (int i=0; i<count; ++i) {
						if (lvSavedGames.isItemChecked(i)) {
							deleteList.add(fileList.get(i));
						}
					}
					for (String fn : deleteList) {
						removeFile(fn);
						fileList.remove(fn);
					}
					adapter.notifyDataSetChanged();

					if (fileList.size() == 0) {
						btnRemove.setVisibility(View.GONE);
						btnSelectAll.setVisibility(View.GONE);
						lvSavedGames.setVisibility(View.GONE);
						tvSaveGames.setVisibility(View.GONE);
					}
				}
	        });
	        
	        // button select all
	        btnSelectAll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int count = lvSavedGames.getAdapter().getCount();
					for (int i=0; i<count; ++i) {
						lvSavedGames.setItemChecked(i, mIsSelectAll);
					}
					
					mIsSelectAll = !mIsSelectAll;
					btnSelectAll.setText(mIsSelectAll ? R.string.button_select_all : R.string.button_deselect_all);
				}
	        });
		}
		
		return builder.create();
    }
	
	public void removeFile(String filename) {
		File file = new File(getActivity().getFilesDir(), filename);
		file.delete();
	}
}
