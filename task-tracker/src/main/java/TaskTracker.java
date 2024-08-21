import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    ObjectMapper mapper;

    public TaskTracker() throws IOException {
        mapper = new ObjectMapper();
        maybeCreateFile();
    }

    public static void main(String[] args) {
        try {
            TaskTracker app = new TaskTracker();
            app.run(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        File file = new File("tasks.json");
        if (file.createNewFile()) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write("[]");
            }
        }
    }

    private List<Task> readExistingTasks() throws IOException {
        List<Task> existingTasks = mapper.readValue(new File("tasks.json"), new TypeReference<List<Task>>() {
        });
        return existingTasks;
    }

    private void writeTasksToFile(List<Task> tasks) throws IOException {
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File("tasks.json"), tasks);
    }

    public void run(String[] args) throws IOException {
        List<Task> existingTasks = null;
        existingTasks = readExistingTasks();
        if (existingTasks == null) {
            throw new RuntimeException("This should not happen");
        }
        Integer maxId = getMaxId(existingTasks);

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
                String description = args[1];
                System.out.println("Adding task with description: " + description);
                Task newTask = new Task(description, ++maxId);
                existingTasks.add(newTask);
                writeTasksToFile(existingTasks);
                break;
            }
            case "update": {
                Integer id = Integer.parseInt(args[1]);
                String newDescription = args[2];
                System.out.println("Updating task with id: " + id + " to new description: " + newDescription);
                if (newDescription != null) {
                    Task taskToUpdate = existingTasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
                    if (taskToUpdate == null) {
                        System.out.println(ANSI_RED + "Failed to update, task not found" + ANSI_RESET);
                    } else {
                        taskToUpdate.setDescription(newDescription);
                        taskToUpdate.setUpdatedAt(new Date());
                        writeTasksToFile(existingTasks);
                    }
                }
                break;
            }
            case "delete": {
                Integer id = Integer.parseInt(args[1]);
                System.out.println("Deleting task with id: " + id);
                existingTasks = existingTasks.stream().filter(task -> task.getId() != id).collect(Collectors.toList());
                writeTasksToFile(existingTasks);
                System.out.println("Deleted task with id: " + id);
                break;
            }
            case "list": {
                if (args.length == 1) {
                    // list all
                    for (var task : existingTasks) {
                        System.out.println(task.toString());
                    }
                } else {
                    // list by status
                    Status status = Status.valueOf(args[1].toUpperCase());
                    var filteredTasks = existingTasks.stream().filter(task -> task.getStatus().equals(status)).collect(Collectors.toList());
                    for (var task : filteredTasks) {
                        System.out.println(task.toString());
                    }
                }
                break;
            }
            case "mark-in-progress": {
                int id = Integer.parseInt(args[1]);
                Task taskToUpdate = existingTasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
                if (taskToUpdate == null) {
                    System.out.println(ANSI_RED + "Failed to update, task not found" + ANSI_RESET);
                } else {
                    taskToUpdate.setStatus(Status.IN_PROGRESS);
                    taskToUpdate.setUpdatedAt(new Date());
                    writeTasksToFile(existingTasks);
                }
                System.out.println("Updated task " + id + " with status IN_PROGRESS");
                break;
            }
            case "mark-done": {
                int id = Integer.parseInt(args[1]);
                Task taskToUpdate = existingTasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
                if (taskToUpdate == null) {
                    System.out.println(ANSI_RED + "Failed to update, task not found" + ANSI_RESET);
                } else {
                    taskToUpdate.setStatus(Status.DONE);
                    taskToUpdate.setUpdatedAt(new Date());
                    writeTasksToFile(existingTasks);
                }
                System.out.println("Updated task " + id + " with status DONE");
                break;
            }
            default:
                System.out.println(ANSI_RED + "Argument not recognized" + ANSI_RESET);
                helpHelper();
                break;
        }
    }

    private Integer getMaxId(List<Task> existingTasks) {
        Optional<Integer> maxIdOptional = existingTasks.stream().max((task1, task2) -> {
            if (task1.getId() > task2.getId()) {
                return 1;
            } else if (task1.getId() < task2.getId()) {
                return -1;
            }
            return 0;
        }).map(Task::getId);
        return maxIdOptional.orElse(0);
    }
}
