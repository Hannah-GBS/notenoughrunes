package com.notenoughrunes.ui;

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NERSourceRouteUtilTest
{
	@Test
	public void parsesValidWorldPoints()
	{
		assertEquals(new WorldPoint(0, 16383, 0), NERSourceRouteUtil.parseWorldPoint("0,16383", "0"));
		assertEquals(new WorldPoint(3200, 3201, 3), NERSourceRouteUtil.parseWorldPoint("3200,3201", "3"));
		assertEquals(new WorldPoint(1380, 2932, 0), NERSourceRouteUtil.parseWorldPoint("1380, 2932", " 0 "));
	}

	@Test
	public void rejectsInvalidWorldPoints()
	{
		String[][] invalid = {
			{null, "0"},
			{"1,2", null},
			{"", "0"},
			{"1", "0"},
			{"1,2,3", "0"},
			{"-1,2", "0"},
			{"1 2,3", "0"},
			{"a,2", "0"},
			{"16384,2", "0"},
			{"999999999999,2", "0"},
			{"1,2", "-1"},
			{"1,2", "4"},
			{"1,2", "a"}
		};

		for (String[] point : invalid)
		{
			assertNull(NERSourceRouteUtil.parseWorldPoint(point[0], point[1]));
		}
	}
}
