package server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanjay on 28/08/2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScoreObject {
    String pack, name;
    int score,coins,deaths;
    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("pack",pack);
        obj.put("name",name);
        obj.put("score",score);
        obj.put("coins",coins);
        obj.put("deaths",deaths);
        return obj;
    }

    @Override
    public String toString() {
        return  "Level Pack: " + pack +
                ", Level Name: " + name +
                ", Coins: " + coins +
                ", Deaths: " + deaths +
                '}';
    }
}
