import lombok.Data;

import java.time.LocalDateTime;

enum Status {
    TODO,
    IN_PROGRESS,
    DONE
}

public @Data
class Task {
    private int id;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
