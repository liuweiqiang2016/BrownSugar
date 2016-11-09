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
import com.alex.myapp.brownsugar.model.NoteModel;
import com.alex.myapp.brownsugar.util.AppUtils;
import com.zeone.framework.db.sqlite.DbUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView title,tv_note_time;
    EditText et_subject,et_body;
    Button button;
    String time;
    private  static DbUtils db;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mdb Parameter 1.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(DbUtils mdb) {
        NoteFragment fragment = new NoteFragment();
        db=mdb;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_note, container, false);
        et_subject= (EditText) view.findViewById(R.id.et_note_subject);
        et_body= (EditText) view.findViewById(R.id.et_note_body);
        tv_note_time= (TextView) view.findViewById(R.id.tv_note_time);
        time= AppUtils.getTime();
        tv_note_time.setText("编辑时间："+ time);
        button= (Button) view.findViewById(R.id.btn_note);
        initEvent();
        return view;
    }

    private void initEvent(){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_subject.getText().toString().trim().equals("")){
                    AppUtils.showToast(getActivity(),"笔记主题必填且不可为空格!");
                    return;
                }
                if(et_body.getText().toString().trim().equals("")){
                    AppUtils.showToast(getActivity(),"笔记内容必填且不可为空格!");
                    return;
                }
                NoteModel model=new NoteModel();
                model.setCid(System.currentTimeMillis()+"");
                model.setSubject(et_subject.getText().toString().trim());
                model.setBody(et_body.getText().toString());
                model.setTime(time);
                db.save(model);
                noteFinish();

            }
        });

    }

    private void noteFinish(){
        time= AppUtils.getTime();
        tv_note_time.setText("编辑时间："+ time);
        et_subject.setText("");
        et_body.setText("");
        AppUtils.showToast(getActivity(),"笔记保存完成!");
    }

}
