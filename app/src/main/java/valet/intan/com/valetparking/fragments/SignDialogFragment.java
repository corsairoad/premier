package valet.intan.com.valetparking.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import valet.intan.com.valetparking.R;

/**
 * Created by DIGIKOM-EX4 on 1/28/2017.
 */

public class SignDialogFragment extends DialogFragment implements View.OnClickListener {

    SignaturePad signPad;
    Button btnClear;
    Button btnDone;
    OnDialogSignListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnDialogSignListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_sign,container,false);
        signPad = (SignaturePad) view.findViewById(R.id.signature_pad);
        btnClear = (Button) view.findViewById(R.id.btn_reset_sign);
        btnDone = (Button) view.findViewById(R.id.btn_done_sig);
        btnClear.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == btnClear) {
            signPad.clear();
        }else {
            if (signPad.isEmpty()) {
                Toast.makeText(getContext(),"Please sign to continue transaction.", Toast.LENGTH_SHORT).show();
                return;
            }

            listener.setBitMapSign(signPad.getSignatureBitmap());

        }
    }

    public interface OnDialogSignListener {
        void setBitMapSign(Bitmap bitMapSign);
    }
}
