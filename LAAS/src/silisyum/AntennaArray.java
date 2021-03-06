package silisyum;

public class AntennaArray {
	
	double lambda = 1;
	double beta = 2*Math.PI/lambda;
	int numberofElements;
	public double[] amplitude;
	public double[] angular_position;
	public double[] phase;
	public int numberofSamplePoints;
	public double[] angle;
	private double[] pattern;
	public double[] pattern_dB;
	public double[] angleForOptimization_ForOuters;
	public double[] patternForOptimization_ForOuters;
	public double[] patternForOptimization_dB_ForOuters;
	public double[] levels_ForOuters;
	public double[] weights_ForOuters;
	public double[] angleForOptimization_ForInners;
	public double[] patternForOptimization_ForInners;
	public double[] patternForOptimization_dB_ForInners;
	public double[] levels_ForInners;
	public double[] weights_ForInners;
	private Mask mask;
	public int numberOfSLLOuters;
	public int numberOfSLLInners;
	public double biggestOne;	
	
	public AntennaArray(int _numberofElements, int _numberofSamplePoints, Mask _mask) {
		
		numberofSamplePoints = _numberofSamplePoints;
		numberofElements = _numberofElements;
		mask = _mask;
		createArrays();
		initializeArrays();

	}
	

	public void createArrays() {
		amplitude = new double[numberofElements];
		angular_position = new double[numberofElements];
		phase = new double[numberofElements];
		createAnlgeAndPatternArrays();
	}
	
	private void createAnlgeAndPatternArrays() {
		angle = new double[numberofSamplePoints];
		pattern = new double[numberofSamplePoints];
		pattern_dB = new double[numberofSamplePoints];
	}
	
	public void initializeArrays() {
		for (int i = 0; i < numberofElements; i++) {
			amplitude[i] = DefaultConfiguration.amplitudeValue;
//			phase[i] = DefaultConfiguration.phaseValue;
//			phase = new double[] {314.59, 45.14, 110.78, 7.37, 135.91, 295.22, 312.95, 196.25, 0, -196.25, -312.95, -295.22, -135.91, -7.37, -110.78, -45.14, -314.59, -35.60, -314.59, -45.14, -110.78, -7.37, -135.91, -295.22, -312.95, -196.25, 0, 196.25, 312.95, 295.22, 135.91, 7.37, 110.78, 45.14, 314.59, 35.60};
			phase = new double[] {264.53, 0, -264.53, -64.21, -264.53, 0, 264.53, 64.21};
			angular_position[i] = 360*((double) (i+1) / (double) numberofElements); //i*DefaultConfiguration.positionValue*lambda;
		}
	}

	public double patternFunction(double theta)
	{
		double result = 0;
		double result_real = 0;
		double result_img = 0;
		double r = (numberofElements*0.25)/(2*Math.PI); // daire dizisi yaricapi
		for (int e = 0; e<numberofElements; e++)
		{
//			result_real = result_real + amplitude[e]*Math.cos(angular_position[e]*beta*Math.cos((theta)/180*Math.PI) + ((phase[e])/180*Math.PI));
//			result_img = result_img + amplitude[e]*Math.sin(angular_position[e]*beta*Math.cos((theta)/180*Math.PI) + ((phase[e])/180*Math.PI));

//			result_real = result_real + amplitude[e]*Math.cos(beta*r*(Math.cos((theta - angular_position[e])/180*Math.PI) - Math.cos((0-angular_position[e])/180*Math.PI)));
//			result_img = result_img + amplitude[e]*Math.sin(beta*r*(Math.cos((theta - angular_position[e])/180*Math.PI) - Math.cos((0-angular_position[e])/180*Math.PI)));			

//			result_real = result_real + amplitude[e]*Math.cos(beta*r*(Math.cos((theta - angular_position[e])/180*Math.PI) - phase[e]));
//			result_img = result_img + amplitude[e]*Math.sin(beta*r*(Math.cos((theta - angular_position[e])/180*Math.PI) - phase[e]));			

// this works very well but we need to add the radius as a parameter that's why we comment this block		
//			result_real = result_real + amplitude[e]*Math.cos(beta*r*(Math.cos((theta - angular_position[e])/180*Math.PI)) + phase[e]/180*Math.PI);
//			result_img = result_img + amplitude[e]*Math.sin(beta*r*(Math.cos((theta - angular_position[e])/180*Math.PI)) + phase[e]/180*Math.PI);				

			result_real = result_real + 1*Math.cos(beta*(r+amplitude[e])*(Math.cos((theta - angular_position[e])/180*Math.PI)) + phase[e]/180*Math.PI);
			result_img = result_img + 1*Math.sin(beta*(r+amplitude[e])*(Math.cos((theta - angular_position[e])/180*Math.PI)) + phase[e]/180*Math.PI);				
			
		}
		result = Math.sqrt(result_real*result_real + result_img*result_img);
					
		return result;
	}
	
	public void createLongArrays() {
		numberOfSLLOuters = mask.outerMaskSegments.size(); 
		if (numberOfSLLOuters > 0) {
			Mask.MaskSegment SLL_outer = null;
			int numberOfAnglesForOuters = 0;
			for (int n = 0; n < numberOfSLLOuters; n++) {
				SLL_outer = mask.outerMaskSegments.get(n);
				numberOfAnglesForOuters += SLL_outer.angles.length;
			}
			angleForOptimization_ForOuters = new double[numberOfAnglesForOuters];
			patternForOptimization_ForOuters = new double[numberOfAnglesForOuters];
			patternForOptimization_dB_ForOuters = new double[numberOfAnglesForOuters];
			levels_ForOuters = new double[numberOfAnglesForOuters];
			weights_ForOuters = new double[numberOfAnglesForOuters];
		}
		
		numberOfSLLInners = mask.innerMaskSegments.size();		
		if (numberOfSLLInners > 0) {
			Mask.MaskSegment SLL_inner = null;
			int numberOfAnglesForInners = 0;
			for (int n = 0; n < numberOfSLLInners; n++) {
				SLL_inner = mask.innerMaskSegments.get(n);
				numberOfAnglesForInners += SLL_inner.angles.length;
			}
			angleForOptimization_ForInners = new double[numberOfAnglesForInners];
			patternForOptimization_ForInners = new double[numberOfAnglesForInners];
			patternForOptimization_dB_ForInners = new double[numberOfAnglesForInners];
			levels_ForInners = new double[numberOfAnglesForInners];
			weights_ForInners = new double[numberOfAnglesForInners];
		}
	}
	
	public void createPattern() {
		
		if(numberofSamplePoints != angle.length) {
			createAnlgeAndPatternArrays();
		}			
		
		angle[0] = -180;
		double biggestOne = patternFunction(angle[0]);
		pattern[0] = patternFunction(angle[0]);
		for (int i = 1; i < numberofSamplePoints; i++) {
			angle[i] = -180 + 360*((double)i/(numberofSamplePoints-1));
			pattern[i] = patternFunction(angle[i]);
			if(pattern[i]>biggestOne) biggestOne = pattern[i];
		}
		
		for (int i = 0; i < numberofSamplePoints; i++) {
			pattern_dB[i] = 20*Math.log10(pattern[i] / biggestOne);
		}
	}

	public void createPatternForOptimization() {	
		// Create an array for the all mask values
		// For this purpose, we have to make a loop.
		// Then, we set angles into the elements of this array.
		
		int i;
		biggestOne = 0;
		
		if (numberOfSLLOuters > 0) {
			// ------------ for Outers ------------
			int numberOfSLLOuters = mask.outerMaskSegments.size();
			Mask.MaskSegment SLL_outer = null;
			i = 0;
			while (i < angleForOptimization_ForOuters.length) {
				for (int n = 0; n < numberOfSLLOuters; n++) {
					SLL_outer = mask.outerMaskSegments.get(n);
					for (int j = 0; j < SLL_outer.angles.length; j++) {
						angleForOptimization_ForOuters[i] = SLL_outer.angles[j];
						levels_ForOuters[i] = SLL_outer.levels[j];
						weights_ForOuters[i] = SLL_outer.weights[j];
						i++;
					}
				}
			}

			biggestOne = patternFunction(angleForOptimization_ForOuters[0]);
			patternForOptimization_ForOuters[0] = patternFunction(angleForOptimization_ForOuters[0]);
			for (int z = 1; z < angleForOptimization_ForOuters.length; z++) { // Attention please it starts from "1"
				patternForOptimization_ForOuters[z] = patternFunction(angleForOptimization_ForOuters[z]);
				if (patternForOptimization_ForOuters[z] > biggestOne)
					biggestOne = patternForOptimization_ForOuters[z];
			}
		}
		
		if (numberOfSLLInners > 0) {
			// ------------ for Inners ------------
			numberOfSLLInners = mask.innerMaskSegments.size();
			Mask.MaskSegment SLL_inner = null;
			i = 0;
			while (i < angleForOptimization_ForInners.length) {
				for (int n = 0; n < numberOfSLLInners; n++) {
					SLL_inner = mask.innerMaskSegments.get(n);
					for (int j = 0; j < SLL_inner.angles.length; j++) {
						angleForOptimization_ForInners[i] = SLL_inner.angles[j];
						levels_ForInners[i] = SLL_inner.levels[j];
						weights_ForInners[i] = SLL_inner.weights[j];
						i++;
					}
				}
			}

			if (numberOfSLLOuters < 1) biggestOne = patternFunction(angleForOptimization_ForInners[0]);
			patternForOptimization_ForInners[0] = patternFunction(angleForOptimization_ForInners[0]);
			for (int z = 1; z < angleForOptimization_ForInners.length; z++) { // Attention please it starts from "1"
				patternForOptimization_ForInners[z] = patternFunction(angleForOptimization_ForInners[z]);
				if(patternForOptimization_ForInners[z]>biggestOne) biggestOne = patternForOptimization_ForInners[z];
			}
		}
		
		if (numberOfSLLOuters > 0) {
			for (int z = 0; z < angleForOptimization_ForOuters.length; z++) {
				patternForOptimization_dB_ForOuters[z] = 20 * Math.log10(patternForOptimization_ForOuters[z] / biggestOne);
			}
		}			
		
		if (numberOfSLLInners > 0) {
			for (int z = 0; z < angleForOptimization_ForInners.length; z++) {
				patternForOptimization_dB_ForInners[z] = 20 * Math.log10(patternForOptimization_ForInners[z] / biggestOne);
			}
		}
	}
}
