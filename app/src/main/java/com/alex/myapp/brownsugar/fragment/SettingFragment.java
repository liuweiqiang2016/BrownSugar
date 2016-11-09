package com.alex.myapp.brownsugar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alex.myapp.brownsugar.R;
import com.alex.myapp.brownsugar.model.PersonModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.zeone.framework.db.sqlite.DbUtils;
import com.zeone.framework.db.sqlite.Selector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText et_cycle,et_last;
    private Button btn;
    private static DbUtils mdb;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2,DbUtils db) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        mdb=db;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_setting, container, false);
        et_cycle= (EditText) view.findViewById(R.id.set_et_cycle);
        et_last= (EditText) view.findViewById(R.id.set_et_last);

        et_cycle.setText(mParam1);
        et_last.setText(mParam2);

        btn= (Button) view.findViewById(R.id.set_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_cycle.getText().toString().trim().equals("")){
                    AppUtils.showToast(getActivity(),"月经周期必填!");
                    return;
                }
                if (et_last.getText().toString().trim().equals("")){
                    AppUtils.showToast(getActivity(),"持续时间必填!");
                    return;
                }
                PersonModel model=mdb.findFirst(Selector.from(PersonModel.class).where("C_CID", "=", "personId"));
                model.setCycle(Integer.parseInt(et_cycle.getText().toString().trim()));
                model.setLast(Integer.parseInt(et_last.getText().toString().trim()));
                mdb.update(model);
                AppUtils.showToast(getActivity(),"保存成功!");

            }
        });

        return view;
    }


}
