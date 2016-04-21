public class SigmoidTrain {

	public double[] train(int numSamples, double[] range, int timeconstant, int fs) {
		// timeconstant *= fs;
		double[] y = new double[numSamples];
		double aUp, aDown;
		for (int numSample = 1; numSample <= numSamples; numSample++) {
			aUp = (range[0] - numSample) / timeconstant;
			aDown = (range[1] - numSample) / timeconstant;
			y[numSample - 1] = (double) ((double) (1 / (1 + Math.exp(aUp))) * (1 - 1 / (1 + Math.exp(aDown))));

		}
		return y;
	}

}
