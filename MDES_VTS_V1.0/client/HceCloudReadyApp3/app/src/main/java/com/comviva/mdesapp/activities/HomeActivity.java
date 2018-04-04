package com.comviva.mdesapp.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.CardState;
import com.comviva.hceservice.common.ComvivaSdk;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SdkError;
import com.comviva.hceservice.common.SdkException;
import com.comviva.hceservice.common.cdcvm.CdCvm;
import com.comviva.hceservice.common.cdcvm.Entity;
import com.comviva.hceservice.common.cdcvm.Type;
import com.comviva.hceservice.digitizationApi.ActiveAccountManagementService;
import com.comviva.hceservice.digitizationApi.CardData;
import com.comviva.hceservice.digitizationApi.CardLcmListener;
import com.comviva.hceservice.digitizationApi.CardLcmReasonCode;
import com.comviva.hceservice.digitizationApi.CardLcmRequest;
import com.comviva.hceservice.digitizationApi.CardMetaData;
import com.comviva.hceservice.digitizationApi.ContentGuid;
import com.comviva.hceservice.digitizationApi.ContentType;
import com.comviva.hceservice.digitizationApi.Digitization;
import com.comviva.hceservice.digitizationApi.GetAssetListener;
import com.comviva.hceservice.register.Registration;
import com.comviva.hceservice.register.RegistrationListener;
import com.comviva.hceservice.tds.TdsRegistrationListener;
import com.comviva.hceservice.tds.TransactionDetailsListener;
import com.comviva.hceservice.tds.TransactionHistory;
import com.comviva.hceservice.tds.TransactionHistoryListener;
import com.comviva.hceservice.tds.UnregisterTdsListener;
import com.comviva.hceservice.util.DeviceLockUtil;
import com.comviva.hceservice.util.GetCardMetaDataListener;
import com.comviva.hceservice.util.NfcSetting;
import com.comviva.hceservice.util.NfcUtil;
import com.comviva.hceservice.util.ResponseListener;
import com.comviva.mdesapp.AndroidHceServiceApp;
import com.comviva.mdesapp.ApduLogListener;
import com.comviva.mdesapp.CardDetails;
import com.comviva.mdesapp.DataBaseOperations;
import com.comviva.mdesapp.MyAppFCMService;
import com.comviva.mdesapp.R;
import com.comviva.mdesapp.constant.Constants;
import com.mastercard.mcbp.card.cvm.PinListener;
import com.mastercard.mcbp.listeners.ProcessContactlessListener;
import com.mastercard.mcbp.userinterface.DisplayTransactionInfo;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ApduLogListener, SwipeRefreshLayout.OnRefreshListener {
    private ViewFlipper cards;
    private ArrayList<PaymentCard> cardList;
    private TextView txtViewTokenCount;
    private TextView txtViewTimer;
    private TextView tvApduLog;
    private PaymentCard currentCard;
    private ProgressDialog progressDialog;
    private LinearLayout payLayout, noCardAddedLay;
    private String tokenUniqueReference;
    private Digitization digitization;
    private final int transactionHistoryCount = 5;
    private ImageButton btnMakeDefault;
    private static final int REQ_CODE_SCREEN_LOCK = 1;
    private boolean isRefreshReq = true;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean replenishLukRequired = false;
    private CardEmulation cardEmulation;


    private ComvivaSdk comvivaSdk;

    private CountDownTimer timer;

    private void setFlipperImage(int res, int tag, String cardNumber, boolean isBlur) {


        ImageView image = new ImageView(getApplicationContext());
        Bitmap bm = BitmapFactory.decodeResource(getResources(), res);
        Bitmap.Config config = bm.getConfig();
        int width = bm.getWidth();
        int height = bm.getHeight();

        Bitmap newImage = Bitmap.createBitmap(width, height, config);

        Canvas c = new Canvas(newImage);
        c.drawBitmap(bm, (float) 0, (float) 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize((float) (width / 18));
        int x = height / 8;
        int y = width / 2;
        c.drawText(cardNumber, x, y, paint);
        image.setImageBitmap(newImage);
        image.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

                cards.setInAnimation(HomeActivity.this, R.anim.slide_in_from_left);
                cards.setOutAnimation(HomeActivity.this, R.anim.slide_out_to_right);
                cards.showNext();

                int tagCard = Integer.parseInt(cards.getCurrentView().getTag().toString());
                currentCard = cardList.get(tagCard);
              //  comvivaSdk.setSelectedCard(currentCard);
                // comvivaSdk.setSelectedCard(currentCard);
                tokenUniqueReference = currentCard.getCardUniqueId();
                int sukCount = currentCard.getTransactionCredentialsLeft();
                txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);
                enableDefaultCardButton();

            }
            public void onSwipeLeft() {
                cards.setInAnimation(HomeActivity.this, R.anim.slide_in_from_right);
                cards.setOutAnimation(HomeActivity.this, R.anim.slide_out_to_left);
                cards.showPrevious();

                int tagCard = Integer.parseInt(cards.getCurrentView().getTag().toString());
                currentCard = cardList.get(tagCard);
              //  comvivaSdk.setSelectedCard(currentCard);
                // comvivaSdk.setSelectedCard(currentCard);
                tokenUniqueReference = currentCard.getCardUniqueId();
                int sukCount = currentCard.getTransactionCredentialsLeft();
                txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);
                enableDefaultCardButton();
            }
            public void onSwipeBottom() {
            }

        });
        image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cards.addView(image);
        image.setTag(tag);
        if (isBlur) {
            image.setAlpha(0.5f);
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setAsPreferredHceService() {
        boolean allowsForeground = cardEmulation.categoryAllowsForegroundPreference(CardEmulation.CATEGORY_PAYMENT);
        if (allowsForeground) {
            ComponentName hceComponentName = new ComponentName(getApplicationContext(), AndroidHceServiceApp.class);
            cardEmulation.setPreferredService(HomeActivity.this, hceComponentName);
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void unsetAsPreferredHceService() {
        boolean allowsForeground = cardEmulation.categoryAllowsForegroundPreference(CardEmulation.CATEGORY_PAYMENT);
        if (allowsForeground) {
            ComponentName hceComponentName = new ComponentName(getApplicationContext(),AndroidHceServiceApp.class );
            cardEmulation.unsetPreferredService(HomeActivity.this);
        }
    }




    private void addCard() {
        startActivity(new Intent(this, AddCardActivity.class));
    }

    private void performCardLifeCycleManagement(final ArrayList<PaymentCard> cardList, final CardLcmOperation operation) {
        final CardLcmRequest cardLcmRequest = new CardLcmRequest();
        cardLcmRequest.setReasonCode(CardLcmReasonCode.ACCOUNT_CLOSED);
        cardLcmRequest.setPaymentCards(cardList);
        cardLcmRequest.setCardLcmOperation(operation);


        digitization.performCardLcm(cardLcmRequest, new CardLcmListener() {
            @Override
            public void onStarted() {

                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess(String message) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Success")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onError(SdkError sdkError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(sdkError.getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private void registerWithTds(final String tokenUniqueReference) {
        TransactionHistory.registerWithTdsInitiate(tokenUniqueReference, new TdsRegistrationListener() {
            @Override
            public void onStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onError(SdkError sdkError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(sdkError.getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                         .setCancelable(false)
                        .show();
            }

            @Override
            public void onSuccess() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Success")
                        .setMessage("Card will be registered soon...")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                        .show();
            }
        });
    }

    private void getTransactionHistory(final String tokenUniqueReference) {
        TransactionHistory.getTransactionDetails(tokenUniqueReference, new TransactionDetailsListener() {
            @Override
            public void onStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onError(SdkError sdkError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(sdkError.getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onSuccess(ArrayList transactionDetails) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Intent intent = new Intent(HomeActivity.this, TransactionHistoryMdesActivity.class);
                intent.putExtra("transactionDetails", transactionDetails);
                startActivity(intent);
            }
        });
    }

    private void getTransactionHistoryVisa() {
        TransactionHistory.getTransactionHistory(currentCard, transactionHistoryCount, new TransactionHistoryListener() {
            @Override
            public void onSuccess(ArrayList transactionInfo) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (transactionInfo.size() > 0) {
                    Intent intent = new Intent(HomeActivity.this, TransactionHistoryMdesActivity.class);
                    intent.putExtra("transactionDetails", transactionInfo);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Success")
                            .setMessage("No transaction history available")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    refreshCardList();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onError(SdkError sdkError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(sdkError.getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }

        });
    }

    private void unregisterFromTds(String tokenUniqueReference) {
        TransactionHistory.unregisterWithTds(tokenUniqueReference, new UnregisterTdsListener() {
            @Override
            public void onStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onError(SdkError sdkError) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(sdkError.getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onSuccess() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Success")
                        .setMessage("Successfully Unregistered from TDS service")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private void displayPINView(final PinListener pinListener) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setTitle("PIN");
        alert.setMessage("Enter Pin :");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.requestFocus();
        input.requestFocusFromTouch();
        input.bringToFront();
        input.setMaxLines(1);
        input.setEms(1);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(4);
        input.setFilters(filters);
        alert.setView(input);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pin = input.getText().toString();
                if (pin.length() < 4) {
                } else {
                    currentCard.pinEntered(pinListener, pin);
                }
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return;
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return;
            }
        });
        alert.show();
    }

    private void startTransaction() {
        final int timeOut = currentCard.getCvmResetTimeout();

        // Start timer
        timer = new CountDownTimer(timeOut * 1000, 1000) {
            @Override
            public void onTick(long remainingMillis) {
                txtViewTimer.setText(Integer.toString((int) (remainingMillis / 1000)) + "s");
            }

            @Override
            public void onFinish() {
                Log.d("PaymentLandingPage", "Timer finished");
                timer.cancel();
                updateOnPaymentCompletion();
                currentCard.stopContactlessTransaction();
            }
        }.start();
    }

    private void updateOnPaymentCompletion() {
        txtViewTimer.setText("");
        int sukCount = currentCard.getTransactionCredentialsLeft();
        txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);
    }

    private void refreshCardList() {
        if (cards != null) {
            cards.removeAllViews();
        }

        cardList = comvivaSdk.getAllCards();

        if (cardList == null || cardList.isEmpty()) {
            setFlipperImage(R.drawable.no_card_added, 0, "", false);
            noCardAddedLay.setVisibility(View.VISIBLE);
            txtViewTokenCount.setText("");
            payLayout.setVisibility(View.INVISIBLE);
        } else {
            payLayout.setVisibility(View.VISIBLE);
            noCardAddedLay.setVisibility(View.GONE);
            int i = 0;
            for (PaymentCard card : cardList) {
                currentCard = card;
              //  Toast.makeText(HomeActivity.this, , Toast.LENGTH_SHORT).show();
                /*try {
                    comvivaSdk.setSelectedCard(currentCard);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                if(currentCard.getTransactionCredentialsLeft() <= 0)
                {
                    replenishLukRequired = true;
                }

                String cardNum = "XXXX XXXX XXXX " + currentCard.getCardLast4Digit();
                String last$Token = "XXXX XXXX XXXX" + currentCard.getTokenLast4Digit();

                Log.d("Token Last 4" , last$Token);
                Log.d("card Last 4" , cardNum);
                int sukCount = currentCard.getTransactionCredentialsLeft();
                CardState cardState = currentCard.getCardState();
                tokenUniqueReference = currentCard.getCardUniqueId();
                String instr = currentCard.getInstrumentId();
             //   Toast.makeText(HomeActivity.this, "get all cards" + instr, Toast.LENGTH_SHORT).show();


                switch (card.getCardType()) {
                    case MDES:
                        setFlipperImage(R.drawable.mastercardimg, i++, cardNum, cardState.equals(CardState.SUSPENDED));
                        break;

                    case VTS:
                        setFlipperImage(R.drawable.large_visa_card, i++, cardNum, cardState.equals(CardState.SUSPENDED));
                        break;
                }
                //enableDefaultCardButton();

                // Replenish Card if it has no transaction credential
              /*  if ((sukCount == 0) && cardState.equals(CardState.INITIALIZED)) {
                    //comvivaSdk.replenishCard(tokenUniqueReference);
                    //replenishLUKVisa();
                }*/
            }
            int sukCount =cardList.get(Integer.parseInt(cards.getCurrentView().getTag().toString())).getTransactionCredentialsLeft();
            currentCard = cardList.get(Integer.parseInt(cards.getCurrentView().getTag().toString()));
            txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);
            swipeRefreshLayout.setRefreshing(false);
        }

       /* if(replenishLukRequired)
        {
            //Toast.makeText(this, "Replenish Luk called", Toast.LENGTH_SHORT).show();
            replenishLUKVisa();
            replenishLukRequired = false;
        }*/


      //  cards.setDisplayedChild(currentCard);
    }

    private void replenish() {
        Digitization digitization = Digitization.getInstance();
        digitization.replenishTransactionCredential(currentCard, new ResponseListener() {
            @Override
            public void onStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Success")
                        .setMessage("Replenishment Successfully")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onError(SdkError sdkError) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(sdkError.getMessage())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private void enableDefaultCardButton() {

        PaymentCard defaultCard = comvivaSdk.getDefaultPaymentCard();
        if(currentCard!=null) {
            if (defaultCard != null && defaultCard.getCardUniqueId().equalsIgnoreCase(currentCard.getCardUniqueId())) {
                btnMakeDefault.setVisibility(View.GONE);
            } else {
                btnMakeDefault.setVisibility(View.VISIBLE);
            }
        }else
        {
            btnMakeDefault.setVisibility(View.GONE);
        }
    }



    private void performTransaction() {
        // Check that NFC is enabled
        if (!NfcUtil.isNfcEnabled(getApplicationContext())) {
            NfcUtil.showNfcSetting(HomeActivity.this, NfcSetting.ENABLE_NFC);
        } else {
            currentCard.getCvmResetTimeout();
            ProcessContactlessListener processContactlessListener = new ProcessContactlessListener() {
                @Override
                public void onContactlessReady() {
                    startTransaction();
                }

                @Override
                public void onContactlessPaymentCompleted(DisplayTransactionInfo displayTransactionInfo) {
                    isRefreshReq = true;
                    currentCard.stopContactlessTransaction();
                    timer.cancel();
                    updateOnPaymentCompletion();
                }

                @Override
                public void onContactlessPaymentAborted(DisplayTransactionInfo displayTransactionInfo) {
                    Toast.makeText(HomeActivity.this, "onContactlessPaymentAborted", Toast.LENGTH_SHORT).show();
                    isRefreshReq = true;
                    currentCard.stopContactlessTransaction();
                    timer.cancel();
                    updateOnPaymentCompletion();
                }

                @Override
                public void onPinRequired(PinListener pinListener) {
                    isRefreshReq = true;
                    Toast.makeText(HomeActivity.this, "onPinRequired", Toast.LENGTH_SHORT).show();
                   // displayPINView(pinListener);
                }
            };
            try {
                CdCvm cdCvm = new CdCvm();
                cdCvm.setEntity(Entity.VERIFIED_MOBILE_DEVICE);
                cdCvm.setType(Type.PATTERN);
                cdCvm.setStatus(true);

            /*   cdCvm.setEntity(Entity.NONE);
               cdCvm.setType(Type.NONE);
               cdCvm.setStatus(false);*/

                currentCard.setCdCvm(cdCvm);
                comvivaSdk.setSelectedCard(currentCard);
                currentCard.startContactlessTransaction(processContactlessListener);
            } catch (SdkException e) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage("Credentials not available please replenish")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cardEmulation = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(getApplicationContext()));

        byte[] bytes = new byte[2];
        String provisionid = "1234";
        String panID = "232323";
        String last4 = "23232dxs";

        DataBaseOperations dataBaseOperations = new DataBaseOperations(HomeActivity.this);
        CardDetails cardDetails = new CardDetails();
        cardDetails.setCardProvisionID(provisionid);
        cardDetails.setPanEnrollmentId(panID);
        cardDetails.setCardLast4(last4);
        cardDetails.setImage(bytes);
        dataBaseOperations.addCardData(cardDetails);

       /* CardDetails cardDetail1 = new CardDetails();
        cardDetails.setCardProvisionID("2234");
        cardDetails.setPanEnrollmentId("2678");
        cardDetails.setCardLast4(last4);
        cardDetails.setImage(bytes);
        dataBaseOperations.addCardData(cardDetails);
        dataBaseOperations.addCardData(cardDetail1);


        CardDetails cardDetails1 = dataBaseOperations.searchCard("1234");

        System.out.println(cardDetails1.getCardLast4() + " " + cardDetails1.getPanEnrollmentId());*/
        CardDetails cardDetails1 = dataBaseOperations.searchCard(provisionid);

        System.out.println(cardDetails1.getCardLast4() + " " + cardDetails1.getPanEnrollmentId());
       /* Intent intent = new Intent();
        intent.setAction(CardEmulation.ACTION_CHANGE_DEFAULT);
        intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, new ComponentName(this, com.comviva.mdesapp.AndroidHceServiceApp.class));
        intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
        startActivity(intent);*/

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        digitization = Digitization.getInstance();
        payLayout = (LinearLayout) findViewById(R.id.pay_lay);
        noCardAddedLay = (LinearLayout) findViewById(R.id.no_card_added_lay);
        progressDialog = new ProgressDialog(HomeActivity.this);
        TextView txtViewUserId = (TextView) findViewById(R.id.tvUserId);
        SharedPreferences userPref = getSharedPreferences(Constants.SHARED_PREF_USER, MODE_PRIVATE);
        txtViewUserId.setText("Welcome " + userPref.getString(Constants.KEY_USER_ID, null) + "...");
        tvApduLog = (TextView) findViewById(R.id.tvApduLog);
        tvApduLog.setMovementMethod(new ScrollingMovementMethod());
        btnMakeDefault = (ImageButton) findViewById(R.id.btnMakeDefault);
        btnMakeDefault.setVisibility(View.INVISIBLE);
        btnMakeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comvivaSdk.setDefaultCard(currentCard);
                btnMakeDefault.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "This Card is set as default", Toast.LENGTH_LONG).show();
            }
        });

        try {
            comvivaSdk = ComvivaSdk.getInstance(null);
        } catch (SdkException e) {
        }

        cards = (ViewFlipper) findViewById(R.id.viewFlipperCards);
        txtViewTokenCount = (TextView) findViewById(R.id.txtViewTokenCount);
        txtViewTimer = (TextView) findViewById(R.id.txtViewTimer);

        refreshCardList();
        replenishLUKVisa();
        enableDefaultCardButton();

        ImageButton payButton = (ImageButton) findViewById(R.id.btnPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if card is suspended
                switch (currentCard.getCardState()) {
                    case SUSPENDED:
                    case UNINITIALIZED:
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Error")
                                .setMessage("Sorry Card is suspended/inactive")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //refreshCardList();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                        return;
                }

                boolean isLockEnabled = DeviceLockUtil.checkLockingMech(getApplicationContext());
               // performTransaction();
                if (isLockEnabled) {
                    isRefreshReq = false;
                    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    Intent screenLock = keyguardManager.createConfirmDeviceCredentialIntent("Please Verify and Tap Again", getResources().getString(R.string.welcome));
                    startActivityForResult(screenLock, REQ_CODE_SCREEN_LOCK);
                } else {
                    Toast.makeText(getApplicationContext(), "Set Any one of Security Lock i.e. PIN, PATTERN, PASSWORD", Toast.LENGTH_LONG).show();
                }
            }
        });
        AndroidHceServiceApp.setApduLogListener(this);

    }

    public void replenishLUKVisa() {

       // Toast.makeText(getApplicationContext(), "replenish Started ", Toast.LENGTH_SHORT).show();
        comvivaSdk.replenishLUKVisa();

   /* List<TokenData> vtsCards;
    ArrayList<TokenKey> tokensToBeReplenished = new ArrayList<>();
    //ComvivaSdkInitData initData = getInitializationData();
    PaymentCard paymentCard;
    vtsCards = VisaPaymentSDKImpl.getInstance().getAllTokenData();
    for (TokenData tokenData : vtsCards) {

            boolean isReplenishmentRequired = (currentCard.getTransactionCredentialsLeft() <= 0);

            if (isReplenishmentRequired) {
                tokensToBeReplenished.add(tokenData.getTokenKey());
                Toast.makeText(getApplicationContext(), "replenish Started for   "+currentCard.getCardLast4Digit(), Toast.LENGTH_LONG).show();
            }

    }
    if(tokensToBeReplenished.size() > 0) {
        Intent intent = new Intent(this.getApplicationContext(), ActiveAccountManagementService.class);
        intent.putExtra(com.visa.cbp.sdk.facade.data.Constants.REPLENISH_TOKENS_KEY, tokensToBeReplenished);
        this.getApplicationContext().startService(intent);
    }*/
}

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isRefreshReq) {
            refreshCardList();
        }
    }

    private boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    protected void onResume() {
        super.onResume();
        if (isLollipopOrHigher()) {
            setAsPreferredHceService();
        }
        if(isRefreshReq) {
            refreshCardList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isLollipopOrHigher()) {
            unsetAsPreferredHceService();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<PaymentCard> cardList = new ArrayList<>();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addCard:
                addCard();
                return true;

            /*case R.id.setPin:
                startActivity(new Intent(this, SetPinActivity.class));
                return true;*/

            case R.id.deleteCard:
                cardList.add(currentCard);
                performCardLifeCycleManagement(cardList, CardLcmOperation.DELETE);
                return true;

            case R.id.suspendCard:
                cardList.add(currentCard);
                performCardLifeCycleManagement(cardList, CardLcmOperation.SUSPEND);
                return true;

            case R.id.resumeCard:
                cardList.add(currentCard);
                performCardLifeCycleManagement(cardList, CardLcmOperation.RESUME);
                return true;

            /*case R.id.changePin:
                startActivity(new Intent(HomeActivity.this, ChangePinActivity.class));
                return true;

            case R.id.registerTds:
                if (comvivaSdk.isTdsRegistered()) {
                    Toast.makeText(HomeActivity.this, "Token is already registered for transaction history", Toast.LENGTH_LONG).show();
                    return true;
                }
                registerWithTds(tokenUniqueReference);
                return true;

            case R.id.transactionDetails:
                getTransactionHistory(tokenUniqueReference);
                return true;*/

            case R.id.unregisterTds:
                unregisterFromTds(tokenUniqueReference);
                return true;

            case R.id.get_content:
                digitization.getContent("", new GetAssetListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onCompleted(ContentGuid contentGuid) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
                return true;

            case R.id.unregister_device:

                deleteCache(HomeActivity.this);
                final Registration registration = Registration.getInstance();
                SharedPreferences sharedPrefsUserDetails = getSharedPreferences(Constants.SHARED_PREF_USER, getApplicationContext().MODE_PRIVATE);
                String userID = sharedPrefsUserDetails.getString(Constants.KEY_USER_ID,"");
                String imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                registration.unRegisterDevice(imei, userID, new RegistrationListener() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(HomeActivity.this, "Device Unregistered Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this,SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onStarted() {
                        System.out.println("Started");

                    }

                    @Override
                    public void onError(SdkError sdkError) {
                        System.out.println(sdkError);

                    }
                });
                return true;


            case R.id.get_metatdata:

                digitization.getCardMetaData(currentCard.getInstrumentId(), new GetCardMetaDataListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onSuccess(CardMetaData cardMetaData) {


                        CardData[] cardDataArray ;

                        cardDataArray = cardMetaData.getCardDatas();

                        for(int i=0;i<cardDataArray.length;i++)
                        {
                            String guid = cardDataArray[i].getGuid();
                            ContentType contentType = cardDataArray[i].getContentType();
                            String content = contentType.getType();
                            System.out.println(guid);
                        }
                        Toast.makeText(HomeActivity.this, "Card Meta Data Success", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(SdkError sdkError) {

                    }
                });
                return true;

            case R.id.transaction_history:
                getTransactionHistoryVisa();
                return true;

            case R.id.token_status:
                digitization.getTokenStatus(currentCard, new ResponseListener() {
                    @Override
                    public void onStarted() {
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Success")
                                .setMessage("Card Get Status Success")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();

                    }

                    @Override
                    public void onError(SdkError sdkError) {

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Error")
                                .setMessage(sdkError.getMessage())
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                    }
                });
                return true;

            case R.id.replenish:
                replenish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean  onTouchEvent(MotionEvent touchevent) {
       if(MyAppFCMService.vtcFcmevrntOccered)
        {
            refreshCardList();
            MyAppFCMService.vtcFcmevrntOccered=false;
        }
        if (cardList == null || cardList.isEmpty()) {
            startActivity(new Intent(this, AddCardActivity.class));
        } else {
           /* float lastX = 0;
            switch (touchevent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    lastX = touchevent.getX();
                    break;

                case MotionEvent.ACTION_UP:

                    float currentX = touchevent.getX();
                    if (lastX < currentX) {
                        if (cards.getChildCount() == 1) {
                            break;
                        }
                        cards.setInAnimation(this, R.anim.slide_in_from_left);
                        cards.setOutAnimation(this, R.anim.slide_out_to_right);
                        cards.showNext();
                    }

                    if (lastX > currentX) {
                        if (cards.getChildCount() == 1) {
                            break;
                        }
                        cards.setInAnimation(this, R.anim.slide_in_from_right);
                        cards.setOutAnimation(this, R.anim.slide_out_to_left);
                        cards.showPrevious();
                    }
                    break;
            }*/



        }
        return false;
    }

    @Override
    public void onCommandApduReceived(String commandApdu) {
        tvApduLog.setText(tvApduLog.getText().toString() + "\nCommand APDU\n" + commandApdu);
    }

    @Override
    public void onResponseApdu(String responseApdu) {
        tvApduLog.setText(tvApduLog.getText().toString() + "\nResponse APDU\n" + responseApdu);
    }

    @Override
    public void onDeactivated(String log) {
        tvApduLog.setText(tvApduLog.getText().toString() + "\nDeactivating card\n" + log);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SCREEN_LOCK:
                if(resultCode == Activity.RESULT_OK){
                    performTransaction();
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    CdCvm cdCvm = new CdCvm();
                    cdCvm.setEntity(Entity.NONE);
                    cdCvm.setType(Type.NONE);
                    cdCvm.setStatus(false);

                    currentCard.setCdCvm(cdCvm);
                    Toast.makeText(HomeActivity.this, "Need to authenticate to Do transaction", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }



    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshCardList();
        Toast.makeText(this, "cards refreshed ", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);


    }



    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
