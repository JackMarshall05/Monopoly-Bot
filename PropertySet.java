import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public enum PropertySet {
    BROWN("Brown", 50),
    LIGHT_BLUE("Light Blue", 50),
    PINK("Pink", 100),
    ORANGE("Orange", 100),
    RED("Red", 150),
    YELLOW("Yellow", 150),
    GREEN("Green", 200),
    DARK_BLUE("Dark Blue", 200),
    STATIONS("Stations", 0),
    UTILITIES("Utilities", 0);

    public final String name;
    private final int houseCost;
    private final List<Property> properties = new ArrayList<>();

    PropertySet(String name, int houseCost) {
        this.name = name;
        this.houseCost = houseCost;
    }

    public void addProperty(Property property) {
        if (properties.contains(property)) {
            return;
        }
        properties.add(property);
    }

    public List<Property> properties() {
        properties.sort(Comparator.comparingInt(p -> p.info.houses()));
        return properties;
    }

    public int houseCost() {
        return houseCost;
    }

    public int propertiesInSet() {
        return properties.size();
    }

    @Override
    public String toString() {
        return "PropertySet{name='%s', properties=%d}".formatted(name, properties.size());
    }
}
