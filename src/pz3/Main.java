import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    // Задача 1: Максимальна заробітна платня серед УСІХ (включно з начальниками відділів та директором)
    public static Optional<Worker> findMaxSalaryWorker(Firm firm) {
        return getAllWorkers(firm)
                .max(Comparator.comparingDouble(Worker::getSalary));
    }

    // Задача 2: Відділи, де хоча б один працівник отримує більше за свого начальника
    public static List<Department> findDepartmentsWhereEmployeeEarnsMore(Firm firm) {
        return firm.getDepartments().stream()
                .filter(dept ->
                        dept.getEmployees().stream()
                            .anyMatch(emp -> emp.getSalary() > dept.getHead().getSalary())
                )
                .collect(Collectors.toList());
    }

    // Задача 3: Повний список усіх співробітників фірми (директор, начальники відділів, рядові працівники)
    public static List<Worker> getAllWorkersList(Firm firm) {
        return getAllWorkers(firm).collect(Collectors.toList());
    }

    // Допоміжний стрім: директор + начальники + рядові 
    private static Stream<Worker> getAllWorkers(Firm firm) {
        Stream<Worker> director   = Stream.of(firm.getDirector());
        Stream<Worker> heads      = firm.getDepartments().stream()
                                        .map(Department::getHead);
        Stream<Worker> employees  = firm.getDepartments().stream()
                                        .flatMap(d -> d.getEmployees().stream());
        return Stream.concat(Stream.concat(director, heads), employees);
    }

    // ════════════════════════════════════════════════════════════════
    public static void main(String[] args) {

        // Ініціалізація фірми
        Worker director = new Worker("Ігор", "Мельниченко", 85_000);
        Firm firm = new Firm("ТОВ «ТехноСвіт»", director);

        // Відділ розробки
        Worker devHead = new Worker("Олена", "Коваль", 60_000);
        Department devDept = new Department("Розробки", devHead);
        devDept.addEmployee(new Worker("Андрій",   "Бондар",    45_000));
        devDept.addEmployee(new Worker("Марія",    "Шевченко",  70_000)); // > начальника
        devDept.addEmployee(new Worker("Василь",   "Литвин",    40_000));

        // Відділ маркетингу
        Worker mktHead = new Worker("Тарас", "Гончаренко", 55_000);
        Department mktDept = new Department("Маркетингу", mktHead);
        mktDept.addEmployee(new Worker("Наталія",  "Савченко",  38_000));
        mktDept.addEmployee(new Worker("Дмитро",   "Іваненко",  42_000));
        mktDept.addEmployee(new Worker("Оксана",   "Ткаченко",  50_000));

        // Відділ бухгалтерії
        Worker accHead = new Worker("Людмила", "Пономаренко", 52_000);
        Department accDept = new Department("Бухгалтерії", accHead);
        accDept.addEmployee(new Worker("Сергій",   "Романченко", 48_000));
        accDept.addEmployee(new Worker("Ірина",    "Захаренко",  35_000));

        firm.addDepartment(devDept);
        firm.addDepartment(mktDept);
        firm.addDepartment(accDept);

        // Заголовок 
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║           Фірма: " + firm.getName() + "           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        // Задача 1
        System.out.println("Задача 1. Максимальна заробітна платня:");
        findMaxSalaryWorker(firm).ifPresent(w ->
            System.out.printf("  %s %s — %.0f грн%n",
                w.getFirstName(), w.getLastName(), w.getSalary())
        );
        System.out.println();

        // Задача 2
        System.out.println("Задача 2. Відділи, де хтось заробляє більше за начальника:");
        List<Department> flaggedDepts = findDepartmentsWhereEmployeeEarnsMore(firm);
        if (flaggedDepts.isEmpty()) {
            System.out.println("  Таких відділів немає.");
        } else {
            flaggedDepts.forEach(dept -> {
                System.out.println("  " + dept);
                System.out.printf("    Зарплата начальника: %.0f грн%n", dept.getHead().getSalary());
                dept.getEmployees().stream()
                    .filter(emp -> emp.getSalary() > dept.getHead().getSalary())
                    .forEach(emp -> System.out.printf(
                        "    ⚠  %s %s заробляє %.0f грн%n",
                        emp.getFirstName(), emp.getLastName(), emp.getSalary()));
            });
        }
        System.out.println();

        // Задача 3
        System.out.println("Задача 3. Повний список співробітників фірми:");
        System.out.printf("  %-5s %-12s %-14s %s%n", "№", "Ім'я", "Прізвище", "Зарплата");
        System.out.println("  " + "─".repeat(48));
        List<Worker> all = getAllWorkersList(firm);
        for (int i = 0; i < all.size(); i++) {
            Worker w = all.get(i);
            System.out.printf("  %-5d %s%n", i + 1, w);
        }
        System.out.printf("%n  Усього: %d осіб%n", all.size());
    }
}