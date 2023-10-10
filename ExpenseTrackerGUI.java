import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class ExpenseTrackerGUI extends JFrame {
    private ArrayList<Expense> expenses = new ArrayList<>();
    private JTextArea expenseTextArea;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private final String dataFileName = "expenses.csv";

    public ExpenseTrackerGUI() {
        // Set up the main frame
        setTitle("Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a text area to display expenses
        expenseTextArea = new JTextArea();
        expenseTextArea.setEditable(false);

        // Create text fields for entering description and amount
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);

        // Create a combo box for selecting a category
        String[] categories = {"Food", "Transportation", "Entertainment", "Utilities", "Other"};
        categoryComboBox = new JComboBox<>(categories);

        // Load saved expenses from file
        loadExpenses();

        // Create a button to add expenses
        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        // Create a button to calculate total expenses
        JButton calculateButton = new JButton("Calculate Total");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });

        // Create a button to calculate total expenses for a specific category
        JButton calculateCategoryButton = new JButton("Calculate Category Total");
        calculateCategoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateCategoryTotal();
            }
        });

        // Create a panel to hold input components
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(addButton);
        inputPanel.add(calculateButton);
        inputPanel.add(calculateCategoryButton);

        // Add components to the main frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(expenseTextArea), BorderLayout.CENTER);
    }

    private void addExpense() {
        try {
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = (String) categoryComboBox.getSelectedItem();

            Expense expense = new Expense(description, amount, category);
            expenses.add(expense);

            // Append the expense details to the text area
            expenseTextArea.append("Description: " + expense.getDescription() + ", Amount: $" + expense.getAmount() + ", Category: " + expense.getCategory() + "\n");

            descriptionField.setText("");
            amountField.setText("");

            // Save expenses to the CSV file after adding a new expense
            saveExpenses();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid expense amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        JOptionPane.showMessageDialog(this, "Total Expenses: $" + total, "Total", JOptionPane.INFORMATION_MESSAGE);
    }

    private void calculateCategoryTotal() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        double total = 0;
        for (Expense expense : expenses) {
            if (expense.getCategory().equals(selectedCategory)) {
                total += expense.getAmount();
            }
        }
        JOptionPane.showMessageDialog(this, "Total Expenses for Category " + selectedCategory + ": $" + total, "Category Total", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFileName))) {
            // Write expenses to the CSV file
            for (Expense expense : expenses) {
                writer.println(expense.toCSVString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line as a CSV and create an Expense object
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String description = parts[0].trim();
                    double amount = Double.parseDouble(parts[1].trim());
                    String category = parts[2].trim();
                    Expense expense = new Expense(description, amount, category);
                    expenses.add(expense);

                    // Append the loaded expense details to the text area
                    expenseTextArea.append("Description: " + expense.getDescription() + ", Amount: $" + expense.getAmount() + ", Category: " + expense.getCategory() + "\n");
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Handle exceptions if the file doesn't exist or cannot be loaded
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ExpenseTrackerGUI().setVisible(true);
            }
        });
    }
}

class Expense {
    private String description;
    private double amount;
    private String category;

    public Expense(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String toCSVString() {
        // Convert the expense to a CSV string format
        return description + "," + amount + "," + category;
    }
}