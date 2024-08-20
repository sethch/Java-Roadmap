import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TaskTracker {
    public final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_BLACK = "\u001B[30m";
    public final String ANSI_RED = "\u001B[31m";
    public final String ANSI_GREEN = "\u001B[32m";
    public final String ANSI_YELLOW = "\u001B[33m";
    public final String ANSI_BLUE = "\u001B[34m";
    public final String ANSI_PURPLE = "\u001B[35m";
    public final String ANSI_CYAN = "\u001B[36m";
    public final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        TaskTracker app = new TaskTracker();
        app.run(args);
    }

    private void helpHelper() {
        System.out.println(ANSI_PURPLE + "Here are a list of commands" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "add <Task Name> -> " + ANSI_YELLOW + "Add a task with <Task Name>" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "update <Id> <Updated Task Name> -> " + ANSI_YELLOW + "Update task <Id> to <Updated Task Name>" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "delete <Id> -> " + ANSI_YELLOW + "Delete task <Id>" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "mark-in-progress <Id> -> " + ANSI_YELLOW + "Update task <Id> to in-progress status" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "mark-done <Id> -> " + "Update task <Id> to done status" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "list -> " + ANSI_YELLOW + "List all tasks" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "list <Status (done, todo, in-progress)> -> " + ANSI_YELLOW + "List all tasks with <Status>" + ANSI_RESET);
    }

    private void maybeCreateFile() throws IOException {
        new File("tasks.json").createNewFile();
    }

    private Task[] readExistingTasks() throws IOException {
        maybeCreateFile();
        ObjectMapper mapper = new ObjectMapper();
        Task[] existingTasks = mapper.readValue(new File("tasks.json"), Task[].class);
        return existingTasks;
    }

    public void run(String[] args) {
        try {
            readExistingTasks();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Tried to read tasks.json but failed :(" + ANSI_RESET);
            System.out.println("Exception was: " + e.getMessage());
        }
        if (args.length == 0) {
            helpHelper();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "help": {
                helpHelper();
                break;
            }
            case "add": {
                String label = args[1];
                System.out.println("Adding task with label: " + label);
            }
            case "update": {
                Integer id = Integer.parseInt(args[1]);
                String newLabel = args[2];
                System.out.println("Updating task with id: " + id + " to new label: " + newLabel);
            }
            case "delete": {
                Integer id = Integer.parseInt(args[1]);
                System.out.println("Deleting task with id: " + id);
            }
            default:
                System.out.println(ANSI_RED + "Argument not recognized" + ANSI_RESET);
                helpHelper();
                break;
        }
    }

}
