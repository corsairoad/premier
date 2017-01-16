package valet.digikom.com.valetparking.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import valet.digikom.com.valetparking.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DefectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefectFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView choosenImageView;
    Button btnClear;

    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;
    OnDefectDrawingListener onDefectDrawingListener;
    RelativeLayout container;

    public DefectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DefectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DefectFragment newInstance(String param1, String param2) {
        DefectFragment fragment = new DefectFragment();
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
        View view = inflater.inflate(R.layout.fragment_defect, container, false);
        choosenImageView = (ImageView) view.findViewById(R.id.ChoosenImageView);
        btnClear = (Button) view.findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
        container = (ViewGroup) view.findViewById(R.id.container_defect);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                 onDefectDrawingListener.onDefectDrawing(true);
                return false;
            }
        });
        setImage();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDefectDrawingListener = (OnDefectDrawingListener) context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        onDefectDrawingListener.onDefectDrawing(false);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        ReviewFragment.reviewFragment.setImageDefect(alteredBitmap);
        return true;

    }

    private void setImage() {
        try {
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cartoon_car, bmpFactoryOptions);

            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
                    .getHeight(), bmp.getConfig());
            canvas = new Canvas(alteredBitmap);
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            matrix = new Matrix();
            canvas.drawBitmap(bmp, matrix, paint);

            choosenImageView.setImageBitmap(alteredBitmap);

            choosenImageView.setOnTouchListener(this);
        } catch (Exception e) {
            Log.v("ERROR", e.toString());
        }
    }

    private void clear() {
        canvas.drawColor(Color.WHITE);
        setImage();
    }

    @Override
    public void onClick(View view) {
        clear();
    }

    public interface OnDefectDrawingListener {
        void onDefectDrawing(boolean isPagingEnabled);
    }
}
