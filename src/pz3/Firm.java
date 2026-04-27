import java.util.ArrayList;
import java.util.List;

public class Firm {
    private String name;
    private Worker director;
    private List<Department> departments;

    public Firm(String name, Worker director) {
        this.name = name;
        this.director = director;
        this.departments = new ArrayList<>();
    }

    public void addDepartment(Department department) {
        departments.add(department);
    }

    public String getName() { return name; }
    public Worker getDirector() { return director; }
    public List<Department> getDepartments() { return departments; }
}
