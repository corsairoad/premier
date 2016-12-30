package valet.digikom.com.valetparking.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.Checkin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_CHECKIN = "chekin";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Checkin checkin = new Checkin();
    private Button btnResetSign;
    private SignaturePad signPad;
    private TextView textDropPoint;
    private TextView textPlatNo;
    private TextView textJenisMobil;
    private TextView textWarnaMobil;
    private TextView textEmail;
    private TextView textDefects;
    private TextView textStuffs;
    public static ReviewFragment reviewFragment;

    public ReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewFragment newInstance(String param1, String param2, Checkin checkin) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelable(ARG_CHECKIN, checkin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewFragment = this;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //checkin = getArguments().getParcelable(ARG_CHECKIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        textDropPoint = (TextView) view.findViewById(R.id.text_drop_point);
        textPlatNo = (TextView) view.findViewById(R.id.text_plat_no);
        textJenisMobil = (TextView) view.findViewById(R.id.text_cartype);
        textWarnaMobil = (TextView) view.findViewById(R.id.text_color);
        textEmail = (TextView) view.findViewById(R.id.text_email);
        textDefects = (TextView) view.findViewById(R.id.text_defect);
        textStuffs = (TextView) view.findViewById(R.id.text_stuff);
        init();
        btnResetSign = (Button) view.findViewById(R.id.btn_reset_sign);
        signPad = (SignaturePad) view.findViewById(R.id.signature_pad);

        btnResetSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signPad.clear();
            }
        });

        return view;
    }

    public void init() {
        textDropPoint.setText(checkin.getDropPoint());
        textPlatNo.setText(checkin.getPlatNo());
        textJenisMobil.setText(checkin.getJenisMobil()  + " " + checkin.getMerkMobil());
        textWarnaMobil.setText(checkin.getWarnaMobil());
        textEmail.setText(checkin.getEmailCustomer());
    }

    public void setCheckin(String dropPoint, String platNo, String carType, String merk, String email, String warna) {
        checkin.setDropPoint(dropPoint);
        checkin.setPlatNo(platNo);
        checkin.setJenisMobil(carType);
        checkin.setMerkMobil(merk);
        checkin.setEmailCustomer(email);
        checkin.setWarnaMobil(warna);
        init();
    }

}
