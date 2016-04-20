/**
 * 
 */
package com.huestew.studio;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * @author Adam
 *
 */
public class TrackView {

	private Canvas canvas;

	/**
	 * Create a new track view with an associated canvas
	 * 
	 * @param canvas
	 * 				the canvas in which the track view will be drawn.
	 */
	public TrackView(Canvas canvas) {
		this.canvas = canvas;
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("TrackView: MouseClicked, x=" + event.getX() + ", y=" + event.getY());
			// Pass to selected tool
			// TODO get light track from y coordinate
			// TODO get time from x coordinate
			//Toolbox.getTool().doAction(event, getTrackFromY(event.getY(), getTimeFromX(event.getX()));
		});
	}

	public void redraw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGRAY);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		drawTimeline(gc);
		drawLightTracks(gc);
	}

	private void drawTimeline(GraphicsContext gc) {

		gc.setFill(Color.WHITESMOKE);
		gc.fillRect(0, 0, canvas.getWidth(), 20);

		gc.setFill(Color.BLACK);
		
		// Draw out 60 ticks on the timeline
		for(int i=0;i<60;i++){
			
			// If the timestamp is divisible by 10, we will draw a longer tick and display the time.
			if(i%10 == 0){
				gc.fillText(""+i, getXFromTime(i), 14);
				GraphicsUtil.sharpLine(gc, getXFromTime(i), 12, getXFromTime(i), 20);
			}else{
				GraphicsUtil.sharpLine(gc, getXFromTime(i), 16, getXFromTime(i), 20);
			}
		}
	}

	private double getXFromTime(int i) {
		return Math.round(i* canvas.getWidth()/60)+0.5;
	}

	private void drawLightTracks(GraphicsContext gc) {
		int i = 0;
		for(LightTrack track : HueStew.getInstance().getShow().getLightTracks()){
			gc.setStroke(Color.GRAY);
			gc.setLineWidth(1);
			gc.strokeLine(0, getTrackPositionY(i), canvas.getWidth(), getTrackPositionY(i));
			i++;
		}
	}

	private double getTrackHeight() {
		return getTotalTrackHeight() / HueStew.getInstance().getShow().getLightTracks().size();
	}

	private double getTotalTrackHeight() {
		return canvas.getHeight() - getTotalTrackPositionY();
	}

	private double getTrackPositionY(int i) {
		return getTotalTrackPositionY() + getTrackHeight() * i;
	}

	private double getTotalTrackPositionY() {
		// TODO Auto-generated method stub
		return 20;
	}

}
