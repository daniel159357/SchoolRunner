package com.example.schoolrunner.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.OrderDao;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.utils.CurrentStudentUtils;
import com.example.schoolrunner.utils.RouteUtils;

import java.util.Date;
import java.util.List;

public class CreateOrderActivity extends AppCompatActivity {

    private ImageView ivBack;
    private Spinner spOrderType, spDeliveryTime;
    private Spinner spPickupAddress, spDeliveryAddress; // Added campus location spinners
    private EditText etPickupAddress, etItemDescription, etDeliveryAddress, etContact;
    private ImageButton btnDecrease, btnIncrease;
    private TextView tvPrice, tvPriceHint;
    private CheckBox cbAgreement;
    private Button btnSubmit;

    // Order type options
    private final String[] orderTypes = {"Express Pickup", "Food Delivery", "Document Printing", "Other Services"};
    // Delivery time options
    private final String[] deliveryTimes = {"Within 30 minutes", "Within 1 hour", "Today"};

    // Currently selected order type
    private String selectedOrderType = "Express Pickup";
    // Currently selected delivery time
    private String selectedDeliveryTime = "Within 30 minutes";

    // Current price (unit: yuan)
    private double currentPrice = 5.0;
    // Minimum price
    private final double MIN_PRICE = 3.0;
    // Maximum price
    private final double MAX_PRICE = 50.0;
    // Price step
    private final double PRICE_STEP = 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        initView();
        initListener();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        spOrderType = findViewById(R.id.sp_order_type);
        spDeliveryTime = findViewById(R.id.sp_delivery_time);
        etPickupAddress = findViewById(R.id.et_pickup_address);
        etItemDescription = findViewById(R.id.et_item_description);
        etDeliveryAddress = findViewById(R.id.et_delivery_address);
        etContact = findViewById(R.id.et_contact);
        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        tvPrice = findViewById(R.id.tv_price);
        tvPriceHint = findViewById(R.id.tv_price_hint);
        cbAgreement = findViewById(R.id.cb_agreement);
        btnSubmit = findViewById(R.id.btn_submit);
        
        // Initialize campus location spinners
        spPickupAddress = findViewById(R.id.sp_pickup_address);
        spDeliveryAddress = findViewById(R.id.sp_delivery_address);

        // Initialize order type spinner
        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, orderTypes);
        orderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrderType.setAdapter(orderTypeAdapter);

        // Initialize delivery time spinner
        ArrayAdapter<String> deliveryTimeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, deliveryTimes);
        deliveryTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDeliveryTime.setAdapter(deliveryTimeAdapter);
        
        // Initialize campus location spinners
        // Add "Select a location" as first item
        String[] pickupOptions = new String[RouteUtils.CAMPUS_PLACES.size() + 1];
        pickupOptions[0] = "Select a campus location";
        for (int i = 0; i < RouteUtils.CAMPUS_PLACES.size(); i++) {
            pickupOptions[i + 1] = RouteUtils.CAMPUS_PLACES.get(i);
        }
        
        ArrayAdapter<String> pickupAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, pickupOptions);
        pickupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPickupAddress.setAdapter(pickupAdapter);
        
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, pickupOptions);
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDeliveryAddress.setAdapter(deliveryAdapter);

        // Initialize price display
        updatePriceView();
    }

    private void initListener() {
        // Back button
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Order type selection listener
        spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrderType = orderTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default select first item
                selectedOrderType = orderTypes[0];
            }
        });

        // Delivery time selection listener
        spDeliveryTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeliveryTime = deliveryTimes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default select first item
                selectedDeliveryTime = deliveryTimes[0];
            }
        });
        
        // Pickup location selection listener
        spPickupAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Set the selected campus location to the EditText
                    etPickupAddress.setText(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        
        // Delivery location selection listener
        spDeliveryAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Set the selected campus location to the EditText
                    etDeliveryAddress.setText(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Price decrease button
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPrice > MIN_PRICE) {
                    currentPrice -= PRICE_STEP;
                    updatePriceView();
                }
            }
        });

        // Price increase button
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPrice < MAX_PRICE) {
                    currentPrice += PRICE_STEP;
                    updatePriceView();
                }
            }
        });

        // Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    createOrder();
                }
            }
        });
    }

    /**
     * Update price display
     */
    private void updatePriceView() {
        // Update price display
        tvPrice.setText("¥" + String.format("%.2f", currentPrice));

        // Update price hint
        double basePrice = 3.0; // Base runner fee
        double tipPrice = currentPrice - basePrice; // Tip part
        tvPriceHint.setText("Runner fee " + String.format("%.2f", basePrice) + " yuan + Tip " 
                + String.format("%.2f", tipPrice) + " yuan");

        // Adjust decrease button state according to current price
        btnDecrease.setEnabled(currentPrice > MIN_PRICE);
        // Adjust increase button state according to current price
        btnIncrease.setEnabled(currentPrice < MAX_PRICE);
    }

    /**
     * Validate input
     */
    private boolean validateInput() {
        // Validate pickup address
        if (TextUtils.isEmpty(etPickupAddress.getText())) {
            Toast.makeText(this, "Please enter pickup address", Toast.LENGTH_SHORT).show();
            etPickupAddress.requestFocus();
            return false;
        }

        // Validate item description
        if (TextUtils.isEmpty(etItemDescription.getText())) {
            Toast.makeText(this, "Please enter item description", Toast.LENGTH_SHORT).show();
            etItemDescription.requestFocus();
            return false;
        }

        // Validate delivery address
        if (TextUtils.isEmpty(etDeliveryAddress.getText())) {
            Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show();
            etDeliveryAddress.requestFocus();
            return false;
        }

        // Validate contact
        if (TextUtils.isEmpty(etContact.getText())) {
            Toast.makeText(this, "Please enter contact information", Toast.LENGTH_SHORT).show();
            etContact.requestFocus();
            return false;
        }

        // Validate user agreement
        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "Please read and agree to the user agreement", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Create order
     */
    private void createOrder() {
        // Create order object
        Order order = new Order();

        // Set student ID (sender ID)
        order.setStudentId(CurrentStudentUtils.getCurrentStudent().getId());

        // Set order type
        order.setOrderType(selectedOrderType);
        
        // Set item description
        order.setItemDescription(etItemDescription.getText().toString());

        // Set pickup address
        String pickupAddress = etPickupAddress.getText().toString();
        order.setPickupAddress(pickupAddress);

        // Set delivery address
        String deliveryAddress = etDeliveryAddress.getText().toString();
        order.setDeliveryAddress(deliveryAddress);
        
        // Check if both addresses are campus places and set route information
        if (RouteUtils.isCampusPlace(pickupAddress) && RouteUtils.isCampusPlace(deliveryAddress)) {
            List<String> route = RouteUtils.getRoute(pickupAddress, deliveryAddress);
            if (route != null) {
                String routeInfo = RouteUtils.formatRouteString(route);
                order.setRouteInfo(routeInfo);
            }
        }

        // Set contact
        order.setContact(etContact.getText().toString());

        // Set price
        order.setPrice(currentPrice);

        // Set delivery time
        Date deliveryTime = new Date();
        if (selectedDeliveryTime.equals("Within 30 minutes")) {
            // Within 30 minutes, delivery time is current time + 30 minutes
            deliveryTime.setTime(deliveryTime.getTime() + 30 * 60 * 1000);
        } else if (selectedDeliveryTime.equals("Within 1 hour")) {
            // Within 1 hour, delivery time is current time + 1 hour
            deliveryTime.setTime(deliveryTime.getTime() + 60 * 60 * 1000);
        } else if (selectedDeliveryTime.equals("Today")) {
            // Today, delivery time is 23:59:59 of today
            deliveryTime.setHours(23);
            deliveryTime.setMinutes(59);
            deliveryTime.setSeconds(59);
        }
        order.setDeliveryTime(deliveryTime);

        // Call DAO to create order
        Result<Order> result = OrderDao.createOrder(order);

        if (result.isSuccess()) {
            Toast.makeText(this, "Order published successfully", Toast.LENGTH_SHORT).show();
            // Close current page after successful publication
            finish();
        } else {
            Toast.makeText(this, "Order publishing failed: " + result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 