public class Worker {
    private String firstName;
    private String lastName;
    private double salary;

    public Worker(String firstName, String lastName, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return String.format("%-12s %-14s — %.0f грн", firstName, lastName, salary);
    }
}
