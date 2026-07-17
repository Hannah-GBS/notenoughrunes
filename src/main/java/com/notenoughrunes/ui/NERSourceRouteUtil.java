package com.notenoughrunes.ui;

import javax.annotation.Nullable;
import net.runelite.api.coords.WorldPoint;

final class NERSourceRouteUtil
{
	private NERSourceRouteUtil()
	{
	}

	@Nullable
	static WorldPoint parseWorldPoint(@Nullable String coords, @Nullable String plane)
	{
		if (coords == null || plane == null)
		{
			return null;
		}

		String[] parts = coords.split(",", -1);
		if (parts.length != 2)
		{
			return null;
		}

		String xText = parts[0].trim();
		String yText = parts[1].trim();
		String planeText = plane.trim();
		if (!xText.matches("\\d{1,5}") || !yText.matches("\\d{1,5}") || !planeText.matches("[0-3]"))
		{
			return null;
		}

		int x = Integer.parseInt(xText);
		int y = Integer.parseInt(yText);
		return x <= 0x3FFF && y <= 0x3FFF ? new WorldPoint(x, y, Integer.parseInt(planeText)) : null;
	}
}
