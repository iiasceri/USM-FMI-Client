package iiasceri.me.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.Calendar;


public class Utilities {

    /**
     * @return default server IP
     */
    public static String getDefaultServerIp() {
        return "192.168.43.165";
    }

    /**
     * @param appContext from activity what u calling
     * @return URL where to connect all requests
     */
    public static String getServerURL(Context appContext) {

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        String jsonDataString = mPrefs.getString("Server", "");
        return  "http://" +
                jsonDataString +
                ":8080/USMFMI/api/";
    }

    /**
     * RETURN JSON filter "par", "impar"
     * @return type of week odd/even as defined in JSON
     */
    public static String getParitate() {
        Calendar calender = Calendar.getInstance();
        if (calender.get(Calendar.WEEK_OF_YEAR) % 2 == 0)
            return "par";
        else
            return "imp";
    }

    /**
     * RETURN TITLE FOR ACTIVITIES (TOOLBAR)
     * @return type of week odd/even as String
     */
    public static String getParitateTitlu() {

        String paritate;
        if (Utilities.getParitate().equals("par"))
            paritate = "pară";
        else
            paritate = "impară";

        return "Saptămână " + paritate;
    }

    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     * @return true or false if device has internet
     */
    public static boolean checkConnection(@NonNull Context context) {
        return ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
    
}
