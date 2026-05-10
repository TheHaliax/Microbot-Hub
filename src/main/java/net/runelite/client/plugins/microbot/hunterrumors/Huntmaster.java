package net.runelite.client.plugins.microbot.hunterrumors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Huntmaster
{
    GILMAN("Huntmaster Gilman", 13121, 46, Tier.NOVICE),
    ORNUS("Guild Hunter Ornus", 13122, 57, Tier.ADEPT),
    CERVUS("Guild Hunter Cervus", 13123, 57, Tier.ADEPT),
    ACO("Guild Hunter Aco", 13124, 72, Tier.EXPERT),
    TECO("Guild Hunter Teco", 13125, 72, Tier.EXPERT),
    WOLF("Guild Hunter Wolf", 13126, 91, Tier.MASTER);

    public enum Tier { NOVICE, ADEPT, EXPERT, MASTER }

    private final String npcName;
    private final int npcId;
    private final int hunterLevelRequired;
    private final Tier tier;

    // Huntmaster room polygon corners (you gave). For now use rectangle WorldArea.
    public static final WorldArea HUNTMASTER_AREA = new WorldArea(1550, 9448, 17, 20, 0);

    // Stairs (you gave); stored as areas for object search.
    public static final WorldArea STAIRS_DOWN_AREA = new WorldArea(1556, 3048, 4, 2, 0);
    public static final int STAIRS_DOWN_OBJECT_ID = 51641;

    public static final WorldArea STAIRS_UP_AREA = new WorldArea(1557, 9446, 2, 5, 0);
    public static final int STAIRS_UP_OBJECT_ID = 51642;

    public static final WorldPoint HUNTER_GUILD_BANK_WEBWALKER = new WorldPoint(1568, 3045, 0); // placeholder; webwalker uses bank name anyway

    @Override
    public String toString()
    {
        return npcName;
    }
}

