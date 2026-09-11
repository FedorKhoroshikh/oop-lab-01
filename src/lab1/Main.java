package lab1;

import lab1.movement.Flying;
import lab1.movement.HorseRiding;
import lab1.movement.MovementStrategy;
import lab1.movement.Walking;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Demonstration: the user picks a way of moving, swaps it while the program
 * is running and sends the hero on his way.
 */
public final class Main {

    /** Every available way of moving. A new one only has to be listed here. */
    private static final List<MovementStrategy> STRATEGIES =
            List.of(new Walking(), new HorseRiding(), new Flying());

    /** Where the log goes. The coursework will plug a text area in here. */
    private static final Consumer<String> OUT = System.out::println;

    public static void main(String[] args) {
        Hero hero = new Hero("Arkady", new Point(0, 0), STRATEGIES.get(0));
        Scanner in = new Scanner(System.in);

        OUT.accept("=== Lab 1: the Strategy pattern ===");
        OUT.accept(hero.toString());

        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine(in);
            if (choice == null) {         // input is over, leave quietly
                break;
            }
            switch (choice) {
                case "1" -> moveHero(hero, in);
                case "2" -> changeStrategy(hero, in);
                case "3" -> OUT.accept(hero.toString());
                case "4" -> compareAll(hero, in);
                case "0" -> running = false;
                default -> OUT.accept("No such menu item.");
            }
        }
        OUT.accept("Done.");
    }

    private static void printMenu() {
        OUT.accept("""

                1 - send the hero to a point
                2 - change the way of moving
                3 - show the hero
                4 - compare every way on the same route
                0 - exit""");
        System.out.print("> ");
    }

    private static void moveHero(Hero hero, Scanner in) {
        Point destination = readPoint(in);
        if (destination != null) {
            hero.move(destination, OUT);
        }
    }

    /** The whole point of the task: the strategy is swapped on a live object. */
    private static void changeStrategy(Hero hero, Scanner in) {
        for (int i = 0; i < STRATEGIES.size(); i++) {
            MovementStrategy s = STRATEGIES.get(i);
            OUT.accept(String.format(Locale.ROOT, "  %d - %s (%.0f km/h)",
                    i + 1, s.name(), s.speedKmH()));
        }
        System.out.print("choice> ");

        Integer choice = readInt(in);
        if (choice == null || choice < 1 || choice > STRATEGIES.size()) {
            OUT.accept("The way of moving is unchanged.");
            return;
        }
        hero.setMovement(STRATEGIES.get(choice - 1));
        OUT.accept("The hero now moves " + hero.movement().name() + ".");
    }

    /** One route, every strategy — and the same Hero instance all along. */
    private static void compareAll(Hero hero, Scanner in) {
        Point destination = readPoint(in);
        if (destination == null) {
            return;
        }
        MovementStrategy original = hero.movement();
        Point start = hero.position();

        for (MovementStrategy strategy : STRATEGIES) {
            hero.setMovement(strategy);
            hero.move(destination, OUT);
            hero.move(start, OUT);       // send the hero back to the start
        }
        hero.setMovement(original);
        OUT.accept("The way of moving is restored: " + original.name() + ".");
    }

    private static Point readPoint(Scanner in) {
        System.out.print("x> ");
        Double x = readDouble(in);
        System.out.print("y> ");
        Double y = readDouble(in);
        if (x == null || y == null) {
            OUT.accept("Two numbers are required.");
            return null;
        }
        return new Point(x, y);
    }

    private static String readLine(Scanner in) {
        try {
            return in.nextLine().trim();
        } catch (NoSuchElementException e) {
            return null;                  // input is over (Ctrl+Z or a pipe)
        }
    }

    private static Integer readInt(Scanner in) {
        Double value = readDouble(in);
        return value == null ? null : (int) value.doubleValue();
    }

    /** Accepts both "1.5" and "1,5" so the locale does not matter. */
    private static Double readDouble(Scanner in) {
        String line = readLine(in);
        if (line == null || line.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(line.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
