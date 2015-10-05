package com.aig.science.damagedetection.controllers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aig.science.damagedetection.R;

public class InputRegistrationCodeActivity extends Activity implements OnClickListener {

	private View focusView = null;
	private EditText inputRegCodeEditText;
	private Button submitBtn, cancelBtn;
	private String inputRegCodeStr, userIdStr,userNameStr,uniqueCode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_registration_code_screen);
		Intent intent = getIntent();
		if(intent!=null)
		{
			userIdStr = (String) intent.getSerializableExtra("USER_ID");
			userNameStr = (String) intent.getSerializableExtra("EMAIL_ID");
			uniqueCode = (String) intent.getSerializableExtra("UNIQUEID");
		}
		initUI();
	}
	
	private void initUI() {
		inputRegCodeEditText = (EditText) findViewById(R.id.inputRegCodeEditText);
		submitBtn = (Button) findViewById(R.id.submitBtnID);
		cancelBtn = (Button) findViewById(R.id.cancelBtnID);
		submitBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		
	}
	

	/**
	 * This method displays the dialog box with the title and message
	 * 
	 * @param title
	 * @param message
	 */
	public void showDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	
	@Override
	public void onClick(View v) {
		if(v==submitBtn)
		{
			if(validInputRegCode())
			{
				Intent main_Intent = new Intent(InputRegistrationCodeActivity.this,SignUpActivity.class);
				main_Intent.putExtra("USER_ID", userIdStr);
				main_Intent.putExtra("EMAIL_ID", userNameStr);
				startActivity(main_Intent);
			}else
			{
				inputRegCodeEditText.setError(getString(R.string.error_incorrect_input_code));
				focusView = inputRegCodeEditText;
			}
		}
		if(v==cancelBtn)
		{
			this.finish();
		}
	}

	private boolean validInputRegCode() {
		inputRegCodeStr = inputRegCodeEditText.getText().toString();
		if (inputRegCodeStr!=null && inputRegCodeStr.equals(uniqueCode)) {
			return true;
		}
		return false;
	}

}
