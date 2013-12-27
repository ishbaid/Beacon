package com.Baid.maps;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements LocationListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener {

	TabHost th;
	GoogleMap googlemap;
	ArrayList<BeaconObject>record;
	
	String reg="This android application, that is supplemented by a web application, allows the user to connect with events occurring in approximately 70 mile radius of their location."
			+"\n\n Whether it is a pickup ultimate frisbee game, a party right around the block, or a lecture by a technological genius, Beacon allows you to discover events happening close to you that you may have never heard about by allowing you to view posted events by other people and by allowing you to send out \"Beacons\" of your own. "
			+"\n\n This app is a map-oriented application powered by the Google maps API and the Parse API."
			+"\n\n It allows you to view various \"Beacons\" as well get directions to the individual events."
			+"\n\n By holding any location on the map, you can create a marker."
			+"\n\n By tapping the marker, you can create a Beacon, that is availible for your community to see."
			+"\n\n To get directions to a Beacon event, go to the list tab, and select the event."
			+"\n\n Hit the \"Get Directions\" button to be directed to the google map navigation system.";
	TextView info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Parse.initialize(this, "69ruC8huA7IrQlYaHg7WVQG5c3KVmWGMDY0eBe9C", "1OVPSuX0SPMaD5DcsbnacuXZBJPJnXKnCqsgxA2b");
		super.onCreate(savedInstanceState);

		if(isGooglePlay()){
			setContentView(R.layout.main);
			setUp();
			setUpMaps();
			load();
		}

	}

	private void setUp(){


		th= (TabHost)findViewById(R.id.tabhost);
		th.setup();

		//first tab
		TabSpec specs=th.newTabSpec("tag1");
		specs.setContent(R.id.tab1);
		specs.setIndicator("Map");
		th.addTab(specs);

		//second tab
		TabSpec specs2=th.newTabSpec("tag2");
		specs2.setContent(R.id.tab2);
		specs2.setIndicator("List");
		th.addTab(specs2);

		//third tab
		TabSpec specs3=th.newTabSpec("tag3");
		specs3.setContent(R.id.tab3);
		specs3.setIndicator("About");
		th.addTab(specs3);

		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
		
		lay = (LinearLayout)findViewById(R.id.back);
		record = new ArrayList<BeaconObject>();
		
		info=(TextView)findViewById(R.id.info);
		info.setText(reg);
	}

	private boolean isGooglePlay(){

		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if(status == ConnectionResult.SUCCESS)
			return true;
		else
			Toast.makeText(this, "Google paly Services is not availible", Toast.LENGTH_SHORT).show();
		return false;


	}

	private void setUpMaps(){

		if(googlemap == null){
			FragmentManager frgm=getSupportFragmentManager();
			//Fragment mfrg = frgm.findFragmentById(R.id.map);
			//getSupportFragmentManager();
			
			
			googlemap = ((SupportMapFragment)frgm.findFragmentById(R.id.map)).getMap();
			
			if(googlemap != null){

				googlemap.setMyLocationEnabled(true);
				LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);

				String provider = lm.getBestProvider(new Criteria(), true);
				if(provider == null){

					onProviderDisabled(provider);
				}
				Location loc = lm.getLastKnownLocation(provider);
				if(loc !=null){

					onLocationChanged(loc);
				}

				googlemap.setOnMapLongClickListener(onLongClickMapSettings());
				googlemap.setOnMarkerClickListener(markerListerner());
				googlemap.setOnInfoWindowClickListener(this);
				googlemap.setOnMyLocationButtonClickListener(this);
				test =googlemap.addMarker(new MarkerOptions()
				.position(new LatLng(0, 0))
				.title("Hello world"));

			}



		}
	}
	Marker test;


	int sHr;
	int sMin;
	int eHr;
	int eMin;



	private OnMarkerClickListener markerListerner() {
		// TODO Auto-generated method stub

		return new GoogleMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker beacon1) {
				// TODO Auto-generated method stub
				if(beacon1.getTitle().equals("New Marker")){
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which){
							case DialogInterface.BUTTON_POSITIVE:
								//Yes button clicked



								LayoutInflater li = LayoutInflater.from(MainActivity.this);
								View promptsView = li.inflate(R.layout.prompt, null);


								AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

								alert.setTitle("Beacon Data");
								alert.setMessage("Enter information:");

								alert.setView(promptsView);

								// Set an EditText view to get user input 
								final EditText inputName = (EditText)promptsView.findViewById(R.id.editText1);
								final EditText inputCat= (EditText)promptsView.findViewById(R.id.editText4);
								final EditText inputStart =(EditText)promptsView.findViewById(R.id.editText2);
								final EditText inputEnd =(EditText)promptsView.findViewById(R.id.editText3);
								final EditText inputDate =(EditText)promptsView.findViewById(R.id.editText5);
								alert
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										// get user input and set it to result
										// edit text
										String name="";	
										String cat="";
										String start="";
										String end="";
										String date="";
										
										boolean valid=!inputName.getText().toString().equals("") && !inputName.getText().toString().equals("") &&!inputStart.getText().toString().equals("") && !inputEnd.getText().toString().equals("") && !inputDate.getText().toString().equals("");
										
										
										//If there is ever a problem with null pointers, it could be very likely here
										if(valid){

											name = inputName.getText().toString();
											cat= inputCat.getText().toString();
											start =inputStart.getText().toString();
											end= inputEnd.getText().toString();
											date=inputDate.getText().toString();
											
											beacon1.setTitle(name);
										
											LatLng becLoc=beacon1.getPosition();
											double lat=becLoc.latitude;
											double lng= becLoc.longitude;

											//creates beacon object
											ParseObject beacon = new ParseObject("Beacon");
											beacon.put("Name", name);
											beacon.put("StartTime", start);
											beacon.put("EndTime", end);
											beacon.put("Category", cat);
											beacon.put("Date", date);
											beacon.put("Latitude", lat);
											beacon.put("Longitude", lng);
											beacon.saveInBackground();
											
											BeaconObject item = new BeaconObject(name, start, end, date, cat, lat, lng);
											record.add(item);
											
											
										}




										else{

											AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();  
											alertDialog.setTitle("Invalid Entry ");  
											alertDialog.setMessage("Try Again");
											alertDialog.show(); 
										}

									}
								})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										dialog.cancel();
										beacon1.remove();
									}
								});

								// create alert dialog
								AlertDialog alertDialog = alert.create();

								// show it
								alertDialog.show();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								//No button clicked
								
								beacon1.remove();
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage("Create Beacon?").setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();


					return false;

				}
				
				else{
					
					
					return false;
				}
			}
		};
	}


	private OnMapLongClickListener onLongClickMapSettings() {
		// TODO Auto-generated method stub
		return new GoogleMap.OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng tapLoc) {
				// TODO Auto-generated method stub

				googlemap.addMarker(new MarkerOptions()
				.position(tapLoc)
				.title("New Marker"));

			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		if(item.getItemId()==R.id.legalnot){

			startActivity(new Intent(this, LegalNoticesActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}
	Location myLoc;
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		myLoc=location;
		LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
		googlemap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
		googlemap.animateCamera(CameraUpdateFactory.zoomTo(10));

	}

	public void onProviderDisabled (String provider){


	}

	
	protected void load() {
		// TODO Auto-generated method stub
		super.onStart();
		if(myLoc!=null){


			final double latmin = myLoc.getLatitude()-.5;
			final double latmax = myLoc.getLatitude()+.5;
			final double longmin= myLoc.getLongitude()-.5;
			final double longmax= myLoc.getLongitude()+.5;

			ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacon");
			query.whereGreaterThan("Latitude", latmin);
			query.whereLessThan("Latitude", latmax);
			query.whereGreaterThan("Longitude", longmin);
			query.whereLessThan("Longitude", longmax);
			query.findInBackground(new FindCallback<ParseObject>() {

				public void done(List<ParseObject> list, ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						
						for(int i =0; i<list.size();i++){

							ParseObject test = list.get(i);
							double testLat = test.getDouble("Latitude");
							double testLong = test.getDouble("Longitude");
							String eventName = test.getString("Name");
							String sTime= test.getString("StartTime");
							String eTime = test.getString("EndTime");
							String catName=test.getString("Category");
							String date= test.getString("Date");
							
							Button bInfo = new Button(MainActivity.this);
							bInfo.setText(eventName);
							bInfo.setId(i);
							bInfo.setOnClickListener(MainActivity.this);
							bInfo.setBackgroundColor(Color.BLACK);
							bInfo.setTextColor(Color.WHITE);
							lay.addView(bInfo);
							
							
							
							LatLng testLoc = new LatLng(testLat,testLong);
							googlemap.addMarker(new MarkerOptions()
							.position(testLoc)
							.title(eventName+"|\n"+date+"|\n"+sTime+"-"+eTime+"|\n"+catName));
							
							BeaconObject item = new BeaconObject(eventName, sTime, eTime, date, catName, testLat, testLong);
							record.add(item);

						}

					} else {
						
						AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();  
						alertDialog.setTitle("Alert ");  
						alertDialog.setMessage("An error occured");
						alertDialog.show(); 

					}
					
				}
			});		

		}
		
	}

	LinearLayout lay;

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		String title = arg0.getTitle();
		if(!title.equals("New Marker")){
			
			int loc =title.indexOf("|");
			String name="", date="", time="", cat="";
			if(loc!=-1)
				name = title.substring(0, loc);
			title=title.substring(loc+1);
			loc =title.indexOf("|");
			if(loc!=-1)
				date = title.substring(0, loc);
			title=title.substring(loc+1);
			loc =title.indexOf("|");
			if(loc!=-1)
				time= title.substring(0, loc);
			title=title.substring(loc+1);
			cat= title;
			
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
			alertDialog.setTitle("Event Info ");  
			alertDialog.setMessage(name+"\n"+date+"\n"+time+"\n"+cat);
			alertDialog.show(); 
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int num= v.getId();
		final BeaconObject obj = record.get(num);
		String name=obj.getName();
		String date = obj.getDate();
		String time = obj.getStart()+"-"+obj.getEnd();
		String cat = obj.getCat();
		
				
		//ADVANCED EVENT DIALOG
		LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View promptsView = li.inflate(R.layout.event, null);


		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

		alert.setTitle("Event Info");
		

		alert.setView(promptsView);

		// Set an EditText view to get user input 
		final TextView nameLab = (TextView)promptsView.findViewById(R.id.textView1);
		final TextView dateLab= (TextView)promptsView.findViewById(R.id.textView2);
		final TextView timeLab =(TextView)promptsView.findViewById(R.id.textView3);
		final TextView catLab =(TextView)promptsView.findViewById(R.id.textView4);
		final TextView latLab =(TextView)promptsView.findViewById(R.id.textView5);
		final TextView longLab =(TextView)promptsView.findViewById(R.id.textView6);
		final Button getDir = (Button)promptsView.findViewById(R.id.button1);
		
		getDir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ myLoc.getLatitude() + "," + myLoc.getLongitude() + "&daddr=" + obj.getLat() + "," + obj.getLong()));
			    intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
			                        startActivity(intent);
				
			}
		});
		
		nameLab.setText(name);
		dateLab.setText(date);
		timeLab.setText(time);
		catLab.setText(cat);
		latLab.setText("Latitude: "+ obj.getLat());
		longLab.setText("Longitude: "+obj.getLong());
		alert
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
				}
				

			});
		
		// create alert dialog
		AlertDialog alertDialog2 = alert.create();

		// show it
		alertDialog2.show();
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		
		double lat2=myLoc.getLatitude();
		double lng2=myLoc.getLongitude();
		LatLng mySpot= new LatLng(lat2, lng2);
		googlemap.addMarker(new MarkerOptions()
		.position(mySpot)
		.title("New Marker"));
		return false;
	}

	





}
