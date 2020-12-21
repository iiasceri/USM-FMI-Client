package iiasceri.me.View.Schedule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iiasceri.me.Model.Pojo;
import iiasceri.me.R;
import iiasceri.me.Utilities.Utilities;
import iiasceri.me.View.ExamScheduleActivity;

/* Fragment used as page 1 */
public class FridayFragment extends Fragment {

    private List<Object> mRecyclerViewItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friday, container, false);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new RecyclerViewAdapterSchedule(getContext(), mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        mRecyclerViewItems.clear();
        addMenuItemsFromJson();

        return rootView;
    }

    private void addMenuItemsFromJson() {
        try {

            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String jsonDataString = mPrefs.getString("Schedule", "");

            JSONArray zile = new JSONArray(jsonDataString);
            JSONArray menuItemsJsonArray = new JSONArray();

            for (int i = 0; i < zile.length(); i++) {

                JSONObject zi = zile.getJSONObject(i);
                if (zi.get("numeZi").equals("Vineri")) {
                    menuItemsJsonArray = zi.getJSONArray("lectii");
                }
            }


            String paritate = Utilities.getParitate();

            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {

                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);
                String strParitate = menuItemObject.getString("paritate");

                if (strParitate.equals(paritate) || strParitate.equals("-")) {

                    String menuItemName = "(" + menuItemObject.getString("ora") + ")" + "  " +  menuItemObject.getString("disciplina");
                    String menuItemDescription = menuItemObject.getString("profesor");
                    String menuItemPrice = menuItemObject.getString("cabinet");
                    String menuItemCategory = menuItemObject.getString("tip");
                    String menuItemImageName = "menu_item_image";

                    Pojo pojo = new Pojo(menuItemName, menuItemDescription, menuItemPrice,
                            menuItemCategory, menuItemImageName);
                    mRecyclerViewItems.add(pojo);
                }
            }
        } catch (JSONException exception) {
            Log.e(ExamScheduleActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
    }

}
