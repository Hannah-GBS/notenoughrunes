package com.notenoughrunes.ui;

import java.util.Comparator;
import java.util.Set;
import javax.annotation.Nullable;
import net.runelite.api.Constants;
import net.runelite.api.coords.WorldPoint;

final class NERSourceRouteUtil
{
	private NERSourceRouteUtil()
	{
	}

	@Nullable
	static WorldPoint findClosest(WorldPoint origin, Set<WorldPoint> targets)
	{
		return targets.stream()
			.min(Comparator.comparingInt((WorldPoint target) -> origin.distanceTo2D(target))
				.thenComparingInt(target -> Math.abs(origin.getPlane() - target.getPlane())))
			.orElse(null);
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

		try
		{
			int x = Integer.parseInt(parts[0].trim());
			int y = Integer.parseInt(parts[1].trim());
			int z = Integer.parseInt(plane.trim());

			// Jagex packed coordinates use 14 bits for x/y, and RuneLite defines four planes.
			return x >= 0 && x <= 0x3FFF && y >= 0 && y <= 0x3FFF && z >= 0 && z < Constants.MAX_Z
				? new WorldPoint(x, y, z)
				: null;
		}
		catch (NumberFormatException ignored)
		{
			return null;
		}
	}
}
