package valet.digikom.com.valetparking.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import java.util.Arrays;
import java.util.List;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.CarTypeAdapter;
import valet.digikom.com.valetparking.domain.Checkin;

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
    private EditText inputDropPoint;
    private EditText inputPlatNo;
    private EditText inputCartype;
    private EditText inputMerk;
    private EditText inputEmail;
    private EditText inputColor;
    InputFilter[] filters = new InputFilter[]{new InputFilter.AllCaps()};
    private OnRegsitrationValid onRegsitrationValid;


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
        inputPlatNo.setFilters(filters);
        inputCartype = (EditText) view.findViewById(R.id.input_car_type);
        inputCartype.setFilters(filters);
        inputMerk = (EditText) view.findViewById(R.id.input_merk_mobil);
        inputMerk.setFilters(filters);
        inputEmail = (EditText) view.findViewById(R.id.input_email);
        inputColor = (EditText) view.findViewById(R.id.input_color);
        inputColor.setFilters(filters);

        btnCarType = (ImageButton) view.findViewById(R.id.btn_dropdown_cartype);
        btnCarType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> carTypeList = Arrays.asList(getContext().getResources().getStringArray(R.array.car_type_array));
                final CarTypeAdapter adapter = new CarTypeAdapter(getContext(), carTypeList);
                DialogPlus dialogPlus  = DialogPlus.newDialog(getContext())
                        .setContentHolder(new ListHolder())
                        .setAdapter(adapter)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                inputCartype.setText((String)adapter.getItem(position));
                                dialog.dismiss();
                            }
                        })
                        .setExpanded(true)
                        .create();
                dialogPlus.show();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onRegsitrationValid = (OnRegsitrationValid) context;
    }

    public boolean isFormValid() {
        boolean allValid = true;
        EditText[] editTexts = {inputDropPoint, inputPlatNo, inputCartype, inputEmail, inputColor};

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
        ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
        //reviewFragment.setCheckin(dropPoint, platNo,carType,merk,email,color);
        onRegsitrationValid.setCheckin(dropPoint,platNo,carType,merk,email,color);
    }

    public interface OnRegsitrationValid{
        void setCheckin(String dropPoint, String platNo, String carType, String merk, String email, String warna);
    }

}
