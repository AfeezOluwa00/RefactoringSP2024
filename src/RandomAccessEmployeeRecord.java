import java.io.RandomAccessFile;
import java.io.IOException;

public class RandomAccessEmployeeRecord extends Employee {
    public static final int RECORD_SIZE = 175;

    public RandomAccessEmployeeRecord() {
        this(0, "", "", "", '\0', "", 0.0, false);
    }

    public RandomAccessEmployeeRecord(int employeeId, String pps, String surname, String firstName,
                                      char gender, String department, double salary, boolean fullTime) {
        super(employeeId, pps, surname, firstName, gender, department, salary, fullTime);
    }

    public void read(RandomAccessFile file) throws IOException {
        setEmployeeId(file.readInt());
        setPps(readString(file));
        setSurname(readString(file));
        setFirstName(readString(file));
        setGender(file.readChar());
        setDepartment(readString(file));
        setSalary(file.readDouble());
        setFullTime(file.readBoolean());
    }

    private String readString(RandomAccessFile file) throws IOException {
        char[] name = new char[20];
        for (int i = 0; i < name.length; i++) {
            name[i] = file.readChar();
        }
        return new String(name).trim();
    }

    public void write(RandomAccessFile file) throws IOException {
        file.writeInt(getEmployeeId());
        writeString(file, getPps().toUpperCase());
        writeString(file, getSurname().toUpperCase());
        writeString(file, getFirstName().toUpperCase());
        file.writeChar(getGender());
        writeString(file, getDepartment());
        file.writeDouble(getSalary());
        file.writeBoolean(getFullTime());
    }

    private void writeString(RandomAccessFile file, String data) throws IOException {
        StringBuffer buffer = new StringBuffer(data != null ? data : "");
        buffer.setLength(20);
        file.writeChars(buffer.toString());
    }
}
