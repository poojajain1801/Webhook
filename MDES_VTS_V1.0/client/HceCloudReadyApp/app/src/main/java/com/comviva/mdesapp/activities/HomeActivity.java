package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.mdes.digitizatioApi.CardLcmListener;
import com.comviva.hceservice.mdes.digitizatioApi.CardLcmReasonCode;
import com.comviva.hceservice.mdes.digitizatioApi.CardLcmRequest;
import com.comviva.hceservice.mdes.digitizatioApi.Digitization;
import com.comviva.hceservice.mdes.tds.TdsRegistrationListener;
import com.comviva.hceservice.mdes.tds.TransactionDetails;
import com.comviva.hceservice.mdes.tds.TransactionDetailsListener;
import com.comviva.hceservice.mdes.tds.TransactionHistory;
import com.comviva.hceservice.mdes.tds.UnregisterTdsListener;
import com.comviva.mdesapp.R;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.cvm.PinListener;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.init.SdkContext;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.listeners.ProcessContactlessListener;
import com.mastercard.mcbp.userinterface.DisplayTransactionInfo;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private ViewFlipper cards;
    private ArrayList<McbpCard> cardList;
    private TextView txtViewTokenCount;
    private TextView txtViewTimer;
    private McbpCard currentCard;
    private ProgressDialog progressDialog;
    private String tokenUniqueReference;

    private ComvivaHce comvivaHce;

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
        image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cards.addView(image);
        image.setTag(tag);
        if (isBlur) {
            image.setAlpha(0.5f);
        }
    }

    private void addCard() {
        startActivity(new Intent(this, AddCardActivity.class));
    }

    private void performCardLifeCycleManagement(final ArrayList<String> cardList, final CardLcmOperation operation) {
        final CardLcmRequest cardLcmRequest = new CardLcmRequest();
        cardLcmRequest.setReasonCode(CardLcmReasonCode.ACCOUNT_CLOSED);
        cardLcmRequest.setTokenUniqueReferences(cardList);
        cardLcmRequest.setCardLcmOperation(operation);

        Digitization digitization = new Digitization();
        digitization.performCardLcm(cardLcmRequest, new CardLcmListener() {
            @Override
            public void onCardLcmStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
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
                        .show();
            }

            @Override
            public void onError(String message) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void registerWithTds(final String tokenUniqueReference) {
        TransactionHistory.registerWithTdsInitiate(tokenUniqueReference, new TdsRegistrationListener() {
            @Override
            public void onRegistrationStarted() {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onError(String message) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
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
            public void onError(String message) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            @Override
            public void onSuccess(ArrayList<TransactionDetails> transactionDetails) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Intent intent = new Intent(HomeActivity.this, TransactionHistoryMdesActivity.class);
                intent.putExtra("transactionDetails", transactionDetails);
                startActivity(intent);
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
            public void onError(String message) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                refreshCardList();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
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
                        .show();
            }
        });
    }

    public void refreshCardList() {
        cards.removeAllViews();
        cardList = McbpWalletApi.getCards();
        if (cardList.isEmpty()) {
            setFlipperImage(R.drawable.loading_card_profile_white, 0, "", false);
            txtViewTokenCount.setText("");
        } else {
            int i = 0;
            for (McbpCard card : cardList) {
                try {
                    currentCard = card;
                    comvivaHce.setPaymentCard(currentCard);
                    LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
                    ProfileState cardState = ldeRemoteManagementService.getCardState(card.getDigitizedCardId());

                    tokenUniqueReference = ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(card.getDigitizedCardId());
                    SdkContext sdkContext = SdkContext.initialize(getApplicationContext());
                    String cardNum = "XXXX XXXX XXXX " + sdkContext.getLdeMcbpCardService().getDisplayablePanDigits(tokenUniqueReference);

                    setFlipperImage(R.drawable.mastercardimg, i++, cardNum, cardState.equals(ProfileState.SUSPENDED));

                    int sukCount = card.numberPaymentsLeft();
                    txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);

                    // Replenish Card if it has no transaction credential
                    if ((sukCount == 0) && cardState.equals(ProfileState.INITIALIZED)) {
                        comvivaHce.replenishCard(tokenUniqueReference);
                    }
                } catch (InvalidInput e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        comvivaHce = ComvivaHce.getInstance(null);

        cards = (ViewFlipper) findViewById(R.id.viewFlipperCards);
        txtViewTokenCount = (TextView) findViewById(R.id.txtViewTokenCount);
        txtViewTimer = (TextView) findViewById(R.id.txtViewTimer);

        refreshCardList();

        ImageButton payButton = (ImageButton) findViewById(R.id.btnPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCard.getCvmResetTimeOut();
                McbpWalletApi.setCurrentCard(currentCard);
                McbpCardApi.prepareContactless(currentCard, new ProcessContactlessListener() {
                    @Override
                    public void onContactlessReady() {
                        startTransaction();
                    }

                    @Override
                    public void onContactlessPaymentCompleted(DisplayTransactionInfo displayTransactionInfo) {
                        try {
                            currentCard.stopContactLess();
                        } catch (InvalidCardStateException e) {
                            e.printStackTrace();
                        }
                        timer.cancel();
                        updateOnPaymentCompletion();
                    }

                    @Override
                    public void onContactlessPaymentAborted(DisplayTransactionInfo displayTransactionInfo) {
                        updateOnPaymentCompletion();
                    }

                    @Override
                    public void onPinRequired(PinListener pinListener) {
                        displayPINView(pinListener);
                    }
                });

                try {
                    currentCard.startContactless(new BusinessLogicTransactionInformation());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshCardList();
    }

    protected void onResume() {
        super.onResume();
        refreshCardList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> cardList = new ArrayList<>();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addCard:
                addCard();
                return true;

            case R.id.setPin:
                //startActivity(new Intent(this, SetPinActivity.class));7
                return true;

            case R.id.deleteCard:
                cardList.add(tokenUniqueReference);
                performCardLifeCycleManagement(cardList, CardLcmOperation.DELETE);
                return true;

            case R.id.suspendCard:
                cardList.add(tokenUniqueReference);
                performCardLifeCycleManagement(cardList, CardLcmOperation.SUSPEND);
                return true;

            case R.id.resumeCard:
                cardList.add(tokenUniqueReference);
                performCardLifeCycleManagement(cardList, CardLcmOperation.RESUME);
                return true;

            case R.id.changePin:
                startActivity(new Intent(HomeActivity.this, ChangePinActivity.class));
                return true;

            case R.id.registerTds:
                if (comvivaHce.isTdsRegistered(tokenUniqueReference)) {
                    Toast.makeText(HomeActivity.this, "Token is already registered for transaction history", Toast.LENGTH_LONG).show();
                    return true;
                }
                registerWithTds(tokenUniqueReference);
                return true;

            case R.id.transactionDetails:
                getTransactionHistory(tokenUniqueReference);
                return true;

            case R.id.unregisterTds:
                unregisterFromTds(tokenUniqueReference);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        float lastX = 0;
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
        }
        int tagCard = Integer.parseInt(cards.getCurrentView().getTag().toString());
        currentCard = cardList.get(tagCard);

        comvivaHce.setPaymentCard(currentCard);

        LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
        try {
            tokenUniqueReference = ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(currentCard.getDigitizedCardId());
        } catch (InvalidInput e) {
        }
        int sukCount = currentCard.numberPaymentsLeft();
        txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);
        return false;
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
                String value = input.getText().toString();
                if (value.length() < 4) {

                    //getBusinessServices().setCurrentCard(null, null);
                } else {
                    pinListener.pinEntered(ByteArray.of(value.getBytes()));
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
        final int timeOut = currentCard.getCvmResetTimeOut();

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
                try {
                    currentCard.stopContactLess();
                } catch (InvalidCardStateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateOnPaymentCompletion() {
        txtViewTimer.setText("");
        int sukCount = currentCard.numberPaymentsLeft();
        txtViewTokenCount.setText(txtViewTokenCount.getHint() + ": " + sukCount);
    }
}
