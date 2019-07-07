package com.lunary.owerwallet.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.TokentransferBean;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.DateUtil;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.weight.SectionPinAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PinAdapter extends BaseAdapter {

	private static final int VIEW_TYPE_ITEM_TIME    = 0;
	private static final int VIEW_TYPE_ITEM_CONTENT = 1;

	private ArrayList<TokentransferBean> tokenInfoses = new ArrayList<>();
	private Context mContext = null;

	public PinAdapter(Context context,ArrayList<TokentransferBean> tokenInfoses) {
		mContext = context;
		this.tokenInfoses = tokenInfoses;
	}

	@Override
	public int getCount() {
		return tokenInfoses == null ? 0 : tokenInfoses.size();
	}

	@Override
	public String getItem(int position) {
		return "";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		View v = null;
		ViewSectionHolder holder;
		if (convertView == null) {
			v = LayoutInflater.from(mContext).inflate(R.layout.item_tokentransaction, parent, false);
			holder = new ViewSectionHolder(v);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewSectionHolder) v.getTag();
		}
		TokentransferBean tokenInfo = tokenInfoses.get(position);
		try {
			holder.createTime.setText(DateUtil.DateDistance(tokenInfo.getTrans_time()));
		}catch (Exception e){
			e.printStackTrace();
		}

		String adress = Const.mFullWallet.getWalletAdress();
		if (adress.contains(tokenInfo.getFrom_addr())){//转出
			holder.walletaddress.setText(tokenInfo.getTo_addr());
			if (TextUtils.isEmpty(tokenInfo.getAmount())){
				holder.amountcount.setText("- ");
			}else{
				holder.amountcount.setText("- "+ Utils.getDecimalFormat(Double.valueOf(tokenInfo.getAmount()))+tokenInfo.getToken_symbol().toLowerCase());
			}

			holder.amountcount.setTextColor(mContext.getResources().getColor(R.color.red));
			holder.tokenImg.setImageResource(R.mipmap.js_images_asset_out);
		}else{
			holder.walletaddress.setText(tokenInfo.getFrom_addr());
			if (TextUtils.isEmpty(tokenInfo.getAmount())){
				holder.amountcount.setText("+ ");
			}else{
				holder.amountcount.setText("+ "+Utils.getDecimalFormat(Double.valueOf(tokenInfo.getAmount()))+tokenInfo.getToken_symbol().toLowerCase());
			}
			holder.amountcount.setTextColor(mContext.getResources().getColor(R.color.color_046EB8));
			holder.tokenImg.setImageResource(R.mipmap.js_images_asset_in);
		}
		if (position == 0 || !tokenInfo.getTransTime().equals(tokenInfoses.get(position-1).getTransTime())){
			holder.mViewContentName.setVisibility(View.VISIBLE);
			holder.mViewContentName.setText(tokenInfo.getTransTime());
		}else{
			holder.mViewContentName.setVisibility(View.GONE);
		}

		return v;
	}
	private class ViewSectionHolder {

		ImageView tokenImg;
		TextView walletaddress;
		TextView createTime;
		TextView amountcount;
		private TextView mViewContentName;

		ViewSectionHolder(View convertView) {
			tokenImg = (ImageView) convertView.findViewById(R.id.tokenImg);
			walletaddress = (TextView) convertView.findViewById(R.id.walletaddress);
			createTime = (TextView) convertView.findViewById(R.id.createTime);
			amountcount = (TextView) convertView.findViewById(R.id.amountcount);
			mViewContentName = (TextView) convertView.findViewById(R.id.text_adapter_content_name);
		}
	}
}
