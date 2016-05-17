package com.huestew.studio;

// TODO move to correct package
/**
 * A scrollbar class that is capable of calculating size and position of a
 * scrollbar, as well as the resulting offset of the content.
 * 
 * @author Patrik
 */
public class Scrollbar {
	private Task<Double> visibleSize;
	private Task<Double> fullSize;

	private double origin;
	private double offset = 0;

	private double barOrigin;
	private double barOffset = 0;

	/**
	 * Create a new scrollbar.
	 * 
	 * @param visibleSize
	 *            A task that returns the size of the visible content.
	 * @param fullSize
	 *            A task that returns the size of the full content.
	 */
	public Scrollbar(Task<Double> visibleSize, Task<Double> fullSize) {
		this.visibleSize = visibleSize;
		this.fullSize = fullSize;
	}

	public double getBarSize() {
		double visibleSize = this.visibleSize.execute();
		double fullSize = this.fullSize.execute();

		return visibleSize * visibleSize / fullSize;
	}

	public double getBarPosition() {
		return barOffset;
	}

	public void setBarOrigin(double barOrigin) {
		this.barOrigin = barOrigin;
	}

	public void setBarPosition(double position) {
		double fullSize = this.fullSize.execute();
		double visibleSize = this.visibleSize.execute();
		double maxBarOffset = visibleSize - getBarSize();
		double maxOffset = fullSize - visibleSize;

		double barOffset = this.barOffset + position - barOrigin;
		setBarOffset(barOffset);

		if (maxBarOffset == 0) {
			setOffset(0);
		} else {
			setOffset(barOffset / maxBarOffset * maxOffset);
		}

		barOrigin = position;
	}

	private void setBarOffset(double barOffset) {
		double visibleSize = this.visibleSize.execute();
		double maxBarOffset = visibleSize - getBarSize();

		this.barOffset = Math.max(0, Math.min(maxBarOffset, barOffset));

		if (Double.isNaN(this.barOffset)) {
			this.barOffset = 0;
		}
	}

	public void setOrigin(double origin) {
		this.origin = origin;
	}

	public void setPosition(double position) {
		double fullSize = this.fullSize.execute();
		double visibleSize = this.visibleSize.execute();
		double maxBarOffset = visibleSize - getBarSize();
		double maxOffset = fullSize - visibleSize;

		double offset = this.offset + origin - position;
		setOffset(offset);

		if (maxOffset == 0) {
			setBarOffset(0);
		} else {
			setBarOffset(offset / maxOffset * maxBarOffset);
		}

		origin = position;
	}

	private void setOffset(double offset) {
		double fullSize = this.fullSize.execute();
		double visibleSize = this.visibleSize.execute();
		double maxOffset = fullSize - visibleSize;

		this.offset = Math.max(0, Math.min(maxOffset, offset));
	}

	public double getBarOffset() {
		return barOffset;
	}

	public double getOffset() {
		return offset;
	}

	public void addOffset(double offset) {
		// TODO
		setOrigin(0);
		setPosition(-offset);
	}

	public void update() {
		setPosition(origin);
	}
}
