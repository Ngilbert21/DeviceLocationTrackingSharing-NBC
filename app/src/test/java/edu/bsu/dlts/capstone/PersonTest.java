package edu.bsu.dlts.capstone;

import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {
    Person p1 = new Person();

    @Test
    public void testPersonID() {
        p1.setPersonID(1);
        assertNotNull(p1.getPersonID());
        assertEquals(1, p1.getPersonID());
    }

    @Test
    public void testFirstName() {
        p1.setFirstName("Clay");
        assertNotNull(p1.getFirstName());
        assertEquals("Clay", p1.getFirstName());
    }

    @Test
    public void testLastName() {
        p1.setLastName("Nor");
        assertNotNull(p1.getLastName());
        assertEquals("Nor", p1.getLastName());
    }

    @Test
    public void testEmailAddress() {
        p1.setEmailAddress("Norc@gmail.com");
        assertNotNull(p1.getEmailAddress());
        assertEquals("Norc@gmail.com", p1.getEmailAddress());
    }

    @Test
    public void testCreatedDateTime() {
        p1.setCreatedAt("12-21-96");
        assertNotNull(p1.getCreatedAt());
        assertEquals("12-21-96", p1.getCreatedAt());
    }

    @Test
    public void testDeletedDateTime() {
        p1.setDeletedDateTime("");
        assertNotNull(p1.getDeletedDateTime());
        assertEquals("", p1.getDeletedDateTime());
    }

}