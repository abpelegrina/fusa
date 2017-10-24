package es.ugr.abpelegrina.fusa.fuzzy;

import java.io.InputStream;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.rule.Rule;

/**
 * 
 */
public class FuzzyWeight {


	private FIS fis;
	private FunctionBlock functionBlock;
	private boolean debugOn = true;

	private FuzzyWeight(){

	}

	public static FuzzyWeight getFuzzyWeight(String filename){
		FuzzyWeight fw = new FuzzyWeight();
		
		InputStream input = fw.getClass().getResourceAsStream(filename);
		
		fw.fis = FIS.load(input, true);
		
		
		fw.functionBlock = fw.fis.getFunctionBlock(null);
		return fw;
	}

	public double evalFeedback(double feedback, double interest){
		functionBlock.setVariable("feedback", feedback);
		functionBlock.setVariable("interest", interest);
		functionBlock.evaluate();
		return functionBlock.getVariable("relevancy").getValue();
	}


	public double evalActivation(double activation, double weight){
		functionBlock.setVariable("source_activation", activation);
		functionBlock.setVariable("relation_weight", weight);
		functionBlock.evaluate();
		return functionBlock.getVariable("final_activation").getValue();
	}

	public void debugLastEval(){

		if (!debugOn) return;

		Gpr.debug("feedback[bad]: " + functionBlock.getVariable("feedback").getMembership("bad"));
		Gpr.debug("feedback[none_neutral]: " + functionBlock.getVariable("feedback").getMembership("none_neutral"));
		Gpr.debug("feedback[good]: " + functionBlock.getVariable("feedback").getMembership("good"));

		Gpr.debug("interest[not]: " + functionBlock.getVariable("interest").getMembership("not_interested"));
		Gpr.debug("interest[somewhat]: " + functionBlock.getVariable("interest").getMembership("somewhat_interested"));
		Gpr.debug("interest[very]: " + functionBlock.getVariable("interest").getMembership("very_interested"));


		for( Rule r : fis.getFunctionBlock("sa").getFuzzyRuleBlock("No1").getRules() )
      			System.out.println(r);
		
		System.out.println("-------------------");
	}

	public static void main(String[] args) {

		String fileName = "/es/ugr/abpelegrina/fusa/fuzzy/sa.fcl";


		FuzzyWeight fw = FuzzyWeight.getFuzzyWeight(fileName);
		
		long t1 = System.nanoTime();
		System.out.println("Primera evaluaci—n (0.7,10): "+fw.evalFeedback(0.7,10));
		fw.debugLastEval();

		long t2 = System.nanoTime();
		System.out.println("Segunda evaluaci—n (0.5,2): "+fw.evalFeedback(0.5,2));
		fw.debugLastEval();

		long t3 = System.nanoTime();
		System.out.println("Tercera evaluaci—n (0.3,3): "+fw.evalFeedback(0.3,3));
		fw.debugLastEval();

		long t4 = System.nanoTime();
		System.out.println("Cuarta evaluaci—n (0.1,10): "+fw.evalFeedback(0.1,10));
		fw.debugLastEval();
		long t5 = System.nanoTime();

		System.out.println("Tiempo primera evaluaci—n: " + ((t2-t1)/1000000));
		System.out.println("Tiempo segunda evaluaci—n: " + ((t3-t2)/1000000));
		System.out.println("Tiempo tercera evaluaci—n: " + ((t4-t3)/1000000));
		System.out.println("Tiempo cuarta  evaluaci—n: " + ((t5-t4)/1000000));
		
		int numLoops = 20;
		double[] randomFeedback = new double[10000];
		double[] randomInterest = new double[10000];
		
		java.util.Random r = new java.util.Random();
		for (int  i=0; i<numLoops; ++i){
			randomFeedback[i] = r.nextDouble();
			randomInterest[i] = r.nextDouble()*10;
		}			
		
		long startLoop = System.nanoTime();
		for (int i=0; i<numLoops; i++){			
			double  rel = fw.evalFeedback(randomFeedback[i],randomInterest[i]);	
			System.out.println("f=" + randomFeedback[i] + " i=" + randomInterest[i] + " r="+rel);
			fw.debugLastEval();
		}
		long endLoop = System.nanoTime();

		System.out.println("Tiempo loop: " + ((endLoop-startLoop)/1000000) + "ms");
	
	}


}
