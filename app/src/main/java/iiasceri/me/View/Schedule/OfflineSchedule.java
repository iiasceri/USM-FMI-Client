package iiasceri.me.View.Schedule;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OfflineSchedule {
    /*
     *
     * @return JSON Object with weekly schedule
     *
     * @groupName ex: IA1602rom
     * @subgroup [I, II]
     * @scheduleType [weekly, exam]
     *
     */
    public String getMySchedule(String subGroup,
                                String scheduleType, Context context) {

        String fName = "ia1602weekly.json";

        if (scheduleType.equals("exam"))
            fName = "ia1602exam.json";

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fName);
        }
        catch (IOException e){
            Log.e("message: ",e.getMessage());
        }
        String content = null;
        try {
            InputStream is = inputStream;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            content = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        String jsonString = content;

        jsonString = jsonString.replaceFirst("\\[", "{\"orar\":\\[");
        jsonString = jsonString.replaceFirst("]$", "]}");

        String response = "";
        try {
            JSONObject jObj = new JSONObject(jsonString);
            JSONArray saptamina_jArray = jObj.getJSONArray("orar");

            JSONArray lectiiFiltrateDinZi = new JSONArray();
            JSONObject ziCuLectiiFiltrate = new JSONObject();
            JSONArray saptamanaCuZileFiltrate = new JSONArray();

            JSONArray examLectiiFiltrate = new JSONArray();

            JSONObject rezultat = new JSONObject();

            for (int i = 0; i < saptamina_jArray.length(); i++) {
                JSONObject zi_jObject = saptamina_jArray.getJSONObject(i);

                if (scheduleType.equals("exam")) {
                    if (zi_jObject.get("subgrupa").equals(subGroup) || zi_jObject.get("subgrupa").equals("-")) {
                        examLectiiFiltrate.put(zi_jObject);
                    }
                }
                else {
                    JSONArray lectii_jArray = zi_jObject.getJSONArray("lectii");
                    for (int j = 0; j < lectii_jArray.length(); j++) {
                        JSONObject lectie_jObect = lectii_jArray.getJSONObject(j);
                        if (lectie_jObect.get("subgrupa").equals(subGroup) || lectie_jObect.get("subgrupa").equals("-")) {
                            lectie_jObect.remove("subgrupa");
                            lectiiFiltrateDinZi.put(lectie_jObect);
                        }
                    }
                    ziCuLectiiFiltrate.put("numeZi", zi_jObject.get("numeZi"));
                    ziCuLectiiFiltrate.put("lectii", lectiiFiltrateDinZi);
                    lectiiFiltrateDinZi = new JSONArray();

                    saptamanaCuZileFiltrate.put(ziCuLectiiFiltrate);
                    ziCuLectiiFiltrate = new JSONObject();
                }
            }
            if (scheduleType.equals("exam")) {
                rezultat.put("orar", examLectiiFiltrate);
                response = rezultat.toString();
                return response;
            }
            rezultat.put("orar", saptamanaCuZileFiltrate);
            response = rezultat.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }
}
