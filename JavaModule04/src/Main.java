import java.io.*;
import java.util.*;

// Base Animal Class
class Animal {
    private String name;
    private int age;
    private String species;

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSpecies() { return species; }

    public void setSpecies(String species) { this.species = species; }

    @Override
    public String toString() {
        return getName() + " (" + getAge() + " years old)";
    }

    // Abstract method for unique animal sounds
    public void makeSound() {
        System.out.println("Animal sound");
    }
}

// Subclasses for specific animals with unique features
class Hyena extends Animal {
    public Hyena(String name, int age) {
        super(name, age);
        setSpecies("Hyena");
    }

    @Override
    public void makeSound() {
        System.out.println("Sound: Hyena's laugh!");
    }
}

class Lion extends Animal {
    public Lion(String name, int age) {
        super(name, age);
        setSpecies("Lion");
    }

    @Override
    public void makeSound() {
        System.out.println("Sound: Lion's roar!");
    }
}

class Tiger extends Animal {
    public Tiger(String name, int age) {
        super(name, age);
        setSpecies("Tiger");
    }

    @Override
    public void makeSound() {
        System.out.println("Sound: Tiger's growl!");
    }
}

class Bear extends Animal {
    public Bear(String name, int age) {
        super(name, age);
        setSpecies("Bear");
    }

    @Override
    public void makeSound() {
        System.out.println("Sound: Bear's grunt!");
    }
}

public class Main {
    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        Map<String, Integer> speciesCount = new HashMap<>();
        Map<String, List<String>> namesMap = new HashMap<>();

        // Files for animals and their names
        File animalsFile = new File("arrivingAnimals.txt");
        File namesFile = new File("animalNames.txt");

        // Check if files exist
        if (!animalsFile.exists() || !namesFile.exists()) {
            System.out.println("Error: One or both input files are missing.");
            return;
        }

        // Read and organize names by species from animalNames.txt
        try (BufferedReader nameReader = new BufferedReader(new FileReader(namesFile))) {
            String line;
            String currentSpecies = "";
            while ((line = nameReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.endsWith("Names:")) {
                    currentSpecies = line.replace(" Names:", "").trim();
                    namesMap.put(currentSpecies.toLowerCase(), new ArrayList<>());
                } else if (!currentSpecies.isEmpty()) {
                    String[] names = line.split(", ");
                    namesMap.get(currentSpecies.toLowerCase()).addAll(Arrays.asList(names));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading names file: " + e.getMessage());
        }

        // Read and process arrivingAnimals.txt and assign names
        try (BufferedReader animalReader = new BufferedReader(new FileReader(animalsFile))) {
            String line;
            int lineCount = 1;  // Line counter to number each reading line
            while ((line = animalReader.readLine()) != null) {
                System.out.println(lineCount + ") " + line);  // Print the line number

                String regex = "(\\d+) year old.*(\\bhyena\\b|\\blion\\b|\\btiger\\b|\\bbear\\b).*";
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    int age = Integer.parseInt(matcher.group(1));
                    String species = matcher.group(2).toLowerCase();

                    if (!isValidSpecies(species)) {
                        System.out.println("Skipping animal due to unknown species: " + species);
                        continue;
                    }

                    List<String> nameList = namesMap.get(species);
                    if (nameList == null || nameList.isEmpty()) {
                        System.out.println("Skipping animal due to missing name for species: " + species);
                        continue;
                    }

                    Random rand = new Random();
                    String name = nameList.get(rand.nextInt(nameList.size()));

                    // Print each detail with a label on separate lines
                    System.out.println("Assigning Name: " + name);
                    System.out.println("Species: " + species);
                    System.out.println("Age: " + age);

                    Animal animal = createAnimal(species, name, age);
                    if (animal != null) {
                        animals.add(animal);
                        speciesCount.put(species, speciesCount.getOrDefault(species, 0) + 1);

                        // Print the animal's sound with a label
                        animal.makeSound(); // Animal makes its sound after being created
                    }
                    // Add a space after each animal
                    System.out.println();
                }
                lineCount++; // Increment line count
            }
        } catch (IOException e) {
            System.out.println("Error reading animals file: " + e.getMessage());
        }

        // Output the report to newAnimals.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("newAnimals.txt"))) {
            writer.write("New Arriving Animals Report\n===========================\n");
            for (String species : speciesCount.keySet()) {
                writer.write(species + "s (Total: " + speciesCount.get(species) + ")\n");
                for (Animal animal : animals) {
                    if (animal.getSpecies().equalsIgnoreCase(species)) {
                        writer.write("  - " + animal.getName() + " (" + animal.getAge() + " years old)\n");
                    }
                }
            }
            writer.flush();
            System.out.println("Report generated successfully.");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private static boolean isValidSpecies(String species) {
        return species.equals("hyena") || species.equals("lion") || species.equals("tiger") || species.equals("bear");
    }

    private static Animal createAnimal(String species, String name, int age) {
        switch (species) {
            case "hyena": return new Hyena(name, age);
            case "lion": return new Lion(name, age);
            case "tiger": return new Tiger(name, age);
            case "bear": return new Bear(name, age);
            default: return null;
        }
    }
}
