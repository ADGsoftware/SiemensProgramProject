package masterdiseasesimulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import datacontainers.InfoJungStorage;
import datacontainers.InfoStorage;
import infectionrate.InfiniteLoopException;
import infectionrate.NotYetSupportedException;
import infectionrate.ObsoleteException;
import infectionrate.Simulator;
import moremethods.GetData;
import moremethods.MoreMethods;
import simulateddata.DataSimulator;
public class SniffleForecast {
	public static void run() throws IOException, InterruptedException, NotYetSupportedException, InfiniteLoopException, ObsoleteException {
		System.out.println("Instantiating variables...");
		//Variables
		int numUsers = 40;
		int numDays = 10;
		String town = "Needham";
		String state =  "MA";
		
		int minFriends = 2;
		int maxFriends = 5;
		int hubNumber = 0;
		Random random = new Random();
		int initiallySick;
		int initiallyVacc;
		int percentSick = 10;
		
		int getWellDays = 3;
		int getVac = 0;
		int curfewDays = 0;
		int runtimes = 100;
		int percentCurfew = 0;
		
		// Create MoreMethods instance
		MoreMethods methods = new MoreMethods();
		
		System.out.println("\n\n");
		System.out.println("Creating simulated data...");
		//Begin by Simulating Data
		DataSimulator dataSim = new DataSimulator();
		int[][] simulatedData = dataSim.run(numUsers, numDays, town, state);
		
		System.out.println("\n\n");
		System.out.println("Obtaining town census data...");
		// Create a network
		GetData gd = new GetData();
		gd.run(town, state);
		int[] hs = gd.hs;
		double[] ha = gd.ha;
		double[] ages = gd.ages;
		
		System.out.println("\n\n");
		System.out.println("Creating model town...");
		ModelTown modelTown = new ModelTown("Scale-Free", minFriends, maxFriends, hubNumber, random, hs, ha, ages);

		// Initiate Lists
		ArrayList<Person> people = modelTown.getPeople();
		
		System.out.println("\n\n");
		System.out.println("Getting infection rate...");
		// Get infection rate
		Simulator infectionRate = new Simulator(numUsers, minFriends, maxFriends);
		float p = infectionRate.run(simulatedData[1]);
//		System.out.println(p);
//		System.out.println("-------------------------------------------------------------------------------------------");
		
		// Place holder for when Dyushka comes back and helps us out with Z
		percentSick = (int)p;
		System.out.println("percentSick: " + p);
		
		System.out.println("\n\n");
		// Modify ArrayLists to fit requirements
		System.out.println("Modifying/scaling data...");
		float numPeople = people.size();
		float suceptible = simulatedData[0][numDays - 1];
		float infected = simulatedData[1][numDays - 1];
		float recovered = simulatedData[2][numDays - 1];
		float denominator = suceptible + infected + recovered;
		
//		System.out.println("PPLSIZE: " + people.size());
//		System.out.println("S: " + suceptible);
//		System.out.println("I: " + infected);
//		System.out.println("R: " + recovered);
		int suceptibleN = (int)(numPeople * suceptible/denominator);
		initiallySick = (int)(numPeople * infected/denominator);
		initiallyVacc = (int)(numPeople * recovered/denominator);
		
//		System.out.println("Suc: " + suceptibleN);
//		System.out.println("initiallySick: " + initiallySick);
//		System.out.println("initiallyVacc: " + initiallyVacc);
		
		System.out.println("\n\n");
		System.out.println("Running simulation...");
		//System.out.println("InitiallySick is "  + initiallySick);
		//System.out.println("InitiallyVacc is "  + initiallyVacc);
		//Run Sims Using last entry of simulated Data/Dyushka's thing
		InfoJungStorage results = methods.simulateForSniffleForecast(people, getWellDays, initiallySick, initiallyVacc, percentSick, getVac, runtimes, true);
		
		//Make Universal Timeline Table
		ArrayList<ArrayList<Integer>> finalTable = finalTable(simulatedData, results, numDays, methods, people, denominator);
		for (int i = 0; i < finalTable.size(); i++) {
			System.out.println(i + ": " + finalTable.get(i));
		}
		//System.out.println(finalTable);
		System.out.println("DONE!!!");
	}
	static ArrayList<ArrayList<Integer>> finalTable(int[][] simulatedData, InfoJungStorage results, int numDays, MoreMethods methods, ArrayList<Person> people, float denominator){
		ArrayList<ArrayList<Integer>> table = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < numDays; i++){
			table.add(new ArrayList<Integer>());
		}
		//Work with simulatedData
		for(int[] SIR : simulatedData){//Iterates through day by day S,I, and R lists
			int day = 0;
			for(int dayValue: SIR){
				table.get(day).add(Math.round(dayValue/denominator * people.size()));
				day++;
			}
		}
		//Work with InfoJungStorageResults
		ArrayList<ArrayList<InfoStorage>> infoStorages = results.getInfoStorages();
		ArrayList<InfoStorage> averageInfoStorages = methods.averagedInfostorageLog(infoStorages);
		for(InfoStorage dayEntry : averageInfoStorages){
			ArrayList<Integer> day = new ArrayList<Integer>(); 
			day.add((int) (people.size() - dayEntry.getImmune() - dayEntry.getNumSick()));
			day.add((int) dayEntry.getNumSick());
			day.add((int) dayEntry.getImmune());
			table.add(day);
		}
		return table;
	}
}
