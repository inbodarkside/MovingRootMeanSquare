import java.util.Arrays;

public class MovingRMS {

	double[] signal;
	double[] temporal;
	double[] temporalWSquare;
	double[] temporalSignalSquare;
	Complex[] temporalW;
	Complex[] temporalX;
	Complex[] inverseFourier;
	double[] rootmeansquare;
	double[] squareroot;
	int size;
	int fftSize = 512;
	double mean;
	double width;
	double riseTime;
	double fs;
	double[] index = new double[2];

	public MovingRMS(double[] signal, double width, double riseTime, double fs) {
		this.signal = signal;

		size = signal.length;
		// System.out.println(size);
		/*
		 * if (size % 2 != 0) { size=size-1;
		 * this.signal=Arrays.copyOf(this.signal, size);
		 * //ArrayUtils.removeElement(signal,signal.length); }
		 */
		if (size > 512) {
			this.signal = Arrays.copyOf(this.signal, fftSize);
		}
		temporal = this.signal;
		this.width = width;
		this.riseTime = riseTime;
		this.fs = fs;
		temporalSignalSquare = new double[fftSize];
		temporalWSquare = new double[fftSize];
		temporalW = new Complex[fftSize];
		temporalX = new Complex[fftSize];
		rootmeansquare = new double[fftSize];
		squareroot = new double[fftSize];

	}

	public double[] calculate() {

		if (width * fs > fftSize / 2) {
			index[0] = 1;
			index[1] = fftSize;
			// w = ones(N,1);
		} else {
			index[0] = (double) Math.ceil((fftSize + (width * fs * -1)) / 2);
			index[1] = (double) Math.ceil((fftSize + (width * fs * 1)) / 2);
			SigmoidTrain sigmoid = new SigmoidTrain();
			temporal = sigmoid.train(fftSize, index, 100, 1);

		}
		for (int i = 0; i < temporal.length; i++) {

			temporalWSquare[i] = temporal[i] * temporal[i];
			temporalW[i] = new Complex(temporalWSquare[i], 0);

		}
		for (int i = 0; i < signal.length; i++) {

			temporalSignalSquare[i] = signal[i] * signal[i];
			temporalX[i] = new Complex(temporalSignalSquare[i], 0);

		}

		temporalW = FFT.fft(temporalW);
		temporalX = FFT.fft(temporalX);
		for (int i = 0; i < signal.length; i++) {
			temporalX[i] = temporalX[i].times(temporalW[i]);
		}

		inverseFourier = FFT.ifft(temporalX);

		for (int i = 0; i < signal.length; i++) {
			rootmeansquare[i] = (double) (inverseFourier[i].divides(new Complex(
					(double) fftSize - 1, (double) 0))).re();
		}

		for (int i = 0; i < rootmeansquare.length; i++) {

			squareroot[i] = (double) Math.sqrt(rootmeansquare[i]);
		}

		mean = Math.ceil((index[0] + index[1]) / 2);
		rootmeansquare = shift_array(squareroot, (int) mean);
		return rootmeansquare;
	}

	public static int gcd(int a, int b) {
		while (b != 0) {
			int c = a;
			a = b;
			b = c % a;
		}
		return a;
	}

	public static double[] shift_array(double[] A, int n) {
		int N = A.length;
		n %= N;
		if (n < 0)
			n = N + n;
		int d = gcd(N, n);
		for (int i = 0; i < d; i++) {
			double temp = A[i];
			for (int j = i - n + N; j != i; j = (j - n + N) % N)
				A[(j + n) % N] = A[j];
			A[i + n] = temp;
		}
		return A;
	}
}
