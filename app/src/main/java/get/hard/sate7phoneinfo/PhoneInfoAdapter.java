package get.hard.sate7phoneinfo;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PhoneInfoAdapter extends RecyclerView.Adapter {

    private ArrayList<PhoneInfo> mInfoList;

    public PhoneInfoAdapter(ArrayList<PhoneInfo> infoList) {
        mInfoList = infoList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhoneInfoHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.phone_info_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((PhoneInfoHolder) viewHolder).mTitle.setText(mInfoList.get(i).getTitle());
        ((PhoneInfoHolder) viewHolder).mContent.setText(mInfoList.get(i).getContent());
        ((PhoneInfoHolder) viewHolder).mContainer.setOnClickListener(new PhoneInfoClickListener(i));
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    private class PhoneInfoClickListener implements View.OnClickListener {
        private int mPosition;

        public PhoneInfoClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            XLog.d("onClick ... " + mInfoList.get(mPosition));
            if (mInfoList.get(mPosition).getType() == PhoneInfo.PhoneInfoType.COLLECTION) {
                XLog.d("onClick 22... " + mInfoList.get(mPosition));
                Intent detail = new Intent(v.getContext(), DetailActivity.class);
                detail.putExtra(DetailActivity.DETAIL_TYPE, DetailActivity.DETAIL_TYPE_CALL_LOG);
                v.getContext().startActivity(detail);
            }
        }
    }

    private static class PhoneInfoHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mContent;
        private View mContainer;

        public PhoneInfoHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.phoneInfoTitle);
            mContent = itemView.findViewById(R.id.phoneInfoContent);
            mContainer = itemView;
        }
    }
}


