package tools;

import javax.swing.Timer;

public class Clock {
	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	public Timer clock;
	private java.util.List<Runnable> listeners = new java.util.ArrayList<>();
	
	public Clock(int hour,int minute) {
		if(hour<=24&&hour>=0) {
			this.hour=hour;
		}
		if(minute<=59&&minute>=0) {
			this.minute=minute;
		}
		
		day=1;
		month=6;
		year=1996;
	}
	
	public Clock() {
		minute=0;
		hour=8;
		day=1;
		month=6;
		year=1996;
	}
	
	
	public void initClock(int time) {
		clock= new Timer(time,e->{
			sumarTiempo();
		});
		clock.start();
	}
	
	public void detener() {
		clock.stop();
	}


	public int getDay() {
		return day;
	}


	public int getMonth() {
		return month;
	}


	public int getYear() {
		return year;
	}


	public int getHour() {
		return hour;
	}


	public int getMinute() {
		return minute;
	}
	
	private void sumarTiempo() {
		this.minute++;
		if(this.minute>59) {
			this.minute=0;
			this.hour++;
			if(this.hour>23) {
				this.hour=0;
				this.day++;
				if(this.day>Utils.diasDelMes(this.month,this.year)) {
					this.day=1;
					this.month++;
					if(this.month>12) {
						this.month=1;
						this.year++;
					}
				}
			}
		}
		listeners.forEach(Runnable::run);
	}
	
	public void setHour(int hour) {
		if(hour<24&&hour>=0) {
			this.hour=hour;
		}
	}
	

	public void addListener(Runnable r) {
	    listeners.add(r);
	}
}
