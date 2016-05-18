package com.huestew.studio.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class for creating a waveform png image from a wav-file. Original code
 * written by Andrew Thompson at stackoverflow.com:
 * http://stackoverflow.com/a/11024268
 * 
 * @author Andrew Thompson
 * @author Adam
 * 
 */
public class WaveBuilder {

	final int SCALE = 2;
	final int ACCURACY = 40;

	AudioInputStream audioInputStream;
	Vector<Line2D.Double> lines = new Vector<Line2D.Double>();
	double duration, seconds;
	String fileName;
	SamplingGraph samplingGraph;
	String waveformFilename;

	int width;
	int height;
	int imgWidth;
	int imgHeight;
	
	private List<String> imagePaths;

	public WaveBuilder(String string, String waveformFilename, int width, int height) {
		this.width = width * SCALE;
		this.height = height * SCALE;
		this.imgWidth = width;
		this.imgHeight = height;
		this.fileName = waveformFilename;
		
		this.imagePaths = new ArrayList<String>();

		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(string));
			long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000)
					/ audioInputStream.getFormat().getFrameRate());
			duration = milliseconds / 1000.0;
			samplingGraph = new SamplingGraph();
			samplingGraph.createWaveForm(null);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the imagePaths
	 */
	public List<String> getImagePaths() {
		return imagePaths;
	}

	/**
	 * Render a WaveForm.
	 */
	class SamplingGraph implements Runnable {

		private Thread thread;
		Color waveColor = new Color(70, 70, 70);

		public SamplingGraph() {
		}

		public void createWaveForm(byte[] audioBytes) {

			lines.removeAllElements(); // clear the old vector

			AudioFormat format = audioInputStream.getFormat();
			if (audioBytes == null) {
				try {
					audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
					audioInputStream.read(audioBytes);
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
			}
			int[] audioData = null;
			if (format.getSampleSizeInBits() == 16) {
				int nlengthInSamples = audioBytes.length / 2;
				audioData = new int[nlengthInSamples];
				if (format.isBigEndian()) {
					for (int i = 0; i < nlengthInSamples; i++) {
						/* First byte is MSB (high order) */
						int MSB = (int) audioBytes[2 * i];
						/* Second byte is LSB (low order) */
						int LSB = (int) audioBytes[2 * i + 1];
						audioData[i] = MSB << 8 | (255 & LSB);
					}
				} else {
					for (int i = 0; i < nlengthInSamples; i++) {
						/* First byte is LSB (low order) */
						int LSB = (int) audioBytes[2 * i];
						/* Second byte is MSB (high order) */
						int MSB = (int) audioBytes[2 * i + 1];
						audioData[i] = MSB << 8 | (255 & LSB);
					}
				}
			} else if (format.getSampleSizeInBits() == 8) {
				int nlengthInSamples = audioBytes.length;
				audioData = new int[nlengthInSamples];
				if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
					for (int i = 0; i < audioBytes.length; i++) {
						audioData[i] = audioBytes[i];
					}
				} else {
					for (int i = 0; i < audioBytes.length; i++) {
						audioData[i] = audioBytes[i] - 128;
					}
				}
			}

			int frames_per_pixel = audioBytes.length / format.getFrameSize() / width;
			byte my_byte = 0;
			double y_last = 0;
			int numChannels = format.getChannels();

			for (double x = 0; x < width * ACCURACY && audioData != null; x++) {
				int totalByte = 0;
				for (int i = 0; i < ACCURACY; i++) {
					int idx = (int) (frames_per_pixel * numChannels * x / ACCURACY);
					if (format.getSampleSizeInBits() == 8) {
						my_byte = (byte) audioData[idx];
					} else {
						my_byte = (byte) (128 * audioData[idx] / 32768);
					}
					totalByte += my_byte;
				}
				int myByte = totalByte / ACCURACY;
				double y_new = (double) (height * (128 - myByte) / 256);
				lines.add(new Line2D.Double(x / ACCURACY, y_last, x / ACCURACY, y_new));
				y_last = y_new;
			}
			saveToFile();
		}

		public void saveToFile() {

			// Create a new image
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bufferedImage.createGraphics();

			// Draw wave on image
			createSampleOnGraphicsContext(g2);
			g2.dispose();

			// Downscale image to proper size
			Image scaledImage = bufferedImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);

			// Create a new BufferedImage to write to disk
			BufferedImage saveImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics g = saveImage.getGraphics();

			// Copy downscaled image onto saveImage
			g.drawImage(scaledImage, 0, 0, null);
			g.dispose();
			
			int images = (int) Math.ceil(imgWidth/1024.0);
			
			for (int i = 0; i < images; i++) {

				int widthCrop = 1024;
				if(1024 * (i+1) > imgWidth)
					widthCrop = imgWidth - 1024 * i;
				
				BufferedImage crop = saveImage.getSubimage(1024 * i, 0, widthCrop, imgHeight);

				try {
					// Save as PNG
					File file = new File(fileName + "_" + i + ".png");
					ImageIO.write(crop, "png", file);
					
					imagePaths.add(file.toURI().toString());

				} catch (IOException e) {
				}
			}
		}

		private void createSampleOnGraphicsContext(Graphics2D g2) {

			g2.setColor(waveColor);
			for (int i = 1; i < lines.size(); i++) {
				g2.draw((Line2D) lines.get(i));
			}

		}

		public void start() {
			thread = new Thread(this);
			thread.setName("SamplingGraph");
			thread.start();
			seconds = 0;
		}

		public void stop() {
			if (thread != null) {
				thread.interrupt();
			}
			thread = null;
		}

		public void run() {
			seconds = 0;
			while (thread != null) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					break;
				}
			}
			seconds = 0;
		}
	} // End class SamplingGraph
}
