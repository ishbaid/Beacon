package com.Baid.maps;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Splash extends Activity {
	
	
	Button menu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		menu = (Button)findViewById(R.id.button1);
		
		menu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Class ourClass;
				try {
					ourClass = Class.forName("com.Baid.maps.MainActivity");
					Intent ourIntent= new Intent(Splash.this, ourClass);
					startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		
	}
	
	
	

}
