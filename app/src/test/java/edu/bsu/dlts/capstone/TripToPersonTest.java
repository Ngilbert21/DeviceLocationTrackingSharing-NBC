package edu.bsu.dlts.capstone;

import org.junit.Test;

import static org.junit.Assert.*;

public class TripToPersonTest {

    TripToPerson TTP1= new TripToPerson();

    @Test
    public void testTripToPersonID() {
        TTP1.setTripToPersonID(15);
        assertNotNull(TTP1.getTripToPersonID());
        assertEquals(15, TTP1.getTripToPersonID());

    }

    @Test
    public void testPersonID() {
        TTP1.setPersonID(5);
        assertNotNull(TTP1.getPersonID());
        assertEquals(5, TTP1.getPersonID());
    }

    @Test
    public void testTripID() {
        TTP1.setTripID(7);
        assertNotNull(TTP1.getTripID());
        assertEquals(7, TTP1.getTripID());
    }

    @Test
    public void testCreatedDateTime() {
        TTP1.setCreatedDateTime("04-15-2017");
        assertNotNull(TTP1.getCreatedDateTime());
        assertEquals("04-15-2017", TTP1.getCreatedDateTime());
    }

    @Test
    public void testDeletedDateTime() {
        TTP1.setDeletedDateTime("06-09-2017");
        assertNotNull(TTP1.getDeletedDateTime());
        assertEquals("06-09-2017", TTP1.getDeletedDateTime());
    }
}