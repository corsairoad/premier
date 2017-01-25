package valet.digikom.com.valetparking.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.CarTypeAdapter;
import valet.digikom.com.valetparking.adapter.ColorTypeAdapter;
import valet.digikom.com.valetparking.adapter.DropPointAdapter;
import valet.digikom.com.valetparking.dao.CarDao;
import valet.digikom.com.valetparking.dao.ColorDao;
import valet.digikom.com.valetparking.dao.DropDao;
import valet.digikom.com.valetparking.domain.CarMaster;
import valet.digikom.com.valetparking.domain.ColorMaster;
import valet.digikom.com.valetparking.domain.DropPointMaster;
import valet.digikom.com.valetparking.util.PrefManager;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepOneFragmet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepOneFragmet extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton btnCarType;
    private ImageButton btnColorType;
    private ImageButton btnDropPoint;
    private EditText inputDropPoint;
    private EditText inputPlatNo;
    private EditText inputCartype;
    private EditText inputMerk;
    private EditText inputEmail;
    private EditText inputColor;
    InputFilter[] filters;
    private OnRegsitrationValid onRegsitrationValid;
    private ValetDbHelper dbHelper;
    private List<DropPointMaster> dpMasters = new ArrayList<>();
    private DropPointAdapter adapter;
    private boolean isDefaultDropSet;
    PrefManager prefManager;

    public StepOneFragmet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepOneFragmet.
     */
    // TODO: Rename and change types and number of parameters
    public static StepOneFragmet newInstance(String param1, String param2) {
        StepOneFragmet fragment = new StepOneFragmet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = PrefManager.getInstance(getContext());
        dbHelper = new ValetDbHelper(getContext());
        adapter = new DropPointAdapter(getContext(), dpMasters);

        filters = new InputFilter[]{new InputFilter.AllCaps()};

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_one, container, false);

        inputDropPoint = (EditText) view.findViewById(R.id.input_drop_point);
        inputDropPoint.setFilters(filters);
        inputPlatNo = (EditText) view.findViewById(R.id.input_plat_no);
        inputPlatNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        inputCartype = (EditText) view.findViewById(R.id.input_car_type);
        inputCartype.setFilters(filters);
        inputMerk = (EditText) view.findViewById(R.id.input_merk_mobil);
        inputMerk.setFilters(filters);
        inputEmail = (EditText) view.findViewById(R.id.input_email);
        inputColor = (EditText) view.findViewById(R.id.input_color);
        inputColor.setFilters(filters);

        btnCarType = (ImageButton) view.findViewById(R.id.btn_dropdown_cartype);
        btnColorType = (ImageButton) view.findViewById(R.id.btn_dropdown_color);
        btnDropPoint = (ImageButton) view.findViewById(R.id.btn_drop);

        btnDropPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchDropPointTask().execute();
            }
        });

        initData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onRegsitrationValid = (OnRegsitrationValid) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public boolean isFormValid() {
        boolean allValid = true;
        EditText[] editTexts = {inputDropPoint, inputPlatNo, inputCartype};

        for (EditText editText : editTexts) {
            if (editText != null){
                String val = editText.getText().toString();
                allValid = !TextUtils.isEmpty(val) && allValid;
                if (TextUtils.isEmpty(val)) {
                    editText.setError("This field can't be empty");
                }else {
                    editText.setError(null);
                }
            }
        }
        return allValid;
    }

    public void setCheckIn() {
        String dropPoint = inputDropPoint.getText().toString();
        String platNo = inputPlatNo.getText().toString();
        String carType = inputCartype.getText().toString();
        String merk = inputMerk.getText().toString();
        String email = inputEmail.getText().toString();
        String color = inputColor.getText().toString();
        onRegsitrationValid.setCheckin(dropPoint,platNo,carType,merk,email,color);
    }


    public interface OnRegsitrationValid{
        void setCheckin(String dropPoint, String platNo, String carType, String merk, String email, String warna);
    }

    private class FetchCarsTask extends AsyncTask<Void, Void, List<CarMaster>> {

        @Override
        protected List<CarMaster> doInBackground(Void... voids) {
            return CarDao.getInstance(dbHelper).fetchAllCars();
        }

        @Override
        protected void onPostExecute(List<CarMaster> s) {
            if (!s.isEmpty()) {
                final CarTypeAdapter adapter = new CarTypeAdapter(getContext(), s);
                btnCarType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogPlus dialogPlus  = DialogPlus.newDialog(getContext())
                                .setContentHolder(new GridHolder(3))
                                .setAdapter(adapter)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        CarMaster carMaster = (CarMaster) item;
                                        inputCartype.setText(carMaster.getAttrib().getCarName());
                                        ReviewFragment.reviewFragment.setCarMaster(carMaster);
                                        dialog.dismiss();
                                    }
                                })
                                .setGravity(Gravity.CENTER)
                                .setExpanded(false)
                                .create();
                        dialogPlus.show();
                    }
                });
            }
        }
    }

    private class FetchColorsTask extends AsyncTask<Void, Void, List<ColorMaster>> {

        @Override
        protected List<ColorMaster> doInBackground(Void... voids) {
            return ColorDao.getInstance(dbHelper).fetchColors();
        }

        @Override
        protected void onPostExecute(final List<ColorMaster> colorMasters) {
            if (!colorMasters.isEmpty()) {
                final ColorTypeAdapter adapter = new ColorTypeAdapter(colorMasters, getContext());
                btnColorType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogPlus dialogPlus  = DialogPlus.newDialog(getContext())
                                .setContentHolder(new GridHolder(3))
                                .setAdapter(adapter)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        ColorMaster cm = (ColorMaster) item;
                                        inputColor.setText(cm.getAttrib().getColorName());
                                        ReviewFragment.reviewFragment.setColorMaster(cm);
                                        dialog.dismiss();
                                    }
                                })
                                .setGravity(Gravity.CENTER)
                                .setExpanded(false)
                                .create();
                        dialogPlus.show();
                    }
                });
            }
        }
    }

    private class FetchDropPointTask extends AsyncTask<Void, Void, List<DropPointMaster>> {

        @Override
        protected List<DropPointMaster> doInBackground(Void... voids) {
            return DropDao.getInstance(dbHelper).fetchAllDropPoints();
        }

        @Override
        protected void onPostExecute(List<DropPointMaster> dropPointMasters) {
            if (!dropPointMasters.isEmpty()){
                dpMasters.clear();
                dpMasters.addAll(dropPointMasters);
                adapter.notifyDataSetChanged();
                //final DropPointAdapter adapter = new DropPointAdapter(getContext(), dropPointMasters);
                View v = initFooter();
                DialogPlus dialogPlus  = DialogPlus.newDialog(getContext())
                        .setContentHolder(new GridHolder(2))
                        .setAdapter(adapter)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                DropPointMaster dropPoint = (DropPointMaster) item;
                                inputDropPoint.setText(dropPoint.getAttrib().getDropName());
                                ReviewFragment.reviewFragment.setDropPoint(dropPoint);

                                if (isDefaultDropSet) {
                                    prefManager.setDefaultDropPoint(dropPoint.getAttrib().getDropId());
                                }

                                dialog.dismiss();
                            }
                        })
                        .setGravity(Gravity.CENTER)
                        .setExpanded(false)
                        .setFooter(v)
                        .create();
                dialogPlus.show();
            }else {
                Toast.makeText(getContext(),"Cannot retrieve drop point at this time.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private View initFooter() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.footer_drop_point, null);

        final CheckBox cb = (CheckBox) v.findViewById(R.id.cb_default_droppoint);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isDefaultDropSet = b;
            }
        });

        return v;
    }

    private void initData() {
        new FetchCarsTask().execute();
        new FetchColorsTask().execute();
        getDefualtDropPoint();
    }

    private void getDefualtDropPoint() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int dropIdDefault = prefManager.getIdDefaultDropPoint();
                if (dropIdDefault > 0) {
                    final DropPointMaster dropPointMaster = DropDao.getInstance(new ValetDbHelper(getContext())).getDropPointById(dropIdDefault);
                    if (dropPointMaster == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inputDropPoint.setText(dropPointMaster.getAttrib().getDropName());
                        }
                    });
                }

            }
        }).run();
    }

    private InputFilter getInputFilterPlate() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence character, int start, int end, Spanned spanned, int dStart, int dEnd) {

                for (int a=start; a<end; a++ ) {

                    if (!Character.isLetterOrDigit(character.charAt(a))) {
                        return "";
                    }

                   if (character.length()>1) {

                       if (Character.isLetter(character.charAt(0)) && Character.isDigit(character.charAt(1))) {
                           return character.subSequence(0, 1).toString() + " " + character.charAt(1);
                       }

                       if (Character.isLetter(character.charAt(a)) && Character.isDigit(character.charAt(a+1))) {
                           return "";
                       }

                       if (Character.isLetter(character.charAt(0)) && Character.isLetter(character.charAt(1))){
                           return character.subSequence(0, 2).toString() + " ";
                       }

                   }

                    if (Character.isLetter(character.charAt(a))) {
                        if (spanned.length() >= 1) {
                            if (Character.isDigit(spanned.charAt(spanned.length()-1))) {
                                return "";
                            }
                        }
                    }

                }
                return null;
            }
        };
        return filter;
    }

}
