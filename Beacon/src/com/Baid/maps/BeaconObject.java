package com.Baid.maps;

public class BeaconObject {

	String name;
	String start;
	String end;
	String date;
	String cat;
	double lat;
	double lng;

	public BeaconObject(String n, String s, String e, String d, String c, double lt, double ln){

		name =n;
		start=s;
		end=e;
		date=d;
		cat=c;
		lat=lt;
		lng=ln;		

	}

	public String getName(){

		return name;
	}
	public String getStart(){

		return start;
	}
	public String getEnd(){

		return end;
	}
	public String getDate(){

		return date;
	}
	public String getCat(){

		return cat;
	}
	public double getLat(){
		
		return lat;
	}
	
	public double getLong(){
		
		return lng;
	}
}
