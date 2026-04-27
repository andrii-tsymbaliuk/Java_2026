import java.util.ArrayList;
import java.util.List;

public class Department {
    private String name;
    private Worker head;
    private List<Worker> employees;

    public Department(String name, Worker head) {
        this.name = name;
        this.head = head;
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Worker worker) {
        employees.add(worker);
    }

    public String getName() { return name; }
    public Worker getHead() { return head; }
    public List<Worker> getEmployees() { return employees; }

    @Override
    public String toString() {
        return "Відділ \"" + name + "\" (начальник: " + head.getFirstName() + " " + head.getLastName() + ")";
    }
}
