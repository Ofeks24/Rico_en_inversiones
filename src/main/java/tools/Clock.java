package tools;

import javax.swing.Timer;

public class Clock {
	private Date date;
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
		
		this.date= new Date(1,6,1996);
	}

	public Clock() {
		minute=0;
		hour=8;
		date= new Date(1,6,1996);
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
	
	

	public Date getDate() {
		return date;
	}
	public int getHour() {
		return hour;
	}
	public int getMinute() {
		return minute;
	}
	
	
	
	private void sumarTiempo() {
		minute++;
		if(minute>59) {
			minute=0;
			hour++;
			if(hour>23) {
				hour=0;
				date.sumDay();
				if(date.getDay()>Utils.diasDelMes(date.getMonth(),date.getYear())) {
					date.setDay(1);
					date.sumMonth();
					if(date.getMonth()>12) {
						date.setMonth(1);
						date.sumYear();
					}
				}
			}
		}
		listeners.forEach(Runnable::run);
	}
	
	
	
	public void setDate(Date date) {
		this.date = date;
	}
	public void setHour(int hour) {
		if(hour<24&&hour>=0) this.hour=hour;
		
	}
	public void setMinute(int minute) {
		if(minute<60&&minute>=0) this.minute = minute;
	}
	
	

	public void addListener(Runnable r) {
	    listeners.add(r);
	}
}
