import java.util.ArrayList;
import java.util.HashMap;

public record PlayerStrat(ArrayList<PropertyValue> propertyValues, HashMap<PropertySet, Float> setValues, float playSafeThreshold) {
    public PlayerStrat{
        for(PropertyValue p : propertyValues){
            if(p.value() < 0){throw new IllegalArgumentException("propertyValue can't be negative");}
        }
        for(float i : setValues.values()){
            if(i < 0){throw new IllegalArgumentException("setValue need to be above 0");}
        }
        if(playSafeThreshold < 0){throw new IllegalArgumentException("playSafeThreshold can't be negative");}
    }
}
