import java.util.*;

public class TrafficLightGA {

    // Parameters
    static final int POPULATION_SIZE = 20;
    static final int GENERATIONS = 50;
    static final int CHROMOSOME_LENGTH = 4; // Number of traffic phases
    static final int MAX_GREEN_TIME = 60; // seconds
    static final int MIN_GREEN_TIME = 10; // seconds
    static final double MUTATION_RATE = 0.1;

    static Random rand = new Random();

    public static void main(String[] args) {
        List<int[]> population = initializePopulation();

        for (int gen = 0; gen < GENERATIONS; gen++) {
            // Evaluate fitness
            Map<int[], Double> fitnessMap = new HashMap<>();
            for (int[] individual : population) {
                double fitness = evaluate(individual);
                fitnessMap.put(individual, fitness);
            }

            // Selection
            List<int[]> selected = select(fitnessMap);

            // Crossover
            List<int[]> offspring = new ArrayList<>();
            for (int i = 0; i < selected.size() / 2; i++) {
                int[] parent1 = selected.get(i);
                int[] parent2 = selected.get(selected.size() - i - 1);
                int[] child = crossover(parent1, parent2);
                offspring.add(child);
            }

            // Mutation
            for (int[] child : offspring) {
                mutate(child);
            }

            // Create new population
            population = new ArrayList<>(selected);
            population.addAll(offspring);

            // Optional: Print best fitness in current generation
            double bestFitness = fitnessMap.values().stream().mapToDouble(f -> f).min().orElse(Double.MAX_VALUE);
            System.out.println("Generation " + gen + ": Best Fitness = " + bestFitness);
        }

        // Find best individual
        int[] bestIndividual = population.get(0);
        double bestFitness = evaluate(bestIndividual);
        for (int[] individual : population) {
            double fitness = evaluate(individual);
            if (fitness < bestFitness) {
                bestFitness = fitness;
                bestIndividual = individual;
            }
        }

        System.out.println("Optimized Green Light Durations (seconds): " + Arrays.toString(bestIndividual));
    }

    // Initialize population with random green light durations
    static List<int[]> initializePopulation() {
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[] individual = new int[CHROMOSOME_LENGTH];
            for (int j = 0; j < CHROMOSOME_LENGTH; j++) {
                individual[j] = rand.nextInt(MAX_GREEN_TIME - MIN_GREEN_TIME + 1) + MIN_GREEN_TIME;
            }
            population.add(individual);
        }
        return population;
    }

    // Evaluate fitness: lower total waiting time is better
    static double evaluate(int[] individual) {
        // Simple simulation: assume more green time reduces waiting
        double totalWaitingTime = 0;
        for (int greenTime : individual) {
            totalWaitingTime += 1000.0 / greenTime; // Arbitrary function for demo
        }
        return totalWaitingTime;
    }

    // Select top 50% individuals based on fitness
    static List<int[]> select(Map<int[], Double> fitnessMap) {
        return fitnessMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(POPULATION_SIZE / 2)
                .map(Map.Entry::getKey)
                .toList();
    }

    // Single-point crossover
    static int[] crossover(int[] parent1, int[] parent2) {
        int crossoverPoint = rand.nextInt(CHROMOSOME_LENGTH);
        int[] child = new int[CHROMOSOME_LENGTH];
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            child[i] = (i < crossoverPoint) ? parent1[i] : parent2[i];
        }
        return child;
    }

    // Mutate by randomly adjusting green time
    static void mutate(int[] individual) {
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            if (rand.nextDouble() < MUTATION_RATE) {
                individual[i] = rand.nextInt(MAX_GREEN_TIME - MIN_GREEN_TIME + 1) + MIN_GREEN_TIME;
            }
        }
    }
}
