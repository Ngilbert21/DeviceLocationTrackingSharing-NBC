package edu.bsu.dlts.capstone;

import org.junit.Test;

import static org.junit.Assert.*;

public class TrackToTripPersonTest {
    TrackToTripPerson TTTP1 = new TrackToTripPerson();

    @Test
    public void getTrackToTripPerson() {
        TTTP1.setId(20);
        assertNotNull(TTTP1.getId());
        assertEquals(20, TTTP1.getId());
    }

    @Test
    public void getTripToPersonID() {
        TTTP1.setTripToPersonID(100);
        assertNotNull(TTTP1.getTripToPersonID());
        assertEquals(100, TTTP1.getTripToPersonID());
    }

    @Test
    public void getTrackID() {
        TTTP1.setTrackID(45);
        assertNotNull(TTTP1.getTrackID());
        assertEquals(45, TTTP1.getTrackID());
    }

    @Test
    public void getCreatedDateTime() {
        TTTP1.setCreatedAt("07-20-2019");
        assertNotNull(TTTP1.getCreatedAt());
        assertEquals("07-20-2019",TTTP1.getCreatedAt());
    }

    @Test
    public void getDeletedDateTime() {
        TTTP1.setDeletedDateTime("09-09-2020");
        assertNotNull(TTTP1.getDeletedDateTime());
        assertEquals("09-09-2020", TTTP1.getDeletedDateTime());
    }
}