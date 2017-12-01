package com.comviva.mdesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.comviva.hceservice.tds.TransactionDetails;

import java.util.ArrayList;

public class TransactionHistoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TransactionDetails> dataList;

    public TransactionHistoryAdapter(Context con, ArrayList<TransactionDetails> transactionDetailsArrayList) {
        this.context = con;
        this.dataList = transactionDetailsArrayList;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    public static class ViewHolder {
        protected TextView transactionType;
        protected TextView transactionTimestamp;
        protected TextView amount;
        protected TextView transactionIdentifier;
        protected TextView tokenUniqueRef;
        protected TextView authorizationStatus;
        protected ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder viewHolder = new ViewHolder();
        if (item == null) {
            item = LayoutInflater.from(context).inflate(R.layout.transaction_history_lay, parent, false);
            viewHolder.amount = (TextView) item.findViewById(R.id.textView3);
            viewHolder.transactionType = (TextView) item.findViewById(R.id.textView2);
            viewHolder.authorizationStatus = (TextView) item.findViewById(R.id.textView5);
            viewHolder.transactionIdentifier = (TextView) item.findViewById(R.id.textView6);
            viewHolder.transactionTimestamp = (TextView) item.findViewById(R.id.textView4);
            viewHolder.image = (ImageView) item.findViewById(R.id.imageView1);
            viewHolder.tokenUniqueRef = (TextView) item.findViewById(R.id.textView1);
        }

        if (dataList.get(position).getTokenUniqueReference() != null) {
            viewHolder.tokenUniqueRef.setText("xxxx xxxx xxxx " + dataList.get(position).getTokenUniqueReference());
        }
        if (dataList.get(position).getTransactionType() != null) {
            viewHolder.transactionType.setText(dataList.get(position).getTransactionType());
        }
        if ((dataList.get(position).getCurrencyCode() != null) && (dataList.get(position).getAmount() != null)) {
            viewHolder.amount.setText(dataList.get(position).getCurrencyCode() + " " + dataList.get(position).getAmount());
        }
        if (dataList.get(position).getTransactionIdentifier() != null) {
            viewHolder.transactionIdentifier.setText(dataList.get(position).getTransactionIdentifier());
        }
        if (dataList.get(position).getTransactionTimestamp() != null) {
            viewHolder.transactionTimestamp.setText(dataList.get(position).getTransactionTimestamp());
        }
        if (dataList.get(position).getAuthorizationStatus() != null) {
            viewHolder.authorizationStatus.setText(dataList.get(position).getAuthorizationStatus());
        }

        //viewHolder.image.setBackgroundResource(R.drawable.ic_master_card);
        viewHolder.image.setBackgroundResource(R.drawable.ic_visa);
        return item;
    }

/*	private void setDateTime(ViewHolder viewHolder, int pos) {
        try {
			String[] arr = dataList.get(pos).getTransactionDateTime().split(" ");
			String[] unOrgDate = arr[0].split("-");
			String year = unOrgDate[0];
			String month = new DateFormatSymbols().getMonths()[Integer
					.parseInt(unOrgDate[1]) - 1];
			String date = unOrgDate[2];
			String[] unOrgTime = arr[1].split("\\.");

			String time = unOrgTime[0];
			String displayTime = "";

			String[] array = time.split(":");

			if (Integer.parseInt(array[0]) < 12) {
				displayTime = array[0] + ":" + array[1] + " am";
			} else if (Integer.parseInt(array[0]) >= 12) {
				displayTime = (Integer.parseInt(array[0]) - 12) + ":" + array[1] + " pm";
			}

			viewHolder.date.setText(date + " " + month + " " + year + "  " + displayTime);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/

}
