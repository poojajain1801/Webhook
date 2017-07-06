package com.comviva.mdesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.comviva.hceservice.common.CardLcmOperation;
import com.comviva.hceservice.common.ComvivaHce;
import com.comviva.hceservice.mdes.digitizatioApi.CardLcmListener;
import com.comviva.hceservice.mdes.digitizatioApi.CardLcmReasonCode;
import com.comviva.hceservice.mdes.digitizatioApi.CardLcmRequest;
import com.comviva.hceservice.mdes.digitizatioApi.Digitization;
import com.comviva.mdesapp.R;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.init.SdkContext;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private ViewFlipper cards;
    private ArrayList<McbpCard> cardList;
    private TextView tokenCount;
    private ProgressDialog progressDialog;
    private String tokenUniqueReference;

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
        if(isBlur) {
            image.setAlpha(0.5f);
        }
    }

    private void addCard() {
        startActivity(new Intent(this, AddCardActivity.class));
    }

    public void refreshCardList() {
        cards.removeAllViews();
        cardList = McbpWalletApi.getCards();
        if (cardList.isEmpty()) {
            setFlipperImage(R.drawable.loading_card_profile_white, 0, "", false);
            tokenCount.setText("");
        } else {
            int i = 0;
            for (McbpCard card : cardList) {
                try {
                    LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
                    ProfileState cardState = ldeRemoteManagementService.getCardState(card.getDigitizedCardId());

                    tokenUniqueReference = ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(card.getDigitizedCardId());
                    SdkContext sdkContext = SdkContext.initialize(getApplicationContext());
                    String cardNum = "XXXX XXXX XXXX " + sdkContext.getLdeMcbpCardService().getDisplayablePanDigits(tokenUniqueReference);

                    setFlipperImage(R.drawable.mastercardimg, i++, cardNum, cardState.equals(ProfileState.SUSPENDED));

                    int sukCount = card.numberPaymentsLeft();
                    tokenCount.setText(tokenCount.getHint() + ": " + sukCount);


                    if (!cardState.equals(ProfileState.INITIALIZED)) {
                        boolean isActivated = McbpCardApi.activateCard(ldeRemoteManagementService.getTokenUniqueReferenceFromCardId(card.getDigitizedCardId()));
                        System.out.print(isActivated ? "Card Activated" : "Card Activation Failed");
                    }

                    // Replenish Card if it has no transaction credential
                    if (sukCount == 0) {
                        ComvivaHce comvivaHce = ComvivaHce.getInstance(null);
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

        cards = (ViewFlipper) findViewById(R.id.viewflipperCards);
        tokenCount = (TextView) findViewById(R.id.tokencount);

        refreshCardList();

        ImageButton payButton = (ImageButton) findViewById(R.id.button_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList cards = McbpInitializer.getInstance().getBusinessService().getAllCards(true);
                LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance().getLdeRemoteManagementService();
                try {
                    ldeRemoteManagementService.getCardState(((McbpCard) cards.get(0)).getDigitizedCardId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //refreshCardList();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //refreshCardList();
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
        //McbpCard card = cardList.get(tagCard);
        //int sukCount = card.numberPaymentsLeft();
        //tokenCount.setText(tokenCount.getHint() + ": " + sukCount);
        return false;
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
}
