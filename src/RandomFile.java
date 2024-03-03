import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	// Create new file
	public void createFile(String fileName) {
		try {
			RandomAccessFile file = new RandomAccessFile(fileName, "rw");
			file.close();
		} catch (IOException ioException) {
			handleIOException("Error processing file!");
		}
	}

	// Open file for adding or changing records
	public void openWriteFile(String fileName) {
		try {
			output = new RandomAccessFile(fileName, "rw");
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		}
	}

	// Close file for adding or changing records
	public void closeWriteFile() {
		try {
			if (output != null)
				output.close();
		} catch (IOException ioException) {
			handleIOException("Error closing file!");
		}
	}

	// Add records to file
	public long addRecords(Employee employeeToAdd) {
		long currentRecordStart = 0;
		RandomAccessEmployeeRecord record = createRandomAccessEmployeeRecord(employeeToAdd);

		try {
			output.seek(output.length());
			record.write(output);
			currentRecordStart = output.length() - RandomAccessEmployeeRecord.SIZE;
		} catch (IOException ioException) {
			handleIOException("Error writing to file!");
		}

		return currentRecordStart;
	}

	// Change details for existing object
	public void changeRecords(Employee newDetails, long byteToStart) {
		RandomAccessEmployeeRecord record = createRandomAccessEmployeeRecord(newDetails);

		try {
			output.seek(byteToStart);
			record.write(output);
		} catch (IOException ioException) {
			handleIOException("Error writing to file!");
		}
	}

	// Delete existing object
	public void deleteRecords(long byteToStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			output.seek(byteToStart);
			record.write(output);
		} catch (IOException ioException) {
			handleIOException("Error writing to file!");
		}
	}

	// Open file for reading
	public void openReadFile(String fileName) {
		try {
			input = new RandomAccessFile(fileName, "r");
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not supported!");
		}
	}

	// Close file
	public void closeReadFile() {
		try {
			if (input != null)
				input.close();
		} catch (IOException ioException) {
			handleIOException("Error closing file!");
		}
	}

	// Get position of first record in file
	public long getFirst() {
		return 0;
	}

	// Get position of last record in file
	public long getLast() {
		try {
			return input.length() - RandomAccessEmployeeRecord.SIZE;
		} catch (IOException e) {
			handleIOException("Error getting last record position!");
		}
		return 0;
	}

	// Get position of next record in file
	public long getNext(long readFrom) {
		long nextPosition = readFrom + RandomAccessEmployeeRecord.SIZE;
		if (nextPosition >= input.length()) {
			nextPosition = 0;
		}
		return nextPosition;
	}

	// Get position of previous record in file
	public long getPrevious(long readFrom) {
		long previousPosition = readFrom - RandomAccessEmployeeRecord.SIZE;
		if (previousPosition < 0) {
			try {
				previousPosition = input.length() - RandomAccessEmployeeRecord.SIZE;
			} catch (IOException e) {
				handleIOException("Error getting previous record position!");
			}
		}
		return previousPosition;
	}

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			input.seek(byteToStart);
			record.read(input);
		} catch (IOException e) {
			handleIOException("Error reading record!");
		}

		return record;
	}

	// Check if PPS Number already in use
	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;

		long currentByte = 0;
		try {
			while (currentByte < input.length() && !ppsExist) {
				if (currentByte != currentByteStart) {
					input.seek(currentByte);
					record.read(input);
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exists!");
					}
				}
				currentByte += RandomAccessEmployeeRecord.SIZE;
			}
		} catch (IOException e) {
			handleIOException("Error checking PPS existence!");
		}

		return ppsExist;
	}

	// Check if any record contains valid ID - greater than 0
	public boolean isSomeoneToDisplay() {
		try {
			long currentByte = 0;
			RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
			while (currentByte < input.length()) {
				input.seek(currentByte);
				record.read(input);
				if (record.getEmployeeId() > 0) {
					return true;
				}
				currentByte += RandomAccessEmployeeRecord.SIZE;
			}
		} catch (IOException e) {
			handleIOException("Error checking existence of records!");
		}
		return false;
	}

	// Helper method to create RandomAccessEmployeeRecord
	private RandomAccessEmployeeRecord createRandomAccessEmployeeRecord(Employee employee) {
