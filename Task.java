import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String id;
    private String companyName;
    private String customerName;
    private String contactNumber;
    private String software;
    private String issue;
    private String postBy;
    private String assignedTo;
    private String method;
    private String email;
    private boolean urgent;
    private String createTime;
    private String status;
    private String progress;

    public Task(String id, String companyName, String customerName, String contactNumber, String software, String issue, String postBy, String assignedTo, String method, String email, boolean urgent, String createTime, String status, String progress) {
        this.id = id;
        this.companyName = companyName;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.software = software;
        this.issue = issue;
        this.postBy = postBy;
        this.assignedTo = assignedTo;
        this.method = method;
        this.email = email;
        this.urgent = urgent;
        this.createTime = createTime;
        this.status = status;
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getSoftware() {
        return software;
    }

    public String getIssue() {
        return issue;
    }

    public String getPostBy() {
        return postBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getMethod() {
        return method;
    }

    public String getEmail() {
        return email;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getStatus() {
        return status;
    }

    public String getProgress() {
        return progress;
    }

    public LocalDateTime getCreateDateTime() {
        try {
            if (createTime == null || createTime.isEmpty()) {
                return null;
            }

            DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return LocalDateTime.parse(createTime, formatter);

        } catch (Exception e) {
            return null;
        }
    } 
}
