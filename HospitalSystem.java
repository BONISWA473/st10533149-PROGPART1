/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.hospitalsystem;

/**
 *
 * @author Noxolo
 */
import java.util.*;

enum PatientCategory {
    INPATIENT, OUTPATIENT, EMERGENCY
}

class Patient {
    private int patientID;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;
    private String bedID;

    public Patient(int id, String fn, String ln, int age, String gender, String condition, PatientCategory cat) {
        this.patientID = id;
        this.firstName = fn;
        this.lastName = ln;
        this.age = age;
        this.gender = gender;
        this.medicalCondition = condition;
        this.category = cat;
        this.bedID = "None";
    }

    public int getPatientID() { return patientID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public PatientCategory getCategory() { return category; }
    public String getBedID() { return bedID; }
    public void setBedID(String bedID) { this.bedID = bedID; }
    public void setMedicalCondition(String condition) { this.medicalCondition = condition; }

    @Override
    public String toString() {
        return String.format("ID:%03d | %s %s | Age:%d | %s | %s | Cat:%s | Bed:%s",
                patientID, firstName, lastName, age, gender, medicalCondition, category, bedID);
    }
}

class Inpatient extends Patient {
    public Inpatient(int id, String fn, String ln, int age, String gender, String condition) {
        super(id, fn, ln, age, gender, condition, PatientCategory.INPATIENT);
    }
}

class Bed {
    private String bedID;
    private boolean occupied;
    private int patientID;

    public Bed(String id) {
        this.bedID = id;
        this.occupied = false;
        this.patientID = -1;
    }

    public String getBedID() { return bedID; }
    public boolean isOccupied() { return occupied; }
    public void allocate(int patientID) { this.occupied = true; this.patientID = patientID; }
    public void release() { this.occupied = false; this.patientID = -1; }
    public int getPatientID() { return patientID; }
}

public class HospitalSystem {
    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Patient> patients = new ArrayList<>();
    private static Bed[][] ward = new Bed[4][5];
    private static int nextPatientID = 1;

    public static void main(String[] args) {
        initWard();
        int choice;
        do {
            System.out.println("\n========== MediCare Hospital System ==========");
            System.out.println("1. Register Patient");
            System.out.println("2. Allocate Bed to Inpatient");
            System.out.println("3. Release Bed / Discharge");
            System.out.println("4. Search Patient by ID");
            System.out.println("5. Update Patient Condition");
            System.out.println("6. Delete Patient");
            System.out.println("7. Display Ward Layout");
            System.out.println("8. Reports Menu");
            System.out.println("9. Sort Patients");
            System.out.println("10. Exit");
            System.out.print("Enter choice: ");
            choice = getIntInput();

            switch (choice) {
                case 1: registerPatient(); break;
                case 2: allocateBed(); break;
                case 3: releaseBedMenu(); break;
                case 4: searchPatient(); break;
                case 5: updatePatient(); break;
                case 6: deletePatient(); break;
                case 7: displayWardLayout(); break;
                case 8: reportsMenu(); break;
                case 9: sortPatients(); break;
                case 10: System.out.println("Exiting... Goodbye!"); break;
                default: System.out.println("Invalid choice. Try 1-10");
            }
        } while (choice!= 10);
        sc.close();
    }

    private static void initWard() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                ward[i][j] = new Bed(String.format("B%02d", count++));
            }
        }
    }

    private static void registerPatient() {
        System.out.print("First Name: "); String fn = sc.nextLine();
        System.out.print("Last Name: "); String ln = sc.nextLine();
        System.out.print("Age: "); int age = getIntInput();
        System.out.print("Gender: "); String gender = sc.nextLine();
        System.out.print("Medical Condition: "); String condition = sc.nextLine();
        System.out.print("Category [1-Inpatient 2-Outpatient 3-Emergency]: ");
        int c = getIntInput();
        if (c < 1 || c > 3) { System.out.println("Invalid category"); return; }
        PatientCategory cat = PatientCategory.values()[c - 1];

        Patient p;
        if (cat == PatientCategory.INPATIENT) {
            p = new Inpatient(nextPatientID++, fn, ln, age, gender, condition);
        } else {
            p = new Patient(nextPatientID++, fn, ln, age, gender, condition, cat);
        }
        patients.add(p);
        System.out.println("Patient Registered Successfully! ID: " + p.getPatientID());
    }

    private static void allocateBed() {
        if (getAvailableBedCount() == 0) {
            System.out.println("ERROR: All 20 beds are occupied. Cannot allocate.");
            return;
        }
        System.out.print("Enter Patient ID: ");
        int id = getIntInput();
        Patient p = findPatient(id);
        if (p == null) { System.out.println("Patient not found"); return; }
        if (p.getCategory()!= PatientCategory.INPATIENT) {
            System.out.println("ERROR: Only Inpatients can be allocated a bed");
            return;
        }
        if (!p.getBedID().equals("None")) {
            System.out.println("Patient already has bed: " + p.getBedID());
            return;
        }
        displayAvailableBeds();
        System.out.print("Enter Bed ID to allocate e.g B05: ");
        String bedID = sc.nextLine().toUpperCase();
        Bed b = findBed(bedID);
        if (b == null) { System.out.println("Invalid Bed ID"); return; }
        if (b.isOccupied()) { System.out.println("Bed already occupied"); return; }
        b.allocate(p.getPatientID());
        p.setBedID(bedID);
        System.out.println("Success: Bed " + bedID + " allocated to " + p.getFirstName());
    }

    private static void releaseBed(int patientID) {
        Patient p = findPatient(patientID);
        if (p == null || p.getBedID().equals("None")) {
            System.out.println("Patient not found or has no bed");
            return;
        }
        Bed b = findBed(p.getBedID());
        if (b!= null) {
            b.release();
            System.out.println("Bed " + p.getBedID() + " released");
        }
        p.setBedID("None");
    }

    private static void releaseBedMenu() {
        System.out.print("Enter Patient ID to discharge: ");
        int id = getIntInput();
        releaseBed(id);
    }

    private static void displayWardLayout() {
        System.out.println("\n--- WARD LAYOUT 4x5 ---");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Bed b = ward[i][j];
                System.out.print(b.isOccupied()? "[" + b.getBedID() + ":X] " : "[" + b.getBedID() + ": ] ");
            }
            System.out.println();
        }
    }

    private static void reportsMenu() {
        System.out.println("\n--- REPORTS ---");
        System.out.println("1. All Registered Patients");
        System.out.println("2. All Available Beds");
        System.out.println("3. All Occupied Beds");
        System.out.println("4. Total Registered Patients");
        System.out.println("5. Total Occupied Beds");
        System.out.println("6. Ward Occupancy Percentage");
        System.out.print("Choose: ");
        int r = getIntInput();
        switch (r) {
            case 1: displayAllPatients(); break;
            case 2: displayAvailableBeds(); break;
            case 3: displayOccupiedBeds(); break;
            case 4: System.out.println("Total Patients: " + patients.size()); break;
            case 5: System.out.println("Occupied Beds: " + (20 - getAvailableBedCount())); break;
            case 6:
                double percent = ((20.0 - getAvailableBedCount()) / 20.0) * 100;
                System.out.printf("Ward Occupancy: %.2f%%\n", percent);
                break;
            default: System.out.println("Invalid");
        }
    }

    private static void sortPatients() {
        System.out.print("Sort by [1] Patient ID or [2] Surname: ");
        int s = getIntInput();
        if (s == 1) {
            patients.sort(Comparator.comparingInt(Patient::getPatientID));
        } else if (s == 2) {
            patients.sort(Comparator.comparing(Patient::getLastName));
        }
        System.out.println("Patients sorted.");
        displayAllPatients();
    }

    private static void displayAllPatients() {
        if (patients.isEmpty()) { System.out.println("No patients registered"); return; }
        for (Patient p : patients) System.out.println(p);
    }
    private static void displayAvailableBeds() {
        System.out.print("Available Beds: ");
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++)
                if (!ward[i][j].isOccupied()) System.out.print(ward[i][j].getBedID() + " ");
        System.out.println();
    }
    private static void displayOccupiedBeds() {
        System.out.print("Occupied Beds: ");
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++)
                if (ward[i][j].isOccupied()) System.out.print(ward[i][j].getBedID() + "(P" + ward[i][j].getPatientID() + ") ");
        System.out.println();
    }
    private static Patient findPatient(int id) {
        for (Patient p : patients) if (p.getPatientID() == id) return p;
        return null;
    }
    private static Bed findBed(String id) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++)
                if (ward[i][j].getBedID().equals(id)) return ward[i][j];
        return null;
    }
    private static int getAvailableBedCount() {
        int count = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 5; j++)
                if (!ward[i][j].isOccupied()) count++;
        return count;
    }
    private static void searchPatient() {
        System.out.print("Enter Patient ID: ");
        Patient p = findPatient(getIntInput());
        System.out.println(p == null? "Not found" : p);
    }
    private static void updatePatient() {
        System.out.print("Enter Patient ID to update: ");
        Patient p = findPatient(getIntInput());
        if (p == null) { System.out.println("Not found"); return; }
        System.out.print("Enter new Medical Condition: ");
        String newCondition = sc.nextLine();
        p.setMedicalCondition(newCondition);
        System.out.println("Patient updated");
    }
    private static void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        int id = getIntInput();
        Patient p = findPatient(id);

        if (p == null) {
            System.out.println("Patient not found");
            return;
        }

        if (!p.getBedID().equals("None")) {
            releaseBed(p.getPatientID());
        }

        patients.removeIf(patient -> patient.getPatientID() == id);
        System.out.println("Patient deleted");
    }
    private static int getIntInput() {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a number: ");
            sc.next();
        }
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }
}