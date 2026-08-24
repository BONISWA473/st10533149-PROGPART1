/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */


 package com.mycompany.hospitalsystem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class hospitalSystemTest {
    
    public hospitalSystemTest() {
    }

    @Test
    public void testPatientCreation() {
        Patient p = new Patient(1, "John", "Doe", 30, "Male", "Pneumonia", PatientCategory.INPATIENT);
        assertEquals(1, p.getPatientID());
        assertEquals("John", p.getFirstName());
        assertEquals("None", p.getBedID());
    }

    @Test
    public void testBedAllocation() {
        Bed b = new Bed("B01");
        assertFalse(b.isOccupied());
        b.allocate(1);
        assertTrue(b.isOccupied());
        assertEquals(1, b.getPatientID());
    }

    @Test
    public void testBedRelease() {
        Bed b = new Bed("B02");
        b.allocate(5);
        b.release();
        assertFalse(b.isOccupied());
    }

    @Test
    public void testInpatientCategory() {
        Inpatient ip = new Inpatient(2, "Anna", "Smith", 25, "Female", "Appendicitis");
        assertEquals(PatientCategory.INPATIENT, ip.getCategory());
    }

    @Test
    public void testOutpatientCategory() {
        Patient p = new Patient(3, "Mike", "Brown", 20, "Male", "Migraine", PatientCategory.OUTPATIENT);
        assertEquals(PatientCategory.OUTPATIENT, p.getCategory());
    }
}