import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

public @Data
@NoArgsConstructor
class Task {
    private int id;
    private String description;
    private Status status;
    private Date createdAt;
    private Date updatedAt;

    public Task(String description, int id) {
        this.id = id;
        this.description = description;
        this.status = Status.TODO;
        this.createdAt = new Date();
        this.updatedAt = createdAt;
    }
}
