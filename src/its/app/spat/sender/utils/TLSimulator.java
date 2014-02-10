package its.app.spat.sender.utils;

public class TLSimulator extends Thread {

	long GREEN = 0x00000001;
	long YELLOW = 0x00000002;
	long RED = 0x00000004;
	long timeTL;

	private long color;


	public TLSimulator() {
	}

	public long getColor() {
		return color;
	}

	public void setColor(long color) {
		this.color = color;
	}

	public long getTime() {
		long TLTime = timeTL;
		timeTL = timeTL - 20;
		if (timeTL < 0) {
			this.changeColor();
			if (color == GREEN) {
				timeTL = 180;
			}
			if (color == RED) {
				timeTL = 180;
			}
			if (color == YELLOW) {
				timeTL = 60;
			}
		}

		return TLTime;
	}

	public void setTime(long time) {
		this.timeTL = time;
	}


	public void changeColor() {
		if (color == GREEN) {
			color = YELLOW;
		} else {
			if (color == YELLOW) {
				color = RED;
			} else {
				color = GREEN;
			}
		}
	}

}
