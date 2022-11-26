package hotelapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArgParser {
    private final Set<String> validArgs;
    private final Map<String, String> argMap;

    /** Constructor for ArgParser */
    public ArgParser() {
        this.validArgs = new HashSet<>();
        this.argMap = new HashMap<>();
    }

    /**
     * Adds valid argument to validArgs set
     * @param arg argument
     */
    public void addValidArg(String arg) {
        validArgs.add(arg);
    }

    /**
     * Returns the mapped argument
     * @param arg key
     * @return argument value
     */
    public String getArgValue(String arg) {
        return argMap.get(arg);
    }

    /**
     * Adds user arguments to argMap if they are valid
     * @param args user arguments
     * @return true if valid user arguments, false otherwise
     */
    public boolean addUserArguments(String[] args) {
        for (int i = 0; i < args.length; i = i + 2) {
            if (validArgs.contains(args[i])) {
                argMap.put(args[i], args[i + 1]);
            } else {
                System.out.println("Invalid argument: " + args[i]);
                System.out.println("Program usage: -reviews directory -hotels filepath -threads t");
                return false;
            }
        }
        return true;
    }
}
