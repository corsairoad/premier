package valet.intan.com.valetparking.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.adapter.CarTypeAdapter;
import valet.intan.com.valetparking.adapter.ColorTypeAdapter;
import valet.intan.com.valetparking.adapter.DropPointAdapter;
import valet.intan.com.valetparking.adapter.ListValetTypeAdapter;
import valet.intan.com.valetparking.dao.CarDao;
import valet.intan.com.valetparking.dao.ColorDao;
import valet.intan.com.valetparking.dao.DropDao;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.dao.ValetTypeDao;
import valet.intan.com.valetparking.domain.CarMaster;
import valet.intan.com.valetparking.domain.ColorMaster;
import valet.intan.com.valetparking.domain.DropPointMaster;
import valet.intan.com.valetparking.domain.ValetTypeJson;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.ChangeBgColorListener;
import valet.intan.com.valetparking.util.LicensePlateRegexInputFilter;
import valet.intan.com.valetparking.util.PrefManager;
import valet.intan.com.valetparking.util.ValetDbHelper;


public class StepOneFragmet extends Fragment implements View.OnClickListener, ChangeBgColorListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
    private InputFilter[] filters;
    private OnRegsitrationValid onRegsitrationValid;
    private ValetDbHelper dbHelper;
    private List<DropPointMaster> dpMasters = new ArrayList<>();
    private DropPointAdapter adapter;
    private boolean isDefaultDropSet;
    private PrefManager prefManager;
    private List<ValetTypeJson.Data> valetTypeJsonList = new ArrayList<>();
    private Spinner spValetType;
    private ListValetTypeAdapter valetTypeAdapter;
    private TextInputLayout tilDropPoint;
    private TextInputLayout tilCarType;
    private TextInputLayout tilColorType;
    private OnValetTypeSelectedListener valetTypeSelectedListener;
    private InputMethodManager imm;
    private RadioGroup rGroupValetType;
    private LinearLayout bgRegistration;

    public StepOneFragmet() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = PrefManager.getInstance(getContext());
        dbHelper = new ValetDbHelper(getContext());
        adapter = new DropPointAdapter(getContext(), dpMasters);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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

        bgRegistration = (LinearLayout) view.findViewById(R.id.bg_registration);

        tilDropPoint = (TextInputLayout) view.findViewById(R.id.til_drop_point);
        tilCarType = (TextInputLayout) view.findViewById(R.id.til_car_type);
        tilColorType = (TextInputLayout) view.findViewById(R.id.til_color_type);

        tilDropPoint.setOnClickListener(this);
        tilCarType.setOnClickListener(this);
        tilColorType.setOnClickListener(this);

        inputDropPoint = (EditText) view.findViewById(R.id.input_drop_point);
        inputDropPoint.setFilters(filters);
        inputPlatNo = (EditText) view.findViewById(R.id.input_plat_no);
        inputPlatNo.requestFocus();
        inputPlatNo.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new LicensePlateRegexInputFilter(), new InputFilter.LengthFilter(15)});
        inputCartype = (EditText) view.findViewById(R.id.input_car_type);
        inputCartype.setFilters(filters);
        inputMerk = (EditText) view.findViewById(R.id.input_merk_mobil);
        inputMerk.setFilters(filters);
        inputEmail = (EditText) view.findViewById(R.id.input_email);
        inputColor = (EditText) view.findViewById(R.id.input_color);
        inputColor.setFilters(filters);

        rGroupValetType = (RadioGroup) view.findViewById(R.id.rgroup_valet_type);
        rGroupValetType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ValetTypeJson.Data data = findValetTypeById(checkedId);
                if (data != null) {
                    valetTypeSelectedListener.onValetTypeSelected(data);
                }
            }
        });

        spValetType = (Spinner) view.findViewById(R.id.spinner_valet_type_x);
        spValetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                valetTypeSelectedListener.onValetTypeSelected(valetTypeJsonList.get(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        valetTypeAdapter = new ListValetTypeAdapter(getContext(),valetTypeJsonList);
        spValetType.setAdapter(valetTypeAdapter);

        btnCarType = (ImageButton) view.findViewById(R.id.btn_dropdown_cartype);
        btnColorType = (ImageButton) view.findViewById(R.id.btn_dropdown_color);
        btnDropPoint = (ImageButton) view.findViewById(R.id.btn_drop);

        // drop point
        btnDropPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                new FetchDropPointTask().execute();
            }
        });
        inputDropPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                new FetchDropPointTask().execute();
            }
        });

        // car type
        btnCarType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                new FetchCarsTask().execute();
            }
        });
        inputCartype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                new FetchCarsTask().execute();
            }
        });

        // color type
        btnColorType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                new FetchColorsTask().execute();
            }
        });
        inputColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                new FetchColorsTask().execute();
            }
        });

        getDefualtDropPoint();
        //downloadValetType();
        new FetchValetTypeTask().execute();
        return view;
    }

    public void changeBgColor(String index) {
        int color = Color.parseColor("#ffffff");
        if ("exclusive".equals(index.toLowerCase())) {
            color = Color.parseColor("#ffea00");
        }
        bgRegistration.setBackgroundColor(color);
    }

    private ValetTypeJson.Data findValetTypeById(int checkedId) {
        for (ValetTypeJson.Data data : valetTypeJsonList) {
            if (data.getAttrib().getId() == checkedId) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onRegsitrationValid = (OnRegsitrationValid) context;
        valetTypeSelectedListener = (OnValetTypeSelectedListener) context;
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
        EditText[] editTexts = {inputDropPoint, inputPlatNo, inputCartype, inputColor};

        for (EditText editText : editTexts) {
            if (editText != null){
                String val = editText.getText().toString();
                allValid = !TextUtils.isEmpty(val) && allValid;
                int id = editText.getId();

                if (TextUtils.isEmpty(val)) {
                    editText.setError("This field can't be empty");
                }else {
                    editText.setError(null);
                }

                if (id == R.id.input_plat_no) {
                    allValid = !checkIfAlreadyCheckedIn(val, editText) && checkLength(val,editText);
                }
            }
        }
        return allValid;
    }

    private boolean checkLength(String val, EditText editText) {
        if (val.length() > 15) {
            editText.setError("License plate too long");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("License plate too long")
                    .setMessage("Maximum license plate length is 15 characters")
                    .setIcon(R.drawable.ic_error_outline)
                    .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return false;
        }
        return true;
    }

    private boolean checkIfAlreadyCheckedIn(String platNo, EditText editText) {
        if (EntryDao.getInstance(getContext()).isAlreadyCheckedIn(platNo)) {
            editText.setError("This car is already checked in");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("Duplicate license plate")
                    .setMessage("This car is already checked in")
                    .setIcon(R.drawable.ic_error_outline)
                    .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return true;
        }
        return false;
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.til_drop_point:
                new FetchDropPointTask().execute();
                break;
            case R.id.til_car_type:
                new FetchCarsTask().execute();
                break;
            case R.id.til_color_type:
                new FetchColorsTask().execute();
                break;
            default:break;
        }
        /*
        if (view == tilDropPoint) {
            new FetchDropPointTask().execute();
        }else if(view == tilCarType) {
            new FetchCarsTask().execute();
        }else if (view == tilColorType) {
            new FetchColorsTask().execute();
        }
        */
    }

    @Override
    public void onValetTypeChange(String index) {
        changeBgColor(index);
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

    private void getDefualtDropPoint() {

        String dropIdDefault = prefManager.getIdDefaultDropPoint();
        String dropName = prefManager.getDefaultDropPointName();
        inputDropPoint.setText(dropName);
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dropIdDefault = prefManager.getIdDefaultDropPoint();
                String dropName = prefManager.getDefaultDropPointName();
                inputDropPoint.setText(dropName);

                if (dropIdDefault != null) {
                    final DropPointMaster dropPointMaster = DropDao.getInstance(new ValetDbHelper(getContext())).getDropPointById(Integer.valueOf(dropIdDefault));
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
        }).run(); */
    }

    private InputFilter getInputFilterPlate() {
        return new InputFilter() {
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

    }

    private void downloadValetType() {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                Call<ValetTypeJson> call = apiEndpoint.getValetType(token);
                call.enqueue(new Callback<ValetTypeJson>() {
                    @Override
                    public void onResponse(Call<ValetTypeJson> call, Response<ValetTypeJson> response) {
                        if (response != null && response.body() != null) {
                            valetTypeSelectedListener.onValetTypeSelected(response.body().getListData().get(0));

                            valetTypeJsonList.clear();
                            valetTypeJsonList.addAll(response.body().getListData());
                            valetTypeAdapter = new ListValetTypeAdapter(getContext(),valetTypeJsonList);
                            spValetType.setAdapter(valetTypeAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ValetTypeJson> call, Throwable t) {

                    }
                });
            }
        }, getContext());
    }

    private class FetchValetTypeTask extends AsyncTask<Void,Void,List<ValetTypeJson.Data>> {

        @Override
        protected List<ValetTypeJson.Data> doInBackground(Void... params) {
            ValetTypeDao vDao = ValetTypeDao.getInstance(getContext());
            return vDao.getListData();
        }

        @Override
        protected void onPostExecute(List<ValetTypeJson.Data> datas) {
            super.onPostExecute(datas);
            if (datas != null && !datas.isEmpty()) {
                valetTypeSelectedListener.onValetTypeSelected(datas.get(0));
                valetTypeJsonList.clear();
                valetTypeJsonList.addAll(datas);
                valetTypeAdapter.notifyDataSetChanged();
                setupRadioGroudValetType(datas);
            }
        }
    }

    private void setupRadioGroudValetType(List<ValetTypeJson.Data> datas) {
        for(ValetTypeJson.Data data : datas) {
            String valetTypeName = data.getAttrib().getValetTypeName();
            RadioButton rb = new RadioButton(getContext());
            rb.setId(data.getAttrib().getId());
            rb.setText(valetTypeName);
            rb.setTextSize(25);
            if ("regular".equals(valetTypeName.toLowerCase())) {
                rb.setChecked(true);
            }else {
                rb.setTextColor(Color.parseColor("#dd2c00"));
            }

            rGroupValetType.addView(rb);
        }
    }

    public interface OnValetTypeSelectedListener{
        void onValetTypeSelected(ValetTypeJson.Data data);
    }

    private void closeKeyboard() {
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }

}
