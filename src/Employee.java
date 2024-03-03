public class Employee{
    private int employeeId;
    private String pps;
    private String surname;
    private String firstName;
    private Gender gender;
    private Department department;
    private BigDecimal salary;
    private boolean fullTime;

    // Constructor
    public Employee() {
        this.employeeId = 0;
        this.pps = "";
        this.surname = "";
        this.firstName = "";
        this.gender = Gender.UNKNOWN;
        this.department = Department.UNKNOWN;
        this.salary = BigDecimal.ZERO;
        this.fullTime = false;
    }

    // Getters
    public int getEmployeeId() {
        return employeeId;
    }

    public String getPps() {
        return pps;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public Department getDepartment() {
        return department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public boolean isFullTime() {
        return fullTime;
    }

    // Setters
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setPps(String pps) {
        this.pps = pps;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setFullTime(boolean fullTime) {
        this.fullTime = fullTime;
    }

    // toString method
    @Override
    public String toString() {
        String fullTimeStr = fullTime ? "Yes" : "No";
        return "Employee ID: " + employeeId + "\nPPS Number: " + pps + "\nSurname: " + surname
                + "\nFirst Name: " + firstName + "\nGender: " + gender + "\nDepartment: " + department + "\nSalary: " + salary
                + "\nFull Time: " + fullTimeStr;
    }
}
