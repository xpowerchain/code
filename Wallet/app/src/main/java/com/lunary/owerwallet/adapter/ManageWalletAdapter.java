package com.lunary.owerwallet.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.FullWallet;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/4.
 */
public class ManageWalletAdapter extends BaseAdapter{
        private Context mContext;
        private ArrayList<FullWallet> fullWallets = new ArrayList<>();

    public ManageWalletAdapter(Context mContext, ArrayList<FullWallet> fullWallets) {
        this.mContext = mContext;
        this.fullWallets = fullWallets;
    }

    @Override
        public int getCount() {
            return fullWallets.size();
        }

        @Override
        public Object getItem(int position) {
            return fullWallets.size() <= 0 ? position :fullWallets.get(position);
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
                convertView = View.inflate(mContext, R.layout.item_managemallet, null);
                holder = new ViewHolder(convertView);
                holder.walletImg = (SimpleDraweeView) convertView.findViewById(R.id.headImg);
                holder.walletName = (TextView) convertView.findViewById(R.id.walletName);
                holder.walletaddress = (TextView) convertView.findViewById(R.id.walletaddress);
                holder.asset = (TextView) convertView.findViewById(R.id.asset);
                convertView.setTag(holder);
            }
            FullWallet fullWallet = fullWallets.get(position);
            if (!TextUtils.isEmpty(fullWallet.getWalletImg())){
                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(fullWallet.getWalletImg())).build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(holder.walletImg.getController())
                        .build();
                holder.walletImg.setController(controller);
            }
            holder.walletName.setText(fullWallet.getWalletName());
            holder.walletaddress.setText(fullWallet.getWalletAdress());
            holder.asset.setText(fullWallet.getAsset());
            return convertView;
        }
class ViewHolder {
    SimpleDraweeView walletImg;
    TextView walletName;
    TextView walletaddress;
    TextView asset;

    public ViewHolder(View view) {
    }
}
}
