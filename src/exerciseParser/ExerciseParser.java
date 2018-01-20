package exerciseParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/** This program converts a csv file from a exercise plan to an array */
public class ExerciseParser {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Expected args: <dir with csv>");
            return;
        }
        Stream.of(Objects.requireNonNull(new File(args[0]).listFiles()))
                .filter(file -> file.getName().endsWith(".csv"))
                .forEach(file -> {
                    List<String> lines;
                    try {
                        lines = Files.readAllLines(file.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println(" -- " + file.getName() + " -- ");
                    for (int i = 1; i < lines.size(); i++) { // line 0 is columns
                        String line = lines.get(i);
                        if (line.endsWith(",")) line += " ";
                        String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        /*if (values.length < 4 && (i + 1) < lines.size()) {
                            // Try to join with the next line
                            i++;
                            line += lines.get(i) + " ";
                            values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        }*/
                        if (values.length != 4) {
                            System.err.println("Error parsing " + line + ": " + Arrays.toString(values));
                        } else {
                            System.out.format("{ \"%s\", \"%s\", \"%s\", \"%s\" }, \n",
                                    values[0].replace("\"", ""), values[1], values[2], values[3]);
                        }
                    }
                });
    }
}
