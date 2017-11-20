package valet.intan.com.valetparking.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.gcacace.signaturepad.views.SignaturePad;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.dao.DropDao;
import valet.intan.com.valetparking.domain.AdditionalItems;
import valet.intan.com.valetparking.domain.CarMaster;
import valet.intan.com.valetparking.domain.Checkin;
import valet.intan.com.valetparking.domain.ColorMaster;
import valet.intan.com.valetparking.domain.DefectMaster;
import valet.intan.com.valetparking.domain.DropPointMaster;
import valet.intan.com.valetparking.domain.EntryCheckin;
import valet.intan.com.valetparking.domain.EntryCheckinContainer;
import valet.intan.com.valetparking.domain.ValetTypeJson;
import valet.intan.com.valetparking.util.PrefManager;
import valet.intan.com.valetparking.util.ValetDbHelper;



public class ReviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private Checkin checkin = new Checkin();
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
    private List<DefectMaster> defectMasterList;
    private List<AdditionalItems> itemsList;
    private CarMaster carMaster;
    private ColorMaster colorMaster;
    private DropPointMaster dropPoint;
    private Bitmap bitmapDefect;
    private ValetTypeJson.Data  valetType;
    private Bitmap signBitmap;
    private String signatureCoordinates;
    private EntryCheckin.Builder builder;

    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reviewFragment = this;

        defectMasterList = new ArrayList<>();
        itemsList = new ArrayList<>();

        if (getArguments() != null) {
            //String mParam1 = getArguments().getString(ARG_PARAM1);
            //String mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getDefualtDropPoint();
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
        Button btnResetSign = (Button) view.findViewById(R.id.btn_reset_sign);
        signPad = (SignaturePad) view.findViewById(R.id.signature_pad);

        btnResetSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signPad.clear();
            }
        });

        Button btnTestJson = (Button) view.findViewById(R.id.test_json);
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

    public ValetTypeJson.Data getValetType() {
        return valetType;
    }

    public void setValetType(ValetTypeJson.Data valetType) {
        this.valetType = valetType;
    }

    private void initStuffs() {
        textStuffs.setText(checkin.stuffsToString());
    }

    public boolean ispadSigned() {
        return !signPad.isEmpty();
    }

    public Bitmap getSignatureBmp() {
        return this.signBitmap;
    }

    public void setSignBitmap(Bitmap signBitmap) {
        this.signBitmap = signBitmap;
    }

    public String getSignatureCoordinates() {
        return signatureCoordinates;
    }

    public void setSignatureCoordinates(String signatureCoordinates) {
        this.signatureCoordinates = signatureCoordinates;
    }

    public Checkin getCheckin() {
        return checkin;
    }

    public void setCarMaster(CarMaster carMaster) {
        this.carMaster = carMaster;
    }

    public List<DefectMaster> getDefectMasterList() {
        return defectMasterList;
    }

    public void setDefectMasterList(List<DefectMaster> defectMasterList) {
        HashSet<DefectMaster> set = new HashSet<>();
        set.addAll(defectMasterList);

        this.defectMasterList.clear();
        this.defectMasterList.addAll(set);
    }


    public void setColorMaster(ColorMaster colorMaster) {
        this.colorMaster = colorMaster;
    }

    public List<AdditionalItems> getItemsList() {
        return itemsList;
    }

    public void setDropPoint(DropPointMaster dropPoint) {
        this.dropPoint = dropPoint;
    }

    public void setImageDefect(Bitmap bitmap) {
        bitmapDefect = bitmap;
        imgDefect.setImageBitmap(bitmap);
    }

    public Bitmap getBitmapDefect() {
        return bitmapDefect;
    }

    public void clearImageDefect() {
        imgDefect.setImageBitmap(null);
    }

    public EntryCheckin.Builder getBuilder() {
        return builder;
    }

    public EntryCheckinContainer getEntryCheckinContainer() {
        return buildCheckinEntry();
    }

    private EntryCheckinContainer buildCheckinEntry() {
        builder = new EntryCheckin.Builder();
        builder.setAttribute(dropPoint,textPlatNo.getText().toString(), carMaster, colorMaster,textEmail.getText().toString(),
                bitmapDefect, signBitmap, valetType, getSignatureCoordinates(), getDefectMasterList());

        builder.setRelationShip(getDefectMasterList(), getItemsList());

        EntryCheckin entryCheckin = builder.build();
        EntryCheckinContainer entryCheckinContainer = new EntryCheckinContainer();
        entryCheckinContainer.setEntryCheckin(entryCheckin);

        return entryCheckinContainer;
    }

    private void getDefualtDropPoint() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dropIdDefault = PrefManager.getInstance(getContext()).getIdDefaultDropPoint();
                if (dropIdDefault != null) {
                    final DropPointMaster dropPointMaster = DropDao.getInstance(new ValetDbHelper(getContext())).getDropPointById(Integer.valueOf(dropIdDefault));
                    if (dropPointMaster == null) {
                        return;
                    }
                    setDropPoint(dropPointMaster);
                }
            }
        }).run();
    }


}
