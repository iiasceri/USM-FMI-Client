package iiasceri.me.View.Marks;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import iiasceri.me.Model.PojoMarks;
import iiasceri.me.R;
import iiasceri.me.View.ExamScheduleActivity;

/* Fragment used as page 1 */
public class GPAFragment extends Fragment {

    private List<Object> mRecyclerViewItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gpa, container, false);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new MarksRecyclerViewAdapter(getContext(), mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        mRecyclerViewItems.clear();
        addMenuItemsFromJson();

        return rootView;
    }


    private void addMenuItemsFromJson() {
        try {

            DecimalFormat df2 = new DecimalFormat("#.##");
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String jsonDataString = mPrefs.getString("Marks", "");

            JSONArray semestre = new JSONArray(jsonDataString);
            new JSONArray();
            JSONArray menuItemsJsonArray;

            List<Float> mediiList = new ArrayList<>();

            for (int i = 0; i < semestre.length(); i++) {

                JSONObject semestru = semestre.getJSONObject(i);

                String  denSem = "Semestru " + semestru.get("idSemestru").toString();
                Float   mediaSem;
                Float   sumaSem = 0f;
                Float   counter = 0f;

                menuItemsJsonArray = semestru.getJSONArray("discipline");

                for (int j = 0; j < menuItemsJsonArray.length(); ++j) {

                    JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(j);

                    String nota = menuItemObject.getString("nota");
                    if (nota.equals("admis") || nota.equals("neadmis") || nota.equals("np")) {
                        System.out.println("its not number");
                    }
                    else {
                        try {
                            sumaSem += Float.valueOf(nota);;
                            counter++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                mediaSem = sumaSem / counter;
                mediiList.add(mediaSem);
                PojoMarks pojoMarks = new PojoMarks(denSem, df2.format(mediaSem));
                mRecyclerViewItems.add(pojoMarks);
            }

            float mediaTotala;
            Float sumaMediilor = 0f;
            for (Object aMediiList : mediiList) sumaMediilor += (Float)aMediiList;
            mediaTotala = sumaMediilor / mediiList.size();
            PojoMarks pojoMarks = new PojoMarks("Media la toate semestrele", df2.format(mediaTotala));
            mRecyclerViewItems.add(pojoMarks);


        } catch (JSONException exception) {
            Log.e(ExamScheduleActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
    }


}
