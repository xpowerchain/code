package com.lunary.owerwallet.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.TokenInfo;
import com.lunary.owerwallet.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/3.
 */
public class TokenInfoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TokenInfo> tokenInfoses = new ArrayList<>();

    public TokenInfoAdapter(Context mContext, ArrayList<TokenInfo> tokenInfoses) {
        this.mContext = mContext;
        if (tokenInfoses != null){
            this.tokenInfoses = tokenInfoses;
        }
    }

    @Override
    public int getCount() {
        return tokenInfoses.size();
    }

    @Override
    public Object getItem(int position) {
        return tokenInfoses.size() <= 0 ? position :tokenInfoses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mContext, R.layout.item_tokeninfo, null);
            holder = new ViewHolder(convertView);
            holder.tokenImg = (SimpleDraweeView) convertView.findViewById(R.id.tokenImg);
            holder.tokenName = (TextView) convertView.findViewById(R.id.tokenName);
            holder.tokenPrice = (TextView) convertView.findViewById(R.id.tokenPrice);
            holder.tokenPriceUnit = (TextView) convertView.findViewById(R.id.tokenPriceUnit);
            convertView.setTag(holder);
        }
        try {
            TokenInfo tokenInfo = tokenInfoses.get(position);
            holder.tokenName.setText(tokenInfo.getToken_symbol());
            if (tokenInfo.getToken_symbol().toUpperCase().equals("ETH")){
                holder.tokenImg.setActualImageResource(R.mipmap.js_images_asset_eth);
            }else{
                if (!TextUtils.isEmpty(tokenInfo.getToken_ico_url())){
                    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(tokenInfo.getToken_ico_url())).build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(holder.tokenImg.getController())
                            .build();
                    holder.tokenImg.setController(controller);
                }
            }

            holder.tokenPrice.setText(Utils.getDecimalFormat(tokenInfo.getToken_num()));
            if (TextUtils.isEmpty(tokenInfo.getRate())){
                holder.tokenPriceUnit.setText("-");
            }else{
                holder.tokenPriceUnit.setText("≈ $ "+Utils.getDecimalFormat(Float.parseFloat(tokenInfo.getRate())*tokenInfo.getToken_num()));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }
    class ViewHolder {
        SimpleDraweeView tokenImg;
        TextView tokenName;
        TextView tokenPrice;
        TextView tokenPriceUnit;

        public ViewHolder(View view) {
        }
    }
}
