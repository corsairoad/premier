package valet.digikom.com.valetparking.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.lukle.clickableareasimage.ImageUtils;
import at.lukle.clickableareasimage.PixelPosition;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.dao.DefectDao;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.util.ValetDbHelper;

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
    private int[] viewCoords;

    private int imageWidthInPx;
    private int imageHeightInPx;
    private List<DefectMaster> defectMasterList;

    public DefectFragment() {
        // Required empty public constructor
    }

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
        new DefectTask().execute();
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
                return false;
            }
        });

        setImage();
        //getImageDimensions(choosenImageView);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDefectDrawingListener = (OnDefectDrawingListener) context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //onDefectDrawingListener.onDefectDrawing(false);
        float[] i = getPointerCoords(choosenImageView, event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = i[0];
                downy = i[1];
                //getDefect(event.getX(), (event.getY()));
                //ReviewFragment.reviewFragment.setDefectMasterList(getDefectMaster((int) event.getX(), (int) (event.getY())));
                onDefectDrawingListener.onDefectDrawing(getDefectMaster((int) event.getX(), (int) (event.getY())));
                break;
            case MotionEvent.ACTION_MOVE:
                upx = i[0];
                upy = i[1];
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = i[0];
                upy = i[1];
                canvas.drawLine(downx, downy, upx, upy, paint);
                choosenImageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        //ReviewFragment.reviewFragment.setImageDefect(alteredBitmap);
        onDefectDrawingListener.setImageDefect(alteredBitmap);
        return true;
    }

    private void setImage() {
        try {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int displayWidth = display.getWidth();
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.car_new, bmpFactoryOptions);
            int width = bmpFactoryOptions.outWidth;

            if (width > displayWidth) {
                int widthRatio = Math.round((float) width / (float) displayWidth);
                bmpFactoryOptions.inSampleSize = widthRatio;
            }

            bmpFactoryOptions.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.car_new, bmpFactoryOptions);
            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.RGB_565);
            canvas = new Canvas(alteredBitmap);
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            matrix = new Matrix();
            canvas.drawBitmap(bmp, matrix, paint);
            choosenImageView.setImageBitmap(alteredBitmap);
            choosenImageView.setOnTouchListener(this);

            viewCoords = new int[2];
            choosenImageView.getLocationOnScreen(viewCoords);

            this.imageWidthInPx = (int)((float)bmp.getWidth() / Resources.getSystem().getDisplayMetrics().density);
            this.imageHeightInPx = (int)((float)bmp.getHeight() / Resources.getSystem().getDisplayMetrics().density);
        } catch (Exception e) {
            Log.v("ERROR", e.toString());
        }
    }

    private void clear() {
        canvas.drawColor(Color.WHITE);
        setImage();
        ReviewFragment.reviewFragment.clearImageDefect();
    }

    @Override
    public void onClick(View view) {
        clear();
    }

    public interface OnDefectDrawingListener {
        void onDefectDrawing(List<DefectMaster> defectMasters);
        void setImageDefect(Bitmap bitmap);
    }

    final float[] getPointerCoords(ImageView view, MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        view.getImageMatrix().invert(matrix);
        matrix.postTranslate(view.getScrollX(), view.getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }

    private List<DefectMaster> getDefectMaster(int mX, int mY) {
        List<DefectMaster> defectMasters = new ArrayList<>();
        Iterator<DefectMaster> i = this.defectMasterList.iterator();
        while (i.hasNext()) {
            DefectMaster dm = i.next();
            int x = (int)dm.getAttributes().getxAxis();
            int w = (int) dm.getAttributes().getImgWidth();
            int y = (int) dm.getAttributes().getyAxis();
            int h = (int) dm.getAttributes().getImgHeight();

            if (isBetween(x, x + w,mX) && isBetween(y, y + h, mY)) {
                defectMasters.add(dm);
            }
        }
        return defectMasters;
    }

    private boolean isBetween(int start, int end, int actual) {
        return start <= actual && actual <= end;
    }

    private class DefectTask extends AsyncTask<Integer, Void, List<DefectMaster>> {

        @Override
        protected List<DefectMaster> doInBackground(Integer... integers) {
             return DefectDao.getInstance(new ValetDbHelper(getContext())).getAllDeffects();
        }

        @Override
        protected void onPostExecute(List<DefectMaster> defectMasters) {
            super.onPostExecute(defectMasters);
            defectMasterList = defectMasters;
        }
    }
}
