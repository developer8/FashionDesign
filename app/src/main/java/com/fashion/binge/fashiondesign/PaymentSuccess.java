package com.fashion.binge.fashiondesign;import android.content.Intent;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.view.View;import android.widget.Button;public class PaymentSuccess extends AppCompatActivity {    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_payment_success);        Button backToStores = (Button) findViewById(R.id.back_to_stores);        assert backToStores != null;        backToStores.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                Intent intent=new Intent(PaymentSuccess.this,Homepage.class);                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);                startActivity(intent);            }        });    }}