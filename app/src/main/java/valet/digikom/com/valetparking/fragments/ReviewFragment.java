package valet.digikom.com.valetparking.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.CarMaster;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.ColorMaster;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.DropPointMaster;
import valet.digikom.com.valetparking.domain.EntryCheckin;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;

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
    private ImageView imgDefect;
    public static ReviewFragment reviewFragment;
    private Button btnTestJson;
    private List<DefectMaster> defectMasterList;
    private List<AdditionalItems> itemsList;
    private CarMaster carMaster;
    private ColorMaster colorMaster;
    private DropPointMaster dropPoint;
    private Bitmap bitmapDefect;
    private EntryCheckinContainer entryCheckinContainer;


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

        defectMasterList = new ArrayList<>();
        DefectMaster defectMaster = new DefectMaster();
        DefectMaster.DefectAttributes attributes = new DefectMaster.DefectAttributes();
        attributes.setId(1);
        attributes.setDefectDesc("no defects");
        attributes.setHref("");
        attributes.setDefectName("no defects");
        defectMaster.setId(0);
        defectMaster.setAttributes(attributes);
        defectMasterList.add(defectMaster);

        itemsList = new ArrayList<>();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        imgDefect = (ImageView) view.findViewById(R.id.img_defecs_review);
        init();
        btnResetSign = (Button) view.findViewById(R.id.btn_reset_sign);
        signPad = (SignaturePad) view.findViewById(R.id.signature_pad);

        btnResetSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signPad.clear();
            }
        });

        btnTestJson = (Button) view.findViewById(R.id.test_json);
        btnTestJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               buildCheckinEntry();
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

    public void initDefects() {
        textDefects.setText(checkin.defectsToString());
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

    public void selectDefect(String defect, DefectMaster defectMaster) {
        checkin.getDefects().add(defect);
        getDefectMasterList().add(defectMaster);
        initDefects();
    }

    public void unSelectDefect(String defect, DefectMaster defectMaster) {
        checkin.getDefects().remove(defect);
        getDefectMasterList().remove(defectMaster);
        initDefects();
    }

    public void onSelectSuff(String stuff, AdditionalItems items) {
        checkin.getStuffs().add(stuff);
        getItemsList().add(items);
        initStuffs();
    }

    public void onUnselectStuff(String stuff, AdditionalItems items) {
        checkin.getStuffs().remove(stuff);
        getItemsList().remove(items);
        initStuffs();
    }

    private void initStuffs() {
        textStuffs.setText(checkin.stuffsToString());
    }

    public boolean ispadSigned() {
        return !signPad.isEmpty();
    }

    public Bitmap getSignatureBmp() {
        return signPad.getSignatureBitmap();
    }

    public Checkin getCheckin() {
        return checkin;
    }

    public CarMaster getCarMaster() {
        return carMaster;
    }

    public void setCarMaster(CarMaster carMaster) {
        this.carMaster = carMaster;
    }

    public List<DefectMaster> getDefectMasterList() {
        return defectMasterList;
    }

    public void setDefectMasterList(List<DefectMaster> defectMasterList) {
        this.defectMasterList = defectMasterList;
    }

    public ColorMaster getColorMaster() {
        return colorMaster;
    }

    public void setColorMaster(ColorMaster colorMaster) {
        this.colorMaster = colorMaster;
    }

    public List<AdditionalItems> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<AdditionalItems> itemsList) {
        this.itemsList = itemsList;
    }

    public DropPointMaster getDropPoint() {
        return dropPoint;
    }

    public void setDropPoint(DropPointMaster dropPoint) {
        this.dropPoint = dropPoint;
    }

    public void setImageDefect(Bitmap bitmap) {
        bitmapDefect = bitmap;
        imgDefect.setImageBitmap(bitmap);
    }

    public void clearImageDefect() {

    }

    public EntryCheckinContainer getEntryCheckinContainer() {
        return buildCheckinEntry();
    }



    private EntryCheckinContainer buildCheckinEntry() {
        EntryCheckin.Builder builder = new EntryCheckin.Builder();
        builder.setAttribute(dropPoint,textPlatNo.getText().toString(), carMaster, colorMaster,textEmail.getText().toString(),bitmapDefect, signPad.getSignatureBitmap());
        builder.setRelationShip(getDefectMasterList(), getItemsList());
        EntryCheckin entryCheckin = builder.build();
        entryCheckinContainer = new EntryCheckinContainer();
        entryCheckinContainer.setEntryCheckin(entryCheckin);
        Gson gson = new Gson();
        String jsonEntryCheckin = gson.toJson(entryCheckinContainer);
        Log.d("JSON CHECKIN", jsonEntryCheckin);
        return entryCheckinContainer;
    }
}
