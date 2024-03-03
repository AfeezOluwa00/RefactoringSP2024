
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener {
    // Decimal formats
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("\u20ac ###,###,##0.00");
    private static final DecimalFormat FIELD_FORMAT = new DecimalFormat("0.00");

    // File related fields
    private long currentByteStart = 0;
    private RandomFile application = new RandomFile();
    private FileNameExtensionFilter datFilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
    private File file;
    private boolean changesMade = false;
    private String generatedFileName;

    // Menu items
    private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
            searchBySurname, listAll, closeApp;

    // Buttons
    private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
            saveChange, cancelChange;

    // Combo boxes
    private JComboBox<Gender> genderCombo, departmentCombo, fullTimeCombo;

    // Text fields
    private JTextField idField, ppsField, surnameField, firstNameField, salaryField, searchByIdField,
            searchBySurnameField;

    // Font
    private Font font = new Font("SansSerif", Font.BOLD, 16);

    // Enums
    private enum Gender { MALE, FEMALE }
    private enum Department { ADMINISTRATION, PRODUCTION, TRANSPORT, MANAGEMENT }
    private enum FullTime { YES, NO }


	// initialize menu bar
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        open = createMenuItem("Open", KeyEvent.VK_O, KeyEvent.VK_O, ActionEvent.CTRL_MASK);
        save = createMenuItem("Save", KeyEvent.VK_S, KeyEvent.VK_S, ActionEvent.CTRL_MASK);
        saveAs = createMenuItem("Save As", KeyEvent.VK_F2, KeyEvent.VK_F2, ActionEvent.CTRL_MASK);
        
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        
        // Records menu
        JMenu recordMenu = new JMenu("Records");
        recordMenu.setMnemonic(KeyEvent.VK_R);
        menuBar.add(recordMenu);
        
        create = createMenuItem("Create new Record", KeyEvent.VK_N, KeyEvent.VK_N, ActionEvent.CTRL_MASK);
        modify = createMenuItem("Modify Record", KeyEvent.VK_E, KeyEvent.VK_E, ActionEvent.CTRL_MASK);
        delete = createMenuItem("Delete Record", KeyEvent.VK_D, KeyEvent.VK_DELETE, 0);
        
        recordMenu.add(create);
        recordMenu.add(modify);
        recordMenu.add(delete);
        
        // Navigate menu
        JMenu navigateMenu = new JMenu("Navigate");
        navigateMenu.setMnemonic(KeyEvent.VK_N);
        menuBar.add(navigateMenu);
        
        firstItem = createMenuItem("First", KeyEvent.VK_F, KeyEvent.VK_HOME, 0);
        prevItem = createMenuItem("Previous", KeyEvent.VK_P, KeyEvent.VK_UP, 0);
        nextItem = createMenuItem("Next", KeyEvent.VK_N, KeyEvent.VK_DOWN, 0);
        lastItem = createMenuItem("Last", KeyEvent.VK_L, KeyEvent.VK_END, 0);
        
        searchById = createMenuItem("Search by ID", KeyEvent.VK_I, KeyEvent.VK_F3, 0);
        searchBySurname = createMenuItem("Search by Surname", KeyEvent.VK_S, KeyEvent.VK_F4, 0);
        listAll = createMenuItem("List all Records", KeyEvent.VK_A, KeyEvent.VK_F5, 0);
        
        navigateMenu.add(firstItem);
        navigateMenu.add(prevItem);
        navigateMenu.add(nextItem);
        navigateMenu.add(lastItem);
        navigateMenu.addSeparator();
        navigateMenu.add(searchById);
        navigateMenu.add(searchBySurname);
        navigateMenu.add(listAll);
        
        // Close menu
        JMenu closeMenu = new JMenu("Exit");
        closeMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(closeMenu);
        
        closeApp = createMenuItem("Close", KeyEvent.VK_C, KeyEvent.VK_F4, ActionEvent.CTRL_MASK);
        closeMenu.add(closeApp);
        
        return menuBar;
    }

    private JMenuItem createMenuItem(String label, int mnemonic, int acceleratorKey, int acceleratorMask) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.setMnemonic(mnemonic);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, acceleratorMask));
        menuItem.addActionListener(this);
        return menuItem;
    }

	// end menuBar

	// initialize search panel
	private JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(searchByIdField = new JTextField(20), "width 200:200:200, growx, pushx");
		searchByIdField.addActionListener(this);
		searchByIdField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(searchId = new JButton("Go"), "width 35:35:35, height 20:20:20, growx, pushx, wrap");
		searchId.addActionListener(this);
		searchId.setToolTipText("Search Employee By ID");
		
		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(searchBySurnameField = new JTextField(20), "width 200:200:200, growx, pushx");
		searchBySurnameField.addActionListener(this);
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(searchSurname = new JButton("Go"), "width 35:35:35, height 20:20:20, growx, pushx, wrap");
		searchSurname.addActionListener(this);
		searchSurname.setToolTipText("Search Employee By Surname");
		
		return searchPanel;
	}


	private JPanel createNavigationPanel() {
		JPanel navigPanel = new JPanel();
		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		
		navigPanel.add(createButton("first.png", "Display first Record"));
		navigPanel.add(createButton("prev.png", "Display next Record"));
		navigPanel.add(createButton("next.png", "Display previous Record"));
		navigPanel.add(createButton("last.png", "Display last Record"));
		
		return navigPanel;
	}
	
	private JButton createButton(String iconPath, String tooltip) {
	    // Create a new JButton with the specified icon and tooltip
	    JButton button = new JButton(new ImageIcon(new ImageIcon(iconPath)
	            .getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH)));
	    
	    // Set preferred size for the button
	    button.setPreferredSize(new Dimension(17, 17));
	    
	    // Set tooltip text for the button
	    button.setToolTipText(tooltip);
	    
	    // Return the created button
	    return button;
	}

	private JPanel createButtonPanel() {
	    JPanel buttonPanel = new JPanel();

	    // Add buttons for various actions
	    addButton = createButton("Add Record", "Add new Employee Record");
	    editButton = createButton("Edit Record", "Edit current Employee");
	    deleteButton = createButton("Delete Record", "Delete current Employee");
	    displayAllButton = createButton("List all Records", "List all Registered Employees");

	    // Add buttons to the button panel
	    buttonPanel.add(addButton, "growx, pushx");
	    buttonPanel.add(editButton, "growx, pushx");
	    buttonPanel.add(deleteButton, "growx, pushx, wrap");
	    buttonPanel.add(displayAllButton, "growx, pushx");

	    return buttonPanel;
	}

	private JButton createButton(String label, String tooltip) {
	    JButton button = new JButton(label);
	    button.setToolTipText(tooltip);
	    button.addActionListener(this);
	    return button;
	}

	private JPanel createDetailsPanel() {
	    JPanel detailsPanel = new JPanel(new MigLayout());
	    detailsPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

	    // Add components for employee details
	    detailsPanel.add(new JLabel("ID:"), "growx, pushx");
	    detailsPanel.add(idField = new JTextField(20), "growx, pushx, wrap");
	    // Add other fields...

	    // Add save and cancel buttons to a button panel
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.add(saveChange = new JButton("Save"));
	    saveChange.addActionListener(this);
	    saveChange.setVisible(false);
	    saveChange.setToolTipText("Save changes");
	    buttonPanel.add(cancelChange = new JButton("Cancel"));
	    cancelChange.addActionListener(this);
	    cancelChange.setVisible(false);
	    cancelChange.setToolTipText("Cancel edit");

	    // Add the button panel to the details panel
	    detailsPanel.add(buttonPanel, "span 2,growx, pushx,wrap");

	    // Customize components within the details panel
	    customizeDetailsPanelComponents(detailsPanel);

	    return detailsPanel;
	}

	private void customizeDetailsPanelComponents(JPanel detailsPanel) {
	    // Loop through panel components and customize them
	    for (Component component : detailsPanel.getComponents()) {
	        if (component instanceof JTextField) {
	            JTextField field = (JTextField) component;
	            field.setFont(font1);
	            field.setEditable(false);
	            // Set document limits for text fields
	            if (field == ppsField)
	                field.setDocument(new JTextFieldLimit(9));
	            else
	                field.setDocument(new JTextFieldLimit(20));
	            field.getDocument().addDocumentListener(this);
	        } else if (component instanceof JComboBox) {
	            JComboBox<String> comboBox = (JComboBox<String>) component;
	            comboBox.setBackground(Color.WHITE);
	            comboBox.setEnabled(false);
	            comboBox.addItemListener(this);
	            // Customize the renderer for combo boxes
	            comboBox.setRenderer(new DefaultListCellRenderer() {
	                public void paint(Graphics g) {
	                    setForeground(new Color(65, 65, 65));
	                    super.paint(g);
	                }
	            });
	        }
	    }
	}


	// Display current Employee details
	public void displayRecords(Employee thisEmployee) {
	    int countGender = 0;
	    int countDep = 0;
	    boolean found = false;

	    searchByIdField.setText("");
	    searchBySurnameField.setText("");
	    if (thisEmployee != null && thisEmployee.getEmployeeId() != 0) {
	        while (!found && countGender < gender.length - 1) {
	            if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
	                found = true;
	            else
	                countGender++;
	        }
	        found = false;
	        while (!found && countDep < department.length - 1) {
	            if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
	                found = true;
	            else
	                countDep++;
	        }
	        idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
	        ppsField.setText(thisEmployee.getPps().trim());
	        surnameField.setText(thisEmployee.getSurname().trim());
	        firstNameField.setText(thisEmployee.getFirstName());
	        genderCombo.setSelectedIndex(countGender);
	        departmentCombo.setSelectedIndex(countDep);
	        salaryField.setText(format.format(thisEmployee.getSalary()));
	        fullTimeCombo.setSelectedIndex(thisEmployee.getFullTime() ? 1 : 2);
	    }
	    change = false;
	}

	// Display Employee summary dialog
	private void displayEmployeeSummaryDialog() {
	    if (isSomeoneToDisplay())
	        new EmployeeSummaryDialog(getAllEmployees());
	}

	// Display search by ID dialog
	private void displaySearchByIdDialog() {
	    if (isSomeoneToDisplay())
	        new SearchByIdDialog(EmployeeDetails.this);
	}

	// Display search by surname dialog
	private void displaySearchBySurnameDialog() {
	    if (isSomeoneToDisplay())
	        new SearchBySurnameDialog(EmployeeDetails.this);
	}

	// Find byte start in file for first active record
	private void firstRecord() {
	    if (isSomeoneToDisplay()) {
	        application.openReadFile(file.getAbsolutePath());
	        currentByteStart = application.getFirst();
	        currentEmployee = application.readRecords(currentByteStart);
	        application.closeReadFile();
	        if (currentEmployee.getEmployeeId() == 0)
	            nextRecord();
	    }
	}

	// Find byte start in file for previous active record
	private void previousRecord() {
	    if (isSomeoneToDisplay()) {
	        application.openReadFile(file.getAbsolutePath());
	        currentByteStart = application.getPrevious(currentByteStart);
	        currentEmployee = application.readRecords(currentByteStart);
	        while (currentEmployee.getEmployeeId() == 0) {
	            currentByteStart = application.getPrevious(currentByteStart);
	            currentEmployee = application.readRecords(currentByteStart);
	        }
	        application.closeReadFile();
	    }
	}

	// Find byte start in file for next active record
	private void nextRecord() {
	    if (isSomeoneToDisplay()) {
	        application.openReadFile(file.getAbsolutePath());
	        currentByteStart = application.getNext(currentByteStart);
	        currentEmployee = application.readRecords(currentByteStart);
	        while (currentEmployee.getEmployeeId() == 0) {
	            currentByteStart = application.getNext(currentByteStart);
	            currentEmployee = application.readRecords(currentByteStart);
	        }
	        application.closeReadFile();
	    }
	}

	// Find byte start in file for last active record
	private void lastRecord() {
	    if (isSomeoneToDisplay()) {
	        application.openReadFile(file.getAbsolutePath());
	        currentByteStart = application.getLast();
	        currentEmployee = application.readRecords(currentByteStart);
	        application.closeReadFile();
	        if (currentEmployee.getEmployeeId() == 0)
	            previousRecord();
	    }
	}


	// search Employee by ID
	public void searchEmployeeById() {
		boolean found = false;

		try {// try to read correct correct from input
				// if any active Employee record search for ID else do nothing
			if (isSomeoneToDisplay()) {
				firstRecord();// look for first record
				int firstId = currentEmployee.getEmployeeId();
				// if ID to search is already displayed do nothing else loop
				// through records
				if (searchByIdField.getText().trim().equals(idField.getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				} // end else if
				else {
					nextRecord();// look for next record
					// loop until Employee found or until all Employees have
					// been checked
					while (firstId != currentEmployee.getEmployeeId()) {
						// if found break from loop and display Employee details
						// else look for next record
						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							displayRecords(currentEmployee);
							break;
						} else
							nextRecord();// look for next record
					} // end while
				} // end else
					// if Employee not found display message
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} // end if
		} // end try
		catch (NumberFormatException e) {
			searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		} // end catch
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}// end searchEmployeeByID

	// search Employee by surname
	public void searchEmployeeBySurname() {
		boolean found = false;
		// if any active Employee record search for ID else do nothing
		if (isSomeoneToDisplay()) {
			firstRecord();// look for first record
			String firstSurname = currentEmployee.getSurname().trim();
			// if ID to search is already displayed do nothing else loop through
			// records
			if (searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(currentEmployee);
			} // end else if
			else {
				nextRecord();// look for next record
				// loop until Employee found or until all Employees have been
				// checked
				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					// if found break from loop and display Employee details
					// else look for next record
					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(currentEmployee);
						break;
					} // end if
					else
						nextRecord();// look for next record
				} // end while
			} // end else
				// if Employee not found display message
			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		} // end if
		searchBySurnameField.setText("");
	}// end searchEmployeeBySurname

	// get next free ID from Employees in the file
	public int getNextFreeId() {
		int nextFreeId = 0;
		// if file is empty or all records are empty start with ID 1 else look
		// for last active record
		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();// look for last active record
			// add 1 to last active records ID to get next ID
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}// end getNextFreeId

	// get values from text fields and create Employee object
	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}// end getChangedDetails

	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
		// open file for writing
		application.openWriteFile(file.getAbsolutePath());
		// write into a file
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();// close file for writing
	}// end addRecord

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display
									// message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {
				// open file for writing
				application.openWriteFile(file.getAbsolutePath());
				// delete (make inactive - empty) record in file proper position
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();// close file for writing
				// if any active record in file display next record
				if (isSomeoneToDisplay()) {
					nextRecord();// look for next record
					displayRecords(currentEmployee);
				} // end if
			} // end if
		} // end if
	}// end deleteDecord

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}// end getAllEmployees

	// Activate fields for editing
	private void editDetails() {
	    if (isSomeoneToDisplay()) {
	        salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
	        change = false;
	        setFieldsEnabled(true);
	    }
	}

	// Ignore changes and disable editing
	private void cancelChange() {
	    setFieldsEnabled(false);
	    displayRecords(currentEmployee);
	}

	// Check if any records are available for display
	private boolean isSomeoneToDisplay() {
	    boolean someoneToDisplay = false;
	    application.openReadFile(file.getAbsolutePath());
	    someoneToDisplay = application.isSomeoneToDisplay();
	    application.closeReadFile();
	    if (!someoneToDisplay) {
	        currentEmployee = null;
	        clearFields();
	        JOptionPane.showMessageDialog(null, "No Employees registered!");
	    }
	    return someoneToDisplay;
	}

	// Check PPS format and existence
	public boolean correctPps(String pps, long currentByte) {
		return changesMade;
	    // Implementation
	}

	// Check file name extension
	private boolean checkFileName(File fileName) {
		return changesMade;
	    // Implementation
	}

	// Check for changes in text fields
	private boolean checkForChanges() {
		return changesMade;
	    // Implementation
	}

	// Check for valid input in text fields
	private boolean checkInput() {
		return changesMade;
	    // Implementation
	}

	// Set text field background color to white
	private void setToWhite() {
	    // Implementation
	}

	// Enable or disable editing of text fields
	public void setFieldsEnabled(boolean enabled) {
	    // Implementation
	}


	// open file
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			file = newFile;// assign opened file to file
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			firstRecord();// look for first record
			displayRecords(currentEmployee);
			application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	private void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!idField.getText().equals("")) {
						// open file for writing
						application.openWriteFile(file.getAbsolutePath());
						// get changes for current Employee
						currentEmployee = getChangedDetails();
						// write changes to file for corresponding Employee
						// record
						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();// close file for writing
					} // end if
				} // end if
			} // end if

			displayRecords(currentEmployee);
			setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			// open file for writing
			application.openWriteFile(file.getAbsolutePath());
			// get changes for current Employee
			currentEmployee = getChangedDetails();
			// write changes to file for corresponding Employee record
			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();// close file for writing
			changesMade = false;// state that all changes has bee saved
		} // end if
		displayRecords(currentEmployee);
		setEnabled(false);
	}// end saveChanges

	// save file as 'save as'
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// check for file name
			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				// create new file
				application.createFile(newFile.getAbsolutePath());
			} // end id
			else
				// create new file
				application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (file.getName().equals(generatedFileName))
					file.delete();// delete file
				file = newFile;// assign new file to file
			} // end try
			catch (IOException e) {
			} // end catch
		} // end if
		changesMade = false;
	}// end saveFileAs

	// allow to save changes to file when exiting the application
	private void exitApp() {
	    if (file.length() != 0) {
	        int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
	                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
	        
	        if (returnVal == JOptionPane.YES_OPTION) {
	            saveFile();
	            if (file.getName().equals(generatedFileName)) {
	                file.delete();
	            }
	            System.exit(0);
	        } else if (returnVal == JOptionPane.NO_OPTION) {
	            if (file.getName().equals(generatedFileName)) {
	                file.delete();
	            }
	            System.exit(0);
	        }
	    } else {
	        if (file.getName().equals(generatedFileName)) {
	            file.delete();
	        }
	        System.exit(0);
	    }
	}

	private String generateFileName() {
	    String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
	    StringBuilder fileName = new StringBuilder();
	    Random rnd = new Random();
	    
	    while (fileName.length() < 20) {
	        int index = (int) (rnd.nextFloat() * fileNameChars.length());
	        fileName.append(fileNameChars.charAt(index));
	    }
	    
	    return fileName.toString();
	}


	// Create file with generated file name when the application is opened
	private void createRandomFile() {
	    generatedFileName = generateFileName() + ".dat";
	    file = new File(generatedFileName);
	    application.createFile(file.getName());
	}

	// ActionListener for buttons, text fields, and menu items
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == closeApp) {
	        if (checkInput() && !checkForChanges()) {
	            exitApp();
	        }
	    } else if (e.getSource() == open) {
	        if (checkInput() && !checkForChanges()) {
	            openFile();
	        }
	    } else if (e.getSource() == save) {
	        if (checkInput() && !checkForChanges()) {
	            saveFile();
	            change = false;
	        }
	    } else if (e.getSource() == saveAs) {
	        if (checkInput() && !checkForChanges()) {
	            saveFileAs();
	            change = false;
	        }
	    } else if (e.getSource() == searchById || e.getSource() == searchBySurname) {
	        if (checkInput() && !checkForChanges()) {
	            if (e.getSource() == searchById) {
	                displaySearchByIdDialog();
	            } else {
	                displaySearchBySurnameDialog();
	            }
	        }
	    } else if (e.getSource() == searchId || e.getSource() == searchByIdField) {
	        searchEmployeeById();
	    } else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField) {
	        searchEmployeeBySurname();
	    } else if (e.getSource() == saveChange) {
	        if (checkInput() && !checkForChanges()) {
	            // Implementation
	        }
	    } else if (e.getSource() == cancelChange) {
	        cancelChange();
	    } else if (e.getSource() == firstItem || e.getSource() == prevItem || e.getSource() == nextItem || e.getSource() == lastItem ||
	            e.getSource() == first || e.getSource() == previous || e.getSource() == next || e.getSource() == last) {
	        if (checkInput() && !checkForChanges()) {
	            if (e.getSource() == firstItem || e.getSource() == first) {
	                firstRecord();
	            } else if (e.getSource() == prevItem || e.getSource() == previous) {
	                previousRecord();
	            } else if (e.getSource() == nextItem || e.getSource() == next) {
	                nextRecord();
	            } else {
	                lastRecord();
	            }
	            displayRecords(currentEmployee);
	        }
	    } else if (e.getSource() == listAll || e.getSource() == displayAll) {
	        if (checkInput() && !checkForChanges() && isSomeoneToDisplay()) {
	            displayEmployeeSummaryDialog();
	        }
	    } else if (e.getSource() == create || e.getSource() == add) {
	        if (checkInput() && !checkForChanges()) {
	            new AddRecordDialog(EmployeeDetails.this);
	        }
	    } else if (e.getSource() == modify || e.getSource() == edit) {
	        if (checkInput() && !checkForChanges()) {
	            editDetails();
	        }
	    } else if (e.getSource() == delete || e.getSource() == deleteButton) {
	        if (checkInput() && !checkForChanges()) {
	            deleteRecord();
	        }
	    } else if (e.getSource() == searchBySurname) {
	        if (checkInput() && !checkForChanges()) {
	            new SearchBySurnameDialog(EmployeeDetails.this);
	        }
	    }
	}

	// Create content pane for the main dialog
	private void createContentPane() {
	    setTitle("Employee Details");
	    createRandomFile();
	    JPanel dialog = new JPanel(new MigLayout());
	    setJMenuBar(menuBar());
	    dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
	    dialog.add(navigPanel(), "width 150:150:150, wrap");
	    dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");
	    dialog.add(detailsPanel(), "gap top 30, gap left 150, center");
	    JScrollPane scrollPane = new JScrollPane(dialog);
	    getContentPane().add(scrollPane, BorderLayout.CENTER);
	    addWindowListener(this);
	}


	// Create and show main dialog
	private static void createAndShowGUI() {
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frame.createContentPane();
	    frame.setSize(760, 600);
	    frame.setLocation(250, 200);
	    frame.setVisible(true);
	}

	// Main method
	public static void main(String args[]) {
	    javax.swing.SwingUtilities.invokeLater(EmployeeDetails::createAndShowGUI);
	}

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
	    change = true;
	    new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
	    changedUpdate(d);
	}

	public void removeUpdate(DocumentEvent d) {
	    changedUpdate(d);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
	    change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
	    exitApp();
	}

	public void windowActivated(WindowEvent e) {}

	public void windowClosed(WindowEvent e) {}

	public void windowDeactivated(WindowEvent e) {}

	public void windowDeiconified(WindowEvent e) {}

	public void windowIconified(WindowEvent e) {}

	public void windowOpened(WindowEvent e) {}
