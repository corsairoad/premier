package valet.digikom.com.valetparking.domain;

import android.content.Context;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.PageList;

/**
 * Created by DIGIKOM-EX4 on 12/23/2016.
 */

public class ValetWizardModel extends AbstractWizardModel {

    private Context context;

    public ValetWizardModel(Context context, Context context1) {
        super(context);
        this.context = context1;
    }

    @Override
    protected PageList onNewRootPageList() {
        return null;
    }
}
