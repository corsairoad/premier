package valet.digikom.com.valetparking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.adapter.PagerCheckinAdapter;
import valet.digikom.com.valetparking.dao.CheckinDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.fragments.DefectFragment;
import valet.digikom.com.valetparking.fragments.ReviewFragment;
import valet.digikom.com.valetparking.fragments.StepOneFragmet;
import valet.digikom.com.valetparking.fragments.StepThreeFragment;
import valet.digikom.com.valetparking.fragments.StepTwoFragment;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.CustomViewPager;
import valet.digikom.com.valetparking.util.ValetDbHelper;

public class AddCarActivity extends ActionBarActivity implements StepOneFragmet.OnRegsitrationValid, StepTwoFragment.OnDefectSelectedListener,
                StepThreeFragment.OnStuffSelectedListener, DefectFragment.OnDefectDrawingListener{

    //private ViewPager mPager;
    CustomViewPager mPager;
    private Button mNextButton;
    private Button mPrevButton;
    CircleIndicator indicator;
    private PagerCheckinAdapter checkinAdapter;
    int position = -1;
    int totalPages = -1;
    boolean isCanScroll;
    Checkin checkin = new Checkin();
    CoordinatorLayout coordinatorLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(4);
        checkinAdapter = new PagerCheckinAdapter(getSupportFragmentManager());
        checkinAdapter.addFragment(StepOneFragmet.newInstance(null, null), "" );
        //checkinAdapter.addFragment(StepTwoFragment.newInstance(null, null), "");
        checkinAdapter.addFragment(DefectFragment.newInstance(null,null),"");
        checkinAdapter.addFragment(StepThreeFragment.newInstance(null, null), "");
        checkinAdapter.addFragment(valet.digikom.com.valetparking.fragments.ReviewFragment.newInstance(null, null, checkin), "");
        mPager.setAdapter(checkinAdapter);

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                StepOneFragmet oneFragmet = (StepOneFragmet) checkinAdapter.getItem(0);
                if (position == 1) {
                        if (!oneFragmet.isFormValid()) {
                            mPager.setCurrentItem(0);
                        }else {
                            oneFragmet.setCheckIn();
                        }
                }
            }

            @Override
            public void onPageSelected(int position) {
                updateBottomBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING){
                    if (mPager.getCurrentItem() == 0) {
                        StepOneFragmet oneFragmet = (StepOneFragmet) checkinAdapter.getItem(0);
                        isCanScroll = oneFragmet.isFormValid();
                    }
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == totalPages) {
                    ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
                    if (reviewFragment.ispadSigned()) {
                        submitCheckin(reviewFragment.getSignatureBmp(), reviewFragment.getCheckin(), reviewFragment.getEntryCheckinContainer());
                    }else {
                        Snackbar sb =  Snackbar.make(coordinatorLayout,"Signature can't be empty", Snackbar.LENGTH_SHORT);
                        View v = sb.getView();
                        TextView text = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        text.setTextColor(Color.RED);
                        sb.show();
                    }
                } else {
                    mPager.setCurrentItem(position + 1);
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(position - 1);
            }
        });

        updateBottomBar();
    }

    private void submitCheckin(final Bitmap bmp, final Checkin checkin, final EntryCheckinContainer checkinContainer) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<EntryCheckinResponse> call = apiEndpoint.postCheckin(checkinContainer);
                call.enqueue(new Callback<EntryCheckinResponse>() {
                    @Override
                    public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                        if (response != null && response.body() != null) {
                            EntryCheckinResponse res = response.body();
                            Log.d("Post checkin success: ", res.getData().getType());
                        }
                    }

                    @Override
                    public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {
                        Log.d("Post checkin error: ", t.getMessage());
                    }
                });
            }
        });

      new Thread(new Runnable() {
          @Override
          public void run() {
              ValetDbHelper valetDbHelper = new ValetDbHelper(AddCarActivity.this);
              CheckinDao checkinDao = CheckinDao.newInstance(valetDbHelper, AddCarActivity.this);
              checkinDao.addCheckIn(checkin,bmp);
              startActivity(new Intent(AddCarActivity.this, Main2Activity.class));
              finish();
          }
      }).run();
    }

    private void updateBottomBar() {
        position = mPager.getCurrentItem();
        totalPages = checkinAdapter.getCount()-1;

        if (position == totalPages) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        }else {
            mNextButton.setText(R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            mNextButton.setTextColor(Color.BLACK);
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void setCheckin(String dropPoint, String platNo, String carType, String merk, String email, String warna) {
        ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
        reviewFragment.setCheckin(dropPoint, platNo, carType, merk, email, warna);
    }

    @Override
    public void onDefectSelected(String defect, DefectMaster defectMaster) {
        ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
        reviewFragment.selectDefect(defect, defectMaster);
    }

    @Override
    public void onDefectUnselected(String defect, DefectMaster defectMaster) {
        ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
        reviewFragment.unSelectDefect(defect, defectMaster);
    }

    @Override
    public void onStuffSelected(String stuff, AdditionalItems items) {
        ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
        reviewFragment.onSelectSuff(stuff, items);
    }

    @Override
    public void onStuffUnselected(String stuff, AdditionalItems items) {
        ReviewFragment reviewFragment = ReviewFragment.reviewFragment;
        reviewFragment.onUnselectStuff(stuff, items);
    }


    @Override
    public void onDefectDrawing(boolean isPagingEnabled) {
        mPager.setPagingEnabled(isPagingEnabled);
    }
}
