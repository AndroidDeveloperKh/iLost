package kh.com.ilost.fragments;


import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import kh.com.ilost.R;
import kh.com.ilost.activities.NotificationActivity;
import kh.com.ilost.activities.UpdateProfile;
import kh.com.ilost.setting.AccountActivity;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ButtomFragment extends Fragment {

    CardView c_camera,c_chooseimg,c_delete,c_cancel;
    View view;
    private ImageView img_upload;
    private  static final int CAMERA_REQUEST_CODE=1;



    public ButtomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view =inflater.inflate(R.layout.fragment_buttom, container, false);

        c_camera=(CardView) view.findViewById(R.id.c_camera);
        c_chooseimg=(CardView)view.findViewById(R.id.c_chooseimg);
        c_cancel=(CardView)view.findViewById(R.id.c_cancel);
        c_delete =(CardView)view.findViewById(R.id.c_delete);



        c_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(),UpdateProfile.class);
                startActivity(i);
            }
        });





        return view;
    }




    //    function take UpdateProfile
    public void show(FragmentManager supportFragmentManager, String tag) {

        Log.e("error","the project");

    }




}
