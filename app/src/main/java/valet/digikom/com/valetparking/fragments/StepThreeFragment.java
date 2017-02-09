package valet.digikom.com.valetparking.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.ListStuffAdapter;
import valet.digikom.com.valetparking.dao.ItemsDao;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepThreeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView mListviewStuff;
    List<AdditionalItems> listItems = new ArrayList<>();
    ImageButton btnAddStuff;
    EditText inputStuff;
    ListStuffAdapter adapter;
    private OnStuffSelectedListener onStuffListener;
    ArrayList<Integer> selectedPositions = new ArrayList<>();

    public StepThreeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepThreeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepThreeFragment newInstance(String param1, String param2) {
        StepThreeFragment fragment = new StepThreeFragment();
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
        View view = inflater.inflate(R.layout.fragment_step_three, container, false);
        mListviewStuff = (ListView) view.findViewById(R.id.listview_stuff);
        inputStuff = (EditText) view.findViewById(R.id.input_stuff);
        btnAddStuff = (ImageButton) view.findViewById(R.id.btn_add_stuff);
        btnAddStuff.setOnClickListener(this);

        new FetchItems().execute();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onStuffListener = (OnStuffSelectedListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        String input = inputStuff.getText().toString();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(getContext(), "Additional info empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        AdditionalItems.AdditionalItemMaster item = new AdditionalItems.AdditionalItemMaster();
        item.setId(listItems.size() + 1);
        item.setName(input);

        AdditionalItems.Attributes attributes = new AdditionalItems.Attributes();
        attributes.setAdditionalItemMaster(item);

        AdditionalItems additionalItems = new AdditionalItems();
        additionalItems.setAttributes(attributes);

        onStuffListener.onStuffSelected(input, additionalItems);

        listItems.add(additionalItems);
        selectedPositions.add(listItems.size()-1);
        adapter.notifyDataSetChanged();
        int post = adapter.getPosition(additionalItems);
        adapter.setPost(post);
        adapter.notifyDataSetChanged();
        inputStuff.setText("");
    }

    public interface OnStuffSelectedListener{
        void onStuffSelected(String stuff, AdditionalItems items);
        void onStuffUnselected(String stuff, AdditionalItems items);
    }

    private class FetchItems extends AsyncTask<Void, Void, List<AdditionalItems>> {

        @Override
        protected List<AdditionalItems> doInBackground(Void... voids) {
            ItemsDao itemsDao = ItemsDao.getInstance(new ValetDbHelper(getContext()));
            return itemsDao.fetchItems();
        }

        @Override
        protected void onPostExecute(List<AdditionalItems> itemsList) {
            if (!itemsList.isEmpty()) {
                listItems.addAll(itemsList);
                adapter = new ListStuffAdapter(getContext(),listItems, onStuffListener);
                mListviewStuff.setAdapter(adapter);
                mListviewStuff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox_defect);
                        if (cb.isChecked()) {
                            cb.setChecked(false);
                        }else {
                            cb.setChecked(true);
                        }
                    }
                });
            }
        }
    }
}
