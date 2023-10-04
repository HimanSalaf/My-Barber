package com.example.mybarber;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentFragment extends Fragment {
    private static final int PAYHERE_REQUEST = 1001;
    private double totalPrice;

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Retrieve data from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String totalPriceString = sharedPreferences.getString("totalPrice", "0.00");
        totalPrice = Double.parseDouble(totalPriceString);
        TextView paymentAmountTextView = view.findViewById(R.id.tv_payment_amount);
        paymentAmountTextView.setText(getString(R.string.payment_amount, totalPrice));

        // Initialize the Pay button
        Button payButton = view.findViewById(R.id.btn_pay);
        payButton.setOnClickListener(v -> initiatePayment());
        return view;
    }

    private void initiatePayment() {
        InitRequest req = new InitRequest();
        req.setMerchantId("1223123");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(totalPrice);             // Final Amount to be charged
        req.setOrderId("230000123");        // Unique Reference ID
        req.setItemsDescription("Barber Services");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Abulkalam");
        req.getCustomer().setLastName("Himan");
        req.getCustomer().setEmail("hmn@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(requireContext(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID e.g. "11001"
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {
                        msg ="Payment Successful";
                    } else {
                        msg ="Payment Failed";
                    }
                } else {
                    msg = "no response";
                }
                Log.d(TAG, msg);
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    Toast.makeText(requireContext(), "User exits from Payment", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "User canceled the request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}






