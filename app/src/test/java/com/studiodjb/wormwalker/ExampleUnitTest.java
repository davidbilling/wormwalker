package com.studiodjb.wormwalker;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Mock
    Context mMockContext;

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void check_intersection(){
        WalkingActivity myObjectUnderTest = new WalkingActivity();

        List<LineStorage> path = new ArrayList<>();
        LineStorage line = new LineStorage();
        line.Start = new LatLng(18.0020,59.3368);
        line.Stop = new LatLng(18.0028,59.3360);
        line.AddTime = new Date();
        path.add(line);
        line = new LineStorage();
        line.Start = new LatLng(18.0028,59.3360);
        line.Stop = new LatLng(18.0024,59.3373);
        line.AddTime = new Date();
        path.add(line);

        myObjectUnderTest.myPath = path;

        List<LatLng> linePoints = new ArrayList<>();
        linePoints.add(new LatLng(18.0024,59.3360));
        linePoints.add(new LatLng(18.0024,59.3373));
        assertTrue(myObjectUnderTest.checkIntersection(linePoints));
    }
}