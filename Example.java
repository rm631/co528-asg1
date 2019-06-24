/*
 *	After spending an hour of my time running java Example, I can say that 
 *	**generally** it runs in 7-11 seconds, BUT sometimes it can take 27-33 seconds
 */
import java.lang.Math;
import java.util.*; 

class Example {
	
	/**
	 *	Both questions use a two-point crossover, question 2 also implements a bit-flip mutation
	 *	They also stop when a 'decent solution' is found, this was added due to the time constraint
	 *	and to avoid getting a bad result due to a bad initial population, which I wasn't sure 
	 *	how else you'd deal with..?
	 */
	public static void main(String[] args){
		long startT=System.currentTimeMillis();
		String name="Ryan Mould";
		String login = "rm631";
		
		/**
		 *
		 *
		 *	Question 1
		 * 	A decent amount of the time the result is ^-x which I assume is supposed to happen
		 *
		 */
		double[] sol1={100,100};
		double fit = Assess.getTest1(sol1[0],sol1[1]);
		
		boolean successful = false;
		int a = 0; // a is to ensure we don't waste time if we keep getting shitty initial pops
		double goodFit = 0.001;
		while(!successful) { 
		System.out.println(a++);
			double[][] pop1 = initialPop1();
			double[][] bestPop1 = findBestPop(pop1, 100);
			
			double[][] modifiedPop1 = crossover1(bestPop1, 100);
			double[][] bestModPop1 = findBestPop(modifiedPop1, 25);
			
			double bestFit = 100; // temp stores best fitness of this pop
			for(int i = 0; i < bestModPop1.length; i++) {
				fit = Assess.getTest1(bestModPop1[i][0],bestModPop1[i][1]);
				if(a==2) { goodFit = 0.01; } else if (a==3) { goodFit = 0.1; }
				if(fit < goodFit && fit > 0) {
					successful = true;
					if(fit < bestFit) {
						bestFit = fit;
						sol1[0] = bestModPop1[i][0];
						sol1[1] = bestModPop1[i][1];
					}
				} 
			}
		}
		fit = Assess.getTest1(sol1[0],sol1[1]);
		System.out.println("The fitness is: "+ fit);
		System.out.println(sol1[0] + " " + sol1[1]);
		if(fit > 0.001) { System.out.println("sol1 is over 0.001 :("); } else { System.out.println("sol1 is less than 0.001, nice work!"); }
		/**
		 *
		 *
		 *	Question 2
		 *	It's unclear whether or not the same range of data will be used for marking..?
		 *	I can only assume so, hence the fitness function will run under the assumption
		 *	that the best utility achievable tends towards ~200, I think I managed 194 on
		 *	one run and the weight was pretty close to 500
		 *
		 */

		//Creating a sample solution for the second problem
		//The higher the fitness, the better, but be careful of  the weight constraint!
		boolean[] sol2 = new boolean[100];
		
		boolean decentResult = false;
		boolean isSol2Set = false;
		goodFit = 85.0;
		int b = 0;
		while(!decentResult) { // again if the initial pop turns out crappy we'll run it again
			System.out.println(b++);
			if(b >= 2) { goodFit = goodFit - 15; }
			
			boolean[][] pop2 = initialPop2(); // 10000x100
			boolean[][] pop2Cleaned;
			int counter = 0;
			boolean first = true;
			// j and i are reversed because j came retroactively... sorry future me
			for(int j = 0; j < 50; j++) {
				if(first) { 
					pop2Cleaned = new boolean[pop2.length][]; // the max size of the first arr is the size of the original unfortunately 
					first = false;
				} else { 
					pop2Cleaned = new boolean[counter][];
				}
				
				counter = 0; // reset the counter after setting the cleaned sections new max size
				double[] result;
				for(int i = 0; i < pop2.length; i++) {
					result = Assess.getTest2(pop2[i]);
					
					if(fitnessFunc(result[0],result[1]) > goodFit) {
						pop2Cleaned[counter] = pop2[i];
						counter++;
						if(!isSol2Set) {
							sol2 = pop2[i]; // we need to make sure the baseline isn't unbeatable
							isSol2Set = true; // being the first run is no longer important
						}
						if(Assess.getTest2(sol2)[1] < result[1]) {
							sol2 = pop2[i]; // if the current solution is better than the last then assign it as the new solution..
						}
					}
					
				}
				
				if(counter == 1) { // if we only have one item then we're done already..
					break; // .. so lets get out of this loop!
				}
				
				boolean[][] popCrossover = crossover2(pop2Cleaned);
				
				pop2 = popCrossover;
				if(Assess.getTest2(sol2)[0] <= 500 && Assess.getTest2(sol2)[1] >= (goodFit*2)) {
					decentResult = true;
				}
			}
		}
		
		//Now checking the fitness of the candidate solution
		double[] tmp =(Assess.getTest2(sol2));

		//The index 0 of tmp gives the weight. Index 1 gives the utility
		System.out.println("The weight is: " + tmp[0]);
		System.out.println("The utility is: " + tmp[1]);
		
		/**
		 *
		 *
		 *	Submission
		 *
		 *
		 */
		
		//Once completed, your code must submit the results you generated, including your name and login: 
		//If you don't check in your results, you will incur a substantial loss of marks
		//Use and adapt  the function below:
		Assess.checkIn(name,login,sol1,sol2);
  
		//Do not delete or alter the next 2 lines
		//Changing them will lead to loss of marks
		long endT= System.currentTimeMillis();
		System.out.println("Total execution time was: " +  ((endT - startT)/1000.0) + " seconds");

	}
	
	/**
	 *	finds the best population
	 *	@pop is the population to search
	 *	@max is the number to get from the pop
	 */
	private static double[][] findBestPop(double[][] pop1, int max) {
		double[][] bestPop1 = new double[max][2];
		int counter = 0;
		for(int i = 0; i < pop1.length; i++) {
			double fitness = Assess.getTest1(pop1[i][0],pop1[i][1]);
			if(fitness < 3 && counter < max) {
				bestPop1[counter][0] = pop1[i][0];
				bestPop1[counter][1] = pop1[i][1];
				counter++;
			}
		}
		return bestPop1;
	}
	
	private static double[][] initialPop1() {
		int row = 10000000;
		int col = 2;
		double[][] arr = new double[row][col];
		
		double leftLimit = -500;
		double rightLimit = 500;
		
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				double rndDouble = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
				rndDouble = Math.round(rndDouble*100)/100D;
				arr[i][j] = rndDouble;
			}
		}
		return arr;
	}
	
	/**
	 *	take the pop and the size, create two lists that will take whole set of the two numbers
	 *	and randomly cross them over using a two-point crossover
	 */
	private static double[][] crossover1(double[][] pop, int counter) {
		double[][] modifiedPop = new double[counter][2];
		
		ArrayList<Double> list1 = new ArrayList<>();
		ArrayList<Double> list2 = new ArrayList<>();
			
		for(int i = 0; i < counter; i++) { // populate the lists
			list1.add(pop[i][0]);
			list2.add(pop[i][1]);
		}
		
		ArrayList<Double> tmp = new ArrayList<Double>();
		ArrayList<Double> tmp2 = new ArrayList<Double>();
		int point1 = new Random().nextInt(counter);
		int point2 = new Random().nextInt(counter);
		
		if(point2 < point1) {
			int tmpInt = point1;
			point1 = point2;
			point2 = tmpInt;
		}
		
		for(int i = point1; i < point2; i++) {
			tmp.add(list1.get(i));
		}
		int tmpCounter = 0; 
		for(int i = point1; i < point2; i++) {
			list1.set(i, list2.get(i));
			list2.set(i, tmp.get(tmpCounter));
			tmpCounter++;
		}
		for(int i = 0; i < counter; i++) {
			modifiedPop[i][0] = list1.get(i);
			modifiedPop[i][1] = list2.get(i);
		}
		
		return modifiedPop;
	}
	
	private static boolean[][] initialPop2() {
		int row = 1000000; // 1000000 seems like a decent sized initial pop for time/acc tradeoff
		int col = 100; // items in bag
		boolean[][] pop = new boolean[row][col];
		
		for(int i = 0; i < pop.length; i++) {
			int start = new Random().nextInt(70); // 100
			int end = new Random().nextInt(100);
			while(end < start) {
				end = new Random().nextInt(100);
			}
			for(int j = start; j < end; j++) {
				pop[i][j] = new Random().nextBoolean();//(Math.random() > 0.5);
			}
		}
		return pop;
	}
	
	private static boolean[][] crossover2(boolean[][] pop) {
		boolean[][] result = new boolean[pop.length][100];
		for(int i = 0; i < pop.length; i+=2) {
			if(pop[i] == null || pop[i+1] == null) { return result; } // every array after this should be null to so lets get out of here
			if(((i+1) < pop.length) && (pop[i+1] != null)) { // we'll try to avoid going out of bounds, ie. we cant crossover with nothing (eg. the 100th (index 99) array might not crossover
				// Additionally since a lot of the arrays get dropped in the first run, it has a lot of null inner arrays, maybe it would be more efficient to set a variable as soon as this happens to avoid running the check everytime after that but we'll deal with that later :)
				if(Math.random() > 0.2) { // 80% crossover chance
					ArrayList<Boolean> list1 = new ArrayList<>();
					ArrayList<Boolean> list2 = new ArrayList<>();
					for(int j = 0; j < pop[i].length; j++) {
						list1.add(pop[i][j]);
					}
					
					for(int j = 0; j < pop[i+1].length; j++) {
						list2.add(pop[i+1][j]);
					}
				
					int point1 = new Random().nextInt(70); // we want it slightly lower
					int point2 = new Random().nextInt(100);
					while(point1 > point2) { point2 = new Random().nextInt(100); }
					
					// Create and populate a temp arr
					ArrayList<Boolean> tmp = new ArrayList<>();
					ArrayList<Boolean> tmp2 = new ArrayList<>();
					for(int j = point1; j < point2; j++) {
						tmp.add(list1.get(j));
						tmp2.add(list2.get(j)); 
					}
					int tmpListCounter = 0;
					for(int j = point1; j < point2; j++) { // crossover lists
						list1.set(j, tmp2.get(tmpListCounter));
						list2.set(j, tmp.get(tmpListCounter));
						tmpListCounter++;
					}
					
					/**
					 *	Mutation happens here because why not..
					 *	it does limit mutation to the area between two point crossover
					 *	but that seems fine since that *CAN* potentially be all the bits
					 *	all in all it reduces the chance of mutation 
					 */
					list1 = mutation(list1);
					list2 = mutation(list2);
					
					for(int j = 0; j < 100; j++) {
						result[i][j] = list1.get(j);
						result[i+1][j] = list2.get(j);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 *	A bool arraylist is passed in with a random chance (about 20%~) of a random bit being flipped
	 */
	private static ArrayList<Boolean> mutation(ArrayList<Boolean> list) {
		if(Math.random() > 0.8) { // we want quite a low chance of mutation so .8-.9 should be fine
			//System.out.println("Woah! Something is mutating!");
			int mutantBit = new Random().nextInt(list.size()); 
			list.set(mutantBit, !list.get(mutantBit));
		}
		return list;
	}
	
	/**
	 *	It says the values will be numerically different, but I'm going to assume
	 *	that the range will be in the same range ie. tend towards 200
	 *	if the new ones are over 200 then this is fucked, if its less that we can deal with that
	 *
	 *	take a double u, which will be the utility value and return a value
	 *	The closer the return value is to 100, the better it is
	 *
	 *	also takes the weight w, the weight doesn't seem to matter(..?) outside of
	 *	just being less that 500, if it is over 500 then we'll just return 0
	 */
	private static double fitnessFunc(double w, double u) {
		if(w > 500) { 
			return 0d; 
		} else {
			return (u/200)*100; // essentially we're just dealing with a % (if it's simple and works...)
		}
	}
	
}